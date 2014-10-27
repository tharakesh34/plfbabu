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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.dashboard.DashboardCreate;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/DashBoards/DashboardConfiguration/dashboardConfigurationDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DashboardConfigurationDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8579170086287103990L;
	private final static Logger logger = Logger.getLogger(DashboardConfigurationDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	public Window 	      window_DashboardConfigurationDialog; 	// autoWired
	protected Groupbox 	  gb_dashboard;							// autoWired

	protected Textbox 	  dashboardCode; 						// autoWired
	protected Textbox     dashboardDesc; 					    // autoWired
	protected Combobox 	  dashboardType; 						// autoWired

	protected Textbox     dashboardCaption;                 	// autoWired
	protected Textbox     subCaption;	                        // autoWired
	protected Textbox     xAxisName;	                        // autoWired
	protected Textbox     yAxisName;	                        // autoWired
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

	protected Label       recordStatus; 						// autoWired
	protected Radiogroup  userAction;							// autoWired
	protected Groupbox    groupboxWf;							// autoWired
	protected Row         statusRow;							// autoWired
	protected Button      btnNew; 								// autoWired
	protected Button      btnEdit; 								// autoWired
	protected Button      btnDelete; 							// autoWired
	protected Button      btnSave; 								// autoWired
	protected Button      btnCancel; 						    // autoWired
	protected Button      btnClose; 							// autoWired
	protected Button      btnHelp; 								// autoWired
	protected Button      btnNotes; 							// autoWired

	// not auto wired variables
	private DashboardConfiguration dashboardConfiguration; // overHanded per parameter
	private transient DashboardConfigurationListCtrl dashboardConfigurationListCtrl; // overHanded per parameter

	//Used to create the new DashBoard
	private DashboardCreate dashboardCreate ;


	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  oldVar_dashboardCode;
	private transient String  oldVar_dashboardDesc;
	private transient String  oldVar_dashboardType;
	private transient String  oldVar_dashboardCaption;
	private transient String  oldVar_query;
	private transient String  oldVar_subCaption;
	private transient String  oldVar_cbDimension;
	private transient String  oldVar_remarks;
	private transient boolean oldVar_isAdtDataSource;
	private transient boolean oldVar_isMultiSeries;
	private transient boolean oldVar_isDataXML;
	private transient String  oldVar_dataXML;
	private transient boolean oldVar_isDrillDownChart;

	private transient String oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_DashboardConfigurationDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient DashboardConfigurationService dashboardConfigurationService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public DashboardConfigurationDialogCtrl() {
		super();
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
		logger.debug("Entering"+event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("dashboardConfiguration")) {
			this.dashboardConfiguration = (DashboardConfiguration) args.get("dashboardConfiguration");
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
			getUserWorkspace().alocateRoleAuthorities(getRole(), "DashboardConfigurationDialog");
		}
		// READ OVERHANDED parameters !
		// we get the dashboardDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete dashboardDetail here.
		if (args.containsKey("dashboardConfigurationListCtrl")) {
			setDashboardConfigurationListCtrl((DashboardConfigurationListCtrl) args.get("dashboardConfigurationListCtrl"));
		} else {
			setDashboardConfigurationListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDashboardConfiguration());
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
		getUserWorkspace().alocateAuthorities("DashboardConfigurationDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DashboardConfigurationDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_DashboardConfigurationDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doClose();
		logger.debug("Leaving"+event.toString());
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
		//remembering old variables

		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_DashboardConfigurationDialog);
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
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering"+event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering"+event.toString());

		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving"+event.toString());
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

	//Generate the chart based on the query and display it in chart.zul
	public void onClick$btnValidate(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering"+event.toString());
		doSetValidation();
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration(); 
		BeanUtils.copyProperties(getDashboardConfiguration(),aDashboardConfiguration);
		doWriteComponentsToBean(aDashboardConfiguration);
		ChartUtil chartUtil=new ChartUtil();
		try{
			String chartStrXML="";
			if(StringUtils.trimToEmpty(aDashboardConfiguration.getDataXML()).equals("")){
				chartStrXML=getLabelAndValues(aDashboardConfiguration,chartUtil);
			}else{
				chartStrXML=aDashboardConfiguration.getDataXML();
			}
			ChartDetail chartDetail=new ChartDetail();
			chartDetail.setStrXML(chartStrXML);
			chartDetail.setChartHeight("85%");
			chartDetail.setChartWidth("85%");
			chartDetail.setiFrameHeight("100%");
			chartDetail.setiFrameWidth("100%");
			chartDetail.setSwfFile(chartUtil.getSWFFileName(aDashboardConfiguration));
			final HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("chartDetail", chartDetail);
			map.put("dashboardConfigurationDialogCtrl", this);

			/** we can additionally handed over the listBox or the controller self,
			 * so we have in the dialog access to the listBox ListModel. This is
			 * fine for synchronizing the data in the NationalityCodesListbox from
			 * the dialog when we do a delete, edit or insert a Nationality.*/

			map.put("welcomectrl", this);

			// call the ZUL-file with the parameters packed in a map

			Executions.createComponents("/chart.zul", null, map);	 
			logger.debug("Leaving"+event.toString());
		}catch (DataAccessException e) {
			logger.error(e.toString());
			showMessage(e);

		}
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
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if(close){
			closeDialog(this.window_DashboardConfigurationDialog, "DashboardDetail");
		}
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
		doResetInitValues();
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
		if(StringUtils.trimToEmpty(aDashboardConfiguration.getDataXML()).equals("")){
			this.isDataXML.setChecked(false);
			this.oldVar_isDataXML=false;

		}else{
			this.row_dataSource.setVisible(false);
			this.row_queryData.setVisible(false);
			this.row_DataXML.setVisible(true);
			this.oldVar_isDataXML=true;
			this.isDataXML.setChecked(true);
			this.isMultiSeries.setDisabled(true);
		}
		this.isAdtDataSource.setChecked(aDashboardConfiguration.isAdtDataSource());
		this.remarks.setValue(aDashboardConfiguration.getRemarks());
		this.isMultiSeries.setChecked(aDashboardConfiguration.isMultiSeries());
		fillComboBox(this.dashboardType,  aDashboardConfiguration.getDashboardType(),PennantStaticListUtil.getDashBoardType(), "");
		fillComboBox(this.cbDimension,aDashboardConfiguration.getDimension() , PennantStaticListUtil.getChartDimensions(), "");	
		logger.debug("Leaving");
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

			aDashboardDetail.setCaption((this.dashboardCaption.getValue()));

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
			if (StringUtils.trimToEmpty(dashboardType).equalsIgnoreCase("")) {
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
			if (StringUtils.trimToEmpty(this.query.getValue()).equals("") && !this.isDataXML.isChecked()) {
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
			if (!StringUtils.trimToEmpty(this.query.getValue()).equals("")) {
				if (StringUtils.containsIgnoreCase(this.query.getValue().trim(), "delete")
						||StringUtils.containsIgnoreCase(this.query.getValue().trim(), "update")
						||StringUtils.containsIgnoreCase(this.query.getValue().trim(), "alter")
						|| StringUtils.containsIgnoreCase(this.query.getValue().trim(), "truncate")
						|| !StringUtils.startsWithIgnoreCase(this.query.getValue().trim(), "select")) {
					throw new WrongValueException(
							this.query,Labels.getLabel("label_DashboardConfigurationDialog_Query_Alert.value"));
				}
			}

		}catch (WrongValueException we) {
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
			if (StringUtils.trimToEmpty(this.dataXML.getValue()).equals("")  && this.isDataXML.isChecked()) {
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
		aDashboardDetail.setDimension(String.valueOf(this.cbDimension.getSelectedItem().getValue()));
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
		// if aDashboardDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aDashboardConfiguration == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aDashboardConfiguration = getDashboardConfigurationService().getNewDashboardDetail();

			setDashboardConfiguration(aDashboardConfiguration);
		} else {
			setDashboardConfiguration(aDashboardConfiguration);
		}

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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_DashboardConfigurationDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_dashboardCode = this.dashboardCode.getValue();
		this.oldVar_dashboardDesc = this.dashboardDesc.getValue();
		this.oldVar_dashboardType = this.dashboardType.getValue();
		this.oldVar_query = this.query.getValue();
		this.oldVar_dataXML=this.dataXML.getValue();
		this.oldVar_cbDimension=this.cbDimension.getValue();
		this.oldVar_subCaption=this.subCaption.getValue();
		this.oldVar_dashboardCaption=this.dashboardCaption.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_isAdtDataSource=this.isAdtDataSource.isChecked();
		this.oldVar_remarks=this.remarks.getValue();
		this.oldVar_isMultiSeries=this.isMultiSeries.isChecked();
		this.oldVar_isDrillDownChart=this.isDrillDownChart.isChecked();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.dashboardCode.setValue(this.oldVar_dashboardCode);
		this.dashboardDesc.setValue(this.oldVar_dashboardDesc);
		this.dashboardType.setValue(this.oldVar_dashboardType);
		this.dataXML.setValue(oldVar_dataXML);
		this.isDataXML.setChecked(oldVar_isDataXML);
		this.query.setValue(this.oldVar_query);
		this.cbDimension.setValue(this.oldVar_cbDimension);
		this.subCaption.setValue(	this.oldVar_subCaption);
		this.dashboardCaption.setValue(this.oldVar_dashboardCaption);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.isAdtDataSource.setChecked(this.oldVar_isAdtDataSource);
		this.remarks.setValue(this.oldVar_remarks);
		this.isMultiSeries.setChecked(this.oldVar_isMultiSeries);
		this.isDrillDownChart.setChecked(this.oldVar_isDrillDownChart);

		if (isWorkFlowEnabled()) {
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
		doClearMessage();

		if (!this.oldVar_dashboardCode.equals(this.dashboardCode.getValue())) {
			return true;
		}
		if (!this.oldVar_dashboardDesc.equals(this.dashboardDesc.getValue())) {
			return true;
		}
		if (!this.oldVar_dashboardType.equals(this.dashboardType.getValue())){
			return true;
		}

		if (!this.oldVar_query.equals(this.query.getValue())) {
			return true;
		}
		if (!this.oldVar_dataXML.equals(this.dataXML.getValue())) {
			return true;
		}
		if (!this.oldVar_subCaption.equals(this.subCaption.getValue())) {
			return true;
		}
		if (!this.oldVar_cbDimension.equals(this.cbDimension.getValue())) {
			return true;
		}
		if (!this.oldVar_dashboardCaption.equals(this.dashboardCaption.getValue())) {
			return true;
		}
		if (!this.oldVar_remarks.equals(this.remarks.getValue())) {
			return true;
		}
		if (this.oldVar_isMultiSeries!=this.isMultiSeries.isChecked()) {
			return true;
		}
		if (this.oldVar_isAdtDataSource!=this.isAdtDataSource.isChecked()) {
			return true;
		}
		if (this.oldVar_isDataXML!=this.isDataXML.isChecked()) {
			return true;
		}
		if (this.oldVar_isDrillDownChart!=this.isDrillDownChart.isChecked()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
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

		if (!this.dashboardCode.isReadonly()) {
			this.dashboardCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DashboardConfigurationDialog_DashboardCode.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));

		}

		if (!this.dashboardDesc.isReadonly()) {
			this.dashboardDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DashboardConfigurationDialog_DashboardDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.dashboardType.isDisabled()) {
			this.dashboardType.setConstraint(new StaticListValidator(PennantStaticListUtil.getDashBoardType()
					,Labels.getLabel("label_DashboardConfigurationDialog_DashboardType.value")));
		}
		if (!this.dashboardCaption.isDisabled()) {
			this.dashboardCaption.setConstraint(new PTStringValidator(Labels.getLabel("label_DashboardConfigurationDialog_Caption.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
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

	// Method for refreshing the list after successful update
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<DashboardConfiguration> soDashboardDetail = getDashboardConfigurationListCtrl().getSearchObj();
		getDashboardConfigurationListCtrl().pagingDashboardConfigurationList.setActivePage(0);
		getDashboardConfigurationListCtrl().getPagedListWrapper().setSearchObject(soDashboardDetail);
		if (getDashboardConfigurationListCtrl().listBoxDashboardConfiguration != null) {
			getDashboardConfigurationListCtrl().listBoxDashboardConfiguration.getListModel();
		}
		logger.debug("Leaving");
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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aDashboardConfiguration.getRecordType()).equals("")) {
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
					closeDialog(this.window_DashboardConfigurationDialog, "DashboardDetail");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new DashboardDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		/** !!! DO NOT BREAK THE TIERS !!! */
		// remember the old variables
		doStoreInitValues();
		// we don't create a new DashboardDetail() in the frontEnd.
		// we get it from the backEnd.
		final DashboardConfiguration aDashboardConfiguration = getDashboardConfigurationService().getNewDashboardDetail();
		aDashboardConfiguration.setNewRecord(true);
		setDashboardConfiguration(aDashboardConfiguration);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.dashboardCode.focus();
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
			if (StringUtils.trimToEmpty(aDashboardConfiguration.getRecordType()).equals("")) {
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
				closeDialog(this.window_DashboardConfigurationDialog,"DashboardConfiguration");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(DashboardConfiguration aDashboardConfiguration, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDashboardConfiguration.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aDashboardConfiguration.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDashboardConfiguration.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aDashboardConfiguration.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDashboardConfiguration.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aDashboardConfiguration);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aDashboardConfiguration))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aDashboardConfiguration.setTaskId(taskId);
			aDashboardConfiguration.setNextTaskId(nextTaskId);
			aDashboardConfiguration.setRoleCode(getRole());
			aDashboardConfiguration.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDashboardConfiguration, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aDashboardConfiguration);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(
								new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),	null));
						retValue = ErrorControl.showErrorControl(this.window_DashboardConfigurationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_DashboardConfigurationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
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
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_DashboardConfigurationDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering"+event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving"+event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("DashboardDetail");
		notes.setReference(getDashboardConfiguration().getDashboardCode());
		notes.setVersion(getDashboardConfiguration().getVersion());
		return notes;
	}

	//	--------------------------------------------------------------------------------------------------

	public String getLabelAndValues(DashboardConfiguration dashboardConfiguration,ChartUtil chartUtil) throws DataAccessException{
		logger.debug("Entering ");
		String whereCondition =getRoleList();
		List<ChartSetElement> listSetElements=getDashboardConfigurationService()
		.getLabelAndValues(dashboardConfiguration,whereCondition);
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
	public String getRoleList(){
		List<SecurityRole> roles=getUserWorkspace().getUserDetails().getSecurityRole();
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public DashboardCreate getDashboardCreate() {
		return dashboardCreate;
	}
	public void setDashboardCreate(DashboardCreate dashboardCreate) {
		this.dashboardCreate = dashboardCreate;
	}
}
