/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  PagedGridWrapper.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.webui.util.pagging;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

/**
 * Helper class for getting a paged record list that can be sorted by DB. <br>
 * <br>
 * 
 * All not used Listheaders must me declared as: <br>
 * listheader.setSortAscending(""); <br>
 * listheader.setSortDescending(""); <br>
 * 
 * <br>
 * zkoss 3.6.0 or greater (by using FieldComparator)
 */
@SuppressWarnings("rawtypes")
public class PagedGridWrapper<E> extends ListModelList implements Serializable {
	private static final long serialVersionUID = -7399727307122148637L;
	static final Logger logger = Logger.getLogger(PagedGridWrapper.class);

	// Service that calls the DAO methods
	private PagedListService pagedListService;

	// param. The listboxes paging component
	private Paging paging;

	// param. The SearchObject, holds the entity and properties to search. <br>
	private JdbcSearchObject<E> jdbcSearchObject;

	/**
	 * default constructor.<br>
	 */
	public PagedGridWrapper() {
		super();
	}

	public void init(JdbcSearchObject<E> jdbcSearchObject1, Grid grid) {
		init(jdbcSearchObject1, grid, grid.getPagingChild());
	}

	public void init(JdbcSearchObject<E> jdbcSearchObject1, Grid grid, Paging paging1) {

		setPaging(paging1);
		setListeners(grid);

		setSearchObject(jdbcSearchObject1);

		grid.setModel(this);
	}

	@SuppressWarnings("unchecked")
	private void initModel() {
		getSearchObject().setFirstResult(0);
		getSearchObject().setMaxResults(getPageSize());

		// clear old data
		clear();

		final SearchResult<E> searchResult = getPagedListService().getSRBySearchObject(getSearchObject());
		getPaging().setTotalSize(searchResult.getTotalCount());
		addAll(searchResult.getResult());
	}

	/**
	 * Refreshes the list by calling the DAO methode with the modified search
	 * object. <br>
	 * 
	 * @param start
	 *            Row to start. <br>
	 */
	@SuppressWarnings("unchecked")
	void refreshModel(int start) {
		getSearchObject().setFirstResult(start);
		getSearchObject().setMaxResults(getPageSize());

		// clear old data
		clear();

		addAll(getPagedListService().getBySearchObject(getSearchObject()));
	}

	public void clearFilters() {
		getSearchObject().clearFilters();
		initModel();
	}

	/**
	 * Sets the listeners. <br>
	 * <br>
	 * 1. "onPaging" for the paging component. <br>
	 */
	private void setListeners(Grid aGrid) {

		// Add 'onPaging' listener to the paging component
		getPaging().addEventListener("onPaging", new OnPagingEventListener());

		aGrid.setModel(this);
	}

	/**
	 * "onPaging" EventListener for the paging component. <br>
	 * <br>
	 * Calculates the next page by currentPage and pageSize values. <br>
	 * Calls the method for refreshing the data with the new rowStart and
	 * pageSize. <br>
	 */
	public final class OnPagingEventListener implements EventListener<Event> {
		
		public OnPagingEventListener() {
			
		}
		
		@Override
		public void onEvent(Event event) throws Exception {

			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();

			// refresh the list
			refreshModel(start);
		}
	}

	public PagedListService getPagedListService() {
		return this.pagedListService;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	JdbcSearchObject<E> getSearchObject() {
		return this.jdbcSearchObject;
	}

	public int getPageSize() {
		return getPaging().getPageSize();
	}

	Paging getPaging() {
		return this.paging;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private void setPaging(Paging paging) {
		this.paging = paging;
	}

	public void setSearchObject(JdbcSearchObject<E> jdbcSearchObject1) {
		this.jdbcSearchObject = jdbcSearchObject1;
		initModel();
	}

}
