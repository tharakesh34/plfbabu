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
 * FileName    		:  FinanceMaintenanceDialogCtrl.java                                                   * 	  
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.FinCollateralMark;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExt;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.dda.DDAProcessService;
import com.pennant.backend.service.finance.FinanceMaintenanceService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.core.EventManager.Notify;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pff.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class FinanceMaintenanceDialogCtrl extends FinanceBaseCtrl<FinanceMain> {
	private static final long				serialVersionUID	= 6004939933729664895L;
	private final static Logger				logger				= Logger.getLogger(FinanceMaintenanceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_FinanceMaintenanceDialog;											// autoWired

	// Finance Main Details Tab---> 1. Key Details
	protected CurrencyBox					downPaySupl;																// autoWired
	protected Row							row_downPaySupl;															// autoWired
	protected Label							windowTitle;																// autoWired

	protected Row							row_finWriteoffPaymentDate;
	protected Datebox						writeoffDate;
	protected Row							row_finWriteoffPayment;
	protected Row							row_DisbAccId;
	protected CurrencyBox					finWriteoffAmount;
	protected CurrencyBox					finWriteoffPaidAmount;
	protected Label							label_FinanceMainDialog_finWriteoffPayAccount;

	protected CurrencyBox					finWriteoffPayAmount;
	protected AccountSelectionBox			finWriteoffPayAccount;
	protected Row							row_finWriteoff;
	protected Button						btnFlagDetails;
	protected Uppercasebox					flagDetails;
	private List<FinFlagsDetail>			finFlagsDetailList	= null;
	protected Map<String, Object>			flagTypeDataMap		= new HashMap<String, Object>();
	protected ExtendedCombobox				mandateRef;

	protected Datebox						odStartDate;
	protected CurrencyBox					odFinAssetValue;
	protected Row							row_ODTenor;
	protected Row							row_ODStartDate;
	protected Row							row_QuickDisb;
	protected Space							space_DroplineDate;

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	protected transient BigDecimal			oldVar_downPaySupl;
	protected transient BigDecimal			oldVar_finWriteoffPayAmount;
	protected transient String				oldVar_finWriteoffPayAccount;

	private FinanceMaintenanceService		financeMaintenanceService;
	private DDAControllerService			ddaControllerService;
	private DDAProcessService				ddaProcessService;
	private FinanceReferenceDetailService	financeReferenceDetailService;
	private CollateralMarkProcess			collateralMarkProcess;

	protected Label							label_FinanceMainDialog_FinAssetValue;
	protected Label							label_FinanceMainDialog_ODFinAssetValue;
	protected Label							label_FinanceMainDialog_FinCurrentAssetValue;

	protected Label							label_FinanceMainDialog_FinDivision;
	protected CurrencyBox					finCurrentAssetValue;
	protected Row							row_FinAssetValue;
	protected CurrencyBox					finAssetValue;
	protected Label							netFinAmount;
	protected Checkbox						manualSchedule;
	protected Row							row_ManualSchedule;
	protected Textbox						finDivisionName;
	protected Hbox							hbox_PromotionProduct;
	private Label							label_FinanceMainDialog_PromoProduct;
	private Label							label_FinanceMainDialog_FinType;
	private Label							label_FinanceMainDialog_FinReference;
	private Label							label_FinanceMainDialog_FinBranch;

	/**
	 * default constructor.<br>
	 */
	public FinanceMaintenanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceMaintenanceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceMaintenanceDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				FinanceMain befImage = new FinanceMain();
				BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
				setFinanceDetail(getFinanceDetail());
			}

			// READ OVERHANDED params !
			// we get the financeMainListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete financeMain here.

			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "FinanceMainDialog", menuItemRightName);
				}
			} else {
				this.south.setHeight("0px");
			}

			isEnquiry = true;
			setMainWindow(window_FinanceMaintenanceDialog);

			setProductCode("Murabaha");

			/* set components visible dependent of the users rights */
			doCheckRights();

			this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52 + "px");
			// set Field Properties
			doSetFieldProperties();

			// Setting tile Name based on Service Action
			if (StringUtils.isNotEmpty(moduleDefiner)) {
				this.windowTitle.setValue(Labels.getLabel(moduleDefiner + "_Window.Title"));
			}

			doShowDialog(getFinanceDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceMaintenanceDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		super.doSetFieldProperties();
		FinanceType fintype = getFinanceDetail().getFinScheduleData().getFinanceType();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(finMain.getFinCcy());

		this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.downPaySupl.setTextBoxWidth(200);
		this.downPaySupl.setScale(format);

		this.odStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.odFinAssetValue.setMandatory(true);
		this.odFinAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.odFinAssetValue.setScale(format);
		this.odFinAssetValue.setTextBoxWidth(200);

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			this.finWriteoffPayAccount.setAccountDetails(fintype.getFinType(), AccountConstants.FinanceAccount_REPY,
					fintype.getFinCcy());
			this.finWriteoffPayAccount.setFormatter(format);
			this.finWriteoffPayAccount.setBranchCode(StringUtils.trimToEmpty(finMain.getFinBranch()));
			this.finWriteoffPayAccount.setTextBoxWidth(165);
			this.finWriteoffPayAccount.setMandatoryStyle(true);

			this.finWriteoffPayAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
			this.finWriteoffPayAmount.setTextBoxWidth(200);
			this.finWriteoffPayAmount.setScale(format);
			this.writeoffDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.finWriteoffAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
			this.finWriteoffAmount.setTextBoxWidth(200);
			this.finWriteoffAmount.setScale(format);

			this.finWriteoffPaidAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
			this.finWriteoffPaidAmount.setTextBoxWidth(200);
			this.finWriteoffPaidAmount.setScale(format);

			if (!ImplementationConstants.ACCOUNTS_APPLICABLE) {
				this.label_FinanceMainDialog_finWriteoffPayAccount.setVisible(false);
				this.finWriteoffPayAccount.setVisible(false);
				this.finWriteoffPayAccount.setMandatoryStyle(false);
			}
		}

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN)) {
			this.mandateRef.setModuleName("Mandate");
			this.mandateRef.setMandatoryStyle(true);
			this.mandateRef.setValueColumn("MandateID");
			this.mandateRef.setDescColumn("MandateRef");
			this.mandateRef.setDisplayStyle(2);
			this.mandateRef.setValidateColumns(new String[] { "MandateID" });
		} else {
			readOnlyComponent(true, this.finRepayMethod);
			readOnlyComponent(true, this.mandateRef);
		}

		//Accounts should be displayed only to the Banks
		if (!ImplementationConstants.ACCOUNTS_APPLICABLE) {
			this.row_DisbAccId.setVisible(false);
			this.downPayAccount.setVisible(false);
			this.label_FinanceMainDialog_DownPayAccount.setVisible(false);
		}
		this.finAssetValue.setProperties(false, format);
		this.finCurrentAssetValue.setProperties(false, format);
		//Field visibility & Naming for FinAsset value and finCurrent asset value by  OD/NONOD.
		setFinAssetFieldVisibility(fintype);
		logger.debug("Leaving");
	}

	private void setFinAssetFieldVisibility(FinanceType financeType) {

		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finMain.getProductCategory())) {
			isOverdraft = true;
		}

		if (isOverdraft) {
			if (StringUtils.isNotEmpty(finMain.getDroplineFrq())) {
				this.droplineFrq.setValue(finMain.getDroplineFrq());
				this.firstDroplineDate.setValue(finMain.getFirstDroplineDate());
				this.row_DroplineFrq.setVisible(true);
			} else {
				this.droplineFrq.setValue(finMain.getRepayFrq());
				this.row_DroplineFrq.setVisible(false);
			}
			this.row_ODTenor.setVisible(true);
			this.row_ODStartDate.setVisible(true);
			this.row_pftServicingODLimit.setVisible(true);
			this.noOfTermsRow.setVisible(false);
			this.row_RpyAdvPftRate.setVisible(false);
			this.gb_gracePeriodDetails.setVisible(false);
			this.row_manualSteps.setVisible(false);
			this.row_stepFinance.setVisible(false);
			this.row_FinAmount.setVisible(false);
			this.row_FinStartDate.setVisible(false);
			this.row_QuickDisb.setVisible(false);
			this.defermentsRow.setVisible(false);
			this.row_MaturityDate.setVisible(false);
			this.repayRateBasisRow.setVisible(false);
			this.row_RpyAdvBaseRate.setVisible(false);
			this.row_FinRepRates.setVisible(false);
			this.scheduleMethodRow.setVisible(false);
			this.rpyPftFrqRow.setVisible(false);
			this.rpyRvwFrqRow.setVisible(false);
			this.rpyCpzFrqRow.setVisible(false);
			this.row_supplementRent.setVisible(false);
			this.rpyFrqRow.setVisible(false);
			this.odRpyFrqRow.setVisible(true);
			this.row_salesDept.setVisible(false);
			this.row_accountsOfficer.setVisible(true);

			this.label_FinanceMainDialog_FinType.setValue(Labels.getLabel("label_FinanceMainDialog_ODFinType.value"));
			this.label_FinanceMainDialog_FinReference.setValue(Labels
					.getLabel("label_FinanceMainDialog_ODFinReference.value"));
			this.label_FinanceMainDialog_FinBranch.setValue(Labels
					.getLabel("label_FinanceMainDialog_ODFinBranch.value"));
		}

		if (financeType.isAlwMaxDisbCheckReq()) {

			if (isOverdraft) {

				this.label_FinanceMainDialog_FinAssetValue.setValue(Labels
						.getLabel("label_FinanceMainDialog_FinOverDftLimit.value"));
				this.label_FinanceMainDialog_FinCurrentAssetValue.setValue("");
				this.finCurrentAssetValue.setVisible(false);
				this.gb_ddaRequest.setVisible(false);
			} else {
				if (!isOverdraft && financeType.isFinIsAlwMD()) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
					this.row_FinAssetValue.setVisible(true);
					this.finAssetValue.setMandatory(true);
					this.finCurrentAssetValue.setReadonly(true);
					this.label_FinanceMainDialog_FinAssetValue.setValue(Labels
							.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setValue(Labels
							.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
				} else {
					this.label_FinanceMainDialog_FinAssetValue.setVisible(false);
					this.finAssetValue.setVisible(false);
					this.label_FinanceMainDialog_FinCurrentAssetValue.setValue(Labels
							.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setVisible(true);
					this.finCurrentAssetValue.setVisible(true);
				}
			}
		} else {
			this.row_FinAssetValue.setVisible(false);

		}
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

		getUserWorkspace().allocateAuthorities("FinanceMainDialog", getRole(), menuItemRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_FinanceMaintenanceDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnDelete(Event event) throws Exception {
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
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * On Selecting
	 * 
	 * @param event
	 */
	public void onSelect$finRepayMethod(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckDDA();
		doCheckMandate(getComboboxValue(this.finRepayMethod), this.custID.longValue(), true);
		logger.debug("Leaving" + event.toString());
	}

	private void doCheckMandate(String val, long CustID, boolean onChange) {
		if (MandateConstants.TYPE_ECS.equals(val) || MandateConstants.TYPE_DDM.equals(val)
				|| MandateConstants.TYPE_NACH.equals(val)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_mandateId"), this.mandateRef);
			if (onChange) {
				this.mandateRef.setValue("");
			}

		} else {
			readOnlyComponent(true, this.mandateRef);
			this.mandateRef.setValue("");
			this.mandateRef.setAttribute("mandateID", new Long(0));
		}
		addMandateFiletrs(val, CustID);

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
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException,
			InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		super.doWriteBeanToComponents(aFinanceDetail, onLoadProcess);
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			this.pftServicingODLimit.setChecked(aFinanceMain.isPftServicingODLimit());
			if (aFinanceMain.getNumberOfTerms() > 1) {
				int odYearlTerms = aFinanceMain.getNumberOfTerms() / 12;
				this.odYearlyTerms.setValue(odYearlTerms);
				this.odMnthlyTerms.setValue(aFinanceMain.getNumberOfTerms() % 12);
			}
			this.pftServicingODLimit.setChecked(aFinanceMain.isPftServicingODLimit());
			this.odStartDate.setValue(aFinanceMain.getFinStartDate());
			this.odRepayFrq.setValue(aFinanceMain.getRepayFrq());
			this.odRepayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			this.odMaturityDate.setValue(aFinanceMain.getMaturityDate());
			this.odFinAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(),
					CurrencyUtil.getFormat(aFinanceMain.getFinCcy())));
		}
		this.mandateRef.setAttribute("mandateID", aFinanceMain.getMandateID());
		Mandate mandate = aFinanceDetail.getMandate();
		if (aFinanceDetail.getMandate() != null) {
			this.mandateRef.setValue(String.valueOf(mandate.getMandateID()),
					StringUtils.trimToEmpty(mandate.getMandateRef()));
		}
		doCheckMandate(aFinanceMain.getFinRepayMethod(), aFinanceMain.getCustID(), false);

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired()
				&& aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.row_downPaySupl.setVisible(true);
			this.downPaySupl.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPaySupl(), format));
		}

		// Customer Details
		appendCustomerDetailTab();

		// Schedule Details
		appendScheduleDetailTab(true, true);

		// Co-applicants & joint account Holder Details
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)
				&& StringUtils.equals(aFinanceMain.getLovDescFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
			appendJointGuarantorDetailTab();
		}
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			this.row_finWriteoff.setVisible(true);
			this.finWriteoffPayAccount.setValue(PennantApplicationUtil.unFormatAccountNumber(aFinanceDetail
					.getFinwriteoffPayment().getWriteoffPayAccount()));
			this.finWriteoffPayAmount.setValue(PennantAppUtil.formateAmount(aFinanceDetail.getFinwriteoffPayment()
					.getWriteoffPayAmount(), format));
			this.row_finWriteoffPayment.setVisible(true);
			this.finWriteoffAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceDetail.getFinwriteoffPayment()
					.getWriteoffAmount(), format));
			this.finWriteoffPaidAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceDetail
					.getFinwriteoffPayment().getWriteoffPaidAmount(), format));
			this.row_finWriteoffPaymentDate.setVisible(true);
			this.writeoffDate.setValue(aFinanceDetail.getFinwriteoffPayment().getWriteoffDate());
		}
		// Agreement Details tab
		appendAgreementsDetailTab(true);

		// Check list Details tab
		appendCheckListDetailTab(aFinanceDetail, false, true);

		// Recommendation Details
		appendRecommendDetailTab(true);

		// Document Details Tab
		appendDocumentDetailTab();

		// Collateral Detail Tab
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)) {
			appendFinCollateralTab();
		}

		// Stage Accounting Details
		appendStageAccountingDetailsTab(true);

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			// Accounting
			appendAccountingDetailTab(true);
		}

		// fill the components with the Finance Flags Data and Display
		doFillFinFlagsList(aFinanceDetail.getFinFlagsDetails());

		//Showing Product Details for Promotion Type
		this.finDivisionName.setValue(aFinanceDetail.getFinScheduleData().getFinanceType().getFinDivision() + " - "
				+ aFinanceDetail.getFinScheduleData().getFinanceType().getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(aFinanceDetail.getFinScheduleData().getFinanceType().getProduct())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.getLabel_FinanceMainDialog_PromoProduct().setVisible(true);
			this.promotionProduct.setValue(aFinanceDetail.getFinScheduleData().getFinanceType().getProduct() + " - "
					+ aFinanceDetail.getFinScheduleData().getFinanceType().getLovDescPromoFinTypeDesc());
			this.label_FinanceMainDialog_FinType.setValue(Labels
					.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isManualSchedule()) {
			this.row_ManualSchedule.setVisible(true);
			this.manualSchedule.setChecked(aFinanceMain.isManualSchedule());
		} else {
			this.row_ManualSchedule.setVisible(false);
		}

		this.finAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(), format));
		this.finCurrentAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinCurrAssetValue(), format));
		setNetFinanceAmount(true);

		logger.debug("Leaving");
	}

	public void setNetFinanceAmount(boolean isDataRender) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO : this.finAmount
				.getActualValue();

		// Fee calculation for Add to Disbursement
		List<FinFeeDetail> finFeeDetails = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail feeDetail : finFeeDetails) {
				if (StringUtils
						.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeChargeAmount = feeChargeAmount.add(feeDetail.getActualAmount()
							.subtract(feeDetail.getWaivedAmount()).subtract(feeDetail.getPaidAmount()));
				}
			}
		}

		feeChargeAmount = PennantApplicationUtil.formateAmount(feeChargeAmount, formatter);
		BigDecimal netFinanceVal = finAmount.subtract(
				this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue())).add(feeChargeAmount);
		if (netFinanceVal.compareTo(BigDecimal.ZERO) < 0) {
			netFinanceVal = BigDecimal.ZERO;
		}

		String netFinAmt = PennantApplicationUtil.amountFormate(
				PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter);
		if (finAmount != null && finAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (ImplementationConstants.ADD_FEEINFTV_ONCALC) {
				this.netFinAmount.setValue(netFinAmt + " ("
						+ ((netFinanceVal.multiply(new BigDecimal(100))).divide(finAmount, 2, RoundingMode.HALF_DOWN))
						+ "%)");
			} else {
				this.netFinAmount.setValue(netFinAmt
						+ " ("
						+ (((netFinanceVal.subtract(feeChargeAmount)).multiply(new BigDecimal(100))).divide(finAmount,
								2, RoundingMode.HALF_DOWN)) + "%)");
			}
		} else {
			this.netFinAmount.setValue("");
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
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) throws InterruptedException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinScheduleData aFinanceSchData = aFinanceDetail.getFinScheduleData();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		doClearMessage();
		doSetValidation();
		doSetLOVValidation();
		doCheckFeeReExecution();

		super.doWriteComponentsToBean(aFinanceSchData, wve);
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		boolean isOverDraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverDraft = true;
		}

		try {

			if (recSave) {

				aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getActualValue(), format));
				aFinanceMain.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), format));
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
						(this.downPayBank.getActualValue()).add(this.downPaySupl.getActualValue()), format));

			} else if (this.row_downPayBank.isVisible()
					&& (!this.downPayBank.isReadonly() || !this.downPaySupl.isReadonly())) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());

				if (downPayment.compareTo(this.finAmount.getValidateValue()) > 0) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("MAND_FIELD_MIN", new String[] {
							Labels.getLabel("label_FinanceMainDialog_DownPayment.value"), reqDwnPay.toString(),
							PennantAppUtil.formatAmount(this.finAmount.getActualValue(), format, false) }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel(
							"PERC_MIN",
							new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBS.value"),
									PennantAppUtil.formatAmount(reqDwnPay, format, false) }));
				}
			}
			aFinanceMain
					.setDownPayAccount(PennantApplicationUtil.unFormatAccountNumber(this.downPayAccount.getValue()));

			aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getActualValue(), format));
			aFinanceMain.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), format));
			aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
					this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), format));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			try {
				if (!recSave && this.finWriteoffPayAccount.isMandatory() && this.finWriteoffPayAccount.isVisible()
						&& !this.finWriteoffPayAccount.isReadonly()) {
					this.finWriteoffPayAccount.setConstraint(new PTStringValidator(Labels
							.getLabel("label_FinanceMaintenanceDialog_finWriteoffPayAccount.value"), null, true));
				}
				aFinanceDetail.getFinwriteoffPayment().setWriteoffPayAccount(
						PennantApplicationUtil.unFormatAccountNumber(this.finWriteoffPayAccount.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.writeoffDate.getValue() == null) {
					this.writeoffDate.setValue(DateUtility.getAppDate());
				}
				aFinanceDetail.getFinwriteoffPayment().setWriteoffDate(this.writeoffDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.finWriteoffPayAmount.getValidateValue() != null) {
					aFinanceDetail.getFinwriteoffPayment().setWriteoffPayAmount(
							PennantAppUtil.unFormateAmount(this.finWriteoffPayAmount.getValidateValue(), format));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		aFinanceSchData = super.doWriteSchData(aFinanceSchData, false);

		FinanceMain finMain = aFinanceSchData.getFinanceMain();

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN)) {
			try {
				this.mandateRef.clearErrorMessage();
				this.mandateRef.setErrorMessage("");
				if (!this.mandateRef.isReadonly()) {
					String ref = this.mandateRef.getValidatedValue();
					if (StringUtils.isEmpty(ref)) {
						throw new WrongValueException(this.mandateRef.getTextbox(), Labels.getLabel("FIELD_IS_MAND",
								new String[] { Labels.getLabel("label_MandateDialog_MandateRef.value") }));
					}

				}
				Object mandateID = this.mandateRef.getAttribute("mandateID");
				if (mandateID != null) {
					finMain.setMandateID((long) mandateID);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

		}

		try {
			if (isOverDraft) {
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, this.moduleDefiner)) {
					if (this.odFinAssetValue.getValidateValue().compareTo(
							PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(), format)) < 0) {
						throw new WrongValueException(this.odFinAssetValue.getCcyTextBox(), Labels.getLabel(
								"NUMBER_MINVALUE_EQ",
								new String[] { Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"),
										String.valueOf(aFinanceMain.getFinAssetValue()) }));
					}

				}
			}
			if (this.row_FinAssetValue.isVisible()) {
				//Validate if the total disbursement amount exceeds maximum disbursement Amount 
				if (((StringUtils.isEmpty(moduleDefiner) || StringUtils.equals(FinanceConstants.FINSER_EVENT_ADDDISB,
						moduleDefiner)))) {
					if (this.finCurrentAssetValue.getActualValue() != null
							&& finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
							&& finCurrentAssetValue.getActualValue().compareTo(finAssetValue.getActualValue()) > 0) {
						throw new WrongValueException(finCurrentAssetValue.getCcyTextBox(), Labels.getLabel(
								"NUMBER_MAXVALUE_EQ",
								new String[] { this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(),
										String.valueOf(label_FinanceMainDialog_FinAssetValue.getValue()) }));
					}
				}
				aFinanceMain.setFinAssetValue(PennantAppUtil.unFormateAmount(
						this.finAssetValue.isReadonly() ? this.finAssetValue.getActualValue() : this.finAssetValue
								.getValidateValue(), format));
			}
			//Validation  on finAsset And fin Current Asset value based on field visibility

			if (!isOverDraft) {
				if (financeType.isFinIsAlwMD()) {
					if (this.row_FinAssetValue.isVisible() && StringUtils.isEmpty(moduleDefiner)) {

						//If max disbursement amount less than prinicpal amount validate the amount
						aFinanceMain.setFinAssetValue(PennantAppUtil.unFormateAmount(
								this.finAssetValue.getActualValue(), format));
						aFinanceMain.setFinCurrAssetValue(PennantAppUtil.unFormateAmount(
								this.finCurrentAssetValue.getActualValue(), format));

						if (this.row_FinAssetValue.isVisible() && finAssetValue.getActualValue() != null
								&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0
								&& finAssetValue.getActualValue().compareTo(this.finAmount.getActualValue()) < 0) {

							throw new WrongValueException(finAssetValue.getCcyTextBox(),
									Labels.getLabel(
											"NUMBER_MINVALUE_EQ",
											new String[] {
													this.label_FinanceMainDialog_FinAssetValue.getValue(),
													String.valueOf(Labels
															.getLabel("label_FinanceMainDialog_FinAmount.value")) }));
						}
					}
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!isOverDraft) {
			try {
				aFinanceMain.setFinAssetValue(PennantAppUtil.unFormateAmount(this.finAssetValue.getActualValue(),
						format));
			} catch (WrongValueException we) {
				wve.add(we);
			}

		}
		aFinanceMain.setManualSchedule(this.manualSchedule.isChecked());
		// FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			buildEvent = false;

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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.manualSchedule.setDisabled(false);
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				this.manualSchedule.setDisabled(true);
				doEdit();
			} else {
				this.btnCtrl.setInitNew();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		// setFocus
		this.finReference.focus();

		// Reset Maintenance Buttons for finance modification
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
			this.btnValidate.setVisible(false);
			this.btnBuildSchedule.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail, true);
			onCheckODPenalty(false);
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isNew()
					&& !afinanceDetail.getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
				changeFrequencies();
				this.finReference.focus();
			}

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (financeType.getFinMinTerm() == 1
					&& financeType.getFinMaxTerm() == 1
					&& (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, afinanceDetail.getFinScheduleData()
							.getFinanceMain().getProductCategory()))) {
				if (!financeType.isFinRepayPftOnFrq()) {
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				} else {
					this.rpyPftFrqRow.setVisible(true);
					this.hbox_finRepayPftOnFrq.setVisible(true);
				}

				if (afinanceDetail.getFinScheduleData().getFinanceMain().isStepFinance()) {
					this.row_stepFinance.setVisible(true);
					this.row_stepType.setVisible(true);
					if (afinanceDetail.getFinScheduleData().getFinanceMain().isAlwManualSteps()) {
						this.row_manualSteps.setVisible(true);
					}
				} else {
					this.row_stepFinance.setVisible(false);
					this.row_manualSteps.setVisible(false);
					this.row_stepType.setVisible(false);
				}

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				this.noOfTermsRow.setVisible(false);
			}

			doStoreServiceIds(afinanceDetail.getFinScheduleData().getFinanceMain());
			setDialog(DialogType.EMBEDDED);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceMaintenanceDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws Exception {
		logger.debug("Entering");

		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		try {
			this.repayAcctId.getValidatedValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!isOverdraft) {
				this.finAmount.getValidateValue();
			} else {
				this.odFinAssetValue.getValidateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			try {
				this.finWriteoffPayAccount.getValidatedValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				this.finWriteoffPayAmount.getValidateValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");
		if (onLoadProcess) {
			showErrorDetails(wve, financeTypeDetailsTab);
		}
		wve = null;

		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

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
	public FinanceDetail onExecuteStageAccDetail() throws InterruptedException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");
		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.disbAcctId.isReadonly()) {
				this.disbAcctId.validateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.repayAcctId.isReadonly()) {
				this.repayAcctId.validateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.downPayAccount.isReadonly()) {
				this.downPayAccount.validateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, financeTypeDetailsTab);
		wve = null;
		doWriteComponentsToBean(getFinanceDetail());
		getFinanceDetail().setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);
		logger.debug("Leaving");
		return getFinanceDetail();
	}

	/**
	 * Method for Executing Accounting tab Rules
	 * 
	 * @throws InterfaceException
	 * @throws Exception
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception {
		logger.debug("Entering");
		if (StringUtils.isNotBlank(this.custCIF.getValue())) {

			if (onLoadProcess) {
				doWriteComponentsToBean(getFinanceDetail());
			}

			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			int format = CurrencyUtil.getFormat(finMain.getFinCcy());

			Date curBDay = DateUtility.getAppDate();
			aeEvent = AEAmounts.procAEAmounts(finMain, getFinanceDetail().getFinScheduleData()
					.getFinanceScheduleDetails(), new FinanceProfitDetail(), eventCode, curBDay, curBDay);
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

			// AmountCodes Setting the FinwriteoffPayAmount
			amountCodes.setWoPayAmt(PennantApplicationUtil.unFormateAmount(this.finWriteoffPayAmount.getActualValue(),
					format));

			HashMap<String, Object> dataMap = aeEvent.getDataMap();
			dataMap = amountCodes.getDeclaredFieldValues();

			List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

			// Loop Repetition for Multiple Disbursement

			List<ReturnDataSet> returnSetEntries = null;
			Map<String, FeeRule> map = null;

			dataMap.putAll(map);
			aeEvent.setDataMap(dataMap);
			aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);

			returnSetEntries = aeEvent.getReturnDataSet();
			accountingSetEntries.addAll(returnSetEntries);
			getFinanceDetail().setReturnDataSetList(accountingSetEntries);
			if (getAccountingDetailDialogCtrl() != null) {
				getAccountingDetailDialogCtrl().doFillAccounting(accountingSetEntries);
			}

		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Returning Fiannce Amount and Currency Data for Contributor Validation
	 * 
	 * @return
	 */
	public List<Object> prepareContributor() {
		logger.debug("Entering");
		List<Object> list = new ArrayList<Object>();

		this.finAmount.setConstraint("");
		this.finCcy.setConstraint("");
		this.finAmount.setErrorMessage("");
		this.finCcy.setErrorMessage("");

		list.add(this.finAmount.getActualValue());
		list.add(this.finCcy.getValue());
		logger.debug("Leaving");
		return list;

	}

	/*
	 * // Helpers
	 *//**
	 * Method for Checking Details whether Fees Are reexecute or not
	 */
	private void doCheckFeeReExecution() {

		isFeeReExecute = false;

		if (this.finRepayMethod.getSelectedIndex() != this.oldVar_finRepayMethod) {
			isFeeReExecute = true;
		}

	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.downPaySupl.setValue(this.oldVar_downPaySupl);
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
		this.finLimitRef.setValue(this.oldVar_finLimitRef);
		this.depreciationFrq.setValue(this.oldVar_depreciationFrq);
		this.finIsActive.setChecked(this.oldVar_finIsActive);
		this.finPurpose.setValue(this.oldVar_finPurpose);
		this.finPurpose.setDescription(this.oldVar_lovDescFinPurpose);
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			this.finWriteoffPayAccount.setValue(this.oldVar_finWriteoffPayAccount);
			this.finWriteoffPayAmount.setValue(this.oldVar_finWriteoffPayAmount);
		}
		// Step Finance Details
		this.stepFinance.setChecked(this.oldVar_stepFinance);
		this.stepPolicy.setValue(this.oldVar_stepPolicy);
		this.alwManualSteps.setChecked(this.oldVar_alwManualSteps);
		this.noOfSteps.setValue(this.oldVar_noOfSteps);
		this.stepType.setSelectedIndex(this.oldVar_stepType);

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.graceTerms.setValue(this.oldVar_graceTerms);
			this.cbGrcSchdMthd.setSelectedIndex(this.oldVar_grcSchdMthd);
			this.grcRateBasis.setSelectedIndex(this.oldVar_grcRateBasis);
			this.allowGrcRepay.setChecked(this.oldVar_allowGrcRepay);
			this.graceRate.setBaseValue(this.oldVar_graceBaseRate);
			this.graceRate.setBaseDescription(this.oldVar_lovDescGraceBaseRateName);
			this.graceRate.setSpecialValue(this.oldVar_graceSpecialRate);
			this.graceRate.setSpecialDescription(this.oldVar_lovDescGraceSpecialRateName);
			this.gracePftRate.setValue(this.oldVar_gracePftRate);
			this.gracePftFrq.setValue(this.oldVar_gracePftFrq);
			this.nextGrcPftDate_two.setValue(this.oldVar_nextGrcPftDate);
			this.gracePftRvwFrq.setValue(this.oldVar_gracePftRvwFrq);
			this.nextGrcPftRvwDate_two.setValue(this.oldVar_nextGrcPftRvwDate);
			this.graceCpzFrq.setValue(this.oldVar_graceCpzFrq);
			this.nextGrcCpzDate_two.setValue(this.oldVar_nextGrcCpzDate);
			this.graceRate.setMarginValue(this.oldVar_grcMargin);
			this.grcPftDaysBasis.setSelectedIndex(this.oldVar_grcPftDaysBasis);
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.repayRateBasis.setSelectedIndex(this.oldVar_repayRateBasis);
		this.repayRate.setBaseValue(this.oldVar_repayBaseRate);
		this.repayRate.setBaseDescription(this.oldVar_lovDescRepayBaseRateName);
		this.repayRate.setSpecialValue(this.oldVar_repaySpecialRate);
		this.repayRate.setSpecialDescription(this.oldVar_lovDescRepaySpecialRateName);
		this.repayProfitRate.setValue(this.oldVar_repayProfitRate);
		this.repayRate.setMarginValue(this.oldVar_repayMargin);
		this.cbScheduleMethod.setSelectedIndex(this.oldVar_scheduleMethod);
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

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
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
		if (super.isDataChanged(close)) {
			return true;
		}
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldDwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			BigDecimal oldFinwriteoffPayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finWriteoffPayAmount,
					formatter);
			BigDecimal newFinwriteoffPayAmount = PennantAppUtil.unFormateAmount(
					this.finWriteoffPayAmount.getActualValue(), formatter);
			if (oldFinwriteoffPayAmount.compareTo(newFinwriteoffPayAmount) != 0) {
				return true;
			}
			if (!StringUtils.equals(this.oldVar_finWriteoffPayAccount, this.finWriteoffPayAccount.getValue())) {
				return true;
			}
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

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}
		// FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && !financeType.isFinIsGenRef()) {

			this.finReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_FinReference.value"), null, true));
		}

		if (!this.finAmount.isReadonly() && !isOverdraft) {
			this.finAmount.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinanceMainDialog_FinAmount.value"), 0, true, false));
		}
		if (!this.defferments.isReadonly()) {
			this.defferments.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_FinanceMainDialog_Defferments.value"), false, false));
		}

		if (!this.planDeferCount.isReadonly()) {
			this.planDeferCount.setConstraint(new PTNumberValidator(Labels
					.getLabel("label_FinanceMainDialog_PlanDeferCount.value"), false, false));
		}

		if (this.finDivision.equals(FinanceConstants.FIN_DIVISION_RETAIL) && !this.accountsOfficer.isReadonly()
				&& !recSave && !buildEvent) {
			this.accountsOfficer.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_AccountsOfficer.value"), null, true, true));
		}

		if (this.finDivision.equals(FinanceConstants.FIN_DIVISION_RETAIL) && !this.dsaCode.isReadonly() && !recSave
				&& !buildEvent) {
			this.dsaCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_DSACode.value"),
					null, true, true));
		}

		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {

			if (!this.writeoffDate.isDisabled()) {
				this.writeoffDate.setConstraint(new PTDateValidator(Labels
						.getLabel("label_FinanceMainDialog_WriteoffDate.value"), false, appStartDate, DateUtility
						.getAppDate(), true));
			}

			if (!recSave && this.finWriteoffPayAccount.isMandatory()) {
				this.finWriteoffPayAccount.setConstraint(new PTStringValidator(Labels
						.getLabel("label_FinanceMaintenanceDialog_finWriteoffPayAccount.value"), null, true));
			}

			if (!this.finWriteoffPayAmount.isReadonly()) {
				this.finWriteoffPayAmount.setConstraint(new PTDecimalValidator(Labels
						.getLabel("label_FinanceMaintenanceDialog_finWriteoffPayAmount.value"), 0, true, false));
			}
		}

		if (isOverdraft) {
			this.odFinAssetValue.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"), format, true, false));
			this.finAssetValue.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"), format, true, false));
		}

		if (this.row_FinAssetValue.isVisible()) {
			if (this.finAssetValue.isVisible() && !this.finAssetValue.isReadonly()) {
				this.finAssetValue.setConstraint(new PTDecimalValidator(label_FinanceMainDialog_FinAssetValue
						.getValue(), format, true, false));
			}
			if (this.finCurrentAssetValue.isVisible() && !this.finCurrentAssetValue.isReadonly()) {
				this.finCurrentAssetValue.setConstraint(new PTDecimalValidator(
						this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(), format, false, false));
			}
		}
		// FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.graceTerms.isReadonly()) {
				this.graceTerms.setConstraint(new PTStringValidator(Labels
						.getLabel("label_FinanceMainDialog_GraceTerms.value"), null, true));
			}

			if (!this.graceRate.isMarginReadonly()) {
				this.graceRate.setMarginConstraint(new PTDecimalValidator(Labels
						.getLabel("label_FinanceMainDialog_GraceMargin.value"), 9, false));
			}

			if (this.allowGrace.isChecked()) {
				this.graceRate.getEffRateComp()
						.setConstraint(
								new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"),
										9, false));
			}

			if (!this.nextGrcPftDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint(new PTDateValidator(Labels
						.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"), true));
			}

			if (!this.nextGrcPftRvwDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(new PTDateValidator(Labels
						.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"), true));
			}
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.rpyFrqRow.isVisible() && !this.nextRepayDate.isReadonly()
				&& StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate_two.setConstraint(new PTDateValidator(Labels
					.getLabel("label_FinanceMainDialog_NextRepayDate.value"), true));
		}

		if (this.rpyPftFrqRow.isVisible() && !this.nextRepayPftDate.isReadonly()
				&& StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setConstraint(new PTDateValidator(Labels
					.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), true));
		}

		if (this.rpyRvwFrqRow.isVisible() && !this.nextRepayRvwDate.isReadonly()
				&& StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate_two.setConstraint(new PTDateValidator(Labels
					.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"), true));
		}

		if (this.rpyCpzFrqRow.isVisible() && !this.nextRepayCpzDate.isReadonly()
				&& StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate_two.setConstraint(new PTDateValidator(Labels
					.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"), true));
		}

		this.repayRate.getEffRateComp().setConstraint(
				new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), 9, false));

		if (!this.repayRate.isMarginReadonly()) {
			this.repayRate.setMarginConstraint(new PTDecimalValidator(Labels
					.getLabel("label_FinanceMainDialog_RepayMargin.value"), 9, false));
		}

		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {

			this.maturityDate_two.setConstraint(new PTDateValidator(Labels
					.getLabel("label_FinanceMainDialog_MaturityDate.value"), true));
		}

		if (this.applyODPenalty.isChecked()) {

			if (!this.oDGraceDays.isReadonly()) {
				this.oDGraceDays.setConstraint(new PTNumberValidator(Labels
						.getLabel("label_FinanceMainDialog_ODGraceDays.value"), false, false));
			}

			if (!this.oDChargeAmtOrPerc.isDisabled()) {
				if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc
							.setConstraint(new PTDecimalValidator(Labels
									.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"), format, true, false,
									9999999));
				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setConstraint(new PTDecimalValidator(Labels
							.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"), 2, true, false, 100));
				}
			}

			if (!this.oDMaxWaiverPerc.isReadonly()) {
				this.oDMaxWaiverPerc.setConstraint(new PTDecimalValidator(Labels
						.getLabel("label_FinanceMainDialog_ODMaxWaiver.value"), 2, true, false, 100));
			}
		}

		if (this.gb_ddaRequest.isVisible()) {
			if (!recSave && !buildEvent) {
				if (!this.bankName.isReadonly()) {
					this.bankName.setConstraint(new PTStringValidator(Labels
							.getLabel("label_FinanceMaintenanceDialog_BankName.value"), null, true, true));
				}
				if (!this.iban.isReadonly()) {
					this.iban.setConstraint(new PTStringValidator(Labels
							.getLabel("label_FinanceMaintenanceDialog_IBAN.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL23, true));
				}
				if (this.hbox_Finance_IfscCode.isVisible() && !this.ifscCode.isReadonly()) {
					this.ifscCode.setConstraint(new PTStringValidator(Labels
							.getLabel("label_FinanceMaintenanceDialog_IBAN.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);

		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setConstraint("");
		this.cbProfitDaysBasis.setConstraint("");
		this.finRemarks.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finContractDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayBank.setConstraint("");
		this.downPaySupl.setConstraint("");
		this.downPayAccount.setConstraint("");
		// M_ this.custID.setConstraint("");
		this.defferments.setConstraint("");
		this.planDeferCount.setConstraint("");
		this.finBranch.setConstraint("");
		this.disbAcctId.setConstraint("");
		this.repayAcctId.setConstraint("");
		this.commitmentRef.setConstraint("");
		this.finLimitRef.setConstraint("");
		this.writeoffDate.setConstraint("");
		this.finWriteoffPayAccount.setConstraint("");
		this.finWriteoffPayAmount.setConstraint("");
		this.accountsOfficer.setConstraint("");
		this.dsaCode.setConstraint("");
		this.referralId.setConstraint("");
		this.dmaCode.setConstraint("");
		this.salesDepartment.setConstraint("");
		this.applicationNo.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setConstraint("");
		this.graceTerms.setConstraint("");
		this.gracePeriodEndDate.setConstraint("");
		this.cbGrcSchdMthd.setConstraint("");
		this.gracePftRate.setConstraint("");
		this.graceRate.getEffRateComp().setConstraint("");
		this.graceRate.setMarginConstraint("");
		this.nextGrcPftDate.setConstraint("");
		this.nextGrcPftRvwDate.setConstraint("");
		this.nextGrcCpzDate.setConstraint("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.finRepaymentAmount.setConstraint("");
		this.repayProfitRate.setConstraint("");
		this.repayRate.getEffRateComp().setConstraint("");
		this.repayRate.setMarginConstraint("");
		this.cbScheduleMethod.setConstraint("");
		this.nextRepayDate.setConstraint("");
		this.nextRepayPftDate.setConstraint("");
		this.nextRepayRvwDate.setConstraint("");
		this.nextRepayCpzDate.setConstraint("");
		this.maturityDate.setConstraint("");
		this.maturityDate_two.setConstraint("");
		this.finRepayMethod.setConstraint("");

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		this.oDChargeCalOn.setConstraint("");
		this.oDChargeType.setConstraint("");
		this.oDChargeAmtOrPerc.setConstraint("");
		this.oDMaxWaiverPerc.setConstraint("");

		// FinanceMain Details Tab ---> 5. DDA Registration Details
		this.bankName.setConstraint("");
		this.iban.setConstraint("");
		this.ifscCode.setConstraint("");
		this.accountType.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to set validation on LOV fields
	 * */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint(new PTStringValidator(Labels
				.getLabel("label_FinanceMainDialog_FinType.value"), null, true));

		this.finCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinCcy.value"), null,
				true));

		if (!this.finBranch.isReadonly()) {
			this.finBranch.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_FinBranch.value"), null, true));
		}

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustID.value"),
					null, true));
		}

		if (this.disbAcctId.isMandatory() && !this.disbAcctId.isReadonly()) {
			this.disbAcctId.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_DisbAcctId.value"), null, true));
		}

		if (this.repayAcctId.isMandatory() && !this.repayAcctId.isReadonly()) {
			this.repayAcctId.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_RepayAcctId.value"), null, true));
		}

		if (this.downPayAccount.isMandatory() && !this.downPayAccount.isReadonly()) {
			this.downPayAccount.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMaintenanceDialog_DownPayAccount.value"), null, true));
		}

		if (!this.commitmentRef.isReadonly()) {
			this.commitmentRef.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_CommitRef.value"), null, true));
		}

		if (!this.finLimitRef.isReadonly()) {
			this.finLimitRef.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_FinLimitRef.value"), null, true));
		}

		if (!this.finPurpose.isReadonly()) {
			this.finPurpose.setConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_FinPurpose.value"), null, true));
		}

		// FinanceMain Details Tab ---> 2. Grace Period Details

		if (!this.graceRate.isBaseReadonly()) {
			this.graceRate.setBaseConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_GraceBaseRate.value"), null, true));
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.repayRate.isBaseReadonly()) {
			this.repayRate.setBaseConstraint(new PTStringValidator(Labels
					.getLabel("label_FinanceMainDialog_RepayBaseRate.value"), null, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 * **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");
		this.finBranch.setConstraint("");
		this.custCIF.setConstraint("");
		this.commitmentRef.setConstraint("");
		this.finLimitRef.setConstraint("");
		this.finPurpose.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages.
	 * */
	public void doClearMessage() {
		logger.debug("Entering");
		super.doClearMessage();
		this.writeoffDate.setErrorMessage("");
		this.finWriteoffPayAccount.setErrorMessage("");
		this.finWriteoffPayAmount.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a financeMain object from database.<br>
	 * 
	 * @throws Exception
	 */
	private void doDelete() throws Exception {
		logger.debug("Entering");

		FinanceDetail afinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), afinanceDetail);

		String tranType = PennantConstants.TRAN_WF;

		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		afinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ afinanceMain.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(afinanceMain.getRecordType())) {
				afinanceMain.setVersion(afinanceMain.getVersion() + 1);
				afinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					afinanceMain.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
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

					// Mail Alert Notification for Customer/Dealer/Provider...etc
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

						List<String> templateTyeList = new ArrayList<String>();
						templateTyeList.add(NotificationConstants.TEMPLATE_FOR_AE);
						templateTyeList.add(NotificationConstants.TEMPLATE_FOR_CN);

						List<ValueLabel> referenceIdList = getFinanceReferenceDetailService().getTemplateIdList(
								afinanceMain.getFinType(), moduleDefiner, getRole(), templateTyeList);

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
									&& getFinanceDetail().getCustomerDetails().getCustomerEMailList() != null
									&& !getFinanceDetail().getCustomerDetails().getCustomerEMailList().isEmpty()) {

								List<CustomerEMail> emailList = getFinanceDetail().getCustomerDetails()
										.getCustomerEMailList();
								List<String> custMailIdList = new ArrayList<String>();
								for (CustomerEMail customerEMail : emailList) {
									custMailIdList.add(customerEMail.getCustEMail());
								}
								if (!custMailIdList.isEmpty()) {
									mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, custMailIdList);
								}
							}

							getMailUtil().sendMail(notificationIdlist, getFinanceDetail(), mailIDMap, null);
						}

					}

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
	public void doEdit() {
		logger.debug("Entering");
		super.doEdit();
		this.downPaySupl.setReadonly(true);
		this.applicationNo.setReadonly(true);
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (StringUtils.equals(financeType.getProductCategory(), FinanceConstants.PRODUCT_ODFACILITY)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
		}
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired()
				&& getFinanceDetail().getFinScheduleData().getFinanceMain().getMinDownPayPerc()
						.compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setReadonly(isReadOnly("FinanceMainDialog_downPayment"));
			this.downPaySupl.setReadonly(isReadOnly("FinanceMainDialog_downPaySupl"));
			this.downPayAccount.setReadonly(isReadOnly("FinanceMainDialog_downPaymentAcc"));
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_finStartDate"), this.odStartDate);
		this.odFinAssetValue.setDisabled(isReadOnly("FinanceMainDialog_finAmount"));

		this.finWriteoffPayAmount.setReadonly(isReadOnly("FinanceMainDialog_WriteoffPayAmount"));
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_WRITEOFFPAY)) {
			this.finWriteoffPayAmount.setMandatory(!isReadOnly("FinanceMainDialog_WriteoffPayAmount"));
			this.finWriteoffAmount.setReadonly(true);
			this.finWriteoffPaidAmount.setReadonly(true);
			this.writeoffDate.setReadonly(true);
		}
		this.finWriteoffPayAccount.setReadonly(isReadOnly("FinanceMainDialog_WriteoffPayAccount"));
		this.flagDetails.setReadonly(true);
		this.btnFlagDetails.setVisible(!isReadOnly("FinanceMainDialog_flagDetails"));
		logger.debug("Leaving");
	}

	/**
	 * Overridden method for Inactive case checking
	 */
	public boolean isReadOnly(String rightName) {

		if (!getFinanceDetail().getFinScheduleData().getFinanceMain().isFinIsActive()) {
			return true;
		}
		return super.isReadOnly(rightName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		super.doReadOnly();
		this.downPaySupl.setReadonly(true);
		this.finAssetValue.setReadonly(true);
		this.finWriteoffPayAccount.setReadonly(true);
		this.finWriteoffPayAmount.setReadonly(true);
		this.manualSchedule.setDisabled(true);
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		super.doClear();
		this.downPaySupl.setValue("");
		this.finWriteoffPayAccount.setValue("");
		this.finWriteoffPayAmount.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		boolean isNew = false;
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

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail);

		// Document Details Saving
		if (getDocumentDetailDialogCtrl() != null) {
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		isNew = aFinanceDetail.isNew();

		// Collateral Flags
		fetchFlagDetals();
		if (getFinFlagsDetailList() != null && !getFinFlagsDetailList().isEmpty()) {
			aFinanceDetail.setFinFlagsDetails(getFinFlagsDetailList());
		} else {
			aFinanceDetail.setFinFlagsDetails(null);
		}

		if (aFinanceDetail.getFinFlagsDetails() != null && !aFinanceDetail.getFinFlagsDetails().isEmpty()) {
			for (FinFlagsDetail flagsDetail : finFlagsDetailList) {
				if (StringUtils.isNotBlank(flagsDetail.getRecordType())) {
					flagsDetail.setReference(aFinanceDetail.getFinReference());
					flagsDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
					flagsDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					flagsDetail.setTaskId(taskId);
					flagsDetail.setNextTaskId(nextTaskId);
					flagsDetail.setRoleCode(getRole());
					flagsDetail.setNextRoleCode(nextRoleCode);
				}
			}
		}

		// Finance Stage Accounting Details Tab
		if (!recSave && getStageAccountingDetailDialogCtrl() != null) {
			// check if accounting rules executed or not
			if (!getStageAccountingDetailDialogCtrl().stageAccountingsExecuted) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
				return;
			}
			if (getStageAccountingDetailDialogCtrl().stageDisbCrSum
					.compareTo(getStageAccountingDetailDialogCtrl().stageDisbDrSum) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return;
			}
		} else {
			aFinanceDetail.setStageAccountingList(null);
		}

		aFinanceDetail.setCustomerEligibilityCheck(prepareCustElgDetail(false).getCustomerEligibilityCheck());

		if (getFinanceCheckListReferenceDialogCtrl() != null
				&& (getFinanceDetail().getFinRefDetailsList() == null || getFinanceDetail().getFinRefDetailsList()
						.isEmpty())) {
			getFinanceDetail().setCustomerEligibilityCheck(prepareCustElgDetail(false).getCustomerEligibilityCheck());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), true);
		}
		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(getFinanceDetail(), false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Guaranteer Details Tab ---> Guaranteer Details
		if (getJointAccountDetailDialogCtrl() != null) {
			if (getJointAccountDetailDialogCtrl().getGuarantorDetailList() != null
					&& getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail);
			}
			if (getJointAccountDetailDialogCtrl().getJountAccountDetailList() != null
					&& getJointAccountDetailDialogCtrl().getJountAccountDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_JointAccountDetail(aFinanceDetail);
			}
		} else {
			aFinanceDetail.setJountAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}

		// Internal Collateral Assignment Details
		if (getCollateralHeaderDialogCtrl() != null) {

			// Validate Assigned Collateral Value
			if (!recSave
					&& (getFinanceDetail().getFinScheduleData().getFinanceType().isFinCollateralReq() || !getCollateralHeaderDialogCtrl()
							.getCollateralAssignments().isEmpty())) {

				BigDecimal utilizedAmt = BigDecimal.ZERO;
				for (FinanceDisbursement curDisb : getFinanceDetail().getFinScheduleData().getDisbursementDetails()) {
					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
						continue;
					}
					utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount()).add(
							aFinanceMain.getFeeChargeAmt().add(aFinanceMain.getInsuranceAmt()));
				}
				utilizedAmt = utilizedAmt.subtract(aFinanceMain.getDownPayment()).subtract(
						aFinanceMain.getFinRepaymentAmount());

				boolean isValid = getCollateralHeaderDialogCtrl().validCollateralValue(utilizedAmt);
				if (!isValid) {
					MessageUtil.showError(Labels.getLabel("label_CollateralAssignment_InSufficient"));
					return;
				}
			}

			aFinanceDetail.setCollateralAssignmentList(getCollateralHeaderDialogCtrl().getCollateralAssignments());
			aFinanceDetail.setFinAssetTypesList(getCollateralHeaderDialogCtrl().getFinAssetTypes());
			aFinanceDetail.setExtendedFieldRenderList(getCollateralHeaderDialogCtrl().getExtendedFieldRenderList());

		} else {
			aFinanceDetail.setCollateralAssignmentList(null);
			aFinanceDetail.setFinAssetTypesList(null);
			aFinanceDetail.setExtendedFieldRenderList(null);
		}

		// Finance Collateral Details validating & Saving
		if (getFinCollateralHeaderDialogCtrl() != null) {
			BigDecimal totCost = BigDecimal.ZERO;
			boolean isFDAmount = false;
			for (FinCollaterals finCollateral : getFinCollateralHeaderDialogCtrl().getFinCollateralDetailsList()) {
				totCost = totCost.add(finCollateral.getValue() == null ? BigDecimal.ZERO : finCollateral.getValue());
				if (StringUtils.equals(finCollateral.getCollateralType(), PennantConstants.FIXED_DEPOSIT)) {
					isFDAmount = true;
				}
			}

			if (!getFinCollateralHeaderDialogCtrl().getFinCollateralDetailsList().isEmpty()
					&& totCost.compareTo(aFinanceMain.getFinAmount()) < 0 && isFDAmount) {
				if (tabsIndexCenter.getFellowIfAny("finCollateralsTab") != null) {
					if (tabsIndexCenter.getFellowIfAny("finCollateralsTab") instanceof Tab) {
						Tab collateralTab = (Tab) tabsIndexCenter.getFellowIfAny("finCollateralsTab");
						collateralTab.setSelected(true);
					}
				}
				MessageUtil.showError(Labels.getLabel("label_Collateral_FDAmount"));
				return;
			}
			aFinanceDetail.setFinanceCollaterals(getFinCollateralHeaderDialogCtrl().getFinCollateralDetailsList());
		} else {
			aFinanceDetail.setFinanceCollaterals(null);
		}

		// save the FinanceMain Extension details
		if (StringUtils.equals(getComboboxValue(this.finRepayMethod), FinanceConstants.REPAYMTH_AUTODDA)
				&& !StringUtils.isBlank(this.repayAcctId.getValue()) && !this.repayAcctId.isReadonly()) {

			FinanceMainExt financeMainExt = new FinanceMainExt();
			financeMainExt.setFinReference(aFinanceMain.getFinReference());
			financeMainExt.setRepayIBAN(this.repayAcctId.getSelectedAccount().getIban());
			financeMainExt.setIfscCode(this.ifscCode.getValue());
			getFinanceMainExtService().saveFinanceMainExtDetails(financeMainExt);
		}

		if (isFeeReExecute) {
			String message = Labels.getLabel("label_FeeExecute");
			Messagebox.show(message, Labels.getLabel("message.Information"), Messagebox.OK, Messagebox.INFORMATION);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
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

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// Mail Alert Notification for Customer/Dealer/Provider...etc
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
								&& aFinanceDetail.getCustomerDetails().getCustomerEMailList() != null
								&& !aFinanceDetail.getCustomerDetails().getCustomerEMailList().isEmpty()) {

							List<CustomerEMail> emailList = aFinanceDetail.getCustomerDetails().getCustomerEMailList();
							List<String> custMailIdList = new ArrayList<String>();
							for (CustomerEMail customerEMail : emailList) {
								custMailIdList.add(customerEMail.getCustEMail());
							}
							if (!custMailIdList.isEmpty()) {
								mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, custMailIdList);
							}
						}

						getMailUtil().sendMail(notificationIdlist, aFinanceDetail, mailIDMap, null);
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
				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			}

		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onSelectCheckListDetailsTab(ForwardEvent event) throws ParseException, InterruptedException,
			IllegalAccessException, InvocationTargetException {

		this.doWriteComponentsToBean(getFinanceDetail());

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
	 * @throws IOException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) throws InterruptedException, IOException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		// Setting workflow details to Finance Flags
		List<FinFlagsDetail> flagList = aFinanceDetail.getFinFlagsDetails();
		if (flagList != null && !flagList.isEmpty()) {
			for (int i = 0; i < flagList.size(); i++) {
				FinFlagsDetail finFlagsDetail = flagList.get(i);
				finFlagsDetail.setReference(afinanceMain.getFinReference());
				finFlagsDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
				finFlagsDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finFlagsDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
				finFlagsDetail.setRecordStatus(afinanceMain.getRecordStatus());
				finFlagsDetail.setWorkflowId(afinanceMain.getWorkflowId());

			}
		}
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

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_DDAMaintenance)) {

					if (FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN.equals(moduleDefiner)) {
						// Validate DDA process
						// =====================
						try {
							processCompleted = doDDAProcess(aFinanceDetail);
						} catch (InterfaceException pfe) {
							MessageUtil.showError(pfe);
							processCompleted = false;
						}
					} else {
						processCompleted = true;
					}
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckCollaterals)) {

					if (FinanceConstants.FINSER_EVENT_BASICMAINTAIN.equals(moduleDefiner)) {
						// Validate Collateral details and process
						// ========================================
						try {
							processCompleted = doCollateralProcess(aFinanceDetail);
						} catch (InterfaceException pfe) {
							MessageUtil.showError(pfe);
							processCompleted = false;
						}
					} else {
						processCompleted = true;
					}

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
					FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					// tFinanceDetail = doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),
						finishedTasks);

			}

			FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					// doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			// doProcess_Assets(aFinanceDetail);
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for process collateral data and send Mark , De-Mark request to midlleware
	 * 
	 * @param financeDetail
	 * @throws InterfaceException
	 */
	private boolean doCollateralProcess(FinanceDetail financeDetail) throws InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = true;

		// Validate collateral marking
		List<FinCollaterals> collateralList = financeDetail.getFinanceCollaterals();

		List<FinCollateralMark> prvCollateralList = new ArrayList<FinCollateralMark>();

		// fetch previous collateral List for the finance
		String finReference = financeDetail.getFinScheduleData().getFinReference();
		prvCollateralList = getCollateralMarkProcess().getCollateralList(finReference);

		List<FinCollaterals> collateralMarkList = new ArrayList<FinCollaterals>();

		if (collateralList != null && !collateralList.isEmpty() && prvCollateralList != null
				&& !prvCollateralList.isEmpty()) {
			// Compare prvCollateral List and current Collateral List to find Mark collateral
			for (FinCollaterals collateral : collateralList) {
				for (FinCollateralMark prvCollateral : prvCollateralList) {
					if (!StringUtils.equals(collateral.getReference(), prvCollateral.getDepositID())) {
						collateralMarkList.add(collateral);
					} else {
						break;
					}
				}
			}

			// Send Collateral Mark request to middleware

			List<FinCollateralMark> collatDeMarkList = new ArrayList<FinCollateralMark>();

			// Compare prvCollateral List and current Collateral List to find DE-Mark collateral
			for (FinCollateralMark prvCollateral : prvCollateralList) {
				for (FinCollaterals collateral : collateralList) {
					if (!StringUtils.equals(collateral.getReference(), prvCollateral.getDepositID())) {
						collatDeMarkList.add(prvCollateral);
					} else {
						break;
					}
				}
			}

			// send Collateral mark request to middleware
			getCollateralMarkProcess().markCollateral(collateralMarkList);

			// send Collateral de-mark request to middleware
			getCollateralMarkProcess().doCollateralDemark(collatDeMarkList);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for validate Repay Method and take below actions<br>
	 * 1. PDC-->DDA(DDA Registration)<br>
	 * 2. STL-->DDA(DDA Registration)<br>
	 * 3. DDA-->PDC(DDA Cancellation)<br>
	 * 4. DDA-->STL(DDA Cancellation)<br>
	 * 5. DDA-->DDA(DDA Re-Registartion)
	 * 
	 * @param financeDetail
	 * @throws InterfaceException
	 * @throws InterruptedException
	 */
	private boolean doDDAProcess(FinanceDetail financeDetail) throws InterfaceException, InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String finReference = financeMain.getFinReference();
		String reqType = PennantConstants.REQ_TYPE_REG;

		// fetch existing DDA registration details
		DDAProcessData ddaProcessData = getDdaProcessService().getDDADetailsById(finReference, reqType);

		if (ddaProcessData != null) {
			if (!StringUtils.equals(financeMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTODDA)) {

				MultiLineMessageBox.doSetTemplate();
				String message = Labels.getLabel("REPAY_DDA_" + financeMain.getFinRepayMethod());

				int conf = MultiLineMessageBox.show(message, Labels.getLabel("DDA_CAN_TITLE"), MultiLineMessageBox.YES
						| MultiLineMessageBox.NO, Messagebox.QUESTION, true);

				if (conf == MultiLineMessageBox.YES) {
					getDdaControllerService().cancelDDARegistration(finReference);
					processCompleted = true;
				} else {
					processCompleted = false;
				}

			} else if (StringUtils.equals(financeMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTODDA)) {

				if (isDDADataChanged(ddaProcessData)) {
					MultiLineMessageBox.doSetTemplate();

					int conf = MultiLineMessageBox.show(Labels.getLabel("REPAY_DDA_DDA"),
							Labels.getLabel("DDA_REREG_TITLE"), MultiLineMessageBox.YES | MultiLineMessageBox.NO,
							Messagebox.QUESTION, true);

					if (conf == MultiLineMessageBox.YES) {

						// send DDA Cancellation request
						getDdaControllerService().cancelDDARegistration(finReference);

						// send DDA Registration request
						getDdaControllerService().doDDARequestProcess(financeDetail, false);

						processCompleted = true;
					} else {
						processCompleted = false;
					}
				} else {
					processCompleted = true;
				}
			}
		} else {
			if (StringUtils.equals(financeMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTODDA)) {
				// send DDA Registration request
				getDdaControllerService().doDDARequestProcess(financeDetail, false);
			}
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean isDDADataChanged(DDAProcessData ddaProcessData) {
		logger.debug("Entering");

		// Bank Name
		if (!StringUtils.equals(this.bankName.getValue(), ddaProcessData.getBankName())) {
			return true;
		}
		// Account Type
		if (!StringUtils.equals(getComboboxValue(this.accountType), ddaProcessData.getAccountType())) {
			return true;
		}
		// IBAN
		if (!StringUtils.equals(this.iban.getValue(), ddaProcessData.getIban())) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					auditHeader = getFinanceMaintenanceService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceMaintenanceService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceMaintenanceService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceMaintenanceDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceMaintenanceDialog, auditHeader);
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

		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getBaseObject();
			if (dataObject instanceof String) {
				this.graceRate.setBaseValue(dataObject.toString());
				this.graceRate.setBaseDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.graceRate.setBaseValue(details.getBRType());
					this.graceRate.setBaseDescription(details.getBRTypeDesc());
				}
			}
			calculateRate(this.graceRate.getBaseComp(), this.graceRate.getSpecialComp(), this.graceRate.getBaseComp(),
					this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getSpecialObject();
			if (dataObject instanceof String) {
				this.graceRate.setSpecialValue(dataObject.toString());
				this.graceRate.setSpecialDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.graceRate.setSpecialValue(details.getSRType());
					this.graceRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}
			calculateRate(this.graceRate.getBaseComp(), this.graceRate.getSpecialComp(), this.graceRate.getBaseComp(),
					this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.graceRate.getMarginValue() != null) {
				this.graceRate.setEffRateText(PennantApplicationUtil.formatRate(
						(this.graceRate.getEffRateValue().add(this.graceRate.getMarginValue())).doubleValue(), 2));
			}
		}

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
					PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
		} else {
			readOnlyComponent(true, this.cbGrcSchdMthd);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Allow/ Not Grace In Finance
	 * 
	 * @param event
	 */
	public void onCheck$allowGrace(Event event) {
		logger.debug("Entering" + event.toString());
		doAllowGraceperiod(true);
		logger.debug("Leaving" + event.toString());
	}

	// FinanceMain Details Tab ---> 3. Repayment Period Details

	/**
	 * when clicks on button "SearchRepayBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$repayRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.repayRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getBaseObject();

			if (dataObject instanceof String) {
				this.repayRate.setBaseValue(dataObject.toString());
				this.repayRate.setBaseDescription("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.repayRate.setBaseValue(details.getBRType());
					this.repayRate.setBaseDescription(details.getBRTypeDesc());
				}
			}

			calculateRate(this.repayRate.getBaseComp(), this.repayRate.getSpecialComp(), this.repayRate.getBaseComp(),
					this.repayRate.getMarginComp(), this.repayRate.getEffRateComp(), this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.repayRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.repayRate.setSpecialValue(dataObject.toString());
				this.repayRate.setSpecialDescription("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.repayRate.setSpecialValue(details.getSRType());
					this.repayRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			calculateRate(this.repayRate.getBaseComp(), this.repayRate.getSpecialComp(), this.repayRate.getBaseComp(),
					this.repayRate.getMarginComp(), this.repayRate.getEffRateComp(), this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.repayRate.getMarginValue() != null && !this.repayProfitRate.isReadonly()) {
				this.repayRate.setEffRateText(PennantApplicationUtil.formatRate(
						(this.repayRate.getEffRateValue().add(this.repayRate.getMarginValue())).doubleValue(), 2));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	// Overdue Penalty Details

	public void onCheck$applyODPenalty(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckODPenalty(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onCheckODPenalty(boolean checkAction) {
		if (this.applyODPenalty.isChecked()) {

			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeType"), this.oDChargeType);
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"), this.oDAllowWaiver);

			if (checkAction) {
				readOnlyComponent(true, this.oDChargeAmtOrPerc);
				readOnlyComponent(true, this.oDMaxWaiverPerc);
			} else {
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}

		} else {
			readOnlyComponent(true, this.oDIncGrcDays);
			readOnlyComponent(true, this.oDChargeType);
			this.oDGraceDays.setReadonly(true);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			readOnlyComponent(true, this.oDAllowWaiver);
			readOnlyComponent(true, this.oDMaxWaiverPerc);

			checkAction = true;
		}

		if (checkAction) {
			this.oDIncGrcDays.setChecked(false);
			if (this.applyODPenalty.isChecked()) {
				this.oDChargeType.setSelectedIndex(0);
				this.oDChargeCalOn.setSelectedIndex(0);
			}
			this.oDGraceDays.setValue(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
	}

	public void onChange$oDChargeType(Event event) {
		logger.debug("Entering" + event.toString());
		onChangeODChargeType(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onChangeODChargeType(boolean changeAction) {
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		if (changeAction) {
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}
		this.space_oDChargeAmtOrPerc.setSclass("mandatory");
		readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			this.space_oDChargeAmtOrPerc.setSclass("");
		} else if (getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT)
				|| getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			// this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(format));
			Clients.clearWrongValue(this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeCalOn);
			if (changeAction) {
				this.oDChargeCalOn.setSelectedIndex(0);
			}
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			// this.oDChargeAmtOrPerc.setMaxlength(6);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
	}

	public void onCheck$oDAllowWaiver(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckODWaiver(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onCheckODWaiver(boolean checkAction) {
		if (checkAction) {
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass("mandatory");
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
		} else {
			readOnlyComponent(true, this.oDMaxWaiverPerc);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
	}

	// OnBlur Events

	/**
	 * When user leaves finReference component
	 * 
	 * @param event
	 */
	public void onChange$finReference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		// doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user leave grace period end date component
	 * 
	 * @param event
	 */
	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		// doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");
		this.graceRate.getEffRateComp().setConstraint("");

		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);

		this.graceRate.setBaseValue("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialDescription("");
		readOnlyComponent(true, this.gracePftRate);
		this.graceRate.setEffRateText("0.00");

		this.gracePftRate.setText("0.00");

		if (!"#".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C.equals(this.grcRateBasis.getSelectedItem().getValue()
							.toString())) {
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialReadonly(true);

				this.graceRate.setBaseDescription("");
				this.graceRate.setSpecialDescription("");

				this.graceRate.setEffRateText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
			} else if (CalculationConstants.RATE_BASIS_R.equals(this.grcRateBasis.getSelectedItem().getValue()
					.toString())) {

				if (StringUtils
						.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate())) {
					this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				}

				this.graceRate.setEffRateText("0.00");
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

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");
		this.repayRate.getEffRateComp().setConstraint("");

		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);

		this.repayRate.setBaseValue("");
		this.repayRate.setBaseDescription("");
		this.repayRate.setSpecialValue("");
		this.repayRate.setSpecialDescription("");
		readOnlyComponent(true, this.repayProfitRate);
		this.repayRate.setEffRateText("0.00");
		this.repayProfitRate.setText("0.00");

		if (!"#".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
			if (CalculationConstants.RATE_BASIS_F.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C.equals(this.repayRateBasis.getSelectedItem().getValue()
							.toString())) {
				this.repayRate.setBaseReadonly(true);
				this.repayRate.setSpecialReadonly(true);

				this.repayRate.setBaseDescription("");
				this.repayRate.setSpecialDescription("");

				this.repayRate.setEffRateText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
			} else if (CalculationConstants.RATE_BASIS_R.equals(this.repayRateBasis.getSelectedItem().getValue()
					.toString())) {
				if (StringUtils.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate())) {
					this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
					this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
				}
				this.repayRate.setEffRateText("0.00");
				this.repayProfitRate.setText("0.00");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Setting Mandatory Check to Repay Account ID based on Repay Method
	 * 
	 * @param event
	 */
	public void onChange$finRepayMethod(Event event) {
		logger.debug("Entering" + event.toString());
		setRepayAccMandatory();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get the Finance Main Details from the Screen
	 */
	public FinanceMain getFinanceMain() {
		FinanceMain financeMain = super.getFinanceMain();
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		financeMain.setDownPayment(PennantAppUtil.unFormateAmount(
				this.downPayBank.getActualValue() == null ? BigDecimal.ZERO : this.downPayBank.getActualValue()
						.add(this.downPaySupl.getActualValue() == null ? BigDecimal.ZERO : this.downPaySupl
								.getActualValue()), format));
		financeMain.setFinAssetValue(PennantAppUtil.unFormateAmount(this.finAssetValue.getActualValue(), format));
		financeMain.setFinCurrAssetValue(PennantAppUtil.unFormateAmount(this.finCurrentAssetValue.getActualValue(),
				format));
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
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$viewCustInfo(Event event) {
		try {
			int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("custCIF", this.custCIF.getValue());
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter", format);
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						window_FinanceMaintenanceDialog, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						window_FinanceMaintenanceDialog, map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	/**
	 * Method for Rendering Finance Schedule Detail For Maintenance purpose
	 */
	public void reRenderScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		if (getScheduleDetailDialogCtrl() != null) {
			getScheduleDetailDialogCtrl().doFillScheduleList(aFinSchData);
		}
		logger.debug("Leaving");
	}

	/**
	 * To pass Data For Agreement Child Windows Used in reflection
	 * 
	 * @return
	 * @throws Exception
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		doWriteComponentsToBean(aFinanceDetail);

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, true);
			if (!validationSuccess) {
				return null;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		// Guaranteer Details Tab ---> Guaranteer Details
		if (getJointAccountDetailDialogCtrl() != null) {
			if (getJointAccountDetailDialogCtrl().getGuarantorDetailList() != null
					&& getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail);
			}
			if (getJointAccountDetailDialogCtrl().getJountAccountDetailList() != null
					&& getJointAccountDetailDialogCtrl().getJountAccountDetailList().size() > 0) {
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

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception
	 */
	protected boolean doSave_CheckList(FinanceDetail aFinanceDetail, boolean isForAgreementGen) throws Exception {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);
		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
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

	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding Flags into Multi Selection Extended box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnFlagDetails(Event event) {
		logger.debug("Entering  " + event.toString());

		Object dataObject = MultiSelectionSearchListBox.show(this.window, "Flag", this.flagDetails.getValue(), null);
		if (dataObject instanceof String) {
			this.flagDetails.setValue(dataObject.toString());
			this.flagDetails.setTooltiptext("");
		} else {
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.flagDetails.setValue(tempflagcode);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method Used for set list of values been class to components finance flags list
	 * 
	 * @param Collateral
	 */
	private void doFillFinFlagsList(List<FinFlagsDetail> finFlagsDetailList) {
		logger.debug("Entering");
		setFinFlagsDetailList(finFlagsDetailList);
		if (finFlagsDetailList == null || finFlagsDetailList.isEmpty()) {
			return;
		}

		String tempflagcode = "";
		for (FinFlagsDetail finFlagsDetail : finFlagsDetailList) {
			if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempflagcode)) {
					tempflagcode = finFlagsDetail.getFlagCode();
				} else {
					tempflagcode = tempflagcode.concat(",").concat(finFlagsDetail.getFlagCode());
				}
			}
		}
		this.flagDetails.setValue(tempflagcode);
		logger.debug("Entering");
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param finFlagsDetailList
	 */
	private void fetchFlagDetals() {
		logger.debug("Entering");

		List<String> finFlagList = Arrays.asList(this.flagDetails.getValue().split(","));

		if (this.finFlagsDetailList == null) {
			this.finFlagsDetailList = new ArrayList<>();
		}

		Map<String, FinFlagsDetail> flagMap = new HashMap<>();
		for (int i = 0; i < finFlagsDetailList.size(); i++) {
			FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
			flagMap.put(finFlagsDetail.getFlagCode(), finFlagsDetail);
		}

		for (String flagCode : finFlagList) {

			if (StringUtils.isEmpty(flagCode)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (flagMap.containsKey(flagCode)) {
				// Do Nothing

				//Removing from map to identify existing modifications
				flagMap.remove(flagCode);
			} else {
				FinFlagsDetail afinFlagsDetail = new FinFlagsDetail();
				afinFlagsDetail.setFlagCode(flagCode);
				afinFlagsDetail.setModuleName(FinanceConstants.MODULE_NAME);
				afinFlagsDetail.setNewRecord(true);
				afinFlagsDetail.setVersion(1);
				afinFlagsDetail.setRecordType(PennantConstants.RCD_ADD);

				this.finFlagsDetailList.add(afinFlagsDetail);
			}
		}

		//Removing unavailable records from DB by using Workflow details
		if (flagMap.size() > 0) {
			for (int i = 0; i < finFlagsDetailList.size(); i++) {
				FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
				if (flagMap.containsKey(finFlagsDetail.getFlagCode())) {

					if (StringUtils.isBlank(finFlagsDetail.getRecordType())) {
						finFlagsDetail.setNewRecord(true);
						finFlagsDetail.setVersion(finFlagsDetail.getVersion() + 1);
						finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else {
						if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
							finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$mandateRef(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = mandateRef.getObject();

		if (dataObject instanceof String) {
			this.mandateRef.setValue(dataObject.toString());
			this.mandateRef.setAttribute("mandateID", new Long(0));
		} else {
			Mandate details = (Mandate) dataObject;
			if (details != null) {
				this.mandateRef.setAttribute("mandateID", details.getMandateID());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	private void addMandateFiletrs(String repaymethod, long custid) {

		// 1.Mandate Swap is allowed after the registration process is completed 
		// 2.Mandate already tagged to the current loan should also made available.
		// 3.Open mandate should be made available even it is linked to another loan
		// 4.Mandate should be active
		// 5.For ECS registration not required

		repaymethod = StringUtils.trimToEmpty(repaymethod);
		Filter[] filters = new Filter[3];
		filters[0] = new Filter("CustID", custid, Filter.OP_EQUAL);
		filters[1] = new Filter("MandateType", repaymethod, Filter.OP_EQUAL);
		filters[2] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.mandateRef.setFilters(filters);
		StringBuilder whereCaluse = new StringBuilder("(OpenMandate = 1 OR");
		whereCaluse.append("((MANDATEID not in (SELECT MANDATEID FROM FINANCEMAIN WHERE FINANCEMAIN.CUSTID=");
		whereCaluse.append(custid);
		whereCaluse.append("AND FINANCEMAIN.FINREFERENCE != '");
		whereCaluse.append(getFinanceMain().getFinReference());
		whereCaluse.append("' )) OR MANDATEID IN (SELECT MANDATEID FROM FINANCEMAIN FM ");
		whereCaluse.append(" WHERE FM.FINREFERENCE='");
		whereCaluse.append(getFinanceMain().getFinReference());
		whereCaluse.append("')))");
		if (!MandateConstants.skipRegistration().contains(repaymethod)) {
			whereCaluse.append(" AND MANDATEREF IS NOT NULL ");
		}else{
			whereCaluse.append(" AND STATUS != '");
			whereCaluse.append(MandateConstants.STATUS_REJECTED);
			whereCaluse.append("'");
		}

		this.mandateRef.setWhereClause(whereCaluse.toString());

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMaintenanceService getFinanceMaintenanceService() {
		return financeMaintenanceService;
	}

	public void setFinanceMaintenanceService(FinanceMaintenanceService financeMaintenanceService) {
		this.financeMaintenanceService = financeMaintenanceService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public DDAProcessService getDdaProcessService() {
		return ddaProcessService;
	}

	public void setDdaProcessService(DDAProcessService ddaProcessService) {
		this.ddaProcessService = ddaProcessService;
	}

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

	public List<FinFlagsDetail> getFinFlagsDetailList() {
		return finFlagsDetailList;
	}

	public void setFinFlagsDetailList(List<FinFlagsDetail> finFlagsDetailList) {
		this.finFlagsDetailList = finFlagsDetailList;
	}

	public Label getLabel_FinanceMainDialog_PromoProduct() {
		return label_FinanceMainDialog_PromoProduct;
	}

	public void setLabel_FinanceMainDialog_PromoProduct(Label label_FinanceMainDialog_PromoProduct) {
		this.label_FinanceMainDialog_PromoProduct = label_FinanceMainDialog_PromoProduct;
	}
}