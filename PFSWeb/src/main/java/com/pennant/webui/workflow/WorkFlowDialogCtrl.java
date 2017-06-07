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
 *
 * FileName    		:  WorkFlowDialogCtl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.workflow;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.engine.WorkflowEngine;

public class WorkFlowDialogCtrl extends GFCBaseCtrl<WorkFlowDetails> {
	private static final long serialVersionUID = -1351367303946249042L;
	private final static Logger logger = Logger.getLogger(WorkFlowDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_workFlowDialog; // autowired
	
	protected Textbox  workFlowType;
	protected Textbox  workFlowSubType;
	protected Textbox  workFlowDesc;
	protected Textbox  workFlowXML;
	protected Checkbox workFlowActive;		
	protected Groupbox gb_basicDetails;
	
	// not auto wired vars
	private WorkFlowDetails workFlowDetails; 			// overhanded per param
	private transient WorkFlowListCtrl workFlowListCtrl; // overhanded per param

	protected Button btnUpload;
	
	private transient WorkFlowDetailsService workFlowDetailsService;
	protected JdbcSearchObject<WorkFlowDetails> searchObj;

	public WorkFlowDialogCtrl(){
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "WorkFlowDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected WorkFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_workFlowDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_workFlowDialog);

		try{
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("workFlowDetails")) {
				this.workFlowDetails = (WorkFlowDetails) arguments
						.get("workFlowDetails");
				WorkFlowDetails flowDetails = new WorkFlowDetails();
				BeanUtils.copyProperties(this.workFlowDetails, flowDetails);
				this.workFlowDetails.setBefImage(flowDetails);

			} else {
				setWorkFlowDetails(null);
			}

			if (arguments.containsKey("workFlowListCtrl")) {
				setWorkFlowListCtrl((WorkFlowListCtrl) arguments
						.get("workFlowListCtrl"));
			} else {
				setWorkFlowListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getWorkFlowDetails());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			this.window_workFlowDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
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
	
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		this.workFlowType.setMaxlength(50);
		this.workFlowSubType.setMaxlength(50);
		this.workFlowDesc.setMaxlength(500);
		logger.debug("Leaving ");
	}


	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		validateErrors();
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onUpload$btnUpload(UploadEvent event) throws Exception{
		logger.debug("Entering" + event.toString());
		this.workFlowXML.setValue("");
		
		if(event.getMedia().getName().endsWith(".xml")) {
			try {
				ByteArrayInputStream xmlStream = new ByteArrayInputStream(event
						.getMedia().getStringData().getBytes());
				XMLStreamReader parser = XMLInputFactory.newInstance()
						.createXMLStreamReader(xmlStream);
				 StAXOMBuilder builder = new StAXOMBuilder(parser);
				 OMElement xmlEnvelope = builder.getDocumentElement();
				 this.workFlowXML.setValue(xmlEnvelope.toString());
				 
				 
				 WorkflowEngine workFlow= new WorkflowEngine(builder);
				this.workFlowDetails.setFirstTaskOwner(workFlow.allFirstTaskOwners());
				this.workFlowDetails.setWorkFlowRoles(StringUtils.join(workFlow.getActors(false), ';'));

				 this.btnSave.setVisible(true);
			} catch (Exception e) {
				logger.error("Exception", e);
				MessageUtil.showError("Unable to parse the File");
			}
		} else{
			MessageUtil.showError("The file must be an .xml file. Please select another file");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.workFlowDetails.getBefImage());		
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aWorkFlowDetails
	 *            WorkFlowDetails
	 * @throws Exception 
	 */
	public void doWriteBeanToComponents(WorkFlowDetails aWorkFlowDetails) {
		logger.debug("Entering ");
		this.workFlowType.setValue(aWorkFlowDetails.getWorkFlowType());
		this.workFlowSubType.setValue(aWorkFlowDetails.getWorkFlowSubType());
		this.workFlowDesc.setValue(aWorkFlowDetails.getWorkFlowDesc());
		if(aWorkFlowDetails.getWorkFlowXml()  != null){
			this.workFlowXML.setValue(aWorkFlowDetails.getWorkFlowXml());
		}else{
			this.workFlowXML.setValue("");
		}
		this.workFlowActive.setChecked(aWorkFlowDetails.isWorkFlowActive());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aWorkFlowDetails
	 * @throws InterruptedException
	 */
	public void doShowDialog(WorkFlowDetails aWorkFlowDetails) throws Exception {
		logger.debug("Entering");
		if(aWorkFlowDetails.isNew()){
			doEdit();
		}else{
			doReadOnly();
		}
		
		
		try {
			// fill the components with the data
			doWriteBeanToComponents(aWorkFlowDetails);
			
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_workFlowDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWorkFlowDetails
	 */
	public void doWriteComponentsToBean(WorkFlowDetails aWorkFlowDetails) throws DocumentException{
		logger.debug("Entering ");
		aWorkFlowDetails.setWorkFlowSubType(this.workFlowSubType.getValue());
		aWorkFlowDetails.setWorkFlowDesc(this.workFlowDesc.getValue());
		aWorkFlowDetails.setWorkFlowXml(this.workFlowXML.getValue());
		aWorkFlowDetails.setWorkFlowType(this.workFlowType.getValue());
		logger.debug("Leaving ");
	} 

	/*Saves the components to table. <br>
	  @throws Exception*/ 

	public void doSave() throws Exception {
		logger.debug("Entering ");
		final WorkFlowDetails aWorkFlowDetails =getWorkFlowDetails();
		// fill the WorkFlowDetails object with the components data
		AuditHeader auditHeader =  null;
		doWriteComponentsToBean(aWorkFlowDetails);		 

		// save it to database
		try {
			
			aWorkFlowDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aWorkFlowDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aWorkFlowDetails.setUserDetails(getUserWorkspace().getLoggedInUser());
			aWorkFlowDetails.setWorkFlowActive(true);

			String tranType="";
			if (aWorkFlowDetails.isNew()){
				tranType=PennantConstants.RECORD_TYPE_NEW;
			}else{
				tranType=PennantConstants.RECORD_TYPE_UPD;
			}
				
			auditHeader =  getAuditHeader(aWorkFlowDetails, tranType);
			
			if(doSaveProcess(auditHeader)){
				final JdbcSearchObject<WorkFlowDetails> so = getWorkFlowListCtrl().getSearchObject();
				
				// Set the ListModel
				getWorkFlowListCtrl().pagingWorkFlowList.setActivePage(0);
				getWorkFlowListCtrl().getPagedListWrapper().setSearchObject(so);
				// call from cusromerList then synchronize the invoiceHeader listBox
				if (getWorkFlowListCtrl().listBoxWorkFlow != null) {
					// now synchronize the invoiceHeader listBox
					getWorkFlowListCtrl().listBoxWorkFlow.getListModel();
				}

				closeDialog();
			}
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
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
	private boolean doSaveProcess(AuditHeader auditHeader){
		logger.debug("Entering ");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){

				auditHeader =   getWorkFlowDetailsService().saveOrUpdate(auditHeader);
				
				retValue = ErrorControl.showErrorControl(this.window_workFlowDialog, auditHeader);
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
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

	/**
	 * For validating the Errors
	 */
	private void validateErrors() {
		logger.debug("Entering ");
		//Window window = (Window) event.getTarget();
		int lastItemIndex = 0;
		InputElement errorComponent = null;

		for (Object component : this.window_workFlowDialog.getFellows()) {

			if (component instanceof InputElement) {
				String errorMessage = ((InputElement) component).getErrorMessage();
				if (errorMessage != null) {
					if (lastItemIndex > ((InputElement) component)
							.getTabindex() || lastItemIndex == 0) {
						errorComponent = (InputElement) component;						
					}
					((InputElement) component).setErrorMessage(errorMessage);
					lastItemIndex = ((InputElement) component).getTabindex();
				}				
			}else if (component instanceof Listbox) {				
			}			
		}
		if (errorComponent != null) {
			errorComponent.select();
			return;
		}	
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.workFlowType.setReadonly(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit(){
		logger.debug("Entering ");
		if (getWorkFlowDetails().isNew()){
			this.workFlowType.setReadonly(false);
		}else{
			this.workFlowType.setReadonly(true);
		}
		
		workFlowDetails.setWorkFlowActive(true);
		this.workFlowActive.setDisabled(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aWorkFlowDetails
	 *            (WorkFlowDetails)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(WorkFlowDetails aWorkFlowDetails, String tranType){
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aWorkFlowDetails.getBefImage(), aWorkFlowDetails);
		return new AuditHeader(String.valueOf(aWorkFlowDetails.getId()), null, null,
				null, auditDetail, aWorkFlowDetails.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public WorkFlowDetails getWorkFlowDetails() {
		return workFlowDetails;
	}
	public void setWorkFlowDetails(WorkFlowDetails workFlowDetails) {
		this.workFlowDetails = workFlowDetails;
	}

	public WorkFlowDetailsService getWorkFlowDetailsService() {
		return workFlowDetailsService;
	}
	public void setWorkFlowDetailsService(
			WorkFlowDetailsService workFlowDetailsService) {
		this.workFlowDetailsService = workFlowDetailsService;
	}
	
	public WorkFlowListCtrl getWorkFlowListCtrl() {
		return workFlowListCtrl;
	}
	public void setWorkFlowListCtrl(WorkFlowListCtrl workFlowListCtrl) {
		this.workFlowListCtrl = workFlowListCtrl;
	}

}
