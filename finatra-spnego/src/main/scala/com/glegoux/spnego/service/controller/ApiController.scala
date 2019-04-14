package com.glegoux.spnego.controller

import com.glegoux.spnego.filter.SpnegoFilter
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class ApiController extends Controller {

  filter(new SpnegoFilter()).prefix("/api") {

    get("/auth/?") { request: Request =>
      val clientPrincipal: String = request.headerMap.get("X-client-principal").getOrElse("undefined")
      s"Hello $clientPrincipal!"
    }

  }

}
