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

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.finance.FacilityType;
import com.pennant.backend.service.PagedListService;
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
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/FacilityDetail/facilityDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FacilityDetailDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FacilityDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FacilityDetailDialog; // autowired
	protected Textbox cAFReference;   // autowired
	protected Textbox facilityRef;    // autowired
	protected Textbox facilityFor;    // autowired
	protected ExtendedCombobox facilityType; // autowired
	protected ExtendedCombobox facilityCCY; // autowired
	protected Decimalbox exposure; // autowired
	protected Decimalbox existingLimit; // autowired
	protected CurrencyBox newLimit; // autowired
	protected Decimalbox financeAmount; // autowired
	protected Textbox pricing; // autowired
	protected Textbox repayments; // autowired
	protected Textbox rateType; // autowired
	protected Textbox lCPeriod; // autowired
	protected Textbox usancePeriod; // autowired
	protected Checkbox securityClean; // autowired
	protected PTCKeditor securityDesc; // autowired
	protected Textbox utilization; // autowired
	protected PTCKeditor commission; // autowired
	protected PTCKeditor purpose; // autowired
	protected PTCKeditor guarantee; // autowired
	protected PTCKeditor covenants; // autowired
	protected PTCKeditor documentsRequired; // autowired
	protected Label recordStatus; // autowired
	protected Radiogroup userAction; // autowired
	protected Groupbox groupboxWf; // autowired
	protected Row statusRow; // autowired
	protected Datebox startDate; // autowired
	protected Datebox maturityDate; // autowired
	
	protected Intbox tenorYear; // autowired
	protected Intbox tenorMonth; // autowired
	protected Textbox tenorDesc; // autowired
	
	protected Combobox          transactionType;   // autowired
	protected Textbox           agentBank;         // autowired
	protected Textbox           otherDetails;      // autowired
	protected CurrencyBox       totalFacility;     // autowired
	protected ExtendedCombobox  totalFacilityCcy;  // autowired
	protected CurrencyBox       underWriting;      // autowired
	protected ExtendedCombobox  underWritingCcy;   // autowired
	protected CurrencyBox       propFinalTake;     // autowired
	protected ExtendedCombobox  propFinalTakeCcy;  // autowired
	protected Row               row_totalFacility; // autowired
	protected Row               row_underWriting;  // autowired
	protected Row               row_propFinalTake; // autowired
	
	//protected Space space_AgentBank;
	//protected Space space_OtherDetails;
	
	// not auto wired vars
	private FacilityDetail facilityDetail; // overhanded per param
	private FacilityDetail prvFacilityDetail; // overhanded per param
	private transient FacilityDetailListCtrl facilityDetailListCtrl; // overhanded
																		// per
																		// param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_cAFReference;
	private transient String oldVar_facilityRef;
	private transient String oldVar_facilityFor;
	private transient String oldVar_facilityType;
	private transient String oldVar_facilityCCY;
	private transient BigDecimal oldVar_exposure;
	private transient BigDecimal oldVar_existingLimit;
	private transient BigDecimal oldVar_newLimit;
	private transient BigDecimal oldVar_financeAmount;
	private transient String oldVar_pricing;
	private transient String oldVar_repayments;
	private transient String oldVar_rateType;
	private transient String oldVar_lCPeriod;
	private transient String oldVar_usancePeriod;
	private transient boolean oldVar_securityClean;
	private transient String oldVar_securityDesc;
	private transient String oldVar_utilization;
	private transient String oldVar_commission;
	private transient String oldVar_purpose;
	private transient String oldVar_guarantee;
	private transient String oldVar_covenants;
	private transient String oldVar_documentsRequired;
	private transient String oldVar_recordStatus;
	private transient int oldVar_tenorYear;
	private transient int oldVar_tenorMonth;
	private transient String oldVar_tenorDesc;
	
	private transient int oldVar_transactionType;
	private transient String oldVar_agentBank;
	private transient String oldVar_otherDetails;
	private transient BigDecimal oldVar_totalFacility;
	private transient String oldVar_totalFacilityCcy;
	private transient BigDecimal oldVar_underWriting;
	private transient String oldVar_underWritingCcy;
	private transient BigDecimal oldVar_propFinalTake;
	private transient String oldVar_propFinalTakeCcy;

	private transient boolean validationOn;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FacilityDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	
	private List<FacilityDetail> facilityDetailsList;
	private int ccyFormat=0;
	Commitment commitment=null;
	String userRole="";
	Date appldate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
	/**
	 * default constructor.<br>
	 */
	public FacilityDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FacilityDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FacilityDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */
		
			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("facilityDetail")) {
				this.facilityDetail = (FacilityDetail) args.get("facilityDetail");
				FacilityDetail befImage = new FacilityDetail();
				BeanUtils.copyProperties(this.facilityDetail, befImage);
				this.facilityDetail.setBefImage(befImage);
				setFacilityDetail(this.facilityDetail);
				ccyFormat=getFacilityDetail().getCCYformat();
			} else {
				setFacilityDetail(null);
			}
			this.facilityDetail.setWorkflowId(0);
			doLoadWorkFlow(this.facilityDetail.isWorkflow(), this.facilityDetail.getWorkflowId(), this.facilityDetail.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "FacilityDetailDialog");
			}
			if (args.containsKey("role")) {
				userRole=args.get("role").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "FacilityDetailDialog");
			}
			if (args.containsKey("commitment")) {
				commitment=(Commitment) args.get("commitment");
			}
			doCheckRights();
			// READ OVERHANDED params !
			// we get the facilityDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete facilityDetail here.
			if (args.containsKey("facilityDetailListCtrl")) {
				setFacilityDetailListCtrl((FacilityDetailListCtrl) args.get("facilityDetailListCtrl"));
			} else {
				setFacilityDetailListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFacilityDetail());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
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
		this.newLimit.setMaxlength(18);
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
		
		this.totalFacility.setMaxlength(18);
		this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(getFacilityDetail().getTotalFacilityFormatter()));
		this.underWriting.setMaxlength(18);
		this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(getFacilityDetail().getUnderWritingFormatter()));
		this.propFinalTake.setMaxlength(18);
		this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(getFacilityDetail().getPropFinalTakeFormatter()));

		this.totalFacilityCcy.setMaxlength(3);
		this.totalFacilityCcy.setMandatoryStyle(true);
		this.totalFacilityCcy.setModuleName("Currency");
		this.totalFacilityCcy.setValueColumn("CcyCode");
		this.totalFacilityCcy.setDescColumn("CcyDesc");
		this.totalFacilityCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.underWritingCcy.setMaxlength(3);
		this.underWritingCcy.setMandatoryStyle(true);
		this.underWritingCcy.setModuleName("Currency");
		this.underWritingCcy.setValueColumn("CcyCode");
		this.underWritingCcy.setDescColumn("CcyDesc");
		this.underWritingCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.propFinalTakeCcy.setMaxlength(3);
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
		
		this.startDate.setFormat(PennantConstants.dateFormat);
		this.maturityDate.setFormat(PennantConstants.dateFormat);
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
		getUserWorkspace().alocateAuthorities("FacilityDetailDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FacilityDetailDialog_btnSave"));
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
	public void onClose$window_FacilityDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
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
		PTMessageUtils.showHelpWindow(event, window_FacilityDetailDialog);
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// GUI Process
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
			closeDialog2(this.window_FacilityDetailDialog,"FacilityDetailDialog");
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
	 * @param aFacilityDetail
	 *            FacilityDetail
	 */
	public void doWriteBeanToComponents(FacilityDetail aFacilityDetail) {
		logger.debug("Entering");
		if (commitment ==null && !aFacilityDetail.isNewRecord() && !getFacilityDetail().getFacilityFor().equals(PennantConstants.FACILITY_NEW)) {
			commitment=getCommitmentRef(aFacilityDetail.getFacilityRef());
		}
		this.cAFReference.setValue(aFacilityDetail.getCAFReference());
		this.facilityRef.setValue(aFacilityDetail.getFacilityRef());
		this.facilityFor.setValue(PennantStaticListUtil.getlabelDesc(aFacilityDetail.getFacilityFor(), PennantStaticListUtil.getFacilityApprovalFor()));
		this.facilityType.setValue(aFacilityDetail.getFacilityType(),StringUtils.trimToEmpty(aFacilityDetail.getFacilityTypeDesc()));
		this.facilityCCY.setValue(aFacilityDetail.getFacilityCCY(),StringUtils.trimToEmpty(aFacilityDetail.getFacilityCCYName()));
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
		this.totalFacility.setValue(PennantApplicationUtil.formateAmount(aFacilityDetail.getTotalFacility(), aFacilityDetail.getTotalFacilityFormatter()));
		this.totalFacilityCcy.setValue(aFacilityDetail.getTotalFacilityCcy(), StringUtils.trimToEmpty(aFacilityDetail.getTotalFacilityCcyName()));
		this.underWriting.setValue(PennantApplicationUtil.formateAmount(aFacilityDetail.getUnderWriting(), aFacilityDetail.getUnderWritingFormatter()));
		this.underWritingCcy.setValue(aFacilityDetail.getUnderWritingCcy(), StringUtils.trimToEmpty(aFacilityDetail.getUnderWritingCcyName()));
		this.propFinalTake.setValue(PennantApplicationUtil.formateAmount(aFacilityDetail.getPropFinalTake(), aFacilityDetail.getPropFinalTakeFormatter()));
		this.propFinalTakeCcy.setValue(aFacilityDetail.getPropFinalTakeCcy(), StringUtils.trimToEmpty(aFacilityDetail.getPropFinalTakeCcyName()));
		
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
			aFacilityDetail.setFacilityCCYName(this.facilityCCY.getDescription());
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
			if (this.newLimit.getValue() != null) {
				aFacilityDetail.setNewLimit(PennantApplicationUtil.unFormateAmount(this.newLimit.getValue(), ccyFormat));
			}else{
				aFacilityDetail.setNewLimit(BigDecimal.ZERO);
			}
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
				aFacilityDetail.setCCYformat(ccyFormat);
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
			if (this.transactionType.getSelectedItem() == null || this.transactionType.getSelectedItem().getValue().toString().equals("#")) {
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
			aFacilityDetail.setTotalFacility(PennantApplicationUtil.unFormateAmount(this.totalFacility.getValue(), aFacilityDetail.getTotalFacilityFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setTotalFacilityCcy(this.totalFacilityCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setUnderWriting(PennantApplicationUtil.unFormateAmount(this.underWriting.getValue(), aFacilityDetail.getUnderWritingFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setUnderWritingCcy(this.underWritingCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFacilityDetail.setPropFinalTake(PennantApplicationUtil.unFormateAmount(this.propFinalTake.getValue(), aFacilityDetail.getPropFinalTakeFormatter()));
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
		// if aFacilityDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFacilityDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			// aFacilityDetail =
			// getFacilityDetailService().getNewFacilityDetail();
			//
			// setFacilityDetail(aFacilityDetail);
		} else {
			setFacilityDetail(aFacilityDetail);
		}
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
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog2(window_FacilityDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_cAFReference = this.cAFReference.getValue();
		this.oldVar_facilityRef = this.facilityRef.getValue();
		this.oldVar_facilityFor = this.facilityFor.getValue();
		this.oldVar_facilityType = this.facilityType.getValue();
		this.oldVar_facilityCCY = this.facilityCCY.getValue();
		this.oldVar_exposure = this.exposure.getValue();
		this.oldVar_existingLimit = this.existingLimit.getValue();
		this.oldVar_newLimit = this.newLimit.getValue();
		this.oldVar_financeAmount = this.financeAmount.getValue();
		this.oldVar_pricing = this.pricing.getValue();
		this.oldVar_repayments = this.repayments.getValue();
		this.oldVar_rateType = this.rateType.getValue();
		this.oldVar_lCPeriod = this.lCPeriod.getValue();
		this.oldVar_usancePeriod = this.usancePeriod.getValue();
		this.oldVar_securityClean = this.securityClean.isChecked();
		this.oldVar_securityDesc = this.securityDesc.getValue();
		this.oldVar_utilization = this.utilization.getValue();
		this.oldVar_commission = this.commission.getValue();
		this.oldVar_purpose = this.purpose.getValue();
		this.oldVar_guarantee = this.guarantee.getValue();
		this.oldVar_covenants = this.covenants.getValue();
		this.oldVar_documentsRequired = this.documentsRequired.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_tenorYear = this.tenorYear.intValue();
		this.oldVar_tenorMonth = this.tenorMonth.intValue();
		this.oldVar_tenorDesc = this.tenorDesc.getValue();
		
		this.oldVar_transactionType = this.transactionType.getSelectedIndex();
		this.oldVar_agentBank = this.agentBank.getValue();
		this.oldVar_otherDetails = this.otherDetails.getValue();
		this.oldVar_totalFacility = this.totalFacility.getValue();
		this.oldVar_totalFacilityCcy = this.totalFacilityCcy.getValue();
		this.oldVar_underWriting = this.underWriting.getValue();
		this.oldVar_underWritingCcy = this.underWritingCcy.getValue();
		this.oldVar_propFinalTake = this.propFinalTake.getValue();
		this.oldVar_propFinalTakeCcy = this.propFinalTakeCcy.getValue();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.cAFReference.setValue(this.oldVar_cAFReference);
		this.facilityRef.setValue(this.oldVar_facilityRef);
		this.facilityFor.setValue(this.oldVar_facilityFor);
		this.facilityType.setValue(this.oldVar_facilityType);
		this.facilityCCY.setValue(this.oldVar_facilityCCY);
		this.exposure.setValue(this.oldVar_exposure);
		this.existingLimit.setValue(this.oldVar_existingLimit);
		this.newLimit.setValue(this.oldVar_newLimit);
		this.financeAmount.setValue(this.oldVar_financeAmount);
		this.pricing.setValue(this.oldVar_pricing);
		this.repayments.setValue(this.oldVar_repayments);
		this.rateType.setValue(this.oldVar_rateType);
		this.lCPeriod.setValue(this.oldVar_lCPeriod);
		this.usancePeriod.setValue(this.oldVar_usancePeriod);
		this.securityClean.setChecked(this.oldVar_securityClean);
		this.securityDesc.setValue(this.oldVar_securityDesc);
		this.utilization.setValue(this.oldVar_utilization);
		this.commission.setValue(this.oldVar_commission);
		this.purpose.setValue(this.oldVar_purpose);
		this.guarantee.setValue(this.oldVar_guarantee);
		this.covenants.setValue(this.oldVar_covenants);
		this.documentsRequired.setValue(this.oldVar_documentsRequired);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.tenorYear.setValue(this.oldVar_tenorYear);
		this.tenorMonth.setValue(this.oldVar_tenorMonth);
		this.tenorDesc.setValue(this.oldVar_tenorDesc);
		
		this.transactionType.setSelectedIndex(this.oldVar_transactionType);
		this.agentBank.setValue(this.oldVar_agentBank);
		this.otherDetails.setValue(this.oldVar_otherDetails);
		this.totalFacility.setValue(this.oldVar_totalFacility);
		this.totalFacilityCcy.setValue(this.oldVar_totalFacilityCcy);
		this.underWriting.setValue(this.oldVar_underWriting);
		this.underWritingCcy.setValue(this.oldVar_underWritingCcy);
		this.propFinalTake.setValue(this.oldVar_propFinalTake);
		this.propFinalTakeCcy.setValue(this.oldVar_propFinalTakeCcy);
		
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
		// To clear the Error Messages
		doClearMessage();
		if (this.oldVar_cAFReference != this.cAFReference.getValue()) {
			return true;
		}
		if (this.oldVar_facilityRef != this.facilityRef.getValue()) {
			return true;
		}
		if (this.oldVar_facilityFor != this.facilityFor.getValue()) {
			return true;
		}
		if (this.oldVar_facilityType != this.facilityType.getValue()) {
			return true;
		}
		if (this.oldVar_facilityCCY != this.facilityCCY.getValue()) {
			return true;
		}
		if (this.oldVar_exposure != this.exposure.getValue()) {
			return true;
		}
		if (this.oldVar_existingLimit != this.existingLimit.getValue()) {
			return true;
		}
		if (this.oldVar_newLimit != this.newLimit.getValue()) {
			return true;
		}
		if (this.oldVar_financeAmount != this.financeAmount.getValue()) {
			return true;
		}
		if (this.oldVar_pricing != this.pricing.getValue()) {
			return true;
		}
		if (this.oldVar_repayments != this.repayments.getValue()) {
			return true;
		}
		if (this.oldVar_rateType != this.rateType.getValue()) {
			return true;
		}
		if (this.oldVar_lCPeriod != this.lCPeriod.getValue()) {
			return true;
		}
		if (this.oldVar_usancePeriod != this.usancePeriod.getValue()) {
			return true;
		}
		if (this.oldVar_securityClean != this.securityClean.isChecked()) {
			return true;
		}
		if (this.oldVar_securityDesc != this.securityDesc.getValue()) {
			return true;
		}
		if (this.oldVar_utilization != this.utilization.getValue()) {
			return true;
		}
		if (this.oldVar_commission != this.commission.getValue()) {
			return true;
		}
		if (this.oldVar_purpose != this.purpose.getValue()) {
			return true;
		}
		if (this.oldVar_guarantee != this.guarantee.getValue()) {
			return true;
		}
		if (this.oldVar_covenants != this.covenants.getValue()) {
			return true;
		}
		if (this.oldVar_documentsRequired != this.documentsRequired.getValue()) {
			return true;
		}
		if (this.oldVar_tenorYear!= this.tenorYear.intValue()) {
			return true;
		}
		if (this.oldVar_tenorMonth!= this.tenorMonth.intValue()) {
			return true;
		}
		if (this.oldVar_tenorDesc != this.tenorDesc.getValue()) {
			return true;
		}
		if (this.oldVar_transactionType != this.transactionType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_agentBank != this.agentBank.getValue()) {
			return true;
		}
		if (this.oldVar_otherDetails != this.otherDetails.getValue()) {
			return true;
		}
		if (this.oldVar_totalFacility.compareTo(this.totalFacility.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_totalFacilityCcy != this.totalFacilityCcy.getValue()) {
			return true;
		}
		if (this.oldVar_underWriting.compareTo(this.underWriting.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_underWritingCcy != this.underWritingCcy.getValue()) {
			return true;
		}
		if (this.oldVar_propFinalTake.compareTo(this.propFinalTake.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_propFinalTakeCcy != this.propFinalTakeCcy.getValue()) {
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
		setValidationOn(true);
		boolean isTranTypeSyndication = this.transactionType.getSelectedItem().getValue().equals(PennantConstants.FACILITY_TRAN_SYNDIACTION);
//		if (!this.cAFReference.isReadonly()) {
//			this.cAFReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_CAFReference.value") }));
//		}
		if (!this.facilityFor.isReadonly()) {
			this.facilityFor.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_FacilityFor.value") }));
		}
		if (!this.facilityType.isReadonly()) {
			this.facilityType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_FacilityType.value") }));
		}
		if (!this.facilityCCY.isReadonly()) {
			this.facilityCCY.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_FacilityCCY.value") }));
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
//			this.tenorDesc.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_TenorDesc.value") }));
//		}
		
		if (!this.startDate.isReadonly() && !this.startDate.isDisabled()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDetailDialog_StartDate.value"), false, appldate,null, true));
		}
		if (!this.maturityDate.isReadonly() && !this.maturityDate.isDisabled()) {
			this.maturityDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDetailDialog_MaturityDate.value"), false, appldate,null, true));
		}
//		if (!this.pricing.isReadonly()) {
//			this.pricing.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_Pricing.value") }));
//		}
//		if (!this.repayments.isReadonly()) {
//			this.repayments.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_Repayments.value") }));
//		}
//		if (!this.rateType.isReadonly()) {
//			this.rateType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_RateType.value") }));
//		}
//		if (!this.lCPeriod.isReadonly()) {
//			this.lCPeriod.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_LCPeriod.value") }));
//		}
//		if (!this.usancePeriod.isReadonly()) {
//			this.usancePeriod.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_UsancePeriod.value") }));
//		}
//		if (!this.securityDesc.isReadonly()) {
//			this.securityDesc.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_SecurityDesc.value") }));
//		}
//		if (!this.utilization.isReadonly()) {
//			this.utilization.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_Utilization.value") }));
//		}
//		if (!this.commission.isReadonly()) {
//			this.commission.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_Commission.value") }));
//		}
//		if (!this.purpose.isReadonly()) {
//			this.purpose.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_Purpose.value") }));
//		}
		
//			if (!this.agentBank.isReadonly()) {
//				this.agentBank.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_agentBank.value") }));
//			}
//
//			if (!this.otherDetails.isReadonly()) {
//				this.otherDetails.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FacilityDetailDialog_otherDetails.value") }));
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
				this.totalFacilityCcy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_FacilityDetailDialog_totalFacilityCcy.value") }));
			}*/
			if(!this.underWritingCcy.isReadonly()){ 
				this.underWritingCcy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_FacilityDetailDialog_underWritingCcy.value") }));
			}

			if(!this.propFinalTakeCcy.isReadonly()){ 
				this.propFinalTakeCcy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FacilityDetailDialog_propFinalTakeCcy.value") }));
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aFacilityDetail.getRecordType()).equals("")) {
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
					closeDialog2(this.window_FacilityDetailDialog,"FacilityDetailDialog");
				}
			
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
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
		if (getFacilityDetail().getFacilityFor().equals(PennantConstants.FACILITY_NEW)) {
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
		readOnlyComponent(isReadOnly("FacilityDetailDialog_guarantee"), this.guarantee);//TODO Create Rights
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
		// remember the old vars
		doStoreInitValues();
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
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aFacilityDetail.getRecordType()).equals("")) {
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
			if (StringUtils.trimToEmpty(aFacilityDetail.getRecordType()).equals("")) {
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
					PTMessageUtils.showErrorMessage(Labels.getLabel("DATA_ALREADY_EXISTS",new String[]{Labels.getLabel("label_FacilityRef")+":"+aFacilityDetail.getFacilityRef()}));
					return;
				}
			}
			
			AuditHeader auditHeader = processFacilityDetails(aFacilityDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FacilityDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFacilityDetailListCtrl().doFillFacilityDetail(this.facilityDetailsList);
				closeDialog2(this.window_FacilityDetailDialog,"FacilityDetailDialog");
			}
	
		} catch (final DataAccessException e) {
			logger.error(e);
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
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aFacilityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							facilityDetailsList.add(aFacilityDetail);
						} else if (aFacilityDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aFacilityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aFacilityDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							facilityDetailsList.add(aFacilityDetail);
						} else if (aFacilityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							List<FacilityDetail> savedList = getFacilityDetailListCtrl().getFacility().getFacilityDetails();
							for (int j = 0; j < savedList.size(); j++) {
								FacilityDetail fee = savedList.get(j);
								if (fee.getFacilityRef().equals(aFacilityDetail.getFacilityRef())) {
									facilityDetailsList.add(fee);
								}
							}
						} else if (aFacilityDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aFacilityDetail.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
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
					getFacilityDetail().setTotalFacilityFormatter(details.getCcyEditField());
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
					getFacilityDetail().setUnderWritingFormatter(details.getCcyEditField());
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
					getFacilityDetail().setPropFinalTakeFormatter(details.getCcyEditField());
					this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				}
			}
			logger.debug("Leaving " + event.toString());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FacilityDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("FacilityDetail");
		notes.setReference(getFacilityDetail().getCAFReference());
		notes.setVersion(getFacilityDetail().getVersion());
		return notes;
	}

	private void doClearMessage() {
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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
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
		if (object != null && object instanceof Currency) {
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
		boolean isTranTypeSyndiation = this.transactionType.getSelectedItem().getValue().equals(PennantConstants.FACILITY_TRAN_SYNDIACTION);
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
		if (!StringUtils.trimToEmpty(getFacilityDetail().getTermSheetRef()).equals("")) {
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
		boolean isTranTypeSyndiation = this.transactionType.getSelectedItem().getValue().equals(PennantConstants.FACILITY_TRAN_SYNDIACTION);
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
		if(!StringUtils.trimToEmpty(this.facilityCCY.getValidatedValue()).equals("")){
			Currency currency=((Currency)this.facilityCCY.getObject());
			if (currency!=null) {
				if(StringUtils.trimToEmpty(totalFacilityCcy.getValue()).equals("")){
					this.totalFacilityCcy.setValue(currency.getCcyCode(),currency.getCcyDesc());
					this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(currency.getCcyEditField()));
					getFacilityDetail().setTotalFacilityFormatter(currency.getCcyEditField());
				}
				if(StringUtils.trimToEmpty(underWritingCcy.getValue()).equals("")){
					this.underWritingCcy.setValue(currency.getCcyCode(),currency.getCcyDesc());
					this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(currency.getCcyEditField()));
					getFacilityDetail().setUnderWritingFormatter(currency.getCcyEditField());
				}
				if(StringUtils.trimToEmpty(propFinalTakeCcy.getValue()).equals("")){
					this.propFinalTakeCcy.setValue(currency.getCcyCode(),currency.getCcyDesc());
					this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(currency.getCcyEditField()));
					getFacilityDetail().setPropFinalTakeFormatter(currency.getCcyEditField());
				}
			}
	
		}
		logger.debug("Leaving");
	}
	
}
