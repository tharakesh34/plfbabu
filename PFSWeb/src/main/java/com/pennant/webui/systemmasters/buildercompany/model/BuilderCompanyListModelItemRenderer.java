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
 * FileName    		:  BuilderCompanyListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-05-2017    														*
 *                                                                  						*
 * Modified Date    :  22-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.buildercompany.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BuilderCompanyListModelItemRenderer implements ListitemRenderer<BuilderCompany>, Serializable {

	private static final long serialVersionUID = 1L;

	public BuilderCompanyListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, BuilderCompany builderCompany, int count) throws Exception {

		Listcell lc;
	  	lc = new Listcell(builderCompany.getName());
		lc.setParent(item);
	  	lc = new Listcell(builderCompany.getSegmentation());
		lc.setParent(item);
	    lc = new Listcell(String.valueOf(builderCompany.getGroupId() + "-" + builderCompany.getGroupIdName()));
		lc.setParent(item);
	  	lc = new Listcell(builderCompany.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(builderCompany.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", builderCompany.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBuilderCompanyItemDoubleClicked");
	}
}