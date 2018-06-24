# SPNEGO Server Finatra

Thanks to HTTP/SPNEGO filter, endpoints served by Finatra web server can be accessible 
only by authenticated users on Kerberos Single Sign-On (SSO). So, your API endpoints are
protected.

<p align="center">
  <img alt="finatra_kerberos.png" src="./media/finatra_kerberos.png" width=200/><br>
  <i>SPNEGO Server Finatra</i>
</p>

See: [MIT Kerberos](https://web.mit.edu/kerberos/) and [Finatra](https://twitter.github.io/finatra/). 

# Prerequisites

You should have:

* MIT kerberos client 1.15+
* Java 1.8+
* Scala 2.11.6+
* Gradle 3+
* GNU Make 4+

> This project can work only with running KDC server, for that you can use:
>
> https://github.com/criteo/kerberos-docker

# Installation

*Build and run project:*

Build project:

~~~
make build
~~~

Run project:

~~~
make run ARGS="../build/libs/spnego-server-1.0-SNAPSHOT-all.jar"
~~~

Finatra web server is now running on `http://0.0.0.0:8000`.

`Ctrl+C` to stop web server.

Or you can run `./script/build-container` to run project in docker container with https://github.com/criteo/kerberos-docker
project.

*Test client:* 

Either by configuring your web browser (see below) or with curl command by running:

~~~
$ ./script/client.sh
~~~

*Configure your favorite web browser*:

* Firefox:

Go to `about:config`, click on `I accept the risk` button, filter in search bar by `negotiate` and 
put in `network.negotiate-auth.trusted-uris` value `127.0.0.1:8000` (or more generally address of your web server).

*See network exchanges*:

You can run `wireshark` with `kerberos` filter. 

# Usage

Do Kerberos authentication to get Ticket-Granting (TGT) Ticket kerberos client `kinit` with principal `bob@EXAMPLE.COM`
by example:

~~~
$ kinit -V bob@EXAMPLE.COM 
Using default cache: /tmp/krb5cc_1000
Using principal: bob@EXAMPLE.COM
Password for bob@EXAMPLE.COM: <type bob@EXAMPLE.COM password (by default bob)>
Authenticated to Kerberos v5
~~~

List your credentials:

~~~
$ klist
Ticket cache: FILE:/tmp/krb5cc_1000
Default principal: bob@EXAMPLE.COM

Valid starting       Expires              Service principal
24/06/2018 21:16:13  25/06/2018 07:16:13  krbtgt/EXAMPLE.COM@EXAMPLE.COM
	renew until 25/06/2018 21:16:11, Flags: FPRIA
	Etype (skey, tkt): des3-cbc-sha1, des3-cbc-sha1 
~~~

Then try to see web page protected by Kerberos authentication and provided by your web server.

# References

*SPNEGO filter for Finatra/Finagle:*

* [Finatra - examples](https://github.com/twitter/finatra/tree/develop/examples)
* [Finagle - SpnegoAuthenticator.scala](https://github.com/twitter/finagle/blob/develop/finagle-http/src/main/scala/com/twitter/finagle/http/SpnegoAuthenticator.scala)
* [Finagle - SpnegoAuthenticatorTest.scala](https://github.com/twitter/finagle/blob/develop/finagle-http/src/test/scala/com/twitter/finagle/http/SpnegoAuthenticatorTest.scala)

*SPNEGO in Java:*

* [Oracle - Secure Authentication Using SPNEGO Java GSS Mechanism](https://docs.oracle.com/javase/10/security/part-v-secure-authentication-using-spnego-java-gss-mechanism.htm#JSSEC-GUID-B51B4169-BD5D-4A19-BC2B-7F6B3ABB9B7A)

*SPNEGO protocol:*

* [Microsoft - HTTP-Based Cross-Platform Authentication by Using the Negotiate Protocol](https://msdn.microsoft.com/en-us/library/ms995330.aspx)
* [IBM - Single sign-on for HTTP requests using SPNEGO web authentication (1)](https://www.ibm.com/support/knowledgecenter/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/cwlp_spnego.html)
* [IBM - Single sign-on for HTTP requests using SPNEGO web authentication (2)](https://www.ibm.com/support/knowledgecenter/en/SSAW57_8.5.5/com.ibm.websphere.nd.multiplatform.doc/ae/csec_SPNEGO_explain.html)