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
 * FileName    		:  PromotionListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.promotion.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listItems in the listBox.
 */
public class PromotionListModelItemRenderer implements ListitemRenderer<Promotion>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public PromotionListModelItemRenderer() {
		
	}

	@Override
	public void render(Listitem item, Promotion promotion, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(promotion.getPromotionCode());
		lc.setParent(item);
		lc = new Listcell(promotion.getPromotionDesc());
		lc.setParent(item);
		lc = new Listcell(promotion.getFinType());
		lc.setParent(item);
		
		lc = new Listcell(PennantAppUtil.formateDate(promotion.getStartDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.formateDate(promotion.getEndDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		
		lc = new Listcell(promotion.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(promotion.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("promotionCode", promotion.getPromotionCode());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onPromotionItemDoubleClicked");
	}
}