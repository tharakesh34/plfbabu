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
 *											    											*
 * FileName    		:  WIFinanceTypeSelectListCtrl.java                                     * 	  
 *                                                                    			    		*
 * Author      		:  PENNANT TECHONOLOGIES              				    				*
 *                                                                  			    		*
 * Creation Date    :  10-10-2011    							    						*
 *                                                                  			    		*
 * Modified Date    :  10-10-2011    							    						*
 *                                                                  			    		*
 * Description 		:                                             			    			*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-10-2011       Pennant	                 0.1                                        	* 
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

package com.pennant.webui.finance.wiffinancemain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class WIFinanceTypeSelectListCtrl extends GFCBaseListCtrl<FinanceType> implements Serializable {

	private static final long serialVersionUID = 3257569537441008225L;
	private final static Logger logger = Logger.getLogger(WIFinanceTypeSelectListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceTypeSelect; 				// autoWired
	protected Borderlayout borderLayout_FinanceTypeList; 	// autoWired
	
	protected ExtendedCombobox finType;
	
	protected Radiogroup custType;
	protected Radio custType_New;
	protected Radio custType_Exist;
	protected Row wIfCustSelectionRow;
	protected Row customerRow;
	protected Longbox custID;
	protected Textbox lovDescCustCIF;
	protected Button btnSearchCustCIF;
	protected Label custShrtName;
	private String finBranch;

	private transient WIFFinanceMainDialogCtrl wifFinanceMainDialogCtrl;
	private transient WIFFinanceMainListCtrl wIFFinanceMainListCtrl;
	private transient FinanceDetail financeDetail;
	
	private transient FinanceDetailService financeDetailService;
	private transient CustomerIncomeService customerIncomeService;
	private transient EligibilityDetailService eligibilityDetailService;
	private transient ScoringDetailService scoringDetailService;
	private transient StepPolicyService stepPolicyService;
	
	private FinanceType financeType;
	private WIFCustomer wifcustomer = new WIFCustomer();
	private String loanType = "";
	
	/**
	 * default constructor.<br>
	 */
	public WIFinanceTypeSelectListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceTypeSelect(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("WIFFinanceMainDialogCtrl")) {
			this.wifFinanceMainDialogCtrl = (WIFFinanceMainDialogCtrl) args.get("WIFFinanceMainDialogCtrl");			
		} else {
			this.wifFinanceMainDialogCtrl = null;
		}
		
		if(args.containsKey("WIFFinanceMainListCtrl")) {
			this.wIFFinanceMainListCtrl = (WIFFinanceMainListCtrl) args.get("WIFFinanceMainListCtrl");			
		} else {
			this.wIFFinanceMainListCtrl = null;
		}
		
		if(args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");			
		} else {
			this.financeDetail = null;
		}
		
		if(args.containsKey("loanType")) {
			this.loanType = (String) args.get("loanType");			
		}
		
		if(loanType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
			this.custType.setSelectedIndex(0);
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
			this.custType.setSelectedIndex(1);
			this.wIfCustSelectionRow.setVisible(false);
			this.customerRow.setVisible(true);
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_COMMERCIAL)){
			this.custType.setSelectedIndex(0);
			this.wIfCustSelectionRow.setVisible(false);
			this.customerRow.setVisible(false);
		}else{
			this.custType.setSelectedIndex(0);
			this.wIfCustSelectionRow.setVisible(false);
			this.customerRow.setVisible(false);
		}
		 doSetFieldProperties();
		this.window_FinanceTypeSelect.doModal();
		logger.debug("Leaving" + event.toString());
	}
	
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		Filter[] filters = new Filter[2] ;
		if(loanType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
			filters[0]= new Filter("FinDivision",  PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_EQUAL);
			filters[1]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
			List<String> divList = new ArrayList<String>(2);
			divList.add(PennantConstants.FIN_DIVISION_COMMERCIAL);
			divList.add(PennantConstants.FIN_DIVISION_CORPORATE);
			filters[0]= Filter.in("FinDivision", divList);
			filters[1]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_COMMERCIAL)){
			filters[0]= new Filter("FinDivision",  PennantConstants.FIN_DIVISION_COMMERCIAL, Filter.OP_EQUAL);
			filters[1]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		}else{
			filters = null;
		}
		
		this.finType.setFilters(filters);

		logger.debug("Leaving");
	}

	
	
	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	public void onFulfill$finType(Event event){
		logger.debug("Entering " + event.toString());

		Object dataObject = finType.getObject();
		if (dataObject instanceof String){
			//
		}else{
			FinanceType details= (FinanceType) dataObject;
			if (details != null) {
				financeType = details;
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.lovDescCustCIF.clearErrorMessage();
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", null);
		
		ArrayList<Filter> filterList = new ArrayList<Filter>();
		if(loanType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
			//filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL));
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_NOT_EQUAL));
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_COMMERCIAL)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_BANK, Filter.OP_EQUAL));
		}
		map.put("filtersList", filterList);
		
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug("Leaving");
	}
	
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		Customer customer = (Customer) nCustomer;
		if(customer != null){
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
			this.finBranch = customer.getCustDftBranch();
			BeanUtils.copyProperties(customer, wifcustomer);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		ArrayList<Filter> filterList = new ArrayList<Filter>();
		if(loanType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
			//filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL));
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_NOT_EQUAL));
		}else if(loanType.equals(PennantConstants.FIN_DIVISION_COMMERCIAL)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_BANK, Filter.OP_EQUAL));
		}

		this.lovDescCustCIF.clearErrorMessage();
		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), filterList);

		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
			this.custShrtName.setValue(customer.getCustShrtName());
			BeanUtils.copyProperties(customer, wifcustomer);
		} else {
			this.custID.setValue(Long.valueOf(0));
			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", 
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$custType(Event event){
		this.customerRow.setVisible(false);
		if(this.custType.getSelectedItem() != null){
			if(this.custType.getSelectedIndex() == 1){
				this.customerRow.setVisible(true);
			}
		}
	}
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * closes the dialog window
	 * @throws InterruptedException 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		this.window_FinanceTypeSelect.onClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		fillFinanceDetails();
		logger.debug("Leaving" + event.toString());
	}    

	/**
	 * Method to invoke filldata method in WIFFinanceMain dialog control.
	 * 
	 * **/
	private void fillFinanceDetails() throws InterruptedException {
		logger.debug("Entering");
		if(this.wifFinanceMainDialogCtrl!=null){
			
			if(StringUtils.trimToEmpty(this.finType.getValue()).equals("")){
				throw new WrongValueException(this.finType,"Finance Type must be selected.");
			}
			if(this.custType.getSelectedIndex() == 1 && this.custID.longValue() == 0){
				throw new WrongValueException(this.btnSearchCustCIF,"Customer must be select from Existing Customer");
			}
			
			String repayFrq = financeType.getFinRpyFrq();
			financeType.setFinGrcDftIntFrq(repayFrq);
			financeType.setFinGrcCpzFrq(repayFrq);
			financeType.setFinGrcRvwFrq(repayFrq);
			financeType.setFinDftIntFrq(repayFrq);
			financeType.setFinRvwFrq(repayFrq);
			financeType.setFinCpzFrq(repayFrq);
			financeType.setFinRepayPftOnFrq(false);
			
			FinanceMain financeMain = this.financeDetail.getFinScheduleData().getFinanceMain();
			if(this.financeDetail == null){
				this.financeDetail = new FinanceDetail();
				financeMain = new FinanceMain();
			}
			this.financeDetail.getFinScheduleData().setFinanceMain(financeMain,financeType);
			this.financeDetail.getFinScheduleData().setFinanceType(financeType);
			this.financeDetail.setNewRecord(true);
			this.financeDetail.getFinScheduleData().getFinanceMain().setAllowGrcPeriod(false);
			
			//Step Policy Details
			if(financeType.isStepFinance()){
				List<StepPolicyDetail> stepPolicyList = getStepPolicyService().getStepPolicyDetailsById(financeType.getDftStepPolicy());
				this.financeDetail.getFinScheduleData().resetStepPolicyDetails(stepPolicyList);
			}
			
			//Fetch Fee Charge Details List
			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			this.financeDetail.setFeeCharges(getFinanceDetailService().getFeeRuleDetails(this.financeDetail.getFinScheduleData().getFinanceType(), 
					curBussDate, true));	
			
			boolean newCust = false;
			if(this.custType.getSelectedIndex() == 0){
				newCust = true;
				wifcustomer.setNewRecord(true);
				PFSParameter parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_CURR");
				wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());
				wifcustomer.setLovDescCustBaseCcyName(parameter.getSysParmDescription());
				parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_NATION");
				wifcustomer.setCustNationality(parameter.getSysParmValue().trim());
				wifcustomer.setLovDescCustNationalityName(parameter.getSysParmDescription());
				wifcustomer.setCustTypeCode("EA");
				wifcustomer.setLovDescCustTypeCodeName("Individual");
				wifcustomer.setCustCtgCode("INDV");
				wifcustomer.setLovDescCustCtgCodeName("Individual");
			}
			
			Map<String, BigDecimal> incomeDetailMap = null;
			if(!newCust && loanType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
				incomeDetailMap = getCustomerIncomeService().getCustomerIncomeByCustomer(wifcustomer.getCustID(), false);
				wifcustomer.setExistCustID(wifcustomer.getCustID());
				wifcustomer.setCustID(0);
				wifcustomer.setNewRecord(true);
			}
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			
			if(loanType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
				wifcustomer.setCustomerIncomeList(getFinanceDetailService().prepareIncomeDetails());
				this.financeDetail.setCustomer(wifcustomer);
				
				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails("", financeType.getFinCcy(),
						BigDecimal.ZERO, true, financeType.getFinType(), null));
				
				getScoringDetailService().setFinanceScoringDetails(financeDetail, 
						this.finType.getValue(), null, wifcustomer.getLovDescCustCtgType());
				
				map.put("incomeDetailMap",incomeDetailMap);
			}else if(loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
				this.financeDetail.getFinScheduleData().getFinanceMain().setCustID(this.custID.longValue());
				this.financeDetail.getFinScheduleData().getFinanceMain().setLovDescCustCIF(this.lovDescCustCIF.getValue());
				this.financeDetail.getFinScheduleData().getFinanceMain().setLovDescCustShrtName(this.custShrtName.getValue());
				this.financeDetail.getFinScheduleData().getFinanceMain().setFinBranch(finBranch);
				
				IndicativeTermDetail termDetail = new IndicativeTermDetail();
				termDetail.setCustId(this.custID.longValue());
				termDetail.setLovDescCustCIF(this.lovDescCustCIF.getValue());
				termDetail.setLovDescCustShrtName(this.custShrtName.getValue());
				termDetail.setNewRecord(true);
				termDetail.setWorkflowId(0);
				this.financeDetail.setIndicativeTermDetail(termDetail);
			}
			
			map.put("wIFFinanceMainListCtrl", this.wIFFinanceMainListCtrl);
			map.put("financeDetail",this.financeDetail);
			map.put("loanType",this.loanType);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				String productType = this.loanType;
				if(!productType.equals("")){
					if(!productType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
						productType = "";
					}else{
						productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
					}
				}
				Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/"+productType+"WIFFinanceMainDialog.zul",null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		doClose();	
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public CustomerIncomeService getCustomerIncomeService() {
		return customerIncomeService;
	}
	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}
	public void setEligibilityDetailService(
			EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public ScoringDetailService getScoringDetailService() {
		return scoringDetailService;
	}
	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
	
}
