/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SecurityGroupListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 27-05-2011 * * Modified Date : 03-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.administration.securitygroup.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SecurityGroupListModelItemRenderer implements ListitemRenderer<SecurityGroup>, Serializable {

	private static final long serialVersionUID = -3424682216721299542L;

	public SecurityGroupListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SecurityGroup securityGroups, int count) {

		Listcell lc;
		lc = new Listcell(securityGroups.getGrpCode());
		lc.setParent(item);
		lc = new Listcell(securityGroups.getGrpDesc());
		lc.setParent(item);
		lc = new Listcell(securityGroups.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(securityGroups.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", securityGroups.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityGroupItemDoubleClicked");

	}
}