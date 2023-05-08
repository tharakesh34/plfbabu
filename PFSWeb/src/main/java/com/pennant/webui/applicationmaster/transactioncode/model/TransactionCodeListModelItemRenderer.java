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
 * * FileName : TransactionCodeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 10-11-2011 * * Modified Date : 10-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.applicationmaster.transactioncode.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TransactionCodeListModelItemRenderer implements ListitemRenderer<TransactionCode>, Serializable {

	private static final long serialVersionUID = 2146337993294728738L;

	public TransactionCodeListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, TransactionCode transactionCode, int count) {

		Listcell lc;
		lc = new Listcell(transactionCode.getTranCode());
		lc.setParent(item);
		lc = new Listcell(transactionCode.getTranDesc());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(transactionCode.getTranType(),
				PennantStaticListUtil.getTranTypeBoth()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbTranIsActive = new Checkbox();
		cbTranIsActive.setDisabled(true);
		cbTranIsActive.setChecked(transactionCode.isTranIsActive());
		lc.appendChild(cbTranIsActive);
		lc.setParent(item);
		lc = new Listcell(transactionCode.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(transactionCode.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", transactionCode.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTransactionCodeItemDoubleClicked");
	}
}