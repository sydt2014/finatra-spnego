package com.example

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class SpnegoController extends Controller {

  filter(new SpnegoFilter()).get("/") { request: Request =>
    response.ok.html("<h1>Hello, world!</h1>")
  }

}
