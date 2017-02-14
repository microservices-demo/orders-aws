package works.weave.socks.aws.orders.domain

import java.net.URI
import java.time.LocalDateTime
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.domain.OrderPlacementService.Log
import works.weave.socks.aws.orders.presentation.value.Order
import works.weave.socks.aws.orders.presentation.value.OrderAddress
import works.weave.socks.aws.orders.presentation.value.OrderCard
import works.weave.socks.aws.orders.presentation.value.OrderCustomer
import works.weave.socks.aws.orders.presentation.value.OrderItems
import works.weave.socks.aws.orders.presentation.value.OrderRequest
import works.weave.socks.aws.orders.presentation.value.OrderShipment
import works.weave.socks.aws.orders.domain.repository.AddressRepository
import works.weave.socks.aws.orders.domain.repository.AddressRepository.Address
import works.weave.socks.aws.orders.domain.repository.CardRepository
import works.weave.socks.aws.orders.domain.repository.CardRepository.Card
import works.weave.socks.aws.orders.domain.repository.CartRepository
import works.weave.socks.aws.orders.domain.repository.CartRepository.OrderItem
import works.weave.socks.aws.orders.domain.repository.CustomerRepository
import works.weave.socks.aws.orders.domain.repository.OrderRepository
import works.weave.socks.aws.orders.presentation.value.OrderLinks
import works.weave.socks.aws.orders.presentation.value.LinksSelf

// FIXME: do not depend on presentation

@Component
class OrderPlacementService(
    orderRepository : OrderRepository,
    addressRepository : AddressRepository,
    cardRepository : CardRepository,
    customerRepository : CustomerRepository,
    cartRepository : CartRepository) {

  def presentCard(card : Card) : OrderCard = {
    OrderCard(longNum = card.longNum, expires = card.expires, ccv = card.ccv)
  }

  def presentAddress(address : Address) : OrderAddress = {
    OrderAddress(number = address.number,
      street = address.street,
      city = address.city,
      postcode = address.postcode,
      country = address.country)
  }

  def presentCustomer(customer : CustomerRepository.Customer) : OrderCustomer = {
    OrderCustomer(firstName = customer.firstName,
      lastName = customer.lastName,
      username = customer.username,
      // Ignoring these
      addresses = Nil,
      cards = Nil)
  }

  def presentItem(item : OrderItem) : OrderItems = {
    OrderItems(id = item.id, itemId = item.itemId, quantity = item.quantity, unitPrice = item.unitPrice)
  }

  /**
    * We want to keep a copy of the customer at the time of order, so here we convert to the internal data format
    */
  def convertCustomer(customer : CustomerRepository.Customer) : OrderRepository.Customer = OrderRepository.Customer()

  def guessId(url : URI) : String = {
    // take last path element
    url.getPath.reverse.takeWhile(_ != '/').reverse
  }

  def placeOrder(orderRequest : OrderRequest) : Order = {
    Log.info("order: {}", orderRequest)

    val address = addressRepository.findByURI(orderRequest.address)
    Log.info("Address: {}", address)

    val card = cardRepository.findByURI(orderRequest.card)
    Log.info("card: {}", card)

    val customer = customerRepository.findByURI(orderRequest.customer)
    Log.info("customer: {}", customer)

    val cart = cartRepository.findItemsByURI(orderRequest.items)
    Log.info("items: {}", cart)

    // FIXME: don't use floating point for money
    val total = cart.map(x => x.quantity.doubleValue() * x.unitPrice.doubleValue()).sum

    val order = OrderRepository.Order(
      id = UUID.randomUUID(),
      customerId = guessId(orderRequest.customer),
      customer = convertCustomer(customer),
      date = LocalDateTime.now(),
      total = total.toFloat)

    Log.info("new order: {}", order)

    orderRepository.save(order)

    Order(
      id = order.id.toString, // FIXME
      customerId = orderRequest.customer.toString,
      customer = presentCustomer(customer),
      address = presentAddress(address),
      card = presentCard(card),
      items = cart.map(presentItem),
      shipment = Some(OrderShipment(id = "hello", name = "world")), // FIXME
      date = LocalDateTime.now().toString,
      total = total,
      _links = OrderLinks(LinksSelf("http://orders/" + order.id.toString)))
  }

}
object OrderPlacementService {
  val Log = LoggerFactory.getLogger(classOf[OrderPlacementService])
}