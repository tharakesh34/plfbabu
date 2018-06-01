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
 * FileName    		:  JointAccountDetailDialogCtrl.java                                    * 	  
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
 * 10-05-2019		Srinivasa Varma			 0.2		  Development Item 82               *  
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.webui.verification.FieldVerificationDialogCtrl;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class JointAccountDetailDialogCtrl extends GFCBaseCtrl<JointAccountDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(JointAccountDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_JointAccountDetailDialog; // autoWired not auto
	// wired variables
	private FinanceDetail financeDetail = null; // over handed per parameters
	private FinanceMain financeMain = null; // over handed per parameters
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();
	// Joint Account and Gurantors Details
	protected Listbox listBoxGurantorsDetail;
	protected Button btnAddGurantorDetails;
	protected Button btnAddJointDetails;
	protected Listbox listBoxJountAccountDetails; // autoWired
	private List<JointAccountDetail> jountAccountDetailList = new ArrayList<JointAccountDetail>();
	private List<JointAccountDetail> oldVar_JountAccountDetailList = new ArrayList<JointAccountDetail>();
	private List<GuarantorDetail> guarantorDetailList = new ArrayList<GuarantorDetail>();
	private List<GuarantorDetail> oldVar_GuarantorDetailList = new ArrayList<GuarantorDetail>();
	int ccDecimal = 0;
	private String finreference = "";
	private String custCIF = "";
	private Object financeMainDialogCtrl;
	private String ccy = "";

	private GuarantorDetailService guarantorDetailService;
	private JointAccountDetailService jointAccountDetailService;

	private String roleCode = "";
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	private FieldVerificationDialogCtrl fieldVerificationDialogCtrl;
	protected Groupbox finBasicdetails;
	private Object mainController;
	private boolean enquiry;
	private	boolean fromApproved;
	//### 10-05-2018 Start Development Item 82
	private Map<String, Object> rules= new HashMap<>();
	
	public Map<String, Object> getRules() {
		return rules;
	}

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
	}

	//### 10-05-2018 End Development Item 82
	/**
	 * default constructor.<br>
	 */
	public JointAccountDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_JointAccountDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_JointAccountDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			finreference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
			custCIF = financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF();
			ccDecimal = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
			ccy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();
		}
		
		if (arguments.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
		}
		if (arguments.containsKey("financeMain")) {
			this.financeMain = (FinanceMain) arguments.get("financeMain");
		}

		if (arguments.containsKey("roleCode")) {
			roleCode = (String) arguments.get("roleCode");
		}
		if (arguments.containsKey("enquiry")) {
			enquiry = (boolean) arguments.get("enquiry");
		}
		
		if (arguments.containsKey("mainController")) {
			setMainController(arguments.get("mainController"));
		}
		
		if (arguments.containsKey("fromApproved")) {
			this.fromApproved = (Boolean) arguments.get("fromApproved");
		}

		rules.put("Total_Co_Applicants_Income", BigDecimal.ZERO);
		rules.put("Total_Co_Applicants_Expense", BigDecimal.ZERO);

		doCheckRights();
		doShowDialog();
		logger.debug("Leaving " + event.toString());
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
		if(!enquiry){
		getUserWorkspace().allocateAuthorities("FinanceMainDialog", roleCode);
		this.btnAddGurantorDetails.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddGurantor"));
		this.btnAddJointDetails.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddJointAccount"));
		}
		logger.debug("Leaving");
	}

	@SuppressWarnings("rawtypes")
	private void doShowDialog() {
		logger.debug("Entering");

		// append finance basic details 
		appendFinBasicDetails();

		if (enquiry) {
			if (!financeMain.isNewRecord()) {
				List<JointAccountDetail> jointAccountDetailList= new ArrayList<JointAccountDetail>();
				List<GuarantorDetail> gurantorsAccDetailList = new ArrayList<GuarantorDetail>();
				if(fromApproved){
				 jointAccountDetailList = this.jointAccountDetailService
						.getJoinAccountDetail(financeMain.getFinReference(), "_AView");
				}else{
					 jointAccountDetailList = this.jointAccountDetailService
								.getJoinAccountDetail(financeMain.getFinReference(), "_View");
				}
				if (jointAccountDetailList != null && !jointAccountDetailList.isEmpty()) {
					doFillJointDetails(jointAccountDetailList);
				}else{
					rules.put("Co_Applicants_Count", 0);	
				}
				if(fromApproved){
				 gurantorsAccDetailList = this.guarantorDetailService
						.getGuarantorDetail(financeMain.getFinReference(), "_AView");
				}else{
					gurantorsAccDetailList = this.guarantorDetailService
							.getGuarantorDetail(financeMain.getFinReference(), "_View");
				}
				if (gurantorsAccDetailList != null && !gurantorsAccDetailList.isEmpty()) {
					doFillGurantorsDetails(gurantorsAccDetailList);
				}else{
					rules.put("Guarantors_Bank_CustomerCount", 0);
					rules.put("Guarantors_Other_CustomerCount", 0);
					rules.put("Guarantors_Total_Count", 0);
				}
			}
			this.finBasicdetails.setVisible(false);
		} else {
			// Rendering Joint Account Details
			List<JointAccountDetail> jointAcctDetailList = financeDetail.getJountAccountDetailList();
			if (jointAcctDetailList != null && !jointAcctDetailList.isEmpty()) {
				doFillJointDetails(jointAcctDetailList);
			}else{
				rules.put("Co_Applicants_Count", 0);
			}

			// Rendering Guaranteer Details
			List<GuarantorDetail> gurantorsDetailList = financeDetail.getGurantorsDetailList();
			if (gurantorsDetailList != null && !gurantorsDetailList.isEmpty()) {
				doFillGurantorsDetails(gurantorsDetailList);
			}else{
				rules.put("Guarantors_Bank_CustomerCount", 0);
				rules.put("Guarantors_Other_CustomerCount", 0);
				rules.put("Guarantors_Total_Count", 0);
			}
			try {
				Class[] paramType = { this.getClass() };
				Object[] stringParameter = { this };
				financeMainDialogCtrl.getClass().getMethod("setJointAccountDetailDialogCtrl", paramType)
						.invoke(financeMainDialogCtrl, stringParameter);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		
		getBorderLayoutHeight();
		this.window_JointAccountDetailDialog.setHeight(borderLayoutHeight-4+"px");
		this.listBoxJountAccountDetails.setHeight(((this.borderLayoutHeight - 250 - 50) / 2) + "px");// 425px
		this.listBoxGurantorsDetail.setHeight(((this.borderLayoutHeight - 250 - 100) / 2) + "px");// 425px
		logger.debug("Leaving");
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	public void doSetValidation() {
		logger.debug("Entering");
		// If include JointAccount is checked then JointAccountList should not
		// be empty
		if (this.jountAccountDetailList.size() < 1) {
			MessageUtil.showError("Please enter JointAccount Details in JointAccount tab");
		}
		logger.debug("Leaving");
	}

	/**
	 * This method set the Guaranteer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_GuarantorDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		if (guarantorDetailList != null && !this.guarantorDetailList.isEmpty()) {
			for (GuarantorDetail details : guarantorDetailList) {
				details.setFinReference(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setUserDetails(getUserWorkspace().getLoggedInUser());
				details.setRecordStatus(aFinanceDetail.getUserAction());
			}
			Cloner cloner = new Cloner();
			aFinanceDetail.setGurantorsDetailList(cloner.deepClone(guarantorDetailList));
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method set the guaranteer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public void doSave_JointAccountDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		if (jountAccountDetailList != null && !this.jountAccountDetailList.isEmpty()) {
			for (JointAccountDetail details : jountAccountDetailList) {
				details.setFinReference(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setUserDetails(getUserWorkspace().getLoggedInUser());
				details.setRecordStatus(aFinanceDetail.getUserAction());
			}
			Cloner cloner = new Cloner();
			aFinanceDetail.setJountAccountDetailList(cloner.deepClone(jountAccountDetailList));
		}
		logger.debug("Leaving ");
	}

	// ================Joint Account Details
	public void onClick$btnAddJointDetails(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		updateFinanceDetails();
		if (StringUtils.isEmpty(this.custCIF)) {
			MessageUtil.showError("Please Select The Customer");
			return;
		}
		JointAccountDetail jountAccountDetail = new JointAccountDetail();
		FinanceMain financeMain = null;
		if (getFinanceDetail() != null && getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
			financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		}
		jountAccountDetail.setNewRecord(true);
		jountAccountDetail.setWorkflowId(0);
		jountAccountDetail.setFinReference(finreference);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("jountAccountDetail", jountAccountDetail);
		map.put("finJointAccountCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("financeMain", financeMain);
		map.put("primaryCustID", custCIF);
		map.put("ccy", ccy);
		map.put("filter", getjointAcFilter());
		try {
			Executions.createComponents("/WEB-INF/pages/JointAccountDetail/JointAccountDetailDialog.zul", window_JointAccountDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillJointDetails(List<JointAccountDetail> jountAccountDetails) {
		logger.debug("Entering");
		this.listBoxJountAccountDetails.getItems().clear();
		setJountAccountDetailList(jountAccountDetails);
		//### 10-05-2018 Development Item 82
		rules.put("Co_Applicants_Count", jountAccountDetails.size());
		rules.put("Total_Co_Applicants_Income", BigDecimal.ZERO);
		rules.put("Total_Co_Applicants_Expense", BigDecimal.ZERO);

		for (JointAccountDetail jountAccountDetail : jountAccountDetails) {
			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(jountAccountDetail.getCustCIF());
			listitem.appendChild(listcell);
			listcell = new Listcell(jountAccountDetail.getLovDescCIFName());
			listitem.appendChild(listcell);
			listcell = new Listcell();
			Checkbox c = new Checkbox();
			c.setChecked(jountAccountDetail.isAuthoritySignatory());
			c.setParent(listcell);
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(jountAccountDetail.getPrimaryExposure() != null ? jountAccountDetail.getPrimaryExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(jountAccountDetail.getSecondaryExposure() != null ? jountAccountDetail.getSecondaryExposure() : "0"),ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(jountAccountDetail.getGuarantorExposure() != null ? jountAccountDetail.getGuarantorExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(jountAccountDetail.getStatus());
			listitem.appendChild(listcell);
			listcell = new Listcell(jountAccountDetail.getWorstStatus());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", jountAccountDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinJointItemDoubleClicked");
			setRuleIncomes(jountAccountDetail);
			this.listBoxJountAccountDetails.appendChild(listitem);
		}
		
		if (fieldVerificationDialogCtrl != null) {
			fieldVerificationDialogCtrl.addCoApplicantAddresses(jountAccountDetails,true);
		}
		logger.debug("Leaving");
	}

	private void setRuleIncomes(JointAccountDetail jountAccountDetail){
		
		BigDecimal totIncome = BigDecimal.ZERO;
		BigDecimal totExpense = BigDecimal.ZERO;

		// 1 Currency Conversion Required
		// 2 Un Formate should be based on the Customer Base Currency. 
		if(CollectionUtils.isNotEmpty(jountAccountDetail.getCustomerIncomeList())){
			for (CustomerIncome income : jountAccountDetail.getCustomerIncomeList()) {
				if (income.getIncomeExpense().equals(PennantConstants.INCOME)) {
					totIncome = totIncome.add(PennantAppUtil.formateAmount(income.getCalculatedAmount(), ccDecimal));
				}else{
					totExpense = totIncome.add(PennantAppUtil.formateAmount(income.getCalculatedAmount(), ccDecimal));
				}	
			}
		}

		if(rules.containsKey("Total_Co_Applicants_Income")){
			totIncome = totIncome.add((BigDecimal) rules.get("Total_Co_Applicants_Income"));
			totExpense = totExpense.add((BigDecimal) rules.get("Total_Co_Applicants_Income"));
		}
		
		rules.put("Total_Co_Applicants_Income", totIncome);
		rules.put("Total_Co_Applicants_Expense", totExpense);

	}
	public void onFinJointItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxJountAccountDetails.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			// CAST AND STORE THE SELECTED OBJECT
			final JointAccountDetail jountAccountDetail = (JointAccountDetail) item.getAttribute("data");
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(jountAccountDetail.getRecordType())||PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(jountAccountDetail.getRecordType())) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("jountAccountDetail", jountAccountDetail);
				map.put("finJointAccountCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				map.put("index", index);
				map.put("ccDecimal", ccDecimal);
				map.put("ccy", ccy);
				map.put("filter", getjointAcFilter());
				if(!enquiry){
					map.put("financeMain", getFinanceDetail().getFinScheduleData().getFinanceMain());
				}else{
					map.put("financeMain", financeMain);
					map.put("enqModule", enquiry);
					map.put("moduleType", "ENQ");
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/JointAccountDetail/JointAccountDetailDialog.zul", window_JointAccountDetailDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ================Gurantors Details
	public void onClick$btnAddGurantorDetails(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		updateFinanceDetails();
		if (StringUtils.isEmpty(this.custCIF)) {
			MessageUtil.showError("Please Select The Customer");
			return;
		}
		FinanceMain financeMain = null;
		if (getFinanceDetail() != null && getFinanceDetail().getFinScheduleData().getFinanceMain() != null) {
			financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		}
		GuarantorDetail guarantorDetail = new GuarantorDetail();
		guarantorDetail.setNewRecord(true);
		guarantorDetail.setWorkflowId(0);
		guarantorDetail.setFinReference(finreference);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("guarantorDetail", guarantorDetail);
		map.put("finJointAccountCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("primaryCustID", custCIF);
		map.put("ccDecimal", ccDecimal);
		map.put("financeMain", financeMain);
		map.put("ccy", ccy);
		map.put("filter", getGurantorFilter());
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul", window_JointAccountDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillGurantorsDetails(List<GuarantorDetail> guarantorDetailList) {
		logger.debug("Entering");
		this.listBoxGurantorsDetail.getItems().clear();
		setGuarantorDetailList(guarantorDetailList);
		int customerCount = 0;
		int otherCount = 0;
		
		for (GuarantorDetail guarantorDetail : guarantorDetailList) {
			
			if(guarantorDetail.isBankCustomer()){
				customerCount++;	
			}else{
				otherCount++;	
			}
			
			

			
			Listitem listitem = new Listitem();
			Listcell listcell;
			if (StringUtils.isBlank(guarantorDetail.getGuarantorCIF())) {
				listcell = new Listcell(guarantorDetail.getGuarantorIDNumber());
				if(guarantorDetail.getGuarantorIDType().equals(PennantConstants.CPRCODE)){
					listcell = new Listcell(PennantApplicationUtil.formatEIDNumber(guarantorDetail.getGuarantorIDNumber()));
				}
			} else {
				listcell = new Listcell(guarantorDetail.getGuarantorCIF());
			}
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getGuarantorCIFName());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getGuarantorIDTypeName());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getGuranteePercentage().toString());
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(guarantorDetail.getPrimaryExposure() != null ? guarantorDetail.getPrimaryExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(guarantorDetail.getSecondaryExposure() != null ? guarantorDetail.getSecondaryExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.amountFormate(new BigDecimal(guarantorDetail.getGuarantorExposure() != null ? guarantorDetail.getGuarantorExposure() : "0"), ccDecimal));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getStatus());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getWorstStatus());
			listitem.appendChild(listcell);
			listcell = new Listcell(guarantorDetail.getMobileNo());
			listitem.appendChild(listcell);
			listcell = new Listcell();
			Button viewBtn = new Button("View");
			viewBtn.setStyle("font-weight:bold");
			listcell.appendChild(viewBtn);
			viewBtn.addForward("onClick", window_JointAccountDetailDialog, "onViewGurantorProofFile", listitem);
			if (StringUtils.isBlank(guarantorDetail.getGuarantorProofName())) {
				viewBtn.setVisible(false);
			}
			viewBtn.setParent(listcell);
			listitem.appendChild(listcell);
			listitem.setAttribute("data", guarantorDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinGurantorItemDoubleClicked");
			this.listBoxGurantorsDetail.appendChild(listitem);
		}
		
		//### 10-05-2018 Start Development Item 82
		rules.put("Guarantors_Bank_CustomerCount", customerCount);
		rules.put("Guarantors_Other_CustomerCount", otherCount);
		rules.put("Guarantors_Total_Count", customerCount+otherCount);
		//### 10-05-2018 End Development Item 82
		logger.debug("Leaving");
	}

	/**
	 * Method for Uploading Agreement Details File
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onViewGurantorProofFile(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		String guarantorProofName = null;
		byte [] guarantorProof = null;

		Listitem listitem = (Listitem) event.getData();
		if (listitem != null) {
			GuarantorDetail detail = (GuarantorDetail) listitem.getAttribute("data");
			guarantorProofName = StringUtils.trimToNull(detail.getGuarantorProofName());
			guarantorProof = detail.getGuarantorProof();

			if(guarantorProofName != null) {
				if(guarantorProof == null) {
					guarantorProof = getGuarantorDetailService().getGuarantorProof(detail).getGuarantorProof();
				}

				detail.setGuarantorProof(guarantorProof);
				try {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("FinGurantorProofDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				} catch (Exception e) {
					logger.debug(e);
				}

			} else {
				MessageUtil.showError("Please Upload an Proof Before View.");
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFinGurantorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxGurantorsDetail.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			// CAST AND STORE THE SELECTED OBJECT
			final GuarantorDetail guarantorDetail = (GuarantorDetail) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(guarantorDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)||PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(guarantorDetail.getRecordType())) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("guarantorDetail", guarantorDetail);
				map.put("finJointAccountCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				map.put("index", index);
				map.put("ccDecimal", ccDecimal);
				map.put("ccy", ccy);
				map.put("filter", getGurantorFilter());
				if(enquiry){
				map.put("enqModule", enquiry);
				map.put("moduleType", "ENQ");
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/GuarantorDetail/GuarantorDetailDialog.zul", window_JointAccountDetailDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	private void updateFinanceDetails() {
		try {
			Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
			if (object != null) {
				FinanceMain main = (FinanceMain) object;
				finreference = main.getFinReference();
				custCIF = main.getLovDescCustCIF();
				ccDecimal = CurrencyUtil.getFormat(main.getFinCcy());
				ccy = main.getFinCcy();
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
	}
	private String[] getjointAcFilter(){
		if (this.guarantorDetailList!=null && guarantorDetailList.size()>0) {
			String cif[]=new String[guarantorDetailList.size()];
			for (int i = 0; i <  guarantorDetailList.size(); i++) {
					if(guarantorDetailList.get(i).getGuarantorCIF()!=null){
						cif[i]=guarantorDetailList.get(i).getGuarantorCIF();
				}else{
					cif[i]=" ";
				}
			}
			//cif[cif.length]=custCIF;
			return cif;
		}else{
			String cif[]=new String[1];
			cif[0]=custCIF;
			return cif;
		}
	}
	private String[] getGurantorFilter(){
		if (this.jountAccountDetailList!=null && jountAccountDetailList.size()>0) {
			String cif[]=new String[jountAccountDetailList.size()];
			for (int i = 0; i <  jountAccountDetailList.size(); i++) {
				cif[i]=jountAccountDetailList.get(i).getCustCIF();
			}
			//cif[cif.length-1]=custCIF;
			return cif;
		}else{
			String cif[]=new String[1];
			cif[0]=custCIF;
			return cif;
		}
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
	 * This method is using for FinanceTaxDetailDialogCtrl for getting the joint account customers.
	 * 
	 * @return List<Customer>
	 */
	public List<Customer> getJointAccountCustomers(){
		logger.debug("Entering");
		
		List<Customer> customersList = new ArrayList<Customer>();
		List<Listitem> listItems =	this.listBoxJountAccountDetails.getItems();

		JointAccountDetail jointAccount = null;
		Customer customer = null;
		for (Listitem listItem : listItems) {
			jointAccount = (JointAccountDetail) listItem.getAttribute("data");
			if (jointAccount != null && !(StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, jointAccount.getRecordType())
					|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, jointAccount.getRecordType()))) {
				customer = new Customer();
				customer.setId(jointAccount.getCustID());
				customer.setCustCIF(jointAccount.getCustCIF());
				customer.setCustShrtName(jointAccount.getLovDescCIFName());
				customersList.add(customer);
			}
		}
		
		logger.debug("Leaving");
		return customersList;
	}
	
	/**
	 * This method is using for FinanceTaxDetailDialogCtrl for getting the joint account customers.
	 * 
	 * @return List<Customer>
	 */
	public List<Customer> getGuarantorCustomers(){
		logger.debug("Entering");
		
		List<Customer> customersList = new ArrayList<Customer>();
		List<Listitem> listItems =	this.listBoxGurantorsDetail.getItems();
		
		GuarantorDetail guarantorDetail = null;
		Customer customer = null;
		for (Listitem listItem : listItems) {
			guarantorDetail =  (GuarantorDetail) listItem.getAttribute("data");
			if (guarantorDetail != null && StringUtils.isNotBlank(guarantorDetail.getGuarantorCIF())
					&& !(StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, guarantorDetail.getRecordType())
							|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, guarantorDetail.getRecordType()))) {
				customer = new Customer();
				customer.setId(guarantorDetail.getCustID());
				customer.setCustCIF(guarantorDetail.getGuarantorCIF());
				customer.setCustShrtName(guarantorDetail.getGuarantorCIFName());
				customersList.add(customer);
			}
		}
		
		logger.debug("Leaving");
		return customersList;
	}
	
	
	
	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public List<JointAccountDetail> getOldVar_JountAccountDetailList() {
		return oldVar_JountAccountDetailList;
	}
	public void setOldVar_JountAccountDetailList(List<JointAccountDetail> oldVarJountAccountDetailList) {
		this.oldVar_JountAccountDetailList = oldVarJountAccountDetailList;
	}

	public List<JointAccountDetail> getJountAccountDetailList() {
		return jountAccountDetailList;
	}
	public void setJountAccountDetailList(List<JointAccountDetail> jountAccountDetailList) {
		this.jountAccountDetailList = jountAccountDetailList;
	}

	public List<GuarantorDetail> getGuarantorDetailList() {
		return guarantorDetailList;
	}
	public void setGuarantorDetailList(List<GuarantorDetail> guarantorDetailList) {
		this.guarantorDetailList = guarantorDetailList;
	}

	public List<GuarantorDetail> getOldVar_GuarantorDetailList() {
		return oldVar_GuarantorDetailList;
	}
	public void setOldVar_GuarantorDetailList(List<GuarantorDetail> oldVarGuarantorDetailList) {
		this.oldVar_GuarantorDetailList = oldVarGuarantorDetailList;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
		if (financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
			((FinanceMainBaseCtrl) financeMainDialogCtrl).setJointAccountDetailDialogCtrl(this);
		}
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}
	public GuarantorDetailService getGuarantorDetailService() {
		return guarantorDetailService;
	}
	
	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
	
	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public Object getMainController() {
		return mainController;
	}

	public void setMainController(Object mainController) {
		this.mainController = mainController;
	}

	public void setFieldVerificationDialogCtrl(FieldVerificationDialogCtrl fieldVerificationDialogCtrl) {
		this.fieldVerificationDialogCtrl = fieldVerificationDialogCtrl;
	}
}
