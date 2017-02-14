package works.weave.socks.aws.orders.dataaccess.web

import java.net.URI
import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.domain.repository.CustomerRepository
import works.weave.socks.aws.orders.domain.repository.CustomerRepository.Customer

@Component
class WebCustomerRepository extends CustomerRepository {
  override def findByURI(uri : URI) : Customer = {
    JSONHTTP.get[Customer](uri)
  }
}
