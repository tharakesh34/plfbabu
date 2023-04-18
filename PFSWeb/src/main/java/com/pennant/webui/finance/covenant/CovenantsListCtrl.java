/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinCovenantTypeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.covenant;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.finance.FinCovenantMaintanceService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinCovenantFileUploadResponce;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennapps.core.util.ObjectUtil;

public class CovenantsListCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = LogManager.getLogger(CovenantsListCtrl.class);

	protected Window covenantListWindow;
	protected Button btnNew_NewFinCovenantType;
	protected Listbox listBoxFinCovenantType;

	protected North north;
	protected South south;

	private FinanceDetail financedetail;
	private FinanceMainBaseCtrl financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private List<Covenant> covenants = new ArrayList<Covenant>();
	private int ccyFormat = 0;
	private transient boolean recSave = false;
	private String roleCode = "";
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private String allowedRoles;

	private boolean isNotFinanceProcess = false;
	private ArrayList<Object> headerList;
	private String moduleCode;
	private LegalDetail legalDetail;
	private Label window_FinCovenantTypeList_title;

	private FinMaintainInstruction finMaintainInstruction;
	private FinanceSelectCtrl financeSelectCtrl = null;

	private transient FinCovenantMaintanceService finCovenantMaintanceService;
	private String module;
	protected boolean disbEnquiry = false;
	protected Combobox enquiryCombobox;

	// File Upload functionality in Covenants
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Button btnImport;
	FinCovenantTypeService finCovenantTypeService;
	@Autowired(required = false)
	private transient FinCovenantFileUploadResponce finCovenantFileUploadResponce;

	private transient Media media = null;
	private File file = null;

	protected transient DataEngineConfig dataEngineConfig;

	private long userId;
	private Configuration config;

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public DataEngineConfig getDataEngineConfig() {
		return dataEngineConfig;
	}

	public CovenantsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CovenantDialog";
	}

	// Component Events

	@SuppressWarnings("unchecked")
	public void onCreate$covenantListWindow(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(covenantListWindow);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				if (arguments.get("financeMainDialogCtrl") instanceof FinanceMainBaseCtrl) {
					financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
					financeMainDialogCtrl.setFinCovenantTypeListCtrl(this);
					this.covenantListWindow.setTitle("");
					setNewFinance(true);
				}
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, this.pageRightName);
			}

			if (arguments.containsKey("ccyFormatter")) {
				ccyFormat = Integer.parseInt(arguments.get("ccyFormatter").toString());
			}

			if (arguments.containsKey("parentTab")) {
				parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("financeDetail")) {
				financedetail = (FinanceDetail) arguments.get("financeDetail");
				if (financedetail != null) {
					if (financedetail.getCovenants() != null) {
						setCovenants(financedetail.getCovenants());
					}
				}
			}

			if (arguments.containsKey("allowedRoles")) {
				allowedRoles = (String) arguments.get("allowedRoles");
			} else {
				allowedRoles = "";
			}

			if (arguments.containsKey("moduleCode")) {
				this.moduleCode = (String) arguments.get("moduleCode");
				finMaintainInstruction = (FinMaintainInstruction) arguments.get("finMaintainInstruction");

				financeSelectCtrl = (FinanceSelectCtrl) arguments.get("financeSelectCtrl");

				// Store the before image.
				FinMaintainInstruction finMaintainInstruction = new FinMaintainInstruction();
				BeanUtils.copyProperties(this.finMaintainInstruction, finMaintainInstruction);
				this.finMaintainInstruction.setBefImage(finMaintainInstruction);

				doLoadWorkFlow(this.finMaintainInstruction.isWorkflow(), this.finMaintainInstruction.getWorkflowId(),
						this.finMaintainInstruction.getNextTaskId());

				if (isWorkFlowEnabled()) {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), this.pageRightName, this.pageRightName);
					this.btnNotes.setVisible(true);
				} else {
					this.south.setHeight("0px");
				}
			}

			if (arguments.containsKey("legalDetail")) {
				legalDetail = (LegalDetail) arguments.get("legalDetail");
			}

			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
			}

			if (arguments.containsKey("finHeaderList")) {
				headerList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}
			if (arguments.containsKey("disbEnquiry")) {
				disbEnquiry = (boolean) arguments.get("disbEnquiry");
			}

			enquiryCombobox = (Combobox) arguments.get("enuiryCombobox");

			doEdit();

			doCheckRights();

			doSetFieldProperties();

			doShowDialog();

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		if (isNotFinanceProcess) {
			window_FinCovenantTypeList_title.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, roleCode);

		this.btnNew_NewFinCovenantType.setVisible(getUserWorkspace().isAllowed("button_CovenantDialog_btnNew"));
		this.btnFileUpload.setVisible(getUserWorkspace().isAllowed("button_CovenantDialog_btnNew"));
		this.btnImport.setVisible(getUserWorkspace().isAllowed("button_CovenantDialog_btnNew"));

		if (enqiryModule) {
			this.btnNew_NewFinCovenantType.setVisible(false);
			this.btnFileUpload.setVisible(false);
			this.btnImport.setVisible(false);
		}

		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (finMaintainInstruction != null && StringUtils.isNotBlank(finMaintainInstruction.getRecordType())) {
				this.btnNotes.setVisible(true);
			}

			this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (FinServiceEvent.COVENANTS.equals(moduleCode)) {
			north.setVisible(true);
			south.setVisible(true);
		}

		try {

			if (!enqiryModule) {
				appendFinBasicDetails();
			}

			doCheckEnquiry();

			if (isWorkFlowEnabled() && !this.finMaintainInstruction.isNewRecord()) {
				this.btnNotes.setVisible(true);
				doEdit();
			}

			try {
				doCheckEnquiry();
				doWriteBeanToComponents();

				this.listBoxFinCovenantType.setHeight(borderLayoutHeight - 226 + "px");
				if (parent != null) {
					this.covenantListWindow.setHeight(borderLayoutHeight - 75 + "px");
					parent.appendChild(this.covenantListWindow);
				} else if (disbEnquiry) {
					north.setVisible(true);
					this.btnSave.setVisible(false);
					appendFinBasicDetails();
					setDialog(DialogType.MODAL);
				} else {
					setDialog(DialogType.EMBEDDED);
				}

			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * 
	 */
	public void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);

		doFillCovenants(getCovenants());
		if (CollectionUtils.isNotEmpty(getCovenants())) {
			for (Covenant covenantType : getCovenants()) {
				Covenant befImage = new Covenant();
				BeanUtils.copyProperties(covenantType, befImage);
				if (finMaintainInstruction != null) {
					if (finMaintainInstruction.getRecordStatus() == null) {
						this.recordStatus.setValue(PennantConstants.RCD_STATUS_APPROVED);
					} else {
						this.recordStatus.setValue(finMaintainInstruction.getRecordStatus());
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckEnquiry() {
		if (enqiryModule) {
			this.btnNew_NewFinCovenantType.setVisible(false);
		}
	}

	@SuppressWarnings("unchecked")
	public void onCovenantTypeValidation(Event event) {
		logger.debug(Literal.ENTERING);

		String userAction = "";
		FinanceDetail finDetail = null;
		Map<String, Object> map = new HashMap<String, Object>();
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}

		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}

		if (map.containsKey("financeDetail")) {
			finDetail = (FinanceDetail) map.get("financeDetail");
		}

		recSave = false;
		if ("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction)
				|| "Reject".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction)) {
			recSave = true;
		}
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (!recSave) {
				FinanceMain main = null;
				if (financeMainDialogCtrl != null) {
					try {
						main = financeMainDialogCtrl.getFinanceMain();
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}
				if (this.listBoxFinCovenantType.getItems() != null
						&& !this.listBoxFinCovenantType.getItems().isEmpty()) {
					if (main != null && main.getFinAmount() != null) {

					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve);

		if (finDetail != null) {
			finDetail.setCovenants(covenants);
		}
		logger.debug(Literal.LEAVING);
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if (parentTab != null) {
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew_NewFinCovenantType(Event event) throws InterruptedException {
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		final Covenant aCovenant = new Covenant();

		if (isNotFinanceProcess) {
			aCovenant.setKeyReference(legalDetail.getLoanReference());
		} else {
			aCovenant.setKeyReference(financedetail.getFinScheduleData().getFinReference());
		}

		if (financedetail.getFinScheduleData().getFinanceMain().getMaturityDate() == null) {
			MessageUtil.showError("Loan maturity date cannot be blank, schedule must be generated");
			return;
		}

		aCovenant.setNewRecord(true);
		aCovenant.setWorkflowId(0);

		createComponents(aCovenant);
	}

	public void onFinCovenantTypeItemDoubleClicked(Event event) throws InterruptedException {
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		Listitem listitem = this.listBoxFinCovenantType.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final Covenant aCovenant = (Covenant) listitem.getAttribute("data");
			if (isDeleteRecord(aCovenant)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				aCovenant.setNewRecord(false);
				createComponents(aCovenant);
			}
		}
	}

	private void createComponents(Covenant aCovenant) {
		final Map<String, Object> map = new HashMap<String, Object>();

		if (!enqiryModule) {
			map.put("roleCode", roleCode);
			map.put("financeMainDialogCtrl", this.financeMainDialogCtrl);
			map.put("allowedRoles", allowedRoles);
		}

		map.put("newRecord", aCovenant.isNewRecord());
		map.put("covenant", aCovenant);
		map.put("ccyFormatter", ccyFormat);
		map.put("covenantsListCtrl", this);
		map.put("enqiryModule", enqiryModule);
		map.put("module", module);

		if (isNotFinanceProcess) {
			map.put("legalDetail", legalDetail);
		} else {
			map.put("financeDetail", financedetail);
		}

		map.put("isNotFinanceProcess", isNotFinanceProcess);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Covenant/CovenantsDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void doFillCovenants(List<Covenant> covenants) {
		this.listBoxFinCovenantType.getItems().clear();
		setCovenants(covenants);
		if (covenants != null && !covenants.isEmpty()) {
			for (Covenant detail : covenants) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(detail.getCovenantTypeCode()));
				lc.setParent(item);
				lc = new Listcell(detail.getMandatoryRole());
				lc.setParent(item);

				Checkbox cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAllowWaiver());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isPdd());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isOtc());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);

				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinCovenantTypeItemDoubleClicked");
				this.recordStatus.setValue(detail.getRecordStatus());
				this.listBoxFinCovenantType.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean isDeleteRecord(Covenant aCovenant) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aCovenant.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aCovenant.getRecordType())) {
			return true;
		}

		if (isNotFinanceProcess && !CollateralConstants.LEGAL_MODULE.equals(aCovenant.getModule())) {
			return true;
		}
		return false;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", headerList);
			map.put("moduleName", moduleCode);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.financedetail.getFinScheduleData().getFinanceMain());
	}

	@Override
	public String getReference() {
		return this.financedetail.getFinScheduleData().getFinanceMain().getFinReference();
	}

	protected void doSave() {
		logger.debug(Literal.ENTERING);

		FinMaintainInstruction aFinMaintainInstruction = new FinMaintainInstruction();
		aFinMaintainInstruction = ObjectUtil.clone(finMaintainInstruction);

		doWriteComponentsToBean(aFinMaintainInstruction);

		boolean isNew;
		isNew = aFinMaintainInstruction.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinMaintainInstruction.getRecordType())) {
				aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
				if (isNew) {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinMaintainInstruction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinMaintainInstruction.setNewRecord(true);
				}
			}
		} else {
			aFinMaintainInstruction.setVersion(aFinMaintainInstruction.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFinMaintainInstruction, tranType)) {
				refreshList();

				String msg = PennantApplicationUtil.getSavingStatus(aFinMaintainInstruction.getRoleCode(),
						aFinMaintainInstruction.getNextRoleCode(), aFinMaintainInstruction.getFinReference() + "",
						" Covenant Details ", aFinMaintainInstruction.getRecordStatus());
				if (StringUtils.equals(aFinMaintainInstruction.getRecordStatus(),
						PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Covenant Detail with Reference " + aFinMaintainInstruction.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = financeSelectCtrl.getSearchObj(true);
		financeSelectCtrl.getPagingFinanceList().setActivePage(0);
		financeSelectCtrl.getPagedListWrapper().setSearchObject(soFinanceMain);
		if (financeSelectCtrl.getListBoxFinance() != null) {
			financeSelectCtrl.getListBoxFinance().getListModel();
		}
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinMaintainInstruction (FinMaintainInstruction)
	 * 
	 * @param tranType                (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFinMaintainInstruction.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinMaintainInstruction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinMaintainInstruction.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFinMaintainInstruction.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinMaintainInstruction.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinMaintainInstruction);
				}

				if (isNotesMandatory(taskId, aFinMaintainInstruction)) {
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

			aFinMaintainInstruction.setTaskId(taskId);
			aFinMaintainInstruction.setNextTaskId(nextTaskId);
			aFinMaintainInstruction.setRoleCode(getRole());
			aFinMaintainInstruction.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			String operationRefs = getServiceOperations(taskId, aFinMaintainInstruction);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinMaintainInstruction, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinMaintainInstruction, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinMaintainInstruction
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinMaintainInstruction aFinMaintainInstruction, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinMaintainInstruction.getBefImage(),
				aFinMaintainInstruction);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinMaintainInstruction.getUserDetails(),
				getOverideMap());
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
		AuditHeader aAuditHeader = auditHeader;
		FinMaintainInstruction aFinMaintainInstruction = (FinMaintainInstruction) aAuditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = finCovenantMaintanceService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = finCovenantMaintanceService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = finCovenantMaintanceService.doApprove(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = finCovenantMaintanceService.doReject(aAuditHeader);

					if (aFinMaintainInstruction.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.covenantListWindow, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.covenantListWindow, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.finMaintainInstruction), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	private void doCancel() {
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(FinMaintainInstruction finMaintainInstruction) {
		logger.debug(Literal.ENTERING);

		List<Covenant> covenants = finMaintainInstruction.getCovenants();

		if (CollectionUtils.isNotEmpty(covenants)) {
			doFillCovenants(finMaintainInstruction.getCovenants());
			for (Covenant covenant : covenants) {
				Covenant befImage = new Covenant();
				BeanUtils.copyProperties(covenant, befImage);
				covenant.setBefImage(befImage);
			}
		}
		this.recordStatus.setValue(finMaintainInstruction.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinMaintainInstruction
	 */
	public void doWriteComponentsToBean(FinMaintainInstruction finMaintainInstruction) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = financedetail.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		finMaintainInstruction.setFinID(fm.getFinID());
		finMaintainInstruction.setFinReference(fm.getFinReference());
		finMaintainInstruction.setEvent(this.moduleCode);

		// List
		finMaintainInstruction.setCovenants(getCovenants());

		List<Covenant> covenants = getCovenants();
		for (Covenant covenant : covenants) {
			finMaintainInstruction.setRecordStatus(covenant.getRecordStatus());
		}

		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (disbEnquiry && enquiryCombobox != null) {
			this.enquiryCombobox.setSelectedIndex(0);
			this.covenantListWindow.onClose();
		} else {
			doClose(this.btnSave.isVisible());
		}
	}

	public void onClick$btnImport(Event event) throws InterruptedException {
		this.btnImport.setDisabled(true);
		if (media == null) {
			MessageUtil.showError("Please upload file.");
			return;
		}
		List<DocumentType> documentData = new ArrayList<>();
		try {
			try {

				DataEngineStatus dataEngineStatus = new DataEngineStatus(
						PennantConstants.NEWCOVENANTS_UPLOADBY_REFERENCE);

				List<Covenant> responceData = finCovenantFileUploadResponce.covenantFileUploadResponceData(this.userId,
						dataEngineStatus, file, media, false, allowedRoles.split(";"), documentData, financedetail);

				StringBuilder exceptions = new StringBuilder();
				if ("S".equals(dataEngineStatus.getStatus()) && dataEngineStatus.getDataEngineLogList() != null) {
					dataEngineStatus.getDataEngineLogList();

					for (DataEngineLog dsLog : dataEngineStatus.getDataEngineLogList()) {

						if (exceptions.length() > 0) {
							exceptions.append("\n");
						}

						exceptions.append(dsLog.getKeyId() + " " + dsLog.getReason());
					}
				}

				if (StringUtils.isNotBlank(exceptions.toString())) {
					MessageUtil.showError(exceptions.toString());
					return;
				}

				if (responceData != null) {
					for (int i = 0; i < covenants.size(); i++) {
						for (int j = 0; j < responceData.size(); j++) {
							if (covenants.get(i).getCovenantTypeCode()
									.equals(responceData.get(j).getCovenantTypeCode())) {
								throw new AppException(
										"Covenant Type Already Exists :" + responceData.get(j).getCovenantType());
							}
						}
					}
					responceData.addAll(covenants);
					doFillCovenants(responceData);

				}
			} catch (Exception e) {
				MessageUtil.showError(e.getMessage());
				return;
			}
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
			return;
		}

	}

	public void onUpload$btnFileUpload(UploadEvent event) {
		logger.debug(Literal.ENTERING);
		try {
			if (config == null) {
				this.config = dataEngineConfig.getConfigurationByName("NEWCOVENANTS_UPLOADBY_REFERENCE");
			}
			if (config == null) {
				return;
			}
			// Clear the file name.
			this.fileName.setText("");

			// Get the media of the selected file.
			media = event.getMedia();

			if (!PennantAppUtil.uploadDocFormatValidation(media)) {
				return;
			}
			String mediaName = media.getName();

			// Get the selected configuration details.
			String prefix = config.getFilePrefixName();
			String extension = config.getFileExtension();

			// Validate the file extension.
			if (!(StringUtils.endsWithIgnoreCase(mediaName, extension))) {
				MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));

				media = null;
				return;
			}

			// Validate the file prefix.
			if (prefix != null && !(StringUtils.startsWith(mediaName, prefix))) {
				MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));

				media = null;
				return;
			}

			this.fileName.setText(mediaName);
			this.btnImport.setDisabled(false);
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
			return;
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public List<Covenant> getCovenants() {
		return covenants;
	}

	public void setCovenants(List<Covenant> covenants) {
		this.covenants = covenants;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setFinCovenantMaintanceService(FinCovenantMaintanceService finCovenantMaintanceService) {
		this.finCovenantMaintanceService = finCovenantMaintanceService;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}
