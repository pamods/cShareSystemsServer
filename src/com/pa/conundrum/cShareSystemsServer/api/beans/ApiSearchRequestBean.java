package com.pa.conundrum.cShareSystemsServer.api.beans;

import javax.servlet.http.HttpServletRequest;

public class ApiSearchRequestBean extends ApiRequestBean {

	String name, creator, sort_field, sort_direction, request_time;
	int minPlanets, maxPlanets, start, limit;
	
	public ApiSearchRequestBean(HttpServletRequest request) {
		super(request);
	}
	
	public String getName() {
		return name;
	}

	public String getCreator() {
		return creator;
	}

	public String getSort_field() {
		return sort_field;
	}

	public String getSort_direction() {
		return sort_direction;
	}

	public String getRequest_time() {
		return request_time;
	}

	public int getMinPlanets() {
		return minPlanets;
	}

	public int getMaxPlanets() {
		return maxPlanets;
	}

	public int getStart() {
		return start;
	}

	public int getLimit() {
		return limit;
	}

	public void setName(String name) {
		name = (name == null || name.equals("")) ? "" : name;
		this.name = name;
	}

	public void setCreator(String creator) {
		creator = (creator == null || creator.equals("")) ? "" : creator;
		this.creator = creator;
	}

	public void setSort_field(String sort_field) {
		if(sort_field == null || !(sort_field.equals("system_id") || sort_field.equals("num_planets"))) {
			sort_field = "system_id"; // Default
		}
		
		this.sort_field = sort_field;
	}

	public void setSort_direction(String sort_direction) {
		if(sort_direction == null || !(sort_direction.equals("DESC") || sort_direction.equals("ASC"))) {
			sort_direction = "DESC"; // Default
		}
		
		this.sort_direction = sort_direction;
	}

	public void setRequest_time(String request_time) {
		request_time = (request_time == null || request_time.equals("")) ? "" : request_time;
		this.request_time = request_time;
	}

	public void setMinPlanets(int minPlanets) {
		if(maxPlanets > 0) {
			if(minPlanets > maxPlanets)
				minPlanets = maxPlanets;
			if(maxPlanets < minPlanets)
				maxPlanets = minPlanets;
		}
		if(minPlanets < 1)
			minPlanets = 1; // Default
		this.minPlanets = minPlanets;
	}

	public void setMaxPlanets(int maxPlanets) {
		if(maxPlanets < 1)
			maxPlanets = 16; // Default
		if(minPlanets > 0) {
			if(minPlanets > maxPlanets)
				minPlanets = maxPlanets;
			if(maxPlanets < minPlanets)
				maxPlanets = minPlanets;
		}
		this.maxPlanets = maxPlanets;
	}

	public void setStart(int start) {
		if(start < 0)
			start = 0;
		this.start = start;
	}

	public void setLimit(int limit) {
		if(limit < 1)
			limit = 10; // Default
		this.limit = limit;
	}
}
