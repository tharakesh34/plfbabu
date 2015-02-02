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
 * FileName    		:  OverdueChargeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-05-2012    														*
 *                                                                  						*
 * Modified Date    :  10-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rulefactory.overduecharge;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.model.rulefactory.OverdueChargeDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rulefactory.OverdueChargeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RuleFactory/OverdueCharge/overdueChargeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class OverdueChargeDialogCtrl extends GFCBaseListCtrl<OverdueCharge> implements Serializable {

	private static final long serialVersionUID = -1845171438320819608L;
	private final static Logger logger = Logger.getLogger(OverdueChargeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_OverdueChargeDialog; // autowired
	protected Textbox 		oDCRuleCode; 				// autowired
	protected Textbox 		oDCPLAccount; 				// autowired
	protected Textbox 		oDCCharityAccount; 			// autowired
	protected Textbox 		oDCPLSubHead; 				// autowired
	protected Textbox 		oDCCharitySubHead; 			//autowired
	protected Decimalbox 	oDCPLShare; 				// autowired
	protected Checkbox 		oDCSweepCharges; 			// autowired
	protected Textbox 		oDCRuleDescription; 		// autowired
	protected Listbox 		listBoxOverdue;				// autowired
	protected Grid			grid_basicDetails;			// autowired

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired vars
	private OverdueCharge overdueCharge; // overhanded per param
	private OverdueCharge prvOverdueCharge; // overhanded per param
	private transient OverdueChargeListCtrl overdueChargeListCtrl; // overhanded
	// per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_oDCRuleCode;
	private transient String oldVar_oDCPLAccount;
	private transient String oldVar_oDCCharityAccount;
	private transient String oldVar_oDCPLSubHead;
	private transient String oldVar_oDCCharitySubHead;
	private transient BigDecimal oldVar_oDCPLShare;
	private transient boolean oldVar_oDCSweepCharges;
	private transient String oldVar_recordStatus;
	private transient String oldVar_oDCRuleDescription;
	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_OverdueChargeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose;	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	protected Button btnSearchODCPLAccount; // autowire
	protected Textbox lovDescODCPLAccountName;
	private transient String oldVar_lovDescODCPLAccountName;
	
	protected Button btnSearchODCCharityAccount; // autowire
	protected Textbox lovDescODCCharityAccountName;
	private transient String oldVar_lovDescODCCharityAccountName;
	
	protected Button btnSearchODCPLSubHead; // autowire
	protected Textbox lovDescODCPLSubHeadName;
	private transient String oldVar_lovDescODCPLSubHeadName;
	
	protected Button btnSearchODCCharitySubHead; // autowire
	protected Textbox lovDescODCCharitySubHeadName;
	private transient String oldVar_lovDescODCCharitySubHeadName;

	// ServiceDAOs / Domain Classes
	private transient OverdueChargeService overdueChargeService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<OverdueChargeDetail> overdueChargeDetailList = new ArrayList<OverdueChargeDetail>();
	private List<OverdueChargeDetail> oldVar_overdueChargeDetailList = new ArrayList<OverdueChargeDetail>();
	private PagedListWrapper<OverdueChargeDetail> OverdueChargeDetailPagedListWrapper;
	private Map<String, Object> overDueChargeDetailMap = new HashMap<String, Object>();

	/**
	 * default constructor.<br>
	 */
	public OverdueChargeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected OverdueCharge object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_OverdueChargeDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("overdueCharge")) {
			this.overdueCharge = (OverdueCharge) args.get("overdueCharge");
			OverdueCharge befImage = new OverdueCharge();
			BeanUtils.copyProperties(this.overdueCharge, befImage);
			this.overdueCharge.setBefImage(befImage);

			setOverdueCharge(this.overdueCharge);
		} else {
			setOverdueCharge(null);
		}

		doLoadWorkFlow(this.overdueCharge.isWorkflow(), this.overdueCharge.getWorkflowId(), this.overdueCharge.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "OverdueChargeDialog");
		}
		
		getBorderLayoutHeight();
		int listboxHeight = borderLayoutHeight - (this.grid_basicDetails.getRows().getVisibleItemCount() * 20)-100;
		this.listBoxOverdue.setHeight(listboxHeight+"px");

		// READ OVERHANDED params !
		// we get the overdueChargeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete overdueCharge here.
		if (args.containsKey("overdueChargeListCtrl")) {
			setOverdueChargeListCtrl((OverdueChargeListCtrl) args.get("overdueChargeListCtrl"));
		} else {
			setOverdueChargeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getOverdueCharge());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.oDCRuleCode.setMaxlength(8);
		this.oDCPLAccount.setMaxlength(8);
		this.oDCCharityAccount.setMaxlength(8);
		this.oDCPLSubHead.setMaxlength(8);
		this.oDCCharitySubHead.setMaxlength(8);
		this.oDCPLShare.setMaxlength(5);
		this.oDCPLShare.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.oDCPLShare.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.oDCRuleDescription.setMaxlength(250);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("OverdueChargeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeDialog_btnSave"));
		this.btnCancel.setVisible(false);

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
	public void onClose$window_OverdueChargeDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving"  +event.toString());
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
		logger.debug("Leaving"  +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving"  +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_OverdueChargeDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" +event.toString());
		doNew();
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" +event.toString());
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
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_OverdueChargeDialog, "OverdueCharge");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aOverdueCharge
	 *            OverdueCharge
	 */
	public void doWriteBeanToComponents(OverdueCharge aOverdueCharge) {
		logger.debug("Entering");

		this.oDCRuleCode.setValue(aOverdueCharge.getODCRuleCode());
		this.oDCPLAccount.setValue(aOverdueCharge.getODCPLAccount());
		this.oDCCharityAccount.setValue(aOverdueCharge.getODCCharityAccount());
		this.oDCPLSubHead.setValue(aOverdueCharge.getoDCPLSubHead());
		this.oDCCharitySubHead.setValue(aOverdueCharge.getoDCCharitySubHead());
		this.oDCPLShare.setValue(aOverdueCharge.getODCPLShare()==null?BigDecimal.ZERO : aOverdueCharge.getODCPLShare());
		this.oDCSweepCharges.setChecked(aOverdueCharge.isODCSweepCharges());
		this.oDCRuleDescription.setValue(aOverdueCharge.getoDCRuleDescription());
		for (int i = 0; i < aOverdueCharge.getChargeDetailEntries().size(); i++) {
			OverdueChargeDetail detail = aOverdueCharge.getChargeDetailEntries().get(i);
			overDueChargeDetailMap.put(detail.getoDCCustCtg(), detail);
		}
		
		fillListBox(this.listBoxOverdue, aOverdueCharge);
		if (aOverdueCharge.isNewRecord()) {

			this.oDCPLAccount.setValue((String) SystemParameterDetails.getSystemParameterValue("ODC_PLAC"));
			this.oDCCharityAccount.setValue((String) SystemParameterDetails.getSystemParameterValue("ODC_CAC"));
			this.lovDescODCPLAccountName.setValue(this.oDCPLAccount.getValue()+"-"+((PFSParameter) SystemParameterDetails.getSystemParameterObject("ODC_PLAC")).getSysParmDesc());
			this.lovDescODCCharityAccountName.setValue(this.oDCCharityAccount.getValue()+"-"+((PFSParameter) SystemParameterDetails.getSystemParameterObject("ODC_CAC")).getSysParmDesc());
			this.oDCPLSubHead.setValue((String) SystemParameterDetails.getSystemParameterValue("ODC_PLACSH"));
			this.oDCCharitySubHead.setValue((String) SystemParameterDetails.getSystemParameterValue("ODC_CACSH"));
			this.lovDescODCPLSubHeadName.setValue(this.oDCPLSubHead.getValue()+"-"+((PFSParameter) SystemParameterDetails.getSystemParameterObject("ODC_PLACSH")).getSysParmDesc());
			this.lovDescODCCharitySubHeadName.setValue(this.oDCCharitySubHead.getValue()+"-"+((PFSParameter) SystemParameterDetails.getSystemParameterObject("ODC_CACSH")).getSysParmDesc());
			this.oDCPLShare.setValue((BigDecimal) SystemParameterDetails.getSystemParameterValue("ODC_PLSHARE"));
			if (SystemParameterDetails.getSystemParameterValue("ODC_SWEEP").equals("Y")) {
				this.oDCSweepCharges.setChecked(true);
			} else {
				this.oDCSweepCharges.setChecked(false);
			}

		} else {
			this.lovDescODCPLAccountName.setValue(aOverdueCharge.getODCPLAccount() + "-" + aOverdueCharge.getLovDescODCPLAccountName());
			this.lovDescODCCharityAccountName.setValue(aOverdueCharge.getODCCharityAccount() + "-" + aOverdueCharge.getLovDescODCCharityAccountName());
			this.lovDescODCPLSubHeadName.setValue(aOverdueCharge.getoDCPLSubHead() + "-" + aOverdueCharge.getLovDescODCPLSubHeadName());
			this.lovDescODCCharitySubHeadName.setValue(aOverdueCharge.getoDCCharitySubHead() + "-" + aOverdueCharge.getLovDescODCCharitySubHeadName());
		}
		this.recordStatus.setValue(aOverdueCharge.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aOverdueCharge
	 */
	public void doWriteComponentsToBean(OverdueCharge aOverdueCharge) {
		logger.debug("Entering");
		doSetLOVValidation();
		detailsValidation(this.listBoxOverdue);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aOverdueCharge.setODCRuleCode(this.oDCRuleCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueCharge.setLovDescODCPLAccountName(this.lovDescODCPLAccountName.getValue());
			aOverdueCharge.setODCPLAccount(this.oDCPLAccount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueCharge.setLovDescODCCharityAccountName(this.lovDescODCCharityAccountName.getValue());
			aOverdueCharge.setODCCharityAccount(this.oDCCharityAccount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueCharge.setLovDescODCPLSubHeadName(this.lovDescODCPLSubHeadName.getValue());
			aOverdueCharge.setoDCPLSubHead(this.oDCPLSubHead.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueCharge.setLovDescODCCharitySubHeadName(this.lovDescODCCharitySubHeadName.getValue());
			aOverdueCharge.setoDCCharitySubHead(this.oDCCharitySubHead.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.oDCPLShare.getValue() != null) {
				aOverdueCharge.setODCPLShare(this.oDCPLShare.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueCharge.setODCSweepCharges(this.oDCSweepCharges.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueCharge.setoDCRuleDescription(this.oDCRuleDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		for (int i = 0; i < this.listBoxOverdue.getItems().size(); i++) {
			Listitem listitem = (Listitem) this.listBoxOverdue.getItems().get(i);
			OverdueChargeDetail chargeDetail = null;

			if (listitem != null && listitem.getChildren().size() > 0) {
				String custCtg = ((Listcell) listitem.getChildren().get(0)).getId();

				if (overDueChargeDetailMap.containsKey(custCtg)) {
					chargeDetail = (OverdueChargeDetail) overDueChargeDetailMap.get(custCtg);
					if (chargeDetail.getRecordType().equals("")) {
						chargeDetail.setRecordType(PennantConstants.RCD_UPD);
						chargeDetail.setNewRecord(true);
						chargeDetail.setVersion(chargeDetail.getVersion() + 1);
					}
				} else {
					chargeDetail = new OverdueChargeDetail();
					chargeDetail.setNewRecord(true);
					chargeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					chargeDetail.setWorkflowId(0);
					chargeDetail.setoDCCustCtg(custCtg);
				}

				Listcell chargeType = (Listcell) listitem.getChildren().get(1);
				Combobox comboChargeType = (Combobox) chargeType.getFirstChild();
				chargeDetail.setoDCType(comboChargeType.getSelectedItem().getValue().toString());

				Listcell calculatedOn = (Listcell) listitem.getChildren().get(2);
				Combobox combocalculatedOn = (Combobox) calculatedOn.getFirstChild();
				chargeDetail.setoDCOn(combocalculatedOn.getSelectedItem().getValue().toString());

				Listcell chargeAmtPerc = (Listcell) listitem.getChildren().get(3);
				Decimalbox decimalChargeAmtPerc = (Decimalbox) chargeAmtPerc.getFirstChild();
				chargeDetail.setoDCAmount(decimalChargeAmtPerc.getValue());

				Listcell graceDays = (Listcell) listitem.getChildren().get(4);
				Intbox intGraceDays = (Intbox) graceDays.getFirstChild();
				chargeDetail.setoDCGraceDays(intGraceDays.intValue());

				Listcell allowWaiver = (Listcell) listitem.getChildren().get(5);
				Checkbox checkboxAllowWaiver = (Checkbox) allowWaiver.getFirstChild();
				chargeDetail.setoDCAllowWaiver(checkboxAllowWaiver.isChecked());

				Listcell maxWaiverPer = (Listcell) listitem.getChildren().get(6);
				Decimalbox decimalMaxWaiverPer = (Decimalbox) maxWaiverPer.getFirstChild();
				chargeDetail.setoDCMaxWaiver(decimalMaxWaiverPer.getValue());

				overdueChargeDetailList.add(chargeDetail);

			}
			aOverdueCharge.setChargeDetailEntries(overdueChargeDetailList);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aOverdueCharge.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aOverdueCharge
	 * @throws InterruptedException
	 */
	public void doShowDialog(OverdueCharge aOverdueCharge) throws InterruptedException {
		logger.debug("Entering");

		// if aOverdueCharge == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aOverdueCharge == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aOverdueCharge = getOverdueChargeService().getNewOverdueCharge();

			setOverdueCharge(aOverdueCharge);
		} else {
			setOverdueCharge(aOverdueCharge);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aOverdueCharge.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.oDCRuleCode.focus();
		} else {
			this.oDCPLAccount.focus();
			if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aOverdueCharge);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_OverdueChargeDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_oDCRuleCode = this.oDCRuleCode.getValue();
		this.oldVar_oDCPLAccount = this.oDCPLAccount.getValue();
		this.oldVar_lovDescODCPLAccountName = this.lovDescODCPLAccountName.getValue();
		this.oldVar_oDCCharityAccount = this.oDCCharityAccount.getValue();
		this.oldVar_lovDescODCCharityAccountName = this.lovDescODCCharityAccountName.getValue();
		this.oldVar_oDCPLSubHead = this.oDCPLSubHead.getValue();
		this.oldVar_lovDescODCPLSubHeadName = this.lovDescODCPLSubHeadName.getValue();
		this.oldVar_oDCCharitySubHead = this.oDCCharitySubHead.getValue();
		this.oldVar_lovDescODCCharitySubHeadName = this.lovDescODCCharitySubHeadName.getValue();
		this.oldVar_oDCPLShare = this.oDCPLShare.getValue();
		this.oldVar_oDCSweepCharges = this.oDCSweepCharges.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_oDCRuleDescription = this.oDCRuleDescription.getValue();
		this.oldVar_overdueChargeDetailList = getOverdueChargeDetailList();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.oDCRuleCode.setValue(this.oldVar_oDCRuleCode);
		this.oDCPLAccount.setValue(this.oldVar_oDCPLAccount);
		this.lovDescODCPLAccountName.setValue(this.oldVar_lovDescODCPLAccountName);
		this.oDCCharityAccount.setValue(this.oldVar_oDCCharityAccount);
		this.lovDescODCCharityAccountName.setValue(this.oldVar_lovDescODCCharityAccountName);
		this.oDCPLSubHead.setValue(this.oldVar_oDCPLSubHead);
		this.lovDescODCPLSubHeadName.setValue(this.oldVar_lovDescODCPLSubHeadName);
		this.oDCCharitySubHead.setValue(this.oldVar_oDCCharitySubHead);
		this.lovDescODCCharitySubHeadName.setValue(this.oldVar_lovDescODCCharitySubHeadName);
		this.oDCPLShare.setValue(this.oldVar_oDCPLShare);
		this.oDCSweepCharges.setChecked(this.oldVar_oDCSweepCharges);
		this.oDCRuleDescription.setValue(this.oldVar_oDCRuleDescription);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.overdueChargeDetailList = this.oldVar_overdueChargeDetailList;

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_oDCRuleCode != this.oDCRuleCode.getValue()) {
			return true;
		}
		if (this.oldVar_oDCPLAccount != this.oDCPLAccount.getValue()) {
			return true;
		}
		if (this.oldVar_oDCCharityAccount != this.oDCCharityAccount.getValue()) {
			return true;
		}
		if (this.oldVar_oDCPLSubHead != this.oDCPLSubHead.getValue()) {
			return true;
		}
		if (this.oldVar_oDCCharitySubHead != this.oDCCharitySubHead.getValue()) {
			return true;
		}
		if (this.oldVar_oDCPLShare != this.oDCPLShare.getValue()) {
			return true;
		}
		if (this.oldVar_oDCSweepCharges != this.oDCSweepCharges.isChecked()) {
			return true;
		}
		if (this.oldVar_oDCRuleDescription != this.oDCRuleDescription.getValue()) {
			return true;
		}
		if (this.oldVar_overdueChargeDetailList != this.overdueChargeDetailList) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.oDCRuleCode.isReadonly()) {
			this.oDCRuleCode.setConstraint(new PTStringValidator(Labels.getLabel("label_OverdueChargeDialog_ODCRuleCode.value"),null,true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.oDCRuleCode.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a OverdueCharge object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final OverdueCharge aOverdueCharge = new OverdueCharge();
		BeanUtils.copyProperties(getOverdueCharge(), aOverdueCharge);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aOverdueCharge.getODCRuleCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aOverdueCharge.getRecordType()).equals("")) {
				aOverdueCharge.setVersion(aOverdueCharge.getVersion() + 1);
				aOverdueCharge.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aOverdueCharge.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aOverdueCharge, tranType)) {
					refreshList();
					closeDialog(this.window_OverdueChargeDialog, "OverdueCharge");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new OverdueCharge object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		
		final OverdueCharge aOverdueCharge = getOverdueChargeService().getNewOverdueCharge();
		aOverdueCharge.setODCSweepCharges(false); // init
		aOverdueCharge.setNewRecord(true);
		setOverdueCharge(aOverdueCharge);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.oDCRuleCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getOverdueCharge().isNewRecord()) {
			this.oDCRuleCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.oDCRuleCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.btnSearchODCPLAccount.setDisabled(isReadOnly("OverdueChargeDialog_oDCPLAccount"));
		this.btnSearchODCCharityAccount.setDisabled(isReadOnly("OverdueChargeDialog_oDCCharityAccount"));
		this.btnSearchODCPLSubHead.setDisabled(isReadOnly("OverdueChargeDialog_oDCPLSubHead"));
		this.btnSearchODCCharitySubHead.setDisabled(isReadOnly("OverdueChargeDialog_oDCCharitySubHead"));
		this.oDCPLShare.setDisabled(isReadOnly("OverdueChargeDialog_oDCPLShare"));
		this.oDCSweepCharges.setDisabled(isReadOnly("OverdueChargeDialog_oDCSweepCharges"));
		this.oDCRuleDescription.setDisabled(isReadOnly("OverdueChargeDialog_oDCRuleDescription"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.overdueCharge.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.oDCRuleCode.setReadonly(true);
		this.btnSearchODCPLAccount.setDisabled(true);
		this.btnSearchODCCharityAccount.setDisabled(true);
		this.btnSearchODCPLSubHead.setDisabled(true);
		this.btnSearchODCCharitySubHead.setDisabled(true);
		this.oDCPLShare.setReadonly(true);
		this.oDCSweepCharges.setDisabled(true);
		this.oDCRuleDescription.setReadonly(true);

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

		this.oDCRuleCode.setValue("");
		this.oDCPLAccount.setValue("");
		this.lovDescODCPLAccountName.setValue("");
		this.oDCCharityAccount.setValue("");
		this.lovDescODCCharityAccountName.setValue("");
		this.oDCPLSubHead.setValue("");
		this.lovDescODCPLSubHeadName.setValue("");
		this.oDCCharitySubHead.setValue("");
		this.lovDescODCCharitySubHeadName.setValue("");
		this.oDCPLShare.setValue("");
		this.oDCSweepCharges.setChecked(false);
		this.oDCRuleDescription.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final OverdueCharge aOverdueCharge = new OverdueCharge();
		BeanUtils.copyProperties(getOverdueCharge(), aOverdueCharge);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the OverdueCharge object with the components data
		doWriteComponentsToBean(aOverdueCharge);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aOverdueCharge.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aOverdueCharge.getRecordType()).equals("")) {
				aOverdueCharge.setVersion(aOverdueCharge.getVersion() + 1);
				if (isNew) {
					aOverdueCharge.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aOverdueCharge.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aOverdueCharge.setNewRecord(true);
				}
			}
		} else {
			aOverdueCharge.setVersion(aOverdueCharge.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aOverdueCharge, tranType)) {
				refreshList();
				closeDialog(this.window_OverdueChargeDialog, "OverdueCharge");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(OverdueCharge aOverdueCharge, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aOverdueCharge.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aOverdueCharge.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aOverdueCharge.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aOverdueCharge.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aOverdueCharge.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aOverdueCharge);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aOverdueCharge))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aOverdueCharge.setTaskId(taskId);
			aOverdueCharge.setNextTaskId(nextTaskId);
			aOverdueCharge.setRoleCode(getRole());
			aOverdueCharge.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aOverdueCharge, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aOverdueCharge);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aOverdueCharge, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aOverdueCharge, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		OverdueCharge aOverdueCharge = (OverdueCharge) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getOverdueChargeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getOverdueChargeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getOverdueChargeService().doApprove(auditHeader);

						if (aOverdueCharge.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getOverdueChargeService().doReject(auditHeader);
						if (aOverdueCharge.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_OverdueChargeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_OverdueChargeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	
	public void onClick$btnSearchODCPLAccount(Event event) {
		logger.debug("Entering" +event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_OverdueChargeDialog, "SystemInternalAccountDefinition");
		if (dataObject instanceof String) {
			this.oDCPLAccount.setValue(dataObject.toString());
			this.lovDescODCPLAccountName.setValue("");
		} else {
			SystemInternalAccountDefinition details = (SystemInternalAccountDefinition) dataObject;
			if (details != null) {
				this.oDCPLAccount.setValue(details.getSIACode());
				this.lovDescODCPLAccountName.setValue(details.getSIACode() + "-" + details.getSIAName());
			}
		}
		logger.debug("Leaving" +event.toString());
	}
	public void onClick$btnSearchODCCharityAccount(Event event) {
		logger.debug("Entering" +event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_OverdueChargeDialog, "SystemInternalAccountDefinition");
		if (dataObject instanceof String) {
			this.oDCCharityAccount.setValue(dataObject.toString());
			this.lovDescODCCharityAccountName.setValue("");
		} else {
			SystemInternalAccountDefinition details = (SystemInternalAccountDefinition) dataObject;
			if (details != null) {
				this.oDCCharityAccount.setValue(details.getSIACode());
				this.lovDescODCCharityAccountName.setValue(details.getSIACode() + "-" + details.getSIAName());
			}
		}
		logger.debug("Leaving" +event.toString());
	}
	
	public void onClick$btnSearchODCPLSubHead(Event event) {
		logger.debug("Entering" +event.toString());
		
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", "SUBHEAD", Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_OverdueChargeDialog, "Rule", filter);
		if (dataObject instanceof String) {
			this.oDCPLSubHead.setValue(dataObject.toString());
			this.lovDescODCPLSubHeadName.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.oDCPLSubHead.setValue(details.getRuleCode());
				this.lovDescODCPLSubHeadName.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
			}
		}
		
		logger.debug("Leaving" +event.toString());
	}
	
	public void onClick$btnSearchODCCharitySubHead(Event event) {
		logger.debug("Entering" +event.toString());
		
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", "SUBHEAD", Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_OverdueChargeDialog, "Rule", filter);
		if (dataObject instanceof String) {
			this.oDCCharitySubHead.setValue(dataObject.toString());
			this.lovDescODCCharitySubHeadName.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.oDCCharitySubHead.setValue(details.getRuleCode());
				this.lovDescODCCharitySubHeadName.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
			}
		}
		
		logger.debug("Leaving" +event.toString());
	}

	private AuditHeader getAuditHeader(OverdueCharge aOverdueCharge, String tranType) {
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aOverdueCharge.getBefImage(), aOverdueCharge);
		logger.debug("Leaving ");
		return new AuditHeader(aOverdueCharge.getODCRuleCode(), null, null, null, auditDetail, aOverdueCharge.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_OverdueChargeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving ");
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private void doSetLOVValidation() {
		this.lovDescODCPLAccountName.setConstraint(new PTStringValidator(Labels.getLabel("label_OverdueChargeDialog_ODCPLAccount.value"),null,true));
		this.lovDescODCCharityAccountName.setConstraint(new PTStringValidator(Labels.getLabel("label_OverdueChargeDialog_ODCCharityAccount.value"),null,true));
	}

	private void doRemoveLOVValidation() {
		this.lovDescODCPLAccountName.setConstraint("");
		this.lovDescODCCharityAccountName.setConstraint("");
	}

	private Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("OverdueCharge");
		notes.setReference(getOverdueCharge().getODCRuleCode());
		notes.setVersion(getOverdueCharge().getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.oDCRuleCode.setErrorMessage("");
		this.lovDescODCPLAccountName.setErrorMessage("");
		this.lovDescODCCharityAccountName.setErrorMessage("");
		this.oDCPLShare.setErrorMessage("");
		this.oDCRuleDescription.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		logger.debug("Entering ");
		final JdbcSearchObject<OverdueCharge> soOverdueCharge = getOverdueChargeListCtrl().getSearchObj();
		getOverdueChargeListCtrl().pagingOverdueChargeList.setActivePage(0);
		getOverdueChargeListCtrl().getPagedListWrapper().setSearchObject(soOverdueCharge);
		if (getOverdueChargeListCtrl().listBoxOverdueCharge != null) {
			getOverdueChargeListCtrl().listBoxOverdueCharge.getListModel();
		}
		logger.debug("Leaving ");
	}



	/**
	 * Method for Filling Data on Listbox
	 * @param listbox
	 * @param aOverdueCharge
	 */
	private void fillListBox(Listbox listbox, OverdueCharge aOverdueCharge) {
		logger.debug("Entering ");
		JdbcSearchObject<CustomerCategory> object = new JdbcSearchObject<CustomerCategory>(CustomerCategory.class);
		object.addTabelName("BMTCustCategories");

		List<CustomerCategory> categories = getPagedListService().getBySearchObject(object);
		for (CustomerCategory customerCategory : categories) {
			
			boolean readonly = false;
			OverdueChargeDetail overdueChargeDetail = null ;
			if(overDueChargeDetailMap.containsKey(customerCategory.getCustCtgCode())){
				overdueChargeDetail = (OverdueChargeDetail) overDueChargeDetailMap.get(customerCategory.getCustCtgCode());
			}else{
				overdueChargeDetail = new OverdueChargeDetail();
				overdueChargeDetail.setNewRecord(true);
				readonly = false;
			}
			if (overdueChargeDetail.isNewRecord()) {
				overdueChargeDetail.setoDCType((String)SystemParameterDetails.getSystemParameterValue("ODC_TYPE"));
				overdueChargeDetail.setoDCOn((String) SystemParameterDetails.getSystemParameterValue("ODC_CALON"));
				overdueChargeDetail.setoDCAmount((BigDecimal)SystemParameterDetails.getSystemParameterValue("ODC_AMT"));
				overdueChargeDetail.setoDCGraceDays(Integer.parseInt(SystemParameterDetails.getSystemParameterValue("ODC_GRACE").toString()) );
				if (SystemParameterDetails.getSystemParameterValue("ODC_WAIVER").equals("Y")) {
					overdueChargeDetail.setoDCAllowWaiver(true);
				} else {
					overdueChargeDetail.setoDCAllowWaiver(false);
				}
				overdueChargeDetail.setoDCMaxWaiver((BigDecimal) SystemParameterDetails.getSystemParameterValue("ODC_MAXWAIVER"));

				
			} 
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(customerCategory.getCustCtgDesc());
			lc.setId(customerCategory.getCustCtgCode());
			lc.setStyle("font-weight:bold");
			lc.setParent(item);

			// Combobox for flat and percentage
			lc = new Listcell();
			lc.appendChild(fillComboboxByFlatorPrecentage(new Combobox(), overdueChargeDetail.getoDCType(), false));
			lc.setParent(item);

			boolean disable = false;
			if( overdueChargeDetail.getoDCType().equals("F") || overdueChargeDetail.getoDCType().equals("#")){
				disable = true;
			}

			// Combobox for calucated On
			lc = new Listcell();
			lc.appendChild(fillComboboxByCalculatedOn(new Combobox(), overdueChargeDetail.getoDCOn(), disable));
			lc.setParent(item);

			lc = new Listcell();
			lc.appendChild(decimalbox(overdueChargeDetail.getoDCAmount() == null ? BigDecimal.ZERO:
				overdueChargeDetail.getoDCAmount(),readonly,overdueChargeDetail.getoDCType()));
			lc.setParent(item);

			lc = new Listcell();
			lc.appendChild(intbox(overdueChargeDetail.getoDCGraceDays(),readonly));
			lc.setParent(item);

			lc = new Listcell("");
			lc.appendChild(checkbox(overdueChargeDetail.isoDCAllowWaiver(),readonly));
			lc.setParent(item);

			lc = new Listcell();
			lc.appendChild(decimalbox(overdueChargeDetail.getoDCMaxWaiver() == null ? BigDecimal.ZERO:
				overdueChargeDetail.getoDCMaxWaiver(),!overdueChargeDetail.isoDCAllowWaiver(),"maxWaiver"));
			lc.setParent(item);

			item.setAttribute("data", "");
			listbox.appendChild(item);
		}
		logger.debug("Leaving ");
	}

	/**
	 * get the static list of ChargeTypes from 
	 * PennantAppUtil and pass to fillComboBox Method 
	 * 
	 * @param combobox
	 * @param value
	 * @param disabled
	 * @return
	 */
	private Combobox fillComboboxByFlatorPrecentage(Combobox combobox, String value, boolean disabled) {
		logger.debug("Entering ");
		List<ValueLabel> odchargetypes = PennantStaticListUtil.getODCChargeType();
		combobox.addForward("onChange", window_OverdueChargeDialog, "onFieldComboSelected", combobox);
		logger.debug("Leaving ");
		return fillCombobox(combobox, value, odchargetypes, disabled);
	}

	/**
	 * get the static list of ODCCalculatedOn from 
	 * PennantAppUtil and pass to fillComboBox Method 
	 * 
	 * @param combobox
	 * @param value
	 * @param disabled
	 * @return
	 */
	private Combobox fillComboboxByCalculatedOn(Combobox combobox, String value, boolean disabled) {
		logger.debug("Entering ");
		List<ValueLabel> odcCalucatedOn = PennantStaticListUtil.getODCCalculatedOn();
		fillCombobox(combobox, value, odcCalucatedOn, disabled);
		logger.debug("Leaving ");
		return combobox;
	}
	/**
	 * Creating the Combobox Dynamically
	 * 
	 * @param combobox
	 * @param value
	 * @param valueLabels
	 * @param disabled
	 * @return combobox
	 */
	private Combobox fillCombobox(Combobox combobox, String value, List<ValueLabel> valueLabels, boolean disabled) {
		logger.debug("Entering ");
		combobox.setReadonly(true);
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		for (ValueLabel product : valueLabels) {
			comboitem = new Comboitem();
			comboitem.setValue(product.getValue());
			comboitem.setLabel(product.getLabel());
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(product.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		combobox.setDisabled(disabled);
		if (disabled) {
			combobox.setSelectedIndex(0);  
		}
		combobox.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		logger.debug("Leaving ");
		return combobox;
	}

	/**
	 * On Change Event for Charge Type Selection Combo Charge Type
	 * 
	 * @param event
	 */
	public void onFieldComboSelected(Event event) {
		logger.debug("Entering" + event.toString());

		Combobox component = (Combobox) event.getData();
		Combobox calOnCombo = (Combobox) ((Listcell) ((Listcell) component.getParent()).getNextSibling()).getFirstChild();
		Decimalbox CalAmtDecimal = (Decimalbox) ((Listcell) ((Listcell) calOnCombo.getParent()).getNextSibling()).getFirstChild();
		Intbox graceDaysBox = (Intbox) ((Listcell) ((Listcell) CalAmtDecimal.getParent()).getNextSibling()).getFirstChild();
		Checkbox allowWaiverCB = (Checkbox) ((Listcell) ((Listcell) graceDaysBox.getParent()).getNextSibling()).getFirstChild();
		Decimalbox waiverPercDB = (Decimalbox) ((Listcell) ((Listcell) allowWaiverCB.getParent()).getNextSibling()).getFirstChild();

		calOnCombo.setSelectedIndex(0);
		CalAmtDecimal.setValue(BigDecimal.ZERO);
		CalAmtDecimal.setReadonly(false);
		graceDaysBox.setValue(0);
		graceDaysBox.setReadonly(false);
		allowWaiverCB.setChecked(false);
		allowWaiverCB.setDisabled(false);
		waiverPercDB.setValue(BigDecimal.ZERO);
		waiverPercDB.setReadonly(true);

		if (component.getSelectedIndex() == 0) {
			calOnCombo.setDisabled(true);
			CalAmtDecimal.setValue(BigDecimal.ZERO);
			CalAmtDecimal.setReadonly(true);
			graceDaysBox.setReadonly(true);
			allowWaiverCB.setDisabled(true);
		}else if (component.getSelectedItem().getValue().equals("F")) {
			calOnCombo.setDisabled(true);
			CalAmtDecimal.setMaxlength(15);
			CalAmtDecimal.setFormat("");
		} else {
			calOnCombo.setDisabled(false);
			CalAmtDecimal.setMaxlength(5);
			CalAmtDecimal.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
		
		graceDaysBox.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		allowWaiverCB.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		waiverPercDB.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		calOnCombo.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		CalAmtDecimal.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Checkbox on Select method
	 * 
	 * Enables the decimal Box if
	 * Checked is Checked
	 * 
	 * @param event
	 */
	public void onCheckboxSelected(Event event) {
		logger.debug("Entering" + event.toString());
		Checkbox checkbox = (Checkbox) event.getData();
		Decimalbox decimalbox = (Decimalbox) ((Listcell) ((Listcell) checkbox.getParent()).getNextSibling()).getFirstChild();
		if (!checkbox.isChecked()) {
			decimalbox.setDisabled(true);
			decimalbox.setValue(BigDecimal.ZERO);
		} else {
			decimalbox.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
			decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(2));
			decimalbox.setMaxlength(5);
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Creating the Decimal Box
	 * 
	 * @param value
	 * @param readOnly
	 * @param component
	 * @return decimalbox
	 */
	private Decimalbox decimalbox(BigDecimal value,boolean readOnly,String component) {
		logger.debug("Entering ");
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setWidth("90px");
		decimalbox.setValue(value);
		decimalbox.setDisabled(readOnly);

		if (component!= null && component.equals("P")){
			decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
		if (component!= null && component.equals("maxWaiver")){
			decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
		decimalbox.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		logger.debug("Leaving ");
		return decimalbox;
	}

	/**
	 * Creating the intbox
	 * 
	 * @param intVal
	 * @param readOnly
	 * @return intbox
	 */
	private Intbox intbox(Integer intVal, boolean readOnly) {
		logger.debug("Entering ");
		Intbox intbox = new Intbox();
		intbox.setMaxlength(3);
		intbox.setReadonly(readOnly);
		intbox.setReadonly(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		intbox.setWidth("40px");
		intbox.setValue(intVal);
		logger.debug("Leaving ");
		return intbox;
	}

	/**
	 * Creating the Checkbox
	 * 
	 * @param checked
	 * @param disable
	 * @return checkbox
	 */
	private Checkbox checkbox(boolean checked,boolean disable) {
		logger.debug("Entering ");
		Checkbox checkbox = new Checkbox();
		checkbox.setChecked(checked);
		checkbox.setDisabled(disable);
		checkbox.addForward("onCheck", window_OverdueChargeDialog, "onCheckboxSelected", checkbox);
		checkbox.setDisabled(isReadOnly("OverdueChargeDialog_listBoxOverdue"));
		logger.debug("Leaving ");
		return checkbox;
	}

	@SuppressWarnings("unchecked")
	public void setOverdueChargeDetailPagedListWrapper() {
		logger.debug("Entering ");
		if (this.OverdueChargeDetailPagedListWrapper == null) {
			this.OverdueChargeDetailPagedListWrapper = (PagedListWrapper<OverdueChargeDetail>) SpringUtil.getBean("pagedListWrapper");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Setting the Validations for OverDue Details List
	 * 
	 * @param listbox
	 */
	public void detailsValidation(Listbox listbox){
		logger.debug("Entering ");
		for(int i=0;i<listbox.getItems().size();i++){
			Listitem li=listbox.getItemAtIndex(i);
			for (int j = 0; j < li.getChildren().size(); j++) {
				Listcell lc = (Listcell) li.getFirstChild();
				Combobox chargeTypeCombo =	(Combobox) lc.getNextSibling().getFirstChild();
				if(chargeTypeCombo.getSelectedIndex() != 0){
					Combobox calOnCombo =	(Combobox) lc.getNextSibling().getNextSibling().getFirstChild();
					Decimalbox calAmtDB = (Decimalbox)lc.getNextSibling().getNextSibling().getNextSibling().getFirstChild();
					Intbox graceDaysIB = (Intbox)lc.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getFirstChild();
					Checkbox waiverCB = (Checkbox)lc.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getFirstChild();
					Decimalbox waiverPerc = (Decimalbox)lc.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getFirstChild();

					if(chargeTypeCombo.getSelectedItem().getValue().equals("P")){
						if(calOnCombo.getSelectedIndex() == 0){
							throw new WrongValueException(calOnCombo,"Calucated On Must Be Selected");
						}
						if(calAmtDB.getValue() == null ){
							throw new WrongValueException(calAmtDB,"Calucated Amount Must Be Entered");
						}
						if(calAmtDB.getValue().compareTo(new BigDecimal(100)) > 0){
							throw new WrongValueException(calAmtDB,"Calucated Amount Must not be greater than 100");
						}

					}else{
						if(calAmtDB.getValue() == null  ){
							throw new WrongValueException(calAmtDB,"Calucated Amount Must Be Entered");
						}
					}

					if(graceDaysIB.intValue() < 0){
						throw new WrongValueException(graceDaysIB,"Grace Days Must Be Entered");
					}

					if(waiverCB.isChecked() && (waiverPerc.getValue()== null || waiverPerc.getValue().compareTo(BigDecimal.ZERO) == 0)){
						throw new WrongValueException(waiverPerc,"Calucated Amount Must be greater than 0");
					}
					if(waiverPerc.getValue().compareTo(new BigDecimal(100)) > 0){
						throw new WrongValueException(waiverPerc,"Calucated Amount Must not be greater than 100");
					}
				}
			}
		}
		logger.debug("Leaving ");
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

	public OverdueCharge getOverdueCharge() {
		return this.overdueCharge;
	}
	public void setOverdueCharge(OverdueCharge overdueCharge) {
		this.overdueCharge = overdueCharge;
	}

	public void setOverdueChargeService(OverdueChargeService overdueChargeService) {
		this.overdueChargeService = overdueChargeService;
	}
	public OverdueChargeService getOverdueChargeService() {
		return this.overdueChargeService;
	}

	public void setOverdueChargeListCtrl(OverdueChargeListCtrl overdueChargeListCtrl) {
		this.overdueChargeListCtrl = overdueChargeListCtrl;
	}
	public OverdueChargeListCtrl getOverdueChargeListCtrl() {
		return this.overdueChargeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverdueChargeDetailList(List<OverdueChargeDetail> overdueChargeDetailList) {
		this.overdueChargeDetailList = overdueChargeDetailList;
	}
	public List<OverdueChargeDetail> getOverdueChargeDetailList() {
		return overdueChargeDetailList;
	}

	public PagedListWrapper<OverdueChargeDetail> getOverdueChargeDetailPagedListWrapper() {
		return OverdueChargeDetailPagedListWrapper;
	}
	public OverdueCharge getPrvOverdueCharge() {
		return prvOverdueCharge;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}


}
