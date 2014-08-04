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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EligibilityEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceEligibilityDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(EligibilityEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_EligibilityEnquiryDialog; 		// autoWired
	protected Listbox 		listBoxEligibility; 					// autoWired
	protected Borderlayout  borderlayoutEligibilityEnquiry;		// autoWired
	private Tabpanel 		tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinanceEligibilityDetail> eligibilityList = null;
	
	/**
	 * default constructor.<br>
	 */
	public EligibilityEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("eligibilityList")) {
			this.eligibilityList = (List<FinanceEligibilityDetail>) args.get("eligibilityList");
		}
		
		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		}

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
			
			// fill the components with the data
			doFillEligibilityList(this.eligibilityList);
			
			if(tabPanel_dialogWindow != null){
				
				getBorderLayoutHeight();
				int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				this.listBoxEligibility.setHeight(this.borderLayoutHeight-rowsHeight-200+"px");
				this.window_EligibilityEnquiryDialog.setHeight(this.borderLayoutHeight-rowsHeight-30+"px");
				tabPanel_dialogWindow.appendChild(this.window_EligibilityEnquiryDialog);

			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill the Finance Eligibility Details List
	 * @param eligibilityDetails
	 */
	public void doFillEligibilityList(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");
		Listitem listitem = null;
		Listcell lc = null;
		for (FinanceEligibilityDetail detail : eligibilityDetails) {
			
			listitem = new Listitem();
			lc = new Listcell(detail.getLovDescElgRuleCode());
			listitem.appendChild(lc);
			
			lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
			listitem.appendChild(lc);
			
			if(detail.isCanOverride()){
				Checkbox checkbox = new Checkbox();
				checkbox.setChecked(true);
				checkbox.setDisabled(true);
				
				lc = new Listcell();
				lc.appendChild(checkbox);
				listitem.appendChild(lc);
			}else{
				lc = new Listcell("");
				listitem.appendChild(lc);
			}
			
			if("D".equals(detail.getRuleResultType())){
				
				lc = new Listcell(detail.getOverridePerc()+"%");
				listitem.appendChild(lc);
				
				if(detail.getRuleResult().equals("E")){
					lc = new Listcell(Labels.getLabel("common.InSuffData"));
					lc.setStyle("font-weight:bold;color:red;");
					lc.setParent(listitem);

					lc = new Listcell("");
					lc.setParent(listitem);
					
					//IF DSR Calculation Rule
				}else if("DSRCAL".equals(detail.getLovDescElgRuleCode())){

					lc = new Listcell(detail.getRuleResult()+"%");
					lc.setParent(listitem);

					lc = new Listcell("");
					lc.setParent(listitem);

				}else{

					lc = new Listcell(PennantAppUtil.amountFormate(new BigDecimal(detail.getRuleResult()),3));
					lc.setStyle("text-align:right;");
					lc.setParent(listitem);

					lc = new Listcell(PennantAppUtil.amountFormate(detail.getOverrideResult(), 3));
					lc.setStyle("text-align:right;");
					lc.setParent(listitem);
				}
				
			}else if("S".equals(detail.getRuleResultType()) || "B".equals(detail.getRuleResultType())){
				
				lc = new Listcell("");
				listitem.appendChild(lc);
				
				lc = new Listcell("Eligible");
				lc.setStyle("font-weight:bold;color:green;");
				listitem.appendChild(lc);
				
				lc = new Listcell("");
				listitem.appendChild(lc);
			}
			
			if(detail.isUserOverride() && detail.isCanOverride()){
				Checkbox checkbox = new Checkbox();
				checkbox.setChecked(detail.isUserOverride());
				checkbox.setDisabled(true);
				
				lc = new Listcell();
				lc.appendChild(checkbox);
				listitem.appendChild(lc);
			}else{
				lc = new Listcell("");
				listitem.appendChild(lc);
			}
			
			this.listBoxEligibility.appendChild(listitem);
		}
		logger.debug("Leaving");
	}


}
