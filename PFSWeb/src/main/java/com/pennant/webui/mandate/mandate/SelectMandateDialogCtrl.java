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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.financemanagement.paymentMode.SelectReceiptPaymentDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class SelectMandateDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectReceiptPaymentDialogCtrl.class);

	protected Window window_SelectMandateDialog;
	protected Textbox custCIF;
	protected Combobox mandateTypes;
	protected ExtendedCombobox entityCode;

	protected Button btnProceed;
	protected Row row_MandateTypes;
	protected Label label_title;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected Label label_SelectMandate_CustomerName;

	Tab parenttab = null;

	private Object financeMainDialogCtrl = null;

	FinanceDetail financeDetail;
	protected Groupbox finBasicdetails;

	private Mandate mandate;
	private transient MandateListCtrl mandateListCtrl;

	private transient SelectMandateDialogCtrl selectMandateDialogCtrl;
	private CustomerDataService customerDataService;
	protected long custId = Long.MIN_VALUE;
	private CustomerDetailsService customerDetailsService;
	private transient CustomerService customerService;
	private transient MandateService mandateService;

	private final List<ValueLabel> mandateTypeList = MandateUtil.getInstrumentTypes();

	private Customer customer;
	Date appDate = SysParamUtil.getAppDate();

	public void onCreate$window_SelectMandateDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_SelectMandateDialog);

		if (arguments.containsKey("mandate")) {
			this.mandate = (Mandate) arguments.get("mandate");
			Mandate befImage = new Mandate();
			BeanUtils.copyProperties(this.mandate, befImage);
			this.mandate.setBefImage(befImage);
			setMandate(this.mandate);
		} else {
			setMandate(null);
		}

		if (arguments.containsKey("mandateListCtrl")) {
			setMandateListCtrl((MandateListCtrl) arguments.get("mandateListCtrl"));
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

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);

		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;
		customer = (Customer) nCustomer;
		addFilter(customer);

		logger.debug(Literal.LEAVING);
	}

	private void addFilter(Customer customer) {
		logger.debug(Literal.ENTERING);

		if (customer != null && customer.getCustID() != 0) {
			// this.custId = customer.getCustID();
			this.custCIF.setValue(customer.getCustCIF());
			this.label_SelectMandate_CustomerName.setValue(customer.getCustShrtName());
		} else {
			this.custCIF.setValue("");
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$custCIF(Event event) {
		customer = fetchCustomerDataByCIF();
		addFilter(customer);
	}

	public Customer fetchCustomerDataByCIF() {

		customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

		if (this.custCIF.getValue().trim().isEmpty()) {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
			this.custId = 0;
			label_SelectMandate_CustomerName.setValue("");
		} else {
			customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.MAIN_TAB.getSuffix());
			if (customer != null) {
				label_SelectMandate_CustomerName.setValue(customer.getCustShrtName());
				this.custId = customer.getCustID();
			} else {
				MessageUtil.showError("Invalid Customer Please Select valid Customer");
			}
		}

		return customer;
	}

	private void doSetFieldProperties() {
		fillComboBox(this.mandateTypes, "", mandateTypeList, "");

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
		logger.debug(Literal.ENTERING);
		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug(Literal.LEAVING);
	}

	public CustomerDetails fetchCustomerData() {
		logger.debug(Literal.ENTERING);

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
			logger.error("Exception: ", e);
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

			List<FinanceMain> validFinreferences = mandateService.getValidFinreferences(this.mandate.getCustID(),
					this.mandate.getMandateType());
			this.mandate.setValidFinreferences(validFinreferences);
			arg.put("mandate", this.mandate);
			arg.put("mandatedata", this);
			arg.put("enqModule", enqiryModule);
			arg.put("fromLoan", false);
			arg.put("validReferences", this.mandate.getValidFinreferences());
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

	public Mandate getMandate() {
		return mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public MandateListCtrl getMandateListCtrl() {
		return mandateListCtrl;
	}

	public void setMandateListCtrl(MandateListCtrl mandateListCtrl) {
		this.mandateListCtrl = mandateListCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public SelectMandateDialogCtrl getSelectMandateDialogCtrl() {
		return selectMandateDialogCtrl;
	}

	public void setSelectMandateDialogCtrl(SelectMandateDialogCtrl selectMandateDialogCtrl) {
		this.selectMandateDialogCtrl = selectMandateDialogCtrl;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

}
