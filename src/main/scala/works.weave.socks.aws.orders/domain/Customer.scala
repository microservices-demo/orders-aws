package works.weave.socks.aws.orders.domain

import java.util.UUID

case class Customer(
  id : UUID,
  firstName : String,
  lastName : String,
  username : String)
// List<Address> addresses
// List<Card> cards

