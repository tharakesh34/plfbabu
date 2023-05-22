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
 * * FileName : AuthorizationListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 20-08-2013 * * Modified Date : 20-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.amtmasters.authorization.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AuthorizationListModelItemRenderer implements ListitemRenderer<Authorization>, Serializable {

	private static final long serialVersionUID = 1L;

	public AuthorizationListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Authorization authorization, int count) {

		Listcell lc;
		lc = new Listcell(
				PennantStaticListUtil.getlabelDesc(authorization.getAuthType(), PennantStaticListUtil.getAuthTypes()));
		lc.setParent(item);
		lc = new Listcell(authorization.getAuthName());
		lc.setParent(item);
		lc = new Listcell(authorization.getAuthDept());
		lc.setParent(item);
		lc = new Listcell(authorization.getAuthDesig());
		lc.setParent(item);
		lc = new Listcell(authorization.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(authorization.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", authorization.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAuthorizationItemDoubleClicked");
	}
}