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
 *
 * FileName : SecurityGroupRightModelItemRenderer.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-06-2011 *
 * 
 * Modified Date : 1-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 1-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.administration.securityuserroles.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityRight;

public class SecurityGroupRightModelItemRenderer implements ListitemRenderer<SecurityRight>, Serializable {

	private static final long serialVersionUID = 8842120255261997095L;
	SecurityRight secRights;

	public SecurityGroupRightModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SecurityRight secRights, int count) {

		Listcell lc = new Listcell(secRights.getRightName());
		lc.setParent(item);
		item.setAttribute("data", secRights);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityGroupItemDoubleClicked");
	}
}
