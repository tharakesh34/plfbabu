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
 * * FileName : ExtendedCombobox.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-05-2013 * * Modified
 * Date : 23-05-2013 * * Description : Module Search box * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Satish/Chaitanya 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.DataTypeUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

public class ExtendedCombobox extends Hbox {
	private static final Logger logger = LogManager.getLogger(ExtendedCombobox.class);

	private static final long serialVersionUID = -4246285143621221275L;
	public static final String ON_FUL_FILL = "onFulfill";
	private static final String DATA_NOT_AVAILABLE = "%s is not valid.";

	private Space space;
	private Textbox textbox;
	private Button button;
	private Label label;

	private Filter[] filters;
	private transient Object object = null;
	private String selctedValue = null;

	/*** Mandatory Properties **/
	private String moduleName;
	private int displayStyle = 1;
	private String valueColumn;
	private DataType valueType = DataType.STRING;
	private boolean isdisplayError = true;
	private boolean inputAllowed = true;
	private boolean isWindowOpened = false;
	private boolean mandatory = false;

	/*** Optional Properties **/
	private String descColumn;
	private String[] validateColumns;

	private transient SearchProcessor searchProcessor;
	private String whereClause = null;
	private String rateModule = "";
	private transient List<?> list = null;

	private boolean multySelection;
	private transient Map<String, Object> selectedValues = new HashMap<>();
	/** For Duplicate Records To Identify setting Filter Columns. **/
	private String[] filterColumns;

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public ExtendedCombobox(boolean allowSpace) {
		displayStyle = 4;
		buildComponent(allowSpace);
	}

	/**
	 * ExtendedCombobox Constructor Defining the components and events
	 */
	public ExtendedCombobox() {
		buildComponent(true);
	}

	private void buildComponent(boolean allowSpace) {
		// Space
		space = new Space();
		if (allowSpace) {
			space.setWidth("2px");
		} else {
			space.setVisible(false);
		}
		this.appendChild(space);

		// Hbox
		Hbox hbox = new Hbox();
		if (allowSpace) {
			hbox.setSpacing("2px");
		}
		hbox.setSclass("cssHbox");

		// Textbox
		textbox = new Textbox();
		textbox.setStyle("border:0px;margin:0px;");

		// If input allowed set text box editable
		if (inputAllowed) {
			textbox.setReadonly(false);
			textbox.addForward("onChange", this, "onChangeTextbox");
		} else {
			textbox.setReadonly(true);
		}
		hbox.appendChild(textbox);

		// Button
		button = new Button();
		button.setSclass("cssBtnSearch");
		button.setImage("/images/icons/LOVSearch.png");
		button.addForward("onClick", this, "onButtonClick");
		hbox.appendChild(button);
		this.appendChild(hbox);

		label = new Label();
		if (getDisplayStyle() == 4) {
			label.setVisible(false);
		} else {
			label.setVisible(true);
		}
		this.label.setStyle("margin-left:10px;display:inline-block;padding-top:6px;white-space: nowrap;");
		this.setSclass("ellipsis");
		this.appendChild(label);

	}

	/**
	 * Called when changing the value of the text box
	 * 
	 * @param event
	 */
	public void onChangeTextbox(Event event) {
		this.setErrorMessage("");
		this.label.setValue("");
		this.label.setTooltiptext("");
		Clients.clearWrongValue(this.button);
		selctedValue = "";

		setObject();

		if (list == null && inputAllowed && !this.textbox.isReadonly()) {
			validateValue(false);
		} else if (CollectionUtils.isNotEmpty(list)) {
			selectFromDefinedList();
		}

		if (this.object == null) {
			return;
		}

		try {
			doWrite();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (StringUtils.isNotEmpty(this.rateModule)) {
				Events.postEvent(ON_FUL_FILL, this.getParent().getParent(), this.rateModule);
			} else {
				Events.postEvent(ON_FUL_FILL, this, null);
			}
		}
	}

