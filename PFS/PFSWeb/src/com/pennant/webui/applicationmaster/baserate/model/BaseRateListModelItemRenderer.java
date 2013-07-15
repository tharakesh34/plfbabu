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
 * FileName    		:  BaseRateListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.baserate.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class BaseRateListModelItemRenderer implements ListitemRenderer<BaseRate>, Serializable {

	private static final long serialVersionUID = -6273517593116519304L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, BaseRate baseRate, int count) throws Exception {
		
		//final BaseRate baseRate = (BaseRate) data;
		Listcell lc;
		lc = new Listcell(baseRate.getBRType()+"-"+baseRate.getLovDescBRTypeName());
		lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formateDate(baseRate.getBREffDate(), PennantConstants.dateFormat));
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.formatRate(baseRate.getBRRate().doubleValue(),9));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(baseRate.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(baseRate.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", baseRate);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBaseRateItemDoubleClicked");
	}
}