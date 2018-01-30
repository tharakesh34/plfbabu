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
 * FileName    		:  SukukBrokerBondsDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-06-2015    														*
 *                                                                  						*
 * Modified Date    :  09-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-06-2015       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmasters.sukukbrokerbonds;

import java.math.BigDecimal;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmasters.SukukBrokerBonds;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.applicationmasters.sukukbroker.SukukBrokerDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;

/**
 * This is the controller class for the /WEB-INF/pages/Application
 * Masters/SukukBrokerBonds/sukukBrokerBondsDialog.zul file.
 */
public class SukukBrokerBondsDialogCtrl extends GFCBaseCtrl<SukukBrokerBonds> {
	private static final long			serialVersionUID		= 1L;
	private static final Logger			logger					= Logger.getLogger(SukukBrokerBondsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SukukBrokerBondsDialog;
	protected Row row0;
	protected Label label_BondCode;

	protected Textbox brokerCode;
	protected ExtendedCombobox bondCode;
	protected Label label_PaymentMode;
	protected Hlayout hlayout_PaymentMode;
	protected Space space_PaymentMode;

	protected Combobox paymentMode;
	protected Row row1;
	protected Label label_IssuerAccount;
	protected Hlayout hlayout_IssuerAccount;
	protected Space space_IssuerAccount;

	protected Textbox issuerAccount;

	protected Combobox commissionType;
	protected Label label_Commission;
	protected Hlayout hlayout_Commission;
	protected Space space_Commission;

	protected Decimalbox commission;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;

	// not auto wired vars
	private SukukBrokerBonds sukukBrokerBonds; // overhanded

	
	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private List<ValueLabel> listPaymentMode = PennantStaticListUtil
			.getPaymentModes();
	private List<ValueLabel> listCommissionType = PennantStaticListUtil
			.getCommissionType();

	public transient int ccyFormatter = 0;
	public transient int percnetageFormatter = 2;

	private SukukBrokerDialogCtrl sukukBrokerDialogCtrl;

	public SukukBrokerDialogCtrl getSukukBrokerDialogCtrl() {
		return sukukBrokerDialogCtrl;
	}

	public void setSukukBrokerDialogCtrl(SukukBrokerDialogCtrl sukukBrokerDialogCtrl) {
		this.sukukBrokerDialogCtrl = sukukBrokerDialogCtrl;
	}

	/**
	 * default constructor.<br>
	 */
	public SukukBrokerBondsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SukukBrokerBondsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected SukukBrokerBonds
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SukukBrokerBondsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SukukBrokerBondsDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("sukukBrokerBonds")) {
				this.sukukBrokerBonds = (SukukBrokerBonds) arguments.get("sukukBrokerBonds");
				SukukBrokerBonds befImage = new SukukBrokerBonds();
				BeanUtils.copyProperties(this.sukukBrokerBonds, befImage);
				this.sukukBrokerBonds.setBefImage(befImage);

				setSukukBrokerBonds(this.sukukBrokerBonds);
			} else {
				setSukukBrokerBonds(null);
			}

			this.sukukBrokerBonds.setWorkflowId(0);

			doLoadWorkFlow(this.sukukBrokerBonds.isWorkflow(), this.sukukBrokerBonds.getWorkflowId(), this.sukukBrokerBonds.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "SukukBrokerBondsDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("role")) {
				getUserWorkspace().allocateRoleAuthorities(arguments.get("role").toString(), "SukukBrokerBondsDialog");
			}

			// READ OVERHANDED params !
			// we get the sukukBrokerBondsListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete sukukBrokerBonds here.
			if (arguments.containsKey("sukukBrokerDialogCtrl")) {
				setSukukBrokerDialogCtrl((SukukBrokerDialogCtrl) arguments.get("sukukBrokerDialogCtrl"));
			} else {
				setSukukBrokerDialogCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSukukBrokerBonds());
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		displayComponents(ScreenCTL.SCRN_GNEDT);
		this.btnCancel.setVisible(true);
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
		doWriteBeanToComponents(this.sukukBrokerBonds.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_SukukBrokerBondsDialog);
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

