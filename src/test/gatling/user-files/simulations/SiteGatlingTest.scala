import java.nio.charset.StandardCharsets
import java.util.Base64

import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
 * Performance test for the Site entity.
 */
class SiteGatlingTest extends Simulation {

    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    // Log all HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
    // Log failed HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

    val baseURL = Option(System.getProperty("baseURL")) getOrElse """http://localhost:8080"""

    val httpConf = http
        .baseURL(baseURL)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")
        .silentResources // Silence all resources like css or css so they don't clutter the results

    val headers_http = Map(
        "Accept" -> """application/json"""
    )

    val authorization_header = "Basic " + Base64.getEncoder.encodeToString("sitesapp:bXktc2VjcmV0LXRva2VuLXRvLWNoYW5nZS1pbi1wcm9kdWN0aW9uLWFuZC10by1rZWVwLWluLWEtc2VjdXJlLXBsYWNl".getBytes(StandardCharsets.UTF_8))

    val headers_http_authentication = Map(
        "Content-Type" -> """application/x-www-form-urlencoded""",
        "Accept" -> """application/json""",
        "Authorization"-> authorization_header
    )

    val headers_http_authenticated = Map(
        "Accept" -> """application/json""",
        "Authorization" -> "Bearer ${access_token}"
    )

    val scn = scenario("Test the Site entity")
        .exec(http("First unauthenticated request")
        .get("/api/account")
        .headers(headers_http)
        .check(status.is(401))
        ).exitHereIfFailed
        .pause(10)
        .exec(http("Authentication")
        .post("/oauth/token")
        .headers(headers_http_authentication)
        .formParam("username", "admin")
        .formParam("password", "admin")
        .formParam("grant_type", "password")
        .formParam("scope", "read write")
        .formParam("client_secret", "bXktc2VjcmV0LXRva2VuLXRvLWNoYW5nZS1pbi1wcm9kdWN0aW9uLWFuZC10by1rZWVwLWluLWEtc2VjdXJlLXBsYWNl")
        .formParam("client_id", "sitesapp")
        .formParam("submit", "Login")
        .check(jsonPath("$.access_token").saveAs("access_token"))).exitHereIfFailed
        .pause(2)
        .exec(http("Authenticated request")
        .get("/api/account")
        .headers(headers_http_authenticated)
        .check(status.is(200)))
        .pause(10)
        .repeat(2) {
            exec(http("Get all sites")
            .get("/sites/api/sites")
            .headers(headers_http_authenticated)
            .check(status.is(200)))
            .pause(10 seconds, 20 seconds)
            .exec(http("Create new site")
            .post("/sites/api/sites")
            .headers(headers_http_authenticated)
            .body(StringBody("""{
                "id":null
                , "budderflyId":"SAMPLE_TEXT"
                , "customerName":"SAMPLE_TEXT"
                , "status":"ACTIVE"
                , "companyType":"SAMPLE_TEXT"
                , "storeNumber":"SAMPLE_TEXT"
                , "address":"SAMPLE_TEXT"
                , "city":"SAMPLE_TEXT"
                , "state":"SAMPLE_TEXT"
                , "zip":"SAMPLE_TEXT"
                , "billingType":"AMU-Forward"
                , "paymentType":"ETF"
                , "siteType":"Physical"
                , "ownerName":"SAMPLE_TEXT"
                , "ownerEmail":"SAMPLE_TEXT"
                , "ownerPhone":"SAMPLE_TEXT"
                , "address1":"SAMPLE_TEXT"
                , "address2":"SAMPLE_TEXT"
                , "latitude":"SAMPLE_TEXT"
                , "longitude":"SAMPLE_TEXT"
                , "taxExempt":null
                , "rollBilling":null
                , "emoVersion":"SAMPLE_TEXT"
                , "billingContact":"SAMPLE_TEXT"
                , "siteContact":"SAMPLE_TEXT"
                , "franchiseContact":"SAMPLE_TEXT"
                }""")).asJSON
            .check(status.is(201))
            .check(headerRegex("Location", "(.*)").saveAs("new_site_url"))).exitHereIfFailed
            .pause(10)
            .repeat(5) {
                exec(http("Get created site")
                .get("/sites${new_site_url}")
                .headers(headers_http_authenticated))
                .pause(10)
            }
            .exec(http("Delete created site")
            .delete("/sites${new_site_url}")
            .headers(headers_http_authenticated))
            .pause(10)
        }

    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(rampUsers(Integer.getInteger("users", 100)) over (Integer.getInteger("ramp", 1) minutes))
    ).protocols(httpConf)
}
