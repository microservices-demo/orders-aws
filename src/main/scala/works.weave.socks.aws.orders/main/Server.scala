package works.weave.socks.aws.orders.main

import org.eclipse.jetty
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import scala.collection.JavaConverters._
import scala.util.control.NonFatal
import Server._

@Component
class Server(val jerseyApp : JerseyApp) {

  def run() : Unit = {

    val port = System.getenv().asScala.getOrElse("PORT", "80").toInt

    val context = new ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")

    val jettyServer = new jetty.server.Server(port)
    jettyServer.setHandler(context)

    val servletContainer = new ServletContainer(jerseyApp)
    val servletHolder = new ServletHolder(servletContainer)
    context.addServlet(servletHolder, "/*")
    val jerseyServlet = servletHolder
    jerseyServlet.setInitOrder(0)

    try {
      Log.info("Starting jetty")
      jettyServer.start()
      Log.info("Jetty started")
      jettyServer.join()
      Log.info("Jetty exited")
    } finally {
      Log.info("Destroying jetty")
      try {
        jettyServer.destroy()
      } catch {
        case NonFatal(e) => Log.error("Error during Jetty shutdown", e)
      }
    }
  }
}

object Server {
  val Log = LoggerFactory.getLogger(classOf[Server])
}

