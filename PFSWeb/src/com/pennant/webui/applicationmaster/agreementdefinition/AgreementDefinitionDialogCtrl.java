/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  AgreementDefinitionDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.agreementdefinition;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/AgreementDefinition/agreementDefinitionDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AgreementDefinitionDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 675917331534316816L;

	private final static Logger logger = Logger.getLogger(AgreementDefinitionDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_AgreementDefinitionDialog; 	// autoWired
	protected Textbox 	aggCode; 							// autoWired
	protected Textbox 	aggName; 							// autoWired
	protected Textbox 	aggDesc; 							// autoWired
	protected Textbox 	aggReportName; 						// autoWired
//	protected Textbox 	aggReportPath; 						// autoWired
	protected Checkbox 	aggIsActive; 						// autoWired
	protected Checkbox 	AggCheck_SelectAll; 						// autoWired

	protected Vlayout 	AgreementDetails; 						// autoWired

	//	protected Button    brwAgreementDoc;					// autoWired
//	protected Div	    signCopyPdf;						// autoWired
	protected Div	    orgDetailTabDiv;
//	protected Iframe    agreementDocView;					// autoWired
	public int	         borderLayoutHeight	     = 0;	
	

	protected Label 		recordStatus; 					// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not auto wired variables
	@SuppressWarnings("unused")
	private String aggImage=null;
	private AgreementDefinition agreementDefinition; // overHanded per parameter
	private AgreementDefinition prvAgreementDefinition; // overHanded per parameter
	private transient AgreementDefinitionListCtrl agreementDefinitionListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_aggCode;
	private transient String  		oldVar_aggName;
	private transient String  		oldVar_aggDesc;
	private transient String  		oldVar_aggReportName;
//	private transient String  		oldVar_aggReportPath;
	private transient boolean  		oldVar_aggIsActive;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_AgreementDefinitionDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 		// autoWire
	protected Button btnEdit; 		// autoWire
	protected Button btnDelete; 	// autoWire
	protected Button btnSave; 		// autoWire
	protected Button btnCancel; 	// autoWire
	protected Button btnClose; 		// autoWire
	protected Button btnHelp; 		// autoWire
	protected Button btnNotes; 		// autoWire


	// ServiceDAOs / Domain Classes
	private transient AgreementDefinitionService agreementDefinitionService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	static final List<ValueLabel> agreementDetailsList = PennantStaticListUtil.getAggDetails();

	/**
	 * default constructor.<br>
	 */
	public AgreementDefinitionDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AgreementDefinition object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AgreementDefinitionDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("agreementDefinition")) {
			this.agreementDefinition = (AgreementDefinition) args.get("agreementDefinition");
			AgreementDefinition befImage =new AgreementDefinition();
			BeanUtils.copyProperties(this.agreementDefinition, befImage);
			this.agreementDefinition.setBefImage(befImage);

			setAgreementDefinition(this.agreementDefinition);
		} else {
			setAgreementDefinition(null);
		}

		doLoadWorkFlow(this.agreementDefinition.isWorkflow(),this.agreementDefinition.getWorkflowId(),this.agreementDefinition.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "AgreementDefinitionDialog");
		}
		
		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
	//	this.signCopyPdf.setHeight(this.borderLayoutHeight - 80 + "px"); 

		// READ OVERHANDED parameters !
		// we get the agreementDefinitionListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete agreementDefinition here.
		if (args.containsKey("agreementDefinitionListCtrl")) {
			setAgreementDefinitionListCtrl((AgreementDefinitionListCtrl) args.get("agreementDefinitionListCtrl"));
		} else {
			setAgreementDefinitionListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAgreementDefinition());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.aggCode.setMaxlength(50);
		this.aggName.setMaxlength(100);
		this.aggDesc.setMaxlength(50);
		this.aggReportName.setMaxlength(100);
