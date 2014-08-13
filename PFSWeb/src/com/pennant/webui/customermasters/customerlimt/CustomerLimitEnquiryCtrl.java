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
 * FileName    		:  AccountingSetDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2011    														*
 *                                                                  						*
 * Modified Date    :  14-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customerlimt;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.backend.model.customermasters.CustomerLimitCategory;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.CustomerLimitProcessException;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerLimit/CustomerLimitEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerLimitEnquiryCtrl extends GFCBaseListCtrl<CustomerLimitCategory> implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(CustomerLimitEnquiryCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerLimitEnquiry; // autowired
	
	protected Label 		custCIF;
	protected Label 		custShortName;
	protected Label 		country;
	protected Label 		groupName;
	protected Label 		currency;
	protected Label 		earliestExpiryDate;
	
	protected Listbox 		listBoxCustomerLimit;
	protected Grid			grid_enquiryDetails;
	
	private CustomerLimit customerLimit;

	/**
	 * default constructor.<br>
	 */
	public CustomerLimitEnquiryCtrl() {
		super();
	}
	
	private CustomerLimitIntefaceService customerLimitIntefaceService;
	private CurrencyService currencyService;

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AccountingSet object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerLimitEnquiry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("customerLimit")) {
			setCustomerLimit((CustomerLimit) args.get("customerLimit"));
		} else {
			setCustomerLimit(null);
		}

		// READ OVERHANDED params !
		// we get the accountingSetListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete accountingSet here.
		if (args.containsKey("customerLimitListCtrl")) {
		} else {
		}

		getBorderLayoutHeight();
		int dialogHeight =  grid_enquiryDetails.getRows().getVisibleItemCount()* 20 + 100 ; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxCustomerLimit.setHeight(listboxHeight+"px");

		// set Field Properties
		doShowDialog(getCustomerLimit());
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CustomerLimitEnquiry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerLimitEnquiry);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		closeDialog(this.window_CustomerLimitEnquiry, "CustomerLimitEnquiry");
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet
	 *            (AccountingSet)
	 * @throws CustomerLimitProcessException 
	 * @throws InterruptedException 
	 */
	public void doWriteBeanToComponents(CustomerLimit customerLimit) throws CustomerLimitProcessException, InterruptedException {
		logger.debug("Entering");
		
		this.custCIF.setValue(customerLimit.getCustCIF());
		this.custShortName.setValue(customerLimit.getCustShortName());
		this.country.setValue(customerLimit.getBranch());
		this.groupName.setValue(customerLimit.getLimitCategory());
		if(customerLimit.isRepeatThousands()){
			this.currency.setValue(customerLimit.getCurrency() +" ( Thousands '000' Omitted) ");
		}else{
			this.currency.setValue(customerLimit.getCurrency());
		}
		this.earliestExpiryDate.setValue(DateUtility.formatDate(DateUtility.addYears(customerLimit.getEarliestExpiryDate(),1900),
				PennantConstants.dateFormate));
		
		doFilllistbox(customerLimit.getCustCIF());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerLimit
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerLimit aCustomerLimit) throws InterruptedException {
		logger.debug("Entering");

		// if aAccountingSet == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerLimit == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCustomerLimit = new CustomerLimit();
			setCustomerLimit(aCustomerLimit);
		} else {
			setCustomerLimit(aCustomerLimit);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerLimit);
			
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_CustomerLimitEnquiry);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param custlimitCategoryList
	 * @throws CustomerLimitProcessException 
	 * @throws InterruptedException 
	 */
	public void doFilllistbox(String custMnemonic) throws CustomerLimitProcessException, InterruptedException {
		logger.debug("Entering");
		
		com.pennant.coreinterface.model.CustomerLimit limit = new com.pennant.coreinterface.model.CustomerLimit();
		limit.setCustMnemonic(custMnemonic);
		limit.setCustLocation("");
		List<com.pennant.coreinterface.model.CustomerLimit> list = null;
		int formatter = 0;
		try {
			
			list = getCustomerLimitIntefaceService().fetchLimitEnquiryDetails(limit);
			Currency currency = getCurrencyService().getCurrencyById(getCustomerLimit().getCurrency());
			if(currency != null){
				formatter = currency.getCcyEditField();
			}
			
			if(getCustomerLimit().isRepeatThousands()){
				formatter = formatter + 3;
			}
		} catch (CustomerLimitProcessException e) {
			logger.error(e.getMessage());
			PTMessageUtils.showErrorMessage(e.getMessage());
		}

		if (list != null) {

			Listitem item = null;
			Listcell lc = null;
			for (com.pennant.coreinterface.model.CustomerLimit category : list) {

				boolean limitExceed = false;
				if(category.getRiskAmount().longValue() > category.getLimitAmount().longValue()){
					limitExceed = true;
				}
				
				item = new Listitem();
				lc = new Listcell(category.getLimitCategory());
				if(limitExceed){
					lc.setStyle("color:red;");
				}
				lc.setParent(item);
				
				lc = new Listcell(category.getLimitCategoryDesc());
				if(limitExceed){
					lc.setStyle("color:red;");
				}
				lc.setParent(item);
				
				lc = new Listcell(String.valueOf(category.getRiskAmount().divide(new BigDecimal(Math.pow(10, formatter)),RoundingMode.HALF_UP)));
				
				if(limitExceed){
					lc.setStyle("color:red;text-align:right;");
				}else{
					lc.setStyle("text-align:right;");
				}
				lc.setParent(item);
				
				lc = new Listcell(String.valueOf(category.getLimitAmount().divide(new BigDecimal(Math.pow(10, formatter)),RoundingMode.HALF_UP)));
				if(limitExceed){
					lc.setStyle("color:red;text-align:right;");
				}else{
					lc.setStyle("text-align:right;");
				}
				lc.setParent(item);
				
				lc = new Listcell(String.valueOf(category.getAvailAmount().divide(new BigDecimal(Math.pow(10, formatter)),RoundingMode.HALF_UP)));
				if(limitExceed){
					lc.setStyle("color:red;text-align:right;");
				}else{
					lc.setStyle("text-align:right;");
				}
				lc.setParent(item);
								
				item.setAttribute("data", category);
				item.setId(category.getLimitCategory());
			//	ComponentsCtrl.applyForward(item, "onDoubleClick=onCategoryItemDoubleClicked");
				this.listBoxCustomerLimit.appendChild(item);
			}
			
		}
		logger.debug("Leaving");
	}
	
	public void onCategoryItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCustomerLimit.getSelectedItem();
		final HashMap<String, Object> map = new HashMap<String, Object>();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			CustomerLimitCategory category = (CustomerLimitCategory) item.getAttribute("data");
			getCustomerLimit().setLimitCategory(category.getLimitCategory());
			map.put("customerLimit", getCustomerLimit());
		}	
		
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CustomerListbox from the
		 * dialog when we do a delete, edit or insert a Customer.
		 */
		map.put("customerListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerLimit/CustomerLimitCategoryEnquiryList.zul",
					null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CustomerLimit getCustomerLimit() {
		return customerLimit;
	}
	public void setCustomerLimit(CustomerLimit customerLimit) {
		this.customerLimit = customerLimit;
	}

	public void setCustomerLimitIntefaceService(
			CustomerLimitIntefaceService customerLimitIntefaceService) {
		this.customerLimitIntefaceService = customerLimitIntefaceService;
	}

	public CustomerLimitIntefaceService getCustomerLimitIntefaceService() {
		return customerLimitIntefaceService;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}
	
}
