package com.example


import com.twitter.finagle.http.SpnegoAuthenticator.{Authenticated, Credentials, ServerFilter}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

class SpnegoFilter extends SimpleFilter[Request, Response] {

  val serverSrc = new Credentials.JAASServerSource("server")

  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request)
    //ServerFilter(serverSrc).apply(request, null)
    /*val authService = new Service[Authenticated[Request], Response] {
      def apply(auth: Authenticated[Request]): Future[Response] = {
        service(request)
      }
    }*/
  }

}
