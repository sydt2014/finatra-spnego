package com.example

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class AssetController extends Controller {

  filter(new SpnegoFilter()).get("/:*") { request: Request =>
    response.ok.fileOrIndex(request.params("*"), "index.html")
  }

}
