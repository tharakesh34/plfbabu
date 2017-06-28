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
 * FileName    		:  BuilderProjcetListModelItemRenderer.java                                                   * 	  
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
package com.pennant.webui.systemmasters.builderprojcet.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.util.PennantJavaUtil;


/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class BuilderProjcetListModelItemRenderer implements ListitemRenderer<BuilderProjcet>, Serializable {

	private static final long serialVersionUID = 1L;

	public BuilderProjcetListModelItemRenderer() {
		super();
	}

	
	@Override
	public void render(Listitem item, BuilderProjcet builderProjcet, int count) throws Exception {
        
		Listcell lc;
	    lc = new Listcell(String.valueOf(builderProjcet.getId()));
		lc.setParent(item);
	  	lc = new Listcell(builderProjcet.getName());
		lc.setParent(item);
	    lc = new Listcell(String.valueOf(builderProjcet.getBuilderId() + "-" + builderProjcet.getbuilderIdName()));
		lc.setParent(item);
	  	lc = new Listcell(builderProjcet.getApfNo());
		lc.setParent(item);
	  	lc = new Listcell(builderProjcet.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(builderProjcet.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", builderProjcet.getId());
		
		ComponentsCtrl.applyForward(item, "onDoubleClick=onBuilderProjcetItemDoubleClicked");
	}
}