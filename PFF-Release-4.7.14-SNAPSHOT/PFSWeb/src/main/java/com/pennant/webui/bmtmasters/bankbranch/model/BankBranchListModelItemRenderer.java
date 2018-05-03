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
 * FileName    		:  BankBranchListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-10-2016    														*
 *                                                                  						*
 * Modified Date    :  17-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.bankbranch.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BankBranchListModelItemRenderer implements ListitemRenderer<BankBranch>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, BankBranch bankBranch, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(bankBranch.getBankCode());
		lc.setParent(item);
	  	lc = new Listcell(bankBranch.getBranchCode());
		lc.setParent(item);
	  	lc = new Listcell(bankBranch.getCity());
		lc.setParent(item);
	  	lc = new Listcell(bankBranch.getMICR());
		lc.setParent(item);
	  	lc = new Listcell(bankBranch.getIFSC());
		lc.setParent(item);
	  	lc = new Listcell(bankBranch.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(bankBranch.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("id", bankBranch.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBankBranchItemDoubleClicked");
	}
}