package works.weave.socks.aws.orders.dataaccess.dynamo

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemResult
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.stereotype.Component
import scala.collection.JavaConverters._
import works.weave.socks.aws.orders.domain.repository.OrderRepository
import works.weave.socks.aws.orders.domain.repository.OrderRepository.Customer
import works.weave.socks.aws.orders.domain.repository.OrderRepository.Order
import DynamoOrderRepository._
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import works.weave.socks.spring.aws.DynamoConfiguration

@Component
class DynamoOrderRepository(dynamoConnection : DynamoConfiguration) extends OrderRepository {

  override def find(key : UUID) : Option[Order] = {
    val r : Option[GetItemResult] = Option(dynamoConnection.client.getItem(OrdersTableName, Map("id" -> new AttributeValue(key.toString)).asJava))

    r.map(rr => fromDB(rr.getItem))
  }

  def customerFromJSON(json : String) : OrderRepository.Customer = {
    Customer()
  }
  def customerToJSON(customer : OrderRepository.Customer) : String = {
    "{}"
  }

  private def fromDB(map : java.util.Map[String, AttributeValue]) : Order = {
    Order(
      id = UUID.fromString(map.get("id").getS),
      customerId = map.get("id").getS,
      customer = customerFromJSON(map.get("customer").getS),
      date = LocalDateTime.parse(map.get("date").getS),
      total = map.get("total").getS.toFloat)
  }

  override def save(order : OrderRepository.Order) : Unit = {

    require(order.customerId != null)

    val _ = dynamoConnection.client.putItem(OrdersTableName, Map(
      "id" -> new AttributeValue(order.id.toString),
      "date" -> new AttributeValue(order.date.toString),
      "customerId" -> new AttributeValue(order.customerId),
      "customer" -> new AttributeValue(customerToJSON(order.customer)),
      "total" -> new AttributeValue(order.total.toString)).asJava)
  }

  override def searchByCustomerId(customerId : String) : List[Order] = {
    /*val r = dynamoConnection.client.query(
      new QueryRequest(OrdersTableName).withKeyConditions(Map("customerId" -> new Condition()
        .withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(List(new AttributeValue(customerId)).asJava)).asJava))*/

    // FIXME: performance
    val r = dynamoConnection.client.scan(new ScanRequest(OrdersTableName).addScanFilterEntry("customerId",
      new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(List(new AttributeValue(customerId)).asJava)))
    r.getItems.asScala.toList map fromDB
  }
}
object DynamoOrderRepository {
  val OrdersTableName = "orders"
}
