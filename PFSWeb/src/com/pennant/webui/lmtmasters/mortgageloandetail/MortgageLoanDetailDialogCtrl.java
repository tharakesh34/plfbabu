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
 * FileName    		:  MortgageLoanDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  19-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.mortgageloandetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.lmtmasters.MortgageLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.constraint.PTListValidator;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/MortgageLoanDetail/mortgageLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class MortgageLoanDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5115034606502184903L;
	private final static Logger logger = Logger.getLogger(MortgageLoanDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window     window_MortgageLoanDetailDialog;            // autoWired
	protected ExtendedCombobox    mortgProperty;                              // autoWired
	protected CurrencyBox 	 mortgCurrentValue;                          // autoWired
	protected Textbox    mortgPurposeOfLoan;                         // autoWired
	protected ExtendedCombobox    mortgPropertyRelation;                      // autoWired
	protected ExtendedCombobox    mortgOwnership;                             // autoWired
	protected Textbox    mortgAddrHNbr;                              // autoWired
	protected Textbox    mortgAddrFlatNbr;                           // autoWired
	protected Textbox    mortgAddrStreet;                            // autoWired
	protected Textbox    mortgAddrLane1;                             // autoWired
	protected Textbox    mortgAddrLane2;                             // autoWired
	protected Textbox    mortgAddrPOBox;                             // autoWired
	protected ExtendedCombobox    mortgAddrCountry;                           // autoWired
	protected ExtendedCombobox    mortgAddrProvince;                          // autoWired
	protected ExtendedCombobox    mortgAddrCity;                              // autoWired
	protected Textbox    mortgAddrZIP;                               // autoWired
	protected Textbox    mortgAddrPhone;                             // autoWired
	
	private Textbox mortDeedNo;
	private Textbox mortRegistrationNo;
	private Decimalbox mortAreaSF;
	private Decimalbox mortAreaSM;
	private Decimalbox mortPricePF;
	private Intbox mortAge;
	private Decimalbox mortFinRatio;
	private Combobox mortStatus;
	
	protected Label      recordStatus;                               // autoWired
	protected Radiogroup userAction;                                 // autoWired
	protected Groupbox   groupboxWf;                                 // autoWired
	protected Row        statusRow;                                  // autoWired
	
	// not auto wired variables
	private MortgageLoanDetail                  mortgageLoanDetail;          // over handed per parameters
	private transient MortgageLoanDetailListCtrl mortgageLoanDetailListCtrl; // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_mortgProperty;
	private transient BigDecimal  	oldVar_mortgCurrentValue;
	private transient String  		oldVar_mortgPurposeOfLoan;
	private transient long  		oldVar_mortgPropertyRelation;
	private transient long  		oldVar_mortgOwnership;
	private transient String  		oldVar_mortgAddrHNbr;
	private transient String  		oldVar_mortgAddrFlatNbr;
	private transient String  		oldVar_mortgAddrStreet;
	private transient String  		oldVar_mortgAddrLane1;
	private transient String  		oldVar_mortgAddrLane2;
	private transient String  		oldVar_mortgAddrPOBox;
	private transient String  		oldVar_mortgAddrCountry;
	private transient String  		oldVar_mortgAddrProvince;
	private transient String  		oldVar_mortgAddrCity;
	private transient String  		oldVar_mortgAddrZIP;
	private transient String  		oldVar_mortgAddrPhone;
	private transient String        oldVar_recordStatus;
	private transient boolean       validationOn;
	private boolean                 notes_Entered=false;
	private transient boolean       newFinance;
	private transient int   ccyFormatter = 0;
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_MortgageLoanDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button     btnNew;                                     // autoWired
	protected Button     btnEdit;                                    // autoWired
	protected Button     btnDelete;                                  // autoWired
	protected Button     btnSave;                                    // autoWired
	protected Button     btnCancel;                                  // autoWired
	protected Button     btnClose;                                   // autoWired
	protected Button     btnHelp;                                    // autoWired
	protected Button     btnNotes;                                   // autoWired
	
	//Search Button declarations
	private transient String 		oldVar_lovDescMortgPropertyRelationName;
	private transient String 		oldVar_lovDescMortgPropertyName;
	private transient String 		oldVar_lovDescMortgOwnershipName;
	private transient String 		oldVar_lovDescMortgAddrCountryName;
	private transient String 		oldVar_lovDescMortgAddrProvinceName;
	private transient String 		oldVar_lovDescMortgAddrCityName;
	
	// ServiceDAOs / Domain Classes
	private transient MortgageLoanDetailService mortgageLoanDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	//For Dynamically calling of this Controller
	private Div toolbar;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private Grid grid_mortgageLoanDetails;
	protected Caption		caption_mortLoan;
	private transient boolean recSave = false;
    private transient String  mortgAddrCountryTemp;
    private transient String  mortgAddrProvinceTemp;
	/**
	 * default constructor.<br>
	 */
	public MortgageLoanDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected MortgageLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_MortgageLoanDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(event.getTarget().getParent() != null){
			panel = (Tabpanel) event.getTarget().getParent();
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("mortgageLoanDetail")) {
			this.mortgageLoanDetail = (MortgageLoanDetail) args.get("mortgageLoanDetail");
			MortgageLoanDetail befImage =new MortgageLoanDetail();
			BeanUtils.copyProperties(this.mortgageLoanDetail, befImage);
			this.mortgageLoanDetail.setBefImage(befImage);
			setMortgageLoanDetail(this.mortgageLoanDetail);
		} else {
			setMortgageLoanDetail(null);
		}
		
		if(args.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
			setNewFinance(true);
			this.mortgageLoanDetail.setWorkflowId(0);
			this.window_MortgageLoanDetailDialog.setTitle("");
			this.caption_mortLoan.setVisible(true);
		}
		
		if(args.containsKey("roleCode")){
			setRole((String) args.get("roleCode"));
			getUserWorkspace().alocateRoleAuthorities(getRole(), "MortgageLoanDetailDialog");
		}
		
		if(args.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)args.get("ccyFormatter");
		}

		doLoadWorkFlow(this.mortgageLoanDetail.isWorkflow(),
				this.mortgageLoanDetail.getWorkflowId(),this.mortgageLoanDetail.getNextTaskId());

		if (isWorkFlowEnabled() && !isNewFinance()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "MortgageLoanDetailDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED params !
		// we get the mortgageLoanDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete mortgageLoanDetail here.
		if (args.containsKey("mortgageLoanDetailListCtrl")) {
			setMortgageLoanDetailListCtrl((MortgageLoanDetailListCtrl) args.get("mortgageLoanDetailListCtrl"));
		} else {
			setMortgageLoanDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getMortgageLoanDetail());
		logger.debug("Leaving" + event.toString());
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

		getUserWorkspace().alocateAuthorities("MortgageLoanDetailDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MortgageLoanDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MortgageLoanDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MortgageLoanDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MortgageLoanDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_MortgageLoanDetailDialog(Event event) throws Exception {
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
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_MortgageLoanDetailDialog);
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
		recSave = false;
		if(("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
				&& !map.containsKey("agreement")){
			recSave = true;
		}else{
			doSetValidation();
			doSetLOVValidation();
		}

		doWriteComponentsToBean(getMortgageLoanDetail());
		if(StringUtils.trimToEmpty(getMortgageLoanDetail().getRecordType()).equals("")){
			getMortgageLoanDetail().setVersion(getMortgageLoanDetail().getVersion() + 1);
			getMortgageLoanDetail().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getMortgageLoanDetail().setNewRecord(true);
		}
		try {
			financeMainDialogCtrl.getClass().getMethod("setMortgageLoanDetail", MortgageLoanDetail.class).invoke(
					financeMainDialogCtrl, getMortgageLoanDetail());
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for checking whethter data has been changed before closing
	 * @param event
	 * @return 
	 * */
	public void onAssetClose(Event event){
		logger.debug("Entering" + event.toString());
			try {
					financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, isDataChanged());
			} catch (Exception e) {
				logger.error(e);
			}
		logger.debug("Leaving" + event.toString());
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	
	public void onFulfill$mortgPropertyRelation(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = mortgPropertyRelation.getObject();
		if (dataObject instanceof String){
			this.mortgPropertyRelation.setValue(String.valueOf(new Long(0)));
		}else{
			LovFieldDetail details= (LovFieldDetail) dataObject;
			if (details != null) {
				this.mortgPropertyRelation.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$mortgOwnership(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = mortgOwnership.getObject();
		if (dataObject instanceof String){
			this.mortgOwnership.setValue(String.valueOf(new Long(0)));
		}else{
			LovFieldDetail details= (LovFieldDetail) dataObject;
			if (details != null) {
				this.mortgOwnership.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	

	public void onFulfill$mortgAddrCountry(Event event){
		logger.debug("Entering" + event.toString());
	
		Object dataObject = mortgAddrCountry.getObject();
		if (dataObject instanceof String){
			this.mortgAddrCountry.setValue(dataObject.toString());
			this.mortgAddrCountry.setDescription("");
		}else{
			Country details= (Country) dataObject;
			if (details != null) {
				this.mortgAddrCountry.setValue(details.getCountryCode());
				this.mortgAddrCountry.setDescription(details.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(mortgAddrCountryTemp).equals(this.mortgAddrCountry.getValue())){
			this.mortgAddrProvince.setValue("");
			this.mortgAddrProvince.setDescription("");
			this.mortgAddrCity.setValue("");
			this.mortgAddrCity.setDescription("");
			this.mortgAddrCity.setReadonly(true);	
			this.mortgAddrCity.setMandatoryStyle(false);	
		}
		if(!StringUtils.trimToEmpty(this.mortgAddrCountry.getValue()).equals("")){
			this.mortgAddrProvince.setReadonly(false);
			this.mortgAddrProvince.setMandatoryStyle(true);
		}else{
			this.mortgAddrProvince.setReadonly(true);
			this.mortgAddrCity.setReadonly(true);
			this.mortgAddrProvince.setMandatoryStyle(false);
			this.mortgAddrCity.setMandatoryStyle(false);
			
		}
		mortgAddrCountryTemp = this.mortgAddrCountry.getValue();
		Filter[] filtersProvince = new Filter[1] ;
		filtersProvince[0]= new Filter("CPCountry", this.mortgAddrCountry.getValue(), Filter.OP_EQUAL);
		this.mortgAddrProvince.setFilters(filtersProvince);
		logger.debug("Leaving" + event.toString());
	
		
	}	
	
	
	public void onFulfill$mortgAddrProvince(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = mortgAddrProvince.getObject();
		if (dataObject instanceof String){
			this.mortgAddrProvince.setValue(dataObject.toString());
			this.mortgAddrProvince.setDescription("");
		}else{
			Province details= (Province) dataObject;
			if (details != null) {
				this.mortgAddrProvince.setValue(details.getCPProvince());
				this.mortgAddrProvince.setDescription(details.getCPProvinceName());
			}
		}

		if (!StringUtils.trimToEmpty(mortgAddrProvinceTemp).equals(this.mortgAddrProvince.getValue())){
			this.mortgAddrCity.setValue("");
			this.mortgAddrCity.setDescription("");   
		}
		if(!StringUtils.trimToEmpty(this.mortgAddrProvince.getValue()).equals("")){
			this.mortgAddrCity.setReadonly(false);
			this.mortgAddrCity.setMandatoryStyle(true);
		}else{
			this.mortgAddrCity.setReadonly(true);		   
			this.mortgAddrCity.setMandatoryStyle(false);		   
		}
		mortgAddrProvinceTemp= this.mortgAddrProvince.getValue();
		Filter[] filtersCity = new Filter[1] ;
		filtersCity[0]= new Filter("PCProvince", this.mortgAddrProvince.getValue(), Filter.OP_EQUAL);
		this.mortgAddrCity.setFilters(filtersCity);
		
		logger.debug("Leaving" + event.toString());
	}

	
	public void onFulfill$mortgAddrCity(Event event){
		logger.debug("Entering" + event.toString());
		
		Object dataObject = mortgAddrCity.getObject();
		if (dataObject instanceof String){
			this.mortgAddrCity.setValue(dataObject.toString());
			this.mortgAddrCity.setDescription("");
		}else{
			City details= (City) dataObject;
			if (details != null) {
				this.mortgAddrCity.setValue(details.getPCCity());
				this.mortgAddrCity.setDescription(details.getPCCityName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ GUI Process +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.mortgProperty.setMaxlength(19);
		this.mortgProperty.setInputAllowed(false);
		this.mortgProperty.setDisplayStyle(3);
        this.mortgProperty.setMandatoryStyle(true);
		this.mortgProperty.setModuleName("PropertyDetail");
		this.mortgProperty.setValueColumn("PropertyDetailId");
		this.mortgProperty.setDescColumn("PropertyDetailDesc");
		this.mortgProperty.setValidateColumns(new String[] { "PropertyDetailId" });		
		
		this.mortDeedNo.setMaxlength(50);
		
		this.mortAreaSF.setMaxlength(15);
		this.mortAreaSF.setFormat(PennantConstants.rateFormate9);
		this.mortAreaSF.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.mortAreaSF.setScale(9);
		this.mortAreaSM.setMaxlength(15);
		this.mortAreaSM.setFormat(PennantConstants.rateFormate9);
		this.mortAreaSM.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.mortAreaSM.setScale(9);
		
		this.mortgCurrentValue.setMandatory(true);
		this.mortgCurrentValue.setMaxlength(18);
		this.mortgCurrentValue.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		
		this.mortFinRatio.setMaxlength(18);
		this.mortFinRatio.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		
		this.mortPricePF.setMaxlength(18);
		this.mortPricePF.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		
		this.mortgPurposeOfLoan.setMaxlength(100);
		
		this.mortgPropertyRelation.setInputAllowed(false);
		this.mortgPropertyRelation.setDisplayStyle(3);
        this.mortgPropertyRelation.setMandatoryStyle(true);
		this.mortgPropertyRelation.setModuleName("PropertyRelation");
		this.mortgPropertyRelation.setValueColumn("FieldCodeValue");
		this.mortgPropertyRelation.setDescColumn("ValueDesc");
		this.mortgPropertyRelation.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.mortgOwnership.setInputAllowed(false);
		this.mortgOwnership.setDisplayStyle(3);
        this.mortgOwnership.setMandatoryStyle(true);
		this.mortgOwnership.setModuleName("Ownership");
		this.mortgOwnership.setValueColumn("FieldCodeValue");
		this.mortgOwnership.setDescColumn("ValueDesc");
		this.mortgOwnership.setValidateColumns(new String[] { "FieldCodeValue" });
		
		this.mortgAddrHNbr.setMaxlength(50);
		this.mortgAddrFlatNbr.setMaxlength(50);
		this.mortgAddrStreet.setMaxlength(50);
		this.mortgAddrLane1.setMaxlength(50);
		this.mortgAddrLane2.setMaxlength(50);
		this.mortgAddrPOBox.setMaxlength(8);
		
		this.mortgAddrCountry.setMaxlength(2);
		this.mortgAddrCountry.setMandatoryStyle(true);
		this.mortgAddrCountry.setModuleName("Country");
		this.mortgAddrCountry.setValueColumn("CountryCode");
		this.mortgAddrCountry.setDescColumn("CountryDesc");
		this.mortgAddrCountry.setValidateColumns(new String[] { "CountryCode" });
		
		this.mortgAddrProvince.setMaxlength(8);
		this.mortgAddrProvince.setModuleName("Province");
		this.mortgAddrProvince.setValueColumn("CPProvince");
		this.mortgAddrProvince.setDescColumn("CPProvinceName");
		this.mortgAddrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.mortgAddrCity.setMaxlength(8);
		this.mortgAddrCity.setModuleName("City");
		this.mortgAddrCity.setValueColumn("PCCity");
		this.mortgAddrCity.setDescColumn("PCCityName");
		this.mortgAddrCity.setValidateColumns(new String[] { "PCCity" });
		
		
		this.mortgAddrZIP.setMaxlength(10);
		this.mortgAddrPhone.setMaxlength(15);
		this.mortgAddrProvince.setReadonly(true);
		this.mortgAddrCity.setReadonly(true);

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
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeDialog(this.window_MortgageLoanDetailDialog, "MortgageLoanDetailDialog");	
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
	 * @param aMortgageLoanDetail
	 *            MortgageLoanDetail
	 */
	public void doWriteBeanToComponents(MortgageLoanDetail aMortgageLoanDetail) {
		logger.debug("Entering") ;
		this.mortgProperty.setValue(String.valueOf(aMortgageLoanDetail.getMortgProperty()));
		this.mortgCurrentValue.setValue(PennantAppUtil.formateAmount(
				aMortgageLoanDetail.getMortgCurrentValue(),this.ccyFormatter));
		
		this.mortDeedNo.setValue(aMortgageLoanDetail.getMortDeedNo());
		this.mortRegistrationNo.setValue(aMortgageLoanDetail.getMortRegistrationNo());
		this.mortAreaSF.setValue(aMortgageLoanDetail.getMortAreaSF());
		this.mortAreaSM.setValue(aMortgageLoanDetail.getMortAreaSM());
		this.mortPricePF.setValue(PennantAppUtil.formateAmount(aMortgageLoanDetail.getMortPricePF(),this.ccyFormatter));
		this.mortAge.setValue(aMortgageLoanDetail.getMortAge());
		this.mortFinRatio.setValue(PennantAppUtil.formateAmount(aMortgageLoanDetail.getMortFinRatio(),this.ccyFormatter));
		fillComboBox(this.mortStatus, aMortgageLoanDetail.getMortStatus(), PennantStaticListUtil.getMortgaugeStatus(), "");
		this.mortgPurposeOfLoan.setValue(aMortgageLoanDetail.getMortgPurposeOfLoan());
		this.mortgPropertyRelation.setValue(String.valueOf(aMortgageLoanDetail.getMortgPropertyRelation()));
		this.mortgOwnership.setValue(String.valueOf(aMortgageLoanDetail.getMortgOwnership()));
		this.mortgAddrHNbr.setValue(aMortgageLoanDetail.getMortgAddrHNbr());
		this.mortgAddrFlatNbr.setValue(aMortgageLoanDetail.getMortgAddrFlatNbr());
		this.mortgAddrStreet.setValue(aMortgageLoanDetail.getMortgAddrStreet());
		this.mortgAddrLane1.setValue(aMortgageLoanDetail.getMortgAddrLane1());
		this.mortgAddrLane2.setValue(aMortgageLoanDetail.getMortgAddrLane2());
		this.mortgAddrPOBox.setValue(aMortgageLoanDetail.getMortgAddrPOBox());
		this.mortgAddrCountry.setValue(aMortgageLoanDetail.getMortgAddrCountry());
		this.mortgAddrProvince.setValue(aMortgageLoanDetail.getMortgAddrProvince());
		this.mortgAddrCity.setValue(aMortgageLoanDetail.getMortgAddrCity());
		this.mortgAddrZIP.setValue((aMortgageLoanDetail.getMortgAddrZIP()==null)?"":
			aMortgageLoanDetail.getMortgAddrZIP().trim());
		this.mortgAddrPhone.setValue(aMortgageLoanDetail.getMortgAddrPhone());
		if (aMortgageLoanDetail.isNewRecord()){
			this.mortgProperty.setDescription("");
			this.mortgPropertyRelation.setDescription("");
			this.mortgOwnership.setDescription("");
			this.mortgAddrCountry.setDescription("");
			this.mortgAddrProvince.setDescription("");
			this.mortgAddrCity.setDescription("");
		}else{
			this.mortgProperty.setDescription(aMortgageLoanDetail.getLovDescMortgPropertyName());
			
			this.mortgPropertyRelation.setDescription(aMortgageLoanDetail.getLovDescMortgPropertyRelationName());
			this.mortgOwnership.setDescription(aMortgageLoanDetail.getLovDescMortgOwnershipName());
			this.mortgAddrCountry.setDescription(aMortgageLoanDetail.getLovDescMortgAddrCountryName());
			this.mortgAddrProvince.setDescription(aMortgageLoanDetail.getLovDescMortgAddrProvinceName());
			this.mortgAddrCity.setDescription(aMortgageLoanDetail.getLovDescMortgAddrCityName());
		}
		this.recordStatus.setValue(aMortgageLoanDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMortgageLoanDetail
	 */
	public void doWriteComponentsToBean(MortgageLoanDetail aMortgageLoanDetail) {
		logger.debug("Entering") ;

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aMortgageLoanDetail.setLovDescMortgPropertyName(this.mortgProperty.getDescription());
			aMortgageLoanDetail.setMortgProperty(Long.valueOf(this.mortgProperty.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.mortgCurrentValue.getValue() != null) {
				aMortgageLoanDetail.setMortgCurrentValue(PennantAppUtil.unFormateAmount(this.mortgCurrentValue.getValue(), this.ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortDeedNo(this.mortDeedNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortRegistrationNo(this.mortRegistrationNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortPricePF(PennantAppUtil.unFormateAmount(this.mortPricePF.getValue(),this.ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortAge(this.mortAge.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortFinRatio(PennantAppUtil.unFormateAmount(this.mortFinRatio.getValue(),this.ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.mortStatus.getSelectedItem()!=null && !this.mortStatus.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				aMortgageLoanDetail.setMortStatus(this.mortStatus.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
			
		try {
			aMortgageLoanDetail.setMortgPurposeOfLoan(this.mortgPurposeOfLoan.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setLovDescMortgPropertyRelationName(
					this.mortgPropertyRelation.getDescription());
			aMortgageLoanDetail.setMortgPropertyRelation(Long.valueOf(this.mortgPropertyRelation.getValue()));	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setLovDescMortgOwnershipName(this.mortgOwnership.getDescription());
			aMortgageLoanDetail.setMortgOwnership(Long.valueOf(this.mortgOwnership.getValue()));	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrHNbr(this.mortgAddrHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrFlatNbr(this.mortgAddrFlatNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrStreet(this.mortgAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrLane1(this.mortgAddrLane1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrLane2(this.mortgAddrLane2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrPOBox(this.mortgAddrPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setLovDescMortgAddrCountryName(this.mortgAddrCountry.getDescription());
			aMortgageLoanDetail.setMortgAddrCountry(StringUtils.trimToEmpty(this.mortgAddrCountry.getValidatedValue()));	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setLovDescMortgAddrProvinceName(this.mortgAddrProvince.getDescription());
			aMortgageLoanDetail.setMortgAddrProvince(this.mortgAddrProvince.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setLovDescMortgAddrCityName(this.mortgAddrCity.getDescription());
			aMortgageLoanDetail.setMortgAddrCity(this.mortgAddrCity.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrZIP(this.mortgAddrZIP.getValue().trim());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aMortgageLoanDetail.setMortgAddrPhone(this.mortgAddrPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if(!recSave){
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
		}
		validateMortAreaSFSM(aMortgageLoanDetail);
		aMortgageLoanDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	
	
	public void validateMortAreaSFSM(MortgageLoanDetail aMortgageLoanDetail){
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		this.mortAreaSF.setConstraint(new PTDecimalValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortAreaSF.value"),9,false,false,99999));
		this.mortAreaSM.setConstraint(new PTDecimalValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortAreaSM.value"),9,false,false,99999));
		try{
			aMortgageLoanDetail.setMortAreaSF(this.mortAreaSF.getValue());
		}catch(WrongValueException wea){
			wve.add(wea);
		}
		try{
			aMortgageLoanDetail.setMortAreaSM(this.mortAreaSM.getValue());
		}catch(WrongValueException wea){
			wve.add(wea);
		}
		WrongValueException [] wvea = new WrongValueException[wve.size()];
		if(wve.size() > 0){
		for (int i = 0; i < wve.size(); i++) {
			wvea[i] = (WrongValueException) wve.get(i);
		}
		throw new WrongValuesException(wvea);
		}
}
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aMortgageLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(MortgageLoanDetail aMortgageLoanDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aMortgageLoanDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aMortgageLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aMortgageLoanDetail = getMortgageLoanDetailService().getNewMortgageLoanDetail();
			setMortgageLoanDetail(aMortgageLoanDetail);
		} else {
			setMortgageLoanDetail(aMortgageLoanDetail);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aMortgageLoanDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.mortgProperty.focus();
		} else {
			this.mortgProperty.focus();
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
			doWriteBeanToComponents(aMortgageLoanDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if(panel != null){
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.statusRow.setVisible(false);
				this.window_MortgageLoanDetailDialog.setHeight(grid_mortgageLoanDetails.getRows().getVisibleItemCount()*20+80+"px");
				//panel.setHeight(grid_mortgageLoanDetails.getRows().getVisibleItemCount()*20+180+"px");
				panel.appendChild(this.window_MortgageLoanDetailDialog);
			}else{
				setDialog(this.window_MortgageLoanDetailDialog);
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
		this.oldVar_mortgProperty = this.mortgProperty.getValue();
		this.oldVar_lovDescMortgPropertyName = this.mortgProperty.getDescription();
		this.oldVar_mortgCurrentValue = this.mortgCurrentValue.getValue();
		this.oldVar_mortgPurposeOfLoan = this.mortgPurposeOfLoan.getValue();
		this.oldVar_mortgPropertyRelation = Long.valueOf(this.mortgPropertyRelation.getValue());
		this.oldVar_lovDescMortgPropertyRelationName = this.mortgPropertyRelation.getDescription();
		this.oldVar_mortgOwnership = Long.valueOf(this.mortgOwnership.getValue());
		this.oldVar_lovDescMortgOwnershipName = this.mortgOwnership.getDescription();
		this.oldVar_mortgAddrHNbr = this.mortgAddrHNbr.getValue();
		this.oldVar_mortgAddrFlatNbr = this.mortgAddrFlatNbr.getValue();
		this.oldVar_mortgAddrStreet = this.mortgAddrStreet.getValue();
		this.oldVar_mortgAddrLane1 = this.mortgAddrLane1.getValue();
		this.oldVar_mortgAddrLane2 = this.mortgAddrLane2.getValue();
		this.oldVar_mortgAddrPOBox = this.mortgAddrPOBox.getValue();
		this.oldVar_mortgAddrCountry = this.mortgAddrCountry.getValue();
		this.oldVar_lovDescMortgAddrCountryName = this.mortgAddrCountry.getDescription();
		this.oldVar_mortgAddrProvince = this.mortgAddrProvince.getValue();
		this.oldVar_lovDescMortgAddrProvinceName = this.mortgAddrProvince.getDescription();
		this.oldVar_mortgAddrCity = this.mortgAddrCity.getValue();
		this.oldVar_lovDescMortgAddrCityName = this.mortgAddrCity.getDescription();
		this.oldVar_mortgAddrZIP = this.mortgAddrZIP.getValue();
		this.oldVar_mortgAddrPhone = this.mortgAddrPhone.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.mortgProperty.setValue(this.oldVar_mortgProperty);
		this.mortgProperty.setDescription(this.oldVar_lovDescMortgPropertyName);
		this.mortgCurrentValue.setValue(this.oldVar_mortgCurrentValue);
		this.mortgPurposeOfLoan.setValue(this.oldVar_mortgPurposeOfLoan);
		this.mortgPropertyRelation.setValue(String.valueOf(this.oldVar_mortgPropertyRelation));
		this.mortgPropertyRelation.setDescription(this.oldVar_lovDescMortgPropertyRelationName);
		this.mortgOwnership.setValue(String.valueOf(this.oldVar_mortgOwnership));
		this.mortgOwnership.setDescription(this.oldVar_lovDescMortgOwnershipName);
		this.mortgAddrHNbr.setValue(this.oldVar_mortgAddrHNbr);
		this.mortgAddrFlatNbr.setValue(this.oldVar_mortgAddrFlatNbr);
		this.mortgAddrStreet.setValue(this.oldVar_mortgAddrStreet);
		this.mortgAddrLane1.setValue(this.oldVar_mortgAddrLane1);
		this.mortgAddrLane2.setValue(this.oldVar_mortgAddrLane2);
		this.mortgAddrPOBox.setValue(this.oldVar_mortgAddrPOBox);
		this.mortgAddrCountry.setValue(this.oldVar_mortgAddrCountry);
		this.mortgAddrCountry.setDescription(this.oldVar_lovDescMortgAddrCountryName);
		this.mortgAddrProvince.setValue(this.oldVar_mortgAddrProvince);
		this.mortgAddrProvince.setDescription(this.oldVar_lovDescMortgAddrProvinceName);
		this.mortgAddrCity.setValue(this.oldVar_mortgAddrCity);
		this.mortgAddrCity.setDescription(this.oldVar_lovDescMortgAddrCityName);
		this.mortgAddrZIP.setValue(this.oldVar_mortgAddrZIP);
		this.mortgAddrPhone.setValue(this.oldVar_mortgAddrPhone);
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
		
		if (this.oldVar_mortgProperty != this.mortgProperty.getValue()) {
			return true;
		}
		if (this.oldVar_mortgCurrentValue != this.mortgCurrentValue.getValue()) {
			return true;
		}
		if (this.oldVar_mortgPurposeOfLoan != this.mortgPurposeOfLoan.getValue()) {
			return true;
		}
		if (this.oldVar_mortgPropertyRelation != Long.valueOf(this.mortgPropertyRelation.getValue())) {
			return true;
		}
		if (this.oldVar_mortgOwnership != Long.valueOf(this.mortgOwnership.getValue())){
			return true;
		}
		if (this.oldVar_mortgAddrHNbr != this.mortgAddrHNbr.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrFlatNbr != this.mortgAddrFlatNbr.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrStreet != this.mortgAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrLane1 != this.mortgAddrLane1.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrLane2 != this.mortgAddrLane2.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrPOBox != this.mortgAddrPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrCountry != this.mortgAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrProvince != this.mortgAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrCity != this.mortgAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrZIP != this.mortgAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_mortgAddrPhone != this.mortgAddrPhone.getValue()) {
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
		if (!this.mortgCurrentValue.isReadonly()){
			this.mortgCurrentValue.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_MortgageLoanDetailDialog_MortgCurrentValue.value")));
		}	
		if (!this.mortDeedNo.isReadonly()) {
			this.mortDeedNo.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortDeedNo.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
/*		if (!this.mortRegistrationNo.isReadonly()) {
			this.mortRegistrationNo.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortRegistrationNo.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}*/
		if (!this.mortStatus.isDisabled()) {
			this.mortStatus.setConstraint(new PTListValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortStatus.value"), PennantStaticListUtil.getMortgaugeStatus()));
		}
	/*	if (!this.mortAreaSF.isReadonly()) {
			this.mortAreaSF.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortAreaSF.value"),PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.mortAreaSM.isReadonly()) {
			this.mortAreaSM.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortAreaSM.value"),PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		if (!this.mortAge.isReadonly()) {
			this.mortAge.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_mortAge.value"),PennantRegularExpressions.REGEX_NUMERIC, true));
		}*/
		if (!this.mortPricePF.isReadonly()) {
			this.mortPricePF.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_MortgageLoanDetailDialog_mortPricePF.value")));
		}
/*		if (!this.mortFinRatio.isReadonly()) {
			this.mortFinRatio.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_MortgageLoanDetailDialog_mortFinRatio.value")));
		}*/
		if (!this.mortgPurposeOfLoan.isReadonly()){
			this.mortgPurposeOfLoan.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgPurposeOfLoan.value"), PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}	
		/*if (!this.mortgAddrHNbr.isReadonly()){
			this.mortgAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrHNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}	
		if (!this.mortgAddrFlatNbr.isReadonly()){
			this.mortgAddrFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrFlatNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}	
		if (!this.mortgAddrStreet.isReadonly()){
			this.mortgAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrStreet.value"),PennantRegularExpressions.REGEX_ADDRESS, true));
		}	
	
		if (!this.mortgAddrPOBox.isReadonly()){
			this.mortgAddrPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}	
		if (!this.mortgAddrZIP.isReadonly()){
			this.mortgAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrZIP.value"),
					PennantRegularExpressions.REGEX_ZIP, false));
		}	
		if (!this.mortgAddrPhone.isReadonly()){
			this.mortgAddrPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrPhone.value"),true));
		}	
*/		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.mortgCurrentValue.setConstraint("");
		this.mortDeedNo.setConstraint("");
		this.mortStatus.setConstraint("");
		this.mortRegistrationNo.setConstraint("");
		this.mortAreaSF.setConstraint("");
		this.mortAreaSM.setConstraint("");
		this.mortAge.setConstraint("");
		this.mortPricePF.setConstraint("");
		this.mortFinRatio.setConstraint("");
		this.mortgPurposeOfLoan.setConstraint("");
		this.mortgAddrHNbr.setConstraint("");
		this.mortgAddrFlatNbr.setConstraint("");
		this.mortgAddrStreet.setConstraint("");
		this.mortgAddrPOBox.setConstraint("");
		this.mortgAddrZIP.setConstraint("");
		this.mortgAddrPhone.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * This method sets the validation for lovFields 
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.mortgProperty.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgProperty.value"), null, true,true));
		
		this.mortgPropertyRelation.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgPropertyRelation.value"), null, true,true));
		
		this.mortgOwnership.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgOwnership.value"), null, true,true));
		
		this.mortgAddrCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrCountry.value"), null, true,true));
		
		this.mortgAddrProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrProvince.value"), null, true,true));
		
		this.mortgAddrCity.setConstraint(new PTStringValidator(Labels.getLabel("label_MortgageLoanDetailDialog_MortgAddrCity.value"), null, true,true));
		logger.debug("Leaving");
	}
	/**
	 * this method removes the validation for lovFields 
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.mortgProperty.setConstraint("");
		this.mortgPropertyRelation.setConstraint("");
		this.mortgOwnership.setConstraint("");
		this.mortgAddrCountry.setConstraint("");
		this.mortgAddrProvince.setConstraint("");
		this.mortgAddrCity.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.mortgProperty.setErrorMessage("");
		this.mortgCurrentValue.setErrorMessage("");
		this.mortgPurposeOfLoan.setErrorMessage("");
		this.mortgPropertyRelation.setErrorMessage("");
		this.mortgOwnership.setErrorMessage("");
		this.mortgAddrHNbr.setErrorMessage("");
		this.mortgAddrFlatNbr.setErrorMessage("");
		this.mortgAddrStreet.setErrorMessage("");
		this.mortgAddrPOBox.setErrorMessage("");
		this.mortgAddrCountry.setErrorMessage("");
		this.mortgAddrProvince.setErrorMessage("");
		this.mortgAddrCity.setErrorMessage("");
		this.mortgAddrZIP.setErrorMessage("");
		this.mortgAddrPhone.setErrorMessage("");
		this.mortDeedNo.setErrorMessage("");
		this.mortStatus.setErrorMessage("");
		this.mortRegistrationNo.setErrorMessage("");
		this.mortAreaSF.setErrorMessage("");
		this.mortAreaSM.setErrorMessage("");
		this.mortAge.setErrorMessage("");
		this.mortPricePF.setErrorMessage("");
		this.mortFinRatio.setErrorMessage("");
		this.mortAreaSF.setErrorMessage("");
		this.mortAreaSM.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	/**
	 * Method for refreshing the list after successful update
	 */
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<MortgageLoanDetail> soMortgageLoanDetail = getMortgageLoanDetailListCtrl().getSearchObj();
		getMortgageLoanDetailListCtrl().pagingMortgageLoanDetailList.setActivePage(0);
		getMortgageLoanDetailListCtrl().getPagedListWrapper().setSearchObject(soMortgageLoanDetail);
		if(getMortgageLoanDetailListCtrl().listBoxMortgageLoanDetail!=null){
			getMortgageLoanDetailListCtrl().listBoxMortgageLoanDetail.getListModel();
		}
		logger.debug("Leaving");
	} 
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a MortgageLoanDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final MortgageLoanDetail aMortgageLoanDetail = new MortgageLoanDetail();
		BeanUtils.copyProperties(getMortgageLoanDetail(), aMortgageLoanDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + 
								"\n\n --> " + aMortgageLoanDetail.getId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aMortgageLoanDetail.getRecordType()).equals("")){
				aMortgageLoanDetail.setVersion(aMortgageLoanDetail.getVersion()+1);
				aMortgageLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aMortgageLoanDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aMortgageLoanDetail,tranType)){
					refreshList();
					closeDialog(this.window_MortgageLoanDetailDialog, "MortgageLoanDetailDialog"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new MortgageLoanDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final MortgageLoanDetail aMortgageLoanDetail = getMortgageLoanDetailService().getNewMortgageLoanDetail();
		setMortgageLoanDetail(aMortgageLoanDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.mortgProperty.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getMortgageLoanDetail().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}
		this.mortgProperty.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgProperty"));
		this.mortgCurrentValue.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgCurrentValue"));
		this.mortgPurposeOfLoan.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgPurposeOfLoan"));
		this.mortgPropertyRelation.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgPropertyRelation"));
		this.mortgOwnership.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgOwnership"));
		this.mortgAddrHNbr.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrHNbr"));
		this.mortgAddrFlatNbr.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrFlatNbr"));
		this.mortgAddrStreet.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrStreet"));
		this.mortgAddrLane1.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrLane1"));
		this.mortgAddrLane2.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrLane2"));
		this.mortgAddrPOBox.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrPOBox"));
		this.mortgAddrCountry.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrCountry"));
		this.mortgAddrZIP.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrZIP"));
		this.mortgAddrPhone.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortgAddrPhone"));
		
		this.mortDeedNo.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortDeedNo"));//TODO ADD RIGHT
		this.mortRegistrationNo.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortRegistrationNo"));//TODO ADD RIGHT
		this.mortAreaSF.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortAreaSF"));//TODO ADD RIGHT
		this.mortAreaSM.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortAreaSM"));//TODO ADD RIGHT
		this.mortPricePF.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortPricePF"));//TODO ADD RIGHT
		this.mortAge.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortAge"));//TODO ADD RIGHT
		this.mortFinRatio.setReadonly(isReadOnly("MortgageLoanDetailDialog_mortFinRatio"));//TODO ADD RIGHT
		this.mortStatus.setDisabled(isReadOnly("MortgageLoanDetailDialog_mortStatus"));//TODO ADD RIGHT

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.mortgageLoanDetail.isNewRecord()){
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
		this.mortgProperty.setReadonly(true);
		this.mortgCurrentValue.setReadonly(true);
		this.mortgPurposeOfLoan.setReadonly(true);
		this.mortgPropertyRelation.setReadonly(true);
		this.mortgOwnership.setReadonly(true);
		this.mortgAddrHNbr.setReadonly(true);
		this.mortgAddrFlatNbr.setReadonly(true);
		this.mortgAddrStreet.setReadonly(true);
		this.mortgAddrLane1.setReadonly(true);
		this.mortgAddrLane2.setReadonly(true);
		this.mortgAddrPOBox.setReadonly(true);
		this.mortgAddrCountry.setReadonly(true);
		this.mortgAddrProvince.setReadonly(true);
		this.mortgAddrCity.setReadonly(true);
		this.mortgAddrZIP.setReadonly(true);
		this.mortgAddrPhone.setReadonly(true);

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
		this.mortgProperty.setValue("");
		this.mortgProperty.setDescription("");
		this.mortgCurrentValue.setValue("");
		this.mortgPurposeOfLoan.setValue("");
		this.mortgPropertyRelation.setValue(String.valueOf(new Long(0)));
		this.mortgPropertyRelation.setDescription("");
		this.mortgOwnership.setValue(String.valueOf(new Long(0)));
		this.mortgOwnership.setDescription("");
		this.mortgAddrHNbr.setValue("");
		this.mortgAddrFlatNbr.setValue("");
		this.mortgAddrStreet.setValue("");
		this.mortgAddrLane1.setValue("");
		this.mortgAddrLane2.setValue("");
		this.mortgAddrPOBox.setValue("");
		this.mortgAddrCountry.setValue("");
		this.mortgAddrCountry.setDescription("");
		this.mortgAddrProvince.setValue("");
		this.mortgAddrProvince.setDescription("");
		this.mortgAddrCity.setValue("");
		this.mortgAddrCity.setDescription("");
		this.mortgAddrZIP.setValue("");
		this.mortgAddrPhone.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final MortgageLoanDetail aMortgageLoanDetail = new MortgageLoanDetail();
		BeanUtils.copyProperties(getMortgageLoanDetail(), aMortgageLoanDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		doSetLOVValidation();
		// fill the MortgageLoanDetail object with the components data
		doWriteComponentsToBean(aMortgageLoanDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aMortgageLoanDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aMortgageLoanDetail.getRecordType()).equals("")){
				aMortgageLoanDetail.setVersion(aMortgageLoanDetail.getVersion()+1);
				if(isNew){
					aMortgageLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aMortgageLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMortgageLoanDetail.setNewRecord(true);
				}
			}
		}else{
			aMortgageLoanDetail.setVersion(aMortgageLoanDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aMortgageLoanDetail,tranType)){
				refreshList();
				closeDialog(this.window_MortgageLoanDetailDialog, "MortgageLoanDetailDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the workFlow Details List to Object
	 * @param aMortgageLoanDetail (MortgageLoanDetail)
	 * @param tranType (String)
	 * @return boolean
	 */
	private boolean doProcess(MortgageLoanDetail aMortgageLoanDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aMortgageLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aMortgageLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMortgageLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String
			aMortgageLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aMortgageLoanDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aMortgageLoanDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aMortgageLoanDetail))) {
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

			aMortgageLoanDetail.setTaskId(taskId);
			aMortgageLoanDetail.setNextTaskId(nextTaskId);
			aMortgageLoanDetail.setRoleCode(getRole());
			aMortgageLoanDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aMortgageLoanDetail, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aMortgageLoanDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aMortgageLoanDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aMortgageLoanDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 *  Get the result after processing DataBase Operations
	 * @param auditHeader (AuditHeader)
	 * @param method (String)
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		MortgageLoanDetail aMortgageLoanDetail = (MortgageLoanDetail) auditHeader.getAuditDetail().
																		getModelData();

		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getMortgageLoanDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getMortgageLoanDetailService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getMortgageLoanDetailService().doApprove(auditHeader);

						if(aMortgageLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getMortgageLoanDetailService().doReject(auditHeader);
						if(aMortgageLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_MortgageLoanDetailDialog,
								auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_MortgageLoanDetailDialog,
						auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
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
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(MortgageLoanDetail aMortgageLoanDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMortgageLoanDetail.getBefImage(),
				aMortgageLoanDetail);   
		return new AuditHeader(aMortgageLoanDetail.getId(),null,null,null,auditDetail,
				aMortgageLoanDetail.getUserDetails(),getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_MortgageLoanDetailDialog, auditHeader);
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
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
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
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("MortgageLoanDetail");
		notes.setReference(getMortgageLoanDetail().getId());
		notes.setVersion(getMortgageLoanDetail().getVersion());
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

	public MortgageLoanDetail getMortgageLoanDetail() {
		return this.mortgageLoanDetail;
	}
	public void setMortgageLoanDetail(MortgageLoanDetail mortgageLoanDetail) {
		this.mortgageLoanDetail = mortgageLoanDetail;
	}

	public void setMortgageLoanDetailService(MortgageLoanDetailService mortgageLoanDetailService) {
		this.mortgageLoanDetailService = mortgageLoanDetailService;
	}
	public MortgageLoanDetailService getMortgageLoanDetailService() {
		return this.mortgageLoanDetailService;
	}

	public void setMortgageLoanDetailListCtrl(MortgageLoanDetailListCtrl mortgageLoanDetailListCtrl) {
		this.mortgageLoanDetailListCtrl = mortgageLoanDetailListCtrl;
	}
	public MortgageLoanDetailListCtrl getMortgageLoanDetailListCtrl() {
		return this.mortgageLoanDetailListCtrl;
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
