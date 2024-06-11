package org.sandbox;

import static io.vertx.core.http.HttpMethod.POST;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;

public class PingPongServer
{
	protected static final Logger LOGGER = LogManager.getLogger();

	private final Vertx vertx;
	private final int port;
	private final ObjectMapper objectMapper;
	private final String remoteHostName;
	private final String thisHostName;
	private final WebClient client;
	private final String prefix;

	public PingPongServer(final Vertx vertx, final int port, final ObjectMapper objectMapper, final String remoteHostName,
			final String thisHostName, final WebClient client, final String loggingPrefix)
	{
		this.vertx = vertx;
		this.port = port;
		this.objectMapper = objectMapper;
		this.remoteHostName = remoteHostName;
		this.thisHostName = thisHostName;
		this.client = client;
		this.prefix = loggingPrefix;
	}

	public void start()
	{
		var router = Router.router(vertx);
		router.route(POST, "/ping/:from_host")//
				.handler(ctx -> {
					//from host - we will pong it back using host from path-param
					var pathParam = ctx.pathParam("from_host");

					LOGGER.info("[{}] Received PING from {}", prefix, pathParam);

					var response = ctx.response();
					var responseBody = Map.of("from-host", pathParam, "server-host", remoteHostName);

					try
					{
						response.setStatusCode(200)//
								.putHeader("content-type", "application/json; charset=UTF-8")//
								.end(objectMapper.writeValueAsString(responseBody));
					}
					catch (final JsonProcessingException e)
					{
						LOGGER.error("[{}] Failed to construct response.", prefix, e);
						//don't be sad, it's not a prod app ;)
						response.setStatusCode(500).end(e.getMessage());
					}
				});

		var server = vertx.createHttpServer();
		server.requestHandler(router).listen(port);

		//every 5seconds send request to second server.
		vertx.setPeriodic(5_000, t -> {
			LOGGER.info("[{}-{}] Sending PING to {}", prefix, thisHostName, remoteHostName);
			var path = "/ping/%s".formatted(thisHostName);
			client.post(port, remoteHostName, path)//
					.send()//
					.onSuccess(resp -> LOGGER.info("[{}] response body from pinged server {}", prefix, resp.bodyAsString()))//
					.onFailure(ex -> LOGGER.error("[{}] error from pinged server {}", prefix, ex.getMessage()));
		});

		LOGGER.info("[{}] Started !", prefix);
	}
}
