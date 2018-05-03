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
 * FileName    		:  FacilityDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.facility.facility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.finance.FacilityType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.PTCKeditor;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/FacilityDetail/facilityDetailDialog.zul file.
 */
public class FacilityDetailDialogCtrl extends GFCBaseCtrl<FacilityDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FacilityDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FacilityDetailDialog; 
	protected Textbox cAFReference;   
	protected Textbox facilityRef;    
	protected Textbox facilityFor;    
	protected ExtendedCombobox facilityType; 
	protected ExtendedCombobox facilityCCY; 
	protected Decimalbox exposure; 
	protected Decimalbox existingLimit; 
	protected CurrencyBox newLimit; 
	protected Decimalbox financeAmount; 
	protected Textbox pricing; 
	protected Textbox repayments; 
	protected Textbox rateType; 
	protected Textbox lCPeriod; 
	protected Textbox usancePeriod; 
	protected Checkbox securityClean; 
	protected PTCKeditor securityDesc; 
	protected Textbox utilization; 
	protected PTCKeditor commission; 
	protected PTCKeditor purpose; 
	protected PTCKeditor guarantee; 
	protected PTCKeditor covenants; 
	protected PTCKeditor documentsRequired; 
	protected Datebox startDate; 
	protected Datebox maturityDate; 
	
	protected Intbox tenorYear; 
	protected Intbox tenorMonth; 
	protected Textbox tenorDesc; 
	
	protected Combobox          transactionType;   
	protected Textbox           agentBank;         
	protected Textbox           otherDetails;      
	protected CurrencyBox       totalFacility;     
	protected ExtendedCombobox  totalFacilityCcy;  
	protected CurrencyBox       underWriting;      
	protected ExtendedCombobox  underWritingCcy;   
	protected CurrencyBox       propFinalTake;     
	protected ExtendedCombobox  propFinalTakeCcy;  
	protected Row               row_totalFacility; 
	protected Row               row_underWriting;  
	protected Row               row_propFinalTake; 
	
	//protected Space space_AgentBank;
	//protected Space space_OtherDetails;
	
	// not auto wired vars
	private FacilityDetail facilityDetail; // overhanded per param
	private FacilityDetail prvFacilityDetail; // overhanded per param
	private transient FacilityDetailListCtrl facilityDetailListCtrl; // overhanded

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();
	
	private List<FacilityDetail> facilityDetailsList;
	private int ccyFormat=0;
	Commitment commitment=null;
	String userRole="";
	Date appldate = DateUtility.getAppDate();
	/**
	 * default constructor.<br>
	 */
	public FacilityDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FacilityDetailDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FacilityDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FacilityDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FacilityDetailDialog);

		try {
			if (arguments.containsKey("facilityDetail")) {
				this.facilityDetail = (FacilityDetail) arguments.get("facilityDetail");
				FacilityDetail befImage = new FacilityDetail();
				BeanUtils.copyProperties(this.facilityDetail, befImage);
				this.facilityDetail.setBefImage(befImage);
				setFacilityDetail(this.facilityDetail);
				
				ccyFormat= CurrencyUtil.getFormat(getFacilityDetail().getFacilityCCY());
			} else {
				setFacilityDetail(null);
			}
			this.facilityDetail.setWorkflowId(0);
			doLoadWorkFlow(this.facilityDetail.isWorkflow(), this.facilityDetail.getWorkflowId(), this.facilityDetail.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FacilityDetailDialog");
			}
			if (arguments.containsKey("role")) {
				userRole=arguments.get("role").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, "FacilityDetailDialog");
			}
			if (arguments.containsKey("commitment")) {
				commitment=(Commitment) arguments.get("commitment");
			}
			doCheckRights();
			// READ OVERHANDED params !
			// we get the facilityDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete facilityDetail here.
			if (arguments.containsKey("facilityDetailListCtrl")) {
				setFacilityDetailListCtrl((FacilityDetailListCtrl) arguments.get("facilityDetailListCtrl"));
			} else {
				setFacilityDetailListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFacilityDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FacilityDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cAFReference.setMaxlength(20);
		this.facilityRef.setMaxlength(20);
		this.facilityFor.setMaxlength(50);
		this.facilityType.setMaxlength(8);
        this.facilityType.setMandatoryStyle(true);
        this.facilityType.setModuleName("FacilityType");
		this.facilityType.setValueColumn("FacilityType");
		this.facilityType.setDescColumn("FacilityDesc");
		this.facilityType.setValidateColumns(new String[] { "FacilityType" });
		this.facilityCCY.setMaxlength(3);
		this.facilityCCY.setMandatoryStyle(true);
		this.facilityCCY.setModuleName("Currency");
		this.facilityCCY.setValueColumn("CcyCode");
		this.facilityCCY.setDescColumn("CcyDesc");
		this.facilityCCY.setValidateColumns(new String[] { "CcyCode" });
		this.exposure.setMaxlength(18);
		this.exposure.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.exposure.setScale(ccyFormat);
		this.existingLimit.setMaxlength(18);
		this.existingLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.existingLimit.setScale(ccyFormat);
		this.newLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.newLimit.setScale(ccyFormat);
		this.newLimit.setMandatory(true);
		this.financeAmount.setMaxlength(18);
		this.financeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.financeAmount.setScale(ccyFormat);
		this.pricing.setMaxlength(200);
		this.repayments.setMaxlength(200);
		this.rateType.setMaxlength(50);
		this.lCPeriod.setMaxlength(50);
		this.usancePeriod.setMaxlength(200);
		this.utilization.setMaxlength(200);
		this.tenorYear.setMaxlength(4);
		this.tenorMonth.setMaxlength(2);
		this.tenorDesc.setMaxlength(200);
		
		this.agentBank.setMaxlength(200);
		this.otherDetails.setMaxlength(200);
		
		int facilityFormatter = CurrencyUtil.getFormat(getFacilityDetail().getFacilityCCY());
		int writingFormatter = CurrencyUtil.getFormat(getFacilityDetail().getUnderWritingCcy());
		int takeFormatter = CurrencyUtil.getFormat(getFacilityDetail().getFacilityCCY());
		
		this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(facilityFormatter));
		this.totalFacility.setScale(facilityFormatter);
		this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(writingFormatter));
		this.underWriting.setScale(writingFormatter);
		this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(takeFormatter));
		this.propFinalTake.setScale(takeFormatter);
		this.totalFacilityCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.totalFacilityCcy.setMandatoryStyle(true);
		this.totalFacilityCcy.setModuleName("Currency");
		this.totalFacilityCcy.setValueColumn("CcyCode");
		this.totalFacilityCcy.setDescColumn("CcyDesc");
		this.totalFacilityCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.underWritingCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.underWritingCcy.setMandatoryStyle(true);
		this.underWritingCcy.setModuleName("Currency");
		this.underWritingCcy.setValueColumn("CcyCode");
		this.underWritingCcy.setDescColumn("CcyDesc");
		this.underWritingCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.propFinalTakeCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.propFinalTakeCcy.setMandatoryStyle(true);
		this.propFinalTakeCcy.setModuleName("Currency");
		this.propFinalTakeCcy.setValueColumn("CcyCode");
		this.propFinalTakeCcy.setDescColumn("CcyDesc");
		this.propFinalTakeCcy.setValidateColumns(new String[] { "CcyCode" });
		
