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
 * FileName    		:  BasicFinanceTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.basicfinancetype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.BasicFinanceType;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for list items in the list box.
 * 
 */
public class BasicFinanceTypeListModelItemRenderer implements ListitemRenderer<BasicFinanceType>, Serializable {

	private static final long serialVersionUID = 6360481436034503547L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, BasicFinanceType basicFinanceType, int count) throws Exception {

		//final BasicFinanceType basicFinanceType = (BasicFinanceType) data;
		Listcell lc;
	  	lc = new Listcell(basicFinanceType.getFinBasicType());
		lc.setParent(item);
	  	lc = new Listcell(basicFinanceType.getFinBasicDesc());
		lc.setParent(item);
	  	lc = new Listcell(basicFinanceType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(basicFinanceType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", basicFinanceType);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBasicFinanceTypeItemDoubleClicked");
	}
}