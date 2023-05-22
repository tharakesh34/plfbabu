/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : PagedListWrapper.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.util.pagging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
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
public class PagedListWrapper<E> extends ListModelList implements Serializable {
	private static final long serialVersionUID = -7399762307122148637L;
	static final Logger logger = LogManager.getLogger(PagedListWrapper.class);

	// Service that calls the DAO methods
	private PagedListService pagedListService;

	// param. The listboxes paging component
	private Paging paging;

	// param. The SearchObject, holds the entity and properties to search. <br>
	private JdbcSearchObject<E> jdbcSearchObject;
	private List<E> itemList;
	boolean searchObject = true;

	/**
	 * default constructor.<br>
	 */
	public PagedListWrapper() {
		super();
	}

	@SuppressWarnings("unchecked")
	public void initList(List<E> iteamList, Listbox listBox, Paging paging1) {
		setPaging(paging1);
		setListeners(listBox);
		clear();
		getPaging().setTotalSize(iteamList.size());
		this.itemList = iteamList;
		List<E> tmpList = new ArrayList<E>();

		for (int i = 0; i < getPaging().getPageSize(); i++) {
			if (iteamList.size() <= i) {
				break;
			}
			tmpList.add(iteamList.get(i));
		}

		addAll(tmpList);
		searchObject = false;
	}

	public void init(JdbcSearchObject<E> jdbcSearchObject1, Listbox listBox, Paging paging1) {

		/*
		 * if(getSearchObject()!=null){ setSearchObject(jdbcSearchObject1); getPaging().setActivePage(0); }else{
		 */
		setPaging(paging1);
		setListeners(listBox);
		setSearchObject(jdbcSearchObject1);
		// }

	}

	@SuppressWarnings("unchecked")
	private void initModel() {
		getSearchObject().setFirstResult(0);
		getSearchObject().setMaxResults(getPageSize());

		// clear old data
		clear();

		final SearchResult<E> searchResult = getPagedListService().getSRBySearchObject(getSearchObject());

		if (getPaging() != null) {
			getPaging().setTotalSize(searchResult.getTotalCount());
		}

		addAll(searchResult.getResult());
	}

	/**
	 * Refreshes the list by calling the DAO methode with the modified search object. <br>
	 * 
	 * @param start Row to start. <br>
	 */
	@SuppressWarnings("unchecked")
	void refreshModel(int start) {
		if (searchObject) {
			getSearchObject().setFirstResult(start);
			getSearchObject().setMaxResults(getPageSize());

			// clear old data
			clear();

			addAll(getPagedListService().getBySearchObject(getSearchObject()));
		} else {
			clear();
			getPaging().setTotalSize(itemList.size());
			List<E> tmpList = new ArrayList<E>();
			for (int i = 0; i < getPaging().getPageSize(); i++) {
				if (itemList.size() <= i + start) {
					break;
				}
				tmpList.add(itemList.get(i + start));
			}

			addAll(tmpList);
		}
	}

	public void clearFilters() {
		getSearchObject().clearFilters();
		initModel();
	}

	/**
	 * Sets the listeners. <br>
	 * <br>
	 * 1. "onPaging" for the paging component. <br>
	 * 2. "onSort" for all listheaders that have a sortDirection declared. <br>
	 * All not used Listheaders must me declared as: listheader.setSortAscending(""); listheader.setSortDescending("");
	 * <br>
	 */
	@SuppressWarnings("unchecked")
	private void setListeners(Listbox listBox) {

		// Remove Listener Events If Already Exists
		Iterable<EventListener<? extends Event>> iter = getPaging().getEventListeners("onPaging");
		if (iter != null && iter.iterator().hasNext()) {
			getPaging().removeEventListener("onPaging", iter.iterator().next());
		}

		// Add 'onPaging' listener to the paging component
		getPaging().addEventListener("onPaging", new OnPagingEventListener());

		final Listhead listhead = listBox.getListhead();
		final List<Component> list = listhead.getChildren();

		final OnSortEventListener onSortEventListener = new OnSortEventListener();
		for (final Object object : list) {
			if (object instanceof Listheader) {
				final Listheader lheader = (Listheader) object;

				if (lheader.getSortAscending() != null || lheader.getSortDescending() != null) {

					// Remove Listener Events If Already Exists
					Iterable<EventListener<? extends Event>> sort = lheader.getEventListeners("onSort");
					if (sort != null && sort.iterator().hasNext()) {
						lheader.removeEventListener("onSort", sort.iterator().next());
					}

					// Add 'onSort' listener to the ListHeader component
					lheader.addEventListener("onSort", onSortEventListener);
				}
			}
		}
		listBox.setModel(this);
	}

	/**
	 * "onPaging" EventListener for the paging component. <br>
	 * <br>
	 * Calculates the next page by currentPage and pageSize values. <br>
	 * Calls the method for refreshing the data with the new rowStart and pageSize. <br>
	 */
	public final class OnPagingEventListener implements EventListener<Event> {

		public OnPagingEventListener() {
		    super();
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

	/**
	 * "onSort" eventlistener for the listheader components. <br>
	 * <br>
	 * Checks wich listheader is clicked and checks which orderDirection must be set. <br>
	 * 
	 * Calls the methode for refreshing the data with the new ordering. and the remembered rowStart and pageSize. <br>
	 */
	public final class OnSortEventListener implements EventListener, Serializable {
		private static final long serialVersionUID = 1L;

		public OnSortEventListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			final Listheader lh = (Listheader) event.getTarget();
			final String sortDirection = lh.getSortDirection();

			if ("ascending".equals(sortDirection)) {
				final Comparator<?> cmpr = lh.getSortDescending();
				if (cmpr instanceof FieldComparator) {
					String orderBy = ((FieldComparator) cmpr).getOrderBy();
					orderBy = StringUtils.substringBefore(orderBy, "DESC").trim();

					// update SearchObject with orderBy
					if (searchObject) {
						getSearchObject().clearSorts();
						getSearchObject().addSort(orderBy, true);
					}
				}
			} else if ("descending".equals(sortDirection) || "natural".equals(sortDirection)
					|| Strings.isBlank(sortDirection)) {
				final Comparator<?> cmpr = lh.getSortAscending();
				if (cmpr instanceof FieldComparator) {
					String orderBy = ((FieldComparator) cmpr).getOrderBy();
					orderBy = StringUtils.substringBefore(orderBy, "ASC").trim();

					// update SearchObject with orderBy
					if (searchObject) {
						getSearchObject().clearSorts();
						getSearchObject().addSort(orderBy, false);
					}
				}
			}

			// refresh the list
			getPaging().setActivePage(0);
			refreshModel(0);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public int getPageSize() {
		if (getPaging() == null) {
			return 0;
		}
		return getPaging().getPageSize();
	}

	Paging getPaging() {
		return this.paging;
	}

	private void setPaging(Paging paging) {
		this.paging = paging;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PagedListService getPagedListService() {
		return this.pagedListService;
	}

	public void setSearchObject(JdbcSearchObject<E> jdbcSearchObject1) {
		this.jdbcSearchObject = jdbcSearchObject1;
		initModel();
	}

	JdbcSearchObject<E> getSearchObject() {
		return this.jdbcSearchObject;
	}

}
