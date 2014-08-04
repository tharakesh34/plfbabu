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
 * FileName    		:  FinanceWorkFlowListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financeworkflow.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FinanceWorkFlowListModelItemRenderer implements ListitemRenderer<FinanceWorkFlow>, Serializable {

	private static final long serialVersionUID = 4456074071015876144L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceWorkFlow financeWorkFlow, int count) throws Exception {

		//final FinanceWorkFlow financeWorkFlow = (FinanceWorkFlow) data;
		Listcell lc;
	  	lc = new Listcell(financeWorkFlow.getFinType());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(financeWorkFlow.getScreenCode(),
				PennantStaticListUtil.getScreenCodes()));
	  	lc.setParent(item);
	  	lc = new Listcell(financeWorkFlow.getWorkFlowType());
		lc.setParent(item);
	  	lc = new Listcell(financeWorkFlow.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeWorkFlow.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", financeWorkFlow);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceWorkFlowItemDoubleClicked");
	}
}