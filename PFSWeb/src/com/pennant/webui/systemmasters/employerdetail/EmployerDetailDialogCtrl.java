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
 * FileName    		:  EmployerDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.employerdetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.EmployerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PTWebValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/EmployerDetail/employerDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EmployerDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(EmployerDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_EmployerDetailDialog; 
	protected Row 			row0; 
	protected Label 		label_EmpIndustry;
	protected Hlayout 		hlayout_EmpIndustry;
	protected Space 		space_EmpIndustry; 

	protected Textbox 		empIndustry; 
	protected Label 		label_EmpName;
	protected Hlayout 		hlayout_EmpName;
	protected Space 		space_EmpName; 

	protected Textbox 		empName; 
	protected Row 			row1; 
	protected Label 		label_EstablishDate;
	protected Hlayout 		hlayout_EstablishDate;
	protected Space 		space_EstablishDate; 

	protected Datebox 		establishDate; 
	protected Label 		label_EmpAddrHNbr;
	protected Hlayout 		hlayout_EmpAddrHNbr;
	protected Space 		space_EmpAddrHNbr; 

	protected Textbox 		empAddrHNbr; 
	protected Row 			row2; 
	protected Label 		label_EmpFlatNbr;
	protected Hlayout 		hlayout_EmpFlatNbr;
	protected Space 		space_EmpFlatNbr; 

	protected Textbox 		empFlatNbr; 
	protected Label 		label_EmpAddrStreet;
	protected Hlayout 		hlayout_EmpAddrStreet;
	protected Space 		space_EmpAddrStreet; 

	protected Textbox 		empAddrStreet; 
	protected Row 			row3; 
	protected Label 		label_EmpAddrLine1;
	protected Hlayout 		hlayout_EmpAddrLine1;
	protected Space 		space_EmpAddrLine1; 

	protected Textbox 		empAddrLine1; 
	protected Label 		label_EmpAddrLine2;
	protected Hlayout 		hlayout_EmpAddrLine2;
	protected Space 		space_EmpAddrLine2; 

	protected Textbox 		empAddrLine2; 
	protected Row 			row4; 
	protected Label 		label_EmpPOBox;
	protected Hlayout 		hlayout_EmpPOBox;
	protected Space 		space_EmpPOBox; 

	protected Textbox 		empPOBox; 
	protected Label 		label_EmpCountry;
	protected Hlayout 		hlayout_EmpCountry;
	protected Space 		space_EmpCountry; 

	protected Textbox 		empCountry; 
	protected Row 			row5; 
	protected Label 		label_EmpProvince;
	protected Hlayout 		hlayout_EmpProvince;
	protected Space 		space_EmpProvince; 

	protected Textbox 		empProvince; 
	protected Label 		label_EmpCity;
	protected Hlayout 		hlayout_EmpCity;
	protected Space 		space_EmpCity; 

	protected Textbox 		empCity; 
	protected Row 			row6; 
	protected Label 		label_EmpPhone;
	protected Hlayout 		hlayout_EmpPhone;
	protected Space 		space_EmpPhone; 

	protected Textbox 		empPhone; 
	protected Label 		label_EmpFax;
	protected Hlayout 		hlayout_EmpFax;
	protected Space 		space_EmpFax; 

	protected Textbox 		empFax; 
	protected Row 			row7; 
	protected Label 		label_EmpTelexNo;
	protected Hlayout 		hlayout_EmpTelexNo;
	protected Space 		space_EmpTelexNo; 

	protected Textbox 		empTelexNo; 
	protected Label 		label_EmpEmailId;
	protected Hlayout 		hlayout_EmpEmailId;
	protected Space 		space_EmpEmailId; 

	protected Textbox 		empEmailId; 
	protected Row 			row8; 
	protected Label 		label_EmpWebSite;
	protected Hlayout 		hlayout_EmpWebSite;
	protected Space 		space_EmpWebSite; 

	protected Textbox 		empWebSite; 
	protected Label 		label_ContactPersonName;
	protected Hlayout 		hlayout_ContactPersonName;
	protected Space 		space_ContactPersonName; 

	protected Textbox 		contactPersonName; 
	protected Row 			row9; 
	protected Label 		label_ContactPersonNo;
	protected Hlayout 		hlayout_ContactPersonNo;
	protected Space 		space_ContactPersonNo; 

	protected Textbox 		contactPersonNo; 
	protected Label 		label_EmpAlocationType;
	protected Hlayout 		hlayout_EmpAlocationType;
	protected Space 		space_EmpAlocationType; 

	protected Combobox 		empAlocationType; 

	protected Label 		recordStatus; 
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;

	// not auto wired vars
	private EmployerDetail employerDetail; // overhanded per param
	private transient EmployerDetailListCtrl employerDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_EmpIndustry;
	private transient String  		oldVar_EmpName;
	private transient Date  		oldVar_EstablishDate;
	private transient String  		oldVar_EmpAddrHNbr;
	private transient String  		oldVar_EmpFlatNbr;
	private transient String  		oldVar_EmpAddrStreet;
	private transient String  		oldVar_EmpAddrLine1;
	private transient String  		oldVar_EmpAddrLine2;
	private transient String  		oldVar_EmpPOBox;
	private transient String  		oldVar_EmpCountry;
	private transient String  		oldVar_EmpProvince;
	private transient String  		oldVar_EmpCity;
	private transient String  		oldVar_EmpPhone;
	private transient String  		oldVar_EmpFax;
	private transient String  		oldVar_EmpTelexNo;
	private transient String  		oldVar_EmpEmailId;
	private transient String  		oldVar_EmpWebSite;
	private transient String  		oldVar_ContactPersonName;
	private transient String  		oldVar_ContactPersonNo;
	private transient String  		oldVar_EmpAlocationType;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_EmployerDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 
	protected Button btnEdit; 
	protected Button btnDelete; 
	protected Button btnSave; 
	protected Button btnCancel; 
	protected Button btnClose; 
	protected Button btnHelp; 
	protected Button btnNotes; 

	protected Button btnSearchEmpIndustry; 
	protected Textbox lovDescIndustryDesc;
	private transient String 		oldVar_LovDescIndustryDesc;
	protected Button btnSearchEmpCountry; 
	protected Textbox lovDescCountryDesc;
	private transient String 		oldVar_LovDescCountryDesc;
	protected Button btnSearchEmpProvince; 
	protected Textbox lovDescProvinceName;
	private transient String 		oldVar_LovDescProvinceName;
	protected Button btnSearchEmpCity; 
	protected Textbox lovDescCityName;
	private transient String 		oldVar_LovDescCityName;

	// ServiceDAOs / Domain Classes
	private transient EmployerDetailService employerDetailService;

	/**
	 * default constructor.<br>
	 */
	public EmployerDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected EmployerDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EmployerDetailDialog(Event event) throws Exception {
		logger.debug("Entring" +event.toString());
		try {

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}

			// READ OVERHANDED params !
			if (args.containsKey("employerDetail")) {
				this.employerDetail = (EmployerDetail) args.get("employerDetail");
				EmployerDetail befImage =new EmployerDetail();
				BeanUtils.copyProperties(this.employerDetail, befImage);
				this.employerDetail.setBefImage(befImage);

				setEmployerDetail(this.employerDetail);
			} else {
				setEmployerDetail(null);
			}
			doLoadWorkFlow(this.employerDetail.isWorkflow(),this.employerDetail.getWorkflowId(),this.employerDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "EmployerDetailDialog");
			}else{
				getUserWorkspace().alocateAuthorities("EmployerDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the employerDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete employerDetail here.
			if (args.containsKey("employerDetailListCtrl")) {
				setEmployerDetailListCtrl((EmployerDetailListCtrl) args.get("employerDetailListCtrl"));
			} else {
				setEmployerDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getEmployerDetail());
		} catch (Exception e) {
			createException(window_EmployerDetailDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doStoreInitValues();
		displayComponents(ScreenCTL.SCRN_GNEDT);
        this.btnCancel.setVisible(true);	
        logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
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
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doResetInitValues();
		displayComponents(ScreenCTL.SCRN_GNINT);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_EmployerDetailDialog);
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
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_EmployerDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("EmployerDetail",
					String.valueOf(getEmployerDetail().getEmployerId()),
					getEmployerDetail().getVersion()),this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());

	}

	public void onClick$btnSearchEmpIndustry(Event event){

		Object dataObject = ExtendedSearchListBox.show(this.window_EmployerDetailDialog,"Industry");
		if (dataObject instanceof String){
			this.empIndustry.setValue(dataObject.toString());
			this.lovDescIndustryDesc.setValue("");
		}else{
			Industry details= (Industry) dataObject;
			if (details != null) {
				this.empIndustry.setValue(details.getIndustryCode());
				this.lovDescIndustryDesc.setValue(details.getIndustryDesc());
			}
		}
	}
	public void onClick$btnSearchEmpCountry(Event event){
		logger.debug("Entering" + event.toString());
		String sEmpCountry= this.empCountry.getValue();
		Object dataObject = ExtendedSearchListBox.show(this.window_EmployerDetailDialog,"Country");
		if (dataObject instanceof String){
			this.empCountry.setValue(dataObject.toString());
			this.lovDescCountryDesc.setValue("");
		}else{
			Country details= (Country) dataObject;
			if (details != null) {
				this.empCountry.setValue(details.getCountryCode());
				this.lovDescCountryDesc.setValue(details.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sEmpCountry).equals(this.empCountry.getValue())){
			this.empProvince.setValue("");
			this.lovDescProvinceName.setValue("");
			this.empCity.setValue("");
			this.lovDescCityName.setValue("");
			this.btnSearchEmpCity.setVisible(false);
		}

		if(!this.lovDescCountryDesc.getValue().equals("")){
			this.btnSearchEmpProvince.setVisible(true);
		}else{
			this.btnSearchEmpProvince.setVisible(false);
			this.lovDescProvinceName.setValue("");
			this.btnSearchEmpCity.setVisible(false);
			this.lovDescCityName.setValue("");
		}
		logger.debug("Leaving" + event.toString());
	}
	public void onClick$btnSearchEmpProvince(Event event){
		logger.debug("Entering" + event.toString());

		String sEmpProvince= this.empProvince.getValue();
		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("CPCountry", this.empProvince.getValue(), Filter.OP_EQUAL); 

		Object dataObject = ExtendedSearchListBox.show(this.window_EmployerDetailDialog,"Province");
		if (dataObject instanceof String){
			this.empProvince.setValue(dataObject.toString());
			this.lovDescProvinceName.setValue("");
		}else{
			Province details= (Province) dataObject;
			if (details != null) {
				this.empProvince.setValue(details.getCPProvince());
				this.lovDescProvinceName.setValue(details.getCPProvinceName());
			}
		}
		if (!StringUtils.trimToEmpty(sEmpProvince).equals(this.empProvince.getValue())){
			this.empCity.setValue("");
			this.lovDescCityName.setValue("");
		}

		if(!this.lovDescProvinceName.getValue().equals("")){
			this.btnSearchEmpCity.setVisible(true);   
		}
		else{
			this.btnSearchEmpCity.setVisible(false);
			this.lovDescCityName.setValue("");
		}
		logger.debug("Leaving" + event.toString());
	}
	public void onClick$btnSearchEmpCity(Event event){
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("PCProvince", this.empProvince.getValue(), Filter.OP_EQUAL); 
		Object dataObject = ExtendedSearchListBox.show(this.window_EmployerDetailDialog,"City");
		if (dataObject instanceof String){
			this.empCity.setValue(dataObject.toString());
			this.lovDescCityName.setValue("");
		}else{
			City details= (City) dataObject;
			if (details != null) {
				this.empCity.setValue(details.getPCCity());
				this.lovDescCityName.setValue(details.getPCCityName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEmployerDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(EmployerDetail aEmployerDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aEmployerDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aEmployerDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aEmployerDetail = getEmployerDetailService().getNewEmployerDetail();

			setEmployerDetail(aEmployerDetail);
		} else {
			setEmployerDetail(aEmployerDetail);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aEmployerDetail);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aEmployerDetail.isNewRecord()));

			doStoreInitValues();
			checkProvCityButtonVisiblity();
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_EmployerDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode){
		logger.debug("Entering");

		System.out.println();
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, 
				isWorkFlowEnabled(),isFirstTask(), this.userAction,this.empName, this.empAlocationType));

		if (getEmployerDetail().isNewRecord()){
		}

		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;

		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(employerDetail.getRecordType())))) {
			tempReadOnly=true;
		}

		setLovAccess("EmployerDetailDialog_EmpIndustry", tempReadOnly, this.btnSearchEmpIndustry, this.space_EmpIndustry, this.label_EmpIndustry, this.hlayout_EmpIndustry,null);
		setComponentAccessType("EmployerDetailDialog_EmpName", tempReadOnly, this.empName, this.space_EmpName, this.label_EmpName, this.hlayout_EmpName,null);
		setRowInvisible(this.row0, this.hlayout_EmpIndustry,this.hlayout_EmpName);
		setComponentAccessType("EmployerDetailDialog_EstablishDate", tempReadOnly, this.establishDate, this.space_EstablishDate, this.label_EstablishDate, this.hlayout_EstablishDate,null);
		setComponentAccessType("EmployerDetailDialog_EmpAddrHNbr", tempReadOnly, this.empAddrHNbr, this.space_EmpAddrHNbr, this.label_EmpAddrHNbr, this.hlayout_EmpAddrHNbr,null);
		setRowInvisible(this.row1, this.hlayout_EstablishDate,this.hlayout_EmpAddrHNbr);
		setComponentAccessType("EmployerDetailDialog_EmpFlatNbr", tempReadOnly, this.empFlatNbr, this.space_EmpFlatNbr, this.label_EmpFlatNbr, this.hlayout_EmpFlatNbr,null);
		setComponentAccessType("EmployerDetailDialog_EmpAddrStreet", tempReadOnly, this.empAddrStreet, this.space_EmpAddrStreet, this.label_EmpAddrStreet, this.hlayout_EmpAddrStreet,null);
		setRowInvisible(this.row2, this.hlayout_EmpFlatNbr,this.hlayout_EmpAddrStreet);
		setComponentAccessType("EmployerDetailDialog_EmpAddrLine1", tempReadOnly, this.empAddrLine1, this.space_EmpAddrLine1, this.label_EmpAddrLine1, this.hlayout_EmpAddrLine1,null);
		setComponentAccessType("EmployerDetailDialog_EmpAddrLine2", tempReadOnly, this.empAddrLine2, this.space_EmpAddrLine2, this.label_EmpAddrLine2, this.hlayout_EmpAddrLine2,null);
		setRowInvisible(this.row3, this.hlayout_EmpAddrLine1,this.hlayout_EmpAddrLine2);
		setComponentAccessType("EmployerDetailDialog_EmpPOBox", tempReadOnly, this.empPOBox, this.space_EmpPOBox, this.label_EmpPOBox, this.hlayout_EmpPOBox,null);
		setLovAccess("EmployerDetailDialog_EmpCountry", tempReadOnly, this.btnSearchEmpCountry, this.space_EmpCountry, this.label_EmpCountry, this.hlayout_EmpCountry,null);
		setRowInvisible(this.row4, this.hlayout_EmpPOBox,this.hlayout_EmpCountry);
		setLovAccess("EmployerDetailDialog_EmpProvince", tempReadOnly, this.btnSearchEmpProvince, this.space_EmpProvince, this.label_EmpProvince, this.hlayout_EmpProvince,null);
		setLovAccess("EmployerDetailDialog_EmpCity", tempReadOnly, this.btnSearchEmpCity, this.space_EmpCity, this.label_EmpCity, this.hlayout_EmpCity,null);
		setRowInvisible(this.row5, this.hlayout_EmpProvince,this.hlayout_EmpCity);
		setComponentAccessType("EmployerDetailDialog_EmpPhone", tempReadOnly, this.empPhone, this.space_EmpPhone, this.label_EmpPhone, this.hlayout_EmpPhone,null);
		setComponentAccessType("EmployerDetailDialog_EmpFax", tempReadOnly, this.empFax, this.space_EmpFax, this.label_EmpFax, this.hlayout_EmpFax,null);
		setRowInvisible(this.row6, this.hlayout_EmpPhone,this.hlayout_EmpFax);
		setComponentAccessType("EmployerDetailDialog_EmpTelexNo", tempReadOnly, this.empTelexNo, this.space_EmpTelexNo, this.label_EmpTelexNo, this.hlayout_EmpTelexNo,null);
		setComponentAccessType("EmployerDetailDialog_EmpEmailId", tempReadOnly, this.empEmailId, this.space_EmpEmailId, this.label_EmpEmailId, this.hlayout_EmpEmailId,null);
		setRowInvisible(this.row7, this.hlayout_EmpTelexNo,this.hlayout_EmpEmailId);
		setComponentAccessType("EmployerDetailDialog_EmpWebSite", tempReadOnly, this.empWebSite, this.space_EmpWebSite, this.label_EmpWebSite, this.hlayout_EmpWebSite,null);
		setComponentAccessType("EmployerDetailDialog_ContactPersonName", tempReadOnly, this.contactPersonName, this.space_ContactPersonName, this.label_ContactPersonName, this.hlayout_ContactPersonName,null);
		setRowInvisible(this.row8, this.hlayout_EmpWebSite,this.hlayout_ContactPersonName);
		setComponentAccessType("EmployerDetailDialog_ContactPersonNo", tempReadOnly, this.contactPersonNo, this.space_ContactPersonNo, this.label_ContactPersonNo, this.hlayout_ContactPersonNo,null);
		setComponentAccessType("EmployerDetailDialog_EmpAlocationType", tempReadOnly, this.empAlocationType, this.space_EmpAlocationType, this.label_EmpAlocationType, this.hlayout_EmpAlocationType,null);
		setRowInvisible(this.row9, this.hlayout_ContactPersonNo,this.hlayout_EmpAlocationType);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		getUserWorkspace().alocateAuthorities("EmployerDetailDialog");

		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnSave"));	
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		logger.debug("Leaving") ;
	}
	private void checkProvCityButtonVisiblity() {
		if(this.empCountry.getValue() != ""){
			this.btnSearchEmpProvince.setVisible(true);
			if(this.empProvince.getValue() == ""){
			    this.btnSearchEmpCity.setVisible(false);
			   }else{
				this.btnSearchEmpCity.setVisible(true);
		}}else{
			    this.btnSearchEmpProvince.setVisible(false);
			    this.btnSearchEmpCity.setVisible(false);
			}
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.empIndustry.setMaxlength(8);
		this.empName.setMaxlength(50);
		this.establishDate.setFormat(PennantConstants.dateFormat);
		this.empAddrHNbr.setMaxlength(20);
		this.empFlatNbr.setMaxlength(20);
		this.empAddrStreet.setMaxlength(20);
		this.empAddrLine1.setMaxlength(20);
		this.empAddrLine2.setMaxlength(20);
		this.empPOBox.setMaxlength(8);
		this.empCountry.setMaxlength(2);
		this.empProvince.setMaxlength(8);
		this.empCity.setMaxlength(8);
		this.empPhone.setMaxlength(13);
		this.empFax.setMaxlength(13);
		this.empTelexNo.setMaxlength(13);
		this.empEmailId.setMaxlength(100);
		this.empWebSite.setMaxlength(100);
		this.contactPersonName.setMaxlength(20);
		this.contactPersonNo.setMaxlength(13);
		this.btnSearchEmpProvince.setVisible(false);
		this.btnSearchEmpCity.setVisible(false);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_EmpIndustry = this.empIndustry.getValue();
		this.oldVar_LovDescIndustryDesc = this.lovDescIndustryDesc.getValue();
		this.oldVar_EmpName = this.empName.getValue();
		this.oldVar_EstablishDate = this.establishDate.getValue();
		this.oldVar_EmpAddrHNbr = this.empAddrHNbr.getValue();
		this.oldVar_EmpFlatNbr = this.empFlatNbr.getValue();
		this.oldVar_EmpAddrStreet = this.empAddrStreet.getValue();
		this.oldVar_EmpAddrLine1 = this.empAddrLine1.getValue();
		this.oldVar_EmpAddrLine2 = this.empAddrLine2.getValue();
		this.oldVar_EmpPOBox = this.empPOBox.getValue();
		this.oldVar_EmpCountry = this.empCountry.getValue();
		this.oldVar_LovDescCountryDesc = this.lovDescCountryDesc.getValue();
		this.oldVar_EmpProvince = this.empProvince.getValue();
		this.oldVar_LovDescProvinceName = this.lovDescProvinceName.getValue();
		this.oldVar_EmpCity = this.empCity.getValue();
		this.oldVar_LovDescCityName = this.lovDescCityName.getValue();
		this.oldVar_EmpPhone = this.empPhone.getValue();
		this.oldVar_EmpFax = this.empFax.getValue();
		this.oldVar_EmpTelexNo = this.empTelexNo.getValue();
		this.oldVar_EmpEmailId = this.empEmailId.getValue();
		this.oldVar_EmpWebSite = this.empWebSite.getValue();
		this.oldVar_ContactPersonName = this.contactPersonName.getValue();
		this.oldVar_ContactPersonNo = this.contactPersonNo.getValue();
		this.oldVar_EmpAlocationType = PennantConstants.List_Select;
		if(this.empAlocationType.getSelectedItem() !=null){
			this.oldVar_EmpAlocationType=this.empAlocationType.getSelectedItem().getValue().toString();
		}
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.empIndustry.setValue(this.oldVar_EmpIndustry);
		this.lovDescIndustryDesc.setValue(this.oldVar_LovDescIndustryDesc);
		this.empName.setValue(this.oldVar_EmpName);
		this.establishDate.setValue(this.oldVar_EstablishDate);
		this.empAddrHNbr.setValue(this.oldVar_EmpAddrHNbr);
		this.empFlatNbr.setValue(this.oldVar_EmpFlatNbr);
		this.empAddrStreet.setValue(this.oldVar_EmpAddrStreet);
		this.empAddrLine1.setValue(this.oldVar_EmpAddrLine1);
		this.empAddrLine2.setValue(this.oldVar_EmpAddrLine2);
		this.empPOBox.setValue(this.oldVar_EmpPOBox);
		this.empCountry.setValue(this.oldVar_EmpCountry);
		this.lovDescCountryDesc.setValue(this.oldVar_LovDescCountryDesc);
		this.empProvince.setValue(this.oldVar_EmpProvince);
		this.lovDescProvinceName.setValue(this.oldVar_LovDescProvinceName);
		this.empCity.setValue(this.oldVar_EmpCity);
		this.lovDescCityName.setValue(this.oldVar_LovDescCityName);
		this.empPhone.setValue(this.oldVar_EmpPhone);
		this.empFax.setValue(this.oldVar_EmpFax);
		this.empTelexNo.setValue(this.oldVar_EmpTelexNo);
		this.empEmailId.setValue(this.oldVar_EmpEmailId);
		this.empWebSite.setValue(this.oldVar_EmpWebSite);
		this.contactPersonName.setValue(this.oldVar_ContactPersonName);
		this.contactPersonNo.setValue(this.oldVar_ContactPersonNo);
		//fillComboBox(combobox, value, list, excludeFields)(this.empAlocationType,this.oldVar_EmpAlocationType ,listEmpAlocationType, "",false);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEmployerDetail
	 *            EmployerDetail
	 */
	public void doWriteBeanToComponents(EmployerDetail aEmployerDetail) {
		logger.debug("Entering") ;
		this.empIndustry.setValue(aEmployerDetail.getEmpIndustry());
		this.empName.setValue(aEmployerDetail.getEmpName());
		this.establishDate.setValue(aEmployerDetail.getEstablishDate());
		this.empAddrHNbr.setValue(aEmployerDetail.getEmpAddrHNbr());
		this.empFlatNbr.setValue(aEmployerDetail.getEmpFlatNbr());
		this.empAddrStreet.setValue(aEmployerDetail.getEmpAddrStreet());
		this.empAddrLine1.setValue(aEmployerDetail.getEmpAddrLine1());
		this.empAddrLine2.setValue(aEmployerDetail.getEmpAddrLine2());
		this.empPOBox.setValue(aEmployerDetail.getEmpPOBox());
		this.empCountry.setValue(aEmployerDetail.getEmpCountry());
		this.empProvince.setValue(aEmployerDetail.getEmpProvince());
		this.empCity.setValue(aEmployerDetail.getEmpCity());
		this.empPhone.setValue(aEmployerDetail.getEmpPhone());
		this.empFax.setValue(aEmployerDetail.getEmpFax());
		this.empTelexNo.setValue(aEmployerDetail.getEmpTelexNo());
		this.empEmailId.setValue(aEmployerDetail.getEmpEmailId());
		this.empWebSite.setValue(aEmployerDetail.getEmpWebSite());
		this.contactPersonName.setValue(aEmployerDetail.getContactPersonName());
		this.contactPersonNo.setValue(aEmployerDetail.getContactPersonNo());
		fillComboBox(this.empAlocationType, aEmployerDetail.getEmpAlocationType(), PennantStaticListUtil.getEmpAlocList(),"");

		if (aEmployerDetail.isNewRecord()){
			this.lovDescIndustryDesc.setValue("");
			this.lovDescCountryDesc.setValue("");
			this.lovDescProvinceName.setValue("");
			this.lovDescCityName.setValue("");
		}else{
			this.lovDescIndustryDesc.setValue(aEmployerDetail.getEmpIndustry()+"-"+aEmployerDetail.getLovDescIndustryDesc());
			this.lovDescCountryDesc.setValue(aEmployerDetail.getEmpCountry()+"-"+aEmployerDetail.getLovDescCountryDesc());
			this.lovDescProvinceName.setValue(aEmployerDetail.getEmpProvince()+"-"+aEmployerDetail.getLovDescProvinceName());
			this.lovDescCityName.setValue(aEmployerDetail.getEmpCity()+"-"+aEmployerDetail.getLovDescCityName());
		}
		this.recordStatus.setValue(aEmployerDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEmployerDetail
	 */
	public void doWriteComponentsToBean(EmployerDetail aEmployerDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Emp Industry
		try {
			aEmployerDetail.setLovDescIndustryDesc(this.lovDescIndustryDesc.getValue());
			aEmployerDetail.setEmpIndustry(this.empIndustry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Name
		try {
			aEmployerDetail.setEmpName(this.empName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Establish Date
		try {
			if (!this.establishDate.getValue().after(((Date) SystemParameterDetails
					.getSystemParameterValue("APP_DFT_START_DATE")))) {
				throw new WrongValueException(this.establishDate,Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[] {Labels.getLabel("label_EmployerDetailDialog_EstablishDate.value"),
						SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
			}
			aEmployerDetail.setEstablishDate(new Timestamp(this.establishDate.getValue().getTime()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr H Nbr
		try {
			aEmployerDetail.setEmpAddrHNbr(this.empAddrHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Flat Nbr
		try {
			aEmployerDetail.setEmpFlatNbr(this.empFlatNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr Street
		try {
			aEmployerDetail.setEmpAddrStreet(this.empAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr Line1
		try {
			aEmployerDetail.setEmpAddrLine1(this.empAddrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr Line2
		try {
			aEmployerDetail.setEmpAddrLine2(this.empAddrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp P O Box
		try {
			aEmployerDetail.setEmpPOBox(this.empPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Country
		try {
			aEmployerDetail.setLovDescCountryDesc(this.lovDescCountryDesc.getValue());
			aEmployerDetail.setEmpCountry(this.empCountry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Province
		try {
			aEmployerDetail.setLovDescProvinceName(this.lovDescProvinceName.getValue());
			aEmployerDetail.setEmpProvince(this.empProvince.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp City
		try {
			aEmployerDetail.setLovDescCityName(this.lovDescCityName.getValue());
			aEmployerDetail.setEmpCity(this.empCity.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Phone
		try {
			aEmployerDetail.setEmpPhone(this.empPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Fax
		try {
			aEmployerDetail.setEmpFax(this.empFax.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Telex No
		try {
			aEmployerDetail.setEmpTelexNo(this.empTelexNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Email Id
		try {
			aEmployerDetail.setEmpEmailId(this.empEmailId.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Web Site
		try {
			aEmployerDetail.setEmpWebSite(this.empWebSite.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Contact Person Name
		try {
			aEmployerDetail.setContactPersonName(this.contactPersonName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Contact Person No
		try {
			aEmployerDetail.setContactPersonNo(this.contactPersonNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Alocation Type
		try {
			String strEmpAlocationType =null; 
			if(this.empAlocationType.getSelectedItem()!=null){
				strEmpAlocationType = this.empAlocationType.getSelectedItem().getValue().toString();
			}
			if(strEmpAlocationType!= null && !PennantConstants.List_Select.equals(strEmpAlocationType)){
				aEmployerDetail.setEmpAlocationType(strEmpAlocationType);	
			}else{
				aEmployerDetail.setEmpAlocationType(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
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

		if (!StringUtils.trimToEmpty(this.oldVar_EmpIndustry).equals(StringUtils.trimToEmpty(this.empIndustry.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpName).equals(StringUtils.trimToEmpty(this.empName.getValue()))) {
			return true;
		}
		String oldEstablishDate = "";
		String newEstablishDate ="";
		if (this.oldVar_EstablishDate!=null){
			oldEstablishDate=DateUtility.formatDate(this.oldVar_EstablishDate,PennantConstants.dateFormat);
		}
		if (this.establishDate.getValue()!=null){
			newEstablishDate=DateUtility.formatDate(this.establishDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldEstablishDate).equals(StringUtils.trimToEmpty(newEstablishDate))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpAddrHNbr).equals(StringUtils.trimToEmpty(this.empAddrHNbr.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpFlatNbr).equals(StringUtils.trimToEmpty(this.empFlatNbr.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpAddrStreet).equals(StringUtils.trimToEmpty(this.empAddrStreet.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpAddrLine1).equals(StringUtils.trimToEmpty(this.empAddrLine1.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpAddrLine2).equals(StringUtils.trimToEmpty(this.empAddrLine2.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpPOBox).equals(StringUtils.trimToEmpty(this.empPOBox.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpCountry).equals(StringUtils.trimToEmpty(this.empCountry.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpProvince).equals(StringUtils.trimToEmpty(this.empProvince.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpCity).equals(StringUtils.trimToEmpty(this.empCity.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpPhone).equals(StringUtils.trimToEmpty(this.empPhone.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpFax).equals(StringUtils.trimToEmpty(this.empFax.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpTelexNo).equals(StringUtils.trimToEmpty(this.empTelexNo.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpEmailId).equals(StringUtils.trimToEmpty(this.empEmailId.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpWebSite).equals(StringUtils.trimToEmpty(this.empWebSite.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_ContactPersonName).equals(StringUtils.trimToEmpty(this.contactPersonName.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_ContactPersonNo).equals(StringUtils.trimToEmpty(this.contactPersonNo.getValue()))) {
			return true;
		}
		String strEmpAlocationType =PennantConstants.List_Select;
		if(this.empAlocationType.getSelectedItem()!=null){
			strEmpAlocationType = this.empAlocationType.getSelectedItem().getValue().toString();	
		}

		if (!StringUtils.trimToEmpty(this.oldVar_EmpAlocationType).equals(strEmpAlocationType)) {
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
		//Emp Name
		if (!this.empName.isReadonly()){
			this.empName.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		//Establish Date
		if (!this.establishDate.isReadonly()) {
			this.establishDate.setConstraint(new PTDateValidator(Labels.getLabel("label_EmployerDetailDialog_EstablishDate.value"), true, null, new Date(), false));
		}
		boolean addressConstraint = false;
		if (StringUtils.trimToEmpty(this.empAddrHNbr.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.empFlatNbr.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.empAddrStreet.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.empAddrLine1.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.empAddrLine2.getValue()).equals("")) {
			addressConstraint = true;
		}
		//Emp Addr H Nbr
		if (addressConstraint) {
			this.empAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrHNbr.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Flat Nbr
		if (addressConstraint) {
			this.empFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpFlatNbr.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Addr Street
		if (addressConstraint) {
			this.empAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrStreet.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Addr Line1
		if (addressConstraint) {
			this.empAddrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrLine1.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Addr Line2
		if (addressConstraint) {
			this.empAddrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrLine2.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp P O Box
		if (!this.empPOBox.isReadonly()) {
			this.empPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		//Emp Phone
		if (!this.empPhone.isReadonly()) {
			this.empPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_EmpPhone.value"),true));
		}
		//Emp Fax
		if (!this.empFax.isReadonly()) {
			this.empFax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_EmpFax.value"),true));
		}
		//Emp Telex No
		if (!this.empTelexNo.isReadonly()) {
			this.empTelexNo.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_EmpTelexNo.value"),true));
		}
		//Emp Email Id MAND_FIELD_MAIL
		if (!this.empEmailId.isReadonly()){
			this.empEmailId.setConstraint(new PTEmailValidator(Labels.getLabel("label_EmployerDetailDialog_EmpEmailId.value"),true));
		}
		//Emp Web Site
		if (!this.empWebSite.isReadonly()){
			this.empWebSite.setConstraint(new PTWebValidator(Labels.getLabel("label_EmployerDetailDialog_EmpWebSite.value"), true));
		}
		//Contact Person Name
		if (!this.contactPersonName.isReadonly()){
			this.contactPersonName.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_ContactPersonName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		//Contact Person No
		if (!this.contactPersonNo.isReadonly()) {
			this.contactPersonNo.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_ContactPersonNo.value"),true));
		}
		//Emp Alocation Type
		if (!this.empAlocationType.isReadonly()){
			this.empAlocationType.setConstraint(new StaticListValidator(PennantStaticListUtil.getEmpAlocList(),
					Labels.getLabel("label_EmployerDetailDialog_EmpAlocationType.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.empName.setConstraint("");
		this.establishDate.setConstraint("");
		this.empAddrHNbr.setConstraint("");
		this.empFlatNbr.setConstraint("");
		this.empAddrStreet.setConstraint("");
		this.empAddrLine1.setConstraint("");
		this.empAddrLine2.setConstraint("");
		this.empPOBox.setConstraint("");
		this.empPhone.setConstraint("");
		this.empFax.setConstraint("");
		this.empTelexNo.setConstraint("");
		this.empEmailId.setConstraint("");
		this.empWebSite.setConstraint("");
		this.contactPersonName.setConstraint("");
		this.contactPersonNo.setConstraint("");
		this.empAlocationType.setConstraint("");
		logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		//Emp Industry
		if(btnSearchEmpIndustry.isVisible()){
			this.lovDescIndustryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpIndustry.value"), null, true));
		}
		//Emp Country
		if(btnSearchEmpCountry.isVisible()){
			this.lovDescCountryDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpCountry.value"), null, true));
		}
		//Emp Province
		if(btnSearchEmpProvince.isVisible()){
			this.lovDescProvinceName.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpProvince.value"), null, true));
		}
		//Emp City
		if(btnSearchEmpCity.isVisible()){
			this.lovDescCityName.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpCity.value"), null, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.lovDescIndustryDesc.setConstraint("");
		this.lovDescCountryDesc.setConstraint("");
		this.lovDescProvinceName.setConstraint("");
		this.lovDescCityName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.lovDescIndustryDesc.setErrorMessage("");
		this.empName.setErrorMessage("");
		this.establishDate.setErrorMessage("");
		this.empAddrHNbr.setErrorMessage("");
		this.empFlatNbr.setErrorMessage("");
		this.empAddrStreet.setErrorMessage("");
		this.empAddrLine1.setErrorMessage("");
		this.empAddrLine2.setErrorMessage("");
		this.empPOBox.setErrorMessage("");
		this.lovDescCountryDesc.setErrorMessage("");
		this.lovDescProvinceName.setErrorMessage("");
		this.lovDescCityName.setErrorMessage("");
		this.empPhone.setErrorMessage("");
		this.empFax.setErrorMessage("");
		this.empTelexNo.setErrorMessage("");
		this.empEmailId.setErrorMessage("");
		this.empWebSite.setErrorMessage("");
		this.contactPersonName.setErrorMessage("");
		this.contactPersonNo.setErrorMessage("");
		this.empAlocationType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList(){
		final JdbcSearchObject<EmployerDetail> soEmployerDetail = getEmployerDetailListCtrl().getSearchObj();
		getEmployerDetailListCtrl().pagingEmployerDetailList.setActivePage(0);
		getEmployerDetailListCtrl().getPagedListWrapper().setSearchObject(soEmployerDetail);
		if(getEmployerDetailListCtrl().listBoxEmployerDetail!=null){
			getEmployerDetailListCtrl().listBoxEmployerDetail.getListModel();
		}
	} 


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
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
		boolean close=true;
		if (!enqModule && isDataChanged()) {
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
			closeDialog(this.window_EmployerDetailDialog, "EmployerDetail");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Deletes a EmployerDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final EmployerDetail aEmployerDetail = new EmployerDetail();
		BeanUtils.copyProperties(getEmployerDetail(), aEmployerDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aEmployerDetail.getEmployerId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aEmployerDetail.getRecordType()).equals("")){
				aEmployerDetail.setVersion(aEmployerDetail.getVersion()+1);
				aEmployerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aEmployerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aEmployerDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEmployerDetail.getNextTaskId(), aEmployerDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aEmployerDetail,tranType)){
					refreshList();
					closeDialog(this.window_EmployerDetailDialog, "EmployerDetail"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_EmployerDetailDialog,e);
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

		this.empIndustry.setValue("");
		this.lovDescIndustryDesc.setValue("");
		this.empName.setValue("");
		this.establishDate.setText("");
		this.empAddrHNbr.setValue("");
		this.empFlatNbr.setValue("");
		this.empAddrStreet.setValue("");
		this.empAddrLine1.setValue("");
		this.empAddrLine2.setValue("");
		this.empPOBox.setValue("");
		this.empCountry.setValue("");
		this.lovDescCountryDesc.setValue("");
		this.empProvince.setValue("");
		this.lovDescProvinceName.setValue("");
		this.empCity.setValue("");
		this.lovDescCityName.setValue("");
		this.empPhone.setValue("");
		this.empFax.setValue("");
		this.empTelexNo.setValue("");
		this.empEmailId.setValue("");
		this.empWebSite.setValue("");
		this.contactPersonName.setValue("");
		this.contactPersonNo.setValue("");
		this.empAlocationType.setSelectedIndex(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final EmployerDetail aEmployerDetail = new EmployerDetail();
		BeanUtils.copyProperties(getEmployerDetail(), aEmployerDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aEmployerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEmployerDetail.getNextTaskId(), aEmployerDetail);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aEmployerDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the EmployerDetail object with the components data
			doWriteComponentsToBean(aEmployerDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aEmployerDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aEmployerDetail.getRecordType()).equals("")){
				aEmployerDetail.setVersion(aEmployerDetail.getVersion()+1);
				if(isNew){
					aEmployerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aEmployerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEmployerDetail.setNewRecord(true);
				}
			}
		}else{
			aEmployerDetail.setVersion(aEmployerDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aEmployerDetail,tranType)){
				//doWriteBeanToComponents(aEmployerDetail);
				refreshList();
				closeDialog(this.window_EmployerDetailDialog, "EmployerDetail");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_EmployerDetailDialog,e);
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

	private boolean doProcess(EmployerDetail aEmployerDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aEmployerDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aEmployerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEmployerDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
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

			aEmployerDetail.setTaskId(getTaskId());
			aEmployerDetail.setNextTaskId(getNextTaskId());
			aEmployerDetail.setRoleCode(getRole());
			aEmployerDetail.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aEmployerDetail, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aEmployerDetail, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aEmployerDetail, tranType), null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		EmployerDetail aEmployerDetail = (EmployerDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getEmployerDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getEmployerDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getEmployerDetailService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aEmployerDetail.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getEmployerDetailService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aEmployerDetail.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_EmployerDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_EmployerDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("EmployerDetail",aEmployerDetail.getEmpName(),aEmployerDetail.getVersion()),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
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

	private AuditHeader getAuditHeader(EmployerDetail aEmployerDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEmployerDetail.getBefImage(), aEmployerDetail);   
		return new AuditHeader(String.valueOf(aEmployerDetail.getEmployerId()),null,null,null,auditDetail,aEmployerDetail.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public EmployerDetail getEmployerDetail() {
		return this.employerDetail;
	}

	public void setEmployerDetail(EmployerDetail employerDetail) {
		this.employerDetail = employerDetail;
	}
	public void setEmployerDetailService(EmployerDetailService employerDetailService) {
		this.employerDetailService = employerDetailService;
	}

	public EmployerDetailService getEmployerDetailService() {
		return this.employerDetailService;
	}

	public void setEmployerDetailListCtrl(EmployerDetailListCtrl employerDetailListCtrl) {
		this.employerDetailListCtrl = employerDetailListCtrl;
	}
	public EmployerDetailListCtrl getEmployerDetailListCtrl() {
		return this.employerDetailListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

}
