# HTTP/SPNego based on Kerberos for Finatra web server

[![Build Status](https://travis-ci.org/glegoux/spnego-finatra.svg?branch=master)](https://travis-ci.org/glegoux/spnego-finatra)

Thanks to HTTP/SPNego filter, endpoints served by Finatra web server can be accessible 
only by authenticated users on Kerberos Single Sign-On (SSO). So, your API endpoints are
protected.

<p align="center">
  <img alt="finatra_kerberos.png" src="./spnego-finatra.png" width=200/><br>
  <i>Kerberos and Finatra</i>
</p>

See: [MIT Kerberos](https://web.mit.edu/kerberos/) and [Finatra](https://twitter.github.io/finatra/). 

# Prerequisites

You should have:

* MIT kerberos client/server 1.15+
* Java 1.8+ (Oracle)
* Scala 2.11+
* Gradle 4.0+
* Bash 4.4+
* GNU Make 4.1+
* Node: 8.11+
* NPM: 5.6+
* Angular CLI: 6+


> This project can work only with running KDC server, for that you can use:
>
> https://github.com/criteo/kerberos-docker

# Installation

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

# Usage

Test client either by configuring your web browser (see below) or with curl command by running:

~~~
$ ./script/client.sh
~~~

*Configure your favorite web browser*:

* Firefox:

Go to `about:config`, click on `I accept the risk` button, filter in search bar by `negotiate` and 
put in `network.negotiate-auth.trusted-uris` value `127.0.0.1:8000` (or more generally address of your web server).

*See network exchanges*:

You can run `wireshark` with `kerberos` filter. 

Then try to see web page protected by Kerberos authentication and provided by your web server.

# References

*SPNego filter for Finatra/Finagle:*

* [Finatra - examples](https://github.com/twitter/finatra/tree/develop/examples)
* [Finagle - SpnegoAuthenticator.scala](https://github.com/twitter/finagle/blob/develop/finagle-http/src/main/scala/com/twitter/finagle/http/SpnegoAuthenticator.scala)
* [Finagle - SpnegoAuthenticatorTest.scala](https://github.com/twitter/finagle/blob/develop/finagle-http/src/test/scala/com/twitter/finagle/http/SpnegoAuthenticatorTest.scala)

*SPNego in Java:*

* [Oracle - Secure Authentication Using SPNEGO Java GSS Mechanism](https://docs.oracle.com/javase/10/security/part-v-secure-authentication-using-spnego-java-gss-mechanism.htm#JSSEC-GUID-B51B4169-BD5D-4A19-BC2B-7F6B3ABB9B7A)
* [SourceForge - JaasLounge](http://jaaslounge.sourceforge.net)
* [SourceForge - Spnego](http://spnego.sourceforge.net)

*SPNEGO protocol:*

* [Microsoft - HTTP-Based Cross-Platform Authentication by Using the Negotiate Protocol](https://msdn.microsoft.com/en-us/library/ms995330.aspx)
* [IBM - Single sign-on for HTTP requests using SPNEGO web authentication (1)](https://www.ibm.com/support/knowledgecenter/SSEQTP_liberty/com.ibm.websphere.wlp.doc/ae/cwlp_spnego.html)
* [IBM - Single sign-on for HTTP requests using SPNEGO web authentication (2)](https://www.ibm.com/support/knowledgecenter/en/SSAW57_8.5.5/com.ibm.websphere.nd.multiplatform.doc/ae/csec_SPNEGO_explain.html)
