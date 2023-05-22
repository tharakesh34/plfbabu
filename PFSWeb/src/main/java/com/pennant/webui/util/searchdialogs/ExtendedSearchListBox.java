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
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : ExtendedSearchListBox.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 26-04-2011
 * 
 * Modified Date : 26-04-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.util.searchdialogs;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.North;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Separator;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.DataTypeUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can search and select a branch object. By onClosing this box <b>returns</b> an object or null. <br>
 * The object can returned by selecting and clicking the OK button or by DoubleClicking on an item from the list.<br>
 * Further the count of results can limited by manipulating the value of a table field for the sql where clause.<br>
 */
public class ExtendedSearchListBox extends Window implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ExtendedSearchListBox.class);
	private Textbox _textbox;

	private Button _searchButton;
	private Paging _paging;

	private static int pageSize = 10;
	private Listbox listbox;
	@SuppressWarnings("rawtypes")
	private ListModelList listModelList;
	private final int _height = 410;
	private int _width = 300;

	// the returned bean object
	private Object objClass = null;
	@SuppressWarnings("rawtypes")
	private JdbcSearchObject jdbcSearchObject;
	private transient PagedListService pagedListService;
	private String[] fieldString = null;
	private ModuleMapping moduleMapping = null;
	private Filter[] filters;
	private List<?> listData = null;
	private String searchString;
	private String whereClause = null;
	private boolean search = true;
	private String valueColumn;
	private DataType valueType;

	private boolean multySelection;
	private transient Map<String, Object> selectedValues = new HashMap<>();

	public ExtendedSearchListBox() {
		super();
	}

	public ExtendedSearchListBox(Component parent, String moduleCode) {
		super();
		setModuleMapping(ModuleUtil.getModuleMapping(moduleCode));
		setParent(parent);
	}

	public ExtendedSearchListBox(Component parent, String moduleCode, String searchValue) {
		super();
		setModuleMapping(ModuleUtil.getModuleMapping(moduleCode));
		this.searchString = searchValue;
		setParent(parent);
	}

	public ExtendedSearchListBox(Component parent, String moduleCode, String searchValue, Filter[] filters) {
		super();
		setModuleMapping(ModuleUtil.getModuleMapping(moduleCode));
		this.searchString = searchValue;
		this.filters = filters;
		setParent(parent);
	}

	public ExtendedSearchListBox(Component parent, String moduleCode, String searchValue, String whereClause) {
		super();
		setModuleMapping(ModuleUtil.getModuleMapping(moduleCode));
		this.searchString = searchValue;
		this.whereClause = whereClause;
		setParent(parent);
	}

	public ExtendedSearchListBox(Component parent, String moduleCode, String searchValue, Filter[] filters,
			String whereClause) {
		super();
		setModuleMapping(ModuleUtil.getModuleMapping(moduleCode));
		this.searchString = searchValue;
		this.filters = filters;
		this.whereClause = whereClause;
		setParent(parent);
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	public ExtendedSearchListBox(Component parent, String moduleCode, Filter[] filters) {
		super();
		this.filters = filters;
		setModuleMapping(PennantJavaUtil.getModuleMap(moduleCode));
		setParent(parent);
	}

	public ExtendedSearchListBox(Component parent, String moduleCode, String searchValue, String whereClause,
			String valueColumn, DataType valueType) {
		super();
		this.searchString = searchValue;
		this.whereClause = whereClause;
		setModuleMapping(PennantJavaUtil.getModuleMap(moduleCode));
		setParent(parent);
		this.valueColumn = valueColumn;
		this.valueType = valueType;
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	public ExtendedSearchListBox(Component parent, String listCode, Filter[] filters, String searchValue) {
		super();
		this.filters = filters;
		this.searchString = searchValue;
		setModuleMapping(PennantJavaUtil.getModuleMap(listCode));
		setParent(parent);
	}

	public ExtendedSearchListBox(Component parent, String listCode, Filter[] filters, String searchValue,
			String whereClause, String valueColumn, DataType valueType) {
		super();

		this.filters = filters;
		this.searchString = searchValue;
		this.whereClause = whereClause;
		setModuleMapping(PennantJavaUtil.getModuleMap(listCode));
		setParent(parent);
		this.valueColumn = valueColumn;
		this.valueType = valueType;
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	public ExtendedSearchListBox(Component parent, String listCode, List<?> listData, boolean search) {
		super();
		setModuleMapping(PennantJavaUtil.getModuleMap(listCode));
		setParent(parent);
		this.listData = listData;
		this.search = search;
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode);
		component.createBox();
		return component.getObject();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode, String searchValue) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, searchValue);
		component.createBox();
		return component.getObject();
	}

	public static Object show(Component parent, String listCode, String searchValue, String whereClause) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, searchValue, whereClause);
		component.createBox();
		return component.getObject();
	}

	public static Object show(Component parent, String listCode, String searchValue, Filter[] filters) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, searchValue, filters);
		component.createBox();
		return component.getObject();
	}

	public static Object show(Component parent, String listCode, String searchValue, Filter[] filters,
			String whereClause) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, searchValue, filters,
				whereClause);
		component.createBox();
		return component.getObject();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * 
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, String listCode, List<?> list) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, list, false);
		component.createBox();
		return component.getObject();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * 
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode, Filter[] filters) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, filters);
		component.createBox();
		return component.getObject();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * 
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode, Filter[] filters, String searchValue) {
		ExtendedSearchListBox component = new ExtendedSearchListBox(parent, listCode, filters, searchValue);
		component.createBox();
		return component.getObject();
	}

	/**
	 * Creates the components, sets the model and show the window as modal.<br>
	 */
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public void createBox() {
		logger.debug("Entering");

		if (getModuleMapping().getLovWidth() != 0) {
			this._width = getModuleMapping().getLovWidth();
		}

		// Window
		this.setWidth(String.valueOf(this._width) + "px");
		this.setHeight(String.valueOf(this._height) + "px");

		this.setVisible(true);
		this.setClosable(true);

		// Borderlayout
		final Borderlayout bl = new Borderlayout();
		bl.setHeight("100%");
		bl.setWidth("100%");
		bl.setParent(this);

		final Center center = new Center();
		center.setBorder("none");
		center.setFlex(true);
		center.setParent(bl);

		// Borderlayout
		final Borderlayout bl2 = new Borderlayout();
		bl2.setHeight("100%");
		bl2.setWidth("100%");
		bl2.setParent(center);

		final North north2 = new North();
		north2.setBorder("none");
		north2.setHeight("26px");
		north2.setParent(bl2);
		// Paging
		this._paging = new Paging();
		this._paging.setDetailed(true);
		this._paging.addEventListener("onPaging", new OnPagingEventListener());
		this._paging.setPageSize(getPageSize());
		this._paging.setParent(north2);
		this._paging.setStyle("border-top:1px solid #C5C5C5;");

		final Center center2 = new Center();
		center2.setBorder("none");
		center2.setFlex(true);
		center2.setParent(bl2);
		// Div Center area
		final Div divCenter2 = new Div();
		divCenter2.setWidth("100%");
		divCenter2.setHeight("100%");
		divCenter2.setParent(center2);

		// Listbox
		this.listbox = new Listbox();
		this.listbox.setCheckmark(multySelection);
		this.listbox.setMultiple(multySelection);
		this.listbox.setHeight("290px");
		this.listbox.setVisible(true);
		// this.listbox.setSizedByContent(true);
		this.listbox.setSpan(true);
		this.listbox.setEmptyMessage(Labels.getLabel("listbox.emptyMessage"));

		this.listbox.setParent(divCenter2);
		this.listbox.setItemRenderer(new SearchBoxItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);

		for (int i = 0; i < this.fieldString.length; i++) {
			final Listheader listheader = new Listheader();
			if (multySelection) {
				listheader.setSclass("BCMListHeader" + i);
			} else {
				listheader.setSclass("ListHeader" + i);
			}
			listheader.setParent(listhead);
			listheader.setLabel(Labels.getLabel("label_" + this.fieldString[i]));
			listheader.setHflex("min");
		}

		final South south2 = new South();
		south2.setBorder("none");
		south2.setHeight("26px");
		south2.setParent(bl2);

		// hbox for holding the Textbox + SearchButton
		final Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setWidth("100%");
		hbox.setHeight("27px");
		hbox.setParent(south2);
		// textbox for inserting the search parameter
		this._textbox = new Textbox();
		this._textbox.setWidth("100%");
		this._textbox.setMaxlength(200);
		this._textbox.addEventListener("onOK", new OnSearchListener());
		this._textbox.setParent(hbox);
		this._textbox.setFocus(true);
		if (StringUtils.isNotBlank(this.searchString)) {
			this._textbox.setValue(this.searchString);
		}
		Space space = new Space();
		space.setWidth("1px");
		hbox.appendChild(space);
		// serachButton
		this._searchButton = new Button();
		this._searchButton.setImage("/images/icons/LOVSearch.png");
		this._searchButton.addEventListener("onClick", new OnSearchListener());
		this._searchButton.setParent(hbox);
		if (search) {
			hbox.setVisible(true);
			north2.setVisible(true);
		} else {
			hbox.setVisible(false);
			north2.setVisible(false);
		}

		final South south = new South();
		south.setBorder("none");
		south.setHeight("40px");
		south.setParent(bl);

		final Div divSouth = new Div();
		divSouth.setWidth("100%");
		divSouth.setHeight("100%");
		divSouth.setParent(south);

		final Separator sep = new Separator();
		sep.setBar(true);
		sep.setOrient("horizontal");
		sep.setParent(divSouth);

		// Button
		final Button btnOK = new Button();
		btnOK.setLabel("OK");
		btnOK.addEventListener("onClick", new OnCloseListener());
		btnOK.setParent(divSouth);
		divSouth.appendChild(new Space());
		// Button
		final Button btnClear = new Button();
		btnClear.setLabel("Clear");
		btnClear.addEventListener("onClick", new OnClearListener());
		btnClear.setParent(divSouth);

		/**
		 * init the model.<br>
		 * The ResultObject is a helper class that holds the generic list and the totalRecord count as int value.
		 */
		if (search) {
			setJdbcSearchObject(0);
			final String searchText = ExtendedSearchListBox.this._textbox.getValue();

			// Add the search filters.
			if (StringUtils.isNotBlank(searchText)) {
				jdbcSearchObject.addFilterOr(getSearchFilters(fieldString, searchText));
			}

			// Add the default sort on first TWO columns, if exists.
			String[] lovFields = getModuleMapping().getLovFields();

			if (lovFields != null) {
				if (lovFields.length > 0) {
					jdbcSearchObject.addSort(lovFields[0], false);
				}

				if (lovFields.length > 1) {
					jdbcSearchObject.addSort(lovFields[1], false);
				}
			}

			if (whereClause != null) {
				this.jdbcSearchObject.addWhereClause(whereClause);
			}

			final SearchResult searchResult = getPagedListService().getSRBySearchObject(getJdbcSearchObject());
			logger.debug("After fetch jdbc Search Count:" + searchResult.getTotalCount());

			this._paging.setTotalSize(searchResult.getTotalCount());
			setListModelList(new ListModelList(searchResult.getResult()));
		} else {
			this._paging.setTotalSize(listData.size());
			setListModelList(new ListModelList(listData));
		}
		this.listbox.setModel(getListModelList());

		try {
			doModal();
		} catch (final SuspendNotAllowedException e) {
			logger.fatal("", e);
			this.detach();
		}

		logger.debug("Leaving");
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class SearchBoxItemRenderer implements ListitemRenderer<Object> {
		public SearchBoxItemRenderer() {
			//
		}

		@Override
		public void render(Listitem item, Object data, int count) throws Exception {
			((Listbox) item.getParent()).setMultiple(isMultySelection());
			for (int i = 0; i < fieldString.length; i++) {
				String fieldValue = "";
				String fieldMethod = "get" + fieldString[i].substring(0, 1).toUpperCase() + fieldString[i].substring(1);

				fieldValue = (String) invokeMethod(fieldMethod, data);
				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
					Date fieldDate = DateUtil.parse(fieldValue, DateFormat.FULL_DATE_TIME.getPattern());
					fieldValue = DateUtil.formatToLongDate(fieldDate);
				}

				if (!search && i == 0) {
					fieldValue = PennantApplicationUtil.formatAccountNumber(fieldValue);
				}

				if (isMultySelection()) {
					if (StringUtils.equals(fieldString[i], valueColumn) && selectedValues.containsKey(fieldValue)) {
						if (selectedValues.containsKey(invokeMethod(fieldMethod, data))) {
							item.setSelected(true);
						}

					}
				}

				Listcell lc = new Listcell(fieldValue);
				lc.setParent(item);
			}

			item.setAttribute("data", data);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");
		}
	}

	/**
	 * If a DoubleClick appears on a listItem. <br>
	 * This method is forwarded in the renderer.<br>
	 * 
	 * @param event
	 */
	public void onDoubleClicked(Event event) {
		logger.debug("Entering");

		if (this.listbox.getSelectedItem() != null) {
			logger.debug("Selected Iteam :" + this.listbox.getSelectedItem().getValue());
			final Listitem li = this.listbox.getSelectedItem();
			final Object object = li.getAttribute("data");

			final Listcell lc = (Listcell) li.getFirstChild();
			try {

				@SuppressWarnings("rawtypes")
				Class[] stringType = { Class.forName("java.lang.String") };
				Object[] stringParameter = { lc.getLabel() };
				if (object.getClass().getMethod("setLovValue", stringType) != null) {
					object.getClass().getMethod("setLovValue", stringType).invoke(object, stringParameter);
				}

			} catch (Exception e) {
				logger.error("setLovValue method is not found in bean class.");
			}
			setObject(object);
			this.onClose();
		} else {
			logger.debug("Selected Iteam null");
		}
		logger.debug("Leaving");
	}

	/**
	 * "onPaging" EventListener for the paging component. <br>
	 * <br>
	 * Calculates the next page by currentPage and pageSize values. <br>
	 * Calls the method for refreshing the data with the new rowStart and pageSize. <br>
	 */
	@SuppressWarnings("rawtypes")
	public final class OnPagingEventListener implements EventListener {
		public OnPagingEventListener() {
			//
		}

		@Override
		public void onEvent(Event event) throws Exception {
			resetSelectedItems();

			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();

			final String searchText = ExtendedSearchListBox.this._textbox.getValue();
			// refresh the list
			refreshModel(searchText, start);
		}
	}

	/**
	 * Refreshes the list by calling the DAO methode with the modified search object. <br>
	 * 
	 * @param so    SearchObject, holds the entity and properties to search. <br>
	 * @param start Row to start. <br>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void refreshModel(String searchText, int start) {
		logger.debug("Entering");

		// clear old data
		getListModelList().clear();

		setJdbcSearchObject(start);

		// Add the search filters.
		if (StringUtils.isNotBlank(searchText)) {
			jdbcSearchObject.addFilterOr(getSearchFilters(fieldString, searchText));
		}

		String[] lovFields = getModuleMapping().getLovFields();
		// if module filters are 1
		if (lovFields != null && lovFields.length == 1) {
			this.jdbcSearchObject.addSort(lovFields[0].trim(), false);
		} else if (lovFields != null && lovFields.length > 1) { // if module filters are > 1
			this.jdbcSearchObject.addSort(lovFields[0].trim(), false);
			this.jdbcSearchObject.addSort(lovFields[1].trim(), false);
		}

		if (whereClause != null) {
			this.jdbcSearchObject.addWhereClause(whereClause);
		}

		final SearchResult searchResult = getPagedListService().getSRBySearchObject(getJdbcSearchObject());
		this._paging.setTotalSize(searchResult.getTotalCount());

		// set the model
		setListModelList(new ListModelList(searchResult.getResult()));
		this.listbox.setModel(getListModelList());

		logger.debug("Leaving");
	}

	/**
	 * Inner OnSearchListener class.<br>
	 */
	@SuppressWarnings("rawtypes")
	final class OnSearchListener implements EventListener {
		public OnSearchListener() {
			//
		}

		@Override
		public void onEvent(Event event) throws Exception {
			resetSelectedItems();

			final String searchText = ExtendedSearchListBox.this._textbox.getValue();

			// we start new
			refreshModel(searchText, 0);
		}
	}

	/**
	 * Inner OnCloseListener class.<br>
	 */
	@SuppressWarnings("rawtypes")
	final class OnCloseListener implements EventListener {
		public OnCloseListener() {
			//
		}

		@Override
		public void onEvent(Event event) throws Exception {
			if (multySelection && ExtendedSearchListBox.this.listbox.getSelectedItems() != null) {
				resetSelectedItems();

				setObject(selectedValues);
			} else if (ExtendedSearchListBox.this.listbox.getSelectedItem() != null) {
				final Listitem li = ExtendedSearchListBox.this.listbox.getSelectedItem();
				final Object object = li.getAttribute("data");

				setObject(object);
			}
			onClose();
		}
	}

	@SuppressWarnings("rawtypes")
	final class OnClearListener implements EventListener {
		public OnClearListener() {
			//
		}

		@Override
		public void onEvent(Event event) throws Exception {
			setObject(String.valueOf(""));
			onClose();
		}
	}

	public Object getObject() {
		return this.objClass;
	}

	private void setObject(Object objClass) {
		this.objClass = objClass;
	}

	public int getPageSize() {
		return ExtendedSearchListBox.pageSize;
	}

	@SuppressWarnings("rawtypes")
	public void setListModelList(ListModelList listModelList) {
		this.listModelList = listModelList;
	}

	@SuppressWarnings("rawtypes")
	public ListModelList getListModelList() {
		return this.listModelList;
	}

	public PagedListService getPagedListService() {
		if (this.pagedListService == null) {
			this.pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		}
		return pagedListService;
	}

	@SuppressWarnings("rawtypes")
	public JdbcSearchObject getJdbcSearchObject() {
		return this.jdbcSearchObject;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setJdbcSearchObject(int start) {
		this.jdbcSearchObject = new JdbcSearchObject(getModuleMapping().getModuleClass());
		this.jdbcSearchObject.setFirstResult(start);
		this.jdbcSearchObject.setMaxResults(getPageSize());
		this.jdbcSearchObject.addTabelName(getModuleMapping().getLovTableName());

		if (this.filters != null) {
			for (int i = 0; i < filters.length; i++) {
				if (filters[i] == null) {
					continue;
				}
				this.jdbcSearchObject.addFilter(filters[i]);
			}
		}

		if (getModuleMapping().getLovFilters() != null) {
			Object[][] condArray = getModuleMapping().getLovFilters();
			Filter filter1;

			for (int i = 0; i < condArray.length; i++) {
				filter1 = new Filter((String) condArray[i][0], condArray[i][2],
						Integer.parseInt((String) condArray[i][1]));
				this.jdbcSearchObject.addFilter(filter1);
			}
		}
	}

	public ModuleMapping getModuleMapping() {
		return moduleMapping;
	}

	public void setModuleMapping(ModuleMapping moduleMapping) {
		this.moduleMapping = moduleMapping;

		if (moduleMapping != null) {
			this.fieldString = moduleMapping.getLovFields();
			this.setTitle(Labels.getLabel(moduleMapping.getModuleName()));
		}
	}

	/**
	 * Get the search filters for all the fields with the specified search text.
	 * 
	 * @param fields The fields for which the search condition to be built.
	 * @param value  The value to search.
	 * @return The search filters for all the fields with the specified search text.
	 */
	private Filter[] getSearchFilters(String[] fields, String value) {
		List<Filter> searchFilters = new ArrayList<>();
		boolean valueColumnAdded = false;

		for (String field : fields) {
			// If the data type of value column is known, build appropriate search filter.
			if (field.equals(valueColumn) && valueType != null) {
				if (valueType == DataType.LONG) {
					searchFilters.add(getSearchFilter(field, value, valueType));
				} else {
					searchFilters.add(getSearchFilter(field, value, DataType.STRING));
				}
				valueColumnAdded = true;
				continue;

			} else if (valueType == DataType.LONG) {
				searchFilters.add(getSearchFilter(field, value, valueType));
			} else {
				searchFilters.add(getSearchFilter(field, value, DataType.STRING));
			}
		}

		// Add the additional filter for value column, only if not specified in the module mapping.
		if (!valueColumnAdded && valueColumn != null && valueType != null) {
			if (valueType == DataType.LONG) {
				searchFilters.add(getSearchFilter(valueColumn, value, valueType));
			} else {
				searchFilters.add(getSearchFilter(valueColumn, value, DataType.STRING));
			}
		}

		return searchFilters.toArray(new Filter[searchFilters.size()]);
	}

	/**
	 * Get the search filter.
	 * 
	 * @param field The field name.
	 * @param value The value to search.
	 * @param type  The data type of value.
	 * @return The search filter.
	 */
	private Filter getSearchFilter(String field, String value, DataType type) {
		Object object = getValueAsObject(field, value, type);

		if (object instanceof String && type == DataType.STRING) {
			return new Filter(field, "%" + value + "%", Filter.OP_LIKE);
		} else {
			return new Filter(field, object, Filter.OP_EQUAL);
		}
	}

	private Object getValueAsObject(String field, String value, DataType type) {
		return DataTypeUtil.getValueAsObject(field, value, getModuleMapping().getModuleClass());
	}

	private Object invokeMethod(String methodName, Object object) {
		try {
			Object result = object.getClass().getMethod(methodName).invoke(object);

			return result == null ? "" : result.toString();
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}
	}

	public boolean isMultySelection() {
		return multySelection;
	}

	public void setMultySelection(boolean multySelection) {
		this.multySelection = multySelection;
	}

	public Map<String, Object> getSelectedValues() {
		return selectedValues;
	}

	public void setSelectedValues(Map<String, Object> selectedValues) {
		this.selectedValues = selectedValues;
	}

	private void resetSelectedItems() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		for (Listitem item : ExtendedSearchListBox.this.listbox.getItems()) {
			Object object = item.getAttribute("data");

			String fieldMethod = "get" + fieldString[0].substring(0, 1).toUpperCase() + fieldString[0].substring(1);
			String fieldValue = "";

			if (object.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
				fieldValue = (String) object.getClass().getMethod(fieldMethod).invoke(object);
			} else {
				fieldValue = object.getClass().getMethod(fieldMethod).invoke(object).toString();
			}

			selectedValues.remove(fieldValue);
			if (item.isSelected()) {
				selectedValues.put(fieldValue, object);
			}
		}
	}
}
