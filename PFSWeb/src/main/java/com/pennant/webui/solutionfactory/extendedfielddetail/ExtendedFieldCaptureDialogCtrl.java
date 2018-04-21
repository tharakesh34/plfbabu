package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtendedFieldCaptureDialogCtrl extends	GFCBaseCtrl<ExtendedFieldHeader> {

	private static final long serialVersionUID = -8108473227202001840L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldCaptureDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExtendedFieldCaptureDialog;
	protected Tabpanel extendedFieldTabPanel;
	protected Intbox seqNo;
	protected Button btnDelete;

	private ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl;
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private ExtendedFieldsGenerator generator;
	private List<ExtendedFieldRender> extendedList = null;
	private int ccyFormat = 0;
	private long queryId = 0;
	private String querySubCode = "";
	private String queryCode = "";
	private boolean newRecord = false;
	private boolean isReadOnly = false;
	private boolean	newExtendedField	      = false;
	private String moduleType = "";
	private ScriptValidationService scriptValidationService;
	private DedupParmService dedupParmService;

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldCaptureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Method for creating window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldCaptureDialog(Event event)
			throws Exception {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_ExtendedFieldCaptureDialog);

		if (arguments.containsKey("extendedFieldRenderDialogCtrl")) {
			setExtendedFieldRenderDialogCtrl((ExtendedFieldRenderDialogCtrl) arguments.get("extendedFieldRenderDialogCtrl"));
			setNewExtendedField(true);
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
		if (arguments.containsKey("queryId")) {
			queryId = (long) arguments.get("queryId");
		}
		if (arguments.containsKey("querySubCode")) {
			querySubCode = (String) arguments.get("querySubCode");
		}
		if (arguments.containsKey("queryCode")) {
			queryCode = (String) arguments.get("queryCode");
		}
		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		
		// Extended Field Details auto population / Rendering into Screen
		generator = new ExtendedFieldsGenerator();
		generator.setWindow(this.window_ExtendedFieldCaptureDialog);
		generator.setTabpanel(extendedFieldTabPanel);
		generator.setRowWidth(260);
		this.extendedFieldTabPanel.setHeight((borderLayoutHeight-175)+"px");
		generator.setCcyFormat(ccyFormat);
		if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);

			generator.setReadOnly(true);
		} else {
			this.btnSave.setVisible(isReadOnly);

			generator.setReadOnly(!isReadOnly);
		}
		
		//Pre-Validation Checking & Setting Defaults
		Map<String, Object> fieldValuesMap = null;
		if (getExtendedFieldRender().getMapValues() != null) {
			fieldValuesMap = getExtendedFieldRender().getMapValues();
		}
		
		if (newRecord) {
			
			//get pre-validation script if record is new
			String preValidationScript = getExtendedFieldRenderDialogCtrl().getPreValidationScript();
			if(StringUtils.isNotEmpty(preValidationScript)){
				ScriptErrors defaults = getScriptValidationService().setPreValidationDefaults(preValidationScript, fieldValuesMap);

				// Initiation of Field Value Map
				if(fieldValuesMap == null){
					fieldValuesMap = new HashMap<>();
				}

				// Overriding Default values
				List<ScriptError> defaultList = defaults.getAll();
				for (int i = 0; i < defaultList.size(); i++) {
					ScriptError dftKeyValue = defaultList.get(i);

					if(fieldValuesMap.containsKey(dftKeyValue.getProperty())){
						fieldValuesMap.remove(dftKeyValue.getProperty());
					}
					fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
				}
			}
			this.btnDelete.setVisible(false);
		}
		if(getExtendedFieldRender().getRecordStatus()!=null){
			if(getExtendedFieldRender().getRecordStatus().equals(PennantConstants.RCD_STATUS_SUBMITTED)){
				this.btnDelete.setVisible(false);
			}
		}
		if(fieldValuesMap != null){
			generator.setFieldValueMap((HashMap<String, Object>) fieldValuesMap);
		}

		try {
			this.seqNo.setValue(getExtendedFieldRender().getSeqNo());
			generator.renderWindow(getExtendedFieldHeader(), newRecord);
			
			// Height Calculation
			int height = borderLayoutHeight-100;
			this.window_ExtendedFieldCaptureDialog.setHeight(height+"px");
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 * @throws IOException
	 * @throws ScriptException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException,
			IllegalAccessException, InvocationTargetException, ParseException,
			ScriptException, IOException, NoSuchMethodException, NoSuchFieldException, SecurityException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event)  throws InterruptedException {
		logger.debug("Entering");
		doDelete();
		logger.debug("Leaving");
	}
	
	/**
	 * Deletes a ExtendedFieldRender object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final ExtendedFieldRender aExtendedFieldRender = new ExtendedFieldRender();
		BeanUtils.copyProperties(getExtendedFieldRender(), aExtendedFieldRender);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> Seq No : "
				+ (aExtendedFieldRender.getSeqNo());

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {

			if (StringUtils.isBlank(aExtendedFieldRender.getRecordType())) {

				aExtendedFieldRender.setVersion(aExtendedFieldRender.getVersion() + 1);
				aExtendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aExtendedFieldRender.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					aExtendedFieldRender.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewExtendedField()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newIRRFeeTypeDetailProcess(aExtendedFieldRender, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldCaptureDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (getExtendedFieldRenderDialogCtrl() != null) {
							getExtendedFieldRenderDialogCtrl().doFillExtendedFieldDetails(this.extendedList);
						}
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
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
		if (getExtendedFieldRenderDialogCtrl() != null) {
			extendedFieldRenderList = getExtendedFieldRenderDialogCtrl().getExtendedFieldRenderList();
		}
		if (extendedFieldRenderList != null && !extendedFieldRenderList.isEmpty()) {
			for (ExtendedFieldRender details : extendedFieldRenderList) {
				if (aExtendedFieldRender.getSeqNo() == details.getSeqNo()) {
					duplicateRecord = true;
				}
				if (duplicateRecord) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
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
	
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws IOException
	 * @throws ScriptException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void doSave() throws InterruptedException, ParseException,
			ScriptException, IOException, NoSuchMethodException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		logger.debug("Entering");
		
		final ExtendedFieldRender aExetendedFieldRender = getExtendedFieldRender();
		boolean isNew = false;

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		Map<String, Object> fielValueMap = generator.doSave(getExtendedFieldHeader().getExtendedFieldDetails(), false);
		aExetendedFieldRender.setMapValues(fielValueMap);
		
		if (this.queryId > 0) {
			DedupParm dedupParm = this.dedupParmService.getApprovedDedupParmById(this.queryCode, FinanceConstants.DEDUP_COLLATERAL, this.querySubCode);
			
			String sqlQuery = "Select T1.CollateralRef, T2.CUSTSHRTNAME From CollateralSetup_Temp T1"
					+ " Inner Join Customers T2 On T2.CustId = T1.DEPOSITORID" + " Inner Join Collateral_"
					+ this.querySubCode + "_ED_Temp  T3 On T3.REFERENCE = T1.COLLATERALREF " + dedupParm.getSQLQuery()
					+ " union all " 
					+ " Select T1.CollateralRef, T2.CUSTSHRTNAME From CollateralSetup T1"
					+ " Inner Join Customers T2 On T2.CustId = T1.DEPOSITORID" + " Inner Join Collateral_"
					+ this.querySubCode + "_ED  T3 On T3.REFERENCE = T1.COLLATERALREF " + dedupParm.getSQLQuery()
					+ " And NOT EXISTS (SELECT 1 FROM Collateral_" + this.querySubCode
					+ "_ED_TEMP  WHERE REFERENCE = T1.CollateralRef)";
			
			List<CollateralSetup> collateralSetupList =  this.dedupParmService.queryExecution(sqlQuery, fielValueMap);
			
			if(collateralSetupList != null && !collateralSetupList.isEmpty()) {
				boolean recordFound = true;
				
				if (collateralSetupList.size() == 1) {
					if (StringUtils.isNotBlank(aExetendedFieldRender.getReference()) && StringUtils.equals(
							collateralSetupList.get(0).getCollateralRef(), aExetendedFieldRender.getReference())) {
						recordFound = false;
					}
				} else {
					recordFound = false;
					for (CollateralSetup collateralSetup : collateralSetupList) {
						if (!(StringUtils.isNotBlank(aExetendedFieldRender.getReference()) && StringUtils
								.equals(collateralSetup.getCollateralRef(), aExetendedFieldRender.getReference()))) {
							recordFound = true;
						}
					}
				}
				if(recordFound) {
					MessageUtil.showError("This collateral already used by some other customer.");
					return;
				}
			}
		}
		
		// Post Validations for the Extended fields
		String postValidationScript = getExtendedFieldRenderDialogCtrl().getPostValidationScript();
		if(StringUtils.isNotEmpty(postValidationScript)){
			ScriptErrors postValidationErrors = getScriptValidationService().getPostValidationErrors(postValidationScript, fielValueMap);

			// Preparing Wrong Value User UI exceptions
			showErrorDetails(postValidationErrors);
		}
		
		isNew = aExetendedFieldRender.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExetendedFieldRender.getRecordType())) {
				aExetendedFieldRender.setVersion(aExetendedFieldRender
						.getVersion() + 1);
				if (isNew) {
					aExetendedFieldRender
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExetendedFieldRender
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
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
				aExetendedFieldRender.setVersion(aExetendedFieldRender
						.getVersion() + 1);
				aExetendedFieldRender.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aExetendedFieldRender.getRecordType().equals(
					PennantConstants.RCD_ADD)
					&& isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aExetendedFieldRender.getRecordType().equals(
					PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			AuditHeader auditHeader = newFieldProcess(aExetendedFieldRender,
					tranType);
			auditHeader = ErrorControl.showErrorDetails(
					this.window_ExtendedFieldCaptureDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE
					|| retValue == PennantConstants.porcessOVERIDE) {

				// Fields Rendering in List box dynamically
				getExtendedFieldRenderDialogCtrl().doFillExtendedFieldDetails(
						getExtendedList());

				// Close Dialog Window
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFieldProcess(
			ExtendedFieldRender aExetendedFieldRender, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aExetendedFieldRender,
				tranType);
		extendedList = new ArrayList<ExtendedFieldRender>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aExetendedFieldRender.getSeqNo());
		// valueParm[1] = aExetendedFieldRender.getCustRatingType();

		errParm[0] = PennantJavaUtil.getLabel("") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("") + ":" + valueParm[1];

		if (getExtendedFieldRenderDialogCtrl().getExtendedFieldRenderList() != null
				&& !getExtendedFieldRenderDialogCtrl()
						.getExtendedFieldRenderList().isEmpty()) {

			for (int i = 0; i < getExtendedFieldRenderDialogCtrl()
					.getExtendedFieldRenderList().size(); i++) {
				ExtendedFieldRender fieldRender = getExtendedFieldRenderDialogCtrl()
						.getExtendedFieldRenderList().get(i);

				if (fieldRender.getSeqNo() == aExetendedFieldRender.getSeqNo()) { // Both Current and Existing list Seqno same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail( new ErrorDetail(PennantConstants.KEY_FIELD,
								"41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
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
							for (int j = 0; j < getExtendedFieldRenderDialogCtrl().getExtendedFieldRenderList().size(); j++) {
								ExtendedFieldRender render = getExtendedFieldRenderDialogCtrl().getExtendedFieldRenderList().get(j);
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
		logger.debug("Leaving");
		return auditHeader;
	}
	
	/**
	 * Method for Showing UI Post validation Errors
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
			
			if(extendedFieldTabPanel.getFellowIfAny("ad_"+error.getProperty()) != null){
				Component component = extendedFieldTabPanel.getFellowIfAny("ad_"+error.getProperty());
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
				if(i == 0){
					Component comp = wvea[i].getComponent();
					if(comp instanceof HtmlBasedComponent){
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
	private AuditHeader getAuditHeader(
			ExtendedFieldRender aExetendedFieldRender, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null,
				aExetendedFieldRender);

		return new AuditHeader(getReference(),
				String.valueOf(aExetendedFieldRender.getSeqNo()), null, null,
				auditDetail, getUserWorkspace().getLoggedInUser(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(
					this.window_ExtendedFieldCaptureDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for entering Multiple Selection values in to Text box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onMultiSelButtonClick(ForwardEvent event) {
		logger.debug("Entering");
		List<Object> list = (List<Object>) event.getData();

		Textbox textbox = (Textbox) list.get(0);
		ExtendedFieldDetail detail = (ExtendedFieldDetail) list.get(1);

		HashMap<String, Object> selectedValues = new HashMap<String, Object>();
		Object dataObject = ExtendedMultipleSearchListBox.show(
				this.window_ExtendedFieldCaptureDialog, detail.getFieldList(),
				selectedValues);
		if (dataObject instanceof String) {
			textbox.setValue(dataObject.toString());
		} else {
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String multivalues = details.keySet().toString();
				textbox.setValue(multivalues.replace("[", "").replace("]", " "));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
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

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRenderDialogCtrl getExtendedFieldRenderDialogCtrl() {
		return extendedFieldRenderDialogCtrl;
	}

	public void setExtendedFieldRenderDialogCtrl(
			ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl) {
		this.extendedFieldRenderDialogCtrl = extendedFieldRenderDialogCtrl;
	}

	public List<ExtendedFieldRender> getExtendedList() {
		return extendedList;
	}

	public void setExtendedList(List<ExtendedFieldRender> extendedList) {
		this.extendedList = extendedList;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public ScriptValidationService getScriptValidationService() {
		return scriptValidationService;
	}

	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

	public boolean isNewExtendedField() {
		return newExtendedField;
	}

	public void setNewExtendedField(boolean newExtendedField) {
		this.newExtendedField = newExtendedField;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	
}
