package works.weave.socks.aws.orders.domain

import org.springframework.stereotype.Component
import works.weave.socks.aws.orders.domain.repository.OrderRepository

@Component
class OrderRetrievalService(
    orderRepository : OrderRepository) {

  def searchByCustomerId(customerId : String) : List[OrderRepository.Order] = {
    orderRepository.searchByCustomerId(customerId)
  }

}
