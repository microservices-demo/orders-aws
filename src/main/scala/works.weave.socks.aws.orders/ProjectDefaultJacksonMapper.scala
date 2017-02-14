package works.weave.socks.aws.orders

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object ProjectDefaultJacksonMapper {
  def build() : ObjectMapper = {
    val mapper = new ObjectMapper()

    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    val javaTime : JavaTimeModule = new JavaTimeModule
    mapper.registerModule(javaTime)

    val scalaModule = new DefaultScalaModule()
    mapper.registerModule(scalaModule)

    mapper
  }
}
