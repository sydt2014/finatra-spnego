package com.example

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.http.SpnegoAuthenticator.Authenticated.Http
import com.twitter.finagle.http.SpnegoAuthenticator.Credentials.ServerSource
import com.twitter.finagle.http.SpnegoAuthenticator.{Authenticated, Credentials, ServerFilter}
import com.twitter.finatra.http.Controller
import com.twitter.util.Future
import org.ietf.jgss.GSSContext

class SpnegoController extends Controller {

  filter(new SpnegoFilter()).get("/") { request: Request =>
    //val serverSource: ServerSource = new Credentials.JAASServerSource("server")
    //val context: GSSContext = serverSource.load().toJavaFuture.get()
    //val authenticatedRequest: Authenticated[Request] = Http(request, context)
    //ServerFilter(serverSource)
    response.ok.html("<h1>Hello, world!</h1>")
    // val authService = new Service[Authenticated[Request], Response] {
    // def apply(auth: Authenticated[Request]): Future[Response] = {
    //    service(request)
    //  }
    //}
  }

}
