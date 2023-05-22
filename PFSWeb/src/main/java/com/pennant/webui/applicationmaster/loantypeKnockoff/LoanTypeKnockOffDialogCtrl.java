package com.pennant.webui.applicationmaster.loantypeKnockoff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennant.backend.service.applicationmaster.LoanTypeKnockOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanTypeKnockOffDialogCtrl extends GFCBaseCtrl<FinTypeKnockOff> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LoanTypeKnockOffDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LoanTypeKnockOffDialog;

	protected Space space_KnockOffCode;
	protected Textbox knockOffCode;
	protected Textbox description;
	protected Checkbox active;
	protected Button btnNew_CodeMapping;
	protected Listbox listBoxKnockOffCode;
	protected ExtendedCombobox loanType;
	private transient boolean isEditable = false;
	private LoanTypeKnockOffListCtrl loanTypeKnockOffListCtrl;
	private FinTypeKnockOff finTypeKnockOff;
	private transient LoanTypeKnockOffService loanTypeKnockOffService;
	private List<FinTypeKnockOff> codeMappingList = new ArrayList<FinTypeKnockOff>();
	boolean isAutoKnockOffNew = true;
	protected Listbox autoKnockOffRows;
	private String filterKnockOffCode = "";
	boolean isNewAutoKnockOffMap = false;
	private List<FinTypeKnockOff> deleteFinTypeKnockOffList = new ArrayList<>();

	public LoanTypeKnockOffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanTypeKnockOffDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finTypeKnockOff.getId());
	}

	public void onCreate$window_LoanTypeKnockOffDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_LoanTypeKnockOffDialog);

		try {
			this.finTypeKnockOff = (FinTypeKnockOff) arguments.get("loanTypeKnockOff");
			this.loanTypeKnockOffListCtrl = (LoanTypeKnockOffListCtrl) arguments.get("loanTypeKnockOffListCtrl");

			if (this.finTypeKnockOff == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			FinTypeKnockOff knockOff = new FinTypeKnockOff();
			BeanUtils.copyProperties(this.finTypeKnockOff, knockOff);
			this.finTypeKnockOff.setBefImage(knockOff);

			doLoadWorkFlow(this.finTypeKnockOff.isWorkflow(), this.finTypeKnockOff.getWorkflowId(),
					this.finTypeKnockOff.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.finTypeKnockOff);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.loanType.setMaxlength(8);
		this.loanType.setMandatoryStyle(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew_CodeMapping.setVisible(getUserWorkspace().isAllowed("button_LoanTypeKnockOffDialog_btnNewCode"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LoanTypeKnockOffDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LoanTypeKnockOffDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LoanTypeKnockOffDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LoanTypeKnockOffDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.finTypeKnockOff);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew_CodeMapping(Event event) {
		logger.debug("Entering");

		FinTypeKnockOff autoKnockOff = new FinTypeKnockOff();
		isAutoKnockOffNew = true;
		autoKnockOff.setNewRecord(true);
		if (StringUtils.isBlank(autoKnockOff.getRecordType())) {
			autoKnockOff.setVersion(1);
			autoKnockOff.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		int keyValue = 0;
		for (Listitem component : autoKnockOffRows.getItems()) {

			FinTypeKnockOff fintypeKnockOff = (FinTypeKnockOff) component.getAttribute("data");

			if (fintypeKnockOff != null && fintypeKnockOff.getKeyvalue() > keyValue) {
				keyValue = fintypeKnockOff.getKeyvalue();
			}
		}

		autoKnockOff.setKeyvalue(keyValue + 1);
		appendAutoKnockOff(autoKnockOff);
		logger.debug("Leaving");
	}

	private void appendAutoKnockOff(FinTypeKnockOff autoKnockOff) {
		Listitem listitem = new Listitem();
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("button_LoanTypeKnockOffDialog_btnNewCode");

		listitem.setAttribute("knockOffCodeMapping", autoKnockOff);
		Listcell listcell;

		hbox = new Hbox();
		ExtendedCombobox knockOffCode = new ExtendedCombobox();
		knockOffCode.setWidth("100px");
		knockOffCode.setModuleName("AutoKnockOffData");
		knockOffCode.setValueColumn("Code");
		knockOffCode.setDescColumn("Description");
		knockOffCode.setWhereClause(" Active = 1");

		knockOffCode.setValidateColumns(new String[] { "Code" });
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(knockOffCode);
		knockOffCode.addForward("onFulfill", window_LoanTypeKnockOffDialog, "onChangeKnockOffCode", listitem);
		knockOffCode.setId("knockOffCode".concat(String.valueOf(autoKnockOff.getKeyvalue())));
		if (!"".equals(this.filterKnockOffCode)) {
			String[] code = filterKnockOffCode.split(",");
			Filter[] codeFilter = new Filter[1];
			codeFilter[0] = Filter.notIn("Code", Arrays.asList(code));
			knockOffCode.setFilters(codeFilter);
		}
		readOnlyComponent(isReadOnly, knockOffCode);

		listcell = new Listcell();
		listcell.appendChild(hbox);
		listcell.setParent(listitem);
		if (autoKnockOff.getKnockOffId() != Long.MIN_VALUE && autoKnockOff.getKnockOffId() != 0) {
			Search search = new Search(AutoKnockOff.class);
			search.addFilterEqual("Id", autoKnockOff.getKnockOffId());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			AutoKnockOff autoKnockOffData = (AutoKnockOff) searchProcessor.getResults(search).get(0);

			if (autoKnockOffData != null) {
				knockOffCode.setValue(autoKnockOffData.getCode());
				knockOffCode.setDescription(autoKnockOffData.getDescription());
				knockOffCode.setObject(autoKnockOffData);
				if (this.filterKnockOffCode.equals("")) {
					this.filterKnockOffCode = autoKnockOffData.getCode();
				} else {
					this.filterKnockOffCode = this.filterKnockOffCode + "," + autoKnockOffData.getCode();
				}
			}
		}

		Intbox knockOffOrder = new Intbox();
		knockOffOrder.setWidth("80px");
		if (autoKnockOff.getKnockOffOrder() != 0) {
			knockOffOrder.setValue(autoKnockOff.getKnockOffOrder());
		} else {
			knockOffOrder.setValue(0);
		}
		knockOffOrder.addForward("onChange", window_LoanTypeKnockOffDialog, "onChangeKnockOffOrder", listitem);
		knockOffOrder.setMaxlength(3);
		knockOffOrder.setId("knockOffOrder".concat(String.valueOf(autoKnockOff.getKeyvalue())));
		readOnlyComponent(isReadOnly, knockOffOrder);
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(knockOffOrder);
		listcell = new Listcell();
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		listcell = new Listcell();
		Textbox recordStatus = new Textbox();
		recordStatus.setWidth("100px");
		recordStatus.setValue(autoKnockOff.getRecordStatus());
		listcell = new Listcell();
		recordStatus.setDisabled(true);
		listcell.appendChild(recordStatus);
		listcell.setParent(listitem);

		listcell = new Listcell();
		Textbox recordType = new Textbox();
		recordType.setWidth("100px");
		recordType.setValue(autoKnockOff.getRecordType());
		listcell = new Listcell();
		recordType.setDisabled(true);
		listcell.appendChild(recordType);
		listcell.setParent(listitem);

		listcell = new Listcell();

		Button deleteButton = new Button();

		deleteButton.setLabel(Labels.getLabel("label_Finoption_Delete.value"));
		deleteButton.addForward("onClick", window_LoanTypeKnockOffDialog, "onButtonClick", listitem);
		deleteButton.setSclass("z-toolbarbutton");
		listcell.appendChild(deleteButton);
		listcell.setParent(listitem);
		readOnlyComponent(isReadOnly, deleteButton);

		listcell = new Listcell();
		Longbox id = new Longbox();
		id.setWidth("80px");
		if (autoKnockOff.getKnockOffOrder() != 0) {
			id.setValue(autoKnockOff.getId());
		} else {
			id.setValue(null);
		}
		id.setVisible(false);
		listcell = new Listcell();
		listcell.appendChild(id);
		listcell.setParent(listitem);

		this.recordStatus.setValue(autoKnockOff.getRecordStatus());
		listitem.setAttribute("data", autoKnockOff);

		this.autoKnockOffRows.appendChild(listitem);
	}

	public void onButtonClick(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();

		FinTypeKnockOff knockOffMapping = (FinTypeKnockOff) listitem.getAttribute("knockOffCodeMapping");

		if (PennantConstants.RECORD_TYPE_NEW.equals(knockOffMapping.getRecordType())) {
			knockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_CAN);
		} else {
			knockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			knockOffMapping.setNewRecord(true);
		}

		deleteFinTypeKnockOffList.add(knockOffMapping);

		autoKnockOffRows.removeChild(listitem);

	}

	public void onChangeKnockOffCode(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Hbox hbox = (Hbox) getComponent(listitem, 1);
		ExtendedCombobox knockOffCode = (ExtendedCombobox) hbox.getLastChild();
		String code = knockOffCode.getValue();
		if (this.filterKnockOffCode.equals("")) {
			this.filterKnockOffCode = code;
		} else {
			this.filterKnockOffCode = this.filterKnockOffCode + "," + code;
		}

	}

	private Component getComponent(Listitem row, int index) {
		int i = 1;
		for (Component component : row.getChildren()) {
			if (i == index) {
				return component.getFirstChild();
			}
			i++;
		}
		return null;
	}

	private void getCompValuetoBean(Listitem listItem, int index) {
		int i = 1;
		Hbox hbox = null;
		for (Component component : listItem.getChildren()) {
			if (i == index) {
				hbox = (Hbox) component.getFirstChild();
			}
			i++;
		}
		String id = StringUtils.trimToNull(hbox.getLastChild().getId());
		if (id == null) {
			return;
		}
		id = id.replaceAll("\\d", "");

		switch (id) {
		case "knockOffCode":
			ExtendedCombobox knockOffCode = (ExtendedCombobox) hbox.getLastChild();
			Clients.clearWrongValue(knockOffCode);
			String code = knockOffCode.getValue();
			if (StringUtils.isEmpty(code)) {
				throw new WrongValueException(knockOffCode,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "KnockOffCode " }));
			}
			break;
		case "knockOffOrder":
			Intbox order = (Intbox) hbox.getLastChild();
			String orderNum = order.getText();

			if (StringUtils.isEmpty(orderNum) || orderNum.equals("0")) {
				throw new WrongValueException(order, Labels.getLabel("FIELD_IS_MAND", new String[] { "Fees Code " }));
			}
			break;
		default:
			break;
		}
	}

	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		loanTypeKnockOffListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finTypeKnockOff.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(FinTypeKnockOff knockOff) {
		logger.debug(Literal.ENTERING);

		this.loanType.setValue(knockOff.getLoanType(), knockOff.getFinTypeDesc());

		this.recordStatus.setValue(knockOff.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVoucherVendor
	 */
	public void doWriteComponentsToBean(FinTypeKnockOff aknockOff) {
		logger.debug(Literal.ENTERING);

		List<FinTypeKnockOff> lonTypeKnockOffKodeMapping = new ArrayList<>();
		List<Integer> knockOrder = new ArrayList<>();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		List<String> knockOffCodes = new ArrayList<>();

		for (Listitem component : autoKnockOffRows.getItems()) {
			Listitem listitem = (Listitem) component;
			FinTypeKnockOff knockOffMapping = (FinTypeKnockOff) listitem.getAttribute("data");

			try {
				Hbox hbox = (Hbox) getComponent(listitem, 1);
				getCompValuetoBean(listitem, 1);
				ExtendedCombobox knockOffCode = (ExtendedCombobox) hbox.getLastChild();
				AutoKnockOff object = (AutoKnockOff) knockOffCode.getObject();

				if (knockOffCodes.contains(object.getCode())) {
					MessageUtil.showError(
							"Duplicate knock off codes are not allowed for same LOAN type " + object.getCode());
					wve.add(new WrongValueException(object.getCode()));
				}

				knockOffCodes.add(object.getCode());

				knockOffMapping.setKnockOffId(object.getId());
				knockOffMapping.setKnockOffCode(object.getCode());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Hbox hbox = (Hbox) getComponent(listitem, 2);
				getCompValuetoBean(listitem, 2);
				Intbox order = (Intbox) hbox.getLastChild();
				if (knockOrder.contains(order.getValue()) && !knockOffMapping.getRecordType().equals("DELETE")) {
					MessageUtil.showError(
							"Duplicate knock off order are not allowed for same LOAN type " + order.getValue());
					wve.add(new WrongValueException(order.getValue()));
				}
				knockOrder.add(order.getValue());
				knockOffMapping.setKnockOffOrder(order.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Longbox id = (Longbox) getComponent(listitem, 6);
				if (id.getValue() != null) {
					knockOffMapping.setId(id.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			boolean isNew = knockOffMapping.isNewRecord();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(knockOffMapping.getRecordType())) {
					knockOffMapping.setVersion(knockOffMapping.getVersion() + 1);
					if (isNew) {
						knockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						knockOffMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						knockOffMapping.setNewRecord(true);
					}
				}
			} else {
				if (isNewAutoKnockOffMap) {
					if (knockOffMapping.isNewRecord()) {
						knockOffMapping.setVersion(1);
						knockOffMapping.setRecordType(PennantConstants.RCD_ADD);
					} else {
					}

					if (StringUtils.isBlank(knockOffMapping.getRecordType())) {
						knockOffMapping.setVersion(knockOffMapping.getVersion() + 1);
						knockOffMapping.setRecordType(PennantConstants.RCD_UPD);
					}

					if (knockOffMapping.getRecordType().equals(PennantConstants.RCD_ADD)
							&& knockOffMapping.isNewRecord()) {
					} else if (knockOffMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						knockOffMapping.setVersion(knockOffMapping.getVersion() + 1);
					}
				} else {
					knockOffMapping.setVersion(knockOffMapping.getVersion() + 1);
					if (isNew) {
					} else {
					}
				}
			}

			knockOffMapping.setLoanType(this.loanType.getValue());

			knockOffMapping.setRecordStatus(this.recordStatus.getValue());

			lonTypeKnockOffKodeMapping.add(knockOffMapping);
		}

		lonTypeKnockOffKodeMapping.addAll(deleteFinTypeKnockOffList);
		aknockOff.setLoanTypeKonckOffMapping(lonTypeKnockOffKodeMapping);
		this.finTypeKnockOff.setLoanTypeKonckOffMapping(lonTypeKnockOffKodeMapping);

		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param voucherVendor The entity that need to be render.
	 */
	public void doShowDialog(FinTypeKnockOff knockOff) {
		logger.debug(Literal.ENTERING);

		if (knockOff.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.loanType.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(knockOff.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(knockOff);

		int keyValue = 0;
		for (FinTypeKnockOff finTypeAutoKnockOff : knockOff.getLoanTypeKonckOffMapping()) {
			keyValue = keyValue + 1;
			finTypeAutoKnockOff.setKeyvalue(keyValue);
			appendAutoKnockOff(finTypeAutoKnockOff);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.loanType.isReadonly()) {
			this.loanType
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LoanTypeKnockOffDialog_LoanType.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.loanType.setConstraint("");

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
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.loanType.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void onLoanTypeMappingItemDoubleClicked(Event event) {
		logger.debug("Entering " + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxKnockOffCode.getSelectedItem();

		if (item != null) {
			final FinTypeKnockOff codeMapping = (FinTypeKnockOff) item.getAttribute("data");

			if (codeMapping.getRecordType() != null
					&& (codeMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| (codeMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)))) {
				MessageUtil.showError(Labels.getLabel("RECORD_NO_MAINTAIN"));
			} else {
				codeMapping.setNewRecord(false);
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("loanTypeKnockOffDialogCtrl", this);
				map.put("loanTypeKnockOff", getLoanTypeKnockOff());
				map.put("loanTypeCodeMapping", codeMapping);
				map.put("roleCode", getRole());
				map.put("isEditable", isEditable);
				map.put("isNewRecord", getLoanTypeKnockOff().isNewRecord());

				try {
					Executions.createComponents(
							"/WEB-INF/pages/ApplicationMaster/LoanTypeKnockOff/LoanTypeKnockOffCodeMapping.zul", null,
							map);

				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinTypeKnockOff aknockOff = new FinTypeKnockOff();
		BeanUtils.copyProperties(getLoanTypeKnockOff(), aknockOff);

		doDelete(String.valueOf(aknockOff.getId()), aknockOff);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.finTypeKnockOff.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(true, this.loanType);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.loanType);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypeKnockOff.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.loanType);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
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

		this.loanType.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final FinTypeKnockOff knockOff = new FinTypeKnockOff();
		BeanUtils.copyProperties(getLoanTypeKnockOff(), knockOff);
		boolean isNew = false;

		doSetValidation();

		if (autoKnockOffRows.getItems().isEmpty()) {
			MessageUtil.showMessage("Please add atleast one knock off details.");
			return;
		}

		doWriteComponentsToBean(knockOff);

		isNew = knockOff.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(knockOff.getRecordType())) {
				knockOff.setVersion(knockOff.getVersion() + 1);
				if (isNew) {
					knockOff.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					knockOff.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					knockOff.setNewRecord(true);
				}
			}
		} else {
			knockOff.setVersion(knockOff.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(knockOff, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinTypeKnockOff aKnockOff, String tranType) {
		logger.debug(Literal.ENTERING);

		List<FinTypeKnockOff> knockOffMapping = aKnockOff.getLoanTypeKonckOffMapping();
		List<FinTypeKnockOff> tempKnockOffMapping = new ArrayList<>();

		for (FinTypeKnockOff KM : knockOffMapping) {
			if (KM.getRecordType() != null && PennantConstants.RECORD_TYPE_CAN.equals(KM.getRecordType())) {
				tempKnockOffMapping.add(KM);
			}
		}

		knockOffMapping.removeAll(tempKnockOffMapping);
		aKnockOff.setLoanTypeKonckOffMapping(knockOffMapping);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aKnockOff.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aKnockOff.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aKnockOff.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aKnockOff.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aKnockOff.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aKnockOff);
				}

				if (isNotesMandatory(taskId, aKnockOff)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aKnockOff.setTaskId(taskId);
			aKnockOff.setNextTaskId(nextTaskId);
			aKnockOff.setRoleCode(getRole());
			aKnockOff.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aKnockOff, tranType);
			String operationRefs = getServiceOperations(taskId, aKnockOff);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aKnockOff, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aKnockOff, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinTypeKnockOff aKnock = (FinTypeKnockOff) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = loanTypeKnockOffService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = loanTypeKnockOffService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = loanTypeKnockOffService.doApprove(auditHeader);

					if (aKnock.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = loanTypeKnockOffService.doReject(auditHeader);
					if (aKnock.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LoanTypeKnockOffDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_LoanTypeKnockOffDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finTypeKnockOff), true);
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

	private AuditHeader getAuditHeader(FinTypeKnockOff aKnockOff, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aKnockOff.getBefImage(), aKnockOff);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aKnockOff.getUserDetails(),
				getOverideMap());
	}

	public LoanTypeKnockOffListCtrl getLoanTypeKnockOffListCtrl() {
		return loanTypeKnockOffListCtrl;
	}

	public void setLoanTypeKnockOffListCtrl(LoanTypeKnockOffListCtrl loanTypeKnockOffListCtrl) {
		this.loanTypeKnockOffListCtrl = loanTypeKnockOffListCtrl;
	}

	public FinTypeKnockOff getLoanTypeKnockOff() {
		return finTypeKnockOff;
	}

	public void setLoanTypeKnockOff(FinTypeKnockOff loanTypeKnockOff) {
		this.finTypeKnockOff = loanTypeKnockOff;
	}

	public List<FinTypeKnockOff> getCodeMappingList() {
		return codeMappingList;
	}

	public void setCodeMappingList(List<FinTypeKnockOff> codeMappingList) {
		this.codeMappingList = codeMappingList;
	}

	public LoanTypeKnockOffService getLoanTypeKnockOffService() {
		return loanTypeKnockOffService;
	}

	public void setLoanTypeKnockOffService(LoanTypeKnockOffService loanTypeKnockOffService) {
		this.loanTypeKnockOffService = loanTypeKnockOffService;
	}

	public boolean isAutoKnockOffNew() {
		return isAutoKnockOffNew;
	}

	public void setAutoKnockOffNew(boolean isAutoKnockOffNew) {
		this.isAutoKnockOffNew = isAutoKnockOffNew;
	}

}
