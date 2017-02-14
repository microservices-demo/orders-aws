package works.weave.socks.aws.orders.domain.repository

import java.net.URI
import works.weave.socks.aws.orders.domain.repository.AddressRepository.Address

trait AddressRepository {
  def findByURI(uri : URI) : Address
}

object AddressRepository {

  case class Address(
    number : String,
    street : String,
    city : String,
    postcode : String,
    country : String)

}