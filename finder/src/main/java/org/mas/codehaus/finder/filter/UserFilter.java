package org.mas.codehaus.finder.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mas.codehaus.finder.common.ConfigUtils;
import org.mas.codehaus.finder.common.Constants;
import org.mas.codehaus.finder.common.ErrorCodes;
import org.mas.codehaus.finder.common.ErrorMessage;
import org.mas.codehaus.finder.common.FileUtils;
import org.mas.codehaus.finder.common.JsonUtils;
import org.mas.codehaus.finder.common.LogUtils;
import org.mas.codehaus.finder.common.web.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class UserFilter extends OncePerRequestFilter {

	private static Logger log = LoggerFactory.getLogger(UserFilter.class);
	protected final List<Pattern> freeCheckPatterns = new LinkedList<Pattern>();

	public UserFilter() {
		loadFreeCheckList();
	}

	protected void loadFreeCheckList() {
		InputStream inputStream = ConfigUtils
				.getInputStreamFromClasspath("finder-free-check.item");
		try {
			for (String item : FileUtils.toLinesCompact(inputStream)) {
				freeCheckPatterns.add(Pattern.compile(item));
			}
			log.debug(LogUtils.format("R", freeCheckPatterns));
		} catch (IOException e) {
			log.error("load free check list", e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("load free check list", e);
				}

			}

		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String newUri = uri.replaceFirst(contextPath, "");
		for (Pattern item : freeCheckPatterns) {
			if (item.matcher(newUri).matches()) {
				filterChain.doFilter(request, response);
				return;
			}
		}
		Integer userId = (Integer) request.getSession().getAttribute(
				Constants.USER_ID);
		if (null == userId) {
			try {
				putError(request, response, "user is not login!",
						ErrorCodes.USER_UNLOGIN);
			} catch (Exception e) {
				log.error("", e);
				e.printStackTrace();
			}
		} else {
			filterChain.doFilter(request, response);
		}

	}

	protected void putError(HttpServletRequest request,
			HttpServletResponse response, String error, int errorCode) {
		ErrorMessage errorMessage = new ErrorMessage(error, errorCode,
				request.getRequestURI());
		ResponseUtils
				.renderJson(response, JsonUtils.toJsonString(errorMessage));
	}
}
