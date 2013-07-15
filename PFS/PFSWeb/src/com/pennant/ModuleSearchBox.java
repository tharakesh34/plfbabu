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
 * FileName    		:  ModuleSearchBox.java		                                            * 	  
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

public class ModuleSearchBox extends Hbox {
	private static final long					serialVersionUID	= -4246285143621221275L;
	private final static Logger					logger	         	= Logger.getLogger(ModuleSearchBox.class);
	private Space	            				space;
	private Textbox	            				textbox;
	private Button	            				button;
	private Label	            				label;
	private Filter[]	        				filters;
	// the returned bean object
	private Object 								object 				= 		null;
	private JdbcSearchObject 					jdbcSearchObject;
	private transient PagedListService 			pagedListService;
	private ModuleMapping 						moduleMapping		=		null;
  

	private String	            				moduleName;	         //mandatory
	//DisplayStyle == 1 ==Show in Separate Label 
	//DisplayStyle == 2 == Append Value with - 
	private int 								displayStyle = 1;        //mandatory 	
	private String	   							valueColumn;	     //mandatory
	private String	    						descColumn;	         //Optional
	private String[]							validateColumns;	 //Optional
	private boolean								isdisplayError = true;	
	private boolean								inputAllowed = true;
  	
	/**
	 * Module Search box 
	 * 
	 */	
	public ModuleSearchBox() {
		logger.debug("Entering ExtenedBox()");
		space = new Space();
		space.setWidth("2px");
		this.appendChild(space);
		
		textbox = new Textbox();
 		textbox.setId("tb_value");
 		if(inputAllowed){
 			textbox.setReadonly(false);
 			textbox.addForward("onChange", this, "onChangeTextbox");
 		}else{
 			textbox.setReadonly(true);
 		}
		this.appendChild(textbox);
		
		button = new Button();
		button.setImage("/images/icons/LOVSearch.png");
		button.setLabel("Search");
		button.addForward("onClick", this, "onButtonClick");
		this.appendChild(button);
		
		label = new Label();
		this.appendChild(label);
	
		logger.debug("Leaving ExtenedBox()");
	}
	
	/**
	 * Called when changing the value of the text box
	 * @param event
	 */
	public void onChangeTextbox(Event event){
		logger.debug("Entering on changing Event");

		this.textbox.setErrorMessage("");
		this.setErrorMessage("");
		this.label.setValue("");
		validateValue(false);
		if(this.object != null){
		try {
			doWrite();
			} catch (Exception e) {
				logger.debug(e.toString());
				e.printStackTrace();
			}
		}	
		logger.debug("Leaving on changing Event");
	}
	
	/**
	 * 
	 * @return
	 */
	public PagedListService getPagedListService() {
		if (this.pagedListService==null){
			this.pagedListService= (PagedListService) SpringUtil.getBean("pagedListService");	
		}
		return pagedListService;
	}
	
	/**
	 * 
	 * @param event
	 */
	public void onButtonClick(Event event) {
		logger.debug("Entering onButtonClick()");
		setErrorMessage("");
		try {
			if (moduleName != null && moduleName.length() != 0 && getValidateColumns() != null && getValidateColumns().length != 0) {
 				if (filters != null) {
 					this.object = ExtendedSearchListBox.show(this, moduleName, filters);
				} else {
					this.object = ExtendedSearchListBox.show(this, moduleName, textbox.getValue());
				}
				if (this.object != null) {
					if (this.object instanceof String) {
						doClear();
					} else {
						doWrite();
					}
				}
			}
			logger.debug("Leaving onButtonClick()");
		} catch (Exception e) {
			logger.debug(e);
		} finally {
			Events.postEvent("onFulfill", this, null);
		}
	}
	
