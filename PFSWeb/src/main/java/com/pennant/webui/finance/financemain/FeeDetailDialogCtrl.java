/**

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
 * FileName    		:  FeeDetailDialogCtrl.java                                             * 	  
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.applicationmaster.TakafulProviderService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FeeDetailDialog.zul file.
 */
public class FeeDetailDialogCtrl extends GFCBaseCtrl<FeeRule> {
	private static final long				serialVersionUID		= 6004939933729664895L;
	private final static Logger				logger					= Logger.getLogger(FeeDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_FeeDetailDialog;												// autoWired

	protected Grid							gridBasicDetail;														// autowired

	protected Button						btnFeeCharges;															// autoWired
	protected Label							label_feeChargesSummaryVal;											// autoWired
	protected Listbox						listBoxFinFeeCharges;													// autoWired
	protected Listbox						listBoxInsuranceDetails;												// autoWired

	private Map<String, BigDecimal>			waiverPaidChargesMap	= null;
	private Map<String, FeeRule>			feeRuleDetailsMap		= null;
	private Map<String, String>				feeMethodDetailsMap		= null;

	//Old Variables
	private String							oldVar_feeActId;
	/*
	 * private String oldVar_waiverReason; // autoWired private String oldVar_takafulReference; // autoWired private
	 * String oldVar_pptWaiverReason; // autoWired private String oldVar_pptTakafulReference; // autoWired
	 */
	private String							eventCode				= "";
	private String							menuItemRightName		= null;
	private boolean							feeChargesExecuted;

	// not auto wired variables
	private FinanceDetail					financeDetail			= null;
	private FinScheduleData					finScheduleData			= null;
	private FinanceMain						financeMain				= null;
	private Object							financeMainDialogCtrl	= null;
	private boolean							isWIF					= false;

	//Bean Setters  by application Context
	private AccountEngineExecution			engineExecution;
	private CustomerService					customerService;
	private FinanceDetailService			financeDetailService;
	private TakafulProviderService			takafulProviderService;
	private FinanceReferenceDetailService	financeReferenceDetailService;
	private MailUtil						mailUtil;
	private FinBasicDetailsCtrl				finBasicDetailsCtrl;
	protected Groupbox						finBasicdetails;
	private BigDecimal						addingFeeToFinance		= BigDecimal.ZERO;
	protected AccountSelectionBox			feeActId;
	protected Button						btnNew_FeeDetailDialog_FinInsurance;
	private List<FinInsurances>				finInsuranceList		= new ArrayList<FinInsurances>();
	private transient RuleExecutionUtil		ruleExecutionUtil;
	private RuleService						ruleService;

	/**
	 * default constructor.<br>
	 */
	public FeeDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FeeDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeeDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}

		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		if (arguments.containsKey("roleCode")) {
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "FeeDetailDialog", menuItemRightName);
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}
		doShowDialog(this.financeDetail);

		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

	public void doEdit() {

		if (!isWIF) {
			getUserWorkspace().allocateAuthorities("FeeDetailDialog", getRole());
			this.btnNew_FeeDetailDialog_FinInsurance.setVisible(getUserWorkspace().isAllowed(
					"btnNew_FeeDetailDialog_FinInsurance"));
		}

		//Set Field Properties
		this.feeActId.setReadonly(isReadOnly("FeeDetailDialog_feeAccountId"));

	}

	public boolean isReadOnly(String componentName) {
		if (!isWIF) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	public boolean isDataChanged(boolean isNotSchChange) {
		logger.debug("Entering");
		doClearErrorMessages();
		if (isNotSchChange && this.oldVar_feeActId != this.feeActId.getValue()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	public void doStoreInitValues() {
		doClearErrorMessages();
		this.oldVar_feeActId = this.feeActId.getValue();
	}

	/**
	 * Method for validating Fee Detail fields
	 * 
	 * @return
	 */
	public ArrayList<WrongValueException> doValidate(int noOfTerms) {

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Check Fee method & Schedule Terms for Each Fee if Existing
		List<Listitem> listItems = listBoxFinFeeCharges.getItems();
		if (listItems != null && !listItems.isEmpty()) {
			for (Listitem curListItem : listItems) {
				String feeMethd = PennantConstants.List_Select;
				String code = curListItem.getId();
				if (this.listBoxFinFeeCharges.getFellowIfAny("feeMethod_" + code) != null) {
					Combobox feeMethdBox = (Combobox) listBoxFinFeeCharges.getFellowIfAny("feeMethod_" + code);
					feeMethd = feeMethdBox.getSelectedItem().getValue().toString();
					try {
						if (!feeMethdBox.isDisabled()
								&& StringUtils.equals(PennantConstants.List_Select, getComboboxValue(feeMethdBox))) {
							throw new WrongValueException(feeMethdBox, Labels.getLabel("CHECK_NO_EMPTY",
									new String[] { Labels.getLabel("label_FeeDetailDialog_FeeMethod.value") }));
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}

				if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, feeMethd)) {
					try {
						if (this.listBoxFinFeeCharges.getFellowIfAny("schdTerms_" + code) != null) {
							Intbox schTermIntBox = (Intbox) this.listBoxFinFeeCharges.getFellowIfAny("schdTerms_"
									+ code);
							if (!schTermIntBox.isReadonly()
									&& (schTermIntBox.intValue() <= 0 || schTermIntBox.intValue() >= noOfTerms)) {
								throw new WrongValueException(schTermIntBox, Labels.getLabel("NUMBER_RANGE",
										new String[] { Labels.getLabel("label_FeeDetailDialog_FeeSchdTerms.value"),
												" 0 ", String.valueOf(noOfTerms) }));
							}
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}
			}
		}

		return wve;
	}

	//Reset Constraints and Error Messages
	public void doRemoveConstraints() {
		this.feeActId.setConstraint("");
	}

	public void doClearErrorMessages() {
		this.feeActId.setErrorMessage("");
	}

	/**
	 * Method for Adding data to Bean object
	 * 
	 * @param finScheduleData
	 * @throws ParseException
	 */
	public FinScheduleData doWriteComponentsToBean(FinScheduleData finScheduleData, boolean istakaful,
			boolean isForValidation) {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setFeeAccountId(
				PennantApplicationUtil.unFormatAccountNumber(this.feeActId.getValue()));
		BigDecimal deductFeeFromDisb = BigDecimal.ZERO;
		BigDecimal feeAddToDisb = BigDecimal.ZERO;

		// Fee Detail Calculations
		List<FeeRule> feeRules = finScheduleData.getFeeRules();
		if (feeRules != null && !feeRules.isEmpty()) {
			for (FeeRule feeRule : feeRules) {
				BigDecimal unPaidFee = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount())
						.subtract(feeRule.getPaidAmount());

				String feeMethd = PennantConstants.List_Select;
				String code = feeRule.getFeeCode()
						+ DateUtility.formateDate(feeRule.getSchDate(), PennantConstants.AS400DateFormat)
						+ feeRule.getSeqNo();
				if (this.listBoxFinFeeCharges.getFellowIfAny("feeMethod_" + code) != null) {
					Combobox feeMethdBox = (Combobox) listBoxFinFeeCharges.getFellowIfAny("feeMethod_" + code);
					feeMethd = feeMethdBox.getSelectedItem().getValue().toString();
					feeRule.setFeeMethod(feeMethd);
				}

				if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT, feeMethd)) {
					feeRule.setScheduleTerms(1);
				} else if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR, feeMethd)) {
					feeRule.setScheduleTerms(finScheduleData.getFinanceMain().getNumberOfTerms());
				} else if (StringUtils.equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, feeMethd)) {
					if (this.listBoxFinFeeCharges.getFellowIfAny("schdTerms_" + code) != null) {
						Intbox schTermBox = (Intbox) listBoxFinFeeCharges.getFellowIfAny("schdTerms_" + code);
						feeRule.setScheduleTerms(schTermBox.intValue());
					}
				} else if (StringUtils.equals(CalculationConstants.REMFEE_PART_OF_DISBURSE, feeMethd)) {
					deductFeeFromDisb = deductFeeFromDisb.add(unPaidFee);
				} else if (StringUtils.equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE, feeMethd)) {
					feeAddToDisb = feeAddToDisb.add(unPaidFee);
				}
			}
		}

		// Insurance Amounts calculation
		List<FinInsurances> insurances = getFinInsuranceList();
		BigDecimal insAddToDisb = BigDecimal.ZERO;
		BigDecimal deductInsFromDisb = BigDecimal.ZERO;
		BigDecimal finAmount = finScheduleData.getFinanceMain().getFinAmount();
		BigDecimal downPayAmt = finScheduleData.getFinanceMain().getDownPayment();
		Rule rule;

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = null;
		if (financeDetail.getCustomerDetails() != null) {
			customer = financeDetail.getCustomerDetails().getCustomer();
		}
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		HashMap<String, Object> declaredFieldValues = getDataMap(financeMain, customer, financeType);

		if (insurances != null && !insurances.isEmpty()) {
			for (FinInsurances insurance : insurances) {
				if (insurance.isInsuranceReq()) {
					String payType = insurance.getPaymentMethod();
					if (StringUtils.equals(InsuranceConstants.PAYTYPE_SCH_FRQ, payType)) {
						continue;
					}

					BigDecimal insAmount = insurance.getAmount();
					insurance.setAmount(BigDecimal.ZERO);

					// Rule Based then Execute rule to Insurance Amount
					if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_RULE)) {
						rule = ruleService.getRuleById(insurance.getCalRule(), RuleConstants.MODULE_INSRULE,
								RuleConstants.MODULE_INSRULE);
						if (rule != null) {
							insAmount = (BigDecimal) getRuleExecutionUtil().executeRule(rule.getSQLRule(),
									declaredFieldValues, financeMain.getFinCcy(), RuleReturnType.DECIMAL);
						}
					}
					// Percentage Based then based on calculation Type, percentage Amount to be calculated
					else if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_PERCENTAGE)) {
						if (insurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
							insAmount = finAmount.multiply(insurance.getCalPerc()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
						} else if (insurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
							insAmount = (finAmount.subtract(downPayAmt)).multiply(insurance.getCalPerc()).divide(
									new BigDecimal(100), RoundingMode.HALF_DOWN);
						}
					}
					// Provider Rate Based then based on calculation Type, Amount to be calculated
					else if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
						if (insurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
							insAmount = finAmount.multiply(insurance.getInsuranceRate()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
						} else if (insurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
							insAmount = (finAmount.subtract(downPayAmt)).multiply(insurance.getInsuranceRate()).divide(
									new BigDecimal(100), RoundingMode.HALF_DOWN);
						}
					}
					// Constant Amount not required any calculation

					if (StringUtils.equals(InsuranceConstants.PAYTYPE_DF_DISB, payType)) {
						deductInsFromDisb = deductInsFromDisb.add(insAmount);
						insurance.setAmount(insAmount);
					} else if (StringUtils.equals(InsuranceConstants.PAYTYPE_ADD_DISB, payType)) {
						insAddToDisb = insAddToDisb.add(insAmount);
						insurance.setAmount(insAmount);
					}
				}
			}
		}

		if (StringUtils.isBlank(getFinanceDetail().getModuleDefiner())) {
			//Fee Amounts
			finScheduleData.getFinanceMain().setDeductFeeDisb(deductFeeFromDisb);
			finScheduleData.getFinanceMain().setFeeChargeAmt(feeAddToDisb);
		}

		//Insurance Amounts
		finScheduleData.getFinanceMain().setInsuranceAmt(insAddToDisb);
		finScheduleData.getFinanceMain().setDeductInsDisb(deductInsFromDisb);

		finScheduleData.setFinInsuranceList(getFinInsuranceList());

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException
	 */
	public void doWriteBeanToComponents() throws ParseException {
		logger.debug("Entering");

		FinanceMain main = getFinanceMain();
		this.feeActId.setCustCIF(main.getLovDescCustCIF());
		this.feeActId.setValue(main.getFeeAccountId());

		if (isReadOnly("FeeDetailDialog_feeAccountId")) {
			this.feeActId.setMandatoryStyle(false);
		} else {
			this.feeActId.setMandatoryStyle(true);
		}
		doStoreInitValues();

		this.feeChargesExecuted = false;
		// fill schedule list and asset tabs
		if (!getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {

			boolean executeSchTab = false;
			if ((getFinScheduleData().getFeeRules() != null && !getFinScheduleData().getFeeRules().isEmpty())
					|| StringUtils.isEmpty(getFinScheduleData().getFinanceMain().getRecordType())) {

				dofillFeeCharges(getFinScheduleData().getFeeRules(), true, true, false, getFinScheduleData(), false);
				feeChargesExecuted = true;
				if (!isWIF) {
					executeSchTab = true;
				}
			}

			if (executeSchTab) {
				try {
					getFinanceMainDialogCtrl().getClass()
							.getMethod("appendScheduleDetailTab", Boolean.class, Boolean.class)
							.invoke(getFinanceMainDialogCtrl(), true, true);

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			// Prepare Amount Code detail Object
			boolean dftFeeExecReq = false;
			if (StringUtils.equals(getFinScheduleData().getFinanceMain().getRecordType(), "")
					&& getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty()) {

				if (StringUtils.equals(getFinanceDetail().getModuleDefiner(), FinanceConstants.FINSER_EVENT_CANCELFIN)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(),
								FinanceConstants.FINSER_EVENT_BASICMAINTAIN)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(),
								FinanceConstants.FINSER_EVENT_LIABILITYREQ)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(),
								FinanceConstants.FINSER_EVENT_NOCISSUANCE)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(),
								FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(),
								FinanceConstants.FINSER_EVENT_INSCLAIM)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(),
								FinanceConstants.FINSER_EVENT_TIMELYCLOSURE)) {
					dftFeeExecReq = true;
					;
				}
			}

			if ((!executeSchTab || dftFeeExecReq) && !isWIF) {
				Events.sendEvent("onClick$btnFeeCharges", this.window_FeeDetailDialog, new Boolean[] { true, true });
				feeChargesExecuted = true;
			}
		}

		if (!feeChargesExecuted) {
			if (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty()) {
				dofillFeeCharges(getFinanceDetail().getFeeCharges(), false, false, false, getFinScheduleData(), false);
			}
		}

		doFillFinInsurances(getFinanceDetail().getFinScheduleData().getFinInsuranceList());
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException, ParseException {
		logger.debug("Entering");

		try {
			// append finance basic details 
			appendFinBasicDetails();

			//Add Fee account details

			dosetFeeAccountDetails();

			getFinanceMainDialogCtrl().getClass().getMethod("setFeeDetailDialogCtrl", this.getClass())
					.invoke(getFinanceMainDialogCtrl(), this);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		// fill the components with the data
		doEdit();

		doWriteBeanToComponents();

		getBorderLayoutHeight();
		/*
		 * if (isWIF) { this.listBoxFinFeeCharges.setHeight(this.borderLayoutHeight - 320 + "px");
		 * this.listBoxInsuranceDetails.setHeight(this.borderLayoutHeight - 320 + "px");
		 * this.window_FeeDetailDialog.setHeight(this.borderLayoutHeight - 30 + "px"); } else {
		 * this.listBoxInsuranceDetails.setHeight(getListBoxHeight(this.gridBasicDetail.getRows() .getVisibleItemCount()
		 * + 3)); }
		 */
		this.window_FeeDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		logger.debug("Leaving");
	}

	private void dosetFeeAccountDetails() {
		logger.debug("Enteing");

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		this.feeActId.setAccountDetails(getFinanceDetail().getFinScheduleData().getFinanceType().getFinType(), "ERLS",
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());
		this.feeActId.setFormatter(finFormatter);
		this.feeActId.setBranchCode(StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain()
				.getFinBranch()));

		logger.debug("Enteing");
	}

	/**
	 * Method to fill list box in FeeCharges Tab <br>
	 * 
	 * @param feeChargesList
	 *            (List)
	 */
	@SuppressWarnings("unchecked")
	public FinScheduleData dofillFeeCharges(List<?> feeChargesList, boolean isSchdCal, boolean renderSchdl,
			boolean isReBuild, FinScheduleData finScheduleData, boolean onChangeAmt) {
		logger.debug("Entering");

		this.listBoxFinFeeCharges.getItems().clear();
		feeRuleDetailsMap = new HashMap<String, FeeRule>();
		BigDecimal totalFee = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());
		addingFeeToFinance = BigDecimal.ZERO;

		BigDecimal totCal = BigDecimal.ZERO;
		BigDecimal totWaived = BigDecimal.ZERO;
		BigDecimal totPaid = BigDecimal.ZERO;

		if (feeChargesList != null && !feeChargesList.isEmpty()) {
			Object feeObj = feeChargesList.get(0);
			if (feeObj instanceof Rule) {
				Comparator<Object> comp = new BeanComparator("seqOrder");
				Collections.sort(feeChargesList, comp);
			} else if (feeObj instanceof FeeRule) {
				Comparator<Object> comp = new BeanComparator("feeOrder");
				Collections.sort(feeChargesList, comp);
			}

			for (Object chargeRule : feeChargesList) {

				Listitem item = new Listitem();
				Listcell lc;
				if (chargeRule instanceof Rule) {
					Rule rule = (Rule) chargeRule;
					lc = new Listcell(rule.getRuleCode());
					lc.setParent(item);
					lc = new Listcell(rule.getRuleCodeDesc());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (chargeRule instanceof FeeRule) {
					FeeRule feeRule = (FeeRule) chargeRule;
					lc = new Listcell(feeRule.getFeeCode());
					lc.setParent(item);
					lc = new Listcell(feeRule.getFeeCodeDesc());
					lc.setParent(item);

					String code = feeRule.getFeeCode()
							+ DateUtility.formateDate(feeRule.getSchDate(), PennantConstants.AS400DateFormat)
							+ feeRule.getSeqNo();
					item.setId(code);
					if (renderSchdl) {
						if (waiverPaidChargesMap == null) {
							waiverPaidChargesMap = new HashMap<String, BigDecimal>();
						}
						if (feeMethodDetailsMap == null) {
							feeMethodDetailsMap = new HashMap<String, String>();
						}

						if (!onChangeAmt && waiverPaidChargesMap.containsKey("actCal_" + code)) {
							waiverPaidChargesMap.remove("actCal_" + code);
						}
						if (waiverPaidChargesMap.containsKey("cal_" + code)) {
							waiverPaidChargesMap.remove("cal_" + code);
						}
						if (waiverPaidChargesMap.containsKey("waiver_" + code)) {
							waiverPaidChargesMap.remove("waiver_" + code);
						}
						if (waiverPaidChargesMap.containsKey("paid_" + code)) {
							waiverPaidChargesMap.remove("paid_" + code);
						}
						if (feeMethodDetailsMap.containsKey("feeMethod_" + code)) {
							feeMethodDetailsMap.remove("feeMethod_" + code);
						}
						if (feeMethodDetailsMap.containsKey("schdTerms_" + code)) {
							feeMethodDetailsMap.remove("schdTerms_" + code);
						}

						waiverPaidChargesMap.put("cal_" + code, feeRule.getFeeAmount());
						waiverPaidChargesMap.put("waiver_" + code, feeRule.getWaiverAmount());
						waiverPaidChargesMap.put("paid_" + code, feeRule.getPaidAmount());

						feeMethodDetailsMap.put("feeMethod_" + code, feeRule.getFeeMethod());
						feeMethodDetailsMap.put("schdTerms_" + code, String.valueOf(feeRule.getScheduleTerms()));

						if (!onChangeAmt) {
							waiverPaidChargesMap.put("actCal_" + code, feeRule.getCalFeeAmount());

							if (isReBuild
									&& StringUtils
											.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_WAIVERBYBANK)) {
								waiverPaidChargesMap.remove("waiver_" + code);
								waiverPaidChargesMap.put("waiver_" + code, feeRule.getFeeAmount());
							}

							if (isReBuild
									&& StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_PAIDBYCUST)) {
								waiverPaidChargesMap.remove("paid_" + code);
								waiverPaidChargesMap.put("paid_" + code, feeRule.getFeeAmount());
							}

							if (isReBuild
									&& StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
								feeMethodDetailsMap.remove("feeMethod_" + code);
								feeMethodDetailsMap.put("feeMethod_" + code,
										CalculationConstants.REMFEE_PART_OF_SALE_PRICE);
							}
						}
					}

					BigDecimal actCalAmt = BigDecimal.ZERO;
					if (waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("actCal_" + code)) {
						actCalAmt = new BigDecimal(waiverPaidChargesMap.get("actCal_" + code).toString()).setScale(0,
								RoundingMode.FLOOR);
					}
					feeRule.setCalFeeAmount(actCalAmt);

					BigDecimal calAmt;
					if (waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("cal_" + code)) {
						calAmt = new BigDecimal(waiverPaidChargesMap.get("cal_" + code).toString()).setScale(0,
								RoundingMode.FLOOR);
						feeRule.setFeeAmount(calAmt);
					} else {
						calAmt = new BigDecimal(feeRule.getFeeAmount().toString()).setScale(0, RoundingMode.FLOOR);
					}

					BigDecimal waiverAmt;
					if (waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("waiver_" + code)) {
						waiverAmt = new BigDecimal(waiverPaidChargesMap.get("waiver_" + code).toString()).setScale(0,
								RoundingMode.FLOOR);
						feeRule.setWaiverAmount(waiverAmt);
					} else {
						waiverAmt = new BigDecimal(feeRule.getWaiverAmount().toString())
								.setScale(0, RoundingMode.FLOOR);
					}

					BigDecimal paidAmt;
					if (waiverPaidChargesMap != null && waiverPaidChargesMap.containsKey("paid_" + code)) {
						paidAmt = new BigDecimal(waiverPaidChargesMap.get("paid_" + code).toString()).setScale(0,
								RoundingMode.FLOOR);
						feeRule.setPaidAmount(paidAmt);
					} else {
						paidAmt = new BigDecimal(feeRule.getPaidAmount().toString()).setScale(0, RoundingMode.FLOOR);
					}

					String feeMethod = "";
					if (feeMethodDetailsMap != null && feeMethodDetailsMap.containsKey("feeMethod_" + code)) {
						feeMethod = feeMethodDetailsMap.get("feeMethod_" + code);
						feeRule.setFeeMethod(feeMethod);
					} else {
						feeMethod = feeRule.getFeeMethod();
					}

					int schTerms = 0;
					if (feeMethodDetailsMap != null && feeMethodDetailsMap.containsKey("schdTerms_" + code)) {
						schTerms = Integer.parseInt(feeMethodDetailsMap.get("schdTerms_" + code));
						feeRule.setScheduleTerms(schTerms);
					} else {
						schTerms = feeRule.getScheduleTerms();
					}

					if (!isReadOnly("FeeDetailDialog_feeCharge")) {

						//Calculate Amount
						Decimalbox calBox = new Decimalbox();
						calBox.setWidth("120px");
						calBox.setMaxlength(18);
						calBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
						calBox.setSclass("feeWaiver");
						calBox.setDisabled(!feeRule.isCalFeeModify());
						calBox.setId("cal_" + code);
						calBox.setValue(PennantAppUtil.formateAmount(calAmt, formatter));
						lc = new Listcell();
						lc.appendChild(calBox);
						lc.setParent(item);

						Decimalbox oldwaiverBox = null;
						Decimalbox maxWaiverBox = null;
						Decimalbox waiverBox = null;
						if (feeRule.isAllowWaiver()) {

							//Storage Max Waiver Amount
							maxWaiverBox = new Decimalbox();
							maxWaiverBox.setVisible(false);
							maxWaiverBox.setId("maxwaiver_" + code);
							maxWaiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
							BigDecimal maxWaiver = PennantAppUtil.getPercentageValue(feeRule.getFeeAmount(),
									feeRule.getWaiverPerc());
							maxWaiverBox.setValue(PennantAppUtil.formateAmount(maxWaiver, formatter));

							//Storage Old Waiver Amount
							oldwaiverBox = new Decimalbox();
							oldwaiverBox.setVisible(false);
							oldwaiverBox.setId("oldwaiver_" + code);
							oldwaiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
							oldwaiverBox.setValue(PennantAppUtil.formateAmount(waiverAmt, formatter));

							waiverBox = new Decimalbox();
							waiverBox.setWidth("120px");
							waiverBox.setMaxlength(18);
							waiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
							waiverBox.setSclass("feeWaiver");
							waiverBox.setDisabled(false);
							waiverBox.setId("waiver_" + code);
							waiverBox.setValue(PennantAppUtil.formateAmount(waiverAmt, formatter));
							lc = new Listcell();
							waiverBox.setInplace(true);
							lc.appendChild(maxWaiverBox);
							lc.appendChild(oldwaiverBox);
							lc.appendChild(waiverBox);
							lc.setSclass("inlineMargin");
							lc.setParent(item);
						} else {
							lc = new Listcell(PennantAppUtil.amountFormate(waiverAmt, formatter));
							lc.setSclass("text-align:right;");
							lc.setParent(item);
						}

						//Storage Paid Customer Amount
						Decimalbox oldPaidBox = new Decimalbox();
						oldPaidBox.setVisible(false);
						oldPaidBox.setId("oldpaid_" + code);
						oldPaidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
						oldPaidBox.setValue(PennantAppUtil.formateAmount(paidAmt, formatter));

						Decimalbox paidBox = new Decimalbox();
						paidBox.setWidth("120px");
						paidBox.setMaxlength(18);
						paidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
						paidBox.setSclass("feeWaiver");
						paidBox.setDisabled(false);
						paidBox.setId("paid_" + code);
						paidBox.setValue(PennantAppUtil.formateAmount(paidAmt, formatter));
						lc = new Listcell();
						lc.appendChild(oldPaidBox);
						lc.appendChild(paidBox);
						lc.setSclass("inlineMargin");
						lc.setParent(item);

						List<Object> list = new ArrayList<Object>(6);
						list.add(calBox);
						list.add(paidBox);
						list.add(oldwaiverBox);
						list.add(waiverBox);
						list.add(false);
						list.add(oldPaidBox);
						if (waiverBox != null) {
							waiverBox.addForward("onChange", window_FeeDetailDialog, "onChangeFeeAmount", list);
						}

						list = new ArrayList<Object>(6);
						list.add(calBox);
						list.add(waiverBox);
						list.add(oldPaidBox);
						list.add(paidBox);
						list.add(false);
						list.add(oldwaiverBox);
						paidBox.addForward("onChange", window_FeeDetailDialog, "onChangeFeeAmount", list);

						list = new ArrayList<Object>(7);
						list.add(calBox);
						list.add(waiverBox);
						list.add(oldPaidBox);
						list.add(paidBox);
						list.add(true);
						list.add(oldwaiverBox);
						list.add(maxWaiverBox);
						calBox.addForward("onChange", window_FeeDetailDialog, "onChangeFeeAmount", list);

					} else {
						lc = new Listcell(PennantAppUtil.amountFormate(feeRule.getFeeAmount(), formatter));
						lc.setStyle("text-align:right;cursor:default;");
						lc.setParent(item);
						lc = new Listcell(PennantAppUtil.amountFormate(feeRule.getWaiverAmount(), formatter));
						lc.setStyle("text-align:right;cursor:default;");
						lc.setParent(item);
						lc = new Listcell(PennantAppUtil.amountFormate(feeRule.getPaidAmount(), formatter));
						lc.setStyle("text-align:right;cursor:default;");
						lc.setParent(item);
					}

					// Fee Method & Schedule terms
					if (!isReadOnly("FeeDetailDialog_feeCharge")) {

						// Remaining Fee schedule Method
						lc = new Listcell();
						Combobox feeMethCombo = new Combobox();
						if (StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
							feeMethCombo.setDisabled(true);
						}
						fillComboBox(feeMethCombo, feeMethod, PennantStaticListUtil.getRemFeeSchdMethods(), "");
						feeMethCombo.setWidth("80px");
						feeMethCombo.setId("feeMethod_" + code);
						if ((feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount()).subtract(feeRule
								.getPaidAmount())).compareTo(BigDecimal.ZERO) <= 0) {
							feeMethCombo.setDisabled(true);
							feeMethCombo.setSelectedIndex(0);
						}
						lc.appendChild(feeMethCombo);
						lc.setParent(item);

						// Schedule terms in case of Fee Adjusted to "N" Terms
						lc = new Listcell();
						Intbox terms = new Intbox();
						terms.setValue(schTerms);
						terms.setWidth("40px");
						terms.setId("schdTerms_" + code);
						lc.appendChild(terms);
						if (!StringUtils.equals(feeMethod, CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
							terms.setReadonly(true);
							terms.setVisible(false);
						}
						lc.setParent(item);
						List<Object> changeList = new ArrayList<Object>(2);
						changeList.add(feeMethCombo);
						changeList.add(terms);
						feeMethCombo.addForward("onChange", window_FeeDetailDialog, "onChangeFeeMethod", changeList);
						terms.addForward("onChange", window_FeeDetailDialog, "onChangeSchTerms", terms);
					} else {
						lc = new Listcell(PennantStaticListUtil.getlabelDesc(feeMethod,
								PennantStaticListUtil.getRemFeeSchdMethods()));
						lc.setStyle("cursor:default;");
						lc.setParent(item);
						lc = new Listcell(String.valueOf(schTerms));
						lc.setStyle("text-align:right;cursor:default;");
						lc.setParent(item);
					}

					BigDecimal remFeeAmt = feeRule.getFeeAmount().subtract(feeRule.getWaiverAmount())
							.subtract(feeRule.getPaidAmount());
					if (StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {

						lc = new Listcell();
						Label label = new Label(PennantAppUtil.amountFormate(remFeeAmt, formatter));
						label.setId("pftCalFee_" + code);
						lc.setStyle("text-align:right;");
						lc.appendChild(label);
						lc.setParent(item);

						lc = new Listcell();
						label = new Label(PennantAppUtil.amountFormate(BigDecimal.ZERO, formatter));
						label.setId("unpaid_" + code);
						lc.setStyle("text-align:right;");
						lc.appendChild(label);
						lc.setParent(item);

					} else {

						lc = new Listcell();
						Label label = new Label(PennantAppUtil.amountFormate(BigDecimal.ZERO, formatter));
						label.setId("pftCalFee_" + code);
						lc.setStyle("text-align:right;");
						lc.appendChild(label);
						lc.setParent(item);

						lc = new Listcell();
						label = new Label(PennantAppUtil.amountFormate(remFeeAmt, formatter));
						label.setId("unpaid_" + code);
						lc.setStyle("text-align:right;");
						lc.appendChild(label);
						lc.setParent(item);
					}

					feeChargesExecuted = true;
					if (!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())) {
						totalFee = totalFee.add(remFeeAmt);
						feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
					}

					// Mandatory Fee Amount added to Finance Amount in profit Calculation
					if (StringUtils.equals(feeMethod, CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
						addingFeeToFinance = addingFeeToFinance.add(remFeeAmt);
					}

					totCal = totCal.add(feeRule.getFeeAmount());
					totWaived = totWaived.add(feeRule.getWaiverAmount());
					totPaid = totPaid.add(feeRule.getPaidAmount());
				}
				this.listBoxFinFeeCharges.appendChild(item);
			}

			if (feeChargesExecuted) {
				setTotals(totCal, totWaived, totPaid, addingFeeToFinance, formatter);
			}

			//Workflow Process Condition as Current Process having Fees or not(If Earlier process fees to be display, need to re-check below conditions).
			if (totalFee.compareTo(BigDecimal.ZERO) > 0) {
				finScheduleData.getFinanceMain().setFeeExists(true);
			} else {
				finScheduleData.getFinanceMain().setFeeExists(false);
			}

			// Only for displaying purpose
			this.label_feeChargesSummaryVal.setValue(PennantAppUtil.amountFormate(totalFee, formatter));
		}
		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Setting totals for Executed Fee Details
	 * 
	 * @param totCal
	 * @param totWaived
	 * @param totPaid
	 * @param formatter
	 */
	private void setTotals(BigDecimal totCal, BigDecimal totWaived, BigDecimal totPaid, BigDecimal feeToFinAmt,
			int formatter) {

		Listitem item = new Listitem();
		item.setStyle("background-color: #C0EBDF;");

		Listcell lc = new Listcell(Labels.getLabel("label_TotalFeeSummary.value"));
		lc.setSpan(2);
		lc.setStyle("font-weight:bold;");
		lc.setParent(item);

		lc = new Listcell();
		Label label = new Label(PennantAppUtil.amountFormate(totCal, formatter));
		label.setId("totcalfee");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(totWaived, formatter));
		label.setId("totwaivedfee");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(totPaid, formatter));
		label.setId("totpaidfee");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(feeToFinAmt, formatter));
		label.setId("totfeetotfin");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		label = new Label(PennantAppUtil.amountFormate(
				totCal.subtract(totWaived).subtract(totPaid).subtract(feeToFinAmt), formatter));
		label.setId("totunpaidfee");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.appendChild(label);
		lc.setParent(item);

		lc = new Listcell();
		lc.setSpan(2);
		lc.setParent(item);

		this.listBoxFinFeeCharges.appendChild(item);
	}

	/**
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void onChangeFeeAmount(ForwardEvent event) throws InterruptedException, IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering" + event.toString());

		List<Object> list = (List<Object>) event.getData();
		Decimalbox calBox = (Decimalbox) list.get(0);
		Decimalbox oppBox = (Decimalbox) list.get(1);
		Decimalbox oldBox = (Decimalbox) list.get(2);
		Decimalbox targetBox = (Decimalbox) list.get(3);
		boolean isCalValueChange = (Boolean) list.get(4);
		Decimalbox oldWaiverBox = (Decimalbox) list.get(5);

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		if (targetBox.getValue() != null && targetBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
			MessageUtil.showErrorMessage(Labels.getLabel("NUMBER_MINVALUE_EQ", new String[] { "Value", "0" }));
			targetBox.setValue(BigDecimal.ZERO);
		}

		BigDecimal calAmount = PennantAppUtil.unFormateAmount(calBox.getValue(), finFormatter);
		if (isCalValueChange) {
			BigDecimal sumAmount = BigDecimal.ZERO;
			if (oppBox != null) {
				sumAmount = PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter);
			}
			sumAmount = sumAmount.add(PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
			if (sumAmount.compareTo(calAmount) > 0) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_ChangeFee.value"));

				if (oppBox != null) {
					oppBox.setValue(BigDecimal.ZERO);
					oldWaiverBox.setValue(BigDecimal.ZERO);

					if (waiverPaidChargesMap.containsKey(oppBox.getId())) {
						waiverPaidChargesMap.remove(oppBox.getId());
					}
					waiverPaidChargesMap.put(targetBox.getId(),
							PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter));
				}
				oldBox.setValue(BigDecimal.ZERO);
				targetBox.setValue(BigDecimal.ZERO);

			}
		}

		//Check Condition based on Waiver is Allowed or Not
		BigDecimal balFeeAmount = null;
		if (oppBox == null) {
			balFeeAmount = calAmount.subtract(PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
		} else {
			balFeeAmount = calAmount.subtract(PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter))
					.subtract(PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
		}
		if (balFeeAmount.compareTo(BigDecimal.ZERO) < 0) {

			//Show Error message for Exceeding Entered Amount Limit
			BigDecimal availAmt = null;
			if (oppBox == null) {
				availAmt = calAmount;
			} else {
				availAmt = calAmount.subtract(PennantAppUtil.unFormateAmount(oppBox.getValue(), finFormatter));
			}

			String msg = targetBox.getId().substring(0, targetBox.getId().indexOf('_')).toUpperCase()
					+ " Amount for "
					+ targetBox.getId().substring(targetBox.getId().indexOf('_') + 1, targetBox.getId().length() - 7)
							.toUpperCase() + " Rule Cannot be greater than Avail Amount:"
					+ PennantAppUtil.formateAmount(availAmt, finFormatter);
			MessageUtil.showErrorMessage(msg);

			//Reset Old Amount back to Target Box
			if (oldBox.getValue().compareTo(availAmt) > 0) {
				targetBox.setValue(BigDecimal.ZERO);
			} else {
				targetBox.setValue(oldBox.getValue());
			}

		} else {

			boolean isReExecSchedule = false;
			if (getFinanceMain().isNewRecord()
					|| PennantConstants.RECORD_TYPE_NEW.equals(getFinanceMain().getRecordType()) || isWIF) {
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
			} else {
				isReExecSchedule = true;
			}

			//Set Waiver & paid Value Amount Storage on Change into map
			if (waiverPaidChargesMap == null) {
				waiverPaidChargesMap = new HashMap<String, BigDecimal>();
			}

			if (waiverPaidChargesMap.containsKey(calBox.getId())) {
				waiverPaidChargesMap.remove(calBox.getId());
			}
			waiverPaidChargesMap.put(calBox.getId(), PennantAppUtil.unFormateAmount(calBox.getValue(), finFormatter));

			if (waiverPaidChargesMap.containsKey(targetBox.getId())) {
				waiverPaidChargesMap.remove(targetBox.getId());
			}
			waiverPaidChargesMap.put(targetBox.getId(),
					PennantAppUtil.unFormateAmount(targetBox.getValue(), finFormatter));
			oldBox.setValue(targetBox.getValue());

			//Recalculation for Fee charges After Modified Fee Sequence
			//Because any Fee calculation will Effect on Before Values 
			if (isCalValueChange) {

				//Recalculate Total Actual Fee Charge Amount
				List<FeeRule> feeRules = getFinanceDetail().getFinScheduleData().getFeeRules();
				boolean isContinueForCalculation = false;
				List<FeeRule> existFeeRules = new ArrayList<FeeRule>();
				int size = feeRules.size();

				for (int i = 0; i < size; i++) {

					FeeRule feeRule = feeRules.get(i);
					String code = feeRule.getFeeCode()
							+ DateUtility.formateDate(feeRule.getSchDate(), PennantConstants.AS400DateFormat)
							+ feeRule.getSeqNo();

					if (!isContinueForCalculation) {
						existFeeRules.add(feeRule);
					}

					if (isContinueForCalculation) {
						if (waiverPaidChargesMap.containsKey("cal_" + code)) {
							waiverPaidChargesMap.remove("cal_" + code);
						}
						if (waiverPaidChargesMap.containsKey("waiver_" + code)) {
							waiverPaidChargesMap.remove("waiver_" + code);
						}
						if (waiverPaidChargesMap.containsKey("paid_" + code)) {
							waiverPaidChargesMap.remove("paid_" + code);
						}
					}

					String calValBoxId = calBox.getId();
					if (calValBoxId.equals("cal_" + code)) {

						if (this.listBoxFinFeeCharges.getFellowIfAny("cal_" + code) != null) {
							Decimalbox calbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("cal_" + code);
							feeRule.setFeeAmount(PennantAppUtil.unFormateAmount(calbox.getValue(), finFormatter));
						}
						if (this.listBoxFinFeeCharges.getFellowIfAny("waiver_" + code) != null) {
							Decimalbox waiverbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("waiver_" + code);
							feeRule.setWaiverAmount(PennantAppUtil.unFormateAmount(waiverbox.getValue(), finFormatter));
						}
						if (this.listBoxFinFeeCharges.getFellowIfAny("paid_" + code) != null) {
							Decimalbox paidbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("paid_" + code);
							feeRule.setPaidAmount(PennantAppUtil.unFormateAmount(paidbox.getValue(), finFormatter));
						}
						if (i != size - 1) {

							//Ask For User Confirmation
							final String msg = Labels.getLabel("label_RecalculationforFeeCharges");
							final String title = Labels.getLabel("message.Conformation");
							MultiLineMessageBox.doSetTemplate();

							int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
									| MultiLineMessageBox.NO, Messagebox.QUESTION, true);

							if (conf == MultiLineMessageBox.YES) {
								isContinueForCalculation = true;
							} else {
								break;
							}
						}
					}
				}

				//Warning Message To Ask for Recalculation of Fee Details After Modified one if Exists
				if (isContinueForCalculation) {
					reExecCalAmtFeeChange(true, getFinScheduleData(), getFinScheduleData().getFinanceMain()
							.getFinStartDate(), existFeeRules);
				}
			}

			if (isReExecSchedule) {

				//Re-rendering Finance Fee Rule Data
				getFinanceDetail().setFinScheduleData(
						dofillFeeCharges(getFinanceDetail().getFinScheduleData().getFeeRules(), true, false, true,
								getFinanceDetail().getFinScheduleData(), true));

				// Need to Modify this , if Add Fee & charges include to Schedule in other Event Actions
				if (StringUtils.isEmpty(eventCode)) {
					Date disbDate = DateUtility
							.getUtilDate(
									targetBox.getId().substring(targetBox.getId().length() - 7,
											targetBox.getId().length() - 1), PennantConstants.AS400DateFormat);

					BigDecimal modifiedFeevalue = PennantAppUtil.unFormateAmount(
							targetBox.getValue().subtract(oldBox.getValue()), finFormatter);
					getFinScheduleData().getFinanceMain().setEventFromDate(disbDate);
					getFinScheduleData().getFinanceMain().setRecalType(CalculationConstants.RPYCHG_TILLMDT);
					getFinScheduleData().getFinanceMain().setRecalToDate(null);
					
					getFinScheduleData().getFinanceMain().setCalRoundingMode(getFinScheduleData().getFinanceType().getRoundingMode());
					getFinScheduleData().getFinanceMain().setRoundingTarget(getFinScheduleData().getFinanceType().getRoundingTarget());
					
					setFinScheduleData(ScheduleCalculator.addDisbursement(getFinanceDetail().getFinScheduleData(),
							BigDecimal.ZERO, modifiedFeevalue.negate(), false));

					try {
						getFinanceMainDialogCtrl().getClass().getMethod("reRenderScheduleList", FinScheduleData.class)
								.invoke(getFinanceMainDialogCtrl(), getFinanceDetail().getFinScheduleData());
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}
			} else {

				//Recalculate Total Actual Fee Charge Amount
				List<FeeRule> feeRules = getFinanceDetail().getFinScheduleData().getFeeRules();
				BigDecimal totalFeeCharge = BigDecimal.ZERO;
				BigDecimal totalcalFee = BigDecimal.ZERO;
				BigDecimal totalwaivedFee = BigDecimal.ZERO;
				BigDecimal totalpaidFee = BigDecimal.ZERO;

				for (FeeRule feeRule : feeRules) {

					String code = feeRule.getFeeCode()
							+ DateUtility.formateDate(feeRule.getSchDate(), PennantConstants.AS400DateFormat)
							+ feeRule.getSeqNo();

					BigDecimal unpaidFee = BigDecimal.ZERO;

					if (this.listBoxFinFeeCharges.getFellowIfAny("cal_" + code) != null) {
						Decimalbox calbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("cal_" + code);
						totalFeeCharge = totalFeeCharge.add(PennantAppUtil.unFormateAmount(calbox.getValue(),
								finFormatter));

						unpaidFee = PennantAppUtil.unFormateAmount(calbox.getValue(), finFormatter);
						totalcalFee = totalcalFee.add(PennantAppUtil.unFormateAmount(calbox.getValue(), finFormatter));
					}
					if (this.listBoxFinFeeCharges.getFellowIfAny("waiver_" + code) != null) {
						Decimalbox waiverbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("waiver_" + code);
						totalFeeCharge = totalFeeCharge.subtract(PennantAppUtil.unFormateAmount(waiverbox.getValue(),
								finFormatter));

						unpaidFee = unpaidFee.subtract(PennantAppUtil.unFormateAmount(waiverbox.getValue(),
								finFormatter));
						totalwaivedFee = totalwaivedFee.add(PennantAppUtil.unFormateAmount(waiverbox.getValue(),
								finFormatter));
					}
					if (this.listBoxFinFeeCharges.getFellowIfAny("paid_" + code) != null) {
						Decimalbox paidbox = (Decimalbox) listBoxFinFeeCharges.getFellowIfAny("paid_" + code);
						totalFeeCharge = totalFeeCharge.subtract(PennantAppUtil.unFormateAmount(paidbox.getValue(),
								finFormatter));

						unpaidFee = unpaidFee
								.subtract(PennantAppUtil.unFormateAmount(paidbox.getValue(), finFormatter));
						totalpaidFee = totalpaidFee
								.add(PennantAppUtil.unFormateAmount(paidbox.getValue(), finFormatter));
					}

					if (StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {

						if (this.listBoxFinFeeCharges.getFellowIfAny("pftCalFee_" + code) != null) {
							Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("pftCalFee_" + code);
							label.setValue(PennantAppUtil.amountFormate(unpaidFee, finFormatter));
						}

						if (this.listBoxFinFeeCharges.getFellowIfAny("unpaid_" + code) != null) {
							Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("unpaid_" + code);
							label.setValue(PennantAppUtil.amountFormate(BigDecimal.ZERO, finFormatter));
						}
					} else {

						if (this.listBoxFinFeeCharges.getFellowIfAny("pftCalFee_" + code) != null) {
							Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("pftCalFee_" + code);
							label.setValue(PennantAppUtil.amountFormate(BigDecimal.ZERO, finFormatter));
						}

						if (this.listBoxFinFeeCharges.getFellowIfAny("unpaid_" + code) != null) {
							Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("unpaid_" + code);
							label.setValue(PennantAppUtil.amountFormate(unpaidFee, finFormatter));
						}
					}
				}

				if (this.listBoxFinFeeCharges.getFellowIfAny("totcalfee") != null) {
					Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("totcalfee");
					label.setValue(PennantAppUtil.amountFormate(totalcalFee, finFormatter));
				}

				if (this.listBoxFinFeeCharges.getFellowIfAny("totwaivedfee") != null) {
					Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("totwaivedfee");
					label.setValue(PennantAppUtil.amountFormate(totalwaivedFee, finFormatter));
				}

				if (this.listBoxFinFeeCharges.getFellowIfAny("totpaidfee") != null) {
					Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("totpaidfee");
					label.setValue(PennantAppUtil.amountFormate(totalpaidFee, finFormatter));
				}

				if (this.listBoxFinFeeCharges.getFellowIfAny("totunpaidfee") != null) {
					Label label = (Label) listBoxFinFeeCharges.getFellowIfAny("totunpaidfee");
					label.setValue(PennantAppUtil.amountFormate(
							totalcalFee.subtract(totalwaivedFee).subtract(totalpaidFee), finFormatter));
				}

				//Finance Fee Charges Recalculated
				this.label_feeChargesSummaryVal.setValue(PennantAppUtil.amountFormate(totalFeeCharge, finFormatter));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for action of event when changing "Fee Method"
	 * 
	 * @param event
	 */
	public void onChangeFeeMethod(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) event.getData();
		Combobox feeMethodBox = (Combobox) list.get(0);
		Intbox termsBox = (Intbox) list.get(1);

		if (!StringUtils.equals(feeMethodBox.getSelectedItem().getValue().toString(),
				CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
			termsBox.setReadonly(true);
			termsBox.setVisible(false);
		} else {
			termsBox.setReadonly(isReadOnly("FeeDetailDialog_feeCharge"));
			termsBox.setVisible(true);
		}

		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		if (feeMethodDetailsMap == null) {
			feeMethodDetailsMap = new HashMap<String, String>();
		}
		if (feeMethodDetailsMap.containsKey(feeMethodBox.getId())) {
			feeMethodDetailsMap.remove(feeMethodBox.getId());
		}
		feeMethodDetailsMap.put(feeMethodBox.getId(), feeMethodBox.getSelectedItem().getValue().toString());

		if (feeMethodDetailsMap.containsKey(termsBox.getId())) {
			feeMethodDetailsMap.remove(termsBox.getId());
		}

		// Calculation Remainign Fee Amount
		String code = StringUtils.replace(feeMethodBox.getId(), "feeMethod_", "");
		BigDecimal balAmount = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(getFinanceMain().getFinCcy());
		if (this.listBoxFinFeeCharges.getFellowIfAny("cal_" + code) != null) {
			Decimalbox calBox = (Decimalbox) this.listBoxFinFeeCharges.getFellowIfAny("cal_" + code);
			balAmount = PennantAppUtil.unFormateAmount(calBox.getValue(), formatter);
		}
		if (this.listBoxFinFeeCharges.getFellowIfAny("waiver_" + code) != null) {
			Decimalbox waiverBox = (Decimalbox) this.listBoxFinFeeCharges.getFellowIfAny("waiver_" + code);
			balAmount = balAmount.subtract(PennantAppUtil.unFormateAmount(waiverBox.getValue(), formatter));
		}
		if (this.listBoxFinFeeCharges.getFellowIfAny("paid_" + code) != null) {
			Decimalbox paidBox = (Decimalbox) this.listBoxFinFeeCharges.getFellowIfAny("paid_" + code);
			balAmount = balAmount.subtract(PennantAppUtil.unFormateAmount(paidBox.getValue(), formatter));
		}

		// Setting Remaining Fee Amount based on Fee method Selection
		BigDecimal feeToFinAmt = BigDecimal.ZERO;
		BigDecimal unpaidFeeAmt = BigDecimal.ZERO;
		if (StringUtils.equals(feeMethodBox.getSelectedItem().getValue().toString(),
				CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
			feeToFinAmt = balAmount;
		} else {
			unpaidFeeAmt = balAmount;
		}

		if (this.listBoxFinFeeCharges.getFellowIfAny("pftCalFee_" + code) != null) {
			Label feeToFinLabel = (Label) this.listBoxFinFeeCharges.getFellowIfAny("pftCalFee_" + code);
			feeToFinLabel.setValue(PennantAppUtil.amountFormate(feeToFinAmt,
					CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
		}
		if (this.listBoxFinFeeCharges.getFellowIfAny("unpaid_" + code) != null) {
			Label unpaidFeeId = (Label) this.listBoxFinFeeCharges.getFellowIfAny("unpaid_" + code);
			unpaidFeeId.setValue(PennantAppUtil.amountFormate(unpaidFeeAmt,
					CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for process data setting while on changing Schedule terms, if Fees Applicable
	 * 
	 * @param event
	 */
	public void onChangeSchTerms(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		Intbox termsBox = (Intbox) event.getData();
		;
		if (feeMethodDetailsMap != null && feeMethodDetailsMap.containsKey(termsBox.getId())) {
			feeMethodDetailsMap.remove(termsBox.getId());
		}
		feeMethodDetailsMap.put(termsBox.getId(), String.valueOf(termsBox.intValue()));
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Executing Fee Charges Details List
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnFeeCharges(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		boolean isSchdCal = false;
		boolean renderSchdl = false;
		if (event.getData() != null) {
			Boolean[] data = (Boolean[]) event.getData();
			isSchdCal = data[0];
			renderSchdl = data[1];
		}

		doExecuteFeeCharges(isSchdCal, renderSchdl, getMainFinaceDetail().getFinScheduleData(), true,
				getMainFinaceDetail().getFinScheduleData().getFinanceMain().getFinStartDate());

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Execution of Fee Details on Build Event / For Render Schedule process
	 * 
	 * @param isSchdCal
	 * @param renderSchdl
	 * @param finScheduleData
	 * @param isReBuild
	 * @param feeApplyDate
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException
	 */
	public FinScheduleData doExecuteFeeCharges(boolean isSchdCal, boolean renderSchdl, FinScheduleData finScheduleData,
			boolean isReBuild, Date feeApplyDate) throws InterruptedException, IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");

		if (!isSchdCal && finScheduleData.getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
			return finScheduleData;
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finScheduleData.getFinanceMain(), finScheduleData
				.getFinanceScheduleDetails(), new FinanceProfitDetail(), eventCode, finScheduleData.getFinanceMain()
				.getFinStartDate(), finScheduleData.getFinanceMain().getFinStartDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		if (isWIF) {
			aeEvent.setNewRecord(true);
		}

		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();

		List<FeeRule> feeRules = getEngineExecution().getFeeChargesExecResults(
				CurrencyUtil.getFormat(getFinanceMain().getFinCcy()), isWIF, executingMap);

		//Get Finance Fee Details For Schedule Render Purpose In maintenance Stage
		List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
		if (!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())
				&& !isWIF) {
			approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinReference(), "", isWIF);
		}

		for (FeeRule feeCharge : feeRules) {
			feeCharge.setSchDate(feeApplyDate);
			int seqNo = 0;
			if (!isReBuild) {
				for (FeeRule feeRule : finScheduleData.getFeeRules()) {
					feeRule.setNewFee(false);
					if (feeRule.getFeeCode().equals(feeCharge.getFeeCode())) {
						if (seqNo < feeRule.getSeqNo() && feeApplyDate.compareTo(feeRule.getSchDate()) == 0) {
							seqNo = feeRule.getSeqNo();
						}
					}
				}
			}

			//Sequence Updations on Maintenance Module Fees
			if (isReBuild && !financeMain.isNewRecord()
					&& !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType()) && !isWIF) {
				if (approvedFeeRules != null && !approvedFeeRules.isEmpty()) {

					for (FeeRule feeRule : approvedFeeRules) {
						if (feeRule.getFeeCode().equals(feeCharge.getFeeCode())) {
							if (seqNo < feeRule.getSeqNo()
									&& feeCharge.getSchDate().compareTo(feeRule.getSchDate()) == 0) {
								seqNo = feeRule.getSeqNo();
							}
						}
					}
				}
			}

			feeCharge.setSeqNo(seqNo + 1);
			feeCharge.setCalFeeAmount(feeCharge.getFeeAmount());
			feeCharge.setNewFee(true);
		}

		if (isReBuild) {
			finScheduleData.setFeeRules(feeRules);
		} else {
			finScheduleData.getFeeRules().addAll(feeRules);
		}
		finScheduleData = dofillFeeCharges(finScheduleData.getFeeRules(), isSchdCal, renderSchdl, isReBuild,
				finScheduleData, false);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method for Execution of Fee Details on Build Event / For Render Schedule process
	 * 
	 * @param isSchdCal
	 * @param renderSchdl
	 * @param finScheduleData
	 * @param isReBuild
	 * @param feeApplyDate
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws AccountNotFoundException
	 */
	public void reExecCalAmtFeeChange(boolean isSchdCal, FinScheduleData finScheduleData, Date feeApplyDate,
			List<FeeRule> existFeeRules) throws InterruptedException, IllegalAccessException,
			InvocationTargetException, PFFInterfaceException {
		logger.debug("Entering");

		finScheduleData.getFinanceMain().setCurDisbursementAmt(finScheduleData.getFinanceMain().getFinAmount());


		AEEvent aeEvent = AEAmounts.procAEAmounts(finScheduleData.getFinanceMain(), finScheduleData
				.getFinanceScheduleDetails(), new FinanceProfitDetail(), eventCode, finScheduleData.getFinanceMain()
				.getFinStartDate(), finScheduleData.getFinanceMain().getFinStartDate());
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		//Customer Data Setup
		aeEvent.setModuleDefiner(getFinanceDetail().getModuleDefiner());
		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();

		getFinScheduleData().getFinanceType().getDeclaredFieldValues(executingMap);

		List<FeeRule> feeRules = getEngineExecution().getReExecFeeResults(
				CurrencyUtil.getFormat(getFinanceMain().getFinCcy()), isWIF, existFeeRules, executingMap);

		//Get Finance Fee Details For Schedule Render Purpose In maintenance Stage
		List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
		if (!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())
				&& !isWIF) {
			approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinReference(), "", isWIF);
		}

		for (FeeRule feeCharge : feeRules) {
			int seqNo = 0;
			feeCharge.setSchDate(feeApplyDate);

			//Sequence Updations on Maintenance Module Fees
			if (!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())
					&& !isWIF) {
				if (approvedFeeRules != null && !approvedFeeRules.isEmpty()) {

					for (FeeRule feeRule : approvedFeeRules) {
						if (feeRule.getFeeCode().equals(feeCharge.getFeeCode())) {
							if (seqNo < feeRule.getSeqNo()
									&& feeCharge.getSchDate().compareTo(feeRule.getSchDate()) == 0) {
								seqNo = feeRule.getSeqNo();
							}
						}
					}
				}
			}

			feeCharge.setSeqNo(seqNo + 1);
			feeCharge.setNewFee(true);
		}

		finScheduleData.setFeeRules(feeRules);
		dofillFeeCharges(finScheduleData.getFeeRules(), isSchdCal, true, true, finScheduleData, true);

		logger.debug("Leaving");
	}

	/**
	 * Method for Fetching Finance Detail Data From Main Controller
	 * 
	 * @return
	 */
	public FinanceDetail getMainFinaceDetail() {
		logger.debug("Entering");
		try {
			FinanceDetail financeDetail = (FinanceDetail) getFinanceMainDialogCtrl().getClass()
					.getMethod("getFinanceDetail").invoke(getFinanceMainDialogCtrl());
			logger.debug("Leaving");
			return financeDetail;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public void onClick$btnNew_FeeDetailDialog_FinInsurance(Event event) throws InterruptedException {
		FinInsurances finInsurance = new FinInsurances();
		finInsurance.setNewRecord(true);
		finInsurance.setInsuranceReq(true);
		finInsurance.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
		finInsurance.setWorkflowId(getWorkFlowId());

		doShowInsuranceDialog(finInsurance);

	}

	private void doShowInsuranceDialog(FinInsurances finInsurance) {

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("finInsurance", finInsurance);
		arg.put("feeDetailDialogCtrl", this);
		arg.put("role", getRole());
		arg.put("isWIF", isWIF);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinInsuranceDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void doFillFinInsurances(List<FinInsurances> finInsurances) {
		logger.debug("Entering");
		try {
			if (finInsurances != null) {
				setFinInsuranceList(finInsurances);
				fillFinInsuranecs(finInsurances);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private void fillFinInsuranecs(List<FinInsurances> finInsurances) {
		this.listBoxInsuranceDetails.getItems().clear();
		for (FinInsurances finInsurance : finInsurances) {
			Listitem item = new Listitem();
			Listcell lc;

			lc = new Listcell(finInsurance.getInsuranceType());
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(finInsurance.getInsuranceTypeDesc());
			lc.setParent(item);

			lc = new Listcell(finInsurance.getInsReference());
			lc.setParent(item);

			lc = new Listcell();
			Checkbox checkbox = new Checkbox();
			checkbox.setChecked(finInsurance.isInsuranceReq());
			checkbox.setDisabled(true);
			checkbox.setParent(lc);
			lc.setParent(item);

			lc = new Listcell(finInsurance.getProvider());
			lc.setParent(item);

			lc = new Listcell(PennantAppUtil.getlabelDesc(String.valueOf(finInsurance.getPaymentMethod()),
					PennantStaticListUtil.getInsurancePaymentType()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(finInsurance.getInsuranceRate()));
			lc.setParent(item);

			lc = new Listcell(finInsurance.getWaiverReason());
			lc.setParent(item);

			lc = new Listcell(finInsurance.getInsuranceFrq());
			lc.setParent(item);

			int format = CurrencyUtil.getFormat(financeMain.getFinCcy());
			if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {

				if (finInsurance.getCalType().equals(InsuranceConstants.CALTYPE_CON_AMT)) {
					lc = new Listcell(PennantAppUtil.amountFormate(finInsurance.getAmount(), format));
				} else {
					lc = new Listcell(PennantAppUtil.amountFormate(BigDecimal.ZERO, format));
				}
			} else {
				lc = new Listcell(PennantAppUtil.amountFormate(finInsurance.getAmount(), format));
			}
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(finInsurance.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(PennantJavaUtil.getLabel(finInsurance.getRecordType()));
			lc.setParent(item);

			item.setAttribute("data", finInsurance);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onFinInsuranceItemDoubleClicked");
			this.listBoxInsuranceDetails.appendChild(item);
		}
	}

	public void onFinInsuranceItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinInsurances finInsurance = (FinInsurances) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(finInsurance.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			finInsurance.setNewRecord(false);
			doShowInsuranceDialog(finInsurance);
		}

		logger.debug("Leaving");

	}

	/**
	 * @param financeMain
	 * @param customer
	 * @param financeType
	 * @return
	 */
	private HashMap<String, Object> getDataMap(FinanceMain financeMain, Customer customer, FinanceType financeType) {
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		if (financeMain != null) {
			dataMap.putAll(financeMain.getDeclaredFieldValues());
		}
		if (customer != null) {
			dataMap.putAll(customer.getDeclaredFieldValues());
		}
		if (financeType != null) {
			dataMap.putAll(financeType.getDeclaredFieldValues());
		}
		return dataMap;
	}

	public boolean isInsuranceReExecute() {

		return feeChargesExecuted;

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public boolean isFeeChargesExecuted() {
		return feeChargesExecuted;
	}

	public void setFeeChargesExecuted(boolean feeChargesExecuted) {
		this.feeChargesExecuted = feeChargesExecuted;
	}

	public Map<String, BigDecimal> getWaiverPaidChargesMap() {
		return waiverPaidChargesMap;
	}

	public void setWaiverPaidChargesMap(Map<String, BigDecimal> waiverPaidChargesMap) {
		this.waiverPaidChargesMap = waiverPaidChargesMap;
	}

	public Map<String, FeeRule> getFeeRuleDetailsMap() {
		return feeRuleDetailsMap;
	}

	public void setFeeRuleDetailsMap(Map<String, FeeRule> feeRuleDetailsMap) {
		this.feeRuleDetailsMap = feeRuleDetailsMap;
	}

	public Map<String, String> getFeeMethodDetailsMap() {
		return feeMethodDetailsMap;
	}

	public void setFeeMethodDetailsMap(Map<String, String> feeMethodDetailsMap) {
		this.feeMethodDetailsMap = feeMethodDetailsMap;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public TakafulProviderService getTakafulProviderService() {
		return takafulProviderService;
	}

	public void setTakafulProviderService(TakafulProviderService takafulProviderService) {
		this.takafulProviderService = takafulProviderService;
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

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public BigDecimal getAddingFeeToFinance() {
		return addingFeeToFinance;
	}

	public void setAddingFeeToFinance(BigDecimal addingFeeToFinance) {
		this.addingFeeToFinance = addingFeeToFinance;
	}

	public List<FinInsurances> getFinInsuranceList() {
		return finInsuranceList;
	}

	public void setFinInsuranceList(List<FinInsurances> finInsuranceList) {
		this.finInsuranceList = finInsuranceList;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

}