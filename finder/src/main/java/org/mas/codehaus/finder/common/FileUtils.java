package org.mas.codehaus.finder.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {
	public static List<String> toLinesCompact(BufferedReader bufferedReader) throws IOException {
		List<String> result = new LinkedList<String>();
		String line = null;
		String trimedLine = null;
		while (null != (line = bufferedReader.readLine())) {
			trimedLine = line.trim();
			if ("".equals(trimedLine) || trimedLine.startsWith("#")) {
				continue;
			}
			result.add(trimedLine);
		}
		return result;
	}
	
	public static List<String> toLinesCompact(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			return toLinesCompact(bufferedReader);
		} finally {
			if (null != bufferedReader)
				bufferedReader.close();
		}
	}
}
