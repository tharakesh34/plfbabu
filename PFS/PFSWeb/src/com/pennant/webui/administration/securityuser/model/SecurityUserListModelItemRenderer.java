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
 * FileName    		:  SecurityUserListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  3-8-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 3-8-2011      Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityuser.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class SecurityUserListModelItemRenderer implements ListitemRenderer<SecurityUser>, Serializable {

	private static final long serialVersionUID = 7204475649828613670L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, SecurityUser securityUser, int count) throws Exception {
		
		//final SecurityUser securityUser = (SecurityUser) data;
		Listcell lc;
		lc = new Listcell(securityUser.getUsrLogin());
		lc.setParent(item);
		lc = new Listcell(securityUser.getUsrFName());
		lc.setParent(item);
		lc = new Listcell(securityUser.getUsrMName());
		lc.setParent(item);
		lc = new Listcell(securityUser.getUsrLName());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUsrCanOverrideLimits = new Checkbox();
		cbUsrCanOverrideLimits.setDisabled(true);
		cbUsrCanOverrideLimits.setChecked(securityUser.isUsrCanOverrideLimits());
		lc.appendChild(cbUsrCanOverrideLimits);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUsrAcExp = new Checkbox();
		cbUsrAcExp.setDisabled(true);
		cbUsrAcExp.setChecked(securityUser.isUsrAcExp());
		lc.appendChild(cbUsrAcExp);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUsrCredentialsExp = new Checkbox();
		cbUsrCredentialsExp.setDisabled(true);
		cbUsrCredentialsExp.setChecked(securityUser.isUsrCredentialsExp());
		lc.appendChild(cbUsrCredentialsExp);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbUsrAcLocked = new Checkbox();
		cbUsrAcLocked.setDisabled(true);
		cbUsrAcLocked.setChecked(securityUser.isUsrAcLocked());
		lc.appendChild(cbUsrAcLocked);
		lc.setParent(item);
		lc = new Listcell(securityUser.getUsrDftAppCode()+"-"+securityUser.getLovDescUsrDftAppCodeName());
		lc.setParent(item);
		lc = new Listcell(securityUser.getUsrBranchCode()+"-"+securityUser.getLovDescUsrBranchCodeName());
		lc.setParent(item);
		lc = new Listcell(securityUser.getUsrDeptCode()+"-"+securityUser.getLovDescUsrDeptCodeName());
		lc.setParent(item);
		lc = new Listcell(securityUser.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(securityUser.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", securityUser);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityUserItemDoubleClicked");
	
	}
}