	/**
	 * 
	 */
	private void doClear() {
		this.textbox.setValue("");
		this.label.setValue("");
		this.textbox.setAttribute("data", null);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void doWrite() throws Exception {
		String valueMethod = "get" + getValueColumn();
		String value = this.object.getClass().getMethod(valueMethod, null).invoke(object, null).toString();
		this.textbox.setValue(value);
		this.textbox.setAttribute("data", object);
		if (getDescColumn()!= null && getDescColumn().length() != 0) {
			String descMethod = "get" + getDescColumn();
			String desc = this.object.getClass().getMethod(descMethod, null).invoke(object, null).toString();
			if (getDisplayStyle() == 2) {
				this.textbox.setValue(value + "-" + desc);
			} else {
				this.label.setValue(desc);
			}
		}
	}
	
	/**
	 * 
	 */
	public void setJdbcSearchObject() {
		setModuleMapping(PennantJavaUtil.getModuleMap(moduleName));
		this.jdbcSearchObject = new JdbcSearchObject (getModuleMapping().getModuleClass());
  		this.jdbcSearchObject.addTabelName(getModuleMapping().getLovDBObjectName());
		
		if (this.filters!=null){
			for (int i = 0; i < filters.length; i++) {
				this.jdbcSearchObject.addFilter(filters[i]);
			}	
		}
		if (getValidateColumns()!=null){
			String[] searchFieldArray = getValidateColumns();
			Filter[] filter1 = new Filter[searchFieldArray.length];
			
			for (int i = 0; i < searchFieldArray.length; i++) {
				filter1[i] = new Filter(searchFieldArray[i], this.textbox.getValue(), Filter.OP_EQUAL);
 				
			}
			this.jdbcSearchObject.addFilterOr(filter1);
		}

		if (getModuleMapping().getLovCondition()!=null){
			String[][] condArray = getModuleMapping().getLovCondition();
			Filter filter1;
			
			for (int i = 0; i < condArray.length; i++) {
				filter1 = new Filter(condArray[i][0], condArray[i][2], Integer.parseInt(condArray[i][1]));
				this.jdbcSearchObject.addFilter(filter1);
				
			}
		}
	}
	
	/**
	 * 
	 * @param value
	 * @param desc
	 */
	public void setValue(String value, String desc) {
		if (getDisplayStyle() == 2) {
			this.textbox.setValue(value + "-" + desc);
		} else {
			this.textbox.setValue(value);
			this.label.setValue(desc);

		}
	}
	
	/**
	 * 
	 * @param showError
	 */
	public void validateValue(boolean showError){
		this.object = null;
 		setJdbcSearchObject();
		final SearchResult searchResult = getPagedListService().getSRBySearchObject(this.jdbcSearchObject);
		if(searchResult.getResult().size() <= 0){
			if(isIsdisplayError() || showError){
				throw new WrongValueException(this, getValueColumn() + " not Found in the database");
			}else{
				Events.postEvent("onClick", this.button, Events.ON_CLICK);
			}
		}else{
			this.object = searchResult.getResult().get(0);
		}
	}
 
	/**
	 * Get the value from the text box
	 * @return String
	 */
	public String getValue() {
		validateValue(true);
		if (!StringUtils.trimToEmpty(this.textbox.getValue()).equals("")) {
			if (getDisplayStyle() == 2) {
				return this.textbox.getValue().substring(0, this.textbox.getValue().indexOf("-"));
			} else {
				return this.textbox.getValue();
			}
		} else {
			return "";
		}

	}
	
	/**
	 * Get the description from the text box
	 * @return String
	 */
	public String getDescription() {
		if (getDisplayStyle() == 2) {
			if (!StringUtils.trimToEmpty(this.textbox.getValue()).equals("")) {
				return this.textbox.getValue().substring(this.textbox.getValue().indexOf("-"));
			} else {
				return "";
			}
		} else {
			return this.label.getValue();

		}
	}
	
	
	public void setTextBoxWidth(int width) {
		if (this.textbox != null) {
			this.textbox.setWidth(width + "px");
		}
	}

	public void setConstraint(String constraint) {
		this.textbox.setConstraint(constraint);
	}

	
	public Filter[] getFilters() {
		return filters;
	}
	
	public void setFilters(Filter[] filters) {
		this.filters = filters;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public void setValue(String value) {
		this.textbox.setValue(value);
	}
	
	public void setErrorMessage(String errmsg) {
		this.textbox.setErrorMessage(errmsg);
	}

	public void setMandatory(boolean mandatory) {
		if (mandatory) {
			this.space.setSclass("mandatory");
		} else {
			this.space.setSclass("");
		}
	}

	public void setButtonDisabled(boolean isDisable) {
		this.button.setDisabled(isDisable);
	}

	public void setButtonVisible(boolean isVisible) {
		this.button.setVisible(isVisible);
		if (!isVisible) {
			this.space.setSclass("");
		}
	}

	public boolean isButtonVisible() {
		return this.button.isVisible();
	}

	public boolean setVisible(boolean decider) {
		return this.setVisible(decider);
	}
	
	public ModuleMapping getModuleMapping() {
		return moduleMapping;
	}

	public void setModuleMapping(ModuleMapping moduleMapping) {
		this.moduleMapping = moduleMapping;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
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

	public int getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(int displayStyle) {
		this.displayStyle = displayStyle;
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
		return validateColumns;
	}

	public void setValidateColumns(String[] validateColumns) {
		this.validateColumns = validateColumns;
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

	public void setInputAllowed(boolean inputAllowed) {
		this.inputAllowed = inputAllowed;
	}
 
}
