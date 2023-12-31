/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SelectCollateralTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2016 *
 * * Modified Date : 14-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.collateral.collateralsetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class SelectCollateralTypeDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(SelectCollateralTypeDialogCtrl.class);

	protected Window window_SelectCollateralDialog;
	protected ExtendedCombobox collateralType;
	protected Button btnProceed;
	protected Button btnSearchCustCIF;
	protected Textbox custCIF;
	protected Label custName;

	private CollateralSetup collateralSetup;
	private CollateralSetupListCtrl collateralSetupListCtrl;
	private FinanceWorkFlow financeWorkFlow;
	private FinanceWorkFlowService financeWorkFlowService;
	private CollateralSetupService collateralSetupService;
	private CustomerDataService customerDataService;
	private CollateralStructureService collateralStructureService;
	private FinanceTypeService financeTypeService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private List<String> userRoleCodeList = new ArrayList<String>();

	private Object dialogCtrl = null;
	private Window window = null;
	private FinanceDetail financeDetail = null;
	private boolean fromLoan = false;

	public SelectCollateralTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectCollateralDialog(Event event) {
		logger.debug("Entering");

		try {
			// Get the required arguments.
			this.collateralSetup = (CollateralSetup) arguments.get("collateralSetup");
			this.collateralSetupListCtrl = (CollateralSetupListCtrl) arguments.get("collateralSetupListCtrl");
			this.userRoleCodeList = (ArrayList<String>) arguments.get("role");

			if (arguments.containsKey("fromLoan")) {
				this.fromLoan = (boolean) arguments.get("fromLoan");
			}
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}
			if (arguments.containsKey("dialogCtrl")) {
				this.dialogCtrl = (Object) arguments.get("dialogCtrl");
			}
			if (arguments.containsKey("window")) {
				this.window = (Window) arguments.get("window");
			}
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}
			if (this.collateralSetup == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		// Set the page level components.
		setPageComponents(window_SelectCollateralDialog);
		showSelectCollateralTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectCollateralTypeDialog() {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectCollateralDialog.doModal();
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
		// collteralType
		this.collateralType.setMaxlength(8);
		this.collateralType.setMandatoryStyle(true);
		this.collateralType.setModuleName("CollateralWorkFlow");
		this.collateralType.setValueColumn("TypeCode");
		this.collateralType.setDescColumn("CollateralDesc");
		this.collateralType.setValidateColumns(new String[] { "FinType" });

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("FinEvent", FinServiceEvent.ORG, Filter.OP_EQUAL);
		this.collateralType.setFilters(filters);

		if (fromLoan) {
			String finType = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType();
			if (StringUtils.isNotEmpty(finType)) {
				StringBuilder whereClause = new StringBuilder();
				String collateralTypes = getFinanceTypeService().getAllowedCollateralTypes(finType);
				if (StringUtils.isNotEmpty(collateralTypes)) {
					String[] collTypes = collateralTypes.split(",");
					whereClause.append("(TypeCode in (");
					for (int i = 0; i < collTypes.length; i++) {
						if (i == 0) {
							whereClause.append("'").append(collTypes[i]).append("'");
						} else {
							whereClause.append(",'").append(collTypes[i]).append("'");
						}
					}
					whereClause.append("))");
				}
				this.collateralType.setWhereClause(whereClause.toString());
			}
		} else {
			String whereClause = getWhereClauseWithFirstTask();
			if (StringUtils.isNotEmpty(whereClause)) {
				this.collateralType.setWhereClause(whereClause);
			}
		}

		if (fromLoan) {
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			this.custCIF.setValue(customer.getCustCIF());
			this.custName.setValue(customer.getCustShrtName());
			this.custCIF.setReadonly(true);
			this.btnSearchCustCIF.setVisible(false);
		}
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
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());

		if (!doFieldValidation()) {
			return;
		}

		if (fromLoan) {
			setLoanCollateralSetupDetails();
		} else {
			if (setCollateralSetupDetails()) {
				return;
			}
		}
		showDetailView();
		logger.debug("Leaving " + event.toString());
	}

	private void setLoanCollateralSetupDetails() {
		// Customer Data Fetching

		CustomerDetails customerDetails = getFinanceDetail().getCustomerDetails();
		if (customerDetails == null) {
			MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
			return;
		}

		// Workflow Details Setup
		WorkFlowDetails workFlowDetails = null;
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			collateralSetup.setWorkflowId(0);
		}
		// Role need to send
		collateralSetup.setNewRecord(true);

		collateralSetup.setCollateralType(this.collateralType.getValue());
		collateralSetup.setCollateralTypeName(this.collateralType.getDescription());
		collateralSetup.setCollateralCcy(SysParamUtil.getAppCurrency());
		collateralSetup.setCustomerDetails(customerDetails);
		collateralSetup.setDepositorCif(customerDetails.getCustomer().getCustCIF());
		collateralSetup.setDepositorId(customerDetails.getCustomer().getCustID());
		collateralSetup.setDepositorName(customerDetails.getCustomer().getCustShrtName());

		// Fetching the collateral Structure details
		collateralSetup.setCollateralStructure(getCollateralStructureService()
				.getApprovedCollateralStructureByType(collateralSetup.getCollateralType()));
		// Fetching Finance Reference Detail
		collateralSetup = getCollateralSetupService().getProcessEditorDetails(collateralSetup, getRole(),
				FinServiceEvent.ORG);
	}

	private boolean setCollateralSetupDetails() {
		// Customer Data Fetching
		CustomerDetails customerDetails = fetchCustomerData();
		if (customerDetails == null) {
			MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
			return true;
		}

		// Setting Workflow Details
		if (getFinanceWorkFlow() == null) {
			FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
					this.collateralType.getValue(), FinServiceEvent.ORG, CollateralConstants.MODULE_NAME);
			setFinanceWorkFlow(financeWorkFlow);
		}

		// Workflow Details Setup
		WorkFlowDetails workFlowDetails = null;
		if (getFinanceWorkFlow() != null) {
			workFlowDetails = WorkFlowUtil.getDetailsByType(getFinanceWorkFlow().getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			collateralSetup.setWorkflowId(0);
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
			collateralSetup.setWorkflowId(workFlowDetails.getWorkFlowId());
			doLoadWorkFlow(collateralSetup.isWorkflow(), collateralSetup.getWorkflowId(),
					collateralSetup.getNextTaskId());
		}

		collateralSetup.setNewRecord(true);

		collateralSetup.setCollateralType(this.collateralType.getValue());
		collateralSetup.setCollateralTypeName(this.collateralType.getDescription());
		collateralSetup.setCollateralCcy(SysParamUtil.getAppCurrency());
		collateralSetup.setCustomerDetails(customerDetails);
		collateralSetup.setDepositorCif(customerDetails.getCustomer().getCustCIF());
		collateralSetup.setDepositorId(customerDetails.getCustomer().getCustID());
		collateralSetup.setDepositorName(customerDetails.getCustomer().getCustShrtName());

		// Fetching the collateral Structure details
		collateralSetup.setCollateralStructure(getCollateralStructureService()
				.getApprovedCollateralStructureByType(collateralSetup.getCollateralType()));

		// Fetching Finance Reference Detail
		if (getFinanceWorkFlow() != null) {
			collateralSetup = getCollateralSetupService().getProcessEditorDetails(collateralSetup, getRole(),
					FinServiceEvent.ORG);
		}
		return false;
	}

	private void showDetailView() {
		logger.debug("Entering");

		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("collateralSetup", this.collateralSetup);
		arguments.put("collateralSetupListCtrl", this.collateralSetupListCtrl);
		arguments.put("dialogCtrl", this.dialogCtrl);
		arguments.put("fromLoan", this.fromLoan);
		arguments.put("roleCode", getRole());
		if (fromLoan) {
			arguments.put("financeDetail", getFinanceDetail());
			arguments.put("newRecord", true);
		}
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", window,
					arguments);
			this.window_SelectCollateralDialog.onClose();
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
			if (StringUtils.trimToNull(this.collateralType.getValue()) == null) {
				throw new WrongValueException(this.collateralType, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectCollateralTypeDialog_CollateralType.value") }));
			}

		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (StringUtils.isEmpty(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectCollateralTypeDialog_CustCIF.value") }));
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
	 */
	public CustomerDetails fetchCustomerData() {
		logger.debug("Entering");

		// Get the data of Customer from Core Banking Customer
		CustomerDetails customerDetails = null;
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

			// If customer exist is checked
			Customer customer = null;
			if (StringUtils.isEmpty(cif)) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
			} else {

				// check Customer Data in LOCAL PFF system
				customer = customerDataService.getCheckCustomerByCIF(cif);
			}

			if (customer != null) {
				customerDetails = customerDataService.getCustomerDetailsbyID(customer.getId(), true, "_AView");
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return customerDetails;
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
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
			this.custName.setValue(customer.getCustShrtName());
		} else {
			this.custCIF.setValue("");
			this.custName.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "SearchFinType" button
	 * 
	 * @param event
	 */

	public void onFulfill$collateralType(Event event) {
		logger.debug("Entering " + event.toString());

		this.collateralType.setConstraint("");
		this.collateralType.clearErrorMessage();
		Clients.clearWrongValue(collateralType);
		Object dataObject = this.collateralType.getObject();
		if (dataObject instanceof String) {
			this.collateralType.setValue(dataObject.toString());
			this.collateralType.setDescription("");
		} else {
			FinanceWorkFlow details = (FinanceWorkFlow) dataObject;
			/* Set FinanceWorkFloe object */
			setFinanceWorkFlow(details);
			if (details != null) {
				this.collateralType.setValue(details.getFinType());
				this.collateralType.setDescription(details.getCollateralDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		this.collateralType.setConstraint("");
		this.custCIF.setConstraint("");
		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.collateralType.setErrorMessage("");
		this.custCIF.setErrorMessage("");
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

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public CollateralStructureService getCollateralStructureService() {
		return collateralStructureService;
	}

	public void setCollateralStructureService(CollateralStructureService collateralStructureService) {
		this.collateralStructureService = collateralStructureService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}
