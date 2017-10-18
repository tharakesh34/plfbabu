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
 *
 * FileName    		:  TreeMenuFactory.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

package com.pennant.common.menu.tree;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.pennant.common.menu.util.ILabelElement;
import com.pennant.common.menu.util.MenuFactory;
import com.pennant.common.menu.util.MenuFactoryDto;

public class TreeMenuFactory extends MenuFactory {

	private static final long serialVersionUID = -1601202637698812546L;

	public static void addMainMenu(Component component) {
		new TreeMenuFactory(component);
	}

	private TreeMenuFactory(Component component) {
		super(component);
	}

	@Override
	protected MenuFactoryDto createMenuComponent(Component parent,int level) {
		Treeitem treeitem = new Treeitem();
		parent.appendChild(treeitem);

		ILabelElement item = insertTreeCell(treeitem);
		Treecell treecell = (Treecell) treeitem.getTreerow().getChildren().get(0);

		switch (level) {
		case 1:
			treecell.setSclass("menu-group-1");
			break;
		case 2:
			treecell.setSclass("menu-group-2");
			break;
		default:
			treecell.setSclass("menu-group-3");
		}

		ComponentsCtrl.applyForward(treeitem, "onClick=onMenuItemSelected");
		Treechildren treechildren = new Treechildren();
		treeitem.appendChild(treechildren);

		return new MenuFactoryDto(treechildren, item);
	}

	@Override
	protected ILabelElement createItemComponent(Component parent) {
		Treeitem treeitem = new Treeitem();
		parent.appendChild(treeitem);

		ILabelElement item = insertTreeCell(treeitem);
		Treecell treecell = (Treecell) treeitem.getTreerow().getChildren().get(0);
		treecell.setSclass("menu-item");

		return item;
	}

	private ILabelElement insertTreeCell(Component parent) {
		Treerow treerow = new Treerow();
		parent.appendChild(treerow);

		DefaultTreecell treecell = new DefaultTreecell();
		treerow.appendChild(treecell);
 		return treecell;
	}
}
