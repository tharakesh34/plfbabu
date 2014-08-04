package com.pennant.webui.facility.facility;

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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.coreinterface.vo.CustomerCollateral;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/Collateral/collateralDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CollateralEnquiryDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralEnquiryDialog; // autowired
	protected Textbox custCIF; // autowired
	protected Textbox collReference; // autowired
	protected Textbox collType; // autowired
	protected Textbox collTypeDesc; // autowired
	protected Textbox collComplete; // autowired
	protected Textbox currency; // autowired
	protected Textbox collExpDate; // autowired
	protected Textbox colllastRvwDate; // autowired
	protected Decimalbox collValue; // autowired
	protected Decimalbox collBankVal; // autowired
	protected Decimalbox collBankValMar; // autowired
	protected Textbox colllocation; // autowired
	protected Textbox colllocationDesc; // autowired

	// not auto wired vars
	private CustomerCollateral customerCollateral; // overhanded per param

	// Button controller for the CRUD buttons
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire

	/**
	 * default constructor.<br>
	 */
	public CollateralEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Collateral object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CollateralEnquiryDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */

			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("eqtnCollateral")) {
				this.setCustomerCollateral((CustomerCollateral) args.get("eqtnCollateral"));

			}
			// READ OVERHANDED params !
			// we get the collateralListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete collateral here.

			// set Field Properties
			doShowDialog(getCustomerCollateral());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_CollateralEnquiryDialog.onClose();
		}
		logger.debug("Leaving");
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
	public void onClose$window_CollateralDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
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
		PTMessageUtils.showHelpWindow(event, window_CollateralEnquiryDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// GUI Process
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
		this.window_CollateralEnquiryDialog.onClose();
		logger.debug("Leaving");
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
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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
