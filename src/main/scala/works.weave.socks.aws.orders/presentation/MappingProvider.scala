package works.weave.socks.aws.orders.presentation

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.ext.Provider
import works.weave.socks.aws.orders.ProjectDefaultJacksonMapper

@Provider
@Produces(Array(MediaType.APPLICATION_JSON))
class MappingProvider extends JacksonJaxbJsonProvider {
  setMapper(MappingProvider.mapper)
}
object MappingProvider {
  val mapper = {
    val presentationMapper = ProjectDefaultJacksonMapper.build()
    // modify as necessary for presentation purpose
    presentationMapper
  }

}