	private void selectFromDefinedList() {
		try {
			boolean found = false;
			this.object = null;
			String value = this.textbox.getValue();
			for (Object item : list) {
				String valueMethod = "get" + getValueColumn();
				String result = invokeMethod(valueMethod, item);
				if (value.equalsIgnoreCase(result)) {
					this.object = item;
					found = true;
					break;
				}
			}
			if (!found) {
				this.textbox.setConstraint("");
				this.textbox.setErrorMessage("");
				this.textbox.setValue("");
				this.label.setValue("");
				if (StringUtils.isNotBlank(value)) {
					throw new WrongValueException(this.button, String.format(DATA_NOT_AVAILABLE, value));
				}
			}

		} catch (WrongValueException e) {
			throw e;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			Events.postEvent(ON_FUL_FILL, this, null);
		}
	}

	/**
	 * Called when clicking on a button
	 * 
	 * @param event
	 */
	public void onButtonClick(Event event) {
		if (this.isWindowOpened) {
			return;
		}

		this.textbox.setErrorMessage("");
		Clients.clearWrongValue(this.button);

		this.isWindowOpened = true;
		try {

			if (moduleName == null || moduleName.length() == 0) {
				return;
			}

			Object tempObject = null;

			if (list != null) {
				tempObject = ExtendedSearchListBox.show(this, moduleName, list);
			} else {
				tempObject = createExtendedSearchListBox();
			}

			if (tempObject != null) {
				this.object = tempObject;
			}

			if (this.object != null) {
				if (this.object instanceof String) {
					doClear();
				} else {
					doWrite();
				}
			}

			Events.sendEvent(Events.ON_CHANGE, textbox, null);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (StringUtils.isNotEmpty(this.rateModule)) {
				Events.postEvent(ON_FUL_FILL, this.getParent().getParent(), this.rateModule);
			} else {
				Events.postEvent(ON_FUL_FILL, this, null);
			}
			this.isWindowOpened = false;
		}
	}

	private Object createExtendedSearchListBox() {
		ExtendedSearchListBox extended = null;
		if (filters != null) {
			if (StringUtils.equals(this.whereClause, "")) {
				extended = new ExtendedSearchListBox(this, moduleName, filters, getSearchValue());
			} else {
				extended = new ExtendedSearchListBox(this, moduleName, filters, getSearchValue(), whereClause,
						valueColumn, valueType);
			}
		} else {
			if (StringUtils.equals(this.whereClause, "")) {
				extended = new ExtendedSearchListBox(this, moduleName, getSearchValue());
			} else {
				extended = new ExtendedSearchListBox(this, moduleName, getSearchValue(), whereClause, valueColumn,
						valueType);
			}
		}

		extended.setMultySelection(isMultySelection());
		extended.setSelectedValues(selectedValues);
		extended.createBox();
		return extended.getObject();

	}

