package com.glegoux.spnego.controller

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class ConfigController extends Controller {

  prefix("/config") {

    get("/jaas-krb5.conf") { _: Request =>
      response.ok.file("/kerberos/service/jaas-krb5.conf")
    }

    get("/krb5.conf") { _: Request =>
      response.ok.file("/kerberos/service/krb5.conf")
    }

  }

}
