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
 * FileName    		:  LegalExpensesListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.process.failProcess.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.DDAFTransactionLog;
import com.pennant.backend.util.PennantConstants;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FailPostingsListModelItemRenderer implements ListitemRenderer<DDAFTransactionLog>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, DDAFTransactionLog ddaFTransactionLog, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(ddaFTransactionLog.getFinRefence());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatDate(ddaFTransactionLog.getValueDate(),PennantConstants.dateAndTimeFormat));
		lc.setParent(item);
		lc = new Listcell(ddaFTransactionLog.getErrorCode());
		lc.setParent(item);
		lc = new Listcell(ddaFTransactionLog.getErrorDesc());
		lc.setParent(item);
		lc = new Listcell(Integer.toString(ddaFTransactionLog.getNoofTries()));
		lc.setParent(item);
		item.setAttribute("data", ddaFTransactionLog);
		ComponentsCtrl.applyForward(item, "onClick=onDDAItemChecked");
		
	}
}