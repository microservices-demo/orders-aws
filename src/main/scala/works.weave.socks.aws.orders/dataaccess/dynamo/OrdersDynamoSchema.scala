package works.weave.socks.aws.orders.dataaccess.dynamo

import com.amazonaws.services.dynamodbv2.model._
import org.springframework.stereotype.Component
import works.weave.socks.spring.aws.DynamoConfiguration
import works.weave.socks.spring.aws.DynamoSchema

/**
  * Declares the DynamoDB schema
  */
@Component
class OrdersDynamoSchema(dynamoConnection : DynamoConfiguration) extends DynamoSchema(dynamoConnection) {

  override protected def schema(declare : (CreateTableRequest) => Any) : Unit = {

    declare(
      table(name = "orders",
        attributeDefinitions = Seq(
          attributeDefinition("id", ScalarAttributeType.S),
          attributeDefinition("customerId", ScalarAttributeType.S)),
        keySchema = Seq(
          keySchemaElement("id", KeyType.HASH),
          keySchemaElement("customerId", KeyType.RANGE)),
        provisionedThrougput = new ProvisionedThroughput(1L, 1L)))

  }
}
