package works.weave.socks

import javax.inject.Inject
import scala.annotation.meta.beanSetter

package object spring {

  /**
    * Creates a spring injection setter.
    */
  type inject = Inject @beanSetter

}
