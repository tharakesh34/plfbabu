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
 * FileName    		:  DiaryNotesDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.diarynotes.diarynotes;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.diarynotes.DiaryNotesService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/DiaryNotes/diaryNotesDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class DiaryNotesDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7815871009759396953L;
	private final static Logger logger = Logger.getLogger(DiaryNotesDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_DiaryNotesDialog; // autoWired

 	protected Combobox dnType; // autoWired
	protected Textbox dnCreatedNo; // autoWired
	protected Textbox dnCreatedName; // autoWired

	protected Textbox frqCode; // autoWired	
	protected Combobox cbfrqCode; // autoWired
	protected Combobox cbfrqMth; // autoWired
	protected Combobox cbfrqDays; // autoWired
	
  	protected Datebox firstActionDate; // autoWired
  	protected Datebox nextActionDate; // autoWired
  	protected Datebox lastActionDate; // autoWired
  	protected Datebox finalActionDate; // autoWired
	protected Checkbox suspend; // autoWired
  	protected Datebox suspendStartDate; // autoWired
  	protected Datebox suspendEndDate; // autoWired
	protected Checkbox recordDeleted; // autoWired
	protected Textbox narration; // autoWired

	protected Label recordStatus; // autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	
	private boolean suspended = false;
	private boolean	 validFrequency = false;
	
	protected Row startSuspend;
	protected Row endSuspend;

	// not auto wired vars
	private DiaryNotes diaryNotes; // overhanded per param
	private transient DiaryNotesListCtrl diaryNotesListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_dnType;
	private transient String  		oldVar_dnCreatedNo;
	private transient String  		oldVar_dnCreatedName;
	private transient String  		oldVar_frqCode;
	private transient Date  		oldVar_firstActionDate;
	private transient Date  		oldVar_nextActionDate;
	private transient Date  		oldVar_lastActionDate;
	private transient Date  		oldVar_finalActionDate;
	private transient boolean  		oldVar_suspend;
	private transient Date  		oldVar_suspendStartDate;
	private transient Date  		oldVar_suspendEndDate;
	private transient boolean  		oldVar_recordDeleted;
	private transient String  		oldVar_narration;
	private transient String 		oldVar_recordStatus;

	private transient boolean 		validationOn;
	private boolean 				notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_DiaryNotesDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	// ServiceDAOs / Domain Classes
	private transient DiaryNotesService diaryNotesService;
	private transient PagedListService pagedListService;
	private List<ValueLabel> listDnType=PennantAppUtil.getNotesType(); // autoWired
		
	private HashMap<String, ArrayList<ErrorDetails>> overrideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	
	/**
	 * default constructor.<br>
	 */
	public DiaryNotesDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected DiaryNotes object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DiaryNotesDialog(Event event) throws Exception {
		logger.debug(event.toString());			
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED params !
		if (args.containsKey("diaryNotes")) {
			this.diaryNotes = (DiaryNotes) args.get("diaryNotes");
			DiaryNotes befImage =new DiaryNotes();
			BeanUtils.copyProperties(this.diaryNotes, befImage);
			this.diaryNotes.setBefImage(befImage);
			
			setDiaryNotes(this.diaryNotes);
		} else {
			setDiaryNotes(null);
		}
	
		doLoadWorkFlow(this.diaryNotes.isWorkflow(),this.diaryNotes.getWorkflowId(),this.diaryNotes.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "DiaryNotesDialog");
		}
		setListDnType();
		
		// READ OVERHANDED params !
		// we get the diaryNotesListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete diaryNotes here.
		if (args.containsKey("diaryNotesListCtrl")) {
			setDiaryNotesListCtrl((DiaryNotesListCtrl) args.get("diaryNotesListCtrl"));
		} else {
			setDiaryNotesListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDiaryNotes());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.dnCreatedNo.setMaxlength(13);
		this.dnCreatedName.setMaxlength(50);
	  	this.firstActionDate.setFormat(PennantConstants.dateFormat);
	  	this.nextActionDate.setFormat(PennantConstants.dateFormat);
	  	this.lastActionDate.setFormat(PennantConstants.dateFormat);
	  	this.finalActionDate.setFormat(PennantConstants.dateFormat);
	  	this.suspendStartDate.setFormat(PennantConstants.dateFormat);
	  	this.suspendEndDate.setFormat(PennantConstants.dateFormat);
		
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
		
		getUserWorkspace().alocateAuthorities("DiaryNotesDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DiaryNotesDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DiaryNotesDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DiaryNotesDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DiaryNotesDialog_btnSave"));
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
	public void onClose$window_DiaryNotesDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_DiaryNotesDialog);
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
	
	// GUI Process

	
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
		logger.debug("Enterring");
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
			closeDialog(this.window_DiaryNotesDialog, "DiaryNotes");	
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
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDiaryNotes
	 *            DiaryNotes
	 */
	public void doWriteBeanToComponents(DiaryNotes aDiaryNotes) {
		logger.debug("Entering") ;
		
		DiaryNotes approvedDN 		= new DiaryNotes();
		String 	   dnType			= "";
		
		this.dnType.setValue(PennantAppUtil.getlabelDesc(aDiaryNotes.getDnType(),listDnType));
		
		// Added Newly to Identify Wheather the Record is Approved 
		approvedDN  = diaryNotesService.getApprovedDiaryNotesById(aDiaryNotes.getId());
			if(approvedDN !=null){ dnType = approvedDN.getDnType();	}
		
		if(!aDiaryNotes.isNew()){
			this.dnType.setDisabled(true);
		}
		
			this.dnCreatedNo.setValue(aDiaryNotes.getDnCreatedNo());
			this.dnCreatedName.setValue(aDiaryNotes.getDnCreatedName());		
			this.frqCode.setValue(aDiaryNotes.getFrqCode());		
		
			fillFrqCode(this.cbfrqCode,aDiaryNotes.getFrqCode(),isReadOnly("DiaryNotesDialog_frqCode"));
			fillFrqMth(this.cbfrqMth,aDiaryNotes.getFrqCode(),isReadOnly("DiaryNotesDialog_frqCode"));
			fillFrqDay(this.cbfrqDays,aDiaryNotes.getFrqCode(),isReadOnly("DiaryNotesDialog_frqCode"));
		
			//this.frqCode.setValue(aDiaryNotes.getFrqCode());
			this.firstActionDate.setValue(aDiaryNotes.getFirstActionDate());
			
			
			 
			
		if(!aDiaryNotes.isNew() && (aDiaryNotes.getRecordStatus().equalsIgnoreCase("Approved") ||
								!dnType.equals(""))){
			this.frqCode.setDisabled(true);
			this.dnCreatedNo.setDisabled(true);
			this.dnCreatedName.setDisabled(true);			
			this.firstActionDate.setDisabled(true);
		}
			
			this.nextActionDate.setDisabled(true);			
			this.nextActionDate.setValue(aDiaryNotes.getNextActionDate());		
			this.lastActionDate.setValue(aDiaryNotes.getLastActionDate());		
			this.finalActionDate.setValue(aDiaryNotes.getFinalActionDate());				
			this.suspend.setChecked(aDiaryNotes.isSuspend());		
		
		if(this.suspend.isChecked()){
		    this.suspendStartDate.setValue(aDiaryNotes.getSuspendStartDate());
		    this.suspendEndDate.setValue(aDiaryNotes.getSuspendEndDate());		  
		}else {			
			this.suspendStartDate.setText("");
			this.suspendEndDate.setText("");		  
			this.startSuspend.setVisible(false);
			this.endSuspend.setVisible(false);
		}
		
 			this.recordDeleted.setChecked(aDiaryNotes.isRecordDeleted());
 			this.narration.setValue(aDiaryNotes.getNarration());	
 			this.recordStatus.setValue(aDiaryNotes.getRecordStatus());
		logger.debug("Leaving");
	}
	
	

	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDiaryNotes
	 */
	public void doWriteComponentsToBean(DiaryNotes aDiaryNotes) {
		logger.debug("Entering") ;
		
		doSetLOVValidation();
		
			ArrayList<ErrorDetails> errorList = new ArrayList<ErrorDetails>();	
			
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
				try {
				    aDiaryNotes.setDnType((String) this.dnType.getSelectedItem().getValue());
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try {
				    aDiaryNotes.setDnCreatedNo(this.dnCreatedNo.getValue());
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try {
				    aDiaryNotes.setDnCreatedName(this.dnCreatedName.getValue());				    
				}catch (WrongValueException we ) {
					wve.add(we);
				}		
				
				try {
				    aDiaryNotes.setFirstActionDate(this.firstActionDate.getValue());				    
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try{
					  
				  if ((validateCombobox(this.cbfrqCode).equals("#"))) {
						throw new WrongValueException(this.cbfrqCode,Labels.getLabel("STATIC_INVALID",new String[] { Labels.getLabel("label_DiaryNotesDialog_dnDftStmtFrqCode.value") }));
				}					  
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try {
					if ((!validateCombobox(this.cbfrqCode).equals("#"))&& (validateCombobox(this.cbfrqMth).equals("#"))) {
						throw new WrongValueException(this.cbfrqMth,Labels.getLabel("STATIC_INVALID",new String[] { Labels.getLabel("label_FinanceTypeDialog_finDftStmtFrqMth.value") }));
					}
					
					aDiaryNotes.setFrqCode(this.frqCode.getValue());				    
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try {
					if ((!validateCombobox(this.cbfrqMth).equals("#"))&& (validateCombobox(this.cbfrqDays).equals("#"))) {
						throw new WrongValueException(this.cbfrqDays,Labels.getLabel("STATIC_INVALID",new String[] { Labels.getLabel("label_DiaryNotesDialog_dnDftStmtFrqDay.value") }));
					}
		
				}catch (WrongValueException we) {
					wve.add(we);
				}
					ErrorDetails errorDetails =null;
		
				try {
					errorDetails = FrequencyUtil.validateFrequency(this.frqCode.getValue());
					if(errorDetails!=null){
						throw new WrongValueException(this.cbfrqDays,errorDetails.getErrorMessage());	
					}
				}catch(WrongValueException we ){
					wve.add(we);
				}		
				
				try {
					if(errorDetails == null){
						validFrequency   = FrequencyUtil.isFrqDate(this.frqCode.getValue(),
											this.firstActionDate.getValue()!=null?this.firstActionDate.getValue():new Date());				
						if(!validFrequency){
							this.frqCode.setConstraint("NO TODAY,NO PAST:" + Labels.getLabel("FREQ_CODE_INVALID",new String[]{Labels.getLabel("label_DiaryNotesDialog_dnDftStmtFrqCode.value")}));				
							throw new WrongValueException(this.cbfrqDays,Labels.getLabel("FREQ_CODE_INVALID"));
						}
					}
				}catch(WrongValueException we){
					wve.add(we);
				}
				
				if(errorDetails== null){
						FrequencyDetails frequencyDetails = FrequencyUtil.getNextDate(this.frqCode.getValue(),1,
												  this.firstActionDate.getValue()!=null?this.firstActionDate.getValue():new Date(),"",false);
						if(this.nextActionDate.getValue() != null){							
							if(!this.nextActionDate.getValue().equals(frequencyDetails.getNextFrequencyDate())) {
								if(DateUtility.compare(this.nextActionDate.getValue(),frequencyDetails.getNextFrequencyDate()) > 0) {									
									errorList.add(new ErrorDetails("nextActionDate","W0001",
											new String[]{Labels.getLabel("label_DiaryNotesDialog_LastActionDate"),
													Labels.getLabel("label_DiaryNotesDialog_LastActionDate")},null));
									
									AuditHeader auditHeader = new AuditHeader();	
									auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
									auditHeader.setOverideMap(overrideMap);
									
									try {
										auditHeader = ErrorControl.showErrorDetails(this.window_DiaryNotesDialog, auditHeader);
										}catch(Exception e){
											e.printStackTrace();
										}
										
									overrideMap = auditHeader.getOverideMap();
									
									/*porcessOVERIDE=0; porcessCANCEL=1;*/								
									
									  if(auditHeader.getProcessStatus() == 1){
										  throw new WrongValueException(this.nextActionDate,Labels.getLabel("NEXT_ACTION_DATE_INAVLID"));
									  }
								}	
							 }
						}
						try {
							if(frequencyDetails.getNextFrequencyDate()!= null){
								aDiaryNotes.setNextActionDate(frequencyDetails.getNextFrequencyDate());
							}						    
						}catch (WrongValueException we ) {
								wve.add(we);
						}
				}	
				
				try {
				    if(this.finalActionDate.getValue() != null){		    	
				    	this.validate(this.finalActionDate, this.finalActionDate.getValue());
				    	aDiaryNotes.setFinalActionDate(this.finalActionDate.getValue());
				    }
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try {
					aDiaryNotes.setSuspend(this.suspend.isChecked());			
					aDiaryNotes.setSuspendStartDate(this.suspendStartDate.getValue());
					aDiaryNotes.setSuspendEndDate(this.suspendEndDate.getValue());
					
				}catch (WrongValueException we ) {
					wve.add(we);
				}
		
				if(!this.suspend.isDisabled()) {
					if(this.suspendStartDate.getValue()!=null || this.suspendEndDate.getValue()!= null){
						this.validate(this.suspendStartDate,this.suspendStartDate.getValue());
						this.validate(this.suspendEndDate,this.suspendEndDate.getValue());		
					}
				}	
		
				try {
					aDiaryNotes.setRecordDeleted(this.recordDeleted.isChecked());
				}catch (WrongValueException we ) {
					wve.add(we);
				}
				
				try {
				    aDiaryNotes.setNarration(this.narration.getValue());
				    
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
		
				aDiaryNotes.setRecordStatus(this.recordStatus.getValue());		
		logger.debug("Leaving");
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDiaryNotes
	 * @throws InterruptedException
	 */
	public void doShowDialog(DiaryNotes aDiaryNotes) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aDiaryNotes == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aDiaryNotes == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aDiaryNotes = getDiaryNotesService().getNewDiaryNotes();
			
			setDiaryNotes(aDiaryNotes);
		} else {
			setDiaryNotes(aDiaryNotes);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aDiaryNotes.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.dnType.focus();
		} else {
			this.dnType.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aDiaryNotes);	
			// stores the initial data for comparing if they are changed
			// during user action.s
			doStoreInitValues();
			setDialog(this.window_DiaryNotesDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	
	
	public void onCheck$suspend(Event event) {
			logger.debug("Entering");
			suspendCheck();
			logger.debug("Leaving");
		    }
	
	  /**
	     * Check Whether Suspend is Checked or Not
	     * 
	     */
    public void suspendCheck() {
		logger.debug("Entering");
		if (this.suspend.isChecked()) {			
		   this.startSuspend.setVisible(true);
		   this.endSuspend.setVisible(true);
		} else {			
		   this.suspendStartDate.setText("");
		   this.suspendEndDate.setText("");	
		   this.startSuspend.setVisible(false);
		   this.endSuspend.setVisible(false);
		}
		logger.debug("Leaving");
	    }
	
	
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Enterring");
		this.oldVar_dnType = this.dnType.getValue();
		this.oldVar_dnCreatedNo = this.dnCreatedNo.getValue();
		this.oldVar_dnCreatedName = this.dnCreatedName.getValue();
		this.oldVar_frqCode = this.frqCode.getValue();
		this.oldVar_firstActionDate = this.firstActionDate.getValue();
		this.oldVar_nextActionDate = this.nextActionDate.getValue();
		this.oldVar_lastActionDate = this.lastActionDate.getValue();
		this.oldVar_finalActionDate = this.finalActionDate.getValue();
		this.oldVar_suspend = this.suspend.isChecked();
		this.oldVar_suspendStartDate = this.suspendStartDate.getValue();
		this.oldVar_suspendEndDate = this.suspendEndDate.getValue();
		this.oldVar_recordDeleted = this.recordDeleted.isChecked();
		this.oldVar_narration = this.narration.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Enterring");
		this.dnType.setValue(this.oldVar_dnType);
		this.dnCreatedNo.setValue(this.oldVar_dnCreatedNo);
		this.dnCreatedName.setValue(this.oldVar_dnCreatedName);
		this.frqCode.setValue(this.oldVar_frqCode);
		this.firstActionDate.setValue(this.oldVar_firstActionDate);
		this.nextActionDate.setValue(this.oldVar_nextActionDate);
		this.lastActionDate.setValue(this.oldVar_lastActionDate);
		this.finalActionDate.setValue(this.oldVar_finalActionDate);
		this.suspend.setChecked(this.oldVar_suspend);
		this.suspendStartDate.setValue(this.oldVar_suspendStartDate);
		this.suspendEndDate.setValue(this.oldVar_suspendEndDate);
		this.recordDeleted.setChecked(this.oldVar_recordDeleted);
		this.narration.setValue(this.oldVar_narration);
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
		logger.debug("Enterring");
		//To clear the Error Messages
		doClearMessage();
		if (this.oldVar_dnType != this.dnType.getValue()) {
			return true;
		}
		if (this.oldVar_dnCreatedNo != this.dnCreatedNo.getValue()) {
			return true;
		}
		if (this.oldVar_dnCreatedName != this.dnCreatedName.getValue()) {
			return true;
		}
		if (this.oldVar_frqCode != this.frqCode.getValue()) {
			return true;
		}
	  	String old_firstActionDate = "";
	  	String new_firstActionDate ="";
		if (this.oldVar_firstActionDate!=null){
			old_firstActionDate=DateUtility.formatDate(this.oldVar_firstActionDate,PennantConstants.dateFormat);
		}
		if (this.firstActionDate.getValue()!=null){
			new_firstActionDate=DateUtility.formatDate(this.firstActionDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_firstActionDate).equals(StringUtils.trimToEmpty(new_firstActionDate))) {
			return true;
		}
	  	String old_nextActionDate = "";
	  	String new_nextActionDate ="";
		if (this.oldVar_nextActionDate!=null){
			old_nextActionDate=DateUtility.formatDate(this.oldVar_nextActionDate,PennantConstants.dateFormat);
		}
		if (this.nextActionDate.getValue()!=null){
			new_nextActionDate=DateUtility.formatDate(this.nextActionDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_nextActionDate).equals(StringUtils.trimToEmpty(new_nextActionDate))) {
			return true;
		}
	  	String old_lastActionDate = "";
	  	String new_lastActionDate ="";
		if (this.oldVar_lastActionDate!=null){
			old_lastActionDate=DateUtility.formatDate(this.oldVar_lastActionDate,PennantConstants.dateFormat);
		}
		if (this.lastActionDate.getValue()!=null){
			new_lastActionDate=DateUtility.formatDate(this.lastActionDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_lastActionDate).equals(StringUtils.trimToEmpty(new_lastActionDate))) {
			return true;
		}
	  	String old_finalActionDate = "";
	  	String new_finalActionDate ="";
		if (this.oldVar_finalActionDate!=null){
			old_finalActionDate=DateUtility.formatDate(this.oldVar_finalActionDate,PennantConstants.dateFormat);
		}
		if (this.finalActionDate.getValue()!=null){
			new_finalActionDate=DateUtility.formatDate(this.finalActionDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_finalActionDate).equals(StringUtils.trimToEmpty(new_finalActionDate))) {
			return true;
		}
		if (this.oldVar_suspend != this.suspend.isChecked()) {
			return true;
		}
	  	String old_suspendStartDate = "";
	  	String new_suspendStartDate ="";
		if (this.oldVar_suspendStartDate!=null){
			old_suspendStartDate=DateUtility.formatDate(this.oldVar_suspendStartDate,PennantConstants.dateFormat);
		}
		if (this.suspendStartDate.getValue()!=null){
			new_suspendStartDate=DateUtility.formatDate(this.suspendStartDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_suspendStartDate).equals(StringUtils.trimToEmpty(new_suspendStartDate))) {
			return true;
		}
	  	String old_suspendEndDate = "";
	  	String new_suspendEndDate ="";
		if (this.oldVar_suspendEndDate!=null){
			old_suspendEndDate=DateUtility.formatDate(this.oldVar_suspendEndDate,PennantConstants.dateFormat);
		}
		if (this.suspendEndDate.getValue()!=null){
			new_suspendEndDate=DateUtility.formatDate(this.suspendEndDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_suspendEndDate).equals(StringUtils.trimToEmpty(new_suspendEndDate))) {
			return true;
		}
		if (this.oldVar_recordDeleted != this.recordDeleted.isChecked()) {
			return true;
		}
		if (this.oldVar_narration != this.narration.getValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Enterring");
		setValidationOn(true);
	
		if (!this.dnType.isDisabled()){
			this.dnType.setConstraint(new StaticListValidator(listDnType,Labels.getLabel("label_DiaryNotesDialog_DnType.value")));
		}	
		if (!this.dnCreatedNo.isReadonly()){
			this.dnCreatedNo.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_DiaryNotesDialog_DnCreatedNo.value")}));
		}	
		if (!this.dnCreatedName.isReadonly()){
			this.dnCreatedName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_DiaryNotesDialog_DnCreatedName.value")}));
		}
		
		if(this.firstActionDate.getValue()!= null && !this.firstActionDate.isDisabled()){
			this.firstActionDate.setConstraint("NO EMPTY,NO TODAY,NO PAST:"+ Labels.getLabel("DATE_EMPTY_PAST_TODAY",new String[] { Labels.getLabel("label_DiaryNotesDialog_FirstActionDate.value") }));
		}
		
		if (this.nextActionDate.getValue()!= null && (!this.recordDeleted.isChecked()) && !this.nextActionDate.isDisabled()){
			this.nextActionDate.setConstraint("NO EMPTY,NO TODAY,NO PAST:" + Labels.getLabel("DATE_EMPTY_PAST_TODAY",new String[]{Labels.getLabel("label_DiaryNotesDialog_NextActionDate.value")}));
		}
				
		if(this.finalActionDate.getValue()!= null){			
			this.finalActionDate.setConstraint("NO EMPTY,NO TODAY,NO PAST:" + Labels.getLabel("DATE_EMPTY_PAST_TODAY",
						new String[]{Labels.getLabel("label_DiaryNotesDialog_FinalActionDate.value")}));
		}
		
		//Added Validation to be done when the Suspended is Selected
		if(this.suspend.isChecked()){
			
			if (!this.suspendStartDate.isDisabled()){
				this.suspendStartDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_DiaryNotesDialog_SuspendStartDate.value")}));
			}
		
			if (!this.suspendEndDate.isDisabled()){
				this.suspendEndDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_DiaryNotesDialog_SuspendEndDate.value")}));
			}
			
			if (!this.finalActionDate.isDisabled()){
				this.finalActionDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_DiaryNotesDialog_FinalActionDate.value")}));
			}
		
		}
		
		if (!this.narration.isReadonly()){
			this.narration.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_DiaryNotesDialog_Narration.value")}));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Enterring");
		setValidationOn(false);
		this.dnType.setConstraint("");
		this.dnCreatedNo.setConstraint("");
		this.dnCreatedName.setConstraint("");
		this.frqCode.setConstraint("");
		this.firstActionDate.setConstraint("");
		this.nextActionDate.setConstraint("");
		this.lastActionDate.setConstraint("");
		this.finalActionDate.setConstraint("");
		this.suspendStartDate.setConstraint("");
		this.suspendEndDate.setConstraint("");
		this.narration.setConstraint("");
	logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a DiaryNotes object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	private void doDelete() throws InterruptedException {
		logger.debug("Enterring");	
		final DiaryNotes aDiaryNotes = new DiaryNotes();
		BeanUtils.copyProperties(getDiaryNotes(), aDiaryNotes);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aDiaryNotes.getSeqNo();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aDiaryNotes.getRecordType()).equals("")){
				aDiaryNotes.setVersion(aDiaryNotes.getVersion()+1);
				aDiaryNotes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aDiaryNotes.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aDiaryNotes,tranType)){

					final JdbcSearchObject<DiaryNotes> soDiaryNotes = getDiaryNotesListCtrl().getSearchObj();
					// Set the ListModel
					getDiaryNotesListCtrl().getPagedListWrapper().setSearchObject(soDiaryNotes);

					// now synchronize the DiaryNotes listBox
					final ListModelList lml = (ListModelList) getDiaryNotesListCtrl().listBoxDiaryNotes.getListModel();

					// Check if the DiaryNotes object is new or updated -1
					// means that the obj is not in the list, so it's new ..
					if (lml.indexOf(aDiaryNotes) == -1) {
					} else {
						lml.remove(lml.indexOf(aDiaryNotes));
					}
					closeDialog(this.window_DiaryNotesDialog, "DiaryNotes"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new DiaryNotes object. <br>
	 */
	private void doNew() {
		logger.debug("Enterring");
		
		final DiaryNotes aDiaryNotes = getDiaryNotesService().getNewDiaryNotes();
		setDiaryNotes(aDiaryNotes);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.dnType.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Enterring");
		
		if (getDiaryNotes().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}
	
	 	this.dnType.setDisabled(isReadOnly("DiaryNotesDialog_dnType"));
		this.dnCreatedNo.setReadonly(isReadOnly("DiaryNotesDialog_dnCreatedNo"));
		this.dnCreatedName.setReadonly(isReadOnly("DiaryNotesDialog_dnCreatedName"));
		
		boolean readOnly=isReadOnly("DiaryNotesDialog_frqCode");

		this.frqCode.setReadonly(readOnly);
		this.cbfrqCode.setDisabled(readOnly);		
		this.cbfrqDays.setDisabled(readOnly);
		this.cbfrqMth.setDisabled(readOnly);

	 	this.firstActionDate.setDisabled(isReadOnly("DiaryNotesDialog_firstActionDate"));
	 	this.nextActionDate.setDisabled(isReadOnly("DiaryNotesDialog_nextActionDate"));
	 	this.lastActionDate.setDisabled(isReadOnly("DiaryNotesDialog_lastActionDate"));
	 	this.finalActionDate.setDisabled(isReadOnly("DiaryNotesDialog_finalActionDate"));
	 	this.suspend.setDisabled(isReadOnly("DiaryNotesDialog_suspend"));
	 	this.suspendStartDate.setDisabled(isReadOnly("DiaryNotesDialog_suspendStartDate"));
	 	this.suspendEndDate.setDisabled(isReadOnly("DiaryNotesDialog_suspendEndDate"));
	 	this.recordDeleted.setDisabled(isReadOnly("DiaryNotesDialog_recordDeleted"));
		this.narration.setReadonly(isReadOnly("DiaryNotesDialog_narration"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.diaryNotes.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Enterring");
		this.dnType.setDisabled(true);
		this.dnCreatedNo.setReadonly(true);
		this.dnCreatedName.setReadonly(true);
		this.frqCode.setReadonly(true);
		this.firstActionDate.setDisabled(true);
		this.nextActionDate.setDisabled(true);
		this.lastActionDate.setDisabled(true);
		this.finalActionDate.setDisabled(true);
		this.suspend.setDisabled(true);
		this.suspendStartDate.setDisabled(true);
		this.suspendEndDate.setDisabled(true);
		this.recordDeleted.setDisabled(true);
		this.narration.setReadonly(true);
		
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
		logger.debug("Enterring");
		// remove validation, if there are a save before
		
		this.dnType.setValue("");
		this.dnCreatedNo.setValue("");
		this.dnCreatedName.setValue("");
		this.frqCode.setValue("");
		this.firstActionDate.setText("");
		this.nextActionDate.setText("");
		this.lastActionDate.setText("");
		this.finalActionDate.setText("");
		this.suspend.setChecked(false);
		this.suspendStartDate.setText("");
		this.suspendEndDate.setText("");
		this.recordDeleted.setChecked(false);
		this.narration.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");
		final DiaryNotes aDiaryNotes = new DiaryNotes();
		BeanUtils.copyProperties(getDiaryNotes(), aDiaryNotes);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the DiaryNotes object with the components data
		doWriteComponentsToBean(aDiaryNotes);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aDiaryNotes.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aDiaryNotes.getRecordType()).equals("")){
				aDiaryNotes.setVersion(aDiaryNotes.getVersion()+1);
				if(isNew){
					aDiaryNotes.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aDiaryNotes.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					//aDiaryNotes.setRecordExist(true);
					aDiaryNotes.setNewRecord(true);
				}
			}
		}else{
			aDiaryNotes.setVersion(aDiaryNotes.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aDiaryNotes,tranType)){
				doWriteBeanToComponents(aDiaryNotes);
				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<DiaryNotes> soDiaryNotes = getDiaryNotesListCtrl().getSearchObj();

				// Set the ListModel
				getDiaryNotesListCtrl().pagingDiaryNotesList.setActivePage(0);
				getDiaryNotesListCtrl().getPagedListWrapper().setSearchObject(soDiaryNotes);

				// call from cusromerList then synchronize the DiaryNotes listBox
				if (getDiaryNotesListCtrl().listBoxDiaryNotes != null) {
					// now synchronize the DiaryNotes listBox
					getDiaryNotesListCtrl().listBoxDiaryNotes.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog(this.window_DiaryNotesDialog, "DiaryNotes");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(DiaryNotes aDiaryNotes,String tranType){
		logger.debug("Enterring");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aDiaryNotes.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aDiaryNotes.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDiaryNotes.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aDiaryNotes.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDiaryNotes.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aDiaryNotes);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aDiaryNotes))) {
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
			
			
			if (! StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;

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

			aDiaryNotes.setTaskId(taskId);
			aDiaryNotes.setNextTaskId(nextTaskId);
			aDiaryNotes.setRoleCode(getRole());
			aDiaryNotes.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aDiaryNotes, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aDiaryNotes);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aDiaryNotes, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aDiaryNotes, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Enterring");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getDiaryNotesService().delete(auditHeader);
					}else{
						auditHeader = getDiaryNotesService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getDiaryNotesService().doApprove(auditHeader);
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getDiaryNotesService().doReject(auditHeader);
						deleteNotes();
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DiaryNotesDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				retValue = ErrorControl.showErrorControl(this.window_DiaryNotesDialog, auditHeader);
				
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
			logger.error(e);
			e.printStackTrace();
		}
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

   private void setListDnType(){
		for (int i = 0; i < listDnType.size(); i++) {
			   Comboitem comboitem = new Comboitem();
			   comboitem = new Comboitem();
			   comboitem.setLabel(listDnType.get(i).getLabel());
			   comboitem.setValue(listDnType.get(i).getValue());
			   this.dnType.appendChild(comboitem);
		} 
	}

 	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public DiaryNotes getDiaryNotes() {
		return this.diaryNotes;
	}

	public void setDiaryNotes(DiaryNotes diaryNotes) {
		this.diaryNotes = diaryNotes;
	}

	public void setDiaryNotesService(DiaryNotesService diaryNotesService) {
		this.diaryNotesService = diaryNotesService;
	}

	public DiaryNotesService getDiaryNotesService() {
		return this.diaryNotesService;
	}

	public void setDiaryNotesListCtrl(DiaryNotesListCtrl diaryNotesListCtrl) {
		this.diaryNotesListCtrl = diaryNotesListCtrl;
	}

	public DiaryNotesListCtrl getDiaryNotesListCtrl() {
		return this.diaryNotesListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	
	private AuditHeader getAuditHeader(DiaryNotes aDiaryNotes, String tranType){
		  AuditDetail auditDetail = new AuditDetail(tranType, 1, aDiaryNotes.getBefImage(), aDiaryNotes);   
		  return new AuditHeader(String.valueOf(aDiaryNotes.getSeqNo()),null,null,null,auditDetail,aDiaryNotes.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_DiaryNotesDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
	
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Enterring");
		// logger.debug(event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private void doSetLOVValidation() {
	}
	private void doRemoveLOVValidation() {
	}
	
	private void deleteNotes(){
			NotesService notesService= (NotesService) SpringUtil.getBean("notesService");		
			notesService.delete(getNotes());
	}
	
	private Notes getNotes(){
			Notes notes = new Notes();
			notes.setModuleName("DiaryNotes");
			notes.setReference(String.valueOf(getDiaryNotes().getSeqNo()));
			notes.setVersion(getDiaryNotes().getVersion());
			return notes;
	}
	
	private void doClearMessage() {
		logger.debug("Enterring");
			this.dnType.setErrorMessage("");
			this.dnCreatedNo.setErrorMessage("");
			this.dnCreatedName.setErrorMessage("");
			this.frqCode.setErrorMessage("");
			this.firstActionDate.setErrorMessage("");
			this.nextActionDate.setErrorMessage("");
			this.lastActionDate.setErrorMessage("");
			this.finalActionDate.setErrorMessage("");
			this.suspendStartDate.setErrorMessage("");
			this.suspendEndDate.setErrorMessage("");
			this.narration.setErrorMessage("");
	logger.debug("Leaving");
	}
	
	// Default Frequency Code comboBox change
	public void onSelect$cbfrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = validateCombobox(this.cbfrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfrqCode,
				this.cbfrqMth, this.cbfrqDays,
				this.frqCode,isReadOnly("DiaryNotesDialog_frqCode"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = validateCombobox(this.cbfrqCode);
		String stmtFrqMonth = validateCombobox(this.cbfrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfrqMth,
				this.cbfrqDays, this.frqCode,isReadOnly("DiaryNotesDialog_frqCode"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = validateCombobox(this.cbfrqCode);
		String stmtFrqMonth = validateCombobox(this.cbfrqMth);
		String stmtFrqday = validateCombobox(this.cbfrqDays);
		onSelectFrqDay(stmtFrqCode, stmtFrqMonth, stmtFrqday, frqCode);
		logger.debug("Leaving" + event.toString());
	}	
	
	/** To get the Combobox selected value */
	private String validateCombobox(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}
	
	public void validate(Component comp, Object value) throws WrongValueException {
		logger.debug("Entering ");
		
		if(this.suspend.isChecked()){		
			if(DateUtility.compare(this.firstActionDate.getValue(), this.suspendStartDate.getValue()) > 0) {				
				this.suspendStartDate.setConstraint("NO TODAY,NO PAST:" + Labels.getLabel("DATE_PAST_TODAY",new String[]{Labels.getLabel("label_DiaryNotesDialog_SuspendStartDate.value")}));
				throw new WrongValueException(suspendStartDate, Labels.getLabel("label_DiaryNotesDialog_SuspendStartDate.value")+"Should be Greater than First Action Date");
			}
			
			if(DateUtility.compare(this.finalActionDate.getValue(), this.suspendEndDate.getValue()) < 0  ||
					DateUtility.compare(this.firstActionDate.getValue(), this.suspendEndDate.getValue()) > 0) {				
				this.suspendEndDate.setConstraint("NO TODAY,NO PAST:" + Labels.getLabel("DATE_PAST_TODAY",new String[]{Labels.getLabel("label_DiaryNotesDialog_SuspendEndDate.value")}));
				throw new WrongValueException(suspendEndDate, Labels.getLabel("label_DiaryNotesDialog_SuspendEndDate.value")+" Should be Between First and Final Action Dates");
			}
		}
			
		 if(DateUtility.compare(this.finalActionDate.getValue(),this.firstActionDate.getValue()) < 0) {				
				this.finalActionDate.setConstraint("NO TODAY,NO PAST:" + Labels.getLabel("DATE_PAST_TODAY",
								new String[]{Labels.getLabel("label_DiaryNotesDialog_FinalActionDate.value")}));
				throw new WrongValueException(finalActionDate, Labels.getLabel("label_DiaryNotesDialog_FinalActionDate.value")+"Should be Greater than First Action Date");
		}
		
		logger.debug("Leaving ");
	}
	
}
