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
 * FileName    		:  SecurityRoleListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  2-8-2011   														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 2-8-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.administration.securityrole.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SecurityRoleListModelItemRenderer implements ListitemRenderer<SecurityRole>, Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SecurityRoleListModelItemRenderer.class);
	
	public SecurityRoleListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, SecurityRole securityRole, int count) throws Exception {
       logger.debug("Entering ");
		Listcell lc;
	  	lc = new Listcell(securityRole.getLovDescRoleAppName());
		lc.setParent(item);
	  	lc = new Listcell(securityRole.getRoleCd());
		lc.setParent(item);
	  	lc = new Listcell(securityRole.getRoleDesc());
		lc.setParent(item);
	  	lc = new Listcell(securityRole.getRoleCategory());
		lc.setParent(item);
	  	lc = new Listcell(securityRole.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(securityRole.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("id", securityRole.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityRoleItemDoubleClicked");
		logger.debug("Leaving ");
	}
}