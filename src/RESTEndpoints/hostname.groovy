import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript

import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

@BaseScript CustomEndpointDelegate delegate

hostname(httpMethod: "GET") { MultivaluedMap queryParams, String body ->
	return Response.ok(InetAddress.getLocalHost().toString()).header("Content-Type", "text/plain").build()
}
