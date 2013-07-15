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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.coremasters.CollateralAssignment;
import com.pennant.backend.model.coremasters.CollateralAssignmentDetail;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CoreMasters/CollateralAssignments/CollateralAssignmentDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CollateralAssignmentDialogCtrl extends GFCBaseListCtrl<CollateralAssignmentDetail> implements Serializable {

	private static final long serialVersionUID = 8602015982512929710L;
	private final static Logger logger = Logger.getLogger(CollateralAssignmentDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CollateralAssignmentDialog; // autowired
	
	protected Label 		custCIF;
	protected Label 		custShortName;
	protected Label 		reference;
	protected Decimalbox 	faceValue;
	protected Label 		currency;
	protected Decimalbox 	actualValue;
	protected Decimalbox 	currentCover;
	protected Decimalbox 	shortFall;
	
	protected Listbox 		listBoxCollateralAssignment;
	protected Grid			grid_enquiryDetails;
	
	private CollateralAssignment collateralAssignment;

	/**
	 * default constructor.<br>
	 */
	public CollateralAssignmentDialogCtrl() {
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
	public void onCreate$window_CollateralAssignmentDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("CollateralAssignment")) {
			setCollateralAssignment((CollateralAssignment) args.get("CollateralAssignment"));
		} else {
			setCollateralAssignment(null);
		}

		// READ OVERHANDED params !
		// we get the accountingSetListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete accountingSet here.

		getBorderLayoutHeight();
		int dialogHeight =  grid_enquiryDetails.getRows().getVisibleItemCount()* 20 + 100 +25; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxCollateralAssignment.setHeight(listboxHeight+"px");

		// set Field Properties
		doShowDialog(getCollateralAssignment());
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
	public void onClose$window_CollateralAssignmentDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CollateralAssignmentDialog);
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
		closeDialog(this.window_CollateralAssignmentDialog, "CustomerLimitEnquiry");
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccountingSet
	 *            (AccountingSet)
	 */
	public void doWriteBeanToComponents(CollateralAssignment assignment) {
		logger.debug("Entering");
		
		this.custCIF.setValue(assignment.getCustCIF());
		this.custShortName.setValue(assignment.getCustShortName());
		this.reference.setValue(assignment.getReference());
		this.faceValue.setValue(new BigDecimal(assignment.getFaceValue()));
		this.currency.setValue(assignment.getCurrency());
		this.actualValue.setValue(new BigDecimal(assignment.getActualValue()));
		this.currentCover.setValue(new BigDecimal(assignment.getCurrentCover()));
		this.shortFall.setValue(new BigDecimal(assignment.getShortFall()));
		
		doFilllistbox(assignment.getAssignmentDetails());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param assignment
	 * @throws InterruptedException
	 */
	public void doShowDialog(CollateralAssignment assignment) throws InterruptedException {
		logger.debug("Entering");

		// if aAccountingSet == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (assignment == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			assignment = new CollateralAssignment();
			setCollateralAssignment(assignment);
		} else {
			setCollateralAssignment(assignment);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(assignment);
			
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_CollateralAssignmentDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for rendering list of TransactionEntry
	 * 
	 * @param details
	 */
	public void doFilllistbox(List<CollateralAssignmentDetail> details) {
		logger.debug("Entering");

		if (details != null) {

			Listitem item = null;
			Listcell lc = null;
			for (CollateralAssignmentDetail detail : details) {

				item = new Listitem();
				
				lc = new Listcell(detail.getCustCIF());
				lc.setParent(item);
				
				lc = new Listcell(detail.getCollateralReference());
				lc.setParent(item);
				
				lc = new Listcell(detail.getAssignRule());
				lc.setParent(item);
				
				lc = new Listcell(detail.getPercentOfBank());
				lc.setParent(item);
				
				lc = new Listcell(detail.getMaxAmount());
				lc.setParent(item);
				
				lc = new Listcell(detail.getCurrency());
				lc.setParent(item);
				
				lc = new Listcell(detail.getEquivalent());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				lc = new Listcell(detail.getAssigned());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
								
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onAssignmentDetailItemDoubleClicked");
				this.listBoxCollateralAssignment.appendChild(item);
			}
			
		}
		logger.debug("Leaving");
	}
	
	public void onAssignmentDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Customer object
		final Listitem item = this.listBoxCollateralAssignment.getSelectedItem();
		final HashMap<String, Object> map = new HashMap<String, Object>();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			CollateralAssignmentDetail detail = (CollateralAssignmentDetail) item.getAttribute("data");
			map.put("collateralAssignmentDetail", detail);
			map.put("collateralAssignment", getCollateralAssignment());
		}	
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CoreMasters/CollateralAssignments/CollateralAssignmentDetailDialog.zul",
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

	public void setCollateralAssignment(CollateralAssignment collateralAssignment) {
		this.collateralAssignment = collateralAssignment;
	}
	
	public CollateralAssignment getCollateralAssignment() {
		return collateralAssignment;
	}

}
