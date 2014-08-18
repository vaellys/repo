package cn.zthz.tool.common;

import java.io.IOException;
import java.io.InputStream;

public interface WithInputStream {
	void with(InputStream inputStream) throws IOException;
}
