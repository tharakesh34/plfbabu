package com.pennanttech.pff.npa.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
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
import com.pennanttech.pff.npa.model.AssetClassCode;
import com.pennanttech.pff.npa.model.AssetClassSetupDetail;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.model.AssetSubClassCode;
import com.pennanttech.pff.npa.service.AssetClassSetupService;

public class AssetClassSetupDialogCtrl extends GFCBaseCtrl<AssetClassSetupHeader> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssetClassSetupDialogCtrl.class);

	protected Window window_AssetClassSetupDialog;
	protected ExtendedCombobox entityCode;
	protected Textbox code;
	protected Textbox description;
	protected Button btnNew_AssetClassSetupDialog;

	protected Listbox listBoxAssetClassSetup;

	private AssetClassSetupHeader assetClassSetupHeader;

	private transient AssetClassSetupListCtrl assetClassSetupListCtrl;
	private transient AssetClassSetupService assetClassSetupService;

	/**
	 * default constructor.<br>
	 */
	public AssetClassSetupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetClassSetupDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AssetClassSetupDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AssetClassSetupDialog);

		try {
			// Get the required arguments.
			this.assetClassSetupHeader = (AssetClassSetupHeader) arguments.get("assetClassSetup");
			this.assetClassSetupListCtrl = (AssetClassSetupListCtrl) arguments.get("assetClassSetupListCtrl");

			if (this.assetClassSetupHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AssetClassSetupHeader assetClassSetupHeader = this.assetClassSetupHeader.copyEntity();
			this.assetClassSetupHeader.setBefImage(assetClassSetupHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.assetClassSetupHeader.isWorkflow(), this.assetClassSetupHeader.getWorkflowId(),
					this.assetClassSetupHeader.getNextTaskId());

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
			doShowDialog(this.assetClassSetupHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.assetClassSetupHeader.getId());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("entityCode");
		this.entityCode.setDescColumn("entityDesc");
		this.entityCode.setValidateColumns(new String[] { "entityCode" });
		this.code.setMaxlength(8);
		this.description.setMaxlength(50);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetClassSetupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetClassSetupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetClassSetupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetClassSetupDialog_btnSave"));
		this.btnNew_AssetClassSetupDialog
				.setVisible(getUserWorkspace().isAllowed("button_AssetClassSetupDialog_btnAdd"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$entityCode(Event event) {
		logger.debug(Literal.ENTERING + " " + event.toString());

		this.entityCode.setConstraint("");
		this.entityCode.clearErrorMessage();
		Clients.clearWrongValue(entityCode);
		Object dataObject = this.entityCode.getObject();

		if (dataObject instanceof String) {
			this.entityCode.setValue(dataObject.toString());
			this.entityCode.setDescription("");
		} else {
			Entity details = (Entity) dataObject;
			if (details != null) {
				this.entityCode.setValue(details.getEntityCode());
				this.entityCode.setDescription(details.getEntityDesc());
			}
		}
		logger.debug(Literal.LEAVING + " " + event.toString());
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
		doShowNotes(this.assetClassSetupHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		assetClassSetupListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.assetClassSetupHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param assetClassificationHeader
	 * 
	 */
	public void doWriteBeanToComponents(AssetClassSetupHeader assetClassSetupHeader) {
		logger.debug(Literal.ENTERING);

		this.code.setValue(assetClassSetupHeader.getCode());
		this.description.setValue(assetClassSetupHeader.getDescription());

		this.entityCode.setValue(assetClassSetupHeader.getEntityCode());
		this.recordStatus.setValue(assetClassSetupHeader.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param subClassCode
	 */
	public void doWriteComponentsToBean(AssetClassSetupHeader assetClassSetupHeader) {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.entityCode.getValidatedValue();
			Object obj = this.entityCode.getObject();
			if (obj != null) {
				assetClassSetupHeader.setEntityCode(((Entity) obj).getEntityCode());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			assetClassSetupHeader.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			assetClassSetupHeader.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		wve.addAll(getAssetClassSetupDetailList(assetClassSetupHeader));
		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}

	public void doShowDialog(AssetClassSetupHeader assetClassSetupHeader) {
		logger.debug(Literal.ENTERING);

		if (assetClassSetupHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.entityCode.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(assetClassSetupHeader.getRecordType())) {
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

		doWriteBeanToComponents(assetClassSetupHeader);
		int keyValue = 0;
		for (AssetClassSetupDetail assetClassSetupDetail : assetClassSetupHeader.getDetails()) {
			keyValue = keyValue + 1;
			assetClassSetupDetail.setKeyvalue(keyValue);
			appendAssetClassSetupDetail(assetClassSetupDetail);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssetClassSetupDialog_Entity.value"), null, true));
		}

		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssetClassificationHeaderDialog_Code.value"),
							PennantRegularExpressions.REGEX_UPPERCASENAME, true));
		}
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssetClassificationHeaderDialog_Description.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew_AssetClassSetupDialog(Event event) {
		logger.debug("Entering");

		AssetClassSetupDetail assetClassSetupDetail = new AssetClassSetupDetail();
		assetClassSetupDetail.setNewRecord(true);
		if (StringUtils.isBlank(assetClassSetupDetail.getRecordType())) {
			assetClassSetupDetail.setVersion(1);
			assetClassSetupDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		int keyValue = 0;
		for (Listitem component : listBoxAssetClassSetup.getItems()) {

			AssetClassSetupDetail acsd = (AssetClassSetupDetail) component.getAttribute("data");

			if (acsd != null && acsd.getKeyvalue() > keyValue) {
				keyValue = acsd.getKeyvalue();
			}
		}

		assetClassSetupDetail.setKeyvalue(keyValue + 1);
		appendAssetClassSetupDetail(assetClassSetupDetail);
		logger.debug("Leaving");
	}

	private void appendAssetClassSetupDetail(AssetClassSetupDetail acsd) {
		Listitem listitem = new Listitem();
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("button_AssetClassSetupDialog_btnAdd");

		listitem.setAttribute("assetClassSetupDetail", acsd);
		Listcell listcell;

		// DPD-Days min
		listcell = new Listcell();
		hbox = new Hbox();
		Intbox npaDPDMin = new Intbox();
		npaDPDMin.setId("DPDMin_" + acsd.getKeyvalue());
		readOnlyComponent(isReadOnly, npaDPDMin);
		npaDPDMin.setValue(acsd.getDpdMin());
		npaDPDMin.setWidth("50px");
		npaDPDMin.setMaxlength(4);

		getSpacing(hbox, npaDPDMin, true, acsd.getKeyvalue(), "DPDMin_");
		hbox.appendChild(npaDPDMin);

		space = new Space();
		space.setSpacing("25px");
		hbox.appendChild(space);

		// DPD-Days Max
		Intbox npaDPDMax = new Intbox();
		npaDPDMax.setId("DPDMax_" + acsd.getKeyvalue());
		readOnlyComponent(isReadOnly, npaDPDMax);
		npaDPDMax.setValue(acsd.getDpdMax());
		npaDPDMax.setWidth("50px");
		npaDPDMax.setMaxlength(4);

		getSpacing(hbox, npaDPDMax, true, acsd.getKeyvalue(), "DPDMax_");
		hbox.appendChild(npaDPDMax);
		listcell.appendChild(hbox);
		listitem.appendChild(listcell);

		listcell = new Listcell();
		hbox = new Hbox();
		ExtendedCombobox assestClassCode = new ExtendedCombobox();
		assestClassCode.setWidth("100px");
		assestClassCode.setModuleName("AssetClassCode");
		assestClassCode.setValueColumn("Code");
		assestClassCode.setDescColumn("Description");
		assestClassCode.setValidateColumns(new String[] { "Code" });
		assestClassCode.addForward("onFulfill", window_AssetClassSetupDialog, "onChangeAssetClassCode", listitem);
		assestClassCode.setId("assestClassCode".concat(String.valueOf(acsd.getKeyvalue())));

		if (acsd.getClassID() != Long.MIN_VALUE && acsd.getClassID() != 0) {
			Search search = new Search(AssetClassCode.class);
			search.addFilterEqual("Id", acsd.getClassID());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			AssetClassCode acc = (AssetClassCode) searchProcessor.getResults(search).get(0);

			if (acc != null) {
				assestClassCode.setValue(acc.getCode());
				assestClassCode.setDescription(acc.getDescription());
				assestClassCode.setObject(acc);
			}
		}

		readOnlyComponent(isReadOnly, assestClassCode);
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(assestClassCode);
		listcell.appendChild(hbox);
		listitem.appendChild(listcell);

		listcell = new Listcell();
		hbox = new Hbox();
		ExtendedCombobox assetSubClassCode = new ExtendedCombobox();
		assetSubClassCode.setWidth("100px");
		assetSubClassCode.setModuleName("AssetSubClassCode");
		assetSubClassCode.setValueColumn("Code");
		assetSubClassCode.setDescColumn("Description");
		assetSubClassCode.setValidateColumns(new String[] { "Code" });
		assetSubClassCode.addForward("onFulfill", window_AssetClassSetupDialog, "onChangeAssetSubClassCode", listitem);
		assetSubClassCode.setId("assetSubClassCode".concat(String.valueOf(acsd.getKeyvalue())));
		readOnlyComponent(isReadOnly, assetSubClassCode);

		if (acsd.getClassID() != Long.MIN_VALUE && acsd.getClassID() != 0) {
			Search search = new Search(AssetSubClassCode.class);
			search.addFilterEqual("Id", acsd.getSubClassID());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			AssetSubClassCode ascc = (AssetSubClassCode) searchProcessor.getResults(search).get(0);

			if (ascc != null) {
				assetSubClassCode.setValue(ascc.getCode());
				assetSubClassCode.setDescription(ascc.getDescription());
				assetSubClassCode.setObject(ascc);
			}
		}

		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(assetSubClassCode);
		listcell.appendChild(hbox);
		listitem.appendChild(listcell);

		// NPA check box
		Listcell lc_NPACheckBox = new Listcell();
		Checkbox npa_CheckBox = new Checkbox();
		npa_CheckBox.setId("NPACheckBox_" + acsd.getKeyvalue());
		readOnlyComponent(isReadOnly, npa_CheckBox);
		npa_CheckBox.addForward("onCheck", window_AssetClassSetupDialog, "onChecknpaCheckbox", listitem);
		npa_CheckBox.setChecked(acsd.isNpaStage());
		npa_CheckBox.setWidth("50px");
		lc_NPACheckBox.appendChild(npa_CheckBox);
		listitem.appendChild(lc_NPACheckBox);

		Intbox npaAge = new Intbox();
		npaAge.setWidth("80px");
		if (acsd.getNpaAge() != 0) {
			npaAge.setValue(acsd.getNpaAge());
		} else {
			npaAge.setValue(0);
		}
		npaAge.setId("npaAge".concat(String.valueOf(acsd.getKeyvalue())));
		readOnlyComponent(true, npaAge);
		hbox = new Hbox();
		hbox.appendChild(npaAge);
		listcell = new Listcell();
		listcell.appendChild(hbox);
		listcell.setParent(listitem);

		listitem.setAttribute("data", acsd);

		this.listBoxAssetClassSetup.appendChild(listitem);
	}

	public void onChangeAssetClassCode(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Hbox hbox = (Hbox) getComponent(listitem, 2);
		ExtendedCombobox assestClassCode = (ExtendedCombobox) hbox.getLastChild();
		Clients.clearWrongValue(assestClassCode);
		AssetClassCode object = (AssetClassCode) assestClassCode.getObject();

		hbox = (Hbox) getComponent(listitem, 3);
		ExtendedCombobox assetSubClassCode = (ExtendedCombobox) hbox.getLastChild();
		Clients.clearWrongValue(assetSubClassCode);

		if (object != null) {
			Filter[] codeFilter = new Filter[1];
			codeFilter[0] = Filter.equalTo("AssetClassId", object.getId());
			assetSubClassCode.setFilters(codeFilter);
		} else {
			assetSubClassCode.setValue(null);
		}

	}

	public void onChangeAssetSubClassCode(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Hbox hbox = (Hbox) getComponent(listitem, 2);
		ExtendedCombobox assestClassCode = (ExtendedCombobox) hbox.getLastChild();
		Clients.clearWrongValue(assestClassCode);

		AssetClassCode object = (AssetClassCode) assestClassCode.getObject();

		hbox = (Hbox) getComponent(listitem, 3);
		ExtendedCombobox assetSubClassCode = (ExtendedCombobox) hbox.getLastChild();
		Clients.clearWrongValue(assetSubClassCode);

		int keyValue = Integer.parseInt(assetSubClassCode.getId().replaceAll("assetSubClassCode", ""));

		AssetSubClassCode ascc = (AssetSubClassCode) assetSubClassCode.getObject();

		if (ascc != null && validateSubClassCode(ascc, keyValue)) {
			assetSubClassCode.setValue(null);
			throw new WrongValueException(assestClassCode,
					Labels.getLabel("DATA_ALREADY_EXISTS", new String[] { "Asset Sub-Classfication " }));
		}

		if (object == null || StringUtils.isEmpty(object.getCode())) {
			assetSubClassCode.setValue(null);

			throw new WrongValueException(assestClassCode,
					Labels.getLabel("FIELD_IS_MAND", new String[] { "Asset Classification " }));

		}

	}

	private boolean validateSubClassCode(AssetSubClassCode ascc, int keyValue) {
		for (Listitem component : listBoxAssetClassSetup.getItems()) {
			Hbox hbox = (Hbox) getComponent(component, 3);
			ExtendedCombobox extendedCombobox = (ExtendedCombobox) hbox.getLastChild();
			Clients.clearWrongValue(extendedCombobox);
			int key = Integer.parseInt(extendedCombobox.getId().replaceAll("assetSubClassCode", ""));

			if (key == keyValue) {
				continue;
			}

			AssetSubClassCode assetSubClassCode = (AssetSubClassCode) extendedCombobox.getObject();

			if (assetSubClassCode == null) {
				continue;
			}

			if (StringUtils.equals(ascc.getCode(), assetSubClassCode.getCode())) {
				return true;
			}
		}

		return false;
	}

	private void getSpacing(Hbox hbox, Component component, boolean mandatory, int idSeq, String idName) {
		Space space = new Space();
		space.setId("Space_" + idName + "" + idSeq);
		space.setSpacing("2px");
		if (mandatory) {
			space.setSclass(PennantConstants.mandateSclass);
		} else {
			space.setSclass("");
		}
		space.setParent(hbox);
		hbox.appendChild(component);
	}

	public void onChecknpaCheckbox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem listitem = (Listitem) event.getData();
		Checkbox checkbox = (Checkbox) getComponent(listitem, 4);
		int keyValue = Integer.parseInt(checkbox.getId().replaceAll("NPACheckBox_", ""));
		if (validateNPAStage(keyValue, checkbox.isChecked())) {
			checkbox.setChecked(true);
			MessageUtil.showError("Unable to Uncheck the current NPA Stage.");
			return;
		}

		if (checkbox.isChecked()) {
			int prvDPDMax = getPrvDPDMax(keyValue);
			setNPAAging(listitem, prvDPDMax);

			for (Listitem component : listBoxAssetClassSetup.getItems()) {
				Listitem item = (Listitem) component;
				Checkbox itemCheckbox = (Checkbox) getComponent(item, 4);
				int id = Integer.parseInt(itemCheckbox.getId().replaceAll("NPACheckBox_", ""));
				if (keyValue >= id) {
					continue;
				}
				itemCheckbox.setChecked(true);
				itemCheckbox.setDisabled(true);
				setNPAAging(item, prvDPDMax);
			}
		} else {
			Intbox npaAge = (Intbox) getComponent(listitem, 5).getLastChild();
			npaAge.setValue(0);
			Intbox npaDPDMax = (Intbox) getComponent(listitem, 1).getLastChild();
			int prvDPDMax = npaDPDMax.getValue();
			for (Listitem component : listBoxAssetClassSetup.getItems()) {
				Listitem item = (Listitem) component;
				Checkbox itemCheckbox = (Checkbox) getComponent(item, 4);
				int id = Integer.parseInt(itemCheckbox.getId().replaceAll("NPACheckBox_", ""));
				if (keyValue > id) {
					continue;
				}
				itemCheckbox.setDisabled(false);
				setNPAAging(item, prvDPDMax);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	private boolean validateNPAStage(int keyValue, boolean checked) {
		if (checked) {
			return false;
		}
		if (keyValue == 0) {
			return false;
		}
		for (Listitem component : listBoxAssetClassSetup.getItems()) {
			Listitem item = (Listitem) component;
			Checkbox checkbox = (Checkbox) getComponent(item, 4);
			int id = Integer.parseInt(checkbox.getId().replaceAll("NPACheckBox_", ""));
			if (keyValue - 1 == id) {
				if (checkbox.isChecked()) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	private void setNPAAging(Listitem listitem, int prvDPDMax) {
		Intbox npaDPDMax = (Intbox) getComponent(listitem, 1).getLastChild();
		int curDPDMax = npaDPDMax.getValue();
		Intbox npaAge = (Intbox) getComponent(listitem, 5).getLastChild();
		npaAge.setValue(curDPDMax - prvDPDMax);
	}

	private int getPrvDPDMax(int keyValue) {
		int prvDPDMax = 0;
		if (keyValue - 1 == -1) {
			return prvDPDMax;
		}
		for (Listitem component : listBoxAssetClassSetup.getItems()) {
			Listitem item = (Listitem) component;
			Intbox npaDPDMax = (Intbox) getComponent(item, 1).getLastChild();
			int id = Integer.parseInt(npaDPDMax.getId().replaceAll("DPDMax_", ""));
			if (keyValue - 1 == id) {
				prvDPDMax = npaDPDMax.getValue();
				break;
			}
		}
		return prvDPDMax;
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		this.entityCode.setConstraint("");
		this.code.setConstraint("");
		this.description.setConstraint("");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		this.entityCode.setErrorMessage("");
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
	}

	/**
	 * Deletes a AssetClassificationHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final AssetClassSetupHeader acsh = this.assetClassSetupHeader.copyEntity();

		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ acsh.getEntityCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(acsh.getRecordType()).equals("")) {
				acsh.setVersion(acsh.getVersion() + 1);
				acsh.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					acsh.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					acsh.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), acsh.getNextTaskId(), acsh);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
				for (AssetClassSetupDetail acsd : acsh.getDetails()) {
					acsd.setVersion(acsd.getVersion() + 1);
					acsd.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						acsd.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						acsd.setNewRecord(true);
					}
				}
			}

			try {
				if (doProcess(acsh, tranType)) {
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

		if (this.assetClassSetupHeader.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.entityCode);
			readOnlyComponent(false, this.code);
			readOnlyComponent(false, this.description);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.entityCode);
			readOnlyComponent(true, this.code);
			readOnlyComponent(true, this.description);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assetClassSetupHeader.isNewRecord()) {
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

		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.code);
		readOnlyComponent(true, this.description);

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
		this.entityCode.setValue("");
		this.code.setValue("");
		this.description.setValue("");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final AssetClassSetupHeader assetClassSetupHeader = this.assetClassSetupHeader.copyEntity();
		boolean isNew = false;

		boolean validateFields = true;
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")) {
				validateFields = false;
			}
		}

		if (isWorkFlowEnabled()) {
			assetClassSetupHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), assetClassSetupHeader.getNextTaskId(),
					assetClassSetupHeader);
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equals(assetClassSetupHeader.getRecordType()) && validateFields) {
			doClearMessage();
			doSetValidation();
			// fill the Promotion object with the components data
			doWriteComponentsToBean(assetClassSetupHeader);

		}

		isNew = assetClassSetupHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(assetClassSetupHeader.getRecordType()).equals("")) {
				assetClassSetupHeader.setVersion(assetClassSetupHeader.getVersion() + 1);
				if (isNew) {
					assetClassSetupHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					assetClassSetupHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					assetClassSetupHeader.setNewRecord(true);
				}
			}
		} else {
			assetClassSetupHeader.setVersion(assetClassSetupHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(assetClassSetupHeader, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(AssetClassSetupHeader assetClassSetupHeader, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		assetClassSetupHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		assetClassSetupHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			assetClassSetupHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(assetClassSetupHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, assetClassSetupHeader);
				}

				if (isNotesMandatory(taskId, assetClassSetupHeader)) {
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

			assetClassSetupHeader.setTaskId(taskId);
			assetClassSetupHeader.setNextTaskId(nextTaskId);
			assetClassSetupHeader.setRoleCode(getRole());
			assetClassSetupHeader.setNextRoleCode(nextRoleCode);

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(assetClassSetupHeader, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				auditHeader = getAuditHeader(assetClassSetupHeader, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(assetClassSetupHeader, tranType), null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AssetClassSetupHeader assetClassSetupHeader = (AssetClassSetupHeader) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = this.assetClassSetupService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = this.assetClassSetupService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = this.assetClassSetupService.doApprove(auditHeader);

					if (assetClassSetupHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = this.assetClassSetupService.doReject(auditHeader);
					if (assetClassSetupHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssetClassSetupDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AssetClassSetupDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.assetClassSetupHeader), true);
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

	private List<WrongValueException> getAssetClassSetupDetailList(AssetClassSetupHeader acsh) {
		List<AssetClassSetupDetail> assetClassSetupDetailList = new ArrayList<>();
		ArrayList<WrongValueException> wve = new ArrayList<>();

		int maxValue = -1;
		for (Listitem component : listBoxAssetClassSetup.getItems()) {
			Listitem listitem = (Listitem) component;
			AssetClassSetupDetail assetClassSetupDetail = (AssetClassSetupDetail) listitem.getAttribute("data");
			Hbox hbox = null;
			Intbox dpdMin = null;
			Intbox dpdMax = null;
			try {
				hbox = (Hbox) getComponent(listitem, 1);
				dpdMin = (Intbox) hbox.getChildren().get(1);
				if (dpdMin.getValue() == null) {
					throw new WrongValueException(dpdMin,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Min DPD " }));
				}
				if (Integer.valueOf(dpdMin.getValue()) < 0) {
					throw new WrongValueException(dpdMin,
							Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] { "Min DPD " }));
				}
				assetClassSetupDetail.setDpdMin(dpdMin.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				hbox = (Hbox) getComponent(listitem, 1);
				dpdMax = (Intbox) hbox.getLastChild();
				if (dpdMax.getValue() == null) {
					throw new WrongValueException(dpdMax,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Max DPD " }));
				}
				if (Integer.valueOf(dpdMax.getValue()) < 0) {
					throw new WrongValueException(dpdMin,
							Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] { "Max DPD " }));
				}
				assetClassSetupDetail.setDpdMax(dpdMax.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (maxValue > assetClassSetupDetail.getDpdMin()) {
					throw new WrongValueException(dpdMin, "Min DPD should be greater than above Max DPD");
				}
				if (assetClassSetupDetail.getDpdMin() > assetClassSetupDetail.getDpdMax()) {
					maxValue = assetClassSetupDetail.getDpdMin();
					throw new WrongValueException(dpdMin, "Min DPD should be lessthan Max DPD");
				} else {
					maxValue = assetClassSetupDetail.getDpdMax();
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				hbox = (Hbox) getComponent(listitem, 2);
				ExtendedCombobox assestClassCode = (ExtendedCombobox) hbox.getLastChild();
				AssetClassCode object = (AssetClassCode) assestClassCode.getObject();
				Clients.clearWrongValue(assestClassCode);
				String code = assestClassCode.getValue();
				if (StringUtils.isEmpty(code)) {
					throw new WrongValueException(assestClassCode,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Asset Classification " }));
				}
				assetClassSetupDetail.setClassID(object.getId());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				hbox = (Hbox) getComponent(listitem, 3);
				ExtendedCombobox assestClassSubCode = (ExtendedCombobox) hbox.getLastChild();
				AssetSubClassCode object = (AssetSubClassCode) assestClassSubCode.getObject();
				Clients.clearWrongValue(assestClassSubCode);
				String code = assestClassSubCode.getValue();
				if (StringUtils.isEmpty(code)) {
					throw new WrongValueException(assestClassSubCode,
							Labels.getLabel("FIELD_IS_MAND", new String[] { "Asset Sub-Classification " }));
				}
				assetClassSetupDetail.setSubClassID(object.getId());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Checkbox npaStage = (Checkbox) getComponent(listitem, 4);
				assetClassSetupDetail.setNpaStage(npaStage.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				hbox = (Hbox) getComponent(listitem, 5);
				Intbox npaAge = (Intbox) hbox.getFirstChild();
				assetClassSetupDetail.setNpaAge(npaAge.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			boolean isNew = assetClassSetupDetail.isNewRecord();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(assetClassSetupDetail.getRecordType())) {
					assetClassSetupDetail.setVersion(assetClassSetupDetail.getVersion() + 1);
					if (isNew) {
						assetClassSetupDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						assetClassSetupDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						assetClassSetupDetail.setNewRecord(true);
					}
				}
			} else {
				assetClassSetupDetail.setVersion(assetClassSetupDetail.getVersion() + 1);
			}

			assetClassSetupDetail.setRecordStatus(this.recordStatus.getValue());

			assetClassSetupDetailList.add(assetClassSetupDetail);
		}
		acsh.setDetails(assetClassSetupDetailList);
		return wve;
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

	private AuditHeader getAuditHeader(AssetClassSetupHeader assetClassSetupHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, assetClassSetupHeader.getBefImage(),
				assetClassSetupHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, getUserWorkspace().getLoggedInUser(),
				getOverideMap());
	}

	public void setAssetClassSetupService(AssetClassSetupService assetClassSetupService) {
		this.assetClassSetupService = assetClassSetupService;
	}

}
