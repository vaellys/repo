package qileke.jer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;

import qileke.jer.server.ActionHelper;
import qileke.jer.server.AsyncHandler;
import qileke.jer.server.JServer;

public class Hello {
	ExecutorService executorService = Executors.newCachedThreadPool();
	public void greete(final HttpServletRequest request , final HttpServletResponse response) throws IOException{
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					System.out.println("hello");
//					response.getWriter().write("hello");
					ActionHelper.write(request, response, "[1,2]");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void hello( HttpServletRequest request , HttpServletResponse response){
//		System.out.println("target :"+ target);
		System.out.println(request.getParameter("name"));
		System.out.println(request.getSession().getId());
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.append(request.getSession().getId()+"</br>");
			printWriter.write("hello");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void startCollectRestServer(boolean isBlock) throws Exception {
		AsyncHandler asyncHandler = new AsyncHandler();
		asyncHandler.load("map.properties");
		JServer server = new JServer(2323 , asyncHandler);
		System.out.println("start jer rest at :"+server.getPort());
		server.start(isBlock);
	}
	public static void main(String[] args) throws Exception {
		startCollectRestServer(true);
	}
}
