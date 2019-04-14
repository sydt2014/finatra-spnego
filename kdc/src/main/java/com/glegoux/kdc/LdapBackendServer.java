package com.glegoux.kdc;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;

public class LdapBackendServer {


  private static Logger LOG = LoggerFactory.getLogger(LdapBackendServer.class);

  private InMemoryDirectoryServer ds;

  private final String host;
  private final int port;
  private final String listenerName = "ldap-backend-kdc";


  public LdapBackendServer() {

    this.host = "localhost";
    this.port = 1389;

  }


  public LdapBackendServer(String host, int port) {

    this.host = host;
    this.port = port;

  }


  public void start(String baseDN, String schemaFile, String[] ldifPaths)
    throws LDAPException, IOException, LDIFException {

    InMemoryDirectoryServerConfig dsConfig = new InMemoryDirectoryServerConfig(baseDN);
    InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig(
      this.listenerName,
      InetAddress.getByName(this.host),
      this.port,
      null
    );
    dsConfig.setListenerConfigs(listenerConfig);

    ClassLoader classLoader = LdapBackendServer.class.getClassLoader();
    Schema schema = Schema.mergeSchemas(Schema.getDefaultStandardSchema(),
      Schema.getSchema(new File(classLoader.getResource(schemaFile).getFile())));
    dsConfig.setSchema(schema);
    LOG.info("Imported schema {}", schemaFile);

    this.ds = new InMemoryDirectoryServer(dsConfig);

    for (String ldifPath : ldifPaths) {
      String path = URLDecoder.decode(classLoader.getResource(ldifPath).getFile(), "UTF8");
      this.ds.importFromLDIF(false, path);
      LOG.info("Imported LDIF {}", ldifPath);
    }

    this.ds.startListening();

    LOG.info("Started {} at {}:{} with base DN {}",
      this.listenerName,
      ds.getListenAddress().getHostAddress(),
      ds.getListenPort(),
      ds.getBaseDNs());

  }


  public void stop() {

    if (ds == null) {
      return;
    }

    ds.shutDown(true);
    LOG.info("Stopped {}", this.listenerName);

  }

  public String getSchema() throws LDAPException {

    LDAPConnection connection = null;
    try {
      connection = new LDAPConnection(this.host, this.port);
      return connection.getSchema().getSchemaEntry().toLDIFString();
    } finally {
      if (connection != null) {
        connection.close();
      }
    }

  }

  public static void main(String[] args) throws Exception {

    new LdapBackendServer().start(
      "dc=example,dc=com",
      "ldap/schema-kdc.ldif",
      new String[]{"ldap/ldap-base.ldif"}
    );

  }

}