//		this.securityDesc.setMaxlength(2000);
//		this.commission.setMaxlength(2000);
//		this.purpose.setMaxlength(2000);
//		this.guarantee.setMaxlength(2000);
//		this.covenants.setMaxlength(2000);
		
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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
		getUserWorkspace().allocateAuthorities("FacilityDetailDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnSave"));
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
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_FacilityDetailDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
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
		doWriteBeanToComponents(this.facilityDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFacilityDetail
	 *            FacilityDetail
	 */
	public void doWriteBeanToComponents(FacilityDetail aFacilityDetail) {
		logger.debug("Entering");
		if (commitment ==null && !aFacilityDetail.isNewRecord() && !getFacilityDetail().getFacilityFor().equals(FacilityConstants.FACILITY_NEW)) {
			commitment=getCommitmentRef(aFacilityDetail.getFacilityRef());
		}
		this.cAFReference.setValue(aFacilityDetail.getCAFReference());
		this.facilityRef.setValue(aFacilityDetail.getFacilityRef());
		this.facilityFor.setValue(PennantStaticListUtil.getlabelDesc(aFacilityDetail.getFacilityFor(), PennantStaticListUtil.getFacilityApprovalFor()));
		this.facilityType.setValue(aFacilityDetail.getFacilityType(),StringUtils.trimToEmpty(aFacilityDetail.getFacilityTypeDesc()));
		this.facilityCCY.setValue(aFacilityDetail.getFacilityCCY(),CurrencyUtil.getCcyDesc(aFacilityDetail.getFacilityCCY()));
		if (commitment!=null) {
			this.existingLimit.setValue(PennantAppUtil.formateAmount(commitment.getCmtAmount(), ccyFormat));
			this.exposure.setValue(PennantAppUtil.formateAmount(commitment.getCmtAvailable(), ccyFormat));
		}else{
			this.existingLimit.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, ccyFormat));
			this.exposure.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, ccyFormat));
		}
		this.newLimit.setValue(PennantAppUtil.formateAmount(aFacilityDetail.getNewLimit(), ccyFormat));
		this.financeAmount.setValue(PennantAppUtil.formateAmount(aFacilityDetail.getFinanceAmount(), ccyFormat));
		this.pricing.setValue(aFacilityDetail.getPricing());
		this.repayments.setValue(aFacilityDetail.getRepayments());
		this.rateType.setValue(aFacilityDetail.getRateType());
		this.lCPeriod.setValue(aFacilityDetail.getLCPeriod());
		this.usancePeriod.setValue(aFacilityDetail.getUsancePeriod());
		this.securityClean.setChecked(aFacilityDetail.isSecurityClean());
		this.securityDesc.setValue(aFacilityDetail.getSecurityDesc());
		this.utilization.setValue(aFacilityDetail.getUtilization());
		this.commission.setValue(aFacilityDetail.getCommission());
		this.purpose.setValue(aFacilityDetail.getPurpose());
		this.guarantee.setValue(aFacilityDetail.getGuarantee());
		this.covenants.setValue(aFacilityDetail.getCovenants());
		this.documentsRequired.setValue(aFacilityDetail.getDocumentsRequired());
		this.startDate.setValue(aFacilityDetail.getStartDate());
		this.tenorYear.setValue(aFacilityDetail.getTenorYear());
		this.tenorMonth.setValue(aFacilityDetail.getTenorMonth());
		this.tenorDesc.setValue(aFacilityDetail.getTenorDesc());
		
		fillComboBox(this.transactionType, aFacilityDetail.getTransactionType(), PennantStaticListUtil.getTransactionTypesList(), "");
		this.agentBank.setValue(aFacilityDetail.getAgentBank());
		this.otherDetails.setValue(aFacilityDetail.getOtherDetails());
		this.totalFacility.setValue(PennantApplicationUtil.formateAmount(aFacilityDetail.getTotalFacility(), CurrencyUtil.getFormat(aFacilityDetail.getTotalFacilityCcy())));
		this.totalFacilityCcy.setValue(aFacilityDetail.getTotalFacilityCcy(), CurrencyUtil.getCcyDesc(aFacilityDetail.getTotalFacilityCcy()));
		this.underWriting.setValue(PennantApplicationUtil.formateAmount(aFacilityDetail.getUnderWriting(),CurrencyUtil.getFormat(aFacilityDetail.getUnderWritingCcy())));
		this.underWritingCcy.setValue(aFacilityDetail.getUnderWritingCcy(), CurrencyUtil.getCcyDesc(aFacilityDetail.getUnderWritingCcy()));
		this.propFinalTake.setValue(PennantApplicationUtil.formateAmount(aFacilityDetail.getPropFinalTake(), CurrencyUtil.getFormat(aFacilityDetail.getPropFinalTakeCcy())));
		this.propFinalTakeCcy.setValue(aFacilityDetail.getPropFinalTakeCcy(),CurrencyUtil.getCcyDesc(aFacilityDetail.getPropFinalTakeCcy()));
		
		this.maturityDate.setValue(aFacilityDetail.getMaturityDate());
		this.recordStatus.setValue(aFacilityDetail.getRecordStatus());
		
		
		onCheckSecurity();
		doDesignByMode();
		doCheckTransactionType();
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFacilityDetail
	 */
	public void doWriteComponentsToBean(FacilityDetail aFacilityDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aFacilityDetail.setCAFReference(this.cAFReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setFacilityRef(this.facilityRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			//aFacilityDetail.setFacilityFor(this.facilityFor.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setFacilityType(this.facilityType.getValue());
			aFacilityDetail.setFacilityTypeDesc(this.facilityType.getDescription());
			Object object=this.facilityType.getObject();
			if (object!=null) {
				FacilityType facilityType=(FacilityType) object;
				aFacilityDetail.setRevolving(facilityType.getRevolving());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setFacilityCCY(this.facilityCCY.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.exposure.getValue() != null) {
				aFacilityDetail.setExposure(PennantApplicationUtil.unFormateAmount(this.exposure.getValue(), ccyFormat));
				
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.existingLimit.getValue() != null) {
				aFacilityDetail.setExistingLimit(PennantApplicationUtil.unFormateAmount(this.existingLimit.getValue(), ccyFormat));
				
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setNewLimit(PennantApplicationUtil.unFormateAmount(this.newLimit.getValidateValue(), ccyFormat));
			//New Commitment amount cannot be less than Utilized Amount
			if (commitment!=null) {
				if (aFacilityDetail.getNewLimit().compareTo(commitment.getCmtUtilizedAmount()) < 0) {
					throw new WrongValueException(this.newLimit, Labels.getLabel("AMOUNT_NO_LESS", new String[] { Labels.getLabel("label_CommitmentDialog_CmtAmount.value"),
					        Labels.getLabel("label_CommitmentDialog_CmtUtilizedAmount.value") }));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.financeAmount.getValue() != null) {
				aFacilityDetail.setFinanceAmount(PennantApplicationUtil.unFormateAmount(this.financeAmount.getValue(), ccyFormat));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setPricing(this.pricing.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setRepayments(this.repayments.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setRateType(this.rateType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setLCPeriod(this.lCPeriod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setUsancePeriod(this.usancePeriod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setSecurityClean(this.securityClean.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setSecurityDesc(this.securityDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setUtilization(this.utilization.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setCommission(this.commission.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setPurpose(this.purpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setGuarantee(this.guarantee.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setCovenants(this.covenants.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setDocumentsRequired(this.documentsRequired.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.tenorMonth.intValue()==0 && this.tenorYear.intValue()==0) {
				this.tenorYear.setConstraint(new PTNumberValidator(Labels.getLabel("label_FacilityDetailDialog_TenorYear.value"),  true, false));
			}
			aFacilityDetail.setTenorYear(this.tenorYear.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
		
			aFacilityDetail.setTenorMonth(this.tenorMonth.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setTenorDesc(this.tenorDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.startDate.getValue()!=null && this.maturityDate.getValue()!=null) {
				this.maturityDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDetailDialog_MaturityDate.value"), false, this.startDate.getValue(), null, true));
			}
			aFacilityDetail.setMaturityDate(this.maturityDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.transactionType.getSelectedItem() == null || "#".equals(this.transactionType.getSelectedItem().getValue().toString())) {
				throw new WrongValueException( this.transactionType, Labels.getLabel( "CHECK_NO_EMPTY",
						new String[] {Labels.getLabel("label_FacilityDetailDialog_transactionType.value")}));			
				} else {
					aFacilityDetail.setTransactionType(this.transactionType.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setAgentBank(StringUtils.trimToEmpty(this.agentBank.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setOtherDetails(StringUtils.trimToEmpty(this.otherDetails.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setTotalFacility(PennantApplicationUtil.unFormateAmount(this.totalFacility.getValidateValue(), 
					CurrencyUtil.getFormat(aFacilityDetail.getTotalFacilityCcy())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setTotalFacilityCcy(this.totalFacilityCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setUnderWriting(PennantApplicationUtil.unFormateAmount(this.underWriting.getValidateValue(), 
					CurrencyUtil.getFormat(aFacilityDetail.getUnderWritingCcy())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setUnderWritingCcy(this.underWritingCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setPropFinalTake(PennantApplicationUtil.unFormateAmount(this.propFinalTake.getValidateValue(), 
					CurrencyUtil.getFormat(aFacilityDetail.getPropFinalTakeCcy())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityDetail.setPropFinalTakeCcy(this.propFinalTakeCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aFacilityDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFacilityDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FacilityDetail aFacilityDetail) throws InterruptedException {
		logger.debug("Entering");
		// set Readonly mode accordingly if the object is new or not.
		if (aFacilityDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cAFReference.focus();
		} else {
			this.facilityFor.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			fillComboBox(this.transactionType, "", PennantStaticListUtil.getTransactionTypesList(), "");
			doWriteBeanToComponents(aFacilityDetail);
			setDialog(DialogType.OVERLAPPED);
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
		boolean isTranTypeSyndication = this.transactionType.getSelectedItem().getValue().equals(FacilityConstants.FACILITY_TRAN_SYNDIACTION);
//		if (!this.cAFReference.isReadonly()) {
//			this.cAFReference.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_CAFReference.value"),null,true));
//		}
		if (!this.facilityFor.isReadonly()) {
			this.facilityFor.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_FacilityFor.value"),null,true));
		}
		if (!this.facilityType.isReadonly()) {
			this.facilityType.setConstraint(new PTStringValidator ( Labels.getLabel("label_FacilityDetailDialog_FacilityType.value"),null,true,true));
		}
		if (!this.facilityCCY.isReadonly()) {
			this.facilityCCY.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_FacilityCCY.value"),null,true,true));
		}
		if (!this.exposure.isReadonly()) {
			// FIXIT
		}
		if (!this.existingLimit.isReadonly()) {
			// FIXIT
		}
		if (!this.newLimit.isReadonly()) {
			this.newLimit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FacilityDetailDialog_NewLimit.value"), ccyFormat, true, false));
		}
		if (!this.financeAmount.isReadonly()) {
			// FIXIT
		}
		
		if (!this.tenorMonth.isReadonly()) {
			this.tenorMonth.setConstraint(new PTNumberValidator(Labels.getLabel("label_FacilityDetailDialog_TenorMonth.value"),  false, false,0,11));
		}
//		if (!this.tenorDesc.isReadonly()) {
//			this.tenorDesc.setConstraint(new PTStringValidator( Labels.getLabel("label_FacilityDetailDialog_TenorDesc.value"),null,true));
//		}
		
		if (!this.startDate.isReadonly() && !this.startDate.isDisabled()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDetailDialog_StartDate.value"), false, appldate,null, true));
		}
		if (!this.maturityDate.isReadonly() && !this.maturityDate.isDisabled()) {
			this.maturityDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDetailDialog_MaturityDate.value"), false, appldate,null, true));
		}
//		if (!this.pricing.isReadonly()) {
//			this.pricing.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_Pricing.value"),null,true));
//		}
//		if (!this.repayments.isReadonly()) {
//			this.repayments.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_Repayments.value"),null,true));
//		}
//		if (!this.rateType.isReadonly()) {
//			this.rateType.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_RateType.value"),null,true));
//		}
//		if (!this.lCPeriod.isReadonly()) {
//			this.lCPeriod.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_LCPeriod.value"),null,true));
//		}
//		if (!this.usancePeriod.isReadonly()) {
//			this.usancePeriod.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_UsancePeriod.value"),null,true));
//		}
//		if (!this.securityDesc.isReadonly()) {
//			this.securityDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_SecurityDesc.value"),null,true));
//		}
//		if (!this.utilization.isReadonly()) {
//			this.utilization.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_Utilization.value"),null,true));
//		}
//		if (!this.commission.isReadonly()) {
//			this.commission.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_Commission.value"),null,true));
//		}
//		if (!this.purpose.isReadonly()) {
//			this.purpose.setConstraint(new PTStringValidator( Labels.getLabel("label_FacilityDetailDialog_Purpose.value"),null,true));
//		}
		
//			if (!this.agentBank.isReadonly()) {
//				this.agentBank.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_agentBank.value"),null,true));
//			}
//
//			if (!this.otherDetails.isReadonly()) {
//				this.otherDetails.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_otherDetails.value"),null,true));
//			}
		if(isTranTypeSyndication){
			/*if (!this.totalFacility.isReadonly()) {
				this.totalFacility.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FacilityDetailDialog_totalFacility.value"), 
						getFacilityDetail().getTotalFacilityFormatter(), isTranTypeSyndication, false));
			}

			if (!this.underWriting.isReadonly()) {
				this.underWriting.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FacilityDetailDialog_underWriting.value"), 
						getFacilityDetail().getUnderWritingFormatter(), isTranTypeSyndication, false));
			}

			if (!this.propFinalTake.isReadonly()) {
				this.propFinalTake.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FacilityDetailDialog_propFinalTake.value"), 
						getFacilityDetail().getPropFinalTakeFormatter(), isTranTypeSyndication, false));
			}

			if(!this.totalFacilityCcy.isReadonly()){ 
				this.totalFacilityCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_totalFacilityCcy.value"),null,true));
			}*/
			if(!this.underWritingCcy.isReadonly()){ 
				this.underWritingCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_underWritingCcy.value"),null,true,true));
			}

			if(!this.propFinalTakeCcy.isReadonly()){ 
				this.propFinalTakeCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDetailDialog_propFinalTakeCcy.value"),null,true,true));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cAFReference.setConstraint("");
		this.facilityRef.setConstraint("");
		this.facilityFor.setConstraint("");
		this.facilityType.setConstraint("");
		this.facilityCCY.setConstraint("");
		this.exposure.setConstraint("");
		this.existingLimit.setConstraint("");
		this.newLimit.setConstraint("");
		this.financeAmount.setConstraint("");
		this.pricing.setConstraint("");
		this.repayments.setConstraint("");
		this.rateType.setConstraint("");
		this.lCPeriod.setConstraint("");
		this.usancePeriod.setConstraint("");
		this.utilization.setConstraint("");
//		this.securityDesc.setConstraint("");
//		this.commission.setConstraint("");
//		this.purpose.setConstraint("");
//		this.guarantee.setConstraint("");
//		this.covenants.setConstraint("");
		this.startDate.setConstraint("");
		this.maturityDate.setConstraint("");
		this.tenorYear.setConstraint("");
		this.tenorMonth.setConstraint("");
		this.tenorDesc.setConstraint("");
		
		this.agentBank.setConstraint("");
		this.otherDetails.setConstraint("");
		this.totalFacility.setConstraint("");
		this.totalFacilityCcy.setConstraint("");
		this.underWriting.setConstraint("");
		this.underWritingCcy.setConstraint("");
		this.propFinalTake.setConstraint("");
		this.propFinalTakeCcy.setConstraint("");
		
		logger.debug("Leaving");
	}

	// CRUD operations
	
	/**
	 * Deletes a FacilityDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FacilityDetail aFacilityDetail = new FacilityDetail();
		BeanUtils.copyProperties(getFacilityDetail(), aFacilityDetail);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFacilityDetail.getCAFReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFacilityDetail.getRecordType())) {
				aFacilityDetail.setVersion(aFacilityDetail.getVersion() + 1);
				aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFacilityDetail.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}else if (StringUtils.trimToEmpty(aFacilityDetail.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aFacilityDetail.setVersion(aFacilityDetail.getVersion() + 1);
				aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = processFacilityDetails(aFacilityDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FacilityDetailDialog, auditHeader);
				
				int retValue = auditHeader.getProcessStatus();
			
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFacilityDetailListCtrl().doFillFacilityDetail(this.facilityDetailsList);
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
		if (getFacilityDetail().isNewRecord()) {
			this.cAFReference.setReadonly(false);
			this.facilityFor.setReadonly(false);
			this.facilityRef.setDisabled(true);
		} else {
			this.cAFReference.setReadonly(true);
			this.facilityFor.setReadonly(true);
			this.facilityRef.setDisabled(true);
		}
		//readOnlyComponent(isReadOnly("FacilityDetailDialog_facilityRef"), this.facilityRef);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_facilityFor"), this.facilityFor);
		this.facilityType.setReadonly(isReadOnly("FacilityDetailDialog_facilityType"));
		if (getFacilityDetail().getFacilityFor().equals(FacilityConstants.FACILITY_NEW)) {
			this.facilityCCY.setReadonly(isReadOnly("FacilityDetailDialog_facilityCCY"));
		}else{
			this.facilityCCY.setReadonly(true);
		}
		readOnlyComponent(isReadOnly("FacilityDetailDialog_exposure"), this.exposure);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_existingLimit"), this.existingLimit);
		this.newLimit.setReadonly(isReadOnly("FacilityDetailDialog_newLimit"));
		this.newLimit.setMandatory(!isReadOnly("FacilityDetailDialog_newLimit"));
		readOnlyComponent(isReadOnly("FacilityDetailDialog_financeAmount"), this.financeAmount);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_pricing"), this.pricing);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_repayments"), this.repayments);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_rateType"), this.rateType);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_lCPeriod"), this.lCPeriod);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_usancePeriod"), this.usancePeriod);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_securityClean"), this.securityClean);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_securityDesc"), this.securityDesc);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_utilization"), this.utilization);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_commission"), this.commission);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_purpose"), this.purpose);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_guarantee"), this.guarantee);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_covenants"), this.covenants);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_documentsRequired"), this.documentsRequired);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_startDate"), this.startDate);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_maturityDate"), this.maturityDate);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_tenorYear"), this.tenorYear);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_tenorMonth"), this.tenorMonth);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_tenorDesc"), this.tenorDesc);
		
		readOnlyComponent(isReadOnly("FacilityDetailDialog_transactionType"), this.transactionType);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_agentBank"), this.agentBank);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_otherDetails"), this.otherDetails);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_totalFacility"), this.totalFacility);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_totalFacilityCcy"), this.totalFacilityCcy);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_underWriting"), this.underWriting);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_underWritingCcy"), this.underWritingCcy);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_propFinalTake"), this.propFinalTake);
		readOnlyComponent(isReadOnly("FacilityDetailDialog_propFinalTakeCcy"), this.propFinalTakeCcy);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.facilityDetail.isNewRecord()) {
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

	public boolean isReadOnly(String componentName){
			return getUserWorkspace().isReadOnly(componentName);
	}
	
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cAFReference.setReadonly(true);
		this.facilityRef.setReadonly(true);
		this.facilityFor.setReadonly(true);
		this.facilityType.setReadonly(true);
		this.facilityCCY.setReadonly(true);
		this.exposure.setReadonly(true);
		this.existingLimit.setReadonly(true);
		this.newLimit.setReadonly(true);
		this.financeAmount.setReadonly(true);
		this.pricing.setReadonly(true);
		this.repayments.setReadonly(true);
		this.rateType.setReadonly(true);
		this.lCPeriod.setReadonly(true);
		this.usancePeriod.setReadonly(true);
		this.securityClean.setDisabled(true);
		this.securityDesc.setReadonly(true);
		this.utilization.setReadonly(true);
		this.commission.setReadonly(true);
		this.purpose.setReadonly(true);
		this.guarantee.setReadonly(true);
		this.covenants.setReadonly(true);
		this.documentsRequired.setReadonly(true);
		this.tenorYear.setReadonly(true);
		this.tenorYear.setReadonly(true);
		this.tenorDesc.setReadonly(true);
		
		this.transactionType.setReadonly(true);
		this.agentBank.setReadonly(true);
		this.otherDetails.setReadonly(true);
		this.totalFacility.setReadonly(true);
	    this.totalFacilityCcy.setReadonly(true);
		this.underWriting.setReadonly(true);
		this.underWritingCcy.setReadonly(true);
		this.propFinalTake.setReadonly(true);
		this.propFinalTakeCcy.setReadonly(true);
		
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
		this.cAFReference.setValue("");
		this.facilityRef.setValue("");
		this.facilityFor.setValue("");
		this.facilityType.setValue("");
		this.facilityCCY.setValue("");
		this.exposure.setValue("");
		this.existingLimit.setValue("");
		this.newLimit.setValue("");
		this.financeAmount.setValue("");
		this.pricing.setValue("");
		this.repayments.setValue("");
		this.rateType.setValue("");
		this.lCPeriod.setValue("");
		this.usancePeriod.setValue("");
		this.securityClean.setChecked(false);
		this.securityDesc.setValue("");
		this.utilization.setValue("");
		this.commission.setValue("");
		this.purpose.setValue("");
		this.guarantee.setValue("");
		this.covenants.setValue("");
		this.documentsRequired.setValue("");
		this.tenorYear.setValue(0);
		this.tenorMonth.setValue(0);
		this.tenorDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FacilityDetail aFacilityDetail = new FacilityDetail();
		BeanUtils.copyProperties(getFacilityDetail(), aFacilityDetail);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FacilityDetail object with the components data
		doWriteComponentsToBean(aFacilityDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aFacilityDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFacilityDetail.getRecordType())) {
				aFacilityDetail.setVersion(aFacilityDetail.getVersion() + 1);
				if (isNew) {
					aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFacilityDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aFacilityDetail.setVersion(1);
				aFacilityDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aFacilityDetail.getRecordType())) {
				aFacilityDetail.setVersion(aFacilityDetail.getVersion() + 1);
				aFacilityDetail.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aFacilityDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFacilityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (aFacilityDetail.isNewRecord()) {
				if (getFacilityDetailByRef(aFacilityDetail.getFacilityRef()) != null) {
					MessageUtil.showError(Labels.getLabel("DATA_ALREADY_EXISTS", new String[] {
							Labels.getLabel("label_FacilityRef") + ":" + aFacilityDetail.getFacilityRef() }));
					return;
				}
			}
			
			AuditHeader auditHeader = processFacilityDetails(aFacilityDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FacilityDetailDialog, auditHeader);
			
			int retValue = auditHeader.getProcessStatus();
			
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFacilityDetailListCtrl().doFillFacilityDetail(this.facilityDetailsList);
				closeDialog();
			}
	
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader processFacilityDetails(FacilityDetail aFacilityDetail, String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFacilityDetail, tranType);
		facilityDetailsList = new ArrayList<FacilityDetail>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aFacilityDetail.getCAFReference();
		valueParm[1] = aFacilityDetail.getFacilityRef();
		errParm[0] = PennantJavaUtil.getLabel("label_CAFReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Reference") + ":" + valueParm[1];
		List<FacilityDetail> list = getFacilityDetailListCtrl().getFacilityDetailList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				FacilityDetail facilityDetail = list.get(i);
				if (facilityDetail.getFacilityRef().equals(aFacilityDetail.getFacilityRef())) {
					// Both Current and Existing list rating same
					if (aFacilityDetail.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aFacilityDetail.getRecordType())) {
							aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							facilityDetailsList.add(aFacilityDetail);
						} else if (PennantConstants.RCD_ADD.equals(aFacilityDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFacilityDetail.getRecordType())) {
							aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							facilityDetailsList.add(aFacilityDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aFacilityDetail.getRecordType())) {
							recordAdded = true;
							List<FacilityDetail> savedList = getFacilityDetailListCtrl().getFacility().getFacilityDetails();
							for (int j = 0; j < savedList.size(); j++) {
								FacilityDetail fee = savedList.get(j);
								if (fee.getFacilityRef().equals(aFacilityDetail.getFacilityRef())) {
									facilityDetailsList.add(fee);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aFacilityDetail.getRecordType())) {
							aFacilityDetail.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							facilityDetailsList.add(facilityDetail);
						}
					}
				} else {
					facilityDetailsList.add(facilityDetail);
				}
			}
		}
		if (!recordAdded) {
			facilityDetailsList.add(aFacilityDetail);
		}
		logger.debug("Leaving");
		return auditHeader;
	}
	
	public void onCheck$securityClean(Event event){
		onCheckSecurity();
	}
	private void onCheckSecurity(){
		logger.debug("Entering");
		if(this.securityClean.isChecked()){
			this.securityDesc.setReadonly(true);
			this.securityDesc.setValue("");
		}else{
			this.securityDesc.setReadonly(isReadOnly("FacilityDetailDialog_securityDesc"));
		}
		logger.debug("Leaving");
	}
	
	public void onChange$transactionType(Event event){
		logger.debug("Entering" + event.toString());
		doCheckTransactionType();
		logger.debug("Leaving" + event.toString());
	}
	
	 public void onFulfill$totalFacilityCcy(Event event) {
			logger.debug("Entering " + event.toString());
			Object dataObject = totalFacilityCcy.getObject();
			if (dataObject instanceof String) {
				this.totalFacilityCcy.setValue(dataObject.toString());
				this.totalFacilityCcy.setDescription("");
			} else {
				Currency details = (Currency) dataObject;
				if (details != null) {
					// To Format Amount based on the currency
					this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				}
			}
			logger.debug("Leaving " + event.toString());
		}
		
		public void onFulfill$underWritingCcy(Event event) {
			logger.debug("Entering " + event.toString());
			Object dataObject = underWritingCcy.getObject();
			if (dataObject instanceof String) {
				this.underWritingCcy.setValue(dataObject.toString());
				this.underWritingCcy.setDescription("");
			} else {
				Currency details = (Currency) dataObject;
				if (details != null) {
					// To Format Amount based on the currency
					this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				}
			}
			logger.debug("Leaving " + event.toString());
		}
		
		public void onFulfill$propFinalTakeCcy(Event event) {
			logger.debug("Entering " + event.toString());
			Object dataObject = propFinalTakeCcy.getObject();
			if (dataObject instanceof String) {
				this.propFinalTakeCcy.setValue(dataObject.toString());
				this.propFinalTakeCcy.setDescription("");
			} else {
				Currency details = (Currency) dataObject;
				if (details != null) {
					// To Format Amount based on the currency
					this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				}
			}
			logger.debug("Leaving " + event.toString());
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

	public FacilityDetail getFacilityDetail() {
		return this.facilityDetail;
	}

	public void setFacilityDetail(FacilityDetail facilityDetail) {
		this.facilityDetail = facilityDetail;
	}

	public void setFacilityDetailListCtrl(FacilityDetailListCtrl facilityDetailListCtrl) {
		this.facilityDetailListCtrl = facilityDetailListCtrl;
	}

	public FacilityDetailListCtrl getFacilityDetailListCtrl() {
		return this.facilityDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(FacilityDetail aFacilityDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFacilityDetail.getBefImage(), aFacilityDetail);
		return new AuditHeader(aFacilityDetail.getCAFReference(), null, null, null, auditDetail, aFacilityDetail.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FacilityDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.facilityDetail);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}


	
	@Override
	protected String getReference() {
		return String.valueOf(this.facilityDetail.getCAFReference());
	}


	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.cAFReference.setErrorMessage("");
		this.facilityRef.setErrorMessage("");
		this.facilityFor.setErrorMessage("");
		this.facilityType.setErrorMessage("");
		this.facilityCCY.setErrorMessage("");
		this.exposure.setErrorMessage("");
		this.existingLimit.setErrorMessage("");
		this.newLimit.setErrorMessage("");
		this.financeAmount.setErrorMessage("");
		this.pricing.setErrorMessage("");
		this.repayments.setErrorMessage("");
		this.rateType.setErrorMessage("");
		this.lCPeriod.setErrorMessage("");
		this.usancePeriod.setErrorMessage("");
		this.utilization.setErrorMessage("");
//		this.securityDesc.setErrorMessage("");
//		this.commission.setErrorMessage("");
//		this.purpose.setErrorMessage("");
//		this.guarantee.setErrorMessage("");
//		this.covenants.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.maturityDate.setErrorMessage("");
		this.tenorYear.setErrorMessage("");
		this.tenorMonth.setErrorMessage("");
		this.tenorDesc.setErrorMessage("");
		
		this.transactionType.setErrorMessage("");
		this.agentBank.setErrorMessage("");
		this.otherDetails.setErrorMessage("");
		this.totalFacility.setErrorMessage("");
		this.totalFacilityCcy.setErrorMessage("");
		this.underWriting.setErrorMessage("");
		this.underWritingCcy.setErrorMessage("");
		this.propFinalTake.setErrorMessage("");
		this.propFinalTakeCcy.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public FacilityDetail getPrvFacilityDetail() {
		return prvFacilityDetail;
	}

	public void setFacilityDetailsList(List<FacilityDetail> facilityDetailsList) {
		this.facilityDetailsList = facilityDetailsList;
	}

	public List<FacilityDetail> getFacilityDetailsList() {
		return facilityDetailsList;
	}
	public void onFulfill$facilityCCY(Event event) {
		logger.debug("Entering");
		Object object = this.facilityCCY.getObject();
		if (object instanceof Currency) {
			Currency currency = (Currency) object;
			ccyFormat = currency.getCcyEditField();
		}
		this.exposure.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.exposure.setScale(ccyFormat);
		this.existingLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.existingLimit.setScale(ccyFormat);
		this.existingLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.existingLimit.setScale(ccyFormat);
		this.financeAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.financeAmount.setScale(ccyFormat);
		this.newLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.newLimit.setScale(ccyFormat);
		boolean isTranTypeSyndiation = this.transactionType.getSelectedItem().getValue().equals(FacilityConstants.FACILITY_TRAN_SYNDIACTION);
		if(isTranTypeSyndiation){
			defaultSyndicationValueByCcy();
		}
		logger.debug("Leaving");
	}
	
	private Commitment getCommitmentRef(String ref){
		logger.debug("Entering");
		JdbcSearchObject<Commitment> jdbcSearchObject=new JdbcSearchObject<Commitment>(Commitment.class);
		jdbcSearchObject.addTabelName("Commitments_Aview");
		jdbcSearchObject.addFilterEqual("CmtReference", ref);
		List<Commitment> list = getPagedListService().getBySearchObject(jdbcSearchObject);
		if (list!=null && !list.isEmpty()) {
			return list.get(0);
		}
		logger.debug("Leaving");
		return null;
	}
	private FacilityDetail getFacilityDetailByRef(String ref){
		logger.debug("Entering");
		JdbcSearchObject<FacilityDetail> jdbcSearchObject=new JdbcSearchObject<FacilityDetail>(FacilityDetail.class);
		jdbcSearchObject.addTabelName("FacilityDetails_view");
		jdbcSearchObject.addFilterEqual("FacilityRef", ref);
		List<FacilityDetail> list = getPagedListService().getBySearchObject(jdbcSearchObject);
		if (list!=null && !list.isEmpty()) {
			return list.get(0);
		}
		logger.debug("Leaving");
		return null;
	}
	
	private void doDesignByMode(){
		logger.debug("Entering");
		if (StringUtils.isNotBlank(getFacilityDetail().getTermSheetRef())) {
			this.startDate.setDisabled(true);
			this.maturityDate.setDisabled(true);
			this.facilityCCY.setReadonly(true);
			this.facilityType.setReadonly(true);			
		}
		logger.debug("Leaving");
	}
	
	public void doCheckTransactionType(){
		logger.debug("Entering");
		doClearMessage();
		boolean isTranTypeSyndiation = this.transactionType.getSelectedItem().getValue().equals(FacilityConstants.FACILITY_TRAN_SYNDIACTION);
		if(isTranTypeSyndiation){
			this.row_totalFacility.setVisible(true);
			this.row_underWriting.setVisible(true);
			this.row_propFinalTake.setVisible(true);
			defaultSyndicationValueByCcy();
		}else{
			this.row_totalFacility.setVisible(false);
			this.row_underWriting.setVisible(false);
			this.row_propFinalTake.setVisible(false);
			this.totalFacility.setValue(BigDecimal.ZERO);
			this.underWriting.setValue(BigDecimal.ZERO);
			this.propFinalTake.setValue(BigDecimal.ZERO);
			this.totalFacilityCcy.setValue("");
			this.totalFacilityCcy.setDescription("");
			this.underWritingCcy.setValue("");
			this.underWritingCcy.setDescription("");
			this.propFinalTakeCcy.setValue("");
			this.propFinalTakeCcy.setDescription("");
			this.totalFacilityCcy.setValue("");
			this.underWritingCcy.setValue("");
			this.propFinalTakeCcy.setValue("");
		}
		logger.debug("Leaving");
	}
	
	private void defaultSyndicationValueByCcy(){
		logger.debug("Entering");
		if(StringUtils.isNotBlank(this.facilityCCY.getValidatedValue())){
			Currency currency=(Currency)this.facilityCCY.getObject();
			if (currency!=null) {
				if(StringUtils.isBlank(totalFacilityCcy.getValue())){
					this.totalFacilityCcy.setValue(currency.getCcyCode(),currency.getCcyDesc());
					this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(currency.getCcyEditField()));
				}
				if(StringUtils.isBlank(underWritingCcy.getValue())){
					this.underWritingCcy.setValue(currency.getCcyCode(),currency.getCcyDesc());
					this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(currency.getCcyEditField()));
				}
				if(StringUtils.isBlank(propFinalTakeCcy.getValue())){
					this.propFinalTakeCcy.setValue(currency.getCcyCode(),currency.getCcyDesc());
					this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(currency.getCcyEditField()));
				}
			}
	
		}
		logger.debug("Leaving");
	}
	
}
