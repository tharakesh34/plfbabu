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
 * FileName    		:  AccountsDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-01-2012    														*
 *                                                                  						*
 * Modified Date    :  02-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.accounts.accounts;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AccountNumberGeneration;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Accounts/Accounts/AccountsDialog.zul file.
 */
public class AccountsDialogCtrl extends GFCBaseCtrl<Accounts> {
	private static final long serialVersionUID = -485666646629753355L;
	private static final Logger logger = Logger.getLogger(AccountsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window      window_AccountsDialog;          // autoWired
	protected Textbox     accountId;                      // autoWired
	protected Textbox     acCcy;                          // autoWired
	protected Textbox     acType;                         // autoWired
	protected Textbox     acBranch;                       // autoWired
	protected Textbox     lovDescAcTypeDesc;              // autoWired
	protected Longbox     acCustId;                       // autoWired
	protected Textbox     lovDescAcCustCIF;               // autoWired
	protected Textbox     acFullName;                     // autoWired
	protected Textbox     acShortName;                    // autoWired
	protected Textbox     acPurpose;                      // autoWired
	protected Checkbox    internalAc;                     // autoWired
	protected Checkbox    custSysAc;                      // autoWired
	protected Decimalbox  acPrvDayBal;                    // autoWired
	protected Decimalbox  acTodayDr;                      // autoWired
	protected Decimalbox  acTodayCr;                      // autoWired
	protected Decimalbox  acTodayNet;                     // autoWired
	protected Decimalbox  acAccrualBal;                   // autoWired
	protected Decimalbox  acTodayBal;                     // autoWired
	protected Datebox     acOpenDate;                     // autoWired
	protected Datebox     acLastCustTrnDate;              // autoWired
	protected Datebox     acLastSysTrnDate;               // autoWired
	protected Checkbox    acActive;                       // autoWired
	protected Checkbox    acBlocked;                      // autoWired
	protected Checkbox    acClosed;                       // autoWired
	protected Textbox     hostAcNumber;                   // autoWired
	protected Textbox     lovDescAcCcy;                   // autoWired
	protected Textbox     lovDescAcBranch;                // autoWired
	protected Space       spc_acOpenDate;                 // autoWired
	protected Label       label_acOpenDate;               // autoWired
	protected Label       label_accTypeDesc;              // autoWired
	protected Textbox     lovDescAcPurpose;               // autoWired
	protected Space       space_AccountId;                // autoWired

	protected Row         row_cust_Names;                 // autoWired
	protected Row         row_custId;                     // autoWired
	protected Row         row_Active_AcOpenDate;          // autoWired
	protected Row         row_Blocked_Closed;             // autoWired
	protected Hbox        hbox_accountId_IntAc;           // autoWired
	protected Textbox     branchCode;                     // autoWired
	protected Textbox     acHead;                         // autoWired
	protected Textbox     acSeqNumber;                    // autoWired
	protected Textbox     ccyNumber;                      // autoWired

	// not auto wired variables
	private Accounts accounts; // over handed per parameters
	private transient AccountsListCtrl acountsListCtrl; // over handed per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient AccountsService accountsService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();

	/**
	 * default constructor.<br>
	 */
	public AccountsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountsDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Accounts object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AccountsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccountsDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("acounts")) {
				this.accounts = (Accounts) arguments.get("acounts");
				Accounts befImage = new Accounts();
				BeanUtils.copyProperties(this.accounts, befImage);
				this.accounts.setBefImage(befImage);
				setAcounts(this.accounts);
			} else {
				setAcounts(null);
			}
			doLoadWorkFlow(this.accounts.isWorkflow(),
					this.accounts.getWorkflowId(),
					this.accounts.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"AccountsDialog");
			}

			// READ OVERHANDED parameters !
			// we get the acountsListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete accounts here.
			if (arguments.containsKey("acountsListCtrl")) {
				setAcountsListCtrl((AccountsListCtrl) arguments
						.get("acountsListCtrl"));
			} else {
				setAcountsListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getAcounts());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AccountsDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		if(!this.accounts.isInternalAc()){
			this.acSeqNumber.setMaxlength(LengthConstants.LEN_ACCOUNT-
					(LengthConstants.LEN_BRANCH+LengthConstants.LEN_ACHEADCODE));
			this.acSeqNumber.setWidth("60px");
			this.space_AccountId.setStyle("background-color:white;");
		}else{
			this.space_AccountId.setStyle("background-color:red;");
			this.acSeqNumber.setMaxlength(LengthConstants.LEN_ACCOUNT-
					(LengthConstants.LEN_BRANCH+LengthConstants.LEN_ACHEADCODE+LengthConstants.LEN_CURRENCY));
			this.acSeqNumber.setWidth("40px");
		}
		if(this.accounts.isNew()){
			this.hbox_accountId_IntAc.setVisible(true);
			if(this.accounts.isInternalAc()){
				this.ccyNumber.setVisible(true);
			}
		}
		this.accountId.setMaxlength(18);
		this.branchCode.setMaxlength(LengthConstants.LEN_BRANCH);
		this.acHead.setMaxlength(4);
		this.ccyNumber.setMaxlength(3);
		this.acCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.acType.setMaxlength(15);
		this.acType.setReadonly(true);
		this.acPurpose.setReadonly(true);
		this.acBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		if(this.accounts.isInternalAc()){
			this.row_cust_Names.setVisible(false);
			this.row_custId.setVisible(false);	
		}
		this.acFullName.setMaxlength(50);
		this.acFullName.setReadonly(true);
		this.acShortName.setMaxlength(20);
		this.acShortName.setReadonly(true);
		this.acPrvDayBal.setMaxlength(18);
		this.acPrvDayBal.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.acPrvDayBal.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.acPrvDayBal.setScale(0);
		this.acTodayDr.setMaxlength(18);
		this.acTodayDr.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.acTodayDr.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.acTodayDr.setScale(0);
		this.acTodayCr.setMaxlength(18);
		this.acTodayCr.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.acTodayCr.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.acTodayCr.setScale(0);
		this.acTodayNet.setMaxlength(18);
		this.acTodayNet.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.acTodayNet.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.acTodayNet.setScale(0);
		this.acAccrualBal.setMaxlength(18);
		this.acAccrualBal.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.acAccrualBal.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.acAccrualBal.setScale(0);
		this.acTodayBal.setMaxlength(18);
		this.acTodayBal.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.acTodayBal.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.acTodayBal.setScale(0);
		this.acOpenDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.acOpenDate.setDisabled(true);
		if(this.accounts.getRecordType()!=null){
			if (!this.accounts.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				if (this.accounts.isNewRecord() || StringUtils.isNotEmpty(this.accounts.getRecordType())) {
					if(!(this.accounts.isNew() && this.accounts.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW))){
						this.row_Active_AcOpenDate.setVisible(false);
						this.row_Blocked_Closed.setVisible(false);
					}
				}
			}
		}else if(this.accounts.isNewRecord()){
			this.row_Active_AcOpenDate.setVisible(false);
			this.row_Blocked_Closed.setVisible(false);
		}
		this.acLastCustTrnDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.acLastSysTrnDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.hostAcNumber.setMaxlength(35);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountsDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountsDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_AccountsDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *			  An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.accounts.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAccounts
	 *            Accounts
	 */
	public void doWriteBeanToComponents(Accounts aAccounts) {
		logger.debug("Entering") ;
		this.accountId.setValue(aAccounts.getAccountId());
		if(aAccounts.isNew()){
			this.branchCode.setValue(aAccounts.getAcBranch());
			this.ccyNumber.setValue(aAccounts.getLovDescCcyNumber());
			this.acHead.setValue(aAccounts.getLovDescAcHeadCode());
		}
		this.acCcy.setValue(aAccounts.getAcCcy());
		this.lovDescAcCcy.setValue(aAccounts.getAcCcy()+"-"+aAccounts.getLovDescCurrency());
		this.acType.setValue(aAccounts.getAcType());
		this.lovDescAcTypeDesc.setValue(aAccounts.getAcType()+"-"+aAccounts.getLovDescAccTypeDesc());
		this.acBranch.setValue(aAccounts.getAcBranch());
		this.lovDescAcBranch.setValue(aAccounts.getAcBranch()+"-"+aAccounts.getLovDescBranchCodeName());
		this.acCustId.setValue(aAccounts.getAcCustId());
		this.lovDescAcCustCIF.setValue(aAccounts.getLovDescCustCIF());
		this.acFullName.setValue(aAccounts.getAcFullName());
		this.acShortName.setValue(aAccounts.getAcShortName());
		this.acPurpose.setValue(aAccounts.getAcPurpose());
		this.lovDescAcPurpose.setValue(PennantAppUtil.getlabelDesc(aAccounts.getAcPurpose()
				,PennantStaticListUtil.getAccountPurpose()));
		this.internalAc.setChecked(aAccounts.isInternalAc());
		this.custSysAc.setChecked(aAccounts.isCustSysAc());
		if(this.custSysAc.isChecked()){
			label_accTypeDesc.setValue(Labels.getLabel("label_CustSysAc"));
		}else if(this.internalAc.isChecked()){
			label_accTypeDesc.setValue(Labels.getLabel("label_InternalAc"));	
		}else if(!this.internalAc.isChecked() && !this.custSysAc.isChecked()){
			label_accTypeDesc.setValue(Labels.getLabel("label_CustAc"));	
		}
		this.acAccrualBal.setValue(PennantAppUtil.formateAmount(aAccounts.getShadowBal(),0));
		this.acTodayBal.setValue(PennantAppUtil.formateAmount(aAccounts.getAcBalance(),0));
		
		//FIXME: PV: 07MAY17: To be fixed when screen used
/*		this.acPrvDayBal.setValue(PennantAppUtil.formateAmount(aAccounts.getAcPrvDayBal(),0));
		this.acTodayDr.setValue(PennantAppUtil.formateAmount(aAccounts.getAcTodayDr(),0));
		this.acTodayCr.setValue(PennantAppUtil.formateAmount(aAccounts.getAcTodayCr(),0));
		this.acTodayNet.setValue(PennantAppUtil.formateAmount(aAccounts.getAcTodayNet(),0));
		this.acAccrualBal.setValue(PennantAppUtil.formateAmount(aAccounts.getAcAccrualBal(),0));
		this.acTodayBal.setValue(PennantAppUtil.formateAmount(aAccounts.getAcTodayBal(),0));
*/		this.acOpenDate.setValue(aAccounts.getAcOpenDate());
		this.acLastCustTrnDate.setValue(aAccounts.getAcLastCustTrnDate());
		this.acLastSysTrnDate.setValue(aAccounts.getAcLastSysTrnDate());
		this.acActive.setChecked(aAccounts.isAcActive());
		this.acBlocked.setChecked(aAccounts.isAcBlocked());
		this.acClosed.setChecked(aAccounts.isAcClosed());
		this.hostAcNumber.setValue(aAccounts.getHostAcNumber());

		this.recordStatus.setValue(aAccounts.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccounts
	 */
	public void doWriteComponentsToBean(Accounts aAccounts) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if(!this.accounts.isInternalAc() && this.accounts.isNew()){
				if(StringUtils.isBlank(this.acSeqNumber.getValue())){
					aAccounts.setAccountId(AccountNumberGeneration.genNewAcountNumber(aAccounts.getAcType()
							,aAccounts.getAcCcy(),aAccounts.getAcBranch()));
				}else{
					/*Here account sequence number length is 16-(branch length(5)+acHead(4) =7*/
					aAccounts.setAccountId(this.branchCode.getValue()+this.acHead.getValue()
							+StringUtils.leftPad(this.acSeqNumber.getValue().trim()
									,LengthConstants.LEN_ACCOUNT 
									-(LengthConstants.LEN_BRANCH+LengthConstants.LEN_ACHEADCODE),'0'));	
				}
			}else if(this.accounts.isInternalAc() && this.accounts.isNew()){
				/*Here account sequence number length is 16-(branch length(5)+acHead(4)+ccyNumber(3) =4*/
				aAccounts.setAccountId(this.branchCode.getValue()+this.acHead.getValue()
						+StringUtils.leftPad(this.acSeqNumber.getValue().trim()
								,LengthConstants.LEN_ACCOUNT 
								- (LengthConstants.LEN_BRANCH+LengthConstants.LEN_ACHEADCODE
										+LengthConstants.LEN_CURRENCY),'0')+this.ccyNumber.getValue());


			}else{
				aAccounts.setAccountId(this.accountId.getValue().trim());		
			}

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcCcy(this.acCcy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcType(this.acType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcBranch(this.acBranch.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aAccounts.setAcFullName(this.acFullName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcShortName(this.acShortName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcPurpose(this.acPurpose.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setInternalAc(this.internalAc.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setCustSysAc(this.custSysAc.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if(!aAccounts.isInternalAc()){
				aAccounts.setAcCustId(this.acCustId.getValue());
			}else{
				aAccounts.setAcCustId(0);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
 		
		//FIXME: PV: 07MAY17: To be fixed when screen used
/*		try {
			if(this.acPrvDayBal.getValue()!=null){
				aAccounts.setAcPrvDayBal(PennantAppUtil.unFormateAmount(this.acPrvDayBal.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
*/		try {
			if(this.acOpenDate.getValue()!=null){
				aAccounts.setAcOpenDate(new Timestamp(this.acOpenDate.getValue().getTime()));
			}	
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if(aAccounts.isNew()){
				aAccounts.setAcActive(true);
			}else{
				aAccounts.setAcActive(this.acActive.isChecked());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcBlocked(this.acBlocked.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setAcClosed(this.acClosed.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAccounts.setHostAcNumber(this.hostAcNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aAccounts.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAccounts
	 * @throws Exception
	 */
	public void doShowDialog(Accounts aAccounts) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aAccounts.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.accountId.focus();
		} else {
			this.acFullName.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAccounts);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AccountsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);


		if (!this.accounts.isInternalAc() && this.accounts.isNew()){	
			if(!StringUtils.isBlank(this.acSeqNumber.getValue())){
				this.acSeqNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AccountId.value"),
						PennantRegularExpressions.REGEX_NUMERIC, true));
			}
		}else if(this.accounts.isInternalAc() && this.accounts.isNew()){

			this.acSeqNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AccountId.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.acCcy.isReadonly()){
			this.acCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AcCcy.value"),null,true));
		}	
		if (!this.acType.isReadonly()){
			this.acType.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AcType.value"),null,true));
		}	
		if (!this.acBranch.isReadonly()){
			this.acBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AcBranch.value"),null,true));
		}	
		if(!this.accounts.isInternalAc()){
			if (!this.acCustId.isReadonly()){
				this.acCustId.setConstraint(new PTNumberValidator(Labels.getLabel("label_AcountsDialog_AcCustId.value"), true));
			}
			if (!this.acFullName.isReadonly()){
				this.acFullName.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AcFullName.value"),null,true));
			}	
			if (!this.acShortName.isReadonly()){
				this.acShortName.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_AcShortName.value"),null,true));
			}	
		}
		if (!this.acPrvDayBal.isReadonly()){
			this.acPrvDayBal.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_AcountsDialog_AcPrvDayBal.value"), 0, true));
		}	
		if (!this.acTodayDr.isReadonly()){
			this.acTodayDr.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_AcountsDialog_AcTodayDr.value"), 0, true));
		}	
		if (!this.acTodayCr.isReadonly()){
			this.acTodayCr.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_AcountsDialog_AcTodayCr.value"), 0, true));
		}	
		if (!this.acTodayNet.isReadonly()){
			this.acTodayNet.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_AcountsDialog_AcTodayNet.value"), 0, true));
		}	
		if (!this.acAccrualBal.isReadonly()){
			this.acAccrualBal.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_AcountsDialog_AcAccrualBal.value"), 0, true));
		}	
		if (!this.acTodayBal.isReadonly()){
			this.acTodayBal.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_AcountsDialog_AcTodayBal.value"), 0, true));
		}	
		if (!this.acOpenDate.isDisabled()){
			this.acOpenDate.setConstraint(new PTDateValidator(Labels.getLabel("label_AcountsDialog_AcOpenDate.value"),true));
		}
		if (!this.acLastCustTrnDate.isDisabled()){
			this.acLastCustTrnDate.setConstraint(new PTDateValidator(Labels.getLabel("label_AcountsDialog_AcLastCustTrnDate.value"),true));
		}
		if (!this.acLastSysTrnDate.isDisabled()){
			this.acLastSysTrnDate.setConstraint(new PTDateValidator(Labels.getLabel("label_AcountsDialog_AcLastSysTrnDate.value"),true));
		}
		if (!this.hostAcNumber.isReadonly()){
			this.hostAcNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_AcountsDialog_HostAcNumber.value"), PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.accountId.setConstraint("");
		this.acSeqNumber.setConstraint("");
		this.acCcy.setConstraint("");
		this.acType.setConstraint("");
		this.acBranch.setConstraint("");
		this.acCustId.setConstraint("");
		this.acFullName.setConstraint("");
		this.acShortName.setConstraint("");
		this.acPrvDayBal.setConstraint("");
		this.acTodayDr.setConstraint("");
		this.acTodayCr.setConstraint("");
		this.acTodayNet.setConstraint("");
		this.acAccrualBal.setConstraint("");
		this.acTodayBal.setConstraint("");
		this.acOpenDate.setConstraint("");
		this.acLastCustTrnDate.setConstraint("");
		this.acLastSysTrnDate.setConstraint("");
		this.hostAcNumber.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.accountId.setReadonly(true);
		this.acCcy.setReadonly(true);
		this.acType.setReadonly(true);
		this.acBranch.setReadonly(true);
		this.acCustId.setReadonly(true);
		this.acFullName.setReadonly(true);
		this.acShortName.setReadonly(true);
		this.acPurpose.setDisabled(true);
		this.internalAc.setDisabled(true);
		this.custSysAc.setDisabled(true);
		this.acPrvDayBal.setReadonly(true);
		this.acTodayDr.setReadonly(true);
		this.acTodayCr.setReadonly(true);
		this.acTodayNet.setReadonly(true);
		this.acAccrualBal.setReadonly(true);
		this.acTodayBal.setReadonly(true);
		this.acOpenDate.setDisabled(true);
		this.acLastCustTrnDate.setDisabled(true);
		this.acLastSysTrnDate.setDisabled(true);
		this.acActive.setDisabled(true);
		this.acBlocked.setDisabled(true);
		this.acClosed.setDisabled(true);
		this.hostAcNumber.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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

		this.accountId.setValue("");
		this.acCcy.setValue("");
		this.acType.setValue("");
		this.acBranch.setValue("");
		this.acCustId.setText("");
		this.acFullName.setValue("");
		this.acShortName.setValue("");
		this.acPurpose.setValue("");
		this.internalAc.setChecked(false);
		this.custSysAc.setChecked(false);
		this.acPrvDayBal.setValue("");
		this.acTodayDr.setValue("");
		this.acTodayCr.setValue("");
		this.acTodayNet.setValue("");
		this.acAccrualBal.setValue("");
		this.acTodayBal.setValue("");
		this.acOpenDate.setText("");
		this.acLastCustTrnDate.setText("");
		this.acLastSysTrnDate.setText("");
		this.acActive.setChecked(false);
		this.acBlocked.setChecked(false);
		this.acClosed.setChecked(false);
		this.hostAcNumber.setValue("");
		logger.debug("Leaving");
	}
	/**
	 * 
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.accountId.setErrorMessage("");
		this.acSeqNumber.setErrorMessage("");
		this.acCcy.setErrorMessage("");
		this.acType.setErrorMessage("");
		this.acBranch.setErrorMessage("");
		this.acCustId.setErrorMessage("");
		this.acFullName.setErrorMessage("");
		this.acShortName.setErrorMessage("");
		this.acPrvDayBal.setErrorMessage("");
		this.acTodayDr.setErrorMessage("");
		this.acTodayCr.setErrorMessage("");
		this.acTodayNet.setErrorMessage("");
		this.acAccrualBal.setErrorMessage("");
		this.acTodayBal.setErrorMessage("");
		this.acOpenDate.setErrorMessage("");
		this.acLastCustTrnDate.setErrorMessage("");
		this.acLastSysTrnDate.setErrorMessage("");
		this.hostAcNumber.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * 
	 */
	private void refreshList(){
		logger.debug("Entering ");
		final JdbcSearchObject<Accounts> soAcounts = getAcountsListCtrl().getSearchObj();
		getAcountsListCtrl().pagingAcountsList.setActivePage(0);
		getAcountsListCtrl().getPagedListWrapper().setSearchObject(soAcounts);
		if(getAcountsListCtrl().listBoxAcounts!=null){
			getAcountsListCtrl().listBoxAcounts.getListModel();
		}
		logger.debug("Leaving ");
	} 


	// CRUD operations

	/**
	 * Deletes a Accounts object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final Accounts aAccounts = new Accounts();
		BeanUtils.copyProperties(getAcounts(), aAccounts);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aAccounts.getAccountId();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aAccounts.getRecordType())){
				aAccounts.setVersion(aAccounts.getVersion()+1);
				aAccounts.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aAccounts.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aAccounts,tranType)){
					refreshList();
					closeDialog(); 
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAcounts().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.accountId.setVisible(false);

		}else{
			this.accountId.setVisible(true);
			this.accountId.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.acCcy.setReadonly(isReadOnly("AccountsDialog_acCcy"));
		this.acBranch.setReadonly(isReadOnly("AccountsDialog_acBranch"));
		this.acPrvDayBal.setReadonly(isReadOnly("AccountsDialog_acPrvDayBal"));
		this.acTodayDr.setReadonly(isReadOnly("AccountsDialog_acTodayDr"));
		this.acTodayCr.setReadonly(isReadOnly("AccountsDialog_acTodayCr"));
		this.acTodayNet.setReadonly(isReadOnly("AccountsDialog_acTodayNet"));
		this.acAccrualBal.setReadonly(isReadOnly("AccountsDialog_acAccrualBal"));
		this.acTodayBal.setReadonly(isReadOnly("AccountsDialog_acTodayBal"));
		this.acLastCustTrnDate.setDisabled(isReadOnly("AccountsDialog_acLastCustTrnDate"));
		this.acLastSysTrnDate.setDisabled(isReadOnly("AccountsDialog_acLastSysTrnDate"));
		this.acActive.setDisabled(isReadOnly("AccountsDialog_acInactive"));
		this.acBlocked.setDisabled(isReadOnly("AccountsDialog_acBlocked"));
		this.acClosed.setDisabled(isReadOnly("AccountsDialog_acClosed"));
		this.hostAcNumber.setReadonly(isReadOnly("AccountsDialog_hostAcNumber"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.accounts.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
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
		final Accounts aAccounts = new Accounts();
		BeanUtils.copyProperties(getAcounts(), aAccounts);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Accounts object with the components data
		doWriteComponentsToBean(aAccounts);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aAccounts.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAccounts.getRecordType())){
				aAccounts.setVersion(aAccounts.getVersion()+1);
				if(isNew){
					aAccounts.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aAccounts.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAccounts.setNewRecord(true);
				}
			}
		}else{
			aAccounts.setVersion(aAccounts.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
			if(accounts.isAcClosed()){
				accounts.setAcCloseDate(new Date(System.currentTimeMillis()));
			}else if(isNew){
				accounts.setAcOpenDate(new Date(System.currentTimeMillis()));
			}
		}
		// save it to database
		try {

			if(doProcess(aAccounts,tranType)){
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	/**
	 * 
	 * @param aAccounts
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(Accounts aAccounts,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aAccounts.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAccounts.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAccounts.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAccounts.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAccounts.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAccounts);
				}

				if (isNotesMandatory(taskId, aAccounts)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}


			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aAccounts.setTaskId(taskId);
			aAccounts.setNextTaskId(nextTaskId);
			aAccounts.setRoleCode(getRole());
			aAccounts.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aAccounts, tranType);

			String operationRefs = getServiceOperations(taskId, aAccounts);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aAccounts, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aAccounts, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		Accounts aAccounts = (Accounts) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getAccountsService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getAccountsService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getAccountsService().doApprove(auditHeader);

						if(aAccounts.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getAccountsService().doReject(auditHeader);
						if(aAccounts.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999
								, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AccountsDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_AccountsDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.accounts),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
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
	
	@Override
	protected String getReference() {
		return String.valueOf(this.accounts.getAccountId());
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

	public Accounts getAcounts() {
		return this.accounts;
	}

	public void setAcounts(Accounts acounts) {
		this.accounts = acounts;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public AccountsService getAccountsService() {
		return this.accountsService;
	}

	public void setAcountsListCtrl(AccountsListCtrl acountsListCtrl) {
		this.acountsListCtrl = acountsListCtrl;
	}

	public AccountsListCtrl getAcountsListCtrl() {
		return this.acountsListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}


	private AuditHeader getAuditHeader(Accounts aAccounts, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccounts.getBefImage(), aAccounts);   
		return new AuditHeader(aAccounts.getAccountId(),String.valueOf(aAccounts.getAcCustId()),aAccounts.getAccountId(),null,auditDetail,aAccounts.getUserDetails(),getOverideMap());
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.accounts);
	}


	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}


	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	
}
