package works.weave.socks.aws.orders.presentation.resource

import java.net.URI
import java.util.Calendar;
import java.util.Date;
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import works.weave.socks.aws.orders.presentation.resource.HealthCheckResource._
import works.weave.socks.spring.aws.DynamoConfiguration

@Component
@Path("health")
class HealthCheckResource(dynamoConnection : DynamoConfiguration) {

  @GET
  def getHealthCheck() : Response = {
    Log.info("health check requested")
    val dateNow = Calendar.getInstance().getTime();

    val ordersHealth = Map(
      "service" -> "orders-aws",
      "status" -> "OK",
      "time" -> dateNow)
    val dynamoDBHealth = scala.collection.mutable.Map(
      "service" -> "orders-aws-dynamodb",
      "status" -> "OK",
      "time" -> dateNow)

    try {
      val table = dynamoConnection.client.describeTable("orders")
    } catch {
      case unknown : Throwable => dynamoDBHealth("status") = "err"
    }

    val map = Map("health" -> Array(ordersHealth, dynamoDBHealth))
    Log.info("health check completed")
    Response.created(new URI("http://tbd")).entity(map).build()
  }

}
object HealthCheckResource {
  val Log = LoggerFactory.getLogger(classOf[HealthCheckResource])
}
