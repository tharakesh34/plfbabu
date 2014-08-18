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
 * FileName    		:  StepPolicyHeaderListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.solutionfactory.steppolicy.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class StepPolicyListModelItemRenderer implements ListitemRenderer<StepPolicyHeader>, Serializable {

	private static final long serialVersionUID = 2118469590661434900L;

	@Override
	public void render(Listitem item, StepPolicyHeader stepPolicyHeader, int count) throws Exception {

			Listcell lc;
			lc = new Listcell(stepPolicyHeader.getPolicyCode());
			lc.setParent(item);
			lc = new Listcell(stepPolicyHeader.getPolicyDesc());
			lc.setParent(item);
			lc = new Listcell(stepPolicyHeader.getStepNumber());
			lc.setParent(item);
			lc.setParent(item);
			String tenorSplitPerc = "";
			if(StringUtils.trimToEmpty(stepPolicyHeader.getTenorSplitPerc()).contains("||")){
				tenorSplitPerc = stepPolicyHeader.getTenorSplitPerc().substring(0, stepPolicyHeader.getTenorSplitPerc().length()-3);
				lc = new Listcell(tenorSplitPerc);		
			}else{
				lc = new Listcell(stepPolicyHeader.getTenorSplitPerc());	
			}
			lc.setParent(item);
			String rateMargin = "";
			if(StringUtils.trimToEmpty(stepPolicyHeader.getRateMargin()).contains("||")){
				 rateMargin = stepPolicyHeader.getRateMargin().substring(0, stepPolicyHeader.getRateMargin().length()-3);
				 lc = new Listcell(rateMargin);
			}else{
				lc = new Listcell(stepPolicyHeader.getRateMargin());
			}
			lc.setParent(item);
			String emiSplitPerc = "";
			if(StringUtils.trimToEmpty(stepPolicyHeader.getEmiSplitPerc()).contains("||")){
				emiSplitPerc = stepPolicyHeader.getEmiSplitPerc().substring(0, stepPolicyHeader.getEmiSplitPerc().length()-3);
				lc = new Listcell(emiSplitPerc);
			}else{
				lc = new Listcell(stepPolicyHeader.getEmiSplitPerc());
			}
			lc.setParent(item);
			lc = new Listcell(stepPolicyHeader.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(stepPolicyHeader.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", stepPolicyHeader);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onStepPolicyHeaderItemDoubleClicked");
	}
}