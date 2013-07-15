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

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;

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
	protected Textbox 		remarks;					// autoWired
	protected Radiogroup 	remarkType; 		 		// autoWired
	protected Radiogroup 	alignType; 		 			// autoWired
	protected Hlayout 		hlayout_cbType;				// autoWired
	
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
	private List<ValueLabel> alignTypeList = PennantStaticListUtil.getAlignType();
	
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
	public void onCreate$window_notesDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

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

		this.mainControl =  args.get("control");
		
		getBorderLayoutHeight();
		listboxNotes.setHeight(borderLayoutHeight-150+"px");
		
		setRemarkTypeList();
		setAlignTypeList();
		doShowDialog(getNotes());
		logger.debug("Leaving" +event.toString());
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
		doClose();
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
		
		if (this.remarks.getValue()==null || this.remarks.getValue().trim().length()<=0){
			Component comp =this.remarks;
			wve.add(new WrongValueException(comp, Labels.getLabel("Notes_NotEmpty")));
		}else{
			aNotes.setRemarks(this.remarks.getValue().trim());
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
			getList();
			this.window_notesDialog.doModal(); // open the dialog in modal mode
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
			getNotesService().saveOrUpdate(this.newNotes);
			getList();
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
		List<Notes> appList = getNotesService().getNotesList(this.notes);
		
		Listitem item = null;
		Listcell lc = null;
		String alignSide = "left";
		for(int i=0; i<appList.size(); i++){
			
			Notes note =(Notes) appList.get(i);
			if(note != null) {

				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
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
