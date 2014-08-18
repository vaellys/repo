package qileke.jer.utils;

import java.io.File;

public class FileUtils {
	
	
	/**
	 * 
	 * @param fileName 以classpath为根路径，fileName前不能有“/”
	 * @return
	 */
	public static File getClassPathFile(String fileName){
		return new File(Thread.currentThread().getContextClassLoader().getResource(fileName).getFile());
	}
	
}
