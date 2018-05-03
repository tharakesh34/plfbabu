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
 * FileName    		:  FinanceEligibilityRuleResultCtrl.java                                                   * 	  
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.finance.FinanceEligibility;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the 
 * /WEB-INF/pages/Enquiry/FinanceInquiry/FinancePurposeSelectDialog.zul file.
 */
public class FinancePurposeSelectDialogCtrl extends GFCBaseCtrl<FinanceEligibility> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(FinancePurposeSelectDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinancePurposeSelectDialog; 		
    protected Combobox      finPurpose;  		
	
	private List<ValueLabel> productAssets = new ArrayList<ValueLabel>();
	private FinanceEligibility finEligibility;
	private FinanceEligibilityRuleResultCtrl finElgRuleResultCtrl;
	
	/**
	 * default constructor.<br>
	 */
	public FinancePurposeSelectDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, 
	 * if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinancePurposeSelectDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinancePurposeSelectDialog);

		if (arguments.containsKey("productAssetlist")) {
			List<ProductAsset> productAssetlist = (List<ProductAsset>) arguments.get("productAssetlist");
			for (ProductAsset productAsset : productAssetlist) {
				productAssets.add(new ValueLabel(productAsset.getAssetCode(),productAsset.getAssetDesc()));
			}
		}
		if (arguments.containsKey("finEligibility")) {
			finEligibility =  (FinanceEligibility) arguments.get("finEligibility");
		}
		if (arguments.containsKey("finElgRuleResultCtrl")) {
			finElgRuleResultCtrl =  (FinanceEligibilityRuleResultCtrl) arguments.get("finElgRuleResultCtrl");
		}
		
		fillComboBox(this.finPurpose, "", productAssets, "");
		
		this.window_FinancePurposeSelectDialog.doModal();
		
		logger.debug("Leaving " + event.toString());
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
			this.window_FinancePurposeSelectDialog.onClose();
		} catch (final WrongValuesException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	public void onClick$btnProceed(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());

		this.finPurpose.setErrorMessage("");
		if("#".equals(this.finPurpose.getSelectedItem().getValue())){
			throw new WrongValueException(this.finPurpose,Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_FinancePurposeDialog_FinPurpose.value")})); 
		}
		this.finEligibility.setLovDescFinPurposeName(this.finPurpose.getSelectedItem().getLabel());
		this.finEligibility.setFinPurpose(this.finPurpose.getSelectedItem().getValue().toString());
		
		processFinanceScreen();
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void processFinanceScreen() throws InterruptedException{
		logger.debug("Entering");
		this.window_FinancePurposeSelectDialog.onClose();
		this.finElgRuleResultCtrl.doCreateFinanceWindow(this.finEligibility);
		logger.debug("Leaving");
	}
	
}
