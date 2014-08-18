package qileke.jer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;

import qileke.jer.utils.StringUtils;

public class SyncHandler extends SessionHandler {
	private static final Log log = LogFactory.getLog(SyncHandler.class);
	// public String defaultContentType = "text/html;charset=utf-8";
	public String defaultContentType = "application/json";
	private String suffix = ".json";
	public final LinkedList<Filter> filters = new LinkedList<>();
	public Invoke invoke = new Invoke();
	public ExceptionHandler exceptionHandler;

	public static final String CONTINUATION = "continuation";

	public SyncHandler() {
		super();
	}

	public SyncHandler(String defaultContentType, String suffix, String... files) {
		super();
		this.defaultContentType = defaultContentType;
		this.suffix = suffix;
		load(files);
	}

	public SyncHandler(String defaultContentType, String suffix, List<String> files) {
		super();
		this.defaultContentType = defaultContentType;
		this.suffix = suffix;
		load(files);
	}
	public SyncHandler(String defaultContentType, String suffix, List<String> handlerFiles , List<String> filterFiles) {
		super();
		this.defaultContentType = defaultContentType;
		this.suffix = suffix;
		load(handlerFiles);
		loadFilters(filterFiles);
	}

	public void load(String... files) {
		invoke.load(files);
	}

	public void load(List<String> files) {
		invoke.load(files);
	}

	public void loadFilters(String... files) {
		List<String> fileList = new ArrayList<>(files.length);
		for (String string : files) {
			fileList.add(string);
		}
		loadFilters(fileList);
	}
	public void loadFilters(List<String> files) {
		List<String> filterNames = new LinkedList<>();
		for (String name : files) {
			BufferedReader reader= new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(StringUtils.ensureStartWith(name, "/"))));
			try {
				String line = null;
				String trimedLine = null;
				while(null!=(line = reader.readLine())){
					trimedLine = line.trim();
					if("".equals(trimedLine) || trimedLine.startsWith("#")){
						continue;
					}
					filterNames.add(trimedLine);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (null != reader)
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
			}
		}
		try {
			for (String filterName : filterNames) {
				filters.add((Filter) Class.forName(filterName).newInstance());
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void doHandle(final String target, Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, ServletException {

		if (!target.endsWith(suffix)) {
			return;
		}
		String path = target.substring(0, target.lastIndexOf(suffix));

		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		response.setHeader("Server", "SuperServer");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setBufferSize(8 * 1024);
		response.setContentType(defaultContentType);
		try {
			for (Filter filter : filters) {
				filter.filter(request, response);
			}
		} catch (Throwable e) {
			if (null != exceptionHandler) {
				exceptionHandler.handle(e, request, response);
			} else {
				log.error("jer invoke error!", e);
				ActionHelper.writeDirectly(request, response, "service error for this url:" + request.getRequestURI());
			}
			return;
		}
		try {
			invoke.invoke(path, request, response);
		} catch (PassException e) {
			log.warn("pass this error," + e.getMessage());
		} catch (ActionException e) {
			if (null != exceptionHandler) {
				exceptionHandler.handle(e, request, response);
			} else {
				log.error("bad request url:" + request.getRequestURI(), e);
				ActionHelper.writeDirectly(request, response, e.getMessage());
			}
		} catch (Exception e) {
			if (null != exceptionHandler) {
				exceptionHandler.handle(e, request, response);
			} else {
				log.error("jer invoke error!", e);
				ActionHelper.writeDirectly(request, response, "service error for this url:" + request.getRequestURI());
			}
		}

	}

}
