package com.glegoux.spnego

import com.twitter.finagle.http.Status
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class SpnegoServerStartupTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new SpnegoServer())

  test("SpnegoServerStartup") {
    server.httpGet(
      path = "/",
      andExpect = Status.Unauthorized)

  }
}
