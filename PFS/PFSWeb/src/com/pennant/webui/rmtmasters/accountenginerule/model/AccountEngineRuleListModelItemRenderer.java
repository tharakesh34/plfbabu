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
 * FileName    		:  AccountEngineRuleListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.rmtmasters.accountenginerule.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class AccountEngineRuleListModelItemRenderer implements ListitemRenderer<AccountEngineRule>, Serializable {

	private static final long serialVersionUID = 3683647700638888679L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, AccountEngineRule accountEngineRule, int count) throws Exception {
		
		//final AccountEngineRule accountEngineRule = (AccountEngineRule) data;
		Listcell lc;
	  	lc = new Listcell(accountEngineRule.getAEEvent()+"-"+accountEngineRule.getLovDescAEEventName());
		lc.setParent(item);
	  	lc = new Listcell(accountEngineRule.getAERule());
		lc.setParent(item);
	  	lc = new Listcell(accountEngineRule.getAERuleDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAEIsSysDefault = new Checkbox();
		cbAEIsSysDefault.setDisabled(true);
		cbAEIsSysDefault.setChecked(accountEngineRule.isAEIsSysDefault());
		lc.appendChild(cbAEIsSysDefault);
		lc.setParent(item);
	  	lc = new Listcell(accountEngineRule.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(accountEngineRule.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", accountEngineRule);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountEngineRuleItemDoubleClicked");
	}
}