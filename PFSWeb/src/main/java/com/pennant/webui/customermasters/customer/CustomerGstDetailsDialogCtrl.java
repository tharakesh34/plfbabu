package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.service.customermasters.CustomerGstService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.gst.CustomerGSTListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/CustomerMaster/Customer/CustomerGstDetailsDialog.zul file. <br>
 * ************************************************************<br>
 */

public class CustomerGstDetailsDialogCtrl extends GFCBaseCtrl<CustomerGST> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = LogManager.getLogger(CustomerGstDetailsDialogCtrl.class);

	// autowired
	protected Window window_customerGstDetailsDialog;
	protected Textbox custID;
	protected Textbox gstNumber;
	protected Combobox frequencyType;
	protected Textbox custCIF;
	protected Toolbar toolBar_CustomerGst;
	protected Listbox listBoxCustomerGst;
	protected Listheader listHead_CustomerGst;
	protected Button button_CustomerGst_New;

	private CustomerDialogCtrl customerDialogCtrl;
	private transient CustomerGSTListCtrl customerGSTListCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;

	private CustomerGST customerGst;
	private List<CustomerGST> customerGstlist = new ArrayList<CustomerGST>();
	private List<CustomerGSTDetails> customerGSTDetailsList = new ArrayList<CustomerGSTDetails>();

	List<ValueLabel> frequencyList = new ArrayList<>();

	private boolean newRecord = false;
	private boolean newCustomer = true;
	private String userRole = "";

	protected long custId;

	private boolean isFinanceProcess = false;
	private boolean workflow = false;
	private transient boolean validationOn;
	private transient CustomerGstService customerGstService;
	private boolean fromDouble;

	private static final int GST_FINANCIAL_START_YEAR = 2018;

	public CustomerGstDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerGstDetailsDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_customerGstDetailsDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_customerGstDetailsDialog);
		try {
			// Get the required arguments.

			if (arguments.containsKey("customerGst")) {
				this.customerGst = (CustomerGST) arguments.get("customerGst");
				CustomerGST befImage = new CustomerGST();
				BeanUtils.copyProperties(this.customerGst, befImage);
				this.customerGst.setBefImage(befImage);
				setCustomerGst(this.customerGst);
			} else {
				setCustomerGst(null);
			}

			if (arguments.containsKey("fromDouble")) {
				this.fromDouble = (boolean) arguments.get("fromDouble");
			}

			if (getCustomerGst().isNewRecord()) {
				setNewRecord(true);
			}
			if (arguments.containsKey("customerDialogCtrl")) {
				setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerGst.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerGstDetailsDialog");
				}
			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (getCustomerDialogCtrl() != null && !isFinanceProcess) {
				workflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
			}

			doLoadWorkFlow(this.customerGst.isWorkflow(), this.customerGst.getWorkflowId(),
					this.customerGst.getNextTaskId());
			doCheckRights();
			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doShowDialog(this.customerGst);

			this.window_customerGstDetailsDialog.setWidth("75%");
			this.window_customerGstDetailsDialog.setHeight("65%");
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.gstNumber.setMaxlength(15);

		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.gstNumber.isReadonly()) {
			this.gstNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_GSTNumber.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}
		if (!this.gstNumber.isReadonly()) {
			this.gstNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_GSTNumber.value"),
					PennantRegularExpressions.REGEX_GSTIN, true));
		}

		logger.debug("Leaving");
	}

	public void doShowDialog(CustomerGST customerGst) {
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.custID.focus();
			if (isNewCustomer()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		doWriteBeanToComponents(customerGst);
	}

	public void doWriteBeanToComponents(CustomerGST acustomerGST) {
		logger.debug("Entering");

		this.gstNumber.setValue(acustomerGST.getGstNumber());
		this.custID.setValue(acustomerGST.getCustCif());
		custId = acustomerGST.getCustId();
		fillComboBox(this.frequencyType, acustomerGST.getFrequencytype(), PennantStaticListUtil.getfrequencyType(), "");

		if (acustomerGST.isNewRecord() && !fromDouble) {
			this.frequencyType.setSelectedIndex(1);
		}
		if (fromDouble) {
			this.frequencyType.setDisabled(true);
		}
		frequencyList = PennantStaticListUtil.getfrequency(this.frequencyType.getSelectedItem().getValue());
		this.recordStatus.setValue(acustomerGST.getRecordStatus());
		if (CollectionUtils.isNotEmpty(acustomerGST.getCustomerGSTDetailslist())) {
			CustomerGST detail = (CustomerGST) ObjectUtil.clone(acustomerGST);
			List<CustomerGSTDetails> customerGSTDetails = detail.getCustomerGSTDetailslist();
			// customerGSTDetails= customerGSTDetails.stream().distinct().collect(Collectors.toList());

			for (int i = 0; i < customerGSTDetails.size(); i++) {
				customerGSTDetails.get(i).setKeyValue(i + 1);
			}
			setCustomerGSTDetailsList(customerGSTDetails);
			doFillCustomerGSTDetails();
		}

		logger.debug("Leaving");
	}

	private void doFillCustomerGSTDetails() {
		logger.debug(Literal.ENTERING);

		this.listBoxCustomerGst.getItems().clear();

		int size = getCustomerGSTDetailsList().size();
		for (int i = 0; i < size; i++) {
			if (!StringUtils.equals(getCustomerGSTDetailsList().get(i).getRecordType(),
					PennantConstants.RECORD_TYPE_DEL)
					&& !StringUtils.equals(getCustomerGSTDetailsList().get(i).getRecordType(),
							PennantConstants.RECORD_TYPE_CAN)) {
				renderItem(getCustomerGSTDetailsList().get(i));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(CustomerGST acustomerGST) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			acustomerGST.setCustId(custId);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			acustomerGST.setGstNumber(this.gstNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.frequencyType.getSelectedItem().getLabel() != null) {
				acustomerGST.setFrequencytype(this.frequencyType.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		boolean focus = false;
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
				Component component = wve.get(i).getComponent();
				if (!focus) {
					focus = setComponentFocus(component);
				}
			}
			throw new WrongValuesException(wvea);
		}
		acustomerGST.setRecordStatus(this.recordStatus.getValue());

		setCustomerGst(acustomerGST);
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		logger.debug("Leaving");
	}

	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerGST aCustomergst = new CustomerGST();
		BeanUtils.copyProperties(getCustomerGst(), aCustomergst);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aCustomergst);

		if (!saveCustomerGSTList(aCustomergst)) {
			return;
		}
		if (CollectionUtils.isEmpty(getCustomerGSTDetailsList())) {
			MessageUtil.showError("Atleast One frequency for a financial year should be given");
			return;
		}
		aCustomergst.setCustomerGSTDetailslist(getCustomerGSTDetailsList());
		// this.customerGstlist.add(aCustomergst);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aCustomergst.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomergst.getRecordType())) {
				aCustomergst.setVersion(aCustomergst.getVersion() + 1);
				if (isNew) {
					aCustomergst.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomergst.setRecordType(PennantConstants.RCD_UPD);
				}
			}
		} else {
			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomergst.setVersion(1);
					aCustomergst.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (workflow && !isFinanceProcess && StringUtils.isBlank(aCustomergst.getRecordType())) {
						aCustomergst.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(aCustomergst.getRecordType())) {
					aCustomergst.setVersion(aCustomergst.getVersion() + 1);
					aCustomergst.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomergst.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomergst.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aCustomergst.setVersion(aCustomergst.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

			}
		}

		// save it to database
		try {
			if (isNewCustomer()) {
				AuditHeader auditHeader = newFinanceCustomerProcess(aCustomergst, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_customerGstDetailsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerGstDetails(this.customerGstlist);
					closeDialog();
				}
			} else if (doProcess(aCustomergst, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		// doSetValidation();
		// customerGstService.savaCustomerGstDeatails(this.customerGst);
		logger.debug(Literal.LEAVING);

	}

	private AuditHeader newGstInfoDetailProcess(CustomerGSTDetails detail, List<CustomerGSTDetails> infoList,
			String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = new AuditHeader();
		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = detail.getFrequancy();
		errParm[0] = "Frequency" + ":" + valueParm[0];

		customerGSTDetailsList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(infoList)) {
			for (int i = 0; i < infoList.size(); i++) {
				if (StringUtils.equals(detail.getFrequancy(), infoList.get(i).getFrequancy())) {
					if (detail.isNewRecord() && StringUtils.isEmpty(detail.getRecordType())) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.customerGSTDetailsList.add(detail);
						} else if (detail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.customerGSTDetailsList.add(detail);
						} else if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < customerGst.getCustomerGSTDetailslist().size(); j++) {
								CustomerGSTDetails infoDetail = customerGst.getCustomerGSTDetailslist().get(j);
								if (infoDetail.getFrequancy().equals(detail.getFrequancy())) {
									this.customerGSTDetailsList.add(infoDetail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.customerGSTDetailsList.add(infoList.get(i));
							if (detail.getRecordType().equals(PennantConstants.RCD_ADD)) {
								recordAdded = true;
							}
						}
					}
				} else {
					this.customerGSTDetailsList.add(infoList.get(i));
				}
			}
		}

		if (!recordAdded) {
			this.customerGSTDetailsList.add(detail);
		}
		return auditHeader;
	}

	private boolean saveCustomerGSTList(CustomerGST aCustomergst) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Map<String, CustomerGSTDetails> hashMap = new HashMap<>();

		List<CustomerGSTDetails> infoList = aCustomergst.getCustomerGSTDetailslist();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		for (Listitem listitem : listBoxCustomerGst.getItems()) {
			try {
				getCompValuetoBean(listitem, "frequency");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				getCompValuetoBean(listitem, "year");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "salAmount");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve);
			CustomerGSTDetails customerGSTDetails = (CustomerGSTDetails) listitem.getAttribute("data");
			boolean isNew = false;
			isNew = customerGSTDetails.isNewRecord();
			String tranType = "";
			if (isNewCustomer()) {
				if (customerGSTDetails.isNewRecord()) {
					customerGSTDetails.setVersion(1);
					customerGSTDetails.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(customerGSTDetails.getRecordType())) {
					customerGSTDetails.setVersion(customerGSTDetails.getVersion() + 1);
					customerGSTDetails.setRecordType(PennantConstants.RCD_UPD);
				}

				if (customerGSTDetails.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (customerGSTDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				customerGSTDetails.setVersion(customerGSTDetails.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			try {
				AuditHeader auditHeader = newGstInfoDetailProcess(customerGSTDetails, infoList, tranType);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader = ErrorControl.showErrorDetails(this.window_customerGstDetailsDialog, auditHeader);
					setCustomerGSTDetailsList(customerGst.getCustomerGSTDetailslist());
					return false;
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					infoList = customerGSTDetailsList;
				}
			} catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
			hashMap.put(customerGSTDetails.getFrequancy(), customerGSTDetails);
		}

		//
		for (CustomerGSTDetails detail : customerGSTDetailsList) {
			if (!hashMap.containsKey(detail.getFrequancy())) {
				if (StringUtils.isBlank(detail.getRecordType())) {
					detail.setNewRecord(true);
					detail.setVersion(detail.getVersion() + 1);
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					if (!StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			}
		}

		setCustomerGSTDetailsList(customerGSTDetailsList);
		logger.debug(Literal.LEAVING);
		return true;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		boolean focus = false;
		if (wve.size() > 0) {
			setCustomerGSTDetailsList(customerGst.getCustomerGSTDetailslist());
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (!focus) {
						focus = setComponentFocus(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	private void getCompValuetoBean(Listitem listitem, String comonentId) {
		CustomerGSTDetails customerGSTDetails = null;
		customerGSTDetails = (CustomerGSTDetails) listitem.getAttribute("data");
		switch (comonentId) {
		case "frequency":

			Hbox hbox1 = (Hbox) getComponent(listitem, "frequency");
			Combobox frequency = (Combobox) hbox1.getLastChild();
			Clients.clearWrongValue(frequency);
			String frequencyValue = frequency.getSelectedItem().getValue();
			if (!frequency.isDisabled()) {
				if (StringUtils.equals(PennantConstants.List_Select, frequencyValue)) {
					throw new WrongValueException(frequency,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Frequency " }));
				}
			}
			customerGSTDetails.setFrequancy(frequencyValue);
			break;
		case "year":
			Hbox hbox2 = (Hbox) getComponent(listitem, "year");
			Combobox year = (Combobox) hbox2.getLastChild();
			Clients.clearWrongValue(year);
			String yearValue = year.getSelectedItem().getValue();
			if (!year.isDisabled()) {
				if (yearValue == null) {
					throw new WrongValueException(year, Labels.getLabel("FIELD_IS_MAND", new String[] { "Year " }));
				}
			}
			customerGSTDetails.setFinancialYear(yearValue);
			break;
		case "salAmount":
			BigDecimal gstAmt = BigDecimal.ZERO;
			Hbox hbox4 = (Hbox) getComponent(listitem, "salAmount");
			CurrencyBox salAmountValue = (CurrencyBox) hbox4.getLastChild();
			Clients.clearWrongValue(salAmountValue);
			if (salAmountValue.getValidateValue() != null) {
				gstAmt = salAmountValue.getValidateValue();
			}
			if (!(salAmountValue.isDisabled()) && (gstAmt.intValue() < 0)) {
				throw new WrongValueException(salAmountValue,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Sale Amount" }));
			}
			customerGSTDetails.setSalAmount(CurrencyUtil.unFormat(gstAmt, 2));
			break;
		default:
			break;
		}
	}

	private Component getComponent(Listitem listitem, String listcellId) {
		List<Listcell> listcels = listitem.getChildren();

		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}

			id = id.replaceAll("\\d", "");
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("this.pageRightName", getRole());

		this.button_CustomerGst_New.setVisible(
				!this.enqiryModule || getUserWorkspace().isAllowed("button_CustomerGstDetailsDialog_btnNew"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerGstDetailsDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerGSTListCtrl().search();
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerAddres (CustomerAddres)
	 * 
	 * @param tranType        (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CustomerGST aCustomergst, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomergst.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomergst.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomergst.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomergst.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomergst.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomergst);
				}

				if (isNotesMandatory(taskId, aCustomergst)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCustomergst.setTaskId(taskId);
			aCustomergst.setNextTaskId(nextTaskId);
			aCustomergst.setRoleCode(getRole());
			aCustomergst.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomergst, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomergst);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomergst, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomergst, tranType);
			// processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerGST customerGst = (CustomerGST) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCustomerGstService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getCustomerGstService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCustomerGstService().doApprove(auditHeader);

					if (customerGst.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCustomerGstService().doReject(auditHeader);

					if (customerGst.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_customerGstDetailsDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_customerGstDetailsDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.customerGst), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private void renderItem(CustomerGSTDetails customerGSTDetails) {
		Listitem listItem = new Listitem();
		Listcell listCell;
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("button_CustomerGstDetailsDialog_btnSave");

		// Frequence
		listCell = new Listcell();
		Combobox frequency = new Combobox();
		fillComboBox(frequency, customerGSTDetails.getFrequancy(), frequencyList, " ");
		if (customerGSTDetails.isNewRecord() && StringUtils.isEmpty(customerGSTDetails.getRecordType())) {
			readOnlyComponent(false, frequency);
		} else {
			readOnlyComponent(true, frequency);
		}
		listCell.setId("frequency".concat(String.valueOf(customerGSTDetails.getKeyValue())));
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		frequency.setWidth("130px");
		hbox.appendChild(space);
		hbox.appendChild(frequency);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listCell = new Listcell();
		Combobox year = new Combobox();
		fillComboBox(year, customerGSTDetails.getFinancialYear(), getYearList(), " ");
		if (customerGSTDetails.isNewRecord() && StringUtils.isEmpty(customerGSTDetails.getRecordType())) {
			readOnlyComponent(false, year);
		} else {
			readOnlyComponent(true, year);
		}
		listCell.setId("year".concat(String.valueOf(customerGSTDetails.getKeyValue())));
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		year.setWidth("130px");
		hbox.appendChild(space);
		hbox.appendChild(year);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Sales Amount
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox gstAmount = new CurrencyBox();
		gstAmount.setBalUnvisible(true);
		listCell.setId("salAmount".concat(String.valueOf(customerGSTDetails.getKeyValue())));
		space.setSclass("mandatory");
		gstAmount.setFormat(PennantApplicationUtil.getAmountFormate(2));
		gstAmount.setScale(2);
		gstAmount.setValue(PennantApplicationUtil.formateAmount(customerGSTDetails.getSalAmount(), 2));
		gstAmount.setDisabled(isReadOnly);
		hbox.appendChild(space);
		hbox.appendChild(gstAmount);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Delete action
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel("Delete");
		if (isFinanceProcess) {
			button.setVisible(false);
		} else {
			button.setVisible(getUserWorkspace().isAllowed("button_CustomerGstDetailsDialog_btnSave"));
		}
		button.addForward("onClick", self, "onClickCustGstDetailButtonDelete", listItem);
		hbox.appendChild(space);
		listCell.appendChild(button);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listItem.setAttribute("data", customerGSTDetails);
		this.listBoxCustomerGst.appendChild(listItem);
	}

	private List<ValueLabel> getYearList() {
		List<ValueLabel> years = new ArrayList<ValueLabel>();
		int currentYear = DateTime.now().getYear();
		for (int year = GST_FINANCIAL_START_YEAR; year <= currentYear; year++) {
			years.add(new ValueLabel(String.valueOf(year), String.valueOf(year)));
		}
		return years;
	}

	public void onClick$button_CustomerGst_New(Event event) {
		logger.debug(Literal.ENTERING);
		CustomerGSTDetails customerGSTDetails = new CustomerGSTDetails();
		customerGSTDetails.setNewRecord(true);
		int keyValue = 0;

		List<Listitem> customerGSTDetailsList = listBoxCustomerGst.getItems();
		if (customerGSTDetailsList != null && !customerGSTDetailsList.isEmpty()) {
			for (Listitem detail : customerGSTDetailsList) {
				CustomerGSTDetails CustomerGstInfo = (CustomerGSTDetails) detail.getAttribute("data");
				if (CustomerGstInfo.getKeyValue() > keyValue) {
					keyValue = CustomerGstInfo.getKeyValue();
				}
			}
		}
		customerGSTDetails.setKeyValue(keyValue + 1);

		renderItem(customerGSTDetails);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * click & onchange events
	 * 
	 */
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Entering" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onChange$frequencyType(Event event) {
		logger.debug("Entering");
		frequencyList = PennantStaticListUtil.getfrequency(this.frequencyType.getValue());
		this.listBoxCustomerGst.getItems().clear();
		int size = getCustomerGSTDetailsList().size();
		for (int i = 0; i < size; i++) {
			if (!StringUtils.equals(getCustomerGSTDetailsList().get(i).getRecordType(),
					PennantConstants.RECORD_TYPE_DEL)
					&& !StringUtils.equals(getCustomerGSTDetailsList().get(i).getRecordType(),
							PennantConstants.RECORD_TYPE_CAN)) {
				renderItem(getCustomerGSTDetailsList().get(i));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		// doWriteBeanToComponents(this.customerGst.getBefImage());
		// doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		// this.academicLevel.setReadonly(true);
		// this.academicDecipline.setReadonly(true);
		// this.academicDesc.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}

	// Audit methods
	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		/*
		 * this.custAddrType.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_CustomerAddresDialog_CustAddrType.value"), null, true, true));
		 * 
		 * this.custAddrCountry.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_CustomerAddresDialog_CustAddrCountry.value"), null, true, true));
		 * 
		 * this.custAddrProvince.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_CustomerAddresDialog_CustAddrProvince.value"), null, true, true));
		 * 
		 * if (!PennantConstants.CITY_FREETEXT) { this.custAddrCity.setConstraint(new PTStringValidator(
		 * Labels.getLabel("label_CustomerAddresDialog_CustAddrCity.value"), null, true, true)); }
		 */

		logger.debug("Leaving");
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_customerGstDetailsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.customerGst.isNewRecord()) {
			this.frequencyType.setReadonly(false);
		} else {
			this.gstNumber.setReadonly(true);
			this.frequencyType.setDisabled(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.customerGst.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	private AuditHeader newFinanceCustomerProcess(CustomerGST aCustomergst, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomergst, tranType);
		customerGstlist = new ArrayList<CustomerGST>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomergst.getLovDescCustCIF());
		valueParm[1] = aCustomergst.getGstNumber();

		errParm[0] = PennantJavaUtil.getLabel("label_CustomerBankInfoDialog_CustID.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustomerDialog_GSTNumber.value") + ":" + valueParm[1];

		if (getCustomerDialogCtrl().getCustomerGstList() != null
				&& getCustomerDialogCtrl().getCustomerGstList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerGstList().size(); i++) {
				CustomerGST customerGSTInfo = getCustomerDialogCtrl().getCustomerGstList().get(i);

				if (aCustomergst.getGstNumber().equals(customerGSTInfo.getGstNumber())
						&& aCustomergst.getFrequencytype().equals(customerGSTInfo.getFrequencytype())) {
					// Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomergst.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCustomergst.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							customerGstlist.add(aCustomergst);
						} else if (aCustomergst.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomergst.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomergst.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							customerGstlist.add(aCustomergst);
						} else if (aCustomergst.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerGstList()
									.size(); j++) {
								CustomerGST customerGSTDts = getCustomerDialogCtrl().getCustomerDetails()
										.getCustomerGstList().get(j);
								if (customerGSTDts.getCustId() == aCustomergst.getCustId()
										&& customerGSTDts.getFrequencytype().equals(aCustomergst.getFrequencytype())) {
									customerGstlist.add(customerGSTDts);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerGstlist.add(customerGSTInfo);
						}
					}
				} else {
					customerGstlist.add(customerGSTInfo);
				}
			}
		}

		if (!recordAdded) {
			customerGstlist.add(aCustomergst);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * @param aCustomerAddres
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CustomerGST aCustomergst, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomergst.getBefImage(), aCustomergst);
		return new AuditHeader(getReference(), String.valueOf(aCustomergst.getCustId()), null, null, auditDetail,
				aCustomergst.getUserDetails(), getOverideMap());
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custID.setErrorMessage("");
		this.gstNumber.setErrorMessage("");
		this.frequencyType.setErrorMessage("");
		// this.frequency.setErrorMessage("");
		logger.debug("Leaving");
	}

	public List<CustomerGSTDetails> getCustomerGSTDetailsList() {
		return customerGSTDetailsList;
	}

	public void setCustomerGSTDetailsList(List<CustomerGSTDetails> customerGSTDetailsList) {
		this.customerGSTDetailsList = customerGSTDetailsList;
	}

	public void onClickCustGstDetailButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxCustomerGst.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
	}

	public CustomerGSTListCtrl getCustomerGSTListCtrl() {
		return customerGSTListCtrl;
	}

	public void setCustomerGSTListCtrl(CustomerGSTListCtrl customerGSTListCtrl) {
		this.customerGSTListCtrl = customerGSTListCtrl;
	}

	// * Setters & Getters
	public CustomerGST getCustomerGst() {
		return customerGst;
	}

	public void setCustomerGst(CustomerGST customerGst) {
		this.customerGst = customerGst;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public CustomerGstService getCustomerGstService() {
		return customerGstService;
	}

	public void setCustomerGstService(CustomerGstService customerGstService) {
		this.customerGstService = customerGstService;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
}
