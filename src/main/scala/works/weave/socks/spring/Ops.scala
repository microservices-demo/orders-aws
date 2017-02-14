package works.weave.socks.spring

/**
  * Extra methods.
  */
object Ops {
  implicit class AnyOps[A](a : A) {
    /**
      * Perform a side effecting operation on an object and then return the object.
      *
      * @param f side effecting operation
      * @tparam B result of f, thrown away
      * @return the same object after the side effect
      */
    def after[B](f : A => B) : A = {
      f(a); a
    }
  }
}
