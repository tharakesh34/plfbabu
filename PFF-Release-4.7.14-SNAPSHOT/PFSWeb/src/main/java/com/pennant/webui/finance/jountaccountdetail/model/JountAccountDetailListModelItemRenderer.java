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
 * FileName    		:  JountAccountDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.jountaccountdetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class JountAccountDetailListModelItemRenderer implements ListitemRenderer<JointAccountDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public JountAccountDetailListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, JointAccountDetail jountAccountDetail, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(jountAccountDetail.getCustCIF()+"-"+jountAccountDetail.getLovDescCIFName());
		lc.setParent(item);
	  	lc = new Listcell(jountAccountDetail.getRepayAccountId());
		lc.setParent(item);
	  	lc = new Listcell(jountAccountDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(jountAccountDetail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", jountAccountDetail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onJountAccountDetailItemDoubleClicked");
	}
}