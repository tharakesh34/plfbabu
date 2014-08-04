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
 * FileName    		:  NotesCtl.java														*                           
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


package com.pennant.webui.util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.PTCKeditor;

public class NotesCtrl extends GFCBaseListCtrl<Notes> implements Serializable{
	
	private static final long serialVersionUID = -1351367303946249042L;
	private final static Logger logger = Logger.getLogger(NotesCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_notesDialog; 		// autoWired
	protected Tabbox 		tabbox;						// autoWired
	protected Listbox 		listboxNotes;				// autoWired
	protected Listhead 		listheadNotes;				// autoWired
	protected PTCKeditor	remarks;					// autoWired
	protected Textbox		remarksText;					// autoWired
	protected Radiogroup 	remarkType; 		 		// autoWired
	protected Label label_NotesDialog_AlignType;
	protected Radiogroup 	alignType; 		 			// autoWired
	protected Hlayout 		hlayout_cbType;				// autoWired
	protected Div 			div_toolbar;				// autoWired
	protected Label 		label_title;				// autoWired
	protected Separator     separator1;			        // autoWired
	protected Separator     separator2;			        // autoWired
	
	// not auto wired variables
	private Notes 			notes; 						// overHanded per parameter
	private NotesCtrl 		notesCtrl;
	
	// Button controller for the CRUD buttons
	protected Button 		btnSave; 					// autoWire
	protected Button 		btnCancel; 					// autoWire
	protected Button 		btnClose; 					// autoWire

	private transient NotesService notesService;
	// not auto wired variables
	private Notes 			newNotes; 					// overHanded per parameter
	private boolean 		notes_Entered=false;
	private Object 			mainControl=null;
	private transient boolean validationOn;	
	private List<ValueLabel> remarkTypeList = PennantStaticListUtil.getRemarkType();
	private List<ValueLabel> recommandTypeList = PennantStaticListUtil.getRecommandType();
	private List<ValueLabel> alignTypeList = PennantStaticListUtil.getAlignType();
	private List<Notes> notesList;
	private boolean isFinanceNotes = false;
	private boolean isRecommendMand = false;
	private String roleCode = "";
	private Tabpanel tabpanel = null;
	private boolean  isEnquiry = false;
	
	public NotesCtrl(){
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected bankDetails object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_notesDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		if(event.getTarget().getParent() != null){
			tabpanel = (Tabpanel) event.getTarget().getParent();
		}

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
			
		// READ OVERHANDED parameters !
		if (args.containsKey("notes")) {
			this.notes = (Notes) args.get("notes");
		} else {
			setNotes(null);
		}
		
		if (args.containsKey("isFinanceNotes")) {
			this.isFinanceNotes = (Boolean) args.get("isFinanceNotes");
		}
		if (args.containsKey("isRecommendMand")) {
			this.isRecommendMand = (Boolean) args.get("isRecommendMand");
		}
		
		if (args.containsKey("userRole")) {
			this.roleCode = (String) args.get("userRole");
		}
		
		if (args.containsKey("enquiry")) {
			isEnquiry = true;
		}
		
		if (args.containsKey("notesList")) {
			this.notesList = (List<Notes>) args.get("notesList");
		}
		
		this.mainControl =  args.get("control");
		this.remarks.setCustomConfigurationsPath(PTCKeditor.SIMPLE_LIST);
		
		getBorderLayoutHeight();
		listboxNotes.setHeight(borderLayoutHeight-310+"px");
		
		doShowDialog(getNotes());
		doCheckEnquiry();	
		logger.debug("Leaving" +event.toString());	
	}

	private void doCheckEnquiry() {
		logger.debug("Entering");
		if (isEnquiry) {
			this.btnSave.setVisible(false);
			this.btnClose.setVisible(false);
			this.remarks.setVisible(false);
			this.remarksText.setVisible(false);
			this.hlayout_cbType.setVisible(false);
			this.div_toolbar.setVisible(false);
			this.separator1.setVisible(false);
			this.separator2.setVisible(false);
			listboxNotes.setHeight(borderLayoutHeight-175+"px");
		}
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
		logger.debug("Entering");
		this.window_notesDialog.setVisible(true);
		this.btnSave.setVisible(true);
		logger.debug("Leaving");
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
		logger.debug("Entering" +event.toString());
		doSave();
		if(mainControl!=null && notes_Entered){
			try {
				String methodString ="setNotes_entered";
				@SuppressWarnings("rawtypes")
				Class[] stringType = {Class.forName("java.lang.String")};
				Object[] stringParm = {""};
				if (notes_Entered){
					stringParm[0]="Y";
				}
				
				if (mainControl.getClass().getMethod(methodString,stringType)!=null){
					mainControl.getClass().getMethod(methodString,stringType).invoke(mainControl, stringParm);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(!this.isFinanceNotes){
			doClose();
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		try {
			doClose();
		} catch (final Exception e) {
			// close anyway
			this.window_notesDialog.onClose();
		}
		logger.debug("Leaving" +event.toString());
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
	private void doClose() throws InterruptedException {
		logger.debug("Entering");

		this.window_notesDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		//window_notesDialog.detach();
		logger.debug("Leaving");
	}
		
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aNotes
	 */
	
	public void doWriteComponentsToBean() {
		logger.debug("Entering");
		Notes aNotes = new Notes();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		if (isFinanceNotes) {
			if (this.remarks.getValue()==null || this.remarks.getValue().trim().length()<=0){
				wve.add(new WrongValueException(this.remarks, Labels.getLabel("Notes_NotEmpty")));
			}else{
				aNotes.setRemarks(this.remarks.getValue().trim());
			}
		}else{
			if (this.remarksText.getValue()==null || this.remarksText.getValue().trim().length()<=0){
				wve.add(new WrongValueException(this.remarksText, Labels.getLabel("Notes_NotEmpty")));
			}else{
				aNotes.setRemarks(this.remarksText.getValue().trim());
			}
		}
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		try {
			aNotes.setRemarkType(this.remarkType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aNotes.setAlignType(this.alignType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		UserWorkspace workspace = getUserWorkspace();
		
		aNotes.setModuleName(this.notes.getModuleName());
		aNotes.setReference(this.notes.getReference());
		aNotes.setVersion(this.notes.getVersion());
		aNotes.setInputBy(workspace.getLoginUserDetails().getLoginUsrID());
		aNotes.setInputDate(new Date());
		aNotes.setRoleCode(getNotes().getRoleCode());
		this.newNotes =aNotes;
		logger.debug("Leaving");
	} 
	
	public void doShowDialog(Notes aNotes) throws Exception {
		logger.debug("Entering");
		// if aNotes == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aNotes == null) {
			
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aNotes = getNotesService().getNewNotes();
			setNotes(aNotes);
		} else {
			setNotes(aNotes);
		}

		try {
			// fill the components with the data
			setAlignTypeList();
			this.listboxNotes.getItems().clear();
			
			if(this.isFinanceNotes){
				
				setRecommendTypeList();
				getRecommendations();
				this.tabpanel.appendChild(this.window_notesDialog);
				this.window_notesDialog.setWidth("100%");
				if (!isEnquiry) {
					this.window_notesDialog.setHeight("98%");
				}
				this.btnClose.setVisible(false);
				this.label_title.setValue(Labels.getLabel("MemoDetails"));
				
				this.alignType.setVisible(false);
				this.label_NotesDialog_AlignType.setVisible(false);
				this.alignType.setSelectedIndex(0);
				
				this.remarksText.setVisible(false);
				this.remarks.setVisible(true);
			}else{
				this.remarksText.setVisible(true);
				this.remarks.setVisible(false);
				setRemarkTypeList();
				getList();
				this.window_notesDialog.doModal(); // open the dialog in modal mode
			}
			
		} catch (final Exception e) {
			Messagebox.show(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");
		// fill the BankDetails object with the components data
		doWriteComponentsToBean();
		// save it to database
		try {
			if(isFinanceNotes){
				if(StringUtils.trimToEmpty(this.newNotes.getReference()).equals("")){
					String finReferece = "";
					try {
						Object object = this.mainControl.getClass().getMethod("getFinanceMain").invoke(this.mainControl);
						if (object != null) {
							FinanceMain main = (FinanceMain) object;
							finReferece = main.getFinReference();
						}
					} catch (Exception e) {
						logger.debug(e);
					}
					try {
						Object object = this.mainControl.getClass().getMethod("getReference").invoke(this.mainControl);
						if (object != null) {
							finReferece=  object.toString();
						}
					} catch (Exception e) {
						logger.debug(e);
					}
					if(StringUtils.trimToEmpty(finReferece).equals("")){
						PTMessageUtils.showErrorMessage("Reference Must be Entered");
						return;
					}
					this.newNotes.setReference(finReferece);
					this.notes.setReference(finReferece);
				}
			}
			
			if(this.notesList == null){
				getNotesService().saveOrUpdate(this.newNotes); // TODO 
			} else {
				this.newNotes.setUsrLogin(getUserWorkspace().getUserDetails().getUsername());
				
				SecurityUser usr = getUserWorkspace().getUserDetails().getSecurityUser();
				this.newNotes.setUsrName(usr.getUsrFName()+" "+usr.getUsrMName()+" "+usr.getUsrLName());
				this.notesList.add(this.newNotes);
			}
			setAlignTypeList();
			this.listboxNotes.getItems().clear();
			
			if(this.isFinanceNotes){
				
				//setRecommendTypeList();
				getRecommendations();
				this.tabpanel.appendChild(this.window_notesDialog);
				this.window_notesDialog.setTitle("");
				this.window_notesDialog.setWidth("100%");
				this.window_notesDialog.setHeight("90%");
				//this.div_toolbar.setVisible(false);
				this.btnClose.setVisible(false);
				this.label_title.setValue(Labels.getLabel("MemoDetails"));
				
				this.alignType.setVisible(false);
				this.alignType.setSelectedIndex(0);
			}else{
				setRemarkTypeList();
				getList();
			}
			
			this.remarks.setValue("");
			this.remarksText.setValue("");
			
			notes_Entered=true;
		} catch (final DataAccessException e) {
			throw e;
		}
		logger.debug("Leaving");
	}
	
	public void setRemarkTypeList() {
		Radio radio;
		for (int i = 0; i < remarkTypeList.size(); i++) {
			radio = new Radio();
			radio.setValue(remarkTypeList.get(i).getValue());
			radio.setLabel(remarkTypeList.get(i).getLabel());
			remarkType.appendChild(radio);
		}
		this.remarkType.setSelectedIndex(0);
	}
	
	public void setRecommendTypeList() {
		Radio radio;
		for (int i = 0; i < recommandTypeList.size(); i++) {
			radio = new Radio();
			radio.setValue(recommandTypeList.get(i).getValue());
			radio.setLabel(recommandTypeList.get(i).getLabel());
			remarkType.appendChild(radio);
		}
		this.remarkType.setSelectedIndex(0);
	}
	
	public void setAlignTypeList() {
		Radio radio;
		for (int i = 0; i < alignTypeList.size(); i++) {
			radio = new Radio();
			radio.setValue(alignTypeList.get(i).getValue());
			radio.setLabel(alignTypeList.get(i).getLabel());
			alignType.appendChild(radio);
		}
		this.alignType.setSelectedIndex(0);
	}
	
	public void getList() throws Exception{
		logger.debug("Entering");
		
		//Retrieve Notes List By Module Reference
		List<Notes> appList = getNotesService().getNotesList(this.notes, true);
		
		Listitem item = null;
		Listcell lc = null;
		String alignSide = "left";
		for(int i=0; i<appList.size(); i++){
			
			Notes note =(Notes) appList.get(i);
			if(note != null) {
				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
				lc.setSpan(4);	
				Html html = new Html();
				
				if("R".equals(note.getAlignType())){
					if("right".equals(alignSide)){
						alignSide = "left";
					}else{
						alignSide = "right";
					}
				}
				
				String usrAlign = "";
				if("right".equals(alignSide)){
					usrAlign = "left";
				}else{
					usrAlign = "right";
				}
				
				String content = "<p class='triangle-right "+alignSide+"'> <font style='font-weight:bold;'> "  +note.getRemarks()+" </font> <br>  ";
				String date = DateUtility.formatUtilDate(note.getInputDate(), PennantConstants.dateTimeAMPMFormat);
				if("I".equals(note.getRemarkType())){
					content = content +  "<font style='color:#FF0000;float:"+usrAlign+";'>"+note.getUsrLogin().toLowerCase()+" : "+date+"</font></p>";
				}else{
					content = content +  "<font style='color:white;float:"+usrAlign+";'>"+note.getUsrLogin().toLowerCase()+" : "+date+"</font></p>";
				}
				html.setContent(content);
				lc.appendChild(html);
				lc.setParent(item);
				listboxNotes.appendChild(item);
				
			}  
		}
		logger.debug("Leaving");
	}
	
	public void getRecommendations() throws Exception{
		logger.debug("Entering");
		
		//Retrieve Notes List By Module Reference
		List<Notes> appList = getNotesService().getNotesList(this.notes, false);
		
		if(this.notesList !=null && this.notesList.size() > 0) {
		  appList.addAll(notesList);
		}
		
		Listitem item = null;
		Listcell lc = null;
		this.listboxNotes.setSizedByContent(true);
		for(int i=0; i<appList.size(); i++){
			
			Notes note =(Notes) appList.get(i);
			if(note != null) {
				
				if(i == 0 && isRecommendMand && StringUtils.trimToEmpty(roleCode).equalsIgnoreCase(note.getRoleCode())){
					try {
						if (mainControl.getClass().getMethod("setRecommendEntered",Boolean.class)!=null){
							mainControl.getClass().getMethod("setRecommendEntered",Boolean.class).invoke(mainControl, true);
						}
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}

				item = new Listitem();
				item.setStyle("vertical-align:top");
				//1
				lc = new Listcell(note.getUsrName());
				lc.setStyle("cursor:default;");
				lc.setParent(item);
				//2
				lc = new Listcell();
				if("R".equals(note.getRemarkType())){
					lc.setLabel("Recommend");
					lc.setStyle("color:orange;cursor:default;");
				}else{
					lc.setLabel("Comment");
					lc.setStyle("color:green;cursor:default;");
				}
				lc.setParent(item);
				//3
				lc = new Listcell(DateUtility.formatUtilDate(note.getInputDate(), PennantConstants.dateTimeAMPMFormat));
				lc.setStyle("cursor:default;");
				lc.setParent(item);
				
				//4
				lc = new Listcell();
				Html html=new Html();
				html.setContent(note.getRemarks());
				lc.appendChild(html);
				lc.setStyle("cursor:default;");
				/*Html html = new Html();
				String content = "<p class='triangle-right left'> <font style='font-weight:bold;'> "  +note.getRemarks()+" </font> <br>  ";
				html.setContent(content);
				lc.appendChild(html);*/
				lc.setParent(item);
				
				listboxNotes.appendChild(item);
				
			}  
		}
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public Notes getNotes() {
		return notes;
	}
	public void setNotes(Notes notes) {
		this.notes = notes;
	}

	public NotesService getNotesService() {
		return notesService;
	}                                                                               
	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

	public NotesCtrl getNotesCtrl() {
		return notesCtrl;
	}
	public void setNotesCtrl(NotesCtrl notesCtrl) {
		this.notesCtrl = notesCtrl;
	}

	public Object getMainControl() {
		return mainControl;
	}
	public void setMainControl(Object mainControl) {
		this.mainControl = mainControl;
	}
	
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

}
