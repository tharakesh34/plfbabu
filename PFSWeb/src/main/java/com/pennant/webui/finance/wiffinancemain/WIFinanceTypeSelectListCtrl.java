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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class WIFinanceTypeSelectListCtrl extends GFCBaseListCtrl<FinanceType> {
	private static final long serialVersionUID = 3257569537441008225L;
	private static final Logger logger = Logger.getLogger(WIFinanceTypeSelectListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceTypeSelect; 				// autoWired
	protected Borderlayout borderLayout_FinanceTypeList; 	// autoWired
	
	protected ExtendedCombobox finType;
	protected ExtendedCombobox promotionCode;
	
	protected Radiogroup custType;
	protected Radio custType_New;
	protected Radio custType_Exist;
	protected Row wIfCustSelectionRow;
	protected Row customerRow;
	protected Row promotionCodeRow;
	
	protected Space space_CustId;
	protected Longbox custID;
	protected Textbox lovDescCustCIF;
	protected Button btnSearchCustCIF;
	
	protected Label custShrtName;
	private String finBranch;
	
	private Row row_EIDNumber;
	private Textbox eidNumber;

	private transient WIFFinanceMainDialogCtrl wifFinanceMainDialogCtrl;
	private transient WIFFinanceMainListCtrl wIFFinanceMainListCtrl;
	private transient FinanceDetail financeDetail;
	
	private transient FinanceDetailService financeDetailService;
	private transient CustomerIncomeService customerIncomeService;
	private transient EligibilityDetailService eligibilityDetailService;
	private transient ScoringDetailService scoringDetailService;
	private transient StepPolicyService stepPolicyService;
	private transient CustomerDetailsService  customerDetailsService;
	
	private FinanceType financeType;
	private WIFCustomer wifcustomer = new WIFCustomer();
	private String loanType = "";
	private BranchService branchService;
	
	/**
	 * default constructor.<br>
	 */
	public WIFinanceTypeSelectListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
	}

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
		

		if (arguments.containsKey("WIFFinanceMainDialogCtrl")) {
			this.wifFinanceMainDialogCtrl = (WIFFinanceMainDialogCtrl) arguments.get("WIFFinanceMainDialogCtrl");			
		} else {
			this.wifFinanceMainDialogCtrl = null;
		}
		
		if(arguments.containsKey("WIFFinanceMainListCtrl")) {
			this.wIFFinanceMainListCtrl = (WIFFinanceMainListCtrl) arguments.get("WIFFinanceMainListCtrl");			
		} else {
			this.wIFFinanceMainListCtrl = null;
		}
		
		if(arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");			
		} else {
			this.financeDetail = null;
		}
		
		if(arguments.containsKey("loanType")) {
			this.loanType = (String) arguments.get("loanType");			
		}
		
		if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_RETAIL)){
			this.custType.setSelectedIndex(0);
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_FACILITY) || 
				StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_COMMERCIAL)){
			this.space_CustId.setSclass("");
			this.custType.setSelectedIndex(1);
			this.wIfCustSelectionRow.setVisible(false);
			this.customerRow.setVisible(true);
			this.row_EIDNumber.setVisible(false);
		}else{
			this.space_CustId.setSclass("");
			this.row_EIDNumber.setVisible(false);
			this.custType.setSelectedIndex(1);
			this.wIfCustSelectionRow.setVisible(false);
			this.customerRow.setVisible(true);
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
		
		this.promotionCode.setMaxlength(8);
		this.promotionCode.setModuleName("PromotionCode");
		this.promotionCode.setValueColumn("FinType");
		this.promotionCode.setDescColumn("FinTypeDesc");
		this.promotionCode.setValidateColumns(new String[]{"FinType"});
		
		Filter[] filters = null ;
		if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_RETAIL)){
			filters = new Filter[2] ;
			filters[0]= new Filter("FinDivision",  FinanceConstants.FIN_DIVISION_RETAIL, Filter.OP_EQUAL);
			filters[1] = new Filter("ProductCategory", "ODFCLITY", Filter.OP_NOT_EQUAL);
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_CORPORATE)){
			filters = new Filter[2] ;
			filters[0]= new Filter("FinCategory",  FinanceConstants.PRODUCT_ISTISNA, Filter.OP_NOT_EQUAL);
			filters[1] = new Filter("ProductCategory", "ODFCLITY", Filter.OP_NOT_EQUAL);
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_FACILITY)){
			filters = new Filter[2] ;
			List<String> divList = new ArrayList<String>(2);
			divList.add(FinanceConstants.FIN_DIVISION_COMMERCIAL);
			divList.add(FinanceConstants.FIN_DIVISION_CORPORATE);
			filters[0]= Filter.in("FinDivision", divList);
			filters[1] = new Filter("ProductCategory", "ODFCLITY", Filter.OP_NOT_EQUAL);
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_COMMERCIAL)){
			filters = new Filter[2] ;
			filters[0]= new Filter("FinDivision",  FinanceConstants.FIN_DIVISION_COMMERCIAL, Filter.OP_EQUAL);
			filters[1] = new Filter("ProductCategory", "ODFCLITY", Filter.OP_NOT_EQUAL);
		}
		filters = new Filter[1] ;
		filters[0] = new Filter("ProductCategory", "ODFCLITY", Filter.OP_NOT_EQUAL);
		this.finType.setFilters(filters);
		this.lovDescCustCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.eidNumber.setMaxlength(LengthConstants.LEN_EID);

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
				Clients.clearWrongValue(this.finType);
			}
		}
		
		this.promotionCode.setValue("","");
		this.promotionCode.setObject("");
		
		this.promotionCode.setFilters(new Filter[0]);
		if(!("").equals(this.finType.getValue().trim())){
			List<FinanceType> promotionCodeList = getPromotionwithAccess(); 
			if(!promotionCodeList.isEmpty()) {
				this.promotionCodeRow.setVisible(true);
				this.promotionCode.setList(promotionCodeList);	
			} else {
				this.promotionCodeRow.setVisible(false);
				this.promotionCode.setList(promotionCodeList);
			}
		}else{
			this.promotionCodeRow.setVisible(false);
			this.promotionCode.setList(null);
		}
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * fill promotionCode and financeType values by Clicking "promotionCode" Search button
	 * 
	 * @param event
	 */
	public void onFulfill$promotionCode(Event event){
		logger.debug("Entering " + event.toString());

		this.promotionCode.setConstraint("");
		this.promotionCode.clearErrorMessage();
		this.promotionCode.setErrorMessage("");

		Object dataObject = this.promotionCode.getObject();
		if (dataObject instanceof String) {
			this.promotionCode.setValue(dataObject.toString());
			this.promotionCode.setDescription("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.financeType = details;
				this.promotionCode.setValue(details.getFinType());
				this.promotionCode.setDescription(details.getFinTypeDesc());
				this.finType.setValue(details.getProduct());
				this.finType.setDescription(details.getLovDescPromoFinTypeDesc());
			} else {
				this.finType.setValue("");
				this.finType.setDescription("");
			}
		}
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * get FinanceWorkFlow list by selecting the "financeType" Search button
	 * 
	 * @return
	 */
	public List<FinanceType> getPromotionwithAccess(){
		logger.debug("Entering");
		
		JdbcSearchObject<FinanceType> searchObject=new JdbcSearchObject<FinanceType>(FinanceType.class);
		searchObject.addTabelName("RMTFinanceTypes_AView");
		Filter[] filters= new Filter[4];

		Date appDate= DateUtility.getAppDate();
		filters[0]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1]= new Filter("Product", StringUtils.trimToEmpty(this.finType.getValue()), Filter.OP_EQUAL);
		filters[2]= new Filter("StartDate", DateUtility.formateDate(appDate, PennantConstants.DBDateFormat), Filter.OP_LESS_OR_EQUAL);
		filters[3]= new Filter("EndDate", DateUtility.formateDate(appDate, PennantConstants.DBDateFormat), Filter.OP_GREATER_OR_EQUAL);
		searchObject.addFilterAnd(filters);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FinanceType> list = pagedListService.getBySearchObject(searchObject);
		if (list!=null && !list.isEmpty()) {
			return list;
		}
		logger.debug("Leaving");
		
		return new ArrayList<FinanceType>();
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.btnSearchCustCIF);
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

		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", null);
		
		ArrayList<Filter> filterList = new ArrayList<Filter>();
		if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_RETAIL)){
			//filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL));
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_FACILITY)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_NOT_EQUAL));
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_COMMERCIAL)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.PFF_CUSTCTG_SME, Filter.OP_EQUAL));
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
			getSwiftBranchCode(this.finBranch);
			BeanUtils.copyProperties(customer, wifcustomer);
		}
		logger.debug("Leaving");
	}

	private void getSwiftBranchCode(String finBranch) {
		Branch branch = this.branchService.getApprovedBranchById(finBranch);
		if (branch != null) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setSwiftBranchCode(branch.getBranchSwiftBrnCde());
		}
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
		if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_RETAIL)){
			//filterList.add(new Filter("lovDescCustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL));
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_FACILITY)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_NOT_EQUAL));
		}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_COMMERCIAL)){
			filterList.add(new Filter("lovDescCustCtgType", PennantConstants.PFF_CUSTCTG_SME, Filter.OP_EQUAL));
		}

		this.lovDescCustCIF.clearErrorMessage();
		Customer customer = null;
		if(StringUtils.isNotBlank(this.lovDescCustCIF.getValue())) {
			customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), filterList);
		} else {
			this.custID.setValue(Long.valueOf(0));
			this.custShrtName.setValue("");
			throw new WrongValueException(this.btnSearchCustCIF,"Customer must be selected from Existing Customers");
		}

		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
			this.custShrtName.setValue(customer.getCustShrtName());
			BeanUtils.copyProperties(customer, wifcustomer);
		} else {
			this.custID.setValue(Long.valueOf(0));
			this.custShrtName.setValue("");
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
				this.row_EIDNumber.setVisible(false);
				Clients.clearWrongValue(this.finType);
			} else {
				Clients.clearWrongValue(this.finType);
				this.row_EIDNumber.setVisible(true);
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
		if (doFieldValidation()) {
			fillFinanceDetails();
		} else {
			return;
		}
		logger.debug("Leaving" + event.toString());
	}    

	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 * @throws InterruptedException 
	 */
	private boolean doFieldValidation() throws InterruptedException {
		logger.debug("Entering ");
		
		this.eidNumber.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.finType.setConstraint("");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Validating finType
		try {
			if(StringUtils.isEmpty(this.finType.getValue())){
				throw new WrongValueException(this.finType,Labels.getLabel("FIELD_IS_MAND" ,new String[]{Labels.getLabel("label_FinanceMainDialog_FinType.value")}));
			}
		} catch(WrongValueException we) {
			wve.add(we);
		}

		// Validating custID
		try {
			this.lovDescCustCIF.setConstraint("");
			this.lovDescCustCIF.setErrorMessage("");
			if(StringUtils.isNotEmpty(loanType) && !StringUtils.equals(loanType, FinanceConstants.FIN_DIVISION_FACILITY)
					&& !StringUtils.equals(loanType, FinanceConstants.FIN_DIVISION_COMMERCIAL)) {
				if(this.custType.getSelectedIndex() == 1 && this.custID.longValue() == 0){
					throw new WrongValueException(this.btnSearchCustCIF,"Customer must be selected from Existing Customers");
				}
			}
		} catch(WrongValueException we) {
			wve.add(we);
		}

		//Validating EID Number
		if (this.custType_New.isChecked()) {
			this.eidNumber.clearErrorMessage();
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.eidNumber.setConstraint(new PTStringValidator(Labels
						.getLabel("label_SelectFinanceTypeDialog_EIDNumber.value"),
						PennantRegularExpressions.REGEX_EIDNUMBER, true));
			}
			
		} else {
			this.eidNumber.clearErrorMessage();
			this.eidNumber.setConstraint("");
		}

		try{
			this.eidNumber.getValue();
		}catch (WrongValueException e) {
			wve.add(e);
		}

		if(wve.size()>0) {
			WrongValueException wvea[] = new WrongValueException[wve.size()];
			for(int i = 0; i<wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		if(custType_New.isChecked()){
			if(StringUtils.isNotBlank(this.eidNumber.getValue())){
				String eidNum = PennantApplicationUtil.unFormatEIDNumber(this.eidNumber.getValue());
				Long custID = getCustomerDetailsService().getEIDNumberByCustId(eidNum, "_View");
				if (!custID.equals(0)) {
					String msg = Labels.getLabel("label_SelectFinanceTypeDialog_ProspectExist",new String[]{Labels.getLabel("label_CustCRCPR"),custID + ". \n"});

					if (MessageUtil.confirm(msg) != MessageUtil.YES) {
						return false;
					}
					this.custType_Exist.setSelected(true);
					this.wifcustomer.setExistCustID(custID);
					this.custID.setValue(custID);
					wifcustomer.setNewRecord(false);
				}else{
					if(StringUtils.isNotEmpty(loanType)){
						wifcustomer = getCustomerDetailsService().getWIFByEIDNumber(eidNum, "_AView");
						if(wifcustomer!=null){
							String msg = Labels.getLabel("label_FinanceTypeDialog_ProspectExist");

							if (MessageUtil.confirm(msg) != MessageUtil.YES) {
								return false;
							}
							this.custType_Exist.setSelected(true);
							wifcustomer.setNewRecord(false);
						}else{
							wifcustomer = new WIFCustomer();
						}
					}
				}
			}
		}
		logger.debug("Leaving ");
		return true;
	}

	/**
	 * onChange Event to adjust EID Number format
	 * @param event
	 */
	public void onChange$eidNumber(Event event) {
		logger.debug("Entering" + event.toString());
		this.eidNumber.setConstraint("");
		this.eidNumber.setErrorMessage("");
		this.eidNumber.setValue(PennantApplicationUtil.formatEIDNumber(this.eidNumber.getValue()));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method to invoke filldata method in WIFFinanceMain dialog control.
	 * 
	 * **/
	private void fillFinanceDetails() throws InterruptedException {
		logger.debug("Entering");
		if(this.wifFinanceMainDialogCtrl!=null){

			/*String repayFrq = financeType.getFinRpyFrq();
			financeType.setFinGrcDftIntFrq(repayFrq);
			financeType.setFinGrcCpzFrq(repayFrq);
			financeType.setFinGrcRvwFrq(repayFrq);
			financeType.setFinDftIntFrq(repayFrq);
			financeType.setFinRvwFrq(repayFrq);
			financeType.setFinCpzFrq(repayFrq);*/
			//financeType.setFinRepayPftOnFrq(false);
			//set the default barch for wif with out customer selection
			getSwiftBranchCode(getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
			FinanceMain financeMain = this.financeDetail.getFinScheduleData().getFinanceMain();
			this.financeDetail.getFinScheduleData().setFinanceMain(financeMain,financeType);
			this.financeDetail.getFinScheduleData().setFinanceType(financeType);
			this.financeDetail.setNewRecord(true);
			if(financeType.getFinCategory().equals(FinanceConstants.PRODUCT_ISTISNA)){
				this.financeDetail.getFinScheduleData().getFinanceMain().setAllowGrcPeriod(financeType.isFInIsAlwGrace());
			}else{
				this.financeDetail.getFinScheduleData().getFinanceMain().setAllowGrcPeriod(false);
			}
			
			//Step Policy Details
			if(financeType.isStepFinance()){
				List<StepPolicyDetail> stepPolicyList = getStepPolicyService().getStepPolicyDetailsById(financeType.getDftStepPolicy());
				this.financeDetail.getFinScheduleData().resetStepPolicyDetails(stepPolicyList);
			}
			
			//Fetch Fee Charge Details List
			Date curBussDate = DateUtility.getAppDate();
			this.financeDetail.setFeeCharges(getFinanceDetailService().getFeeRuleDetails(this.financeDetail.getFinScheduleData().getFinanceType(), 
					curBussDate, true));	
			
			if (curBussDate != null) {
				String finEvent = PennantApplicationUtil.getEventCode(curBussDate);
				this.financeDetail.getFinScheduleData().setFeeEvent(finEvent);
				this.financeDetail.setFinTypeFeesList(getFinanceDetailService().getFinTypeFees(this.finType.getValue(),
						finEvent, true, FinanceConstants.MODULEID_FINTYPE));
			}
			
			boolean newCust = false;
			if(this.custType.getSelectedIndex() == 0){
				newCust = true;
				wifcustomer.setCustCRCPR(this.eidNumber.getValue());
				wifcustomer.setNewRecord(true);
				PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_CURR");
				wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());

				Filter[] countrysystemDefault=new Filter[1];
				countrysystemDefault[0]=new Filter("SystemDefault", "1",Filter.OP_EQUAL);
				Object countryObj=	PennantAppUtil.getSystemDefault("Country","", countrysystemDefault);
				
				if (countryObj!=null) {
					Country country=(Country) countryObj;
					wifcustomer.setCustNationality(country.getCountryCode());
					wifcustomer.setLovDescCustNationalityName(country.getCountryDesc());
				}
				
				wifcustomer.setCustTypeCode(PennantConstants.DEFAULT_CUST_TYPE);
				wifcustomer.setLovDescCustTypeCodeName(PennantConstants.DEFAULT_CUST_TYPE);
				wifcustomer.setCustCtgCode(PennantConstants.PFF_CUSTCTG_INDIV);
				wifcustomer.setLovDescCustCtgCodeName(PennantConstants.PFF_CUSTCTG_INDIV);
			}
			
			Map<String, BigDecimal> incomeDetailMap = null;
			if(!newCust && StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_RETAIL)){
				incomeDetailMap = getCustomerIncomeService().getCustomerIncomeByCustomer(wifcustomer.getCustID(), false);
				try {
					wifcustomer = fetchCustomerData();
				} catch (InterfaceException e) {
					logger.error("Exception: ", e);
				}
			}
			
			Map<String, Object> map = getDefaultArguments();
			if(wifcustomer != null) {
				if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_RETAIL)){
					wifcustomer.setCustomerIncomeList(getFinanceDetailService().prepareIncomeDetails());
					this.financeDetail.setCustomer(wifcustomer);
					
					financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails("", financeType.getFinCcy(),
							BigDecimal.ZERO, true, financeType.getFinType(), null,FinanceConstants.FINSER_EVENT_ORG));
					
					getScoringDetailService().setFinanceScoringDetails(financeDetail, 
							this.finType.getValue(), null, wifcustomer.getLovDescCustCtgType(),FinanceConstants.FINSER_EVENT_ORG);
					
					map.put("incomeDetailMap",incomeDetailMap);
				}else if(StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_FACILITY) ||
						StringUtils.equals(loanType,FinanceConstants.FIN_DIVISION_COMMERCIAL)){
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
				} else {
					if(this.custID.longValue() != 0){
						wifcustomer.setExistCustID(this.custID.longValue());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						this.financeDetail.setCustomer(wifcustomer);
					}else{
						this.financeDetail.setCustomer(null);
					}
				}
			}
			
			map.put("wIFFinanceMainListCtrl", this.wIFFinanceMainListCtrl);
			map.put("financeDetail",this.financeDetail);
			map.put("loanType",this.loanType);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				String productType = this.loanType;
				if(StringUtils.isNotEmpty(productType)){
					if(!StringUtils.equals(productType,FinanceConstants.FIN_DIVISION_RETAIL)){
						productType = "";
					}else{
						productType = "RETAIL";
						productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
					}
				}
				Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/"+productType+"WIFFinanceMainDialog.zul",null,map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		
		doClose();	
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
	public WIFCustomer fetchCustomerData() throws InterruptedException, InterfaceException {
		logger.debug("Entering");
		
		// Get the data of Customer from Core Banking Customer
		try {
			String cif = String.valueOf(this.custID.getValue());
			
			//If WIFCustomer Exists
			if (this.custID.getValue() == null) {
				return wifcustomer;
			}
			//If  customer exist is checked 
			if (this.custType_Exist.isChecked()){
				if (StringUtils.isEmpty(cif)) {
					throw new WrongValueException(this.custID, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
				} else {
					// check Customer Data in LOCAL PFF system
					wifcustomer = getCustomerDetailsService().getWIFCustomerByCIF(this.custID.getValue());
					wifcustomer.setExistCustID(this.custID.getValue());
					wifcustomer.setCustID(0);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);

			if (MessageUtil.confirm(Labels.getLabel("Cust_NotFound_NewCustomer")) == MessageUtil.YES) {
				return null;
			}
		}
		logger.debug("Leaving");
		return wifcustomer;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	
}
