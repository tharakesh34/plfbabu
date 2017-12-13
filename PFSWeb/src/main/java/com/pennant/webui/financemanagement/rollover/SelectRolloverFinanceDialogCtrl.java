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
 * FileName    		:  SelectRolloverFinanceDialogCtrl.java                                 * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :      																	*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.rollover;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.RolledoverFinanceHeader;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.constants.InterfaceConstants;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/ SelectRolloverFinanceDialog.zul file.
 */
public class SelectRolloverFinanceDialogCtrl extends GFCBaseCtrl<RolledoverFinanceDetail> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = Logger.getLogger(SelectRolloverFinanceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window       			window_SelectRolloverFinanceDialog;         // autoWired
	protected Textbox				custCIF;									// autoWired
	protected Label					custShrtName;								// autoWired
	protected ExtendedCombobox      finType;                                   	// autoWired
	protected ExtendedCombobox      limitRef;                                   // autoWired
	protected Combobox      		rolloverDate;                               // autoWired
	protected Button       			btnProceed;                                	// autoWired
	protected Row       			customerRow;                                // autoWired
	protected Row       			finTypeRow;                                	// autoWired
	protected Row       			rolloverDateRow;                            // autoWired
	protected Row       			rolloverFinanceRow;                         // autoWired
	protected Listbox       		listBoxRolloverFinance;                     // autoWired
	protected Combobox				finPurpose;
	protected Row					finPurposerow;

	protected transient FinanceWorkFlow         financeWorkFlow;
	private transient   WorkFlowDetails         workFlowDetails=null;
	private FinanceType financeType =null;
	private List<String> userRoleCodeList = new ArrayList<String>();
	private List<Date> rolloverDateList = new ArrayList<Date>();
	private List<RolledoverFinanceDetail> rolledoverFinances = null;
	private BigDecimal totalRolloverAmount = BigDecimal.ZERO;
	private String moduleDefiner = "";
	private RolledoverFinanceDetail latestRollover = null;
	private ArrayList<ValueLabel> assetTypeList = new ArrayList<>();

	private transient 	FinanceTypeService      financeTypeService;
	private transient   FinanceWorkFlowService  financeWorkFlowService;
	private transient   FinanceDetailService    financeDetailService;   
	private transient   CustomerDetailsService  customerDetailsService;   
	private transient StepPolicyService stepPolicyService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private CustomerInterfaceService customerInterfaceService;
	private PagedListService pagedListService;

	private String menuItemRightName= null;	
	boolean isFinAssetTypeEmpty= false;
	private FinanceSelectCtrl financeSelectCtrl;
	
	private List<String> limitRefList = null;

	/**
	 * default constructor.<br>
	 */
	public SelectRolloverFinanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events


	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectRolloverFinanceDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectRolloverFinanceDialog);


		if (arguments.containsKey("menuItemRightName")) {
			this.menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		if (arguments.containsKey("role")) {
			userRoleCodeList = (ArrayList<String>) arguments.get("role");
		}
		
		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}
		
		if (arguments.containsKey("financeSelectCtrl")) {
			this.financeSelectCtrl = (FinanceSelectCtrl) arguments.get("financeSelectCtrl");
			setFinanceSelectCtrl(this.financeSelectCtrl);
		} else {
			setFinanceSelectCtrl(null);
		}
		
		doSetFieldProperties();

		showSelectRolloverFinanceDialog();
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		String whereClause = getWhereClauseWithFirstTask();

		// Empty sent any required attributes
		this.limitRef.setMaxlength(8);
		this.limitRef.setMandatoryStyle(true);
		this.limitRef.setModuleName("CustomerLimit");
		this.limitRef.setValueColumn("LimitRef");
		this.limitRef.setDescColumn("LimitDesc");
		this.limitRef.setValidateColumns(new String[] { "LimitRef" });
		
		//Get Finances only with Rollover Dates and Exclude already utilized
		
		limitRefList = getLimitRefList();
		if(limitRefList != null && !limitRefList.isEmpty()){
			Filter[] filters = new Filter[1];
			filters[0]= new Filter("LimitRef", limitRefList, Filter.OP_IN);
			this.limitRef.setFilters(filters);
		}
				
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceWorkFlow");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("LovDescFinTypeName");
		this.finType.setValidateColumns(new String[] { "FinType" });
		if(StringUtils.isNotEmpty(whereClause)){
			this.finType.setWhereClause(whereClause);
		}

		logger.debug("Leaving");
	}
	
	private String getWhereClauseWithFirstTask(){
		StringBuilder whereClause = new StringBuilder();
		if (userRoleCodeList != null && userRoleCodeList.size() > 0) {
			for (String role : userRoleCodeList) {
				if (whereClause.length() > 0) {
					whereClause.append(" OR ");
				}
				
				whereClause.append("(',' ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" LovDescFirstTaskOwner ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" ',' LIKE '%,");
				whereClause.append(role);
				whereClause.append(",%')");
			}
		}
		return whereClause.toString();
	}

	private void getFinancewithAccess(){
		Filter[] filters = new Filter[4];
		filters[0]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1]= new Filter("ModuleName", "FINANCE", Filter.OP_EQUAL);
		filters[2]= new Filter("FinEvent", FinanceConstants.FINSER_EVENT_ROLLOVER, Filter.OP_EQUAL);
		List<String> finTypeList = getFinTypeList(this.limitRef.getValue());
		filters[3]= Filter.in("FinType", finTypeList.toArray(new String[finTypeList.size()]));
		this.finType.setFilters(filters);
	}

	/**
	 * Method to get List of LimitReferences from Finance Main which are used for Rollover
	 * @return
	 */
	private List<String> getLimitRefList(){
		return getFinanceDetailService().getRollOverLimitRefList();
	}
	
	/**
	 * Method to get List of Finance Type from Finance Main which are used for Rollover & Selected Limit Ref
	 * @return
	 */
	private List<String> getFinTypeList(String limitRef){
		return getFinanceDetailService().getRollOverFinTypeList(limitRef);
	}
	
	/**
	 * Method to get List of Rollover Dates from Finance Main which are used for Rollover & Selected Limit Ref with FinaceType Selection
	 * @return
	 */
	private List<Date> getRolloverNextDateList(String limitRef, String finType) {
		return getFinanceDetailService().getRollOverNextDateList(limitRef, finType);
	}
	
	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	public void onFulfill$limitRef(Event event){
		logger.debug("Entering " + event.toString());

		this.limitRef.setConstraint("");
		this.limitRef.clearErrorMessage();
		this.customerRow.setVisible(false);
		this.finTypeRow.setVisible(false);
		this.rolloverFinanceRow.setVisible(false);
		Clients.clearWrongValue(limitRef);
		Object dataObject = this.limitRef.getObject();
		if (dataObject instanceof String){
			this.limitRef.setValue(dataObject.toString());
			this.limitRef.setDescription("");
			this.custCIF.setValue("");
			this.custShrtName.setValue("");
		}else{
			CustomerLimit details= (CustomerLimit) dataObject;
			
			if (details != null) {
				this.limitRef.setValue(details.getLimitRef());
				this.limitRef.setDescription(details.getLimitDesc());
				this.custCIF.setValue(details.getCustomerReference());
				this.customerRow.setVisible(true);
				this.finTypeRow.setVisible(true);
				this.finType.setValue("", "");
				rolloverDateList.clear();
				this.rolloverDateRow.setVisible(false);
				fillRolloverDates();
				getFinancewithAccess();
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	public void onFulfill$finType(Event event){
		logger.debug("Entering " + event.toString());

		this.finType.setConstraint("");
		this.finType.clearErrorMessage();
		Clients.clearWrongValue(finType);
		Object dataObject = this.finType.getObject();
		this.rolloverDateList = new ArrayList<>();
		this.rolloverFinanceRow.setVisible(false);
		this.rolloverDateRow.setVisible(false);
		this.finPurposerow.setVisible(false);
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		}else{
			FinanceWorkFlow details= (FinanceWorkFlow) dataObject;
			/*Set FinanceWorkFlow object*/
			setFinanceWorkFlow(details);
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getLovDescFinTypeName());
				this.finPurposerow.setVisible(true);
				
				if(details.getFinAssetType()!=null && StringUtils.isNotEmpty(details.getFinAssetType())){
					doFillAssestType(details.getFinAssetType());
				}else{
					isFinAssetTypeEmpty=true;
				}
				
				this.rolloverDateList = getRolloverNextDateList(this.limitRef.getValue(), this.finType.getValue());
			}
		}
		
		//Filling Rollover Date Details list
		fillRolloverDates();
		
		logger.debug("Leaving " + event.toString());
	}
	
	private void doFillAssestType(String finasset) {
		logger.debug("Entering");
		
		ArrayList<String> finAssetTypelist = new ArrayList<String>();
		assetTypeList = new ArrayList<ValueLabel>();
		List<ProductAsset> productAssetlist = new ArrayList<ProductAsset>();
		String finAssetvalues[]=finasset.split(",");
		for (int i=0;i<finAssetvalues.length;i++) {
			finAssetTypelist.add(finAssetvalues[i]);
		}
		
		// Fetch Asset Details From Product Details
		 productAssetlist=getFinanceTypeService().getFinPurposeByAssetId(finAssetTypelist,"");		
		if(productAssetlist!=null){
			if(productAssetlist.size()>1){
				this.finPurposerow.setVisible(true);
				for (ProductAsset productAsset : productAssetlist) {
					assetTypeList.add(new ValueLabel(productAsset.getAssetCode(),productAsset.getAssetDesc()));
				}
			}else{
				for (ProductAsset productAsset : productAssetlist) {
					assetTypeList.add(new ValueLabel(productAsset.getAssetCode(),productAsset.getAssetDesc()));
				}
				this.finPurposerow.setVisible(false);
			}
		}
	    
		fillComboBox(this.finPurpose,"",assetTypeList,"");
		logger.debug("Leaving");
	}
	
	private void fillRolloverDates() {
		logger.debug("Entering");
		rolloverDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		rolloverDate.appendChild(comboitem);
		rolloverDate.setSelectedItem(comboitem);
		if (rolloverDateList != null && !rolloverDateList.isEmpty()) {
			this.rolloverDateRow.setVisible(true);
			for (int i = 0; i < rolloverDateList.size(); i++) {
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(rolloverDateList.get(i)));
				comboitem.setValue(rolloverDateList.get(i));
				rolloverDate.appendChild(comboitem);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Processing of Selecting Rollover Date
	 * @param event
	 */
	public void onSelect$rolloverDate(Event event){
		logger.debug("Entering " + event.toString());
		
		this.rolloverFinanceRow.setVisible(false);
		totalRolloverAmount = BigDecimal.ZERO;
		if(this.rolloverDate.getSelectedIndex() > 0){
			
			this.listBoxRolloverFinance.getItems().clear();
			this.rolloverFinanceRow.setVisible(true);
			
			rolledoverFinances = getFinanceDetailService().getRolloverFinanceList(
					this.limitRef.getValue(),this.finType.getValue(),
					DateUtility.getDBDate(this.rolloverDate.getSelectedItem().getValue().toString()));
			
			Date startDate = null;
			
			for (int i = 0; i < rolledoverFinances.size(); i++) {
				
				RolledoverFinanceDetail detail = rolledoverFinances.get(i);
				Listitem listitem = new Listitem();
				Listcell lc = new Listcell(detail.getFinReference());
				listitem.appendChild(lc);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getStartDate()));
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getFinAmount(), CurrencyUtil.getFormat("")));
				listitem.appendChild(lc);
				lc = new Listcell(PennantApplicationUtil.formatRate(detail.getProfitRate().doubleValue(), 9) +" %");
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getTotalProfit(), CurrencyUtil.getFormat("")));
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getRolloverAmount(), CurrencyUtil.getFormat("")));
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getFinAmount().add(detail.getTotalProfit()), CurrencyUtil.getFormat("")));
				listitem.appendChild(lc);
				
				//Fetch Latest Created Rollover Details
				if(startDate == null || startDate.compareTo(detail.getStartDate()) < 0){
					latestRollover = detail;
					startDate = detail.getStartDate();
				}
				
				listBoxRolloverFinance.appendChild(listitem);
				totalRolloverAmount = totalRolloverAmount.add(detail.getRolloverAmount());
			}
			
			if(latestRollover != null){
				fillComboBox(this.finPurpose, latestRollover.getFinPurpose(), assetTypeList, "");
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(!doFieldValidation()){
			return;
		}
		
		//Validating Rollover Date finances List
		if(rolloverDateList.isEmpty()){
			MessageUtil.showError(Labels.getLabel("NO_PROCEED_ROLLOVER"));
			return;
		}
		
		//Customer Data Fetching
		CustomerDetails customerDetails = fetchCustomerData();
		if(customerDetails == null){
			return;
		}

		if(StringUtils.isNotBlank(this.finType.getValue())){
			financeType = getFinanceTypeService().getApprovedFinanceTypeById(this.finType.getValue().trim());

			if(getFinanceWorkFlow() == null){
				FinanceWorkFlow financeWorkFlow=getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
						this.finType.getValue().trim(),FinanceConstants.FINSER_EVENT_ROLLOVER, PennantConstants.WORFLOW_MODULE_FINANCE);
				setFinanceWorkFlow(financeWorkFlow);
			}
		}

		// create a new FinanceMain object, We GET it from the back end.
		FinanceDetail financeDetail = getFinanceDetailService().getNewFinanceDetail(false);
		financeDetail.setNewRecord(true);

		FinanceMain finMain = financeDetailService.setDefaultFinanceMain(new FinanceMain(), financeType);
		FinODPenaltyRate finOdPenalty = financeDetailService.setDefaultODPenalty(new FinODPenaltyRate(), financeType);
		financeDetail.getFinScheduleData().setFinanceMain(finMain);
		financeDetail.getFinScheduleData().setFinODPenaltyRate(finOdPenalty);
		financeDetail.getFinScheduleData().setFinanceType(financeType);

		//Step Policy Details
		if(financeType.isStepFinance()){
			List<StepPolicyDetail> stepPolicyList = getStepPolicyService().getStepPolicyDetailsById(financeType.getDftStepPolicy());
			financeDetail.getFinScheduleData().resetStepPolicyDetails(stepPolicyList);
		}

		try {
			//Fetch & set Default statuses f
			if (financeDetail.getFinScheduleData().getFinanceMain() != null) {
				financeDetail.getFinScheduleData().getFinanceMain().setFinStsReason(FinanceConstants.FINSTSRSN_SYSTEM);
				financeDetail.getFinScheduleData().getFinanceMain().setFinStatus(getFinanceDetailService().getCustStatusByMinDueDays());
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		//Workflow Details Setup

		if(getFinanceWorkFlow()!=null){
			workFlowDetails = WorkFlowUtil.getDetailsByType(getFinanceWorkFlow().getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(workFlowDetails.getWorkFlowId());
		} else {
			setWorkFlowEnabled(true);

			if (workFlowDetails.getFirstTaskOwner().contains(PennantConstants.DELIMITER_COMMA)) {
				String[] fisttask=workFlowDetails.getFirstTaskOwner().split(PennantConstants.DELIMITER_COMMA);
				for (String string : fisttask) {
					if (getUserWorkspace().isRoleContains(string)) {
						setFirstTask(true);
						break;
					}
				}
			}else{
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			}
			setWorkFlowId(workFlowDetails.getId());
			financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(workFlowDetails.getWorkFlowId());

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		}

		financeDetail.setNewRecord(true);
		financeDetail.getFinScheduleData().getFinanceMain().setFinAmount(totalRolloverAmount);
		financeDetail.setCustomerDetails(customerDetails);
		financeDetail.getFinScheduleData().getFinanceMain().setCustID(customerDetails.getCustomer().getCustID());
		financeDetail.getFinScheduleData().getFinanceMain().setLovDescCustCIF(customerDetails.getCustomer().getCustCIF());
		financeDetail.getFinScheduleData().getFinanceMain().setLovDescCustShrtName(customerDetails.getCustomer().getCustShrtName());
		financeDetail.getFinScheduleData().getFinanceMain().setFinBranch(customerDetails.getCustomer().getCustDftBranch());
		financeDetail.getFinScheduleData().getFinanceMain().setLovDescFinBranchName(customerDetails.getCustomer().getLovDescCustDftBranchName());
		financeDetail.getFinScheduleData().getFinanceMain().setCustID(customerDetails.getCustomer().getCustID());
		financeDetail.getFinScheduleData().getFinanceMain().setFinLimitRef(this.limitRef.getValue());
		
		if(!isFinAssetTypeEmpty){
			if(this.finPurposerow.isVisible() && !getComboboxValue(this.finPurpose).equals(PennantConstants.List_Select)){
				financeDetail.getFinScheduleData().getFinanceMain().setLovDescFinPurposeName(this.finPurpose.getSelectedItem().getLabel());
				financeDetail.getFinScheduleData().getFinanceMain().setFinPurpose(this.finPurpose.getSelectedItem().getValue().toString());
			}else if(!this.finPurposerow.isVisible()){
				financeDetail.getFinScheduleData().getFinanceMain().setLovDescFinPurposeName(this.finPurpose.getItemAtIndex(1).getLabel());
				financeDetail.getFinScheduleData().getFinanceMain().setFinPurpose(this.finPurpose.getItemAtIndex(1).getValue().toString());
			}
		}	
		
		// Setting Rollover Finance Details data
		RolledoverFinanceHeader header = new RolledoverFinanceHeader();
		header.setRolledoverFinanceDetails(rolledoverFinances);
		
		// Reset Latest Rollover Detail object Data to Current Finance Detail
		header.setLatePayAmount(CalculationUtil.calInterest((Date) this.rolloverDate.getSelectedItem().getValue(),
				DateUtility.getAppDate(), totalRolloverAmount, financeDetail.getFinScheduleData().getFinanceMain()
						.getProfitDaysBasis(), latestRollover.getProfitRate()));
		
		financeDetail.setRolledoverFinanceHeader(header);

		//Fetching Finance Reference Detail
		if(getFinanceWorkFlow() != null){
			financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail,getRole(),
					getFinanceWorkFlow().getScreenCode(),"", FinanceConstants.FINSER_EVENT_ORG, false);
		}

		// Repayment Method Reset on Basis of Customer Employee Status
		CustEmployeeDetail employeeDetail = customerDetails.getCustEmployeeDetail();
		if(employeeDetail != null){
			if(StringUtils.equals(employeeDetail.getEmpStatus(),PennantConstants.CUSTEMPSTS_EMPLOYED)){
				String repayMethod ="";
				if(customerDetails.getCustomer().isSalariedCustomer()){
					repayMethod = FinanceConstants.REPAYMTH_AUTO;
				} else {
					repayMethod = FinanceConstants.REPAYMTH_AUTODDA;
				}
				financeDetail.getFinScheduleData().getFinanceMain().setFinRepayMethod(repayMethod);
			}
		}
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(getFinanceWorkFlow()!=null){

			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/RolloverFinanceMainDialog.zul");

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			map.put("financeType", financeType);
			map.put("menuItemRightName", menuItemRightName);
			map.put("moduleDefiner", moduleDefiner);
			map.put("financeSelectCtrl", getFinanceSelectCtrl());

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(fileLocaation.toString(),null,map);
				this.window_SelectRolloverFinanceDialog.onClose();
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} else {
			MessageUtil.showError(Labels.getLabel("Workflow_Not_Found")
					+ financeDetail.getFinScheduleData().getFinanceMain().getFinType());
		}

		logger.debug("Leaving " + event.toString());
	}

	// GUI Process

	/**
	 * Opens the SelectRolloverFinanceDialog window modal.
	 */
	private void showSelectRolloverFinanceDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			if(limitRefList != null && !limitRefList.isEmpty()){
				this.window_SelectRolloverFinanceDialog.doModal();
			}else{
				this.window_SelectRolloverFinanceDialog.onClose();
				MessageUtil.showError(Labels.getLabel("NO_PROCEED_ROLLOVER"));
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * @throws InterruptedException 
	 */
	private boolean doFieldValidation() throws InterruptedException {
		logger.debug("Entering ");
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(StringUtils.isBlank(this.limitRef.getValue())){
				throw new WrongValueException(this.limitRef,Labels.getLabel("CHECK_NO_EMPTY"
						,new String[]{Labels.getLabel("label_SelectRolloverFinanceDialog_LimitRef.value")})); 
			}
		}catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			if(StringUtils.isBlank(this.finType.getValue())){
				throw new WrongValueException(this.finType,Labels.getLabel("CHECK_NO_EMPTY"
						,new String[]{Labels.getLabel("label_SelectRolloverFinanceDialog_FinType.value")})); 
			}
		}catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (StringUtils.isEmpty(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectRolloverFinanceDialog_CustCIF.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		
		try {
			if (!this.rolloverDateList.isEmpty() && this.rolloverDate.getSelectedIndex() == 0) {
				throw new WrongValueException(this.rolloverDate, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_SelectRolloverFinanceDialog_RolloverDate.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		
		try {
			if(this.finPurposerow.isVisible()){
				if(this.finPurpose.getSelectedItem().getValue().equals(PennantConstants.List_Select)){
						throw new WrongValueException(this.finPurpose,Labels.getLabel("STATIC_INVALID"
							,new String[]{Labels.getLabel("label_SelectRolloverFinanceDialog_FinPurpose.value")})); 
				}
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if(wve.size() > 0){
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving ");
		return true;
	}

	/**
	 * Method for remove constraints of fields
	 */
	private void doRemoveValidation(){
		logger.debug("Entering");
		this.finType.setConstraint("");
		this.custCIF.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws InterfaceException 
	 * @throws Exception
	 */
	public CustomerDetails fetchCustomerData() throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		CustomerDetails customerDetails = new CustomerDetails();
		// Get the data of Customer from Core Banking Customer
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());
			//If  customer exist is checked 
			Customer customer = null;
			if (StringUtils.isEmpty(cif)) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
			}else{

				//check Customer Data in LOCAL PFF system
				customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);
			}

			//Interface Core Banking System call
			if (customer == null) {
				customerDetails.setNewRecord(true);
				customerDetails = getCustomerInterfaceService().getCustomerInfoByInterface(cif, "");
				if (customerDetails == null) {
					throw new InterfaceException("9999", "Customer Not found.");
				}
			}

			if (customer != null) {
				customerDetails = getCustomerDetailsService().getCustomerDetailsById(customer.getId(), true, "_View" );
			}

		} catch (InterfaceException pfe) {
			if(StringUtils.equals(pfe.getErrorCode(), InterfaceConstants.CUST_NOT_FOUND)) {
				if (MessageUtil.confirm(Labels.getLabel("Cust_NotFound_NewCustomer")) == MessageUtil.YES) {
					return null;
				}
			} else {
				MessageUtil.showError(pfe);
				return null;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);

			if (MessageUtil.confirm(Labels.getLabel("Cust_NotFound_NewCustomer")) == MessageUtil.YES) {
				return null;
			}
		}
		logger.debug("Leaving");
		return customerDetails;
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if(customer != null){
			this.custCIF.setValue(customer.getCustCIF());
		}else{
			this.custCIF.setValue("");
		}
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

	// Getters and Setters

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public FinanceWorkFlow getFinanceWorkFlow() {
		return financeWorkFlow;
	}
	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}
	public void setCustomerInterfaceService(
			CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}
	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	
}
