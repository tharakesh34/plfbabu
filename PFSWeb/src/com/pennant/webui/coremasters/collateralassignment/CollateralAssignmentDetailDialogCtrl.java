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

package com.pennant.webui.coremasters.collateralassignment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.coremasters.CollateralAssignment;
import com.pennant.backend.model.coremasters.CollateralAssignmentDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerLimit/CustomerLimitEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CollateralAssignmentDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(CollateralAssignmentDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CollateralAssignmentDetailDialog; // autowired
	
	protected Label 		custCIF;
	protected Label 		custShortName;
	protected Label 		reference;
	protected Decimalbox 	value;
	protected Label 		currency;
	protected Decimalbox	shortFall;

	protected Label 		collateralRef;
	protected Label 		dealType;
	protected Label 		reference1;
	protected Label 		branch;
	protected Label 		accNum;
	protected Label 		custCIF1;
	protected Label 		custShortName1;
	protected Label 		currency1;
	protected Label 		collateralType;
	protected Label 		collateralTypeDesc;
	protected Decimalbox 	collateralAvail;
	protected Label 		assignRule;
	protected Label 		percent;
	protected Decimalbox	maxAmount;
	protected Decimalbox	bankValuation;
	protected Decimalbox	equivalent;
	
	
	protected Listbox 		listBoxCustomerLimit;
	protected Grid			grid_enquiryDetails;
	
	private CollateralAssignment collateralAssignment;
	private CollateralAssignmentDetail collateralAssignmentDetail;

	/**
	 * default constructor.<br>
	 */
	public CollateralAssignmentDetailDialogCtrl() {
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
	public void onCreate$window_CollateralAssignmentDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("collateralAssignmentDetail")) {
			setCollateralAssignmentDetail((CollateralAssignmentDetail) args.get("collateralAssignmentDetail"));
		} else {
			setCollateralAssignmentDetail(null);
		}
		
		if (args.containsKey("collateralAssignment")) {
			setCollateralAssignment((CollateralAssignment) args.get("collateralAssignment"));
		} else {
			setCollateralAssignment(null);
		}

		// set Field Properties
		doShowDialog(getCollateralAssignmentDetail());
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
	public void onClose$window_CollateralAssignmentDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CollateralAssignmentDetailDialog);
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
		this.window_CollateralAssignmentDetailDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet
	 *            (AccountingSet)
	 */
	public void doWriteBeanToComponents(CollateralAssignmentDetail assignmentDetail) {
		logger.debug("Entering");
		
		this.custCIF.setValue(assignmentDetail.getCustCIF());
		this.custShortName.setValue(assignmentDetail.getCustShortName());
		this.reference.setValue(assignmentDetail.getReference());
		this.value.setValue(new BigDecimal(getCollateralAssignment().getActualValue()));
		this.currency.setValue(getCollateralAssignment().getCurrency());
		this.shortFall.setValue(new BigDecimal(getCollateralAssignment().getShortFall()));

		this.collateralRef.setValue(assignmentDetail.getCollateralReference());
		this.dealType.setValue(assignmentDetail.getDealType());
		this.reference1.setValue(assignmentDetail.getReference());
		this.branch.setValue(assignmentDetail.getBranch());
		this.accNum.setValue(assignmentDetail.getAccNumber());
		this.custCIF1.setValue(assignmentDetail.getCustCIF());
		this.custShortName1.setValue(assignmentDetail.getCustShortName());
		this.currency1.setValue(assignmentDetail.getCurrency());
		this.bankValuation.setValue(new BigDecimal(assignmentDetail.getBankValuation()));
		this.collateralType.setValue(assignmentDetail.getCollateralType());
		this.collateralTypeDesc.setValue(assignmentDetail.getCollateralTypeDesc());
		this.collateralAvail.setValue(new BigDecimal(assignmentDetail.getCollateralAvail()));
		this.assignRule.setValue(assignmentDetail.getAssignRule());
		this.percent.setValue(assignmentDetail.getPercentOfBank());
		this.maxAmount.setValue(new BigDecimal(assignmentDetail.getMaxAmount()));
		this.equivalent.setValue(new BigDecimal(assignmentDetail.getEquivalent()));
		
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param assignmentDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CollateralAssignmentDetail assignmentDetail) throws InterruptedException {
		logger.debug("Entering");

		// if aAccountingSet == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (assignmentDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			assignmentDetail = new CollateralAssignmentDetail();
			setCollateralAssignmentDetail(assignmentDetail);
		} else {
			setCollateralAssignmentDetail(assignmentDetail);
		}
		
		try {
			// fill the components with the data
			doWriteBeanToComponents(assignmentDetail);
			
			// stores the initial data for comparing if they are changed
			// during user action.
			this.window_CollateralAssignmentDetailDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCollateralAssignmentDetail(CollateralAssignmentDetail collateralAssignmentDetail) {
		this.collateralAssignmentDetail = collateralAssignmentDetail;
	}

	public CollateralAssignmentDetail getCollateralAssignmentDetail() {
		return collateralAssignmentDetail;
	}

	public void setCollateralAssignment(CollateralAssignment collateralAssignment) {
		this.collateralAssignment = collateralAssignment;
	}

	public CollateralAssignment getCollateralAssignment() {
		return collateralAssignment;
	}

}
