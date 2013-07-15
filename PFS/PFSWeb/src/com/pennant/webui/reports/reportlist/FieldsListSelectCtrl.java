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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.LabelElement;

import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Reports/ReportList/fieldsListSelect.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FieldsListSelectCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7403304686538288944L;
	private final static Logger logger = Logger.getLogger(ReportListDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window 	window_FieldsListSelect;	// autoWired
	protected Listbox 	listBoxFields; 				// autoWired

	protected Combobox 	cbOrder; 					// autoWired
	protected Combobox 	cdType; 					// autoWired

	// List headers
	protected Listheader listheader_Select; 		// autoWired
	protected Listheader listheader_Fields; 		// autoWired
	protected Listheader listheader_Type;		 	// autoWired

	protected Button       btnSave;               	// autoWired
	protected Button       btnClose;                // autoWired

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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected ReportList object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FieldsListSelect(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("reportListDialogCtrl")) {
			this.setReportListDialogCtrl((ReportListDialogCtrl) args.get("reportListDialogCtrl"));			
		} else {
			this.setReportListDialogCtrl(null);
		}
		
		if (args.containsKey("reportList")) {
			this.reportList = (ReportList) args.get("reportList");
			ReportList befImage =new ReportList();
			BeanUtils.copyProperties(this.reportList, befImage);
			this.reportList.setBefImage(befImage);

			setReportList(this.reportList);
		} else {
			setReportList(null);
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("fieldListSelectCtrl",this);
		
		this.moduleName = args.get("moduleName").toString();
		this.fileName = args.get("fileName").toString();
		this.configureMode = args.get("btnConfigure").toString();
		doShowDialog(this.reportList);
		logger.debug("Leaving" +event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
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
		doClose();
		logger.debug("Leaving" +event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 * @throws InterruptedException 
	 */
	private void doClose() throws InterruptedException {
		this.window_FieldsListSelect.onClose();
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
			this.fields = aReportList.getFieldValues().split(",");
			this.type = aReportList.getFieldType().split(",");
			this.labels= aReportList.getFieldLabels().split(",");
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(ReportList aReportList) throws InterruptedException {
		logger.debug("Entering") ;

		ModuleMapping mapping =  PennantJavaUtil.getModuleMap(this.moduleName);
		
		List<Field> fields=null;
		if(mapping !=null){
			fields =getFieldList(mapping.getModuleObject()); 
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
			this.window_FieldsListSelect.doModal();
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			logger.error("doShowDialog() " + e.getMessage());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	/**
	 * Saves the components to ReportList. <br>
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void doSave()throws InterruptedException {
		boolean error=false;
		int selectedCount=0;
		this.fields = new String[15];
		this.type= new String[15];
		this.labels= new String[15];

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>(); 
		//Upgraded to ZK-6.5.1.1 Changed from get children to get items 	
		List<Listitem> list = listBoxFields.getItems();

		for (int i = 1; i < list.size(); i++) {
			List<Component> listcells = list.get(i).getChildren();
			Checkbox checkbox = (Checkbox) listcells.get(0).getLastChild();

			if(checkbox.isChecked()){
				selectedCount=selectedCount+1;

				Combobox cbmType = (Combobox) listcells.get(2).getLastChild();
				Combobox cbmSeq= (Combobox) listcells.get(3).getLastChild();

				if(StringUtils.trimToEmpty(cbmSeq.getValue())!=""){

					int seq =0;

					try {
						seq = Integer.valueOf(cbmSeq.getValue())-1;	
					} catch (Exception e) {
						error=true;
						wve.add(new WrongValueException(cbmSeq, Labels.getLabel(
								"FIELD_NO_NUMBER",new String[] {Labels.getLabel("listheader_Order.label")})));
						break;
					}

					boolean listFound=false;
					for (int j = 0; j < this.sequenceList.size(); j++) {

						if(this.sequenceList.get(j).getValue().equals(cbmSeq.getValue())){
							listFound=true;
							break;
						}
					}

					if(!listFound){
						error=true;
						wve.add(new WrongValueException(cbmSeq, Labels.getLabel(
								"STATIC_INVALID",new String[] {Labels.getLabel("listheader_Order.label")})));
						break;
					}

					if(StringUtils.trimToEmpty(this.fields[seq]).equals("")){
						//Upgraded to ZK-6.5.1.1 Casted it with Label Element 	
						this.fields[seq]=((LabelElement) listcells.get(1)).getLabel();
						this.type[seq]=cbmType.getValue();
						//Upgraded to ZK-6.5.1.1 Casted it with Label Element
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

			if (selectedCount!=sequenceList.size()){
				PTMessageUtils.showErrorMessage(Labels.getLabel(
						"SELECTED_COUNT",new String[] {String.valueOf(this.sequenceList.size())}));
				error=true;
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

			if(this.fields[i]==null){
				break;
			}else{

				if(i!=0){
					fieldValues = fieldValues.concat(",");
					typeValues = typeValues.concat(",");
					labelValues = labelValues.concat(",");
				}
				fieldValues = fieldValues.concat(this.fields[i]);
				typeValues = typeValues.concat(this.type[i]);
				labelValues = labelValues.concat("label_" + this.fields[i].substring(0, 1).toUpperCase()+ this.fields[i].substring(1));
			} 
		}

		reportList.setFieldValues(fieldValues);
		reportList.setFieldType(typeValues);
		reportList.setFieldLabels(labelValues);
		this.reportListDialogCtrl.setReportList(reportList);
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

				if(aReportList.getFieldValues().contains(fields.get(i).getName())){
					checkbox.setChecked(true);
				}else{
					checkbox.setChecked(false);
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
			combobox.appendChild(new Comboitem("String"));
			combobox.appendChild(new Comboitem("long"));
			combobox.setValue(fields.get(i).getType().getSimpleName());

			if(!checkbox.isChecked()){
				combobox.setDisabled(true);		 			
			}

			lc = new Listcell();	
			lc.appendChild(combobox);
			lc.setParent(item);

			Combobox comSeq = new Combobox();
			comSeq.appendChild(new Comboitem(" "));

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
				comSeq.setDisabled(true);		 			
			}
			lc = new Listcell();	
			lc.appendChild(comSeq);
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
	public final class onCheckBoxCheked implements EventListener{
		public void onEvent(Event event) throws Exception {
			logger.debug("Entering");

			Checkbox checkbox = (Checkbox) event.getTarget();
			Combobox cmbType = (Combobox) event.getTarget().getParent().getNextSibling().getNextSibling().getLastChild();
			cmbType.setDisabled(!checkbox.isChecked());

			Combobox cmbSeq = (Combobox) event.getTarget().getParent().getNextSibling().getNextSibling().getNextSibling().getLastChild();
			cmbSeq.setDisabled(!checkbox.isChecked());

			logger.debug("Leaving");
		}
	}

	/**
	 * Method to get fields related to selected module
	 */
	private static List<Field> getFieldList(Object detailObject) {

		Field[] fields = detailObject.getClass().getDeclaredFields();
		List<Field> list= new ArrayList<Field>();
		String excludeFields = "roleCode,nextRoleCode,taskId,nextTaskId,userAction,workflowId,serialVersionUID,newRecord,lovValue," +
		"befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,customerQDE," +
		"auditDetailMap,version,lastMntBy,lastMntOn,";

		for (int i = 0; i < fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName() + ",") && !fields[i].getName().startsWith("lovDesc") 
					&& !fields[i].getName().startsWith("list") && !fields[i].getName().endsWith("List")) {
				list.add(fields[i]);	
			}
		}
		return list;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
