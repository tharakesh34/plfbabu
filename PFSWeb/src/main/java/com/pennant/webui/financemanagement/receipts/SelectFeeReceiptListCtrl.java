/**
Copyright 2011 - Pennant Technologies
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
 * FileName    		:  SelectFeeReceiptListCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2019    														*
 *                                                                  						*
 * Modified Date    :  21-03-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2019       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.receipts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceType/SelectFinTypeDialog.zul file.
 */
public class SelectFeeReceiptListCtrl extends GFCBaseCtrl<FinReceiptHeader> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = Logger.getLogger(SelectFeeReceiptListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectFeeReceiptList;
	protected Combobox recAgainst;
	protected Button btnProceed;

	private FeeReceiptListCtrl feeReceiptListCtrl;
	private FinReceiptHeader receiptHeader;

	/**
	 * default constructor.<br>
	 */
	public SelectFeeReceiptListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeReceiptDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectFeeReceiptList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectFeeReceiptList);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("feeReceiptListCtrl")) {
			this.feeReceiptListCtrl = (FeeReceiptListCtrl) arguments.get("feeReceiptListCtrl");
		}

		if (arguments.containsKey("receiptHeader")) {
			this.receiptHeader = (FinReceiptHeader) arguments.get("receiptHeader");
		}

		doLoadWorkFlow(this.receiptHeader.isWorkflow(), this.receiptHeader.getWorkflowId(),
				this.receiptHeader.getNextTaskId());

		if (isWorkFlowEnabled() && !enqiryModule) {
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
		}

		fillComboBox(this.recAgainst, "", PennantStaticListUtil.getReceiptAgainstList(), "");
		doCheckRights();

		doSetFieldProperties();

		this.window_SelectFeeReceiptList.doModal();

		logger.debug("Leaving ");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnProceed.setVisible(getUserWorkspace().isAllowed("button_FeeReceiptDialog_btnReceipt"));

		logger.debug("Leaving");
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering ");

		doSetValidation();
		doWriteComponentsToBean(this.receiptHeader);
		doShowDialogPage(this.receiptHeader);

		logger.debug("Leaving ");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financeType
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinReceiptHeader receiptHeader) {
		logger.debug("Entering");

		Map<String, Object> aruments = new HashMap<String, Object>();

		aruments.put("receiptHeader", receiptHeader);
		aruments.put("feeReceiptListCtrl", this.feeReceiptListCtrl);
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Receipts/FeeReceiptDialog.zul", null,
					aruments);
			this.window_SelectFeeReceiptList.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.recAgainst.isDisabled()) {
			this.recAgainst.setConstraint(new StaticListValidator(PennantStaticListUtil.getReceiptAgainstList(),
					Labels.getLabel("label_FeeReceiptList_RecAgainst.value")));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.recAgainst.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType
	 */
	public void doWriteComponentsToBean(FinReceiptHeader receiptHeader) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//product
		try {
			if (isValidComboValue(this.recAgainst, Labels.getLabel("label_FeeReceiptList_RecAgainst.value"))) {
				receiptHeader.setRecAgainst(getComboboxValue(this.recAgainst));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FeeReceiptListCtrl getFeeReceiptListCtrl() {
		return feeReceiptListCtrl;
	}

	public void setFeeReceiptListCtrl(FeeReceiptListCtrl feeReceiptListCtrl) {
		this.feeReceiptListCtrl = feeReceiptListCtrl;
	}
}
