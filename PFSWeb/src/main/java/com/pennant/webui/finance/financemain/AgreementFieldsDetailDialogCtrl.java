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
 * FileName    		:  AgreementFieldsDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-06-2016    														*
 *                                                                  						*
 * Modified Date    :  29-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AgreementFieldDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.finance.AgreementFieldsDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/AgreementFields/AgreementFieldsDetailDialog.zul file.
 */
public class AgreementFieldsDetailDialogCtrl extends GFCBaseCtrl<AgreementFieldDetails> {
	private static final long serialVersionUID = 3141943554064485540L;
	private static final Logger logger = Logger.getLogger(AgreementFieldsDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_AgreementFieldsDetailDialog;// autowired
	protected Groupbox 		gb_basicDetails; 			// autowired
	protected Groupbox 		gbx_ArabicFieldDetail; 			// autowired

	
	protected Textbox    custCityArabic;
	protected Textbox    sellerNameArabic;
	protected Textbox    custNationalityArabic;
	protected Textbox    plotUnitNumArabic;
	protected Textbox    otherbankNameArabic;
	protected Textbox    propertyTypeArabic;
	protected Textbox    sectorOrComArabic;
	protected Textbox    finAmountArabic;
	protected Textbox    propertyDescArabic;
	protected Textbox    propertyLocArabic;
	
	
	
	protected Textbox    custPoBox;
	protected Textbox    JointApplicant;
	protected Textbox    sellerNationalityArabic;
	protected Textbox    Sellercity;
	protected Textbox    propertyUse;
	protected Textbox    plotArea;
	protected Textbox    builtUpArea;
	protected Textbox    ahbBranch;
	protected Textbox    institution;
	protected Textbox    facilityName;
	protected Textbox    sellerContributionAmt;
	protected Textbox    custContributionAmt;
	protected Textbox    beneficiaryAmt;
	protected Textbox    propertyOwner;
	protected Textbox    collateralAuthority;
	protected Textbox    collaeral1;
	protected Textbox    area;
	protected Textbox    sellerInternal;
	
	protected Space space_sellerName;
	protected Space space_sellerCity;
	protected Space space_SellerNationality;
	protected Space space_JointApplicantOrBorrower;
	protected Space space_AhbBranch;
	protected Space space_Institution;
	protected Space space_FinAmountArabic;
	protected Space space_CustomerContribution;
	protected Space space_sellerContribution;
	protected Space space_BeneficiaryAmt;
	
	// not auto wired vars
	private AgreementFieldDetails agreementFieldDetails; 							 // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient AgreementFieldsDetailService agreementFieldsDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	//For Dynamically calling of this Controller
	private Div toolbar;
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private Grid grid_basicDetails;

	private transient boolean   newFinance;
	public transient int   ccyFormatter = 0;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;

	/**
	 * default constructor.<br>
	 */
	public AgreementFieldsDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AgreementFieldsDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected AgreementFieldDetails object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AgreementFieldsDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AgreementFieldsDetailDialog);

		if(event.getTarget().getParent() != null){
			parent = event.getTarget().getParent();
		}
		
		parent.setAttribute("ca:data-scrollable", "true");

		// READ OVERHANDED params !
		if (arguments.containsKey("agreementFieldDetail")) {
			this.agreementFieldDetails = (AgreementFieldDetails) arguments.get("agreementFieldDetail");
			AgreementFieldDetails befImage = new AgreementFieldDetails();
			BeanUtils.copyProperties(this.agreementFieldDetails, befImage);
			this.agreementFieldDetails.setBefImage(befImage);
			setAgreementFieldDetails(this.agreementFieldDetails);
		} else {
			setAgreementFieldDetails(null);
		}

		if (arguments.containsKey("financeDetail")) {
			setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
			if (getFinancedetail()!=null) {
				setAgreementFieldDetails(getFinancedetail().getAgreementFieldDetails());
			}
		}
		
