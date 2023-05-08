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
 * * FileName : DeviationParamListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 22-06-2015 * * Modified Date : 22-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.solutionfactory.deviationparam.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DeviationParamListModelItemRenderer implements ListitemRenderer<DeviationParam>, Serializable {

	private static final long serialVersionUID = 1L;

	public DeviationParamListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DeviationParam deviationParam, int count) {

		Listcell lc;
		lc = new Listcell(deviationParam.getCode());
		lc.setParent(item);
		lc = new Listcell(deviationParam.getDescription());
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(deviationParam.getDataType(),
				PennantStaticListUtil.getDeviationDataTypes()));
		lc.setParent(item);
		lc = new Listcell(deviationParam.getType());
		lc.setParent(item);

		lc = new Listcell(deviationParam.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(deviationParam.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", deviationParam.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDeviationParamItemDoubleClicked");
	}
}