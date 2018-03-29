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
 * FileName    		:  SelectVASConfigurationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-01-2017    														*
 *                                                                  						*
 * Modified Date    :  10-01-2017   														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *10-01-2017         Pennant	                 0.1                                            * 
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
package com.pennant.webui.configuration.vasrecording;

import java.util.ArrayList;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.configuration.VASConfigurationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.financemain.FinVasRecordingDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectVASConfigurationDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final long				serialVersionUID	= 1L;

	private static final Logger				logger				= Logger.getLogger(SelectVASConfigurationDialogCtrl.class);

	protected Window						window_SelectVASConfiguration;
	protected ExtendedCombobox				productType;
	protected Button						btnProceed;

	protected Row							customerRow;
	protected Button						btnSearchCustCIF;
	protected Textbox						custCIF;
	protected Label							custName;

	protected Row							loanRow;
	protected ExtendedCombobox				loanType;

	protected Row							collateralRow;
	protected ExtendedCombobox				collteralType;

	private VASRecording					vasRecording;
	private VASConfiguration				vasConfiguration;
	private VASRecordingListCtrl			vasRecordingListCtrl;
	private FinVasRecordingDialogCtrl		finVasRecordingDialogCtrl;
	private FinanceWorkFlow					financeWorkFlow;
	private FinanceWorkFlowService			financeWorkFlowService;
	private VASRecordingService				vASRecordingService;
	private CustomerDetailsService			customerDetailsService;
	private VASConfigurationService			vasConfigurationService;
	private transient   FinanceDetailService    financeDetailService;   
	protected JdbcSearchObject<Customer>	custCIFSearchObject;
	private CustomerDAO						customerDAO;
	private FinTypeVASProducts				finTypeVASProducts;

	private List<String>					userRoleCodeList	= new ArrayList<String>();
	private boolean 						isFinanceProcess = false;
	private boolean 						waivedFlag = false;
	private boolean 						newRecord = false;
	private String							finType;

	public SelectVASConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectVASConfiguration(Event event) throws Exception {
		logger.debug("Entering");

		try {
			// Get the required arguments.
			this.vasRecording = (VASRecording) arguments.get("vASRecording");
			if (arguments.containsKey("vASRecordingListCtrl")) {
				this.vasRecordingListCtrl = (VASRecordingListCtrl) arguments.get("vASRecordingListCtrl");
			}
			if (arguments.containsKey("finVasRecordingDialogCtrl")) {
				this.finVasRecordingDialogCtrl = (FinVasRecordingDialogCtrl) arguments.get("finVasRecordingDialogCtrl");
				this.isFinanceProcess = true;
			}
			if (arguments.containsKey("newRecord")) {
				this.newRecord = (boolean) arguments.get("newRecord");
			}
			this.userRoleCodeList = (ArrayList<String>) arguments.get("role");
			if (this.vasRecording == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			
			if (arguments.containsKey("finType")) {
				this.finType = (String) arguments.get("finType");
			}
			if (arguments.containsKey("waivedFlag")) {
				this.waivedFlag = (boolean) arguments.get("waivedFlag");
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		// Set the page level components.
		setPageComponents(window_SelectVASConfiguration);
		showSelectproductTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectproductTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectVASConfiguration.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// productType
		this.productType.setMaxlength(8);
		this.productType.setMandatoryStyle(true);
		
		if (isFinanceProcess) {
			this.productType.setModuleName("FinTypeVASProducts");
			this.productType.setValueColumn("VasProduct");
			this.productType.setValidateColumns(new String[] { "VasProduct" });

			Filter[] filters = new Filter[1];
			filters[0] = new Filter("FinType", finType.toString(), Filter.OP_EQUAL);
			this.productType.setFilters(filters);
		}else{
			this.productType.setModuleName("VASWorkFlow");
			this.productType.setValueColumn("TypeCode");
			this.productType.setDescColumn("VasProductDesc");
			this.productType.setValidateColumns(new String[] { "TypeCode" });

			Filter[] filters = new Filter[1];
			filters[0] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_ORG, Filter.OP_EQUAL);
			this.productType.setFilters(filters);
			
			String whereClause = getWhereClauseWithFirstTask();
			if (StringUtils.isNotEmpty(whereClause)) {
				this.productType.setWhereClause(whereClause);
			}
		}

		this.custCIF.setMaxlength(LengthConstants.LEN_CIF);

		// loanType
		this.loanType.setWidth("120px");
		this.loanType.setMandatoryStyle(true);
		this.loanType.setModuleName("FinanceDetail");
		this.loanType.setValueColumn("FinReference");
		this.loanType.setDescColumn("FinType");
		this.loanType.setValidateColumns(new String[] { "FinReference" });

		// collteralType
		this.collteralType.setWidth("120px");
		this.collteralType.setMandatoryStyle(true);
		this.collteralType.setModuleName("CollateralSetup");
		this.collteralType.setValueColumn("CollateralRef");
		this.collteralType.setDescColumn("CollateralType");
		this.collteralType.setValidateColumns(new String[] { "CollateralRef" });

		logger.debug("Leaving");
	}

	private String getWhereClauseWithFirstTask() {
		StringBuilder whereClause = new StringBuilder();
		if (userRoleCodeList != null && !userRoleCodeList.isEmpty()) {
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

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if (!doFieldValidation()) {
			return;
		}
		VasCustomer vasCustomer = new VasCustomer();
		if (this.customerRow.isVisible()) {
			if (!isCustomerExists()) {
				MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
				return;
			}
			vasRecording.setPrimaryLinkRef(this.custCIF.getValue());
			
		} else if (this.loanRow.isVisible()) {
			vasRecording.setPrimaryLinkRef(this.loanType.getValue());
		} else if (this.collateralRow.isVisible()) {
			vasRecording.setPrimaryLinkRef(this.collteralType.getValue());
		}
		
		// Setting Workflow Details
		if (getFinanceWorkFlow() == null && !isFinanceProcess) {
			FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
					this.productType.getValue(), FinanceConstants.FINSER_EVENT_ORG,
					PennantConstants.WORFLOW_MODULE_VAS);
			setFinanceWorkFlow(financeWorkFlow);
		}

		// Workflow Details Setup
		WorkFlowDetails workFlowDetails = null;
		if (getFinanceWorkFlow() != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(getFinanceWorkFlow().getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			vasRecording.setWorkflowId(0);
		} else {
			setWorkFlowEnabled(true);

			if (workFlowDetails.getFirstTaskOwner().contains(PennantConstants.DELIMITER_COMMA)) {
				String[] fisttask = workFlowDetails.getFirstTaskOwner().split(PennantConstants.DELIMITER_COMMA);
				for (String string : fisttask) {
					if (getUserWorkspace().isRoleContains(string)) {
						setFirstTask(true);
						break;
					}
				}
			} else {
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			}
			setWorkFlowId(workFlowDetails.getId());
			vasRecording.setWorkflowId(workFlowDetails.getWorkFlowId());
			doLoadWorkFlow(vasRecording.isWorkflow(), vasRecording.getWorkflowId(), vasRecording.getNextTaskId());
		}
		
		if(vasConfiguration == null){
			//Fetching the vasConfiguration details
			vasConfiguration = getVasConfigurationService().getApprovedVASConfigurationByCode(this.productType.getValue());
		}
		
		// Vas Customer Details
		if(!isFinanceProcess){
			vasCustomer = getvASRecordingService().getVasCustomerDetails(vasRecording.getPrimaryLinkRef(), vasConfiguration.getRecAgainst());
			vasRecording.setVasCustomer(vasCustomer);
		}
		
		vasRecording.setVasConfiguration(vasConfiguration);
		vasRecording.setNewRecord(true);
		vasRecording.setProductCode(vasConfiguration.getProductCode());
		vasRecording.setPostingAgainst(vasConfiguration.getRecAgainst());
		vasRecording.setProductDesc(vasConfiguration.getProductDesc());
		vasRecording.setProductType(vasConfiguration.getProductType());
		vasRecording.setProductTypeDesc(vasConfiguration.getProductTypeDesc());
		vasRecording.setProductCtg(vasConfiguration.getProductCategory());
		vasRecording.setProductCtgDesc(vasConfiguration.getProductCategoryDesc());
		vasRecording.setManufacturerDesc(vasConfiguration.getManufacturerName());
		vasRecording.setFee(vasConfiguration.getVasFee());

		// Fetching Finance Reference Detail
		if (getFinanceWorkFlow() != null && !isFinanceProcess) {
			vasRecording = getvASRecordingService().getProcessEditorDetails(vasRecording, getRole(),
					FinanceConstants.FINSER_EVENT_ORG);
		}
		showDetailView();
		logger.debug("Leaving " + event.toString());
	}

	private void showDetailView() {
		logger.debug("Entering");

		HashMap<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("vASRecording", this.vasRecording);
		if(this.vasRecordingListCtrl != null){
			arguments.put("vASRecordingListCtrl", this.vasRecordingListCtrl);
		}
		if(this.finVasRecordingDialogCtrl != null){
			arguments.put("finVasRecordingDialogCtrl", this.finVasRecordingDialogCtrl);
		}
		arguments.put("newRecord", this.newRecord);
		arguments.put("waivedFlag", this.waivedFlag);
		if(userRoleCodeList != null && !userRoleCodeList.isEmpty()){
			arguments.put("roleCode", userRoleCodeList.get(0));
		}
		try { 
			
			if(isFinanceProcess){
				Executions.createComponents("/WEB-INF/pages/VASRecording/VASRecordingDialog.zul",
						this.finVasRecordingDialogCtrl.window_FinVasRecordingDialog, arguments);
			}else{
				Executions.createComponents("/WEB-INF/pages/VASRecording/VASRecordingDialog.zul",
						this.vasRecordingListCtrl.window_VASRecordingList, arguments);
			}
			this.window_SelectVASConfiguration.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for checking /validating fields before proceed.
	 * 
	 * @return
	 */
	private boolean doFieldValidation() {
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.productType.getValue()) == null) {
				throw new WrongValueException(this.productType, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectVASConfiguration_ProductType.value") }));
			}

		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (this.customerRow.isVisible() && StringUtils.trimToNull(this.custCIF.getValue()) == null) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectVASConfiguration_CustCIF.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (this.loanRow.isVisible() && StringUtils.trimToNull(this.loanType.getValue()) == null) {
				throw new WrongValueException(this.loanType, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectVASConfiguration_LoanType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (this.collateralRow.isVisible() && StringUtils.trimToNull(this.collteralType.getValue()) == null) {
				throw new WrongValueException(this.collteralType, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectCollateralTypeDialog_CollateralType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		return true;
	}
	
	
	/**
	 * Call the Customer dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 * @throws Exception
	 */
	public boolean isCustomerExists() throws InterruptedException, InterfaceException {
		logger.debug("Entering");

		boolean isCustExists = true;
		// Get the data of Customer from Core Banking Customer
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

			//If  customer exist is checked 
			Customer customer = null;
			if (StringUtils.isEmpty(cif)) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
			} else {

				//check Customer Data in LOCAL PFF system
				customer = getCustomerDetailsService().getCheckCustomerByCIF(cif);
			}

			if (customer == null) {
				isCustExists = false;
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return isCustExists;
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
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");

		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
			this.custName.setValue(customer.getCustShrtName());
			VasCustomer vasCustomer = new VasCustomer();
			vasCustomer.setCustomerId(customer.getCustID());
			vasCustomer.setCustCIF(customer.getCustCIF());
			vasCustomer.setCustShrtName(customer.getCustShrtName());
			this.vasRecording.setVasCustomer(vasCustomer);
		} else {
			this.custCIF.setValue("");
			this.custName.setValue("");
		}

		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "productType" button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onFulfill$productType(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.productType.setConstraint("");
		this.productType.clearErrorMessage();
		Clients.clearWrongValue(productType);
		Object dataObject = this.productType.getObject();
		if (dataObject instanceof String) {
			this.productType.setValue(dataObject.toString());
			this.productType.setDescription("");
			showProductTypeRow("");
		} else {

			if(dataObject instanceof FinanceWorkFlow){
				FinanceWorkFlow details = (FinanceWorkFlow) dataObject;
				/* Set FinanceWorkFloe object */
				setFinanceWorkFlow(details);
				this.productType.setValue(details.getFinType());
				this.productType.setDescription(details.getVasProductDesc());
			}else if(dataObject instanceof VASConfiguration){
				VASConfiguration details = (VASConfiguration) dataObject;
				this.productType.setValue(details.getProductCode());
				this.productType.setDescription(details.getProductDesc());
			}
		}
		if (StringUtils.trimToNull(this.productType.getValue()) != null && !isFinanceProcess) {
			//Fetching the vasConfiguration details
			vasConfiguration = getVasConfigurationService().getApprovedVASConfigurationByCode(this.productType.getValue());
			if(vasConfiguration == null){
				this.productType.setValue("","");
				this.productType.setObject(null);
				MessageUtil.showError(Labels.getLabel("label_SelectVASConfiguration_Product_NotExists"));
			}else{
				showProductTypeRow(vasConfiguration.getRecAgainst());
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/*
	 * display the customer,Loan or commitemet deatils based on produtype selection
	 */
	private void showProductTypeRow(String recAgainst) {
		logger.debug("Entering ");

		if (VASConsatnts.VASAGAINST_CUSTOMER.equals(recAgainst)) {
			this.customerRow.setVisible(true);
			this.collateralRow.setVisible(false);
			this.loanRow.setVisible(false);
		} else if (VASConsatnts.VASAGAINST_FINANCE.equals(recAgainst)) {
			this.customerRow.setVisible(false);
			this.collateralRow.setVisible(false);
			this.loanRow.setVisible(true);
		} else if (VASConsatnts.VASAGAINST_COLLATERAL.equals(recAgainst)) {
			this.customerRow.setVisible(false);
			this.collateralRow.setVisible(true);
			this.loanRow.setVisible(false);
		} else {
			this.customerRow.setVisible(false);
			this.collateralRow.setVisible(false);
			this.loanRow.setVisible(false);
		}

		logger.debug("Leaving ");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		this.productType.setConstraint("");
		this.custCIF.setConstraint("");
		this.collteralType.setConstraint("");
		this.loanType.setConstraint("");
		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.productType.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.collteralType.setErrorMessage("");
		this.loanType.setErrorMessage("");
		logger.debug("Leaving");
	}

	public FinanceWorkFlow getFinanceWorkFlow() {
		return financeWorkFlow;
	}

	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public VASRecording getvASRecording() {
		return vasRecording;
	}

	public void setvASRecording(VASRecording vASRecording) {
		this.vasRecording = vASRecording;
	}

	public VASConfiguration getvASConfiguration() {
		return vasConfiguration;
	}

	public void setvASConfiguration(VASConfiguration vASConfiguration) {
		this.vasConfiguration = vASConfiguration;
	}

	public VASRecordingListCtrl getvASRecordingListCtrl() {
		return vasRecordingListCtrl;
	}

	public void setvASRecordingListCtrl(VASRecordingListCtrl vASRecordingListCtrl) {
		this.vasRecordingListCtrl = vASRecordingListCtrl;
	}

	public VASRecordingService getvASRecordingService() {
		return vASRecordingService;
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public VASConfigurationService getVasConfigurationService() {
		return vasConfigurationService;
	}

	public void setVasConfigurationService(VASConfigurationService vasConfigurationService) {
		this.vasConfigurationService = vasConfigurationService;
	}
	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public FinVasRecordingDialogCtrl getFinVasRecordingDialogCtrl() {
		return finVasRecordingDialogCtrl;
	}

	public void setFinVasRecordingDialogCtrl(FinVasRecordingDialogCtrl finVasRecordingDialogCtrl) {
		this.finVasRecordingDialogCtrl = finVasRecordingDialogCtrl;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinTypeVASProducts getFinTypeVASProducts() {
		return finTypeVASProducts;
	}

	public void setFinTypeVASProducts(FinTypeVASProducts finTypeVASProducts) {
		this.finTypeVASProducts = finTypeVASProducts;
	}
}
