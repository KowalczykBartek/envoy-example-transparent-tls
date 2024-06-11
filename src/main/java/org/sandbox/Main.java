package org.sandbox;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

public class Main
{
	protected static final Logger LOG = LogManager.getLogger();

	public static void main(final String[] args)
	{
		var remoteHostName = System.getenv().get("REMOTE_HOST_NAME");
		var thisHostName = System.getenv().get("THIS_SERVER_HOSTNAME");

		if (StringUtils.isEmpty(remoteHostName))
		{
			LOG.warn("REMOTE_HOST_NAME env, cannot be empty");
			return;
		}

		if (StringUtils.isEmpty(thisHostName))
		{
			LOG.warn("THIS_SERVER_HOSTNAME env, cannot be empty");
			return;
		}

		var vertx = Vertx.vertx();
		var client = WebClient.create(vertx);
		var mapper = new ObjectMapper();

		new PingPongServer(vertx, 9099, mapper, remoteHostName, thisHostName, client, "WITH_ENVOY").start();
		new PingPongServer(vertx, 8099, mapper, remoteHostName, thisHostName, client, "NO_ENVOY").start();
	}
}