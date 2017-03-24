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
 * FileName    		:  InventorySettlementDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.inventorysettlement.inventorysettlement;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.inventorysettlement.InventorySettlement;
import com.pennant.backend.model.inventorysettlement.InventorySettlementDetails;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.inventorysettlement.InventorySettlementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/InventorySettlement/InventorySettlement
 * /inventorySettlementDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class InventorySettlementDialogCtrl extends GFCBaseCtrl<InventorySettlement> {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(InventorySettlementDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_InventorySettlementDialog;
	protected Row row0;
	protected Label label_BrokerCode;
	protected Hlayout hlayout_BrokerCode;
	protected Space space_BrokerCode;

	protected ExtendedCombobox brokerCode;
	protected Label label_SettlementDate;
	protected Hlayout hlayout_SettlementDate;
	protected Space space_SettlementDate;

	protected Datebox settlementDate;

	// not auto wired vars
	private InventorySettlement inventorySettlement; // overhanded
														// per
	private InventorySettlementListCtrl inventorySettlementListCtrl; // overhanded

	
	protected Listbox listBoxCommodities;
	// ServiceDAOs / Domain Classes
	private InventorySettlementService inventorySettlementService;
	private PagedListService pagedListService;

	protected Tab tabPosting;
	protected Listbox listBoxFinAccountings;
	private PostingsPreparationUtil postingsPreparationUtil;

	/**
	 * default constructor.<br>
	 */
	public InventorySettlementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InventorySettlementDialog";
		super.enqiryModule = (Boolean) arguments.get("enqiryModule");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected InventorySettlement object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InventorySettlementDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InventorySettlementDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("inventorySettlement")) {
				this.inventorySettlement = (InventorySettlement) arguments.get("inventorySettlement");
				InventorySettlement befImage = new InventorySettlement();
				BeanUtils.copyProperties(this.inventorySettlement, befImage);
				this.inventorySettlement.setBefImage(befImage);

				setInventorySettlement(this.inventorySettlement);
			} else {
				setInventorySettlement(null);
			}

			doLoadWorkFlow(this.inventorySettlement.isWorkflow(), this.inventorySettlement.getWorkflowId(),
					this.inventorySettlement.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED params !
			// we get the inventorySettlementListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete inventorySettlement here.
			if (arguments.containsKey("inventorySettlementListCtrl")) {
				setInventorySettlementListCtrl((InventorySettlementListCtrl) arguments
						.get("inventorySettlementListCtrl"));
			} else {
				setInventorySettlementListCtrl(null);
			}

			this.listBoxFinAccountings.setHeight((this.borderLayoutHeight - 220) + "px");
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getInventorySettlement());
		} catch (Exception e) {
			createException(window_InventorySettlementDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		try {
			doWriteBeanToComponents(this.inventorySettlement.getBefImage());
		} catch (Exception e) {
			logger.debug("Exception:", e);
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
		MessageUtil.showHelpWindow(event, window_InventorySettlementDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(
					getNotes("InventorySettlement", String.valueOf(getInventorySettlement().getId()),
							getInventorySettlement().getVersion()), this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			MessageUtil.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onFulfill$brokerCode(Event event) throws IllegalAccessException, InvocationTargetException,
			PFFInterfaceException {
		logger.debug("Entering");
		if (!StringUtils.isBlank(this.brokerCode.getValue())) {
			doWriteComponentsToBean(getInventorySettlement());
			processUnsoldComoodities(getInventorySettlement());

		} else {
			this.listBoxCommodities.getItems().clear();
		}

		logger.debug("Leaving");
	}

	private void processUnsoldComoodities(InventorySettlement invSet) throws IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		List<InventorySettlementDetails> inventSettleDetList = getInventorySettlementService().getUnsoldCommodities(
				invSet.getBrokerCode(), invSet.getSettlementDate());
		doRenderList(inventSettleDetList);
		showAccounting();

	}

	private void doRenderList(List<InventorySettlementDetails> inventSettDetList) {
		logger.debug("Entering");

		this.listBoxCommodities.getItems().clear();

		BigDecimal totalfee = BigDecimal.ZERO;
		BigDecimal totalAmt = BigDecimal.ZERO;
		long brokerCustid = 0;
		String accNumer = "";

		if (inventSettDetList != null && !inventSettDetList.isEmpty()) {
			for (InventorySettlementDetails inventDetail : inventSettDetList) {
				String ccy = inventDetail.getCommodityCcy();
				int format = CurrencyUtil.getFormat(ccy);

				/*
				 * since fee on not sold for one million AED we will calculate fee for 1 AED item;
				 */
				BigDecimal fee = inventDetail.getFeeOnUnsold();
				fee = PennantApplicationUtil.formateAmount(fee, CurrencyUtil.getFormat(SysParamUtil.getAppCurrency()));
				fee = fee.divide(new BigDecimal(1000000));
				fee = CalculationUtil.getConvertedAmount(SysParamUtil.getAppCurrency(), ccy, fee);

				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(inventDetail.getHoldCertificateNo());
				lc.setParent(item);

				lc = new Listcell(inventDetail.getCommodityCode());
				lc.setParent(item);

				lc = new Listcell(String.valueOf(inventDetail.getQuantity()));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(inventDetail.getSaleQuantity()));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(inventDetail.getUnsoldQty()));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(inventDetail.getUnitPrice()));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(inventDetail.getCommodityCcy()));
				lc.setParent(item);

				BigDecimal unsoldQty = BigDecimal.valueOf(inventDetail.getUnsoldQty());
				BigDecimal unitPrice = inventDetail.getUnitPrice();
				BigDecimal unsoldamt = unsoldQty.multiply(unitPrice);

				BigDecimal unsoldFee = unsoldamt.multiply(fee);
				inventDetail.setUnsoldFee(unsoldFee);

				lc = new Listcell(PennantApplicationUtil.formatAmount(unsoldamt, format, false));
				lc.setParent(item);

				lc = new Listcell(PennantAppUtil.formatAmount(unsoldFee, format, false));
				lc.setParent(item);
				this.listBoxCommodities.appendChild(item);

				brokerCustid = inventDetail.getBrokerCustID();
				accNumer = inventDetail.getAccountNumber();

				totalfee = totalfee.add(CalculationUtil.getConvertedAmount(inventDetail.getCommodityCcy(),
						SysParamUtil.getAppCurrency(), unsoldFee));
				totalAmt = totalAmt.add(CalculationUtil.getConvertedAmount(inventDetail.getCommodityCcy(),
						SysParamUtil.getAppCurrency(), unsoldamt));

			}
			InventorySettlement settlement = getInventorySettlement();
			settlement.setAccountNumber(accNumer);
			settlement.setBrokerCustID(brokerCustid);
			settlement.setUnSoldFee(totalfee);
			settlement.setSettleAmt(totalAmt);

			getInventorySettlement().setInventSettleDetList(inventSettDetList);
		}

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aInventorySettlement
	 * @throws InterruptedException
	 */
	public void doShowDialog(InventorySettlement aInventorySettlement) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aInventorySettlement.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.brokerCode.focus();
		} else {
			this.brokerCode.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aInventorySettlement.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aInventorySettlement);
			// set ReadOnly mode accordingly if the object is new or not.

			this.listBoxCommodities.setHeight(getListBoxHeight(10));
			readOnlyComponent(true, this.settlementDate);

			setDialog(DialogType.EMBEDDED);
		} catch (final Exception e) {
			logger.error(e);
			MessageUtil.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	/*
	 * private void displayComponents(int mode){ logger.debug("Entering");
	 * 
	 * System.out.println(); doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes,
	 * isWorkFlowEnabled(),isFirstTask(), this.userAction,this.id,this.brokerCode));
	 * 
	 * if (getInventorySettlement().isNewRecord()){ }
	 * 
	 * logger.debug("Leaving"); }
	 */

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");

		this.brokerCode.setReadonly(true);
		this.settlementDate.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 * MSTGRP1_MAKER
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getInventorySettlement().isNewRecord()) {
			this.brokerCode.setReadonly(false);
			this.settlementDate.setReadonly(false);
			this.btnDelete.setVisible(false);
		} else {
			this.brokerCode.setReadonly(true);
			this.settlementDate.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (isFirstTask()) {
					this.btnDelete.setVisible(true);
				} else {
					this.btnDelete.setVisible(false);
				}
			}
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.inventorySettlement.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InventorySettlementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InventorySettlementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InventorySettlementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InventorySettlementDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.brokerCode.setMaxlength(8);
		this.brokerCode.setMandatoryStyle(true);
		this.brokerCode.setModuleName("CommodityBrokerDetail");
		this.brokerCode.setValueColumn("BrokerCode");
		this.brokerCode.setDescColumn("LovDescBrokerShortName");
		this.brokerCode.setValidateColumns(new String[] { "BrokerCode" });

		this.settlementDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.settlementDate.setValue(DateUtility.getAppDate());

		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aInventorySettlement
	 *            InventorySettlement
	 * @throws PFFInterfaceException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doWriteBeanToComponents(InventorySettlement aInventorySettlement) throws IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");
		this.brokerCode.setValue(aInventorySettlement.getBrokerCode());
		this.settlementDate.setValue(aInventorySettlement.getSettlementDate());

		if (aInventorySettlement.isNewRecord()) {
			this.brokerCode.setDescription("");
			this.settlementDate.setValue(DateUtility.getAppDate());
		} else {
			this.brokerCode.setDescription(aInventorySettlement.getBrokerCodeName());
			this.settlementDate.setValue(aInventorySettlement.getSettlementDate());
			processUnsoldComoodities(aInventorySettlement);
		}

		this.recordStatus.setValue(aInventorySettlement.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInventorySettlement
	 */
	public void doWriteComponentsToBean(InventorySettlement aInventorySettlement) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Broker
		try {
			aInventorySettlement.setBrokerCode(this.brokerCode.getValue());
			aInventorySettlement.setBrokerCodeName(this.brokerCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Settlement Date
		try {
			aInventorySettlement.setSettlementDate(this.settlementDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Broker
		if (!this.brokerCode.isReadonly()) {
			this.brokerCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InventorySettlementDialog_BrokerCode.value"), null, true, true));
		}
		// Settlement Date
		if (!this.settlementDate.isReadonly()) {
			this.settlementDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_InventorySettlementDialog_SettlementDate.value"), true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.brokerCode.setConstraint("");
		this.settlementDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.brokerCode.setErrorMessage("");
		this.settlementDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getInventorySettlementListCtrl().search();
	}

	/**
	 * Deletes a InventorySettlement object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final InventorySettlement aInventorySettlement = new InventorySettlement();
		BeanUtils.copyProperties(getInventorySettlement(), aInventorySettlement);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aInventorySettlement.getId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aInventorySettlement.getRecordType()).equals("")) {
				aInventorySettlement.setVersion(aInventorySettlement.getVersion() + 1);
				aInventorySettlement.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aInventorySettlement.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aInventorySettlement.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aInventorySettlement.getNextTaskId(),
							aInventorySettlement);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aInventorySettlement, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.debug("Exception: ", e);
				showErrorMessage(this.window_InventorySettlementDialog, e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.brokerCode.setValue("");
		this.settlementDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final InventorySettlement aInventorySettlement = new InventorySettlement();
		BeanUtils.copyProperties(getInventorySettlement(), aInventorySettlement);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aInventorySettlement.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aInventorySettlement.getNextTaskId(),
					aInventorySettlement);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aInventorySettlement.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the InventorySettlement object with the components data
			doWriteComponentsToBean(aInventorySettlement);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aInventorySettlement.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aInventorySettlement.getRecordType()).equals("")) {
				aInventorySettlement.setVersion(aInventorySettlement.getVersion() + 1);
				if (isNew) {
					aInventorySettlement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInventorySettlement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInventorySettlement.setNewRecord(true);
				}
			}
		} else {
			aInventorySettlement.setVersion(aInventorySettlement.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aInventorySettlement, tranType)) {
				// doWriteBeanToComponents(aInventorySettlement);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_InventorySettlementDialog, e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(InventorySettlement aInventorySettlement, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aInventorySettlement.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aInventorySettlement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInventorySettlement.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					try {
						if (!notesEntered) {
							MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
					}
				}
			}

			aInventorySettlement.setTaskId(getTaskId());
			aInventorySettlement.setNextTaskId(getNextTaskId());
			aInventorySettlement.setRoleCode(getRole());
			aInventorySettlement.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aInventorySettlement, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aInventorySettlement, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aInventorySettlement, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		InventorySettlement aInventorySettlement = (InventorySettlement) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getInventorySettlementService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getInventorySettlementService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getInventorySettlementService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aInventorySettlement.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getInventorySettlementService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aInventorySettlement.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InventorySettlementDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InventorySettlementDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("InventorySettlement", String.valueOf(aInventorySettlement.getId()),
										aInventorySettlement.getVersion()), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(InventorySettlement aInventorySettlement, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aInventorySettlement.getBefImage(), aInventorySettlement);
		return new AuditHeader(String.valueOf(aInventorySettlement.getId()), null, null, null, auditDetail,
				aInventorySettlement.getUserDetails(), getOverideMap());
	}

	private void showAccounting() throws IllegalAccessException, InvocationTargetException, PFFInterfaceException {
		this.tabPosting.setVisible(true);
		InventorySettlement inventorySettlemen = getInventorySettlement();

		if (enqiryModule) {
			doFillAccounting(getPostings(inventorySettlemen));
		} else {
			List<ReturnDataSet> list = getPostingsPreparationUtil().prepareAccountingDataSet(inventorySettlemen,
					AccountEventConstants.ACCEVENT_CMTINV_SET, "N");
			doFillAccounting(list);
		}

	}

	private List<ReturnDataSet> getPostings(InventorySettlement aCommodityInventory) {

		List<ReturnDataSet> postingAccount = new ArrayList<ReturnDataSet>();
		JdbcSearchObject<ReturnDataSet> searchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addTabelName("Postings_view");
		searchObject.addFilterEqual("finreference", aCommodityInventory.getBrokerCode());
		searchObject.addFilterEqual("finEvent", AccountEventConstants.ACCEVENT_CMTINV_SET);
		List<ReturnDataSet> postings = pagedListService.getBySearchObject(searchObject);
		if (postings != null && !postings.isEmpty()) {
			return postings;
		}

		logger.debug("Leaving");
		return postingAccount;
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 * 
	 * @param accountingSetEntries
	 *            (List)
	 * 
	 */
	public void doFillAccounting(List<ReturnDataSet> accountingSetEntries) {
		logger.debug("Entering");

		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;

				ReturnDataSet entry = accountingSetEntries.get(i);

				// Highlighting Failed Posting Details
				String sClassStyle = "";
				if (StringUtils.isNotBlank(entry.getErrorId())
						&& !"0000".equals(StringUtils.trimToEmpty(entry.getErrorId()))) {
					sClassStyle = "color:#FF0000;";
				}

				Hbox hbox = new Hbox();
				Label label = new Label(PennantAppUtil.getlabelDesc(entry.getDrOrCr(),
						PennantStaticListUtil.getTranType()));
				label.setStyle(sClassStyle);
				hbox.appendChild(label);
				if (StringUtils.isNotBlank(entry.getPostStatus())) {
					Label la = new Label("*");
					la.setStyle("color:red;");
					hbox.appendChild(la);
				}
				lc = new Listcell();
				lc.setStyle(sClassStyle);
				lc.appendChild(hbox);
				lc.setParent(item);
				lc = new Listcell(entry.getTranDesc());
				lc.setStyle(sClassStyle);
				lc.setParent(item);
				if (entry.isShadowPosting()) {
					lc = new Listcell("Shadow");
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell("Shadow");
					lc.setStyle(sClassStyle);
					lc.setParent(item);
				} else {
					lc = new Listcell(entry.getTranCode());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell(entry.getRevTranCode());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
				}
				lc = new Listcell(entry.getAccountType());
				lc.setStyle(sClassStyle);
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
				lc.setStyle("font-weight:bold;");
				lc.setStyle(sClassStyle);
				lc.setParent(item);

				lc = new Listcell(entry.getAcCcy());
				lc.setParent(item);

				BigDecimal amt = entry.getPostAmount() != null ? entry.getPostAmount() : BigDecimal.ZERO;
				lc = new Listcell(PennantApplicationUtil.amountFormate(amt, entry.getFormatter()));

				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setStyle(sClassStyle + "font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? ""
						: StringUtils.trimToEmpty(entry.getErrorId()));
				lc.setStyle("font-weight:bold;color:red;");
				lc.setTooltiptext(entry.getErrorMsg());
				lc.setParent(item);

				this.listBoxFinAccountings.appendChild(item);
			}

		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public InventorySettlement getInventorySettlement() {
		return this.inventorySettlement;
	}

	public void setInventorySettlement(InventorySettlement inventorySettlement) {
		this.inventorySettlement = inventorySettlement;
	}

	public void setInventorySettlementService(InventorySettlementService inventorySettlementService) {
		this.inventorySettlementService = inventorySettlementService;
	}

	public InventorySettlementService getInventorySettlementService() {
		return this.inventorySettlementService;
	}

	public void setInventorySettlementListCtrl(InventorySettlementListCtrl inventorySettlementListCtrl) {
		this.inventorySettlementListCtrl = inventorySettlementListCtrl;
	}

	public InventorySettlementListCtrl getInventorySettlementListCtrl() {
		return this.inventorySettlementListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

}
