package qileke.jer.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;

public class AsyncHandler extends SessionHandler {
	private static final Log log = LogFactory.getLog(AsyncHandler.class);
	// public String defaultContentType = "text/html;charset=utf-8";
	public String defaultContentType = "application/json";
	private String suffix = ".json";
	public final LinkedList<Filter> filters = new LinkedList<>();
	public Invoke invoke = new Invoke();

	public static final String CONTINUATION = "continuation";

	public AsyncHandler() {
		super();
	}

	public AsyncHandler(String defaultContentType, String suffix, String... files) {
		super();
		this.defaultContentType = defaultContentType;
		this.suffix = suffix;
		load(files);
	}

	public AsyncHandler(String defaultContentType, String suffix, List<String> files) {
		super();
		this.defaultContentType = defaultContentType;
		this.suffix = suffix;
		load(files);
	}

	public void load(String... files) {
		invoke.load(files);
	}

	public void load(List<String> files) {
		invoke.load(files);
	}

	@Override
	public void doHandle(final String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, ServletException {

		if (!target.endsWith(suffix)) {
			return;
		}
		try {
			for (Filter filter : filters) {
				filter.filter(request, response);
			}
		} catch (Throwable e) {
			// response.getWriter().write(e.getMessage());
			return;
		}
		String path = target.substring(0, target.lastIndexOf(suffix));
		final Continuation continuation = ContinuationSupport.getContinuation(request);

		if (continuation.isInitial()) {
			response.setCharacterEncoding("utf-8");
			request.setCharacterEncoding("utf-8");
			response.setHeader("Server", "SuperServer");
			response.setBufferSize(8 * 1024);
			response.setContentType(defaultContentType);
			continuation.suspend();
			request.setAttribute(AsyncHandler.CONTINUATION, continuation);
			try {
				invoke.invoke(path, request, response);
			} catch (PassException e) {
				log.warn("pass this error", e);
			} catch (ActionException e) {
				log.error("bad request url:" + request.getRequestURI(), e);
				ActionHelper.write(request, response, e.getMessage());
			} catch (Exception e) {
				log.error("jer invoke error!", e);
				ActionHelper.write(request, response, "service error for this url:" + request.getRequestURI());
			}

		} else {
			baseRequest.setHandled(true);
		}

	}

}
