package com.pennant.webui.finance.financemain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.validation.FinanceCancelValidator;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.pff.fincancelupload.exception.FinCancelUploadError;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.web.util.ComponentUtil;

public class SelectFinanceCancellationDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = -8233696024677792766L;
	private static final Logger logger = LogManager.getLogger(SelectFinanceCancellationDialogCtrl.class);

	protected Window windowSelectFinanceCancellationDialog;
	protected ExtendedCombobox finReference;
	protected Textbox custCIF;
	protected Button btnProceed;
	protected Label customerNameLabel;
	private String moduleDefiner;
	private String workflowCode;
	private CustomerDAO customerDAO;

	private FinanceSelectCtrl financeSelectCtrl;

	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient FinanceCancellationService financeCancellationService;
	private transient FinanceCancelValidator financeCancelValidator;

	public SelectFinanceCancellationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$windowSelectFinanceCancellationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(this.windowSelectFinanceCancellationDialog);

		try {
			if (arguments.containsKey("moduleDefiner")) {
				this.moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("workflowCode")) {
				this.workflowCode = (String) arguments.get("workflowCode");
			}

			if (arguments.containsKey("financeSelectCtrl")) {
				this.financeSelectCtrl = (FinanceSelectCtrl) arguments.get("financeSelectCtrl");
			}

			doSetFieldProperties();
			showSelectFinanceCancellationDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void showSelectFinanceCancellationDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		try {
			this.windowSelectFinanceCancellationDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug(Literal.ENTERING);

		this.custCIF.clearErrorMessage();
		Customer customer = (Customer) nCustomer;

		setCustomerData(customer);

		logger.debug(Literal.LEAVING);
	}

	private void setCustomerData(Customer customer) {
		addFilter(customer);

		if (customer == null || customer.getCustID() == 0) {
			this.custCIF.setValue("");
			return;
		}

		this.custCIF.setValue(customer.getCustCIF());
		this.customerNameLabel.setValue(customer.getCustShrtName());
	}

	public void onChange$custCIF(Event event) {
		setCustomerData(fetchCustomerDataByCIF(this.custCIF.getValue()));
	}

	private void doSetFieldProperties() {
		this.finReference.setButtonDisabled(false);
		this.finReference.setTextBoxWidth(155);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMaintenance");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		Filter filters[] = new Filter[3];
		int backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_CANFIN_BACK_DATE");
		Date backValueDate = DateUtil.addDays(SysParamUtil.getAppDate(), backValueDays);
		String backValDate = DateUtil.formatToFullDate(backValueDate);

		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
		filters[2] = new Filter("FinstartDate", backValDate, Filter.OP_GREATER_OR_EQUAL);
		this.finReference.setFilters(filters);
	}

	public void onClick$btnSearchCustCIF(Event event) {
		doClearMessage();
		final Map<String, Object> map = new HashMap<>();
		map.put("DialogCtrl", this);

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING);

		validateFinReference(event, false);

		Clients.clearWrongValue(this.finReference);
		Object dataObject = this.finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			FinanceMain finMain = (FinanceMain) dataObject;
			if (finMain != null) {
				this.finReference.setValue(finMain.getFinReference());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		if (!doFieldValidation()) {
			logger.debug(Literal.LEAVING.concat(event.toString()));
			return;
		}

		Object dataObject = this.finReference.getObject();

		FinanceMain finMain = null;
		if (dataObject instanceof FinanceMain) {
			finMain = (FinanceMain) dataObject;
		}

		if (finMain == null) {
			return;
		}

		String finType = finMain.getFinType();
		String product = finMain.getLovDescFinProduct();
		WorkFlowDetails workFlowDetails = getWorkFlow(finType, StringUtils.isNotEmpty(product));

		if (workFlowDetails == null) {
			MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			return;
		}

		setWorkFlowEnabled(true);
		setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
		setWorkFlowId(workFlowDetails.getId());

		long finID = ComponentUtil.getFinID(this.finReference);

		String userRole = finMain.getNextRoleCode();
		if (StringUtils.isEmpty(userRole)) {
			userRole = workFlowDetails.getFirstTaskOwner();
		}

		final FinanceDetail fd = financeCancellationService.getFinanceDetailById(finID, "_View", userRole,
				moduleDefiner);
		fd.setModuleDefiner(moduleDefiner);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		if (fm.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			fm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		fd.getFinScheduleData().setFinanceMain(fm);

		String nextroleCode = fm.getNextRoleCode();
		if (StringUtils.isNotBlank(nextroleCode) && !StringUtils.equals(userRole, nextroleCode)) {
			String[] errParm = new String[1];
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + fm.getFinReference();
			MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("41005", errParm)).getError());
			logger.debug(Literal.LEAVING + event.toString());
			return;
		}

		String maintainSts = StringUtils.trimToEmpty(fm.getRcdMaintainSts());
		if (StringUtils.isNotEmpty(maintainSts) && !maintainSts.equals(moduleDefiner)) {
			String[] errParm = new String[1];
			errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + fm.getFinReference();
			MessageUtil.showError(ErrorUtil.getErrorDetail(new ErrorDetail("41005", errParm)).getError());
			return;
		}

		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		schdData.getFinanceMain().setAppDate(SysParamUtil.getAppDate());
		FinCancelUploadError error = financeCancelValidator.validLoan(schdData.getFinanceMain(), schedules);

		if (error != null) {
			MessageUtil.showError(error.description());
			return;
		}

		try {
			this.windowSelectFinanceCancellationDialog.onClose();
			financeSelectCtrl.showCancellationDetailView(fd);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		this.windowSelectFinanceCancellationDialog.onClose();
	}

	private WorkFlowDetails getWorkFlow(String finType, boolean isPromotion) {
		if (StringUtils.isNotEmpty(workflowCode)) {
			String workflowTye = financeWorkFlowService.getFinanceWorkFlowType(finType, workflowCode,
					isPromotion ? PennantConstants.WORFLOW_MODULE_PROMOTION : PennantConstants.WORFLOW_MODULE_FINANCE);
			if (workflowTye != null) {
				return WorkFlowUtil.getDetailsByType(workflowTye);
			}
		}

		return null;
	}

	private void addFilter(Customer customer) {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue("");
		this.finReference.setObject("");
		this.custCIF.setValue("");
		int backValueDays = SysParamUtil.getValueAsInt("MAINTAIN_CANFIN_BACK_DATE");
		Date backValueDate = DateUtil.addDays(SysParamUtil.getAppDate(), backValueDays);
		String backValDate = DateUtil.formatToFullDate(backValueDate);

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
		filters[2] = new Filter("FinstartDate", backValDate, Filter.OP_GREATER_OR_EQUAL);
		this.finReference.setFilters(filters);

		if (customer != null && customer.getCustID() != 0) {
			this.custCIF.setValue(customer.getCustCIF());

			filters = new Filter[4];
			filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL);
			filters[2] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
			filters[3] = new Filter("FinstartDate", backValDate, Filter.OP_GREATER_OR_EQUAL);

			this.finReference.setFilters(filters);
		}

		logger.debug(Literal.LEAVING);
	}

	public Customer fetchCustomerDataByCIF(String custCIF) {
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();

		String cif = StringUtils.trimToEmpty(custCIF);

		if (this.custCIF.getValue().trim().isEmpty()) {
			customerNameLabel.setValue("");
			return null;
		}

		Customer customer = this.customerDAO.getCustomer(cif);
		if (customer != null) {
			customerNameLabel.setValue(customer.getCustShrtName());
		} else {
			customerNameLabel.setValue("");
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
		}

		return customer;
	}

	private boolean doFieldValidation() {
		logger.debug(Literal.ENTERING);

		this.finReference.setErrorMessage("");

		List<WrongValueException> wve = new ArrayList<>();
		try {
			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectFinanceCancellation_Finreference.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		this.finReference.setConstraint("");

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	private void validateFinReference(Event event, boolean b) {
		logger.debug(Literal.ENTERING + event.toString());

		this.finReference.setConstraint("");
		this.finReference.clearErrorMessage();

		Clients.clearWrongValue(finReference);

		Object dataObject = this.finReference.getObject();

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_NOT_EQUAL);
		filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
		this.finReference.setFilters(filters);

		if (!StringUtils.isEmpty(this.custCIF.getValue())) {
			long custID = this.customerDAO.getCustIDByCIF(this.custCIF.getValue());

			filters = new Filter[3];
			filters[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_NOT_EQUAL);
			filters[2] = new Filter("CustId", custID, Filter.OP_EQUAL);
			filters[1] = new Filter("AllowCancelFin", 1, Filter.OP_EQUAL);
			this.finReference.setFilters(filters);
		}

		if (dataObject == null || dataObject instanceof String) {
			this.finReference.setValue("");
			this.finReference.setDescription("");
			this.custCIF.setValue("");
			this.customerNameLabel.setValue("");
			this.finReference.setFilters(null);
		} else {
			FinanceMain fm = (FinanceMain) dataObject;
			if (fm != null) {
				this.finReference.setValue(fm.getFinReference());
				this.finReference.setDescription(fm.getFinType());
				this.custCIF.setValue(String.valueOf(fm.getCustCIF()));

				Customer cust = this.customerDAO.getCustomer(fm.getCustCIF());
				if (cust.getCustCoreBank() != null) {
					this.customerNameLabel.setValue(cust.getCustShrtName());
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	@Autowired
	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	@Autowired
	public void setFinanceCancelValidator(FinanceCancelValidator financeCancelValidator) {
		this.financeCancelValidator = financeCancelValidator;
	}

}