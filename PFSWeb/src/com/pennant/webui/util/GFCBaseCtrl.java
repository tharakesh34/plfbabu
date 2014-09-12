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
 * FileName    		:  GFCBaseCtl.java														*                           
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

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.QueryBuilder;
import com.pennant.UserWorkspace;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.UserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Workflow;


/**
 * Base controller for creating the controllers of the zul files with the spring
 * framework.
 * 
 */
@SuppressWarnings("rawtypes")
abstract public class GFCBaseCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = -1171206258809472640L;
	private final static Logger logger = Logger.getLogger(GFCBaseCtrl.class);

	protected transient Map<String, Object> args;
	private transient UserService userService;

	private transient Workflow workFlow = null;
	private transient String role = "";
	private transient boolean firstTask=false;		
	// Variables that are required for workflow
	private boolean workFlowEnabled = false;
	private long workFlowId= Long.MIN_VALUE;

	private final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
	private West menuWest;
	private Groupbox groupboxMenu;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private String reaOnlyStyle="#F2F2F2";
	private String generalStyle="#FFFFFF";

	private String nextRoleCode="";
	private String taskId = "";
	private String nextTaskId= "";
	private String auditingReq="";
	private String operationRefs="";
	private boolean validation=true;

	private int	                       listRows	          = 0;
	private int	                       gridRows	          = 0;
	public int	                       borderLayoutHeight	= 0;


	/**
	 * Get the params map that are overhanded at creation time. <br>
	 * Reading the params that are binded to the createEvent.<br>
	 * 
	 * @param event
	 * @return params map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCreationArgsMap(Event event) {
		CreateEvent ce = (CreateEvent) ((ForwardEvent) event).getOrigin();
		return (Map<String, Object>) ce.getArg(); //Upgraded to ZK-6.5.1.1 Added casting to Map 	
	}

	@SuppressWarnings("unchecked")
	public void doOnCreateCommon(Window w, Event fe) throws Exception {
		CreateEvent ce = (CreateEvent) ((ForwardEvent) fe).getOrigin();
		args = (Map<String, Object>) ce.getArg(); //Upgraded to ZK-6.5.1.1 Added casting to Map
	}

	private transient UserWorkspace userWorkspace;
	@Override
	public void onEvent(Event evt) throws Exception {
		final Object controller = getController();
		final Method mtd = ComponentsCtrl.getEventMethod(controller.getClass(), evt.getName());

		if (mtd != null) {
			isAllowed(mtd);
		}
		super.onEvent(evt);
	}

	/**
	 * With this method we get the @Secured Annotation for a method.<br>
	 * Captured the method call and check if it's allowed. <br>
	 * 
	 * @param mtd
	 */
	private void isAllowed(Method mtd) {
		Annotation[] annotations = mtd.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Secured) {
				Secured secured = (Secured) annotation;
				for (String rightName : secured.value()) {
					if (!userWorkspace.isAllowed(rightName)) {
						throw new SecurityException("Call of this method is not allowed! Missing right: \n\n" + "needed RightName: " + rightName + "\n\n" + "Method: " + mtd);
					}
				}
				return;
			}
		}
	}

	final protected UserWorkspace getUserWorkspace() {
		return userWorkspace;
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userWorkspace.setUserService(userService);
		this.userService = userService;
	}

	public Workflow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(Workflow workFlow) {
		this.workFlow = workFlow;
	}


	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isFirstTask() {
		return firstTask;
	}

	public void setFirstTask(boolean firstTask) {
		this.firstTask = firstTask;
	}


	public void doLoadWorkFlow(boolean workFlowEnabled, long workFlowId,String nextTaskID) throws FileNotFoundException,XMLStreamException {

		this.workFlowEnabled=workFlowEnabled;
		this.workFlowId=workFlowId;

		if (this.workFlowEnabled) {
			setWorkFlow(new Workflow(this.workFlowId));

			// set the Role

			if (StringUtils.trimToEmpty(nextTaskID).equals("")) {
				this.role = this.workFlow.firstTask.owner;
			} else {
				String[] nextTasks = nextTaskID.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					String currentRole="";

					for (int i = 0; i < nextTasks.length; i++) {
						currentRole =this.workFlow.getTaskOwner(nextTasks[i]);
						if(userWorkspace.isRoleContains(currentRole )){
							this.role=currentRole;
							break;
						}
					}


					//this.role = this.workFlow.getTaskOwner(nextTasks[0]);
				}else{
					this.role = this.workFlow.getTaskOwner(nextTaskID);
				}
			}

			if (this.role.equals(this.workFlow.firstTask.owner) && StringUtils.trimToEmpty(nextTaskID).equals("") ) {
				this.firstTask=true;
			}else{
				this.firstTask=false;
			}
		}
	}
	
	public String getCurrentTab(){
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		return tabbox.getSelectedTab().getId().toString();
	}

	/**
	 * @return the workFlowEnabled
	 */
	public boolean isWorkFlowEnabled() {
		return workFlowEnabled;
	}

	/**
	 * @param workFlowEnabled the workflowEnabled to set
	 */
	public void setWorkFlowEnabled(boolean workFlowEnabled) {
		this.workFlowEnabled = workFlowEnabled;
	}

	/**
	 * @return the workFlowId
	 */
	public long getWorkFlowId() {
		return this.workFlowId;

	}
	/**
	 * @param workFlowId the workFlowId to set
	 */
	public void setWorkFlowId(long workFlowId) {
		this.workFlowId = workFlowId;
	}

	public Radiogroup setListRecordStatus(Radiogroup userAction) {
		return setListRecordStatus(userAction, true);
	}

	public Radiogroup setListRecordStatus(Radiogroup userAction, boolean defaultSave) {
		String sequences = "";

		if (this.role.equals(this.workFlow.firstTask.owner)) {
			sequences = this.workFlow.getAllSequenceFlows(this.workFlow.firstTask.id);
		} else {
			sequences = this.workFlow.getAllSequenceFlows(this.workFlow.getTaskId(getRole()));
		}

		String[] list = sequences.split("/");

		if(defaultSave){
			boolean isSaveSpecified = false;

			for (int i = 0; i < list.length; i++) {
				String[] a = list[i].split("=");
				if (a[0].equalsIgnoreCase("Save")){
					isSaveSpecified = true;
					break;
				}

			}

			if (!isSaveSpecified) {
				userAction.appendItem("Save", "Saved");
			}
		}

		for (int i = 0; i < list.length; i++) {
			String[] a = list[i].split("=");
			if (!a[0].equalsIgnoreCase("Cancel") || !isFirstTask()){
				if(!a[0].equalsIgnoreCase(PennantConstants.RCD_STATUS_REJECTAPPROVAL)){
					userAction.appendItem(a[0], a[1]);
				}
			}
		}
		userAction.setSelectedIndex(0);
		return userAction;
	}
		
	public Radiogroup setRejectRecordStatus(Radiogroup userAction) {
		String sequences = "";

		if (this.role.equals(this.workFlow.firstTask.owner)) {
			sequences = this.workFlow.getAllSequenceFlows(this.workFlow.firstTask.id);
		} else {
			sequences = this.workFlow.getAllSequenceFlows(this.workFlow.getTaskId(getRole()));
		}

		String[] list = sequences.split("/");
		for (int i = 0; i < list.length; i++) {
			String[] a = list[i].split("=");
			if (a[1].equalsIgnoreCase(PennantConstants.RCD_STATUS_REJECTED)){
				userAction.appendItem(a[0], a[1]);
			}
		}
		userAction.setSelectedIndex(0);
		return userAction;
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	public void setDialog(Window dialogWindow){
		menuWest = borderlayout.getWest();
		groupboxMenu = (Groupbox) borderlayout.getFellowIfAny("groupbox_menu");
		menuWest.setVisible(false);
		groupboxMenu.setVisible(false);
		dialogWindow.setParent(groupboxMenu.getParent()); // open the dialog in modal mode
	}
	
	public void setDialog2(Window dialogWindow){
		
		menuWest = borderlayout.getWest();
		groupboxMenu = (Groupbox) borderlayout.getFellowIfAny("groupbox_menu");
		for(int i=1 ; i< groupboxMenu.getParent().getChildren().size();i++ ){
			Window win = (Window)groupboxMenu.getParent().getChildren().get(i);
			win.setVisible(false);
		}
	/*	if(groupboxMenu.getParent().getChildren().size() > 1){
			Window win = (Window)groupboxMenu.getParent().getChildren().get(1);
			win.setVisible(false);
		}*/
		menuWest.setVisible(false);
		groupboxMenu.setVisible(false);
 		dialogWindow.setParent(groupboxMenu.getParent()); // open the dialog in modal mode
 	}
	public void closeDialog2(Window dialogWindow, String dialogName){
		getUserWorkspace().deAlocateAuthorities(dialogName);
		getUserWorkspace().deAlocateRoleAuthorities(dialogName);
		dialogWindow.onClose();
		
		if(groupboxMenu.getParent().getChildren().size() >1){
			Window win = (Window)groupboxMenu.getParent().getChildren().get(groupboxMenu.getParent().getChildren().size()-1);
			win.setVisible(true);
		}else{
			menuWest.setVisible(true);
			groupboxMenu.setVisible(true);
		}
	}
	
	public void closePopUpWindow(Window dialogWindow,String dialogName){
		getUserWorkspace().deAlocateAuthorities(dialogName);
		getUserWorkspace().deAlocateRoleAuthorities(dialogName);
		dialogWindow.onClose();
	}

	public void closeDialog(Window dialogWindow, String dialogName){
		menuWest.setVisible(true);
		groupboxMenu.setVisible(true);
		getUserWorkspace().deAlocateAuthorities(dialogName);
		getUserWorkspace().deAlocateRoleAuthorities(dialogName);
		dialogWindow.onClose();
	}

	//Remove notes entered for the version 
	public void deleteNotes(Notes notes, boolean allNotes){
		NotesService notesService= (NotesService) SpringUtil.getBean("notesService");
		if(allNotes){
			notesService.deleteAllNotes(notes);
		}else{
			notesService.delete(notes);	
		}

	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}


	/**To fill frequency code  with values by calling getFrequency() method of pennantAppUtils Class	*/
	public void fillFrqCode(Combobox codeCombobox, String frqency,boolean readOnly){	

		List<ValueLabel> frqList = 	FrequencyUtil.getFrequency();
		Comboitem comboitem =  new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);		
		for (int i = 0; i < frqList.size(); i++) {
			comboitem =  new Comboitem();
			comboitem.setValue(frqList.get(i).getValue());
			comboitem.setLabel(frqList.get(i).getLabel());
			codeCombobox.appendChild(comboitem);
			if(FrequencyUtil.getFrequencyCode(frqency).equals(frqList.get(i).getValue())){
				codeCombobox.setSelectedItem(comboitem);
			}
		} 
		readOnlyComponent(readOnly, codeCombobox);
	}	


	public void fillFrqMth(Combobox mthCombobox, String frqency,boolean readOnly){
		clearField(mthCombobox);
		String frqCode=FrequencyUtil.getFrequencyCode(frqency);
		String frqMth=FrequencyUtil.getFrequencyMth(frqency);		
		List<ValueLabel> frqMthList = 	FrequencyUtil.getFrequencyDetails(frqCode.charAt(0));
		Comboitem comboitem =  new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		mthCombobox.appendChild(comboitem);
		mthCombobox.setSelectedItem(comboitem);
		for (int i = 0; i < frqMthList.size(); i++) {
			comboitem =  new Comboitem();
			comboitem.setValue(frqMthList.get(i).getValue());
			comboitem.setLabel(frqMthList.get(i).getLabel());
			mthCombobox.appendChild(comboitem);
			if(StringUtils.trimToEmpty(frqMth).equals(frqMthList.get(i).getValue())){
				mthCombobox.setSelectedItem(comboitem);
			}
		} 

		if(!readOnly){
			switch(frqCode.charAt(0)){
			case 'M':
				readOnly=true;
				break;
			case 'F':
				readOnly=true;
				break;
			case 'W':
				readOnly=true;
				break;
			case 'D':
				readOnly=true;
				break;
			}
		}
		readOnlyComponent(readOnly, mthCombobox);

	}

	/**To fill frequency Days with values by calling getFrqdays() method of pennantAppUtils Class	*/
	public void fillFrqDay(Combobox dayCombobox, String frqency,boolean readOnly){
		clearField(dayCombobox);
		List<ValueLabel> frqDaysList = 	FrequencyUtil.getFrqdays(frqency);
		Comboitem comboitem =  new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel("00");
		dayCombobox.appendChild(comboitem);
		dayCombobox.setSelectedItem(comboitem);
		for (int i = 0; i < frqDaysList.size(); i++) {
			comboitem =  new Comboitem();
			comboitem.setValue(frqDaysList.get(i).getValue());
			comboitem.setLabel(frqDaysList.get(i).getLabel());
			dayCombobox.appendChild(comboitem);
			if(FrequencyUtil.getFrequencyDay(frqency).equals(frqDaysList.get(i).getValue())){
				dayCombobox.setSelectedItem(comboitem);
			}
		} 

		if(FrequencyUtil.getFrequencyCode(frqency).equals("D")){
			readOnly=true;
		}

		readOnlyComponent(readOnly, dayCombobox);

	}
	public void clearField(Combobox combobox){
		int count =  combobox.getItemCount();		
		for (int i =0; i < count; i++) {	
			combobox.removeItemAt(0);
		}
	}	

	// ================================To Fill Frequency comboBoxes On Event======================================//	
	// params: Selected comboBox value as a String, Three ComboBoxes Names  and the TextBox Name	
	/**To fill the Frequency Month and Day value based on the selected value of Frequency code	
	 * parms are  Selected value of   frequency code and  frequency code comboBox name
	 * and names of the month and day comboBoxes and a text field
	 * name to store the selected day value
	 * 
	 * */ 

	public void onSelectFrqCode(String frqCode,Combobox codecombo,Combobox monthcombo,Combobox daycombo,Textbox txtFrq,boolean readOnly){
		boolean fillDays=false;
		if(!frqCode.equalsIgnoreCase("#")){			
			switch(frqCode.charAt(0)){
			case 'M':
				fillDays=true;
				break;
			case 'F':
				fillDays=true;
				break;
			case 'W':
				fillDays=true;
				break;
			case 'D':
				fillDays=true;
				break;
			default:
				fillDays=false;
			}

			if (fillDays){
				txtFrq.setValue(frqCode+"0000");
			}else{
				txtFrq.setValue(frqCode);
			}
			fillFrqMth(monthcombo,txtFrq.getValue(),readOnly);		

			if (fillDays){
				fillFrqDay(daycombo, txtFrq.getValue(),readOnly);
				daycombo.setSelectedIndex(0);
				daycombo.setFocus(true);
				daycombo.setSelectionRange(0, daycombo.getValue().length());
				monthcombo.setSelectedIndex(1);
			}else{
				monthcombo.setSelectedIndex(0);
				monthcombo.setFocus(true);
				monthcombo.setSelectionRange(0, monthcombo.getValue().length());
			}

		}else{
			monthcombo.setSelectedIndex(0);
			daycombo.setSelectedIndex(0);
			txtFrq.setValue("");
		}
	}


	/**To fill the Frequency Month value based on the selected value of Frequency Code	 
	 * params: Selected comboBox value as a String,Parent comboBox(frequency code) Selected value as a String,
             Two ComboBoxes (Month and Day) Names  and the TextBox Name*/
	public void onSelectFrqMth(String frqCode,String frqMth,Combobox monthcombo,Combobox daycombo,Textbox txtFrq,boolean readOnly){

		if(!frqMth.equalsIgnoreCase("#")){
			txtFrq.setValue(frqCode+frqMth);
			fillFrqDay(daycombo, txtFrq.getValue(),readOnly);
			daycombo.setSelectedIndex(0);
			daycombo.setFocus(true);
			daycombo.setSelectionRange(0, daycombo.getValue().length());
		}else{
			txtFrq.setValue("");
			daycombo.setSelectedIndex(0);
		}

	}


	/**To fill frequency day comboBox 
	 * params: Selected comboBox(Day) value, frequency as a String and the TextBox Name*/
	public void onSelectFrqDay(Combobox cbfrqCode, Combobox cbfrqMth, Combobox cbfrqDay,Textbox txtFrq) {
		String frqCode = getComboboxValue(cbfrqCode);
		String frqMth = getComboboxValue(cbfrqMth);
		String frqDay = getComboboxValue(cbfrqDay);

		if(!frqDay.equalsIgnoreCase("#")){
			txtFrq.setValue(frqCode+frqMth+frqDay);
		}
	}

	/**
	 * Method for Getting Selected value From ComboBox
	 * @param combobox
	 * @return
	 */
	public String getComboboxValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}
	

	/**
	 * Method to validate the combo box
	 * 
	 * @param Combobox (combobox)
	 * 			 String (label)
	 * */
	public boolean isValidComboValue(Combobox combobox, String label) {
		if(!combobox.isDisabled() && combobox.getSelectedIndex() <= 0) {
			throw new WrongValueException(combobox,Labels.getLabel("STATIC_INVALID",new String[]{label}));
		}
		return true;
	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list 
	 */
	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			if(!excludeFields. contains(","+valueLabel.getValue()+",")){
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);
			}
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	public void readOnlyComponent(boolean isReadOnly,Component component) {
		if(isReadOnly){
			if(component instanceof Combobox){
				((Combobox) component).setTabindex(-1);
				((Combobox) component).setAutodrop(false);
				((Combobox) component).setReadonly(true);
				((Combobox) component).setStyle(reaOnlyStyle);
				((Combobox) component).setButtonVisible(false);
				((Combobox) component).setAutocomplete(false);
				((Combobox) component).setDisabled(true);
			}else if(component instanceof Datebox){
				((Datebox) component).setReadonly(true);
				((Datebox) component).setTabindex(-1);
				((Datebox) component).setButtonVisible(false);
				((Datebox) component).setStyle(reaOnlyStyle);
			}else if(component instanceof Timebox){
				((Timebox) component).setReadonly(true);
				((Timebox) component).setTabindex(-1);
				((Timebox) component).setReadonly(true);
				((Timebox) component).setStyle(reaOnlyStyle);
				((Timebox) component).setButtonVisible(false);
			}else if(component instanceof Intbox){
				((Intbox) component).setReadonly(true);
				((Intbox) component).setTabindex(-1);
			}else if(component instanceof Decimalbox){
				((Decimalbox) component).setReadonly(true);
				((Decimalbox) component).setTabindex(-1);
			}else if(component instanceof Listbox){
				((Listbox) component).setTabindex(-1);
			}else if(component instanceof Textbox){
				((Textbox) component).setReadonly(true);
				((Textbox) component).setTabindex(-1);
			}else if(component instanceof Checkbox){
				((Checkbox) component).setDisabled(true);
				((Checkbox) component).setTabindex(-1);
			}else if(component instanceof Button){
				((Button) component).setDisabled(true);
				((Button) component).setTabindex(-1);
			}else if(component instanceof Longbox){
				((Longbox) component).setReadonly(true);
				((Longbox) component).setTabindex(-1);
			} else if(component instanceof ExtendedCombobox){
 				((ExtendedCombobox) component).setReadonly(true);
			} else if(component instanceof CurrencyBox){
 				((CurrencyBox) component).setReadonly(true);
 				((CurrencyBox) component).setTabindex(-1);
			}else if(component instanceof QueryBuilder){
 				((QueryBuilder) component).setEditable(false);
			} 
		}else{
			if(component instanceof Combobox){
				((Combobox) component).setTabindex(0);
				((Combobox) component).setAutodrop(true);
				((Combobox) component).setButtonVisible(true);
				((Combobox) component).setStyle(reaOnlyStyle);
				((Combobox) component).setReadonly(true);
				((Combobox) component).setDisabled(false);
			}else if(component instanceof Datebox){
				((Datebox) component).setTabindex(0);
				((Datebox) component).setButtonVisible(true);
				((Datebox) component).setReadonly(false);
				((Datebox) component).setStyle(reaOnlyStyle);
			}else if(component instanceof Timebox){
				((Timebox) component).setTabindex(0);
				((Timebox) component).setButtonVisible(true);
				((Timebox) component).setReadonly(false);
				((Timebox) component).setStyle(generalStyle);
			}else if(component instanceof Intbox){
				((Intbox) component).setTabindex(0);
			}else if(component instanceof Decimalbox){
				((Decimalbox) component).setTabindex(0);
				((Decimalbox) component).setReadonly(false);
			}else if(component instanceof Listbox){
				((Listbox) component).setTabindex(0);
			}else if(component instanceof Textbox){
				((Textbox) component).setReadonly(false);
				((Textbox) component).setTabindex(0);
			} else if(component instanceof Checkbox){
				((Checkbox) component).setDisabled(false);
				((Checkbox) component).setTabindex(0);
			}else if(component instanceof Button){
				((Button) component).setDisabled(false);
				((Button) component).setTabindex(0);
			}else if(component instanceof Longbox){
				((Longbox) component).setTabindex(0);
			} else if(component instanceof ExtendedCombobox){
 				((ExtendedCombobox) component).setReadonly(false);
			} else if(component instanceof CurrencyBox){
 				((CurrencyBox) component).setReadonly(false);
 				((CurrencyBox) component).setTabindex(0);
			}else if(component instanceof QueryBuilder){
				((QueryBuilder) component).setEditable(true);
			} 
		}
	}



	// Set The Component Property like Visibility,ReadOnly.

	public void setComponentAccessType(String rightName, boolean isReadOnly,
			Component component, Space space, Label label, Hlayout hlayout,Row row) {

		int accessType = 1;

		if(isWorkFlowEnabled()){
			//accessType = userWorkspace.getAccessType(rightName);
			//TODO Temporary Fix to be changed Later
			if (isReadOnly(rightName)) {
				accessType=0;
			}
		}

		if (accessType == -1) {
			label.setVisible(false);
			hlayout.setVisible(false);
			readOnlyComponent(true, component);
			setRowInvisible(row, hlayout, null);
		} else {
			label.setVisible(true);
			hlayout.setVisible(true);
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, component);
				if(space!=null){
					space.setSclass("");
				}

			} else {
				readOnlyComponent(false, component);
				if(space!=null){
					if(component instanceof Checkbox){
						space.setSclass("");  
					}else{
						space.setSclass("mandatory");
					}
				}
			}
		}
	}

	public void setRowInvisible(Row row, Component comp1,Component comp2){
		boolean visible = false;

		if(comp1==null ||  comp1.isVisible()){
			visible=true;
		}
		if(!visible && ( comp2==null ||  comp2.isVisible())){
			visible=true;
		}
		if(row!=null){
			row.setVisible(visible);
		}	
	} 

	//////=======================new ===============
	// Get the notes entered for rejected reason
	public static Notes getNotes(String moduleName,String notesKey,int version){
		Notes notes = new Notes();
		notes.setModuleName(moduleName);
		notes.setReference(notesKey);
		notes.setVersion(version);
		return notes;
	}
	public void setLovAccess(String rightName, boolean isReadOnly,Button lovButton ,Space space, Label label, Hlayout hlayout,Row row){
		setComponentAccessType(rightName, isReadOnly, lovButton, space, label, hlayout,row);

		if(lovButton.isDisabled()){
			lovButton.setVisible(false);
		}else{
			lovButton.setVisible(true);
		}
	}
	public void  setStatusDetails(Groupbox gb_statusDetails,Groupbox groupboxWf,South south,boolean enqModule){
		if (isWorkFlowEnabled()){
			if(gb_statusDetails != null){
				gb_statusDetails.setVisible(true);
			}
			if(enqModule){
				groupboxWf.setVisible(false);
				south.setHeight("60px");
			}
		}else{
			if(gb_statusDetails != null){
				gb_statusDetails.setVisible(false);
			}
		}
	}

	public void  getWorkFlowDetails(String userAction,String nextTaskId, Object beanObject){

		setTaskId(getWorkFlow().getTaskId(getRole()));
		setNextTaskId(StringUtils.trimToEmpty(nextTaskId));

		if ("Save".equals(userAction)) {
			setNextTaskId(getTaskId() + ";");
		}else{
			setNextTaskId(getNextTaskId().replaceFirst(getTaskId() + ";", ""));

			if (StringUtils.trimToEmpty(getNextTaskId()).equals("")) {
				setNextTaskId(getWorkFlow().getNextTaskIds(getTaskId(),beanObject));
			}
			setAuditingReq(getWorkFlow().getAuditingReq(getTaskId(), beanObject));
		}

		if (!StringUtils.trimToEmpty(getNextTaskId()).equals("")) {
			String[] nextTasks = getNextTaskId().split(";");

			if (nextTasks!=null && nextTasks.length>0){
				for (int i = 0; i < nextTasks.length; i++) {

					if(getNextRoleCode().length()>1){
						setNextRoleCode(getNextRoleCode()+",");
					}
					setNextRoleCode(getWorkFlow().getTaskOwner(nextTasks[i]));
				}
			}else{
				setNextRoleCode(getWorkFlow().getTaskOwner(nextTaskId));
			}
		}
		setOperationRefs(getWorkFlow().getOperationRefs(getTaskId(),beanObject));
		setValidation(true);
	}

	public void showErrorMessage(Window window,Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",PennantConstants.ERR_UNDEF,PennantConstants.ERR_SEV_ERROR,e.getMessage(),null,null));
			ErrorControl.showErrorControl(window, auditHeader);
			e.printStackTrace();
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public void createException(Window window,Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",PennantConstants.ERR_UNDEF,PennantConstants.ERR_SEV_ERROR,e.getMessage(),null,null));
			ErrorControl.showErrorControl(window, auditHeader);
			e.printStackTrace();
			window.onClose();
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public void setExtAccess(String rightName, boolean isReadOnly,ExtendedCombobox extendedCombobox,Row row){
		setExtAccess(rightName, isReadOnly, extendedCombobox, row, true);
	}

	public void setExtAccess(String rightName, boolean isReadOnly, ExtendedCombobox extendedCombobox, Row row, boolean mandatory){

		int accessType = 1;
		if(isWorkFlowEnabled()){
			accessType = userWorkspace.getAccessType(rightName);	
		}
		if (accessType == -1) {
			readOnlyComponent(true, extendedCombobox);
		} else {
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, extendedCombobox);
				extendedCombobox.setMandatoryStyle(false);
			} else {
				readOnlyComponent(false, extendedCombobox);
				if(mandatory){
					extendedCombobox.setMandatoryStyle(true);
				}else{
					extendedCombobox.setMandatoryStyle(false);	
				}
			}
		}
	}

	public int getGridRows() {
		return gridRows;
	}

	public int getListRows() {
		return this.listRows;
	}

	public String getBorderLayoutHeight() {
		return calculateBorderLayoutHeight()+ "px";
	}
	
	public int calculateBorderLayoutHeight() {
		if(this.borderLayoutHeight == 0){
			int northHeight = 55;
			int tabHeight = 26;
			int toolBarHeight = 26;
			int rowheight = 26;
			
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - (northHeight + tabHeight + toolBarHeight);
			this.listRows = Math.round(this.borderLayoutHeight/ rowheight) - 1;
		}
		return borderLayoutHeight;
	}

	public String getListBoxHeight(int gridRowCount) {
		int rowheight = 22;  
		int listBoxHScroll = 16;
		if(this.borderLayoutHeight == 0){
			getBorderLayoutHeight();
		}
		
		int listboxheight = this.borderLayoutHeight - listBoxHScroll;		
		listboxheight = listboxheight-(gridRowCount * 26);			
		this.listRows = Math.round(listboxheight / rowheight) - 1; // - 1  For cross browser; 
		
		return listboxheight + "px";
	}

	/*
	 * Method For Getting UsrFinAuthentication By Branch and Division
	 */
	public String getUsrFinAuthenticationQry(boolean isForReports) {
		StringBuilder wherQuery = new StringBuilder();
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList() == null) {
			getUserWorkspace().getLoginUserDetails().setSecurityUserDivBranchList(PennantAppUtil.getSecurityUserDivBranchList(getUserWorkspace().getLoginUserDetails().getLoginUsrID())); // TODO
		}

		String divisionField = "";
		String branchField = "";
		if (isForReports) {
			divisionField = "FinDivision";
			branchField = "BranchCode";
		} else {
			divisionField = "lovDescFinDivision";
			branchField = "FinBranch";
		}

		String divisionCode = "";
		String branchCode = "";
		if (getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList().isEmpty()) {
			return " ( " + divisionField + "= '' and (" + branchField + "= '' ))";
		}
		for (SecurityUserDivBranch securityUserDivBranch : getUserWorkspace().getLoginUserDetails().getSecurityUserDivBranchList()) {
			if (!divisionCode.equals("") && !divisionCode.equals(securityUserDivBranch.getUserDivision())) {
				divisionCode = securityUserDivBranch.getUserDivision();
				wherQuery.append("  )) or (( " + divisionField + "= '");
				wherQuery.append(divisionCode + "' ) And " + branchField + " In( ");
				branchCode = "";
			} else if (divisionCode.equals("")) {
				divisionCode = securityUserDivBranch.getUserDivision();
				wherQuery.append(" ((( " + divisionField + "= '" + divisionCode + "' ) And " + branchField + " In( ");
			} else if (!branchCode.equals("")) {
				wherQuery.append(", " + securityUserDivBranch.getUserBranch() + " ");
			}
			if (branchCode.equals("")) {
				wherQuery.append(securityUserDivBranch.getUserBranch() + " ");
			}
			branchCode = securityUserDivBranch.getUserBranch();
		}
		wherQuery.append(" ))) ");
		return wherQuery.toString();
	}
	
	/**
	 * Set Component Accesss for Query Builder
	 * @param rightName
	 * @param isReadOnly
	 * @param queryBuilder
	 * @param row
	 */
	public void setQueryAccess(String rightName, boolean isReadOnly, QueryBuilder queryBuilder, Row row){
		
		int accessType = 1;
		if(isWorkFlowEnabled()){
			accessType = userWorkspace.getAccessType(rightName);	
		}
		if (accessType == -1) {
			readOnlyComponent(true, queryBuilder);
		} else {
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, queryBuilder);
			} else {
				readOnlyComponent(false, queryBuilder);
			}
		}
	}
	
	public static Listbox setRecordType(Listbox recordType) {
		recordType.appendItem(Labels.getLabel("Combo.All"),PennantConstants.List_Select);
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_NEW),
				PennantConstants.RECORD_TYPE_NEW);
		
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_UPD),
				PennantConstants.RECORD_TYPE_UPD);
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_DEL),
				PennantConstants.RECORD_TYPE_DEL);
		recordType.setSelectedIndex(0);
		return recordType;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public String getAuditingReq() {
		return auditingReq;
	}

	public void setAuditingReq(String auditingReq) {
		this.auditingReq = auditingReq;
	}

	public String getOperationRefs() {
		return operationRefs;
	}

	public void setOperationRefs(String operationRefs) {
		this.operationRefs = operationRefs;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

}
