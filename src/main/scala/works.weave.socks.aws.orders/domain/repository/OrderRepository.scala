package works.weave.socks.aws.orders.domain.repository

import java.time.LocalDateTime
import java.util.UUID
import works.weave.socks.aws.orders.domain.repository.OrderRepository.Order

trait OrderRepository extends Repository[UUID, Order] {
  def save(order : Order)

  def searchByCustomerId(customerId : String) : List[Order]
}

object OrderRepository {

  case class Order(
    id : UUID,
    customerId : String,
    customer : Customer,

    // address : Address

    // card : Card

    // items : List<Items>

    // shipment : Shipment

    date : LocalDateTime,
    total : Float)

  // FIXME: add some fields
  case class Customer()

}