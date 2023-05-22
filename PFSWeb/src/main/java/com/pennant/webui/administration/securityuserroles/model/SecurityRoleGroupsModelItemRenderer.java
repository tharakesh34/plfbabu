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
 * FileName : SecurityRoleGroupsModelItemRenderer.java *
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

import com.pennant.backend.model.administration.SecurityGroup;

public class SecurityRoleGroupsModelItemRenderer implements ListitemRenderer<SecurityGroup>, Serializable {

	private static final long serialVersionUID = 2251512646510457618L;

	public SecurityRoleGroupsModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SecurityGroup securityGroup, int count) {

		Listcell lc = new Listcell(securityGroup.getGrpCode());
		lc.setParent(item);
		lc = new Listcell(securityGroup.getGrpDesc());
		lc.setParent(item);

		item.setAttribute("id", securityGroup.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityGroupItemDoubleClicked");
	}
}
