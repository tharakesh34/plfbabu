package com.pennant.pff.excess.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennant.pff.excess.service.ExcessTransferService;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class SelectExcessTransferCtrl extends GFCBaseCtrl<Object> {

	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectExcessTransferCtrl.class);

	protected Window window_SelectExcessTransfer;
	protected Textbox custCIF;
	protected ExtendedCombobox finReference;
	protected Button btnProceed;
	protected Button btnSearchCustCIF;
	protected Button btnSearchFinRef;

	private transient CustomerDetailsService customerDetailsService;
	private transient CustomerService customerService;
	private transient ExcessTransferService excessTransferService;
	protected long custId = Long.MIN_VALUE;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected JdbcSearchObject<FinanceMain> searchObject;
	private Customer customer;
	private FinanceMain financeMain;
	private FinExcessTransfer finExcessTransfer;
	private ExcessTransferListCtrl excessTransferListCtrl;
	private FinanceMainService financeMainService;

	/**
	 * default constructor.<br>
	 */
	public SelectExcessTransferCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinExcessTransfer";
		super.pageRightName = "ExcessTransferMaker";
	}

	public void onCreate$window_SelectExcessTransfer(Event event) {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_SelectExcessTransfer);

		this.finExcessTransfer = (FinExcessTransfer) arguments.get("finExcessTransfer");
		this.excessTransferListCtrl = (ExcessTransferListCtrl) arguments.get("excessTransferListCtrl");

		doSetFieldProperties();

		this.window_SelectExcessTransfer.doModal();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 */
	private void doSetFieldProperties() {
		this.finReference.setButtonDisabled(false);
		this.finReference.setTextBoxWidth(155);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("ReceiptFinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setFilters((new Filter[] { new Filter("FinIsActive", 1, Filter.OP_EQUAL),
				new Filter("MaturityDate", SysParamUtil.getAppDate(), Filter.OP_GREATER_THAN) }));

	}

	private void addFilter(Customer customer) {
		logger.debug("Entering ");

		if (customer != null && customer.getCustID() != 0) {
			this.custId = customer.getCustID();
			this.custCIF.setValue(customer.getCustCIF());
			this.finReference.setValue("");
			this.finReference.setObject("");
			this.finReference.setFilters(new Filter[] { new Filter("CustId", this.custId, Filter.OP_EQUAL) });
		} else {
			this.finReference.setValue("");
			this.finReference.setObject("");
			this.custCIF.setValue("");
			this.finReference.setFilters(null);
		}

		logger.debug("Leaving ");
	}

	/**
	 * When user changes textbox "custCIF"
	 * 
	 * @param event
	 */
	public void onChange$custCIF(Event event) {
		customer = fetchCustomerDataByCIF();
		addFilter(customer);
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());

		validateReference();
		doSetValidation();
		doWriteComponentsToBean();

		doShowDialog();
		this.window_SelectExcessTransfer.onClose();

		logger.debug("Leaving " + event.toString());
	}

	private void validateReference() {
		logger.debug(Literal.ENTERING);

		try {
			boolean isDataExists = excessTransferService.isReferenceExist(this.finReference.getValue());

			if (isDataExists) {
				throw new AppException("");
			}
		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("label_ExcessTransfer_Validation"));
			this.finReference.setValue("");
			logger.debug(Literal.LEAVING);
			return;
		}

		logger.debug(Literal.LEAVING);
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
		} else {
			customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.MAIN_TAB.getSuffix());
			if (customer != null) {
				this.custId = customer.getCustID();
			} else {
				MessageUtil.showError("Invalid Customer Please Select valid Customer");
			}
		}

		return customer;
	}

	public Customer fetchCustomerDataByID(long custID) {
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();

		Customer customer = this.customerDetailsService.getCustomer(custID);

		this.finReference.setFilters(new Filter[] { new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL) });

		this.custId = customer.getCustID();
		this.custCIF.setValue(customer.getCustCIF());

		return customer;
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnSearchCustCIF Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug("Entering " + event.toString());
		this.finReference.setValue("");
		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		logger.debug("Entering" + event.toString());
		this.window_SelectExcessTransfer.onClose();
		logger.debug("Leaving" + event.toString());
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug("Entering");

		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;
		customer = (Customer) nCustomer;
		addFilter(customer);

		logger.debug("Leaving ");
	}

	private void doShowDialog() {
		logger.debug("Entering ");

		final Map<String, Object> map = new HashMap<String, Object>();
		FinExcessTransfer finExcessTransfer = excessTransferService.getFinExcessData(getFinanceMain().getFinID());
		FinanceMain financeMain = financeMainService.getFinanceMainByFinRef(getFinanceMain().getFinID());
		finExcessTransfer.setCustId(custId);
		finExcessTransfer.setFinReference(this.finReference.getValue());
		finExcessTransfer.setTransferDate(SysParamUtil.getAppDate());
		finExcessTransfer.setWorkflowId(this.finExcessTransfer.getWorkflowId());
		finExcessTransfer.setNewRecord(this.finExcessTransfer.isNewRecord());
		map.put("financeMain", financeMain);
		map.put("finExcessTransfer", finExcessTransfer);
		map.put("customer", getCustomer());
		map.put("ccyFormat", CurrencyUtil.getFormat(SysParamUtil.getAppCurrency()));
		map.put("excessTransferListCtrl", excessTransferListCtrl);

		Executions.createComponents("/WEB-INF/pages/FinanceManagement/ExcessTransfer/ExcessTransferDialog.zul", null,
				map);

		logger.debug("Leaving ");

	}

	public void onClick$btnSearchFinRef(Event event) {
		validateFinReference(event, true);
	}

	public void onFulfill$finReference(Event event) {
		validateFinReference(event, false);
	}

	public void validateFinReference(Event event, boolean isShowSearchList) {
		logger.debug("Entering " + event.toString());

		this.finReference.setConstraint("");
		this.finReference.clearErrorMessage();
		Clients.clearWrongValue(finReference);
		Object dataObject = null;

		if (isShowSearchList) {
			dataObject = ExtendedSearchListBox.show(this.window_SelectExcessTransfer, "FinanceMain");
		} else {
			dataObject = this.finReference.getObject();
		}

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				this.finReference.setValue(financeMain.getFinReference());
				String finIsActive = financeMain.isFinIsActive() == true ? "[Active]" : "[InActive]";
				this.finReference.setDescription(financeMain.getFinType() + " - " + finIsActive);
				this.custCIF.setValue(String.valueOf(financeMain.getCustCIF()));
				setFinanceMain(financeMain);
				resetDefaults(financeMain);
			}

		}
		logger.debug("Leaving " + event.toString());
	}

	public void doWriteComponentsToBean() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isEmpty(this.custCIF.getValue())) {
				throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptPayment_Customer.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.finReference.getValue())) {
				throw new WrongValueException(this.finReference, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReceiptPayment_LoanReference.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doClearMessage();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

		this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_Customer.value"),
				PennantRegularExpressions.REGEX_NUMERIC, true));

		this.finReference
				.setConstraint(new PTStringValidator(Labels.getLabel("label_ReceiptPayment_LoanReference.value"),
						PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	public void resetDefaults(FinanceMain financeMain) {
		this.finReference.setValue(financeMain.getFinReference());

		if (!this.custCIF.getValue().isEmpty()) {
			customer = fetchCustomerDataByID(financeMain.getCustID());
		}

	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public ExcessTransferService getExcessTransferService() {
		return excessTransferService;
	}

	public void setExcessTransferService(ExcessTransferService excessTransferService) {
		this.excessTransferService = excessTransferService;
	}

	public ExcessTransferListCtrl getExcessTransferListCtrl() {
		return excessTransferListCtrl;
	}

	public void setExcessTransferListCtrl(ExcessTransferListCtrl excessTransferListCtrl) {
		this.excessTransferListCtrl = excessTransferListCtrl;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

}
