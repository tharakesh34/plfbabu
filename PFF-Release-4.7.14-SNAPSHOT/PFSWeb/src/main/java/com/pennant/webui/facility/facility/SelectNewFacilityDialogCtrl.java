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
 * FileName    		:  FinanceMainQDEDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-11-2011    														*
 *                                                                  						*
 * Modified Date    :  16-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/
 * SelectFinanceTypeDialog.zul file.
 */
public class SelectNewFacilityDialogCtrl extends GFCBaseCtrl<Facility> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = Logger.getLogger(SelectNewFacilityDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectNewFacilityDialog; 
	protected Combobox approvalFor;
	protected Label label_financeReference;
	protected ExtendedCombobox financeReference;
	protected Button btnProceed; 
	protected FacilityDetailListCtrl facilityDetailListCtrl; // over handed
	// parameter
	private Facility facility = null;
	private FacilityDetail facilityDetail;
	private Commitment commitment=null;
	private transient FacilityService facilityService;
	private String[] termsdetails;

	/**
	 * default constructor.<br>
	 */
	public SelectNewFacilityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectNewFacilityDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectNewFacilityDialog);

		if (arguments.containsKey("facility")) {
			setFacility((Facility) arguments.get("facility"));
		} else {
			setFacility(null);
		}
		if (arguments.containsKey("filter")) {
			termsdetails= (String[]) arguments.get("filter");
		}
		if (arguments.containsKey("facilityDetail")) {
			setFacilityDetail((FacilityDetail) arguments.get("facilityDetail"));
		} else {
			setFacilityDetail(null);
		}
		if (arguments.containsKey("facilityDetailListCtrl")) {
			setFacilityDetailListCtrl((FacilityDetailListCtrl) arguments.get("facilityDetailListCtrl"));
		} else {
			setFacilityDetailListCtrl(null);
		}
		fillComboBox(approvalFor, "", PennantStaticListUtil.getFacilityApprovalFor(), "");
		showSelectFinanceTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	public void onChange$approvalFor(Event event) {
		logger.debug("Entering");
		this.financeReference.setValue("", "");
		if (this.approvalFor.getSelectedItem() != null && !StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
			this.financeReference.setReadonly(false);
			if (StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()).equals(FacilityConstants.FACILITY_NEW)) {
				this.financeReference.setMandatoryStyle(false);
				this.financeReference.setModuleName("IndicativeTermDetail");
				this.financeReference.setValueColumn("FinReference");
				this.financeReference.setDescColumn("RpsnName");
				Filter[] filters =null;
				if (termsdetails!=null && termsdetails.length!=0) {
					filters = new Filter[2];
					filters[0] = new Filter("CustID", getFacility().getCustID(), Filter.OP_EQUAL);
					filters[1] = new Filter("FinReference", termsdetails, Filter.OP_NOT_IN);
				}else{
					filters = new Filter[1];
					filters[0] = new Filter("CustID", getFacility().getCustID(), Filter.OP_EQUAL);
				}
				this.financeReference.setFilters(filters);
				this.financeReference.setValidateColumns(new String[] { "FinReference" });
				this.label_financeReference.setValue(Labels.getLabel("label_SelectNewFacilityDialog_FinanceReference.value"));

			} else if (StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()).equals(FacilityConstants.FACILITY_REVIEW)||
					StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()).equals(FacilityConstants.FACILITY_AMENDMENT)) {
				this.financeReference.setMandatoryStyle(true);
				this.financeReference.setModuleName("Commitment");
				this.financeReference.setValueColumn("CmtReference");
				this.financeReference.setDescColumn("CmtTitle");
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("CustID", getFacility().getCustID(), Filter.OP_EQUAL);
				this.financeReference.setFilters(filters);
				this.financeReference.setValidateColumns(new String[] { "CmtReference" });
				this.label_financeReference.setValue(Labels.getLabel("label_SelectNewFacilityDialog_FacilityReference.value"));
			}
		} else {
			this.financeReference.setValue("", "");
			this.financeReference.setReadonly(true);
			this.label_financeReference.setValue(Labels.getLabel("label_SelectNewFacilityDialog_FinanceReference.value"));
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (this.approvalFor.getSelectedItem() == null || StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()).equals(PennantConstants.List_Select)) {
			throw new WrongValueException(this.approvalFor, Labels.getLabel("FIELD_IS_MAND", new String[] { label_financeReference.getValue() }));
		}
		if (!StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()).equals(FacilityConstants.FACILITY_NEW)) {
			if (this.financeReference.getValidatedValue() == null || StringUtils.isBlank(this.financeReference.getValidatedValue())) {
				throw new WrongValueException(this.financeReference, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_SelectNewFacilityDialog_FinanceReference.value") }));
			}
		}

		dowriteData();
		this.window_SelectNewFacilityDialog.onClose();
		getFacilityDetailListCtrl().loadWindow(getFacilityDetail(),getCommitment());
		logger.debug("Leaving " + event.toString());
	}

	private void dowriteData() {
		logger.debug("Entering");
		getFacilityDetail().setFacilityFor(StringUtils.trimToEmpty(this.approvalFor.getSelectedItem().getValue().toString()));
		if (getFacilityDetail().getFacilityFor().equals(FacilityConstants.FACILITY_NEW)) {
			getFacilityDetail().setFacilityRef(ReferenceUtil.genNewFacilityRef(getFacilityDetail().getCAFReference()));
			IndicativeTermDetail termDetail = null;
			if (this.financeReference.getObject()!=null && this.financeReference.getObject() instanceof IndicativeTermDetail) {
				termDetail = (IndicativeTermDetail) this.financeReference.getObject();
			}
			if (termDetail !=null) {
				FinanceMain financeMain = getFinanceByCust(this.financeReference.getValue());
				
				getFacilityDetail().setFacilityType(termDetail.getFacilityType());
				getFacilityDetail().setFacilityCCY(financeMain.getFinCcy());
				getFacilityDetail().setFinanceAmount(financeMain.getFinAmount());
				getFacilityDetail().setPricing(termDetail.getPricing());
				getFacilityDetail().setRepayments(termDetail.getRepayments());
				getFacilityDetail().setLCPeriod(termDetail.getLCPeriod());
				getFacilityDetail().setUsancePeriod(termDetail.getUsancePeriod());
				getFacilityDetail().setSecurityClean(termDetail.isSecurityClean());
				getFacilityDetail().setSecurityDesc(termDetail.getSecurityName());
				getFacilityDetail().setUtilization(termDetail.getUtilization());
				getFacilityDetail().setCommission(termDetail.getCommission());
				getFacilityDetail().setPurpose(termDetail.getPurpose());
				getFacilityDetail().setExposure(BigDecimal.ZERO);
				getFacilityDetail().setExistingLimit(BigDecimal.ZERO);
				getFacilityDetail().setTermSheetRef(termDetail.getFinReference());
				getFacilityDetail().setRevolving(termDetail.getLovDescRevolving());
				getFacilityDetail().setStartDate(termDetail.getLovDescFinStartDate());
				getFacilityDetail().setMaturityDate(termDetail.getLovDescMaturityDate());
				getFacilityDetail().setGuarantee(termDetail.getGuarantee());
				getFacilityDetail().setCovenants(termDetail.getCovenants());
				getFacilityDetail().setDocumentsRequired(termDetail.getDocumentsRequired());
				getFacilityDetail().setTenorYear(termDetail.getTenorYear());
				getFacilityDetail().setTenorMonth(termDetail.getTenorMonth());
				getFacilityDetail().setTenorDesc(termDetail.getTenorDesc());

				getFacilityDetail().setTransactionType(termDetail.getTransactionType());
				getFacilityDetail().setAgentBank(termDetail.getAgentBank());
				getFacilityDetail().setOtherDetails(termDetail.getOtherDetails());
				getFacilityDetail().setTotalFacility(termDetail.getTotalFacility());
				getFacilityDetail().setTotalFacilityCcy(termDetail.getTotalFacilityCCY());
				getFacilityDetail().setUnderWriting(termDetail.getUnderWriting());
				getFacilityDetail().setUnderWritingCcy(termDetail.getUnderWritingCCY());
				getFacilityDetail().setPropFinalTake(termDetail.getPropFinalTake());
				getFacilityDetail().setPropFinalTakeCcy(termDetail.getPropFinalTakeCCY());

			}
		} else if (getFacilityDetail().getFacilityFor().equals(FacilityConstants.FACILITY_REVIEW) || 
				getFacilityDetail().getFacilityFor().equals(FacilityConstants.FACILITY_AMENDMENT)) {
			Object object=this.financeReference.getObject();
			if (object!=null) {
				if( object instanceof Commitment){
					Commitment commitment=(Commitment) object;
					setCommitment(commitment);
					FacilityDetail facilitydetails = getFacilityDetailsByReference(commitment.getCmtReference());
					if (facilitydetails != null) {
						facilitydetails.setFacilityFor(getFacilityDetail().getFacilityFor());
						setFacilityDetail(facilitydetails);
					}else{
						getFacilityDetail().setFacilityRef(commitment.getCmtReference());
						getFacilityDetail().setFacilityCCY(commitment.getCmtCcy());
						if (commitment.getCmtPftRateMin()!=null ) {
							getFacilityDetail().setPricing(commitment.getCmtPftRateMin().toString());
						}
					
					}
				}
			}

		}
		logger.debug("Leaving");
	}

	private FinanceMain getFinanceByCust(String finReference) {
		logger.debug("Entering");
		JdbcSearchObject<FinanceMain> searchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		searchObject.addTabelName("WIFFinanceMain_View");
		searchObject.addFilterEqual("FinReference", finReference);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FinanceMain> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			logger.debug("Leaving");
			return list.get(0);
		}
		logger.debug("Leaving");
		return null;
	}
	private FacilityDetail getFacilityDetailsByReference(String facilityRef) {
		logger.debug("Entering");
		JdbcSearchObject<FacilityDetail> searchObject = new JdbcSearchObject<FacilityDetail>(FacilityDetail.class);
		searchObject.addTabelName("FacilityDetails_AView");
		searchObject.addFilterEqual("FacilityRef", facilityRef);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FacilityDetail> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			logger.debug("Leaving");
			return list.get(0);
		}
		logger.debug("Leaving");
		return null;
	}

	// GUI Process
	
	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			this.financeReference.setMandatoryStyle(true);
			this.financeReference.setMaxlength(20);
			// open the dialog in modal mode
			this.window_SelectNewFacilityDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// Getters and Setters
	
	public FacilityDetailListCtrl getFacilityDetailListCtrl() {
		return facilityDetailListCtrl;
	}

	public void setFacilityDetailListCtrl(FacilityDetailListCtrl facilityDetailListCtrl) {
		this.facilityDetailListCtrl = facilityDetailListCtrl;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public void setFacilityDetail(FacilityDetail facilityDetail) {
		this.facilityDetail = facilityDetail;
	}

	public FacilityDetail getFacilityDetail() {
		return facilityDetail;
	}

	public Commitment getCommitment() {
		return commitment;
	}

	public void setCommitment(Commitment commitment) {
		this.commitment = commitment;
	}
	
}
