package com.glegoux.kdc;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.local.LocalKadmin;
import org.apache.kerby.kerberos.kerb.admin.kadmin.local.LocalKadminImpl;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;
import org.apache.kerby.kerberos.kerb.server.KdcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosServer extends KdcServer {

    private static Logger LOG = LoggerFactory.getLogger(KerberosServer.class);

    private LocalKadmin kadmin;
    private String realm;

    private KerberosServer(KdcConfig kdcConfig, BackendConfig backendConfig, String realm)
            throws KrbException {
        super(kdcConfig, backendConfig);
        this.realm = realm;
    }

    private static KdcConfig getKdcConfig(File kdcConfigFile) throws IOException {
        KdcConfig kdcConfig = new KdcConfig();
        kdcConfig.addIniConfig(kdcConfigFile);
        return kdcConfig;
    }

    private static BackendConfig getBackendConfig(File backendConfigFile) throws IOException {
        BackendConfig backendConfig = new BackendConfig();
        backendConfig.addIniConfig(backendConfigFile);
        return backendConfig;
    }

    private static Set<String> getPrincipalsToCreate() {
        Set<String> principals = new HashSet<>();
        ClassLoader classLoader = KerberosServer.class.getClassLoader();
        URL principalsResource = classLoader.getResource("kerberos/kdc/principals.txt");
        if (principalsResource == null) {
            LOG.error("Resource kerberos/kdc/principals.txt not found");
            System.exit(1);
        }
        File principalsFile = new File(principalsResource.getFile());
        try {
            Path principalsPath = principalsFile.toPath();
            principals = Files.readAllLines(principalsPath)
                    .stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            LOG.error("Resource kerberos/kdc/principals.txt not found", e);
            System.exit(1);
        }
        return principals;
    }

    private static Set<String> principals = getPrincipalsToCreate();

    private synchronized String principal(String component) {
        return String.format("%s@%s", component, realm);
    }

    private synchronized void provision() throws KrbException {
        for (String principal : principals) {
            kadmin.addPrincipal(principal(principal), principal);
            LOG.info(String.format("Add principal %s", principal));
        }
    }

    private void exportKeytab(String keytabName, String principal) throws KrbException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String keytabFilename = String.format("%s.keytab", keytabName);
        String keytabFilePathname = Paths.get(tmpDir, keytabFilename).toString();
        File keytabFile = new File(keytabFilePathname);
        if (keytabFile.exists()) {
            LOG.error(String.format("%s already exists, append Kerberos secret principal if present",
                    keytabFilePathname));
            System.exit(1);
        }
        kadmin.exportKeytab(keytabFile, principal);
        LOG.info(String.format("Export keytab for principal %s to %s", principal, keytabFilePathname));
    }

    private synchronized void deployment() throws KrbException {
        for (String principal : principals) {
            exportKeytab(principal.replace("/", "_"), principal(principal));
        }
    }

    public synchronized void init() throws KrbException {
        super.init();
        kadmin = new LocalKadminImpl(getKdcSetting(), getIdentityService());
        kadmin.createBuiltinPrincipals();
    }

    public static void main(String[] args) throws KrbException, ParseException, IOException {
        Options options = new Options();
        options.addOption("h", "help", false, "This help message");
        options.addOption("r", "realm", true, "KDC Server realm (by default EXAMPLE.COM)");
        options.addOption("host", "hostname", true, "KDC Server hostname (by default localhost)");
        options.addOption("p", "port", true, "KDC Server port (by default 1088)");
        options.addOption("backend", true, "Use memory or json backend (by default memory backend)");
        options.addOption("debug", false, "Enable debug (by default disable)");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("kdc-server", options);
            System.exit(0);
        }

        String host = commandLine.getOptionValue("host", "localhost");
        int port = Integer.parseInt(commandLine.getOptionValue("port", "1088"));
        String realm = commandLine.getOptionValue("realm", "EXAMPLE.COM").toUpperCase();

        LOG.info("Build and configure KDC server");
        ClassLoader classLoader = KerberosServer.class.getClassLoader();
        File kdcConfigFile = new File(classLoader.getResource("kerberos/kdc/kdc.conf").getFile());
        KdcConfig kdcConfig = getKdcConfig(kdcConfigFile);
        BackendConfig backendConfig = null;
        String typeBackend = commandLine.getOptionValue("host", "memory");
        switch (typeBackend) {
            case "memory":
                backendConfig = new BackendConfig();
                break;
            case "json":
                File backendConfigFile = new File(classLoader.getResource("kerberos/kdc/json-backend.conf").getFile());
                backendConfig = getBackendConfig(backendConfigFile);
                break;
            default:
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("kdc-server", options);
                System.exit(1);
        }
        KerberosServer kdcServer = new KerberosServer(kdcConfig, backendConfig, realm);
        if (commandLine.hasOption("debug")) {
            kdcServer.enableDebug();
        }
        kdcServer.setKdcRealm(realm);
        kdcServer.setKdcHost(host);
        kdcServer.setAllowTcp(true);
        kdcServer.setKdcTcpPort(port);
        kdcServer.setAllowUdp(true);
        kdcServer.setKdcUdpPort(port);

        LOG.info("Init KDC server");
        kdcServer.init();

        LOG.info(String.format("Start KDC server at %s:%d with realm=%s", host, port, realm));
        kdcServer.start();

        LOG.info("Provision server");
        kdcServer.provision();

        LOG.info("Deploy keytabs");
        kdcServer.deployment();
    }

}
