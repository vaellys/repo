package qileke.jer.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.HashSessionIdManager;

public class JServer {
	private Server server = new Server();
	private int port = 8080;
	public Handler handler;
	public int maxIdleTime = 30000;
	
	public int queueSize=20; 

	private void init() {
		server.setSessionIdManager(new HashSessionIdManager());
		server.setConnectors(getConnects());
		server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 5000000);
		server.setHandler(handler);
	}
	

	public JServer(int port, org.eclipse.jetty.server.Handler handler) {
		super();
		this.port = port;
		this.handler = handler;
	}
	public JServer(int port) {
		super();
		this.port = port;
	}

	public JServer() {
		super();
	}

	public void start(boolean isBlocking) throws Exception {
		init();
		server.start();
		if (isBlocking) {
			server.join();
		}
	}

	private Connector[] getConnects() {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setAcceptQueueSize(queueSize);
		connector.setMaxIdleTime(maxIdleTime);

		return new Connector[] { connector };
	}


	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		JServer server = new JServer();
		server.start(true);

	}

}
