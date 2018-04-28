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
 * FileName    		:  FinanceReferenceDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.financereferencedetail.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FinanceReferenceDetailListModelItemRenderer implements ListitemRenderer<FinanceWorkFlow>, Serializable {

	private static final long serialVersionUID = 1L;
	List<ValueLabel> eventList = null;
	String moduleName = null;

	public FinanceReferenceDetailListModelItemRenderer(List<ValueLabel> list, String moduleName) {
		this.eventList = list;
		this.moduleName = moduleName;
	}
	
	@Override
	public void render(Listitem item, FinanceWorkFlow financeWorkflow, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(financeWorkflow.getFinType().toUpperCase());
		lc.setParent(item);
		
		String desc = "";
		if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_FINANCE)){
			desc = financeWorkflow.getLovDescFinTypeName();
		}else if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COLLATERAL)){ 
			desc = financeWorkflow.getCollateralDesc();
		} else if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_VAS)){
			desc = financeWorkflow.getVasProductDesc();
		} else if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COMMITMENT)){
			desc = financeWorkflow.getCommitmentTypeDesc();
		}else if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_PROMOTION)){
			desc = financeWorkflow.getLovDescPromotionName();
		}else if(StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_FACILITY)){
			desc = financeWorkflow.getLovDescFacilityTypeName();
		}
				
		lc = new Listcell(desc);
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(financeWorkflow.getFinEvent(), eventList));
		lc.setParent(item);
		item.setAttribute("data", financeWorkflow);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceReferenceDetailItemDoubleClicked");
	}
}