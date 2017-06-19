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
import org.springframework.dao.DataAccessException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;

public class ExtendedFieldCaptureDialogCtrl extends	GFCBaseCtrl<ExtendedFieldHeader> {

	private static final long serialVersionUID = -8108473227202001840L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldCaptureDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExtendedFieldCaptureDialog;
	protected Rows extendedFieldRows;
	protected Intbox seqNo;

	private ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl;
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private ExtendedFieldsGenerator generator;
	private List<ExtendedFieldRender> extendedList = null;
	private int ccyFormat = 0;
	private boolean newRecord = false;
	private boolean isReadOnly = false;
	private String moduleType = "";
	private ScriptValidationService scriptValidationService;

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
		generator.setWindow(this.window_ExtendedFieldCaptureDialog);
		generator.setRows(extendedFieldRows);
		generator.setCcyFormat(ccyFormat);
		if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
			this.btnSave.setVisible(false);
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
		}
		
		if(fieldValuesMap != null){
			generator.setFieldValueMap((HashMap<String, Object>) fieldValuesMap);
		}

		try {
			this.seqNo.setValue(getExtendedFieldRender().getSeqNo());
			generator.renderWindow(getExtendedFieldHeader(), newRecord);
			
			// Height Calculation
			int height = (this.extendedFieldRows.getVisibleItemCount() * 25) + 130;
			if((borderLayoutHeight*0.95) < height){
				height = (int) (borderLayoutHeight*0.95);
			}
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
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail( new ErrorDetails(PennantConstants.KEY_FIELD,
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
			
			if(extendedFieldRows.getFellowIfAny("ad_"+error.getProperty()) != null){
				Component component = extendedFieldRows.getFellowIfAny("ad_"+error.getProperty());
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
			auditHeader.setErrorDetails(new ErrorDetails(
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

}
