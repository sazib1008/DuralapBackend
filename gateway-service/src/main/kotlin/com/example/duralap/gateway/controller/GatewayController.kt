package com.example.duralap.gateway.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpStatusCodeException
import org.slf4j.LoggerFactory

@RestController
@CrossOrigin(origins = ["*"])
class GatewayController {

    private val logger = LoggerFactory.getLogger(GatewayController::class.java)
    private val restTemplate = RestTemplate()

    private val servicePorts = mapOf(
        "auth" to 8081,
        "users" to 8082,
        "conversations" to 8083,
        "conversation-requests" to 8083,
        "messages" to 8084,
        "media" to 8085,
        "calls" to 8086,
        "notifications" to 8087,
        "analytics" to 8088,
        "search" to 8089
    )

    @RequestMapping(value = ["/api/{serviceName}/**", "/api/{serviceName}"])
    fun proxyRequest(
        @PathVariable serviceName: String,
        @RequestBody(required = false) body: ByteArray?,
        request: HttpServletRequest
    ): ResponseEntity<ByteArray> {
        val port = servicePorts[serviceName] 
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found".toByteArray())
        
        // Build downstream URI
        val requestUri = request.requestURI
        val queryString = request.queryString
        val targetUrl = "http://localhost:$port$requestUri" + (if (queryString != null) "?$queryString" else "")
        
        logger.info("Proxying request: ${request.method} $requestUri -> $targetUrl")
        
        // Copy headers
        val headers = HttpHeaders()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            // Skip host, accept-encoding, and content-length headers
            if (headerName.equals("host", ignoreCase = true) ||
                headerName.equals("accept-encoding", ignoreCase = true) ||
                headerName.equals("content-length", ignoreCase = true)) continue
            val headerValues = request.getHeaders(headerName)
            while (headerValues.hasMoreElements()) {
                headers.add(headerName, headerValues.nextElement())
            }
        }
        
        val httpMethod = HttpMethod.valueOf(request.method)
        val entity = HttpEntity<ByteArray>(body, headers)
        
        return try {
            val response = restTemplate.exchange(targetUrl, httpMethod, entity, ByteArray::class.java)
            val responseHeaders = HttpHeaders()
            response.headers.forEach { (key, value) ->
                if (!key.equals("transfer-encoding", ignoreCase = true) &&
                    !key.equals("content-encoding", ignoreCase = true) &&
                    !key.equals("content-length", ignoreCase = true)) {
                    responseHeaders[key] = value
                }
            }
            ResponseEntity(response.body, responseHeaders, response.statusCode)
        } catch (ex: HttpStatusCodeException) {
            val responseHeaders = HttpHeaders()
            ex.responseHeaders?.forEach { (key, value) ->
                if (!key.equals("transfer-encoding", ignoreCase = true) &&
                    !key.equals("content-encoding", ignoreCase = true) &&
                    !key.equals("content-length", ignoreCase = true)) {
                    responseHeaders[key] = value
                }
            }
            ResponseEntity(ex.responseBodyAsByteArray, responseHeaders, ex.statusCode)
        } catch (ex: Exception) {
            logger.error("Error proxying request to $targetUrl: ${ex.message}", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Gateway Proxy Error: ${ex.message}".toByteArray())
        }
    }
}
