package com.pennant.webui.assettype.assettypeassignment;

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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.configuration.AssetTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.CollateralHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

public class AssetTypeAssignmentDialogCtrl extends GFCBaseCtrl<ExtendedFieldHeader> {

	private static final long serialVersionUID 	= 4558487274958745612L;
	private static final Logger		logger		= Logger.getLogger(AssetTypeAssignmentDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 						window_AssetTypeAssignmentDialog;
	
	protected Groupbox						grid_fdDetails;
	protected Tabpanel						extendedFieldTabPanel;
	protected Intbox 						seqNo;
	protected ExtendedCombobox				assetType;

	private CollateralHeaderDialogCtrl 		collateralHeaderDialogCtrl;
	private ExtendedFieldHeader 			extendedFieldHeader;
	private ExtendedFieldRender 			extendedFieldRender;
	private ExtendedFieldsGenerator 		generator;
	private List<ExtendedFieldRender> 		extendedList = null;
	private boolean 						newRecord = false;
	private boolean 						isReadOnly = false;
	private int 							ccyFormat = 0;
	private AssetTypeService     	 		assetTypeService;
	private ScriptValidationService      	scriptValidationService;
	private String 							preValidationScript;
	private String 							postValidationScript;
	
	/**
	 * default constructor.<br>
	 */
	public AssetTypeAssignmentDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetTypeAssignmentDialog";
	}

	/**
	 * Method for creating window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AssetTypeAssignmentDialog(Event event) throws Exception {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_AssetTypeAssignmentDialog);

		// collateralSetupCtrl
		if (arguments.containsKey("collateralHeaderDialogCtrl")) {
			setCollateralHeaderDialogCtrl((CollateralHeaderDialogCtrl) arguments.get("collateralHeaderDialogCtrl"));
			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), "AssetTypeAssignmentDialog");
			}
		}
		
		if (arguments.containsKey("extendedFieldRender")) {
			setExtendedFieldRender((ExtendedFieldRender) arguments.get("extendedFieldRender"));
		}
		
		if (arguments.containsKey("isReadOnly")) {
			isReadOnly = (Boolean) arguments.get("isReadOnly");
		}
		
		if (arguments.containsKey("ccyFormat")) {
			ccyFormat = (int) arguments.get("ccyFormat");
		}

		// Extended Field Details auto population / Rendering into Screen
		generator = new ExtendedFieldsGenerator();
		generator.setWindow(this.window_AssetTypeAssignmentDialog);
		generator.setTabpanel(extendedFieldTabPanel);
		this.generator.setRowWidth(180);
		this.extendedFieldTabPanel.setHeight((borderLayoutHeight-75)+"px");
		generator.setReadOnly(isReadOnly);
		generator.setCcyFormat(ccyFormat);

		if (getExtendedFieldRender().getMapValues() != null) {
			generator.setFieldValueMap((HashMap<String, Object>) getExtendedFieldRender().getMapValues());
		}

		doCheckRights();
		doSetFieldProperties();
		doShowDialog(getExtendedFieldRender());
		
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param collateralAssignment
	 * @throws InterruptedException
	 */
	public void doShowDialog(ExtendedFieldRender extendedFieldRender) throws InterruptedException {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.assetType.focus();
		}
		
