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
 * FileName : ExtendedMultipleSearchListBox.java
 * 
 * Author : Chaitanya Varma
 * 
 * Creation Date : 15-12-2011
 * 
 * Modified Date : 15-12-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-12-2011 Pennant 0.1 * 21-12-2011 Chaitanya 0.2 Changed the Listbox checkmark * * property to True.Changed the *
 * Event Listeners to Forward Events * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.util.searchdialogs;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
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
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.Uppercasebox;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.jdbc.DataTypeUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

/**
 * This class creates a modal window as a dialog in which the user <br>
 * can search and select a branch object. By onClosing this box <b>returns</b> an object or null. <br>
 * The object can returned by selecting and clicking the OK button or by DoubleClicking on an item from the list.<br>
 * Further the count of results can limited by manipulating the value of a table field for the sql where clause.<br>
 */
public class ExtendedMultipleSearchListBox extends Window implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ExtendedMultipleSearchListBox.class);
	private Textbox _textbox;

	private Button _searchButton;
	private Paging _paging;

	private int pageSize = PennantConstants.searchGridSize;
	private Listbox listbox;
	@SuppressWarnings("rawtypes")
	private ListModelList listModelList;
	private final int _height = 430;
	private int _width = 300;

	// the returned bean object
	private Object objClass = null;
	@SuppressWarnings("rawtypes")
	private JdbcSearchObject jdbcSearchObject;
	private transient PagedListService pagedListService;
	private String[] fieldString = null;
	private ModuleMapping moduleMapping = null;
	private Filter[] filters;
	Map<String, Object> selectedValuesMap = new HashMap<String, Object>();
	boolean selectAll = false;

	public ExtendedMultipleSearchListBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode, Map<String, Object> selectedValuesMap) {
		return new ExtendedMultipleSearchListBox(parent, listCode, selectedValuesMap, null).getObject();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * 
	 * @return a BeanObject from the listBox or null.
	 */

	public static Object show(Component parent, String listCode, Map<String, Object> selectedValuesMap,
			Filter[] filters) {
		return new ExtendedMultipleSearchListBox(parent, listCode, selectedValuesMap, filters).getObject();
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	private ExtendedMultipleSearchListBox(Component parent, String listCode, Map<String, Object> selectedValuesMap,
			Filter[] filters) {
		super();
		this.selectedValuesMap = selectedValuesMap;
		this.filters = filters;
		setModuleMapping(PennantJavaUtil.getModuleMap(listCode));
		setParent(parent);
		createBox();
	}

	/**
	 * Creates the components, sets the model and show the window as modal.<br>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createBox() {
		logger.debug("Entering Method createBox()");

		if (getModuleMapping().getLovWidth() != 0) {
			this._width = getModuleMapping().getLovWidth();
		}

		// Window
		this.setWidth(String.valueOf(this._width + 5) + "px");
		this.setHeight(String.valueOf(this._height) + "px");

		this.setVisible(true);
		this.setClosable(true);
		this.addForward("onClose", this, "onClick$close");

		final Vlayout vbox = new Vlayout();
		vbox.setParent(this);
		vbox.setStyle("text-align: left;");
		// Paging
		this._paging = new Paging();
		this._paging.setDetailed(true);
		this._paging.setId("paging");
		this._paging.addForward("onPaging", this, "onPaging$paging");
		this._paging.setPageSize(getPageSize());
		this._paging.setParent(vbox);
		// Listbox
		this.listbox = new Listbox();
		listbox.setCheckmark(true);
		listbox.setMultiple(true);
		this.listbox.setHeight("290px");
		this.listbox.setVisible(true);
		this.listbox.setParent(vbox);
		this.listbox.setItemRenderer(new SearchBoxItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);

		for (int i = 0; i < this.fieldString.length; i++) {
			final Listheader listheader = new Listheader();
			listheader.setSclass("BCMListHeader" + i);
			listheader.setParent(listhead);
			listheader.setLabel(Labels.getLabel("label_" + this.fieldString[i]));
		}

		// hbox for holding the Textbox + SearchButton
		final Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setStyle("padding-left: 5px");
		hbox.setWidth("100%");
		hbox.setHeight("27px");
		hbox.setParent(vbox);
		// textbox for inserting the search parameter
		this._textbox = new Uppercasebox();
		this._textbox.setWidth("100%");
		this._textbox.setMaxlength(100);
		this._textbox.addForward("onOK", this, "onSearchListener");
		this._textbox.setParent(hbox);
		this._textbox.setFocus(true);
		// serachButton
		this._searchButton = new Button();
		this._searchButton.setImage("/images/icons/search.gif");
		this._searchButton.addForward("onClick", this, "onSearchListener");
		this._searchButton.setParent(hbox);

		final Div divSouth = new Div();
		divSouth.setWidth("100%");
		divSouth.setHeight("100%");
		divSouth.setParent(vbox);

		final Separator sep = new Separator();
		sep.setBar(true);
		sep.setOrient("horizontal");
		sep.setParent(divSouth);

		// Button
		final Button btnOK = new Button();
		btnOK.setStyle("padding-left: 5px");
		btnOK.setLabel("OK");
		btnOK.setId("ok");
		btnOK.addForward("onClick", this, "onClick$ok");
		btnOK.setParent(divSouth);
		divSouth.appendChild(new Space());

		// Button
		final Button btnClear = new Button();
		btnClear.setStyle("padding-left: 5px");
		btnClear.setLabel("Clear");
		btnClear.setId("clear");
		btnClear.addForward("onClick", this, "onClick$clear");
		btnClear.setParent(divSouth);

		if (StringUtils.equals(getModuleMapping().getModuleName(), "Branch")) {
			Space space = new Space();
			space.setSpacing("10px");
			space.setParent(divSouth);

			final Button btnSelectAll = new Button();
			btnSelectAll.setStyle("padding-left: 1px");
			btnSelectAll.setLabel("select All");
			btnSelectAll.setId("selectAll");
			btnSelectAll.addForward("onClick", this, "onClick$selectAll");
			btnSelectAll.setParent(divSouth);
		}
		/**
		 * init the model.<br>
		 * The ResultObject is a helper class that holds the generic list and the totalRecord count as int value.
		 */

		logger.debug("Before fetch jdbc Search");
		setJdbcSearchObject(0);

		final SearchResult searchResult = getPagedListService().getSRBySearchObject(getJdbcSearchObject());
		logger.debug("After fetch jdbc Search Count:" + searchResult.getTotalCount());

		this._paging.setTotalSize(searchResult.getTotalCount());
		setListModelList(new ListModelList(searchResult.getResult()));
		this.listbox.setModel(getListModelList());

		try {
			doModal();
		} catch (final SuspendNotAllowedException e) {
			logger.fatal("", e);
			this.detach();
		}

		logger.debug("Leaving Method createBox()");
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
			((Listbox) item.getParent()).setMultiple(true);

			for (int i = 0; i < fieldString.length; i++) {

				String fieldValue = "";
				String fieldMethod = "get" + fieldString[i].substring(0, 1).toUpperCase() + fieldString[i].substring(1);

				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
				} else {
					fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
				}

				final Listcell lc = new Listcell(fieldValue);
				if (selectedValuesMap.containsKey(fieldValue) && i == 0) {
					item.setSelected(true);
				}

				lc.setParent(item);
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
	public void onPaging$paging(ForwardEvent event) throws Exception {

		final PagingEvent pe = (PagingEvent) event.getOrigin();
		final int pageNo = pe.getActivePage();
		final int start = pageNo * getPageSize();
		setSelectedItems();
		final String searchText = ExtendedMultipleSearchListBox.this._textbox.getValue();
		refreshModel(searchText, start);

	}

	@SuppressWarnings("rawtypes")
	public void setSelectedItems() {
		List items = listbox.getItems();

		for (int i = 0; i < items.size(); i++) {
			Listitem item = (Listitem) items.get(i);
			String fieldValue = "";
			String fieldMethod = "get" + fieldString[0].substring(0, 1).toUpperCase() + fieldString[0].substring(1);
			Object obj = (Object) item.getAttribute("data");
			try {
				if (obj.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) obj.getClass().getMethod(fieldMethod).invoke(obj);
				} else {
					fieldValue = obj.getClass().getMethod(fieldMethod).invoke(obj).toString();
				}
			} catch (SecurityException e) {
				logger.error("Exception: ", e);
			} catch (IllegalArgumentException e) {
				logger.error("Exception: ", e);
			} catch (NoSuchMethodException e) {
				logger.error("Exception: ", e);
			} catch (IllegalAccessException e) {
				logger.error("Exception: ", e);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}

			if (selectedValuesMap.containsKey(fieldValue)) {
				selectedValuesMap.remove(fieldValue);
			}
			if (item.isSelected()) {
				selectedValuesMap.put(fieldValue, obj);
			}
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
		logger.debug("Entering Method refreshModel");

		// clear old data
		getListModelList().clear();

		setJdbcSearchObject(start);

		if (StringUtils.isNotBlank(searchText)) {

			Filter[] filters = new Filter[fieldString.length];

			for (int i = 0; i < fieldString.length; i++) {
				filters[i] = getSearchFilter(fieldString[i], searchText);
			}
			this.jdbcSearchObject.addFilterOr(filters);
		}

		final SearchResult searchResult = getPagedListService().getSRBySearchObject(getJdbcSearchObject());
		this._paging.setTotalSize(searchResult.getTotalCount());

		// set the model
		setListModelList(new ListModelList(searchResult.getResult()));
		this.listbox.setModel(getListModelList());

		logger.debug("Leaving Method refreshModel");
	}

	public void onClick$clear(Event event) {
		selectedValuesMap.clear();
		setObject(selectedValuesMap);
		onClose();
	}

	public void onClick$ok(Event event) {
		setSelectedItems();
		setObject(selectedValuesMap);
		onClose();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$selectAll(Event event) throws Exception {
		selectAll = true;
		// select already loaded records.
		for (Listitem item : this.listbox.getItems()) {
			item.setSelected(true);
		}
		getJdbcSearchObject().setMaxResults(this._paging.getTotalSize());
		final SearchResult searchResult = getPagedListService().getSRBySearchObject(getJdbcSearchObject());
		List list = searchResult.getResult();
		for (Object data : list) {
			String fieldValue = "";
			String fieldMethod = "get" + fieldString[0].substring(0, 1).toUpperCase() + fieldString[0].substring(1);

			if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
				fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
			} else {
				fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
			}
			selectedValuesMap.put(fieldValue, data);
		}

	}

	public void onClick$close(Event event) {
		setObject(selectedValuesMap);
		onClose();
	}

	/**
	 * Inner OnSearchListener class.<br>
	 */
	public void onSearchListener(Event event) throws Exception {
		final String searchText = ExtendedMultipleSearchListBox.this._textbox.getValue();

		// we start new
		refreshModel(searchText, 0);
	}

	// ************************************************* //
	// **************** Setter/Getter ****************** //
	// ************************************************* //

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
		showOrder(this.jdbcSearchObject, getModuleMapping().getLovFields());

		if (this.filters != null) {
			for (int i = 0; i < filters.length; i++) {
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

	private Filter getSearchFilter(String field, String value) {
		Object object = DataTypeUtil.getValueAsObject(field, value, getModuleMapping().getModuleClass());

		if (object instanceof String) {
			return new Filter(field, "%" + value + "%", Filter.OP_LIKE);
		} else {
			return new Filter(field, object, Filter.OP_EQUAL);
		}
	}

	@SuppressWarnings("rawtypes")
	public void showOrder(JdbcSearchObject jdbcSearchObject, String[] lovFields) {
		if (StringUtils.equals(getModuleMapping().getModuleName(), "FileBatchStatus")) {
			this.jdbcSearchObject.addSort(lovFields[0], true);
		}
	}
}
