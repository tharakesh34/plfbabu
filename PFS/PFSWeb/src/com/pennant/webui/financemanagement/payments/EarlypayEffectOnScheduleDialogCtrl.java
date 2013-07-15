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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  ManualPaymentDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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
package com.pennant.webui.financemanagement.payments;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/Payments/EarlypayEffectOnSchedule.zul <br/>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EarlypayEffectOnScheduleDialogCtrl extends GFCBaseListCtrl<FinanceMain> {

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger.getLogger(EarlypayEffectOnScheduleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 			window_EarlypayEffectOnSchedule;
	protected Borderlayout		borderlayout_EarlypayEffectOnSchedule;

	//Summary Details
	protected Combobox 			effectOnSchedule;
	
	//Buttons
	protected Button 			btnHelp;
	protected Button 			btnClose;
	protected Button 			btnProceed;
	
	private ManualPaymentDialogCtrl manualPaymentDialogCtrl = null;
	private RepayData repayData = null;

	static final List<ValueLabel> earlyRpyEffectList = PennantAppUtil.getScheduleOn();

	/**
	 * default constructor.<br>
	 */
	public EarlypayEffectOnScheduleDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EarlypayEffectOnSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("manualPaymentDialogCtrl")) {
			manualPaymentDialogCtrl = (ManualPaymentDialogCtrl) args.get("manualPaymentDialogCtrl");
		}
		
		if (args.containsKey("repayData")) {
			repayData = (RepayData) args.get("repayData");
		}
		
		fillComboBox(this.effectOnSchedule, "", earlyRpyEffectList, "");
		this.window_EarlypayEffectOnSchedule.doModal();
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
		closeWindow();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnProceed(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.effectOnSchedule.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)){
			throw new WrongValueException(this.effectOnSchedule, Labels.getLabel("STATIC_INVALID",
					new String[]{Labels.getLabel("label_EarlypayEffectOnSchedule_effectOnSchedule.value")}));
		}
		
		fillComboBox(this.manualPaymentDialogCtrl.earlyRpyEffectOnSchd, 
				this.effectOnSchedule.getSelectedItem().getValue().toString(), earlyRpyEffectList, "");

		this.manualPaymentDialogCtrl.setEarlyRepayEffectOnSchedule(this.repayData);
		closeWindow();
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * To Close the tab when fin reference search dialog is closed <br>
	 * IN ManualPaymentDialogCtrl.java void
	 */
	public void closeWindow() {
		this.window_EarlypayEffectOnSchedule.onClose();
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

}