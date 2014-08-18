package cn.zthz.actor.rest;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.JsonUtils;

public class IORest extends FunctionalRest {



	protected List<String> gets(String name, HttpServletRequest request) {
		return (List<String>)request.getAttribute(name);
	}

	public void u(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = upload(request, response);
		System.out.println(JsonUtils.toJsonString(request.getParameterMap()));
		putJson(request, response, params);

	}

	protected Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		for(Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()){
			for (String value : entry.getValue()) {
				putValues(params, entry.getKey(), value);
			}
		}
		if (!ServletFileUpload.isMultipartContent(request)) {
			return params;
		}
		if (!UPLOAD_FILE_TEMP_DIR.exists()) {
			UPLOAD_FILE_TEMP_DIR.mkdirs();
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(UPLOAD_MEMORY_SIZE);
		factory.setRepository(UPLOAD_FILE_TEMP_DIR);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(UPLOAD_MAX_FILE_SIZE);
		List<FileItem> items = upload.parseRequest(request);
		for (FileItem item : items) {
			if (!item.isFormField()) {
				processUploadedFile(params, item);
			} else {
				processFormField(params, item);

			}
		}
		return params;
	}

	private void processFormField(Map<String, Object> params, FileItem item) {
		String name = item.getFieldName();
		String value = item.getString();

		putValues(params, name, value);

	}

	private void putValues(Map<String, Object> params, String name, String value) {
		Object preValue = (Object) params.get(name);
		if (null == preValue) {
			params.put(name, value);
		} else {
			if (preValue instanceof List) {
				((List<String>) preValue).add(value);
			}
			if (preValue instanceof String) {
				List<String> values = new LinkedList<>();
				values.add((String) preValue);
				values.add(value);
				params.put(name, values);
			}
		}
	}

	private void processUploadedFile(Map<String, Object> params, FileItem item) throws Exception {
		long sizeInBytes = item.getSize();
		if (0 >= sizeInBytes) {
			return;
		}
		String fieldName = item.getFieldName();
		String fileName = item.getName();
		String contentType = item.getContentType();
//		boolean isInMemory = item.isInMemory();
		File file = new File(UPLOAD_FILE_TEMP_DIR, Long.toHexString(System.currentTimeMillis()));
		item.write(file);
		// Map<String, String[]> map = request.getParameterMap();;
		params.put(fieldName, fileName);
		params.put(fieldName + "_content_type", contentType);
		params.put(fieldName + "_file", file.getAbsolutePath());
		params.put(fieldName + "_size", file.getAbsolutePath());

	}

}
