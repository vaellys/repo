package qileke.jer.server.session;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.server.session.AbstractSessionManager;

public class MemcachedSessionManager extends AbstractSessionManager {
	private static final Log log = LogFactory.getLog(MemcachedSessionManager.class);
	
	private String memcachedAddress = "localhost:11211";
	
	private MemcachedClientBuilder memcachedClientBuilder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedAddress));
	private MemcachedClient client ;
	public MemcachedSessionManager() {
		try {
			client = memcachedClientBuilder.build();
		} catch (IOException e) {
			log.error("create memcached client failed! memcached address:"+memcachedAddress ,e);
			throw new RuntimeException(e);
		}
	}
	

	@Override
	protected void addSession(AbstractSession session) {
		
	}

	@Override
	public AbstractSession getSession(String idInCluster) {
		return null;
	}

	@Override
	protected void invalidateSessions() throws Exception {
		
	}

	@Override
	protected AbstractSession newSession(HttpServletRequest request) {
		return null;
	}

	@Override
	protected boolean removeSession(String idInCluster) {
		return false;
	}

}
