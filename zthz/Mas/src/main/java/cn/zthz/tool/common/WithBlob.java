package cn.zthz.tool.common;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

public interface WithBlob {
	void with(Blob blob) throws IOException , SQLException;
}
