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
 * * FileName : PFSParameterListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-07-2011
 * * * Modified Date : 12-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.smtmasters.pfsparameter.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class PFSParameterListModelItemRenderer implements ListitemRenderer<PFSParameter>, Serializable {

	private static final long serialVersionUID = -8769182690540455637L;

	public PFSParameterListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, PFSParameter pFSParameter, int count) {

		Listcell lc;
		lc = new Listcell(pFSParameter.getSysParmCode());
		lc.setParent(item);
		lc = new Listcell(pFSParameter.getSysParmDesc());
		lc.setParent(item);
		lc = new Listcell(pFSParameter.getSysParmValue());
		lc.setParent(item);
		lc = new Listcell(pFSParameter.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(pFSParameter.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", pFSParameter.getSysParmCode());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPFSParameterItemDoubleClicked");
	}
}