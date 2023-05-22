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
 * * FileName : JVPostingListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 *
 * * Modified Date : 21-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.others.jvposting.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class JVPostingListModelItemRenderer implements ListitemRenderer<JVPosting>, Serializable {

	private static final long serialVersionUID = 1L;

	public JVPostingListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, JVPosting jVPosting, int count) {
		Listcell lc;
		lc = new Listcell(String.valueOf(jVPosting.getBatchReference()));
		lc.setParent(item);
		lc = new Listcell(jVPosting.getReference());
		lc.setParent(item);
		lc = new Listcell(jVPosting.getBatch());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(jVPosting.getDebitCount()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(jVPosting.getCreditsCount()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(jVPosting.getTotDebitsByBatchCcy(),
				CurrencyUtil.getFormat(jVPosting.getCurrency())));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(jVPosting.getTotCreditsByBatchCcy(),
				CurrencyUtil.getFormat(jVPosting.getCurrency())));
		lc.setParent(item);
		lc = new Listcell(jVPosting.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(jVPosting.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", jVPosting);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onJVPostingItemDoubleClicked");
	}
}