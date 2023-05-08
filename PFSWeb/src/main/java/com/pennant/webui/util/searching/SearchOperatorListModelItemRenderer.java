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
 *
 * FileName : SearchOperationListModelItemRender.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.util.searching;

import java.io.Serializable;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SearchOperatorListModelItemRenderer implements ListitemRenderer<SearchOperators>, Serializable {

	private static final long serialVersionUID = 1L;

	public SearchOperatorListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, SearchOperators searchOp, int count) {

		final Listcell lc = new Listcell(searchOp.getSearchOperatorSign());
		lc.setParent(item);
		item.setAttribute("data", searchOp);

		// Default Selecting of EQUAL Parameter List item on Selection
		if (count == 0) {
			if (item.getParent() instanceof Listbox) {
				if (((Listbox) item.getParent()).getSelectedItem() == null) {
					((Listbox) item.getParent()).setSelectedItem(item);
				}
			}
		}
	}

}
