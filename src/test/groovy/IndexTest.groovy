import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.bonitasoft.console.common.server.page.PageContext
import org.bonitasoft.console.common.server.page.PageResourceProvider
import org.bonitasoft.console.common.server.page.RestApiResponse
import org.bonitasoft.console.common.server.page.RestApiResponseBuilder
import org.bonitasoft.console.common.server.page.RestApiUtil
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.util.logging.Logger

/**
 * @author Laurent Leseigneur
 */
class IndexTest extends Specification {

    def request = Mock(HttpServletRequest)
    def pageResourceProvider = Mock(PageResourceProvider)
    def pageContext = Mock(PageContext)
    def apiResponseBuilder = new RestApiResponseBuilder()
    def restApiUtil = Mock(RestApiUtil)
    def logger = Mock(Logger)
    def sql = Mock(Sql)

    def index = Spy(Index)


    def "rest api call should return data"() {
        request.parameterNames >> (["queryId"] as Enumeration)
        request.getParameter("queryId") >> "getClients"

        pageResourceProvider.getResourceAsStream("queries.properties") >> index.class.classLoader.getResourceAsStream("queries.properties")
        pageResourceProvider.getResourceAsStream("datasource.properties") >> index.class.classLoader.getResourceAsStream("datasource.properties")

        index.buildSql(pageResourceProvider) >> sql
        def results = new ArrayList<>()
        results.add(["clientId": "894184d6-0930-11e5-a6c0-1697f925ec7b", "first_name": "William", "last_name": "Jobs", "city": "Grenoble", "country": "France"])
        sql.rows("SELECT first_name, last_name, city, country FROM client") >> results

        when:
        index.doHandle(request, pageResourceProvider, pageContext, apiResponseBuilder, restApiUtil)

        then:
        def returnedJson = new JsonSlurper().parseText(apiResponseBuilder.build().response)
        returnedJson[0].clientId == "894184d6-0930-11e5-a6c0-1697f925ec7b"
        returnedJson[0].first_name == "William"
        returnedJson[0].last_name == "Jobs"
        returnedJson[0].city == "Grenoble"
        returnedJson[0].country == "France"

    }

    def "rest api call without query id should return error"() {
        request.parameterNames >> (["wrongParameter"] as Enumeration)
        restApiUtil.logger >> logger

        when:
        index.doHandle(request, pageResourceProvider, pageContext, apiResponseBuilder, restApiUtil)

        then:
        1 * logger.severe("the parameter queryId is missing")
        RestApiResponse restApiResponse = apiResponseBuilder.build()
        restApiResponse.httpStatus == 400
        restApiResponse.response == ["error": "the parameter queryId is missing"]

    }

    def "rest api call wrong queryId should return error"() {
        request.parameterNames >> (["queryId"] as Enumeration)
        request.getParameter("queryId") >> "wrongQuery"
        pageResourceProvider.getResourceAsStream("queries.properties") >> index.class.classLoader.getResourceAsStream("queries.properties")
        restApiUtil.logger >> logger

        when:
        index.doHandle(request, pageResourceProvider, pageContext, apiResponseBuilder, restApiUtil)

        then:
        1 * logger.severe("the queryId does not refer to an existing query")
        RestApiResponse restApiResponse = apiResponseBuilder.build()
        restApiResponse.httpStatus == 400
        restApiResponse.response == ["error": "the queryId does not refer to an existing query"]

    }

    def "error message should be logged"() {

        when:
        index.buildErrorResponse(apiResponseBuilder, "ugly exception", logger)

        then:
        1 * logger.severe("ugly exception")
    }

    def "when error occurs bad request status should be returned"() {

        when:
        def apiResponse = index.buildErrorResponse(apiResponseBuilder, "ugly exception", logger)

        then:
        apiResponse.response == ["error": "ugly exception"]
        apiResponse.httpStatus == 400
    }


    def "response should contain result"() {
        when:
        def apiResponse = index.buildResponse(apiResponseBuilder, "{result:value}")

        then:
        apiResponse.response == "{result:value}"
        apiResponse.httpStatus == 200


    }

    def "sql parameters should remove queryId"() {
        request.parameterNames >> (["queryId", "country"] as Enumeration)
        request.getParameter("country") >> "FR"

        when:
        def map = index.getSqlParameters(request)

        then:
        map == ["country": "FR"]

    }

    def "BuildSql"() {

    }

    def "should get query from queryId parameter"() {
        pageResourceProvider.getResourceAsStream("queries.properties") >> index.class.classLoader.getResourceAsStream("queries.properties")

        when:
        def clientQueryByCountry = index.getQuery("getClientsByCountry", pageResourceProvider)

        then:
        clientQueryByCountry == "SELECT first_name, last_name, city, country FROM client WHERE country =:country"
    }

    def "LoadProperties"() {

    }
}
