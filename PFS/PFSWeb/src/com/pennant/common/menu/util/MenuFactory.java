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
 * FileName    		:  MenuFActory.java														*                           
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

package com.pennant.common.menu.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

import com.pennant.UserWorkspace;
import com.pennant.common.menu.domain.IMenuDomain;
import com.pennant.common.menu.domain.MenuDomain;
import com.pennant.common.menu.domain.MetaMenuFactory;


/**
 * 
 */
abstract public class MenuFactory implements Serializable {

	private static final long serialVersionUID = 142621423557135573L;
	private final Log loger = LogFactory.getLog(getClass());

	final private LinkedList<Component> stack;
	final private UserWorkspace workspace;

	@SuppressWarnings("deprecation")
	protected MenuFactory(Component component) {
		super();
		this.workspace = UserWorkspace.getInstance();

		assert component != null : "Parent component is null!";
		assert this.workspace != null : "No UserWorkspace exists!";

		@SuppressWarnings("unused")
		long t1 = System.nanoTime();

		this.stack = new LinkedList<Component>();
		push(component);

		createMenu(MetaMenuFactory.getRootMenuDomain().getItems());

//		if (getLogger().isTraceEnabled()) {
//			t1 = System.nanoTime() - t1;
//			getLogger().trace("Needed time for inserting the menu: " + t1 / 1000000 + "ms");
//			// getLogger().trace("\n" + ZkossBaumUtil.getZulBaum(component));
//		}
	}

	private void createMenu(List<IMenuDomain> items) {
		if (items.isEmpty()) {
			return;
		}
		for (IMenuDomain menuDomain : items) {
			if (menuDomain instanceof MenuDomain) {
				MenuDomain menu = (MenuDomain) menuDomain;
				if (addSubMenuImpl(menu)) {
					createMenu(menu.getItems());
					ebeneHoch();
				}
			} else {
				addItemImpl(menuDomain);
				this.workspace.setHasMenuRights(menuDomain.getId(), StringUtils.trimToEmpty(menuDomain.getRightName()));
			}
		}
	}

	private void addItemImpl(IMenuDomain itemDomain) {
		if (isAllowed(itemDomain)) {
				setAttributes(itemDomain, createItemComponent(getCurrentComponent()));
				if(itemDomain.getId().equals("menu_Item_Home")){
					getCurrentComponent().getChildren().get(0).setVisible(false);
				}	
				if(itemDomain.getId().equals("menu_Item_ChgPwd")){
					getCurrentComponent().getChildren().get(1).setVisible(false);
				}
		}
	}

	abstract protected ILabelElement createItemComponent(Component parent);

	private boolean addSubMenuImpl(MenuDomain menu) {
		if (isAllowed(menu)) {
			MenuFactoryDto dto = createMenuComponent(getCurrentComponent());

			setAttributes(menu, dto.getNode());

			push(dto.getParent());

			return true;
		}
		return false;
	}

	abstract protected MenuFactoryDto createMenuComponent(Component parent);

	private boolean isAllowed(IMenuDomain treecellValue) {
		return isAllowed(treecellValue.getRightName());
	}

	public void ebeneHoch() {
		poll();
	}

	private Component getCurrentComponent() {
		return peek();
	}

	@SuppressWarnings("unused")
	private Log getLogger() {
		return this.loger;
	}

	private UserWorkspace getWorkspace() {
		return this.workspace;
	}

	private boolean isAllowed(String rightName) {
		if (StringUtils.isEmpty(rightName)) {
			return true;
		}
		return getWorkspace().isAllowed(rightName);
	}

	private Component peek() {
		return this.stack.peek();
	}

	private Component poll() {
		try {
			return this.stack.poll();
		} finally {
			if (this.stack.isEmpty()) {
				throw new RuntimeException("Root no longer exists!");
			}
		}
	}

	private void push(Component e) {
		this.stack.push(e);
	}

	protected void setAttributes(IMenuDomain treecellValue, ILabelElement defaultTreecell) {
		if (treecellValue.isWithOnClickAction() == null || treecellValue.isWithOnClickAction().booleanValue()) {
			defaultTreecell.setZulNavigation(treecellValue.getZulNavigation());

			if (!StringUtils.isEmpty(treecellValue.getIconName())) {
				defaultTreecell.setImage(treecellValue.getIconName());
			}
		}

		setAttributesWithoutAction(treecellValue, defaultTreecell);
	}

	private void setAttributesWithoutAction(IMenuDomain treecellValue, ILabelElement defaultTreecell) {
		assert treecellValue.getId() != null : "In mainmenu.xml file is a node who's ID is missing!";

		defaultTreecell.setId(treecellValue.getId());
		String label = treecellValue.getLabel();
		if (StringUtils.isEmpty(label)) {
			label = Labels.getLabel(treecellValue.getId());
		} else {
			label = Labels.getLabel(label);
		}
		defaultTreecell.setLabel(" " + label);
	}
}
