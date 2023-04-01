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
 * * FileName : FinanceMainListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011
 * * * Modified Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class FinanceMainListModelItemRenderer implements ListitemRenderer<FinanceMain>, Serializable {

	private static final long serialVersionUID = -4562142056572229437L;

	public FinanceMainListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FinanceMain financeMain, int count) {

		Listcell lc;
		lc = new Listcell();

		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		String custCIF = financeMain.getLovDescCustCIF();
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE
				&& StringUtils.isBlank(financeMain.getLovDescCustCIF())) {
			custCIF = "In Process";
			lc.setStyle("font-weight:bold;color:green;");
		}
		lc.setLabel(custCIF);
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescCustShrtName());
		lc.setParent(item);
		lc = new Listcell(financeMain.getFinReference());
		lc.setParent(item);

		if (StringUtils.isBlank(financeMain.getLovDescFinProduct())) {
			lc = new Listcell(financeMain.getLovDescFinTypeName());
			lc.setParent(item);
			lc = new Listcell("");
			lc.setParent(item);
		} else {
			lc = new Listcell(financeMain.getLovDescFinProduct());
			lc.setParent(item);
			lc = new Listcell(financeMain.getLovDescFinTypeName());
			lc.setParent(item);
		}

		lc = new Listcell(financeMain.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(
				String.valueOf(financeMain.getGraceTerms() + financeMain.getCalTerms() + financeMain.getAdvTerms()));
		lc.setParent(item);
		lc.setStyle("text-align:right;");
		lc = new Listcell(CurrencyUtil.format(financeMain.getFinAmount(), format));
		lc.setParent(item);
		lc.setStyle("text-align:right;");
		BigDecimal finAmount = financeMain.getFinCurrAssetValue().add(financeMain.getFeeChargeAmt());
		if (financeMain.getFinAssetValue().compareTo(BigDecimal.ZERO) == 0) {
			lc = new Listcell(CurrencyUtil.format(finAmount, format));
			lc.setParent(item);
		} else {
			lc = new Listcell(CurrencyUtil.format(financeMain.getFinAssetValue(), format));
			lc.setParent(item);
		}
		lc.setStyle("text-align:right;");
		lc = new Listcell(DateUtil.format(financeMain.getInitiateDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(financeMain.getLovDescRequestStage());
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.getLabelDesc(String.valueOf(financeMain.getPriority()),
				PennantStaticListUtil.getQueuePriority()));
		switch (financeMain.getPriority()) {
		case 0:
			lc.setStyle("font-weight:bold;color:#00F566;");
			break;
		case 1:
			lc.setStyle("font-weight:bold;color:#00ABF5;");
			break;
		case 2:
			lc.setStyle("font-weight:bold;color:#E37114;");
			break;
		case 3:
			lc.setStyle("font-weight:bold;color:#F20C0C;");
			break;
		}
		lc.setParent(item);

		lc = new Listcell(financeMain.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(financeMain.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", financeMain);

		lc = new Listcell(financeMain.getApplicationNo());
		lc.setParent(item);
		lc = new Listcell(financeMain.getOfferId());
		lc.setParent(item);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceMainItemDoubleClicked");
	}
}