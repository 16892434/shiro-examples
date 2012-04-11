package org.zy.examples.shiro.test;

import static junit.framework.Assert.assertTrue;

import java.net.BindException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.gargoylesoftware.htmlunit.WebClient;

public abstract class AbstractContainerTest {

	public static final int MAX_PORT = 9200;
	
	protected static PauseableServer server;
	
	protected static int port = 9180;
	
	protected final WebClient webClient = new WebClient();
	
	@BeforeClass
	public static void startContainer() throws Exception {
		while(server == null && port < MAX_PORT) {
			try {
				server = createAndStartServer(port);
			}catch(BindException e) {
				System.err.printf("Unable to listen on port %d. Trying next port.", port);
				port++;
			}
		}
		assertTrue(server.isStarted());
	}
	
	private static PauseableServer createAndStartServer(final int port) throws Exception {
		PauseableServer server = new PauseableServer();
		Connector connector = new SelectChannelConnector();
		connector.setPort(port);
		server.setConnectors(new Connector[]{connector});
		server.setHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();
		return server;
	}
	
	protected static String getBaseUri() {
		return "http://localhost:" + port + "/";
	}
	
	@Before
	public void beforeTest() {
		webClient.setThrowExceptionOnFailingStatusCode(true);
	}
	
	public static class PauseableServer extends Server {
		public synchronized void pause(boolean paused) {
			try {
				if(paused) {
					for(Connector connector : getConnectors()) {
						connector.stop();
					}
				} else {
					for(Connector connector : getConnectors()) {
						connector.start();
					}
				}
			}catch(Exception e) {}
		}
	}
}
