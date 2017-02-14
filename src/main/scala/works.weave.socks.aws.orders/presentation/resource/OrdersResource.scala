package works.weave.socks.aws.orders.presentation.resource

import java.net.URI
import java.time.LocalDateTime
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.domain.OrderPlacementService
import works.weave.socks.aws.orders.domain.OrderRetrievalService
import works.weave.socks.aws.orders.presentation.resource.OrdersResource._
import works.weave.socks.aws.orders.presentation.value.OrderRequest
import works.weave.socks.aws.orders.presentation.value.OrdersList
import works.weave.socks.aws.orders.presentation.value._

@Component
@Path("/orders")
class OrdersResource(
    orderPlacementService : OrderPlacementService,
    orderRetrievalService : OrderRetrievalService) {

  val dummyShipment = OrderShipment(id = "dummy shipment has no idea", "dummy shipment name")

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  def orders : OrdersList = {

    Log.debug("GET /orders handler running")

    val order = Order(id = "id",
      customerId = "custId",
      customer = OrderCustomer(
        firstName = "John",
        lastName = "Doe",
        username = "jdoe",
        addresses = Nil,
        cards = Nil),
      address = OrderAddress(
        number = "2 a #3",
        street = "Weaver Street",
        city = "Soxton",
        postcode = "F00B4R",
        country = "New Zealand"),
      card = OrderCard(
        longNum = "1234-5678-9098-7654-3210",
        expires = "11/56",
        ccv = "123"),
      items = List(OrderItems(id = "dummy item", itemId = "dummy product", quantity = 1, unitPrice = 1)),
      shipment = Some(dummyShipment),
      date = LocalDateTime.now().toString,
      total = 42.0f,
      _links = OrderLinks(LinksSelf("http://orders/id_tbd")))

    makeOrdersList(List(order))
  }

  @POST
  @Produces(Array(MediaType.APPLICATION_JSON))
  def postOrder(order : OrderRequest) : Response = {

    Log.debug("POST /orders handler running")

    val response = orderPlacementService.placeOrder(order)

    Log.info("POST /orders response: {}", response)

    Response.created(new URI("http://tbd")).entity(response).build()
  }

  @Path("search/customerId")
  @GET
  def searchByCustomerId(@QueryParam("custId") customerId : String) : OrdersList = {
    // @QueryParam("custId") customerId : String /*, @QueryParam("sort") sort : String */ ) : OrdersList = {

    Log.info("Search by {}", customerId)

    val result = orderRetrievalService.searchByCustomerId(customerId)

    Log.info("Search result: {}", result)

    makeOrdersList(result.map { x =>
      Order(id = x.id.toString,
        customerId = x.customerId,
        customer = null,
        address = null,
        card = null,
        items = null,
        shipment = null,
        date = x.date.toString,
        total = x.total,
        _links = OrderLinks(LinksSelf(
          "http://orders/" + x.id)))
    })
  }

  def makeOrdersList(list : List[Order]) : OrdersList = {
    OrdersList(
      _embedded = OrdersListEmbedded(
        list),
      _links = OrdersListLinks(
        self = LinksSelf("http://orders/orders/FIXME"),
        profile = LinksSelf("http://orders/orders/FIXME"), // FIXME 3×
        search = LinksSelf("http://orders/orders/FIXME")),
      _page = OrdersListPage(
        size = list.size,
        totalElements = list.size,
        totalPages = 1,
        number = list.size))
  }
}
object OrdersResource {
  val Log = LoggerFactory.getLogger(classOf[OrdersResource])
}
