package com.glegoux.spnego.filter

import com.twitter.finagle.http.SpnegoAuthenticator.Credentials.ServerSource
import com.twitter.finagle.http.SpnegoAuthenticator.{Authenticated, Credentials, ServerFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.logging.Logger
import com.twitter.util.Future
import org.ietf.jgss.GSSName

class SpnegoFilter extends SimpleFilter[Request, Response] {
  private val log = Logger("SpnegoFilter")

  val serverSource: ServerSource = new Credentials.JAASServerSource("service")

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val authService = new Service[Authenticated[Request], Response] {
      def apply(auth: Authenticated[Request]): Future[Response] = {
        val clientGssName: GSSName = auth.context.getSrcName
        val clientPrincipal: Option[String] = if (clientGssName != null) Some(clientGssName.toString) else None
        if (clientPrincipal.isEmpty) {
          log.error("Client principal is null")
        } else {
          log.info(s"Client principal is $clientPrincipal")
          request.headerMap.set("X-client-principal", clientPrincipal.get)
        }
        service(request)
      }
    }
    ServerFilter(serverSource).apply(request, authService)
  }
}
