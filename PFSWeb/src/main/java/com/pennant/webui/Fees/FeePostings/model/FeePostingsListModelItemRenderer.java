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

package com.pennant.webui.Fees.FeePostings.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.accounting.AccountingUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FeePostingsListModelItemRenderer implements ListitemRenderer<FeePostings>, Serializable {

	private static final long serialVersionUID = 1L;

	public FeePostingsListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FeePostings feePostings, int count) {
		Listcell lc;
		lc = new Listcell(PennantStaticListUtil
				.getlabelDesc(StringUtils.equals(null, feePostings.getPostAgainst()) ? feePostings.getPostAgainst()
						: feePostings.getPostAgainst().trim(), AccountingUtil.getpostingPurposeList()));
		lc.setParent(item);
		lc = new Listcell(feePostings.getReference());
		lc.setParent(item);
		lc = new Listcell(feePostings.getFeeTyeCode());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(feePostings.getPostingAmount(),
				CurrencyUtil.getFormat(feePostings.getCurrency())));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(feePostings.getPostDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(feePostings.getValueDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(feePostings.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(feePostings.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", feePostings.getPostId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFeePostingItemDoubleClicked");
	}
}