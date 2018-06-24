package com.example

import com.twitter.finagle.http.SpnegoAuthenticator.Credentials.ServerSource
import com.twitter.finagle.http.SpnegoAuthenticator.{Authenticated, Credentials, ServerFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.logging.Logger
import com.twitter.util.Future

class SpnegoFilter extends SimpleFilter[Request, Response] {
  private val log = Logger("SpnegoFilter")

  val serverSource: ServerSource = new Credentials.JAASServerSource("server")

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val authService = new Service[Authenticated[Request], Response] {
      def apply(auth: Authenticated[Request]): Future[Response] = {
        log.info("Client principal: " + auth.context.getSrcName)
        service(request)
      }
    }
    ServerFilter(serverSource).apply(request, authService)
  }
}
