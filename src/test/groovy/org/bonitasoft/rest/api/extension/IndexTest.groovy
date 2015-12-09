package org.bonitasoft.rest.api.extension

import groovy.json.JsonSlurper
import groovy.sql.Sql

import javax.servlet.http.HttpServletRequest

import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestAPIContext
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder

import spock.lang.Specification

/**
 * @author Laurent Leseigneur
 */
class IndexTest extends Specification {

    def httpRequest = Mock(HttpServletRequest)
    def resourceProvider = Mock(ResourceProvider)
    def context = Mock(RestAPIContext)
    def sql = Mock(Sql)

    def index = Spy(Index)

    def setup(){
        resourceProvider.getResourceAsStream("queries.properties") >> Index.class.classLoader.getResourceAsStream("queries.properties")
        resourceProvider.getResourceAsStream("datasource.properties") >> Index.class.classLoader.getResourceAsStream("datasource.properties")
        context.resourceProvider >> resourceProvider;
        index.buildSql(resourceProvider) >> sql
    }

    def "rest api call should return data"() {
        httpRequest.parameterNames >> new StringTokenizer("queryId")
        httpRequest.getParameter("queryId") >> "getClients"

        def results = new ArrayList<>()
        results.add(["clientId": "894184d6-0930-11e5-a6c0-1697f925ec7b", "first_name": "William", "last_name": "Jobs", "city": "Grenoble", "country": "France"])
        sql.rows("SELECT first_name, last_name, city, country FROM client") >> results

        when:
        def apiResponse = index.doHandle(httpRequest, new RestApiResponseBuilder(),context)

        then:
        def returnedJson = new JsonSlurper().parseText(apiResponse.response)
        returnedJson[0].clientId == "894184d6-0930-11e5-a6c0-1697f925ec7b"
        returnedJson[0].first_name == "William"
        returnedJson[0].last_name == "Jobs"
        returnedJson[0].city == "Grenoble"
        returnedJson[0].country == "France"
    }

    def "rest api call without query id should return error"() {
        httpRequest.parameterNames >> new StringTokenizer("wrongParameter")

        when:
        def apiResponse = index.doHandle(httpRequest, new RestApiResponseBuilder(), context)

        then:
        apiResponse.httpStatus == 400
        apiResponse.response == ["error": "the parameter queryId is missing"]
    }

    def "rest api call wrong queryId should return error"() {
        httpRequest.getParameter("queryId") >> "wrongQuery"

        when:
        def apiResponse = index.doHandle(httpRequest,new RestApiResponseBuilder(),context)

        then:
        apiResponse.httpStatus == 400
        apiResponse.response == ["error": "the queryId does not refer to an existing query"]
    }

    def "when error occurs bad request status should be returned"() {
        when:
        def apiResponse = index.buildErrorResponse(new RestApiResponseBuilder(), "ugly exception")

        then:
        apiResponse.response == ["error": "ugly exception"]
        apiResponse.httpStatus == 400
    }


    def "response should contain result"() {
        when:
        def apiResponse = index.buildResponse(new RestApiResponseBuilder(), "{result:value}")

        then:
        apiResponse.response == "{result:value}"
        apiResponse.httpStatus == 200
    }

    def "sql parameters should remove queryId"() {
        httpRequest.parameterNames >> new StringTokenizer("queryId,country",",")
        httpRequest.getParameter("country") >> "FR"

        when:
        def map = index.getSqlParameters(httpRequest)

        then:
        map == ["country": "FR"]
    }

    def "should get query from queryId parameter"() {
        when:
        def clientQueryByCountry = index.getQuery("getClientsByCountry", resourceProvider)

        then:
        clientQueryByCountry == "SELECT first_name, last_name, city, country FROM client WHERE country =:country"
    }
}
