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

		// Get query id from the REST request. See queries.properties for declared queries ids.
		String queryId = request.getParameter "queryId"
		if (queryId == null) {
			return buildErrorResponse(responseBuilder, "The parameter queryId is missing.")
		}

		// Get the query SQL definition from the queries.properties file using query id.
		String query = getQuery queryId, context.resourceProvider
		if (query == null) {
			return buildErrorResponse(responseBuilder, "The queryId does not refer to an existing query. Check your query id and queries.properties file content.")
		}

		// Build a map will all SQL queries parameters (all REST call parameters expect "queryId").
		Map<String, String> params = getSqlParameters request

		// Get the database connection using the data source declared in datasource.properties
		Sql sql = buildSql context.resourceProvider

		try {
			// Run the query with or without parameters.
			def rows = params.isEmpty() ? sql.rows(query) : sql.rows(query, params)

			// Build the JSON answer with the query result
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

