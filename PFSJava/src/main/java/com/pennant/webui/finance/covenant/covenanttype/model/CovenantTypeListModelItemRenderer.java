/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CovenantTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-02-2019    														*
 *                                                                  						*
 * Modified Date    :  06-02-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-02-2019       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.webui.finance.covenant.covenanttype.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CovenantTypeListModelItemRenderer implements ListitemRenderer<CovenantType>, Serializable {
	private static final long serialVersionUID = 1L;

	private transient List<Property> listCategory = PennantStaticListUtil.getCovenantCategories();

	public CovenantTypeListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, CovenantType covenantType, int count) {

		Listcell lc;
		lc = new Listcell(covenantType.getCode());
		lc.setParent(item);
		lc = new Listcell(covenantType.getDescription());
		lc.setParent(item);
		lc = new Listcell(getCategory(covenantType.getCategory()));
		lc.setParent(item);

		if (StringUtils.isNotEmpty(covenantType.getDocType())) {
			lc = new Listcell(covenantType.getDocType() + " - " + covenantType.getDocTypeName());
		} else {
			lc = new Listcell();
		}
		lc.setParent(item);
		lc = new Listcell(covenantType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(covenantType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", covenantType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCovenantTypeItemDoubleClicked");
	}

	private String getCategory(String category) {
		for (Property property : listCategory) {
			if (StringUtils.equals(property.getKey().toString(), category)) {
				return category + " - " + property.getValue();
			}
		}

		return "";
	}
}