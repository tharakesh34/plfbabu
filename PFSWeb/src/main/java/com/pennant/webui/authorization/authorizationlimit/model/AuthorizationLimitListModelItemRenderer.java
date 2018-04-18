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
 * FileName    		:  AuthorizationLimitListModelItemRenderer.java                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-04-2018    														*
 *                                                                  						*
 * Modified Date    :  06-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-04-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.authorization.authorizationlimit.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.authorization.AuthorizationLimit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AuthorizationLimitListModelItemRenderer implements ListitemRenderer<AuthorizationLimit>, Serializable {

	private static final long serialVersionUID = 1L;

	public AuthorizationLimitListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, AuthorizationLimit authorizationLimit, int count) throws Exception {

		
		Listcell lc;
		lc = new Listcell(authorizationLimit.getUsrLogin());
		lc.setParent(item);
		lc = new Listcell(authorizationLimit.getRoleCd());
		lc.setParent(item);

		
		if(authorizationLimit.getLimitType()==1){
			lc = new Listcell(PennantApplicationUtil.getFullName(authorizationLimit.getUsrFName(),authorizationLimit.getUsrMName(),authorizationLimit.getUsrLName()));
			lc.setParent(item);
		}else{
			lc = new Listcell(authorizationLimit.getRoleName());
			lc.setParent(item);
		}

		lc = new Listcell(PennantApplicationUtil.formatAmount(authorizationLimit.getLimitAmount(), CurrencyUtil.getFormat(""), false));
		lc.setParent(item);
		lc.setStyle("text-align:Right;");
		
		lc = new Listcell(DateUtil.format(authorizationLimit.getExpiryDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		
		lc = new Listcell(DateUtil.format(authorizationLimit.getHoldStartDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		
		lc = new Listcell(DateUtil.format(authorizationLimit.getHoldExpiryDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		
		lc = new Listcell();
		final Checkbox cbActive = new Checkbox();
		cbActive.setDisabled(true);
		cbActive.setChecked(authorizationLimit.isActive());
		lc.appendChild(cbActive);
		lc.setParent(item);
	  	lc = new Listcell(authorizationLimit.getRecordStatus());
		lc.setParent(item);
		
		lc = new Listcell(PennantJavaUtil.getLabel(authorizationLimit.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", authorizationLimit.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onAuthorizationLimitItemDoubleClicked");
	}
}