			ScreenCTL.displayNotes(getNotes("SukukBrokerBonds", getSukukBrokerBonds().getBrokerCode(), getSukukBrokerBonds().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSukukBrokerBonds
	 * @throws InterruptedException
	 */
	public void doShowDialog(SukukBrokerBonds aSukukBrokerBonds) throws InterruptedException {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doWriteBeanToComponents(aSukukBrokerBonds);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aSukukBrokerBonds.isNewRecord()));

			setDialog(DialogType.EMBEDDED);
			this.window_SukukBrokerBondsDialog.doModal() ;

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(), this.userAction, this.bondCode, this.bondCode));

		if (getSukukBrokerBonds().isNewRecord()) {
			readOnlyComponent(false, this.bondCode);
		} else {
			readOnlyComponent(true, this.bondCode);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");
		boolean tempReadOnly = readOnly;
		if (readOnly){
			tempReadOnly = true;
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(this.sukukBrokerBonds.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("SukukBrokerBondsDialog_PaymentMode", tempReadOnly, this.paymentMode, this.space_PaymentMode, this.label_PaymentMode, this.hlayout_PaymentMode, null);
		setComponentAccessType("SukukBrokerBondsDialog_IssuerAccount", tempReadOnly, this.issuerAccount, this.space_IssuerAccount, this.label_IssuerAccount, this.hlayout_IssuerAccount, null);
		setComponentAccessType("SukukBrokerBondsDialog_CommissionType", tempReadOnly, this.commissionType, this.space_Commission, this.label_Commission, this.hlayout_Commission, null);
		setRowInvisible(this.row1, this.hlayout_IssuerAccount, null);
		setComponentAccessType("SukukBrokerBondsDialog_Commission", tempReadOnly, this.commission, null, this.label_Commission, this.hlayout_Commission, null);
		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("SukukBrokerBondsDialog",arguments.get("role").toString());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerBondsDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerBondsDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerBondsDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SukukBrokerBondsDialog_btnSave"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bondCode.setMaxlength(20);
		this.bondCode.setMandatoryStyle(true);
		this.bondCode.setTextBoxWidth(138);
		this.bondCode.setModuleName("SukukBond");
		this.bondCode.setValueColumn("BondCode");
		this.bondCode.setDescColumn("BondDesc");
		this.bondCode.setValidateColumns(new String[] { "BondCode" });

		this.issuerAccount.setMaxlength(20);
		this.commission.setMaxlength(18);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSukukBrokerBonds
	 *            SukukBrokerBonds
	 */
	public void doWriteBeanToComponents(SukukBrokerBonds aSukukBrokerBonds) {
		logger.debug("Entering");
		this.bondCode.setValue(aSukukBrokerBonds.getBondCode(),aSukukBrokerBonds.getBondDesc());
		this.brokerCode.setValue(aSukukBrokerBonds.getBrokerCode());
		fillComboBox(this.paymentMode, aSukukBrokerBonds.getPaymentMode(), listPaymentMode);
		this.issuerAccount.setValue(aSukukBrokerBonds.getIssuerAccount());
		fillComboBox(this.commissionType, aSukukBrokerBonds.getCommissionType(), listCommissionType);

		if (StringUtils.trimToEmpty(aSukukBrokerBonds.getCommissionType()).equals(PennantConstants.COMMISSION_TYPE_FLAT)) {
			this.commission.setValue(PennantAppUtil.formateAmount(aSukukBrokerBonds.getCommission(), ccyFormatter));
		} else if (StringUtils.trimToEmpty(aSukukBrokerBonds.getCommissionType()).equals(PennantConstants.COMMISSION_TYPE_PERCENTAGE)) {
			this.commission.setValue(PennantAppUtil.formateAmount(aSukukBrokerBonds.getCommission(), percnetageFormatter));
		}
		doSetCommissionproperties();
		this.recordStatus.setValue(aSukukBrokerBonds.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aSukukBrokerBonds.getRecordType()));
		logger.debug("Leaving");
	}

	public void onChange$commissionType() {
		doSetCommissionproperties();
	}

	public void doSetCommissionproperties() {

		this.commission.setMaxlength(20);
		String commissionType = this.commissionType.getSelectedItem().getValue().toString();
		if (PennantConstants.COMMISSION_TYPE_FLAT.equals(commissionType)) {
			this.commission.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
			this.commission.setScale(ccyFormatter);
		} else if (PennantConstants.COMMISSION_TYPE_PERCENTAGE.equals(commissionType)) {
			this.commission.setFormat(PennantApplicationUtil.getAmountFormate(percnetageFormatter));
			this.commission.setScale(percnetageFormatter);
		}

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSukukBrokerBonds
	 */
	public void doWriteComponentsToBean(SukukBrokerBonds aSukukBrokerBonds) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Bond Code
		try {
			aSukukBrokerBonds.setBondCode(this.bondCode.getValue());
			aSukukBrokerBonds.setBrokerCode(this.brokerCode.getValue());
			aSukukBrokerBonds.setBondDesc(this.bondCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Payment Mode
		try {
			String strPaymentMode = null;
			if (this.paymentMode.getSelectedItem() != null) {
				strPaymentMode = this.paymentMode.getSelectedItem().getValue().toString();
			}
			if (strPaymentMode != null && !PennantConstants.List_Select.equals(strPaymentMode)) {
				aSukukBrokerBonds.setPaymentMode(strPaymentMode);
			} else {
				aSukukBrokerBonds.setPaymentMode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Issuer Account
		try {
			aSukukBrokerBonds.setIssuerAccount(this.issuerAccount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Commission Type
		try {
			String strCommissionType = null;
			if (this.commissionType.getSelectedItem() != null) {
				strCommissionType = this.commissionType.getSelectedItem().getValue().toString();
			}
			if (strCommissionType != null && !PennantConstants.List_Select.equals(strCommissionType)) {
				aSukukBrokerBonds.setCommissionType(strCommissionType);
			} else {
				aSukukBrokerBonds.setCommissionType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Commission
		try {
			this.commission.setConstraint("");

			if (this.commission.getValue() == null) {
				throw new WrongValueException(this.commission, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_BrokerBondsDialog_Commission.value") }));
			}

			if (this.commission.getValue().compareTo(BigDecimal.ZERO) != 0) {

				if (PennantConstants.COMMISSION_TYPE_FLAT.equals(this.commissionType.getSelectedItem().getValue().toString())) {

					this.commission.setConstraint(new PTDecimalValidator(Labels.getLabel("label_BrokerBondsDialog_Commission.value"), ccyFormatter, true, false));

					aSukukBrokerBonds.setCommission(PennantAppUtil.unFormateAmount(this.commission.getValue(), ccyFormatter));

				} else if (PennantConstants.COMMISSION_TYPE_PERCENTAGE.equals(this.commissionType.getSelectedItem().getValue().toString())) {

					this.commission.setConstraint(new PTDecimalValidator(Labels.getLabel("label_BrokerBondsDialog_Commission.value"), percnetageFormatter, true, false, 0, 100D));

					aSukukBrokerBonds.setCommission(PennantAppUtil.unFormateAmount(this.commission.getValue(), percnetageFormatter));
				}
			} else {
				aSukukBrokerBonds.setCommission(BigDecimal.ZERO);
			}
			
			
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
		// Bond Code
		if (!this.bondCode.isReadonly()) {
			this.bondCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BrokerBondsDialog_BondCode.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		// Payment Mode
		// if (!this.paymentMode.isReadonly()){
		// this.paymentMode.setConstraint(new
		// StaticListValidator(listPaymentMode,Labels.getLabel("label_BrokerBondsDialog_PaymentMode.value"),true));
		// }
		// Issuer Account
		if (!this.issuerAccount.isReadonly()) {
			this.issuerAccount.setConstraint(new PTStringValidator(Labels.getLabel("label_BrokerBondsDialog_IssuerAccount.value"), PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		// Commission Type
		// if (!this.commissionType.isReadonly()){
		// this.commissionType.setConstraint(new
		// StaticListValidator(listCommissionType,Labels.getLabel("label_BrokerBondsDialog_CommissionType.value"),true));
		// }
		// Commission
		if (!this.commission.isReadonly()) {
			this.commission.setConstraint(new PTDecimalValidator(Labels.getLabel("label_BrokerBondsDialog_Commission.value"), 0, true, false, 0));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.bondCode.setConstraint("");
		this.paymentMode.setConstraint("");
		this.issuerAccount.setConstraint("");
		this.commissionType.setConstraint("");
		this.commission.setConstraint("");
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
		this.bondCode.setErrorMessage("");
		this.paymentMode.setErrorMessage("");
		this.issuerAccount.setErrorMessage("");
		this.commissionType.setErrorMessage("");
		this.commission.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a SukukBrokerBonds object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final SukukBrokerBonds aSukukBrokerBonds = new SukukBrokerBonds();
		BeanUtils.copyProperties(getSukukBrokerBonds(), aSukukBrokerBonds);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_BrokerBondsDialog_BondCode.value")+" : "+aSukukBrokerBonds.getBondCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aSukukBrokerBonds.getRecordType())) {
				aSukukBrokerBonds.setVersion(aSukukBrokerBonds.getVersion() + 1);
				aSukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSukukBrokerBonds.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aSukukBrokerBonds.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aSukukBrokerBonds.setVersion(aSukukBrokerBonds.getVersion() + 1);
				aSukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				tranType = PennantConstants.TRAN_DEL;
				List<SukukBrokerBonds> list = processbrokerbond(aSukukBrokerBonds, tranType);
				if (list!=null) {
					getSukukBrokerDialogCtrl().doFilllistbox(list);
					window_SukukBrokerBondsDialog.onClose();
					getSukukBrokerDialogCtrl().window_SukukBrokerDialog.setVisible(true);
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);

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

		this.bondCode.setValue("");
		this.paymentMode.setSelectedIndex(0);
		this.issuerAccount.setValue("");
		this.commissionType.setSelectedIndex(0);
		this.commission.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SukukBrokerBonds aSukukBrokerBonds = new SukukBrokerBonds();
		BeanUtils.copyProperties(getSukukBrokerBonds(), aSukukBrokerBonds);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aSukukBrokerBonds.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSukukBrokerBonds.getNextTaskId(), aSukukBrokerBonds);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aSukukBrokerBonds.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the SukukBrokerBonds object with the components data
			doWriteComponentsToBean(aSukukBrokerBonds);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aSukukBrokerBonds.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSukukBrokerBonds.getRecordType())) {
				aSukukBrokerBonds.setVersion(aSukukBrokerBonds.getVersion() + 1);
				if (isNew) {
					aSukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSukukBrokerBonds.setNewRecord(true);
				}
			}
		} else {

			if (isNew) {
				aSukukBrokerBonds.setVersion(1);
				aSukukBrokerBonds.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aSukukBrokerBonds.getRecordType())) {
				aSukukBrokerBonds.setVersion(aSukukBrokerBonds.getVersion() + 1);
				aSukukBrokerBonds.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aSukukBrokerBonds.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aSukukBrokerBonds.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			List<SukukBrokerBonds> list = processbrokerbond(aSukukBrokerBonds, tranType);
			if (list!=null) {
				getSukukBrokerDialogCtrl().doFilllistbox(list);
				window_SukukBrokerBondsDialog.onClose();
				getSukukBrokerDialogCtrl().window_SukukBrokerDialog.setVisible(true);
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private List<SukukBrokerBonds> processbrokerbond(SukukBrokerBonds aSukukBrokerBonds, String tranType) throws InterruptedException {

		boolean recordAdded = false;
		List<SukukBrokerBonds>	sukukBrokerBondsList = new ArrayList<SukukBrokerBonds>();

		String[] errParm = new String[1];
		errParm[0] = PennantJavaUtil.getLabel("label_BondCode") + ":" + aSukukBrokerBonds.getBondCode();
		
		List<SukukBrokerBonds> list = getSukukBrokerDialogCtrl().getSukukBrokerBondsList();
		
		for (SukukBrokerBonds sukukBrokerBonds : list) {

			String recordType=aSukukBrokerBonds.getRecordType();
			
			if (sukukBrokerBonds.getBondCode().equals(aSukukBrokerBonds.getBondCode()) && 
					sukukBrokerBonds.getBrokerCode().equals(aSukukBrokerBonds.getBrokerCode())) {
				// Both Current and Existing list rating same
				if (aSukukBrokerBonds.isNew()) {
					ErrorDetail details=ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, null), getUserWorkspace().getUserLanguage());
					MessageUtil.showError(details.getError());
					return null;
				}

				if (PennantConstants.TRAN_DEL.equals(tranType)) {
					if (recordType.equals(PennantConstants.RECORD_TYPE_UPD)) {
						aSukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						recordAdded = true;
						sukukBrokerBondsList.add(aSukukBrokerBonds);
					} else if (recordType.equals(PennantConstants.RCD_ADD)) {
						recordAdded = true;
					} else if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {
						aSukukBrokerBonds.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						recordAdded = true;
						sukukBrokerBondsList.add(aSukukBrokerBonds);
					} else if (recordType.equals(PennantConstants.RECORD_TYPE_CAN)) {
						recordAdded = true;
						
						List<SukukBrokerBonds> prvList = getSukukBrokerDialogCtrl().getSukukBroker().getSukukBrokerBonds();
						for (SukukBrokerBonds sbb : prvList) {
							if (sbb.getBondCode() == aSukukBrokerBonds.getBondCode() && 
									sbb.getBrokerCode().equals(aSukukBrokerBonds.getBrokerCode())) {
								sukukBrokerBondsList.add(sbb);
							}
						}
					} else if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
						aSukukBrokerBonds.setNewRecord(true);
					}
				} else {
					if (!PennantConstants.TRAN_UPD.equals(tranType)) {
						sukukBrokerBondsList.add(sukukBrokerBonds);
					}
				}
			} else {
				sukukBrokerBondsList.add(sukukBrokerBonds);
			}
		
		}
		
		if (!recordAdded) {
			sukukBrokerBondsList.add(aSukukBrokerBonds);
		}

		logger.debug("Leaving");
		return sukukBrokerBondsList;
	}

	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);

			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
			if (combobox.getSelectedIndex() == -1) {
				combobox.setSelectedIndex(0);
			}

		}

		logger.debug("Leaving fillComboBox()");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SukukBrokerBonds getSukukBrokerBonds() {
		return this.sukukBrokerBonds;
	}

	public void setSukukBrokerBonds(SukukBrokerBonds sukukBrokerBonds) {
		this.sukukBrokerBonds = sukukBrokerBonds;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
