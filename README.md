# SPNEGO Server Finatra

HTTP/SPNEGO with Kerberos authentication for Finatra web server.

**Installation**

*Configure your favorite web browser*:

* Firefox:

Go to `about:config`, click on `I accept the risk` button, filter in search bar by `negotiate` and 
put in `network.negotiate-auth.trusted-uris` value `127.0.0.1:8000` (or more generally address of your web server).

**Usage**

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