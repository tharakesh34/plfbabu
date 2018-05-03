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
 * FileName    		:  AccountEngineEventListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  27-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.bmtmasters.accountengineevent.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class AccountEngineEventListModelItemRenderer implements ListitemRenderer<AccountEngineEvent>, Serializable {

	private static final long serialVersionUID = -2080374371892916897L;
	
	public AccountEngineEventListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, AccountEngineEvent accountEngineEvent, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(accountEngineEvent.getAEEventCode());
		lc.setParent(item);
		lc = new Listcell(accountEngineEvent.getAEEventCodeDesc());
		lc.setParent(item);
		lc = new Listcell(accountEngineEvent.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(accountEngineEvent.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", accountEngineEvent);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountEngineEventItemDoubleClicked");
	}
}