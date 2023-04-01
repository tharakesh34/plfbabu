/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 *********************************************************************************************
 * FILE HEADER *
 *********************************************************************************************
 *
 * FileName : FinanceWriteoffDialogCtrl.java
 * 
 * Author : PENNANT TECHONOLOGIES
 * 
 * Creation Date : 03-06-2011
 * 
 * Modified Date : 03-06-2011
 * 
 * Description :
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.payments;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Payments/FinanceWriteoffDialog.zul
 */
public class FinanceWriteoffDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = LogManager.getLogger(FinanceWriteoffDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinWriteoffDialog;
	protected Borderlayout borderlayoutFinWriteoffDialog;

	// Summary Details
	protected Textbox finReference;
	protected Textbox finType;
	protected Textbox finBranch;
	protected Textbox finCcy;
	protected Textbox custID;
	protected Datebox finStartDate;
	protected Datebox maturityDate;
	protected Datebox writeoffDate;

	protected Decimalbox label_FinWriteoffDialog_WOPriAmt;
	protected Decimalbox label_FinWriteoffDialog_WOPftAmt;
	protected Decimalbox label_FinWriteoffDialog_WOSchdFeeAmt;
	protected Decimalbox label_FinWriteoffDialog_ODPriAmt;
	protected Decimalbox label_FinWriteoffDialog_ODPftAmt;
	protected Decimalbox label_FinWriteoffDialog_UnPaidPriAmt;
	protected Decimalbox label_FinWriteoffDialog_UnPaidPftAmt;
	protected Decimalbox label_FinWriteoffDialog_UnPaidSchFeeAmt;
	protected Decimalbox label_FinWriteoffDialog_OutStandPrincipal;
	protected Decimalbox label_FinWriteoffDialog_OutStandProfit;
	protected Decimalbox label_FinWriteoffDialog_OutStandSchFee;
	protected Decimalbox label_FinWriteoffDialog_ProvisionAmt;
	protected Decimalbox label_FinWriteoffDialog_PenaltyAmt;

	protected Decimalbox writeoffPriAmt;
	protected Decimalbox writeoffPftAmt;
	protected Decimalbox writeoffSchFee;
	protected Decimalbox adjAmount;
	protected Textbox remarks;
	protected Row row_WrittenOff;

	protected transient Date oldVar_writeoffDate;
	protected transient BigDecimal oldVar_writeoffPriAmt;
	protected transient BigDecimal oldVar_writeoffPftAmt;
	protected transient BigDecimal oldVar_writeoffSchFee;
	protected transient BigDecimal oldVar_adjAmount;
	protected transient String oldVar_remarks;

	protected Listheader listheader_ScheduleDetailDialog_Date;
	protected Listheader listheader_ScheduleDetailDialog_ScheduleEvent;
	protected Listheader listheader_ScheduleDetailDialog_CalProfit;
	protected Listheader listheader_ScheduleDetailDialog_SchFee;
	protected Listheader listheader_ScheduleDetailDialog_SchProfit;
	protected Listheader listheader_ScheduleDetailDialog_Principal;
	protected Listheader listheader_ScheduleDetailDialog_Total;
	protected Listheader listheader_ScheduleDetailDialog_ScheduleEndBal;
	protected Listheader listHeader_orgPrincipalDue;

	protected Button btnWriteoffCal;
	protected Button btnWriteoffReCal;
	protected Button btnWriteoffPay;
	protected Button btnNotes;

	protected Listbox listBoxSchedule;
	protected Tab finWriteoffTab;
	protected Tab finScheduleTab;

	private transient FinanceSelectCtrl financeSelectCtrl = null;
	private FinanceMain financeMain;
	private FinanceWriteoffHeader financeWriteoffHeader;
	private FinanceWriteoffHeader effectFinScheduleData;
	private FinanceWriteoff financeWriteoff;
	private FinanceWriteoffService financeWriteoffService;
	private FinanceReferenceDetailService financeReferenceDetailService;
	private AccrualService accrualService;

	private int format = 0;

	private NotificationService notificationService;

	private boolean WRITEOFF_FULLAMOUNT = true;
	private String finEvent = "";

	/**
	 * default constructor.<br>
	 */
	public FinanceWriteoffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinWriteoffDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinWriteoffDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinWriteoffDialog);

		try {

			if (arguments.containsKey("financeWriteoffHeader")) {
				setFinanceWriteoffHeader((FinanceWriteoffHeader) arguments.get("financeWriteoffHeader"));
				setFinanceDetail(getFinanceWriteoffHeader().getFinanceDetail());
				financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				financeWriteoff = getFinanceWriteoffHeader().getFinanceWriteoff();

				Cloner cloner = new Cloner();
				FinanceMain befImage = cloner.deepClone(financeMain);
				getFinanceWriteoffHeader().getFinanceDetail().getFinScheduleData().getFinanceMain()
						.setBefImage(befImage);

			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}

			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "FinWriteoffDialog", menuItemRightName);
			} else {
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			doEdit();
			if (!financeMain.isNewRecord()) {
				this.btnNotes.setVisible(true);
				this.btnWriteoffCal.setDisabled(true);
			} else {
				this.btnWriteoffReCal.setDisabled(true);
				this.btnWriteoffPay.setDisabled(true);
			}

			doWriteBeanToComponents();
			listBoxSchedule.setHeight(this.borderLayoutHeight - 100 - 20 + "px");

			doStoreServiceIds(getFinanceDetail().getFinScheduleData().getFinanceMain());
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinWriteoffDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("FinWriteoffDialog", getRole(), menuItemRightName);

		this.btnWriteoffCal.setVisible(getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffCal"));
		this.btnWriteoffReCal.setVisible(getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setVisible(getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		this.btnWriteoffCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffCal"));
		this.btnWriteoffReCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		this.finStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.writeoffDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.writeoffPriAmt.setMaxlength(18);
		this.writeoffPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffPriAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.writeoffPriAmt.setScale(format);

		this.writeoffPftAmt.setMaxlength(18);
		this.writeoffPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffPftAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.writeoffPftAmt.setScale(format);

		this.writeoffSchFee.setMaxlength(18);
		this.writeoffSchFee.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffSchFee.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.writeoffSchFee.setScale(format);

		this.adjAmount.setMaxlength(18);
		this.adjAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.adjAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.adjAmount.setScale(format);

		this.label_FinWriteoffDialog_WOPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOPriAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_WOPriAmt.setScale(format);

		this.label_FinWriteoffDialog_WOPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOPftAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_WOPftAmt.setScale(format);

		this.label_FinWriteoffDialog_WOSchdFeeAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setScale(format);

		this.label_FinWriteoffDialog_ODPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ODPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_ODPriAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_ODPriAmt.setScale(format);

		this.label_FinWriteoffDialog_ODPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ODPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_ODPftAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_ODPftAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidPriAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_UnPaidPriAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidPftAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_UnPaidPftAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setScale(format);

		this.label_FinWriteoffDialog_OutStandPrincipal.setMaxlength(18);
		this.label_FinWriteoffDialog_OutStandPrincipal.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_OutStandPrincipal.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_OutStandPrincipal.setScale(format);

		this.label_FinWriteoffDialog_OutStandProfit.setMaxlength(18);
		this.label_FinWriteoffDialog_OutStandProfit.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_OutStandProfit.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_OutStandProfit.setScale(format);

		this.label_FinWriteoffDialog_ProvisionAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ProvisionAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_ProvisionAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_ProvisionAmt.setScale(format);

		this.label_FinWriteoffDialog_PenaltyAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_PenaltyAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_PenaltyAmt.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.label_FinWriteoffDialog_PenaltyAmt.setScale(format);

		this.remarks.setMaxlength(200);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		this.writeoffDate.setDisabled(true);// isReadOnly("FinWriteoffDialog_writeoffDate")
		this.writeoffPriAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPriAmt"));
		this.writeoffPftAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPftAmt"));
		this.adjAmount.setDisabled(isReadOnly("FinWriteoffDialog_adjAmount"));
		this.remarks.setReadonly(isReadOnly("FinWriteoffDialog_remarks"));

		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgRpy
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void doWriteBeanToComponents()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		this.listheader_ScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Date"));
		this.listheader_ScheduleDetailDialog_ScheduleEvent
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"));
		this.listheader_ScheduleDetailDialog_CalProfit
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_CalProfit"));
		this.listheader_ScheduleDetailDialog_SchFee.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchFee"));
		this.listheader_ScheduleDetailDialog_SchProfit
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchProfit"));
		this.listheader_ScheduleDetailDialog_Principal
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Principal"));
		this.listheader_ScheduleDetailDialog_Total.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Total"));
		this.listheader_ScheduleDetailDialog_ScheduleEndBal
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEndBal"));
		listHeader_orgPrincipalDue.setLabel(Labels.getLabel("listheader_OrgPrincipalDue"));

		Customer customer = null;
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			customer = getFinanceDetail().getCustomerDetails().getCustomer();
		}

		this.finReference.setValue(financeMain.getFinReference());
		this.finType.setValue(financeMain.getLovDescFinTypeName());
		this.finBranch.setValue(financeMain.getFinBranch() + "-" + financeMain.getLovDescFinBranchName());
		this.finCcy.setValue(financeMain.getFinCcy());
		if (customer != null) {
			this.custID.setValue(customer.getCustCIF());
		}
		this.finStartDate.setValue(financeMain.getFinStartDate());
		this.maturityDate.setValue(financeMain.getMaturityDate());

		this.label_FinWriteoffDialog_WOPriAmt.setValue(CurrencyUtil.parse(financeWriteoff.getWrittenoffPri(), format));
		this.label_FinWriteoffDialog_WOPftAmt.setValue(CurrencyUtil.parse(financeWriteoff.getWrittenoffPft(), format));

		this.label_FinWriteoffDialog_WOSchdFeeAmt
				.setValue(CurrencyUtil.parse(financeWriteoff.getWrittenoffSchFee(), format));

		this.label_FinWriteoffDialog_ODPriAmt.setValue(CurrencyUtil.parse(financeWriteoff.getCurODPri(), format));
		this.label_FinWriteoffDialog_ODPftAmt.setValue(CurrencyUtil.parse(financeWriteoff.getCurODPft(), format));
		this.label_FinWriteoffDialog_UnPaidPriAmt
				.setValue(CurrencyUtil.parse(financeWriteoff.getUnPaidSchdPri(), format));
		this.label_FinWriteoffDialog_UnPaidPftAmt
				.setValue(CurrencyUtil.parse(financeWriteoff.getUnPaidSchdPft(), format));
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt
				.setValue(CurrencyUtil.parse(financeWriteoff.getUnpaidSchFee(), format));
		this.label_FinWriteoffDialog_OutStandPrincipal
				.setValue(CurrencyUtil.parse(financeWriteoff.getUnPaidSchdPri(), format));
		this.label_FinWriteoffDialog_OutStandProfit
				.setValue(CurrencyUtil.parse(financeWriteoff.getUnPaidSchdPft(), format));
		this.label_FinWriteoffDialog_PenaltyAmt
				.setValue(CurrencyUtil.parse(financeWriteoff.getPenaltyAmount(), format));
		this.label_FinWriteoffDialog_ProvisionAmt
				.setValue(CurrencyUtil.parse(financeWriteoff.getProvisionedAmount(), format));

		this.writeoffPriAmt.setValue(CurrencyUtil.parse(financeWriteoff.getWriteoffPrincipal(), format));
		this.writeoffPftAmt.setValue(CurrencyUtil.parse(financeWriteoff.getWriteoffProfit(), format));
		this.writeoffSchFee.setValue(CurrencyUtil.parse(financeWriteoff.getWriteoffSchFee(), format));
		this.adjAmount.setValue(CurrencyUtil.parse(financeWriteoff.getAdjAmount(), format));
		this.remarks.setValue(financeWriteoff.getRemarks());
		this.writeoffDate.setValue(financeWriteoff.getWriteoffDate());

		if (financeWriteoff.getWriteoffDate() == null) {
			Date curBDay = SysParamUtil.getAppDate();
			this.writeoffDate.setValue(curBDay);
		}

		if (!financeMain.isNewRecord()) {
			this.finScheduleTab.setVisible(true);

			Cloner cloner = new Cloner();
			effectFinScheduleData = cloner.deepClone(financeWriteoffHeader);
			doFillScheduleList(effectFinScheduleData);

		}

		// For Full Writeoff amount based on Constant defined
		if (WRITEOFF_FULLAMOUNT) {
			this.btnWriteoffReCal.setVisible(false);
			this.btnWriteoffCal.setVisible(false);
			this.row_WrittenOff.setVisible(false);

			if (financeWriteoff.getWriteoffPrincipal().compareTo(BigDecimal.ZERO) == 0
					&& financeWriteoff.getWriteoffProfit().compareTo(BigDecimal.ZERO) == 0
					&& financeWriteoff.getWriteoffSchFee().compareTo(BigDecimal.ZERO) == 0) {

				this.writeoffPriAmt.setValue(CurrencyUtil.parse(financeWriteoff.getUnPaidSchdPri(), format));
				this.writeoffPftAmt.setValue(CurrencyUtil.parse(financeWriteoff.getUnPaidSchdPft(), format));
				this.writeoffSchFee.setValue(CurrencyUtil.parse(financeWriteoff.getUnpaidSchFee(), format));

				Events.sendEvent("onClick$btnWriteoffCal", this.window_FinWriteoffDialog, null);
			} else {
				doStoreInitValues();
			}
		}
		this.recordStatus.setValue(financeMain.getRecordStatus());
		getFinanceDetail().setModuleDefiner(moduleDefiner);

		// Customer Details Tab
		appendCustomerDetailTab();

		// Fee Details
		appendFeeDetailTab(true);

		// Agreement Details Tab
		appendAgreementsDetailTab(true);

		// Check List Details Tab
		appendCheckListDetailTab(getFinanceDetail(), false, true);

		// Recommendation Details Tab
		appendRecommendDetailTab(true);

		// Document Details Tab
		appendDocumentDetailTab();

		// Stage Accounting Details
		appendStageAccountingDetailsTab(true);

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			// Accounting Details
			appendAccountingDetailTab(true);
		}

		appendExtendedFieldDetails(getFinanceDetail(), FinServiceEvent.WRITEOFF);

		logger.debug("Leaving");
	}

	public void onSelectCheckListDetailsTab(ForwardEvent event)
			throws ParseException, InterruptedException, IllegalAccessException, InvocationTargetException {

		this.doWriteComponentsToBean();

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}

	}

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_writeoffDate = this.writeoffDate.getValue();
		this.oldVar_writeoffPriAmt = this.writeoffPriAmt.getValue();
		this.oldVar_writeoffPftAmt = this.writeoffPftAmt.getValue();
		this.oldVar_writeoffSchFee = this.writeoffSchFee.getValue();
		this.oldVar_adjAmount = this.adjAmount.getValue();
		this.oldVar_remarks = this.remarks.getValue();
		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		this.writeoffDate.setErrorMessage("");
		this.writeoffPriAmt.setErrorMessage("");
		this.writeoffPftAmt.setErrorMessage("");
		this.writeoffSchFee.setErrorMessage("");
		this.adjAmount.setErrorMessage("");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnWriteoffPay.isVisible());
		if (extendedFieldCtrl != null && financeWriteoffHeader.getFinanceDetail().getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Method for Calculate Write-off Effect Schedule
	 * 
	 * @param event
	 */
	public void onClick$btnWriteoffCal(Event event) {
		logger.debug("Entering" + event.toString());

		if (!isValidated()) {
			return;
		}

		/*
		 * this.writeoffPriAmt.setDisabled(true); this.writeoffPftAmt.setDisabled(true);
		 * this.adjAmount.setDisabled(true);
		 */

		this.btnWriteoffCal.setDisabled(true);
		this.btnWriteoffReCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		this.finScheduleTab.setVisible(true);
		this.listBoxSchedule.getItems().clear();

		// Reset only Schedule Details Data
		Cloner cloner = new Cloner();
		FinanceWriteoffHeader schdData = cloner.deepClone(financeWriteoffHeader);
		schdData.getFinanceDetail().getFinScheduleData()
				.setFinanceScheduleDetails(financeWriteoffService.getFinScheduleDetails(financeMain.getFinID()));

		calScheduleWriteOffDetails(schdData);
		doStoreInitValues();

		logger.debug("Leaving" + event.toString());
	}

	private List<FinanceScheduleDetail> calScheduleWriteOffDetails(FinanceWriteoffHeader financeWriteoffHeader) {
		logger.debug("Entering");

		// Copy Total Finance Schedule Data for Calculation without Effecting the Original Schedule Data
		Cloner cloner = new Cloner();
		effectFinScheduleData = cloner.deepClone(financeWriteoffHeader);

		BigDecimal woPriAmt = CurrencyUtil.unFormat(this.writeoffPriAmt.getValue(), format);
		BigDecimal woPftAmt = CurrencyUtil.unFormat(this.writeoffPftAmt.getValue(), format);
		BigDecimal woSchFee = CurrencyUtil.unFormat(this.writeoffSchFee.getValue(), format);

		List<FinanceScheduleDetail> effectedFinSchDetails = effectFinScheduleData.getFinanceDetail()
				.getFinScheduleData().getFinanceScheduleDetails();

		if (effectedFinSchDetails != null && effectedFinSchDetails.size() > 0) {
			for (int i = 0; i < effectedFinSchDetails.size(); i++) {

				FinanceScheduleDetail curSchdl = effectedFinSchDetails.get(i);

				// Reset Write-off Principal Amount
				if (woPriAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schPriBal = curSchdl.getPrincipalSchd().subtract(curSchdl.getSchdPriPaid())
							.subtract(curSchdl.getWriteoffPrincipal());
					if (schPriBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woPriAmt.compareTo(schPriBal) >= 0) {
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(schPriBal));
							woPriAmt = woPriAmt.subtract(schPriBal);
						} else {
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(woPriAmt));
							woPriAmt = BigDecimal.ZERO;
						}
					}
				}
				// Reset Write-off Profit Amount
				if (woPftAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schPftBal = curSchdl.getProfitSchd().subtract(curSchdl.getSchdPftPaid())
							.subtract(curSchdl.getWriteoffProfit());
					if (schPftBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woPftAmt.compareTo(schPftBal) >= 0) {
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(schPftBal));
							woPftAmt = woPftAmt.subtract(schPftBal);
						} else {
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(woPftAmt));
							woPftAmt = BigDecimal.ZERO;
						}
					}
				}

				// Reset Write-off Schedule Fee
				if (woSchFee.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schFee = curSchdl.getFeeSchd()
							.subtract(curSchdl.getSchdFeePaid().subtract(curSchdl.getWriteoffSchFee()));
					if (schFee.compareTo(BigDecimal.ZERO) > 0) {
						if (woSchFee.compareTo(schFee) >= 0) {
							curSchdl.setWriteoffSchFee(curSchdl.getWriteoffSchFee().add(schFee));
							woSchFee = woSchFee.subtract(schFee);
						} else {
							curSchdl.setWriteoffSchFee(curSchdl.getWriteoffSchFee().add(woSchFee));
							woSchFee = BigDecimal.ZERO;
						}
					}
				}

			}
		}

		doFillScheduleList(effectFinScheduleData);

		logger.debug("Leaving");
		return effectedFinSchDetails;
	}

	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * 
	 * @param aFinScheduleData (FinScheduleData)
	 * 
	 */
	public void doFillScheduleList(FinanceWriteoffHeader writeoffHeader) {
		logger.debug("Entering");

		FinanceScheduleDetail prvSchDetail = null;

		// Reset To Finance Schedule Data Object For rendering purpose
		FinScheduleData aFinScheduleData = new FinScheduleData();
		aFinScheduleData.setFinanceMain(writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain());
		aFinScheduleData.setFinanceScheduleDetails(
				sortSchdDetails(writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()));
		aFinScheduleData.setDisbursementDetails(
				writeoffHeader.getFinanceDetail().getFinScheduleData().getDisbursementDetails());
		aFinScheduleData.setFinanceType(writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceType());

		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if (aFinScheduleData != null && sdSize > 0) {

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
			if (aFinScheduleData.getRepayDetails() != null && aFinScheduleData.getRepayDetails().size() > 0) {
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : aFinScheduleData.getRepayDetails()) {
					if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					} else {
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
			if (aFinScheduleData.getPenaltyDetails() != null && aFinScheduleData.getPenaltyDetails().size() > 0) {
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				for (OverdueChargeRecovery penaltyDetail : aFinScheduleData.getPenaltyDetails()) {
					if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
								.get(penaltyDetail.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					} else {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}

			// Clear all the listitems in listbox
			int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());
			aFinScheduleData.setFinanceScheduleDetails(sortSchdDetails(aFinScheduleData.getFinanceScheduleDetails()));
			for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
				boolean showRate = false;
				FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
					if (aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
						showRate = true;
					}
				}

				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinScheduleData);
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("penaltyDetailsMap", penaltyDetailsMap);
				map.put("formatter", formatter);

				map.put("window", this.window_FinWriteoffDialog);
				finRender.render(map, prvSchDetail, false, true, true, aFinScheduleData.getFinFeeDetailList(), showRate,
						false);

				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, true, true, aFinScheduleData.getFinFeeDetailList(),
							showRate, false);
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Calculate Write-off Effect Schedule
	 * 
	 * @param event
	 */
	public void onClick$btnWriteoffReCal(Event event) {
		logger.debug("Entering" + event.toString());

		this.writeoffPriAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPriAmt"));
		this.writeoffPftAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPftAmt"));
		this.writeoffSchFee.setDisabled(isReadOnly("FinWriteoffDialog_writeoffSchFee"));
		this.adjAmount.setDisabled(isReadOnly("FinWriteoffDialog_adjAmount"));

		this.btnWriteoffCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffCal"));
		this.btnWriteoffReCal.setDisabled(true);
		this.btnWriteoffPay.setDisabled(true);

		this.listBoxSchedule.getItems().clear();
		this.finScheduleTab.setVisible(false);
		this.finWriteoffTab.setSelected(true);
		logger.debug("Leaving" + event.toString());
	}

	private boolean isValidated() {
		logger.debug("Entering");

		if ((this.writeoffPriAmt.getValue() == null || this.writeoffPriAmt.getValue().compareTo(BigDecimal.ZERO) <= 0)
				&& (this.writeoffPftAmt.getValue() == null
						|| this.writeoffPftAmt.getValue().compareTo(BigDecimal.ZERO) <= 0)
				&& (this.writeoffSchFee.getValue() == null
						|| this.writeoffSchFee.getValue().compareTo(BigDecimal.ZERO) <= 0)) {

			MessageUtil.showError("Write-off Amount must be Entered.");
			return false;
		}

		BigDecimal woPriAmt = CurrencyUtil.unFormat(this.writeoffPriAmt.getValue(), format);
		BigDecimal woPftAmt = CurrencyUtil.unFormat(this.writeoffPftAmt.getValue(), format);
		BigDecimal woSchFee = CurrencyUtil.unFormat(this.writeoffSchFee.getValue(), format);

		if (woPriAmt.compareTo(financeWriteoff.getUnPaidSchdPri()) > 0
				|| woPftAmt.compareTo(financeWriteoff.getUnPaidSchdPft()) > 0
				|| woSchFee.compareTo(financeWriteoff.getUnpaidSchFee()) > 0) {
			MessageUtil.showError("Entered Write-off Amount Should be less than Unpaid Balances.");
			return false;
		}

		logger.debug("Leaving");
		return true;
	}

	private FinanceWriteoff doWriteComponentsToBean() {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		FinanceWriteoff writeoff = getFinanceWriteoff();

		writeoff.setFinID(financeMain.getFinID());
		writeoff.setFinReference(this.finReference.getValue());
		writeoff.setWrittenoffPri(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_WOPriAmt.getValue(), format));
		writeoff.setWrittenoffPft(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_WOPftAmt.getValue(), format));
		writeoff.setWrittenoffSchFee(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_WOSchdFeeAmt.getValue(), format));
		writeoff.setCurODPri(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ODPriAmt.getValue(), format));
		writeoff.setCurODPft(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ODPftAmt.getValue(), format));
		writeoff.setUnPaidSchdPri(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_UnPaidPriAmt.getValue(), format));
		writeoff.setUnPaidSchdPft(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_UnPaidPftAmt.getValue(), format));
		writeoff.setUnpaidSchFee(PennantApplicationUtil
				.unFormateAmount(this.label_FinWriteoffDialog_UnPaidSchFeeAmt.getValue(), format));
		writeoff.setPenaltyAmount(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_PenaltyAmt.getValue(), format));
		writeoff.setProvisionedAmount(
				PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ProvisionAmt.getValue(), format));

		writeoff.setWriteoffPrincipal(PennantApplicationUtil.unFormateAmount(this.writeoffPriAmt.getValue(), format));
		writeoff.setWriteoffProfit(PennantApplicationUtil.unFormateAmount(this.writeoffPftAmt.getValue(), format));
		writeoff.setWriteoffSchFee(PennantApplicationUtil.unFormateAmount(this.writeoffSchFee.getValue(), format));
		writeoff.setAdjAmount(PennantApplicationUtil.unFormateAmount(this.adjAmount.getValue(), format));
		writeoff.setRemarks(this.remarks.getValue());

		List<WrongValueException> wve = new ArrayList<>();
		try {
			if (this.writeoffDate.getValue() == null) {
				this.writeoffDate.setValue(SysParamUtil.getAppDate());
			} else if (!this.writeoffDate.isDisabled()) {
				this.writeoffDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinWriteoffDialog_WriteoffDate.value"), false,
								SysParamUtil.getValueAsDate("APP_DFT_START_DATE"), SysParamUtil.getAppDate(), true));
			}

			writeoff.setWriteoffDate(this.writeoffDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, finWriteoffTab);

		logger.debug("Leaving");
		return writeoff;
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(List<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			this.writeoffDate.setConstraint("");

			this.writeoffDate.setErrorMessage("");

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for event of Changing Repayment Amount
	 * 
	 * @param event
	 */
	public void onClick$btnWriteoffPay(Event event) {
		logger.debug("Entering" + event.toString());

		boolean isDataChanged = false;
		if (this.oldVar_writeoffPriAmt.compareTo(this.writeoffPriAmt.getValue()) != 0) {
			isDataChanged = true;
		}
		if (this.oldVar_writeoffPftAmt.compareTo(this.writeoffPftAmt.getValue()) != 0) {
			isDataChanged = true;
		}

		if (isDataChanged) {
			MessageUtil.showError("Amounts Changed. Must need to Recalculate Schedule.");
			return;
		}

		Cloner cloner = new Cloner();
		FinanceWriteoffHeader aFinanceWriteoffHeader = cloner.deepClone(getFinanceWriteoffHeader());
		FinanceDetail aFinanceDetail = aFinanceWriteoffHeader.getFinanceDetail();

		aFinanceDetail.getFinScheduleData().setFinanceScheduleDetails(
				effectFinScheduleData.getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}

		aFinanceDetail.setAccountingEventCode(eventCode);

		// Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);

		// Prepare Validation & Calling
		if (!isValidated()) {
			return;
		}

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		// Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum()
					.compareTo(getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aFinanceDetail.setStageAccountingList(null);
		}

		// Finance Accounting Details
		if (!recSave && getAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return;
			}
			if (getAccountingDetailDialogCtrl().getDisbCrSum()
					.compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		}

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		// loading Write Off Object Entered Data into Bean
		aFinanceWriteoffHeader.setFinanceWriteoff(doWriteComponentsToBean());

		// Extended Fields
		if (aFinanceDetail.getExtendedFieldHeader() != null) {
			aFinanceDetail.setExtendedFieldRender(extendedFieldCtrl.save(!recSave));
		}

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aFinanceMain.setNewRecord(true);
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		// save it to database
		try {
			aFinanceMain.setRcdMaintainSts(FinServiceEvent.WRITEOFF);
			aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceWriteoffHeader, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				FinanceDetail financeDetail = aFinanceWriteoffHeader.getFinanceDetail();
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
					notification.setModule("WRITE_OFF");
					notification.setSubModule(FinServiceEvent.WRITEOFF);
					notification.setKeyReference(financeMain.getFinReference());
					notification.setStage(financeMain.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());
					notificationService.sendNotifications(notification, aFinanceDetail, financeMain.getFinType(),
							financeDetail.getDocumentDetailsList());
				}

				// User Notifications Message/Alert
				FinanceMain fm = aFinanceMain;
				if (fm.getNextUserId() != null) {
					publishNotification(Notify.USER, fm.getFinReference(), fm);
				} else {
					if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm);
					} else {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm, finDivision,
								aFinanceMain.getFinBranch());
					}
				}

				if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

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
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
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
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(FinanceWriteoffHeader aFinanceWriteoffHeader, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().setFinanceMain(afinanceMain);

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceWriteoffHeader, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(FinanceConstants.method_scheduleChange)) {
					List<String> finTypeList = getFinanceDetailService().getScheduleEffectModuleList(true);
					boolean isScheduleModify = false;
					for (String fintypeList : finTypeList) {
						if (StringUtils.equals(moduleDefiner, fintypeList)) {
							isScheduleModify = true;
							break;
						}
					}
					if (isScheduleModify) {
						afinanceMain.setScheduleChange(true);
					} else {
						afinanceMain.setScheduleChange(false);
					}
				} else {
					FinanceWriteoffHeader tFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
							.getModelData();
					setNextTaskDetails(taskId,
							tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceWriteoffHeader);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceWriteoffHeader tFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
						.getModelData();
				serviceTasks = getServiceTasks(taskId,
						tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain(), finishedTasks);

			}

			FinanceWriteoffHeader tFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
					.getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId,
					tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId,
							tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceWriteoffHeader);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aFinanceWriteoffHeader, tranType);
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

		FinanceWriteoffHeader aFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
				.getModelData();
		FinanceDetail aFinanceDetail = aFinanceWriteoffHeader.getFinanceDetail();
		FinanceMain afinanceMain = aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();

		// Extended Field details
		if (aFinanceDetail.getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aFinanceDetail.getExtendedFieldRender();
			details.setReference(afinanceMain.getFinReference());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(afinanceMain.getRecordStatus());
			details.setRecordType(afinanceMain.getRecordType());
			details.setVersion(afinanceMain.getVersion());
			details.setWorkflowId(afinanceMain.getWorkflowId());
			details.setTaskId(afinanceMain.getTaskId());
			details.setNextTaskId(afinanceMain.getNextTaskId());
			details.setRoleCode(afinanceMain.getRoleCode());
			details.setNextRoleCode(afinanceMain.getNextRoleCode());
			details.setNewRecord(afinanceMain.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(afinanceMain.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				auditHeader = getFinanceWriteoffService().saveOrUpdate(auditHeader);

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getFinanceWriteoffService().doApprove(auditHeader);

					if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getFinanceWriteoffService().doReject(auditHeader);
					if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinWriteoffDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_FinWriteoffDialog, auditHeader);
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
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for return Document detail list object
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		logger.debug("Entering");

		if (getFinanceWriteoffHeader() != null) {
			if (getFinanceWriteoffHeader().getFinanceDetail() != null) {
				return getFinanceWriteoffHeader().getFinanceDetail().getDocumentDetailsList();
			}
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, getFinanceMain().getFinType());
		arrayList.add(1, getFinanceMain().getFinCcy());
		arrayList.add(2, getFinanceMain().getScheduleMethod());
		arrayList.add(3, getFinanceMain().getFinReference());
		arrayList.add(4, getFinanceMain().getProfitDaysBasis());
		arrayList.add(5, getFinanceMain().getGrcPeriodEndDate());
		arrayList.add(6, getFinanceMain().isAllowGrcPeriod());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (fianncetype != null && StringUtils.isNotEmpty(fianncetype.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		arrayList.add(9, getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
		arrayList.add(10, false);
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinanceWriteoffHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, header);
		return new AuditHeader(header.getFinReference(), null, null, null, auditDetail,
				header.getFinanceDetail().getFinScheduleData().getFinanceMain().getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		this.btnNotes.setSclass("");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeMain.getFinReference());
	}

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug("Entering");

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		getFinanceDetail().setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);
		return getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		FinanceProfitDetail profitDetail = null;
		if (StringUtils.isEmpty(moduleDefiner)) {
			profitDetail = new FinanceProfitDetail();
		} else {
			profitDetail = getFinanceDetailService()
					.getFinProfitDetailsById(getFinanceDetail().getFinScheduleData().getFinID());
		}

		AEEvent aeEvent = prepareAccountingData(onLoadProcess, profitDetail);
		Map<String, Object> dataMap = aeEvent.getDataMap();

		prepareFeeRulesMap(aeEvent.getAeAmountCodes(), dataMap);
		aeEvent.getAeAmountCodes().setTotalWriteoff(financeWriteoff.getWriteoffPrincipal()
				.add(financeWriteoff.getWriteoffProfit().add(financeWriteoff.getWrittenoffSchFee())));
		aeEvent.getAeAmountCodes().getDeclaredFieldValues(dataMap);
		financeWriteoff.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		getEngineExecution().getAccEngineExecResults(aeEvent);
		accountingSetEntries.addAll(aeEvent.getReturnDataSet());

		// Disb Instruction Posting
		if (eventCode.equals(AccountingEvent.ADDDBS) || eventCode.equals(AccountingEvent.ADDDBSF)
				|| eventCode.equals(AccountingEvent.ADDDBSN) || eventCode.equals(AccountingEvent.ADDDBSP)) {
			prepareDisbInstructionPosting(accountingSetEntries, aeEvent);
		}

		getFinanceDetail().setReturnDataSetList(accountingSetEntries);

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(accountingSetEntries);
		}

		logger.debug("Leaving");
	}

	private AEEvent prepareAccountingData(boolean onLoadProcess, FinanceProfitDetail profitDetail) {

		Date curBDay = SysParamUtil.getAppDate();

		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		FinanceWriteoff financeWriteoff = doWriteComponentsToBean();

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
		}

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		// For New Records Profit Details will be set inside the AEAmounts
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {// FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, curBDay, curBDay);
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode,
					FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(finMain, finSchdDetails, newProfitDetail, curBDay);
		if (!FinanceConstants.BPI_NO.equals(finMain.getBpiTreatment())) {
			amountCodes.setBpi(finMain.getBpiAmount());
		}
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(FinServiceEvent.ORG);
		amountCodes.setDisburse(finMain.getFinCurrAssetValue());

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}

		return aeEvent;
	}

	private void prepareDisbInstructionPosting(List<ReturnDataSet> accountingSetEntries, AEEvent aeEvent) {
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setAccountingEvent(AccountingEvent.DISBINS);
		List<FinAdvancePayments> advPayList = getFinanceDetail().getAdvancePaymentsList();

		aeEvent.getAcSetIDList().clear();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(),
					AccountingEvent.DISBINS, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(),
					AccountingEvent.DISBINS, FinanceConstants.MODULEID_FINTYPE));
		}

		// loop through the disbursements.
		if (advPayList != null && !advPayList.isEmpty()) {

			for (int i = 0; i < advPayList.size(); i++) {
				FinAdvancePayments advPayment = advPayList.get(i);

				aeEvent.setModuleDefiner(FinServiceEvent.ORG);
				amountCodes.setDisbInstAmt(advPayment.getAmtToBeReleased());
				amountCodes.setPartnerBankAc(advPayment.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(advPayment.getPartnerBankAcType());

				Map<String, Object> dataMap = aeEvent.getDataMap();
				dataMap = amountCodes.getDeclaredFieldValues();
				aeEvent.setDataMap(dataMap);
				if (advPayment.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(advPayment.getRecordType())) {
					aeEvent.setNewRecord(true);
				}

				// Call Map Build Method
				getEngineExecution().getAccEngineExecResults(aeEvent);
				List<ReturnDataSet> returnDataSet = aeEvent.getReturnDataSet();
				accountingSetEntries.addAll(returnDataSet);
			}
		}

	}

	private void prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, Object> dataMap) {
		logger.debug("Entering");

		List<FinFeeDetail> finFeeDetailList = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (finFeeDetailList != null) {
			FeeRule feeRule;

			BigDecimal deductFeeDisb = BigDecimal.ZERO;
			BigDecimal addFeeToFinance = BigDecimal.ZERO;
			BigDecimal paidFee = BigDecimal.ZERO;
			BigDecimal feeWaived = BigDecimal.ZERO;

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				feeRule = new FeeRule();

				feeRule.setFeeCode(finFeeDetail.getFeeTypeCode());
				feeRule.setFeeAmount(finFeeDetail.getActualAmount());
				feeRule.setWaiverAmount(finFeeDetail.getWaivedAmount());
				feeRule.setPaidAmount(finFeeDetail.getPaidAmount());
				feeRule.setFeeToFinance(finFeeDetail.getFeeScheduleMethod());
				feeRule.setFeeMethod(finFeeDetail.getFeeScheduleMethod());

				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmountOriginal());

				if (feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
						|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SCH", finFeeDetail.getRemainingFee());
				} else {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SCH", 0);
				}

				if (StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_AF", finFeeDetail.getRemainingFee());
				} else {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_AF", 0);
				}

				if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					deductFeeDisb = deductFeeDisb.add(finFeeDetail.getRemainingFee());
				} else if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					addFeeToFinance = addFeeToFinance.add(finFeeDetail.getRemainingFee());
				}

				paidFee = paidFee.add(finFeeDetail.getPaidAmount());
				feeWaived = feeWaived.add(finFeeDetail.getWaivedAmount());
			}

			amountCodes.setDeductFeeDisb(deductFeeDisb);
			amountCodes.setAddFeeToFinance(addFeeToFinance);
			amountCodes.setFeeWaived(feeWaived);
			amountCodes.setPaidFee(paidFee);
		}

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceWriteoffHeader getFinanceWriteoffHeader() {
		return financeWriteoffHeader;
	}

	public void setFinanceWriteoffHeader(FinanceWriteoffHeader financeWriteoffHeader) {
		this.financeWriteoffHeader = financeWriteoffHeader;
	}

	public FinanceWriteoff getFinanceWriteoff() {
		return financeWriteoff;
	}

	public void setFinanceWriteoff(FinanceWriteoff financeWriteoff) {
		this.financeWriteoff = financeWriteoff;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceWriteoffService getFinanceWriteoffService() {
		return financeWriteoffService;
	}

	public void setFinanceWriteoffService(FinanceWriteoffService financeWriteoffService) {
		this.financeWriteoffService = financeWriteoffService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}
}