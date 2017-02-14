package works.weave.socks.spring.aws

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._
import works.weave.socks.spring.Ops._

abstract class DynamoSchema(dynamoConnection : DynamoConfiguration) {

  val LOG = LoggerFactory.getLogger(getClass)

  def createMissing(client : AmazonDynamoDBClient) : Unit = {
    val tableNames = client.listTables().getTableNames.asScala.toSet

    schema { table =>
      val name = table.getTableName
      if (tableNames contains name) {
        LOG.info("Table '{}' present", name)
      } else {
        LOG.info("Table '{}' missing, creating...", name)
        client.createTable(table)
        LOG.info("Table '{}' created", name)
      }
    }
  }

  /**
    * @param timeout max time waiting (excludes the actuall polling effort)
    * @param f procedure that should return true
    * @return
    */
  def pollWithTimeout(timeout : Int) = new {
    def until(f : => Boolean) : Boolean = {
      def loop(timeoutBudget : Int)(delayMillis : Int) : Boolean = {
        if (f) {
          true
        } else if (timeoutBudget <= 0) {
          false
        } else {
          Thread.sleep(Math.min(timeoutBudget, delayMillis))
          loop(timeoutBudget - delayMillis)(delayMillis * 2)
        }
      }
      loop(timeout)(10)
    }
  }

  def resetDestructively(client : AmazonDynamoDBClient) : Unit = {
    val tableNames = client.listTables().getTableNames.asScala.toSet

    schema { table =>
      val name = table.getTableName
      if (tableNames contains name) {
        LOG.info("Table '{}' present, destroying...", name)

        client.deleteTable(name)
        LOG.info("Awaiting deletion")
        pollWithTimeout(60000) until {
          try {
            client.describeTable(name)
            false
          } catch {
            case e : ResourceNotFoundException =>
              true
          }
        }
        //client.describeTable(name).table.tableStatus.

      }

      LOG.info("Table '{}' creating...", name)
      client.createTable(table)
      LOG.info("Table '{}' created", name)

    }
  }

  protected def schema(declare : CreateTableRequest => Any) : Unit

  val hash = KeyType.HASH
  val range = KeyType.RANGE

  final protected def keySchemaElement(name : String, keyType : KeyType) =
    new KeySchemaElement(name, keyType)

  final protected def attributeDefinition(name : String, scalarAttributeType : ScalarAttributeType) =
    new AttributeDefinition(name, scalarAttributeType)

  final protected def table(name : String,
    attributeDefinitions : Seq[AttributeDefinition],
    keySchema : Seq[KeySchemaElement],
    provisionedThrougput : ProvisionedThroughput) : CreateTableRequest = (new CreateTableRequest()
    after (_.setTableName(name))
    after (_.setAttributeDefinitions(attributeDefinitions.asJava))
    after (_.setKeySchema(keySchema.asJava))
    after (_.setProvisionedThroughput(provisionedThrougput)))

}
