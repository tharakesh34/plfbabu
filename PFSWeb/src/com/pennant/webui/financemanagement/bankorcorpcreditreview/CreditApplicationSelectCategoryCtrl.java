package com.pennant.webui.financemanagement.bankorcorpcreditreview;


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


import java.io.Serializable;
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
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class CreditApplicationSelectCategoryCtrl extends GFCBaseListCtrl<FinanceType> implements Serializable {

	private static final long serialVersionUID = 3257569537441008225L;
	private final static Logger logger = Logger.getLogger(CreditApplicationSelectCategoryCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CreditRevSelectCategory; 				// autoWired
	protected Borderlayout borderLayout_FinanceTypeList; 	// autoWired
		
	protected Radiogroup custType;
	protected Radio custType_Existing;
	protected Radio custType_Prospect;
	
	protected Row customerRow;
	protected Longbox custID;
	protected Textbox lovDescCustCIF;
	protected Button btnSearchCustCIF;
	protected Label custShrtName;

	protected Row       auditYearRow;
	protected Intbox    auditYear;
	
	protected Row        customerCategoryRow;
	protected Combobox   custCategory;
	
	protected Row        auditPeriodRow;
	protected Combobox   auditPeriod;
	
	@SuppressWarnings("unused")
	private transient CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl;
	private transient CreditApplicationReviewListCtrl creditApplicationReviewListCtrl;
	private transient FinCreditReviewDetails creditReviewDetail;
	private    Customer customer;
	private    CreditApplicationReviewService creditApplicationReviewService;
	private    FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private    WIFCustomer wifcustomer = new WIFCustomer();
	int currentYear = DateUtility.getYear((Date)SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));

	List<Filter> filterList = null;	
	protected JdbcSearchObject<Customer> newSearchObject ;

	/**
	 * default constructor.<br>
	 */
	public CreditApplicationSelectCategoryCtrl() {
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
	public void onCreate$window_CreditRevSelectCategory(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("creditApplicationReviewDialogCtrl")) {
			this.creditApplicationReviewDialogCtrl = (CreditApplicationReviewDialogCtrl) args.get("creditApplicationReviewDialogCtrl");			
		} else {
			this.creditApplicationReviewDialogCtrl = null;
		}
		
		if(args.containsKey("creditApplicationReviewListCtrl")) {
			this.creditApplicationReviewListCtrl = (CreditApplicationReviewListCtrl) args.get("creditApplicationReviewListCtrl");			
		} else {
			this.creditApplicationReviewListCtrl = null;
		}
		
		if(args.containsKey("aCreditReviewDetails")) {
			this.creditReviewDetail = (FinCreditReviewDetails) args.get("aCreditReviewDetails");			
		} else {
			this.creditReviewDetail = null;
		}
		fillComboBox(custCategory, "", PennantStaticListUtil.getCategoryType(), "");
		fillComboBox(auditPeriod, "", PennantStaticListUtil.getPeriodList(), "");
//		if (!this.auditYear.isReadonly()) {
//			this.auditYear.setConstraint(new IntValidator(4,Labels.getLabel("label_CreditApplicationReviewDialog_auditPeriod.value"), false));
//		}
		this.customerCategoryRow.setVisible(false);
		this.btnSearchCustCIF.setVisible(true);
		this.window_CreditRevSelectCategory.doModal();
	
		logger.debug("Leaving" + event.toString());
	}
	
	
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("filtersList", getFilterList());
		map.put("searchObject",getSearchObj());

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}
	
	
	
	private List<Filter> getFilterList() {
		filterList = new ArrayList<Filter>();		

		if(this.custType.getSelectedIndex() == 1){
			filterList.add(new Filter("CustCoreBank",  "", Filter.OP_EQUAL));
		}else{
			filterList.add(new Filter("CustCoreBank",  "", Filter.OP_NOT_EQUAL));
		}
		filterList.add(new Filter("lovDescCustCtgType", "I", Filter.OP_NOT_EQUAL));

		return filterList;
	}
	
	
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		Customer aCustomer = (Customer) nCustomer;
		if(aCustomer != null){
			this.custID.setValue(aCustomer.getCustID());
			this.lovDescCustCIF.setValue(aCustomer.getCustCIF());
			this.custShrtName.setValue(aCustomer.getCustShrtName());
			BeanUtils.copyProperties(aCustomer, wifcustomer);
		}
		logger.debug("Leaving");
	}
	
	public JdbcSearchObject<Customer> getSearchObj() {
		
		newSearchObject = new JdbcSearchObject<Customer>(Customer.class,getListRows());
		newSearchObject.addTabelName("Customers_AView");
		if (filterList != null & filterList.size() > 0) {
			for (int k = 0; k < filterList.size(); k++) {
				newSearchObject.addFilter(filterList.get(k));
			}
		}
		return this.newSearchObject;
	}
	
	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
	
		this.lovDescCustCIF.clearErrorMessage();
		    customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), getFilterList());

		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
			this.custShrtName.setValue(customer.getCustShrtName());
			BeanUtils.copyProperties(customer, wifcustomer);
		} else {
			this.custID.setValue(Long.valueOf(0));
			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", 
					new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_CustId.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$custType(Event event){
		if(this.custType.getSelectedItem() != null){
			if(this.custType.getSelectedIndex() == 0){
				this.customerCategoryRow.setVisible(false);
				this.btnSearchCustCIF.setVisible(true);
                this.lovDescCustCIF.setValue("");
                this.custShrtName.setValue("");
                this.auditYear.setText("");
             } else {
                 this.lovDescCustCIF.setValue("");
                 this.custShrtName.setValue("");
                 this.auditYear.setText("");
				//this.customerCategoryRow.setVisible(true);
				//this.btnSearchCustCIF.setVisible(false);
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
		this.window_CreditRevSelectCategory.onClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doWriteComponentsToBean();
		getCreditApplicationRevDialog();
		this.window_CreditRevSelectCategory.onClose();
		logger.debug("Leaving" + event.toString());
	}    

	
	public void getCreditApplicationRevDialog(){
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("creditReviewDetails", creditReviewDetail);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listBox ListModel. This is fine for synchronizing the data in the CreditReviewDetailsListbox from the dialog when
		 * we do a delete, edit or insert a FinCreditReviewDetails.
		 */
		map.put("creditApplicationReviewListCtrl", creditApplicationReviewListCtrl);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			try {
				PTMessageUtils.showErrorMessage(e.toString());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	
	public void doWriteComponentsToBean(){
		logger.debug("Entering");
		    List<WrongValueException> wveList = new ArrayList<WrongValueException>();
		    
		    if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
		    	this.lovDescCustCIF.clearErrorMessage();
		    	customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), getFilterList());
		    	if(customer != null){
		    		creditReviewDetail.setLovDescCustCIF((this.lovDescCustCIF.getValue()));
		    	} else {
		    		try {
		    			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_EMPTY",
		    					new String[] {Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value"),
		    					Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value") }));
		    		}catch(WrongValueException wve){
		    			wveList.add(wve);
		    		}
		    	}
		    } else {
		    	try {
		    		throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_EMPTY",
		    				new String[] {Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value"),
		    				Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value") }));
		    	}catch(WrongValueException wve){
		    		wveList.add(wve);
		    	}
		    }
              
            if(this.auditYear.intValue() != 0){
            	Date appDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
            	Date startDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");
            	startDate = DateUtility.addDays(startDate, 1);
            	if(this.auditYear.intValue() > DateUtility.getYear(appDate)){
            		try{
            			throw new WrongValueException(this.auditYear, Labels.getLabel("const_NO_FUTURE_YEAR",
            					new String[] {Labels.getLabel("label_CreditRevSelectCategory_AuditYear.value"),
            					Labels.getLabel("label_CreditRevSelectCategory_AuditYear.value") }));
            		}catch(WrongValueException wve){
            			wveList.add(wve);
            		}
            	} 
            	if(this.auditYear.intValue() < DateUtility.getYear(startDate)){
            		try{
            			throw new WrongValueException(this.auditYear, Labels.getLabel("label_CreditReviewNotValidYear",
            					new String[] {Labels.getLabel("label_CreditRevSelectCategory_AuditYear.value"),
            					Labels.getLabel("label_CreditRevSelectCategory_AuditYear.value") }));
            		}catch(WrongValueException wve){
            			wveList.add(wve);
            		}
            	} else {
            		creditReviewDetail.setAuditYear(String.valueOf(this.auditYear.intValue()));
            	}
            } else {
            	try{
            		throw new WrongValueException(this.auditYear, Labels.getLabel("FIELD_NO_EMPTY",
            				new String[] {Labels.getLabel("label_CreditRevSelectCategory_AuditYear.value"),
            				Labels.getLabel("label_CreditRevSelectCategory_AuditYear.value") }));
            	}catch(WrongValueException wve){
            		wveList.add(wve);
            	}
            }
            
              if(this.customerCategoryRow.isVisible()){
            	  if(!StringUtils.trimToEmpty(this.custCategory.getSelectedItem().getValue().toString()).equals("#")){
                	  creditReviewDetail.setLovDescCustCtgCode(this.custCategory.getSelectedItem().getValue().toString());
                  } else {
                	  try{
                	  throw new WrongValueException(this.custCategory, Labels.getLabel("FIELD_NO_EMPTY",
                			  new String[] {Labels.getLabel("label_CreditRevSelectCategory_CustomerCategory.value"),
                			  Labels.getLabel("label_CreditRevSelectCategory_CustomerCategory.value") }));
                	  }catch(WrongValueException wve){
                		  wveList.add(wve);
                	  }
                  }
              }
              
              if(!StringUtils.trimToEmpty(this.auditPeriod.getSelectedItem().getValue().toString()).equals("#")){
            	  creditReviewDetail.setAuditPeriod(Integer.parseInt(this.auditPeriod.getSelectedItem().getValue().toString()));
              } else {
            	  try{
            	  throw new WrongValueException(this.auditPeriod, Labels.getLabel("FIELD_NO_EMPTY",
            			  new String[] {Labels.getLabel("label_CreditRevSelectCategory_auditPeriod.value"),
            			  Labels.getLabel("label_CreditRevSelectCategory_auditPeriod.value") }));
            	  }catch(WrongValueException wve){
            		  wveList.add(wve);
            	  }
              }
              
              if (wveList.size() > 0) {
        			WrongValueException[] wvea = new WrongValueException[wveList.size()];
        			for (int i = 0; i < wveList.size(); i++) {
        				wvea[i] = (WrongValueException) wveList.get(i);
        			}
        			throw new WrongValuesException(wvea);
        		} 
              
              String category="";
              // Duplicate Audit Year Validation
              if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
            	  customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), getFilterList());
            	  if(customer.getCustCtgCode().equalsIgnoreCase("Bank")){
            		  category = "B";
            	  } else {
            		  category = "C";
            	  }
              }
               
              if(this.custID.getValue() != null && creditReviewDetail.getAuditPeriod() != 0){
            	  Map<String, List<FinCreditReviewSummary>>   creditReviewSummaryMap= getCreditApplicationReviewService().
            	  getListCreditReviewSummaryByCustId2(custID.getValue(), 0, this.auditYear.getValue().equals("") ? 0 : 
            		  this.auditYear.intValue(),  category, creditReviewDetail.getAuditPeriod(), true, "_View");
            	  if(creditReviewSummaryMap.size() > 0){
            		 
            			  throw new WrongValueException(this.auditYear, Labels.getLabel("const_YEAR_PERIOD",
            					  new String[] {String.valueOf(this.auditYear.intValue()),
            					  String.valueOf(this.auditPeriod.getValue())}));
            	  }
              }
             
		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public FinCreditReviewDetails getCreditReviewDetail() {
		return creditReviewDetail;
	}

	public void setCreditReviewDetail(FinCreditReviewDetails creditReviewDetail) {
		this.creditReviewDetail = creditReviewDetail;
	}

	public void setCreditApplicationReviewService(
			CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setFinCreditRevSubCategoryService(
			FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public FinCreditRevSubCategoryService getFinCreditRevSubCategoryService() {
		return finCreditRevSubCategoryService;
	}
	
}
