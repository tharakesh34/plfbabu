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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  FinanceWriteoffDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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
package com.pennant.webui.financemanagement.payments;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
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
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinanceBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the WEB-INF/pages/FinanceManagement/Payments/FinanceWriteoffDialog.zul
 */
public class FinanceWriteoffDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long				serialVersionUID	= 966281186831332116L;
	private static final Logger				logger				= Logger.getLogger(FinanceWriteoffDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_FinWriteoffDialog;
	protected Borderlayout					borderlayoutFinWriteoffDialog;

	//Summary Details
	protected Textbox						finReference;
	protected Textbox						finType;
	protected Textbox						finBranch;
	protected Textbox						finCcy;
	protected Textbox						custID;
	protected Datebox						finStartDate;
	protected Datebox						maturityDate;
	protected Datebox						writeoffDate;

	protected Decimalbox					label_FinWriteoffDialog_WOPriAmt;
	protected Decimalbox					label_FinWriteoffDialog_WOPftAmt;
	protected Decimalbox					label_FinWriteoffDialog_WOInsAmt;
	protected Decimalbox					label_FinWriteoffDialog_WOIncrCostAmt;
	protected Decimalbox					label_FinWriteoffDialog_WOSuplRentAmt;
	protected Decimalbox					label_FinWriteoffDialog_WOSchdFeeAmt;
	protected Decimalbox					label_FinWriteoffDialog_ODPriAmt;
	protected Decimalbox					label_FinWriteoffDialog_ODPftAmt;
	protected Decimalbox					label_FinWriteoffDialog_UnPaidPriAmt;
	protected Decimalbox					label_FinWriteoffDialog_UnPaidPftAmt;
	protected Decimalbox					label_FinWriteoffDialog_UnPaidInsAmt;
	protected Decimalbox					label_FinWriteoffDialog_UnPaidIncrCostAmt;
	protected Decimalbox					label_FinWriteoffDialog_UnPaidSuplRentAmt;
	protected Decimalbox					label_FinWriteoffDialog_UnPaidSchFeeAmt;
	protected Decimalbox					label_FinWriteoffDialog_OutStandPrincipal;
	protected Decimalbox					label_FinWriteoffDialog_OutStandProfit;
	protected Decimalbox					label_FinWriteoffDialog_OutStandIns;
	protected Decimalbox					label_FinWriteoffDialog_OutStandIncrCost;
	protected Decimalbox					label_FinWriteoffDialog_OutStandSuplRent;
	protected Decimalbox					label_FinWriteoffDialog_OutStandSchFee;
	protected Decimalbox					label_FinWriteoffDialog_ProvisionAmt;
	protected Decimalbox					label_FinWriteoffDialog_PenaltyAmt;

	protected Decimalbox					writeoffPriAmt;
	protected Decimalbox					writeoffPftAmt;
	protected Decimalbox					writeoffInsAmt;
	protected Decimalbox					writeoffIncrCost;
	protected Decimalbox					writeoffSuplRent;
	protected Decimalbox					writeoffSchFee;
	protected AccountSelectionBox			writtenoffAcc;
	protected Label							label_FinWriteoffDialog_WrittenoffAcc;
	protected Decimalbox					adjAmount;
	protected Textbox						remarks;
	protected Row							row_WrittenOff;

	protected transient Date				oldVar_writeoffDate;
	protected transient BigDecimal			oldVar_writeoffPriAmt;
	protected transient BigDecimal			oldVar_writeoffPftAmt;
	protected transient BigDecimal			oldVar_writeoffInsAmt;
	protected transient BigDecimal			oldVar_writeoffIncrCost;
	protected transient BigDecimal			oldVar_writeoffSuplRent;
	protected transient BigDecimal			oldVar_writeoffSchFee;
	protected transient BigDecimal			oldVar_adjAmount;
	protected transient String				oldVar_remarks;
	protected transient String				oldVar_writtenoffAcc;

	protected Listheader					listheader_ScheduleDetailDialog_Date;
	protected Listheader					listheader_ScheduleDetailDialog_ScheduleEvent;
	protected Listheader					listheader_ScheduleDetailDialog_CalProfit;
	protected Listheader					listheader_ScheduleDetailDialog_SchFee;
	protected Listheader					listheader_ScheduleDetailDialog_SupplementRent;
	protected Listheader					listheader_ScheduleDetailDialog_IncreasedCost;
	protected Listheader					listheader_ScheduleDetailDialog_SchAdvProfit;
	protected Listheader					listheader_ScheduleDetailDialog_SchProfit;
	protected Listheader					listheader_ScheduleDetailDialog_Principal;
	protected Listheader					listheader_ScheduleDetailDialog_AdvTotal;
	protected Listheader					listheader_ScheduleDetailDialog_Rebate;
	protected Listheader					listheader_ScheduleDetailDialog_Total;
	protected Listheader					listheader_ScheduleDetailDialog_ScheduleEndBal;
	protected Listheader					listHeader_cashFlowEffect;
	protected Listheader					listHeader_vSProfit;
	protected Listheader					listHeader_orgPrincipalDue;

	protected Button						btnWriteoffCal;
	protected Button						btnWriteoffReCal;
	protected Button						btnWriteoffPay;
	protected Button						btnNotes;

	protected Listbox						listBoxSchedule;
	protected Tab							finWriteoffTab;
	protected Tab							finScheduleTab;

	private transient FinanceSelectCtrl		financeSelectCtrl	= null;
	private FinanceMain						financeMain;
	private FinanceWriteoffHeader			financeWriteoffHeader;
	private FinanceWriteoffHeader			effectFinScheduleData;
	private FinanceWriteoff					financeWriteoff;
	private FinanceWriteoffService			financeWriteoffService;
	private FinanceReferenceDetailService	financeReferenceDetailService;
	private AccrualService 									accrualService;


	private int								format				= 0;

	private MailUtil						mailUtil;

	private boolean							WRITEOFF_FULLAMOUNT	= true;

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
	 * @throws Exception
	 */
	public void onCreate$window_FinWriteoffDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinWriteoffDialog);

		try {

			if (arguments.containsKey("financeWriteoffHeader")) {
				setFinanceWriteoffHeader((FinanceWriteoffHeader) arguments.get("financeWriteoffHeader"));
				FinanceMain befImage = new FinanceMain();
				setFinanceDetail(getFinanceWriteoffHeader().getFinanceDetail());
				financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				financeWriteoff = getFinanceWriteoffHeader().getFinanceWriteoff();

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(financeMain);
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
				String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "FinWriteoffDialog", menuItemRightName);
				}
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
		//Empty sent any required attributes
		format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		this.finStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.writeoffDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.writeoffPriAmt.setMaxlength(18);
		this.writeoffPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffPriAmt.setScale(format);

		this.writeoffPftAmt.setMaxlength(18);
		this.writeoffPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffPftAmt.setScale(format);

		this.writeoffInsAmt.setMaxlength(18);
		this.writeoffInsAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffInsAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffInsAmt.setScale(format);

		this.writeoffIncrCost.setMaxlength(18);
		this.writeoffIncrCost.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffIncrCost.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffIncrCost.setScale(format);

		this.writeoffSuplRent.setMaxlength(18);
		this.writeoffSuplRent.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffSuplRent.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffSuplRent.setScale(format);

		this.writeoffSchFee.setMaxlength(18);
		this.writeoffSchFee.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.writeoffSchFee.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffSchFee.setScale(format);

		this.adjAmount.setMaxlength(18);
		this.adjAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.adjAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.adjAmount.setScale(format);

		this.label_FinWriteoffDialog_WOPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOPriAmt.setScale(format);

		this.label_FinWriteoffDialog_WOPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOPftAmt.setScale(format);

		this.label_FinWriteoffDialog_WOInsAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOInsAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOInsAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOInsAmt.setScale(format);

		this.label_FinWriteoffDialog_WOIncrCostAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOIncrCostAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOIncrCostAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOIncrCostAmt.setScale(format);

		this.label_FinWriteoffDialog_WOSuplRentAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOSuplRentAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOSuplRentAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOSuplRentAmt.setScale(format);

		this.label_FinWriteoffDialog_WOSchdFeeAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setScale(format);

		this.label_FinWriteoffDialog_ODPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ODPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_ODPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_ODPriAmt.setScale(format);

		this.label_FinWriteoffDialog_ODPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ODPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_ODPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_ODPftAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidPriAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidPftAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidInsAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidInsAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidInsAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidInsAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidIncrCostAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidIncrCostAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidIncrCostAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidIncrCostAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidSuplRentAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidSuplRentAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidSuplRentAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidSuplRentAmt.setScale(format);

		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setScale(format);

		this.label_FinWriteoffDialog_OutStandPrincipal.setMaxlength(18);
		this.label_FinWriteoffDialog_OutStandPrincipal.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_OutStandPrincipal.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_OutStandPrincipal.setScale(format);

		this.label_FinWriteoffDialog_OutStandProfit.setMaxlength(18);
		this.label_FinWriteoffDialog_OutStandProfit.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_OutStandProfit.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_OutStandProfit.setScale(format);

		this.label_FinWriteoffDialog_ProvisionAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ProvisionAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_ProvisionAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_ProvisionAmt.setScale(format);

		this.label_FinWriteoffDialog_PenaltyAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_PenaltyAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.label_FinWriteoffDialog_PenaltyAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_PenaltyAmt.setScale(format);

		this.writtenoffAcc.setAccountDetails(financeMain.getFinType(), AccountConstants.FinanceAccount_DISB,
				financeMain.getFinCcy());
		this.writtenoffAcc.setFormatter(format);
		this.writtenoffAcc.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
		this.writtenoffAcc.setTextBoxWidth(165);

		this.remarks.setMaxlength(200);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		this.writeoffDate.setDisabled(isReadOnly("FinWriteoffDialog_writeoffDate"));
		this.writeoffPriAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPriAmt"));
		this.writeoffPftAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPftAmt"));
		/*
		 * this.writeoffInsAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffInsAmt"));
		 * this.writeoffPptInsAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPptInsAmt"));
		 * this.writeoffCrInsAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffCrInsAmt"));
		 * this.writeoffIncrCost.setDisabled(isReadOnly("FinWriteoffDialog_writeoffIncrCost"));
		 * this.writeoffSuplRent.setDisabled(isReadOnly("FinWriteoffDialog_writeoffSuplRent"));
		 * this.writeoffSchFee.setDisabled(isReadOnly("FinWriteoffDialog_writeoffSchFee"));
		 */
		this.adjAmount.setDisabled(isReadOnly("FinWriteoffDialog_adjAmount"));
		this.remarks.setReadonly(isReadOnly("FinWriteoffDialog_remarks"));

		if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
			this.writtenoffAcc.setReadonly(isReadOnly("FinWriteoffDialog_writtenoffAcc"));
			if (getWorkFlow() != null && !"Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
				this.writtenoffAcc.setMandatoryStyle(!isReadOnly("FinWriteoffDialog_writtenoffAcc"));
			} else {
				this.writtenoffAcc.setMandatoryStyle(true);
			}
		} else {
			this.label_FinWriteoffDialog_WrittenoffAcc.setVisible(false);
			this.writtenoffAcc.setVisible(false);
			this.writtenoffAcc.setMandatoryStyle(false);
		}

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
	private void doWriteBeanToComponents() throws InterruptedException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		this.listheader_ScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Date"));
		this.listheader_ScheduleDetailDialog_ScheduleEvent.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"));
		this.listheader_ScheduleDetailDialog_CalProfit.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_CalProfit"));
		this.listheader_ScheduleDetailDialog_SchFee.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchFee"));
		this.listheader_ScheduleDetailDialog_SupplementRent.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_SupplementRent"));
		this.listheader_ScheduleDetailDialog_IncreasedCost.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_IncreasedCost"));
		this.listheader_ScheduleDetailDialog_SchProfit.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_SchProfit"));
		this.listheader_ScheduleDetailDialog_SchAdvProfit.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_SchAdvProfit"));
		this.listheader_ScheduleDetailDialog_Principal.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_Principal"));
		this.listheader_ScheduleDetailDialog_AdvTotal.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_AdvTotal"));
		this.listheader_ScheduleDetailDialog_Rebate.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Rebate"));
		this.listheader_ScheduleDetailDialog_Total.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Total"));
		this.listheader_ScheduleDetailDialog_ScheduleEndBal.setLabel(Labels
				.getLabel("listheader_ScheduleDetailDialog_ScheduleEndBal"));
		listHeader_cashFlowEffect.setLabel(Labels.getLabel("listheader_CashFlowEffect"));
		listHeader_vSProfit.setLabel(Labels.getLabel("listheader_VsProfit"));
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

		this.label_FinWriteoffDialog_WOPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWrittenoffPri(),
				format));
		this.label_FinWriteoffDialog_WOPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWrittenoffPft(),
				format));

		this.label_FinWriteoffDialog_WOInsAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWrittenoffIns(),
				format));
		this.label_FinWriteoffDialog_WOIncrCostAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getWrittenoffIncrCost(), format));
		this.label_FinWriteoffDialog_WOSuplRentAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getWrittenoffSuplRent(), format));
		this.label_FinWriteoffDialog_WOSchdFeeAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getWrittenoffSchFee(), format));

		this.label_FinWriteoffDialog_ODPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getCurODPri(),
				format));
		this.label_FinWriteoffDialog_ODPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getCurODPft(),
				format));
		this.label_FinWriteoffDialog_UnPaidPriAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnPaidSchdPri(), format));
		this.label_FinWriteoffDialog_UnPaidPftAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnPaidSchdPft(), format));
		this.label_FinWriteoffDialog_UnPaidInsAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnpaidIns(),
				format));
		this.label_FinWriteoffDialog_UnPaidIncrCostAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnpaidIncrCost(), format));
		this.label_FinWriteoffDialog_UnPaidSuplRentAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnpaidSuplRent(), format));
		this.label_FinWriteoffDialog_UnPaidSchFeeAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnpaidSchFee(), format));
		this.label_FinWriteoffDialog_OutStandPrincipal.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnPaidSchdPri(), format));
		this.label_FinWriteoffDialog_OutStandProfit.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getUnPaidSchdPft(), format));
		this.label_FinWriteoffDialog_PenaltyAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getPenaltyAmount(), format));
		this.label_FinWriteoffDialog_ProvisionAmt.setValue(PennantAppUtil.formateAmount(
				financeWriteoff.getProvisionedAmount(), format));

		this.writeoffPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffPrincipal(), format));
		this.writeoffPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffProfit(), format));
		this.writeoffInsAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffIns(), format));
		this.writeoffIncrCost.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffIncrCost(), format));
		this.writeoffSuplRent.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffSuplRent(), format));
		this.writeoffSchFee.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffSchFee(), format));
		this.adjAmount.setValue(PennantAppUtil.formateAmount(financeWriteoff.getAdjAmount(), format));
		this.remarks.setValue(financeWriteoff.getRemarks());
		this.writeoffDate.setValue(financeWriteoff.getWriteoffDate());
		this.writtenoffAcc.setValue(financeWriteoff.getWrittenoffAcc());

		if (financeWriteoff.getWriteoffDate() == null) {
			Date curBDay = DateUtility.getAppDate();
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
					&& financeWriteoff.getWriteoffIns().compareTo(BigDecimal.ZERO) == 0
					&& financeWriteoff.getWriteoffIncrCost().compareTo(BigDecimal.ZERO) == 0
					&& financeWriteoff.getWriteoffSuplRent().compareTo(BigDecimal.ZERO) == 0
					&& financeWriteoff.getWriteoffSchFee().compareTo(BigDecimal.ZERO) == 0) {

				this.writeoffPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnPaidSchdPri(), format));
				this.writeoffPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnPaidSchdPft(), format));
				this.writeoffInsAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnpaidIns(), format));
				this.writeoffIncrCost
						.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnpaidIncrCost(), format));
				this.writeoffSuplRent
						.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnpaidSuplRent(), format));
				this.writeoffSchFee.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnpaidSchFee(), format));

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

		//Stage Accounting Details 
		appendStageAccountingDetailsTab(true);

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			//Accounting Details
			appendAccountingDetailTab(true);
		}

		logger.debug("Leaving");
	}

	public void onSelectCheckListDetailsTab(ForwardEvent event) throws ParseException, InterruptedException,
			IllegalAccessException, InvocationTargetException {

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
		this.oldVar_writeoffInsAmt = this.writeoffInsAmt.getValue();
		this.oldVar_writeoffIncrCost = this.writeoffIncrCost.getValue();
		this.oldVar_writeoffSuplRent = this.writeoffSuplRent.getValue();
		this.oldVar_writeoffSchFee = this.writeoffSchFee.getValue();
		this.oldVar_adjAmount = this.adjAmount.getValue();
		this.oldVar_remarks = this.remarks.getValue();
		this.oldVar_writtenoffAcc = this.writtenoffAcc.getValue();
		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		this.writeoffDate.setErrorMessage("");
		this.writeoffPriAmt.setErrorMessage("");
		this.writeoffPftAmt.setErrorMessage("");
		this.writeoffInsAmt.setErrorMessage("");
		this.writeoffIncrCost.setErrorMessage("");
		this.writeoffSuplRent.setErrorMessage("");
		this.writeoffSchFee.setErrorMessage("");
		this.adjAmount.setErrorMessage("");
		this.writtenoffAcc.setErrorMessage("");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnWriteoffPay.isVisible());
	}

	/**
	 * Method for Calculate Write-off Effect Schedule
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnWriteoffCal(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (!isValidated()) {
			return;
		}

		/*
		 * this.writeoffPriAmt.setDisabled(true); this.writeoffPftAmt.setDisabled(true);
		 * this.writeoffInsAmt.setDisabled(true); this.writeoffPptInsAmt.setDisabled(true);
		 * this.writeoffCrInsAmt.setDisabled(true); this.writeoffIncrCost.setDisabled(true);
		 * this.writeoffSuplRent.setDisabled(true); this.writeoffSchFee.setDisabled(true);
		 * this.adjAmount.setDisabled(true);
		 */

		this.btnWriteoffCal.setDisabled(true);
		this.btnWriteoffReCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		this.finScheduleTab.setVisible(true);
		this.listBoxSchedule.getItems().clear();

		//Reset only Schedule Details Data
		Cloner cloner = new Cloner();
		FinanceWriteoffHeader schdData = cloner.deepClone(financeWriteoffHeader);
		schdData.getFinanceDetail()
				.getFinScheduleData()
				.setFinanceScheduleDetails(
						getFinanceWriteoffService().getFinScheduleDetails(financeMain.getFinReference()));

		calScheduleWriteOffDetails(schdData);
		doStoreInitValues();

		logger.debug("Leaving" + event.toString());
	}

	private List<FinanceScheduleDetail> calScheduleWriteOffDetails(FinanceWriteoffHeader financeWriteoffHeader)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		//Copy Total Finance Schedule Data for Calculation without Effecting the Original Schedule Data
		Cloner cloner = new Cloner();
		effectFinScheduleData = cloner.deepClone(financeWriteoffHeader);

		BigDecimal woPriAmt = PennantAppUtil.unFormateAmount(this.writeoffPriAmt.getValue(), format);
		BigDecimal woPftAmt = PennantAppUtil.unFormateAmount(this.writeoffPftAmt.getValue(), format);
		BigDecimal woInsAmt = PennantAppUtil.unFormateAmount(this.writeoffInsAmt.getValue(), format);
		BigDecimal woIncrCost = PennantAppUtil.unFormateAmount(this.writeoffIncrCost.getValue(), format);
		BigDecimal woSuplRent = PennantAppUtil.unFormateAmount(this.writeoffSuplRent.getValue(), format);
		BigDecimal woSchFee = PennantAppUtil.unFormateAmount(this.writeoffSchFee.getValue(), format);

		List<FinanceScheduleDetail> effectedFinSchDetails = effectFinScheduleData.getFinanceDetail()
				.getFinScheduleData().getFinanceScheduleDetails();

		if (effectedFinSchDetails != null && effectedFinSchDetails.size() > 0) {
			for (int i = 0; i < effectedFinSchDetails.size(); i++) {

				FinanceScheduleDetail curSchdl = effectedFinSchDetails.get(i);

				//Reset Write-off Principal Amount
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
				//Reset Write-off Profit Amount
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
				//Reset Write-off Insurance Amount
				if (woInsAmt.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schInsBal = curSchdl.getInsSchd().subtract(
							curSchdl.getSchdInsPaid().subtract(curSchdl.getWriteoffIns()));
					if (schInsBal.compareTo(BigDecimal.ZERO) > 0) {
						if (woInsAmt.compareTo(schInsBal) >= 0) {
							curSchdl.setWriteoffIns(curSchdl.getWriteoffIns().add(schInsBal));
							woInsAmt = woInsAmt.subtract(schInsBal);
						} else {
							curSchdl.setWriteoffIns(curSchdl.getWriteoffIns().add(woInsAmt));
							woInsAmt = BigDecimal.ZERO;
						}
					}
				}

				//Reset Write-off Increased Cost 
				if (woIncrCost.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schIncrCost = curSchdl.getIncrCost().subtract(
							curSchdl.getIncrCostPaid().subtract(curSchdl.getWriteoffIncrCost()));
					if (schIncrCost.compareTo(BigDecimal.ZERO) > 0) {
						if (woIncrCost.compareTo(schIncrCost) >= 0) {
							curSchdl.setWriteoffIncrCost(curSchdl.getWriteoffIncrCost().add(schIncrCost));
							woIncrCost = woIncrCost.subtract(schIncrCost);
						} else {
							curSchdl.setWriteoffIncrCost(curSchdl.getWriteoffIncrCost().add(woIncrCost));
							woIncrCost = BigDecimal.ZERO;
						}
					}
				}
				//Reset Write-off Suplement Rent 
				if (woSuplRent.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schSuplRent = curSchdl.getSuplRent().subtract(
							curSchdl.getSuplRentPaid().subtract(curSchdl.getWriteoffSuplRent()));
					if (schSuplRent.compareTo(BigDecimal.ZERO) > 0) {
						if (woSuplRent.compareTo(schSuplRent) >= 0) {
							curSchdl.setWriteoffSuplRent(curSchdl.getWriteoffSuplRent().add(schSuplRent));
							woSuplRent = woSuplRent.subtract(schSuplRent);
						} else {
							curSchdl.setWriteoffSuplRent(curSchdl.getWriteoffSuplRent().add(woSuplRent));
							woSuplRent = BigDecimal.ZERO;
						}
					}
				}
				//Reset Write-off Schedule Fee
				if (woSchFee.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal schFee = curSchdl.getFeeSchd().subtract(
							curSchdl.getSchdFeePaid().subtract(curSchdl.getWriteoffSchFee()));
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
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * 
	 * @param aFinScheduleData
	 *            (FinScheduleData)
	 * 
	 */
	public void doFillScheduleList(FinanceWriteoffHeader writeoffHeader) {
		logger.debug("Entering");

		FinanceScheduleDetail prvSchDetail = null;

		//Reset To Finance Schedule Data Object For rendering purpose
		FinScheduleData aFinScheduleData = new FinScheduleData();
		aFinScheduleData.setFinanceMain(writeoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain());
		aFinScheduleData.setFinanceScheduleDetails(sortSchdDetails(writeoffHeader.getFinanceDetail()
				.getFinScheduleData().getFinanceScheduleDetails()));
		aFinScheduleData.setDisbursementDetails(writeoffHeader.getFinanceDetail().getFinScheduleData()
				.getDisbursementDetails());
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
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap.get(penaltyDetail
								.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					} else {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}

			//Clear all the listitems in listbox
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

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinScheduleData);
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("penaltyDetailsMap", penaltyDetailsMap);
				map.put("window", this.window_FinWriteoffDialog);
				finRender.render(map, prvSchDetail, false, true, true,  aFinScheduleData.getFinFeeDetailList(), showRate, false);

				if (i == sdSize - 1) {
					finRender.render(map, prvSchDetail, true, true, true,  aFinScheduleData.getFinFeeDetailList(), showRate, false);
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
	 * @throws Exception
	 */
	public void onClick$btnWriteoffReCal(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.writeoffPriAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPriAmt"));
		this.writeoffPftAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPftAmt"));
		this.writeoffInsAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffInsAmt"));
		this.writeoffIncrCost.setDisabled(isReadOnly("FinWriteoffDialog_writeoffIncrCost"));
		this.writeoffSuplRent.setDisabled(isReadOnly("FinWriteoffDialog_writeoffSuplRent"));
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

	private boolean isValidated() throws InterruptedException {
		logger.debug("Entering");

		if ((this.writeoffPriAmt.getValue() == null || this.writeoffPriAmt.getValue().compareTo(BigDecimal.ZERO) <= 0)
				&& (this.writeoffPftAmt.getValue() == null || this.writeoffPftAmt.getValue().compareTo(BigDecimal.ZERO) <= 0)
				&& (this.writeoffInsAmt.getValue() == null || this.writeoffInsAmt.getValue().compareTo(BigDecimal.ZERO) <= 0)
				&& (this.writeoffIncrCost.getValue() == null || this.writeoffIncrCost.getValue().compareTo(
						BigDecimal.ZERO) <= 0)
				&& (this.writeoffSuplRent.getValue() == null || this.writeoffSuplRent.getValue().compareTo(
						BigDecimal.ZERO) <= 0)
				&& (this.writeoffSchFee.getValue() == null || this.writeoffSchFee.getValue().compareTo(BigDecimal.ZERO) <= 0)) {

			MessageUtil.showError("Write-off Amount must be Entered.");
			return false;
		}

		BigDecimal woPriAmt = PennantAppUtil.unFormateAmount(this.writeoffPriAmt.getValue(), format);
		BigDecimal woPftAmt = PennantAppUtil.unFormateAmount(this.writeoffPftAmt.getValue(), format);
		BigDecimal woInsAmt = PennantAppUtil.unFormateAmount(this.writeoffInsAmt.getValue(), format);
		BigDecimal woIncrCost = PennantAppUtil.unFormateAmount(this.writeoffIncrCost.getValue(), format);
		BigDecimal woSuplRent = PennantAppUtil.unFormateAmount(this.writeoffSuplRent.getValue(), format);
		BigDecimal woSchFee = PennantAppUtil.unFormateAmount(this.writeoffSchFee.getValue(), format);

		if (woPriAmt.compareTo(financeWriteoff.getUnPaidSchdPri()) > 0
				|| woPftAmt.compareTo(financeWriteoff.getUnPaidSchdPft()) > 0
				|| woInsAmt.compareTo(financeWriteoff.getUnpaidIns()) > 0
				|| woIncrCost.compareTo(financeWriteoff.getUnpaidIncrCost()) > 0
				|| woSuplRent.compareTo(financeWriteoff.getUnpaidSuplRent()) > 0
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
		;

		FinanceWriteoff writeoff = getFinanceWriteoff();

		writeoff.setFinReference(this.finReference.getValue());
		writeoff.setWrittenoffPri(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_WOPriAmt.getValue(), format));
		writeoff.setWrittenoffPft(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_WOPftAmt.getValue(), format));
		writeoff.setWriteoffIns(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_WOInsAmt.getValue(), format));
		writeoff.setWrittenoffIncrCost(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_WOIncrCostAmt.getValue(), format));
		writeoff.setWrittenoffSuplRent(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_WOSuplRentAmt.getValue(), format));
		writeoff.setWrittenoffSchFee(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_WOSchdFeeAmt.getValue(), format));
		writeoff.setCurODPri(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ODPriAmt.getValue(),
				format));
		writeoff.setCurODPft(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ODPftAmt.getValue(),
				format));
		writeoff.setUnPaidSchdPri(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_UnPaidPriAmt.getValue(), format));
		writeoff.setUnPaidSchdPft(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_UnPaidPftAmt.getValue(), format));
		writeoff.setUnpaidIns(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_UnPaidInsAmt.getValue(), format));
		writeoff.setUnpaidIncrCost(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_UnPaidIncrCostAmt.getValue(), format));
		writeoff.setUnpaidSuplRent(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_UnPaidSuplRentAmt.getValue(), format));
		writeoff.setUnpaidSchFee(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_UnPaidSchFeeAmt.getValue(), format));
		writeoff.setPenaltyAmount(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_PenaltyAmt.getValue(), format));
		writeoff.setProvisionedAmount(PennantApplicationUtil.unFormateAmount(
				this.label_FinWriteoffDialog_ProvisionAmt.getValue(), format));

		writeoff.setWriteoffPrincipal(PennantApplicationUtil.unFormateAmount(this.writeoffPriAmt.getValue(), format));
		writeoff.setWriteoffProfit(PennantApplicationUtil.unFormateAmount(this.writeoffPftAmt.getValue(), format));
		writeoff.setWriteoffIns(PennantApplicationUtil.unFormateAmount(this.writeoffInsAmt.getValue(), format));
		writeoff.setWriteoffIncrCost(PennantApplicationUtil.unFormateAmount(this.writeoffIncrCost.getValue(), format));
		writeoff.setWriteoffSuplRent(PennantApplicationUtil.unFormateAmount(this.writeoffSuplRent.getValue(), format));
		writeoff.setWriteoffSchFee(PennantApplicationUtil.unFormateAmount(this.writeoffSchFee.getValue(), format));
		writeoff.setAdjAmount(PennantApplicationUtil.unFormateAmount(this.adjAmount.getValue(), format));
		writeoff.setRemarks(this.remarks.getValue());

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (this.writeoffDate.getValue() == null) {
				this.writeoffDate.setValue(DateUtility.getAppDate());
			} else if (!this.writeoffDate.isDisabled()) {
				this.writeoffDate.setConstraint(new PTDateValidator(Labels
						.getLabel("label_FinWriteoffDialog_WriteoffDate.value"), false, SysParamUtil
						.getValueAsDate("APP_DFT_START_DATE"), DateUtility.getAppDate(), true));
			}

			writeoff.setWriteoffDate(this.writeoffDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!recSave && this.writtenoffAcc.isMandatory() && this.writtenoffAcc.isVisible()
					&& !this.writtenoffAcc.isReadonly()) {
				this.writtenoffAcc.setConstraint(new PTStringValidator(Labels
						.getLabel("label_FinWriteoffDialog_WrittenoffAcc.value"), null, true));
			}
			writeoff.setWrittenoffAcc(this.writtenoffAcc.getValue());
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
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			this.writeoffDate.setConstraint("");
			this.writtenoffAcc.setConstraint("");

			this.writeoffDate.setErrorMessage("");
			this.writtenoffAcc.setErrorMessage("");

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
	 * @throws Exception
	 */
	public void onClick$btnWriteoffPay(Event event) throws Exception {
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

		FinanceWriteoffHeader aFinanceWriteoffHeader = new FinanceWriteoffHeader();
		Cloner cloner = new Cloner();
		aFinanceWriteoffHeader = cloner.deepClone(getFinanceWriteoffHeader());
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

		//Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);

		//Prepare Validation & Calling
		if (!isValidated()) {
			return;
		}

		//Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		//Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().isStageAccountingsExecuted()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().getStageDisbCrSum().compareTo(
					getStageAccountingDetailDialogCtrl().getStageDisbDrSum()) != 0) {
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

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		//loading Write Off Object Entered Data into Bean
		aFinanceWriteoffHeader.setFinanceWriteoff(doWriteComponentsToBean());

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
			aFinanceMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_WRITEOFF);
			aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceWriteoffHeader, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				//Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				//Mail Alert Notification for Customer/Dealer/Provider...etc
				FinanceDetail financeDetail = aFinanceWriteoffHeader.getFinanceDetail();
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					List<String> templateTyeList = new ArrayList<String>();
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_AE);
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_CN);

					List<ValueLabel> referenceIdList = getFinanceReferenceDetailService().getTemplateIdList(
							aFinanceMain.getFinType(), moduleDefiner, getRole(), templateTyeList);

					templateTyeList = null;
					if (!referenceIdList.isEmpty()) {

						boolean isCustomerNotificationExists = false;
						List<Long> notificationIdlist = new ArrayList<Long>();
						for (ValueLabel valueLabel : referenceIdList) {
							notificationIdlist.add(Long.valueOf(valueLabel.getValue()));
							if (NotificationConstants.TEMPLATE_FOR_CN.equals(valueLabel.getLabel())) {
								isCustomerNotificationExists = true;
							}
						}

						// Mail ID details preparation
						Map<String, List<String>> mailIDMap = new HashMap<String, List<String>>();

						// Customer Email Preparation
						if (isCustomerNotificationExists
								&& financeDetail.getCustomerDetails().getCustomerEMailList() != null
								&& !financeDetail.getCustomerDetails().getCustomerEMailList().isEmpty()) {

							List<CustomerEMail> emailList = financeDetail.getCustomerDetails().getCustomerEMailList();
							List<String> custMailIdList = new ArrayList<String>();
							for (CustomerEMail customerEMail : emailList) {
								custMailIdList.add(customerEMail.getCustEMail());
							}
							if (!custMailIdList.isEmpty()) {
								mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, custMailIdList);
							}
						}

						getMailUtil().sendMail(notificationIdlist, financeDetail, mailIDMap, null);
					}

				}

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {
						String reference = aFinanceMain.getFinReference();

						// Send message Notification to Users
						if (aFinanceMain.getNextUserId() != null) {
							String usrLogins = aFinanceMain.getNextUserId();
							List<String> usrLoginList = new ArrayList<String>();

							if (usrLogins.contains(",")) {
								String[] to = usrLogins.split(",");
								for (String roleCode : to) {
									usrLoginList.add(roleCode);
								}
							} else {
								usrLoginList.add(usrLogins);
							}

							List<String> userLogins = getFinanceDetailService().getUsersLoginList(usrLoginList);

							String[] to = new String[userLogins.size()];
							for (int i = 0; i < userLogins.size(); i++) {
								to[i] = String.valueOf(userLogins.get(i));
							}

							if (StringUtils.isNotEmpty(reference)) {
								if (!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(aFinanceMain
										.getRecordStatus())) {
									getEventManager().publish(
											Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference" + ":"
													+ reference, Notify.USER, to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), Notify.USER, to);
							}
						} else {
							if (StringUtils.isNotEmpty(aFinanceMain.getNextRoleCode())) {
								if (!PennantConstants.RCD_STATUS_CANCELLED.equals(aFinanceMain.getRecordStatus())) {
									String[] to = aFinanceMain.getNextRoleCode().split(",");
									String message;

									if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
										message = Labels.getLabel("REC_FINALIZED_MESSAGE");
									} else {
										message = Labels.getLabel("REC_PENDING_MESSAGE");
									}
									message += " with Reference" + ":" + reference;

									getEventManager().publish(message, to, finDivision, aFinanceMain.getFinBranch());
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
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
	 * @throws InterruptedException
	 */
	private boolean doProcess(FinanceWriteoffHeader aFinanceWriteoffHeader, String tranType)
			throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
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

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)) {
					processCompleted = true;
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {
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
					setNextTaskDetails(taskId, tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData()
							.getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceWriteoffHeader);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceWriteoffHeader tFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
						.getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData()
						.getFinanceMain(), finishedTasks);

			}

			FinanceWriteoffHeader tFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
					.getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData()
					.getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData()
							.getFinanceMain());
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
	 * @throws InterruptedException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceWriteoffHeader aFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail()
				.getModelData();
		FinanceMain afinanceMain = aFinanceWriteoffHeader.getFinanceDetail().getFinScheduleData().getFinanceMain();

		try {

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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
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

		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Exception: ", e);
		}

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
		return new AuditHeader(header.getFinReference(), null, null, null, auditDetail, header.getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		this.btnNotes.setSclass("");
		final HashMap<String, Object> map = new HashMap<String, Object>();
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
	 * 
	 * @throws Exception
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws Exception {
		logger.debug("Entering");

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");

		//Finance Accounting Details Execution
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
	public FinanceDetail onExecuteStageAccDetail() throws InterruptedException, IllegalAccessException,
			InvocationTargetException {
		getFinanceDetail().setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);
		return getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 * 
	 * @throws Exception
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception {
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		
		FinanceProfitDetail profitDetail = null;
		if(StringUtils.isEmpty(moduleDefiner)){
			profitDetail = new FinanceProfitDetail();
		}else{
			profitDetail = getFinanceDetailService().getFinProfitDetailsById(getFinanceDetail().getFinScheduleData().getFinReference());
		}
		
		AEEvent aeEvent = prepareAccountingData(onLoadProcess, profitDetail);
		HashMap<String, Object> dataMap = aeEvent.getDataMap();

		prepareFeeRulesMap(aeEvent.getAeAmountCodes(), dataMap);
		aeEvent.getAeAmountCodes().setTotalWriteoff(financeWriteoff.getWriteoffPrincipal().add(
				financeWriteoff.getWriteoffProfit().add(financeWriteoff.getWrittenoffSchFee())));
		aeEvent.getAeAmountCodes().getDeclaredFieldValues(dataMap);
		financeWriteoff.getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);
		
		aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
		accountingSetEntries.addAll(aeEvent.getReturnDataSet());

		//Disb Instruction Posting
		if (eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBS)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSF)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSN)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSP)) {
			prepareDisbInstructionPosting(accountingSetEntries, aeEvent);
		}

		getFinanceDetail().setReturnDataSetList(accountingSetEntries);

		if (getAccountingDetailDialogCtrl() != null) {
			getAccountingDetailDialogCtrl().doFillAccounting(accountingSetEntries);
		}

		logger.debug("Leaving");
	}

	private AEEvent prepareAccountingData(boolean onLoadProcess, FinanceProfitDetail profitDetail) throws InterruptedException, IllegalAccessException,
			InvocationTargetException {

		Date curBDay = DateUtility.getAppDate();

		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		FinanceWriteoff financeWriteoff = doWriteComponentsToBean();

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
		}

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		//For New Records Profit Details will be set inside the AEAmounts 
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {//FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, curBDay, curBDay);
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE));
		}
		
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(finMain, finSchdDetails, newProfitDetail, curBDay);
		amountCodes.setBpi(finMain.getBpiAmount());
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
		amountCodes.setDisburse(finMain.getFinCurrAssetValue());

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}

		return aeEvent;
	}

	private void prepareDisbInstructionPosting(List<ReturnDataSet> accountingSetEntries, AEEvent aeEvent) throws Exception {
		
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_DISBINS);
		List<FinAdvancePayments> advPayList = getFinanceDetail().getAdvancePaymentsList();
		
		aeEvent.getAcSetIDList().clear();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), AccountEventConstants.ACCEVENT_DISBINS, FinanceConstants.MODULEID_PROMOTION));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), AccountEventConstants.ACCEVENT_DISBINS, FinanceConstants.MODULEID_FINTYPE));
		}

		//loop through the disbursements.
		if (advPayList != null && !advPayList.isEmpty()) {

			for (int i = 0; i < advPayList.size(); i++) {
				FinAdvancePayments advPayment = advPayList.get(i);

				aeEvent.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
				amountCodes.setDisbInstAmt(advPayment.getAmtToBeReleased());
				amountCodes.setPartnerBankAc(advPayment.getPartnerBankAc());
				amountCodes.setPartnerBankAcType(advPayment.getPartnerBankAcType());

				HashMap<String, Object> dataMap = aeEvent.getDataMap();
				dataMap = amountCodes.getDeclaredFieldValues();
				aeEvent.setDataMap(dataMap);
				if (advPayment.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(advPayment.getRecordType())) {
					aeEvent.setNewRecord(true);
				}
				
				// Call Map Build Method
				aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
				List<ReturnDataSet> returnDataSet = aeEvent.getReturnDataSet();
				accountingSetEntries.addAll(returnDataSet);
			}
		}

	}

	private void prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap) {
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
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmount());

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

	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
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
}