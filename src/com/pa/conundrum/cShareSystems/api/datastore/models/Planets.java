package com.pa.conundrum.cShareSystems.api.datastore.models;

import java.util.ArrayList;

public class Planets {
	
	ArrayList<Object> planets;
	
	public Planets() { }
	
	public int getNum_planets() {
		return planets.size();
	}
}
