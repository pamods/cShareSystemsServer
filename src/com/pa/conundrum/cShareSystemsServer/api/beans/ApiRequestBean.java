package com.pa.conundrum.cShareSystemsServer.api.beans;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

public class ApiRequestBean {
	
	public ApiRequestBean(HttpServletRequest request) {
		populateWithBeanUtils(request);
	}

	/**
	 * Use Apache Commons BeanUtils to read the GET parameters into a Java object.
	 * @param request
	 */
	void populateWithBeanUtils(HttpServletRequest request) {
		// http://commons.apache.org/proper/commons-beanutils/apidocs/org/apache/commons/beanutils/package-summary.html#conversion.beanutils
		HashMap<String, Object> map = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Enumeration<Object> names = request.getParameterNames();
		while (names.hasMoreElements()) {
		  String name = (String) names.nextElement();
		  map.put(name, request.getParameterValues(name));
		}
		try {
			BeanUtils.populate(this, map);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// Looks like someone tried to send too many parameters.
			throw new InvalidParameterException("Invalid request parameters.");
		}
	}
}
