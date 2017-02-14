package works.weave.socks.aws.orders.domain

trait GOrderTypes {
  type Customer
  type Address
  type Card
  type Items
  type Shipment
}

// Generalized Order
trait GOrder[T <: GOrderTypes] {
  def id : String
  def customerId : String
  def customer : T#Customer // Swagger: OrderCustomer,
  def address : T#Address // Swagger: OrderAddress,
  def card : T#Card // Swagger: OrderCard,
  def items : T#Items // Swagger: List[OrderItems],
  def shipment : T#Shipment
  def date : String
  def total : Number
}
