package com.pennant.webui.applicationmaster.loantypewriteoff;

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
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennant.backend.model.finance.psl.PSLCategory;
import com.pennant.backend.service.applicationmaster.LoanTypeWriteOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanTypeWriteOffDialogCtrl extends GFCBaseCtrl<FinTypeWriteOff> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LoanTypeWriteOffDialogCtrl.class);

	protected Window windowLoanTypeWriteOffDialog;
	protected Button btnNewCodeMapping;
	protected Listbox listBoxWriteOffCode;
	protected ExtendedCombobox loanType;
	private transient boolean isEditable = false;
	private LoanTypeWriteOffListCtrl loanTypeWriteOffListCtrl;
	private FinTypeWriteOff finTypeWriteOff;
	private transient LoanTypeWriteOffService loanTypeWriteOffService;
	private List<FinTypeWriteOff> codeMappingList = new ArrayList<>();
	boolean isAutoWriteOffNew = true;
	protected Listbox autoWriteOffRows;
	private String filterWriteOffCode = "";
	boolean isNewAutoWriteOffMap = false;
	private List<FinTypeWriteOff> deleteFinTypeWriteOffList = new ArrayList<>();

	public LoanTypeWriteOffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanTypeWriteOffDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finTypeWriteOff.getId());
	}

	public void onCreate$windowLoanTypeWriteOffDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowLoanTypeWriteOffDialog);

		try {
			this.finTypeWriteOff = (FinTypeWriteOff) arguments.get("loanTypeWriteOff");
			this.loanTypeWriteOffListCtrl = (LoanTypeWriteOffListCtrl) arguments.get("loanTypeWriteOffListCtrl");

			if (this.finTypeWriteOff == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			FinTypeWriteOff writeOff = new FinTypeWriteOff();
			BeanUtils.copyProperties(this.finTypeWriteOff, writeOff);
			this.finTypeWriteOff.setBefImage(writeOff);

			doLoadWorkFlow(this.finTypeWriteOff.isWorkflow(), this.finTypeWriteOff.getWorkflowId(),
					this.finTypeWriteOff.getNextTaskId());

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
			doShowDialog(this.finTypeWriteOff);
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

		this.btnNewCodeMapping.setVisible(getUserWorkspace().isAllowed("button_LoanTypeWriteOffDialog_btnPSLCode"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LoanTypeWriteOffDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LoanTypeWriteOffDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LoanTypeWriteOffDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LoanTypeWriteOffDialog_btnSave"));
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
	public void onClick$btnDelete(Event event) {
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
		doShowNotes(this.finTypeWriteOff);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNewCodeMapping(Event event) {
		logger.debug("Entering");

		FinTypeWriteOff autoWriteOff = new FinTypeWriteOff();

		isAutoWriteOffNew = true;
		autoWriteOff.setNewRecord(true);

		if (StringUtils.isBlank(autoWriteOff.getRecordType())) {
			autoWriteOff.setVersion(1);
			autoWriteOff.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		int keyValue = 0;
		for (Listitem component : autoWriteOffRows.getItems()) {

			FinTypeWriteOff fintypeWriteOff = (FinTypeWriteOff) component.getAttribute("data");

			if (fintypeWriteOff != null && fintypeWriteOff.getKeyvalue() > keyValue) {
				keyValue = fintypeWriteOff.getKeyvalue();
			}
		}

		autoWriteOff.setKeyvalue(keyValue + 1);

		appendAutoWriteOff(autoWriteOff);

		logger.debug(Literal.LEAVING);
	}

	private void appendAutoWriteOff(FinTypeWriteOff autoWriteOff) {
		Listitem listitem = new Listitem();
		boolean isReadOnly = isReadOnly("button_LoanTypeWriteOffDialog_btnPSLCode");

		listitem.setAttribute("writeOffCodeMapping", autoWriteOff);

		ExtendedCombobox writeOffCode = new ExtendedCombobox();
		writeOffCode.setWidth("100px");
		writeOffCode.setModuleName("PSLCategory");
		writeOffCode.setValueColumn("Code");
		writeOffCode.setDescColumn("Description");

		writeOffCode.setValidateColumns(new String[] { "Code" });

		Hbox hbox = new Hbox();
		Space space = new Space();

		space.setSpacing("2px");
		space.setSclass("mandatory");

		hbox.appendChild(space);
		hbox.appendChild(writeOffCode);

		writeOffCode.addForward("onFulfill", windowLoanTypeWriteOffDialog, "onChangeWriteOffCode", listitem);
		writeOffCode.setId("pslCode".concat(String.valueOf(autoWriteOff.getKeyvalue())));

		if (!"".equals(this.filterWriteOffCode)) {
			String[] code = filterWriteOffCode.split(",");
			Filter[] codeFilter = new Filter[1];
			codeFilter[0] = Filter.notIn("Code", Arrays.asList(code));
			writeOffCode.setFilters(codeFilter);
		}

		readOnlyComponent(isReadOnly, writeOffCode);

		Listcell listcell = new Listcell();
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		if (autoWriteOff.getPslCode() != null) {
			Search search = new Search(PSLCategory.class);
			search.addFilterEqual("Code", autoWriteOff.getPslCode());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			PSLCategory autoWriteOffData = (PSLCategory) searchProcessor.getResults(search).get(0);

			if (autoWriteOffData != null) {
				writeOffCode.setValue(autoWriteOffData.getCode());
				writeOffCode.setDescription(autoWriteOffData.getDescription());
				writeOffCode.setObject(autoWriteOffData);
				if (this.filterWriteOffCode.equals("")) {
					this.filterWriteOffCode = autoWriteOffData.getCode();
				} else {
					this.filterWriteOffCode = this.filterWriteOffCode + "," + autoWriteOffData.getCode();
				}
			}
		}

		Intbox writeOffOrder = new Intbox();
		writeOffOrder.setWidth("80px");
		if (autoWriteOff.getDpdDays() != 0) {
			writeOffOrder.setValue(autoWriteOff.getDpdDays());
		} else {
			writeOffOrder.setValue(0);
		}
		writeOffOrder.setMaxlength(3);
		writeOffOrder.setId("dpdDays".concat(String.valueOf(autoWriteOff.getKeyvalue())));
		readOnlyComponent(isReadOnly, writeOffOrder);
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(writeOffOrder);
		listcell = new Listcell();
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		Textbox recordStatus = new Textbox();
		recordStatus.setWidth("100px");
		recordStatus.setValue(autoWriteOff.getRecordStatus());
		listcell = new Listcell();
		recordStatus.setDisabled(true);
		listcell.appendChild(recordStatus);
		listcell.setParent(listitem);

		Textbox recordType = new Textbox();
		recordType.setWidth("100px");
		recordType.setValue(autoWriteOff.getRecordType());
		listcell = new Listcell();
		recordType.setDisabled(true);
		listcell.appendChild(recordType);
		listcell.setParent(listitem);

		listcell = new Listcell();

		Button deleteButton = new Button();
		deleteButton.setLabel(Labels.getLabel("label_Finoption_Delete.value"));
		deleteButton.addForward("onClick", windowLoanTypeWriteOffDialog, "onButtonClick", listitem);
		deleteButton.setSclass("z-toolbarbutton");
		listcell.appendChild(deleteButton);
		listcell.setParent(listitem);
		readOnlyComponent(isReadOnly, deleteButton);

		Longbox id = new Longbox();
		id.setWidth("80px");
		if (autoWriteOff.getDpdDays() != 0) {
			id.setValue(autoWriteOff.getId());
		} else {
			id.setValue(null);
		}
		id.setVisible(false);
		listcell = new Listcell();
		listcell.appendChild(id);
		listcell.setParent(listitem);

		this.recordStatus.setValue(autoWriteOff.getRecordStatus());
		listitem.setAttribute("data", autoWriteOff);

		this.autoWriteOffRows.appendChild(listitem);
	}

	public void onButtonClick(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();

		FinTypeWriteOff writeOffMapping = (FinTypeWriteOff) listitem.getAttribute("writeOffCodeMapping");

		if (PennantConstants.RECORD_TYPE_NEW.equals(writeOffMapping.getRecordType())) {
			writeOffMapping.setRecordType(PennantConstants.RECORD_TYPE_CAN);
		} else {
			writeOffMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			writeOffMapping.setNewRecord(true);
		}

		deleteFinTypeWriteOffList.add(writeOffMapping);

		autoWriteOffRows.removeChild(listitem);

	}

	public void onChangeWriteOffCode(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Hbox hbox = (Hbox) getComponent(listitem, 1);

		if (hbox != null) {
			ExtendedCombobox writeOffCode = (ExtendedCombobox) hbox.getLastChild();
			String code = writeOffCode.getValue();
			if (this.filterWriteOffCode.equals("")) {
				this.filterWriteOffCode = code;
			} else {
				this.filterWriteOffCode = this.filterWriteOffCode + "," + code;
			}
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

		if (hbox == null) {
			return;
		}

		String id = StringUtils.trimToNull(hbox.getLastChild().getId());
		if (id == null) {
			return;
		}

		id = id.replaceAll("\\d", "");

		switch (id) {
		case "pslCode":
			ExtendedCombobox writeOffCode = (ExtendedCombobox) hbox.getLastChild();
			Clients.clearWrongValue(writeOffCode);
			String code = writeOffCode.getValue();
			if (StringUtils.isEmpty(code)) {
				throw new WrongValueException(writeOffCode,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "PSL Category " }));
			}
			break;
		case "dpdDays":
			Intbox order = (Intbox) hbox.getLastChild();
			int dpdDays = order.getValue();

			if (dpdDays == 0) {
				throw new WrongValueException(order, Labels.getLabel("FIELD_IS_MAND", new String[] { "DPD Days " }));
			}

			if (dpdDays < 0) {
				throw new WrongValueException(order,
						Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] { "DPD Days " }));
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		loanTypeWriteOffListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finTypeWriteOff.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(FinTypeWriteOff writeOff) {
		logger.debug(Literal.ENTERING);

		this.loanType.setValue(writeOff.getLoanType(), writeOff.getFinTypeDesc());

		this.recordStatus.setValue(writeOff.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(FinTypeWriteOff awriteOff) {
		logger.debug(Literal.ENTERING);

		List<FinTypeWriteOff> lonTypeWriteOffKodeMapping = new ArrayList<>();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		List<String> writeOffCodes = new ArrayList<>();

		for (Listitem component : autoWriteOffRows.getItems()) {
			Listitem listitem = component;
			FinTypeWriteOff writeOffMapping = (FinTypeWriteOff) listitem.getAttribute("data");

			try {
				Hbox hbox = (Hbox) getComponent(listitem, 1);
				getCompValuetoBean(listitem, 1);

				if (hbox != null) {
					ExtendedCombobox writeOffCode = (ExtendedCombobox) hbox.getLastChild();
					PSLCategory object = (PSLCategory) writeOffCode.getObject();

					if (writeOffCodes.contains(object.getCode())) {
						MessageUtil.showError(
								"Duplicate PSL codes are not allowed for same LOAN type " + object.getCode());
						wve.add(new WrongValueException(object.getCode()));
					}

					writeOffCodes.add(object.getCode());

					writeOffMapping.setPslCode(object.getCode());
					writeOffMapping.setPslCodeDesc(object.getDescription());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				Hbox hbox = (Hbox) getComponent(listitem, 2);
				getCompValuetoBean(listitem, 2);

				if (hbox != null) {
					Component lastChild = hbox.getLastChild();
					if (lastChild != null) {
						Intbox order = (Intbox) lastChild;
						writeOffMapping.setDpdDays(order.getValue());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Longbox id = (Longbox) getComponent(listitem, 6);
				if (id != null && id.getValue() != null) {
					writeOffMapping.setId(id.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			boolean isNew = writeOffMapping.isNewRecord();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(writeOffMapping.getRecordType())) {
					writeOffMapping.setVersion(writeOffMapping.getVersion() + 1);
					if (isNew) {
						writeOffMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						writeOffMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						writeOffMapping.setNewRecord(true);
					}
				}
			} else {
				if (isNewAutoWriteOffMap) {
					if (writeOffMapping.isNewRecord()) {
						writeOffMapping.setVersion(1);
						writeOffMapping.setRecordType(PennantConstants.RCD_ADD);
					} else {
					}

					if (StringUtils.isBlank(writeOffMapping.getRecordType())) {
						writeOffMapping.setVersion(writeOffMapping.getVersion() + 1);
						writeOffMapping.setRecordType(PennantConstants.RCD_UPD);
					}

					if (writeOffMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						writeOffMapping.setVersion(writeOffMapping.getVersion() + 1);
					}
				} else {
					writeOffMapping.setVersion(writeOffMapping.getVersion() + 1);
				}
			}

			writeOffMapping.setLoanType(this.loanType.getValue());

			writeOffMapping.setRecordStatus(this.recordStatus.getValue());

			lonTypeWriteOffKodeMapping.add(writeOffMapping);
		}

		lonTypeWriteOffKodeMapping.addAll(deleteFinTypeWriteOffList);
		awriteOff.setLoanTypeWriteOffMapping(lonTypeWriteOffKodeMapping);
		this.finTypeWriteOff.setLoanTypeWriteOffMapping(lonTypeWriteOffKodeMapping);

		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
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
	public void doShowDialog(FinTypeWriteOff writeOff) {
		logger.debug(Literal.ENTERING);

		if (writeOff.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.loanType.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(writeOff.getRecordType())) {
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

		doWriteBeanToComponents(writeOff);

		int keyValue = 0;
		for (FinTypeWriteOff finTypeAutoWriteOff : writeOff.getLoanTypeWriteOffMapping()) {
			keyValue = keyValue + 1;
			finTypeAutoWriteOff.setKeyvalue(keyValue);
			appendAutoWriteOff(finTypeAutoWriteOff);
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
					.setConstraint(new PTStringValidator(Labels.getLabel("label_LoanTypeWriteOffDialog_LoanType.value"),
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
		logger.debug(Literal.ENTERING);

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxWriteOffCode.getSelectedItem();

		if (item != null) {
			final FinTypeWriteOff codeMapping = (FinTypeWriteOff) item.getAttribute("data");

			if (codeMapping.getRecordType() != null
					&& (codeMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| (codeMapping.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)))) {
				MessageUtil.showError(Labels.getLabel("RECORD_NO_MAINTAIN"));
			} else {
				codeMapping.setNewRecord(false);
				final Map<String, Object> map = new HashMap<>();
				map.put("loanTypeWriteOffDialogCtrl", this);
				map.put("loanTypeWriteOff", getLoanTypeWriteOff());
				map.put("loanTypeCodeMapping", codeMapping);
				map.put("roleCode", getRole());
				map.put("isEditable", isEditable);
				map.put("isNewRecord", getLoanTypeWriteOff().isNewRecord());

				try {
					Executions.createComponents(
							"/WEB-INF/pages/ApplicationMaster/LoanTypeWriteOff/LoanTypeWriteOffCodeMapping.zul", null,
							map);

				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final FinTypeWriteOff awriteOff = new FinTypeWriteOff();
		BeanUtils.copyProperties(getLoanTypeWriteOff(), awriteOff);

		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ awriteOff.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(awriteOff.getRecordType()).equals("")) {
				awriteOff.setVersion(awriteOff.getVersion() + 1);
				awriteOff.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					awriteOff.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					awriteOff.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), awriteOff.getNextTaskId(), awriteOff);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
				for (FinTypeWriteOff acsd : awriteOff.getLoanTypeWriteOffMapping()) {
					acsd.setVersion(acsd.getVersion() + 1);
					acsd.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						acsd.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						acsd.setNewRecord(true);
					}
				}
			}

			try {
				if (doProcess(awriteOff, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.finTypeWriteOff.isNewRecord()) {
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
			if (this.finTypeWriteOff.isNewRecord()) {
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

		final FinTypeWriteOff writeOff = new FinTypeWriteOff();
		BeanUtils.copyProperties(getLoanTypeWriteOff(), writeOff);
		boolean isNew = false;

		doSetValidation();

		if (autoWriteOffRows.getItems().isEmpty()) {
			MessageUtil.showMessage("Please add atleast one write off details.");
			return;
		}

		doWriteComponentsToBean(writeOff);

		isNew = writeOff.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(writeOff.getRecordType())) {
				writeOff.setVersion(writeOff.getVersion() + 1);
				if (isNew) {
					writeOff.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					writeOff.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					writeOff.setNewRecord(true);
				}
			}
		} else {
			writeOff.setVersion(writeOff.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(writeOff, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(FinTypeWriteOff aWriteOff, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aWriteOff.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aWriteOff.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aWriteOff.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aWriteOff.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aWriteOff.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aWriteOff);
				}

				if (isNotesMandatory(taskId, aWriteOff)) {
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

			aWriteOff.setTaskId(taskId);
			aWriteOff.setNextTaskId(nextTaskId);
			aWriteOff.setRoleCode(getRole());
			aWriteOff.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aWriteOff, tranType);
			String operationRefs = getServiceOperations(taskId, aWriteOff);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aWriteOff, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aWriteOff, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinTypeWriteOff aWrite = (FinTypeWriteOff) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = loanTypeWriteOffService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = loanTypeWriteOffService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = loanTypeWriteOffService.doApprove(auditHeader);

					if (aWrite.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = loanTypeWriteOffService.doReject(auditHeader);
					if (aWrite.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.windowLoanTypeWriteOffDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.windowLoanTypeWriteOffDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finTypeWriteOff), true);
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

	private AuditHeader getAuditHeader(FinTypeWriteOff aWriteOff, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aWriteOff.getBefImage(), aWriteOff);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aWriteOff.getUserDetails(),
				getOverideMap());
	}

	public LoanTypeWriteOffListCtrl getLoanTypeWriteOffListCtrl() {
		return loanTypeWriteOffListCtrl;
	}

	public void setLoanTypeWriteOffListCtrl(LoanTypeWriteOffListCtrl loanTypeWriteOffListCtrl) {
		this.loanTypeWriteOffListCtrl = loanTypeWriteOffListCtrl;
	}

	public FinTypeWriteOff getLoanTypeWriteOff() {
		return finTypeWriteOff;
	}

	public void setLoanTypeWriteOff(FinTypeWriteOff loanTypeWriteOff) {
		this.finTypeWriteOff = loanTypeWriteOff;
	}

	public List<FinTypeWriteOff> getCodeMappingList() {
		return codeMappingList;
	}

	public void setCodeMappingList(List<FinTypeWriteOff> codeMappingList) {
		this.codeMappingList = codeMappingList;
	}

	public void setLoanTypeWriteOffService(LoanTypeWriteOffService loanTypeWriteOffService) {
		this.loanTypeWriteOffService = loanTypeWriteOffService;
	}

	public boolean isAutoWriteOffNew() {
		return isAutoWriteOffNew;
	}

	public void setAutoWriteOffNew(boolean isAutoWriteOffNew) {
		this.isAutoWriteOffNew = isAutoWriteOffNew;
	}

}
