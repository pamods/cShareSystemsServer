package com.pa.conundrum.cShareSystemsServer.datastore;

import java.util.Comparator;

import com.pa.conundrum.cShareSystemsServer.datastore.models.SharedSystem;

public class SharedSystemComparator implements Comparator<SharedSystem> {
	
	static final String DEFAULT_SORT_FIELD = "system_id";
	static final String DEFAULT_SORT_DIRECTION = "desc";
	
	String sort_field = DEFAULT_SORT_FIELD;
	String sort_direction = DEFAULT_SORT_DIRECTION;
	
	public SharedSystemComparator() { }
	
	public SharedSystemComparator(String sort_field, String sort_direction) {
		this.sort_field = sort_field;
		this.sort_direction = sort_direction;
	}
	
	@Override
	public int compare(SharedSystem arg0, SharedSystem arg1) {
		if(sort_field.equalsIgnoreCase("system_id")) {
			if(sort_direction.equalsIgnoreCase("desc")) {
				return longToInt(arg1.getDate_created().getTime() - arg0.getDate_created().getTime());
			} else if(sort_direction.equalsIgnoreCase("asc")) {
				return longToInt(arg0.getDate_created().getTime() - arg1.getDate_created().getTime());
			}
		} else if(sort_field.equalsIgnoreCase("num_planets")) {
			if(sort_direction.equalsIgnoreCase("desc")) {
				return longToInt(arg1.getNum_planets() - arg0.getNum_planets());
			} else if(sort_direction.equalsIgnoreCase("asc")) {
				return longToInt(arg0.getNum_planets() - arg1.getNum_planets());
			}
		}
		return 0;
	}
	
	public static int longToInt(long l) {
	    if (l < Integer.MIN_VALUE)
	    	return Integer.MIN_VALUE;
	    else if(l > Integer.MAX_VALUE)
	    	return Integer.MAX_VALUE;
	    else
	    	return (int) l;
	}

}
