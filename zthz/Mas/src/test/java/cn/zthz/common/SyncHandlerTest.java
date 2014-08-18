package cn.zthz.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;

public class SyncHandlerTest extends SessionHandler{
	private String suffix = ".json";
	private Invoke invoke = new Invoke();
	private static  String defaultContentType = "application/json";
	
	public SyncHandlerTest(String contentType, String suffix, List<String> handlerFiles){
		this.defaultContentType = contentType;
		this.suffix = suffix;
		invoke.load(handlerFiles);
		
	}
	
	
	public void load(List<String> files){
		for(String file : files){
			InputStream is = this.getClass().getResourceAsStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			String trimedLine = null;
			try {
				while(null != (line = br.readLine())){
					trimedLine = line.trim();
					if("".equals(trimedLine) || trimedLine.equals("#")){
						continue;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		if(!target.endsWith(suffix)){
			return;
		}
		
		String path = target.substring(0, target.lastIndexOf(suffix));
		
		try {
			invoke.invoke(path, request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
