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
 * FileName    		:  ScheduleEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class EligibilityEnquiryDialogCtrl extends GFCBaseCtrl<FinanceEligibilityDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(EligibilityEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_EligibilityEnquiryDialog; 		
	protected Listbox 		listBoxEligibility; 					
	protected Borderlayout  borderlayoutEligibilityEnquiry;		
	private Tabpanel 		tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinanceEligibilityDetail> eligibilityList = null;
	private int formatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	
	/**
	 * default constructor.<br>
	 */
	public EligibilityEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_EligibilityEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EligibilityEnquiryDialog);

		if(event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("eligibilityList")) {
			this.eligibilityList = (List<FinanceEligibilityDetail>) arguments.get("eligibilityList");
		}
		
		// READ OVERHANDED parameters !
		if (arguments.containsKey("finAmountformatter")) {
			this.formatter = Integer.parseInt(arguments.get("finAmountformatter").toString());
		}
		
		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl");
		}

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			String finreference="";
			if (eligibilityList!=null && !eligibilityList.isEmpty()) {
				finreference=eligibilityList.get(0).getFinReference();
			}
			
			List<FinanceDeviations>	deviations=getFinanceDevaitions(finreference);
			
			if (deviations!=null && !deviations.isEmpty()) {
				for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityList) {
					setStatusByDevaition(financeEligibilityDetail,deviations);
				}
			}
		
			// fill the components with the data
			doFillEligibilityList(this.eligibilityList);
			
			if(tabPanel_dialogWindow != null){
				
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxEligibility.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_EligibilityEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				tabPanel_dialogWindow.appendChild(this.window_EligibilityEnquiryDialog);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill the Finance Eligibility Details List
	 * @param eligibilityDetails
	 */
	public void doFillEligibilityList(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");

		this.listBoxEligibility.getItems().clear();
		String overridePerc = "";
		if (eligibilityDetails != null && !eligibilityDetails.isEmpty()) {
			for (FinanceEligibilityDetail detail : eligibilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				// Rule Code
				lc = new Listcell(detail.getLovDescElgRuleCode());
				lc.setParent(item);

				// Rule Code Desc
				lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
				lc.setParent(item);

				// Can Override
				lc = new Listcell();
				Checkbox cbOverride = new Checkbox();
				cbOverride.setDisabled(true);

				if (detail.isCanOverride()) {
					cbOverride.setChecked(true);
					overridePerc = String.valueOf(detail.getOverridePerc());
				} else {
					cbOverride.setChecked(false);
					overridePerc = "";
				}
				lc.appendChild(cbOverride);
				lc.setParent(item);

				// Override Value
				lc = new Listcell(overridePerc);
				lc.setParent(item);

				// If Rule Not Executed
				// Bug Fix Change
				if (StringUtils.isEmpty(detail.getRuleResult())) {

					lc = new Listcell("");
					lc.setParent(item);

				} else {

					String labelCode = "";
					String StyleCode = "";

					if (detail.isEligible()) {
						if (detail.isEligibleWithDevaition()) {
							labelCode = Labels.getLabel("common.Eligible_Deviation");
						} else {
							labelCode = Labels.getLabel("common.Eligible");
						}
						StyleCode = "font-weight:bold;color:green;";

					} else {
						labelCode = Labels.getLabel("common.Ineligible");
						StyleCode = "font-weight:bold;color:red;";
					}

					if(RuleConstants.RETURNTYPE_DECIMAL.equals(detail.getRuleResultType())){
						if(RuleConstants.ELGRULE_DSRCAL.equals(detail.getLovDescElgRuleCode()) || 
								RuleConstants.ELGRULE_PDDSRCAL.equals(detail.getLovDescElgRuleCode())){
							BigDecimal val = new BigDecimal(detail.getRuleResult());
							val=val.setScale(2,RoundingMode.HALF_DOWN);
							labelCode = String.valueOf(val)+"%";
						}else{
							labelCode =	PennantAppUtil.amountFormate(new BigDecimal(detail.getRuleResult()),formatter);
						}
						
						StyleCode = "text-align:right;";
					}

					lc = new Listcell(labelCode);
					lc.setStyle(StyleCode);
					lc.setParent(item);

				}

				lc = new Listcell("");
				lc.setParent(item);

				lc = new Listcell("");
				lc.setParent(item);

				listBoxEligibility.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}


	private void setStatusByDevaition(FinanceEligibilityDetail finElgDet,List<FinanceDeviations> deviations) {

		FinanceDeviations deviation = null;

		for (FinanceDeviations financeDeviations : deviations) {
			if (PennantConstants.RCD_STATUS_REJECTED.equals(StringUtils.trimToEmpty(financeDeviations.getApprovalStatus()))) {
				continue;
			}
			if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
				deviation = financeDeviations;
				break;
			}
		}
	
		if (deviation != null) {
			finElgDet.setEligibleWithDevaition(true);
		} 
	}
	
	
	public List<FinanceDeviations> getFinanceDevaitions(String finReference){
		JdbcSearchObject<FinanceDeviations> jdbcSearchObject=new JdbcSearchObject<FinanceDeviations>(FinanceDeviations.class);
		jdbcSearchObject.addTabelName("FinanceDeviations");
		jdbcSearchObject.addFilterEqual("FinReference", finReference);
		jdbcSearchObject.addFilterEqual("Module", DeviationConstants.TY_ELIGIBILITY);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		return pagedListService.getBySearchObject(jdbcSearchObject);
	}
	
}
