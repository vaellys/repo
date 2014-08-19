package org.mas.codehaus.finder.common;

import java.io.InputStream;

public class ConfigUtils {
	public static InputStream getInputStreamFromClasspath(String name) {
		return ConfigUtils.class.getResourceAsStream(StringUtils.ensureStartWith(name, "/"));
	}
}
