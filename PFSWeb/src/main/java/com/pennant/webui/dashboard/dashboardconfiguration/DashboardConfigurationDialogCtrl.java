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
 * FileName    		:  DashboardConfigurationDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2011    														*
 *                                                                  						*
 * Modified Date    :  14-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dashboard.dashboardconfiguration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.dashboard.DashboardCreate;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/DashBoards/DashboardConfiguration/dashboardConfigurationDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DashboardConfigurationDialogCtrl extends GFCBaseCtrl<DashboardConfiguration> {
	private static final long serialVersionUID = 8579170086287103990L;
	private static final Logger logger = Logger.getLogger(DashboardConfigurationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	public Window 	      window_DashboardConfigurationDialog; 	// autoWired`

	protected Textbox 	  dashboardCode; 						// autoWired
	protected Textbox     dashboardDesc; 					    // autoWired
	protected Combobox 	  dashboardType; 						// autoWired

	protected Textbox     dashboardCaption;                 	// autoWired
	protected Textbox     subCaption;	                        // autoWired
	protected Textbox     xAxisName;	                        // autoWired
	protected Textbox     yAxisName;	                        // autoWiredR
	protected Codemirror  query; 		                        // autoWired
	protected Codemirror  dataXML; 		                        // autoWired
	protected Codemirror  remarks;                              // autoWired
	protected Checkbox    isAdtDataSource;	                    // autoWired
	protected Checkbox    isMultiSeries;                        // autoWired
	protected Checkbox    isDataXML;                            // autoWired
	protected Checkbox    isDrillDownChart;                     // autoWired

	protected Textbox     txtBgColor;                           // autoWired
	protected Textbox     txtCanvasBgColor;                     // autoWired
	protected Textbox     colb_BgColor;                         // autoWired
	protected Textbox     colb_CanvasBgColor;                   // autoWired
	protected Combobox    cbDimension;                          // autoWired
	protected Button      btnValidate;                          // autoWired

	protected Tab        dashBoardDetailsTab;                   // autoWired
	protected Row         row_XYAxisNames;                      // autoWired
	protected Row         row_DataXML;                          // autoWired
	protected Row         row_queryData;                        // autoWired
	protected Row         row_dataSource;                       // autoWired
	protected Row         statusRow;							// autoWired

	// not auto wired variables
	private DashboardConfiguration dashboardConfiguration; // overHanded per parameter
	private transient DashboardConfigurationListCtrl dashboardConfigurationListCtrl; // overHanded per parameter

	//Used to create the new DashBoard
	private DashboardCreate dashboardCreate ;

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient DashboardConfigurationService dashboardConfigurationService;
	private transient PagedListService pagedListService;

	private List<ValueLabel> listDashboardType = PennantAppUtil.getDashBoardType(); 	// autoWiredgetChartDimensions()
	private List<ValueLabel> listDimensions = PennantAppUtil.getChartDimensions();

	/**
	 * default constructor.<br>
	 */
	public DashboardConfigurationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DashboardConfigurationDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected DashboardDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DashboardConfigurationDialog(Event event)throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DashboardConfigurationDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("dashboardConfiguration")) {
			this.dashboardConfiguration = (DashboardConfiguration) arguments.get("dashboardConfiguration");
			DashboardConfiguration befImage = new DashboardConfiguration();
			BeanUtils.copyProperties(this.dashboardConfiguration, befImage);
			this.dashboardConfiguration.setBefImage(befImage);

			setDashboardConfiguration(this.dashboardConfiguration);
		} else {
			setDashboardConfiguration(null);
		}

		doLoadWorkFlow(this.dashboardConfiguration.isWorkflow(), this.dashboardConfiguration.getWorkflowId()
				,this.dashboardConfiguration.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "DashboardConfigurationDialog");
		}
		// READ OVERHANDED parameters !
		// we get the dashboardDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete dashboardDetail here.
		if (arguments.containsKey("dashboardConfigurationListCtrl")) {
			setDashboardConfigurationListCtrl((DashboardConfigurationListCtrl) arguments.get("dashboardConfigurationListCtrl"));
		} else {
			setDashboardConfigurationListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDashboardConfiguration());
		setListType(this.listDashboardType,this.dashboardType);
		setListType(this.listDimensions,this.cbDimension);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.dashboardCode.setMaxlength(20);
		this.dashboardDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		logger.debug("Leaving");
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
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doSave();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering"+event.toString());
		doEdit();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		MessageUtil.showHelpWindow(event, window_DashboardConfigurationDialog);
		logger.debug("Leaving"+event.toString());
	}
	/**
	 * when the "isDataXML" Checkbox is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$isDataXML(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		this.isMultiSeries.setDisabled(false);
		if(this.isDataXML.isChecked()){
			this.row_queryData.setVisible(false);
			this.row_dataSource.setVisible(false);
			this.query.setValue("");
			this.remarks.setValue("");
			this.isDrillDownChart.setChecked(false);
			this.row_DataXML.setVisible(true);
		}else{
			this.remarks.setReadonly(false);
			this.row_DataXML.setVisible(false);
			this.dataXML.setValue("");
			this.row_queryData.setVisible(true);
			this.row_dataSource.setVisible(true);
		}
		logger.debug("Leaving"+event.toString());
	}

	/** when the "isDrillDownChart" Check box is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onCheck$isDrillDownChart(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		if(this.isDrillDownChart.isChecked()){
			this.isMultiSeries.setChecked(false);
			this.isMultiSeries.setDisabled(true);
		}else{
			this.isMultiSeries.setDisabled(false);
		}
		logger.debug("Leaving"+event.toString());

	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());
		doDelete();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering"+event.toString());
		doCancel();
		logger.debug("Leaving"+event.toString());
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


	public void onSelect$dashboardType(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		if(this.dashboardType.getSelectedItem().getLabel().equals(Labels.getLabel("label_Select_Pie"))){
			this.row_XYAxisNames.setVisible(false);
		}else{
			this.row_XYAxisNames.setVisible(false);
		}

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++custom component events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	//Generate the chart based on the query and display it in chartSimulator.zul
	public void onClick$btnValidate(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering"+event.toString());
		doSetValidation();
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration(); 
		BeanUtils.copyProperties(getDashboardConfiguration(),aDashboardConfiguration);
		doWriteComponentsToBean(aDashboardConfiguration);
		ChartUtil chartUtil=new ChartUtil();
		try{
			String chartStrXML="";
			if(StringUtils.isBlank(aDashboardConfiguration.getDataXML())){
				chartStrXML=getLabelAndValues(aDashboardConfiguration,chartUtil);
			}else{
				chartStrXML=aDashboardConfiguration.getDataXML();
			}
			ChartDetail chartDetail=new ChartDetail();
			// to avoid id issue onclick simulate button
			chartDetail.setChartId(aDashboardConfiguration.getDashboardCode()+"simulate"); 
			chartDetail.setStrXML(chartStrXML);
			chartDetail.setChartHeight("450px");//85%-450px
			chartDetail.setChartWidth("512px");//85%-512px
			chartDetail.setiFrameHeight("100%");
			chartDetail.setiFrameWidth("100%");
			chartDetail.setChartType(chartUtil.getChartType(aDashboardConfiguration));
			final HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("chartDetail", chartDetail);
			map.put("dashboardConfigurationDialogCtrl", this);

			/** we can additionally handed over the listBox or the controller self,
			 * so we have in the dialog access to the listBox ListModel. This is
			 * fine for synchronizing the data in the NationalityCodesListbox from
			 * the dialog when we do a delete, edit or insert a Nationality.*/

			map.put("welcomectrl", this);

			// call the ZUL-file with the parameters packed in a map

			Executions.createComponents("/Charts/chartSimulator.zul", null, map);	 
			logger.debug("Leaving"+event.toString());
		} catch (DataAccessException e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_DashboardConfigurationDialog_UnCategorizedSQLException"));
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.dashboardConfiguration.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDashboardDetail
	 *            DashboardDetail
	 */
	public void doWriteBeanToComponents(DashboardConfiguration aDashboardConfiguration) {
		logger.debug("Entering");
		this.dashboardCode.setValue(aDashboardConfiguration.getDashboardCode());
		this.dashboardDesc.setValue(aDashboardConfiguration.getDashboardDesc());
		this.dashboardCaption.setValue(aDashboardConfiguration.getCaption());
		this.subCaption.setValue(aDashboardConfiguration.getSubCaption());
		this.query.setValue(aDashboardConfiguration.getQuery());
		this.dataXML.setValue(aDashboardConfiguration.getDataXML());
		this.isDrillDownChart.setChecked(aDashboardConfiguration.isDrillDownChart());
		if(aDashboardConfiguration.isDrillDownChart()){
			this.isMultiSeries.setDisabled(true);
		}
		if(StringUtils.isBlank(aDashboardConfiguration.getDataXML())){
			this.isDataXML.setChecked(false);

		}else{
			this.row_dataSource.setVisible(false);
			this.row_queryData.setVisible(false);
			this.row_DataXML.setVisible(true);
			this.isDataXML.setChecked(true);
			this.isMultiSeries.setDisabled(true);
		}
		this.isAdtDataSource.setChecked(aDashboardConfiguration.isAdtDataSource());
		this.remarks.setValue(aDashboardConfiguration.getRemarks());
		this.isMultiSeries.setChecked(aDashboardConfiguration.isMultiSeries());
		if(aDashboardConfiguration.isNew()){
			this.dashboardType.setValue(PennantAppUtil.getlabelDesc("",PennantAppUtil.getDashBoardType()));
		}else{
			this.dashboardType.setValue(PennantAppUtil.getlabelDesc(
					String.valueOf(aDashboardConfiguration.getDashboardType()),PennantAppUtil.getDashBoardType()));
		}
		if(aDashboardConfiguration.isNew()){
			this.cbDimension.setValue(PennantAppUtil.getlabelDesc(
					String.valueOf(Labels
							.getLabel("label_Select_2D")),PennantAppUtil.getChartDimensions()));

		}else{
			this.cbDimension.setValue(PennantAppUtil.getlabelDesc(
					String.valueOf(aDashboardConfiguration.getDimension()),PennantAppUtil.getChartDimensions()));

		}

		logger.debug("Leaving");
	}
	private void setListType(List<ValueLabel> listDashboardType,Combobox comboBox) {
		logger.debug("Entering ");
		for (int i = 0; i < listDashboardType.size(); i++) {

			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listDashboardType.get(i).getLabel());
			comboitem.setValue(listDashboardType.get(i).getValue());
			comboBox.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDashboardDetail
	 */
	public void doWriteComponentsToBean(DashboardConfiguration aDashboardDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			ChartsConfig chartsConfig=new ChartsConfig(this.dashboardCaption.getValue(),this.subCaption.getValue()
					,this.xAxisName.getValue(),this.yAxisName.getValue());
			aDashboardDetail.setLovDescChartsConfig(chartsConfig);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDashboardDetail.setDashboardDesc(this.dashboardDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			aDashboardDetail.setDashboardCode(this.dashboardCode.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			aDashboardDetail.setCaption(this.dashboardCaption.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			aDashboardDetail.setSubCaption(this.subCaption.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			String dashboardType = (String) this.dashboardType.getSelectedItem().getValue();
			if (StringUtils.isBlank(dashboardType)) {
				throw new WrongValueException(this.dashboardType,Labels.getLabel("STATIC_INVALID"
						,new String[] { Labels.getLabel("label_DashboardConfigurationDialog_DashboardType.value") }));
			}
			aDashboardDetail.setDashboardType(dashboardType);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDashboardDetail.setAdtDataSource(this.isAdtDataSource.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}


		try {
			if (StringUtils.isBlank(this.query.getValue()) && !this.isDataXML.isChecked()) {
				throw new WrongValueException(
						this.query,
						Labels.getLabel(
								"FIELD_NO_EMPTY",
								new String[] { Labels
										.getLabel("label_DashboardConfigurationDialog_Query.value") }));
			}
			aDashboardDetail.setQuery(this.query.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotBlank(this.query.getValue())) {
				if (StringUtils.containsIgnoreCase(this.query.getValue().trim(), "delete")
						|| StringUtils.containsIgnoreCase(this.query.getValue().trim(), "update")
						|| StringUtils.containsIgnoreCase(this.query.getValue().trim(), "alter")
						|| StringUtils.containsIgnoreCase(this.query.getValue().trim(), "truncate")
						|| !StringUtils.startsWithIgnoreCase(this.query.getValue().trim(), "select")) {
					throw new WrongValueException(this.query,
							Labels.getLabel("label_DashboardConfigurationDialog_Query_Alert.value"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.isDrillDownChart.isChecked()) {
				if (!StringUtils.containsIgnoreCase(this.query.getValue().trim(), "||")
						|| StringUtils.countMatches(this.query.getValue(), "||")>3 
						|| !StringUtils.containsIgnoreCase(this.query.getValue().trim(), "reference")){
					throw new WrongValueException(
							this.query,Labels
							.getLabel("label_DashboardConfigurationDialog_QueryDrillDown.value"));
				}
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.isDrillDownChart.isChecked()) {
				if (!StringUtils.containsIgnoreCase(this.remarks.getValue().trim(), "||")){
					throw new WrongValueException(
							this.remarks,Labels
							.getLabel("label_DashboardConfigurationDialog_RemarksDrillDown.value"));
				}
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isBlank(this.dataXML.getValue())  && this.isDataXML.isChecked()) {
				throw new WrongValueException(
						this.dataXML,
						Labels.getLabel(
								"FIELD_NO_EMPTY",
								new String[] { Labels
										.getLabel("label_DashboardConfigurationDialog_dataXML.value") }));
			}

			aDashboardDetail.setDataXML(this.dataXML.getValue());
		}catch (WrongValueException we) {
			wve.add(we);
		}


		aDashboardDetail.setMultiSeries(this.isMultiSeries.isChecked());
		aDashboardDetail.setDimension(this.cbDimension.getSelectedItem().getValue().toString());
		aDashboardDetail.setRemarks(this.remarks.getValue());
		aDashboardDetail.setDrillDownChart(this.isDrillDownChart.isChecked());
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			this.dashBoardDetailsTab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aDashboardDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDashboardDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(DashboardConfiguration aDashboardConfiguration) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aDashboardConfiguration.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();

			// setFocus
			this.dashboardCode.focus();
		} else {
			this.dashboardDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aDashboardConfiguration);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.dashboardCode.setErrorMessage("");
		this.dashboardDesc.setErrorMessage("");
		this.dashboardType.setErrorMessage("");
		this.dashboardCaption.setErrorMessage("");
		Clients.clearWrongValue(this.query);
		Clients.clearWrongValue(this.remarks);
		Clients.clearWrongValue(this.dataXML);

		logger.debug("Leaving");
	}
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		//Code
		if (!this.dashboardCode.isReadonly()) {
			this.dashboardCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DashboardConfigurationDialog_DashboardCode.value"),PennantRegularExpressions.REGEX_ALPHA_CODE,true));
		}
		//Description
		if (!this.dashboardDesc.isReadonly()){
			this.dashboardDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DashboardConfigurationDialog_DashboardDesc.value"),PennantRegularExpressions.REGEX_DESCRIPTION,true));
		}
		//Type
		if (!this.dashboardType.isDisabled()) {
			this.dashboardType.setConstraint(new StaticListValidator(listDashboardType,Labels.getLabel("label_DashboardConfigurationDialog_DashboardType.value")));
		}
		//Caption
		if (!this.dashboardCaption.isReadonly()){
			this.dashboardCaption.setConstraint(new PTStringValidator(Labels.getLabel("label_DashboardConfigurationDialog_Caption.value"),PennantRegularExpressions.REGEX_NAME,true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.dashboardCode.setConstraint("");
		this.dashboardDesc.setConstraint("");
		this.dashboardType.setConstraint("");
		this.dashboardCaption.setConstraint("");
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getDashboardConfigurationListCtrl().search();
	}
	/**
	 * Deletes a DashboardDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		BeanUtils.copyProperties(getDashboardConfiguration(), aDashboardConfiguration);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aDashboardConfiguration.getDashboardCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDashboardConfiguration.getRecordType())) {
				aDashboardConfiguration.setVersion(aDashboardConfiguration.getVersion() + 1);
				aDashboardConfiguration.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDashboardConfiguration.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDashboardConfiguration, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getDashboardConfiguration().isNewRecord()) {
			this.dashboardCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.dashboardCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.dashboardDesc.setReadonly(isReadOnly("DashboardConfigurationDialog_dashboardDesc"));
		this.dashboardType.setDisabled(isReadOnly("DashboardConfigurationDialog_dashboardType"));
		this.query.setReadonly(isReadOnly("DashboardConfigurationDialog_query"));
		this.dataXML.setReadonly(isReadOnly("DashboardConfigurationDialog_DataXML"));
		this.isDataXML.setDisabled(isReadOnly("DashboardConfigurationDialog_IsDataXML"));
		this.dashboardCaption.setReadonly(isReadOnly("DashboardConfigurationDialog_Caption"));
		this.subCaption.setReadonly(isReadOnly("DashboardConfigurationDialog_SubCaption"));
		this.cbDimension.setDisabled(isReadOnly("DashboardConfigurationDialog_Dimension"));
		this.btnValidate.setDisabled(isReadOnly("DashboardConfigurationDialog_Validate"));
		this.isAdtDataSource.setDisabled(isReadOnly("DashboardConfigurationDialog_IsAdtDataSource"));
		this.remarks.setReadonly(isReadOnly("DashboardConfigurationDialog_Remarks"));
		this.isMultiSeries.setDisabled(isReadOnly("DashboardConfigurationDialog_IsMultiSeries"));
		this.isDrillDownChart.setDisabled(isReadOnly("DashboardConfigurationDialog_IsDrillDownChart"));
		if(this.isDataXML.isChecked() || this.isDrillDownChart.isChecked()){
			this.isMultiSeries.setDisabled(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.dashboardConfiguration.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.dashboardCode.setReadonly(true);
		this.dashboardDesc.setReadonly(true);
		this.dashboardType.setDisabled(true);
		this.query.setReadonly(true);
		this.dataXML.setReadonly(true);
		this.isDataXML.setDisabled(true);
		this.dashboardCaption.setReadonly(true);
		this.cbDimension.setDisabled(true);
		this.subCaption.setReadonly(true);
		this.btnValidate.setDisabled(true);
		this.isAdtDataSource.setDisabled(true);
		this.remarks.setReadonly(true);
		this.isMultiSeries.setDisabled(true);
		this.isDrillDownChart.setDisabled(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
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
		this.dashboardCode.setValue("");
		this.dashboardDesc.setValue("");
		this.dashboardType.setSelectedIndex(0);
		this.cbDimension.setSelectedIndex(0);
		this.isAdtDataSource.setChecked(false);
		this.query.setValue("");
		this.dataXML.setValue("");
		this.isDataXML.setChecked(false);
		this.remarks.setValue("");
		logger.debug("Leaving");
	}



	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		BeanUtils.copyProperties(getDashboardConfiguration(), aDashboardConfiguration);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the DashboardDetail object with the components data
		doWriteComponentsToBean(aDashboardConfiguration);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aDashboardConfiguration.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			if (StringUtils.isBlank(aDashboardConfiguration.getRecordType())) {
				aDashboardConfiguration.setVersion(aDashboardConfiguration.getVersion() + 1);
				if (isNew) {
					aDashboardConfiguration.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDashboardConfiguration.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDashboardConfiguration.setNewRecord(true);
				}
			}
		} else {
			aDashboardConfiguration.setVersion(aDashboardConfiguration.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aDashboardConfiguration, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(DashboardConfiguration aDashboardConfiguration, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDashboardConfiguration.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDashboardConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDashboardConfiguration.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aDashboardConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDashboardConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDashboardConfiguration);
				}

				if (isNotesMandatory(taskId, aDashboardConfiguration)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aDashboardConfiguration.setTaskId(taskId);
			aDashboardConfiguration.setNextTaskId(nextTaskId);
			aDashboardConfiguration.setRoleCode(getRole());
			aDashboardConfiguration.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDashboardConfiguration, tranType);

			String operationRefs = getServiceOperations(taskId, aDashboardConfiguration);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDashboardConfiguration, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aDashboardConfiguration, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted=false;
		DashboardConfiguration aDashboardConfiguration = (DashboardConfiguration) auditHeader.getAuditDetail().getModelData();
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getDashboardConfigurationService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getDashboardConfigurationService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getDashboardConfigurationService().doApprove(auditHeader);

						if (aDashboardConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getDashboardConfigurationService().doReject(auditHeader);

						if (aDashboardConfiguration.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),	null));
						retValue = ErrorControl.showErrorControl(this.window_DashboardConfigurationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_DashboardConfigurationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(dashboardConfiguration), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**

	 * Get Audit Header Details
	 * 
	 * @param aDashboardDetail
	 *            (DashboardDetail)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(DashboardConfiguration aDashboardConfiguration, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDashboardConfiguration.getBefImage(), aDashboardConfiguration);
		return new AuditHeader(String.valueOf(aDashboardConfiguration.getId()), null, null,
				null, auditDetail, aDashboardConfiguration.getUserDetails(), getOverideMap());
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
		doShowNotes(this.dashboardConfiguration);
	}

	
	
	@Override
	protected String getReference() {
		return String.valueOf(this.dashboardConfiguration.getDashboardCode());
	}


	//	--------------------------------------------------------------------------------------------------

	public String getLabelAndValues(DashboardConfiguration dashboardConfiguration, ChartUtil chartUtil)
			throws DataAccessException {
		logger.debug("Entering ");
		String whereCondition =getRoleList();
		List<ChartSetElement> listSetElements = getDashboardConfigurationService().getLabelAndValues(
				dashboardConfiguration, whereCondition, getUserWorkspace().getLoggedInUser(),
				getUserWorkspace().getSecurityRoles());
		if(listSetElements!=null && listSetElements.size()>0){
			dashboardConfiguration.getLovDescChartsConfig().setSetElements(listSetElements);
			dashboardConfiguration.getLovDescChartsConfig().setRemarks(dashboardConfiguration.getRemarks());
		}
		
		if(chartUtil.isAGauge(dashboardConfiguration)){
			return dashboardConfiguration.getLovDescChartsConfig().getAGaugeXML();
		}else if(dashboardConfiguration.isDrillDownChart()){
			return dashboardConfiguration.getLovDescChartsConfig().getDrillDownChartXML();
		}else if(chartUtil.isMultiSeries(dashboardConfiguration)){
			return dashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(dashboardConfiguration.getRenderAs());
		}else{
			return dashboardConfiguration.getLovDescChartsConfig().getChartXML();
		}
	}
	
	public String getRoleList() {
		List<SecurityRole> roles = getUserWorkspace().getSecurityRoles();
		
		String role="where rolecode in (";
		for (int i = 0; i < roles.size(); i++) {
			role=role+"'"+roles.get(i).getRoleCd()+"'";

			if(i!=roles.size()-1){
				role=role+",";
			}
		}
		role=role+")";
		return role;
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

	public DashboardConfiguration getDashboardConfiguration() {
		return dashboardConfiguration;
	}
	public void setDashboardConfiguration(DashboardConfiguration dashboardConfiguration) {
		this.dashboardConfiguration = dashboardConfiguration;
	}

	public DashboardConfigurationListCtrl getDashboardConfigurationListCtrl() {
		return dashboardConfigurationListCtrl;
	}
	public void setDashboardConfigurationListCtrl(DashboardConfigurationListCtrl dashboardConfigurationListCtrl) {
		this.dashboardConfigurationListCtrl = dashboardConfigurationListCtrl;
	}

	public DashboardConfigurationService getDashboardConfigurationService() {
		return dashboardConfigurationService;
	}
	public void setDashboardConfigurationService(DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public DashboardCreate getDashboardCreate() {
		return dashboardCreate;
	}
	public void setDashboardCreate(DashboardCreate dashboardCreate) {
		this.dashboardCreate = dashboardCreate;
	}
}
