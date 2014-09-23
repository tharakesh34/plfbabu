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

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/
 * SelectFinanceTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SelectFacilityTypeDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 8556168885363682933L;
	private final static Logger logger = Logger.getLogger(SelectFacilityTypeDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_SelectFacilityTypeDialog; // autoWired
	protected ExtendedCombobox custId;
	protected Button btnProceed; // autoWired
	protected FacilityListCtrl facilityListCtrl; // over handed parameter
	private Facility facility = null;
	private transient WorkFlowDetails workFlowDetails = null;
	private transient FinanceWorkFlowService financeWorkFlowService;
	private transient FacilityService facilityService;
	private String cafType="";
	

	/**
	 * default constructor.<br>
	 */
	public SelectFacilityTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectFacilityTypeDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("facility")) {
			this.facility = (Facility) args.get("facility");
			Facility befImage = new Facility();
			BeanUtils.copyProperties(this.facility, befImage);
			this.facility.setBefImage(befImage);
			setFacility(this.facility);
		} else {
			setFacility(null);
		}
		if (args.containsKey("facilityListCtrl")) {
			this.facilityListCtrl = (FacilityListCtrl) args.get("facilityListCtrl");
		} else {
			setFacilityListCtrl(null);
		}
		if (args.containsKey("cafType")) {
			this.cafType = (String) args.get("cafType");
		}
		showSelectFinanceTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if(StringUtils.trimToEmpty(this.custId.getValue()).equals("")){
			throw new WrongValueException(this.custId,Labels.getLabel("FIELD_IS_MAND",new String[]{Labels.getLabel("label_SelectFacilityTypeDialog_CustId.value")})); 
		}
		this.window_SelectFacilityTypeDialog.onClose();
		Customer customer = (Customer) this.custId.getObject();
		if (customer!=null) {
			FinanceWorkFlow financeWorkFlow =null;
			financeWorkFlow = getFinanceWorkFlow(cafType);
			getFacility().setFacilityType(cafType);
			// Workflow Details Setup
			if (financeWorkFlow != null) {
				workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				if (workFlowDetails == null) {
					setWorkFlowEnabled(false);
					getFacility().setWorkflowId(0);
				} else {
					setWorkFlowEnabled(true);
					setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
					setWorkFlowId(workFlowDetails.getId());
					getFacility().setWorkflowId(workFlowDetails.getWorkFlowId());
				}
				Date appldate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
				getFacility().setStartDate(appldate);
				getFacility().setPresentingUnit(PennantConstants.FACILITY_PRESENTING_UNIT);
				getFacility().setCustID(customer.getCustID());
				getFacility().setCustCIF(customer.getCustCIF());
				getFacility().setCustShrtName(customer.getCustShrtName());
				getFacility().setCustCtgCode(customer.getCustCtgCode());
				getFacility().setCountryOfDomicile(customer.getCustCOB());
				getFacility().setCountryOfDomicileName(customer.getLovDescCustCOBName());
				getFacility().setCountryOfRisk(customer.getCustRiskCountry());
				getFacility().setCountryOfRiskName(customer.getLovDescCustRiskCountryName());
				getFacility().setEstablishedDate(customer.getCustDOB());
				getFacility().setNatureOfBusiness(customer.getCustSector());
				getFacility().setNatureOfBusinessName(customer.getLovDescCustSectorName());
				getFacility().setSICCode(customer.getCustSubSector());
				getFacility().setSICCodeName(customer.getLovDescCustSubSectorName());
				getFacility().setRelationshipManager(customer.getCustRO1());
				getFacility().setCustDOB(customer.getCustFirstBusinessDate());
				getFacility().setCustCoreBank(customer.getCustCoreBank());
				getFacility().setCustRelation(customer.getCustRelation());
				getFacility().setCustTypeDesc(customer.getLovDescCustTypeCodeName());
				if (customer.getCustGroupID() != 0) {
					getFacility().setCustomerGroup(customer.getCustGroupID());
					getFacility().setCustGrpCodeName(customer.getLovDescCustGroupCode());
					getFacility().setCustomerGroupName(customer.getLovDesccustGroupIDName());
				}
		
				getFacility().setUserRole(getRole());
				//set Previous Facility  Data if any
				setFacility(setPreviousCAfDetails(getFacility()));
				// Fetching Finance Reference Detail
				setFacility(getFacilityService().getFacilityChildRecords(getFacility()));
				getFacilityService().setFacilityScoringDetails(getFacility());
				getFacility().setCustomerEligibilityCheck(getFacilityService().getCustomerEligibility(customer, 0));
				getFacility().setCustomerRatings(getFacilityService().getCustomerRatingByCustomer(facility.getCustID()));
				getFacility().setCAFReference(PennantReferenceIDUtil.genNewCafRef(cafType,getFacility().getCustCtgCode()));
				setFacility(getFacilityService().setCustomerDocuments(getFacility()));

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("facility", getFacility());
				map.put("facilityListCtrl", this.facilityListCtrl);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}else{
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	private Facility setPreviousCAfDetails(Facility facility) {
		logger.debug("Entering");
		try {
			Facility prvFacility = getFacilityService().getLatestFacilityByCustID(facility.getCustID());
			if (prvFacility!=null) {
				facility.setCustomerBackGround(prvFacility.getCustomerBackGround());
				facility.setStrength(prvFacility.getStrength());
				facility.setWeaknesses(prvFacility.getWeaknesses());
				facility.setSourceOfRepayment(prvFacility.getSourceOfRepayment());
				facility.setAdequacyOfCashFlows(prvFacility.getAdequacyOfCashFlows());
				facility.setTypesOfSecurities(prvFacility.getTypesOfSecurities());
				facility.setGuaranteeDescription(prvFacility.getGuaranteeDescription());
				facility.setFinancialSummary(prvFacility.getFinancialSummary());
				facility.setMitigants(prvFacility.getMitigants());
				facility.setPurpose(prvFacility.getPurpose());
				facility.setAccountRelation(prvFacility.getAccountRelation());
				facility.setLimitAndAncillary(prvFacility.getLimitAndAncillary());
				facility.setAntiMoneyLaunderSection(prvFacility.getAntiMoneyLaunderSection());	
			}
			
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
		return facility;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			this.custId.setTextBoxWidth(150);
			this.custId.setMaxlength(12);
			this.custId.setMandatoryStyle(true);
			this.custId.setModuleName("Customer");
			this.custId.setValueColumn("CustCIF");
			this.custId.setDescColumn("CustShrtName");
			this.custId.setValidateColumns(new String[] { "CustCIF" });
			// open the dialog in modal mode
			this.window_SelectFacilityTypeDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	public FinanceWorkFlow getFinanceWorkFlow(String finref){
		JdbcSearchObject<FinanceWorkFlow> jdbcSearchObject=new JdbcSearchObject<FinanceWorkFlow>(FinanceWorkFlow.class);
		jdbcSearchObject.addFilterEqual("FinType", finref);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FinanceWorkFlow> list = pagedListService.getBySearchObject(jdbcSearchObject);
		if (list!=null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Getters and Setters ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	public FacilityListCtrl getFacilityListCtrl() {
		return facilityListCtrl;
	}

	public void setFacilityListCtrl(FacilityListCtrl facilityListCtrl) {
		this.facilityListCtrl = facilityListCtrl;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

}