		if(arguments.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
			try {
					financeMainDialogCtrl.getClass().getMethod("setAgreementFieldsDetailDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			setNewFinance(true);
			this.window_AgreementFieldsDetailDialog.setTitle("");
		}

		if(arguments.containsKey("roleCode")){
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateRoleAuthorities(getRole(), "AgreementFieldsDetailDialog");
		}

		if(arguments.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)arguments.get("ccyFormatter");
		}		
		
		if (arguments.containsKey("parentTab")) {
			parentTab = (Tab) arguments.get("parentTab");
		}
		
		if (isWorkFlowEnabled() && !isNewFinance()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "AgreementFieldsDetailDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getAgreementFieldDetails());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCityArabic.setMaxlength(50);
		this.sellerNameArabic.setMaxlength(50);
		this.custNationalityArabic.setMaxlength(50);
		this.plotUnitNumArabic.setMaxlength(50);
		this.otherbankNameArabic.setMaxlength(50);
		this.propertyTypeArabic.setMaxlength(50);
		this.finAmountArabic.setMaxlength(100);
		this.propertyDescArabic.setMaxlength(100);
		this.propertyLocArabic.setMaxlength(100);
		this.sellerNationalityArabic.setMaxlength(50);
		this.Sellercity.setMaxlength(50);
		this.propertyUse.setMaxlength(50);
		this.JointApplicant.setMaxlength(50);
		this.plotArea.setMaxlength(50);
		this.builtUpArea.setMaxlength(50);
		this.ahbBranch.setMaxlength(50);
		this.institution.setMaxlength(50);
		this.facilityName.setMaxlength(50);
		this.sellerContributionAmt.setMaxlength(50);
		this.custContributionAmt.setMaxlength(50);
		this.beneficiaryAmt.setMaxlength(50);
		this.propertyOwner.setMaxlength(50);
		this.collateralAuthority.setMaxlength(50);
		this.collaeral1.setMaxlength(50);
		this.sellerInternal.setMaxlength(50);
		this.area.setMaxlength(50);
		
		
		logger.debug("Entering");
		
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

		getUserWorkspace().allocateAuthorities("AgreementFieldsDetailDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AgreementFieldsDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AgreementFieldsDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AgreementFieldsDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AgreementFieldsDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}


	public void doSave_AgreementFieldsDetail(FinanceDetail financeDetail,boolean recSave) throws InterruptedException{
		logger.debug("Entering");

		doClearMessage();
		if(!recSave && allowValidation()){
			doSetValidation();	
			
		}
		AgreementFieldDetails agreementFieldDetails =getAgreementFieldDetails();
		doWriteComponentsToBean(agreementFieldDetails);
		if(StringUtils.isBlank(getAgreementFieldDetails().getRecordType())){
			agreementFieldDetails.setVersion(getAgreementFieldDetails().getVersion() + 1);
			agreementFieldDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			agreementFieldDetails.setNewRecord(true);
		}
		agreementFieldDetails.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		agreementFieldDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		agreementFieldDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		agreementFieldDetails.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setAgreementFieldDetails(agreementFieldDetails);
		logger.debug("Leaving");
	}

	

	private boolean allowValidation(){
		return false;
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
		MessageUtil.showHelpWindow(event, window_AgreementFieldsDetailDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.agreementFieldDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param AgreementFieldDetails
	 *            aAgreementFieldDetail
	 */
	public void doWriteBeanToComponents(AgreementFieldDetails aAgreementFieldDetails) {
		logger.debug("Entering");
		
		this.custCityArabic.setValue(aAgreementFieldDetails.getCustCity());
		this.sellerNameArabic.setValue(aAgreementFieldDetails.getSellerName());
		this.custNationalityArabic.setValue(aAgreementFieldDetails.getCustNationality());
		this.plotUnitNumArabic.setValue(aAgreementFieldDetails.getPlotOrUnitNo());
		this.otherbankNameArabic.setValue(aAgreementFieldDetails.getOtherbankName());
		this.propertyTypeArabic.setValue(aAgreementFieldDetails.getPropertyType());
		this.sectorOrComArabic.setValue(aAgreementFieldDetails.getSectorOrCommunity());
		this.finAmountArabic.setValue(aAgreementFieldDetails.getFinAmount());
		this.propertyDescArabic.setValue(aAgreementFieldDetails.getProprtyDesc());
		this.propertyLocArabic.setValue(aAgreementFieldDetails.getPropertyLocation());
		this.JointApplicant.setValue(aAgreementFieldDetails.getJointApplicant()); 
		this.custPoBox.setValue(aAgreementFieldDetails.getCustPoBox()); 
		this.sellerNationalityArabic.setValue(aAgreementFieldDetails.getSellerNationality());
		this.Sellercity.setValue(aAgreementFieldDetails.getSellerPobox());
		this.propertyUse.setValue(aAgreementFieldDetails.getPropertyUse());
		this.plotArea.setValue(aAgreementFieldDetails.getPlotareainsqft());
		this.builtUpArea.setValue(aAgreementFieldDetails.getBuiltupAreainSqft());
		this.ahbBranch.setValue(aAgreementFieldDetails.getAhbBranch());
		this.institution.setValue(aAgreementFieldDetails.getFininstitution());
		this.facilityName.setValue(aAgreementFieldDetails.getFacilityName());
		this.sellerContributionAmt.setValue(aAgreementFieldDetails.getSellerCntbAmt());
		this.custContributionAmt.setValue(aAgreementFieldDetails.getCustCntAmt());
		this.beneficiaryAmt.setValue(aAgreementFieldDetails.getOtherBankAmt());
		this.propertyOwner.setValue(aAgreementFieldDetails.getPropertyOwner());
		this.collateralAuthority.setValue(aAgreementFieldDetails.getCollateralAuthority());
		this.collaeral1.setValue(aAgreementFieldDetails.getCollateral1());
		this.sellerInternal.setValue(aAgreementFieldDetails.getSellerInternal());
		this.area.setValue(aAgreementFieldDetails.getArea());
		
		this.recordStatus.setValue(aAgreementFieldDetails.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param AgreementFieldDetail
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(AgreementFieldDetails agreementFieldDetails) throws InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			agreementFieldDetails.setCustCity(this.custCityArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		
		try {
			agreementFieldDetails.setCustPoBox(this.custPoBox.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setCustNationality(this.custNationalityArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setSellerName(this.sellerNameArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		
		try {
			agreementFieldDetails.setPlotOrUnitNo(this.plotUnitNumArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setOtherbankName(this.otherbankNameArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setPropertyType(this.propertyTypeArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setSectorOrCommunity(this.sectorOrComArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setFinAmount(this.finAmountArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
			
		}
		try {
			agreementFieldDetails.setProprtyDesc(this.propertyDescArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setPropertyLocation(this.propertyLocArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setJointApplicant(this.JointApplicant.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		
		try {
			agreementFieldDetails.setSellerNationality(this.sellerNationalityArabic.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setSellerPobox(this.Sellercity.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setPropertyUse(this.propertyUse.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setPlotareainsqft(this.plotArea.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setBuiltupAreainSqft(this.builtUpArea.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setAhbBranch(this.ahbBranch.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setFininstitution(this.institution.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setSellerCntbAmt(this.sellerContributionAmt.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setCustCntAmt(this.custContributionAmt.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setOtherBankAmt(this.beneficiaryAmt.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setPropertyOwner(this.propertyOwner.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setCollateralAuthority(this.collateralAuthority.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setCollateral1(this.collaeral1.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setSellerInternal(this.sellerInternal.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setArea(this.area.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			agreementFieldDetails.setFacilityName(this.facilityName.getValue());
		} catch (WrongValueException e) {
			wve.add(e);
		}
		showErrorDetails(wve);
		agreementFieldDetails.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
		
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		doRemoveValidation();
	
			if (wve.size() > 0) {
				logger.debug("Throwing occured Errors By using WrongValueException");
				if(parentTab != null){
					parentTab.setSelected(true);
				}
				
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
					if(i == 0){
						Component comp = wvea[i].getComponent();
						if(comp instanceof HtmlBasedComponent){
							Clients.scrollIntoView(comp);
						}
					}
					logger.debug(wvea[i]);
				}
				throw new WrongValuesException(wvea);
			}
		
		logger.debug("Leaving");
	}
	
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aAgreementFieldDetails
	 * @throws InterruptedException
	 */
	public void doShowDialog(AgreementFieldDetails aAgreementFieldDetails) throws InterruptedException {
		logger.debug("Entering");

		getBorderLayoutHeight();
		// append finance basic details
		appendFinBasicDetails();

		// if aAgreementFieldDetails == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aAgreementFieldDetails == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			aAgreementFieldDetails = new AgreementFieldDetails();
			aAgreementFieldDetails.setNewRecord(true);
			setAgreementFieldDetails(aAgreementFieldDetails);
		} else {
			setAgreementFieldDetails(aAgreementFieldDetails);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aAgreementFieldDetails.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus

		} else {

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
			doWriteBeanToComponents(aAgreementFieldDetails);

			if (parent != null) {
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.gb_basicDetails.setHeight("100%");
				int visibleRows = grid_basicDetails.getRows().getVisibleItemCount();
				this.gbx_ArabicFieldDetail.setHeight(visibleRows * 22 + 190 + "px");
				this.window_AgreementFieldsDetailDialog.setHeight(visibleRows * 22 + 550 + "px");
				parent.appendChild(this.window_AgreementFieldsDetailDialog);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.custNationalityArabic.isReadonly()) {
		      this.custNationalityArabic.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_CustNationalityArabic.value"),null,true));
		}
		if (!this.propertyUse.isReadonly()) {
		      this.propertyUse.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_PropertyUse.value"),null,true));
		}
		if (!this.propertyDescArabic.isReadonly()) {
		      this.propertyDescArabic.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_PropertyDescArabic.value"),null,true));
		}
		if (!this.propertyOwner.isReadonly()) {
		      this.propertyOwner.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_PropertyOwner.value"),null,true));
		}
		if (!this.plotUnitNumArabic.isReadonly()) {
		      this.plotUnitNumArabic.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_PlotUnitNumArabic.value"),null,true));
		}
		if (!this.collateralAuthority.isReadonly()) {
			  this.collateralAuthority.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_CollateralAuthority.value"),null,true));
		}
		if (!this.custCityArabic.isReadonly()) {
			this.custCityArabic.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_CustCityArabic.value"),null,true));
		}
		if (!this.builtUpArea.isReadonly()) {
			this.builtUpArea.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_BuiltUpArea.value"),null,true));
		}
		if (!this.custPoBox.isReadonly()) {
			this.custPoBox.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_CustomerPoBox.value"),null,true));
		}
		if (!this.collaeral1.isReadonly()) {
			this.collaeral1.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_collaeral1.value"),null,true));
		}
		if (!this.area.isReadonly()) {
			this.area.setConstraint(new PTStringValidator(Labels.getLabel("label_AgreementDialog_collaeral1.value"),null,true));
		}
		
		
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		
		this.custNationalityArabic.setConstraint("");
		this.propertyUse.setConstraint("");
		this.propertyDescArabic.setConstraint("");
		this.propertyOwner.setConstraint("");
		this.plotUnitNumArabic.setConstraint("");
		this.collateralAuthority.setConstraint("");
		
		logger.debug("Leaving");
	}


	
	/**
	 * Method for clear the error Messages 
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custNationalityArabic.setErrorMessage("");
		this.propertyUse.setErrorMessage("");
		this.propertyDescArabic.setErrorMessage("");
		this.propertyOwner.setErrorMessage("");
		this.plotUnitNumArabic.setErrorMessage("");
		this.collateralAuthority.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}
	
	// CRUD operations

	/**
	 * Deletes a AgreementFieldDetails object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final AgreementFieldDetails aAgreementFieldDetails = new AgreementFieldDetails();
		BeanUtils.copyProperties(getAgreementFieldDetails(), aAgreementFieldDetails);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aAgreementFieldDetails.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aAgreementFieldDetails.getRecordType())) {
				aAgreementFieldDetails.setVersion(aAgreementFieldDetails.getVersion() + 1);
				aAgreementFieldDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAgreementFieldDetails.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAgreementFieldDetails, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getAgreementFieldDetails().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.custCityArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_custCity"));
		this.sellerNameArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_sellerName"));
		this.custNationalityArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_custNationality"));
		this.plotUnitNumArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_plotOrUnitNo"));
		this.otherbankNameArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_otherbankName"));
		this.propertyTypeArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_propertyType"));
		this.sectorOrComArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_sectorOrCommunity"));
		this.finAmountArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_finAmount"));
		this.propertyDescArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_proprtyDesc"));
		this.propertyLocArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_propertyLocation"));
		
		this.custPoBox.setReadonly(isReadOnly("AgreementFieldsDetailDialog_CustPoBox"));
		this.JointApplicant.setReadonly(isReadOnly("AgreementFieldsDetailDialog_JointApplicant"));
		this.sellerNationalityArabic.setReadonly(isReadOnly("AgreementFieldsDetailDialog_sellerNationalityArabic"));
		this.Sellercity.setReadonly(isReadOnly("AgreementFieldsDetailDialog_Sellercity"));
		this.propertyUse.setReadonly(isReadOnly("AgreementFieldsDetailDialog_propertyUse"));
		this.plotArea.setReadonly(isReadOnly("AgreementFieldsDetailDialog_plotArea"));
		this.builtUpArea.setReadonly(isReadOnly("AgreementFieldsDetailDialog_builtUpArea"));
		this.ahbBranch.setReadonly(isReadOnly("AgreementFieldsDetailDialog_ahbBranch"));
		this.institution.setReadonly(isReadOnly("AgreementFieldsDetailDialog_institution"));
		this.facilityName.setReadonly(isReadOnly("AgreementFieldsDetailDialog_facilityName"));
		this.sellerContributionAmt.setReadonly(isReadOnly("AgreementFieldsDetailDialog_sellerContributionAmt"));
		this.custContributionAmt.setReadonly(isReadOnly("AgreementFieldsDetailDialog_custContributionAmt"));
		this.beneficiaryAmt.setReadonly(isReadOnly("AgreementFieldsDetailDialog_beneficiaryAmt"));
		this.propertyOwner.setReadonly(isReadOnly("AgreementFieldsDetailDialog_propertyOwner"));
		this.collateralAuthority.setReadonly(isReadOnly("AgreementFieldsDetailDialog_collateralAuthority"));
		this.collaeral1.setReadonly(isReadOnly("AgreementFieldsDetailDialog_collaeral1"));
		this.sellerInternal.setReadonly(isReadOnly("AgreementFieldsDetailDialog_sellerInternal"));
		this.area.setReadonly(isReadOnly("AgreementFieldsDetailDialog_area"));
		
		
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.agreementFieldDetails.isNewRecord()) {
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
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final AgreementFieldDetails aAgreementFieldDetails = new AgreementFieldDetails();
		BeanUtils.copyProperties(getAgreementFieldDetails(), aAgreementFieldDetails);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		
		// fill the AgreementFieldDetails object with the components data
		doWriteComponentsToBean(aAgreementFieldDetails);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aAgreementFieldDetails.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAgreementFieldDetails.getRecordType())) {
				aAgreementFieldDetails.setVersion(aAgreementFieldDetails.getVersion() + 1);
				if (isNew) {
					aAgreementFieldDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAgreementFieldDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAgreementFieldDetails.setNewRecord(true);
				}
			}
		} else {
			aAgreementFieldDetails.setVersion(aAgreementFieldDetails.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aAgreementFieldDetails, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAgreementFieldDetails (AgreementFieldDetail)
	 * @param tranType (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(AgreementFieldDetails aAgreementFieldDetails, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAgreementFieldDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAgreementFieldDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAgreementFieldDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAgreementFieldDetails.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAgreementFieldDetails.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAgreementFieldDetails);
				}

				if (isNotesMandatory(taskId, aAgreementFieldDetails)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aAgreementFieldDetails.setTaskId(taskId);
			aAgreementFieldDetails.setNextTaskId(nextTaskId);
			aAgreementFieldDetails.setRoleCode(getRole());
			aAgreementFieldDetails.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAgreementFieldDetails, tranType);

			String operationRefs = getServiceOperations(taskId, aAgreementFieldDetails);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAgreementFieldDetails, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAgreementFieldDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		AgreementFieldDetails aAgreementFieldDetails = (AgreementFieldDetails) auditHeader.getAuditDetail().getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getAgreementFieldsDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAgreementFieldsDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getAgreementFieldsDetailService().doApprove(auditHeader);

						if (aAgreementFieldDetails.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAgreementFieldsDetailService().doReject(auditHeader);
						if (aAgreementFieldDetails.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_AgreementFieldsDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_AgreementFieldsDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.agreementFieldDetails), true);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAgreementFieldDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(AgreementFieldDetails aAgreementFieldDetails,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aAgreementFieldDetails.getBefImage(), aAgreementFieldDetails);
		return new AuditHeader(String.valueOf(aAgreementFieldDetails.getId()),
				null, null, null, auditDetail,
				aAgreementFieldDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_AgreementFieldsDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.agreementFieldDetails);
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.agreementFieldDetails.getId());
	}

	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
		
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}
	
	public AgreementFieldsDetailService getAgreementFieldsDetailService() {
		return agreementFieldsDetailService;
	}

	public void setAgreementFieldsDetailService(
			AgreementFieldsDetailService agreementFieldsDetailService) {
		this.agreementFieldsDetailService = agreementFieldsDetailService;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
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
	
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}
	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
	
	public AgreementFieldDetails getAgreementFieldDetails() {
		return agreementFieldDetails;
	}

	public void setAgreementFieldDetails(AgreementFieldDetails agreementFieldDetails) {
		this.agreementFieldDetails = agreementFieldDetails;
	}

}
