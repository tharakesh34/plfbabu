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
 * * FileName : CollateralSetupListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 13-12-2016 * * Modified Date : 13-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.collateral.collateralsetup.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CollateralSetupListModelItemRenderer implements ListitemRenderer<CollateralSetup>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, CollateralSetup collateralSetup, int count) {

		Listcell lc;

		lc = new Listcell(collateralSetup.getDepositorCif());
		lc.setParent(item);
		lc = new Listcell(collateralSetup.getCollateralRef());
		lc.setParent(item);
		lc = new Listcell(collateralSetup.getCollateralCcy());
		lc.setParent(item);
		lc = new Listcell(collateralSetup.getCollateralType());
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(collateralSetup.getExpiryDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(collateralSetup.getNextReviewDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(collateralSetup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(collateralSetup.getRecordType()));
		lc.setParent(item);
		item.setAttribute("collateralSetup", collateralSetup);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralSetupItemDoubleClicked");
	}
}