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
 * FileName    		:  CarLoanDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.carloandetail;

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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceMainDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/CarLoanDetail/carLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CarLoanDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5058430665774376406L;
	private final static Logger logger = Logger.getLogger(CarLoanDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CarLoanDetailDialog;	// autowired
	protected Textbox 		loanRefNumber; 				// autowired
	protected Checkbox 		loanRefType; 				// autowired
	protected Longbox 		carLoanFor; 				// autowired
	protected Longbox 		carUsage; 					// autowired
	protected Longbox 		carManufacturer; 			//autowired
	protected Longbox 		carModel;					//autowired
	protected Longbox 		carVersion; 				// autowired
	protected Intbox 		carMakeYear; 				// autowired
	protected Intbox 		carCapacity; 				// autowired
	protected Longbox 		carDealer; 					// autowired
	protected Intbox 		carCc; 						// autowired
	protected Textbox 		carChasisNo; 				// autowired
	protected Textbox 		carInsuranceNo; 			// autowired
	protected Textbox 		carRegNo; 					// autowired
	protected Combobox 		cbCarColor; 				// autowired
	protected Caption		caption_carLoan;
	
	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired vars
	private CarLoanDetail carLoanDetail; 							// overhanded per param
	private transient CarLoanDetailListCtrl carLoanDetailListCtrl; 	// overhanded per param
	
	//For Dynamically calling of this Controller
	private Div toolbar;
	private FinanceMainDialogCtrl financeMainDialogCtrl;
	private Tabpanel panel = null;
	private Grid grid_carLoanDetails;
	
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  	oldVar_loanRefNumber;
	private transient boolean  	oldVar_loanRefType;
	private transient long  	oldVar_carLoanFor;
	private transient long  	oldVar_carUsage;
	private transient long  	oldVar_carVersion;
	private transient int  		oldVar_carMakeYear;
	private transient int  		oldVar_carCapacity;
	private transient long  	oldVar_carDealer;
	private transient int  		oldVar_carCc;
	private transient String  	oldVar_carChasisNo;
	private transient String  	oldVar_carInsuranceNo;
	private transient String  	oldVar_carRegNo;
	private transient int  	    oldVar_carColor;
	private transient String    oldVar_recordStatus;
	private transient String 	oldVar_lovDescCarLoanForName;
	private transient String 	oldVar_lovDescCarUsageName;
	private transient String 	oldVar_lovDescCarVersionName;
	private transient String 	oldVar_lovDescCarDealerName;

	private transient boolean validationOn;
	private boolean notes_Entered=false;
	private transient boolean   newFinance;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CarLoanDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	protected Button btnSearchCarLoanFor; 		// autowire
	protected Button btnSearchCarUsage; 		// autowire
	protected Button btnSearchCarManufacturer; 	// autowire
	protected Button btnSearchCarModel; 		// autowire
	protected Button btnSearchCarVersion; 		// autowire
	protected Button btnSearchCarDealer; 		// autowire

	protected Textbox lovDescCarLoanForName;
	protected Textbox lovDescCarUsageName;
	protected Textbox lovDescCarManufacturerName;
	protected Textbox lovDescCarModelName;
	protected Textbox lovDescCarVersionName;
	protected Textbox lovDescCarDealerName;

	// ServiceDAOs / Domain Classes
	private transient CarLoanDetailService carLoanDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	static final List<ValueLabel>	      carColors	      = PennantAppUtil.getCarColors();

	private transient boolean recSave;
	/**
	 * default constructor.<br>
	 */
	public CarLoanDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CarLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CarLoanDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		if(event.getTarget().getParent() != null){
			panel = (Tabpanel) event.getTarget().getParent();
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, 
				this.btnCancel, this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("carLoanDetail")) {
			this.carLoanDetail = (CarLoanDetail) args.get("carLoanDetail");
			CarLoanDetail befImage =new CarLoanDetail();
			BeanUtils.copyProperties(this.carLoanDetail, befImage);
			this.carLoanDetail.setBefImage(befImage);
			setCarLoanDetail(this.carLoanDetail);
		} else {
			setCarLoanDetail(null);
		}

		if(args.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (FinanceMainDialogCtrl) args.get("financeMainDialogCtrl");
			setNewFinance(true);
			this.carLoanDetail.setWorkflowId(0);
			this.window_CarLoanDetailDialog.setTitle("");
			this.caption_carLoan.setVisible(true);
		}
		if(args.containsKey("roleCode")){
			getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "CarLoanDetailDialog");
		}
		
		doLoadWorkFlow(this.carLoanDetail.isWorkflow(),this.carLoanDetail.getWorkflowId(),
				this.carLoanDetail.getNextTaskId());
		
		if (isWorkFlowEnabled() && !isNewFinance()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CarLoanDetailDialog");
		}

		// READ OVERHANDED params !
		// we get the carLoanDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete carLoanDetail here.
		if (args.containsKey("carLoanDetailListCtrl")) {
			setCarLoanDetailListCtrl((CarLoanDetailListCtrl) args.get("carLoanDetailListCtrl"));
		} else {
			setCarLoanDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCarLoanDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.carLoanFor.setMaxlength(19);
		this.carUsage.setMaxlength(19);
		this.carVersion.setMaxlength(19);
		this.carMakeYear.setMaxlength(4);
		this.carCapacity.setMaxlength(2);
		this.carDealer.setMaxlength(19);
		this.carCc.setMaxlength(5);
		this.carChasisNo.setMaxlength(19);
		this.carInsuranceNo.setMaxlength(19);
		this.carRegNo.setMaxlength(19);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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

		getUserWorkspace().alocateAuthorities("CarLoanDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnSave"));
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
	public void onClose$window_CarLoanDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CarLoanDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Event for checking validation for dynamically calling condition
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onAssetValidation(Event event){
		logger.debug("Entering" + event.toString());
		
		String userAction = "";
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}
		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}
		doClearMessage();
		if("Submit".equalsIgnoreCase(userAction)){
			//doSetValidation();
			//doSetLOVValidation();
		}else{
			recSave = true;
		}
		doWriteComponentsToBean(getCarLoanDetail());
		if(StringUtils.trimToEmpty(getCarLoanDetail().getRecordType()).equals("")){
			getCarLoanDetail().setVersion(getCarLoanDetail().getVersion() + 1);
			getCarLoanDetail().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getCarLoanDetail().setNewRecord(true);
		}
		this.financeMainDialogCtrl.getFinanceDetail().setCarLoanDetail(getCarLoanDetail());
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Event for checking whethter data has been changed before closing
	 * @param event
	 * @return 
	 * */
	public void onAssetClose(Event event){
		logger.debug("Entering" + event.toString());
		this.financeMainDialogCtrl.setAssetDataChanged(isDataChanged());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_CarLoanDetailDialog, "CarLoanDetail");	
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
	 * @param aCarLoanDetail
	 *            CarLoanDetail
	 */
	public void doWriteBeanToComponents(CarLoanDetail aCarLoanDetail) {
		logger.debug("Entering") ;
		this.loanRefNumber.setValue(aCarLoanDetail.getLoanRefNumber());
		this.loanRefType.setChecked(aCarLoanDetail.isLoanRefType());
		this.carLoanFor.setValue(aCarLoanDetail.getCarLoanFor());
		this.carUsage.setValue(aCarLoanDetail.getCarUsage());
		this.carManufacturer.setValue(aCarLoanDetail.getLovDescManufacturerId());
		this.carModel.setValue(aCarLoanDetail.getLovDescVehicleModelId());
		this.carVersion.setValue (aCarLoanDetail.getCarVersion());
		this.carMakeYear.setValue(aCarLoanDetail.getCarMakeYear());
		this.carCapacity.setValue(aCarLoanDetail.getCarCapacity());
		this.carDealer.setValue(aCarLoanDetail.getCarDealer());
		this.carCc.setValue(aCarLoanDetail.getCarCc());
		this.carChasisNo.setValue(aCarLoanDetail.getCarChasisNo());
		this.carInsuranceNo.setValue(aCarLoanDetail.getCarInsuranceNo());
		this.carRegNo.setValue(aCarLoanDetail.getCarRegNo());
		fillComboBox(this.cbCarColor, aCarLoanDetail.getCarColor(), carColors,"");

		if (aCarLoanDetail.isNewRecord()){
			this.lovDescCarLoanForName.setValue("");
			this.lovDescCarUsageName.setValue("");
			this.lovDescCarManufacturerName.setValue("");
			this.lovDescCarModelName.setValue("");
			this.lovDescCarVersionName.setValue("");
			this.lovDescCarDealerName.setValue("");
			this.lovDescCarDealerName.setValue("");
		}else{
			this.lovDescCarLoanForName.setValue(aCarLoanDetail.getLovDescLoanForValue());
			this.lovDescCarUsageName.setValue(aCarLoanDetail.getLovDescCarUsageValue());
			this.lovDescCarManufacturerName.setValue(aCarLoanDetail.getLovDescManufacturerName());
			this.lovDescCarModelName.setValue(aCarLoanDetail.getLovDescModelDesc());
			this.lovDescCarVersionName.setValue(aCarLoanDetail.getLovDescVehicleVersionCode());
			this.lovDescCarDealerName.setValue(aCarLoanDetail.getLovDescCarDealerName());
		}
		this.recordStatus.setValue(aCarLoanDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCarLoanDetail
	 */
	public void doWriteComponentsToBean(CarLoanDetail aCarLoanDetail) {
		logger.debug("Entering") ;

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCarLoanDetail.setLoanRefNumber(this.loanRefNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLoanRefType(this.loanRefType.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarLoanForName(this.lovDescCarLoanForName.getValue());
			aCarLoanDetail.setCarLoanFor(this.carLoanFor.longValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarUsageName(this.lovDescCarUsageName.getValue());
			aCarLoanDetail.setCarUsage(this.carUsage.longValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescManufacturerName(this.lovDescCarManufacturerName.getValue());
			aCarLoanDetail.setLovDescManufacturerId(this.carManufacturer.longValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescModelDesc(this.lovDescCarModelName.getValue());
			aCarLoanDetail.setLovDescVehicleModelId(this.carModel.longValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarVersionName(this.lovDescCarVersionName.getValue());
			aCarLoanDetail.setCarVersion(this.carVersion.longValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if (!recSave && this.carMakeYear.intValue() != 0 && (this.carMakeYear.intValue() <	DateUtility.getYear((Date)
							SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE"))) ||
					(this.carMakeYear.intValue() > DateUtility.getYear(new Date()))) {				
				throw new WrongValueException(this.carMakeYear, Labels.getLabel("DATE_RANGE", new String[] {
						Labels.getLabel("label_CarLoanDetailDialog_CarMakeYear.value"),
						SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString(),
						String.valueOf(DateUtility.today())}));
			}
			aCarLoanDetail.setCarMakeYear(this.carMakeYear.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarCapacity(this.carCapacity.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarDealerName(this.lovDescCarDealerName.getValue());
			aCarLoanDetail.setCarDealer(this.carDealer.longValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarCc(this.carCc.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarInsuranceNo(this.carInsuranceNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarRegNo(this.carRegNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarChasisNo(this.carChasisNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			this.cbCarColor.clearErrorMessage();
			/*if (!recSave && getCbSlctVal(this.cbCarColor).equals("#")) {
				throw new WrongValueException(this.cbCarColor, Labels.getLabel("STATIC_INVALID",// FIXME After Demo
				        new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarColor.value") }));
			}*/
			aCarLoanDetail.setCarColor(getCbSlctVal(this.cbCarColor));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			if(panel != null){
				((Tab)panel.getParent().getParent().getFellowIfAny("loanAssetTab")).setSelected(true);
			}
			throw new WrongValuesException(wvea);
		}

		aCarLoanDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCarLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CarLoanDetail aCarLoanDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCarLoanDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCarLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCarLoanDetail = getCarLoanDetailService().getNewCarLoanDetail();
			setCarLoanDetail(aCarLoanDetail);
		} else {
			setCarLoanDetail(aCarLoanDetail);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCarLoanDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.carLoanFor.focus();
		} else {
			this.carLoanFor.focus();
			if(isNewFinance()){
				doEdit();
			}else if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aCarLoanDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			
			if(panel != null){
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.statusRow.setVisible(false);
				this.window_CarLoanDetailDialog.setHeight(grid_carLoanDetails.getRows().getVisibleItemCount()*20+100+"px");
				//panel.setHeight(grid_carLoanDetails.getRows().getVisibleItemCount()*20+160+"px");
				panel.appendChild(this.window_CarLoanDetailDialog);
			}else{
				setDialog(this.window_CarLoanDetailDialog);
			}
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
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_loanRefNumber = this.loanRefNumber.getValue();
		this.oldVar_loanRefType = this.loanRefType.isChecked();
		this.oldVar_carLoanFor = this.carLoanFor.longValue();
		this.oldVar_lovDescCarLoanForName = this.lovDescCarLoanForName.getValue();
		this.oldVar_carUsage = this.carUsage.longValue();
		this.oldVar_lovDescCarUsageName = this.lovDescCarUsageName.getValue();
		this.oldVar_carVersion = this.carVersion.longValue();
		this.oldVar_lovDescCarVersionName = this.lovDescCarVersionName.getValue();
		this.oldVar_carMakeYear = this.carMakeYear.intValue();	
		this.oldVar_carCapacity = this.carCapacity.intValue();	
		this.oldVar_carDealer = this.carDealer.longValue();
		this.oldVar_lovDescCarDealerName = this.lovDescCarDealerName.getValue();
		this.oldVar_carCc = this.carCc.getValue();
		this.oldVar_carInsuranceNo = this.carInsuranceNo.getValue();
		this.oldVar_carRegNo = this.carRegNo.getValue();
		this.oldVar_carChasisNo = this.carChasisNo.getValue();
		this.oldVar_carColor = this.cbCarColor.getSelectedIndex();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_loanRefNumber);
		this.loanRefType.setChecked(this.oldVar_loanRefType);
		this.carLoanFor.setValue(this.oldVar_carLoanFor);
		this.lovDescCarLoanForName.setValue(this.oldVar_lovDescCarLoanForName);
		this.carUsage.setValue(this.oldVar_carUsage);
		this.lovDescCarUsageName.setValue(this.oldVar_lovDescCarUsageName);
		this.carVersion.setValue(this.oldVar_carVersion);
		this.lovDescCarVersionName.setValue(this.oldVar_lovDescCarVersionName);
		this.carMakeYear.setValue(this.oldVar_carMakeYear);
		this.carCapacity.setValue(this.oldVar_carCapacity);
		this.carDealer.setValue(this.oldVar_carDealer);
		this.lovDescCarDealerName.setValue(this.oldVar_lovDescCarDealerName);
		this.carCc.setValue(this.oldVar_carCc);
		this.carInsuranceNo.setValue(this.oldVar_carInsuranceNo);
		this.carRegNo.setValue(this.oldVar_carRegNo);
		this.carChasisNo.setValue(this.oldVar_carChasisNo);
		this.cbCarColor.setSelectedIndex(this.oldVar_carColor);
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

		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_loanRefNumber != this.loanRefNumber.getValue()) {
			return true;
		}
		if (this.oldVar_loanRefType != this.loanRefType.isChecked()) {
			return true;
		}
		if (this.oldVar_carLoanFor != this.carLoanFor.longValue()) {
			return true;
		}
		if (this.oldVar_carUsage != this.carUsage.longValue()) {
			return true;
		}
		if (this.oldVar_carVersion != this.carVersion.longValue()) {
			return true;
		}
		if (this.oldVar_carMakeYear != this.carMakeYear.intValue()) {
			return  true;
		}
		if (this.oldVar_carCapacity != this.carCapacity.intValue()) {
			return  true;
		}
		if (this.oldVar_carDealer != this.carDealer.longValue()) {
			return true;
		}
		if (this.oldVar_carCc != this.carCc.intValue()) {
			return true;
		}
		if (this.oldVar_carInsuranceNo != this.carInsuranceNo.getValue()) {
			return true;
		}
		if (this.oldVar_carRegNo != this.carRegNo.getValue()) {
			return true;
		}
		if (this.oldVar_carChasisNo != this.carChasisNo.getValue()) {
			return true;
		}
		if (this.oldVar_carColor != this.cbCarColor.getSelectedIndex()) {
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.carMakeYear.isReadonly()){
			this.carMakeYear.setConstraint("NO EMPTY:"+ Labels.getLabel(
					"FIELD_NO_EMPTY",new String[] { Labels.getLabel(
							"label_CarLoanDetailDialog_CarMakeYear.value") }));
		}	
		if (!this.carCapacity.isReadonly()){
			this.carCapacity.setConstraint("NO ZERO, NO NEGATIVE, NO EMPTY:" + 
					Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
							Labels.getLabel("label_CarLoanDetailDialog_CarCapacity.value")
							}));
		}
		if (!this.carCc.isReadonly()){
			this.carCc.setConstraint("NO ZERO, NO NEGATIVE, NO EMPTY:" + 
					Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] {
							Labels.getLabel("label_CarLoanDetailDialog_CarCc.value")}));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.carMakeYear.setConstraint("");
		this.carCapacity.setConstraint("");
		this.carCc.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for set constraints of LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescCarLoanForName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CarLoanDetailDialog_CarLoanFor.value")}));
		this.lovDescCarUsageName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CarLoanDetailDialog_CarUsage.value")}));
		this.lovDescCarManufacturerName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CarLoanDetailDialog_CarManufacturer.value")}));
		this.lovDescCarModelName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CarLoanDetailDialog_CarModel.value")}));
		this.lovDescCarVersionName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CarLoanDetailDialog_CarVersion.value")}));
		this.lovDescCarDealerName.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CarLoanDetailDialog_CarDealer.value")}));
		logger.debug("Leaving");
	}
	
	/**
	 * Method for remove constraints of LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCarLoanForName.setConstraint("");
		this.lovDescCarUsageName.setConstraint("");
		this.lovDescCarManufacturerName.setConstraint("");
		this.lovDescCarModelName.setConstraint("");
		this.lovDescCarVersionName.setConstraint("");
		this.lovDescCarDealerName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.lovDescCarLoanForName.setErrorMessage("");
		this.lovDescCarUsageName.setErrorMessage("");
		this.lovDescCarVersionName.setErrorMessage("");
		this.carMakeYear.setErrorMessage("");
		this.carCapacity.setErrorMessage("");
		this.lovDescCarDealerName.setErrorMessage("");
		this.carCc.setErrorMessage("");
		this.cbCarColor.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful update
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<CarLoanDetail> soCarLoanDetail = getCarLoanDetailListCtrl().getSearchObj();
		getCarLoanDetailListCtrl().pagingCarLoanDetailList.setActivePage(0);
		getCarLoanDetailListCtrl().getPagedListWrapper().setSearchObject(soCarLoanDetail);
		if(getCarLoanDetailListCtrl().listBoxCarLoanDetail!=null){
			getCarLoanDetailListCtrl().listBoxCarLoanDetail.getListModel();
		}
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CarLoanDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CarLoanDetail aCarLoanDetail = new CarLoanDetail();
		BeanUtils.copyProperties(getCarLoanDetail(), aCarLoanDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aCarLoanDetail.getLoanRefNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCarLoanDetail.getRecordType()).equals("")){
				aCarLoanDetail.setVersion(aCarLoanDetail.getVersion()+1);
				aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCarLoanDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCarLoanDetail,tranType)){
					refreshList();
					closeDialog(this.window_CarLoanDetailDialog, "CarLoanDetail"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CarLoanDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final CarLoanDetail aCarLoanDetail = getCarLoanDetailService().getNewCarLoanDetail();
		setCarLoanDetail(aCarLoanDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.carLoanFor.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCarLoanDetail().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}

		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(isReadOnly("CarLoanDetailDialog_loanRefType"));
		this.btnSearchCarLoanFor.setDisabled(isReadOnly("CarLoanDetailDialog_carLoanFor"));
		this.btnSearchCarUsage.setDisabled(isReadOnly("CarLoanDetailDialog_carUsage"));
		this.btnSearchCarVersion.setDisabled(isReadOnly("CarLoanDetailDialog_carVersion"));
		this.carMakeYear.setReadonly(isReadOnly("CarLoanDetailDialog_carMakeYear"));
		this.carCapacity.setReadonly(isReadOnly("CarLoanDetailDialog_carCapacity"));
		this.btnSearchCarDealer.setDisabled(isReadOnly("CarLoanDetailDialog_carDealer"));
		this.btnSearchCarManufacturer.setDisabled(isReadOnly("CarLoanDetailDialog_carManufacturer"));
		this.btnSearchCarModel.setDisabled(isReadOnly("CarLoanDetailDialog_carModel"));
		
		this.carCc.setReadonly(isReadOnly("CarLoanDetailDialog_carCc"));
		this.carChasisNo.setReadonly(isReadOnly("CarLoanDetailDialog_carChasisNo"));
		this.carInsuranceNo.setReadonly(isReadOnly("CarLoanDetailDialog_carInsuranceNo"));
		this.carRegNo.setReadonly(isReadOnly("CarLoanDetailDialog_carRegNo"));
		this.cbCarColor.setDisabled(isReadOnly("CarLoanDetailDialog_carColor"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.carLoanDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewFinance()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(true);
		this.btnSearchCarLoanFor.setDisabled(true);
		this.btnSearchCarUsage.setDisabled(true);
		this.btnSearchCarVersion.setDisabled(true);
		this.carMakeYear.setReadonly(true);
		this.carCapacity.setReadonly(true);
		this.btnSearchCarDealer.setDisabled(true);
		this.carCc.setReadonly(true);
		this.cbCarColor.setDisabled(true);
		this.carChasisNo.setReadonly(true);
		this.carInsuranceNo.setReadonly(true);
		this.carRegNo.setReadonly(true);

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
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.loanRefNumber.setValue("");
		this.loanRefType.setChecked(false);
		this.carLoanFor.setText("");
		this.lovDescCarLoanForName.setValue("");
		this.carUsage.setText("");
		this.lovDescCarUsageName.setValue("");
		this.carManufacturer.setText("");
		this.lovDescCarManufacturerName.setValue("");
		this.carModel.setText("");
		this.lovDescCarModelName.setValue("");
		this.carVersion.setText("");
		this.lovDescCarVersionName.setValue("");
		this.carMakeYear.setText("");
		this.carCapacity.setText("");
		this.carDealer.setText("");
		this.lovDescCarDealerName.setValue("");
		this.cbCarColor.setSelectedIndex(0);
		this.carCc.setText("");
		this.carInsuranceNo.setText("");
		this.carRegNo.setText("");
		this.carChasisNo.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CarLoanDetail aCarLoanDetail = new CarLoanDetail();
		BeanUtils.copyProperties(getCarLoanDetail(), aCarLoanDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();	
		doSetLOVValidation();
		// fill the CarLoanDetail object with the components data
		doWriteComponentsToBean(aCarLoanDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCarLoanDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCarLoanDetail.getRecordType()).equals("")){
				aCarLoanDetail.setVersion(aCarLoanDetail.getVersion()+1);
				if(isNew){
					aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCarLoanDetail.setNewRecord(true);
				}
			}
		}else{
			aCarLoanDetail.setVersion(aCarLoanDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aCarLoanDetail,tranType)){
				refreshList();
				closeDialog(this.window_CarLoanDetailDialog, "CarLoanDetail");
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
	 * @param aCarLoanDetail
	 *            (CarLoanDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CarLoanDetail aCarLoanDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCarLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCarLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCarLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCarLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCarLoanDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCarLoanDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aCarLoanDetail))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
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

			aCarLoanDetail.setTaskId(taskId);
			aCarLoanDetail.setNextTaskId(nextTaskId);
			aCarLoanDetail.setRoleCode(getRole());
			aCarLoanDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCarLoanDetail, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCarLoanDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCarLoanDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCarLoanDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method (String)
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CarLoanDetail aCarLoanDetail = (CarLoanDetail) auditHeader.getAuditDetail().getModelData();

		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCarLoanDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCarLoanDetailService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCarLoanDetailService().doApprove(auditHeader);

						if (aCarLoanDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCarLoanDetailService().doReject(auditHeader);
						if (aCarLoanDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_CarLoanDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CarLoanDetailDialog, auditHeader);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchCarLoanFor(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_CarLoanDetailDialog,
				"CarLoanFor");
		if (dataObject instanceof String){
			this.carLoanFor.setText("");
			this.lovDescCarLoanForName.setValue("");
		}else{
			LovFieldDetail details= (LovFieldDetail) dataObject;
			if (details != null) {
				this.carLoanFor.setValue(details.getFieldCodeId());
				this.lovDescCarLoanForName.setValue(details.getFieldCodeValue());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCarUsage(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_CarLoanDetailDialog,
				"CarUsage");
		if (dataObject instanceof String){
			this.carUsage.setText("");
			this.lovDescCarUsageName.setValue("");
		}else{
			LovFieldDetail details= (LovFieldDetail) dataObject;
			if (details != null) {
				this.carUsage.setValue(details.getFieldCodeId());
				this.lovDescCarUsageName.setValue(details.getFieldCodeValue());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCarManufacturer(Event event){
		logger.debug("Entering" + event.toString());
		
		long manufacturer = this.carManufacturer.longValue();

		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("ManufacturerId", 0, Filter.OP_NOT_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_CarLoanDetailDialog,"VehicleManufacturer",filters);
		if (dataObject instanceof String){
			this.carManufacturer.setText("");
			this.lovDescCarManufacturerName.setValue("");
		}else{
			VehicleManufacturer details= (VehicleManufacturer) dataObject;
			if (details != null) {
				this.carManufacturer.setValue(details.getManufacturerId());
				this.lovDescCarManufacturerName.setValue(details.getManufacturerName());
			}
		}
		if (manufacturer != this.carManufacturer.longValue()){
			this.carModel.setText("");
			this.lovDescCarModelName.setValue("");
			this.carVersion.setText("");
			this.lovDescCarVersionName.setValue("");
		}
		this.btnSearchCarModel.setDisabled(false);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCarModel(Event event){
		logger.debug("Entering" + event.toString());
		
		long model = this.carModel.longValue();
		
		Filter[] filters = new Filter[2] ;
		filters[0]= new Filter("lovDescVehicleManufacturerId", this.carManufacturer.getValue(),
				Filter.OP_EQUAL);
		filters[1]= new Filter("VehicleModelId", 0, Filter.OP_NOT_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_CarLoanDetailDialog,
				"VehicleModel",filters);
		if (dataObject instanceof String){
			this.carModel.setText("");
			this.lovDescCarModelName.setValue("");
		}else{
			VehicleModel details= (VehicleModel) dataObject;
			if (details != null) {
				this.carModel.setValue(details.getVehicleModelId());
				this.lovDescCarModelName.setValue(details.getVehicleModelDesc());
			}
		}
		if (model != this.carModel.longValue()){
			this.carVersion.setText("");
			this.lovDescCarVersionName.setValue("");
		}
		this.btnSearchCarVersion.setDisabled(false);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCarVersion(Event event){
		logger.debug("Entering" + event.toString());
		
		Filter[] filters = new Filter[2] ;
		filters[0]= new Filter("VehicleModelId", this.carModel.getValue(), Filter.OP_EQUAL);
		filters[1]= new Filter("VehicleVersionId", 0, Filter.OP_NOT_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_CarLoanDetailDialog,
				"VehicleVersion",filters);
		if (dataObject instanceof String){
			this.carVersion.setText("");
			this.lovDescCarVersionName.setValue("");
		}else{
			VehicleVersion details= (VehicleVersion) dataObject;
			if (details != null) {
				this.carVersion.setValue(details.getVehicleVersionId());
				this.lovDescCarVersionName.setValue(details.getVehicleVersionCode());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCarDealer(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_CarLoanDetailDialog,"VehicleDealer");
		if (dataObject instanceof String){
			this.carDealer.setText("");
			this.lovDescCarDealerName.setValue("");
		}else{
			VehicleDealer details= (VehicleDealer) dataObject;
			if (details != null) {
				this.carDealer.setValue(details.getDealerId());
				this.lovDescCarDealerName.setValue(details.getDealerName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCarLoanDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CarLoanDetail aCarLoanDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCarLoanDetail.getBefImage(), aCarLoanDetail);   
		return new AuditHeader(String.valueOf(aCarLoanDetail.getLoanRefNumber()),null,null,
				null,auditDetail,aCarLoanDetail.getUserDetails(),getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CarLoanDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}
	
	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

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
		logger.debug("Leaving" + event.toString());
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
	
	// Get the notes entered for rejected reason
	private Notes getNotes(){
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CarLoanDetail");
		notes.setReference(String.valueOf(getCarLoanDetail().getLoanRefNumber()));
		notes.setVersion(getCarLoanDetail().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public CarLoanDetail getCarLoanDetail() {
		return this.carLoanDetail;
	}
	public void setCarLoanDetail(CarLoanDetail carLoanDetail) {
		this.carLoanDetail = carLoanDetail;
	}

	public void setCarLoanDetailService(CarLoanDetailService carLoanDetailService) {
		this.carLoanDetailService = carLoanDetailService;
	}
	public CarLoanDetailService getCarLoanDetailService() {
		return this.carLoanDetailService;
	}

	public void setCarLoanDetailListCtrl(CarLoanDetailListCtrl carLoanDetailListCtrl) {
		this.carLoanDetailListCtrl = carLoanDetailListCtrl;
	}
	public CarLoanDetailListCtrl getCarLoanDetailListCtrl() {
		return this.carLoanDetailListCtrl;
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
	
	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

}
