package works.weave.socks.aws.orders.main

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import scala.reflect.ClassTag
import works.weave.socks.spring.aws.DynamoConfiguration
import works.weave.socks.spring.aws.DynamoSchema

/**
  * Entrypoint for the orders web service
  */
object ServiceMain {
  def main(args : Array[String]) : Unit = {

    System.setProperty("org.jboss.logging.provider", "slf4j")

    val appContext = new AnnotationConfigApplicationContext(classOf[Config])
    def bean[T : ClassTag] : T = appContext.getBean(implicitly[ClassTag[T]].runtimeClass).asInstanceOf[T]

    def initSchema() : Unit = {
      val dynamo = bean[DynamoConfiguration]
      bean[DynamoSchema].createMissing(dynamo.client)
    }
    def resetSchema() : Unit = {
      val dynamo = bean[DynamoConfiguration]
      bean[DynamoSchema].resetDestructively(dynamo.client)
    }

    // FIXME: do neither of initSchema, resetSchema
    //initSchema()
    //resetSchema()

    bean[Server].run()
  }

  @ComponentScan(basePackages = Array("works.weave.socks.aws.orders", "works.weave.socks.spring"))
  class Config {
  }
}
