package com.pa.conundrum.cShareSystems.api.datastore.models;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;
import com.pa.conundrum.cShareSystems.datastore.DatastoreModel;

@PersistenceCapable
public class SharedSystem extends DatastoreModel {
		
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long system_id;
	
	@Persistent
	Long num_planets;
	
	@Persistent
	public String name;
	
	@Persistent
	public String creator;
	
	@Persistent
	Text system;
	
	@Persistent
	String ip;
	
	@Persistent
	Date date_created;
	
	// Index name and creator to make them fulltext searchable	
	private String[] indexedFields = {
		"name",
		"creator"
	};
	
	public SharedSystem() {	}
	
	protected String[] getIndexedFields() {
		return indexedFields;
	}
	
	protected long getId() {
		return getSystem_id();
	}
	
	protected void onCreate() {
		date_created = new Date();
	}
	
	public long getSystem_id() {
		return system_id;
	}
	
	public long getNum_planets() {
		return num_planets;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public String getSystem() {
		return system.getValue();
	}
	
	public String getIp() {
		return ip;
	}
	
	public Date getDate_created() {
		return date_created;
	}
	
	public void setSystem_id(long system_id) {
		this.system_id = system_id;
	}
	
	public void setNum_planets(long num_planets) {
		this.num_planets = num_planets;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public void setSystem(String system) {
		this.system = new Text(system);
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setDate_created(Date date_created) {
		this.date_created = date_created;
	}
}
