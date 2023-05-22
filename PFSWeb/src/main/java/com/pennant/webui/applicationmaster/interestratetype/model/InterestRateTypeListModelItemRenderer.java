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
 * * FileName : InterestRateTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 06-05-2011 * * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.interestratetype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class InterestRateTypeListModelItemRenderer implements ListitemRenderer<InterestRateType>, Serializable {

	private static final long serialVersionUID = -2562119248315296673L;

	public InterestRateTypeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, InterestRateType interestRateType, int count) {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(interestRateType.getIntRateTypeCode(),
				PennantStaticListUtil.getInterestRateType(false)));
		lc.setParent(item);
		lc = new Listcell(interestRateType.getIntRateTypeDesc());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIntRateTypeIsActive = new Checkbox();
		cbIntRateTypeIsActive.setDisabled(true);
		cbIntRateTypeIsActive.setChecked(interestRateType.isIntRateTypeIsActive());
		lc.appendChild(cbIntRateTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(interestRateType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(interestRateType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", interestRateType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInterestRateTypeItemDoubleClicked");
	}
}