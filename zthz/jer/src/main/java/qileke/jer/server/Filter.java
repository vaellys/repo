package qileke.jer.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Filter {
	void filter(HttpServletRequest request , HttpServletResponse response) throws Throwable;
}
