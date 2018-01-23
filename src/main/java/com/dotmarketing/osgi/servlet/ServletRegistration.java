package com.dotmarketing.osgi.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotcms.repackage.org.apache.commons.collections.iterators.IteratorEnumeration;
import com.dotmarketing.util.Config;

public class ServletRegistration implements WebInterceptor {


	private static final long serialVersionUID = 1L;
	final HttpServlet servlet;
	final String[] filters;
	final ServletConfig config;
	final Map<String, String> params;
	
	
	public ServletRegistration(HttpServlet servlet, String... filters) {
		this(servlet, new HashMap<>(), filters);
	}
	
	public ServletRegistration(HttpServlet servlet, Map<String, String> params, String... filters) {
		this.servlet = servlet;
		this.filters = filters;
		this.params = params;
		this.config=new ServletConfig() {
			
			@Override
			public String getServletName() {
				return servlet.getServletName();
			}
			
			@Override
			public ServletContext getServletContext() {
				return Config.CONTEXT;
			}
			
			@Override
			public Enumeration<String> getInitParameterNames() {
				return new IteratorEnumeration(params.keySet().iterator());
			}
			
			@Override
			public String getInitParameter(String arg0) {
				return params.get(arg0);
			}
		};
	}

	final @Override public void init() {

		try {
			servlet.init(this.config);
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Result intercept(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			servlet.service(req, res);
			return Result.SKIP_NO_CHAIN;
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public String[] getFilters() {
		return filters;
	}

	@Override
	public String getName() {
		return servlet.getClass().getName();
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void destroy() {
		servlet.destroy();
	}

}