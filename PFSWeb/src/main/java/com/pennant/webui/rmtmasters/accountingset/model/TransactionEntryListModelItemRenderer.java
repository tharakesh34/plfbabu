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
 * * FileName : TransactionEntryListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 14-12-2011 * * Modified Date : 14-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.accountingset.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TransactionEntryListModelItemRenderer implements ListitemRenderer<TransactionEntry>, Serializable {

	private static final long serialVersionUID = 6906998807263283546L;

	public TransactionEntryListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, TransactionEntry transactionEntry, int count) {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.formateInt(transactionEntry.getTransOrder()));
		lc.setParent(item);
		lc = new Listcell(transactionEntry.getTransDesc());
		lc.setParent(item);
		lc = new Listcell(transactionEntry.getLovDescAccountTypeName());
		lc.setParent(item);
		String amountRule = transactionEntry.getAmountRule();
		lc = new Listcell(
				StringUtils.substring(amountRule, amountRule.indexOf("=") + 1, amountRule.lastIndexOf(";")).trim());
		lc.setParent(item);
		lc = new Listcell();
		Checkbox checkbox = new Checkbox();
		checkbox.setDisabled(true);
		checkbox.setChecked(transactionEntry.isEntryByInvestment());
		lc.appendChild(checkbox);
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(transactionEntry.getDebitcredit(),
				PennantStaticListUtil.getTranType()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(transactionEntry.getAccount(),
				PennantStaticListUtil.getTransactionalAccount(ImplementationConstants.ALLOW_RIA)));
		lc.setParent(item);
		lc = new Listcell(
				StringUtils.trimToEmpty(transactionEntry.getPostToSys()).equals(AccountConstants.POSTTOSYS_CORE) ? "T24"
						: "ERP");
		lc.setParent(item);
		lc = new Listcell(transactionEntry.getTranscationCode());
		lc.setParent(item);
		lc = new Listcell(transactionEntry.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(transactionEntry.getRecordType()));
		lc.setParent(item);
		if (transactionEntry.getDerivedTranOrder() == 0) {
			item.setAttribute("data", transactionEntry);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onTransactionEntryItemDoubleClicked");
		}
	}
}