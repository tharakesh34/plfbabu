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
 * * FileName : FacilityReferenceDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 26-11-2011 * * Modified Date : 26-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.lmtmasters.financereferencedetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.CAFFacilityType;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FacilityReferenceDetailListModelItemRenderer implements ListitemRenderer<CAFFacilityType>, Serializable {

	private static final long serialVersionUID = 1L;

	public FacilityReferenceDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CAFFacilityType facilityType, int count) {

		Listcell lc;
		lc = new Listcell(facilityType.getFacilityType().toUpperCase());
		lc.setParent(item);
		lc = new Listcell(facilityType.getFacilityDesc());
		lc.setParent(item);
		item.setAttribute("data", facilityType);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFacilityReferenceDetailItemDoubleClicked");
	}
}