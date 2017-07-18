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
 *																							*
 * FileName    		:  ExtendedCombobox.java		                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-05-2013    														*
 *                                                                  						*
 * Modified Date    :  23-05-2013    														*
 *                                                                  						*
 * Description 		:  Module Search box                                            		*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Satish/Chaitanya	      0.1       		                            * 
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
package com.pennant;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
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

import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.util.ModuleUtil;

public class ExtendedCombobox extends Hbox {
	private static final long			serialVersionUID	= -4246285143621221275L;
	private static final Logger			logger				= Logger.getLogger(ExtendedCombobox.class);

	private Space						space;
	private Textbox						textbox;
	private Button						button;
	private Label						label;
	private Hbox						hbox;

	private Filter[]					filters;
	private Object						object				= null;
	private String						selctedValue		= null;

	/*** Mandatory Properties **/
	private String						moduleName;														//mandatory
	private int							displayStyle		= 1;										//mandatory 	
	private String						valueColumn;													//mandatory
	private boolean						isdisplayError		= true;										//mandatory
	private boolean						inputAllowed		= true;										//mandatory
	private boolean						isWindowOpened		= false;									//mandatory
	private boolean						mandatory			= false;									//mandatory

	/*** Optional Properties **/
	private String						descColumn;														//Optional
	private String[]					validateColumns;												//Optional

	@SuppressWarnings("rawtypes")
	private JdbcSearchObject			jdbcSearchObject;
	private transient PagedListService	pagedListService;
	private String						whereClause			= null;
	private String						rateModule			= "";
	private List<?>						list				= null;

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
		//Space
		space = new Space();
		if (allowSpace) {
			space.setWidth("2px");
		} else {
			space.setVisible(false);
		}
		this.appendChild(space);

		//Hbox
		hbox = new Hbox();
		if (allowSpace) {
			hbox.setSpacing("2px");
		}
		hbox.setSclass("cssHbox");

		//Textbox
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

