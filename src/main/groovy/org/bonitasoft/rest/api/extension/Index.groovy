package org.bonitasoft.rest.api.extension

import groovy.json.JsonBuilder
import groovy.sql.Sql

import javax.naming.Context
import javax.naming.InitialContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource

import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestAPIContext
import org.bonitasoft.web.extension.rest.RestApiController
import org.bonitasoft.web.extension.rest.RestApiResponse
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Index implements RestApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Index.class)

    @Override
    RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {
        String queryId = request.getParameter "queryId"
        if (queryId == null) {
            return buildErrorResponse(responseBuilder, "the parameter queryId is missing")
        }
        String query = getQuery queryId, context.resourceProvider
        if (query == null) {
            return buildErrorResponse(responseBuilder, "the queryId does not refer to an existing query")
        }
        Map<String, String> params = getSqlParameters request
        Sql sql = buildSql context.resourceProvider
        try {
            def rows = params.isEmpty() ? sql.rows(query) : sql.rows(query, params)
            JsonBuilder builder = new JsonBuilder(rows)
            String table = builder.toPrettyString()
            return buildResponse(responseBuilder, table)
        } finally {
            sql.close()
        }
    }


    protected RestApiResponse buildErrorResponse(RestApiResponseBuilder apiResponseBuilder, String message) {
        LOGGER.error message

        Map<String, String> result = [:]
        result.put "error", message
        apiResponseBuilder.withResponseStatus(HttpServletResponse.SC_BAD_REQUEST)
        buildResponse apiResponseBuilder, result
    }

    protected RestApiResponse buildResponse(RestApiResponseBuilder apiResponseBuilder, Serializable result) {
        apiResponseBuilder.with {
            withResponse(result)
            build()
        }
    }

    protected Map<String, String> getSqlParameters(HttpServletRequest request) {
        Map<String, String> params = [:]
        for (String parameterName : request.getParameterNames()) {
            params.put(parameterName, request.getParameter(parameterName))
        }
        params.remove("queryId")
        params
    }

    protected Sql buildSql(ResourceProvider pageResourceProvider) {
        Properties props = loadProperties "datasource.properties", pageResourceProvider
        Context ctx = new InitialContext(props)
        DataSource dataSource = (DataSource) ctx.lookup(props["datasource.name"])
        new Sql(dataSource)
    }

    protected String getQuery(String queryId, ResourceProvider resourceProvider) {
        Properties props = loadProperties "queries.properties", resourceProvider
        props[queryId]
    }

    protected Properties loadProperties(String fileName, ResourceProvider resourceProvider) {
        Properties props = new Properties()
        resourceProvider.getResourceAsStream(fileName).withStream { InputStream s ->
            props.load s
        }
        props
    }
}

