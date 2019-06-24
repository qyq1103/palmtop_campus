package net.JL.admin.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

public class AdminInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		Record user = controller.getSessionAttr("user");
		if (user != null) {
			inv.invoke();
		} else {
			String header = controller.getRequest().getHeader("X-Requested-With");
			boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(header);
			if (isAjax) {
				HashMap<String, Object> res = new HashMap<String, Object>();
				res.put("status", "over");
				res.put("msg", "/admin/over");
				res.put("code", 0);
				controller.renderJson(res);
				return;
			}
			controller.renderNull();
			try {
				PrintWriter out = controller.getResponse().getWriter();
				out.print("<script type='text/javascript'>top.location = '/admin/over'</script>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
