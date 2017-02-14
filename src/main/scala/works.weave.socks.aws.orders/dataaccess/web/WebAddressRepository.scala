package works.weave.socks.aws.orders.dataaccess.web

import java.net.URI
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.domain.repository.AddressRepository
import works.weave.socks.aws.orders.domain.repository.AddressRepository.Address

@Component
class WebAddressRepository extends AddressRepository {
  override def findByURI(uri : URI) : Address = {
    JSONHTTP.get[Address](uri)
  }
}
