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
 * FileName    		:  CollateralDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.facility.facility;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/Collateral/collateralDialog.zul file.
 */
public class CollateralEnquiryDialogCtrl extends GFCBaseCtrl<CustomerCollateral> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CollateralDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CollateralEnquiryDialog; 
	protected Textbox custCIF; 
	protected Textbox collReference; 
	protected Textbox collType; 
	protected Textbox collTypeDesc; 
	protected Textbox collComplete; 
	protected Textbox currency; 
	protected Textbox collExpDate; 
	protected Textbox colllastRvwDate; 
	protected Decimalbox collValue; 
	protected Decimalbox collBankVal; 
	protected Decimalbox collBankValMar; 
	protected Textbox colllocation; 
	protected Textbox colllocationDesc; 

	// not auto wired vars
	private CustomerCollateral customerCollateral; // overhanded per param

	/**
	 * default constructor.<br>
	 */
	public CollateralEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Collateral object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CollateralEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralEnquiryDialog);

		try {
			/* set components visible dependent of the users rights */

			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			if (arguments.containsKey("eqtnCollateral")) {
				this.setCustomerCollateral((CustomerCollateral) arguments.get("eqtnCollateral"));

			}
			// READ OVERHANDED params !
			// we get the collateralListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete collateral here.

			// set Field Properties
			doShowDialog(getCustomerCollateral());
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_CollateralEnquiryDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_CollateralEnquiryDialog);
		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerCollateral
	 *            Collateral
	 */
	public void doWriteBeanToComponents(CustomerCollateral aCustomerCollateral) {
		logger.debug("Entering");
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomerCollateral.getCustCIF()));
		this.collReference.setValue(StringUtils.trimToEmpty(aCustomerCollateral.getCollReference()));
		this.collType.setValue(aCustomerCollateral.getCollType());
		this.collTypeDesc.setValue(aCustomerCollateral.getCollTypeDesc());

		this.collComplete.setValue(aCustomerCollateral.getCollComplete());
		this.currency.setValue(aCustomerCollateral.getCollCcy());

		this.collExpDate.setValue(StringUtils.trimToEmpty(aCustomerCollateral.getCollExpDate().toString()));
		this.colllastRvwDate.setValue(StringUtils.trimToEmpty(aCustomerCollateral.getColllastRvwDate().toString()));

		this.collValue.setValue(new BigDecimal(aCustomerCollateral.getCollValue().toString()));
		this.collBankVal.setValue(new BigDecimal(aCustomerCollateral.getCollBankVal().toString()));
		this.collBankValMar.setValue(new BigDecimal(aCustomerCollateral.getCollBankValMar().toString()));

		this.colllocation.setValue(aCustomerCollateral.getColllocation());
		this.colllocationDesc.setValue(aCustomerCollateral.getColllocationDesc());

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerCollateral
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerCollateral aCustomerCollateral) throws InterruptedException {
		logger.debug("Entering");
		try {
			doWriteBeanToComponents(aCustomerCollateral);
			this.window_CollateralEnquiryDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public CustomerCollateral getCustomerCollateral() {
		return customerCollateral;
	}

	public void setCustomerCollateral(CustomerCollateral customerCollateral) {
		this.customerCollateral = customerCollateral;
	}

}
