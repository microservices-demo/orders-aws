package works.weave.socks.spring.aws

import com.amazonaws.ClientConfigurationFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import org.springframework.stereotype.Component
import scala.collection.JavaConverters._
import works.weave.socks.spring.Ops._

@Component
class DynamoConfiguration {

  lazy val client : AmazonDynamoDBClient = {
    new AmazonDynamoDBClient(DefaultAWSCredentialsProviderChain.getInstance(), new ClientConfigurationFactory().getConfig)
      .after(_.setEndpoint(endpoint))
  }

  private lazy val endpoint =
    System.getenv().asScala.getOrElse("AWS_DYNAMODB_ENDPOINT", "http://dynamo:8000")

}
