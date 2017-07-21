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
 * FileName    		:  FieldsListSelectCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2012    														*
 *                                                                  						*
 * Modified Date    :  23-01-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.reports.reportlist;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.LabelElement;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Reports/ReportList/fieldsListSelect.zul file.
 */
public class FieldsListSelectCtrl extends GFCBaseCtrl<ReportList> {
	private static final long serialVersionUID = 7403304686538288944L;
	private static final Logger logger = Logger.getLogger(FieldsListSelectCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_FieldsListSelect;	// autoWired
	protected Listbox 	listBoxFields; 				// autoWired

	protected Combobox 	cbOrder; 					// autoWired
	protected Combobox 	cdType; 					// autoWired

	// List headers
	protected Listheader listheader_Select; 		// autoWired
	protected Listheader listheader_Fields; 		// autoWired
	protected Listheader listheader_Type;		 	// autoWired


	// not auto wired variables
	private ReportList reportList; // overHanded per parameter                        
	private transient PagedListService pagedListService;
	private transient ReportListDialogCtrl reportListDialogCtrl; 
	protected JdbcSearchObject<ReportList> searchObj;

	List<ValueLabel> sequenceList = null;

	private String[] fields=new String[15];
	private String[] type=new String[15];
	private String[] labels=new String[15];
	
	public String moduleName;
	public String fileName;
	public String configureMode;

	/**
	 * default constructor.<br>
	 */
	public FieldsListSelectCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportList object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FieldsListSelect(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FieldsListSelect);

		try {

			if (arguments.containsKey("reportListDialogCtrl")) {
				this.setReportListDialogCtrl((ReportListDialogCtrl) arguments
						.get("reportListDialogCtrl"));
			} else {
				this.setReportListDialogCtrl(null);
			}

			if (arguments.containsKey("reportList")) {
				this.reportList = (ReportList) arguments.get("reportList");
				ReportList befImage = new ReportList();
				BeanUtils.copyProperties(this.reportList, befImage);
				this.reportList.setBefImage(befImage);

				setReportList(this.reportList);
			} else {
				setReportList(null);
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("fieldListSelectCtrl", this);

			this.moduleName = arguments.get("moduleName").toString();
			this.fileName = arguments.get("fileName").toString();
			this.configureMode = arguments.get("btnConfigure").toString();
			doShowDialog(this.reportList);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FieldsListSelect.onClose();
		}
		logger.debug("Leaving" +event.toString());
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
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aReportList
	 *            ReportList
	 */
	public void doWriteBeanToComponents(ReportList aReportList) {
		logger.debug("Entering") ;

		if(!aReportList.isNewRecord()){
			this.fields = StringUtils.trimToEmpty(aReportList.getFieldValues()).split(",");
			this.type = StringUtils.trimToEmpty(aReportList.getFieldType()).split(",");
			this.labels= StringUtils.trimToEmpty(aReportList.getFieldLabels()).split(",");
		}
		
		sequenceList = new ArrayList<ValueLabel>();
		int noColmns = PennantAppUtil.getReportListColumns(this.fileName);

		for (int i = 0; i < noColmns; i++) {
			sequenceList.add(new ValueLabel(String.valueOf(i+1), String.valueOf(i+1)));
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aReportList
	 * @throws Exception
	 */
	public void doShowDialog(ReportList aReportList) throws Exception {
		logger.debug("Entering") ;

		ModuleMapping mapping =  PennantJavaUtil.getModuleMap(this.moduleName);
		
		List<Field> fields=null;
		if(mapping !=null){
			fields =getFieldList(mapping.getModuleClass()); 
		}
		if(fields != null){
			// fill the components with the data
			doWriteBeanToComponents(aReportList);
			loadFieldsList(fields,aReportList);

			if(configureMode.equals(Labels.getLabel("label_ReportListDialog_btnConfiguration.value"))){
				for(int i=0;i<this.listBoxFields.getItems().size();i++){
					this.listBoxFields.getItemAtIndex(i).setDisabled(true);
				}
				this.btnSave.setVisible(false);
			} 
		}
		try {

			// stores the initial data for comparing if they are changed
			// during user action.
			this.listBoxFields.setHeight("450px");
			this.window_FieldsListSelect.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FieldsListSelect.onClose();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving") ;
	}

	// CRUD operations

	/**
	 * Saves the components to ReportList. <br>
	 * 
	 * @throws InterruptedException
	 */
	private void doSave()throws InterruptedException {
		logger.debug("Entering");
		
		boolean error=false;
		int selectedCount=0;
		this.fields = new String[15];
		this.type= new String[15];
		this.labels= new String[15];

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>(); 
		List<Listitem> list = listBoxFields.getItems();
		int seqMaxValue = 0; 
		int reportColCount = PennantAppUtil.getReportListColumns(this.fileName);

		for (int i = 0; i < list.size(); i++) {
			List<Component> listcells = list.get(i).getChildren();
			Checkbox checkbox = (Checkbox) listcells.get(0).getLastChild();

			if(checkbox.isChecked()){

				Combobox cbmType = (Combobox) listcells.get(2).getLastChild();
				Combobox cbmSeq= (Combobox) listcells.get(3).getLastChild().getLastChild();

				if(!StringUtils.equals(cbmSeq.getValue(),Labels.getLabel("Combo.Select"))){

					int seq =0;

					try {
						seq = Integer.valueOf(cbmSeq.getValue())-1;	
						if(seq > 14){
							error=true;
							wve.add(new WrongValueException(cbmSeq, Labels.getLabel(
									"FIELD_NO_NUMBER",new String[] {Labels.getLabel("listheader_Order.label")})));
							break;
						}
					} catch (Exception e) {
						error=true;
						wve.add(new WrongValueException(cbmSeq, Labels.getLabel(
								"FIELD_NO_NUMBER",new String[] {Labels.getLabel("listheader_Order.label")})));
						break;
					}

					if(reportColCount > seq){
						selectedCount=selectedCount+1;
					}
					
					//Setting Maximum Sequence Number Usage
					if(seqMaxValue < seq){
						seqMaxValue = seq;
					}

					if(StringUtils.isBlank(this.fields[seq])){
						this.fields[seq]=((LabelElement) listcells.get(1)).getLabel();
						this.type[seq]=cbmType.getValue();
						this.labels[seq]=((LabelElement) listcells.get(1)).getLabel();
					}else{
						error=true;
						wve.add(new WrongValueException(cbmSeq, Labels.getLabel(
								"DATA_ALREADY_EXISTS",new String[] {Labels.getLabel("listheader_Order.label")})));							
					}
				}else{
					wve.add(new WrongValueException(cbmSeq, Labels.getLabel(
							"MUST_BE_ENTERED",new String[] {Labels.getLabel("listheader_Order.label")})));
					error=true;
					break;
				}
			}
		}
		
		if(!error){
			
			if (selectedCount != reportColCount){
				MessageUtil.showError(
						Labels.getLabel("SELECTED_COUNT", new String[] { String.valueOf(this.sequenceList.size()) }));
				return;
			}
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		String fieldValues="";
		String typeValues="";
		String labelValues="";

		for (int i = 0; i < this.fields.length; i++) {
			
			if(seqMaxValue < i){
				break;
			}

			if(i!=0){
				fieldValues = fieldValues.concat(",");
				typeValues = typeValues.concat(",");
				labelValues = labelValues.concat(",");
			}
			
			if(this.fields[i] == null){
				fieldValues = fieldValues.concat("null");
				typeValues = typeValues.concat("null");
				labelValues = labelValues.concat("");
			}else{
				fieldValues = fieldValues.concat(this.fields[i]);
				typeValues = typeValues.concat(this.type[i]);
				labelValues = labelValues.concat("listheader_" + this.fields[i].substring(0, 1).toUpperCase()+ this.fields[i].substring(1)+".label");
			}
		}

		reportList.setFieldValues(fieldValues);
		reportList.setFieldType(typeValues);
		reportList.setFieldLabels(labelValues);
		this.reportListDialogCtrl.setReportList(reportList);
		
		closeDialog();
		logger.debug("Leaving");
	}

	/**
	 * Method to load fields of the selected module
	 */
	private void loadFieldsList(List<Field> fields,ReportList aReportList) {
		logger.debug("Entering");

		Listitem item;
		Listcell lc;
		for(int i=0; i<fields.size();i++){
			item = new Listitem();
			Checkbox checkbox = new Checkbox();

			if(!aReportList.isNewRecord()){
				if(aReportList.getFieldValues()!=null){
					if ((aReportList.getFieldValues().concat(",")).contains(fields.get(i).getName().concat(","))) {
						checkbox.setChecked(true);
					}else{
						checkbox.setChecked(false);
					}
				}
				
			}
			
			checkbox.setValue(fields.get(i).getName() +"-"+ fields.get(i).getType().getSimpleName());
			checkbox.addEventListener("onCheck", new onCheckBoxCheked());

			lc = new Listcell();
			lc.appendChild(checkbox);
			lc.setParent(item);
			lc = new Listcell(fields.get(i).getName());
			lc.setParent(item);

			Combobox combobox = new Combobox();
			combobox.appendChild(new Comboitem("String","String"));
			combobox.appendChild(new Comboitem("long", "long"));
			if(!StringUtils.equalsIgnoreCase(fields.get(i).getType().getSimpleName(), "String") && 
					!StringUtils.equalsIgnoreCase(fields.get(i).getType().getSimpleName(), "long")){
				combobox.appendChild(new Comboitem(fields.get(i).getType().getSimpleName(), 
						fields.get(i).getType().getSimpleName()));
			}
			combobox.setValue(fields.get(i).getType().getSimpleName());

			if(!checkbox.isChecked()){
				combobox.setDisabled(true);		 			
			}

			lc = new Listcell();	
			lc.appendChild(combobox);
			lc.setParent(item);
			
			Hlayout hbox = new Hlayout();
			hbox.setSpacing("2px");
			Space space = new Space();
			space.setWidth("2px");

			Combobox comSeq = new Combobox();
			comSeq.appendChild(new Comboitem(Labels.getLabel("Combo.Select")));
			comSeq.setSelectedIndex(0);

			for (int k = 0; k < sequenceList.size(); k++) {
				comSeq.appendChild(new Comboitem(sequenceList.get(k).getLabel()));
			}
			if(!aReportList.isNewRecord()){
				for (int j = 0; j < this.fields.length; j++) {
					if(this.fields[j].equals(fields.get(i).getName())){
						comSeq.setValue(String.valueOf(j+1));
					}
				}
			}
			if(!checkbox.isChecked()){
				space.setSclass("");		
				comSeq.setDisabled(true);		 			
			}else{
				space.setSclass(PennantConstants.mandateSclass);
			}
			lc = new Listcell();	
			hbox.appendChild(space);
			hbox.appendChild(comSeq);
			lc.appendChild(hbox);
			lc.setParent(item);

			item.setParent(listBoxFields);
			if(configureMode.equals(Labels.getLabel("label_ReportListDialog_btnConfiguration.value"))){
				checkbox.setDisabled(true);
				combobox.setDisabled(true);
				comSeq.setDisabled(true);
			}

		}
		
		logger.debug("Leaving");
	}

	/**
	 * when the "checkBox" is checked. <br>
	 * 
	 * onEvent 
	 * @param event
	 * @throws Exception
	 */
	public final class onCheckBoxCheked implements EventListener<Event> {
		public onCheckBoxCheked() {
			//
		}
		
		public void onEvent(Event event) throws Exception {
			logger.debug("Entering");

			Checkbox checkbox = (Checkbox) event.getTarget();
			Combobox cmbType = (Combobox) event.getTarget().getParent().getNextSibling().getNextSibling().getLastChild();
		
			Hlayout hbox = (Hlayout) event.getTarget().getParent().getNextSibling().getNextSibling().getNextSibling().getLastChild();
			Combobox cmbSeq = (Combobox) hbox.getLastChild();
			Space space = (Space) cmbSeq.getPreviousSibling();
			cmbSeq.setValue(Labels.getLabel("Combo.Select"));
			if(!checkbox.isChecked()){
				cmbType.setDisabled(true);
				cmbSeq.setDisabled(true);
				space.setSclass("");	
			}else{
				cmbType.setDisabled(false);
				cmbSeq.setDisabled(false);
				space.setSclass(PennantConstants.mandateSclass);	
			}

			logger.debug("Leaving");
		}
	}

	/**
	 * Method to get fields related to selected module
	 */
	private static List<Field> getFieldList(Class<?> clazz) {

		Field[] fields = clazz.getDeclaredFields();
		List<Field> list= new ArrayList<Field>();
		String excludeFields = "roleCode,nextRoleCode,taskId,nextTaskId,userAction,workflowId,serialVersionUID,newRecord,lovValue," +
		"befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,customerQDE," +
		"auditDetailMap,version,lastMntBy,lastMntOn,";

		for (int i = 0; i < fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName() + ",") 
					&& !fields[i].getName().startsWith("list") && !fields[i].getName().endsWith("List")) {
				list.add(fields[i]);	
			}
		}
		return list;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ReportList getReportList() {
		return reportList;
	}
	public void setReportList(ReportList reportList) {
		this.reportList = reportList;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public ReportListDialogCtrl getReportListDialogCtrl() {
		return reportListDialogCtrl;
	}
	public void setReportListDialogCtrl(ReportListDialogCtrl reportListDialogCtrl) {
		this.reportListDialogCtrl = reportListDialogCtrl;
	}

	public JdbcSearchObject<ReportList> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ReportList> searchObj) {
		this.searchObj = searchObj;
	}

}
