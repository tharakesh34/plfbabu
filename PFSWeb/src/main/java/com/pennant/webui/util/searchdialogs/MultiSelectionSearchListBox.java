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
 * FileName : MultiSelectionSearchListBox.java
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.zkoss.zul.Checkbox;
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

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can search and select a branch object. By onClosing this box <b>returns</b> an object or null. <br>
 * The object can returned by selecting and clicking the OK button or by DoubleClicking on an item from the list.<br>
 * Further the count of results can limited by manipulating the value of a table field for the sql where clause.<br>
 */
public class MultiSelectionSearchListBox extends Window implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(MultiSelectionSearchListBox.class);

	private Textbox _textbox;
	private Button _searchButton;
	private Paging _paging;
	private int pageSize = PennantConstants.searchGridSize;
	private Listbox listbox;
	@SuppressWarnings("rawtypes")
	private ListModelList listModelList;
	private final int _height = 410;
	private int _width = 300;

	private Object objClass = null;
	@SuppressWarnings("rawtypes")
	private JdbcSearchObject jdbcSearchObject;
	private transient PagedListService pagedListService;
	private String[] fieldString = null;
	private String selectedValues = null;
	private Map<String, String> checkMap = new HashMap<String, String>();
	private ModuleMapping moduleMapping = null;
	private List<ValueLabel> staticList = null;
	private boolean searchRequired = true;
	private Filter[] filters;
	private String whereClause = null;
	private Class<?> moduleClass = null;

	public MultiSelectionSearchListBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode, String selectedValues, Filter[] filters) {
		return new MultiSelectionSearchListBox(parent, listCode, selectedValues, filters, null).getObject();
	}

	/**
	 * Method for selecting values from Static List
	 * 
	 * @param parent
	 * @param listCode
	 * @param selectedValues
	 * @param filters
	 * @return
	 */
	public static Object show(Component parent, List<ValueLabel> staticList, String selectedValues, Filter[] filters) {
		return new MultiSelectionSearchListBox(parent, staticList, selectedValues, filters, null).getObject();
	}

	public static Object show(Component parent, String listCode, String selectedValues, Filter[] filters,
			String whereClause) {
		return new MultiSelectionSearchListBox(parent, listCode, selectedValues, filters, whereClause).getObject();
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	private MultiSelectionSearchListBox(Component parent, String listCode, String selectedValues, Filter[] filters,
			String whereClause) {
		super();
		this.filters = filters;
		this.selectedValues = selectedValues;
		searchRequired = true;
		this.staticList = null;
		this.whereClause = whereClause;
		setModuleMapping(PennantJavaUtil.getModuleMap(listCode));
		setCheckMap();
		setParent(parent);
		createBox();
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	private MultiSelectionSearchListBox(Component parent, List<ValueLabel> staticList, String selectedValues,
			Filter[] filters, String whereClause) {
		super();
		this.filters = filters;
		this.selectedValues = selectedValues;
		this.whereClause = whereClause;
		searchRequired = false;
		this.staticList = staticList;
		fieldString = new String[2];
		fieldString[0] = "Value";
		fieldString[1] = "Label";
		setModuleMapping(null);
		this.setTitle(Labels.getLabel("label_StaticList"));
		setCheckMap();
		setParent(parent);
		createBox();
	}

	/**
	 * Creates the components, sets the model and show the window as modal.<br>
	 */
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	private void createBox() {
		logger.debug("Entering");

		if (getModuleMapping() != null && getModuleMapping().getLovWidth() != 0) {
			this._width = getModuleMapping().getLovWidth();
		}

		// Window
		this.setWidth(String.valueOf(this._width + 5) + "px");
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
		// listbox.setStyle("border: none;");
		this.listbox.setHeight("290px");
		this.listbox.setMultiple(true);
		this.listbox.setVisible(true);
		this.listbox.setParent(divCenter2);
		this.listbox.setItemRenderer(new SearchBoxItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);

		for (int i = 0; i < this.fieldString.length; i++) {
			final Listheader listheader = new Listheader();
			listheader.setSclass("ListHeader" + i);
			listheader.setParent(listhead);
			listheader.setLabel(Labels.getLabel("label_" + this.fieldString[i]));
		}

		final South south2 = new South();
		south2.setBorder("none");
		south2.setHeight("26px");
		south2.setParent(bl2);
		// hbox for holding the Textbox + SearchButton
		final Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setStyle("padding-left: 5px");
		hbox.setWidth("100%");
		hbox.setHeight("27px");
		hbox.setParent(south2);
		// textbox for inserting the search parameter
		this._textbox = new Textbox();
		this._textbox.setWidth("100%");
		this._textbox.setMaxlength(200);
		this._textbox.setParent(hbox);
		this._textbox.setFocus(true);
		// serachButton
		this._searchButton = new Button();
		this._searchButton.setImage("/images/icons/search.gif");
		this._searchButton.addEventListener("onClick", new OnSearchListener());
		this._searchButton.setParent(hbox);
		if (searchRequired) {
			hbox.setVisible(true);
			north2.setVisible(true);
		} else {
			hbox.setVisible(false);
			north2.setVisible(false);
		}

		final South south = new South();
		south.setBorder("none");
		south.setHeight("30px");
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
		btnOK.setStyle("padding-left: 5px");
		btnOK.setLabel("OK");
		btnOK.addEventListener("onClick", new OnCloseListener());
		btnOK.setParent(divSouth);
		divSouth.appendChild(new Space());

		// Button
		final Button btnClear = new Button();
		btnClear.setStyle("padding-left: 5px");
		btnClear.setLabel("Clear");
		btnClear.addEventListener("onClick", new OnClearListener());
		btnClear.setParent(divSouth);

		/**
		 * init the model.<br>
		 * The ResultObject is a helper class that holds the generic list and the totalRecord count as int value.
		 */

		logger.debug("Before fetch jdbc Search");
		if (searchRequired) {
			setJdbcSearchObject(0);

			final SearchResult searchResult = getPagedListService().getSRBySearchObject(getJdbcSearchObject());
			logger.debug("After fetch jdbc Search Count:" + searchResult.getTotalCount());

			this._paging.setTotalSize(searchResult.getTotalCount());
			setListModelList(new ListModelList(searchResult.getResult()));
		} else {
			this._paging.setTotalSize(staticList.size());
			setListModelList(new ListModelList(staticList));
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
		    super();
		}

		@Override
		public void render(Listitem item, Object data, int count) throws Exception {

			for (int i = 0; i < fieldString.length; i++) {

				String fieldValue = "";
				String fieldMethod = "get" + fieldString[i].substring(0, 1).toUpperCase() + fieldString[i].substring(1);

				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
				} else {
					fieldValue = ((Object) data.getClass().getMethod(fieldMethod).invoke(data)).toString();
				}

				if (i == 0) {
					final Listcell lc = new Listcell();
					final Checkbox cbCode = new Checkbox();
					cbCode.setLabel(fieldValue);
					cbCode.setValue(fieldValue);
					cbCode.setChecked(checkMap.containsKey(fieldValue));
					cbCode.addEventListener("onCheck", new onCheckBoxCheked());
					lc.appendChild(cbCode);
					lc.setParent(item);
				} else {
					final Listcell lc = new Listcell(fieldValue);
					lc.setParent(item);
				}

			}

			item.setAttribute("data", data);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");
		}
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

			final String searchText = MultiSelectionSearchListBox.this._textbox.getValue();
			// refresh the list
			refreshModel(searchText, start);
		}
	}

	public final class onCheckBoxCheked implements EventListener<Event> {
		public onCheckBoxCheked() {
			//
		}

		public void onEvent(Event event) throws Exception {
			Checkbox checkbox = (Checkbox) event.getTarget();

			if (checkbox.isChecked()) {
				checkMap.put(checkbox.getValue().toString(), checkbox.getValue().toString());
			} else {
				checkMap.remove(checkbox.getValue());
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void refreshModel(String searchText, int start) {
		logger.debug("Entering");

		// clear old data
		getListModelList().clear();
		setJdbcSearchObject(start);

		if (StringUtils.isNotBlank(searchText)) {
			if (fieldString.length > 1) {
				Filter[] filters = new Filter[fieldString.length];
				for (int i = 0; i < fieldString.length; i++) {
					filters[i] = getFilter(fieldString[i], searchText);
				}
				this.jdbcSearchObject.addFilterOr(filters);
			} else {
				this.jdbcSearchObject.addFilter(getFilter(fieldString[0], searchText));
			}
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
	final class OnSearchListener implements EventListener<Event> {

		public OnSearchListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			final String searchText = MultiSelectionSearchListBox.this._textbox.getValue();

			// we start new
			refreshModel(searchText, 0);
		}
	}

	/**
	 * Inner OnCloseListener class.<br>
	 */
	final class OnCloseListener implements EventListener<Event> {

		public OnCloseListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {

			String returnValues = null;
			Object[] object = checkMap.values().toArray();
			for (int i = 0; i < object.length; i++) {
				if (returnValues == null) {
					returnValues = object[i].toString();
				} else {
					returnValues = returnValues + "," + object[i].toString();
				}
			}
			if (object.length == 0) {
				setObject("");
			} else {
				setObject(returnValues);
			}

			onClose();
		}
	}

	/**
	 * Method for Clearing Items from Search Box
	 */
	final class OnClearListener implements EventListener<Event> {

		public OnClearListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			setObject(String.valueOf(""));
			onClose();
		}
	}

	// Setter/Getter

	public Object getObject() {
		return this.objClass;
	}

	private void setObject(Object objClass) {
		this.objClass = objClass;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return this.pageSize;
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

	public String[] getFieldString() {
		return fieldString;
	}

	public void setFieldString(String[] fieldString) {
		this.fieldString = fieldString;
	}

	public void setCheckMap() {

		if (StringUtils.isNotBlank(selectedValues)) {
			String[] checkedValues = selectedValues.split(",");
			for (int i = 0; i < checkedValues.length; i++) {
				this.checkMap.put(checkedValues[i], checkedValues[i]);
			}
		}
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

		String[] lovFields = getModuleMapping().getLovFields();
		if (lovFields != null && lovFields.length > 0) {
			this.jdbcSearchObject.addSort(lovFields[0].trim(), false);
			// this.jdbcSearchObject.addSort(lovFields[1].trim(), false);
		}

		if (this.filters != null) {
			for (int i = 0; i < filters.length; i++) {
				this.jdbcSearchObject.addFilter(filters[i]);
			}
		}
		if (this.whereClause != null) {
			this.jdbcSearchObject.addWhereClause(whereClause);
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
			this.moduleClass = moduleMapping.getModuleClass();
			this.setTitle(Labels.getLabel(moduleMapping.getModuleName()));
		}

	}

	private Filter getFilter(String searchField, String searchText) {
		Filter filter = null;
		Object value = null;
		String fieldMethod = "get" + searchField;
		try {
			if (moduleClass.getMethod(fieldMethod).getReturnType().equals(String.class)) {
				filter = new Filter(searchField, "%" + searchText + "%", Filter.OP_LIKE);
			} else {
				if (moduleClass.getMethod(fieldMethod).getReturnType().isPrimitive()) {
					value = getObject(moduleClass.getMethod(fieldMethod).getReturnType().toString(), searchText);
				}
				filter = new Filter(searchField, value, Filter.OP_EQUAL);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return filter;
	}

	private Object getObject(String returnType, String type) {
		switch (returnType.toLowerCase()) {
		case "boolean":
			return Boolean.valueOf(type);
		case "byte":
			return Byte.valueOf(type);
		case "short":
			return NumberUtils.toShort(type);
		case "int":
			return NumberUtils.toInt(type);
		case "long":
			return NumberUtils.toLong(type);
		case "float":
			return NumberUtils.toFloat(type);
		case "double":
			return NumberUtils.toDouble(type);
		}
		return new Object();
	}
}
