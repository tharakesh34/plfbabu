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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.constraint.PTListValidator;

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
	protected Window window_CarLoanDetailDialog; // autowired
	protected Textbox loanRefNumber; // autowired
	protected Checkbox loanRefType; // autowired
	protected ExtendedCombobox carLoanFor; // autowired
	protected ExtendedCombobox carUsage; // autowired
	protected ExtendedCombobox carManufacturer; // autowired
	protected ExtendedCombobox carModel; // autowired
	protected ExtendedCombobox carVersion; // autowired
	protected Intbox carMakeYear; // autowired
//The Below CarCapacity Field Is Changed To Number Of Cylinders (Only LableName and Purpose)
	protected Intbox carCapacity; // autowired
	protected ExtendedCombobox carDealer; // autowired
	protected Intbox carCc; // autowired
	protected Textbox carChasisNo; // autowired
	protected Textbox carInsuranceNo; // autowired
	protected Textbox carRegNo; // autowired
	protected ExtendedCombobox cbCarColor; // autowired
	protected Caption caption_carLoan;
	protected Textbox engineNumber;
	protected Combobox insuranceType;
 //The Below InsuranceDescription Field Is Changed To Sales Person Name (Only LableName and Purpose)
	protected Textbox insuranceDesc;
	protected Combobox paymentMode;
	protected Textbox purchageOdrNumber;
	protected Textbox quoationNbr;
	protected Datebox quoationDate;
	protected Textbox dealerPhone;
	protected Datebox purchaseDate;
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;
	// not auto wired vars
	private CarLoanDetail carLoanDetail; // overhanded per param
	private transient CarLoanDetailListCtrl carLoanDetailListCtrl; // overhanded
																	// per param
	// For Dynamically calling of this Controller
	private Div toolbar;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private Grid grid_carLoanDetails;
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String oldVar_loanRefNumber;
	private transient boolean oldVar_loanRefType;
	private transient long oldVar_carLoanFor;
	private transient long oldVar_carUsage;
	private transient long oldVar_carVersion;
	private transient int oldVar_carMakeYear;
	private transient int oldVar_carCapacity;
	private transient long oldVar_carDealer;
	private transient int oldVar_carCc;
	private transient String oldVar_carChasisNo;
	private transient String oldVar_carInsuranceNo;
	private transient String oldVar_carRegNo;
	private transient String oldVar_carColor;
	private transient String oldVar_recordStatus;
	private transient String oldVar_lovDescCarLoanForName;
	private transient String oldVar_lovDescCarUsageName;
	private transient String oldVar_lovDescCarVersionName;
	private transient String oldVar_lovDescCarDealerName;
	private transient String oldVar_dealerPhone;
	private transient Date oldVar_purchaseDate;
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	private transient boolean newFinance;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CarLoanDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	protected Groupbox gb_statusDetails;
	protected South south;
	// ServiceDAOs / Domain Classes
	private transient CarLoanDetailService carLoanDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private final List<ValueLabel> insurenceType =  PennantAppUtil.getInsurenceTypes();
	private final List<ValueLabel> paymentModes = PennantStaticListUtil.getPaymentModes();
	private transient boolean recSave = false;

	private long manufacturer;
	private long  model;
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
	 * ZUL-file is called with a parameter for a selected CarLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCreate$window_CarLoanDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			
			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}
			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("carLoanDetail")) {
				this.carLoanDetail = (CarLoanDetail) args.get("carLoanDetail");
				CarLoanDetail befImage = new CarLoanDetail();
				BeanUtils.copyProperties(this.carLoanDetail, befImage);
				this.carLoanDetail.setBefImage(befImage);
				setCarLoanDetail(this.carLoanDetail);
			} else {
				setCarLoanDetail(null);
			}
			if (args.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
				try {
					financeMainDialogCtrl.getClass().getMethod("setChildWindowDialogCtrl", Object.class).invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error(e);
				}
				setNewFinance(true);
				this.carLoanDetail.setWorkflowId(0);
				this.window_CarLoanDetailDialog.setTitle("");
				this.caption_carLoan.setVisible(true);
			}
			if (args.containsKey("roleCode")) {
				setRole((String) args.get("roleCode"));
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CarLoanDetailDialog");
			}
			doLoadWorkFlow(this.carLoanDetail.isWorkflow(), this.carLoanDetail.getWorkflowId(), this.carLoanDetail.getNextTaskId());
			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CarLoanDetailDialog");
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			// READ OVERHANDED params !
			// we get the carLoanDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete carLoanDetail here.
			if (args.containsKey("carLoanDetailListCtrl")) {
				setCarLoanDetailListCtrl((CarLoanDetailListCtrl) args.get("carLoanDetailListCtrl"));
			} else {
				setCarLoanDetailListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCarLoanDetail());
		} catch (Exception e) {
			logger.debug("Leaving" + e.getMessage());
			this.window_CarLoanDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.carLoanFor.setInputAllowed(false);
		this.carLoanFor.setDisplayStyle(3);
        this.carLoanFor.setMandatoryStyle(true);
		this.carLoanFor.setModuleName("CarLoanFor");
		this.carLoanFor.setValueColumn("FieldCodeValue");
		this.carLoanFor.setDescColumn("ValueDesc");
		this.carLoanFor.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.carUsage.setInputAllowed(false);
		this.carUsage.setDisplayStyle(3);
		this.carUsage.setModuleName("CarUsage");
		this.carUsage.setValueColumn("FieldCodeValue");
		this.carUsage.setDescColumn("ValueDesc");
		this.carUsage.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.carManufacturer.setInputAllowed(false);
		this.carManufacturer.setDisplayStyle(3);
        this.carManufacturer.setMandatoryStyle(true);
		this.carManufacturer.setModuleName("VehicleManufacturer");
		this.carManufacturer.setValueColumn("ManufacturerId");
		this.carManufacturer.setDescColumn("ManufacturerName");
		this.carManufacturer.setValidateColumns(new String[] { "ManufacturerId" });
		Filter[] manufactureFilters = new Filter[1];
		manufactureFilters[0] = new Filter("ManufacturerId", 0, Filter.OP_NOT_EQUAL);
		this.carManufacturer.setFilters(manufactureFilters);
		
		this.carModel.setInputAllowed(false);
		this.carModel.setDisplayStyle(3);
        this.carModel.setMandatoryStyle(true);
		this.carModel.setModuleName("VehicleModel");
		this.carModel.setValueColumn("VehicleModelId");
		this.carModel.setDescColumn("VehicleModelDesc");
		this.carModel.setValidateColumns(new String[] { "VehicleModelId" });
		
		this.carVersion.setInputAllowed(false);
		this.carVersion.setDisplayStyle(3);
		this.carVersion.setMandatoryStyle(false);
		this.carVersion.setModuleName("VehicleVersion");
		this.carVersion.setValueColumn("VehicleVersionId");
		this.carVersion.setDescColumn("VehicleVersionCode");
		this.carVersion.setValidateColumns(new String[] { "VehicleVersionId" });
		
		this.carDealer.setInputAllowed(false);
		this.carDealer.setDisplayStyle(3);
        this.carDealer.setMandatoryStyle(true);
		this.carDealer.setModuleName("VehicleDealer");
		this.carDealer.setValueColumn("DealerId");
		this.carDealer.setDescColumn("DealerName");
		this.carDealer.setValidateColumns(new String[] { "DealerId" });
		Filter carDealerfilter[] = new Filter[1];
		carDealerfilter[0] = new Filter("DealerType", "V", Filter.OP_EQUAL);
		this.carDealer.setFilters(carDealerfilter);
		
		this.cbCarColor.setInputAllowed(false);
		this.cbCarColor.setDisplayStyle(3);
        this.cbCarColor.setMandatoryStyle(true);
		this.cbCarColor.setModuleName("CarColor");
		this.cbCarColor.setValueColumn("FieldCodeValue");
		this.cbCarColor.setDescColumn("ValueDesc");
		this.cbCarColor.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.carMakeYear.setMaxlength(4);
		this.carCapacity.setMaxlength(2);
		this.engineNumber.setMaxlength(50);
		this.insuranceDesc.setMaxlength(50);
		this.quoationNbr.setMaxlength(50);
		this.purchageOdrNumber.setMaxlength(50);
		this.carCc.setMaxlength(5);
		this.carChasisNo.setMaxlength(19);
		this.carInsuranceNo.setMaxlength(19);
		this.carRegNo.setMaxlength(19);
		this.quoationDate.setFormat(PennantConstants.dateFormat);
		this.purchaseDate.setFormat(PennantConstants.dateFormat);
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
		getUserWorkspace().alocateAuthorities("CarLoanDetailDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CarLoanDetailDialog_btnSave"));
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
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "unchecked" })
	public void onAssetValidation(Event event) {
		logger.debug("Entering" + event.toString());
		String userAction = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}
		doClearMessage();
		recSave = false;
		if (("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
			&& !map.containsKey("agreement")) {
			recSave = true;
		} else {
			doSetValidation();
			doSetLOVValidation();
		}
		doWriteComponentsToBean(getCarLoanDetail());
		if (StringUtils.trimToEmpty(getCarLoanDetail().getRecordType()).equals("")) {
			getCarLoanDetail().setVersion(getCarLoanDetail().getVersion() + 1);
			getCarLoanDetail().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getCarLoanDetail().setNewRecord(true);
		}
	
			try {
				financeMainDialogCtrl.getClass().getMethod("setCarLoanDetail", CarLoanDetail.class).invoke(financeMainDialogCtrl, this.getCarLoanDetail());
			} catch (Exception e) {
				logger.error(e);
			}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 * */
	
	public void onAssetClose(Event event) {
		logger.debug("Entering" + event.toString());
			try {
				financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, this.isDataChanged());
			} catch (Exception e) {
				logger.error(e);
			}
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
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);
			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}
		if (close) {
			closeDialog(this.window_CarLoanDetailDialog, "CarLoanDetailDialog");
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
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCarLoanDetail
	 *            CarLoanDetail
	 */
	public void doWriteBeanToComponents(CarLoanDetail aCarLoanDetail) {
		logger.debug("Entering");
		this.loanRefNumber.setValue(aCarLoanDetail.getLoanRefNumber());
		this.loanRefType.setChecked(aCarLoanDetail.isLoanRefType());
		this.carLoanFor.setValue(String.valueOf(aCarLoanDetail.getCarLoanFor()));
		this.carUsage.setValue(String.valueOf(aCarLoanDetail.getCarUsage()));
		this.carManufacturer.setValue(String.valueOf(aCarLoanDetail.getManufacturerId()));
		this.carModel.setValue(String.valueOf(aCarLoanDetail.getVehicleModelId()));
		this.carVersion.setValue(String.valueOf(aCarLoanDetail.getCarVersion()));
		this.carMakeYear.setValue(aCarLoanDetail.getCarMakeYear());
		this.carCapacity.setValue(aCarLoanDetail.getCarCapacity());
		this.carDealer.setValue(String.valueOf(aCarLoanDetail.getCarDealer()));
		this.carCc.setValue(aCarLoanDetail.getCarCc());
		this.dealerPhone.setValue(aCarLoanDetail.getDealerPhone());
		this.carChasisNo.setValue(aCarLoanDetail.getCarChasisNo());
		this.carInsuranceNo.setValue(aCarLoanDetail.getCarInsuranceNo());
		this.carRegNo.setValue(aCarLoanDetail.getCarRegNo());
		this.cbCarColor.setValue(aCarLoanDetail.getCarColor());
		fillComboBox(this.insuranceType, aCarLoanDetail.getInsuranceType(), insurenceType, "");
		fillComboBox(this.paymentMode, aCarLoanDetail.getPaymentMode(), paymentModes, "");
		this.engineNumber.setValue(aCarLoanDetail.getEngineNumber());
		this.insuranceDesc.setValue(aCarLoanDetail.getInsuranceDesc());
		this.purchageOdrNumber.setValue(aCarLoanDetail.getPurchageOdrNumber());
		this.quoationNbr.setValue(aCarLoanDetail.getQuoationNbr());
		this.quoationDate.setValue(aCarLoanDetail.getQuoationDate());
		this.purchaseDate.setValue(aCarLoanDetail.getPurchaseDate());
		if (aCarLoanDetail.isNewRecord()) {
			this.carLoanFor.setDescription("");
			this.carUsage.setDescription("");
			this.carManufacturer.setDescription("");
			this.carModel.setDescription("");
			this.carVersion.setDescription("");
			this.carDealer.setDescription("");
		} else {
			this.carLoanFor.setDescription(aCarLoanDetail.getLovDescLoanForValue());
			this.carUsage.setDescription(aCarLoanDetail.getLovDescCarUsageValue());
			this.carManufacturer.setDescription(aCarLoanDetail.getLovDescManufacturerName());
			this.carModel.setDescription(aCarLoanDetail.getLovDescModelDesc());
			this.carVersion.setDescription(aCarLoanDetail.getLovDescVehicleVersionCode());
			this.carDealer.setDescription(aCarLoanDetail.getLovDescCarDealerName());
			this.cbCarColor.setDescription(aCarLoanDetail.getCarColor());
		}
		this.recordStatus.setValue(aCarLoanDetail.getRecordStatus());
		doSetVersionFilters();
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCarLoanDetail
	 */
	public void doWriteComponentsToBean(CarLoanDetail aCarLoanDetail) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCarLoanDetail.setLoanRefNumber(this.loanRefNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLoanRefType(this.loanRefType.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarLoanForName(this.carLoanFor.getDescription());
			aCarLoanDetail.setCarLoanFor(Long.valueOf(this.carLoanFor.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarUsageName(this.carUsage.getDescription());
			aCarLoanDetail.setCarUsage(Long.valueOf(this.carUsage.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescManufacturerName(this.carManufacturer.getDescription());
			aCarLoanDetail.setManufacturerId(Long.valueOf(this.carManufacturer.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescModelDesc(this.carModel.getDescription());
			aCarLoanDetail.setVehicleModelId(Long.valueOf(this.carModel.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarVersionName(this.carVersion.getDescription());
			aCarLoanDetail.setCarVersion(Long.valueOf(this.carVersion.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarMakeYear(this.carMakeYear.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarCapacity(this.carCapacity.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setLovDescCarDealerName(this.carDealer.getDescription());
			aCarLoanDetail.setCarDealer(Long.valueOf(this.carDealer.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarCc(this.carCc.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarInsuranceNo(this.carInsuranceNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarRegNo(this.carRegNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setCarChasisNo(this.carChasisNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setEngineNumber(this.engineNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.insuranceType.getSelectedItem() != null && !StringUtils.trimToEmpty(this.insuranceType.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				aCarLoanDetail.setInsuranceType(this.insuranceType.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setInsuranceDesc(this.insuranceDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.paymentMode.getSelectedItem() != null && !StringUtils.trimToEmpty(this.paymentMode.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
				aCarLoanDetail.setPaymentMode(this.paymentMode.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setPurchageOdrNumber(this.purchageOdrNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setQuoationNbr(this.quoationNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setQuoationDate(this.quoationDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCarLoanDetail.setDealerPhone(this.dealerPhone.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		FinanceMain main = null;
		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (getFinanceMainDialogCtrl().getClass().getMethod("getFinanceMain") != null) {
					Object object = getFinanceMainDialogCtrl().getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
					if (object != null) {
						main = (FinanceMain) object;
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		
		try {
			if(this.purchaseDate.getValue() == null && main != null){
				this.purchaseDate.setValue(main.getFinStartDate());
			}
			aCarLoanDetail.setPurchaseDate(this.purchaseDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
				aCarLoanDetail.setCarColor(this.cbCarColor.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.quoationDate.getValue() != null && this.purchaseDate.getValue() != null) {
				if (this.purchaseDate.getValue().compareTo(this.quoationDate.getValue()) < 0) {
					throw new WrongValueException(this.purchaseDate, Labels.getLabel("DATE_ALLOWED_AFTER", 
							new String[] { Labels.getLabel("label_CarLoanDetailDialog_purchaseDate.value"),
							Labels.getLabel("label_CarLoanDetailDialog_quoationDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(main != null){
			String purchaseOrderNum = "";
			if(!StringUtils.trimToEmpty(main.getLovDescCustCIF()).equals("")){
				purchaseOrderNum = main.getLovDescCustCIF() +"-";
			}
			if(!StringUtils.trimToEmpty(main.getFinBranch()).equals("")){
				purchaseOrderNum = purchaseOrderNum + main.getFinBranch() + "-";
			}
			if(!StringUtils.trimToEmpty(main.getLovDescCustCIF()).equals("")){
				purchaseOrderNum = purchaseOrderNum + main.getFinReference();
			}
			aCarLoanDetail.setPurchageOdrNumber(purchaseOrderNum);
			
		}
		try {
			if (main != null && this.purchaseDate.getValue() != null && main.getFinStartDate() != null) {
				if (main.getFinStartDate().compareTo(this.purchaseDate.getValue()) < 0) {
					throw new WrongValueException(this.purchaseDate, Labels.getLabel("DATE_ALLOWED_BEFORE", new String[] { Labels.getLabel("label_CarLoanDetailDialog_purchaseDate.value"), Labels.getLabel("label_FinStartDate") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();
		if (!recSave) {
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				if (panel != null) {
					((Tab) panel.getParent().getParent().getFellowIfAny("loanAssetTab")).setSelected(true);
				}
				throw new WrongValuesException(wvea);
			}
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
		logger.debug("Entering");
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
			if (isNewFinance()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aCarLoanDetail);
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if (panel != null) {
				this.toolbar.setVisible(false);
				this.gb_statusDetails.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.statusRow.setVisible(false);
				this.window_CarLoanDetailDialog.setHeight(grid_carLoanDetails.getRows().getVisibleItemCount() * 20 + 100 + "px");
				this.south.setHeight("0px");
				// panel.setHeight(grid_carLoanDetails.getRows().getVisibleItemCount()*20+160+"px");
				panel.appendChild(this.window_CarLoanDetailDialog);
			} else {
				setDialog(this.window_CarLoanDetailDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
			this.window_CarLoanDetailDialog.onClose();
		}
		logger.debug("Leaving");
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
		this.oldVar_carLoanFor = Long.valueOf(this.carLoanFor.getValue());
		this.oldVar_lovDescCarLoanForName = this.carLoanFor.getDescription();
		this.oldVar_carUsage = Long.valueOf(this.carUsage.getValue());
		this.oldVar_lovDescCarUsageName = this.carUsage.getDescription();
		this.oldVar_carVersion = Long.valueOf(this.carVersion.getValue());
		this.oldVar_lovDescCarVersionName = this.carVersion.getDescription();
		this.oldVar_carMakeYear = this.carMakeYear.intValue();
		this.oldVar_carCapacity = this.carCapacity.intValue();
		this.oldVar_carDealer = Long.valueOf(this.carDealer.getValue());
		this.oldVar_lovDescCarDealerName = this.carDealer.getDescription();
		this.oldVar_carCc = this.carCc.getValue();
		this.oldVar_carInsuranceNo = this.carInsuranceNo.getValue();
		this.oldVar_carRegNo = this.carRegNo.getValue();
		this.oldVar_carChasisNo = this.carChasisNo.getValue();
		this.oldVar_carColor = this.cbCarColor.getValue();
		this.oldVar_dealerPhone = this.dealerPhone.getValue();
		this.oldVar_purchaseDate = this.purchaseDate.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_loanRefNumber);
		this.loanRefType.setChecked(this.oldVar_loanRefType);
		this.carLoanFor.setValue(String.valueOf(this.oldVar_carLoanFor));
		this.carLoanFor.setDescription(this.oldVar_lovDescCarLoanForName);
		this.carUsage.setValue(String.valueOf(this.oldVar_carUsage));
		this.carUsage.setDescription(this.oldVar_lovDescCarUsageName);
		this.carVersion.setValue(String.valueOf(this.oldVar_carVersion));
		this.carVersion.setDescription(this.oldVar_lovDescCarVersionName);
		this.carMakeYear.setValue(this.oldVar_carMakeYear);
		this.carCapacity.setValue(this.oldVar_carCapacity);
		this.carDealer.setValue(String.valueOf(this.oldVar_carDealer));
		this.carDealer.setDescription(this.oldVar_lovDescCarDealerName);
		this.carCc.setValue(this.oldVar_carCc);
		this.carInsuranceNo.setValue(this.oldVar_carInsuranceNo);
		this.carRegNo.setValue(this.oldVar_carRegNo);
		this.carChasisNo.setValue(this.oldVar_carChasisNo);
		this.cbCarColor.setValue(this.oldVar_carColor);
		this.dealerPhone.setValue(this.oldVar_dealerPhone);
		this.purchaseDate.setValue(this.oldVar_purchaseDate);
		this.recordStatus.setValue(this.oldVar_recordStatus);
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
		// To clear the Error Messages
		doClearMessage();
		if (this.oldVar_loanRefNumber != this.loanRefNumber.getValue()) {
			return true;
		}
		if (this.oldVar_loanRefType != this.loanRefType.isChecked()) {
			return true;
		}
		if (this.oldVar_carLoanFor != Long.valueOf(this.carLoanFor.getValue())){
			return true;
		}
		if (this.oldVar_carUsage != Long.valueOf(this.carUsage.getValue())) {
			return true;
		}
		if (this.oldVar_carVersion != Long.valueOf(this.carVersion.getValue())) {
			return true;
		}
		if (this.oldVar_carMakeYear != this.carMakeYear.intValue()) {
			return true;
		}
		if (this.oldVar_carCapacity != this.carCapacity.intValue()) {
			return true;
		}
		if (this.oldVar_carDealer != Long.valueOf(this.carDealer.getValue())) {
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
		if (!this.oldVar_carColor.equals(this.cbCarColor.getValue())) {
			return true;
		}
		if (this.oldVar_dealerPhone != this.dealerPhone.getValue()) {
			return true;
		}
		if (this.oldVar_purchaseDate != this.purchaseDate.getValue()) {
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
		if (!recSave && !this.carMakeYear.isReadonly()) {
			this.carMakeYear.setConstraint(new PTNumberValidator(Labels.getLabel("label_CarLoanDetailDialog_CarMakeYear.value"), true, false, 
					DateUtility.getYear((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE")), DateUtility.getYear(new Date())+1));
		}
		if (!this.carCapacity.isReadonly()) {
			this.carCapacity.setConstraint("NO NEGATIVE:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarCapacity.value") }));
		}
		if (!this.carCc.isReadonly()) {
			this.carCc.setConstraint("NO NEGATIVE:" + Labels.getLabel("FIELD_NO_NEGATIVE", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarCc.value") }));
		}
		/*if (!this.engineNumber.isReadonly()) {
			this.engineNumber.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_engineNumber.value") }));
		}*/
		if (!this.insuranceType.isDisabled()) {
			this.insuranceType.setConstraint(new PTListValidator(Labels.getLabel("label_CarLoanDetailDialog_insuranceType.value"), insurenceType));
		}
		
		// if (!this.insuranceDesc.isReadonly()){
		// this.insuranceDesc.setConstraint("NO EMPTY:"+
		// Labels.getLabel("FIELD_NO_EMPTY",new String[] {
		// Labels.getLabel("label_CarLoanDetailDialog_insuranceDesc.value") }));
		// }
		if (!this.paymentMode.isReadonly()) {
			this.paymentMode.setConstraint(new PTListValidator(Labels.getLabel("label_CarLoanDetailDialog_paymentMode.value"), paymentModes));
		}
		
		if (!this.carChasisNo.isReadonly()) {
			this.carChasisNo.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarChasisNo.value") }));
		}
/*		if (!this.purchageOdrNumber.isReadonly()) {
			this.purchageOdrNumber.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_purchageOdrNumber.value") }));
		}*/
		if (!this.quoationNbr.isReadonly()) {
			this.quoationNbr.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_quoationNbr.value") }));
		}
		if (!this.quoationDate.isReadonly()) {
			this.quoationDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_quoationDate.value") }));
		}
		// if (!this.dealerPhone.isReadonly()){
		// this.dealerPhone.setConstraint("NO EMPTY:"+
		// Labels.getLabel("FIELD_NO_EMPTY",new String[] {
		// Labels.getLabel("label_CarLoanDetailDialog_dealerPhone.value") }));
		// }
		/*if (!this.purchaseDate.isReadonly()) {
			this.purchaseDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_purchaseDate.value") }));
		}*/
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
		this.engineNumber.setConstraint("");
		this.insuranceType.setConstraint("");
		this.insuranceDesc.setConstraint("");
		this.paymentMode.setConstraint("");
		this.purchageOdrNumber.setConstraint("");
		this.quoationNbr.setConstraint("");
		this.quoationDate.setConstraint("");
		this.dealerPhone.setConstraint("");
		this.purchaseDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for set constraints of LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.carLoanFor.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarLoanFor.value") }));
	//	this.lovDescCarUsageName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarUsage.value") }));
		this.carManufacturer.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarManufacturer.value") }));
		this.carModel.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarModel.value") }));
	//	this.carVersion.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarVersion.value") }));
		this.carDealer.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarDealer.value") }));
		this.cbCarColor.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CarLoanDetailDialog_CarColor.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints of LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.carLoanFor.setConstraint("");
		this.carUsage.setConstraint("");
		this.carManufacturer.setConstraint("");
		this.carModel.setConstraint("");
		this.carVersion.setConstraint("");
		this.carDealer.setConstraint("");
		this.cbCarColor.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.carLoanFor.setErrorMessage("");
		this.carManufacturer.setErrorMessage("");
		this.carModel.setErrorMessage("");
		this.carUsage.setErrorMessage("");
		this.carVersion.setErrorMessage("");
		this.carMakeYear.setErrorMessage("");
		this.carCapacity.setErrorMessage("");
		this.carCc.setErrorMessage("");
		this.cbCarColor.setErrorMessage("");
		this.carDealer.setErrorMessage("");
		this.dealerPhone.setErrorMessage("");
		this.engineNumber.setErrorMessage("");
		this.carChasisNo.setErrorMessage("");
		this.carInsuranceNo.setErrorMessage("");
		this.insuranceType.setErrorMessage("");
		this.insuranceDesc.setErrorMessage("");
		this.purchageOdrNumber.setErrorMessage("");
		this.purchaseDate.setErrorMessage("");
		this.quoationNbr.setErrorMessage("");
		this.quoationDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful update
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CarLoanDetail> soCarLoanDetail = getCarLoanDetailListCtrl().getSearchObj();
		getCarLoanDetailListCtrl().pagingCarLoanDetailList.setActivePage(0);
		getCarLoanDetailListCtrl().getPagedListWrapper().setSearchObject(soCarLoanDetail);
		if (getCarLoanDetailListCtrl().listBoxCarLoanDetail != null) {
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
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCarLoanDetail.getLoanRefNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aCarLoanDetail.getRecordType()).equals("")) {
				aCarLoanDetail.setVersion(aCarLoanDetail.getVersion() + 1);
				aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aCarLoanDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aCarLoanDetail, tranType)) {
					refreshList();
					closeDialog(this.window_CarLoanDetailDialog, "CarLoanDetailDialog");
				}
			} catch (DataAccessException e) {
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
		if (getCarLoanDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(isReadOnly("CarLoanDetailDialog_loanRefType"));
		this.carLoanFor.setReadonly(isReadOnly("CarLoanDetailDialog_carLoanFor"));
		this.carUsage.setReadonly(isReadOnly("CarLoanDetailDialog_carUsage"));
		this.carVersion.setReadonly(isReadOnly("CarLoanDetailDialog_carVersion"));
		this.carMakeYear.setReadonly(isReadOnly("CarLoanDetailDialog_carMakeYear"));
		this.carCapacity.setReadonly(isReadOnly("CarLoanDetailDialog_carCapacity"));
		this.carDealer.setReadonly(isReadOnly("CarLoanDetailDialog_carDealer"));
		this.carManufacturer.setReadonly(isReadOnly("CarLoanDetailDialog_carManufacturer"));
		this.carModel.setReadonly(isReadOnly("CarLoanDetailDialog_carModel"));
		this.carCc.setReadonly(isReadOnly("CarLoanDetailDialog_carCc"));
		this.carChasisNo.setReadonly(isReadOnly("CarLoanDetailDialog_carChasisNo"));
		this.carInsuranceNo.setReadonly(isReadOnly("CarLoanDetailDialog_carInsuranceNo"));
		this.carRegNo.setDisabled(isReadOnly("CarLoanDetailDialog_carRegNo"));
		this.cbCarColor.setReadonly(isReadOnly("CarLoanDetailDialog_cbCarColor"));  
		this.engineNumber.setDisabled(isReadOnly("CarLoanDetailDialog_engineNumber"));
		this.insuranceType.setDisabled(isReadOnly("CarLoanDetailDialog_insuranceType"));
		this.insuranceDesc.setDisabled(isReadOnly("CarLoanDetailDialog_insuranceDesc"));
		this.paymentMode.setDisabled(isReadOnly("CarLoanDetailDialog_paymentMode"));
		this.purchageOdrNumber.setDisabled(true);//isReadOnly("CarLoanDetailDialog_purchageOdrNumber")
		this.quoationNbr.setDisabled(isReadOnly("CarLoanDetailDialog_quoationNbr"));
		this.quoationDate.setDisabled(isReadOnly("CarLoanDetailDialog_quoationDate"));
		this.dealerPhone.setDisabled(true); // isReadOnly("CarLoanDetailDialog_dealerPhone")
		this.purchaseDate.setDisabled(isReadOnly("CarLoanDetailDialog_purchaseDate"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.carLoanDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
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
		this.carLoanFor.setReadonly(true);
		this.carUsage.setReadonly(true);
		this.carVersion.setReadonly(true);
		this.carMakeYear.setReadonly(true);
		this.carCapacity.setReadonly(true);
		this.carDealer.setReadonly(true);
		this.carCc.setReadonly(true);
		this.cbCarColor.setReadonly(true);
		this.carChasisNo.setReadonly(true);
		this.carInsuranceNo.setReadonly(true);
		this.carRegNo.setReadonly(true);
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
		this.loanRefNumber.setValue("");
		this.loanRefType.setChecked(false);
		this.carLoanFor.setValue(String.valueOf(new Long(0)));
		this.carLoanFor.setDescription("");
		this.carUsage.setValue(String.valueOf(new Long(0)));
		this.carUsage.setDescription("");
		this.carManufacturer.setValue(String.valueOf(new Long(0)));
		this.carManufacturer.setDescription("");
		this.carModel.setValue(String.valueOf(new Long(0)));
		this.carModel.setDescription("");
		this.carVersion.setValue(String.valueOf(new Long(0)));
		this.carVersion.setDescription("");
		this.carMakeYear.setText("");
		this.carCapacity.setText("");
		this.carDealer.setValue(String.valueOf(new Long(0)));
		this.carDealer.setDescription("");
		this.cbCarColor.setValue("");
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
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCarLoanDetail.getRecordType()).equals("")) {
				aCarLoanDetail.setVersion(aCarLoanDetail.getVersion() + 1);
				if (isNew) {
					aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCarLoanDetail.setNewRecord(true);
				}
			}
		} else {
			aCarLoanDetail.setVersion(aCarLoanDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aCarLoanDetail, tranType)) {
				refreshList();
				closeDialog(this.window_CarLoanDetailDialog, "CarLoanDetailDialog");
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
	private boolean doProcess(CarLoanDetail aCarLoanDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		aCarLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCarLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCarLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			// Upgraded to ZK-6.5.1.1 Added casting to String
			aCarLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCarLoanDetail.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCarLoanDetail);
				}
				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCarLoanDetail))) {
					try {
						if (!isNotes_Entered()) {
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
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
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
			aCarLoanDetail.setTaskId(taskId);
			aCarLoanDetail.setNextTaskId(nextTaskId);
			aCarLoanDetail.setRoleCode(getRole());
			aCarLoanDetail.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(aCarLoanDetail, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aCarLoanDetail);
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCarLoanDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCarLoanDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * @param method
	 *            (String)
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		CarLoanDetail aCarLoanDetail = (CarLoanDetail) auditHeader.getAuditDetail().getModelData();
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCarLoanDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCarLoanDetailService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCarLoanDetailService().doApprove(auditHeader);
						if (aCarLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCarLoanDetailService().doReject(auditHeader);
						if (aCarLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CarLoanDetailDialog, auditHeader);
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


	public void onFulfill$carLoanFor(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = carLoanFor.getObject();
		if (dataObject instanceof String) {
			this.carLoanFor.setValue(String.valueOf(new Long(0)));
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.carLoanFor.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$carUsage(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = carUsage.getObject();
		if (dataObject instanceof String) {
			this.carUsage.setValue(String.valueOf(new Long(0)));
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.carUsage.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$carManufacturer(Event event) {
		logger.debug("Entering" + event.toString());
		if (manufacturer != Long.valueOf(this.carManufacturer.getValue())) {
			this.carModel.setValue(String.valueOf(new Long(0)));
			this.carModel.setDescription("");
			this.carVersion.setValue(String.valueOf(new Long(0)));
			this.carVersion.setDescription("");
		}
		Filter[] carModelFilters = new Filter[2];
		carModelFilters[0] = new Filter("lovDescVehicleManufacturerId", this.carManufacturer.getValue(), Filter.OP_EQUAL);
		carModelFilters[1] = new Filter("VehicleModelId", 0, Filter.OP_NOT_EQUAL);
		this.carModel.setFilters(carModelFilters);
		manufacturer = Long.valueOf(this.carManufacturer.getValue());
		this.carModel.setReadonly(false);
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$carModel(Event event) {
		logger.debug("Entering" + event.toString());
	
		if (model != Long.valueOf(this.carModel.getValue())) {
			this.carVersion.setValue(String.valueOf(new Long(0)));
			this.carVersion.setDescription("");
		}
		doSetVersionFilters();
		model = Long.valueOf(this.carModel.getValue());
		this.carVersion.setReadonly(false);
		logger.debug("Leaving" + event.toString());
	}


	public void onFulfill$carDealer(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = carDealer.getObject();
		if (dataObject instanceof String) {
			//
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.dealerPhone.setValue(details.getDealerTelephone());
				getCarLoanDetail().setLovDescCarDealerPhone(details.getDealerTelephone());
				getCarLoanDetail().setLovDescCarDealerFax(details.getDealerFax());
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
	private AuditHeader getAuditHeader(CarLoanDetail aCarLoanDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCarLoanDetail.getBefImage(), aCarLoanDetail);
		return new AuditHeader(String.valueOf(aCarLoanDetail.getLoanRefNumber()), null, null, null, auditDetail, aCarLoanDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CarLoanDetailDialog, auditHeader);
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
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CarLoanDetail");
		notes.setReference(String.valueOf(getCarLoanDetail().getLoanRefNumber()));
		notes.setVersion(getCarLoanDetail().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	public void doSetVersionFilters(){
		logger.debug("Entering");
		Filter[] carVersionFilters = new Filter[2];
		carVersionFilters[0] = new Filter("VehicleModelId", this.carModel.getValue(), Filter.OP_EQUAL);
		carVersionFilters[1] = new Filter("VehicleVersionId", 0, Filter.OP_NOT_EQUAL);
		this.carVersion.setFilters(carVersionFilters);
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

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
}
