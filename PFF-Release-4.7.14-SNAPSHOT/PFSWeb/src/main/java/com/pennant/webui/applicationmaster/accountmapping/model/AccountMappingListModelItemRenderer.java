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
 * FileName    		:  AccountMappingListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.accountmapping.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AccountMappingListModelItemRenderer implements ListitemRenderer<AccountMapping>, Serializable {

	private static final long serialVersionUID = 1L;

	public AccountMappingListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AccountMapping accountMapping, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(accountMapping.getAccount());
		lc.setParent(item);
		lc = new Listcell(accountMapping.getHostAccount());
		lc.setParent(item);
		lc = new Listcell(accountMapping.getAccountType());
		lc.setParent(item);
		lc = new Listcell(accountMapping.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(accountMapping.getRecordType()));
		lc.setParent(item);

		item.setAttribute("account", accountMapping.getAccount());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountMappingItemDoubleClicked");
	}
}