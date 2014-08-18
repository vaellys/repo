package qileke.jer.utils;

import java.awt.Event;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MruCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "hh:mm:ss";
	public static final String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
	public static final String DEFAULT_ENCODING = "utf8";
	
	private static Configuration configuration;
	
	static {
		
		configuration = new Configuration();
		TemplateLoader tplLoader = new ClassTemplateLoader(FreeMarkerUtils.class , "/");
		configuration.setTemplateLoader(tplLoader);
		configuration.setDefaultEncoding(DEFAULT_ENCODING);
		configuration.setTimeFormat(TIME_FORMAT);
		configuration.setDateFormat(DATE_FORMAT);
		configuration.setDateTimeFormat(DATETIME_FORMAT);
		configuration.setCacheStorage(new MruCacheStorage(20, 250));
	}

	/**
	 * 
	 * @param tplName base pass is the classpath's root;
	 * @param data
	 * @param writer
	 * @throws TemplateException
	 * @throws IOException
	 */
	public static void writeContent(String tplName, Object data, Writer writer) throws TemplateException, IOException {
		Template tpl = configuration.getTemplate(tplName);
		Environment environment = tpl.createProcessingEnvironment(data, writer);
//		tpl.process(data, writer);
		environment.process();
	}
	
	public static void main(String[] args) throws TemplateException, IOException {
		Map<String, String> data = new HashMap<String, String>();
		data.put("name", "jim");
		for(int i = 0 ; i < 10 ; i++){
			
		writeContent("hello.tpl", data, new PrintWriter(System.out));
		}
		
	}

}
