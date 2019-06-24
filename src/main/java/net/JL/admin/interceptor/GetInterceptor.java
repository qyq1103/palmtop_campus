package net.JL.admin.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class GetInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub
		Controller controller = inv.getController();
		if ("GET".equals(controller.getRequest().getMethod())) {
			inv.invoke();
		} else {
			controller.renderError(404);
		}
	}

}
