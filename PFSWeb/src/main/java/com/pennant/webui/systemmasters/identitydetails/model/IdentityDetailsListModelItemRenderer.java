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
 * * FileName : IdentityDetailsListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 05-05-2011 * * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.identitydetails.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class IdentityDetailsListModelItemRenderer implements ListitemRenderer<IdentityDetails>, Serializable {

	private static final long serialVersionUID = -3653224021041165546L;

	public IdentityDetailsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, IdentityDetails identityDetails, int count) {

		Listcell lc;
		lc = new Listcell(identityDetails.getIdentityType());
		lc.setParent(item);
		lc = new Listcell(identityDetails.getIdentityDesc());
		lc.setParent(item);
		lc = new Listcell(identityDetails.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(identityDetails.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", identityDetails.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onIdentityDetailsItemDoubleClicked");
	}
}