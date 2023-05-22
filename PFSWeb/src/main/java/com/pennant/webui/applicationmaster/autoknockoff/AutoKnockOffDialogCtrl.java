package com.pennant.webui.applicationmaster.autoknockoff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.service.applicationmaster.AutoKnockOffService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionStaticListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class AutoKnockOffDialogCtrl extends GFCBaseCtrl<AutoKnockOff> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AutoKnockOffDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AutoKnockOffDialog;

	protected Space space_KnockOffCode;
	protected Uppercasebox knockOffCode;
	protected Space space_Description;
	protected Textbox description;
	protected Textbox executionDays;
	protected Button btnExecutionDays;
	protected Checkbox active;
	private AutoKnockOff autoKnockOff;
	protected Button btnNew_FeePayables;
	protected Listbox listBoxKnockOffPayables;
	private transient AutoKnockOffListCtrl autoKnockOffListCtrl;
	private transient AutoKnockOffService autoKnockOffService;
	private AutoKnockOffFeeMapping autoKnockOffFeeMapping;
	private List<AutoKnockOffFeeMapping> feeMappingList = new ArrayList<AutoKnockOffFeeMapping>();
	private List<AutoKnockOffFeeMapping> deletefeeMappingList = new ArrayList<AutoKnockOffFeeMapping>();

	/**
	 * 
	 */
	public AutoKnockOffDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AutoKnockOffDialog";
		super.moduleCode = "AutoKnockOff";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.autoKnockOff.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AutoKnockOffDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AutoKnockOffDialog);

		try {

			// Get the required arguments.
			this.autoKnockOff = (AutoKnockOff) arguments.get("AutoKnockOff");
			this.autoKnockOffListCtrl = (AutoKnockOffListCtrl) arguments.get("autoKnockOffListCtrl");
			// Store the before image.
			AutoKnockOff knockOff = new AutoKnockOff();
			BeanUtils.copyProperties(this.autoKnockOff, knockOff);
			this.autoKnockOff.setBefImage(knockOff);
			setAutoKnockOff(this.autoKnockOff);
			if (this.autoKnockOff == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.autoKnockOff.isWorkflow(), this.autoKnockOff.getWorkflowId(),
					this.autoKnockOff.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.autoKnockOff);
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
		this.knockOffCode.setMaxlength(8);
		this.description.setMaxlength(100);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew_FeePayables.setVisible(getUserWorkspace().isAllowed("button_AutoKnockOffDialog_btnNewPayable"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AutoKnockOffDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AutoKnockOffDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AutoKnockOffDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AutoKnockOffDialog_btnSave"));
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
		doShowNotes(this.autoKnockOff);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew_FeePayables(Event event) {
		logger.debug(Literal.ENTERING);
		AutoKnockOffFeeMapping mapping = new AutoKnockOffFeeMapping();
		mapping.setNewRecord(true);
		int keyValue = 0;
		List<Listitem> mappingList = listBoxKnockOffPayables.getItems();
		if (mappingList != null && !mappingList.isEmpty()) {
			for (Listitem detail : mappingList) {
				AutoKnockOffFeeMapping bankInfo = (AutoKnockOffFeeMapping) detail.getAttribute("data");
				if (bankInfo != null && bankInfo.getKeyValue() > keyValue) {
					keyValue = bankInfo.getKeyValue();
				}
			}
		}
		mapping.setKeyValue(keyValue + 1);
		renderItem(mapping);
		logger.debug("Leaving");
	}

	private void renderItem(AutoKnockOffFeeMapping mapping) {
		Listitem listItem = new Listitem();
		Listcell listCell;
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("button_AutoKnockOffDialog_btnNewPayable");

		String recordType = mapping.getRecordType();
		String recordStatus = mapping.getRecordStatus();

		if (!(mapping.isNewRecord()) || (recordStatus != null || "".equals(recordStatus))) {
			isReadOnly = true;
		}

		// Order IntBox
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		Intbox order = new Intbox();
		order.setStyle("text-align:right");
		order.setMaxlength(3);
		order.setValue(mapping.getFeeOrder());
		readOnlyComponent(isReadOnly, order);
		listCell.setId("order".concat(String.valueOf(mapping.getKeyValue())));
		hbox.appendChild(space);
		hbox.appendChild(order);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// FeeTypeCode ExtendedCombobox
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		ExtendedCombobox fees = new ExtendedCombobox();
		if (mapping.getFeeTypeId() != 0) {
			Search search = new Search(FeeType.class);
			search.addFilterEqual("FeeTypeId", mapping.getFeeTypeId());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			FeeType feeType = (FeeType) searchProcessor.getResults(search).get(0);

			fees.setValue(String.valueOf(feeType.getFeeTypeID()));
			fees.setDescription(feeType.getFeeTypeCode());
			fees.setObject(feeType);
		}
		readOnlyComponent(isReadOnly, fees);
		fees.setWidth("80px");
		fees.setModuleName("FeeType");
		fees.setValueColumn("FeeTypeID");
		fees.setDescColumn("FeeTypeCode");
		listCell.setId("fees".concat(String.valueOf(mapping.getKeyValue())));
		hbox.appendChild(space);
		hbox.appendChild(fees);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		listCell = new Listcell(recordStatus);
		listCell.setParent(listItem);

		listCell = new Listcell(recordType);
		listCell.setParent(listItem);

		// Delete action
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel("Delete");

		if ("DELETE".equalsIgnoreCase(recordType)) {
			button.setDisabled(true);
		} else {
			button.setDisabled(isReadOnly("button_AutoKnockOffDialog_btnNewPayable"));
		}

		listCell.appendChild(button);
		listCell.setParent(listItem);
		button.addForward("onClick", self, "onClickAutoknockButtonDelete", listItem);

		listItem.setAttribute("data", mapping);
		this.listBoxKnockOffPayables.appendChild(listItem);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		autoKnockOffListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.autoKnockOff.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param voucherVendor
	 * 
	 */

	public void onClick$btnExecutionDays(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Textbox txtbx = (Textbox) btnExecutionDays.getPreviousSibling();
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_AutoKnockOffDialog,
				"FrequencyDaysMethod", txtbx.getValue());
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void doWriteBeanToComponents(AutoKnockOff knockOff) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		this.knockOffCode.setValue(knockOff.getCode());
		this.description.setValue(knockOff.getDescription());

		if (knockOff.isNewRecord()) {
			List<ValueLabel> frequencyDays = PennantStaticListUtil.getFrequencyDays();
			for (ValueLabel valueLabel : frequencyDays) {
				sql.append(valueLabel.getValue()).append(",");
			}
		} else {
			sql.append(knockOff.getExecutionDays());
		}

		this.executionDays.setValue(sql.toString());

		this.active.setChecked(knockOff.isActive());
		this.recordStatus.setValue(knockOff.getRecordStatus());
		if (knockOff.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(knockOff.getRecordType())) {
			this.active.setChecked(true);
			// this.active.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVoucherVendor
	 */
	public void doWriteComponentsToBean(AutoKnockOff aknockOff) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// knockOffCode
		try {
			aknockOff.setCode(this.knockOffCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// description
		try {
			aknockOff.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aknockOff.setExecutionDays(this.executionDays.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Active
		try {
			aknockOff.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private boolean saveFeeMappingList(AutoKnockOff aknockOff) {
		ArrayList<WrongValueException> wve = new ArrayList<>();
		if (this.listBoxKnockOffPayables.getItemCount() == 0) {
			throw new WrongValueException(this.btnNew_FeePayables,
					Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_FeePayables.title") }));
		} else {
			feeMappingList.clear();
			for (Listitem listitem : listBoxKnockOffPayables.getItems()) {
				AutoKnockOffFeeMapping mapping = (AutoKnockOffFeeMapping) listitem.getAttribute("data");
				List<Listcell> listcels = listitem.getChildren();
				for (Listcell listcell : listcels) {
					try {
						getCompValuetoBean(listcell, mapping);
					} catch (WrongValueException we) {
						wve.add(we);
					}
				}
				boolean isNew = false;

				isNew = mapping.isNewRecord();
				String tranType = "";
				if (isWorkFlowEnabled()) {
					tranType = PennantConstants.TRAN_WF;
					if (StringUtils.isBlank(mapping.getRecordType())) {
						mapping.setVersion(mapping.getVersion() + 1);
						if (isNew) {
							mapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						} else {
							mapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							mapping.setNewRecord(true);
						}
					}
				} else {
					// set the tranType according to RecordType
					if (isNew) {
						tranType = PennantConstants.TRAN_ADD;
						mapping.setVersion(1);
						mapping.setRecordType(PennantConstants.RCD_ADD);
					} else {
						tranType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isBlank(mapping.getRecordType())) {
						tranType = PennantConstants.TRAN_UPD;
						mapping.setRecordType(PennantConstants.RCD_UPD);
					}
					if (mapping.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
						tranType = PennantConstants.TRAN_ADD;
					} else if (mapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						tranType = PennantConstants.TRAN_UPD;
					}
				}
				showErrorDetails(wve);
				feeMappingList.add(mapping);
			}
			if (deletefeeMappingList != null) {
				feeMappingList.addAll(deletefeeMappingList);
			}
			if (!feeMappingList.isEmpty()) {
				Set<Integer> order = new HashSet<>();
				List<AutoKnockOffFeeMapping> feemapbyorder = feeMappingList.stream()
						.filter(e -> order.add(e.getFeeOrder())).collect(Collectors.toList());
				if (feeMappingList.size() == feemapbyorder.size()) {
					Set<Integer> code = new HashSet<>();
					List<AutoKnockOffFeeMapping> feemapbycode = feemapbyorder.stream()
							.filter(e -> code.add(e.getFeeTypeId())).collect(Collectors.toList());
					if (feeMappingList.size() == feemapbycode.size()) {
						aknockOff.setMappingList(feemapbycode);
					} else {
						MessageUtil.showError("FeeTypeCode : "
								+ feeMappingList.get(feeMappingList.size() - 1).getFeeTypeId() + " Already Exist");
						return false;
					}
				} else {
					MessageUtil.showError("KnockOff OrderID : "
							+ feeMappingList.get(feeMappingList.size() - 1).getFeeOrder() + " Already Exist");
					return false;
				}
			}

		}
		return true;
	}

	private void getCompValuetoBean(Listcell listcell, AutoKnockOffFeeMapping mapping) {
		String id = StringUtils.trimToNull(listcell.getId());
		if (id == null) {
			return;
		}
		id = id.replaceAll("\\d", "");

		switch (id) {
		case "order":
			Hbox hbox2 = (Hbox) listcell.getFirstChild();
			Intbox order = (Intbox) hbox2.getLastChild();
			Clients.clearWrongValue(order);
			String orderId = order.getText();
			if (StringUtils.isEmpty(orderId) || (StringUtils.equals(orderId, "0"))) {
				throw new WrongValueException(order, Labels.getLabel("FIELD_IS_MAND", new String[] { "Order " }));
			}
			mapping.setFeeOrder(Integer.parseInt(orderId));
			break;
		case "fees":
			Hbox hbox1 = (Hbox) listcell.getFirstChild();
			ExtendedCombobox fees = (ExtendedCombobox) hbox1.getLastChild();
			Clients.clearWrongValue(fees);
			String feeTypeId = fees.getValue();

			if (StringUtils.isEmpty(feeTypeId)) {
				throw new WrongValueException(fees, Labels.getLabel("FIELD_IS_MAND", new String[] { "Fees Code " }));
			}
			mapping.setFeeTypeId(Integer.parseInt(feeTypeId));
			break;
		default:
			break;
		}
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		boolean focus = false;
		if (wve.size() > 0) {
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

	/**
	 * Displays the dialog page.
	 * 
	 * @param voucherVendor The entity that need to be render.
	 */
	public void doShowDialog(AutoKnockOff knockOff) {
		logger.debug(Literal.ENTERING);

		if (knockOff.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.knockOffCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(knockOff.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.description.focus();
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
		doFillCheckListDetailsList(getAutoKnockOff().getMappingList());
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.knockOffCode.isReadonly()) {
			this.knockOffCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AutoKnockOffDialog_KnockOffCode.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.description.isReadonly()) {
			this.description
					.setConstraint(new PTStringValidator(Labels.getLabel("label_AutoKnockOffDialog_Description.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FSLASH_SPACE, true));
		}
		if (!this.executionDays.isReadonly()) {
			this.executionDays.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AutoKnockOffDialog_ExecutionDays.value"), null, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.knockOffCode.setConstraint("");
		this.description.setConstraint("");
		this.executionDays.setConstraint("");
		// this.frequency.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.knockOffCode.setErrorMessage("");
		this.description.setErrorMessage("");
		this.executionDays.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	public void doFillCheckListDetailsList(List<AutoKnockOffFeeMapping> mappingList) {
		logger.debug("Entering");

		this.listBoxKnockOffPayables.getItems().clear();
		int keyValue = 0;
		for (AutoKnockOffFeeMapping autoKnockOffFeeMapping : mappingList) {
			autoKnockOffFeeMapping.setKeyValue(keyValue + 1);
			renderItem(autoKnockOffFeeMapping);
			keyValue = autoKnockOffFeeMapping.getKeyValue();
		}

		logger.debug("Leaving ");
	}

	public void onClickAutoknockButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem item = (Listitem) event.getData();
		AutoKnockOffFeeMapping mapping = (AutoKnockOffFeeMapping) item.getAttribute("data");

		if (mapping.isNewRecord()) {
			listBoxKnockOffPayables.removeItemAt(item.getIndex());
		} else {

			if ("Approved".equals(mapping.getRecordStatus())) {
				mapping.setNewRecord(true);
			}

			mapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if ((item.getIndex() == 0) && (item.getNextSibling() == null)) {
				MessageUtil.showError("At Least single FeeType details required connot be Delete ");
			} else {
				deletefeeMappingList.add(mapping);
				listBoxKnockOffPayables.removeItemAt(item.getIndex());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AutoKnockOff aknockOff = new AutoKnockOff();
		BeanUtils.copyProperties(getAutoKnockOff(), aknockOff);

		doDelete(String.valueOf(aknockOff.getId()), aknockOff);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final AutoKnockOff aknockOff) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.trimToEmpty(aknockOff.getRecordType()).equals("")) {
			aknockOff.setVersion(aknockOff.getVersion() + 1);
			aknockOff.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			for (AutoKnockOffFeeMapping autoKnockOffFeeMapping : aknockOff.getMappingList()) {
				autoKnockOffFeeMapping.setVersion(autoKnockOffFeeMapping.getVersion() + 1);
				autoKnockOffFeeMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				autoKnockOffFeeMapping.setNewRecord(true);
			}

			if (isWorkFlowEnabled()) {
				aknockOff.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				aknockOff.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aknockOff.getNextTaskId(), aknockOff);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (doProcess(aknockOff, tranType)) {
				refreshList();
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

		if (this.autoKnockOff.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.knockOffCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.knockOffCode);
		}

		readOnlyComponent(isReadOnly("AutoKnockOffDialog_Description"), this.description);
		readOnlyComponent(isReadOnly("AutoKnockOffDialog_ExecutionDays"), this.executionDays);
		readOnlyComponent(isReadOnly("AutoKnockOffDialog_ExecutionDays"), this.btnExecutionDays);
		readOnlyComponent(isReadOnly("AutoKnockOffDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.autoKnockOff.isNewRecord()) {
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

		readOnlyComponent(true, this.knockOffCode);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.executionDays);
		readOnlyComponent(true, this.active);
		readOnlyComponent(true, this.btnExecutionDays);

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

		this.knockOffCode.setValue("");
		this.description.setValue("");
		this.executionDays.setValue("");
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final AutoKnockOff knockOff = new AutoKnockOff();
		BeanUtils.copyProperties(getAutoKnockOff(), knockOff);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(knockOff);
		if (!saveFeeMappingList(knockOff)) {
			return;
		}
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
	protected boolean doProcess(AutoKnockOff aKnockOff, String tranType) {
		logger.debug(Literal.ENTERING);

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
		AutoKnockOff aKnock = (AutoKnockOff) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = autoKnockOffService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = autoKnockOffService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = autoKnockOffService.doApprove(auditHeader);

					if (aKnock.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = autoKnockOffService.doReject(auditHeader);
					if (aKnock.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AutoKnockOffDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AutoKnockOffDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.autoKnockOff), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(AutoKnockOff aKnockOff, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aKnockOff.getBefImage(), aKnockOff);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aKnockOff.getUserDetails(),
				getOverideMap());
	}

	public AutoKnockOffService getAutoKnockOffService() {
		return autoKnockOffService;
	}

	public AutoKnockOff getAutoKnockOff() {
		return autoKnockOff;
	}

	public void setAutoKnockOff(AutoKnockOff autoKnockOff) {
		this.autoKnockOff = autoKnockOff;
	}

	public void setAutoKnockOffService(AutoKnockOffService autoKnockOffService) {
		this.autoKnockOffService = autoKnockOffService;
	}

	public List<AutoKnockOffFeeMapping> getFeeMappingList() {
		return feeMappingList;
	}

	public AutoKnockOffFeeMapping getAutoKnockOffFeeMapping() {
		return autoKnockOffFeeMapping;
	}

	public void setAutoKnockOffFeeMapping(AutoKnockOffFeeMapping autoKnockOffFeeMapping) {
		this.autoKnockOffFeeMapping = autoKnockOffFeeMapping;
	}

	public void setFeeMappingList(List<AutoKnockOffFeeMapping> feeMappingList) {
		this.feeMappingList = feeMappingList;
	}

}
