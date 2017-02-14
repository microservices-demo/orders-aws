package works.weave.socks.aws.orders.dataaccess.web

import com.amazonaws.util.IOUtils
import com.fasterxml.jackson.core.`type`.TypeReference
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URI
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import works.weave.socks.aws.orders.ProjectDefaultJacksonMapper

object JSONHTTP {

  val Log = LoggerFactory.getLogger(getClass)

  val objectMapper = ProjectDefaultJacksonMapper.build() // after (_.bla)

  def get[T : Manifest : NotNothing](uri : URI) : T = {
    val client = HttpClientBuilder.create.build
    val get = new HttpGet(uri)
    get.addHeader("Accept", "application/json")
    val response = client.execute(get)
    val responseString = IOUtils.toString(response.getEntity.getContent)
    Log.info(s"Got status ${response.getStatusLine.getStatusCode}")
    require(response.getStatusLine.getStatusCode == 200)
    Log.info(s"Got response from URI $uri: $responseString")
    objectMapper.readValue(responseString, typeReference[T])
  }

  def typeReference[T : Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  def typeFromManifest(m : Manifest[_]) : Type = {
    if (m.typeArguments.isEmpty) { m.runtimeClass }
    else new ParameterizedType {
      def getRawType = m.runtimeClass
      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray
      def getOwnerType = null
    }
  }

  // Trick to disable nothign for type param
  sealed trait NotNothing[-T]

  object NotNothing {
    implicit object notNothing extends NotNothing[Any]
    implicit object `The error is because the missing type parameter was resolved to Nothing` extends NotNothing[Nothing]
  }

}
