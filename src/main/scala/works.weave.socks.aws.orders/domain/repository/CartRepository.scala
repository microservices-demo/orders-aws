package works.weave.socks.aws.orders.domain.repository

import java.net.URI
import works.weave.socks.aws.orders.domain.repository.CartRepository.OrderItem

trait CartRepository {
  def findItemsByURI(items : URI) : List[OrderItem]
}
object CartRepository {
  case class OrderItem(id : String,
    itemId : String,
    quantity : Integer,
    unitPrice : Number)
}
