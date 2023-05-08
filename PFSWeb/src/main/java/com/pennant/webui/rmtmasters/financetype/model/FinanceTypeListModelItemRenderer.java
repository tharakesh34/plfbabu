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
 * * FileName : FinanceTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011
 * * * Modified Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.rmtmasters.financetype.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class FinanceTypeListModelItemRenderer implements ListitemRenderer<FinanceType>, Serializable {

	private static final long serialVersionUID = 2118469590661434900L;

	boolean isOverdraft = false;

	public FinanceTypeListModelItemRenderer() {
	    super();
	}

	public FinanceTypeListModelItemRenderer(boolean isOverdraft) {
		this.isOverdraft = isOverdraft;
	}

	@Override
	public void render(Listitem item, FinanceType financeType, int count) {

		if (item instanceof Listgroup) {
			Listcell cell = new Listcell(financeType.getFinCategoryDesc());
			cell.setStyle("font-weight:bold;color:##FF4500;");
			item.appendChild(cell);
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			if (!isOverdraft) {
				cell.setSpan(10);
			} else {
				cell.setSpan(9);
			}
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(financeType.getFinType());
			lc.setParent(item);
			lc = new Listcell(financeType.getFinTypeDesc());
			lc.setParent(item);
			lc = new Listcell(financeType.getFinCcy());
			lc.setParent(item);
			lc = new Listcell(StringUtils.equals(financeType.getFinDaysCalType(), PennantConstants.List_Select) ? ""
					: financeType.getFinDaysCalType());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.getLabelDesc(financeType.getFinSchdMthd(),
					PennantStaticListUtil.getScheduleMethods()));
			lc.setParent(item);

			if (!isOverdraft) {
				lc = new Listcell();
				Checkbox checkbox = new Checkbox();
				checkbox.setChecked(financeType.isFInIsAlwGrace());
				checkbox.setDisabled(true);
				checkbox.setParent(lc);
				lc.setParent(item);
			}

			lc = new Listcell(financeType.getFinDivision());
			lc.setParent(item);

			lc = new Listcell(financeType.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(financeType.getRecordType()));
			lc.setParent(item);

			item.setAttribute("finType", financeType.getFinType());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceTypeItemDoubleClicked");
		}
	}
}