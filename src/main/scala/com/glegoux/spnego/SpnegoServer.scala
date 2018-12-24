package com.glegoux.spnego

import com.glegoux.spnego.controller.{ApiController, AssetController, ConfigController, HealthCheckController}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter

object SpnegoServerMain extends SpnegoServer

class SpnegoServer extends HttpServer {

  override protected def defaultFinatraHttpPort = ":8000"

  override protected def disableAdminHttpServer = true

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[HealthCheckController]
      .add[ConfigController]
      .add[ApiController]
      .add[AssetController]
  }
}
