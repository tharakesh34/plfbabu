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
 * FileName    		:  FinFeeDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FeePaymentDetail;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul file.
 */
public class FinFeeDetailListCtrl extends GFCBaseCtrl<FinFeeDetail> {
	private static final long serialVersionUID = 4157448822555239535L;
	private final static Logger logger = Logger.getLogger(FinFeeDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FeeDetailList;

	protected Button btnNew_NewPaymentDetail;
	protected Listbox listBoxPaymentDetails;
	protected Listbox listBoxFeeDetail;
	protected Listbox listBoxInsuranceDetails;
	protected Button  btnNew_FeeDetailList_FinInsurance;
	protected Groupbox  gb_InsuranceDetails;
	protected Groupbox  gb_PaymentDetails;
	protected Listheader  listheader_FeeDetailList_PaymentRef;
	
	protected Listheader  listheader_FeeDetailList_FeeScheduleMethod;
	protected Listheader  listheader_FeeDetailList_Terms;
	
	// For Dynamically calling of this Controller
	private FinanceDetail financeDetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private List<FinFeeDetail> finFeeDetailList = new ArrayList<FinFeeDetail>();
	private List<FeePaymentDetail> feePaymentDetailList = new ArrayList<FeePaymentDetail>();
	private List<FinInsurances>	 finInsuranceList		= new ArrayList<FinInsurances>();
	private int ccyFormat=0;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	private  String		ModuleType_Loan="LOAN";
	private boolean	 isWIF = false;
	
	public static final String FEE_UNIQUEID_CALCULATEDAMOUNT = "CALAMT"; 
	public static final String FEE_UNIQUEID_ACTUALAMOUNT = "ACTUALAMT"; 
	public static final String FEE_UNIQUEID_WAIVEDAMOUNT = "WAIVEDAMT"; 
	public static final String FEE_UNIQUEID_PAIDAMOUNT = "PAIDAMT"; 
	public static final String FEE_UNIQUEID_PAYMENTMETHOD = "PAYMETHD"; 
	public static final String FEE_UNIQUEID_FEESCHEDULEMETHOD = "FEEMTHD"; 
	public static final String FEE_UNIQUEID_TERMS = "TERMS"; 
	public static final String FEE_UNIQUEID_REMAININGFEE = "REMFEE"; 
	
	private FinanceDetailService financeDetailService;
	private RuleService ruleService;
	private CustomerService customerService;
	private AccountEngineExecution engineExecution;
	private RuleExecutionUtil ruleExecutionUtil;
	private FinScheduleData 		finScheduleData = null;
	private FinanceMain 			financeMain = null;
	private boolean	 dataChanged = false;
	private String	 numberOfTermsLabel = "";
	private String	 moduleDefiner = "";
	private String	eventCode	   = "";

	/**
	 * default constructor.<br>
	 */
	public FinFeeDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinFeeDetailListCtrl";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinAdvancePayment object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FeeDetailList(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FeeDetailList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_FeeDetailList.setTitle("");
				setNewFinance(true);
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "FinFeeDetailListCtrl");
			}
			
			if (arguments.containsKey("ccyFormatter")) {
				ccyFormat=Integer.parseInt(arguments.get("ccyFormatter").toString());
			}
			
			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}
			
			if (arguments.containsKey("numberOfTermsLabel")) {
				numberOfTermsLabel = (String) arguments.get("numberOfTermsLabel");
			}
			
			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}
			
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}
			
			if (arguments.containsKey("eventCode")) {
				eventCode = (String) arguments.get("eventCode");
			}

			if(arguments.containsKey("financeMainDialogCtrl")){
				this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
				try {
						financeMainDialogCtrl.getClass().getMethod("setFinFeeDetailListCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.window_FeeDetailList.setTitle("");
			}
			
			if (arguments.containsKey("isWIF")) {
				isWIF = (Boolean) arguments.get("isWIF");
			}
			
			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}
			
			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog(getFinanceDetail());
		} catch (Exception e) {
			createException(window_FeeDetailList, e);
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}
		
	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}
	
	
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FinFeeDetailListCtrl",roleCode);
		this.btnNew_NewPaymentDetail.setVisible(getUserWorkspace().isAllowed("FinFeeDetailListCtrl_NewPaymentDetail"));
		this.btnNew_FeeDetailList_FinInsurance.setVisible(getUserWorkspace().isAllowed("FinFeeDetailListCtrl_NewFinInsurance"));
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if(isWIF){
			return false;
		}else if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}	
		return false;
	}
	
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail financeDetail) throws InterruptedException {
		logger.debug("Entering");
	
		try {
			appendFinBasicDetails();
			doCheckEnquiry();
			if (financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate() != null) {
				String feeEvent = "";
				if(StringUtils.isBlank(moduleDefiner)){
					if (financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate().after(DateUtility.getAppDate())) {
						if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
							feeEvent = AccountEventConstants.ACCEVENT_ADDDBSF;
						} else {
							feeEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
						}
					} else {
						feeEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
					}
				}else{
					feeEvent = eventCode;
				}
				financeDetail.getFinScheduleData().setFeeEvent(feeEvent);
			}
			doWriteBeanToComponents(financeDetail);
			int divHeight = this.borderLayoutHeight - 80;
			int semiBorderlayoutHeights = divHeight / 2;
			this.listBoxFeeDetail.setHeight(semiBorderlayoutHeights - 85 +"px");
			this.listBoxPaymentDetails.setHeight(semiBorderlayoutHeights - 105 +"px");
			this.listBoxInsuranceDetails.setHeight(semiBorderlayoutHeights - 105 +"px");
			if(isWIF){
				this.gb_PaymentDetails.setVisible(false);
				this.listheader_FeeDetailList_PaymentRef.setVisible(false);
			}else{
				this.gb_PaymentDetails.setVisible(false);
				this.listheader_FeeDetailList_PaymentRef.setVisible(false);
			}
			if (parent != null) {
				this.window_FeeDetailList.setHeight(borderLayoutHeight-75+"px");
				parent.appendChild(this.window_FeeDetailList);
			}
			
			if (StringUtils.isNotBlank(this.moduleDefiner)) {
				this.listheader_FeeDetailList_FeeScheduleMethod.setVisible(false);
				this.listheader_FeeDetailList_Terms.setVisible(false);
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param commodityHeader
	 *            
	 */
	public void doWriteBeanToComponents(FinanceDetail financeDetail) {
		logger.debug("Entering ");
		doFillFeePaymentDetails(financeDetail.getFeePaymentDetailList(),false);
		if(financeDetail.isNewRecord() || StringUtils.isEmpty(financeDetail.getFinScheduleData().getFinanceMain().getRecordType())){
			if(!financeDetail.getFinScheduleData().getFinFeeDetailActualList().isEmpty()){
				for (FinFeeDetail finFeeDetail : financeDetail.getFinScheduleData().getFinFeeDetailActualList()) {
					finFeeDetail.setNewRecord(true);
					finFeeDetail.setRecordType("");
					finFeeDetail.setRecordStatus("");
				}
				doFillFinFeeDetailList(financeDetail.getFinScheduleData().getFinFeeDetailActualList());
			}else{
				finFeeDetailList = convertToFinanceFees(financeDetail.getFinTypeFeesList());
				calculateFees(finFeeDetailList);
				doFillFinFeeDetailList(finFeeDetailList);
			}
		}else{
			doFillFinFeeDetailList(financeDetail.getFinScheduleData().getFinFeeDetailActualList());
		}
		doFillFinInsurances(financeDetail.getFinScheduleData().getFinInsuranceList());
		logger.debug("Leaving ");
	}
	
	
	private List<FinFeeDetail> convertToFinanceFees(List<FinTypeFees> finTypeFeesList){
		List<FinFeeDetail> finFeeDetails = new ArrayList<FinFeeDetail>();
		if(finTypeFeesList != null && !finTypeFeesList.isEmpty()){
			FinFeeDetail finFeeDetail = null;
			for (FinTypeFees finTypeFee : finTypeFeesList) {
				finFeeDetail = new FinFeeDetail();
				finFeeDetail.setNewRecord(true);
				finFeeDetail.setOriginationFee(finTypeFee.isOriginationFee());
				finFeeDetail.setFinEvent(finTypeFee.getFinEvent());
				finFeeDetail.setFinEventDesc(finTypeFee.getFinEventDesc());
				finFeeDetail.setFeeTypeID(finTypeFee.getFeeTypeID());
				finFeeDetail.setFeeOrder(finTypeFee.getFeeOrder());
				finFeeDetail.setFeeTypeCode(finTypeFee.getFeeTypeCode());
				finFeeDetail.setFeeTypeDesc(finTypeFee.getFeeTypeDesc());
				
				finFeeDetail.setFeeScheduleMethod(finTypeFee.getFeeScheduleMethod());
				finFeeDetail.setCalculationType(finTypeFee.getCalculationType());
				finFeeDetail.setRuleCode(finTypeFee.getRuleCode());
				finFeeDetail.setFixedAmount(finTypeFee.getAmount());
				finFeeDetail.setPercentage(finTypeFee.getPercentage());
				finFeeDetail.setCalculateOn(finTypeFee.getCalculateOn());
				finFeeDetail.setAlwDeviation(finTypeFee.isAlwDeviation());
				finFeeDetail.setMaxWaiverPerc(finTypeFee.getMaxWaiverPerc());
				finFeeDetail.setAlwModifyFee(finTypeFee.isAlwModifyFee());
				finFeeDetail.setAlwModifyFeeSchdMthd(finTypeFee.isAlwModifyFeeSchdMthd());
				
				finFeeDetail.setCalculatedAmount(finTypeFee.getAmount());
				finFeeDetail.setActualAmount(finTypeFee.getAmount());
				if(StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)){
					finFeeDetail.setPaidAmount(finTypeFee.getAmount());
				}
				if(StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)){
					finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
				}
				finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
				
				finFeeDetails.add(finFeeDetail);
			}
		}
		return finFeeDetails;
	}
	
	
	
	private void doCheckEnquiry() {
		if(isEnquiry){
			this.btnNew_NewPaymentDetail.setVisible(false);
		}
	}
	
	
	public void onClick$btnNew_NewPaymentDetail(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewPaymentDetail);
		doClearFeeWrongValueExceptions();
		
		final FeePaymentDetail aFeePaymentDetail = new FeePaymentDetail();
		aFeePaymentDetail.setFinReference(getFinanceDetail().getFinScheduleData().getFinReference());
		aFeePaymentDetail.setNewRecord(true);
		aFeePaymentDetail.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("feePaymentDetail", aFeePaymentDetail);
		map.put("ccyFormatter", ccyFormat);
		map.put("finFeeDetailListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
		map.put("moduleType", ModuleType_Loan);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeePaymentDetailDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e.toString());
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onPaymentDetailDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewPaymentDetail);
		doClearFeeWrongValueExceptions();

		Listitem listitem = this.listBoxPaymentDetails.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FeePaymentDetail aFeePaymentDetail = (FeePaymentDetail) listitem.getAttribute("data");
			if (isDeleteRecord(aFeePaymentDetail.getRecordType())) {
				MessageUtil.showErrorMessage(Labels.getLabel("common_NoMaintainance"));
			}else{
				aFeePaymentDetail.setNewRecord(false);

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("feePaymentDetail", aFeePaymentDetail);
				map.put("ccyFormatter", ccyFormat);
				map.put("finFeeDetailListCtrl", this);
				map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
				map.put("roleCode", roleCode);
				map.put("enqModule", isEnquiry);
				map.put("moduleType", ModuleType_Loan);

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeePaymentDetailDialog.zul", null, map);
				} catch (Exception e) {
					logger.error("Exception: Opening window", e);
					MessageUtil.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillFeePaymentDetails(List<FeePaymentDetail> feePaymentDetails,boolean isDataModify) {
		logger.debug("Entering");
		this.listBoxPaymentDetails.getItems().clear();
		setFeePaymentDetailList(feePaymentDetails);
		getFinanceDetail().setFeePaymentDetailList(feePaymentDetails);
		if (feePaymentDetails != null && !feePaymentDetails.isEmpty()) {
			for (FeePaymentDetail detail : feePaymentDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getPaymentReference());
				lc.setParent(item);
				lc = new Listcell(detail.getPaymentMethod());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getPaymentAmount(), ccyFormat));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getValueDate()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onPaymentDetailDoubleClicked");
				this.listBoxPaymentDetails.appendChild(item);
			}
		}
		if(isDataModify){
			doFillPaymentRefData();
		}
		logger.debug("Leaving");
	}
	
	public void processFeeDetails(FinScheduleData aFinScheduleData){
		logger.debug("Entering");
		if(aFinScheduleData !=null){
			List<FinFeeDetail> finFeeDetailList = fetchFeeDetails(aFinScheduleData,true);
			Cloner cloner = new Cloner();
			finFeeDetailList = cloner.deepClone(finFeeDetailList);
			if (finFeeDetailList != null && !finFeeDetailList.isEmpty()) {
				for (FinFeeDetail finFeeDetail : finFeeDetailList) {
					finFeeDetail.setFinReference(aFinScheduleData.getFinanceMain().getFinReference());
					finFeeDetail.setRecordStatus(aFinScheduleData.getFinanceMain().getRecordStatus());
					finFeeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
					finFeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					finFeeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
				}
			}
			aFinScheduleData.setFinFeeDetailList(finFeeDetailList);
		}
		logger.debug("Leaving");
	}
	
	private List<FinFeeDetail> fetchFeeDetails(FinScheduleData aFinScheduleData, boolean validate) {
		logger.debug("Entering");
		doClearFeeWrongValueExceptions();

		Decimalbox calbox;
		Decimalbox actualBox;
		Decimalbox paidBox;
		Decimalbox waivedBox;
		Decimalbox remFeeBox;
		Combobox feeSchdMthdbox;
		Combobox paymentMthdbox;
		Intbox termsbox;

		if (this.finFeeDetailList != null && !this.finFeeDetailList.isEmpty()) {
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
			int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());
			for (FinFeeDetail finFeeDetail : this.finFeeDetailList) {
				if (!finFeeDetail.isRcdVisible()) {
					continue;
				}

				calbox = (Decimalbox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_CALCULATEDAMOUNT,
						finFeeDetail));
				actualBox = (Decimalbox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_ACTUALAMOUNT,
						finFeeDetail));
				paidBox = (Decimalbox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_PAIDAMOUNT,
						finFeeDetail));
				waivedBox = (Decimalbox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_WAIVEDAMOUNT,
						finFeeDetail));
				remFeeBox = (Decimalbox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_REMAININGFEE,
						finFeeDetail));
				paymentMthdbox = (Combobox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_PAYMENTMETHOD,
						finFeeDetail));
				feeSchdMthdbox = (Combobox) this.listBoxFeeDetail.getFellow(getComponentId(
						FEE_UNIQUEID_FEESCHEDULEMETHOD, finFeeDetail));
				termsbox = (Intbox) this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_TERMS, finFeeDetail));

				if (calbox != null) {
					if (validate && calbox.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(calbox, Labels.getLabel(
								"NUMBER_MINVALUE",
								new String[] { Labels.getLabel("FeeDetail_CalculateAmount"),
										String.valueOf(BigDecimal.ZERO) }));
					}
					finFeeDetail.setCalculatedAmount(PennantAppUtil.unFormateAmount(
							BigDecimal.valueOf(calbox.doubleValue()), formatter));
				}

				if (actualBox != null) {
					if (validate &&  actualBox.getValue() != null && actualBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(actualBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
								Labels.getLabel("FeeDetail_ActualAmount"), String.valueOf(BigDecimal.ZERO) }));
					}
					finFeeDetail.setActualAmount(PennantAppUtil.unFormateAmount(
							BigDecimal.valueOf(actualBox.doubleValue()), formatter));
				}

				try {
					if (paidBox != null) {
						if (validate && paidBox.getValue() != null && paidBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
							throw new WrongValueException(paidBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
									Labels.getLabel("FeeDetail_PaidAmount"), String.valueOf(BigDecimal.ZERO) }));
						}
						if (validate
								&& BigDecimal.valueOf(paidBox.doubleValue()).compareTo(
										PennantAppUtil.formateAmount(finFeeDetail.getActualAmount(), formatter)) > 0) {
							throw new WrongValueException(paidBox, Labels.getLabel(
									"label_FeeDetail_Validation_Exceed",
									new String[] { Labels.getLabel("FeeDetail_PaidAmount"),
											Labels.getLabel("FeeDetail_ActualAmount") }));
						}
						finFeeDetail.setPaidAmount(PennantAppUtil.unFormateAmount(
								BigDecimal.valueOf(paidBox.doubleValue()), formatter));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				if (waivedBox != null) {
					if (validate && waivedBox.getValue() != null && waivedBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(waivedBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
								Labels.getLabel("FeeDetail_WaivedAmount"), String.valueOf(BigDecimal.ZERO) }));
					}
					BigDecimal waivedAmtTemp = BigDecimal.valueOf(waivedBox.doubleValue());
					try {
						if (validate
								&& waivedAmtTemp.compareTo(PennantAppUtil.formateAmount(finFeeDetail.getActualAmount(),
										formatter)) > 0) {
							throw new WrongValueException(waivedBox, Labels.getLabel(
									"label_FeeDetail_Validation_Exceed",
									new String[] { Labels.getLabel("FeeDetail_WaivedAmount"),
											Labels.getLabel("FeeDetail_ActualAmount") }));
						}
						if (finFeeDetail.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal alwWaiverAmt = finFeeDetail.getActualAmount()
									.multiply(finFeeDetail.getMaxWaiverPerc())
									.divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
							alwWaiverAmt = PennantAppUtil.formateAmount(alwWaiverAmt, formatter);
							if (validate && waivedAmtTemp.compareTo(alwWaiverAmt) > 0) {
								throw new WrongValueException(waivedBox, Labels.getLabel(
										"label_Fee_WaiverPercentage_Exceed",
										new String[] { String.valueOf(finFeeDetail.getMaxWaiverPerc()),
												alwWaiverAmt.toPlainString() }));
							}
						}
						finFeeDetail.setWaivedAmount(PennantAppUtil.unFormateAmount(waivedAmtTemp, formatter));
						BigDecimal nonOutstandingFeeAmt = finFeeDetail.getPaidAmount().add(
								finFeeDetail.getWaivedAmount());
						if (validate && finFeeDetail.getPaidAmount().compareTo(finFeeDetail.getActualAmount()) <= 0
								&& nonOutstandingFeeAmt.compareTo(finFeeDetail.getActualAmount()) > 0) {
							throw new WrongValueException(waivedBox,
									Labels.getLabel("label_Fee_PaidWaiverAmount_Exceed"));
						}

					} catch (WrongValueException we) {
						wve.add(we);
					}
				}

				try {
					if (remFeeBox != null) {
						if (validate && remFeeBox.getValue() != null && remFeeBox.getValue().compareTo(BigDecimal.ZERO) < 0) {
							throw new WrongValueException(remFeeBox, Labels.getLabel("NUMBER_MINVALUE", new String[] {
									Labels.getLabel("FeeDetail_RemFeeAmount"), String.valueOf(BigDecimal.ZERO) }));
						}
						BigDecimal remainingFee = PennantAppUtil.unFormateAmount(
								BigDecimal.valueOf(remFeeBox.doubleValue()), formatter);

						if (StringUtils.isNotBlank(this.moduleDefiner) && remainingFee.compareTo(BigDecimal.ZERO) != 0) {
							throw new WrongValueException(remFeeBox,
									Labels.getLabel("label_Fee_PaidWaiverAmount_NotEqual"));
						}

						finFeeDetail.setRemainingFee(remainingFee);
					}
				} catch (WrongValueException we) {
					wve.add(we);

				}

				try {
					if (paymentMthdbox != null) {
						if (validate && finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
							if ("#".equals(getComboboxValue(paymentMthdbox))) {
								// throw new WrongValueException(paymentMthdbox, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("FeeDetail_PaymentRef") }));
							}
						}
						finFeeDetail.setPaymentRef(getComboboxValue(paymentMthdbox));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if (feeSchdMthdbox != null) {
						if (validate && finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) > 0) {
							if ("#".equals(getComboboxValue(feeSchdMthdbox))) {
								throw new WrongValueException(feeSchdMthdbox, Labels.getLabel("STATIC_INVALID",
										new String[] { Labels.getLabel("FeeDetail_FeeScheduleMethod") }));
							}
						}
						finFeeDetail.setFeeScheduleMethod(getComboboxValue(feeSchdMthdbox));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if (termsbox != null) {
						if (validate && !termsbox.isReadonly() && !termsbox.isDisabled() && termsbox.intValue() == 0) {
							throw new WrongValueException(termsbox, Labels.getLabel("FIELD_IS_MAND",
									new String[] { Labels.getLabel("label_Fee_Terms") }));
						}
						if (validate && !termsbox.isReadonly() && !termsbox.isDisabled() && termsbox.intValue() < 0) {
							throw new WrongValueException(termsbox, Labels.getLabel("NUMBER_NOT_NEGATIVE",
									new String[] { Labels.getLabel("label_Fee_Terms") }));
						}
						if (validate && !termsbox.isReadonly() && !termsbox.isDisabled()
								&& termsbox.intValue() > aFinScheduleData.getFinanceMain().getNumberOfTerms()) {
							throw new WrongValueException(termsbox, Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
									Labels.getLabel("listheader_FeeDetailList_Terms.label"), numberOfTermsLabel }));
						}
						finFeeDetail.setTerms(termsbox.intValue());
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				if (aFinScheduleData.getFinanceMain().isWorkflow()) {
					// Saving Properties
					if (finFeeDetail.isNewRecord()) {
						finFeeDetail.setDataModified(true);
						finFeeDetail.setVersion(1);
						finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
					} else {
						finFeeDetail.setDataModified(isDataMaintained(finFeeDetail, finFeeDetail.getBefImage()));
					}
				} else {
					if (finFeeDetail.isNewRecord()) {
						finFeeDetail.setDataModified(true);
						finFeeDetail.setVersion(1);
						finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
					} else {
						finFeeDetail.setDataModified(isDataMaintained(finFeeDetail, finFeeDetail.getBefImage()));
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
				finFeeDetail.setRecordStatus(this.recordStatus.getValue());
			}
			
			showErrorDetails(wve);
		}

		logger.debug("Leaving");

		return this.finFeeDetailList;
	}
	
	private boolean isDataMaintained(FinFeeDetail finFeeDetail,FinFeeDetail finFeeDetailBefImage){
		if(finFeeDetailBefImage == null){
			return true;
		}else{
			for(Field field : finFeeDetail.getClass().getDeclaredFields()){
				if(StringUtils.equalsIgnoreCase(field.getName(),"befImage") ||
						StringUtils.equalsIgnoreCase(field.getName(),"validateFinFeeDetail")){
					continue;
				}
				try {
					field.setAccessible(true);
					if(field.get(finFeeDetail) == null && field.get(finFeeDetailBefImage) != null){
						return true;
					}else if(field.get(finFeeDetail) != null && field.get(finFeeDetailBefImage) == null){
						return true;
					}else if(field.get(finFeeDetail) != null && field.get(finFeeDetailBefImage) != null && 
							!field.get(finFeeDetail).equals(field.get(finFeeDetailBefImage))){
						return true;
					}
				}  catch (Exception e) {
					return true;
				}
			}
		}
		return false;
	}
	

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if(parentTab != null){
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}
	
	public void doFillFinFeeDetailList(List<FinFeeDetail> finFeeDetails) {
		logger.debug("Entering");
		this.listBoxFeeDetail.getItems().clear();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		int formatter =  CurrencyUtil.getFormat(finMain.getFinCcy());
		finFeeDetails = sortFeesByFeeOrder(finFeeDetails);
		setFinFeeDetailList(finFeeDetails);
		boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance"); 
		if (finMain.isQuickDisb() && readOnly) {
			readOnly=isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP"); 
		}

		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail detail : finFeeDetails) {
				if(!detail.isRcdVisible()){
					continue;
				}
				if(!detail.isNewRecord()){
					FinFeeDetail befImage = new FinFeeDetail();
					BeanUtils.copyProperties(detail, befImage);
					detail.setBefImage(befImage);
				}
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getFeeTypeDesc());
				lc.setParent(item);
				
				//Calculate Amount
				Decimalbox calBox = new Decimalbox();
				calBox.setMaxlength(18);
				calBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				calBox.setDisabled(true);
				calBox.setId(getComponentId(FEE_UNIQUEID_CALCULATEDAMOUNT, detail));
				calBox.setValue(PennantAppUtil.formateAmount(detail.getCalculatedAmount(),formatter));
				lc = new Listcell();
				lc.appendChild(calBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);		
				
				//Actual Amount
				Decimalbox actualBox = new Decimalbox();
				actualBox.setMaxlength(18);
				actualBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				actualBox.setDisabled(readOnly ? true : !detail.isAlwModifyFee());
				actualBox.setId(getComponentId(FEE_UNIQUEID_ACTUALAMOUNT, detail));
				actualBox.setValue(PennantAppUtil.formateAmount(detail.getActualAmount(),formatter));
				lc = new Listcell();
				lc.setStyle("text-align:right;");
				lc.appendChild(actualBox);
				lc.setParent(item);
				
				//Waived Amount
				Decimalbox waiverBox = new Decimalbox();
				waiverBox.setMaxlength(18);
				waiverBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				if(detail.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) > 0){
					waiverBox.setDisabled(readOnly);
				}else{
					waiverBox.setDisabled(true);
				}
				waiverBox.setId(getComponentId(FEE_UNIQUEID_WAIVEDAMOUNT, detail));
				waiverBox.setValue(PennantAppUtil.formateAmount(detail.getWaivedAmount(),formatter));
				lc = new Listcell();
				lc.appendChild(waiverBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				//Paid Amount
				Decimalbox paidBox = new Decimalbox();
				paidBox.setMaxlength(18);
				paidBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				paidBox.setDisabled(readOnly);
				paidBox.setId(getComponentId(FEE_UNIQUEID_PAIDAMOUNT, detail));
				paidBox.setValue(PennantAppUtil.formateAmount(detail.getPaidAmount(),formatter));
				lc = new Listcell();
				lc.appendChild(paidBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);		
				
				//Remaining Fee
				Decimalbox remFeeBox = new Decimalbox();
				remFeeBox.setMaxlength(18);
				remFeeBox.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				remFeeBox.setDisabled(true);
				remFeeBox.setId(getComponentId(FEE_UNIQUEID_REMAININGFEE, detail));
				remFeeBox.setValue(PennantAppUtil.formateAmount(detail.getRemainingFee(),formatter));
				lc = new Listcell();
				lc.appendChild(remFeeBox);
				lc.setStyle("text-align:right;");
				lc.setParent(item);	
				
				//Payment Method
				lc = new Listcell();
				Combobox payMethCombo = new Combobox();
				fillPaymentRefComboBox(payMethCombo, detail.getPaymentRef(),this.feePaymentDetailList, "");
				payMethCombo.setWidth("96%");
				payMethCombo.setId(getComponentId(FEE_UNIQUEID_PAYMENTMETHOD, detail));
				payMethCombo.setDisabled(readOnly);
				lc.appendChild(payMethCombo);
				lc.setParent(item);
				
				// Remaining Fee schedule Method
				lc = new Listcell();
				Combobox feeSchdMethCombo = new Combobox();
				String excludeFields = "";
				
				if(!(StringUtils.equals(detail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK) || 
						StringUtils.equals(detail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER))) {
					excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + "," + "," + CalculationConstants.REMFEE_PAID_BY_CUSTOMER + ",";
				} else if ((detail.getMaxWaiverPerc().compareTo(BigDecimal.ZERO) == 0)
						&& (!StringUtils.equals(detail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK))) {
					excludeFields = "," + CalculationConstants.REMFEE_WAIVED_BY_BANK + ",";
				}
				
				fillComboBox(feeSchdMethCombo, detail.getFeeScheduleMethod(), PennantStaticListUtil.getRemFeeSchdMethods(), excludeFields);
				feeSchdMethCombo.setWidth("96%");
				feeSchdMethCombo.setId(getComponentId(FEE_UNIQUEID_FEESCHEDULEMETHOD, detail));
				boolean feeSchdMthdDisable = false;
				if (detail.isAlwModifyFeeSchdMthd() && detail.getRemainingFee().compareTo(BigDecimal.ZERO) > 0) {
					feeSchdMthdDisable = readOnly;
				} else {
					feeSchdMthdDisable = true;
				}
				feeSchdMethCombo.setDisabled(feeSchdMthdDisable);
				lc.appendChild(feeSchdMethCombo);
				lc.setParent(item);
				
				if (StringUtils.equals(detail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
					remFeeBox.setValue(BigDecimal.ZERO);
					paidBox.setValue(BigDecimal.ZERO);
					paidBox.setDisabled(true);
					waiverBox.setDisabled(true);
					waiverBox.setValue(actualBox.getValue());
				} else if (StringUtils.equals(detail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					remFeeBox.setValue(BigDecimal.ZERO);
					waiverBox.setValue(BigDecimal.ZERO);
					waiverBox.setDisabled(true);
					paidBox.setDisabled(true);
					paidBox.setValue(actualBox.getValue());
				} 
				
				// Terms
				Intbox termsBox = new Intbox();
				termsBox.setMaxlength(5);
				termsBox.setId(getComponentId(FEE_UNIQUEID_TERMS, detail));
				termsBox.setValue(detail.getTerms());
				boolean termsDisable = false;
				if(detail.getRemainingFee().compareTo(BigDecimal.ZERO) > 0){
					if(StringUtils.equals(detail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)){
						termsDisable = readOnly;
					}else{
						termsDisable = true;
					}
				}else{
					termsDisable = true;
				}
				termsBox.setDisabled(termsDisable); 
				lc = new Listcell();
				lc.appendChild(termsBox);
				lc.setParent(item);
				
				this.listBoxFeeDetail.appendChild(item);
				
				List<Object> amountBoxlist = new ArrayList<Object>(8);
				amountBoxlist.add(actualBox);
				amountBoxlist.add(paidBox);
				amountBoxlist.add(waiverBox);
				amountBoxlist.add(remFeeBox);
				amountBoxlist.add(feeSchdMethCombo);
				amountBoxlist.add(termsBox);
				amountBoxlist.add(detail);
				amountBoxlist.add(finMain.isQuickDisb());
				actualBox.addForward("onChange",window_FeeDetailList,"onChangeActualBox",amountBoxlist);
				paidBox.addForward("onChange",window_FeeDetailList,"onChangeFeeAmount",amountBoxlist);
				waiverBox.addForward("onChange",window_FeeDetailList,"onChangeFeeAmount",amountBoxlist);
				termsBox.addForward("onChange",window_FeeDetailList,"onChangeFeeTerms", null);
				feeSchdMethCombo.addForward("onChange",window_FeeDetailList,"onChangeFeeScheduleMethod",amountBoxlist);
			}
		}
		logger.debug("Leaving");
	}
	
	@SuppressWarnings("unchecked")
	private List<FinFeeDetail> sortFeesByFeeOrder(List<FinFeeDetail> finFeeDetailList){
		Comparator<FinFeeDetail> beanComp = new BeanComparator("feeOrder");
		 Collections.sort(finFeeDetailList, beanComp);
		return finFeeDetailList;
	}
	
	/**
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeFeeAmount(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		List<Object> list = (List<Object>) event.getData();
		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		Decimalbox remFeeBox = (Decimalbox) list.get(3);
		Combobox feeSchdMthdBox = (Combobox) list.get(4);
		Intbox termsBox = (Intbox) list.get(5);
		FinFeeDetail detail  = (FinFeeDetail) list.get(6);
		boolean quickDisb  = (boolean) list.get(7);

		actualBox.setErrorMessage("");
		paidBox.setErrorMessage("");
		waiverBox.setErrorMessage("");
		remFeeBox.setErrorMessage("");
		feeSchdMthdBox.setErrorMessage("");
		termsBox.setErrorMessage("");

		String feeSchedule = getComboboxValue(feeSchdMthdBox);
		
		remFeeBox.setValue(BigDecimal.valueOf(actualBox.doubleValue())
				.subtract(BigDecimal.valueOf(waiverBox.doubleValue()))
				.subtract(BigDecimal.valueOf(paidBox.doubleValue())));
		if(!(StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_WAIVED_BY_BANK) || 
				StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_PAID_BY_CUSTOMER))) {
			if(detail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) == 0){
				feeSchdMthdBox.setDisabled(true);
				feeSchdMthdBox.setSelectedIndex(0);
			}
		} else {
			boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance");
			
			if (quickDisb && readOnly) {
				readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP");
			}

			boolean feeSchdMthdDisable = false;
			if(detail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) > 0){
				feeSchdMthdDisable = readOnly;
			}else{
				feeSchdMthdDisable = true;
			}

			feeSchdMthdBox.setDisabled(feeSchdMthdDisable);
		}
		
		this.dataChanged = true;

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Record each log Entry of Modification either Waiver/Paid By Customer
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeActualBox(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		List<Object> list = (List<Object>) event.getData();

		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		Decimalbox remFeeBox = (Decimalbox) list.get(3);
		Combobox feeSchdMthdBox = (Combobox) list.get(4);
		Intbox termsBox = (Intbox) list.get(5);
		FinFeeDetail detail = (FinFeeDetail) list.get(6);
		boolean quickDisb  = (boolean) list.get(7);

		actualBox.setErrorMessage("");
		paidBox.setErrorMessage("");
		waiverBox.setErrorMessage("");
		remFeeBox.setErrorMessage("");
		feeSchdMthdBox.setErrorMessage("");
		termsBox.setErrorMessage("");

		String feeSchedule = getComboboxValue(feeSchdMthdBox);
		if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
			waiverBox.setValue(actualBox.getValue());
		} else if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
			paidBox.setValue(actualBox.getValue());
		} else {
			remFeeBox.setValue(BigDecimal.valueOf(actualBox.doubleValue())
					.subtract(BigDecimal.valueOf(waiverBox.doubleValue()))
					.subtract(BigDecimal.valueOf(paidBox.doubleValue())));

			if (detail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) == 0) {
				feeSchdMthdBox.setDisabled(true);
				feeSchdMthdBox.setSelectedIndex(0);
			} else {
				boolean readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance");
				if (quickDisb && readOnly) {
					readOnly = isReadOnly("FinFeeDetailListCtrl_AlwFeeMaintenance_QDP");
				}
				boolean feeSchdMthdDisable = false;
				if(detail.isAlwModifyFeeSchdMthd() && remFeeBox.getValue().compareTo(BigDecimal.ZERO) > 0){
					feeSchdMthdDisable = readOnly;
				}else{
					feeSchdMthdDisable = true;
				}

				feeSchdMthdBox.setDisabled(feeSchdMthdDisable);
			}
		}
		
		this.dataChanged = true;
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for actions to be made on fee terms change
	 * @param event
	 */
	public void onChangeFeeTerms(ForwardEvent event){
		logger.debug("Entering" + event.toString());
		this.dataChanged = true;
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method to make related changes with respective of Fee Schedule method change
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeFeeScheduleMethod(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		List<Object> list = (List<Object>) event.getData();

		Decimalbox actualBox = (Decimalbox) list.get(0);
		Decimalbox paidBox = (Decimalbox) list.get(1);
		Decimalbox waiverBox = (Decimalbox) list.get(2);
		Decimalbox remFeeBox = (Decimalbox) list.get(3);
		Combobox feeSchdMethodBox = (Combobox) list.get(4);
		Intbox termBox = (Intbox) list.get(5);

		paidBox.setDisabled(false);
		waiverBox.setDisabled(false);
		String feeSchedule = getComboboxValue(feeSchdMethodBox);
		if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
			termBox.setDisabled(false);
		} else {
			termBox.setDisabled(true);
			termBox.setValue(0);
			if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
				remFeeBox.setValue(BigDecimal.ZERO);
				paidBox.setValue(BigDecimal.ZERO);
				paidBox.setDisabled(true);
				waiverBox.setDisabled(true);
				waiverBox.setValue(actualBox.getValue());
			} else if (StringUtils.equals(feeSchedule, CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				remFeeBox.setValue(BigDecimal.ZERO);
				waiverBox.setValue(BigDecimal.ZERO);
				waiverBox.setDisabled(true);
				paidBox.setDisabled(true);
				paidBox.setValue(actualBox.getValue());
			} 
		}
		this.dataChanged = true;

		logger.debug("Leaving" + event.toString());
	}
	
	private String getComponentId(String feeField,FinFeeDetail finFeeDetail){
		return  feeField+finFeeDetail.getFinEvent()+"_"+finFeeDetail.getFeeTypeCode();
	}
	
	private boolean isDeleteRecord(String rcdType){
		if(StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,rcdType) || 
				StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
		
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillPaymentRefComboBox(Combobox combobox, String value, List<FeePaymentDetail> list, String excludeFields) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		if(list != null){
			for (FeePaymentDetail feePaymentDetail : list) {
				if (!excludeFields.contains("," + feePaymentDetail.getPaymentReference() + ",")) {
					comboitem = new Comboitem();
					comboitem.setValue(feePaymentDetail.getPaymentReference());
					comboitem.setLabel(feePaymentDetail.getPaymentReference());
					combobox.appendChild(comboitem);
				}
				if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(feePaymentDetail.getPaymentReference()))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	private void doFillPaymentRefData(){
		if(this.finFeeDetailList != null && !this.finFeeDetailList.isEmpty()){
			Component comp = null;
			for (FinFeeDetail finFeeDetail : this.finFeeDetailList) {
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_PAYMENTMETHOD, finFeeDetail));
				if(comp != null){
					Combobox paymentMthdbox = (Combobox) comp;
					paymentMthdbox.setErrorMessage("");
					fillPaymentRefComboBox(paymentMthdbox, getComboboxValue(paymentMthdbox),this.feePaymentDetailList, "");
				}
			}
		}
	}
	
	
	public void onClick$btnNew_FeeDetailList_FinInsurance(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		FinInsurances finInsurance = new FinInsurances();
		finInsurance.setNewRecord(true);
		finInsurance.setInsuranceReq(true);
		finInsurance.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
		finInsurance.setWorkflowId(getWorkFlowId());
		doShowInsuranceDialog(finInsurance);
		logger.debug("Leaving" + event.toString());
	}

	private void doShowInsuranceDialog(FinInsurances finInsurance) {

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("finInsurance", finInsurance);
		arg.put("finFeeDetailListCtrl", this);
		arg.put("role", roleCode);
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
			
			lc = new Listcell(finInsurance.getPolicyCode());
			lc.setStyle("font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(finInsurance.getPolicyDesc());
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

			if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)) {

				if (finInsurance.getCalType().equals(InsuranceConstants.CALTYPE_CON_AMT)) {
					lc = new Listcell(PennantAppUtil.amountFormate(finInsurance.getAmount(),CurrencyUtil.getFormat(financeMain.getFinCcy())));
				} else {
					lc = new Listcell(PennantAppUtil.amountFormate(BigDecimal.ZERO,CurrencyUtil.getFormat(financeMain.getFinCcy())));
				}
			} else {
				lc = new Listcell(PennantAppUtil.amountFormate(finInsurance.getAmount(),CurrencyUtil.getFormat(financeMain.getFinCcy())));
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
	
	
	
	public void doExecuteFeeCharges(boolean isSchdCal,FinScheduleData finScheduleData) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		Clients.clearWrongValue(this.listBoxFeeDetail.getChildren());
		
		if (!isSchdCal && finScheduleData.getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
			return;
		}

		finScheduleData.setFinFeeDetailList(fetchFeeDetails(finScheduleData,false));
		
		if(StringUtils.isBlank(moduleDefiner)){
			doSetFeeChanges(finScheduleData);
		}
		
		calculateFees(getFinFeeDetailList());
		
		if(StringUtils.isBlank(moduleDefiner)){
			fetchFeeDetails(finScheduleData,true);
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
		if(financeDetail.getCustomerDetails() != null){
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
							insAmount = (finAmount.subtract(downPayAmt)).multiply(insurance.getCalPerc()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
						}
					}
					// Provider Rate Based then based on calculation Type, Amount to be calculated
					else if (insurance.getCalType().equals(InsuranceConstants.CALTYPE_PROVIDERRATE)) {
						if (insurance.getCalOn().equals(InsuranceConstants.CALCON_FINAMT)) {
							insAmount = finAmount.multiply(insurance.getInsuranceRate()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
						} else if (insurance.getCalOn().equals(InsuranceConstants.CALCON_OSAMT)) {
							insAmount = (finAmount.subtract(downPayAmt)).multiply(insurance.getInsuranceRate()).divide(new BigDecimal(100),
									RoundingMode.HALF_DOWN);
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

		//Insurance Amounts
		finScheduleData.getFinanceMain().setInsuranceAmt(insAddToDisb);
		finScheduleData.getFinanceMain().setDeductInsDisb(deductInsFromDisb);

		finScheduleData.setFinInsuranceList(getFinInsuranceList());
		
		logger.debug("Leaving");
	}
	
	
	private void doSetFeeChanges(FinScheduleData finScheduleData){
		logger.debug("Entering");
		String feeEvent = "";
		if (finScheduleData.getFinanceMain().getFinStartDate().after(DateUtility.getAppDate())) {
			if (AccountEventConstants.ACCEVENT_ADDDBSF_REQ) {
				feeEvent = AccountEventConstants.ACCEVENT_ADDDBSF;
			} else {
				feeEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
			}
		} else {
			feeEvent = AccountEventConstants.ACCEVENT_ADDDBSP;
		}
		if(!StringUtils.equals(finScheduleData.getFeeEvent(), feeEvent)){
			List<FinTypeFees> finTypeFeesList = getFinanceDetailService().getFinTypeFees(
					finScheduleData.getFinanceMain().getFinType(),feeEvent, true);
			
			getFinanceDetail().setFinTypeFeesList(finTypeFeesList);
			
			Map<String,FinFeeDetail> feeDetailMap = new HashMap<String,FinFeeDetail>();
			
			for (FinFeeDetail finFeeDetail : getFinFeeDetailUpdateList()) {
				if(!finFeeDetail.isNewRecord()){
					if(!finFeeDetail.isRcdVisible() &&
							StringUtils.equals(finFeeDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)){
						finFeeDetail.setRcdVisible(true);
						finFeeDetail.setDataModified(true);
						finFeeDetail.setNewRecord(false);
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
					}else{
						finFeeDetail.setVersion(finFeeDetail.getVersion() + 1);
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						finFeeDetail.setRcdVisible(false);
						finFeeDetail.setDataModified(true);
						feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
					}
				}
			}
			
			List<FinFeeDetail> finFeeDetailListNew = convertToFinanceFees(finTypeFeesList);
			for (FinFeeDetail finFeeDetail : finFeeDetailListNew) {
				if(!feeDetailMap.containsKey(getUniqueID(finFeeDetail))){
					feeDetailMap.put(getUniqueID(finFeeDetail), finFeeDetail);
				}
			}
			
			setFinFeeDetailList(new ArrayList<FinFeeDetail>(feeDetailMap.values()));
		}
		finScheduleData.setFeeEvent(feeEvent);
		logger.debug("Leaving");
	}
	
	
	private List<FinFeeDetail> calculateFees(List<FinFeeDetail> finFeeDetailsList){
		
		List<String> feeRuleCodes = new ArrayList<String>();
		for (FinFeeDetail finFeeDetail : finFeeDetailsList) {
			if(StringUtils.isNotEmpty(finFeeDetail.getRuleCode())){
				feeRuleCodes.add(finFeeDetail.getRuleCode());
			}
		}
		
		if(feeRuleCodes.size() > 0){
			List<Rule> feeRules =  getRuleService().getRuleDetailList(feeRuleCodes, RuleConstants.MODULE_FEES, finScheduleData.getFeeEvent());
			if(feeRules != null && !feeRules.isEmpty()){
				HashMap<String, Object> executionMap = new HashMap<String, Object>();
				Map<String,String> ruleSqlMap = new HashMap<String,String>();
				List<Object> objectList = new ArrayList<Object>();
				if (getFinanceDetail().getCustomerDetails() != null) {
					objectList.add(getFinanceDetail().getCustomerDetails().getCustomer());
					if (getFinanceDetail().getCustomerDetails().getCustEmployeeDetail() != null) {
						objectList.add(getFinanceDetail().getCustomerDetails().getCustEmployeeDetail());
					}
				}
				if (getFinanceDetail().getFinScheduleData() != null) {
					objectList.add(getFinanceDetail().getFinScheduleData().getFinanceMain());
					objectList.add(getFinanceDetail().getFinScheduleData().getFinanceType());
				}
				for (Rule feeRule : feeRules) {
					if (feeRule.getFields() != null) {
						String[] fields = feeRule.getFields().split(",");
						for(String field : fields) {
							if (!executionMap.containsKey(field)) {
								getRuleExecutionUtil().setExecutionMap(field, objectList, executionMap);
							}
						}
					}
					ruleSqlMap.put(feeRule.getRuleCode(), feeRule.getSQLRule());
				}
				for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
					if(StringUtils.isEmpty(finFeeDetail.getRuleCode())){
						continue;
					}
					BigDecimal feeResult = getFeeResult(ruleSqlMap.get(finFeeDetail.getRuleCode()), executionMap,
							finScheduleData.getFinanceMain().getFinCcy());
					finFeeDetail.setCalculatedAmount(feeResult);
					if(finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) == 0){
						finFeeDetail.setActualAmount(feeResult);
					}
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).
							subtract(finFeeDetail.getWaivedAmount()));
				}
			}
		}
		calculateFeePercentageAmount(finScheduleData);
		
		BigDecimal deductFeeFromDisbTot = BigDecimal.ZERO;
		BigDecimal feeAddToDisbTot = BigDecimal.ZERO;
		for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
			if(StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)){
				deductFeeFromDisbTot = deductFeeFromDisbTot.add(finFeeDetail.getRemainingFee());
			}else if(StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
				feeAddToDisbTot = feeAddToDisbTot.add(finFeeDetail.getRemainingFee());
			}else if(StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)){
				if(finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) == 0){
					finFeeDetail.setPaidAmount(finFeeDetail.getActualAmount());
				}
			}else if(StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)){
				if(finFeeDetail.getWaivedAmount().compareTo(BigDecimal.ZERO) == 0){
					finFeeDetail.setWaivedAmount(finFeeDetail.getActualAmount());
				}
			}
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).
							subtract(finFeeDetail.getWaivedAmount()));
		}
	
		//FIXME as discussed should be added in finance main table
		if (StringUtils.isBlank(getFinanceDetail().getModuleDefiner())) {
		finScheduleData.getFinanceMain().setDeductFeeDisb(deductFeeFromDisbTot);
        finScheduleData.getFinanceMain().setFeeChargeAmt(feeAddToDisbTot);
		}
		doFillFinFeeDetailList(getFinFeeDetailUpdateList());
		
		finScheduleData.setFinFeeDetailList(getFinFeeDetailList());
		
		
		return finFeeDetailsList;
	}
	
	private String getUniqueID(FinFeeDetail finFeeDetail){
		return StringUtils.trimToEmpty(finFeeDetail.getFinEvent())+"_"+
				String.valueOf(finFeeDetail.getFeeTypeID());
	}
	

	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	public BigDecimal getFeeResult(String sqlRule, HashMap<String, Object> executionMap,String finCcy) {
		logger.debug("Entering");
		BigDecimal result = BigDecimal.ZERO;
		
		try {
			Object exereslut =  getRuleExecutionUtil().executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut ==null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			}else{
				result = new BigDecimal(exereslut.toString());
			}
			
		} catch (Exception e) {
			logger.debug(e);
		}
		
		
		logger.debug("Leaving");
		return result;
	}
	
	
	private void calculateFeePercentageAmount(FinScheduleData finScheduleData){
		if(getFinFeeDetailList() != null && !getFinFeeDetailList().isEmpty()){
			for (FinFeeDetail finFeeDetail : getFinFeeDetailList()) {
				if(StringUtils.equals(finFeeDetail.getCalculationType(), PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE)){
					BigDecimal calPercentageFee = getCalculatedPercentageFee(finFeeDetail,finScheduleData);
					finFeeDetail.setCalculatedAmount(calPercentageFee);
					if(finFeeDetail.getActualAmount().compareTo(BigDecimal.ZERO) == 0){
						finFeeDetail.setActualAmount(calPercentageFee);
					}
					finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getPaidAmount()).
							subtract(finFeeDetail.getWaivedAmount()));
				}
			}
		}
	}
	
	private BigDecimal getCalculatedPercentageFee(FinFeeDetail finFeeDetail,FinScheduleData finScheduleData){
		BigDecimal calculatedAmt = BigDecimal.ZERO;
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		switch (finFeeDetail.getCalculateOn()) {
		case PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = financeMain.getFinAssetValue();
			break;
		case PennantConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = financeMain.getFinAmount().subtract(financeMain.getDownPayment());
			break;
		default:
			break;
		}
		calculatedAmt = calculatedAmt.multiply(finFeeDetail.getPercentage()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_DOWN);
		return calculatedAmt;
	}
	
	
	/**
	 * Method for Fetching Customer Data to Calculate
	 * @return
	 */
	public AEAmountCodes getCustomerData(AEAmountCodes amountCodes){
		logger.debug("Entering");
		try {
			amountCodes = (AEAmountCodes) getFinanceMainDialogCtrl().getClass().getMethod("doGetFeeCustomerData", 
					AEAmountCodes.class).invoke(getFinanceMainDialogCtrl(), amountCodes);
			logger.debug("Leaving");
			return  amountCodes;	
		} catch (Exception e) {
			logger.info(e);
		}
		logger.debug("Leaving");
		return amountCodes;	
	}
	
	
	private void doClearFeeWrongValueExceptions(){
		Component comp = null;
		if(this.finFeeDetailList != null){
			for (FinFeeDetail finFeeDetail : this.finFeeDetailList) {
				if(!finFeeDetail.isRcdVisible()){
					continue;
				}
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_WAIVEDAMOUNT, finFeeDetail));
				if(comp != null){
					Decimalbox waivedbox = (Decimalbox) comp;
					waivedbox.setErrorMessage("");
				}
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_PAIDAMOUNT, finFeeDetail));
				if(comp != null){
					Decimalbox paidbox = (Decimalbox) comp;
					paidbox.setErrorMessage("");
				}
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_PAYMENTMETHOD, finFeeDetail));
				if(comp != null){
					Combobox payMethodBox = (Combobox) comp;
					payMethodBox.setErrorMessage("");
				}
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_FEESCHEDULEMETHOD, finFeeDetail));
				if(comp != null){
					Combobox feeSchdBox = (Combobox) comp;
					feeSchdBox.setErrorMessage("");
				}
				comp = this.listBoxFeeDetail.getFellow(getComponentId(FEE_UNIQUEID_TERMS, finFeeDetail));
				if(comp != null){
					Intbox termbox = (Intbox) comp;
					termbox.setErrorMessage("");
				}
			}
		}
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
	
	public void doReSetDataChanged() {
		this.dataChanged = false;
	}
	
	public boolean isDataChanged() {
		return dataChanged;
	}
	
	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	
	public List<FinFeeDetail> getFinFeeDetailUpdateList() {
		return this.finFeeDetailList;
	}
	public List<FinFeeDetail> getFinFeeDetailList() {
		List<FinFeeDetail> finFeeDetailTemp = new ArrayList<FinFeeDetail>();
		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if(finFeeDetail.isRcdVisible()){
				finFeeDetailTemp.add(finFeeDetail);
			}
		}
		return finFeeDetailTemp;
	}
	public void setFinFeeDetailList(List<FinFeeDetail> finFeeDetailList) {
		this.finFeeDetailList = finFeeDetailList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<FeePaymentDetail> getFeePaymentDetailList() {
		return feePaymentDetailList;
	}
	public void setFeePaymentDetailList(List<FeePaymentDetail> feePaymentDetailList) {
		this.feePaymentDetailList = feePaymentDetailList;
	}

	public List<FinInsurances> getFinInsuranceList() {
		return finInsuranceList;
	}
	public void setFinInsuranceList(List<FinInsurances> finInsuranceList) {
		this.finInsuranceList = finInsuranceList;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
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

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

}
