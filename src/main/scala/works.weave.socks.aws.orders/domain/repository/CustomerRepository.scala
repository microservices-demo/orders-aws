package works.weave.socks.aws.orders.domain.repository

import java.net.URI
import works.weave.socks.aws.orders.domain.repository.CustomerRepository.Customer

trait CustomerRepository {
  def findByURI(customer : URI) : Customer
}
object CustomerRepository {
  case class Customer(firstName : String, lastName : String, username : String)
}
