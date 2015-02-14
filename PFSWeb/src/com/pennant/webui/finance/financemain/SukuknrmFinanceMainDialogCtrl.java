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
 * FileName    		:  SukuknrmFinanceMainDialogCtrl.java                                                   * 	  
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
import java.util.Calendar;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

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
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTDateValidator;
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
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SukuknrmFinanceMainDialogCtrl extends FinanceBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(SukuknrmFinanceMainDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SukuknrmFinanceMainDialog; 				// autoWired

	//Finance Main Details Tab---> 1. Key Details

	protected Label 		label_SukuknrmFinanceMainDialog_ScheduleMethod;
	protected Label 		label_SukuknrmFinanceMainDialog_FinRepayPftOnFrq;
	protected Label 		label_SukuknrmFinanceMainDialog_CommitRef; 	// autoWired
	protected Label 		label_SukuknrmFinanceMainDialog_DepriFrq; 	// autoWired
	protected Label 		label_SukuknrmFinanceMainDialog_PlanDeferCount;	// autoWired
	protected Label 		label_SukuknrmFinanceMainDialog_CbbApproved;
	protected Label 		label_SukuknrmFinanceMainDialog_AlwGrace;
	protected Label 		label_SukuknrmFinanceMainDialog_GraceMargin; 		// autoWired
	protected Label 		label_SukuknrmFinanceMainDialog_StepPolicy; 		// autoWired
	protected Label 		label_SukuknrmFinanceMainDialog_numberOfSteps; 		// autoWired

	protected JdbcSearchObject<Customer> custCIFSearchObject;
	
	Date startDate = (Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");
	Date endDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_END_DATE");

	/**
	 * default constructor.<br>
	 */
	public SukuknrmFinanceMainDialogCtrl() {
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
	public void onCreate$window_SukuknrmFinanceMainDialog(Event event) throws Exception {
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

		setMainWindow(window_SukuknrmFinanceMainDialog);
		setLabel_FinanceMainDialog_CbbApproved(label_SukuknrmFinanceMainDialog_CbbApproved);
		setLabel_FinanceMainDialog_CommitRef(label_SukuknrmFinanceMainDialog_CommitRef);
		setLabel_FinanceMainDialog_DepriFrq(label_SukuknrmFinanceMainDialog_DepriFrq);
		setLabel_FinanceMainDialog_FinRepayPftOnFrq(label_SukuknrmFinanceMainDialog_FinRepayPftOnFrq);
		setLabel_FinanceMainDialog_PlanDeferCount(label_SukuknrmFinanceMainDialog_PlanDeferCount);
		setLabel_FinanceMainDialog_AlwGrace(label_SukuknrmFinanceMainDialog_AlwGrace);
		setLabel_FinanceMainDialog_GraceMargin(label_SukuknrmFinanceMainDialog_GraceMargin);
		setLabel_FinanceMainDialog_StepPolicy(label_SukuknrmFinanceMainDialog_StepPolicy);
		setLabel_FinanceMainDialog_numberOfSteps(label_SukuknrmFinanceMainDialog_numberOfSteps);
		setProductCode("Sukuknrm");


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
	public void onClose$window_SukuknrmFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
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
					window_SukuknrmFinanceMainDialog, map);
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
		PTMessageUtils.showHelpWindow(event, window_SukuknrmFinanceMainDialog);
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
			closeWindow();
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
		closeDialog(this.window_SukuknrmFinanceMainDialog, "FinanceMainDialog");
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

			//Fee Details Tab Addition
			appendFeeDetailsTab(true);

			//Asset Details Tab Addition
			if(moduleDefiner.equals("")){
				appendAssetDetailTab();
			}

			//Schedule Details Tab Adding
			appendScheduleDetailTab(true, false);
		}

		//Joint Account and Guaranteer  Tab Addition
		if (!finDivision.equals(PennantConstants.FIN_DIVISION_COMMERCIAL) && !finDivision.equals(PennantConstants.FIN_DIVISION_CORPORATE)) {
			if(moduleDefiner.equals("")){
				appendJointGuarantorDetailTab();
			}
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
	 * @throws Exception 
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
 		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		doClearMessage();
 		doSetValidation();
		doSetLOVValidation();
		
		super.doWriteComponentsToBean(aFinanceSchData, wve);
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		
		try {

			if (this.downPayBank.getValue() == null) {
				this.downPayBank.setValue(BigDecimal.ZERO);
			}

			if (recSave) {

				aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));
				aFinanceMain.setDownPaySupl(BigDecimal.ZERO);
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
						(this.downPayBank.getValue()), formatter));

			} else if (!this.downPayBank.isReadonly()) {

				this.downPayBank.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount());

				BigDecimal downPayment = this.downPayBank.getValue();

				if (downPayment.compareTo(this.finAmount.getValue()) > 0) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("MAND_FIELD_MIN",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_DownPayment.value"),
							reqDwnPay.toString(),PennantAppUtil.formatAmount(this.finAmount.getValue(),
									formatter,false).toString() }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("PERC_MIN",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_DownPayBS.value"),
							PennantAppUtil.formatAmount(reqDwnPay, formatter, false).toString()}));
				}
			}
			aFinanceMain.setDownPayAccount(PennantApplicationUtil.unFormatAccountNumber(this.downPayAccount.getValue()));

			aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));
			aFinanceMain.setDownPaySupl(BigDecimal.ZERO);
			aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(wve.isEmpty()){
			aFinanceSchData = super.doWriteSchData(aFinanceSchData, false);
		}

		//FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug("Leaving");
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
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * */
	public boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		return super.doValidation(auditHeader);
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
			if(afinanceDetail.getFinScheduleData().getFinanceMain().isNew() && 
					!afinanceDetail.getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()){
				changeFrequencies();
				this.finReference.focus();
			}

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			if(financeType.getFinMinTerm() == 1 &&  financeType.getFinMaxTerm() == 1){
				if(!financeType.isFinRepayPftOnFrq()){
					this.label_SukuknrmFinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}else{
					this.label_SukuknrmFinanceMainDialog_FinRepayPftOnFrq.setVisible(true);
					this.rpyPftFrqRow.setVisible(true);
					this.hbox_finRepayPftOnFrq.setVisible(true);
				}

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				this.label_SukuknrmFinanceMainDialog_ScheduleMethod.setVisible(false);
				this.noOfTermsRow.setVisible(false);
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
			if (moduleDefiner.equals("")){
				setDiscrepancy(getFinanceDetail());
			}
			setDialog(this.window_SukuknrmFinanceMainDialog);

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * @throws Exception 
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws Exception{
		logger.debug("Entering");

		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finAmount.getValue();
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
			this.disbAcctId.validateValue();
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
	 * @throws Exception 
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception{
		logger.debug("Entering");

		if(!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")){

			if(onLoadProcess){
				doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			}
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			DataSet dataSet = AEAmounts.createDataSet(finMain, eventCode, finMain.getFinStartDate(), finMain.getFinStartDate());

			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			amountCodes = AEAmounts.procAEAmounts(getFinanceDetail().getFinScheduleData().getFinanceMain(),
					getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(), new FinanceProfitDetail(), curBDay);

			setAmountCodes(amountCodes);

			List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

			// Loop Repetation for Multiple Disbursement
			if(getFinanceDetail().getFinScheduleData().getDisbursementDetails() != null &&
					getFinanceDetail().getFinScheduleData().getDisbursementDetails().size() > 0){

				for (FinanceDisbursement disbursement : getFinanceDetail().getFinScheduleData().getDisbursementDetails()) {

					if(disbursement.getDisbAmount().compareTo(BigDecimal.ZERO) > 0){

						if(eventCode.equals("")){
							if(!moduleDefiner.equals("")){
								if(disbursement.getDisbReqDate().compareTo(curBDay) != 0){
									continue;
								}
							}
							if (disbursement.getDisbDate().after((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"))) {
								dataSet.setFinEvent("ADDDBSF");
							} else {
								dataSet.setFinEvent("ADDDBSP");
							}
						}

						if(StringUtils.trimToEmpty(disbursement.getDisbAccountId()).equals("")){
							dataSet.setDisburseAccount(dataSet.getDisburseAccount());
						}else{
							dataSet.setDisburseAccount(disbursement.getDisbAccountId());
						}
						dataSet.setDisburseAmount(disbursement.getDisbAmount());

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
				aeCommitment.setDISBURSE(CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(),
						finMain.getFinAmount().subtract(finMain.getDownPayment() == null ? BigDecimal.ZERO : finMain.getDownPayment())));
				aeCommitment.setRPPRI(BigDecimal.ZERO);

				getFinanceDetail().setCmtDataSetList(getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTDISB", "N", null));
				getAccountingDetailDialogCtrl().doFillCmtAccounting(getFinanceDetail().getCmtDataSetList(), commitment.getCcyEditField());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Returning Fiannce Amount and Currency Data for Contributor Validation 
	 * @return
	 */
	public List<Object> prepareContributor(){
		logger.debug("Entering");
		List<Object> list = new ArrayList<Object>();

		this.finAmount.setConstraint("");
		this.finCcy.setConstraint("");
		this.finAmount.setErrorMessage("");
		this.finCcy.setErrorMessage("");

		list.add(this.finAmount.getValue());
		list.add(this.finCcy.getValue());
		logger.debug("Leaving");
		return list;

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

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
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
		this.oldVar_finRemarks = this.finRemarks.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finContractDate = this.finContractDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_downPayBank = this.downPayBank.getValue();
		this.oldVar_downPayAccount = this.downPayAccount.getValue();
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_planDeferCount = this.planDeferCount.intValue();
		this.oldVar_finBranch = this.finBranch.getValue();
		this.oldVar_lovDescFinBranchName = this.finBranch.getDescription();
		this.oldVar_disbAcctId = this.disbAcctId.getValue();
		this.oldVar_repayAcctId = this.repayAcctId.getValue();
		this.oldVar_commitmentRef = this.commitmentRef.getValue();
		this.oldVar_depreciationFrq = this.depreciationFrq.getValue();
		this.oldVar_finIsActive = this.finIsActive.isChecked();
		this.oldVar_finPurpose = this.finPurpose.getValue();
		this.oldVar_lovDescFinPurpose = this.finPurpose.getDescription();

		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_graceTerms = this.graceTerms_Two.intValue();
			this.oldVar_allowGrace  = this.allowGrace.isChecked();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_allowGrcRepay = this.allowGrcRepay.isChecked();
			this.oldVar_graceBaseRate = this.graceBaseRate.getValue();
			this.oldVar_lovDescGraceBaseRateName = this.graceBaseRate.getDescription();
			this.oldVar_graceSpecialRate = this.graceSpecialRate.getValue();
			this.oldVar_lovDescGraceSpecialRateName = this.graceSpecialRate.getDescription();
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
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayBaseRate.getValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_lovDescRepayBaseRateName = this.repayBaseRate.getDescription();
		this.oldVar_repaySpecialRate = this.repaySpecialRate.getValue();
		this.oldVar_lovDescRepaySpecialRateName = this.repaySpecialRate.getDescription();
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
		this.finRemarks.setValue(this.oldVar_finRemarks);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finContractDate.setValue(this.oldVar_finContractDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.downPayBank.setValue(this.oldVar_downPayBank);
		this.finRepaymentAmount.setValue(this.oldVar_finRepaymentAmount);
		this.custID.setValue(this.oldVar_custID);
		this.defferments.setValue(this.oldVar_defferments);
		this.planDeferCount.setValue(this.oldVar_planDeferCount);
		this.finBranch.setValue(this.oldVar_finBranch);
		this.finBranch.setDescription(this.oldVar_lovDescFinBranchName);
		this.downPayAccount.setValue(this.oldVar_downPayAccount);
		this.disbAcctId.setValue(this.oldVar_disbAcctId);
		this.repayAcctId.setValue(this.oldVar_repayAcctId);
		this.commitmentRef.setValue(this.oldVar_commitmentRef);
		this.depreciationFrq.setValue(this.oldVar_depreciationFrq);
		this.finIsActive.setChecked(this.oldVar_finIsActive);
		this.finPurpose.setValue(this.oldVar_finPurpose);
		this.finPurpose.setDescription(this.oldVar_lovDescFinPurpose);

		// Step Finance Details
		this.stepFinance.setChecked(this.oldVar_stepFinance);
		this.stepPolicy.setValue(this.oldVar_stepPolicy);
		this.alwManualSteps.setChecked(this.oldVar_alwManualSteps);
		this.noOfSteps.setValue(this.oldVar_noOfSteps);

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.graceTerms.setValue(this.oldVar_graceTerms);
			this.cbGrcSchdMthd.setSelectedIndex(this.oldVar_grcSchdMthd);
			this.grcRateBasis.setSelectedIndex(this.oldVar_grcRateBasis);
			this.allowGrcRepay.setChecked(this.oldVar_allowGrcRepay);
			this.graceBaseRate.setValue(this.oldVar_graceBaseRate);
			this.graceBaseRate.setDescription(this.oldVar_lovDescGraceBaseRateName);
			this.graceSpecialRate.setValue(this.oldVar_graceSpecialRate);
			this.graceSpecialRate.setDescription(this.oldVar_lovDescGraceSpecialRateName);
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
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.repayRateBasis.setSelectedIndex(this.oldVar_repayRateBasis);
		this.repayBaseRate.setValue(this.oldVar_repayBaseRate);
		this.repayBaseRate.setDescription(this.oldVar_lovDescRepayBaseRateName);
		this.repaySpecialRate.setValue(this.oldVar_repaySpecialRate);
		this.repaySpecialRate.setDescription(this.oldVar_lovDescRepaySpecialRateName);
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
		if (this.oldVar_depreciationFrq != this.depreciationFrq.getValue()) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
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
		if (this.oldVar_planDeferCount != this.planDeferCount.intValue()) {
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

		BigDecimal oldFinRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount, formatter);
		BigDecimal newFinRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(), formatter);

		if (oldFinRepayAmount.compareTo(newFinRepayAmount) != 0) {
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
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
			return true;
		}
		
		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
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
		if (getFeeDetailDialogCtrl() != null && getFeeDetailDialogCtrl().isDataChanged()) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteEligibilityDetail(){
		logger.debug("Entering");

		doSetValidation();
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		showErrorDetails(wve, financeTypeDetailsTab);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		//FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && 
				!financeType.isFinIsGenRef()) {

			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinReference.value"),null,true));
		}

		if (!this.finAmount.isReadonly()) {
			this.finAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_SukuknrmFinanceMainDialog_FinAmount.value"), false));
		}
		if(!this.defferments.isReadonly()){
			this.defferments.setConstraint(new PTNumberValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_Defferments.value"), false, false));
		}

		if(!this.planDeferCount.isReadonly()){
			this.planDeferCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_PlanDeferCount.value"), false, false));
		}

		if(!this.stepPolicy.isReadonly() && this.stepFinance.isChecked() && !this.alwManualSteps.isChecked()){
			this.stepPolicy.setConstraint(new PTStringValidator( Labels.getLabel("label_SukuknrmFinanceMainDialog_StepPolicy.value"), null, true,true));
		}
        
		if(!this.noOfSteps.isReadonly() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()){
			this.noOfSteps.setConstraint(new PTNumberValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NumberOfSteps.value"), true, false));
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {
			
			if (!this.graceTerms.isReadonly()) {
				this.graceTerms.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_GraceTerms.value"),null,true));
			}
			
			if (!this.grcMargin.isReadonly()) {
				this.grcMargin.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_SukuknrmFinanceMainDialog_GraceMargin.value"), true));
			}

			if(this.allowGrace.isChecked()){
				this.grcEffectiveRate.setConstraint(new RateValidator(13, 9,
						Labels.getLabel("label_SukuknrmFinanceMainDialog_GracePftRate.value"), true));
			}

			if (!this.nextGrcPftDate.isReadonly() && 
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NextGrcPftDate.value"),true));
			}

			if (!this.nextGrcPftRvwDate.isReadonly() && 
					FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NextGrcPftRvwDate.value"),true));
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.nextRepayDate.isReadonly() && 
				FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NextRepayDate.value"),true));
		}

		if (!this.nextRepayPftDate.isReadonly() && 
				FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NextRepayPftDate.value"),true));
		}

		if (!this.nextRepayRvwDate.isReadonly() && 
				FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NextRepayRvwDate.value"),true));
		}

		if (!this.nextRepayCpzDate.isReadonly() && 
				FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_NextRepayCpzDate.value"),true));
		}

		this.repayEffectiveRate.setConstraint(new RateValidator(13, 9,
				Labels.getLabel("label_SukuknrmFinanceMainDialog_ProfitRate.value"), true));

		if (!this.repayMargin.isReadonly()) {
			this.repayMargin.setConstraint(new RateValidator(13, 9, 
					Labels.getLabel("label_SukuknrmFinanceMainDialog_RepayMargin.value"), true));
		}

		if(financeType.getFinMinTerm() == 1 &&
				financeType.getFinMaxTerm() == 1){

			this.maturityDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_MaturityDate.value"),true));
		}
		if(!this.finStartDate.isReadonly()){
			this.finStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinStartDate.value"), true,startDate,endDate,false));
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
		this.finRemarks.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finContractDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayBank.setConstraint("");
		this.downPayAccount.setConstraint("");
		//M_ this.custID.setConstraint("");
		this.defferments.setConstraint("");
		this.planDeferCount.setConstraint("");
		this.finBranch.setConstraint("");
		this.disbAcctId.setConstraint("");
		this.repayAcctId.setConstraint("");
		this.commitmentRef.setConstraint("");
		this.depreciationFrq.setConstraint("");
		
		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setConstraint("");
		this.graceTerms.setConstraint("");
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

		this.lovDescFinTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinType.value"),null,true));

		this.finCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinCcy.value"),null,true,true));

		if (!this.finBranch.isReadonly()) {
			this.finBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinBranch.value"),null,true,true));
		}

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_CustID.value"),null,true,true));
		}

		if (!recSave && StringUtils.trimToEmpty(this.disbAcctId.getSclass()).equals("mandatory")) {
			this.disbAcctId.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_DisbAcctId.value"),null,true));
		}

		if (!recSave && this.repayAcctId.getSclass().equals("mandatory")) {
			this.repayAcctId.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_RepayAcctId.value"),null,true));
		}

		if (!recSave && this.downPayAccount.getSclass().equals("mandatory")) {
			this.downPayAccount.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_DownPayAccount.value"),null,true));
		}

		if (!this.btnSearchCommitmentRef.isDisabled() && 
				StringUtils.trimToEmpty(space_commitmentRef.getSclass()).equals("mandatory")) {
			this.lovDescCommitmentRefName.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_CommitRef.value"),null,true));
		}

		if (!this.finPurpose.isReadonly()) {
			this.finPurpose.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinPurpose.value"),null,true,true));
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if(!this.graceBaseRate.isReadonly()) {
			this.graceBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_GraceBaseRate.value"),null,true,true));
		}

		if(this.allowGrcInd.isChecked() && !this.btnSearchGrcIndBaseRate.isDisabled()){
			this.lovDescGrcIndBaseRateName.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinGrcIndBaseRate.value"),null,true));			
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if(!this.repayBaseRate.isReadonly()) {
			this.repayBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_RepayBaseRate.value"),null,true,true));
		}

		if(this.allowRpyInd.isChecked() && !this.btnSearchRpyIndBaseRate.isDisabled()){
			this.lovDescRpyIndBaseRateName.setConstraint(new PTStringValidator(Labels.getLabel("label_SukuknrmFinanceMainDialog_FinRpyIndBaseRate.value"),null,true));			
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

		this.graceBaseRate.setConstraint("");
		this.graceSpecialRate.setConstraint("");
		this.lovDescGrcIndBaseRateName.setConstraint("");

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayBaseRate.setConstraint("");
		this.repaySpecialRate.setConstraint("");
		this.lovDescRpyIndBaseRateName.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages.
	 * */
	public void doClearMessage() {
		logger.debug("Entering");
		super.doClearMessage();
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
					closeWindow();
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_SukuknrmFinanceMainDialog, e);
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

		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setDisabled(isReadOnly("FinanceMainDialog_downPayment"));
			this.downPayAccount.setReadonly(isReadOnly("FinanceMainDialog_downPaymentAcc"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		super.doReadOnly();
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		super.doClear();
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
		recSave = false;
		if (this.userAction.getSelectedItem() != null){
			if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") ||
				this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel") ||
				this.userAction.getSelectedItem().getLabel().contains("Reject") ||
				this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
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
			if(!recSave && !doValidateCommitment(aFinanceDetail)){
				return;
			}
		}
		
		//Validation For Mandatory Recommendation
		if(!doValidateRecommendation()){
			return;
		}

		String tempRecordStatus = aFinanceMain.getRecordType();
		isNew = aFinanceDetail.isNew();
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
			boolean validationSuccess = doSave_CheckList(aFinanceDetail,false);
			if(!validationSuccess){
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		//Finance Fee Charge Details Tab
		if (getFeeDetailDialogCtrl() != null &&  getFinanceDetail().getFinScheduleData().getFeeRules() != null &&
				getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0) {
			// check if fee & charges rules executed or not
			if (!getFeeDetailDialogCtrl().isFeeChargesExecuted()) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_Fee"));
				return;
			}
		}

		if(StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")){
			aFinanceDetail.setStageAccountingList(null);
		}else{

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

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

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
				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_SukuknrmFinanceMainDialog, e);
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

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (getWorkFlow().getAuditingReq(taskId, afinanceMain).contains(PennantConstants.WF_Audit_Notes)) {
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
					tFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(),aFinanceDetail, this.window_SukuknrmFinanceMainDialog);
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
					
					List<ErrorDetails> discrepancies = getFinanceDetailService().getDiscrepancies(aFinanceDetail);
					boolean isDispError = getDiscrepancyDetails(discrepancies,aFinanceDetail, false);
					String discrepancyDetails =  aFinanceDetail.getFinScheduleData().getFinanceMain().getLimitStatus();
					String msg = "";
					if (!StringUtils.trimToEmpty(discrepancyDetails).equals("")) {
						final String title = Labels.getLabel("title.Discrepancy");
						MultiLineMessageBox.doSetTemplate();
						if(isDispError){
							msg = Labels.getLabel("message.Discrepancy_Error",new String[]{discrepancyDetails+"\n"});
							MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, Messagebox.ERROR, true);
							return false;
						}else{
							msg = Labels.getLabel("message.Discrepancy_Warning",new String[]{discrepancyDetails+"\n"});
							int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
							if (conf != MultiLineMessageBox.YES) {
								return false;
							}
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

			if (getWorkFlow().getAuditingReq(taskId, afinanceMain).contains(PennantConstants.WF_DiscrepancyCheck)){
				
				List<ErrorDetails> discrepancies = getFinanceDetailService().getDiscrepancies(aFinanceDetail);
				boolean isDispError = getDiscrepancyDetails(discrepancies,aFinanceDetail, false);
				String discrepancyDetails =  aFinanceDetail.getFinScheduleData().getFinanceMain().getLimitStatus();
				String msg = "";
				if (!StringUtils.trimToEmpty(discrepancyDetails).equals("")) {
					final String title = Labels.getLabel("title.Discrepancy");
					MultiLineMessageBox.doSetTemplate();
					if(isDispError){
						msg = Labels.getLabel("message.Discrepancy_Error",new String[]{discrepancyDetails+"\n"});
						MultiLineMessageBox.show(msg, title, MultiLineMessageBox.OK, Messagebox.ERROR, true);
						return false;
					}else{
						msg = Labels.getLabel("message.Discrepancy_Warning",new String[]{discrepancyDetails+"\n"});
						int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
						if (conf != MultiLineMessageBox.YES) {
							return false;
						}
					}
				}
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
						retValue = ErrorControl.showErrorControl(this.window_SukuknrmFinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SukuknrmFinanceMainDialog, auditHeader);
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

		Object dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "FinanceType");
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
		Object dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "Currency");
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {

				this.disbAcctId.setValue("");
				this.repayAcctId.setValue("");
				this.finCcy.setValue(details.getCcyCode(), details.getCcyDesc());

				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescFinFormatter(details.getCcyEditField());
				getFinanceDetail().getFinScheduleData().getFinanceMain().setFinCcy(details.getCcyCode());
				
				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));

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
				if (moduleDefiner.equals("")){
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(PennantAppUtil.unFormateAmount
							(this.finAmount.getValue(), details.getCcyEditField()));
					setDiscrepancy(getFinanceDetail());
				}
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/*
	 * onFullFill Event For CustCIF
	 */
	/*public void onFulfill$custCIF(Event event){
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
			}
		}
		//doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}*/

	/**
	 * when clicks on button "btnSearchDisbAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 *//*
	public void onClick$btnSearchDisbAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.disbAcctId.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();
		this.downPayAccount.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());

			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);

				dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.disbAcctId.setValue(dataObject.toString());
					this.disbAcctBal.setValue(getAcBalance(""));
				} else {
					IAccounts details = (IAccounts) dataObject;

					if (details != null) {
						this.disbAcctId.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
						this.disbAcctBal.setValue(getAcBalance(details.getAccountId()));

						if(StringUtils.trimToEmpty(this.downPayAccount.getValue()).equals("")){
							this.downPayAccount.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
							this.downPayAccBal.setValue(getAcBalance(details.getAccountId()));
						}

						if(StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
							this.repayAcctId.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
							this.repayAcctBal.setValue(getAcBalance(details.getAccountId()));
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_SukuknrmFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}
*/
/*	*//**
	 * when clicks on button "btnSearchDownPayAccount"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 *//*
	public void onClick$btnSearchDownPayAcc(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.downPayAccount.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());

			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);

				dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.downPayAccount.setValue(dataObject.toString());
					this.downPayAccBal.setValue(getAcBalance(""));
				} else {
					IAccounts details = (IAccounts) dataObject;

					if (details != null) {
						this.downPayAccount.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
						this.downPayAccBal.setValue(getAcBalance(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_SukuknrmFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}
*/
/*	*//**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 *//*
	public void onClick$btnSearchRepayAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();
		this.downPayAccount.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());

			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.repayAcctId.setValue(dataObject.toString());
					this.repayAcctBal.setValue(getAcBalance(""));
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.repayAcctId.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
						this.repayAcctBal.setValue(getAcBalance(details.getAccountId()));

						if(StringUtils.trimToEmpty(this.downPayAccount.getValue()).equals("")){
							this.downPayAccount.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
							this.downPayAccBal.setValue(getAcBalance(details.getAccountId()));
						}

					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_SukuknrmFinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}
*/
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
//		filters[1] = new Filter("cmtCcy", this.finCcy.getValue(), Filter.OP_EQUAL);
		if(this.finStartDate.getValue() != null){
			filters[1] = new Filter("CmtExpDate", DateUtility.formatUtilDate(this.finStartDate.getValue(), PennantConstants.DBDateFormat), Filter.OP_GREATER_OR_EQUAL);
		}else{
			throw new WrongValueException(finStartDate,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_SukuknrmFinanceMainDialog_FinStartDate.value")}));
		}

		Object dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "Commitment", filters);

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
		//	executeAccounting(true);

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
		onCheckCustAcceptance();		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Check Method for Accessing Customer Acceptance Approval
	 * @param event
	 */
	public void onCheck$custAcceptance(Event event){
		logger.debug("Entering " + event.toString());
		onCheckCustAcceptance();		
		logger.debug("Leaving " + event.toString());
	}

	private void onCheckCustAcceptance(){
		this.cbbApprovalRequired.setChecked(false);
		this.cbbApproved.setChecked(false);
		this.label_SukuknrmFinanceMainDialog_CbbApproved.setVisible(false);
		this.hbox_cbbApproved.setVisible(false);
		this.row_cbbApproval.setVisible(false);

		if(this.custAcceptance.isChecked()){
			this.row_cbbApproval.setVisible(!isReadOnly("FinanceMainDialog_cbbApprovalRequired"));
			readOnlyComponent(!isReadOnly("FinanceMainDialog_cbbApprovalRequired"), this.cbbApprovalRequired);
		}
	}

	/**
	 * Check Method for Accessing Approval From CBB committee
	 * @param event
	 */
	public void onCheck$cbbApprovalRequired(Event event){
		logger.debug("Entering " + event.toString());
		onCheckCBBApproval(false);
		logger.debug("Leaving " + event.toString());
	}

	//FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.grcEffectiveRate.setConstraint("");
		Object dataObject = graceBaseRate.getObject();

		if (dataObject instanceof String) {
			this.graceBaseRate.setValue(dataObject.toString());
			this.graceBaseRate.setDescription("");
			this.grcEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.graceBaseRate.setValue(details.getBRType());
				this.graceBaseRate.setDescription(details.getBRTypeDesc());
			}
		}

		calculateRate(this.graceBaseRate, this.graceSpecialRate, this.graceBaseRate, this.grcMargin, this.grcEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "GraceSpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceSpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.grcEffectiveRate.setConstraint("");
		Object dataObject = graceSpecialRate.getObject();

		if (dataObject instanceof String) {
			this.graceSpecialRate.setValue(dataObject.toString());
			this.graceSpecialRate.setDescription("");
			this.grcEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.graceSpecialRate.setValue(details.getSRType());
				this.graceSpecialRate.setDescription(details.getSRTypeDesc());
			}
		}

		calculateRate(this.graceBaseRate, this.graceSpecialRate, this.graceBaseRate, this.grcMargin, this.grcEffectiveRate);

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
			readOnlyComponent(false, this.cbGrcSchdMthd);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
					schMethodList, ",EQUAL,PRI_PFT,PRI,");
		} else {
			readOnlyComponent(true, this.cbGrcSchdMthd);
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

		Object dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "BaseRateCode");
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
	public void onFulfill$repayBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.repayEffectiveRate.setConstraint("");
		Object dataObject = repayBaseRate.getObject();

		if (dataObject instanceof String) {
			this.repayBaseRate.setValue(dataObject.toString());
			this.repayBaseRate.setDescription("");
			this.repayEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.repayBaseRate.setValue(details.getBRType());
				this.repayBaseRate.setDescription(details.getBRTypeDesc());
			}
		}

		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.repayBaseRate, this.repayMargin, this.repayEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchRepaySpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$repaySpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.repayEffectiveRate.setConstraint("");
		Object dataObject = repaySpecialRate.getObject();

		if (dataObject instanceof String) {
			this.repaySpecialRate.setValue(dataObject.toString());
			this.repaySpecialRate.setDescription("");
			this.repayEffectiveRate.setValue(BigDecimal.ZERO);
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.repaySpecialRate.setValue(details.getSRType());
				this.repaySpecialRate.setDescription(details.getSRTypeDesc());
			}
		}

		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.repayBaseRate, this.repayMargin, this.repayEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * To get the BaseRateCode LOV List From RMTBaseRateCodes Table
	 * @param event
	 */
	public void onClick$btnSearchRpyIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_SukuknrmFinanceMainDialog, "BaseRateCode");
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

			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeType"), this.oDChargeType);
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"),this.oDAllowWaiver);

			if(checkAction){
				readOnlyComponent(true, this.oDChargeAmtOrPerc);
				readOnlyComponent(true, this.oDMaxWaiverPerc);
			}else{
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}

		}else{
			readOnlyComponent(true, this.oDIncGrcDays);
			readOnlyComponent(true, this.oDChargeType);
			this.oDGraceDays.setReadonly(true);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			readOnlyComponent(true, this.oDAllowWaiver);
			readOnlyComponent(true, this.oDMaxWaiverPerc);

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
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			this.space_oDChargeAmtOrPerc.setSclass("");
		}else if (getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
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
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
		}else {
			readOnlyComponent(true, this.oDMaxWaiverPerc);
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
 			
 			//Calculation Process for Planned Deferment Profit in below Case by adding Terms & adjusting Maturity Date
 			BigDecimal plannedDeferPft = BigDecimal.ZERO;
 			if(validFinScheduleData.getFinanceMain().getPlanDeferCount() > 0){
 				
 				Cloner cloner = new Cloner();
				FinScheduleData planDeferSchdData = cloner.deepClone(validFinScheduleData);
				
				//Terms Recalculation
				FinanceMain planFinMain = planDeferSchdData.getFinanceMain();
				planFinMain.setNumberOfTerms(planFinMain.getNumberOfTerms() + planFinMain.getDefferments());
				
				//Maturity Date Recalculation using Number of Terms
				List<Calendar> scheduleDateList = null;				
				if(this.finRepayPftOnFrq.isChecked()){
					
					Date nextPftDate = this.nextRepayPftDate.getValue();
					if(nextPftDate == null){
						nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate();
					}
					
					scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
							planFinMain.getNumberOfTerms(), nextPftDate, "A", true).getScheduleList();
				}else{
					scheduleDateList = FrequencyUtil.getNextDate(this.repayFrq.getValue(),
							planFinMain.getNumberOfTerms(), this.nextRepayDate_two.getValue(), "A", true).getScheduleList();
				}

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					planFinMain.setMaturityDate(calendar.getTime());
				}			
				
				planDeferSchdData = ScheduleGenerator.getNewSchd(planDeferSchdData);
				planDeferSchdData = ScheduleCalculator.getPlanDeferPft(planDeferSchdData);
				
				FinanceMain planDefFinMain = planDeferSchdData.getFinanceMain();
				
				if (planDefFinMain.isAllowGrcPeriod() && StringUtils.trimToEmpty(planDefFinMain.getGrcRateBasis()).equals(CalculationConstants.RATE_BASIS_R)
				        && planDefFinMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)
				        && StringUtils.trimToEmpty(planDefFinMain.getGrcSchdMthd()).equals(CalculationConstants.NOPAY)) {
					plannedDeferPft = planDefFinMain.getTotalGrossPft();
				} else {
					plannedDeferPft = planDefFinMain.getTotalGrossPft().subtract(planDefFinMain.getTotalGrossGrcPft());
				}
				
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
						getFinanceDetail().getFinScheduleData(), plannedDeferPft));
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

	/**
	 * Method for Reset Customer Data
	 */
	private void setCustomerData(){
		logger.debug("Entering");
		
		this.custID.setValue(customer.getCustID());
		this.custCIF.setValue(customer.getCustCIF());
		this.custShrtName.setValue(customer.getCustShrtName());
		this.disbAcctId.setCustCIF(customer.getCustCIF());
		this.repayAcctId.setCustCIF(customer.getCustCIF());
		this.downPayAccount.setCustCIF(customer.getCustCIF());
		this.disbAcctId.setValue("");
		this.repayAcctId.setValue("");
		this.downPayAccount.setValue("");
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

		FinanceDetail financeDetail = getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName();

		financeMain.setCustID(customer.getCustID());
		setFinanceDetail(getFinanceDetailService().fetchFinCustDetails(financeDetail, custCtgType,financeMain.getFinType(), getRole()));
		financeMain.setLovDescCustFName(StringUtils.trimToEmpty(customer.getCustFName()));
		financeMain.setLovDescCustLName(StringUtils.trimToEmpty(customer.getCustLName()));
		financeMain.setLovDescCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));

		//Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
				financeMain.getMaturityDate(), true);

		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		// Set Customer Data to check the eligibility
		financeDetail.setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
				productCode, financeMain.getFinCcy(), curFinRepayAmt, months, financeMain.getFinAmount(), financeMain.getCustDSR()));

		setCustomerScoringData();

		// Execute Eligibility Rule and Display Result
		if(getEligibilityDetailDialogCtrl() != null){
			getEligibilityDetailDialogCtrl().doFillExecElgList(financeDetail.getFinElgRuleList());
			//	getEligibilityDetailDialogCtrl().doFillEligibilityListbox(financeDetail.getEligibilityRuleList(), false);
		}else{
			appendEligibilityDetailTab(false);
		}

		//Scoring Detail Tab
		financeMain.setLovDescCustCtgTypeName(custCtgType);
		appendFinScoringDetailTab(false);

		//Agreement Details Tab
		setAgreementDetailTab(this.window_SukuknrmFinanceMainDialog);

		// Fill Check List Details based on Rule Execution if Rule Exist
		appendCheckListDetailTab(financeDetail, financeMain.isNewRecord(),false);

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if(getWorkFlow().getTaskTabs(getWorkFlow().getTaskId(getRole())).equals("Accounting")){

			// Finance Accounting Posting Details
			if(getAccountingDetailDialogCtrl() != null){
				getAccountingDetailDialogCtrl().doFillAccounting(financeDetail.getTransactionEntries());
				if(!StringUtils.trimToEmpty(this.commitmentRef.getValue()).equals("")){
					getAccountingDetailDialogCtrl().doFillCmtAccounting(financeDetail.getCmtFinanceEntries(),0);
				}
			}else{
				setAccountingDetailTab(this.window_SukuknrmFinanceMainDialog);

			}
		}

		getDocumentDetailDialogCtrl().doFillDocumentDetails(financeDetail.getDocumentDetailsList());
		//Finance Stage Accounting Posting Details
		appendStageAccountingDetailsTab(false);
		if (moduleDefiner.equals("")){
			setDiscrepancy(getFinanceDetail());
		}
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

		this.graceBaseRate.setConstraint("");
		this.graceSpecialRate.setConstraint("");
		this.grcEffectiveRate.setConstraint("");

		this.graceBaseRate.setReadonly(true);
		this.graceSpecialRate.setReadonly(true);

		this.graceBaseRate.setValue("");
		this.graceSpecialRate.setValue("");
		this.graceBaseRate.setDescription("");
		this.graceSpecialRate.setDescription("");
		readOnlyComponent(true, this.gracePftRate);
		this.grcEffectiveRate.setText("0.00");
		this.gracePftRate.setText("0.00");

		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if(CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString()) || 
					CalculationConstants.RATE_BASIS_C.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.graceBaseRate.setReadonly(true);
				this.graceSpecialRate.setReadonly(true);

				this.graceBaseRate.setDescription("");
				this.graceSpecialRate.setDescription("");

				this.grcEffectiveRate.setText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
			}else if(CalculationConstants.RATE_BASIS_R.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					this.graceBaseRate.setReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceSpecialRate.setReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
				}else{
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
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

		this.repayBaseRate.setConstraint("");
		this.repaySpecialRate.setConstraint("");
		this.repayEffectiveRate.setConstraint("");

		this.repayBaseRate.setReadonly(true);
		this.repaySpecialRate.setReadonly(true);

		this.repayBaseRate.setValue("");
		this.repaySpecialRate.setValue("");
		this.repayBaseRate.setDescription("");
		this.repaySpecialRate.setDescription("");
		readOnlyComponent(true, this.repayProfitRate);
		this.repayEffectiveRate.setText("0.00");
		this.repayProfitRate.setText("0.00");

		if(!this.repayRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if(CalculationConstants.RATE_BASIS_F.equals(this.repayRateBasis.getSelectedItem().getValue().toString()) ||
					CalculationConstants.RATE_BASIS_C.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.repayBaseRate.setReadonly(true);
				this.repaySpecialRate.setReadonly(true);

				this.repayBaseRate.setDescription("");
				this.repaySpecialRate.setDescription("");

				this.repayEffectiveRate.setText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
			}else if(CalculationConstants.RATE_BASIS_R.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()).equals("")){
					this.repayBaseRate.setReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
					this.repaySpecialRate.setReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
				}else{
					readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
				}
				this.repayEffectiveRate.setText("0.00");
				this.repayProfitRate.setText("0.00");
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
		if(this.repayMargin.getValue() != null && !this.repayProfitRate.isReadonly()) {
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
			readOnlyComponent(true, this.allowRpyInd);
			this.allowRpyInd.setChecked(false);
		}else if(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw()) {
			readOnlyComponent(false, this.allowRpyInd);
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
	public FinanceDetail doProcess_Assets(FinanceDetail aFinanceDetail) {
		logger.debug("Entering"); 
		super.doProcess_Assets(aFinanceDetail);
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	/**
	 * Method to set transaction properties for assets while deleting
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
	public FinanceMain getFinanceMain(){
		FinanceMain financeMain =  super.getFinanceMain();
		financeMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayBank.getValue() == null ? BigDecimal.ZERO :this.downPayBank.getValue()
				,getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		return financeMain;
	}

	/**
	 * when user clicks on button "Notes"
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
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (finDivision.equals(PennantConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", window_SukuknrmFinanceMainDialog, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul", window_SukuknrmFinanceMainDialog, map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		setCustomer((Customer) nCustomer);
		setCustomerData();
		this.custCIFSearchObject = newSearchObject;
		logger.debug("Leaving ");
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}
	
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null, map);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Finance Schedule Detail For Maintenance purpose
	 */
	public void reRenderScheduleList(FinScheduleData aFinSchData){
		logger.debug("Entering");
		if(getScheduleDetailDialogCtrl() != null){
			getScheduleDetailDialogCtrl().doFillScheduleList(aFinSchData);
		}
		logger.debug("Leaving");
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
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}