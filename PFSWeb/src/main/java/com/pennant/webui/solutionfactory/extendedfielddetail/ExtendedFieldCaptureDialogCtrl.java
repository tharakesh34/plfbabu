package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.UserWorkspace;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.util.ErrorControl;
import com.pennant.webui.finance.financemain.ExtendedFieldMaintenanceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtendedFieldCaptureDialogCtrl extends GFCBaseCtrl<ExtendedFieldRender> {

	private static final long serialVersionUID = -8108473227202001840L;
	private static final Logger logger = LogManager.getLogger(ExtendedFieldCaptureDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExtendedFieldCaptureDialog;
	protected Tabpanel extendedFieldTabPanel;
	protected Intbox seqNo;
	protected Button btnDelete;

	private ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl;
	private ExtendedFieldMaintenanceDialogCtrl extendedFieldMaintenanceDialogCtrl;
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private ExtendedFieldsGenerator generator;
	private List<ExtendedFieldRender> extendedList = null;
	private int ccyFormat = 0;
	private boolean newRecord = false;
	private boolean isReadOnly = false;
	private boolean newExtendedField = false;
	private String moduleType = "";
	private ScriptValidationService scriptValidationService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private Combobox loanSerEvent;

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldCaptureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldCaptureDialog";
	}

	/**
	 * Method for creating window
	 * 
	 * @param event
	 */
	public void onCreate$window_ExtendedFieldCaptureDialog(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ExtendedFieldCaptureDialog);

		if (arguments.containsKey("extendedFieldRenderDialogCtrl")) {
			setExtendedFieldRenderDialogCtrl(
					(ExtendedFieldRenderDialogCtrl) arguments.get("extendedFieldRenderDialogCtrl"));
			setNewExtendedField(true);
		}
		if (arguments.containsKey("extendedFieldMaintenanceDialogCtrl")) {
			setExtendedFieldMaintenanceDialogCtrl(
					(ExtendedFieldMaintenanceDialogCtrl) arguments.get("extendedFieldMaintenanceDialogCtrl"));
			setNewExtendedField(true);
		}

		if (arguments.containsKey("eventName")) {
			loanSerEvent = (Combobox) arguments.get("eventName");
		}
		if (arguments.containsKey("extendedFieldHeader")) {
			setExtendedFieldHeader((ExtendedFieldHeader) arguments.get("extendedFieldHeader"));
		}
		if (arguments.containsKey("extendedFieldRender")) {
			setExtendedFieldRender((ExtendedFieldRender) arguments.get("extendedFieldRender"));
		}
		if (arguments.containsKey("ccyFormat")) {
			ccyFormat = (int) arguments.get("ccyFormat");
		}
		if (arguments.containsKey("newRecord")) {
			newRecord = (Boolean) arguments.get("newRecord");
		}
		if (arguments.containsKey("isReadOnly")) {
			isReadOnly = (Boolean) arguments.get("isReadOnly");
		}
		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}

		// Extended Field Details auto population / Rendering into Screen
		generator = new ExtendedFieldsGenerator();

		if (arguments.containsKey("isCommodity")) {
			generator.setCommodity((boolean) arguments.get("isCommodity"));
		}

		if (arguments.containsKey("hsnCodes")) {
			generator.setHsnCodes((List<String>) arguments.get("hsnCodes"));
		}
		if (arguments.containsKey("moduleDefiner")) {
			generator.setModuleDefiner((String) arguments.get("moduleDefiner"));
		}

		if (arguments.containsKey("roleCode")) {
			generator.setUserRole((String) arguments.get("roleCode"));
			setRole((String) arguments.get("roleCode"));
		}

		if (arguments.containsKey("userWorkspace")) {
			generator.setUserWorkspace((UserWorkspace) arguments.get("userWorkspace"));
			setUserWorkspace((UserWorkspace) arguments.get("userWorkspace"));
		}

		getUserWorkspace().allocateAuthorities(pageRightName, getRole());
		try {
			doShowDialog();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);
		generator.setWindow(this.window_ExtendedFieldCaptureDialog);
		generator.setTabpanel(extendedFieldTabPanel);
		generator.setRowWidth(260);
		generator.setExtendedFieldDetailsService(extendedFieldDetailsService);
		this.extendedFieldTabPanel.setHeight("100%");
		generator.setCcyFormat(ccyFormat);
		if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);

			generator.setReadOnly(true);
		} else if (PennantConstants.MODULETYPE_MAINT.equals(moduleType)) {
			this.btnDelete.setVisible(false);
			// generator.setUserWorkspace(getUserWorkspace());
		} else {
			this.btnSave.setVisible(isReadOnly);

			generator.setReadOnly(!isReadOnly);
		}

		if (!PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldCaptureDialog_btnSave"));
		}

		// Pre-Validation Checking & Setting Defaults
		Map<String, Object> fieldValuesMap = null;
		if (extendedFieldRender.getMapValues() != null) {
			fieldValuesMap = extendedFieldRender.getMapValues();
		}

		if (newRecord) {
			// get pre-validation script if record is new
			String preValidationScript = extendedFieldRenderDialogCtrl.getPreValidationScript();
			if (StringUtils.isNotEmpty(preValidationScript)) {
				ScriptErrors defaults = scriptValidationService.setPreValidationDefaults(preValidationScript,
						fieldValuesMap);

				// Initiation of Field Value Map
				if (fieldValuesMap == null) {
					fieldValuesMap = new HashMap<>();
				}

				// Overriding Default values
				List<ScriptError> defaultList = defaults.getAll();
				for (int i = 0; i < defaultList.size(); i++) {
					ScriptError dftKeyValue = defaultList.get(i);

					if (fieldValuesMap.containsKey(dftKeyValue.getProperty())) {
						fieldValuesMap.remove(dftKeyValue.getProperty());
					}
					fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
				}
			}
			this.btnDelete.setVisible(false);
		}
		if (extendedFieldRender.getRecordStatus() != null) {
			if (extendedFieldRender.getRecordStatus().equals(PennantConstants.RCD_STATUS_SUBMITTED)) {
				this.btnDelete.setVisible(false);
			}
		}
		if (fieldValuesMap != null) {
			generator.setFieldValueMap((Map<String, Object>) fieldValuesMap);
		}

		this.seqNo.setValue(extendedFieldRender.getSeqNo());
		generator.setOverflow(true);
		generator.renderWindow(extendedFieldHeader, newRecord);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) throws AppException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
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

	protected boolean doCustomDelete(final ExtendedFieldRender aExtendedFieldRender, String tranType) {
		if (newExtendedField) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newIRRFeeTypeDetailProcess(aExtendedFieldRender, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldCaptureDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (extendedFieldRenderDialogCtrl != null) {
					extendedFieldRenderDialogCtrl.doFillExtendedFieldDetails(this.extendedList);
				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ExtendedFieldRender aExtendedFieldRender = new ExtendedFieldRender();
		BeanUtils.copyProperties(extendedFieldRender, aExtendedFieldRender);

		doDelete(String.valueOf(aExtendedFieldRender.getSeqNo()), aExtendedFieldRender);

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
	private AuditHeader newIRRFeeTypeDetailProcess(ExtendedFieldRender aExtendedFieldRender, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aExtendedFieldRender, tranType);
		this.extendedList = new ArrayList<ExtendedFieldRender>();
		String seqNO = String.valueOf(aExtendedFieldRender.getSeqNo());
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = seqNO;
		errParm[0] = PennantJavaUtil.getLabel("label_ExtendedFieldCaptureDialog_SeqNo.value") + ": " + valueParm[0];

		List<ExtendedFieldRender> extendedFieldRenderList = null;
		if (extendedFieldRenderDialogCtrl != null) {
			extendedFieldRenderList = extendedFieldRenderDialogCtrl.getExtendedFieldRenderList();
		}
		if (extendedFieldRenderList != null && !extendedFieldRenderList.isEmpty()) {
			for (ExtendedFieldRender details : extendedFieldRenderList) {
				if (aExtendedFieldRender.getSeqNo() == details.getSeqNo()) {
					duplicateRecord = true;
				}
				if (duplicateRecord) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(details.getRecordType())) {
							details.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.extendedList.add(details);
						} else if (PennantConstants.RCD_ADD.equals(details.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(details.getRecordType())) {
							details.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.extendedList.add(details);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(details.getRecordType())) {
							recordAdded = true;
						}
					}
				} else {
					this.extendedList.add(details);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.extendedList.add(aExtendedFieldRender);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.extendedList.add(aExtendedFieldRender);
		}
		return auditHeader;
	}

	public void doSave() throws AppException {
		logger.debug(Literal.ENTERING);

		final ExtendedFieldRender aExetendedFieldRender = extendedFieldRender;
		boolean isNew = false;

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		Map<String, Object> fielValueMap = generator.doSave(extendedFieldHeader.getExtendedFieldDetails(), false);
		aExetendedFieldRender.setMapValues(fielValueMap);

		// Post Validations for the Extended fields
		if (ObjectUtils.isEmpty(extendedFieldMaintenanceDialogCtrl)) {
			String postValidationScript = extendedFieldRenderDialogCtrl.getPostValidationScript();
			if (StringUtils.isNotEmpty(postValidationScript)) {
				ScriptErrors postValidationErrors = scriptValidationService
						.getPostValidationErrors(postValidationScript, fielValueMap);

				// Preparing Wrong Value User UI exceptions
				showErrorDetails(postValidationErrors);
			}
		}

		isNew = aExetendedFieldRender.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExetendedFieldRender.getRecordType())) {
				aExetendedFieldRender.setVersion(aExetendedFieldRender.getVersion() + 1);
				if (isNew) {
					aExetendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExetendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExetendedFieldRender.setNewRecord(true);
				}
			}
		} else {

			if (isNewRecord()) {
				aExetendedFieldRender.setVersion(1);
				aExetendedFieldRender.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aExetendedFieldRender.getRecordType())) {
				aExetendedFieldRender.setVersion(aExetendedFieldRender.getVersion() + 1);
				aExetendedFieldRender.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			AuditHeader auditHeader = null;
			if (PennantConstants.MODULETYPE_MAINT.equals(moduleType)) {
				auditHeader = extFieldsMaintProcess(aExetendedFieldRender, tranType);
			} else {
				auditHeader = newFieldProcess(aExetendedFieldRender, tranType);
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldCaptureDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {

				// Fields Rendering in List box dynamically
				if (PennantConstants.MODULETYPE_MAINT.equals(moduleType)) {
					extendedFieldMaintenanceDialogCtrl.doFillExtendedDetails(extendedList, extendedFieldHeader);
				} else {
					extendedFieldRenderDialogCtrl.doFillExtendedFieldDetails(extendedList);
				}

				// Close Dialog Window
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			showMessage(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader newFieldProcess(ExtendedFieldRender aExetendedFieldRender, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aExetendedFieldRender, tranType);
		extendedList = new ArrayList<ExtendedFieldRender>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aExetendedFieldRender.getSeqNo());
		// valueParm[1] = aExetendedFieldRender.getCustRatingType();

		errParm[0] = PennantJavaUtil.getLabel("") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("") + ":" + valueParm[1];

		List<ExtendedFieldRender> extFldList = extendedFieldRenderDialogCtrl.getExtendedFieldRenderList();
		if (CollectionUtils.isNotEmpty(extFldList)) {
			for (ExtendedFieldRender fieldRender : extFldList) {
				if (fieldRender.getSeqNo() == aExetendedFieldRender.getSeqNo()) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aExetendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							extendedList.add(aExetendedFieldRender);
						} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aExetendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							extendedList.add(aExetendedFieldRender);
						} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (ExtendedFieldRender efr : extFldList) {
								if (efr.getSeqNo() == aExetendedFieldRender.getSeqNo()) {
									extendedList.add(efr);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							extendedList.add(fieldRender);
						}
					}
				} else {
					extendedList.add(fieldRender);
				}
			}
		}
		if (!recordAdded) {
			extendedList.add(aExetendedFieldRender);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader extFieldsMaintProcess(ExtendedFieldRender aExetendedFieldRender, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aExetendedFieldRender, tranType);
		extendedList = new ArrayList<ExtendedFieldRender>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aExetendedFieldRender.getSeqNo());

		errParm[0] = PennantJavaUtil.getLabel("") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("") + ":" + valueParm[1];

		if (extendedFieldMaintenanceDialogCtrl.getExtendedFieldRenderList() != null
				&& !extendedFieldMaintenanceDialogCtrl.getExtendedFieldRenderList().isEmpty()) {

			for (int i = 0; i < extendedFieldMaintenanceDialogCtrl.getExtendedFieldRenderList().size(); i++) {
				ExtendedFieldRender fieldRender = extendedFieldMaintenanceDialogCtrl.getExtendedFieldRenderList()
						.get(i);

				if (fieldRender.getSeqNo() == aExetendedFieldRender.getSeqNo()) {

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aExetendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							extendedList.add(aExetendedFieldRender);
						} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aExetendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							extendedList.add(aExetendedFieldRender);
						} else if (aExetendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < extendedFieldMaintenanceDialogCtrl.getExtendedFieldRenderList()
									.size(); j++) {
								ExtendedFieldRender render = extendedFieldMaintenanceDialogCtrl
										.getExtendedFieldRenderList().get(j);
								if (render.getSeqNo() == aExetendedFieldRender.getSeqNo()) {
									extendedList.add(render);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							extendedList.add(fieldRender);
						}
					}
				} else {
					extendedList.add(fieldRender);
				}
			}
		}
		if (!recordAdded) {
			extendedList.add(aExetendedFieldRender);
		}
		loanSerEvent.setDisabled(true);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for Showing UI Post validation Errors
	 * 
	 * @param postValidationErrors
	 */
	public void showErrorDetails(ScriptErrors postValidationErrors) {
		List<ScriptError> errorList = postValidationErrors.getAll();
		if (errorList == null || errorList.isEmpty()) {
			return;
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		for (int i = 0; i < errorList.size(); i++) {
			ScriptError error = errorList.get(i);

			if (extendedFieldTabPanel.getFellowIfAny("ad_" + error.getProperty()) != null) {
				Component component = extendedFieldTabPanel.getFellowIfAny("ad_" + error.getProperty());
				if (component instanceof CurrencyBox) {
					CurrencyBox box = (CurrencyBox) component;
					component = box.getErrorComp();
				}
				WrongValueException we = new WrongValueException(component, error.getValue());
				wve.add(we);
			}
		}

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}

	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ExtendedFieldRender aExetendedFieldRender, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null, aExetendedFieldRender);

		return new AuditHeader(getReference(), String.valueOf(aExetendedFieldRender.getSeqNo()), null, null,
				auditDetail, getUserWorkspace().getLoggedInUser(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug(Literal.ENTERING);
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ExtendedFieldCaptureDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(Literal.EXCEPTION, exp);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for entering Multiple Selection values in to Text box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onMultiSelButtonClick(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		List<Object> list = (List<Object>) event.getData();

		Textbox textbox = (Textbox) list.get(0);
		ExtendedFieldDetail detail = (ExtendedFieldDetail) list.get(1);

		Map<String, Object> selectedValues = new HashMap<String, Object>();
		Object dataObject = ExtendedMultipleSearchListBox.show(this.window_ExtendedFieldCaptureDialog,
				detail.getFieldList(), selectedValues);
		if (dataObject instanceof String) {
			textbox.setValue(dataObject.toString());
		} else {
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String multivalues = details.keySet().toString();
				textbox.setValue(multivalues.replace("[", "").replace("]", " "));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Extended fields button
	 */
	public void onClickExtbtnOPENURLBUTTON() {
		logger.debug(Literal.ENTERING);
		try {
			if (generator == null) {
				return;
			}
			if (generator.getWindow() == null) {
				return;
			}

			Component component = null;

			component = generator.getWindow().getFellowIfAny("ad_KRAMANURL");
			if (component == null) {
				return;
			}

			Textbox url = (Textbox) component;
			String urlVal = url.getValue();

			Executions.getCurrent().sendRedirect(urlVal, "_blank");

		} catch (Exception e) {
			{
				if (e.getLocalizedMessage() != null) {
					MessageUtil.showError(e.getLocalizedMessage());
				} else {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public void setExtendedFieldRenderDialogCtrl(ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl) {
		this.extendedFieldRenderDialogCtrl = extendedFieldRenderDialogCtrl;
	}

	public void setExtendedList(List<ExtendedFieldRender> extendedList) {
		this.extendedList = extendedList;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

	public void setNewExtendedField(boolean newExtendedField) {
		this.newExtendedField = newExtendedField;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldMaintenanceDialogCtrl(
			ExtendedFieldMaintenanceDialogCtrl extendedFieldMaintenanceDialogCtrl) {
		this.extendedFieldMaintenanceDialogCtrl = extendedFieldMaintenanceDialogCtrl;
	}
}