//		this.aggReportPath.setMaxlength(100);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("AgreementDefinitionDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_AgreementDefinitionDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}
	
	/**
	 * when the selectAll CheckBox is checked . <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$AggCheck_SelectAll(Event event) throws InterruptedException {
		logger.debug(event.toString());
		
			for(int i=0;i<agreementDetailsList.size();i++){
			 Checkbox checkBox=(Checkbox)AgreementDetails.getChildren().get(i);
			 if(AggCheck_SelectAll.isChecked()){
			    checkBox.setChecked(true);
			}else{
				checkBox.setChecked(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_AgreementDefinitionDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_AgreementDefinitionDialog, "AgreementDefinition");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAgreementDefinition
	 *            AgreementDefinition
	 */
	public void doWriteBeanToComponents(AgreementDefinition aAgreementDefinition) {
		logger.debug("Entering") ;
		this.aggCode.setValue(aAgreementDefinition.getAggCode());
		this.aggName.setValue(aAgreementDefinition.getAggName());
		this.aggDesc.setValue(aAgreementDefinition.getAggDesc());
		this.aggReportName.setValue(aAgreementDefinition.getAggReportName());
//		this.aggReportPath.setValue(aAgreementDefinition.getAggReportPath());
		this.aggIsActive.setChecked(aAgreementDefinition.isAggIsActive());
		this.recordStatus.setValue(aAgreementDefinition.getRecordStatus());
		
		if(aAgreementDefinition.isNew() || (aAgreementDefinition.getRecordType() != null ? aAgreementDefinition.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.aggIsActive.setChecked(true);
			this.aggIsActive.setDisabled(true);
		}
		this.aggImage= aAgreementDefinition.getAggImage();
		doFillAggDetailsList(aAgreementDefinition);
		
		/*
		AMedia amedia = null;
		String docType = aAgreementDefinition.getAggtype();
		if (aAgreementDefinition.getAggImage() != null) {
			final InputStream data = new ByteArrayInputStream(aAgreementDefinition.getAggImage());
						
			if("JPEG".equals(docType)){
				amedia = new AMedia("document.jpg", "jpeg", "image/jpeg", data);
			} else if("PNG".equals(docType)){
				amedia = new AMedia("document.png", "png", "image/png", data);
			} else if("GIF".equals(docType)){
				amedia = new AMedia("document.gif", "gif", "image/gif", data);
			} else if("PDF".equals(docType)){
				amedia = new AMedia("document.pdf", "pdf", "application/pdf", data);
			} else if("TEXT".equals(docType)){
				amedia = new AMedia("document.txt", "txt", "text/plain", data);
			} 
			
			try{
				if (docType.equals("WORD")) {			

					FileOutputStream out = new FileOutputStream(aAgreementDefinition.getAggReportName()); 
					out.write(aAgreementDefinition.getAggImage());
					out.close();

					Document doc = new Document(aAgreementDefinition.getAggReportName());

					String pdfFileName = aAgreementDefinition.getAggReportName().substring(0, aAgreementDefinition.getAggReportName().lastIndexOf("."));
					pdfFileName = pdfFileName +".pdf";

					doc.save(pdfFileName, SaveFormat.PDF);		
					amedia = new AMedia("document.pdf", "pdf", "application/pdf", new FileInputStream(pdfFileName));


				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			agreementDocView.setContent(amedia);
		}
*/				
			
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAgreementDefinition
	 */
	public void doWriteComponentsToBean(AgreementDefinition aAgreementDefinition) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aAgreementDefinition.setAggCode(this.aggCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAggName(this.aggName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAggDesc(this.aggDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAgreementDefinition.setAggReportName(this.aggReportName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*try {
			aAgreementDefinition.setAggReportPath(this.aggReportPath.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		try {
			aAgreementDefinition.setAggIsActive(this.aggIsActive.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			doSaveAggDetailsList(aAgreementDefinition);
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aAgreementDefinition.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	
	/**
	 * This method Fills Agreement Details in Listbox
	 */
	private void doFillAggDetailsList(AgreementDefinition aAgreementDefinition){
		logger.debug("Entering");
		String aggDetail1 =aAgreementDefinition.getAggImage()==null ? "": aAgreementDefinition.getAggImage();
		if(agreementDetailsList!=null){
			for (ValueLabel agreementDetail : agreementDetailsList) {
				if(!agreementDetail.getValue().equals("")){
					Checkbox checkbox=new Checkbox(); 
					checkbox.setId(agreementDetail.getValue());
					checkbox.setLabel(agreementDetail.getLabel());
					checkbox.setDisabled(isReadOnly("AgreementDefinitionDialog_aggDesc"));
					if(aggDetail1.contains(agreementDetail.getValue())){	
						checkbox.setChecked(true);
					}
					this.AgreementDetails.appendChild(checkbox);
				}
			}
		}
		logger.debug("Leaving");
	}
	
	private void doSaveAggDetailsList(AgreementDefinition aAgreementDefinition)
	{
		String aggImageTemp="";
		List<Component> components = AgreementDetails.getChildren();
		for (Component component : components) {
			Checkbox checkBox=(Checkbox)component;
			if(checkBox.isChecked()){
				aggImageTemp =  aggImageTemp + checkBox.getId()+ ",";
			}
		}
		aAgreementDefinition.setAggImage(aggImageTemp);
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAgreementDefinition
	 * @throws InterruptedException
	 */
	public void doShowDialog(AgreementDefinition aAgreementDefinition) throws InterruptedException {
		logger.debug("Entering") ;

		// if aAgreementDefinition == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aAgreementDefinition == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aAgreementDefinition = getAgreementDefinitionService().getNewAgreementDefinition();

			setAgreementDefinition(aAgreementDefinition);
		} else {
			setAgreementDefinition(aAgreementDefinition);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAgreementDefinition.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.aggCode.focus();
		} else {
			this.aggName.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aAgreementDefinition.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aAgreementDefinition);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_AgreementDefinitionDialog);
		} catch (final Exception e) {
			logger.error(e);
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_aggCode = this.aggCode.getValue();
		this.oldVar_aggName = this.aggName.getValue();
		this.oldVar_aggDesc = this.aggDesc.getValue();
		this.oldVar_aggReportName = this.aggReportName.getValue();
//		this.oldVar_aggReportPath = this.aggReportPath.getValue();
		this.oldVar_aggIsActive = this.aggIsActive.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.aggCode.setValue(this.oldVar_aggCode);
		this.aggName.setValue(this.oldVar_aggName);
		this.aggDesc.setValue(this.oldVar_aggDesc);
		this.aggReportName.setValue(this.oldVar_aggReportName);
//		this.aggReportPath.setValue(this.oldVar_aggReportPath);
		this.aggIsActive.setChecked(this.oldVar_aggIsActive);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_aggCode != this.aggCode.getValue()) {
			return true;
		}
		if (this.oldVar_aggName != this.aggName.getValue()) {
			return true;
		}
		if (this.oldVar_aggDesc != this.aggDesc.getValue()) {
			return true;
		}
		if (this.oldVar_aggReportName != this.aggReportName.getValue()) {
			return true;
		}
		/*if (this.oldVar_aggReportPath != this.aggReportPath.getValue()) {
			return true;
		}*/
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.aggCode.isReadonly()){
			this.aggCode.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDefinitionDialog_AggCode.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		
		if (!this.aggName.isReadonly()){
			this.aggName.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDefinitionDialog_AggName.value"), PennantRegularExpressions.REGEX_COMPANY_NAME, true));
		}
		
		if (!this.aggReportName.isReadonly()){
			this.aggReportName.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDefinitionDialog_AggReportName.value"), PennantRegularExpressions.REGEX_COMPANY_NAME, true));
		}
		
		/*if (!this.aggReportPath.isReadonly()){
			this.aggReportPath.setConstraint(new SimpleConstraint(
				PennantConstants.PATH_REGEX, Labels.getLabel(
					"MAND_FIELD_ALPHANUMERIC_SPECIALCHARS",new String[]{Labels.getLabel(
						"label_AgreementDefinitionDialog_AggReportPath.value")})));
		}*/
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.aggCode.setConstraint("");
		this.aggName.setConstraint("");
		this.aggDesc.setConstraint("");
		this.aggReportName.setConstraint("");
//		this.aggReportPath.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.aggCode.setErrorMessage("");
		this.aggName.setErrorMessage("");
		this.aggDesc.setErrorMessage("");
		this.aggReportName.setErrorMessage("");
//		this.aggReportPath.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a AgreementDefinition object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final AgreementDefinition aAgreementDefinition = new AgreementDefinition();
		BeanUtils.copyProperties(getAgreementDefinition(), aAgreementDefinition);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aAgreementDefinition.getAggCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAgreementDefinition.getRecordType()).equals("")){
				aAgreementDefinition.setVersion(aAgreementDefinition.getVersion()+1);
				aAgreementDefinition.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aAgreementDefinition.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aAgreementDefinition,tranType)){
					refreshList();
					closeDialog(this.window_AgreementDefinitionDialog, "AgreementDefinition"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new AgreementDefinition object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final AgreementDefinition aAgreementDefinition = getAgreementDefinitionService().getNewAgreementDefinition();
		aAgreementDefinition.setNewRecord(true);
		setAgreementDefinition(aAgreementDefinition);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.aggCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAgreementDefinition().isNewRecord()){
			this.aggCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.aggCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.aggName.setReadonly(isReadOnly("AgreementDefinitionDialog_aggName"));
		this.aggDesc.setReadonly(isReadOnly("AgreementDefinitionDialog_aggDesc"));
		this.aggReportName.setReadonly(isReadOnly("AgreementDefinitionDialog_aggReportName"));
//		this.aggReportPath.setReadonly(isReadOnly("AgreementDefinitionDialog_aggReportPath"));
		this.aggIsActive.setDisabled(isReadOnly("AgreementDefinitionDialog_aggIsActive"));
		this.AggCheck_SelectAll.setDisabled(isReadOnly("AgreementDefinitionDialog_aggDesc"));
		this.AggCheck_SelectAll.setVisible(!isReadOnly("AgreementDefinitionDialog_aggDesc"));
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.agreementDefinition.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.aggCode.setReadonly(true);
		this.aggName.setReadonly(true);
		this.aggDesc.setReadonly(true);
		this.aggReportName.setReadonly(true);
//		this.aggReportPath.setReadonly(true);
		this.aggIsActive.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.aggCode.setValue("");
		this.aggName.setValue("");
		this.aggDesc.setValue("");
		this.aggReportName.setValue("");
//		this.aggReportPath.setValue("");
		this.aggIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AgreementDefinition aAgreementDefinition = new AgreementDefinition();
		BeanUtils.copyProperties(getAgreementDefinition(), aAgreementDefinition);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the AgreementDefinition object with the components data
		doWriteComponentsToBean(aAgreementDefinition);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAgreementDefinition.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aAgreementDefinition.getRecordType()).equals("")){
				aAgreementDefinition.setVersion(aAgreementDefinition.getVersion()+1);
				if(isNew){
					aAgreementDefinition.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aAgreementDefinition.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAgreementDefinition.setNewRecord(true);
				}
			}
		}else{
			aAgreementDefinition.setVersion(aAgreementDefinition.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aAgreementDefinition,tranType)){
				refreshList();
				closeDialog(this.window_AgreementDefinitionDialog, "AgreementDefinition");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAgreementDefinition
	 *            (AgreementDefinition)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(AgreementDefinition aAgreementDefinition,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aAgreementDefinition.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aAgreementDefinition.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAgreementDefinition.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aAgreementDefinition.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAgreementDefinition.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aAgreementDefinition);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aAgreementDefinition))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}


			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aAgreementDefinition.setTaskId(taskId);
			aAgreementDefinition.setNextTaskId(nextTaskId);
			aAgreementDefinition.setRoleCode(getRole());
			aAgreementDefinition.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aAgreementDefinition, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aAgreementDefinition);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aAgreementDefinition, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aAgreementDefinition, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		AgreementDefinition aAgreementDefinition = (AgreementDefinition) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getAgreementDefinitionService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getAgreementDefinitionService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getAgreementDefinitionService().doApprove(auditHeader);

						if(aAgreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getAgreementDefinitionService().doReject(auditHeader);
						if(aAgreementDefinition.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AgreementDefinitionDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_AgreementDefinitionDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAddressType
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(AgreementDefinition aAgreementDefinition, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAgreementDefinition.getBefImage(), aAgreementDefinition);   
		return new AuditHeader(String.valueOf(aAgreementDefinition.getAggId()),
				null,null,null,auditDetail,aAgreementDefinition.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_AgreementDefinitionDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("AgreementDefinition");
		notes.setReference(String.valueOf(getAgreementDefinition().getAggId()));
		notes.setVersion(getAgreementDefinition().getVersion());
		return notes;
	}

	// Method for refreshing the list after successful updation
	private void refreshList(){
		final JdbcSearchObject<AgreementDefinition> soAgreementDefinition = getAgreementDefinitionListCtrl().getSearchObj();
		getAgreementDefinitionListCtrl().pagingAgreementDefinitionList.setActivePage(0);
		getAgreementDefinitionListCtrl().getPagedListWrapper().setSearchObject(soAgreementDefinition);
		if(getAgreementDefinitionListCtrl().listBoxAgreementDefinition!=null){
			getAgreementDefinitionListCtrl().listBoxAgreementDefinition.getListModel();
		}
	} 
	
	/*public void onUpload$brwAgreementDoc(UploadEvent event) {
		logger.debug(event.toString());
		Media media = event.getMedia();
		
		browseDoc(media, getAgreementDefinition());
		logger.debug("Leaving");
	}*/

	
/*	private void browseDoc(Media media, AgreementDefinition agreementDefinition) {
		logger.debug("Entering");
		try {
			boolean isSupported = true;
			String docType = "";	
			String fileName = media.getName();
			String mediaDocType = media.getContentType();			
			if (mediaDocType.equals("image/gif")) {
				docType = "GIF";
			} else  if (mediaDocType.equals("image/png")) {
				docType = "PNG";
			} else  if (mediaDocType.equals("image/jpeg")) {
				docType = "JPEG";
			} else if (mediaDocType.equals("application/pdf")) {
				docType = "PDF";
			} else if (mediaDocType.equals("application/msword")) {
				docType = "WORD";
			} else if (mediaDocType.equals("text/plain")) {
				docType = "TEXT";
			} else {
				isSupported = false;
				PTMessageUtils.showErrorMessage("Un Supported Format.only "+PennantConstants.AGREEMENT_DEFINITION_DOCS+" are allowed");
			}
			if (isSupported) {
				byte[] imageData = null;
				if(media.isBinary()) {
					imageData = IOUtils.toByteArray(media.getStreamData());
				} else {
					imageData =  IOUtils.toByteArray(media.getReaderData());
				}
				agreementDefinition.setAggImage(imageData);
				agreementDefinition.setAggtype(docType);
				this.aggReportName.setValue(fileName);
//				this.aggReportPath.setValue(fileName);	
				if(docType.equals("WORD")) {			
					FileOutputStream out = new FileOutputStream(fileName); 
					out.write(imageData);
					out.close();
					Document doc = new Document(fileName);
					String pdfFileName = fileName.substring(0, media.getName().lastIndexOf("."));
					pdfFileName = pdfFileName +".pdf";
					doc.save(pdfFileName, SaveFormat.PDF);		
					imageData =  IOUtils.toByteArray(new FileInputStream(pdfFileName));				 			 
				}
				if("JPEG".equals(docType)){
					this.agreementDocView.setContent(new AMedia("document.jpg", "image/jpeg", mediaDocType, imageData));
				} else if("PNG".equals(docType)){
					this.agreementDocView.setContent(new AMedia("document.png", "image/png", mediaDocType, imageData));
				} else if("GIF".equals(docType)){
					this.agreementDocView.setContent(new AMedia("document.gif", "image/gif", mediaDocType, imageData));
				} else if("PDF".equals(docType) || "WORD".equals(docType)){
					this.agreementDocView.setContent(new AMedia("document.pdf", "pdf", "application/pdf", imageData));
				} else if("TEXT".equals(docType)){
					this.agreementDocView.setContent(new AMedia("document.txt", "txt", "text/plain", imageData));
				} 
				
			}
		

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("Leaving");
	}*/
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public AgreementDefinition getAgreementDefinition() {
		return this.agreementDefinition;
	}

	public void setAgreementDefinition(AgreementDefinition agreementDefinition) {
		this.agreementDefinition = agreementDefinition;
	}

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return this.agreementDefinitionService;
	}

	public void setAgreementDefinitionListCtrl(AgreementDefinitionListCtrl agreementDefinitionListCtrl) {
		this.agreementDefinitionListCtrl = agreementDefinitionListCtrl;
	}

	public AgreementDefinitionListCtrl getAgreementDefinitionListCtrl() {
		return this.agreementDefinitionListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public AgreementDefinition getPrvAgreementDefinition() {
		return prvAgreementDefinition;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

}
