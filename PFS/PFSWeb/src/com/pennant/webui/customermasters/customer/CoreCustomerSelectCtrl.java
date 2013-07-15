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
 * FileName    		:  CustomerListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.customer;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/CustomerList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CoreCustomerSelectCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 9086034736503097868L;
	private final static Logger logger = Logger.getLogger(CoreCustomerSelectCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUl-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CoreCustomer; 		// autowired
	protected Textbox		custCIF;					// autowired
	protected Borderlayout 	borderLayout_CoreCustomer; 	// autowired

	// checkRights
	protected Button btnSearchCustFetch; 				// autowired
	protected Button btnClose;							// autowired
	protected CustomerListCtrl customerListCtrl;		// autowired
	private CustomerDetailsService customerDetailsService;
	private CustomerInterfaceService customerInterfaceService;
	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public CoreCustomerSelectCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CoreCustomer(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("customerListCtrl")) {
			this.customerListCtrl = (CustomerListCtrl) args.get("customerListCtrl");
		} else {
			this.customerListCtrl = null;
		}
		
		this.custCIF.setFocus(true);
		this.window_CoreCustomer.doModal();
		logger.debug("Leaving" + event.toString());
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearErrorMessage();
		setValidationOn(true);
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearErrorMessage() {
		logger.debug("Enterring");
		this.custCIF.setErrorMessage("");
		logger.debug("Leaving");
	}
	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearchCustFetch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSetValidation();
		//Get the data of Customer from Core Banking Customer
		Customer customer = new Customer();
		customer.setCustCIF(this.custCIF.getValue());
		try {
			customer = getCustomerInterfaceService().fetchCustomerDetails(customer);
			this.customerListCtrl.buildDialogWindow(customer);
			this.window_CoreCustomer.onClose();
		} catch (CustomerNotFoundException e) {
			logger.error(e);
			MultiLineMessageBox.show("Customer Not Exist in Core Banking System", 
					Labels.getLabel("message.Error"), MultiLineMessageBox.ABORT, MultiLineMessageBox.ERROR);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException,ParseException {
		logger.debug("Entering" + event.toString());
		this.window_CoreCustomer.onClose();
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}
	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

}