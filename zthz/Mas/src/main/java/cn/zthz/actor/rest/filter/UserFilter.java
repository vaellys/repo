package cn.zthz.actor.rest.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.rest.FunctionalRest;
import cn.zthz.tool.common.ConfigUtils;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.proxy.UserProxy;

import qileke.jer.server.Filter;

public class UserFilter extends FunctionalRest implements Filter {
	private static final Log log = LogFactory.getLog(UserFilter.class);
//	protected final Set<String> freeCheckList = new HashSet<String>();
	protected final List<Pattern> freeCheckPatterns = new LinkedList<>();
	
	public UserFilter() {
		loadFreeCheckList();
	}
	
	protected void loadFreeCheckList() {
		InputStream inputStream = ConfigUtils.getInputStreamFromClasspath("mas-free-check.item");
		try {
			for(String item : cn.zthz.tool.common.FileUtils.toLinesCompact(inputStream)){
				freeCheckPatterns.add(Pattern.compile(item));
			}
//			freeCheckList.addAll();
		} catch (IOException e) {
			log.error("load free check list" , e);
		} finally {
			if(null!=inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("load free check list" , e);
				}
				
			}
			
		}
	}
	private Map<String, Object> disposeLoginParams(Map<String, String[]> params){
		Map<String, Object> result = new HashMap<>(params.size());
		String[] value =null; 
		String key=null; 
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			key = entry.getKey();
			value=entry.getValue();
			if(null == value|| value.length == 0){
				result.put(key, null);
			}else if(1 == value.length){
				result.put(key, StringUtils.omitString(value[0], 100));
			}else{
				result.put(key, value);
			}
		}
		return result;
	}
	
	@Override
	public void filter(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String uri = request.getRequestURI();
		log.info(getRemoteRealIp(request)+" "+request.getMethod()+" "+request.getRequestURI()+" "+JsonUtils.toJsonString(disposeLoginParams(request.getParameterMap()))  );
//		if(freeCheckList.contains(uri)){
//			return;
//		}
		for (Pattern item : freeCheckPatterns) {
			if(item.matcher(uri).matches()){
				return;
			}
		}
		checkUserToken(request, response);
		if(isMultipart(request)){
			log.info("upload params:"+JsonUtils.toJsonString(uploadFields(request, response)));
		}
		UserProxy.resetUserStatus(request.getParameter(USER_ID));
	}

}
