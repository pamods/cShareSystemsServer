package com.pa.conundrum.cShareSystemsServer.datastore;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

public abstract class DatastoreModel {
	
	protected abstract String[] getIndexedFields();
	
	protected abstract long getId();
	
	protected abstract void onCreate();

	public static boolean create(DatastoreModel model) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		boolean success = false;
		
        try {
        	model.onCreate();
            pm.makePersistent(model);
            addIndexedValues(model);
            
            success = true;
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        	success = false;
        } finally {
            pm.close();
        }
        
        return success;
	}
	
	static void addIndexedValues(DatastoreModel model) throws PutException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Document.Builder docBuilder = Document.newBuilder().setId("" + model.getId());
		
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(model.getClass().getSimpleName()).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		
		System.out.println("Adding document to index for " + model.getClass().getSimpleName());
		
		for(int i=0; i<model.getIndexedFields().length; i++) {
			String indexedfield = model.getIndexedFields()[i];
			
			java.lang.reflect.Field indexedField = model.getClass().getField(indexedfield);
			String indexedValue = (String)indexedField.get(model);
			System.out.println("Indexing field: " + indexedfield + " with value: " + indexedValue);

			docBuilder.addField(Field.newBuilder().setName(indexedfield).setText(indexedValue));
		}
		
		Document doc = docBuilder.build();
		
		try {
			index.put(doc);
			System.out.println("Document successfully indexed!");
			System.out.println();
	    } catch (PutException e) {
	        if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
	        	index.put(doc);
	        }
	    }
	}
}
