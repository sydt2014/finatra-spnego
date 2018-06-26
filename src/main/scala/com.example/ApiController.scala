package com.example

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class ApiController extends Controller {

  prefix("/api") {

    filter(new SpnegoFilter()).get("/hello/?") { request: Request =>
      val clientPrincipal: String = request.headerMap.get("X-client-principal").getOrElse("undefined")
      s"Hello $clientPrincipal!"
    }

    get("/check/?") { request: Request =>
      "Alive"
    }

  }

}
