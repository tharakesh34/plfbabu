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
 * FileName    		:  CommitmentRateDialogCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-12-2016    														*
 *                                                                  						*
 * Modified Date    :  22-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.commitment.commitmentrate;

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
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.CommitmentRate;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.commitment.commitment.CommitmentDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/commitment/CommitmentRate/commitmentRateDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CommitmentRateDialogCtrl extends GFCBaseCtrl<CommitmentRate> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CommitmentRateDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */

	protected Window 						window_CommitmentRateDialog; 

	protected Row 							row_CmtReference; 
	protected Label 						label_CmtReference;
	protected Hbox 							hlayout_CmtReference;
	protected Space 						space_CmtReference; 
	protected Textbox 						cmtReference; 
	protected Label 						label_CmtRvwFrq;
	protected FrequencyBox  				cmtRvwFrq; 
	protected Label 						label_CmtBaseRate;
	protected RateBox 						cmtBaseRate;
	protected Label 						label_CmtActualRate;
	protected Hbox 							hlayout_CmtActualRate;
	protected Space 						space_CmtActualRate; 
	protected Decimalbox 					cmtActualRate; 
	protected Label 						label_CmtCalculatedRate;
	protected Hbox 							hlayout_CmtCalculatedRate;
	protected Space 						space_CmtCalculatedRate; 
	protected Decimalbox 					cmtCalculatedRate; 
	protected Label 						recordType;	 
	protected Groupbox 						gb_statusDetails;

	private CommitmentRate 					commitmentRate; 
 
	private boolean 						newRecord 	 	 		= false;
	private boolean 						newCommitment 	 		= false;
	private String 							userRole 		 		= "";
	private List<CommitmentRate> 			commitmentRateDetailList;
	private CommitmentDialogCtrl 			commitmentDialogCtrl;


	/**
	 * default constructor.<br>
	 */
	public CommitmentRateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommitmentRateDialog";
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommitmentRateDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CommitmentRateDialog);

		try {

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (Boolean) arguments.get("enqiryModule");
			} else {
				enqiryModule = false;
			}

			if (arguments.containsKey("commitmentRate")) {
				this.commitmentRate = (CommitmentRate) arguments.get("commitmentRate");
				CommitmentRate befImage = new CommitmentRate();
				BeanUtils.copyProperties(this.commitmentRate, befImage);
				this.commitmentRate.setBefImage(befImage);
				setCommitmentRate(this.commitmentRate);
			} else {
				setCommitmentRate(null);
			}
			if (getCommitmentRate().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("commitmentDialogCtrl")) {
				setCommitmentDialogCtrl((CommitmentDialogCtrl) arguments.get("commitmentDialogCtrl"));
				setNewCommitment(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.commitmentRate.setWorkflowId(0);

				if (arguments.containsKey("roleCode") && !enqiryModule) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CommitmentRateDialog");
				}
			}

			doLoadWorkFlow(this.commitmentRate.isWorkflow(), this.commitmentRate.getWorkflowId(), this.commitmentRate.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CommitmentRateDialog");
			}  

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCommitmentRate());

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
		logger.debug("Entering" +event.toString());
		doEdit();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());

		doWriteBeanToComponents(this.commitmentRate.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		MessageUtil.showHelpWindow(event, window_CommitmentRateDialog);

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
		doShowNotes(this.commitmentRate);
	}


	/**
	 * when clicks on button "Commitment BaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$cmtBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		ForwardEvent forwardEvent = (ForwardEvent)event;
		String rateType = (String) forwardEvent.getOrigin().getData();

		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.cmtCalculatedRate.setConstraint("");
			Object dataObject = cmtBaseRate.getBaseObject();

			doRemoveValidation();
			doClearMessage();

			if (dataObject instanceof String) {
				this.cmtBaseRate.setBaseValue(dataObject.toString());
				this.cmtBaseRate.setBaseDescription("");
				this.cmtCalculatedRate.setValue(BigDecimal.ZERO);

				this.cmtBaseRate.setMarginValue(BigDecimal.ZERO);
				this.cmtBaseRate.setMarginReadonly(true);

			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.cmtBaseRate.setBaseValue(details.getBRType());
					this.cmtBaseRate.setBaseDescription(details.getBRTypeDesc());

					this.cmtBaseRate.setMarginReadonly(false);
				}
			}

			if (StringUtils.isNotBlank(this.cmtBaseRate.getBaseValue())) {
				calculateRate(this.cmtBaseRate.getBaseValue(), getCommitmentRate().getCmtCcy(), this.cmtBaseRate.getSpecialComp(), this.cmtBaseRate.getBaseComp(), 
						this.cmtBaseRate.getMarginValue(), this.cmtCalculatedRate, getCommitmentRate().getCmtPftRateMin(), getCommitmentRate().getCmtPftRateMax());
			} else {
				this.cmtCalculatedRate.setValue(this.cmtActualRate.getValue());
			}

		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.cmtCalculatedRate.setConstraint("");
			Object dataObject = cmtBaseRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.cmtBaseRate.setSpecialValue(dataObject.toString());
				this.cmtBaseRate.setSpecialDescription("");
				this.cmtCalculatedRate.setValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.cmtBaseRate.setSpecialValue(details.getSRType());
					this.cmtBaseRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			if (StringUtils.isNotBlank(this.cmtBaseRate.getSpecialValue())) {
				calculateRate(this.cmtBaseRate.getBaseValue(), getCommitmentRate().getCmtCcy(), this.cmtBaseRate.getSpecialComp(), this.cmtBaseRate.getBaseComp(),
						this.cmtBaseRate.getMarginValue(), this.cmtCalculatedRate, getCommitmentRate().getCmtPftRateMin(), getCommitmentRate().getCmtPftRateMax());
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if(this.cmtBaseRate.getMarginValue() != null) {
				this.cmtCalculatedRate.setValue(PennantApplicationUtil.formatRate((
						this.cmtCalculatedRate.getValue().add(this.cmtBaseRate.getMarginValue())).doubleValue(),2));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 * **/
	private void calculateRate(String rate, String currency, ExtendedCombobox splRate,ExtendedCombobox lovFieldTextBox, 
			BigDecimal margin, Decimalbox effectiveRate, BigDecimal minAllowedRate, BigDecimal maxAllowedRate) throws InterruptedException {
		logger.debug("Entering");

		RateDetail rateDetail = RateUtil.rates(rate, currency, splRate.getValue(), margin, minAllowedRate, maxAllowedRate);

		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setDescription("");
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Changing cmtActualRate
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$cmtActualRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if(this.cmtActualRate.getValue() == null){
			this.cmtActualRate.setValue(BigDecimal.ZERO);
		}

		if (StringUtils.isBlank(this.cmtBaseRate.getBaseValue())) {
			this.cmtCalculatedRate.setValue(this.cmtActualRate.getValue());
		}
	}

	// GUI operations

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		//Empty sent any required attributes
		this.cmtReference.setMaxlength(20);
		this.cmtRvwFrq.setMandatoryStyle(true);

		this.cmtBaseRate.setBaseProperties("BaseRateCode","BRType","BRTypeDesc");
		this.cmtBaseRate.setSpecialProperties("SplRateCode","SRType","SRTypeDesc");

		this.cmtActualRate.setMaxlength(13);
		this.cmtActualRate.setFormat(PennantConstants.rateFormate9);
		this.cmtActualRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtActualRate.setScale(9);

		this.cmtCalculatedRate.setMaxlength(13);
		this.cmtCalculatedRate.setFormat(PennantConstants.rateFormate9);
		this.cmtCalculatedRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.cmtCalculatedRate.setScale(9);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommitmentRate
	 * @throws InterruptedException
	 */
	public void doShowDialog(CommitmentRate aCommitmentRate) throws InterruptedException {
		logger.debug("Entering") ;

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cmtRvwFrq.focus();
		} else {
			this.cmtBaseRate.focus();
			if (isNewCommitment()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCommitmentRate);

			if (isNewCommitment()) {
				this.window_CommitmentRateDialog.setHeight("30%");
				this.window_CommitmentRateDialog.setWidth("70%");
				this.groupboxWf.setVisible(false);
				this.window_CommitmentRateDialog.doModal();
			} else {
				this.window_CommitmentRateDialog.setWidth("100%");
				this.window_CommitmentRateDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCancel.setVisible(false);

			this.cmtRvwFrq.setDisabled(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtRvwFrq"));
			this.cmtRvwFrq.setMandatoryStyle(!getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtRvwFrq"));
		} else {
			this.btnCancel.setVisible(true);
			this.cmtRvwFrq.setDisabled(true);
		}

		this.cmtReference.setReadonly(true);
		this.cmtCalculatedRate.setReadonly(true);

		this.cmtBaseRate.setBaseReadonly(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtBaseRate"));
		readOnlyComponent(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtMargin"), this.cmtBaseRate.getMarginComp());
		readOnlyComponent(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtActualRate"), this.cmtActualRate);

		//readOnlyComponent(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtReference"), this.cmtReference);
		//this.cmtBaseRate.setSpecialReadonly(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtSpecialRate"));
		//readOnlyComponent(getUserWorkspace().isReadOnly("CommitmentRateDialog_CmtCalculatedRate"), this.cmtCalculatedRate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.commitmentRate.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCommitment) {
				if (enqiryModule) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCommitment);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	private void doReadOnly() {
		logger.debug("Entering");

		this.cmtReference.setReadonly(true);
		this.cmtRvwFrq.setDisabled(true);

		this.cmtBaseRate.setBaseReadonly(true);
		this.cmtBaseRate.setSpecialReadonly(true);
		this.cmtBaseRate.setMarginReadonly(true);

		this.cmtActualRate.setReadonly(true);
		this.cmtCalculatedRate.setReadonly(true);

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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;

		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities("CommitmentRateDialog", userRole);

			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommitmentRateDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommitmentRateDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommitmentRateDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommitmentRateDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCommitmentRate
	 *            CommitmentRate
	 */
	public void doWriteBeanToComponents(CommitmentRate aCommitmentRate) {
		logger.debug("Entering") ;

		this.cmtReference.setValue(aCommitmentRate.getCmtReference());
		this.cmtRvwFrq.setValue(aCommitmentRate.getCmtRvwFrq());

		// Base Rate
		this.cmtBaseRate.setBaseValue(aCommitmentRate.getCmtBaseRate());
		this.cmtBaseRate.setBaseDescription(aCommitmentRate.getCmtBaseRate() == null ? "" : aCommitmentRate.getCmtBaseRateName());

		//Special Rate
		//this.cmtBaseRate.setSpecialValue(aCommitmentRate.getCmtSpecialRate());
		//this.cmtBaseRate.setSpecialDescription(aCommitmentRate.getCmtSpecialRate() == null ? "" : aCommitmentRate.getCmtSpecialRateName());

		//Margin Rate
		if (StringUtils.isBlank(aCommitmentRate.getCmtBaseRate())) {
			this.cmtBaseRate.setMarginReadonly(true);
			this.cmtBaseRate.setMarginValue(BigDecimal.ZERO);
		} else {
			this.cmtBaseRate.setMarginValue(aCommitmentRate.getCmtMargin());
		}

		this.cmtActualRate.setValue(aCommitmentRate.getCmtActualRate());
		this.cmtCalculatedRate.setValue(aCommitmentRate.getCmtCalculatedRate());

		//this.recordStatus.setValue(aCommitmentRate.getRecordStatus());
		//this.recordType.setValue(PennantJavaUtil.getLabel(aCommitmentRate.getRecordType()));

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommitmentRate
	 */
	public void doWriteComponentsToBean(CommitmentRate aCommitmentRate) {
		logger.debug("Entering") ;

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Commitment Reference
		try {
			aCommitmentRate.setCmtReference(this.cmtReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		// Review Frequency
		try {
			if (this.cmtRvwFrq.isValidComboValue()) {
				aCommitmentRate.setCmtRvwFrq(this.cmtRvwFrq.getValue() == null ? "" : this.cmtRvwFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Base Rate
		try {
			// Field is foreign key and not a mandatory value so it should, be either null or non empty
			if (StringUtils.isBlank(this.cmtBaseRate.getBaseValue())) {
				aCommitmentRate.setCmtBaseRateName("");
				aCommitmentRate.setCmtBaseRate(null);
			} else {
				aCommitmentRate.setCmtBaseRateName(this.cmtBaseRate.getBaseDescription());
				aCommitmentRate.setCmtBaseRate(this.cmtBaseRate.getBaseValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Special Rate
		try {
			// Field is foreign key and not a mandatory value so it should, be either null or non empty
			if (StringUtils.isBlank(this.cmtBaseRate.getSpecialValue())) {
				aCommitmentRate.setCmtSpecialRateName("");
				aCommitmentRate.setCmtSpecialRate(null);
			} else {
				aCommitmentRate.setCmtSpecialRateName(this.cmtBaseRate.getSpecialDescription());
				aCommitmentRate.setCmtSpecialRate(this.cmtBaseRate.getSpecialValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		} 

		//Fields (Base Rate, Margin) and Actual Rate are mutually Exclusive Validation
		try {
			if (this.cmtActualRate.getValue() != null && !this.cmtActualRate.isReadonly()) {
				if ((this.cmtActualRate.getValue().intValue() > 0) && (StringUtils.isNotBlank(this.cmtBaseRate.getBaseValue()))) {

					throw new WrongValueException(this.cmtActualRate, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_CommitmentRateDialog_CmtBaseRate.value"), Labels.getLabel("label_CommitmentRateDialog_CmtActualRate.value") }));
				}
			} 
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Calculated Rate
		try {
			if(!this.cmtBaseRate.isBaseReadonly() && StringUtils.isNotBlank(this.cmtBaseRate.getBaseValue())) {
				calculateRate(this.cmtBaseRate.getBaseValue(), getCommitmentRate().getCmtCcy(), this.cmtBaseRate.getSpecialComp(),
						this.cmtBaseRate.getBaseComp(), this.cmtBaseRate.getMarginValue(), this.cmtCalculatedRate, getCommitmentRate().getCmtPftRateMin(), getCommitmentRate().getCmtPftRateMax());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// Margin Rate 
		try {
			if (StringUtils.isNotBlank(this.cmtBaseRate.getBaseValue())) {
				aCommitmentRate.setCmtMargin(this.cmtBaseRate.getMarginValue());
			} else {
				aCommitmentRate.setCmtMargin(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Actual Rate
		try {
			aCommitmentRate.setCmtActualRate(this.cmtActualRate.getValue() == null ? BigDecimal.ZERO : this.cmtActualRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		//Calculated Rate
		try {
			if (StringUtils.isBlank(this.cmtBaseRate.getBaseValue())) {
				aCommitmentRate.setCmtCalculatedRate(this.cmtActualRate.getValue());
			} else {
				aCommitmentRate.setCmtCalculatedRate(this.cmtCalculatedRate.getValue());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
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

		//Base Rate //Margin
		if(!this.cmtBaseRate.isBaseReadonly()) {
			this.cmtBaseRate.setBaseConstraint(new PTStringValidator(Labels.getLabel("label_CommitmentRateDialog_CmtBaseRate.value"), null, false, true));
		}
		if (!this.cmtBaseRate.isMarginReadonly()) {
			this.cmtBaseRate.setMarginConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentRateDialog_CmtBaseRate.value"), 9, false,true,-9999,9999));
		}

		//Actual Rate
		if (!this.cmtActualRate.isReadonly()){
			this.cmtActualRate.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentRateDialog_CmtActualRate.value"), 9, false,false,0,9999));
		}

		//Calculated Rate
		/*if (!this.cmtCalculatedRate.isReadonly()){
			this.cmtCalculatedRate.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommitmentRateDialog_CmtCalculatedRate.value"), 9, false,false,0,9999));
		}*/

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.cmtBaseRate.setBaseConstraint("");
		this.cmtBaseRate.setSpecialConstraint("");
		this.cmtActualRate.setConstraint("");
		//this.cmtCalculatedRate.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	protected void doClearMessage() {
		logger.debug("Entering");

		this.cmtActualRate.setErrorMessage("");
		//this.cmtCalculatedRate.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.cmtReference.setValue("");
		this.cmtRvwFrq.setValue("");
		this.cmtActualRate.setValue("");
		this.cmtCalculatedRate.setValue("");

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CommitmentRate object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	

		final CommitmentRate aCommitmentRate = new CommitmentRate();
		BeanUtils.copyProperties(getCommitmentRate(), aCommitmentRate);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_CmtRvwFrq")+" : " + aCommitmentRate.getCmtRvwFrq();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCommitmentRate.getRecordType())) {
				aCommitmentRate.setVersion(aCommitmentRate.getVersion() + 1);
				aCommitmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aCommitmentRate.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aCommitmentRate.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aCommitmentRate.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aCommitmentRate.setVersion(aCommitmentRate.getVersion() + 1);
				aCommitmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			try {
				if (isNewCommitment()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newCommitmentRateProcess(aCommitmentRate, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CommitmentRateDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (getCommitmentDialogCtrl() != null) {
							getCommitmentDialogCtrl().doFillCommitmentRateDetails(this.commitmentRateDetailList);
						}
						// send the data back to customer
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CommitmentRate aCommitmentRate = new CommitmentRate();
		BeanUtils.copyProperties(getCommitmentRate(), aCommitmentRate);
		boolean isNew = false;

		if(!PennantConstants.RECORD_TYPE_DEL.equals(aCommitmentRate.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the CommitmentRate object with the components data
			doWriteComponentsToBean(aCommitmentRate);
		}

		isNew = aCommitmentRate.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCommitmentRate.getRecordType()).equals("")){
				aCommitmentRate.setVersion(aCommitmentRate.getVersion()+1);
				if(isNew){
					aCommitmentRate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCommitmentRate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommitmentRate.setNewRecord(true);
				}
			}
		} else {

			if (isNewCommitment()) {
				if (isNewRecord()) {
					aCommitmentRate.setVersion(1);
					aCommitmentRate.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aCommitmentRate.getRecordType())) {
					aCommitmentRate.setVersion(aCommitmentRate.getVersion() + 1);
					aCommitmentRate.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCommitmentRate.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCommitmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCommitmentRate.setVersion(aCommitmentRate.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewCommitment()) {
				AuditHeader auditHeader = newCommitmentRateProcess(aCommitmentRate, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CommitmentRateDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCommitmentDialogCtrl().doFillCommitmentRateDetails(this.commitmentRateDetailList);

					// send the data back to customer
					closeDialog();
				}
			} 
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	/**
	 * 
	 * @param aCommitmentRate
	 * @param tranType
	 * @return
	 */
	private AuditHeader newCommitmentRateProcess(CommitmentRate aCommitmentRate, String tranType) {
		logger.debug("Entering");

		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCommitmentRate, tranType);
		commitmentRateDetailList = new ArrayList<CommitmentRate>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aCommitmentRate.getCmtReference();
		valueParm[1] = aCommitmentRate.getCmtRvwFrq();

		errParm[0] = PennantJavaUtil.getLabel("label_CmtReference") + " : " + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CmtRvwFrq") + " : " + valueParm[1];

		List<CommitmentRate> commitmentRateList = getCommitmentDialogCtrl().getCommitmentRateDetailList();
		if (commitmentRateList != null && !commitmentRateList.isEmpty()) {
			for (CommitmentRate commitmentRate : commitmentRateList) {
				if (aCommitmentRate.getCmtRvwFrq().equals(commitmentRate.getCmtRvwFrq())) {

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {

						if (aCommitmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCommitmentRate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							commitmentRateDetailList.add(aCommitmentRate);

						} else if (aCommitmentRate.getRecordType().equals(PennantConstants.RCD_ADD)) {

							recordAdded = true;

						} else if (aCommitmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

							aCommitmentRate.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							commitmentRateDetailList.add(aCommitmentRate);

						} else if (aCommitmentRate.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCommitmentDialogCtrl().getCommitmentRateDetailList().size(); j++) {
								CommitmentRate cmtRate = getCommitmentDialogCtrl().getCommitmentRateDetailList().get(j);
								if (cmtRate.getCmtReference() == aCommitmentRate.getCmtReference() && cmtRate.getCmtRvwFrq().equals(aCommitmentRate.getCmtRvwFrq())) {
									commitmentRateDetailList.add(cmtRate);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							commitmentRateDetailList.add(commitmentRate);
						}
					}
				} else {
					commitmentRateDetailList.add(commitmentRate);
				}
			}
		}

		if (!recordAdded) {
			commitmentRateDetailList.add(aCommitmentRate);
		}

		logger.debug("Leaving");
		return auditHeader;
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CommitmentRate aCommitmentRate, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommitmentRate.getBefImage(), aCommitmentRate);   
		return new AuditHeader(aCommitmentRate.getCmtRvwFrq(),null,null,null,auditDetail,aCommitmentRate.getUserDetails(),getOverideMap());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CommitmentRate getCommitmentRate() {
		return this.commitmentRate;
	}

	public void setCommitmentRate(CommitmentRate commitmentRate) {
		this.commitmentRate = commitmentRate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCommitment() {
		return newCommitment;
	}

	public void setNewCommitment(boolean newCommitment) {
		this.newCommitment = newCommitment;
	}
	public CommitmentDialogCtrl getCommitmentDialogCtrl() {
		return commitmentDialogCtrl;
	}

	public void setCommitmentDialogCtrl(CommitmentDialogCtrl commitmentDialogCtrl) {
		this.commitmentDialogCtrl = commitmentDialogCtrl;
	}
}
