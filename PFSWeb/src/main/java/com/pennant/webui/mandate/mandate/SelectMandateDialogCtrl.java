package com.pennant.webui.mandate.mandate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.financemanagement.paymentMode.SelectReceiptPaymentDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SelectMandateDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectReceiptPaymentDialogCtrl.class);

	protected Window window_SelectMandateDialog;
	protected Textbox custCIF;
	protected Combobox mandateTypes;
	protected ExtendedCombobox entityCode;

	protected Button btnProceed;
	protected Label customerNameLabel;

	private Mandate mandate;
	private transient MandateListCtrl mandateListCtrl;

	private transient CustomerDataService customerDataService;
	private transient CustomerDetailsService customerDetailsService;
	private transient MandateService mandateService;

	private final List<ValueLabel> mandateTypeList = MandateUtil.getInstrumentTypes();

	Date appDate = SysParamUtil.getAppDate();

	public void onCreate$window_SelectMandateDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_SelectMandateDialog);

		if (arguments.containsKey("mandate")) {
			this.mandate = (Mandate) arguments.get("mandate");
			Mandate befImage = new Mandate();
			BeanUtils.copyProperties(this.mandate, befImage);
			this.mandate.setBefImage(befImage);
		}

		if (arguments.containsKey("mandateListCtrl")) {
			this.mandateListCtrl = ((MandateListCtrl) arguments.get("mandateListCtrl"));
		}

		try {
			doSetFieldProperties();
			this.window_SelectMandateDialog.doModal();
		} catch (Exception e) {
			closeDialog();
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

		if (customer == null || customer.getCustID() == 0) {
			this.custCIF.setValue("");
			return;
		}

		this.custCIF.setValue(customer.getCustCIF());
		this.customerNameLabel.setValue(customer.getCustShrtName());

	}

	public void onChange$custCIF(Event event) {
		Customer customer = fetchCustomerDataByCIF();
		setCustomerData(customer);
	}

	public Customer fetchCustomerDataByCIF() {
		Customer customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();

		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

		if (this.custCIF.getValue().trim().isEmpty()) {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
			customerNameLabel.setValue("");

			return null;
		}

		customer = this.customerDetailsService.getCustomer(cif);

		if (customer != null) {
			customerNameLabel.setValue(customer.getCustShrtName());
		} else {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
		}

		return customer;
	}

	private void doSetFieldProperties() {
		fillComboBox(this.mandateTypes, "", mandateTypeList, InstrumentType.PDC.name());

		this.entityCode.setMaxlength(8);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);

		this.entityCode.setFilters(filter);
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";

	}

	public void onClick$btnSearchCustCIF(Event event) {
		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
	}

	public CustomerDetails fetchCustomerData() {
		CustomerDetails customerDetails = null;
		try {
			this.custCIF.setConstraint("");
			this.custCIF.setErrorMessage("");
			this.custCIF.clearErrorMessage();
			String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

			Customer customer = null;
			if (StringUtils.isEmpty(cif)) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerDialog_CoreCustID.value") }));
			} else {
				customer = customerDataService.getCheckCustomerByCIF(cif);
			}

			if (customer != null) {
				customerDetails = customerDataService.getCustomerDetailsbyID(customer.getId(), true, "_AView");
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);
		if (!doSetValidation()) {
			return;
		}
		if (validCustomerDetails()) {
			return;
		}
		doShowDialog();
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);
		try {
			Map<String, Object> arg = getDefaultArguments();

			String mandateType = this.mandate.getMandateType();

			List<FinanceMain> customerLoans = mandateService.getLoans(this.mandate.getCustID(), mandateType);

			Mandate employerDatails = mandateService.getEmployerDetails(this.mandate.getCustID());

			if (employerDatails != null) {
				this.mandate.setEmployeeID(employerDatails.getEmployeeID());
				this.mandate.setEmployerName(employerDatails.getEmployerName());
			} else {
				MessageUtil.showError("Employer details are not available for this Customer");
				return;
			}

			if (customerLoans.isEmpty()) {
				MessageUtil.showError("There are no active loans for the selected Customer and Instrument Type.");
				return;
			}

			arg.put("mandate", this.mandate);
			arg.put("mandatedata", this);
			arg.put("enqModule", enqiryModule);
			arg.put("fromLoan", false);
			arg.put("customerLoans", customerLoans);
			arg.put("mandateListCtrl", this.mandateListCtrl);

			if (MandateStatus.isAwaitingConf(mandate.getStatus())) {
				arg.put("enqModule", true);
			}

			if (MandateStatus.isApproved(mandate.getStatus()) || MandateStatus.isHold(mandate.getStatus())) {
				arg.put("maintain", true);
			}

			String page = "/WEB-INF/pages/Mandate/MandateDialog.zul";

			Executions.createComponents(page, null, arg);
			this.window_SelectMandateDialog.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	protected Map<String, Object> getDefaultArguments() {
		Map<String, Object> aruments = new HashMap<>();

		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		return aruments;
	}

	private boolean validCustomerDetails() {
		CustomerDetails customerDetails = fetchCustomerData();

		if (customerDetails == null) {
			MessageUtil.showError(Labels.getLabel("Cust_NotFound"));
			return true;
		}

		mandate.setNewRecord(true);
		mandate.setMandateType(this.mandateTypes.getValue());
		mandate.setMandateType(getComboboxValue(this.mandateTypes));
		mandate.setCustCIF(customerDetails.getCustomer().getCustCIF());
		mandate.setCustShrtName(customerDetails.getCustomer().getCustShrtName());
		mandate.setCustID(customerDetails.getCustomer().getCustID());

		mandate.setEntityCode(this.entityCode.getValue());
		mandate.setEntityDesc(this.entityCode.getDescription());
		return false;
	}

	private boolean doSetValidation() {
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.isEmpty(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectCollateralTypeDialog_CustCIF.value") }));
			}

			if (!this.mandateTypes.isDisabled()) {
				this.mandateTypes.setConstraint(new StaticListValidator(mandateTypeList,
						Labels.getLabel("label_MandateDialog_MandateType.value")));
			}

			if (!this.entityCode.isReadonly()) {
				this.entityCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_MandateDialog_EntityCode.value"), null, true, true));
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

	private void doRemoveValidation() {
		this.custCIF.setConstraint("");
		this.mandateTypes.setConstraint("");
	}

	public void onClick$btnClose(Event event) throws InterruptedException, ParseException {
		this.window_SelectMandateDialog.onClose();
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

}
