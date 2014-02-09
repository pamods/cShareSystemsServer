package com.pa.conundrum.cShareSystemsServer.api;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.pa.conundrum.cShareSystemsServer.datastore.DatastoreModel;
import com.pa.conundrum.cShareSystemsServer.datastore.models.Planets;
import com.pa.conundrum.cShareSystemsServer.datastore.models.SharedSystem;

public class SaveServlet extends HttpServlet {

	private static final long serialVersionUID = -1457384842460537269L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String system = request.getParameter("system");

		SharedSystem sharedSystem = new SharedSystem();
		Planets planets = new Planets();
		
		Gson gson = new Gson();
		sharedSystem = gson.fromJson(system, SharedSystem.class);
		planets = gson.fromJson(system, Planets.class);

		sharedSystem.setSystem(system);
		sharedSystem.setNum_planets(planets.getNum_planets());
		sharedSystem.setIp(getClientIpAddr(request));
		
		boolean success = DatastoreModel.create(sharedSystem);

		response.getWriter().print(success);
	}

	public static String getClientIpAddr(HttpServletRequest request) {  
		String ip = request.getHeader("X-Forwarded-For");  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("Proxy-Client-IP");  
		}  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("WL-Proxy-Client-IP");  
		}  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("HTTP_CLIENT_IP");  
		}  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
		}  
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			ip = request.getRemoteAddr();  
		}  
		return ip;  
	}  
}
