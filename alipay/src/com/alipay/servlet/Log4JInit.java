package com.alipay.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

/**
 * Servlet implementation class Log4JInit
 */
public class Log4JInit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Log4JInit() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException{
        String prefix = getServletContext().getRealPath("/"); 
        String test = getServletContext().getRealPath(""); 
        System.out.println(prefix); 
        System.out.println(test); 
       
        System.setProperty("webappHome", test);
        String file = getServletConfig().getInitParameter("log4j-config-file"); 
        System.out.println(prefix+file); 
        // 从Servlet参数读取log4j的配置文件 
         if (file != null) { 
          PropertyConfigurator.configure(prefix + file); 
        
        }
    }

}