		//Button
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
		this.label.setStyle("margin-left:10px;display:inline-block;padding-top:6px;");
		this.appendChild(label);

	}

	/**
	 * Called when changing the value of the text box
	 * 
	 * @param event
	 */
	public void onChangeTextbox(Event event) {
		logger.debug("Entering");
		this.setErrorMessage("");
		this.label.setValue("");
		this.label.setTooltiptext("");
		Clients.clearWrongValue(this.button);
		selctedValue = "";
		if (list == null) {
			validateValue(false);
		} else {
			selectFromDefinedList();
		}
		if (this.object != null) {
			try {
				doWrite();
			} catch (Exception e) {
				logger.error("Exception: ", e);
			} finally {
				if (StringUtils.isNotEmpty(this.rateModule)) {
					Events.postEvent("onFulfill", this.getParent().getParent(), this.rateModule);
				} else {
					Events.postEvent("onFulfill", this, null);
				}
			}
		}
		logger.debug("Leaving");
	}

	private void selectFromDefinedList() {
		try {
			boolean found = false;
			this.object = null;
			String value = this.textbox.getValue();
			for (Object object : list) {
				String valueMethod = "get" + getValueColumn();
				String selctedValue = object.getClass().getMethod(valueMethod).invoke(object).toString();
				if (value.equalsIgnoreCase(selctedValue)) {
					this.object = object;
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
					throw new WrongValueException(this.button, value + " is not valid.");
				}
			}

		} catch (WrongValueException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			Events.postEvent("onFulfill", this, null);
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
		logger.debug("Entering");
		this.isWindowOpened = true;

		this.textbox.setErrorMessage("");
		Clients.clearWrongValue(this.button);
		try {
			if (moduleName != null && moduleName.length() != 0) {
				Object object = null;
				if (list == null) {
					if (filters != null) {
						if (StringUtils.equals(this.whereClause, "")) {
							object = ExtendedSearchListBox.show(this, moduleName, filters, this.textbox.getValue());
						} else {
							object = ExtendedSearchListBox.show(this, moduleName, this.textbox.getValue(), filters,
									whereClause);
						}
					} else {
						if (StringUtils.equals(this.whereClause, "")) {
							object = ExtendedSearchListBox.show(this, moduleName, this.textbox.getValue());
						} else {
							object = ExtendedSearchListBox.show(this, moduleName, this.textbox.getValue(), whereClause);
						}
					}
				} else {
					object = ExtendedSearchListBox.show(this, moduleName, list);
				}

				if (object != null) {
					this.object = object;
				}

				if (this.object != null) {
					if (this.object instanceof String) {
						doClear();
					} else {
						doWrite();
					}
				}
			}

			logger.debug("Leaving");
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			if (StringUtils.isNotEmpty(this.rateModule)) {
				Events.postEvent("onFulfill", this.getParent().getParent(), this.rateModule);
			} else {
				Events.postEvent("onFulfill", this, null);
			}
			this.isWindowOpened = false;
		}
	}

	/**
	 * Used to clear the values and attributes of the components
	 * 
	 * @param event
	 */
	private void doClear() {
		logger.debug("Entering");

		selctedValue = "";
		clearErrorMessage();
		setConstraint("");

		this.textbox.setValue("");
		this.label.setValue("");
		this.label.setTooltiptext("");
		this.textbox.setAttribute("data", null);

		logger.debug("Leaving");
	}

	/**
	 * Internal method to write the calues to the components
	 * 
	 * @throws Exception
	 */
	private void doWrite() throws Exception {
		logger.debug("Entering");
		String valueMethod = "get" + getValueColumn();
		selctedValue = this.object.getClass().getMethod(valueMethod).invoke(object).toString();
		this.textbox.setValue(selctedValue);
		this.textbox.setAttribute("data", object);

		if (getDescColumn() != null && getDescColumn().length() != 0) {
			String descMethod = "get" + getDescColumn();
			String desc = this.object.getClass().getMethod(descMethod).invoke(object).toString();
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

		logger.debug("Leaving");
	}

	/**
	 * Set JdbcSearchobjext Set the properties to the search object from module mapping and the filters
	 */
	private void setJdbcSearchObject() {
		logger.debug("Entering");
		this.jdbcSearchObject = new JdbcSearchObject<>(ModuleUtil.getModuleClass(moduleName));
		this.jdbcSearchObject.addTabelName(ModuleUtil.getLovTableName(moduleName));

		String[] lovFields = ModuleUtil.getLovFields(moduleName);
		if (lovFields != null && lovFields.length > 0) {
			this.jdbcSearchObject.addSort(lovFields[0].trim(), false);
			this.jdbcSearchObject.addSort(lovFields[1].trim(), false);
		}

		if (this.filters != null) {
			for (int i = 0; i < filters.length; i++) {
				this.jdbcSearchObject.addFilter(filters[i]);
			}
		}
		if (getValidateColumns() != null) {
			String[] searchFieldArray = getValidateColumns();
			Filter[] filter1 = new Filter[searchFieldArray.length];

			for (int i = 0; i < searchFieldArray.length; i++) {
				filter1[i] = new Filter(searchFieldArray[i], this.textbox.getValue(), Filter.OP_EQUAL);

			}
			this.jdbcSearchObject.addFilterOr(filter1);
		}

		if (whereClause != null) {
			this.jdbcSearchObject.addWhereClause(whereClause);
		}

		String[][] lovFilters = ModuleUtil.getLovFilters(moduleName);
		if (lovFilters != null) {
			Filter filter1;

			for (int i = 0; i < lovFilters.length; i++) {
				filter1 = new Filter(lovFilters[i][0], lovFilters[i][2], Integer.parseInt(lovFilters[i][1]));
				this.jdbcSearchObject.addFilter(filter1);

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Validate the value of the text entered in the textbox
	 * 
	 * @param showError
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void validateValue(boolean showError) {
		if (getValidateColumns() != null && getValidateColumns().length != 0) {
			this.object = null;
			setJdbcSearchObject();
			try {
				final SearchResult searchResult = getPagedListService().getSRBySearchObject(this.jdbcSearchObject);
				if (searchResult.getResult().size() <= 0) {
					if (isIsdisplayError() || showError) {
						this.textbox.setFocus(true);
						if (StringUtils.isNotEmpty(this.rateModule)) {
							Events.postEvent("onFulfill", this.getParent().getParent(), this.rateModule);
						} else {
							Events.postEvent("onFulfill", this, null);
						}

						String value = StringUtils.trimToEmpty(this.textbox.getValue());
						this.textbox.setConstraint("");
						this.textbox.setErrorMessage("");
						this.textbox.setValue("");
						if (StringUtils.isNotBlank(value)) {
							throw new WrongValueException(this.button, value + " is not valid.");
						}

					} else {
						Events.postEvent("onClick", this.button, Events.ON_CLICK);
					}
				} else {
					this.object = searchResult.getResult().get(0);
				}
			} catch (WrongValueException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Exception: ", e);
				String value = StringUtils.trimToEmpty(this.textbox.getValue());
				this.textbox.setValue("");
				throw new WrongValueException(this.button, value + " is not valid.");
			}
		}
	}

	/*
	 *//**
		 * update the object
		 * 
		 * @param showError
		 *//*
			 * @SuppressWarnings({ "unchecked", "rawtypes" }) public void setObjectData() { if (getValidateColumns() !=
			 * null && getValidateColumns().length != 0) { this.object = null; setJdbcSearchObject(); final SearchResult
			 * searchResult = getPagedListService().getSRBySearchObject(this.jdbcSearchObject); if
			 * (searchResult.getResult().size() > 0) { this.object = searchResult.getResult().get(0); } } }
			 */

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
			return null;
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
		this.textbox.getValue();//to call the constraint if any
		if (inputAllowed) {
			if (StringUtils.isNotBlank(selctedValue)) {
				return selctedValue;
			} else {
				return "";
			}
		} else {
			if (StringUtils.isNotBlank(this.textbox.getValue())) {
				if (getDisplayStyle() == 2) {
					return this.textbox.getValue().substring(0, this.textbox.getValue().indexOf("-"));
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
	}

	/**
	 * Get the value from the text box after validating it
	 * 
	 * @return String
	 */
	public String getValidatedValue() {
		this.textbox.getValue();//to call the constraint if any
		if (StringUtils.isNotBlank(this.textbox.getValue())) {
			if (inputAllowed && !this.textbox.isReadonly()) {
				validateValue(true);
			}
			if (getDisplayStyle() == 2) {
				return this.textbox.getValue().substring(0, this.textbox.getValue().indexOf("-"));
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
				return this.textbox.getValue().substring(this.textbox.getValue().indexOf("-"));
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
				return this.textbox.getValue().substring(this.textbox.getValue().indexOf("-"));
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

	/**
	 * Get the Db Object based on the module mapping and the code
	 * 
	 * @return
	 *//*
		 * @SuppressWarnings({ "unchecked", "rawtypes" }) public Object getDBObject() {
		 * setModuleMapping(PennantJavaUtil.getModuleMap(moduleName)); JdbcSearchObject jdbcSearchObj = new
		 * JdbcSearchObject(getModuleMapping().getModuleClass());
		 * jdbcSearchObj.addTabelName(getModuleMapping().getLovTableName()); Filter filter = new Filter(valueColumn,
		 * getValue(), Filter.OP_EQUAL); jdbcSearchObj.addFilter(filter); List<Object> objects =
		 * getPagedListService().getBySearchObject(jdbcSearchObj); if (objects != null && objects.size() > 0) { return
		 * objects.get(0); } return null;
		 * 
		 * }
		 */

	public Object getObject() {
		this.textbox.getValue();//to call the constraint if any
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
			//this.space.setSclass("");
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

	public boolean setVisible(boolean decider) {
		return super.setVisible(decider);
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

	public String getDescColumn() {
		return descColumn;
	}

	public void setDescColumn(String descColumn) {
		this.descColumn = descColumn;
	}

	public String[] getValidateColumns() {
		if (validateColumns == null) {
			return null;
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

	public PagedListService getPagedListService() {
		if (this.pagedListService == null) {
			this.pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		}
		return pagedListService;
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

}
