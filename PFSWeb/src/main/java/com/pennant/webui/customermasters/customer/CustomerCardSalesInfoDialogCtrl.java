package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

public class CustomerCardSalesInfoDialogCtrl extends GFCBaseCtrl<CustCardSales> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = LogManager.getLogger(CustomerCardSalesInfoDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerCardSalesInfoDialog;

	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Textbox merchantId;

	protected Button button_CustomerCardSalesInfoDialog_btnCardMonthSales;
	protected Toolbar toolBar_cardMonthSales;
	protected Listbox listBoxCardMonthSales;
	protected Listheader listHead_CardMonthSales;

	private CustCardSales custCardSales;
	private transient boolean validationOn;
	protected Button btnSearchPRCustid;
	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustCardSales> CustCardSalesList;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	protected int accNoLength;

	private List<CustCardSalesDetails> custCardMonthSales = new ArrayList<>();
	private boolean workflow = false;

	/**
	 * default constructor.<br>
	 */
	public CustomerCardSalesInfoDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerCardSalesInfo";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerBankInfo object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerCardSalesInfoDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_CustomerCardSalesInfoDialog);

		try {

			if (arguments.containsKey("customerCardSales")) {
				this.custCardSales = (CustCardSales) arguments.get("customerCardSales");
				CustCardSales befImage = new CustCardSales();
				BeanUtils.copyProperties(this.custCardSales, befImage);
				this.custCardSales.setBefImage(befImage);
				setCustCardSales(this.custCardSales);
			} else {
				setCustCardSales(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}
			if (arguments.containsKey("retailCustomer")) {
				boolean isRetailCust = (boolean) arguments.get("retailCustomer");
				if (!isRetailCust) {
				}
			}

			if (getCustCardSales().isNewRecord()) {
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
				this.custCardSales.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerCardSalesInfoDialog");
				}
			}

			if (arguments.containsKey("customerViewDialogCtrl")) {
				setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.custCardSales.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerCardSalesDialog");
				}
			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (getCustomerDialogCtrl() != null && !isFinanceProcess) {
				workflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
			}

			if (arguments.containsKey("CustCardSalesList")) {
				CustCardSalesList = (List<CustCardSales>) arguments.get("CustomerCardSalesInfoList");
			}
			doLoadWorkFlow(this.custCardSales.isWorkflow(), this.custCardSales.getWorkflowId(),
					this.custCardSales.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerCardSalesInfo");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustCardSales());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerCardSalesInfoDialog.onClose();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		// Empty sent any required attributes

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
		}

		this.merchantId.setMaxlength(20);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities("CustomerCardSalesInfo", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerCardSalesInfo_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerCardSalesInfo_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerCardSalesInfo_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerCardSalesInfo_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_CustomerCardSalesInfoDialog_btnCardMonthSales(Event event) {
		logger.debug(Literal.ENTERING);
		CustCardSalesDetails custCardMonthSales = new CustCardSalesDetails();
		custCardMonthSales.setNewRecord(true);
		int keyValue = 0;

		List<Listitem> custCardMonthSalesData = listBoxCardMonthSales.getItems();
		if (custCardMonthSalesData != null && !custCardMonthSalesData.isEmpty()) {
			for (Listitem detail : custCardMonthSalesData) {
				CustCardSalesDetails cardMonthData = (CustCardSalesDetails) detail.getAttribute("data");
				if (cardMonthData.getKeyValue() > keyValue) {
					keyValue = cardMonthData.getKeyValue();
				}
			}
		}
		custCardMonthSales.setKeyValue(keyValue + 1);

		renderItem(custCardMonthSales);
		logger.debug(Literal.LEAVING);

	}

	private void doFillCustCardMonthSales() {
		logger.debug(Literal.ENTERING);

		this.listBoxCardMonthSales.getItems().clear();
		int size = getCustCardMonthSales().size();
		for (int i = 0; i < size; i++) {
			if (!StringUtils.equals(getCustCardMonthSales().get(i).getRecordType(), PennantConstants.RECORD_TYPE_DEL)
					&& !StringUtils.equals(getCustCardMonthSales().get(i).getRecordType(),
							PennantConstants.RECORD_TYPE_CAN)) {
				renderItem(getCustCardMonthSales().get(i));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void renderItem(CustCardSalesDetails custCardMonthSales) {
		Listitem listItem = new Listitem();
		listItem.setHeight("50px");
		Listcell listCell;
		Hbox hbox;
		Space space;

		// Month
		listCell = new Listcell();
		Datebox month = new Datebox();
		month.setFormat(PennantConstants.monthYearFormat);
		month.setValue(custCardMonthSales.getMonth());
		listCell.setId("month".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		// space.setSclass("mandatory");
		month.setWidth("100px");
		hbox.appendChild(space);
		hbox.appendChild(month);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Sale Amount
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox saleAmt = new CurrencyBox();
		saleAmt.setBalUnvisible(true);
		listCell.setId("saleAmount".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		space.setSclass("mandatory");
		saleAmt.setFormat(PennantApplicationUtil.getAmountFormate(2));
		saleAmt.setScale(2);
		saleAmt.setValue(PennantApplicationUtil.formateAmount(custCardMonthSales.getSalesAmount(), 2));
		saleAmt.setTextBoxWidth(130);
		hbox.appendChild(space);
		hbox.appendChild(saleAmt);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// No Of Settlements
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Intbox noOfSettlements = new Intbox();
		noOfSettlements.setMaxlength(4);
		listCell.setId("NoOfSettlements".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		noOfSettlements.setValue(custCardMonthSales.getNoOfSettlements());
		hbox.appendChild(space);
		hbox.appendChild(noOfSettlements);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Total No Of Credits
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Intbox totalNoOfCredits = new Intbox();
		totalNoOfCredits.setMaxlength(4);
		listCell.setId("totalNoOfCredits".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		totalNoOfCredits.setValue(custCardMonthSales.getTotalNoOfCredits());
		hbox.appendChild(space);
		hbox.appendChild(totalNoOfCredits);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Credit Value
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox totalCreditValue = new CurrencyBox();
		totalCreditValue.setBalUnvisible(true);
		listCell.setId("totalCreditValue".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		totalCreditValue.setFormat(PennantApplicationUtil.getAmountFormate(2));
		totalCreditValue.setScale(2);
		totalCreditValue.setValue(PennantApplicationUtil.formateAmount(custCardMonthSales.getTotalCreditValue(), 2));
		totalCreditValue.setTextBoxWidth(130);
		hbox.appendChild(space);
		hbox.appendChild(totalCreditValue);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Total No Of Debits
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Intbox totalNoOfDebits = new Intbox();
		totalNoOfDebits.setMaxlength(4);
		listCell.setId("totalNoOfDebits".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		totalNoOfDebits.setValue(custCardMonthSales.getTotalNoOfDebits());
		hbox.appendChild(space);
		hbox.appendChild(totalNoOfDebits);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Debit Amount
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox totalDebitValue = new CurrencyBox();
		totalDebitValue.setBalUnvisible(true);
		listCell.setId("totalDebitValue".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		totalDebitValue.setFormat(PennantApplicationUtil.getAmountFormate(2));
		totalDebitValue.setScale(2);
		totalDebitValue.setValue(PennantApplicationUtil.formateAmount(custCardMonthSales.getTotalDebitValue(), 2));
		totalDebitValue.setTextBoxWidth(130);
		hbox.appendChild(space);
		hbox.appendChild(totalDebitValue);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Bounce In Ward
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox bounceInWard = new CurrencyBox();
		bounceInWard.setBalUnvisible(true);
		listCell.setId("inwardBounce".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		bounceInWard.setFormat(PennantApplicationUtil.getAmountFormate(2));
		bounceInWard.setScale(2);
		bounceInWard.setValue(PennantApplicationUtil.formateAmount(custCardMonthSales.getInwardBounce(), 2));
		bounceInWard.setTextBoxWidth(130);
		hbox.appendChild(space);
		hbox.appendChild(bounceInWard);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// Bounce Out Ward
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		CurrencyBox outwardBounce = new CurrencyBox();
		outwardBounce.setBalUnvisible(true);
		listCell.setId("outwardBounce".concat(String.valueOf(custCardMonthSales.getKeyValue())));
		// space.setSclass("mandatory");
		outwardBounce.setFormat(PennantApplicationUtil.getAmountFormate(2));
		outwardBounce.setScale(2);
		outwardBounce.setValue(PennantApplicationUtil.formateAmount(custCardMonthSales.getOutwardBounce(), 2));
		outwardBounce.setTextBoxWidth(130);
		hbox.appendChild(space);
		hbox.appendChild(outwardBounce);
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
		button.addForward("onClick", self, "onClickAccBehaviourButtonDelete", listItem);
		hbox.appendChild(space);
		listCell.appendChild(button);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listItem.setAttribute("data", custCardMonthSales);
		this.listBoxCardMonthSales.appendChild(listItem);
	}

	public void onClickAccBehaviourButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxCardMonthSales.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doEdit();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		MessageUtil.showHelpWindow(event, window_CustomerCardSalesInfoDialog);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doDelete();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCancel();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.custCardSales.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerBankInfo CustomerBankInfo
	 */
	public void doWriteBeanToComponents(CustCardSales aCustCardSales) {
		logger.debug(Literal.ENTERING);

		if (aCustCardSales.getCustID() != Long.MIN_VALUE) {
			this.custID.setValue(aCustCardSales.getCustID());
		}
		this.merchantId.setText(aCustCardSales.getMerchantId());
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustCardSales.getLovDescCustCIF()));
		this.custShrtName.setValue(StringUtils.trimToEmpty(aCustCardSales.getLovDescCustShrtName()));

		this.recordStatus.setValue(aCustCardSales.getRecordStatus());

		if (aCustCardSales.getCustCardMonthSales().size() > 0) {
			Cloner cloner = new Cloner();
			CustCardSales detail = (CustCardSales) cloner.deepClone(aCustCardSales);
			List<CustCardSalesDetails> custCardMonthSales = detail.getCustCardMonthSales();
			for (int i = 0; i < custCardMonthSales.size(); i++) {
				custCardMonthSales.get(i).setKeyValue(i + 1);
			}
			setCustCardMonthSales(custCardMonthSales);
			doFillCustCardMonthSales();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustCardSales
	 */
	public void doWriteComponentsToBean(CustCardSales aCustCardSales) {
		logger.debug(Literal.ENTERING);
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustCardSales.setLovDescCustCIF(this.custCIF.getValue());
			aCustCardSales.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustCardSales.setMerchantId(this.merchantId.getValue());
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

		aCustCardSales.setRecordStatus(this.recordStatus.getValue());
		setCustCardSales(aCustCardSales);
		logger.debug(Literal.LEAVING);
	}

	public boolean saveCardSalesDetailInfoList(CustCardSales custCardSales) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Map<Date, CustCardSalesDetails> hashMap = new HashMap<>();

		List<CustCardSalesDetails> infoList = custCardSales.getCustCardMonthSales();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		for (Listitem listitem : listBoxCardMonthSales.getItems()) {
			try {
				getCompValuetoBean(listitem, "month");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				getCompValuetoBean(listitem, "saleAmount");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "NoOfSettlements");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "totalNoOfCredits");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "totalNoOfDebits");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "totalCreditValue");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "totalDebitValue");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "inwardBounce");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				getCompValuetoBean(listitem, "outwardBounce");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			showErrorDetails(wve);
			CustCardSalesDetails custCardMonthSalesData = (CustCardSalesDetails) listitem.getAttribute("data");

			boolean isNew = false;
			isNew = custCardMonthSalesData.isNewRecord();
			String tranType = "";

			if (custCardMonthSalesData.isNewRecord()) {
				custCardMonthSalesData.setVersion(1);
				custCardMonthSalesData.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				if (workflow && !isFinanceProcess && StringUtils.isBlank(custCardMonthSalesData.getRecordType())) {
					custCardMonthSalesData.setNewRecord(true);
				}
			}

			if (StringUtils.isBlank(custCardMonthSalesData.getRecordType())) {
				custCardMonthSalesData.setVersion(custCardMonthSalesData.getVersion() + 1);
				custCardMonthSalesData.setRecordType(PennantConstants.RCD_UPD);
			}

			if (custCardMonthSalesData.getRecordType().equals(PennantConstants.RCD_ADD)
					&& custCardMonthSalesData.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (custCardMonthSalesData.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			} else {
				custCardMonthSalesData.setVersion(custCardMonthSalesData.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
			try {
				AuditHeader auditHeader = newCardSaleInfoDetailProcess(custCardMonthSalesData, infoList, tranType);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerCardSalesInfoDialog, auditHeader);
					setCustCardMonthSales(custCardSales.getCustCardMonthSales());
					return false;
				}
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					infoList = custCardMonthSales;
				}
			} catch (final DataAccessException e) {
				logger.error(Literal.EXCEPTION, e);
				showMessage(e);
			}
			hashMap.put(custCardMonthSalesData.getMonth(), custCardMonthSalesData);
		}

		//
		for (CustCardSalesDetails detail : custCardMonthSales) {
			if (!hashMap.containsKey(detail.getMonth())) {
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

		setCustCardMonthSales(custCardMonthSales);
		logger.debug(Literal.LEAVING);
		return true;
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		boolean focus = false;
		if (wve.size() > 0) {
			setCustCardMonthSales(custCardSales.getCustCardMonthSales());
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

	private AuditHeader newCardSaleInfoDetailProcess(CustCardSalesDetails detail, List<CustCardSalesDetails> infoList,
			String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = new AuditHeader();
		String[] valueParm = new String[1];
		String[] errParm = new String[1];
		valueParm[0] = String.valueOf(DateUtil.format(detail.getMonth(), PennantConstants.monthYearFormat));
		errParm[0] = "Monthyear" + ":" + valueParm[0];

		custCardMonthSales = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(infoList)) {
			for (int i = 0; i < infoList.size(); i++) {
				if (DateUtil.compare(detail.getMonth(), infoList.get(i).getMonth()) == 0) {
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
							this.custCardMonthSales.add(detail);
						} else if (detail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.custCardMonthSales.add(detail);
						} else if (detail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < custCardSales.getCustCardMonthSales().size(); j++) {
								CustCardSalesDetails infoDetail = custCardSales.getCustCardMonthSales().get(j);
								if (infoDetail.getMonth().equals(detail.getMonth())) {
									this.custCardMonthSales.add(infoDetail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.custCardMonthSales.add(infoList.get(i));
							if (detail.getRecordType().equals(PennantConstants.RCD_ADD)) {
								recordAdded = true;
							}
						}
					}
				} else {
					this.custCardMonthSales.add(infoList.get(i));
				}
			}
		}

		if (!recordAdded) {
			this.custCardMonthSales.add(detail);
		}
		return auditHeader;
	}

	@SuppressWarnings({ "deprecation" })
	private void getCompValuetoBean(Listitem listitem, String comonentId) {
		CustCardSalesDetails custCardMonthSales = null;

		custCardMonthSales = (CustCardSalesDetails) listitem.getAttribute("data");
		switch (comonentId) {
		case "month":

			Hbox hbox1 = (Hbox) getComponent(listitem, "month");
			Datebox monthYear = (Datebox) hbox1.getLastChild();
			Clients.clearWrongValue(monthYear);
			Date monthYearValue = monthYear.getValue();
			if (!monthYear.isDisabled()) {
				if (monthYearValue == null) {
					/*
					 * throw new WrongValueException(monthYear, Labels.getLabel("FIELD_IS_MAND", new String[] { "Month"
					 * }));
					 */
				} else {
					monthYearValue.setDate(1);
					if (DateUtil.compare(monthYearValue, SysParamUtil.getAppDate()) == 1) {
						throw new WrongValueException(monthYear,
								Labels.getLabel("DATE_NO_FUTURE", new String[] { "Month" }));
					}
				}
			}
			if (monthYearValue == null) {
				custCardMonthSales.setMonth(null);
			} else {
				monthYearValue.setDate(1);
				custCardMonthSales.setMonth(monthYearValue);
			}
			break;
		case "saleAmount":
			BigDecimal saleAmount = BigDecimal.ZERO;
			Hbox hbox2 = (Hbox) getComponent(listitem, "saleAmount");
			CurrencyBox saleAmtValue = (CurrencyBox) hbox2.getLastChild();
			Clients.clearWrongValue(saleAmtValue);
			if (saleAmtValue.getValidateValue() != null) {
				saleAmount = saleAmtValue.getValidateValue();
			}
			if (!(saleAmtValue.isReadonly()) && (saleAmount.intValue() <= 0)) {
				throw new WrongValueException(saleAmtValue,
						Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Sale Amount" }));
			}
			custCardMonthSales.setSalesAmount(CurrencyUtil.unFormat(saleAmount, 2));
			break;
		case "NoOfSettlements":
			Hbox hbox3 = (Hbox) getComponent(listitem, "NoOfSettlements");
			Intbox noOfSettlements = (Intbox) hbox3.getLastChild();
			Clients.clearWrongValue(noOfSettlements);
			/*
			 * if (!noOfSettlements.isReadonly() && noOfSettlements.getValue() == null) { throw new
			 * WrongValueException(noOfSettlements, Labels.getLabel("FIELD_IS_MAND", new String[] { "No Of Settlements"
			 * })); } else if (!noOfSettlements.isReadonly() && noOfSettlements.getValue() <= 0) { throw new
			 * WrongValueException(noOfSettlements, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
			 * "No Of Settlements" })); }
			 */
			custCardMonthSales.setNoOfSettlements(noOfSettlements.getValue());
			break;
		case "totalNoOfCredits":
			Hbox hbox4 = (Hbox) getComponent(listitem, "totalNoOfCredits");
			Intbox totalNoOfCredits = (Intbox) hbox4.getLastChild();
			Clients.clearWrongValue(totalNoOfCredits);
			/*
			 * if (!totalNoOfCredits.isReadonly() && totalNoOfCredits.getValue() == null) { throw new
			 * WrongValueException(totalNoOfCredits, Labels.getLabel("FIELD_IS_MAND", new String[] {
			 * "Total No Of Credits" })); } else if (!totalNoOfCredits.isReadonly() && totalNoOfCredits.getValue() <= 0)
			 * { throw new WrongValueException(totalNoOfCredits, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new
			 * String[] { "Total No Of Credits" })); }
			 */
			custCardMonthSales.setTotalNoOfCredits(totalNoOfCredits.getValue());
			break;
		case "totalNoOfDebits":
			Hbox hbox5 = (Hbox) getComponent(listitem, "totalNoOfDebits");
			Intbox totalNoOfDebits = (Intbox) hbox5.getLastChild();
			Clients.clearWrongValue(totalNoOfDebits);
			/*
			 * if (!totalNoOfDebits.isReadonly() && totalNoOfDebits.getValue() == null) { throw new
			 * WrongValueException(totalNoOfDebits, Labels.getLabel("FIELD_IS_MAND", new String[] { "Total No Of Debits"
			 * })); } else if (!totalNoOfDebits.isReadonly() && totalNoOfDebits.getValue() <= 0) { throw new
			 * WrongValueException(totalNoOfDebits, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
			 * "Total No Of Debits" })); }
			 */
			custCardMonthSales.setTotalNoOfDebits(totalNoOfDebits.getValue());
			break;
		case "totalCreditValue":
			BigDecimal totalCreditValue = BigDecimal.ZERO;
			Hbox hbox6 = (Hbox) getComponent(listitem, "totalCreditValue");
			CurrencyBox totalCreditAmtValue = (CurrencyBox) hbox6.getLastChild();
			Clients.clearWrongValue(totalCreditAmtValue);
			if (totalCreditAmtValue.getValidateValue() != null) {
				totalCreditValue = totalCreditAmtValue.getValidateValue();
			}
			/*
			 * if (!(totalCreditAmtValue.isReadonly()) && (totalCreditValue.intValue() <= 0)) { throw new
			 * WrongValueException(totalCreditAmtValue, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
			 * "Total Credit Value" })); }
			 */
			custCardMonthSales.setTotalCreditValue(CurrencyUtil.unFormat(totalCreditValue, 2));
			break;
		case "totalDebitValue":
			BigDecimal totalDebitValue = BigDecimal.ZERO;
			Hbox hbox7 = (Hbox) getComponent(listitem, "totalDebitValue");
			CurrencyBox totalDebitAmtValue = (CurrencyBox) hbox7.getLastChild();
			Clients.clearWrongValue(totalDebitAmtValue);
			if (totalDebitAmtValue.getValidateValue() != null) {
				totalDebitValue = totalDebitAmtValue.getValidateValue();
			}
			/*
			 * if (!(totalDebitAmtValue.isReadonly()) && (totalDebitValue.intValue() <= 0)) { throw new
			 * WrongValueException(totalDebitAmtValue, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
			 * "Total Debit Value" })); }
			 */
			custCardMonthSales.setTotalDebitValue(CurrencyUtil.unFormat(totalDebitValue, 2));
			break;
		case "inwardBounce":
			BigDecimal inwardBounce = BigDecimal.ZERO;
			Hbox hbox8 = (Hbox) getComponent(listitem, "inwardBounce");
			CurrencyBox bounceIn = (CurrencyBox) hbox8.getLastChild();
			Clients.clearWrongValue(bounceIn);
			if (bounceIn.getValidateValue() != null) {
				inwardBounce = bounceIn.getValidateValue();
			}
			/*
			 * if (!(bounceIn.isReadonly()) && (inwardBounce.intValue() <= 0)) { throw new WrongValueException(bounceIn,
			 * Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { "Inward Bounce" })); }
			 */
			custCardMonthSales.setInwardBounce(CurrencyUtil.unFormat(inwardBounce, 2));
			break;
		case "outwardBounce":
			BigDecimal bounceInOut = BigDecimal.ZERO;
			Hbox hbox9 = (Hbox) getComponent(listitem, "outwardBounce");
			CurrencyBox bounceOut = (CurrencyBox) hbox9.getLastChild();
			Clients.clearWrongValue(bounceOut);
			if (bounceOut.getValidateValue() != null) {
				bounceInOut = bounceOut.getValidateValue();
			}
			/*
			 * if (!(bounceOut.isReadonly()) && (bounceInOut.intValue() <= 0)) { throw new
			 * WrongValueException(bounceOut, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
			 * "Outward Bounce" })); }
			 */
			custCardMonthSales.setOutwardBounce(CurrencyUtil.unFormat(bounceInOut, 2));
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

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustCardSales
	 */
	public void doShowDialog(CustCardSales aCustCardSales) {
		logger.debug(Literal.ENTERING);

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.merchantId.focus();
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

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustCardSales);

			doCheckEnquiry();
			if (isNewCustomer()) {
				this.window_CustomerCardSalesInfoDialog.setHeight("90%");
				this.window_CustomerCardSalesInfoDialog.setWidth("90%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerCardSalesInfoDialog.doModal();
			} else {
				this.window_CustomerCardSalesInfoDialog.setWidth("100%");
				this.window_CustomerCardSalesInfoDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerCardSalesInfoDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custID.setReadonly(true);
			this.custCIF.setReadonly(true);
			this.merchantId.setReadonly(true);

			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		if (!this.merchantId.isReadonly()) {
			if (!this.merchantId.isReadonly()) {
				this.merchantId.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerCardSalesInfoDialog_MerchantId.value"), null, true));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);
		this.merchantId.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.merchantId.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustCardSales aCustCardSales = new CustCardSales();
		BeanUtils.copyProperties(getCustCardSales(), aCustCardSales);

		final String keyReference = Labels.getLabel("label_CustomerBankInfoDialog_BankName.value") + " : "
				+ aCustCardSales.getMerchantId();

		doDelete(keyReference, aCustCardSales);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final CustCardSales aCustCardSales) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(aCustCardSales.getRecordType())) {
			aCustCardSales.setVersion(aCustCardSales.getVersion() + 1);
			aCustCardSales.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
				aCustCardSales.setNewRecord(true);
			}
			if (isWorkFlowEnabled()) {
				aCustCardSales.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newCustomerCardSaleProcess(aCustCardSales, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerCardSalesInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getCustomerDialogCtrl().doFillCustomerCardSalesInfoDetails(this.CustCardSalesList);
				closeDialog();
			}
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (isNewRecord()) {
			if (isNewCustomer()) {
				this.btnCancel.setVisible(false);
				this.btnSearchPRCustid.setVisible(false);
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.merchantId.setReadonly(isReadOnly("CustomerCardSalesInfo_MerchantName"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.merchantId.setReadonly(true);
		}

		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.toolBar_cardMonthSales.setVisible(true);
		this.listBoxCardMonthSales.setVisible(true);
		this.button_CustomerCardSalesInfoDialog_btnCardMonthSales
				.setVisible(!isReadOnly("CustomerCardSalesInfo_NewCardMnthDetails"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.custCardSales.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.merchantId.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.merchantId.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final CustCardSales aCustCardSales = new CustCardSales();
		BeanUtils.copyProperties(getCustCardSales(), aCustCardSales);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();
		// fill the CustomerBankInfo object with the components data
		doWriteComponentsToBean(aCustCardSales);

		if (!saveCardSalesDetailInfoList(aCustCardSales)) {
			return;
		}

		custCardSales.setCustCardMonthSales(getCustCardMonthSales());
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustCardSales.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustCardSales.getRecordType())) {
				aCustCardSales.setVersion(aCustCardSales.getVersion() + 1);
				if (isNew) {
					aCustCardSales.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustCardSales.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustCardSales.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustCardSales.setVersion(1);
					aCustCardSales.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aCustCardSales.getRecordType())) {
					aCustCardSales.setVersion(aCustCardSales.getVersion() + 1);
					aCustCardSales.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustCardSales.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustCardSales.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCustCardSales.setVersion(aCustCardSales.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newCustomerCardSaleProcess(aCustCardSales, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerCardSalesInfoDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getCustomerDialogCtrl().doFillCustomerCardSalesInfoDetails(this.CustCardSalesList);
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCustomerCardSaleProcess(CustCardSales aCustCardSales, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustCardSales, tranType);
		CustCardSalesList = new ArrayList<CustCardSales>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustCardSales.getLovDescCustCIF());
		valueParm[1] = aCustCardSales.getMerchantId();

		errParm[0] = PennantJavaUtil.getLabel("label_CustomerBankInfoDialog_CustID.value") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustomerCardSalesInfoDialog_MerchantId.label") + ":"
				+ valueParm[1];

		if (getCustomerDialogCtrl().getCustomerCardSales() != null
				&& getCustomerDialogCtrl().getCustomerCardSales().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerCardSales().size(); i++) {
				CustCardSales custCardSales = getCustomerDialogCtrl().getCustomerCardSales().get(i);

				if (aCustCardSales.getMerchantId().equals(custCardSales.getMerchantId())) {
					// Both Current and Existing list rating same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustCardSales.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCustCardSales.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							CustCardSalesList.add(aCustCardSales);
						} else if (aCustCardSales.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustCardSales.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustCardSales.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							CustCardSalesList.add(aCustCardSales);
						} else if (aCustCardSales.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustCardSales()
									.size(); j++) {
								CustCardSales email = getCustomerDialogCtrl().getCustomerDetails().getCustCardSales()
										.get(j);
								if (email.getCustID() == aCustCardSales.getCustID()
										&& email.getMerchantId().equals(aCustCardSales.getMerchantId())) {
									CustCardSalesList.add(email);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							CustCardSalesList.add(custCardSales);
						}
					}
				} else {
					CustCardSalesList.add(custCardSales);
				}
			}
		}

		if (!recordAdded) {
			CustCardSalesList.add(aCustCardSales);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	// Search Button Component Events

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustCardSales
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustCardSales aCustCardSales, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustCardSales.getBefImage(), aCustCardSales);
		return new AuditHeader(getReference(), String.valueOf(aCustCardSales.getCustID()), null, null, auditDetail,
				aCustCardSales.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerCardSalesInfoDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.custCardSales);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustCardSales().getCustID() + PennantConstants.KEY_SEPERATOR + getCustCardSales().getMerchantId();
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

	public CustCardSales getCustCardSales() {
		return custCardSales;
	}

	public void setCustCardSales(CustCardSales custCardSales) {
		this.custCardSales = custCardSales;
	}

	public List<CustCardSales> getCustCardSalesList() {
		return CustCardSalesList;
	}

	public void setCustCardSalesList(List<CustCardSales> custCardSalesList) {
		CustCardSalesList = custCardSalesList;
	}

	public List<CustCardSalesDetails> getCustCardMonthSales() {
		return custCardMonthSales;
	}

	public void setCustCardMonthSales(List<CustCardSalesDetails> custCardMonthSales) {
		this.custCardMonthSales = custCardMonthSales;
	}
}