		doEdit();
		try {

			this.seqNo.setValue(getExtendedFieldRender().getSeqNo());
			this.assetType.setValue(getExtendedFieldRender().getTypeCode());
			this.assetType.setDescription(getExtendedFieldRender().getTypeCodeDesc());

			if(getExtendedFieldRender().getTypeCode() != null){
				AssetType assetType = assetTypeService.getAssetTypeById(getExtendedFieldRender().getTypeCode());
				setPreValidationScript(assetType.getPreValidation());
				setPostValidationScript(assetType.getPostValidation());
				setExtendedFieldHeader(assetType.getExtendedFieldHeader());
				generator.renderWindow(getExtendedFieldHeader(), newRecord);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.assetType.setReadonly(false);
 		} else {
			this.assetType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.extendedFieldRender.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				this.btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(true);
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities("AssetTypeAssignmentDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetTypeAssignmentDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetTypeAssignmentDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetTypeAssignmentDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetTypeAssignmentDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");

		this.assetType.setMandatoryStyle(true);
		this.assetType.setModuleName("AssetType");
		this.assetType.setValueColumn("AssetType");
		this.assetType.setDescColumn("AssetDesc");
		this.assetType.setValidateColumns(new String[]{"AssetType"});
		this.assetType.setFilters(new Filter[]{new Filter("Active", 1, Filter.OP_EQUAL)});
		
		logger.debug("Leaving ");
	}
	
	/**
	 * 
	 * When user clicks on "btnSearch assetType" button
	 * This method displays ExtendedSearchListBox with Asset Type details
	 * @param event
	 * @throws ScriptException 
	 */
	public void onFulfill$assetType(Event event) throws ScriptException{
		logger.debug("Entering  "+event.toString());
		
		Object dataObject = assetType.getObject();
		if (dataObject instanceof String){
			this.assetType.setValue(dataObject.toString());
			this.assetType.setDescription("");
		}else{
			AssetType details= (AssetType) dataObject;
			if (details != null) {
				this.assetType.setValue(details.getAssetType());
				this.assetType.setDescription(details.getAssetDesc());
				try {
					
					this.assetType.setReadonly(true);
					AssetType assetType = assetTypeService.getAssetTypeById(details.getAssetType());
					getExtendedFieldRender().setTypeCode(assetType.getAssetType());
					getExtendedFieldRender().setTypeCodeDesc(assetType.getAssetDesc());
					setPreValidationScript(assetType.getPreValidation());
					setPostValidationScript(assetType.getPostValidation());
					setExtendedFieldHeader(assetType.getExtendedFieldHeader());
					
					//Pre-Validation Checking & Setting Defaults
					Map<String, Object> fieldValuesMap = null;
					if (getExtendedFieldRender().getMapValues() != null) {
						fieldValuesMap = getExtendedFieldRender().getMapValues();
					}

					if (newRecord) {

						//get pre-validation script if record is new
						if(StringUtils.isNotEmpty(getPreValidationScript())){
							ScriptErrors defaults = getScriptValidationService().setPreValidationDefaults(getPreValidationScript(), fieldValuesMap);

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
					
					generator.renderWindow(getExtendedFieldHeader(), newRecord);
				} catch (ParseException e) {
					logger.error(e);
				}
			}
		}
		logger.debug("Leaving"+event.toString());
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
	 * @throws InvocationTargetException 
	 */
	public void doSave() throws InterruptedException, ParseException,
			ScriptException, IOException, NoSuchMethodException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		try {
			this.assetType.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetTypeAssignmentDialog_AssetType.value"), null, true, true));
			this.assetType.getValidatedValue();
		} catch (WrongValueException e) {
			throw e; 
		}
		
		final ExtendedFieldRender aExetendedFieldRender = getExtendedFieldRender();
		boolean isNew = false;

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		Map<String, Object> map = generator.doSave(getExtendedFieldHeader().getExtendedFieldDetails(), false);
		aExetendedFieldRender.setMapValues(map);
		
		// Post Validations for the Extended fields
		if(StringUtils.isNotEmpty(getPostValidationScript())){
			ScriptErrors postValidationErrors = getScriptValidationService().getPostValidationErrors(
					getPostValidationScript(), map);

			// Preparing Wrong Value User UI exceptions
			showErrorDetails(postValidationErrors);
		}
				
		isNew = aExetendedFieldRender.isNew();
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

			AuditHeader auditHeader = newFieldProcess(aExetendedFieldRender, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_AssetTypeAssignmentDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {

				// Fields Rendering in List box dynamically
				getCollateralHeaderDialogCtrl().doFillAssetDetails(getExtendedList());

				// Close Dialog Window
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Asset Type Details through extended fields
	 * @param aExetendedFieldRender
	 * @param tranType
	 * @return
	 */
	private AuditHeader newFieldProcess(ExtendedFieldRender aExetendedFieldRender, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aExetendedFieldRender, tranType);
		extendedList = new ArrayList<ExtendedFieldRender>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aExetendedFieldRender.getSeqNo());
		valueParm[1] = aExetendedFieldRender.getTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("label_SeqNo") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_AssetType") + ":" + valueParm[1];

		if (getCollateralHeaderDialogCtrl().getExtendedFieldRenderList() != null
				&& !getCollateralHeaderDialogCtrl().getExtendedFieldRenderList().isEmpty()) {

			for (int i = 0; i < getCollateralHeaderDialogCtrl().getExtendedFieldRenderList().size(); i++) {
				ExtendedFieldRender fieldRender = getCollateralHeaderDialogCtrl().getExtendedFieldRenderList().get(i);

				if (fieldRender.getSeqNo() == aExetendedFieldRender.getSeqNo()) { // Both Current and Existing list Seqno same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001", errParm, valueParm),getUserWorkspace().getUserLanguage()));
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
							for (int j = 0; j < getCollateralHeaderDialogCtrl().getExtendedFieldRenderList().size(); j++) {
								ExtendedFieldRender render = getCollateralHeaderDialogCtrl().getExtendedFieldRenderList().get(j);
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
		
		// Asset Type Details Rendering (Record Type details resetting)
		if(isNewRecord()){
			FinAssetTypes assetType = new FinAssetTypes();
			assetType.setAssetType(aExetendedFieldRender.getTypeCode());
			assetType.setSeqNo(aExetendedFieldRender.getSeqNo());
			assetType.setRecordType(aExetendedFieldRender.getRecordType());
			
			getCollateralHeaderDialogCtrl().getFinAssetTypes().add(assetType);
		}else{
			// Fetch Related Finance Asset Type Details from List
			List<FinAssetTypes> assets = getCollateralHeaderDialogCtrl().getFinAssetTypes();
			if(assets != null){
				for (int i = 0; i < assets.size(); i++) {
					FinAssetTypes finAssetType = assets.get(i);
					if(StringUtils.equals(finAssetType.getAssetType(), aExetendedFieldRender.getTypeCode()) &&
							finAssetType.getSeqNo() == aExetendedFieldRender.getSeqNo()){
						finAssetType.setRecordType(aExetendedFieldRender.getRecordType());
					}
				}
			}
		}
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ExtendedFieldRender aExetendedFieldRender, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, null,aExetendedFieldRender);

		return new AuditHeader(getReference(),String.valueOf(aExetendedFieldRender.getSeqNo()), null, null,auditDetail, getUserWorkspace().getLoggedInUser(),getOverideMap());
	}
	
	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Deletes a GuarantorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private void doDelete() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");
		final ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		BeanUtils.copyProperties(getExtendedFieldRender(), extendedFieldRender);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ (extendedFieldRender.getSeqNo());

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(extendedFieldRender.getRecordType())) {
				extendedFieldRender.setVersion(extendedFieldRender.getVersion() + 1);
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				extendedFieldRender.setNewRecord(true);
				
				if (isWorkFlowEnabled()) {
					extendedFieldRender.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFieldProcess(extendedFieldRender, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_AssetTypeAssignmentDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getCollateralHeaderDialogCtrl() != null) {
						getCollateralHeaderDialogCtrl().doFillAssetDetails(this.extendedList);
					}
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
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
					this.window_AssetTypeAssignmentDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Showing UI Post validation Errors
	 * @param postValidationErrors
	 */
	public void showErrorDetails(ScriptErrors postValidationErrors) {
		List<ScriptError> errorList = postValidationErrors.getAll();
		if(errorList == null || errorList.isEmpty()){
			return;
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		for (int i = 0; i < errorList.size(); i++) {
			ScriptError error = errorList.get(i);
			
			if(extendedFieldTabPanel.getFellowIfAny("ad_"+error.getProperty()) != null){
				Component component = extendedFieldTabPanel.getFellowIfAny("ad_"+error.getProperty());
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}
	
	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}
	
	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}
	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}
	
	public ExtendedFieldsGenerator getGenerator() {
		return generator;
	}
	public void setGenerator(ExtendedFieldsGenerator generator) {
		this.generator = generator;
	}
	
	public List<ExtendedFieldRender> getExtendedList() {
		return extendedList;
	}
	public void setExtendedList(List<ExtendedFieldRender> extendedList) {
		this.extendedList = extendedList;
	}
	
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public CollateralHeaderDialogCtrl getCollateralHeaderDialogCtrl() {
		return collateralHeaderDialogCtrl;
	}
	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
	}

	public AssetTypeService getAssetTypeService() {
		return assetTypeService;
	}
	public void setAssetTypeService(AssetTypeService assetTypeService) {
		this.assetTypeService = assetTypeService;
	}

	public String getPreValidationScript() {
		return preValidationScript;
	}
	public void setPreValidationScript(String preValidationScript) {
		this.preValidationScript = preValidationScript;
	}

	public String getPostValidationScript() {
		return postValidationScript;
	}
	public void setPostValidationScript(String postValidationScript) {
		this.postValidationScript = postValidationScript;
	}

	public ScriptValidationService getScriptValidationService() {
		return scriptValidationService;
	}
	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

}
