package com.boco.irms.app.dm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.irms.app.utils.ActionContextUtil;
import com.boco.transnms.common.dto.base.BoActionContext;

public class UserInfoFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		ServiceActionContext ac = new ServiceActionContext((HttpServletRequest)request);
		BoActionContext actionContext = new BoActionContext();
		actionContext.setUserId(ac.getUserId());
		actionContext.setUserName(ac.getUserId());
		ThreadLocal<BoActionContext> userinfo = new ThreadLocal<BoActionContext>();
		userinfo.set(actionContext);
		ActionContextUtil.setContextLocal(userinfo);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
