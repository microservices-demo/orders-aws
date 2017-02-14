package works.weave.socks.aws.orders.domain

import java.time.LocalDateTime
import java.util.UUID

case class CustomerOrder(
  id : UUID,
  customerId : UUID,

  // address : Address

  // card : Card

  // items : List<Items>

  // shipment : Shipment

  date : LocalDateTime,
  total : Float)
