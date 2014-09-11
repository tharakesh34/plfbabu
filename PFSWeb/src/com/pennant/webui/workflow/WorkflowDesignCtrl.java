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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

import com.pennant.UserWorkspace;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.WorkFlowDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Workflow;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class WorkflowDesignCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = -1351367303946249042L;

	private final static Logger logger = Logger
			.getLogger(WorkflowDesignCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_workflowDesign; // autowired
	protected Textbox workFlowType;
	protected Textbox workFlowSubType;
	protected Textbox workFlowDesc;
	protected Textbox workFlowXML;
	protected Checkbox workFlowActive;
	protected Groupbox gb_basicDetails;

	// not auto wired vars
	private WorkFlowDetails workFlowDetails; // overhanded per param
	private transient WorkFlowListCtrl workFlowListCtrl; // overhanded per param

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_WorkFlowDialog_";
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnUpload; // autowire
	protected Button btnClose;
	protected Iframe iframe;

	private transient String oldVar_type;
	private transient String oldVar_subType;
	private transient String oldVar_Desc;
	private transient String oldVar_xmlData;
	private transient boolean oldVar_status;

	private transient WorkFlowDetailsService workFlowDetailsService;
	protected JdbcSearchObject<WorkFlowDetails> searchObj;

	public WorkflowDesignCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected WorkFlow object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_workflowDesign(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("workFlowDetails")) {
			this.workFlowDetails = (WorkFlowDetails) args
					.get("workFlowDetails");
			WorkFlowDetails flowDetails = new WorkFlowDetails();
			BeanUtils.copyProperties(this.workFlowDetails, flowDetails);
			this.workFlowDetails.setBefImage(flowDetails);

		} else {
			setWorkFlowDetails(null);
		}

		if (args.containsKey("workFlowListCtrl")) {
			setWorkFlowListCtrl((WorkFlowListCtrl) args.get("workFlowListCtrl"));
		} else {
			setWorkFlowListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getWorkFlowDetails());
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		this.workFlowXML.setValue("");

		if (event.getMedia().getName().endsWith(".xml")) {
			try {
				ByteArrayInputStream xmlStream = new ByteArrayInputStream(event
						.getMedia().getStringData().getBytes());
				XMLStreamReader parser = XMLInputFactory.newInstance()
						.createXMLStreamReader(xmlStream);
				StAXOMBuilder builder = new StAXOMBuilder(parser);
				OMElement xmlEnvelope = builder.getDocumentElement();
				this.workFlowXML.setValue(xmlEnvelope.toString());

				Workflow workFlow = new Workflow(builder);
				this.workFlowDetails
						.setFirstTaskOwner(workFlow.firstTask.owner);
				this.workFlowDetails.setWorkFlowRoles(workFlow.getRoles());

				this.btnSave.setVisible(true);
			} catch (Exception e) {
				PTMessageUtils.showErrorMessage("Unable to parse the File");
			}
		} else {
			PTMessageUtils
					.showErrorMessage("The file must be an .xml file. Please select another file");
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
		doResetInitValues();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initialized values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.workFlowSubType.setValue(this.oldVar_subType);
		this.workFlowDesc.setValue(this.oldVar_Desc);
		this.workFlowXML.setValue(this.oldVar_xmlData);
		this.workFlowType.setValue(this.oldVar_type);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aWorkFlowDetails
	 *            WorkFlowDetails
	 * @throws Exception
	 */
	public void doWriteBeanToComponents(WorkFlowDetails aWorkFlowDetails)
			throws Exception {
		logger.debug("Entering ");
		this.workFlowType.setValue(aWorkFlowDetails.getWorkFlowType());
		this.workFlowSubType.setValue(aWorkFlowDetails.getWorkFlowSubType());
		this.workFlowDesc.setValue(aWorkFlowDetails.getWorkFlowDesc());
		if (aWorkFlowDetails.getWorkFlowXml() != null) {
			this.workFlowXML.setValue(aWorkFlowDetails.getWorkFlowXml()
					.toString());
		} else {
			this.workFlowXML.setValue("");
		}
		this.workFlowActive.setChecked(aWorkFlowDetails.isWorkFlowActive());

		Workflow.writeJsonToFile(aWorkFlowDetails.getWorkFlowType(),
				aWorkFlowDetails.getJsonDesign());

		this.iframe.setSrc(Workflow.getPbpmUrl() + "&uuid="
				+ Workflow.getPbpmPackage() + "_"
				+ aWorkFlowDetails.getWorkFlowType());
		this.iframe.setAttribute("MYNAME", "SAI");

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
		logger.debug("Entering ");
		// if aWorkFlowDetails == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aWorkFlowDetails == null) {
			aWorkFlowDetails = getWorkFlowDetailsService()
					.getNewWorkFlowDetails();
			setWorkFlowDetails(aWorkFlowDetails);
		} else {
			setWorkFlowDetails(aWorkFlowDetails);
		}
		if (aWorkFlowDetails.isNew()) {
			doEdit();
		} else {
			doReadOnly();
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aWorkFlowDetails);
			doStoreInitValues();
			setDialog(this.window_workflowDesign); // open the dialog in modal
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * Stores the initialized values in member variables. <br>
	 */
	public void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_type = this.workFlowType.getValue();
		this.oldVar_subType = this.workFlowSubType.getValue();
		this.oldVar_Desc = this.workFlowDesc.getValue();
		this.oldVar_xmlData = this.workFlowXML.getValue();
		if (this.workFlowActive.isChecked()) {
			this.oldVar_status = true;
		} else {
			this.oldVar_status = false;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWorkFlowDetails
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 */
	public void doWriteComponentsToBean(WorkFlowDetails aWorkFlowDetails)
			throws DocumentException, FileNotFoundException, IOException,
			XMLStreamException, FactoryConfigurationError {
		logger.debug("Entering ");
		aWorkFlowDetails.setWorkFlowSubType(this.workFlowSubType.getValue());
		aWorkFlowDetails.setWorkFlowDesc(this.workFlowDesc.getValue());
		aWorkFlowDetails.setWorkFlowXml(this.workFlowXML.getValue());
		aWorkFlowDetails.setWorkFlowType(this.workFlowType.getValue());

		aWorkFlowDetails.setJsonDesign(Workflow.getJsonDesign(aWorkFlowDetails
				.getWorkFlowType()));

		logger.debug("Leaving ");
	}

	/*
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */

	public void doSave() throws Exception {
		logger.debug("Entering ");

		if (!Workflow.bpmnSaved(this.workFlowType.getValue())) {
			PTMessageUtils
					.showErrorMessage("Please save the process diagram before submitting.");
			return;
		}

		StAXOMBuilder builder = Workflow.getBpmnBuilder(this.workFlowType
				.getValue());

		OMElement element = builder.getDocumentElement();
		this.workFlowXML.setValue(element.toString());

		Workflow workflow = null;
		try {
			workflow = new Workflow(builder);
		} catch (Exception ex) {
			PTMessageUtils
					.showErrorMessage("Please validate the process diagram before submitting.");
			return;
		}
		this.workFlowDetails.setFirstTaskOwner(workflow.firstTask.owner);
		this.workFlowDetails.setWorkFlowRoles(workflow.getRoles());

		final WorkFlowDetails aWorkFlowDetails = getWorkFlowDetails();
		// fill the WorkFlowDetails object with the components data
		AuditHeader auditHeader = null;
		doWriteComponentsToBean(aWorkFlowDetails);

		// save it to database
		try {

			aWorkFlowDetails.setLastMntBy(getUserWorkspace()
					.getLoginUserDetails().getLoginUsrID());
			aWorkFlowDetails.setLastMntOn(new Timestamp(System
					.currentTimeMillis()));
			aWorkFlowDetails.setUserDetails(getUserWorkspace()
					.getLoginUserDetails());
			aWorkFlowDetails.setWorkFlowActive(true);

			String tranType = "";
			if (aWorkFlowDetails.isNew()) {
				tranType = PennantConstants.RECORD_TYPE_NEW;
			} else {
				tranType = PennantConstants.RECORD_TYPE_UPD;
			}

			auditHeader = getAuditHeader(aWorkFlowDetails, tranType);

			if (doSaveProcess(auditHeader)) {
				final JdbcSearchObject<WorkFlowDetails> so = getWorkFlowListCtrl()
						.getSearchObj();

				// Set the ListModel
				getWorkFlowListCtrl().pagingWorkFlowList.setActivePage(0);
				getWorkFlowListCtrl().getPagedListWrapper().setSearchObject(so);
				// call from cusromerList then synchronize the invoiceHeader
				// listBox
				if (getWorkFlowListCtrl().listBoxWorkFlow != null) {
					// now synchronize the invoiceHeader listBox
					getWorkFlowListCtrl().listBoxWorkFlow.getListModel();
				}

				closeDialog(this.window_workflowDesign, "WorkFlowDialog");
			}

		} catch (final DataAccessException e) {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_9999, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_workflowDesign,
					auditHeader);
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
	private boolean doSaveProcess(AuditHeader auditHeader) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				auditHeader = getWorkFlowDetailsService().saveOrUpdate(
						auditHeader);

				retValue = ErrorControl.showErrorControl(
						this.window_workflowDesign, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_invoiceHeaderDialog(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final Exception e) {
			// close anyway
			closeDialog(this.window_workflowDesign, "WorkFlowDialog");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws Exception {
		logger.debug("Entering ");
		if (isDataChanged()) {

			// Show a confirm box
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
					| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,
					true, new EventListener<Event>() {
						@Override
						public void onEvent(Event evt) {
							switch (((Integer) evt.getData()).intValue()) {
							case MultiLineMessageBox.YES:
								try {
									validateErrors();
									doSave();
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							case MultiLineMessageBox.NO:
								break; //
							}
						}
					}

			) == MultiLineMessageBox.YES) {
			}
		}
		final UserWorkspace workspace = getUserWorkspace();
		workspace.deAlocateAuthorities("WorkFlowDialog");
		closeDialog(this.window_workflowDesign, "WorkFlowDialog");
		logger.debug("Leaving ");
	}

	/**
	 * For validating the Errors
	 */
	private void validateErrors() {
		logger.debug("Entering ");
		// Window window = (Window) event.getTarget();
		int lastItemIndex = 0;
		InputElement errorComponent = null;

		for (Object component : this.window_workflowDesign.getFellows()) {

			if (component instanceof InputElement) {
				String errorMessage = ((InputElement) component)
						.getErrorMessage();
				if (errorMessage != null) {
					if (lastItemIndex > ((InputElement) component)
							.getTabindex() || lastItemIndex == 0) {
						errorComponent = ((InputElement) component);
					}
					((InputElement) component).setErrorMessage(errorMessage);
					lastItemIndex = ((InputElement) component).getTabindex();
				}
			} else if (component instanceof Listbox) {
			}
		}
		if (errorComponent != null) {
			errorComponent.select();
			return;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering ");
		boolean changed = false;

		if (this.oldVar_type != this.workFlowType.getValue()) {
			changed = true;
		}
		if (this.oldVar_subType != this.workFlowSubType.getValue()) {
			changed = true;
		}
		if (this.oldVar_Desc != this.workFlowDesc.getValue()) {
			changed = true;
		}
		if (this.oldVar_xmlData != this.workFlowXML.getValue()) {
			changed = true;
		}
		if (this.oldVar_status != this.workFlowActive.isChecked()) {
			changed = true;
		}
		logger.debug("Leaving ");
		return changed;
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
	public void doEdit() {
		logger.debug("Entering ");
		if (getWorkFlowDetails().isNew()) {
			this.workFlowType.setReadonly(false);
		} else {
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
	private AuditHeader getAuditHeader(WorkFlowDetails aWorkFlowDetails,
			String tranType) {
		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aWorkFlowDetails.getBefImage(), aWorkFlowDetails);
		return new AuditHeader(String.valueOf(aWorkFlowDetails.getId()), null,
				null, null, auditDetail, aWorkFlowDetails.getUserDetails(),
				getOverideMap());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