	/**
	 * Used to clear the values and attributes of the components
	 * 
	 * @param event
	 */
	private void doClear() {
		selctedValue = "";
		clearErrorMessage();
		setConstraint("");

		this.textbox.setValue("");
		this.label.setValue("");
		this.label.setTooltiptext("");
		this.textbox.setAttribute("data", null);

		if (multySelection) {
			selectedValues.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	private String invokeMethod(String methodName, Object object) {
		try {
			if (multySelection) {
				StringBuilder value = new StringBuilder();
				for (String key : selectedValues.keySet()) {
					if (value.length() > 0) {
						value.append(",");
					}

					value.append(key);
				}

				return value.toString();

			} else if (object != null) {
				return object.getClass().getMethod(methodName).invoke(object).toString();
			}
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		}

		return null;
	}

	/**
	 * Internal method to write the calues to the components
	 * 
	 * @throws Exception
	 */
	private void doWrite() {
		String method = "get" + getValueColumn();
		selctedValue = invokeMethod(method, this.object);
		this.textbox.setValue(selctedValue);
		this.textbox.setAttribute("data", object);
		this.setAttribute("data", object);

		if (getDescColumn() == null || getDescColumn().length() == 0) {
			return;
		}

		String descMethod = "get" + getDescColumn();
		String desc = invokeMethod(descMethod, this.object);
		if (getDisplayStyle() == 2) {
			this.textbox.setValue(selctedValue + "-" + desc);
		} else if (getDisplayStyle() == 3) {
			this.textbox.setValue(desc);
			this.label.setValue(selctedValue);
			this.label.setStyle("valign:center;");
			this.label.setVisible(false);
		} else {
			this.label.setTooltiptext(desc);
			this.label.setValue(desc);
		}
	}

	/**
	 * Set JdbcSearchobjext Set the properties to the search object from module mapping and the filters
	 */
	private Search getSearch() {
		Search search = new Search(ModuleUtil.getModuleClass(moduleName));
		search.addTabelName(ModuleUtil.getLovTableName(moduleName));

		String[] lovFields = ModuleUtil.getLovFields(moduleName);
		if (lovFields != null && lovFields.length > 0) {
			search.addSort(lovFields[0].trim(), false);
			search.addSort(lovFields[1].trim(), false);
		}

		if (this.filters != null) {
			for (int i = 0; i < filters.length; i++) {
				if (filters[i] == null) {
					continue;
				}
				search.addFilter(filters[i]);
			}
		}

		addSearchFields(search);

		if (whereClause != null) {
			search.addWhereClause(whereClause);
		}

		Object[][] lovFilters = ModuleUtil.getLovFilters(moduleName);
		if (lovFilters != null) {
			Filter filter1;

			for (int i = 0; i < lovFilters.length; i++) {
				filter1 = new Filter((String) lovFilters[i][0], lovFilters[i][2],
						Integer.parseInt((String) lovFilters[i][1]));
				search.addFilter(filter1);

			}
		}
		return search;
	}

	private void addSearchFields(Search search) {
		if (getValidateColumns() == null) {
			return;
		}

		String[] searchFieldArray = getValidateColumns();
		Filter[] filter1 = new Filter[searchFieldArray.length];
		int i = 0;
		Object fieldValue;

		for (String field : searchFieldArray) {
			if (field.equals(getValueColumn())) {
				fieldValue = DataTypeUtil.getValueAsObject(textbox.getValue(), valueType);
			} else {
				fieldValue = textbox.getValue();
			}

			filter1[i++] = new Filter(field, fieldValue, Filter.OP_EQUAL);
		}

		search.addFilterOr(filter1);
	}

	/**
	 * Validate the value of the text entered in the textbox
	 * 
	 * @param showError
	 */
	public void validateValue(boolean showError) {
		if (this.object != null) {
			return;
		}

		try {
			if (isIsdisplayError() || showError) {
				this.textbox.setFocus(true);
				if (StringUtils.isNotEmpty(this.rateModule)) {
					Events.postEvent(ON_FUL_FILL, this.getParent().getParent(), this.rateModule);
				} else {
					Events.postEvent(ON_FUL_FILL, this, null);
				}

				String value = StringUtils.trimToEmpty(this.textbox.getValue());
				this.textbox.setConstraint("");
				this.textbox.setErrorMessage("");
				this.textbox.setValue("");
				if (StringUtils.isNotBlank(value)) {
					throw new WrongValueException(this.button, String.format(DATA_NOT_AVAILABLE, value));
				}
			} else {
				Events.postEvent("onClick", this.button, Events.ON_CLICK);
			}

		} catch (WrongValueException e) {
			throw e;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			String value = StringUtils.trimToEmpty(this.textbox.getValue());
			this.textbox.setValue("");
			throw new WrongValueException(this.button, String.format(DATA_NOT_AVAILABLE, value));
		}

	}

	private void setObject() {
		if (getValidateColumns() == null || getValidateColumns().length == 0 || isMultySelection()) {
			return;
		}

		Object object2 = this.object;
		this.object = null;
		final SearchResult<?> searchResult = getSearchProcessor().getResults(getSearch(), false);
		if (CollectionUtils.isNotEmpty(searchResult.getResult())) {
			if (searchResult.getResult().size() > 1 && this.filterColumns != null) {
				setObjectByFilters(object2, searchResult);
			} else {
				this.object = searchResult.getResult().get(0);
			}
		}
	}

	private void setObjectByFilters(Object object, SearchResult<?> searchResult) {
		Boolean exist = false;

		try {
			for (Object result : searchResult.getResult()) {
				if (exist) {
					break;
				}

				for (int i = 0; i < this.filterColumns.length; i++) {
					String fieldValue1 = null;
					String fieldValue2 = null;

					String filterColumn = filterColumns[i];
					String fieldMethod = "get" + filterColumn.substring(0, 1).toUpperCase() + filterColumn.substring(1);

					Class<?> returnType = result.getClass().getMethod(fieldMethod).getReturnType();
					Class<?> objReturnType = object.getClass().getMethod(fieldMethod).getReturnType();

					if (returnType.equals(String.class)) {
						fieldValue1 = (String) result.getClass().getMethod(fieldMethod).invoke(result);
					} else {
						fieldValue1 = result.getClass().getMethod(fieldMethod).invoke(result).toString();
					}

					if (objReturnType.equals(String.class)) {
						fieldValue2 = (String) object.getClass().getMethod(fieldMethod).invoke(object);
					} else {
						fieldValue2 = object.getClass().getMethod(fieldMethod).invoke(object).toString();
					}

					if (StringUtils.isNotEmpty(fieldValue1) && StringUtils.isNotEmpty(fieldValue1)
							&& fieldValue1.equals(fieldValue2)) {
						exist = true;
					} else {
						exist = false;
					}
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		if (exist) {
			this.object = object;
		} else {
			this.object = searchResult.getResult().get(0);
		}
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setFilters(Filter[] filters) {
		if (filters == null) {
			this.filters = null;
		} else {
			this.filters = filters.clone();
		}
	}

	public Filter[] getFilters() {
		if (filters == null) {
			return new Filter[] {};
		}

		return filters.clone();
	}

	public void setValue(String value) {
		this.selctedValue = value;
		if (getDisplayStyle() == 3) {
			this.label.setValue(value);
		} else {
			this.textbox.setValue(value);
			if (StringUtils.isBlank(value)) {
				this.label.setValue(value);
			}
		}
	}

	public String getValue() {
		this.textbox.getValue();// to call the constraint if any
		if (inputAllowed) {
			if (StringUtils.isNotBlank(selctedValue)) {
				return selctedValue;
			} else {
				return "";
			}
		}

		if (StringUtils.isNotBlank(this.textbox.getValue())) {
			if (getDisplayStyle() == 2) {
				return this.textbox.getValue().substring(0, this.textbox.getValue().indexOf('-'));
			} else if (getDisplayStyle() == 3) {
				return this.label.getValue();
			} else {
				return this.textbox.getValue();
			}
		} else {
			if (getDisplayStyle() == 3) {
				return "0";
			} else {
				return "";
			}
		}
	}

	public Object getActualValue() {
		return DataTypeUtil.getValueAsObject(getValue(), valueType);
	}

	/**
	 * Get the value from the text box after validating it
	 * 
	 * @return String
	 */
	public String getValidatedValue() {

		setObject();

		this.textbox.getValue();// to call the constraint if any
		if (StringUtils.isNotBlank(this.textbox.getValue())) {
			if (inputAllowed && !this.textbox.isReadonly()) {
				validateValue(true);
			}
			if (getDisplayStyle() == 2) {
				return this.textbox.getValue().substring(0, this.textbox.getValue().indexOf('-'));
			} else if (getDisplayStyle() == 3) {
				return this.label.getValue();
			} else {
				return this.textbox.getValue();
			}
		} else {
			return "";
		}
	}

	/**
	 * Get the description from the text box
	 * 
	 * @return String(Description)
	 */
	public String setDescription() {
		if (getDisplayStyle() == 2) {
			if (StringUtils.isNotBlank(this.textbox.getValue())) {
				return this.textbox.getValue().substring(this.textbox.getValue().indexOf('-'));
			} else {
				return "";
			}
		} else if (getDisplayStyle() == 3) {
			if (StringUtils.isNotBlank(this.textbox.getValue())) {
				return this.textbox.getValue();
			} else {
				return "0";
			}
		} else {
			return this.label.getValue();
		}
	}

	/**
	 * set Description to the label
	 * 
	 * @param desc
	 */
	public void setDescription(String desc) {
		if (getDisplayStyle() == 2) {
			this.textbox.setValue(this.selctedValue + "-" + desc);
		} else if (getDisplayStyle() == 3) {
			this.textbox.setValue(desc);
			this.label.setVisible(false);
		} else {
			this.label.setValue(desc);
			this.label.setTooltiptext(desc);
		}
	}

	/**
	 * Set Value and description to the textbox and label
	 * 
	 * @param value
	 * @param desc
	 */
	public void setValue(String value, String desc) {
		this.selctedValue = value;
		if (getDisplayStyle() == 2) {
			this.textbox.setValue(value + "-" + desc);
		} else {
			this.textbox.setValue(value);
			this.label.setValue(desc);
			this.label.setTooltiptext(desc);
		}
	}

	/**
	 * Get the description from the text box
	 * 
	 * @return String(Description)
	 */
	public String getDescription() {
		if (getDisplayStyle() == 2) {
			if (StringUtils.isNotBlank(this.textbox.getValue())) {
				return this.textbox.getValue().substring(this.textbox.getValue().indexOf('-'));
			} else {
				return "";
			}
		} else if (getDisplayStyle() == 3) {
			if (StringUtils.isNotBlank(this.textbox.getValue())) {
				return this.textbox.getValue();
			} else {
				return "";
			}
		} else {
			return this.label.getValue();
		}
	}

	public Object getObject() {
		this.textbox.getValue();
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void setTextBoxWidth(int width) {
		if (this.textbox != null) {
			this.textbox.setWidth(width + "px");
		}
	}

	public void setConstraint(String constraint) {
		this.textbox.setConstraint(constraint);
	}

	public void setConstraint(Constraint constraint) {
		this.textbox.setConstraint(constraint);
	}

	public void setErrorMessage(String errmsg) {
		this.textbox.setErrorMessage(errmsg);
		if (StringUtils.isBlank(errmsg)) {
			Clients.clearWrongValue(this.button);
		}
	}

	public void clearErrorMessage() {
		this.textbox.clearErrorMessage();
	}

	public void setMandatoryStyle(boolean mandatory) {
		if (mandatory) {
			this.space.setSclass(PennantConstants.mandateSclass);
			this.mandatory = true;
		} else {
			this.space.setSclass("");
			this.mandatory = false;
		}
	}

	public void setButtonDisabled(boolean isDisable) {
		this.button.setDisabled(isDisable);
		if (inputAllowed) {
			this.textbox.setReadonly(isDisable);
		} else {
			this.textbox.setReadonly(true);
		}
		if (isDisable) {
			this.space.setSclass("");
		}
	}

	public boolean isMandatory() {
		return this.mandatory;
	}

	public void setReadonly(boolean isReadOnly) {
		this.button.setVisible(!isReadOnly);
		if (inputAllowed) {
			this.textbox.setReadonly(isReadOnly);
		} else {
			this.textbox.setReadonly(true);
			this.textbox.setTabindex(-1);
		}
		if (isReadOnly) {
			this.textbox.setTabindex(-1);
		}
	}

	public boolean isReadonly() {
		return this.textbox.isReadonly();
	}

	public boolean isButtonVisible() {
		return this.button.isVisible();
	}

	public boolean isButtonDisabled() {
		return this.button.isDisabled();
	}

	public void setMaxlength(int length) {
		if (inputAllowed) {
			this.textbox.setMaxlength(length);
			if (getDisplayStyle() == 1) {
				this.textbox.setWidth(length * 15 + "px");
			} else if (getDisplayStyle() != 4) {
				this.textbox.setWidth("180px");
			}
		} else {
			this.textbox.setMaxlength(-1);
			this.textbox.setWidth("180px");
		}
	}

	public Textbox getTextbox() {
		return textbox;
	}

	public void setTextbox(Textbox textbox) {
		this.textbox = textbox;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public Space getSpace() {
		return this.space;
	}

	public int getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(int style) {
		if (style == 3) {
			setInputAllowed(false);
		}
		if (inputAllowed) {
			this.displayStyle = 1;
		} else {
			this.displayStyle = style;
		}
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}

	public DataType getValueType() {
		return valueType;
	}

	public void setValueType(DataType valueType) {
		this.valueType = valueType;
	}

	public String getDescColumn() {
		return descColumn;
	}

	public void setDescColumn(String descColumn) {
		this.descColumn = descColumn;
	}

	public String[] getValidateColumns() {
		if (validateColumns == null) {
			return new String[] {};
		}

		return validateColumns.clone();
	}

	public void setValidateColumns(String[] validateColumns) {
		if (validateColumns == null) {
			this.validateColumns = null;
		} else {
			this.validateColumns = validateColumns.clone();
		}
	}

	public boolean isIsdisplayError() {
		return isdisplayError;
	}

	public void setIsdisplayError(boolean isdisplayError) {
		this.isdisplayError = isdisplayError;
	}

	public boolean isInputAllowed() {
		return inputAllowed;
	}

	public void setInputAllowed(boolean inputAlwd) {
		this.inputAllowed = inputAlwd;
		if (inputAllowed) {
			this.textbox.setReadonly(false);
		} else {
			this.textbox.setReadonly(true);
			this.textbox.setMaxlength(-1);
		}
	}

	public SearchProcessor getSearchProcessor() {
		if (this.searchProcessor == null) {
			this.searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		}
		return searchProcessor;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public void setProperties(String moduleName, String valueColumn, String descColumn, String rateModule,
			int maxlength) {
		setModuleName(moduleName);
		setValueColumn(valueColumn);
		setDescColumn(descColumn);
		setMaxlength(maxlength);
		this.rateModule = rateModule;
		if (this.validateColumns == null) {
			this.validateColumns = new String[] { valueColumn }.clone();
		}
	}

	public void setProperties(String moduleName, String valueColumn, String descColumn, boolean mandatory,
			int maxlength) {
		setModuleName(moduleName);
		setValueColumn(valueColumn);
		setDescColumn(descColumn);
		setMandatoryStyle(mandatory);
		setMaxlength(maxlength);
		if (this.validateColumns == null) {
			this.validateColumns = new String[] { valueColumn }.clone();
		}
	}

	public void setProperties(String moduleName, String valueColumn, String descColumn, boolean mandatory,
			int displayStyle, int maxlength, int txtbxWidth) {
		setModuleName(moduleName);
		setValueColumn(valueColumn);
		setDescColumn(descColumn);
		setMandatoryStyle(mandatory);
		setDisplayStyle(displayStyle);
		setMaxlength(maxlength);
		setTextBoxWidth(txtbxWidth);
		if (this.validateColumns == null) {
			this.validateColumns = new String[] { valueColumn }.clone();
		}
	}

	public void setProperties(String moduleName, String valueColumn, String descColumn, boolean mandatory,
			int displayStyle, int txtbxWidth) {
		setModuleName(moduleName);
		setValueColumn(valueColumn);
		setDescColumn(descColumn);
		setDisplayStyle(displayStyle);
		setMandatoryStyle(mandatory);
		setTextBoxWidth(txtbxWidth);
		if (this.validateColumns == null) {
			this.validateColumns = new String[] { valueColumn }.clone();
		}
	}

	private String getSearchValue() {
		if (isMultySelection()) {
			return null;
		}

		return this.textbox.getValue();
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

	public String[] getFilterColumns() {
		return filterColumns;
	}

	public void setFilterColumns(String[] filterColumns) {
		this.filterColumns = filterColumns;
	}

	public void setRemoveSpace(boolean removeSpace) {
		space.setVisible(!removeSpace);
	}

}
