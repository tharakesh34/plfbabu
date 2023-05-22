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
 * * FileName : CustomerDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file.
 */
public class ProspectCustomerDialogCtrl extends GFCBaseCtrl<Customer> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = LogManager.getLogger(ProspectCustomerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProspectCustomerDialog; // autowired

	protected ExtendedCombobox lovDescCustCIF; // autowired
	protected Label lovDescCustShrtName; // autowired
	protected Textbox custCoreBank; // autowired

	private Customer customer; // overhanded per param

	private transient boolean validationOn;
	private CustomerDetailsService customerDetailsService;

	protected Groupbox gb_Action;
	// Module Type Details

	/**
	 * default constructor.<br>
	 */
	public ProspectCustomerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ProspectCustomerDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProspectCustomerDialog);

		logger.debug("Entering");

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			// Hegiht Setting
			doShowDialog(this.customer);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProspectCustomerDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.lovDescCustCIF.setMaxlength(10);
		this.lovDescCustCIF.setModuleName("Customer");
		this.lovDescCustCIF.setValidateColumns(new String[] { "CustCIF" });
		this.lovDescCustCIF.setMandatoryStyle(true);
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("custCoreBank", "", Filter.OP_EQUAL);

		this.lovDescCustCIF.setFilters(filters);

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnSave"));
		this.btnCancel.setVisible(false);
		// Customer related List Buttons
		logger.debug("Leaving");
	}

	public void onClick$viewCustInfo(Event event) {
		try {
			if (customer != null) {
				CustomerDetails customerDetails = getCustomerDetailsService().getCustomerById(customer.getCustID());
				final Map<String, Object> map = new HashMap<String, Object>();
				String pageName = PennantAppUtil.getCustomerPageName();
				map.put("customerDetails", customerDetails);
				map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
				map.put("ProspectCustomerEnq", "ProspectCustomerEnq");
				Executions.createComponents(pageName, null, map);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws CustomerNotFoundException
	 */
	public void onClick$btnSave(Event event)
			throws InterruptedException, ParseException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		try {
			doSave();
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			MessageUtil.showError("Customer Not Found.");
		}
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
		MessageUtil.showHelpWindow(event, window_ProspectCustomerDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer Customer
	 */
	public void onFulfill$lovDescCustCIF(Event event) {
		Object dataObject = this.lovDescCustCIF.getObject();

		lovDescCustShrtName.setValue("");
		this.lovDescCustCIF.setValue("");
		this.customer = null;

		if (dataObject instanceof Customer) {
			this.customer = (Customer) dataObject;
			this.lovDescCustCIF.setValue(customer.getCustCIF());
			this.lovDescCustShrtName.setValue(customer.getCustShrtName());
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 */
	public void doWriteComponentsToBean(Customer aCustomer) throws ParseException, InterfaceException {
		logger.debug("Entering");
		doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCustomer.setCustCIF(this.lovDescCustCIF.getValue());
			aCustomer.setCustCIF(this.lovDescCustCIF.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.customer != null) {
				aCustomer.setCustCoreBank(StringUtils.trimToEmpty(this.custCoreBank.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve);
		if (this.customer != null) {
			aCustomer.setCustID(this.customer.getCustID());
		} else {
			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_ProspectCustomerDialog_CustID.value") }));
		}

		doRemoveValidation();
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 */
	public void doShowDialog(Customer aCustomer) {
		logger.debug("Entering");
		try {
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProspectCustomerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		doClearMessage();
		this.lovDescCustCIF.setConstraint(
				new PTStringValidator(Labels.getLabel("label_ProspectCustomerDialog_CustID.value"), null, true));
		this.custCoreBank
				.setConstraint(new PTStringValidator(Labels.getLabel("label_ProspectCustomerDialog_CustCoreBank.value"),
						PennantRegularExpressions.REGEX_CUSTCIF, true));

		/*
		 * if (!this.lovDescCustCIF.isReadonly()) { this.lovDescCustCIF.setConstraint(new
		 * PTStringValidator(Labels.getLabel ("label_FinanceMainDialog_CustID.value"), null, true)); } if
		 * (!this.custCoreBank.isReadonly()) { this.custCoreBank.setConstraint(new PTStringValidator(Labels.getLabel(
		 * "label_CustomerDialog_CustCoreBank.value"), null, true)); }
		 */
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.lovDescCustCIF.setConstraint("");
		this.custCoreBank.setConstraint("");
		doClearMessage();
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.lovDescCustCIF.setErrorMessage("");
		this.custCoreBank.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.lovDescCustCIF.setReadonly(true);
		this.custCoreBank.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.lovDescCustCIF.setValue("");
		this.lovDescCustShrtName.setValue("");
		this.custCoreBank.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doSave() throws InterruptedException, ParseException, InterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		Customer aCustomer = new Customer();
		aCustomer = ObjectUtil.clone(this.customer);

		// BeanUtils.copyProperties(aCustomer, this.customer);
		doWriteComponentsToBean(aCustomer);

		Customer coreCustomer = new Customer();
		coreCustomer.setCustCIF(aCustomer.getCustCoreBank());

		boolean coreBankIdExist = getCustomerDetailsService().getCustomerByCoreBankId(aCustomer.getCustCoreBank());
		if (coreBankIdExist) {
			// Show Confirmation Message
			Clients.showNotification(Labels.getLabel("label_CustCoreBank_Exist"), "warning", null, null, -1);
			this.custCoreBank.setValue("");
			return;
		}

		coreCustomer = getCustomerDetailsService().fetchCoreCustomerDetails(coreCustomer);
		getCustomerDetailsService().updateProspectCustomer(aCustomer);

		// Show Confirmation Message
		Clients.showNotification("Updated SuccessFully", "info", null, null, -1);

		doClear();
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}