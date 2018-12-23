package com.example.controller

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class HealthCheckController extends Controller {

  get("/check/?") { _: Request =>
    "Alive"
  }
}
