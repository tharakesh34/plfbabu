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
 * FileName    		:  ProvisionMovementListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.provisionmovement.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.util.PennantAppUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ProvisionPostingsListModelItemRenderer implements ListitemRenderer<ReturnDataSet>, Serializable {

	private static final long serialVersionUID = -4343497695244309847L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, ReturnDataSet returnDataSet, int count) throws Exception {

		//final ReturnDataSet returnDataSet = (ReturnDataSet) data;
		Listcell lc;
	  	lc = new Listcell(returnDataSet.getPostDate().toString());
		lc.setParent(item);
	  	lc = new Listcell(returnDataSet.getValueDate().toString());
	  	lc.setParent(item);
	  	lc = new Listcell(returnDataSet.getAccount());
	  	lc.setParent(item);
	  	lc = new Listcell(returnDataSet.getDrOrCr().equals("D")?"Debit":"Credit");
	  	lc.setParent(item);
	  	lc = new Listcell(returnDataSet.getTranCode());
	  	lc.setParent(item);
	  	lc = new Listcell(PennantAppUtil.amountFormate(returnDataSet.getPostAmount(),0));
	  	lc.setStyle("text-align:right;");
	  	lc.setParent(item);
	  	lc = new Listcell(returnDataSet.getPostStatus());
	  	lc.setParent(item);
		lc = new Listcell(returnDataSet.getPostingId());
		lc.setParent(item);
	}
}