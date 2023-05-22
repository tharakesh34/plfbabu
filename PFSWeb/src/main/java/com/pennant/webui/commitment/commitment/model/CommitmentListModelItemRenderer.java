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
 * * FileName : CommitmentListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 *
 * * Modified Date : 25-03-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.commitment.commitment.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CommitmentListModelItemRenderer implements ListitemRenderer<Commitment>, Serializable {

	private static final long serialVersionUID = 5487388031427098891L;

	public CommitmentListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Commitment commitment, int count) {
		Listcell lc;

		int formatter = CurrencyUtil.getFormat(commitment.getCmtCcy());

		lc = new Listcell(StringUtils.trimToEmpty(commitment.getCustCIF()));
		lc.setParent(item);
		lc = new Listcell(commitment.getCmtReference());
		lc.setParent(item);
		lc = new Listcell(commitment.getCmtBranch());
		lc.setParent(item);
		lc = new Listcell(commitment.getCmtCcy());
		lc.setParent(item);
		lc = new Listcell(commitment.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(commitment.getCmtExpDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(commitment.getCmtRvwDate()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAmount(), formatter));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtUtilizedAmount(), formatter));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAvailable(), formatter));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(commitment.getCmtStartDate()));
		lc.setParent(item);
		lc = new Listcell(commitment.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(commitment.getRecordType()));
		lc.setParent(item);

		item.setAttribute("commitment", commitment);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCommitmentItemDoubleClicked");
	}
}