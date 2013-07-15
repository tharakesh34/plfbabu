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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.CustLimitCategoryBreakdown;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerLimit/CustomerLimitEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerLimitCategoryEnquiryCtrl extends GFCBaseListCtrl<CustLimitCategoryBreakdown> implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(CustomerLimitCategoryEnquiryCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerLimitCategoryEnquiry; // autowired
	
	protected Label 		custCIF;
	protected Label 		custShortName;
	protected Label 		country;
	protected Label 		groupName;
	protected Label 		limitCategory;
	
	protected Listbox 		listBoxCustomerLimit;
	protected Grid			grid_enquiryDetails;
	
	private CustomerLimit customerLimit;

	/**
	 * default constructor.<br>
	 */
	public CustomerLimitCategoryEnquiryCtrl() {
		super();
	}

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
	public void onCreate$window_CustomerLimitCategoryEnquiry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("customerLimit")) {
			setCustomerLimit((CustomerLimit) args.get("customerLimit"));
		} else {
			setCustomerLimit(null);
		}

		getBorderLayoutHeight();
		int dialogHeight =  grid_enquiryDetails.getRows().getVisibleItemCount()* 20 + 400; 
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
	public void onClose$window_CustomerLimitCategoryEnquiry(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerLimitCategoryEnquiry);
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
		this.window_CustomerLimitCategoryEnquiry.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet
	 *            (AccountingSet)
	 */
	public void doWriteBeanToComponents(CustomerLimit customerLimit) {
		logger.debug("Entering");
		
		this.custCIF.setValue(customerLimit.getCustCIF());
		this.custShortName.setValue(customerLimit.getCustShortName());
		this.country.setValue("");
		this.groupName.setValue("");
		this.limitCategory.setValue(customerLimit.getLimitCategory());
		
		doFilllistbox(null);
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
			this.window_CustomerLimitCategoryEnquiry.doModal();
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
	 */
	public void doFilllistbox(List<CustLimitCategoryBreakdown> categoryBreakdowns) {
		logger.debug("Entering");

		if (categoryBreakdowns != null) {

			Listitem item = null;
			Listcell lc = null;
			for (CustLimitCategoryBreakdown breakdown : categoryBreakdowns) {

				item = new Listitem();
				lc = new Listcell(breakdown.getAccountNum());
				lc.setParent(item);
				lc = new Listcell(breakdown.getCurrency());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(breakdown.getEquivalent(), 3));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(breakdown.getEndDate()==null? "": DateUtility.formatDate(breakdown.getEndDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				lc = new Listcell(breakdown.getType());
				lc.setParent(item);
				lc = new Listcell(breakdown.getComments());
				lc.setParent(item);
				this.listBoxCustomerLimit.appendChild(item);
			}
			
		}
		logger.debug("Leaving");
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

}
