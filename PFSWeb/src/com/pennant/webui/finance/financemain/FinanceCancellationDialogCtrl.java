package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.rits.cloning.Cloner;

public class FinanceCancellationDialogCtrl extends FinanceBaseCtrl implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(FinanceCancellationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceCancellationDialog; 				// autoWired

	//Finance Main Details Tab---> 1. Key Details
	protected CurrencyBox 	downPaySupl; 							// autoWired
	protected Row			row_downPaySupl;						// autoWired

	protected Label 		label_FinanceCancellationDialog_ScheduleMethod;
	protected Label 		label_FinanceCancellationDialog_FinRepayPftOnFrq;
	protected Label 		label_FinanceCancellationDialog_CommitRef; 	// autoWired
	protected Label 		label_FinanceCancellationDialog_DepriFrq; 	// autoWired
	protected Label 		label_FinanceCancellationDialog_FrqDef;	// autoWired
	protected Label 		label_FinanceCancellationDialog_CbbApproved;
	protected Label 		label_FinanceCancellationDialog_AlwGrace;
	protected Label         label_FinanceCancellationDialog_StepPolicy;
	protected Label         label_FinanceCancellationDialog_numberOfSteps;
	protected Label         label_FinanceCancellationDialog_GraceMargin;


	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.
	protected transient BigDecimal 		oldVar_downPaySupl;
	private FinanceCancellationService financeCancellationService;
	protected Listbox listBoxCancelFinancePosting;
	/**
	 * default constructor.<br>
	 */
	public FinanceCancellationDialogCtrl() {
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
	public void onCreate$window_FinanceCancellationDialog(Event event) throws Exception {
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

		setMainWindow(window_FinanceCancellationDialog);
		setLabel_FinanceMainDialog_CbbApproved(label_FinanceCancellationDialog_CbbApproved);
		setLabel_FinanceMainDialog_CommitRef(label_FinanceCancellationDialog_CommitRef);
		setLabel_FinanceMainDialog_DepriFrq(label_FinanceCancellationDialog_DepriFrq);
		setLabel_FinanceMainDialog_FinRepayPftOnFrq(label_FinanceCancellationDialog_FinRepayPftOnFrq);
		setLabel_FinanceMainDialog_FrqDef(label_FinanceCancellationDialog_FrqDef);
		setLabel_FinanceMainDialog_AlwGrace(label_FinanceCancellationDialog_AlwGrace);
		setLabel_FinanceMainDialog_numberOfSteps(label_FinanceCancellationDialog_numberOfSteps);
		setLabel_FinanceMainDialog_StepPolicy(label_FinanceCancellationDialog_StepPolicy);
		setLabel_FinanceMainDialog_GraceMargin(label_FinanceCancellationDialog_GraceMargin);
		setProductCode("Murabaha");


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
		this.downPaySupl.setMaxlength(18);
		this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
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

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
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
	public void onClose$window_FinanceCancellationDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		closeDialog(this.window_FinanceCancellationDialog, "FinanceMainDialog");
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
		doSave();
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
					window_FinanceCancellationDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceCancellationDialog);
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
			closeDialog(this.window_FinanceCancellationDialog, "FinanceMainDialog");
		} catch (final WrongValuesException e) {
			logger.error(e.getMessage());
			closeDialog(this.window_FinanceCancellationDialog, "FinanceMainDialog");
		}
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

		if (aFinanceMain.isLovDescDwnPayReq() && 
				aFinanceDetail.getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			this.row_downPaySupl.setVisible(true);
			this.downPaySupl.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPaySupl(),
					aFinanceMain.getLovDescFinFormatter()));
		}
		//Posting Details
		doFillPostingdetails(aFinanceMain.getFinReference());
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

		super.doWriteComponentsToBean(aFinanceSchData, wve);
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();

		try {

			if (this.downPayBank.getValue() == null) {
				this.downPayBank.setValue(BigDecimal.ZERO);
			}
			if (this.downPaySupl.getValue() == null) {
				this.downPaySupl.setValue(BigDecimal.ZERO);
			}

			if (recSave) {

				aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));
				aFinanceMain.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter));
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
						(this.downPayBank.getValue()).add(this.downPaySupl.getValue()), formatter));

			} else if (!this.downPayBank.isReadonly() || !this.downPaySupl.isReadonly()) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescMinDwnPayPercent());

				BigDecimal downPayment = this.downPayBank.getValue().add(this.downPaySupl.getValue());

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
			aFinanceMain.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter));
			aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
					this.downPayBank.getValue().add(this.downPaySupl.getValue()), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		//FinanceMain Details Tab Validation Error Throwing
		//showErrorDetails(wve, financeTypeDetailsTab);

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
		doReadOnly();
		if (!StringUtils.trimToEmpty(afinanceDetail.getFinScheduleData().getFinanceMain().getRecordType()).equals("")) {
			this.btnNotes.setVisible(true);
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
					this.label_FinanceCancellationDialog_FinRepayPftOnFrq.setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}else{
					this.label_FinanceCancellationDialog_FinRepayPftOnFrq.setVisible(true);
					this.rpyPftFrqRow.setVisible(true);
					this.hbox_finRepayPftOnFrq.setVisible(true);
				}

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				this.label_FinanceCancellationDialog_ScheduleMethod.setVisible(false);
				this.noOfTermsRow.setVisible(false);
			}

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			//setDiscrepancy(getFinanceDetail());
			setDialog(this.window_FinanceCancellationDialog);

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
		this.oldVar_lovDescFinCcyName = this.lovDescFinCcyName.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finContractDate = this.finContractDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_downPayBank = this.downPayBank.getValue();
		this.oldVar_downPaySupl = this.downPaySupl.getValue();
		this.oldVar_downPayAccount = this.downPayAccount.getValue();
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_frqDefferments = this.frqDefferments.intValue();
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

		this.oldVar_recordStatus = this.recordStatus.getValue();

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

		BigDecimal old_dwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal new_dwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (old_dwnPaySupl.compareTo(new_dwnPaySupl) != 0) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		super.doReadOnly();
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
		} 
		this.downPaySupl.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		super.doClear();
		this.downPaySupl.setValue("");
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

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
				}

				closeDialog(this.window_FinanceCancellationDialog, "FinanceMainDialog");
			} 

		} catch (final Exception e) {
			logger.error(e);
			showErrorMessage(this.window_FinanceCancellationDialog, e);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private String getServiceTasks(String taskId, FinanceMain financeMain,
			String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getWorkFlow().getOperationRefs(taskId, financeMain);

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

				if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {

				} else {
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
	 * @throws InterruptedException 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					auditHeader = getFinanceCancellationService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceCancellationService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceCancellationService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceCancellationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceCancellationDialog, auditHeader);
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
			PTMessageUtils.showErrorMessage(e.getErrorMsg());
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++Overdue Penalty Details++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++++++ OnBlur Events ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get the Finance Main Details from the Screen 
	 */
	public FinanceMain getFinanceMain(){
		FinanceMain financeMain =  super.getFinanceMain();
		financeMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayBank.getValue() == null ? BigDecimal.ZERO :this.downPayBank.getValue() .add(
				this.downPaySupl.getValue()== null ? BigDecimal.ZERO :this.downPaySupl.getValue()),getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		return financeMain;
	}

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
			map.put("custCIF", this.lovDescCustCIF.getValue());
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (finDivision.equals(PennantConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", window_FinanceCancellationDialog, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul", window_FinanceCancellationDialog, map);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
		}
	}

	private void doFillPostingdetails(String finReference) {
		logger.debug("Entering");
		
		JdbcSearchObject<ReturnDataSet> jdbcSearchObject = new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		jdbcSearchObject.addTabelName("Postings");
		jdbcSearchObject.addFilterEqual("finReference",finReference);
		jdbcSearchObject.addFilterIn("FinEvent", new String[]{"ADDDBSP","ADDDBSF","ADDDBSN"});
		
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<ReturnDataSet> postingList = pagedListService.getBySearchObject(jdbcSearchObject);
		
		if(postingList != null && !postingList.isEmpty()){
			Listitem item;
			for (ReturnDataSet returnDataSet : postingList) {
				item = new Listitem();
				Listcell lc = new Listcell();
				if(returnDataSet.getDrOrCr().equals(PennantConstants.CREDIT)){
					lc = new Listcell(Labels.getLabel("common.Debit"));
				}else if(returnDataSet.getDrOrCr().equals(PennantConstants.DEBIT)){
					lc = new Listcell(Labels.getLabel("common.Credit"));
				}
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranDesc());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getRevTranCode());
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getTranCode());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(returnDataSet.getAccount()));
				lc.setParent(item);
				lc = new Listcell(returnDataSet.getAcCcy());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(returnDataSet.getPostAmount(), getFinanceMain().getLovDescFinFormatter()));
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setParent(item);
				this.listBoxCancelFinancePosting.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	public void updateFinanceMain(FinanceMain financeMain){
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");

	}

	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}
	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

}
