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
 * FileName    		:  EligibilityRuleResultCtrl.java                                                   * 	  
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
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.pennant.backend.service.finance.EligibilityRule;
import com.pennant.webui.finance.enquiry.model.EligibilityCheckComparator;
import com.pennant.webui.finance.enquiry.model.EligibilityCheckListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * This is the controller class for the 
 * /WEB-INF/pages/Enquiry/FinanceInquiry/EligibilityRuleResult.zul file.
 */
public class EligibilityRuleResultCtrl extends GFCBaseListCtrl<EligibilityRule> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(EligibilityRuleResultCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ElgRuleResult; 		

	protected Borderlayout	borderlayoutElgRuleResult;	
	protected Listbox		listBoxElgRule;

	// not auto wired variables
	private int formatter = 3;

	/**
	 * default constructor.<br>
	 */
	public EligibilityRuleResultCtrl() {
		super();
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onCreate$window_ElgRuleResult(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		if (arguments.containsKey("formatter")) {
			this.formatter = (Integer) arguments.get("formatter");
		}
		
		List<EligibilityRule> elgRuleList = new ArrayList<EligibilityRule>();
		if (arguments.containsKey("elgRuleList")) {
			elgRuleList = (List<EligibilityRule>) arguments.get("elgRuleList");
		}
		
		getBorderLayoutHeight();
		this.listBoxElgRule.setHeight(borderLayoutHeight - 249+"px");
		
		this.listBoxElgRule.setModel(new GroupsModelArray(
				elgRuleList.toArray(),new EligibilityCheckComparator()));
		this.listBoxElgRule.setItemRenderer(new EligibilityCheckListItemRenderer(formatter));
		this.window_ElgRuleResult.doModal();
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
			this.window_ElgRuleResult.onClose();
		} catch (final WrongValuesException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

}
