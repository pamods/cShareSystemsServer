package com.pa.conundrum.cShareSystemsServer.api;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchQueryException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;
import com.pa.conundrum.cShareSystems.api.datastore.models.SharedSystem;
import com.pa.conundrum.cShareSystems.datastore.PMF;
import com.pa.conundrum.cShareSystems.datastore.SharedSystemComparator;
import com.pa.conundrum.cShareSystemsServer.api.beans.ApiSearchRequestBean;

public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = -8404857379452342002L;

	List<Long> fulltextSearch(String name, String creator, int attempts) {
		ArrayList<Long> idList = new ArrayList<Long>();

		// SEARCH name, creator
		try {
			IndexSpec indexSpec = IndexSpec.newBuilder().setName("SharedSystem").build();
			Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

			String queryString = "";
			if((name==null || name.equals("")) && (creator==null | creator.equals(""))) {
				// Nothing to search for
				return idList;
			}

			if(name!=null && !name.equals("")) {
				queryString = "name: " + name;
				if(creator!=null && !creator.equals("")) {
					queryString += " AND creator: " + creator;
				}
			} else if(creator!=null && !creator.equals("")) {
				queryString = "creator: " + creator;
			}

			Results<ScoredDocument> results = index.search(queryString);
			
			// Record IDs
			for (ScoredDocument document : results) {
				try {
					String id = document.getId();
					idList.add(Long.parseLong(id));
				} catch(NumberFormatException nfe) { 
					// Skip it
				}
			}
		} catch (SearchException | SearchQueryException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
				if(attempts > 1) {
					return fulltextSearch(name, creator, attempts-1);
				}
			}
		}

		return idList;
	}

	SearchResultPair doSearch(ApiSearchRequestBean parameters, PersistenceManager pm) throws IOException {
		
		List<Long> idList = fulltextSearch(parameters.getName(), parameters.getCreator(), 2);

		if(idList.size() == 0 && ((parameters.getName() != null && !parameters.getName().equals("")) || (parameters.getCreator() != null && !parameters.getCreator().equals("")))) {
			return new SearchResultPair(0,"");
		}
		
		PriorityQueue<SharedSystem> systemsCollection = null;
		if(idList.size() > 0) {
			SharedSystemComparator comparator = new SharedSystemComparator(parameters.getSort_field(), parameters.getSort_direction());
			systemsCollection = new PriorityQueue<SharedSystem>(idList.size(), comparator);
	
			// Add to the collection and sort
			for(Long id: idList) {
				Key key = KeyFactory.createKey("SharedSystem", id);
				try {
					SharedSystem result = pm.getObjectById(SharedSystem.class, key);
					System.out.println(result.getName());
					systemsCollection.add(result);
				} catch(JDOObjectNotFoundException e) { }
			}
		}

		Collection<SharedSystem> results;
		if(systemsCollection != null && systemsCollection.size() > 0)
			results = systemsCollection;
		else
			results = getSearchResults(parameters, pm);

		long total = parameters.getStart() + results.size();
		int numResults = Math.min(parameters.getLimit(), results.size());
		String systems = "";

		if (!results.isEmpty()) {
			Iterator<SharedSystem> it = results.iterator();
			SharedSystem system;
			boolean firstItem = true;
			for(int j=0; j<numResults; j++) {
				if(it.hasNext())
					system = it.next();
				else
					break;
				if(system.getNum_planets() >= parameters.getMinPlanets() && system.getNum_planets() <= parameters.getMaxPlanets()) { 
					if(!firstItem) {
						systems += ", ";
					}
					firstItem = false;
					systems+=(system.getSystem());
				}
			}
		}

		return new SearchResultPair(total, systems);	
	}

	@SuppressWarnings("unchecked")
	Collection<SharedSystem> getSearchResults(ApiSearchRequestBean parameters, PersistenceManager pm) {

		Query query = pm.newQuery(SharedSystem.class);

		// Google hates me and won't allow a filter on one thing and sort on another
		// I'll just have to be a noob and filter the results list instead

		if(parameters.getSort_field().equals("system_id")) {
			query.setOrdering("date_created " + parameters.getSort_direction());
		} else if(parameters.getSort_field().equals("num_planets")) {
			query.setOrdering(parameters.getSort_field() + " " + parameters.getSort_direction());
		}

		// Set the limit to limit+1
		// There is no efficient way to count total the way I wanted, which would require a new query with no limit.
		// Luckily the client doesn't really care about the total results.
		// I just need to know if there are more.
		// I'm violating my own spec here, which will cause the client to break if I ever try to display the total number of results pages.
		long total =  parameters.getStart() + parameters.getLimit() + 1;
		query.setRange(parameters.getStart(), total);

		List<SharedSystem> results = new ArrayList<SharedSystem>();

		try {
			results = (List<SharedSystem>)query.execute();
		} finally {
			query.closeAll();
		}
		return results;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			ApiSearchRequestBean parameters = new ApiSearchRequestBean(request);
			PersistenceManager pm = PMF.get().getPersistenceManager();

			SearchResultPair pair = doSearch(parameters, pm);
			
			pm.close();

			System.out.println("{ \"request_time\": " + parameters.getRequest_time() + ", \"total\": " + pair.total + ", \"systems\": [" + pair.systems + "] }");
			response.getWriter().println("{ \"request_time\": " + parameters.getRequest_time() + ", \"total\": " + pair.total + ", \"systems\": [" + pair.systems + "] }");

		} catch(NumberFormatException | InvalidParameterException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	class SearchResultPair {
		long total;
		String systems;

		SearchResultPair(long t, String s) {
			total = t;
			systems = s;
		}
	}
}
