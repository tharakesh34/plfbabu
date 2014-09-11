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
 * FileName    		:  IstisnaFinanceMainDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/IstisnaFinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class IstisnaFinanceMainDialogCtrl extends FinanceBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(IstisnaFinanceMainDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IstisnaFinanceMainDialog; 				// autoWired

	//Finance Main Details Tab---> 1. Key Details
	protected CurrencyBox 	securityDeposit;								// autoWired
	protected CurrencyBox 	downPayment; 									// autoWired
	protected Row 			row_downpayAcc;									// autoWired

	protected Label 		label_IstisnaFinanceMainDialog_CommitRef; 		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_CbbApproved;		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_DownPayment;		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_DepriFrq;		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_FrqDef;			// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_AlwGrace;		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_GraceMargin; 		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_StepPolicy; 		// autoWired
	protected Label 		label_IstisnaFinanceMainDialog_numberOfSteps; 	// autoWired


	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.
	//Finance Main Details Tab---> 1. Key Details
	private transient BigDecimal 	oldVar_securityDeposit;
	private transient BigDecimal 	oldVar_downPayment;

	//Sub Window Child Details Dialog Controllers
	private transient DisbursementDetailDialogCtrl disbursementDetailDialogCtrl = null;

	/**
	 * default constructor.<br>
	 */
	public IstisnaFinanceMainDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IstisnaFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) args.get("financeDetail"));
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(getFinanceDetail());
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (args.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) args.get("financeMainListCtrl"));
		} 
		
		if (args.containsKey("financeSelectCtrl")) {
			setFinanceSelectCtrl((FinanceSelectCtrl) args.get("financeSelectCtrl"));
		} 

		if (args.containsKey("tabbox")) {
			listWindowTab = (Tab) args.get("tabbox");
		}

		if (args.containsKey("moduleDefiner")) {
			moduleDefiner = (String) args.get("moduleDefiner");
		}

		if (args.containsKey("eventCode")) {
			eventCode = (String) args.get("eventCode");
		}
		
		if (args.containsKey("menuItemRightName")) {
			menuItemRightName = (String) args.get("menuItemRightName");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled()) {
			String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
			if(recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)){
				this.userAction = setRejectRecordStatus(this.userAction);
			}else {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateMenuRoleAuthorities(getRole(), "FinanceMainDialog", menuItemRightName);	
			}
		}else{
			this.south.setHeight("0px");
		}
		
		setMainWindow(window_IstisnaFinanceMainDialog);
		setLabel_FinanceMainDialog_CbbApproved(label_IstisnaFinanceMainDialog_CbbApproved);
		setLabel_FinanceMainDialog_CommitRef(label_IstisnaFinanceMainDialog_CommitRef);
		setLabel_FinanceMainDialog_DepriFrq(label_IstisnaFinanceMainDialog_DepriFrq);
		//setLabel_FinanceMainDialog_FinRepayPftOnFrq(label_IstisnaFinanceMainDialog_FinRepayPftOnFrq);
		setLabel_FinanceMainDialog_FrqDef(label_IstisnaFinanceMainDialog_FrqDef);
		setLabel_FinanceMainDialog_AlwGrace(label_IstisnaFinanceMainDialog_AlwGrace);
		setLabel_FinanceMainDialog_GraceMargin(label_IstisnaFinanceMainDialog_GraceMargin);
		setLabel_FinanceMainDialog_StepPolicy(label_IstisnaFinanceMainDialog_StepPolicy);
		setLabel_FinanceMainDialog_numberOfSteps(label_IstisnaFinanceMainDialog_numberOfSteps);
		this.downPayBank = downPayment;
		setProductCode("Istisna");

		
		/* set components visible dependent of the users rights */
		doCheckRights();

		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
		.getValue().intValue()- PennantConstants.borderlayoutMainNorth;

		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52+ "px");
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceDetail());

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		super.doSetFieldProperties();
		this.securityDeposit.setMandatory(false);
		this.securityDeposit.setMaxlength(18);
		this.securityDeposit.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getLovDescFinFormatter()));

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

		getUserWorkspace().alocateAuthorities("FinanceMainDialog",getRole(), menuItemRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnValidate"));
		this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd"));
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
	public void onClose$window_IstisnaFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		String recStatus = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		
		if(this.userAction.getSelectedItem() != null && !recStatus.equals(PennantConstants.RCD_STATUS_REJECTED) &&
				(this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED) ||
				this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_CANCELLED))){
		   doReject();
		}else{
		   doSave();
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When  record is rejected . <br>
	 * 
	 */
	
	public void doReject() throws InterruptedException{
		logger.debug("Entering");
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMain", getFinanceDetail().getFinScheduleData().getFinanceMain());
		map.put("financeMainDialogCtrl", this);
		try{
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceReject.zul",
					window_IstisnaFinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_IstisnaFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering " + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e.getMessage());
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * @throws Exception 
	 * 
	 */
	private void doClose() throws Exception {
		logger.debug("Entering ");
		boolean close = true;
		if (isDataChanged(true)) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
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
			closeDialog(this.window_IstisnaFinanceMainDialog, "FinanceMainDialog");
		}

		logger.debug("Leaving ");
	}

	private void closeWindow(){
		//De Allocate rights for Asset Details Tab Dialog
		if(childWindow != null){
			String dialogWindowName = getAssetDialogName();
			if(dialogWindowName != null){
				closeDialog((Window)childWindow, dialogWindowName);
			}
		}
		closeDialog(this.window_IstisnaFinanceMainDialog, "FinanceMainDialog");
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
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException, InterruptedException, AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		super.doWriteBeanToComponents(aFinanceDetail, onLoadProcess);

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
	 	this.securityDeposit.setValue(PennantAppUtil.formateAmount(aFinanceMain.getSecurityDeposit(), aFinanceMain.getLovDescFinFormatter()));
	 
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && 
				aFinanceDetail.getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			this.label_IstisnaFinanceMainDialog_DownPayment.setVisible(true);
			this.downPayment.setDisabled(isReadOnly("FinanceMainDialog_downPayment"));
			this.downPayment.setVisible(true);
			this.row_downpayAcc.setVisible(true);

			if (aFinanceMain.isNewRecord()) {
				this.downPayment.setValue(BigDecimal.ZERO);
				this.downPayAccount.setValue("");
				
			} else {
				this.downPayment.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayment(),
						aFinanceMain.getLovDescFinFormatter()));
				this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			}
		}
		setDownPayAcMand();
		
		//Filling Child Window Details Tabs
		doFillTabs(aFinanceDetail);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab,
	 * fee charges tab, accounting tab, agreements tab and additional field
	 * details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * 
	 */
	private void doFillTabs(FinanceDetail aFinanceDetail) throws ParseException, InterruptedException {
		logger.debug("Entering");

		if(isReadOnly("FinanceMainDialog_NoScheduleGeneration")){
			
			//Step Policy Details
			appendStepDetailTab(true);
			
			//Contributor details Tab Addition
			if(aFinanceDetail.getFinScheduleData().getFinanceMain().isNewRecord()){
				if(aFinanceDetail.getFinScheduleData().getFinanceType() != null && aFinanceDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
					isRIAExist = true;
				}else{
					if(aFinanceDetail.getFinScheduleData().getFinanceType() != null && 
							aFinanceDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
						isRIAExist = true;
					}
				}
			}else{
				if(aFinanceDetail.getFinScheduleData().getFinanceType() != null && 
						aFinanceDetail.getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
					isRIAExist = true;
				}
			}

			if(isRIAExist){
				appendContributorDetailsTab(true);
			}

			//Disbursement Detail Tab
			appendDisbursementDetailTab();
			
			//Schedule Details Tab Adding
			appendScheduleDetailTab(true, false);
		}

		//Joint Account and Guaranteer  Tab Addition
		if (!finDivision.equals(PennantConstants.FIN_DIVISION_COMMERCIAL)  && !finDivision.equals(PennantConstants.FIN_DIVISION_CORPORATE)) {
			if(moduleDefiner.equals("")){
				appendJointGuarantorDetailTab();
			}
		}
		//Asset Details Tab Addition
		if(moduleDefiner.equals("")){
			appendAssetDetailTab();
		}

		//Eligibility Details Tab Adding
		if(moduleDefiner.equals("")){
			appendEligibilityDetailTab(true);
		}

		//Scoring Detail Tab Addition
		if(moduleDefiner.equals("")){
			appendFinScoringDetailTab(true);
		}

		//Agreements Detail Tab Addition
		if(moduleDefiner.equals("")){
			appendAgreementsDetailTab(true);
		}

		//CheckList Details Tab Addition
		if(moduleDefiner.equals("")){
			boolean finIsNewRecord = getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord();
			appendCheckListDetailTab(getFinanceDetail(), finIsNewRecord, true);
		}

		//Recommend & Comments Details Tab Addition
		appendRecommendDetailTab(true);

		// Additional Detail Tab Dynamic Display
		if(moduleDefiner.equals("")){
			appendAddlDetailsTab();
		}

		// Document Detail Tab Addition
		appendDocumentDetailTab();

		//Fee Details Tab Addition
		appendFeeDetailsTab(true);
		
		//Stage Accounting details Tab Addition
		if(moduleDefiner.equals("")){
			appendStageAccountingDetailsTab(true);
		}

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if(getWorkFlow().getTaskTabs(getWorkFlow().getTaskId(getRole())).equals("Accounting")){
			//Accounting Details Tab Addition
			appendAccountingDetailTab(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData
	 *            (FinScheduleData)
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData) throws InterruptedException, IllegalAccessException, InvocationTargetException { 
		logger.debug("Entering");
		
 		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		doClearMessage();
 		doSetValidation();
		doSetLOVValidation();
		
		super.doWriteComponentsToBean(aFinanceSchData, wve);
		
		if(buildEvent) {
			if(getDisbursementDetailDialogCtrl() != null){
				aFinanceSchData.setDisbursementDetails(getDisbursementDetailDialogCtrl().getDisbursementDetails());	
			}
		}
		
		if(wve.isEmpty()){
			aFinanceSchData = super.doWriteSchData(aFinanceSchData, true);
		}

		//FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rest Total Contract Advance after Disbursements Added
	 * @param contractAdv
	 */
	public void setFinAmount(BigDecimal contractAdv){
		getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(contractAdv);
		this.finAmount.setValue(PennantAppUtil.formateAmount(contractAdv, 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		
		if(contractAdv.compareTo(BigDecimal.ZERO) > 0){
			this.finStartDate.setDisabled(true);
			this.gracePeriodEndDate.setDisabled(true);
			this.finCcy.setReadonly(true);
		}else{
			this.finStartDate.setDisabled(false);
			this.gracePeriodEndDate.setDisabled(false);
			this.finCcy.setReadonly(false);
		}
	}
	
	public List<Object> doValidateFinDetail(){
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(StringUtils.trimToEmpty(this.finCcy.getValue()).equals("")){
				throw new WrongValueException(this.finCcy, "Currency must not be Empty");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.finStartDate.getValue() == null){
				throw new WrongValueException(this.gracePeriodEndDate_two, "Start Date must not be Empty");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if ((this.gracePeriodEndDate_two.getValue() == null && this.gracePeriodEndDate.getValue() == null)) {
				throw new WrongValueException(this.gracePeriodEndDate_two, "Construction Completion Date must be greater than Start Date");
			}
			
			if(this.gracePeriodEndDate.getValue() != null && this.gracePeriodEndDate.getValue().compareTo(this.finStartDate.getValue()) <= 0){
				throw new WrongValueException(this.gracePeriodEndDate_two, "Construction Completion Date must be greater than Start Date");
			}
			
			if(this.gracePeriodEndDate.getValue() == null){
				if(this.gracePeriodEndDate_two.getValue() != null && 
						this.gracePeriodEndDate_two.getValue().compareTo(this.finStartDate.getValue()) <= 0){
					throw new WrongValueException(this.gracePeriodEndDate_two, "Construction Completion Date must be greater than Start Date");
				}
			}
			if(this.gracePeriodEndDate.getValue() != null){
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, this.financeTypeDetailsTab);
		
		List<Object> list = new ArrayList<Object>();
		list.add(this.finCcy.getValue());
		list.add(this.finStartDate.getValue());
		list.add(this.gracePeriodEndDate_two.getValue());
		
		logger.debug("Leaving");
		return list;
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			buildEvent = false;
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		// if afinanceMain == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (afinanceDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			afinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
			setFinanceDetail(afinanceDetail);
		} else {
			setFinanceDetail(afinanceDetail);
		}

		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitNew();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		// setFocus
		this.finReference.focus();
		
		//Reset Maintenance Buttons for finance modification
		if (!moduleDefiner.equals("")){
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
			this.btnValidate.setVisible(false);
			this.btnBuildSchedule.setVisible(false);
			afinanceDetail.getFinScheduleData().getFinanceMain().setCurDisbursementAmt(afinanceDetail.getFinScheduleData().getFinanceMain().getFinAmount());
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail,true);
			onCheckODPenalty(false);
			if(afinanceDetail.getFinScheduleData().getFinanceMain().isNew()){
				changeFrequencies();
				this.finReference.focus();
			}
			
			if(!isReadOnly("FinanceMainDialog_NoScheduleGeneration")){
				this.gb_gracePeriodDetails.setVisible(false);
				this.gb_repaymentDetails.setVisible(false);
				this.gb_OverDuePenalty.setVisible(false);
				if(this.numberOfTerms_two.intValue() == 0){
					this.numberOfTerms_two.setValue(1);
				}
				this.row_stepFinance.setVisible(false);
				this.row_manualSteps.setVisible(false);
			}

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDiscrepancy(getFinanceDetail());
			setDialog(this.window_IstisnaFinanceMainDialog);

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Disbursement Details Data in finance
	 */
	private void appendDisbursementDetailTab(){
		logger.debug("Entering");

		Tabpanel tabpanel = null;

		Tab tab = new Tab("Advance & Billing");
		tab.setId("disbursementTab");
		tabsIndexCenter.appendChild(tab);

		tabpanel = new Tabpanel();
		tabpanel.setId("disbursementTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 - 50 + "px");

		//Eligibility Detail Tab
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("profitDaysBasisList", profitDaysBasisList);
		map.put("roleCode", getRole());
		map.put("ccyFormatter", getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getLovDescFinFormatter());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DisbursementDetailDialog.zul", tabpanel, map);

		logger.debug("Leaving");
	}
		
	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteEligibilityDetail(){
		logger.debug("Entering");
		
		doSetValidation();
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.securityDeposit.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.custCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, financeTypeDetailsTab);
		
		/*getEligibilityDetailDialogCtrl().setEligible(true);
		getEligibilityDetailDialogCtrl().label_ElgRuleSummaryVal.setValue("");
		getEligibilityDetailDialogCtrl().doFillExecElgList(getFinanceDetail().getFinElgRuleList());
		getEligibilityDetailDialogCtrl().doFillEligibilityListbox(getFinanceDetail().getEligibilityRuleList(), true);*/
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Executing Eligibility Details
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws InterruptedException, AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.securityDeposit.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.custCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.finBranch.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.repayAcctId.validateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.downPayAccount.validateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");
		if(onLoadProcess){
			showErrorDetails(wve, financeTypeDetailsTab);
		}

		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

		//Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Executing Accounting tab Rules
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 * @throws InterruptedException 
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws AccountNotFoundException, IllegalAccessException,
		InvocationTargetException, InterruptedException{
		logger.debug("Entering");

		if(!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")){
			
			if(onLoadProcess){
				doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			}
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			DataSet dataSet = AEAmounts.createDataSet(finMain, eventCode, finMain.getFinStartDate(), finMain.getFinStartDate());

			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
			amountCodes = AEAmounts.procAEAmounts(getFinanceDetail().getFinScheduleData().getFinanceMain(),
					getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), new FinanceProfitDetail(), curBDay);

			setAmountCodes(amountCodes);
			
			List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
			
			// Loop Repetation for Multiple Disbursement
			if(getDisbursementDetailDialogCtrl() != null && getDisbursementDetailDialogCtrl().getDisbursementDetails() != null &&
					getDisbursementDetailDialogCtrl().getDisbursementDetails().size() > 0){

				List<FinanceDisbursement> disbList = sortDisbDetails(getDisbursementDetailDialogCtrl().getDisbursementDetails());

				Map<Long, BigDecimal> advPendingDueMap = new HashMap<Long, BigDecimal>();			
				for (FinanceDisbursement disbursement : disbList) {
					
					//Stop Posting Process for future Disbursements
					if(disbursement.getDisbDate().after((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"))){
						if("B".equals(disbursement.getDisbType())){
							continue;						
						}
					}

					if(eventCode.equals("")){
						if (disbursement.getDisbDate().after((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"))) {
							dataSet.setFinEvent("ADDDBSF");
						} else {
							dataSet.setFinEvent("ADDDBSP");
						}
					}

					dataSet.setDisburseAccount(disbursement.getDisbAccountId());
					dataSet.setDisburseAmount(disbursement.getDisbAmount());
					dataSet.setCurDisbRet(disbursement.getDisbRetAmount());
					dataSet.setNetRetDue(disbursement.getNetRetDue());
					dataSet.setClaimAmt(disbursement.getDisbClaim());
					dataSet.setAdvDue(BigDecimal.ZERO);

					//Net Customer Advance Amount Calculation
					BigDecimal netAdvDue = BigDecimal.ZERO;
					if(eventCode.equals("")){
						if("B".equals(disbursement.getDisbType())){
							dataSet.setFinEvent("ISTBILL");//TODO--- Hard code FIXME

							if(advPendingDueMap.containsKey(disbursement.getDisbBeneficiary())){
								netAdvDue = advPendingDueMap.get(disbursement.getDisbBeneficiary());
							}

							BigDecimal balAdv = netAdvDue.subtract(disbursement.getNetAdvDue());
							if(balAdv.compareTo(BigDecimal.ZERO) > 0){
								dataSet.setAdvDue(balAdv);
								balAdv = BigDecimal.ZERO;
							}

							advPendingDueMap.put(disbursement.getDisbBeneficiary(), balAdv);

						}else if("A".equals(disbursement.getDisbType())){

							if(advPendingDueMap.containsKey(disbursement.getDisbBeneficiary())){
								netAdvDue = advPendingDueMap.get(disbursement.getDisbBeneficiary());
							}
							netAdvDue = netAdvDue.add(disbursement.getNetAdvDue());
							advPendingDueMap.put(disbursement.getDisbBeneficiary(), netAdvDue);
						}

						dataSet.setGrcPftTillNow(calculateTillGrcProfit(getFinanceDetail().getFinScheduleData(), disbursement.getDisbDate()));
					}
					
					List<ReturnDataSet> returnSetEntries = null;
					if(!isRIAExist){
						returnSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, 
								getAmountCodes(), "N", getFeeDetailDialogCtrl() != null ? getFeeDetailDialogCtrl().getFeeRuleDetailsMap() : null,
										false, getFinanceDetail().getFinScheduleData().getFinanceType());
					}else{

						List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(
								getContributorDetailsDialogCtrl() == null? null: getContributorDetailsDialogCtrl().getContributorsList(), 
										dataSet.getFinReference());
						returnSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataSet, 
								getAmountCodes(), "N", riaDetailList);
					}

					accountingSetEntries.addAll(returnSetEntries);
				}
			}
			getFinanceDetail().setReturnDataSetList(accountingSetEntries);
			if(getAccountingDetailDialogCtrl() != null){
				getAccountingDetailDialogCtrl().doFillAccounting(accountingSetEntries);
			}
		}
		
		if(getAccountingDetailDialogCtrl() != null){
			if("".equals(moduleDefiner) && !StringUtils.trimToEmpty(this.commitmentRef.getValue()).equals("")){

				Commitment commitment = getCommitmentService().getApprovedCommitmentById(this.commitmentRef.getValue());
				FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

				AECommitment aeCommitment = new AECommitment();
				aeCommitment.setCMTAMT(commitment.getCmtAmount());
				aeCommitment.setCHGAMT(commitment.getCmtCharges());
				aeCommitment.setDISBURSE(CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(),finMain.getFinAmount()));
				aeCommitment.setRPPRI(BigDecimal.ZERO);

				getFinanceDetail().setCmtDataSetList(getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTDISB", "N", null));

				getAccountingDetailDialogCtrl().doFillCmtAccounting(getFinanceDetail().getCmtDataSetList(), commitment.getCcyEditField());
			}
		}
		
		logger.debug("Leaving");
	}
	
	public List<FinanceDisbursement> sortDisbDetails(
	        List<FinanceDisbursement> financeDisbursement) {

		if (financeDisbursement != null && financeDisbursement.size() > 0) {
			Collections.sort(financeDisbursement, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					if (detail1.getDisbDate().after(detail2.getDisbDate()) && detail1.getDisbType().compareTo(detail2.getDisbType()) > 0) {
						return 1;
					}
					return 0;
				}
			});
		}

		return financeDisbursement;
	}
	
	/**
	 * Method for Calculate Grace Profit Till 
	 * @return
	 */
	private BigDecimal calculateTillGrcProfit(FinScheduleData scheduleData, Date disbDate){
		logger.debug("Entering");
		
		BigDecimal totGrcPftTillNow = BigDecimal.ZERO;
		List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
		for (FinanceScheduleDetail curSchdl : list) {
			if(curSchdl.getSchDate().compareTo(disbDate) <= 0){
				totGrcPftTillNow = totGrcPftTillNow.add(curSchdl.getProfitCalc());
			}else{
				break;
			}
		}
		logger.debug("Leaving");
		return totGrcPftTillNow;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Method for Checking Details whether Fees Are reexecute or not
	 */
	private void doCheckFeeReExecution(){

		if(!isFinValidated){
			isFeeReExecute = false;
		}

		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();

		BigDecimal old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal old_dwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal new_dwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (old_dwnPayBank.compareTo(new_dwnPayBank) != 0) {
			isFeeReExecute = true;
		}

		Date maturDate = null;
		if(this.maturityDate.getValue() != null){
			maturDate = this.maturityDate.getValue();
		}else{
			maturDate = this.maturityDate_two.getValue();
		}
		
		int months = DateUtility.getMonthsBetween(maturDate , this.finStartDate.getValue(), true);
		if (months != this.oldVar_tenureInMonths) {
			isFeeReExecute = true;
		}
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		doClearMessage();

		//FinanceMain Details Tab ---> 1. Basic Details

		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finContractDate = this.finContractDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_securityDeposit = this.securityDeposit.getValue();
		this.oldVar_downPayment = this.downPayment.getValue();
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_finBranch = this.finBranch.getValue();
		this.oldVar_lovDescFinBranchName = this.finBranch.getDescription();
		this.oldVar_repayAcctId = this.repayAcctId.getValue();
		this.oldVar_disbAcctId = this.disbAcctId.getValue();
		this.oldVar_downPayAccount = this.downPayAccount.getValue();
		this.oldVar_commitmentRef = this.commitmentRef.getValue();
		this.oldVar_finPurpose = this.finPurpose.getValue();
		this.oldVar_finRemarks = this.finRemarks.getValue();
		this.oldVar_depreciationFrq = this.depreciationFrq.getValue();

		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_allowGrace  = this.allowGrace.isChecked();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_allowGrcRepay = this.allowGrcRepay.isChecked();
			this.oldVar_graceBaseRate = this.graceBaseRate.getValue();
			this.oldVar_lovDescGraceBaseRateName = this.lovDescGraceBaseRateName.getValue();
			this.oldVar_graceSpecialRate = this.graceSpecialRate.getValue();
			this.oldVar_lovDescGraceSpecialRateName = this.lovDescGraceSpecialRateName.getValue();
			this.oldVar_gracePftRate = this.gracePftRate.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_grcMargin = this.grcMargin.getValue();
			this.oldVar_grcPftDaysBasis = this.grcPftDaysBasis.getSelectedIndex();
			this.oldVar_allowGrcInd = this.allowGrcInd.isChecked();
			this.oldVar_grcIndBaseRate = this.grcIndBaseRate.getValue();
			this.oldVar_lovDescGrcIndBaseRateName = this.lovDescGrcIndBaseRateName.getValue();
			this.oldVar_graceTerms = this.graceTerms_Two.intValue();

		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayBaseRate.getValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_lovDescRepayBaseRateName = this.lovDescRepayBaseRateName.getValue();
		this.oldVar_repaySpecialRate = this.repaySpecialRate.getValue();
		this.oldVar_lovDescRepaySpecialRateName = this.lovDescRepaySpecialRateName.getValue();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue();
		this.oldVar_repayMargin = this.repayMargin.getValue();
		this.oldVar_scheduleMethod = this.cbScheduleMethod.getSelectedIndex();
		this.oldVar_allowRpyInd = this.allowRpyInd.isChecked();
		this.oldVar_rpyIndBaseRate = this.rpyIndBaseRate.getValue();
		this.oldVar_lovDescRpyIndBaseRateName = this.lovDescRpyIndBaseRateName.getValue();
		this.oldVar_repayFrq = this.repayFrq.getValue();
		this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
		this.oldVar_repayPftFrq = this.repayPftFrq.getValue();
		this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
		this.oldVar_repayRvwFrq = this.repayRvwFrq.getValue();
		this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
		this.oldVar_repayCpzFrq = this.repayCpzFrq.getValue();
		this.oldVar_nextRepayCpzDate = this.nextRepayCpzDate_two.getValue();
		this.oldVar_maturityDate = this.maturityDate_two.getValue();
		this.oldVar_finRepaymentAmount = this.finRepaymentAmount.getValue();
		this.oldVar_finRepayPftOnFrq = this.finRepayPftOnFrq.isChecked();
		this.oldVar_finRepayMethod = this.finRepayMethod.getSelectedIndex();
		
		Date maturDate = null;
		if(this.maturityDate.getValue() != null){
			maturDate = this.maturityDate.getValue();
		}else{
			maturDate = this.maturityDate_two.getValue();
		}
		
		int months = DateUtility.getMonthsBetween(maturDate , this.finStartDate.getValue(), true);
		this.oldVar_tenureInMonths = months;
		
		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		if(getFinanceDetail().getFinScheduleData().getFinanceType().isApplyODPenalty() && this.gb_OverDuePenalty.isVisible()) {
	    	this.oldVar_applyODPenalty = this.applyODPenalty.isChecked();
		    this.oldVar_oDIncGrcDays = this.oDIncGrcDays.isChecked();
		    this.oldVar_oDChargeType = getComboboxValue(this.oDChargeType);
		    this.oldVar_oDGraceDays = this.oDGraceDays.intValue();
		    this.oldVar_oDChargeCalOn = getComboboxValue(this.oDChargeCalOn);
		    this.oldVar_oDChargeAmtOrPerc = this.oDChargeAmtOrPerc.getValue();
		    this.oldVar_oDAllowWaiver = oDAllowWaiver.isChecked();
		    this.oldVar_oDMaxWaiverPerc = this.oDMaxWaiverPerc.getValue();
		}
		
		super.oldVar_finStepPolicyList = getFinanceDetail().getFinScheduleData().getStepPolicyDetails();
		this.oldVar_recordStatus = this.recordStatus.getValue();

		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setValue(this.oldVar_finReference);
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finContractDate.setValue(this.oldVar_finContractDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.securityDeposit.setValue(this.oldVar_securityDeposit);
		this.downPayment.setValue(this.oldVar_downPayment);
		this.finRepaymentAmount.setValue(this.oldVar_finRepaymentAmount);
		this.custID.setValue(this.oldVar_custID);
		this.finBranch.setValue(this.oldVar_finBranch);
		this.finBranch.setDescription(this.oldVar_lovDescFinBranchName);
		this.repayAcctId.setValue(this.oldVar_repayAcctId);
		this.downPayAccount.setValue(this.oldVar_downPayAccount);
		this.commitmentRef.setValue(this.oldVar_commitmentRef);
		this.finPurpose.setValue(this.oldVar_finPurpose);

		// Step Finance Details
		this.stepFinance.setChecked(this.oldVar_stepFinance);
		this.stepPolicy.setValue(this.oldVar_stepPolicy);
		this.alwManualSteps.setChecked(this.oldVar_alwManualSteps);
		this.noOfSteps.setValue(this.oldVar_noOfSteps);

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.cbGrcSchdMthd.setSelectedIndex(this.oldVar_grcSchdMthd);
			this.grcRateBasis.setSelectedIndex(this.oldVar_grcRateBasis);
			this.allowGrcRepay.setChecked(this.oldVar_allowGrcRepay);
			this.graceBaseRate.setValue(this.oldVar_graceBaseRate);
			this.lovDescGraceBaseRateName.setValue(this.oldVar_lovDescGraceBaseRateName);
			this.graceSpecialRate.setValue(this.oldVar_graceSpecialRate);
			this.lovDescGraceSpecialRateName.setValue(this.oldVar_lovDescGraceSpecialRateName);
			this.gracePftRate.setValue(this.oldVar_gracePftRate);
			this.gracePftFrq.setValue(this.oldVar_gracePftFrq);
			this.nextGrcPftDate_two.setValue(this.oldVar_nextGrcPftDate);
			this.gracePftRvwFrq.setValue(this.oldVar_gracePftRvwFrq);
			this.nextGrcPftRvwDate_two.setValue(this.oldVar_nextGrcPftRvwDate);
			this.graceCpzFrq.setValue(this.oldVar_graceCpzFrq);
			this.nextGrcCpzDate_two.setValue(this.oldVar_nextGrcCpzDate);
			this.grcMargin.setValue(this.oldVar_grcMargin);
			this.grcPftDaysBasis.setSelectedIndex(this.oldVar_grcPftDaysBasis);
			this.allowGrcInd.setChecked(this.oldVar_allowGrcInd);
			this.lovDescGrcIndBaseRateName.setValue(this.oldVar_lovDescGrcIndBaseRateName);
			this.graceTerms.setValue(this.oldVar_graceTerms);
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.repayRateBasis.setSelectedIndex(this.oldVar_repayRateBasis);
		this.repayBaseRate.setValue(this.oldVar_repayBaseRate);
		this.lovDescRepayBaseRateName.setValue(this.oldVar_lovDescRepayBaseRateName);
		this.repaySpecialRate.setValue(this.oldVar_repaySpecialRate);
		this.lovDescRepaySpecialRateName.setValue(this.oldVar_lovDescRepaySpecialRateName);
		this.repayProfitRate.setValue(this.oldVar_repayProfitRate);
		this.repayMargin.setValue(this.oldVar_repayMargin);
		this.cbScheduleMethod.setSelectedIndex(this.oldVar_scheduleMethod);
		this.allowRpyInd.setChecked(this.oldVar_allowRpyInd);
		this.lovDescRpyIndBaseRateName.setValue(this.oldVar_lovDescRpyIndBaseRateName);
		this.repayFrq.setValue(this.oldVar_repayFrq);
		this.nextRepayDate_two.setValue(this.oldVar_nextRepayDate);
		this.repayPftFrq.setValue(this.oldVar_repayPftFrq);
		this.nextRepayPftDate_two.setValue(this.oldVar_nextRepayPftDate);
		this.repayRvwFrq.setValue(this.oldVar_repayRvwFrq);
		this.nextRepayRvwDate_two.setValue(this.oldVar_nextRepayRvwDate);
		this.repayCpzFrq.setValue(this.oldVar_repayCpzFrq);
		this.nextRepayCpzDate_two.setValue(this.oldVar_nextRepayCpzDate);
		this.maturityDate.setValue(this.oldVar_maturityDate);
		this.finRepayPftOnFrq.setChecked(this.oldVar_finRepayPftOnFrq);
		this.finRepayMethod.setSelectedIndex(this.oldVar_finRepayMethod);
		
		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		this.applyODPenalty.setChecked(this.oldVar_applyODPenalty);
		this.oDIncGrcDays.setChecked(this.oldVar_oDIncGrcDays);
		fillComboBox(this.oDChargeType, this.oldVar_oDChargeType, PennantStaticListUtil.getODCChargeType(), "");
		this.oDGraceDays.setValue(this.oldVar_oDGraceDays);
		fillComboBox(this.oDChargeCalOn, this.oldVar_oDChargeCalOn, PennantStaticListUtil.getODCChargeType(), "");
		this.oDChargeAmtOrPerc.setValue(this.oldVar_oDChargeAmtOrPerc);
		this.oDAllowWaiver.setChecked(this.oldVar_oDAllowWaiver);
		this.oDMaxWaiverPerc.setValue(this.oldVar_oDMaxWaiverPerc);

		this.recordStatus.setValue(this.oldVar_recordStatus);

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
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();
		if(super.isDataChanged(close)){
			return true;
		}
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();

		BigDecimal old_securityDeposit = PennantAppUtil.unFormateAmount(this.oldVar_securityDeposit, formatter);
		BigDecimal new_securityDeposit = PennantAppUtil.unFormateAmount(this.securityDeposit.getValue(), formatter);
		if (old_securityDeposit.compareTo(new_securityDeposit) != 0) {
			return true;
		}
	 
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isSchdlRegenerate() {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();

		//FinanceMain Details Tab ---> 1. Basic Details

		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();

		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}

		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}

		BigDecimal old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			return true;
		}
		
		BigDecimal old_securityDeposit = PennantAppUtil.unFormateAmount(this.oldVar_securityDeposit, formatter);
		BigDecimal new_securityDeposit = PennantAppUtil.unFormateAmount(this.securityDeposit.getValue(), formatter);
		if (old_securityDeposit.compareTo(new_securityDeposit) != 0) {
			return true;
		}

		if (this.gracePeriodEndDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue()) != 0) {
			return true;
		}
		
		// Step Finance Details
		if (this.oldVar_stepFinance != this.stepFinance.isChecked()) {
			return true;
		}
		if (!this.oldVar_stepPolicy.equals(this.stepPolicy.getValue())) {
			return true;
		}
		if (this.oldVar_alwManualSteps != this.alwManualSteps.isChecked()) {
			return true;
		}
		if (this.oldVar_noOfSteps != this.noOfSteps.intValue()) {
			return true;
		}

		// Step Finance Details List Validation
		if(getStepDetailDialogCtrl() != null && 
				getStepDetailDialogCtrl().getFinStepPoliciesList() != this.oldVar_finStepPolicyList){
			return true;
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {
			if (this.oldVar_allowGrace != this.allowGrace.isChecked()) {
				return true;
			}
			if (this.graceTerms.intValue() != 0) {
				if (this.oldVar_graceTerms != this.graceTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_graceTerms != this.graceTerms_Two.intValue()) {
				return true;
			}
			if (this.oldVar_graceBaseRate != this.graceBaseRate.getValue()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceSpecialRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
				return true;
			}
			if (this.oldVar_grcMargin != this.grcMargin.getValue()) {
				return true;
			}
			if(this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()){
				return true;
			}
			if(this.oldVar_allowGrcInd != this.allowGrcInd.isChecked()){
				return true;
			}
			if (this.oldVar_grcIndBaseRate != this.grcIndBaseRate.getValue()) {
				return true;
			}
			if (this.nextGrcPftDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_gracePftRvwFrq != this.gracePftRvwFrq.getValue()) {
				return true;
			}
			if (this.nextGrcPftRvwDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue()) != 0) {
				return true;
			}

			if (this.oldVar_graceCpzFrq != this.graceCpzFrq.getValue()) {
				return true;
			}
			if (this.nextGrcCpzDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_allowGrcRepay != this.allowGrcRepay.isChecked()) {
				return true;
			}
			if (this.oldVar_grcSchdMthd != this.cbGrcSchdMthd.getSelectedIndex()) {
				return true;
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.numberOfTerms.intValue() != 0) {
			if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
				return true;
			}
		} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
			return true;
		}

		BigDecimal old_finRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount, formatter);
		BigDecimal new_finRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(), formatter);

		if (old_finRepayAmount.compareTo(new_finRepayAmount) != 0) {
			return true;
		}
		if (this.oldVar_repayFrq != this.repayFrq.getValue()) {
			return true;
		}
		if (this.nextRepayDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue()) != 0) {
			return true;
		}
		if (this.maturityDate.getValue() != null ) {
			if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue()) != 0) {
			return true;
		}

		BigDecimal old_dwnPayment = PennantAppUtil.unFormateAmount(this.oldVar_downPayment, formatter);
		BigDecimal new_dwnPayment = PennantAppUtil.unFormateAmount(this.downPayment.getValue(), formatter);
		if (old_dwnPayment.compareTo(new_dwnPayment) != 0) {
			return true;
		}
		
		if (this.oldVar_repayBaseRate != this.repayBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repaySpecialRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			if (this.repayProfitRate.getValue().intValue() > 0) {
				return true;
			}
		}
		if (this.oldVar_repayMargin != this.repayMargin.getValue()) {
			return true;
		}
		if (this.oldVar_scheduleMethod != this.cbScheduleMethod.getSelectedIndex()) {
			return true;
		}
		if(this.oldVar_allowRpyInd != this.allowRpyInd.isChecked()){
			return true;
		}
		if (this.oldVar_rpyIndBaseRate != this.rpyIndBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayPftFrq != this.repayPftFrq.getValue()) {
			return true;
		}
		if (this.nextRepayPftDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayRvwFrq != this.repayRvwFrq.getValue()) {
			return true;
		}
		if (this.nextRepayRvwDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayCpzFrq != this.repayCpzFrq.getValue()) {
			return true;
		}
		if (this.nextRepayCpzDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue()) != 0) {
			return true;
		}		

		if(!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()){
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		//FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && 
				!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {

			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_FinReference.value") }));
		}

		if (!this.finAmount.isDisabled()) {
			this.finAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_IstisnaFinanceMainDialog_FinAmount.value"), false));
		}
		
		/*if (!this.securityDeposit.isDisabled()) {
			this.securityDeposit.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_IstisnaFinanceMainDialog_SecurityDeposit.value"), false));
		}*/
		
		if(!this.stepPolicy.isReadonly() && this.stepFinance.isChecked() && !this.alwManualSteps.isChecked()){
			this.stepPolicy.setConstraint(new PTStringValidator( Labels.getLabel("label_IstisnaFinanceMainDialog_StepPolicy.value"), null, true));
		}
        
		if(!this.noOfSteps.isReadonly() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()){
			this.noOfSteps.setConstraint(new PTNumberValidator(Labels.getLabel("label_IstisnaFinanceMainDialog_NumberOfSteps.value"), true, false));
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.graceTerms.isReadonly()) {
				this.graceTerms_Two.setConstraint("NO NEGATIVE,NO ZERO:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_GraceTerms.value") }));
			}	
			
			if (!this.grcMargin.isDisabled()) {
				this.grcMargin.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_IstisnaFinanceMainDialog_GraceMargin.value"), true));
			}

			if(this.allowGrace.isChecked()){
				this.grcEffectiveRate.setConstraint(new RateValidator(13, 9,
						Labels.getLabel("label_IstisnaFinanceMainDialog_GracePftRate.value"), true));
			}

			if (!this.nextGrcPftDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_NextGrcPftDate.value") }));
			}

			if (!this.nextGrcPftRvwDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_NextGrcPftRvwDate.value") }));
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.nextRepayDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_NextRepayDate.value") }));
		}

		if (!this.nextRepayPftDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_NextRepayPftDate.value") }));
		}

		if (!this.nextRepayRvwDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_NextRepayRvwDate.value") }));
		}

		if (!this.nextRepayCpzDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate_two.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_NextRepayCpzDate.value") }));
		}

		this.repayEffectiveRate.setConstraint(new RateValidator(13, 9,
				Labels.getLabel("label_IstisnaFinanceMainDialog_ProfitRate.value"), true));

		if (!this.repayMargin.isDisabled()) {
			this.repayMargin.setConstraint(new RateValidator(13, 9, 
					Labels.getLabel("label_IstisnaFinanceMainDialog_RepayMargin.value"), true));
		}
		
		if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
			
			this.maturityDate_two.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_MaturityDate.value") }));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setConstraint("");
		this.cbProfitDaysBasis.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finContractDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.securityDeposit.setConstraint("");
		this.downPayment.setConstraint("");
		//M_ this.custID.setConstraint("");
		this.finBranch.setConstraint("");
		this.repayAcctId.setConstraint("");
		this.downPayAccount.setConstraint("");
		this.commitmentRef.setConstraint("");
		
		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.graceTerms.setConstraint("");
		this.graceTerms_Two.setConstraint("");
		this.grcRateBasis.setConstraint("");
		this.gracePeriodEndDate.setConstraint("");
		this.cbGrcSchdMthd.setConstraint("");
		this.gracePftRate.setConstraint("");
		this.grcEffectiveRate.setConstraint("");
		this.grcMargin.setConstraint("");
		this.grcPftDaysBasis.setConstraint("");
		this.cbGracePftFrqCode.setConstraint("");
		this.cbGracePftFrqMth.setConstraint("");
		this.cbGracePftFrqDay.setConstraint("");
		this.gracePftFrq.setConstraint("");
		this.nextGrcPftDate.setConstraint("");
		this.cbGracePftRvwFrqCode.setConstraint("");
		this.cbGracePftRvwFrqMth.setConstraint("");
		this.cbGracePftRvwFrqDay.setConstraint("");
		this.gracePftRvwFrq.setConstraint("");
		this.nextGrcPftRvwDate.setConstraint("");
		this.cbGraceCpzFrqCode.setConstraint("");
		this.cbGraceCpzFrqMth.setConstraint("");
		this.cbGraceCpzFrqDay.setConstraint("");
		this.graceCpzFrq.setConstraint("");
		this.nextGrcCpzDate.setConstraint("");

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.finRepaymentAmount.setConstraint("");
		this.repayProfitRate.setConstraint("");
		this.repayEffectiveRate.setConstraint("");
		this.repayMargin.setConstraint("");
		this.cbScheduleMethod.setConstraint("");
		this.cbRepayFrqCode.setConstraint("");
		this.cbRepayFrqMth.setConstraint("");
		this.cbRepayFrqDay.setConstraint("");
		this.repayFrq.setConstraint("");
		this.nextRepayDate.setConstraint("");
		this.cbRepayPftFrqCode.setConstraint("");
		this.cbRepayPftFrqMth.setConstraint("");
		this.cbRepayPftFrqDay.setConstraint("");
		this.repayPftFrq.setConstraint("");
		this.nextRepayPftDate.setConstraint("");
		this.cbRepayRvwFrqCode.setConstraint("");
		this.cbRepayRvwFrqMth.setConstraint("");
		this.cbRepayRvwFrqDay.setConstraint("");
		this.repayRvwFrq.setConstraint("");
		this.nextRepayRvwDate.setConstraint("");
		this.cbRepayCpzFrqCode.setConstraint("");
		this.cbRepayCpzFrqMth.setConstraint("");
		this.cbRepayCpzFrqDay.setConstraint("");
		this.repayCpzFrq.setConstraint("");
		this.nextRepayCpzDate.setConstraint("");
		this.maturityDate.setConstraint("");
		this.maturityDate_two.setConstraint("");
		this.finRepayMethod.setConstraint("");
		
		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		this.oDChargeCalOn.setConstraint("");
		this.oDChargeType.setConstraint("");
		this.oDChargeAmtOrPerc.setConstraint("");
		this.oDMaxWaiverPerc.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to set validation on LOV fields
	 * */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_FinType.value") }));

		this.finCcy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_FinCcy.value") }));

		if (!this.finBranch.isReadonly()) {
			this.finBranch.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_FinBranch.value") }));
		}

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_CustID.value") }));
		}

		if (!recSave && this.repayAcctId.getSclass().equals("mandatory")) {
			this.repayAcctId.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_RepayAcctId.value") }));
		}

		if (!recSave && this.downPayAccount.getSclass().equals("mandatory")) {
			this.downPayAccount.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_DownPayAccount.value") }));
		}
		
		if (!this.btnSearchCommitmentRef.isDisabled() && 
				StringUtils.trimToEmpty(space_commitmentRef.getSclass()).equals("mandatory")) {
			this.lovDescCommitmentRefName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_CommitRef.value") }));
		}
		
		if (!this.finPurpose.isReadonly()) {
			this.finPurpose.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FinPurpose.value") }));
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		if(!this.btnSearchGraceBaseRate.isDisabled()) {
			this.lovDescGraceBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_GraceBaseRate.value") }));
		}

		if(this.allowGrcInd.isChecked() && !this.btnSearchGrcIndBaseRate.isDisabled()){
			this.lovDescGrcIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_IstisnaFinanceMainDialog_FinGrcIndBaseRate.value")}));			
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if(!this.btnSearchRepayBaseRate.isDisabled()) {
			this.lovDescRepayBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_RepayBaseRate.value") }));
		}

		if(this.allowRpyInd.isChecked() && !this.btnSearchRpyIndBaseRate.isDisabled()){
			this.lovDescRpyIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_IstisnaFinanceMainDialog_FinRpyIndBaseRate.value")}));			
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 * **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");
		this.finBranch.setConstraint("");
		this.custCIF.setConstraint("");
		this.lovDescCommitmentRefName.setConstraint("");
		this.finPurpose.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.lovDescGraceBaseRateName.setConstraint("");
		this.lovDescGraceSpecialRateName.setConstraint("");
		this.lovDescGrcIndBaseRateName.setConstraint("");

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.lovDescRepayBaseRateName.setConstraint("");
		this.lovDescRepaySpecialRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages.
	 * */
	public void doClearMessage() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finContractDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.securityDeposit.setErrorMessage("");
		this.downPayment.setErrorMessage("");
		//M_ this.custID.setErrorMessage("");
		this.finBranch.setErrorMessage("");
		this.repayAcctId.setErrorMessage("");
		this.downPayAccount.setErrorMessage("");
		this.commitmentRef.setErrorMessage("");
		this.lovDescCommitmentRefName.setErrorMessage("");
		this.finPurpose.setErrorMessage("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.lovDescGraceBaseRateName.setErrorMessage("");
		this.lovDescGraceSpecialRateName.setErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcEffectiveRate.setErrorMessage("");
		this.grcMargin.setErrorMessage("");
		this.gracePftFrq.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");
		this.gracePftRvwFrq.setErrorMessage("");
		this.nextGrcPftRvwDate.setErrorMessage("");
		this.graceCpzFrq.setErrorMessage("");
		this.nextGrcCpzDate.setErrorMessage("");
		this.cbGrcSchdMthd.setErrorMessage("");
		this.graceTerms.setErrorMessage("");
		this.graceTerms_Two.setErrorMessage("");

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setErrorMessage("");
		this.repayRateBasis.setErrorMessage("");
		this.lovDescRepayBaseRateName.setErrorMessage("");
		this.lovDescRepaySpecialRateName.setErrorMessage("");
		this.repayProfitRate.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		this.repayMargin.setErrorMessage("");
		this.cbScheduleMethod.setErrorMessage("");
		this.repayFrq.setErrorMessage("");
		this.nextRepayDate.setErrorMessage("");
		this.repayPftFrq.setErrorMessage("");
		this.nextRepayPftDate.setErrorMessage("");
		this.repayRvwFrq.setErrorMessage("");
		this.nextRepayRvwDate.setErrorMessage("");
		this.repayCpzFrq.setErrorMessage("");
		this.nextRepayCpzDate.setErrorMessage("");
		this.maturityDate.setErrorMessage("");
		this.maturityDate_two.setErrorMessage("");
		this.finRepaymentAmount.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		
		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		this.oDChargeCalOn.setErrorMessage("");
		this.oDChargeType.setErrorMessage("");
		this.oDChargeAmtOrPerc.setErrorMessage("");
		this.oDMaxWaiverPerc.setErrorMessage("");

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a financeMain object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		FinanceDetail afinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), afinanceDetail);

		String tranType = PennantConstants.TRAN_WF;
		String tempRecordStatus = "NOTEMPTY";

		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		afinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + afinanceMain.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(afinanceMain.getRecordType()).equals("")) {
				tempRecordStatus = afinanceMain.getRecordType();
				afinanceMain.setVersion(afinanceMain.getVersion() + 1);
				afinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					afinanceMain.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}

				// For Saving The Additional Fields
				//Additional Field Details Validation and Saving
				afinanceDetail = getAdditionalDetailValidation().doSaveAdditionFieldDetails(afinanceDetail, 
						this.additionalDetails, new ArrayList<WrongValueException>(), addlDetailTab,
						isReadOnly("FinanceMainDialog_addlDetail"));

				doDelete_Assets(afinanceDetail, tranType, tempRecordStatus);
			}

			try {
				afinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
				if (doProcess(afinanceDetail, tranType)) {
					if (getFinanceMainListCtrl() != null) {
						refreshList();
					}
					if (getFinanceSelectCtrl() != null) {
						refreshMaintainList();
					}
					closeDialog(this.window_IstisnaFinanceMainDialog, "FinanceMainDialog");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_IstisnaFinanceMainDialog, e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new financeMain object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();

		final FinanceDetail afinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		setFinanceDetail(afinanceDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.finReference.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");
		super.doEdit();
		this.finAmount.setDisabled(true);
		this.securityDeposit.setDisabled(isReadOnly("FinanceMainDialog_securityDeposit"));
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		super.doReadOnly();
		this.securityDeposit.setDisabled(true);
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		super.doClear();
		this.securityDeposit.setValue("");
		logger.debug("Leaving");
	}


	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());
		
		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if (this.userAction.getSelectedItem() != null){
			if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") ||
					this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel") ||
					this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Resubmit")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}
		aFinanceDetail.setAccountingEventCode(eventCode);
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		
		//Save Contributor List Details
		if(isRIAExist){
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("contributorsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("contributorsTab");
			}
			
			aFinanceDetail = getContributorDetailsDialogCtrl().doSaveContributorsDetail(aFinanceDetail,tab);
		}else{
			aFinanceDetail.setFinContributorHeader(null);
		}
		aFinanceDetail.setFinBillingHeader(null);

		//Schedule details Tab Validation
		if(isReadOnly("FinanceMainDialog_NoScheduleGeneration")){
			if (isSchdlRegenerate()) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}

			if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
				return;
			}

			//Commitment Available Amount Checking During Finance Approval
			if(!doValidateCommitment(aFinanceDetail)){
				return;
			}
		}
		
		//Validation For Mandatory Recommendation
		if(!doValidateRecommendation()){
			return;
		}
		
		String tempRecordStatus = aFinanceMain.getRecordType();
		//Finance Asset Loan Details Tab
		if (childWindow != null) {
			doSave_Assets(aFinanceDetail, isNew, tempRecordStatus,false);
		}

		//Finance Agreement Details Tab
		if (getAgreementDetailDialogCtrl()!=null) {
			getAgreementDetailDialogCtrl().doSave_Agreements(aFinanceDetail);
		}
		
		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if(!validationSuccess){
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}
		
		if(StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")){
			aFinanceDetail.setStageAccountingList(null);
		}else{
			// ##### TO BE VERIFIED #####
			//Finance Fee Charge Details Tab
			if (getFeeDetailDialogCtrl() != null &&  getFinanceDetail().getFinScheduleData().getFeeRules() != null &&
					getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0) {
				// check if fee & charges rules executed or not
				if (!getFeeDetailDialogCtrl().isFeeChargesExecuted()) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_Fee"));
					return;
				}
			}

			//Finance Accounting Details Tab
			if (getAccountingDetailDialogCtrl() != null) {
				// check if accounting rules executed or not
				if (!recSave && !getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (!recSave && getAccountingDetailDialogCtrl().getDisbCrSum().compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}

			//Finance Stage Accounting Details Tab
			if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
				// check if accounting rules executed or not
				if (!getStageAccountingDetailDialogCtrl().stageAccountingsExecuted) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_StageAccountings"));
					return;
				}
				if (getStageAccountingDetailDialogCtrl().stageDisbCrSum.compareTo(getStageAccountingDetailDialogCtrl().stageDisbDrSum) != 0) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}else{
				aFinanceDetail.setStageAccountingList(null);
			}
		}

		//Document Details Saving
		if(getDocumentDetailDialogCtrl() != null){
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		}else{
			aFinanceDetail.setDocumentDetailsList(null);
		}
		
		// Finance Additional Details Tab ----> For Saving The Additional Fields
		if(!recSave){
			aFinanceDetail = getAdditionalDetailValidation().doSaveAdditionFieldDetails(aFinanceDetail, 
					this.additionalDetails, new ArrayList<WrongValueException>(), addlDetailTab,
					isReadOnly("FinanceMainDialog_addlDetail"));
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		isNew = aFinanceDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		//Finance Eligibility Details Tab
		if (getEligibilityDetailDialogCtrl() != null) {
			aFinanceDetail = getEligibilityDetailDialogCtrl().doSave_EligibilityList(aFinanceDetail);
		}

		//Finance Scoring Details Tab
		if (getScoringDetailDialogCtrl()!=null) {
			getScoringDetailDialogCtrl().doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}
		
		// Guaranteer Details Tab ---> Guaranteer Details 
		if (getJointAccountDetailDialogCtrl() != null){
			if(getJointAccountDetailDialogCtrl().getGuarantorDetailList()!=null &&
					getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail);
			}
			if(getJointAccountDetailDialogCtrl().getJountAccountDetailList()!=null &&
					getJointAccountDetailDialogCtrl().getJountAccountDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_JointAccountDetail(aFinanceDetail);
			}
		} else {
			aFinanceDetail.setJountAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}
		
		//Check for Security Collateral Condition
		if(!isReadOnly("FinanceMainDialog_secCollateral") && !aFinanceDetail.getFinScheduleData().getFinanceMain().isSecurityCollateral()){
			if(isReadOnly("FinanceMainDialog_amountBHD")){
				aFinanceDetail.getFinScheduleData().setFinanceMain(getFinanceDetailService().fetchConvertedAmounts(
						aFinanceDetail.getFinScheduleData().getFinanceMain(), true));
			}
		}else if(!isReadOnly("FinanceMainDialog_amountBHD")){
			aFinanceDetail.getFinScheduleData().setFinanceMain(getFinanceDetailService().fetchConvertedAmounts(
					aFinanceDetail.getFinScheduleData().getFinanceMain(), false));
		}
		
		if (getDisbursementDetailDialogCtrl() != null){
			
			List<ContractorAssetDetail> assetDetails = getDisbursementDetailDialogCtrl().getContractorAssetDetails();
			
			if(assetDetails == null || assetDetails.isEmpty()){
				if(tabsIndexCenter.getFellowIfAny("disbursementTab") != null){
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("disbursementTab");
					tab.setSelected(true);
				}
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_ContractorList_Empty"));
				return;
			}
			
			List<ContractorAssetDetail> contractorAssetDetails = getDisbursementDetailDialogCtrl().validateContractorAssetDetails();
			if(contractorAssetDetails == null){
				if(tabsIndexCenter.getFellowIfAny("disbursementTab") != null){
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("disbursementTab");
					tab.setSelected(true);
				}
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_ContractorBilling_NotComplete"));
				return;
			}
			aFinanceDetail.setContractorAssetDetails(assetDetails);
		}
		
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getFinanceMainListCtrl() != null) {
					refreshList();
				}
				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}
				
				//Customer Notification for Role Identification
				if(StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("")){
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),aFinanceMain.getNextRoleCode(), 
						aFinanceMain.getFinReference(), " Finance ", aFinanceMain.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				//Mail Alert Notification for User
				if(!StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("") && 
						!StringUtils.trimToEmpty(aFinanceMain.getNextRoleCode()).equals(aFinanceMain.getRoleCode())){
					getMailUtil().sendMail("FIN", aFinanceDetail,this);
					//getMailUtil().sendMail(1, PennantConstants.TEMPLATE_FOR_AE, aFinanceMain);
				}
				closeWindow();
				//closeDialog(this.window_IstisnaFinanceMainDialog, "FinanceMainDialog");
				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_IstisnaFinanceMainDialog, e);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private String getServiceTasks(String taskId, FinanceMain financeMain,
			String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getWorkFlow().getOperationRefs(taskId,
				financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getWorkFlow().getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getWorkFlow().firstTask.owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + ",";
					}
					nextRoleCode += getWorkFlow().getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException 
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if(getScheduleDetailDialogCtrl() != null){
			if(getScheduleDetailDialogCtrl().getFeeChargesMap() != null && getScheduleDetailDialogCtrl().getFeeChargesMap().size() > 0){
				List<Date> feeRuleKeys = new ArrayList<Date>(getScheduleDetailDialogCtrl().getFeeChargesMap().keySet());
				List<FeeRule> feeRuleList = new ArrayList<FeeRule>();
				for (Date date : feeRuleKeys) {
					feeRuleList.addAll(getScheduleDetailDialogCtrl().getFeeChargesMap().get(date));
				}
				aFinanceDetail.getFinScheduleData().setFeeRules(feeRuleList);
			}
		}

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, afinanceMain))) {
				try {
					if (!isNotes_Entered()) {
						PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doDedup)) {

					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(),aFinanceDetail, this.window_IstisnaFinanceMainDialog);
					if (tFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound()&& 
							!tFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doBlacklist)) {

					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					boolean isBlackListed = getFinanceDetailService().doCheckBlackListedCustomer(auditHeader);
					tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(isBlackListed);
					if (isBlackListed) {
						processCompleted = false;
						PTMessageUtils.showErrorMessage(Labels.getLabel("label_IsBlackListedCustomer"));
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_CheckLimits)) {

					processCompleted = doSaveProcess(auditHeader, method);

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckExceptions)) {

					auditHeader = getFinanceDetailService().doCheckExceptions(auditHeader);

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {
					
					/*FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					FinanceMain financeMain = tFinanceDetail.getFinScheduleData().getFinanceMain();
					
					List<Long> templateIDList = getFinanceDetailService().getMailTemplatesByFinType(financeMain.getFinType(), financeMain.getRoleCode());
					for (Long templateId : templateIDList) {
						getMailUtil().sendMail(templateId, PennantConstants.TEMPLATE_FOR_CN, financeMain);
					}*/

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doDiscrepancy)) {
					aFinanceDetail = getFinanceDetailService().checkDiscrepancy(aFinanceDetail);
					String discrepancy = StringUtils.trimToEmpty(aFinanceDetail.getFinScheduleData().getFinanceMain().getDiscrepancy());
					String msg = "";
					if (!StringUtils.trimToEmpty(discrepancy).equals("")) {
						if(discrepancy.equals(PennantConstants.WF_PAST_DUE_OVERRIDE)){
							msg = Labels.getLabel("message.Discrepancy_PastDue",new String[]{discrepancy});
						}else if(discrepancy.equals(PennantConstants.WF_PAST_DUE)){
							msg = Labels.getLabel("message.Discrepancy_PastDue_OverDue",new String[]{discrepancy});
						}else if(discrepancy.equals(PennantConstants.WF_LIMIT_EXPIRED)){
							msg = Labels.getLabel("message.Discrepancy_LimitExpired",new String[]{discrepancy});
						}else if(discrepancy.equals(PennantConstants.WF_NO_LIMIT)){
							msg = Labels.getLabel("message.Discrepancy_NoLimit",new String[]{discrepancy});
						}else{
							msg = Labels.getLabel("message.Discrepancy_Check",new String[]{discrepancy});
						}
						final String title = Labels.getLabel("title.Discrepancy");
						int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
						if (conf != MultiLineMessageBox.YES) {
							return false;
						}
					}

				}else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doClearQueues)) {

					aFinanceDetail.getFinScheduleData().getFinanceMain().setNextTaskId("");

				}else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doFundsAvailConfirmed)) {

					String nextRoleCode = StringUtils.trimToEmpty(aFinanceDetail.getFinScheduleData().getFinanceMain().getNextRoleCode());
					String nextRoleCodes[]=nextRoleCode.split(",");

					if (nextRoleCodes.length > 1 ) {
						aFinanceDetail.getFinScheduleData().getFinanceMain().setFundsAvailConfirmed(false);
						PTMessageUtils.showErrorMessage(Labels.getLabel("message.Conformation_Check"));
					}else{
						aFinanceDetail.getFinScheduleData().getFinanceMain().setFundsAvailConfirmed(true);
					}

				}else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckProspectCustomer)){
					//Prospect Customer Checking
					if(StringUtils.trimToEmpty(aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCoreBank()).equals("")){
						PTMessageUtils.showErrorMessage(Labels.getLabel("label_FinanceMainDialog_Mandatory_Prospect.value"));
						return false;
					}
					
				}else {

					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					tFinanceDetail = doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),finishedTasks);

			}

			FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getWorkFlow().getNextTaskIds(taskId,tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			doProcess_Assets(aFinanceDetail);
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceDetailService().delete(auditHeader, false);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader, false);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceDetailService().doApprove(auditHeader, false);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceDetailService().doReject(auditHeader, false);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)){
						if(afinanceDetail.getFinScheduleData().getFinanceType().isLimitRequired()){
							getFinanceDetailService().doCheckLimits(auditHeader);
						}else{
							afinanceDetail.getFinScheduleData().getFinanceMain().setLimitValid(true);
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_IstisnaFinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_IstisnaFinanceMainDialog, auditHeader);
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

					if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)){

						if(overideMap.containsKey("Limit")){
							FinanceDetail tfinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
							tfinanceDetail.getFinScheduleData().getFinanceMain().setOverrideLimit(true);
							auditHeader.getAuditDetail().setModelData(tfinanceDetail);
						}
					}
				}
			}
			setOverideMap(auditHeader.getOverideMap());

		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ Search Button Events++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	//FinanceMain Details Tab ---> 1. Basic Details

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "FinanceType");
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType() + "-" + details.getFinTypeDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug("Entering " + event.toString()); 

		this.finCcy.setConstraint("");
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {

				this.repayAcctId.setValue("");
				this.finCcy.setValue(details.getCcyCode(), details.getCcyDesc());

				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescFinFormatter(details.getCcyEditField());
				getFinanceDetail().getFinScheduleData().getFinanceMain().setFinCcy(details.getCcyCode());

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.securityDeposit.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayment.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));

				try {
					if (getChildWindowDialogCtrl().getClass().getField("ccyFormatter") != null) {
						getChildWindowDialogCtrl().getClass().getField("ccyFormatter").setInt(getChildWindowDialogCtrl(),
								details.getCcyEditField());

						if (getChildWindowDialogCtrl().getClass().getMethod("doSetFieldProperties") != null) {
							getChildWindowDialogCtrl().getClass().getMethod("doSetFieldProperties").invoke(getChildWindowDialogCtrl());
						}
					}
				} catch (Exception e) {
				}
			}
		}

		//doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchRepayAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.custCIF.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());

			iAccount.setAcCustCIF(this.custCIF.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.repayAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.repayAcctId.setValue(details.getAccountId());
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.custCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_IstisnaFinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when clicks on button "btnSearchDownPayAccount"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchDownPayAcc(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.custCIF.clearErrorMessage();
		this.downPayAccount.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setAcCustCIF(this.custCIF.getValue());
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());

			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);

				dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.downPayAccount.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;

					if (details != null) {
						this.downPayAccount.setValue(details.getAccountId());
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.custCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when clicks on button "btnSearchCommitmentRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void onClick$btnSearchCommitmentRef(Event event) throws InterruptedException, AccountNotFoundException, 
				IllegalAccessException, InvocationTargetException {
		logger.debug("Entering " + event.toString());

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("CustID", this.custID.longValue(), Filter.OP_EQUAL);
		//filters[1] = new Filter("cmtBranch", this.finBranch.getValue(), Filter.OP_EQUAL);
	//	filters[1] = new Filter("cmtCcy", this.finCcy.getValue(), Filter.OP_EQUAL);
		if(this.finStartDate.getValue() != null){
			filters[1] = new Filter("CmtExpDate", DateUtility.formatUtilDate(this.finStartDate.getValue(), PennantConstants.DBDateFormat), Filter.OP_GREATER_OR_EQUAL);
		}else{
			throw new WrongValueException(finStartDate,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FinStartDate.value")}));
		}


		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "Commitment", filters);

		if (dataObject instanceof String) {
			this.commitmentRef.setValue(dataObject.toString());
			this.lovDescCommitmentRefName.setValue("");
			commitment=null;
		} else {
			Commitment details = (Commitment) dataObject;
			commitment=details;
			if (details != null) {
				this.commitmentRef.setValue(details.getCmtReference());
				this.lovDescCommitmentRefName.setValue(details.getCmtReference() +" - "+details.getCmtTitle());
				this.availCommitAmount = details.getCmtAvailable();
				if(repayProfitRate.getValue() != null && repayProfitRate.getValue().compareTo(BigDecimal.ZERO) <= 0 ){
					this.repayProfitRate.setValue(details.getCmtPftRateMin());
				}
			}
		}

		//Finance Accounting Details Execution
		executeAccounting(true);

		logger.debug("Leaving " + event.toString());
	}
	

	/**
	 * Method for Checking Approval Status Credit Admin Officer
	 * @param event
	 */
	public void onChange$approved(Event event){
		logger.debug("Entering " + event.toString());
		
		this.custAcceptance.setChecked(false);
		this.row_custAcceptance.setVisible(false);
		if(this.approved.getSelectedItem() != null && 
				!this.approved.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
			
			if(this.approved.getSelectedItem().getValue().toString().equals("Yes")){
				this.custAcceptance.setChecked(true);
				this.row_custAcceptance.setVisible(true);
			}
		}
	//	onCheckCustAcceptance();		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Check Method for Accessing Customer Acceptance Approval
	 * @param event
	 */
	public void onCheck$custAcceptance(Event event){
		logger.debug("Entering " + event.toString());
		//onCheckCustAcceptance();		
		logger.debug("Leaving " + event.toString());
	}
	
	/*private void onCheckCustAcceptance(){
		//this.cbbApprovalRequired.setChecked(false);
		//this.cbbApproved.setChecked(false);
		this.label_IstisnaFinanceMainDialog_CbbApproved.setVisible(false);
		this.hbox_cbbApproved.setVisible(false);
		this.row_cbbApproval.setVisible(false);
		
		if(this.custAcceptance.isChecked()){
			this.row_cbbApproval.setVisible(!isReadOnly("FinanceMainDialog_cbbApprovalRequired"));
			this.cbbApprovalRequired.setDisabled(isReadOnly("FinanceMainDialog_cbbApprovalRequired"));
		}
	}*/
	
	/**
	 * Check Method for Accessing Approval From CBB committee
	 * @param event
	 */
	/*public void onCheck$cbbApprovalRequired(Event event){
		logger.debug("Entering " + event.toString());
		onCheckCBBApproval(false);
		logger.debug("Leaving " + event.toString());
	}*/
	
	/*private void onCheckCBBApproval(boolean isLoadProc){
		logger.debug("Entering");
		if(this.cbbApprovalRequired.isChecked()){
			this.hbox_cbbApproved.setVisible(true);
			this.label_IstisnaFinanceMainDialog_CbbApproved.setVisible(true);
			if(isLoadProc){
				this.cbbApproved.setDisabled(true);
			}else{
				this.cbbApproved.setDisabled(false);
			}
		}else{
			this.hbox_cbbApproved.setVisible(false);
			this.label_IstisnaFinanceMainDialog_CbbApproved.setVisible(false);
			this.cbbApproved.setChecked(false);
			this.cbbApproved.setDisabled(true);
		}
		logger.debug("Leaving");
	}*/

	//FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchGraceBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.grcEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "BaseRateCode");

		if (dataObject instanceof String) {
			this.graceBaseRate.setValue(dataObject.toString());
			this.lovDescGraceBaseRateName.setValue("");
			this.grcEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.graceBaseRate.setValue(details.getBRType());
				this.lovDescGraceBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}

		calculateRate(this.graceBaseRate, this.graceSpecialRate,
				this.lovDescGraceBaseRateName, this.grcMargin, this.grcEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "GraceSpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchGraceSpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.grcEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "SplRateCode");

		if (dataObject instanceof String) {
			this.graceSpecialRate.setValue(dataObject.toString());
			this.lovDescGraceSpecialRateName.setValue("");
			this.grcEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.graceSpecialRate.setValue(details.getSRType());
				this.lovDescGraceSpecialRateName.setValue(details.getSRType() + "-" + details.getSRTypeDesc());
			}
		}

		calculateRate(this.graceBaseRate, this.graceSpecialRate,
				this.lovDescGraceBaseRateName, this.grcMargin, this.grcEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when user checks the allowGrcRepay checkbox
	 * 
	 * @param event
	 */
	public void onCheck$allowGrcRepay(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.allowGrcRepay.isChecked()) {
			this.cbGrcSchdMthd.setDisabled(false);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
					schMethodList, ",EQUAL,PRI_PFT,PRI,");
		} else {
			this.cbGrcSchdMthd.setDisabled(true);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 *  To get the BaseRateCode LOV List From RMTBaseRateCodes Table
	 * @param event
	 */
	public void onClick$btnSearchGrcIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "BaseRateCode");
		if (dataObject instanceof String) {
			this.grcIndBaseRate.setValue(dataObject.toString());
			this.lovDescGrcIndBaseRateName.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.grcIndBaseRate.setValue(details.getBRType());
				this.lovDescGrcIndBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Allow/ Not Grace In Finance
	 * @param event
	 */
	public void onCheck$allowGrace(Event event) {
		logger.debug("Entering" + event.toString());
		doAllowGraceperiod(true);
		logger.debug("Leaving" + event.toString());
	}

	//FinanceMain Details Tab ---> 3. Repayment Period Details

	/**
	 * when clicks on button "SearchRepayBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchRepayBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.repayEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "BaseRateCode");

		if (dataObject instanceof String) {
			this.repayBaseRate.setValue(dataObject.toString());
			this.lovDescRepayBaseRateName.setValue("");
			this.repayEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.repayBaseRate.setValue(details.getBRType());
				this.lovDescRepayBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}

		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.lovDescRepayBaseRateName, this.repayMargin, this.repayEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchRepaySpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchRepaySpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.repayEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "SplRateCode");

		if (dataObject instanceof String) {
			this.repaySpecialRate.setValue(dataObject.toString());
			this.lovDescRepaySpecialRateName.setValue("");
			this.repayEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.repaySpecialRate.setValue(details.getSRType());
				this.lovDescRepaySpecialRateName.setValue(details.getSRType() + "-" + details.getSRTypeDesc());
			}
		}

		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.lovDescRepayBaseRateName, this.repayMargin, this.repayEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * To get the BaseRateCode LOV List From RMTBaseRateCodes Table
	 * @param event
	 */
	public void onClick$btnSearchRpyIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaFinanceMainDialog, "BaseRateCode");
		if (dataObject instanceof String) {
			this.rpyIndBaseRate.setValue(dataObject.toString());
			this.lovDescRpyIndBaseRateName.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.rpyIndBaseRate.setValue(details.getBRType());
				this.lovDescRpyIndBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnSelect ComboBox Events++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	//FinanceMain Details Tab ---> 1. Basic Details
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnCheck CheckBox Events+++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onCheck$allowGrcInd(Event event) {
		logger.debug("Entering" + event.toString());
		doDisableGrcIndRateFields();
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$allowRpyInd(Event event) {
		logger.debug("Entering" + event.toString());
		doDisableRpyIndRateFields();
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++Overdue Penalty Details++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onCheck$applyODPenalty(Event event){
		logger.debug("Entering" + event.toString());
		onCheckODPenalty(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private void onCheckODPenalty(boolean checkAction){
		if(this.applyODPenalty.isChecked()){
			
			this.oDIncGrcDays.setDisabled(isReadOnly("FinanceMainDialog_oDIncGrcDays"));
			this.oDChargeType.setDisabled(isReadOnly("FinanceMainDialog_oDChargeType"));
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));
			this.oDChargeCalOn.setDisabled(isReadOnly("FinanceMainDialog_oDChargeCalOn"));
			this.oDAllowWaiver.setDisabled(isReadOnly("FinanceMainDialog_oDAllowWaiver"));
			
			if(checkAction){
				this.oDChargeAmtOrPerc.setDisabled(true);
				this.oDMaxWaiverPerc.setDisabled(true);
			}else{
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}
			
		}else{
			this.oDIncGrcDays.setDisabled(true);
			this.oDChargeType.setDisabled(true);
			this.oDGraceDays.setReadonly(true);
			this.oDChargeCalOn.setDisabled(true);
			this.oDChargeAmtOrPerc.setDisabled(true);
			this.oDAllowWaiver.setDisabled(true);
			this.oDMaxWaiverPerc.setDisabled(true);
			
			checkAction = true;
		}
		
		if(checkAction){
			this.oDIncGrcDays.setChecked(false);
			if(this.applyODPenalty.isChecked()){
				this.oDChargeType.setSelectedIndex(0);
				this.oDChargeCalOn.setSelectedIndex(0);
			}
			this.oDGraceDays.setValue(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
	}
	
	public void onChange$oDChargeType(Event event){
		logger.debug("Entering" + event.toString());
		onChangeODChargeType(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private void onChangeODChargeType(boolean changeAction){
		if(changeAction){
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}
		this.space_oDChargeAmtOrPerc.setSclass("mandatory");
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			this.oDChargeAmtOrPerc.setDisabled(true);
			this.space_oDChargeAmtOrPerc.setSclass("");
		}else if (getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)) {
			this.oDChargeAmtOrPerc.setDisabled(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"));
			this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		} else {
			this.oDChargeAmtOrPerc.setDisabled(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"));
			this.oDChargeAmtOrPerc.setMaxlength(6);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
	}
	
	public void onCheck$oDAllowWaiver(Event event){
		logger.debug("Entering" + event.toString());
		onCheckODWaiver(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private void onCheckODWaiver(boolean checkAction) {
		if(checkAction){
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass("mandatory");
			this.oDMaxWaiverPerc.setDisabled(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"));
		}else {
			this.oDMaxWaiverPerc.setDisabled(true);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given
	 * finance details.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		validate();
		isFinValidated = true;
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "buildSchedule" button is clicked. <br>
	 * Stores the default values, sets the validation, validates the given
	 * finance details, builds the schedule.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnBuildSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.buildEvent = true;
		
		if (validate() != null) {
 			this.buildEvent = false;
 			isFinValidated = false;

 			//Setting Finance Step Policy Details to Finance Schedule Data Object
 			if(getStepDetailDialogCtrl() != null){
 				validFinScheduleData.setStepPolicyDetails(getStepDetailDialogCtrl().getFinStepPoliciesList());
 				this.oldVar_finStepPolicyList = getStepDetailDialogCtrl().getFinStepPoliciesList();
 			}
 			
			//Prepare Finance Schedule Generator Details List
 			getFinanceDetail().getFinScheduleData().setRepayInstructions(new ArrayList<RepayInstruction>());
			getFinanceDetail().getFinScheduleData().setDefermentHeaders(new ArrayList<DefermentHeader>());
			getFinanceDetail().getFinScheduleData().setDefermentDetails(new ArrayList<DefermentDetail>());
			getFinanceDetail().setFinScheduleData(ScheduleGenerator.getNewSchd(validFinScheduleData));
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleMaintained(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMigratedFinance(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleRegenerated(false);

			//Build Finance Schedule Details List
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() != 0) {
				getFinanceDetail().setFinScheduleData(ScheduleCalculator.getCalSchd(
						getFinanceDetail().getFinScheduleData()));
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
				getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
				
				//Current Finance Monthly Installment Calculation
				BigDecimal totalRepayAmount = getFinanceDetail().getFinScheduleData().getFinanceMain().getTotalRepayAmt();
				int installmentMnts = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), false);
				
				BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
				int months = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate());
				
				//Customer Data Fetching
				if(customer == null){
					customer = getCustomerService().getCustomerById(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
				}
				
				// Set Customer Data to check the eligibility
				getFinanceDetail().setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
						getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(), curFinRepayAmt,
						months, getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount(),null));
				
				getFinanceDetail().getFinScheduleData().getFinanceMain().setCustDSR(getFinanceDetail().getCustomerEligibilityCheck().getDSCR());
				setCustomerScoringData();

				//Fill Finance Schedule details List data into ListBox
				if(getScheduleDetailDialogCtrl() != null){
					getScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
				}else{
					appendScheduleDetailTab(false, false);
				}

				//Finance Related Rules Execution After Schedule Data Preparation
				if(!(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID() == 0 || 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID() == Long.MIN_VALUE) && 
						!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getDisbAccountId()).equals("") && 
						!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayAccountId()).equals("")){
					doRulesExecution(false);
				}
				
			}

			//Schedule tab Selection After Schedule Re-modified
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setSelected(true);
			}
			
			if(getStepDetailDialogCtrl() != null){
				getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to validate given details
	 * 
	 * @throws InterruptedException
	 * @return validfinanceDetail
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * */
	private FinanceDetail validate() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		recSave = false;
		
		if(isSchdlRegenerate()){
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		}

		doStoreDefaultValues();
		doCheckFeeReExecution();
		doStoreInitValues();
		doSetValidation();

		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);

		getFinanceDetail().setFinScheduleData(validFinScheduleData);
		doWriteComponentsToBean(validFinScheduleData);
		
		if(getDisbursementDetailDialogCtrl() != null && getDisbursementDetailDialogCtrl().getDisbursementDetails().size() == 0){
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("disbursementTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("disbursementTab");
				tab.setSelected(true);
			}
			
			PTMessageUtils.showErrorMessage("Billing & Advance Details must be Added.");
			return null;
		}else{
			
			boolean disbVaidated = false;
			for (FinanceDisbursement finDisb : getDisbursementDetailDialogCtrl().getDisbursementDetails()) {
				if (!finDisb.getDisbDate().after(this.finStartDate.getValue())) {
					if(finDisb.getDisbType().equals("A") || finDisb.getDisbType().equals("B")){
						if(finDisb.getDisbAmount().compareTo(BigDecimal.ZERO) > 0){
							disbVaidated = true;
						}
					}
				}
			}
			
			if(!disbVaidated){
				PTMessageUtils.showErrorMessage("Must have Minimum one Disbursement Payment(Either Advance or Billing) on or before Cureent Application Date");
				return null;
			}
		}
		
		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {
			
			//Contributor Details Checking for RIA Accounting
			if(isRIAExist && getContributorDetailsDialogCtrl() != null){
				
				Tab tab = null;
				if(tabsIndexCenter.getFellowIfAny("contributorsTab") != null){
					tab = (Tab) tabsIndexCenter.getFellowIfAny("contributorsTab");
				}
				getContributorDetailsDialogCtrl().doSaveContributorsDetail(getFinanceDetail(), tab);
			}

			validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetails>());

			logger.debug("Leaving");
			return getFinanceDetail();
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method to store the default values if no values are entered in respective
	 * fields when validate or build schedule buttons are clicked
	 * 
	 * */
	public void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
		doClearMessage();
		super.doStoreDefaultValues();
		logger.debug("Leaving");
	}


	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * */
	public boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		return super.doValidation(auditHeader);
	}
	
	
	
	/**
	 * Change the branch for the Account on changing the finance Branch
	 * @param event
	 */
	public void onFulfill$finBranch(Event event) {
		logger.debug("Entering");
		this.disbAcctId.setBranchCode(this.finBranch.getValue());
		this.repayAcctId.setBranchCode(this.finBranch.getValue());
		this.downPayAccount.setBranchCode(this.finBranch.getValue());
		logger.debug("Leaving");
	}
	
	/*
	 * onFullFill Event For CustCIF
	 */
	public void onFulfill$custCIF(Event event){
		logger.debug("Entering " + event.toString()); 

		this.custCIF.setConstraint("");
		Object dataObject = custCIF.getObject();
		
		if (dataObject instanceof String) {
			this.custCIF.setValue(dataObject.toString());
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				customer = (Customer) dataObject;
				setCustomerData();
			} else {
				this.custID.setValue((long) 0);
				this.custCIF.setValue("");
				this.commitmentRef.setValue("");
				this.lovDescCommitmentRefName.setValue("");
				this.disbAcctId.setCustCIF("");
				this.repayAcctId.setCustCIF("");
				this.downPayAccount.setCustCIF("");
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_IjarahFinanceMainDialog_CustID.value") }));
			}
		}
		//doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Reset Customer Data
	 */
	private void setCustomerData(){
		logger.debug("Entering");
		
		this.custID.setValue(customer.getCustID());
		this.custCIF.setValue(customer.getCustCIF(), customer.getCustShrtName());
		this.disbAcctId.setCustCIF(customer.getCustCIF());
		this.repayAcctId.setCustCIF(customer.getCustCIF());
		this.downPayAccount.setCustCIF(customer.getCustCIF());

		this.repayAcctId.setValue("");
		this.finBranch.setValue(customer.getCustDftBranch());
		this.finBranch.setDescription(customer.getLovDescCustDftBranchName());
		this.disbAcctId.setBranchCode(customer.getCustDftBranch());
		this.repayAcctId.setBranchCode(customer.getCustDftBranch());
		this.downPayAccount.setBranchCode(customer.getCustDftBranch());
		
		this.commitmentRef.setValue("");
		this.lovDescCommitmentRefName.setConstraint("");
		this.lovDescCommitmentRefName.setErrorMessage("");
		this.lovDescCommitmentRefName.setValue("");

		custCtgType = customer.getLovDescCustCtgType();
		setFinanceDetail(getFinanceDetailService().fetchFinCustDetails(getFinanceDetail(), custCtgType,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType(), getRole()));

		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustFName(
				StringUtils.trimToEmpty(customer.getCustFName()));

		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustLName(
				StringUtils.trimToEmpty(customer.getCustLName()));

		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustCIF(
				StringUtils.trimToEmpty(customer.getCustCIF()));

		//Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = getFinanceDetail().getFinScheduleData().getFinanceMain().getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), true);

		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(), 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate());

		// Set Customer Data to check the eligibility
		getFinanceDetail().setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
				getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(), curFinRepayAmt,
				months, getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getCustDSR()));
		setCustomerScoringData();

		// Execute Eligibility Rule and Display Result
		if(getEligibilityDetailDialogCtrl() != null){
			getEligibilityDetailDialogCtrl().doFillExecElgList(getFinanceDetail().getFinElgRuleList());
			// getEligibilityDetailDialogCtrl().doFillEligibilityListbox(getFinanceDetail().getEligibilityRuleList(), false);
		}else{
			appendEligibilityDetailTab(false);
		}

		//Scoring Detail Tab
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustCtgTypeName(custCtgType);
		appendFinScoringDetailTab(false);

		//Agreement Details Tab
		setAgreementDetailTab(this.window_IstisnaFinanceMainDialog);

		// Fill Check List Details based on Rule Execution if Rule Exist
		appendCheckListDetailTab(getFinanceDetail(), getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord(),false);

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if(getWorkFlow().getTaskTabs(getWorkFlow().getTaskId(getRole())).equals("Accounting")){

			// Finance Accounting Posting Details
			if(getAccountingDetailDialogCtrl() != null){
				getAccountingDetailDialogCtrl().doFillAccounting(getFinanceDetail().getTransactionEntries());
				if(!StringUtils.trimToEmpty(this.commitmentRef.getValue()).equals("")){
					getAccountingDetailDialogCtrl().doFillCmtAccounting(getFinanceDetail().getCmtFinanceEntries(), 0);
				}
			}else{
				setAccountingDetailTab(this.window_IstisnaFinanceMainDialog);
			}
		}

		//Finance Stage Accounting Posting Details
		appendStageAccountingDetailsTab(false);
		setDiscrepancy(getFinanceDetail());	
		logger.debug("Leaving");
	}
	
	/**
	 * Method to prepare data required for scoring check
	 * 
	 * @return CustomerScoringCheck
	 */
	public void setCustomerScoringData() {
		logger.debug("Entering");
		CustomerScoringCheck customerScoringCheck = new CustomerScoringCheck();
		BeanUtils.copyProperties(getFinanceDetail().getCustomerEligibilityCheck(), customerScoringCheck);
		getFinanceDetail().setCustomerScoringCheck(customerScoringCheck);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++++++ OnBlur Events ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * When user leaves finReference component
	 * 
	 * @param event
	 */
	public void onChange$finReference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		//doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user leave grace period end date component
	 * 
	 * @param event
	 */
	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		//doFillCommonDetails();
		if(this.graceTerms_Two.intValue() == 0 && 
				(this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null)){

			if(this.gracePeriodEndDate.getValue() != null){
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
			if(this.nextGrcPftDate.getValue() == null){
				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1,
						this.finStartDate.getValue(), "A", false).getNextFrequencyDate());
			}
			if(this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0){
				this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
						this.nextGrcPftDate_two.getValue(), this.gracePeriodEndDate_two.getValue(), false, true).getTerms());
			}else if(this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0){
				this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
						this.nextGrcPftDate_two.getValue(), this.gracePeriodEndDate_two.getValue(), true, true).getTerms());
			}

			this.graceTerms.setText("");
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 *  To calculate the grace effective rate value 
	 * including margin rate.
	 * 
	 * */
	public void onChange$grcMargin(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.grcMargin.getValue() != null) {
			this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate((
					this.grcEffectiveRate.getValue().add(this.grcMargin.getValue())).doubleValue(),2));
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.lovDescGraceBaseRateName.setConstraint("");
		this.lovDescGraceSpecialRateName.setConstraint("");
		this.grcEffectiveRate.setConstraint("");

		this.btnSearchGraceBaseRate.setDisabled(true);
		this.btnSearchGraceSpecialRate.setDisabled(true);

		this.graceBaseRate.setValue("");
		this.graceSpecialRate.setValue("");
		this.lovDescGraceBaseRateName.setValue("");
		this.lovDescGraceSpecialRateName.setValue("");
		this.gracePftRate.setDisabled(true);
		this.grcEffectiveRate.setText("0.00");
		this.gracePftRate.setText("0.00");

		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString()) || 
					"C".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchGraceBaseRate.setDisabled(true);
				this.btnSearchGraceSpecialRate.setDisabled(true);

				this.lovDescGraceBaseRateName.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");

				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setDisabled(isReadOnly("FinanceMainDialog_gracePftRate"));
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					this.btnSearchGraceBaseRate.setDisabled(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("FinanceMainDialog_graceSpecialRate"));
				}else{
					this.gracePftRate.setDisabled(isReadOnly("FinanceMainDialog_gracePftRate"));
				}

				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setText("0.00");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$repayRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.lovDescRepayBaseRateName.setConstraint("");
		this.lovDescRepaySpecialRateName.setConstraint("");
		this.repayEffectiveRate.setConstraint("");

		this.btnSearchRepayBaseRate.setDisabled(true);
		this.btnSearchRepaySpecialRate.setDisabled(true);

		this.repayBaseRate.setValue("");
		this.repaySpecialRate.setValue("");
		this.lovDescRepayBaseRateName.setValue("");
		this.lovDescRepaySpecialRateName.setValue("");
		this.repayProfitRate.setDisabled(true);
		this.repayEffectiveRate.setText("");
		this.repayProfitRate.setText("");

		if(!this.repayRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.repayRateBasis.getSelectedItem().getValue().toString()) ||
					"C".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchRepayBaseRate.setDisabled(true);
				this.btnSearchRepaySpecialRate.setDisabled(true);

				this.lovDescRepayBaseRateName.setValue("");
				this.lovDescRepaySpecialRateName.setValue("");

				this.repayEffectiveRate.setText("");
				this.repayProfitRate.setDisabled(isReadOnly("FinanceMainDialog_profitRate"));
			}else if("R".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {

				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()).equals("")){
					this.btnSearchRepayBaseRate.setDisabled(isReadOnly("FinanceMainDialog_repayBaseRate"));
					this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("FinanceMainDialog_repaySpecialRate"));
				}else{
					this.repayProfitRate.setDisabled(isReadOnly("FinanceMainDialog_profitRate"));
				}
				this.repayEffectiveRate.setText("");
				this.repayProfitRate.setText("");
			}
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * To calculate the repay effective rate value 
	 * including margin rate.
	 * 
	 * */
	public void onChange$repayMargin(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.repayMargin.getValue() != null && !this.repayProfitRate.isDisabled()) {
			this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate((
					this.repayEffectiveRate.getValue().add(this.repayMargin.getValue())).doubleValue(),2));
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbScheduleMethod(Event event) {
		logger.debug("Entering" + event.toString());
		this.lovDescRpyIndBaseRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.clearErrorMessage();
		this.rpyIndBaseRate.setValue("");
		this.lovDescRpyIndBaseRateName.setValue("");
		this.btnSearchRpyIndBaseRate.setDisabled(true);
		if(!getComboboxValue(this.cbScheduleMethod).equals(CalculationConstants.PFT)) {
			this.allowRpyInd.setDisabled(true);
			this.allowRpyInd.setChecked(false);
		}else if(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw()) {
			this.allowRpyInd.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Setting Mandatory Check to Repay Account ID based on Repay Method
	 * @param event
	 */
	public void onChange$finRepayMethod(Event event){
		logger.debug("Entering" + event.toString());
		setRepayAccMandatory();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Set Mandatory On DownPay Account Based on Downpayment Amount
	 * 
	 * @param event
	 */
	public void onFulfill$downPayBank(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		setDownPayAcMand();
		logger.debug("Leaving " + event.toString());
	}
	
	private void setDownPayAcMand(){
		if(this.downPayBank.getValue() != null && this.downPayBank.getValue().compareTo(BigDecimal.ZERO) > 0){
			this.downPayAccount.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandownPaymentAcc"));
		}else{
			this.downPayAccount.setMandatoryStyle(false);	
		}
	}
	
	/**
	 * Method to add version and record type values to assets
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param isNew
	 *            (boolean)
	 * **/
	public void doSave_Assets(FinanceDetail aFinanceDetail, boolean isNew,
			String tempRecordStatus,boolean agreement) { 
		super.doSave_Assets(aFinanceDetail, isNew, tempRecordStatus, agreement);
		
	}
 
	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception 
	 */
	private boolean doSave_CheckList(FinanceDetail aFinanceDetail, boolean agreement) throws Exception {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);
		boolean validationSuccess = true;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		if(agreement){
			map.put("agreement", agreement);
		}

		try {
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getFinanceDetail().getFinanceCheckList();
		selAnsCountMap = getFinanceDetail().getLovDescSelAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			getFinanceDetail().setFinanceCheckList(chkList);
			getFinanceDetail().setLovDescSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");
		return validationSuccess;

	}
	
	/**
	 * Method to set user details values to asset objects
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 ***/
	public FinanceDetail  doProcess_Assets(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");
		super.doProcess_Assets(aFinanceDetail);
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	/**
	 * Method to set transation properties for assets while deleting
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param tranType
	 *            (String)
	 ***/
	public void doDelete_Assets(FinanceDetail aFinanceDetail, String tranType,
			String tempRecordStatus) {
		logger.debug("Entering");
		 super.doDelete_Assets(aFinanceDetail, tranType, tempRecordStatus);
		logger.debug("Leaving");
	}
	
	/**
	 * Get the Finance Main Details from the Screen
	 */
	public FinanceMain getFinanceMain() {
		return super.getFinanceMain();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		this.btnNotes.setSclass("");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$viewCustInfo(Event event){
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("custCIF", this.custCIF.getValue());
			map.put("custShrtName", this.custCIF.getDescription());
			map.put("finFormatter", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (finDivision.equals(PennantConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", window_IstisnaFinanceMainDialog, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul", window_IstisnaFinanceMainDialog, map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
	}
	
	/** To pass Data For Agreement Child Windows
	 * Used in reflection
	 * @return
	 * @throws Exception 
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());
		
		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		//Schedule details Tab Validation
		if (isSchdlRegenerate()) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return null;
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
			return null;
		}
		
		String tempRecordStatus = aFinanceMain.getRecordType();
		isNew = aFinanceDetail.isNew();
		//Finance Asset Loan Details Tab
		if (childWindow != null) {
			doSave_Assets(aFinanceDetail, isNew, tempRecordStatus,true);
		}

		//Finance Scoring Details Tab  --- > Scoring Module Details Check
		//Check if any overrides exits then the overridden score count is same or not
		if(getScoringDetailDialogCtrl() != null){
			if (getScoringDetailDialogCtrl().isScoreExecuted()) {
				if (!getScoringDetailDialogCtrl().isSufficientScore()) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Insufficient_Score"));
					return null;
				}
			} else {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Verify_Score"));
				return null;
			}
		}
		
		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, true);
			if(!validationSuccess){
				return null;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}
		
		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		//Finance Eligibility Details Tab
		if (getEligibilityDetailDialogCtrl() != null) {
			aFinanceDetail = getEligibilityDetailDialogCtrl().doSave_EligibilityList(aFinanceDetail);
		}

		//Finance Scoring Details Tab
		if (getScoringDetailDialogCtrl()!=null) {
			getScoringDetailDialogCtrl().doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}
		
		// Guaranteer Details Tab ---> Guaranteer Details 
		if (getJointAccountDetailDialogCtrl() != null){
			if(getJointAccountDetailDialogCtrl().getGuarantorDetailList()!=null &&
					getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail);
			}
			if(getJointAccountDetailDialogCtrl().getJountAccountDetailList()!=null &&
					getJointAccountDetailDialogCtrl().getJountAccountDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_JointAccountDetail(aFinanceDetail);
			}
		} else {
			aFinanceDetail.setJountAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		logger.debug("Leaving");
		return aFinanceDetail;
	}
	public void updateFinanceMain(FinanceMain financeMain){
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");

	}

	public DisbursementDetailDialogCtrl getDisbursementDetailDialogCtrl() {
		return disbursementDetailDialogCtrl;
	}

	public void setDisbursementDetailDialogCtrl(
			DisbursementDetailDialogCtrl disbursementDetailDialogCtrl) {
		this.disbursementDetailDialogCtrl = disbursementDetailDialogCtrl;
	}
}