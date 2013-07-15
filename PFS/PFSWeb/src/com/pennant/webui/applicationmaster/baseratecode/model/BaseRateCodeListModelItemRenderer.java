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
 * FileName    		:  BaseRateCodeListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.baseratecode.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class BaseRateCodeListModelItemRenderer implements ListitemRenderer<BaseRateCode>, Serializable {

	private static final long serialVersionUID = -8176701674841176336L;
	private final static Logger logger = Logger.getLogger(BaseRateCodeListModelItemRenderer.class);

	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, BaseRateCode baseRateCode, int count) throws Exception {
		logger.debug("Entering");
		//final BaseRateCode baseRateCode = (BaseRateCode) data;
		Listcell lc;
	  	lc = new Listcell(baseRateCode.getBRType());
		lc.setParent(item);
	  	lc = new Listcell(baseRateCode.getBRTypeDesc());
		lc.setParent(item);
	  	lc = new Listcell(baseRateCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(baseRateCode.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", baseRateCode);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBaseRateCodeItemDoubleClicked");
		logger.debug("Leaving");
	}
}