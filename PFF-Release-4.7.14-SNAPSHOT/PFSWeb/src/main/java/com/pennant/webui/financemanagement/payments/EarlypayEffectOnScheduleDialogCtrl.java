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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/Payments/EarlypayEffectOnSchedule.zul
 */
public class EarlypayEffectOnScheduleDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(EarlypayEffectOnScheduleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 			window_EarlypayEffectOnSchedule;
	protected Borderlayout		borderlayout_EarlypayEffectOnSchedule;

	//Summary Details
	protected Combobox 			effectOnSchedule;
	
	//Buttons
	protected Button 			btnHelp;
	protected Button 			btnProceed;
	
	private ManualPaymentDialogCtrl manualPaymentDialogCtrl = null;
	private RepayData repayData = null;

	List<ValueLabel> earlyRpyEffectList = PennantStaticListUtil.getEarlyPayEffectOn();

	/**
	 * default constructor.<br>
	 */
	public EarlypayEffectOnScheduleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EarlypayEffectOnSchedule(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EarlypayEffectOnSchedule);


		if (arguments.containsKey("manualPaymentDialogCtrl")) {
			manualPaymentDialogCtrl = (ManualPaymentDialogCtrl) arguments.get("manualPaymentDialogCtrl");
		}
		
		if (arguments.containsKey("repayData")) {
			repayData = (RepayData) arguments.get("repayData");
		}
		
		List<ValueLabel> epList = new ArrayList<>();
		if(manualPaymentDialogCtrl.getFinanceType() != null){
			FinanceType financeType = manualPaymentDialogCtrl.getFinanceType();
			String[] epMthds = financeType.getAlwEarlyPayMethods().split(",");
			if(epMthds.length > 0){
				List<String> list = Arrays.asList(epMthds);
				for (ValueLabel epMthd : earlyRpyEffectList) {
					if(list.contains(epMthd.getValue())){
						epList.add(epMthd);
					}
				}
			}
		}
		
		if(repayData.getFinanceDetail().getFinScheduleData().getFinanceMain() != null && 
				CalculationConstants.RATE_BASIS_D.equals(StringUtils.trimToEmpty(repayData.getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayRateBasis()))){
			fillComboBox(this.effectOnSchedule, "", epList, ",ADJMUR,ADMPFI,RECRPY,RECPFI,");
		}else{
			fillComboBox(this.effectOnSchedule, "", epList, "");
		}
		
		
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
		
		this.manualPaymentDialogCtrl.totRefundAmt.setDisabled(true);
		repayData.getRepayMain().setRepayAmountExcess(BigDecimal.ZERO);
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
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

}