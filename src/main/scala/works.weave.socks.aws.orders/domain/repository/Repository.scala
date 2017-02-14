package works.weave.socks.aws.orders.domain.repository

/**
  * Minimal repository
  */
trait Repository[K, V] {

  def find(key : K) : Option[V]

}
