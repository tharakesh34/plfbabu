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
 * FileName    		:  MailTemplateDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2012    														*
 *                                                                  						*
 * Modified Date    :  04-10-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.mail.mailtemplate;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
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
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.zkforge.ckez.CKeditor;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.model.DisbursementDetail;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.TemplateFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Mail/MailTemplate/mailTemplateDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class MailTemplateDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4140622258920094017L;
	private final static Logger logger = Logger.getLogger(MailTemplateDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_MailTemplateDialog; // autowired
	protected Tab  			basicDetailsTab;// autowired
	protected Tab  			emailDetailsTab;// autowired
	protected Div 			div_emailDetailsTab; // autowired
	protected Groupbox		gb_emailDetails; // autowired
	protected Label 		label_MailTemplateDialog_TemplateCode;
	protected Hlayout 		hlayout_templateCode; // autowired
	protected Space 		space_templateCode; // autowired
	protected Row 			row_Code; // autowired
	protected Textbox 		templateModule; // autowired
	protected Label 		label_MailTemplateDialog_TemplateDesc;
	protected Hlayout 		hlayout_templateDesc; // autowired
	protected Space 		space_templateDesc; // autowired
	protected Textbox 		templateDesc; // autowired
	protected Row 			row_turnAroundTime; // autowired
	protected Label 		label_MailTemplateDialog_turnAroundTime;
	protected Hlayout 		hlayout_turnAroundTime; // autowired
	protected Space 		space_turnAroundTime; // autowired
 	protected Intbox 		turnAroundTime; // autowired
	protected Label 		label_MailTemplateDialog_TemplateType;
	protected Hlayout 		hlayout_templateType; // autowired
	protected Space 		space_templateType; // autowired	
	protected Row 			row_templateRepeat; // autowired
 	protected Checkbox 		templateRepeat; // autowired
	protected Label 		label_MailTemplateDialog_TemplateRepeat;
	protected Hlayout 		hlayout_templateRepeat; // autowired
	protected Space 		space_templateRepeat; // autowired	
	protected Row 			row_templateFor; // autowired
 	protected Checkbox 		templateForSMS; // autowired
	protected Label 		label_MailTemplateDialog_TemplateForSMS;
	protected Hlayout 		hlayout_templateForSMS; // autowired
	protected Space 		space_templateForSMS; // autowired
	protected Checkbox 		templateForEmail; // autowired
	protected Label 		label_MailTemplateDialog_TemplateForEmail;
	protected Hlayout 		hlayout_templateForEmail; // autowired
	protected Space 		space_templateForEmail; // autowired
	protected Row 			row_EmailFormat; // autowired
	protected Label 		label_MailTemplateDialog_EmailFormat;
	protected Hlayout 		hlayout_emailFormat; // autowired
	protected Space 		space_emailFormat; // autowired
 	protected Combobox 		emailFormat; // autowired
	protected Label 		label_MailTemplateDialog_EmailSubject;
	protected Hlayout 		hlayout_emailSubject; // autowired
	protected Space 		space_emailSubject; // autowired
	protected Row 			row_EmailSubject; // autowired
	protected Textbox 		emailSubject; // autowired
	protected Row 			row_EmailSendTo; // autowired	
	protected Hlayout 		hlayout_emailSendTo; // autowired
	protected Label 		label_MailTemplateDialog_EmailSendTo;
	protected Space 		space_SendTo; // autowired
	protected Hbox	 		hboxPSH; // autowired
	protected Hbox	 		hboxUsers; // autowired
	protected Textbox	 	userIds; // autowired
	protected Textbox	 	lovDescUserNames; // autowired
	protected Button	 	btnUserIds; // autowired
	protected Checkbox		emailSendToPrimary; // autowired
	protected Checkbox 		emailSendToSecondary; // autowired
	protected Checkbox 		emailSendToHelpdesk; // autowired
	
	protected Label 		label_MailTemplateDialog_Active;
	protected Hlayout 		hlayout_active; // autowired
	protected Row 			row_SMSContent; // autowired
	protected Textbox 		smsContent; // autowired
	protected Label 		label_MailTemplateDialog_SMSContent;
	protected Hlayout 		hlayout_SMSContent; // autowired
	protected Space 		space_active; // autowired
	protected Checkbox 		active; // autowired
	protected Row 			row_EmailContent;// autowired
	protected Space 		space_templateContent;// autowired
	protected Div 			divHtmlArtifact; // autowired

	protected CKeditor 		htmlArtifact; // autowired
	protected Textbox 		plainText; // autowire
	protected Hbox 			scriptBox; // autowire
	
	protected Listbox 		templateData; // auto wired
	protected Column 		column_ckEditor; // autowire
	protected Column 		column_templateData; // autowire
	
	protected Label 		recordStatus; // autowired
	protected Label 		recordType;	 // autoWiredd
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean enqModule=false;
	
	
	// not auto wired vars
	private MailTemplate mailTemplate; // overhanded per param
	private MailTemplate prvMailTemplate; // overhanded per param
	private transient MailTemplateListCtrl mailTemplateListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_templateCode;
	private transient String  		oldVar_templateDesc;
	private transient boolean  		oldVar_templateForSMS;
	private transient boolean  		oldVar_templateRepeat;
	private transient int  			oldVar_turnAroundTime;
	private transient String		oldVar_SMSContent;
	private transient boolean  		oldVar_templateForEmail;
	private transient String		oldVar_EmailContent;
	private transient int  			oldVar_emailFormat;
	private transient String  		oldVar_emailSubject;
	private transient boolean  		oldVar_active;
	private transient String  		oldVar_sendToUserIds;
	private transient boolean  		oldVar_sendToPrimary;
	private transient boolean  		oldVar_sendToSecondary;
	private transient boolean  		oldVar_sendToHelpdesk;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_MailTemplateDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	protected Button btnSimulate;
	
	// ServiceDAOs / Domain Classes
	private transient MailTemplateService mailTemplateService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<ValueLabel> listEmailFormat=PennantAppUtil.getTemplateFormat(); // autowired
	private HashMap<String, String> filedValues=new HashMap<String, String>();
	private HashMap<String, String> filedDesc=new HashMap<String, String>();
	public int borderLayoutHeight=0;
	
	/**
	 * default constructor.<br>
	 */
	public MailTemplateDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected MailTemplate object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_MailTemplateDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
	
			// READ OVERHANDED params !
			if (args.containsKey("mailTemplate")) {
				this.mailTemplate = (MailTemplate) args.get("mailTemplate");
				MailTemplate befImage =new MailTemplate();
				BeanUtils.copyProperties(this.mailTemplate, befImage);
				this.mailTemplate.setBefImage(befImage);
				
				setMailTemplate(this.mailTemplate);
			} else {
				setMailTemplate(null);
			}
			doLoadWorkFlow(this.mailTemplate.isWorkflow(),this.mailTemplate.getWorkflowId(),this.mailTemplate.getNextTaskId());
	
			if (isWorkFlowEnabled()){
				if(!enqModule){
					this.userAction	= setListRecordStatus(this.userAction);
				}
				getUserWorkspace().alocateRoleAuthorities(getRole(), "MailTemplateDialog");
			}
	
			// READ OVERHANDED params !
			// we get the mailTemplateListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete mailTemplate here.
			if (args.containsKey("mailTemplateListCtrl")) {
				setMailTemplateListCtrl((MailTemplateListCtrl) args.get("mailTemplateListCtrl"));
			} else {
				setMailTemplateListCtrl(null);
			}
	
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
			.getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.div_emailDetailsTab.setHeight(borderLayoutHeight-140+"px");//425px
			this.gb_emailDetails.setHeight(borderLayoutHeight-140+"px");//425px
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMailTemplate());
			this.btnDelete.setVisible(false);
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_MailTemplateDialog.onClose();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnUserIds(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		String userIds ="";
		String userNames = "";
		@SuppressWarnings("unchecked")
		List<Object> selectedValues= (List<Object>) MultiSelectionSearchListBox.show(this.window_MailTemplateDialog, "SecurityUser",
				String.valueOf(this.userIds.getValue()),new Filter[]{});
		if (selectedValues!= null) {			
			for(int i=0;i<selectedValues.size();i++){
				SecurityUser selectedValue = (SecurityUser) selectedValues.get(i);
				userIds = userIds+selectedValue.getUsrID()+",";
				userNames = userNames+selectedValue.getUsrLogin()+",";
				if(i == selectedValues.size() - 1){
					userIds = userIds.substring(0, userIds.lastIndexOf(','));
					userNames = userNames.substring(0, userNames.lastIndexOf(','));
				}
			}
			this.userIds.setValue(userIds);
			this.lovDescUserNames.setValue(userNames);
			this.lovDescUserNames.setTooltiptext(userNames);
		}
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.emailSubject.setMaxlength(100);
		this.smsContent.setMaxlength(Integer.parseInt(SystemParameterDetails.getSystemParameterValue("SMS_LEN").toString()));
		
		if (isWorkFlowEnabled()){
			if(enqModule){
				groupboxWf.setVisible(false);
				south.setHeight("60px");
			}
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
		getUserWorkspace().alocateAuthorities("MailTemplateDialog");
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MailTemplateDialog_btnSave"));	
		}
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
	public void onClose$window_MailTemplateDialog(Event event) throws Exception {
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
	public void onClick$btnSave(Event event) throws Exception {
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
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_MailTemplateDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		// remember the old vars
		doStoreInitValues();
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
	public void onClick$btnClose(Event event) throws Exception {
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
	private void doClose() throws Exception {
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
			closeDialog(this.window_MailTemplateDialog, "MailTemplate");	
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
		doReadOnly(true);
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMailTemplate
	 *            MailTemplate
	 * @throws Exception 
	 */
	public void doWriteBeanToComponents(MailTemplate aMailTemplate) throws Exception {
		logger.debug("Entering") ;
		
		this.templateModule.setValue(aMailTemplate.getTemplateCode());
		if (!getMailTemplate().isNewRecord()) {
			this.templateModule.setDisabled(true);
		}
		
		this.templateDesc.setValue(aMailTemplate.getTemplateDesc());

		if(getMailTemplate().getTemplateFor().equals(PennantConstants.TEMPLATE_FOR_AE)) {
			this.row_turnAroundTime.setVisible(true);
			this.turnAroundTime.setValue(aMailTemplate.getTurnAroundTime());
			this.row_templateRepeat.setVisible(true);
			this.templateRepeat.setChecked(aMailTemplate.isRepeat());
		}
		
		this.templateForSMS.setChecked(aMailTemplate.isSmsTemplate());
		this.smsContent.setValue(aMailTemplate.getSmsContent());
		loadSMSFields();	
		
		this.templateForEmail.setChecked(aMailTemplate.isEmailTemplate());
		aMailTemplate.setEmailFormat("H");
		fillComboBox(this.emailFormat, aMailTemplate.getEmailFormat(), listEmailFormat,"");
				
		if(aMailTemplate.isEmailTemplate()) {
			this.emailDetailsTab.setDisabled(false);
			if(getMailTemplate().getTemplateFor().equals(PennantConstants.TEMPLATE_FOR_AE)) {
				this.hboxPSH.setVisible(false);
				this.hboxUsers.setVisible(true);
				this.row_EmailSendTo.setVisible(true);
				if(!StringUtils.trimToEmpty(aMailTemplate.getEmailSendTo()).equals("")) {
					List<SecurityUser> secUsers = getMailTemplateService().getSecurityUsersByIds(aMailTemplate.getEmailSendTo());
					String userNames = "";
					for(int i = 0;i<secUsers.size();i++){
						userNames = userNames+secUsers.get(i).getUsrLogin()+",";
						if(i == secUsers.size()-1) {
							userNames = userNames.substring(0, userNames.lastIndexOf(','));
						}
					}
					this.userIds.setValue(aMailTemplate.getEmailSendTo());
					this.lovDescUserNames.setValue(userNames);
				}
			}
			this.emailSubject.setValue(aMailTemplate.getEmailSubject());

			doSetArtifact(aMailTemplate.getEmailFormat());
			String type = aMailTemplate.getEmailFormat();
			if (type != null) {
				if (PennantConstants.TEMPLATE_FORMAT_HTML.equals(type)) {
					this.htmlArtifact.setValue(new String(aMailTemplate.getEmailContent() , PennantConstants.DEFAULT_CHARSET));
					this.divHtmlArtifact.appendChild(new Html(new String(
							aMailTemplate.getEmailContent(),
							PennantConstants.DEFAULT_CHARSET)));
					doFillTemplateFields(aMailTemplate.getModule()); 
				} else if (PennantConstants.TEMPLATE_FORMAT_PLAIN.equals(type)) {
					this.plainText.setValue(new String(aMailTemplate.getEmailContent(),
							PennantConstants.DEFAULT_CHARSET));
				} 
			}
		}
		this.active.setChecked(aMailTemplate.isActive());
		if(aMailTemplate.isNew() || aMailTemplate.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
	
		this.recordStatus.setValue(aMailTemplate.getRecordStatus());
		this.recordType.setValue(aMailTemplate.getRecordType());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMailTemplate
	 * @throws Exception 
	 */
	public void doWriteComponentsToBean(MailTemplate aMailTemplate) throws Exception {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		// Basic Details Tab
		//Template Code
		try {
		    aMailTemplate.setTemplateCode(this.templateModule.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		//Template Desc
		try {
		    aMailTemplate.setTemplateDesc(this.templateDesc.getValue().toString());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		//Template TAT
		try {
			if(this.row_turnAroundTime.isVisible()) {
				aMailTemplate.setTurnAroundTime(this.turnAroundTime.getValue());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		//Template Repeat
		try {
			if(this.row_templateRepeat.isVisible()) {
				aMailTemplate.setRepeat(this.templateRepeat.isChecked());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		
		//Template For SMS
		try {
			aMailTemplate.setSmsTemplate(this.templateForSMS.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(!this.templateForSMS.isChecked() && !this.templateForEmail.isChecked()) {
				throw new WrongValueException(
						this.templateForSMS,
						Labels.getLabel(
								"EITHER_OR",
								new String[] {Labels
										.getLabel("label_MailTemplateDialog_TemplateForSMS.value"), Labels
										.getLabel("label_MailTemplateDialog_TemplateForEmail.value") }));			
			}
		} catch(WrongValueException we ) {
			wve.add(we);
		}
		
		//SMS Content
		if(this.templateForSMS.isChecked()) {
			try {
				if (StringUtils.trimToEmpty(this.smsContent.getValue())
						.equals("")) {
					throw new WrongValueException(
							this.smsContent,
							Labels.getLabel(
									"FIELD_NO_EMPTY",
									new String[] { Labels
											.getLabel("label_MailTemplateDialog_SMS.value") }));
				} else {
					aMailTemplate.setSmsContent(this.smsContent.getValue());
				}

			} catch(WrongValueException we ) {
				wve.add(we);
			}				
		}
		
		showErrorDetails(wve, this.basicDetailsTab);
		
		// Email Template Tab		
		//Template For EMail
		try {
			aMailTemplate.setEmailTemplate(this.templateForEmail.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		if(this.templateForEmail.isChecked()) {
			//Email Send to
			if(this.row_EmailSendTo.isVisible()){
				try {
					if(getMailTemplate().getTemplateFor().equals(PennantConstants.TEMPLATE_FOR_AE)) {
						aMailTemplate.setEmailSendTo(this.userIds.getValue());
					}
				} catch(WrongValueException we ) {
					wve.add(we);
				}
			}
			
			//Email Content
			try {
				String emailFormat = "H";
				if(this.emailFormat.getSelectedItem().getValue() != null){
					emailFormat = this.emailFormat.getSelectedItem().getValue().toString();

					if(emailFormat == PennantConstants.List_Select){
						emailFormat =null;
					}
				}
				aMailTemplate.setEmailFormat(emailFormat);
			}catch (WrongValueException we ) {
				wve.add(we);
			}

			//Email Subject
			try {
				aMailTemplate.setEmailSubject(this.emailSubject.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}

			//EMail Template Content
			try {

				if (PennantConstants.TEMPLATE_FORMAT_HTML.equals(aMailTemplate.getEmailFormat())) {
					if (StringUtils.trimToEmpty(this.htmlArtifact.getValue())
							.equals("")) {
						throw new WrongValueException(
								this.htmlArtifact,
								Labels.getLabel(
										"FIELD_NO_EMPTY",
										new String[] { Labels
												.getLabel("label_MailTemplateDialog_EMailContent.value") }));
					} else {
						aMailTemplate.setEmailContent(this.htmlArtifact.getValue()
								.getBytes(PennantConstants.DEFAULT_CHARSET));
					}
				}  else if (PennantConstants.TEMPLATE_FORMAT_PLAIN.equals(aMailTemplate.getEmailFormat())) {
					if (StringUtils.trimToEmpty(this.plainText.getValue()).equals("")) {
						throw new WrongValueException(
								this.plainText,
								Labels.getLabel(
										"FIELD_NO_EMPTY",
										new String[] { Labels
												.getLabel("label_MailTemplateDialog_EMailContent.value") }));
					} else {
						aMailTemplate.setEmailContent(this.plainText.getValue()
								.getBytes(PennantConstants.DEFAULT_CHARSET));
					}
				} 
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}	
		
		//Active
		try {
			aMailTemplate.setActive(this.active.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		showErrorDetails(wve, this.emailDetailsTab);
		
		aMailTemplate.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	
	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		if (!wve.isEmpty() && wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aMailTemplate
	 * @throws InterruptedException
	 */
	public void doShowDialog(MailTemplate aMailTemplate) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aMailTemplate == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aMailTemplate == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aMailTemplate = getMailTemplateService().getNewMailTemplate();
			
			setMailTemplate(aMailTemplate);
		} else {
			setMailTemplate(aMailTemplate);
		}

		if(enqModule){
			
			doReadOnly(true);
		// set Readonly mode accordingly if the object is new or not.
		}else if (aMailTemplate.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			//this.templateModule.focus();
		} else {
			this.templateDesc.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aMailTemplate);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_MailTemplateDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_templateCode = this.templateModule.getValue();
		this.oldVar_templateDesc = this.templateDesc.getValue();
		if(this.row_turnAroundTime.isVisible()) {
			this.oldVar_turnAroundTime = this.turnAroundTime.getValue();
		}
		if(this.row_templateRepeat.isVisible()){
			this.oldVar_templateRepeat = this.templateRepeat.isChecked();
		}
		this.oldVar_templateForSMS = this.templateForSMS.isChecked();
		if(this.templateForSMS.isChecked()){
			this.oldVar_SMSContent = this.smsContent.getValue();
		}
		
		this.oldVar_templateForEmail = this.templateForEmail.isChecked();		
		if(this.templateForEmail.isChecked()) {
			this.oldVar_EmailContent = this.htmlArtifact.getValue();
			this.oldVar_emailFormat = this.emailFormat.getSelectedIndex();
			this.oldVar_emailSubject = this.emailSubject.getValue();
			this.oldVar_sendToPrimary = this.emailSendToPrimary.isChecked();
			this.oldVar_sendToSecondary = this.emailSendToSecondary.isChecked();
			this.oldVar_sendToHelpdesk = this.emailSendToHelpdesk.isChecked();
			if(this.row_EmailSendTo.isVisible()) {
				this.oldVar_sendToUserIds = this.userIds.getValue();
			}
		}
		
		this.oldVar_active = this.active.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.templateModule.setValue(this.oldVar_templateCode);
		this.templateDesc.setValue(this.oldVar_templateDesc);
		if(this.row_turnAroundTime.isVisible()) {
			this.turnAroundTime.setValue(this.oldVar_turnAroundTime);
		}
		if(this.row_templateRepeat.isVisible()){
			this.templateRepeat.setChecked(this.oldVar_templateRepeat);
		}
		this.templateForSMS.setChecked(this.oldVar_templateForSMS);
		if(this.templateForSMS.isChecked()) {
			this.smsContent.setValue(this.oldVar_SMSContent);
		}
		
	  	this.templateForEmail.setChecked(this.oldVar_templateForEmail);
	  	if(this.templateForEmail.isChecked()) {
	  		this.htmlArtifact.setValue(this.oldVar_EmailContent);
			this.emailFormat.setSelectedIndex(this.oldVar_emailFormat);
			this.emailSubject.setValue(this.oldVar_emailSubject);
			this.emailSendToPrimary.setChecked(this.oldVar_sendToPrimary);
			this.emailSendToSecondary.setChecked(this.oldVar_sendToSecondary);
			this.emailSendToHelpdesk.setChecked(this.oldVar_sendToHelpdesk);
			if(this.row_EmailSendTo.isVisible()) {
				this.userIds.setValue(this.oldVar_sendToUserIds);
			}
		}
	  	
		this.active.setChecked(this.oldVar_active);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled() & !enqModule){
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
		if (this.oldVar_templateCode != this.templateModule.getValue()) {
			return true;
		}
		
		if (this.oldVar_templateDesc != this.templateDesc.getValue()) {
			return true;
		}
		
		if(this.row_turnAroundTime.isVisible() && this.oldVar_turnAroundTime != this.turnAroundTime.getValue()){
			return true;
		}
		
		if(this.row_templateRepeat.isVisible() && this.oldVar_templateRepeat != this.templateRepeat.isChecked()){
			return true;
		}
		
		if (this.oldVar_templateForSMS != this.templateForSMS.isChecked()) {
			return true;
		}
		
		
		if(this.templateForSMS.isChecked()) {
			if (this.oldVar_SMSContent != this.smsContent.getValue()) {
				return true;
			}	
		}
		
		if (this.oldVar_templateForEmail != this.templateForEmail.isChecked()) {
			return true;
		}
		
		if(this.templateForEmail.isChecked()) {
			if (this.oldVar_EmailContent != this.htmlArtifact.getValue()) {
				return true;
			}
			if (this.oldVar_emailFormat != this.emailFormat.getSelectedIndex()) {
				return true;
			}
			if (this.oldVar_emailSubject != this.emailSubject.getValue()) {
				return true;
			}
		}
		
		if (this.oldVar_active != this.active.isChecked()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		// TAT
		if(this.row_turnAroundTime.isVisible()){
			this.turnAroundTime.setConstraint(new IntValidator(3, Labels.getLabel("label_MailTemplateDialog_turnAroundTime.value")));
		}

		//Email Subject
		if (!this.emailSubject.isReadonly()){
			this.emailSubject.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX, Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel(
					"label_MailTemplateDialog_EmailSubject.value") })));
		}
		
	logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.turnAroundTime.setConstraint("");
		this.templateModule.setConstraint("");
		this.templateDesc.setConstraint("");
		this.emailFormat.setConstraint("");
		this.emailSubject.setConstraint("");
	logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}
	
	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.templateModule.setErrorMessage("");
		this.templateDesc.setErrorMessage("");
		this.emailFormat.setErrorMessage("");
		this.emailSubject.setErrorMessage("");
		this.turnAroundTime.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList(){
		final JdbcSearchObject<MailTemplate> soMailTemplate = getMailTemplateListCtrl().getSearchObj();
		getMailTemplateListCtrl().pagingMailTemplateList.setActivePage(0);
		getMailTemplateListCtrl().getPagedListWrapper().setSearchObject(soMailTemplate);
		if(getMailTemplateListCtrl().listBoxMailTemplate!=null){
			getMailTemplateListCtrl().listBoxMailTemplate.getListModel();
		}
	} 


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a MailTemplate object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final MailTemplate aMailTemplate = new MailTemplate();
		BeanUtils.copyProperties(getMailTemplate(), aMailTemplate);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aMailTemplate.getTemplateCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aMailTemplate.getRecordType()).equals("")){
				aMailTemplate.setVersion(aMailTemplate.getVersion()+1);
				aMailTemplate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aMailTemplate.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aMailTemplate,tranType)){
					refreshList();
					closeDialog(this.window_MailTemplateDialog, "MailTemplate"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new MailTemplate object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final MailTemplate aMailTemplate = getMailTemplateService().getNewMailTemplate();
			aMailTemplate.setActive(true); // init
		aMailTemplate.setNewRecord(true);
		setMailTemplate(aMailTemplate);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.templateModule.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getMailTemplate().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}
		doReadOnly(false);
		if (isWorkFlowEnabled()){
			
			if (this.mailTemplate.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		
		if (isReadOnly("MailTemplateDialog_templateContent")) {
			this.btnSimulate.setVisible(false);
			this.divHtmlArtifact.setVisible(true);
			this.htmlArtifact.setVisible(false);
			this.space_templateForSMS.setSclass("");
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(mailTemplate.getRecordType())))) {
			readOnly=true;
		}

		this.templateDesc.setReadonly(isReadOnly("MailTemplateDialog_templateDesc"));
		this.turnAroundTime.setReadonly(isReadOnly("MailTemplateDialog_turnAroundTime"));
		this.templateRepeat.setDisabled(isReadOnly("MailTemplateDialog_templateRepeat"));
	
		//SMS
		this.templateForSMS.setDisabled(isReadOnly("MailTemplateDialog_templateForSMS"));
		this.smsContent.setDisabled(isReadOnly("MailTemplateDialog_templateContent"));
		
		// EMAIL
		this.templateForEmail.setDisabled(isReadOnly("MailTemplateDialog_templateForEmail"));
		
		//this.htmlArtifact.setsetReadonly(isReadOnly("MailTemplateDialog_templateContent")); // FIXME
		
		
		this.emailFormat.setDisabled(isReadOnly("MailTemplateDialog_emailFormat"));		
		this.emailSubject.setDisabled(isReadOnly("MailTemplateDialog_emailSubject"));		
		this.active.setDisabled(isReadOnly("MailTemplateDialog_active"));

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		
		this.templateModule.setValue("");
		this.templateDesc.setValue("");
		this.templateRepeat.setChecked(false);
	 	this.templateForSMS.setChecked(false);
	 	this.templateForEmail.setChecked(false);
	 	this.emailFormat.setSelectedIndex(0);
		this.emailSubject.setValue("");
		this.active.setChecked(false);
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");
		final MailTemplate aMailTemplate = new MailTemplate();
		BeanUtils.copyProperties(getMailTemplate(), aMailTemplate);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aMailTemplate.getRecordType())) {
			doSetValidation();
			// fill the MailTemplate object with the components data
			doWriteComponentsToBean(aMailTemplate);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aMailTemplate.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aMailTemplate.getRecordType()).equals("")){
				aMailTemplate.setVersion(aMailTemplate.getVersion()+1);
				if(isNew){
					aMailTemplate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aMailTemplate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMailTemplate.setNewRecord(true);
				}
			}
		}else{
			aMailTemplate.setVersion(aMailTemplate.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aMailTemplate,tranType)){
				doWriteBeanToComponents(aMailTemplate);
				refreshList();
				closeDialog(this.window_MailTemplateDialog, "MailTemplate");
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	
	private boolean doProcess(MailTemplate aMailTemplate,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		String nextRoleCode="";
		
		aMailTemplate.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aMailTemplate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMailTemplate.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String
			aMailTemplate.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aMailTemplate.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aMailTemplate);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aMailTemplate))) {
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

			aMailTemplate.setTaskId(taskId);
			aMailTemplate.setNextTaskId(nextTaskId);
			aMailTemplate.setRoleCode(getRole());
			aMailTemplate.setNextRoleCode(nextRoleCode);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aMailTemplate);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(aMailTemplate,tranType,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(aMailTemplate, PennantConstants.TRAN_WF, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(aMailTemplate, tranType,null);
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
	
	private boolean doSaveProcess(MailTemplate aMailTemplate, String tranType,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		AuditHeader auditHeader =  getAuditHeader(aMailTemplate, tranType);
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getMailTemplateService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getMailTemplateService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getMailTemplateService().doApprove(auditHeader);

						if(tranType.equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getMailTemplateService().doReject(auditHeader);
						if(tranType.equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_MailTemplateDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_MailTemplateDialog, auditHeader);
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
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(MailTemplate aMailTemplate, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMailTemplate.getBefImage(), aMailTemplate);   
		return new AuditHeader(aMailTemplate.getTemplateCode(),null,null,null,auditDetail,aMailTemplate.getUserDetails(),getOverideMap());
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
			ErrorControl.showErrorControl(this.window_MailTemplateDialog, auditHeader);
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
		
		// call the zul-file with the parameters packed in a map
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

	// Get the notes details for the Module
	
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("MailTemplate");
		notes.setReference(getMailTemplate().getTemplateCode());
		notes.setVersion(getMailTemplate().getVersion());
		return notes;
	}
	
	
	/*
	 * onselect Event for Content Type
	 */
	public void onChange$emailFormat(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		String emailTypeVal = (String) this.emailFormat.getSelectedItem().getValue();
		this.row_EmailSubject.setVisible(true);
		this.row_EmailSendTo.setVisible(true);
		this.row_EmailContent.setVisible(true);
		doSetArtifact(emailTypeVal);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * OnSelect event for tempalte task 
	 * @param event
	 */
	public void onSelect$templateTask(Event event) {/*
		logger.debug("Entering" + event.toString());
		
		String selectedMod = this.templateModule.getSelectedItem().getValue().toString();

		if(!selectedMod.equals("") && !selectedMod.equals(PennantConstants.List_Select)) {
			if(!selectedTask.equals("")) {
				if(selectedMod.equals(PennantConstants.TEMPLATE_MOD_ORG) && selectedTask.equals(PennantConstants.TEMPLATE_TSK_REQ)) {
					getMailTemplate().setId(200);
					doFillTemplateFields(1);
				} else if(selectedMod.equals(PennantConstants.TEMPLATE_MOD_ORG) && selectedTask.equals(PennantConstants.TEMPLATE_TSK_RES)) {
					getMailTemplate().setId(210);
					doFillTemplateFields(2);
				} else if(selectedMod.equals(PennantConstants.TEMPLATE_MOD_ORG) && selectedTask.equals(PennantConstants.TEMPLATE_TSK_CNF)) {
					getMailTemplate().setId(220);
					doFillTemplateFields(3);
				} else if(selectedMod.equals(PennantConstants.TEMPLATE_MOD_DDA) && selectedTask.equals(PennantConstants.TEMPLATE_TSK_REQ)) {
					getMailTemplate().setId(300);
					doFillTemplateFields(4);
				} else if(selectedMod.equals(PennantConstants.TEMPLATE_MOD_DDA) && selectedTask.equals(PennantConstants.TEMPLATE_TSK_CNF)) {
					getMailTemplate().setId(310);
					doFillTemplateFields(5);
				} 
			}
		}
		
		
		logger.debug("Leaving" + event.toString());
	*/}
	
	/** 
	 * OnCheck event for template For SMS
	 * @param event
	 */
	public void onCheck$templateForSMS(Event event) {
		loadSMSFields();
	}
	
	private void loadSMSFields() {
		this.row_SMSContent.setVisible(false);
		if(this.templateForSMS.isChecked()) {
			Clients.clearWrongValue(this.templateForSMS);
			this.row_SMSContent.setVisible(true);
		}
	}
	
	/** 
	 * OnCheck event for template For Email
	 * @param event
	 */
	public void onCheck$templateForEmail(Event event) {
		loadEmailFields();
	}
	
	private void loadEmailFields() {
		this.emailDetailsTab.setDisabled(true);
		this.row_EmailContent.setVisible(false);
		this.row_EmailSubject.setVisible(false);
		this.btnSimulate.setVisible(false);
		this.templateData.setVisible(false);
		if(this.templateForEmail.isChecked()) {
			Clients.clearWrongValue(this.templateForSMS);
			this.emailDetailsTab.setDisabled(false);
			this.emailDetailsTab.setSelected(true);
			this.row_EmailContent.setVisible(true);
			this.row_EmailSubject.setVisible(true);
			this.btnSimulate.setVisible(true);
			if(getMailTemplate().getTemplateFor().equals(PennantConstants.TEMPLATE_FOR_AE)) {
				this.row_EmailSendTo.setVisible(true);
				this.hboxPSH.setVisible(false);
				this.hboxUsers.setVisible(true);
			}
			
			if(!this.templateForEmail.isDisabled()){
				this.templateData.setVisible(true);
			}
			doFillTemplateFields(getMailTemplate().getModule()); 
		}
	}
	
	/*
	 * changing the artifact based on selection of Content Type
	 * 
	 * @ param content type
	 */
	private void doSetArtifact(String type) {
		logger.debug("Entering");

		doClearArtifact();

		if (null == type || PennantConstants.List_Select.equals(type)) {
			this.row_EmailSubject.setVisible(false);
			return;
		}

		this.row_EmailContent.setVisible(true);
		this.row_EmailSubject.setVisible(true);
		if (type.equals(PennantConstants.TEMPLATE_FORMAT_HTML)) {
			if(isReadOnly("MailTemplateDialog_templateContent")) {
				this.btnSimulate.setVisible(false);
				this.divHtmlArtifact.setVisible(true);
				this.htmlArtifact.setVisible(false);
			}else {
				this.btnSimulate.setVisible(true);
				this.htmlArtifact.setVisible(true);
				this.templateData.setVisible(true);
				this.plainText.setVisible(false);
			}
		} else if (type.equals(PennantConstants.TEMPLATE_FORMAT_PLAIN)) {
			this.plainText.setVisible(true);
			this.htmlArtifact.setVisible(false);
			this.templateData.setVisible(false);
		} 

		logger.debug("Leaving ");
	}
	
	private void doClearArtifact() {
		this.row_EmailContent.setVisible(false);
		this.htmlArtifact.setVisible(false);
		this.templateData.setVisible(false);
		this.htmlArtifact.setValue("");
		this.plainText.setVisible(false);
		this.plainText.setValue("");
	}
	
	/**
	 * To get template fields from MessageDetail table and fill the template fields listbox
	 * @param msgKey (long)
	 */
	private void doFillTemplateFields(String module) {
		logger.debug("Entering");
		this.templateData.getItems().clear();
		List<TemplateFields> templateFieldsList = new ArrayList<TemplateFields>();
		JdbcSearchObject<TemplateFields> searchObj = new JdbcSearchObject<TemplateFields>(TemplateFields.class);
		searchObj.addTabelName("TemplateFields");
		searchObj.addFilterEqual("Module", module);
		templateFieldsList = this.pagedListService.getBySearchObject(searchObj);
		String lcLabel = "";
		if(templateFieldsList.size()==0) {
			this.templateData.setVisible(false);
			return;
		}
		for (int i = 0; i < templateFieldsList.size(); i++) {
			Listitem item = new Listitem();
			String value = templateFieldsList.get(i).getField().trim();
			if(Character.isLowerCase(value.substring(1, 2).charAt(0))){
				StringBuilder myBuilder = new StringBuilder(value);
				char lower = value.substring(0, 1).toLowerCase().charAt(0);
				myBuilder.setCharAt(0, lower);
				value = myBuilder.toString();
			}
			String defaultTo=" ";
			lcLabel = "${vo."+value+"!"+defaultTo+"}";
			if(templateFieldsList.get(i).getFieldFormat().equals("D")) {
				lcLabel = "${vo."+value+"?date}";
			}else if(templateFieldsList.get(i).getFieldFormat().equals("AM2") || templateFieldsList.get(i).getFieldFormat().equals("AM3")) {
				lcLabel = "${vo."+value+"?string.currency}";
			}else if(templateFieldsList.get(i).getFieldFormat().equals("T")) {
				lcLabel = "${vo."+value+"?datetime}";
			}
			Listcell lc = new Listcell(lcLabel);
			filedValues.put(lcLabel, templateFieldsList.get(i).getField().trim());
			filedDesc.put(templateFieldsList.get(i).getField().trim(), templateFieldsList.get(i).getFieldDesc()+":"+templateFieldsList.get(i).getFieldFormat());
			lc.setParent(item);
			lc.setVisible(false);
			lc = new Listcell(templateFieldsList.get(i).getFieldDesc());
			lc.setParent(item);
			this.templateData.appendChild(item);
		}
		logger.debug("Leaving");
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

	public MailTemplate getMailTemplate() {
		return this.mailTemplate;
	}

	public void setMailTemplate(MailTemplate mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public MailTemplateService getMailTemplateService() {
		return this.mailTemplateService;
	}

	public void setMailTemplateListCtrl(MailTemplateListCtrl mailTemplateListCtrl) {
		this.mailTemplateListCtrl = mailTemplateListCtrl;
	}

	public MailTemplateListCtrl getMailTemplateListCtrl() {
		return this.mailTemplateListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public MailTemplate getPrvMailTemplate() {
		return prvMailTemplate;
	}
	
	public void onClick$btnSimulate(Event event) throws Exception {
		logger.debug(event.toString());		
		Clients.clearWrongValue(this.htmlArtifact);
		try {
			StringTemplateLoader loader = new StringTemplateLoader();
			
			String content = this.htmlArtifact.getValue();
			if (StringUtils.trimToEmpty(content)
					.equals("")) {
				throw new WrongValueException(
						this.htmlArtifact,
						Labels.getLabel(
								"FIELD_NO_EMPTY",
								new String[] { Labels
										.getLabel("label_MailTemplateDialog_EMailContent.value") }));
			}
			loader.putTemplate("Template", content);
			
			Configuration configuration = new Configuration();
			configuration.setTemplateLoader(loader);
			Template template = configuration.getTemplate("Template");
			
			Map<String, Object> model = new HashMap<String, Object>();
			if(getMailTemplate().getModule().equals("FIN")) {
				FinanceMain fm = new FinanceMain();
				fm.setMaturityDate((Date) SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE"));
				fm.setFinStartDate((Date) SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE"));
				model.put("vo", fm);
			}else if(getMailTemplate().getModule().equals("RPY")) {
				FinanceRepayments fm = new FinanceRepayments();
				fm.setFinPostDate((Date) SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE"));
				fm.setFinSchdDate((Date) SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE"));
				model.put("vo", fm);
			}else if(getMailTemplate().getModule().equals("DIS")) {
				DisbursementDetail fm = new DisbursementDetail();
				fm.setDisbDate((Date) SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE"));
				fm.setDisbAmount(BigDecimal.ZERO);
				model.put("vo", fm);
			}
			
			FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (TemplateException e) {
			
			throw new WrongValueException(this.htmlArtifact,Labels.getLabel("INVALID_TEMPLATE_CONTENT",new String[] {e.getMessage()}));
		
		} catch (IOException e) {

			throw new WrongValueException(this.htmlArtifact,Labels.getLabel("INVALID_TEMPLATE_CONTENT",new String[] {e.getMessage()}));
		
		}
		createSimulator(this.htmlArtifact.getValue());
		logger.debug("Leaving");
	}
	
	private void createSimulator(String mailContent) throws InterruptedException{
		final HashMap<String, String> fieldsMap = new HashMap<String, String>();
		for (String field : filedValues.keySet()) {
			if(mailContent.contains(field)){				
				fieldsMap.put(filedValues.get(field), filedDesc.get(filedValues.get(field)));
			}
		}
		
		final HashMap<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("mailTemplateDialogCtrl", this);
		argsMap.put("fieldsMap", fieldsMap);		
		argsMap.put("mailContent", mailContent);
		argsMap.put("module", getMailTemplate().getModule());
	
		try {
			Executions.createComponents("/WEB-INF/pages/Mail/MailTemplate/TemplatePreview.zul", null, argsMap);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
	}
	
}
