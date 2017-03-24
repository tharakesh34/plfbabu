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
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkmax.zul.Nav;

import com.pennant.UserWorkspace;
import com.pennant.common.menu.domain.IMenuDomain;
import com.pennant.common.menu.domain.MenuDomain;
import com.pennant.common.menu.domain.MetaMenuFactory;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.AuthenticationType;

abstract public class NavbarFactory implements Serializable {
	private static final long serialVersionUID = 142621423557135573L;

	final private LinkedList<Component> stack;
	final private UserWorkspace workspace;

	protected NavbarFactory(Component component) {
		super();
		this.workspace = (UserWorkspace) SpringUtil.getBean("userWorkspace");

		assert component != null : "Parent component is null!";
		assert this.workspace != null : "No UserWorkspace exists!";

		this.stack = new LinkedList<Component>();
		push(component);

		createMenu(MetaMenuFactory.getRootMenuDomain().getItems());

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
		}
	}

	abstract protected ILabelElement createItemComponent(Component parent);

	private boolean addSubMenuImpl(MenuDomain menu) {
		if (isAllowed(menu)) {
			MenuFactoryDto dto = createMenuComponent(getCurrentComponent());

			setAttributes(menu, (Nav) dto.getParent());

			push(dto.getParent());

			return true;
		}
		return false;
	}

	abstract protected MenuFactoryDto createMenuComponent(Component parent);

	private boolean isAllowed(IMenuDomain treecellValue) {

		if (!App.AUTH_TYPE.equals(AuthenticationType.DAO)) {
			if ("menu_Item_ChgPwd".equals(treecellValue.getId())
					|| "menu_Item_PasswordResetUser".equals(treecellValue.getId())) {
				return false;
			}
		}

		return isAllowed(treecellValue.getRightName());
	}

	public void ebeneHoch() {
		poll();
	}

	private Component getCurrentComponent() {
		return peek();
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

	private void poll() {
		this.stack.poll();
	}

	private void push(Component e) {
		this.stack.push(e);
	}

	protected void setAttributes(IMenuDomain navItemValue, ILabelElement defaultNavItem) {
		if (navItemValue.isWithOnClickAction() == null || navItemValue.isWithOnClickAction().booleanValue()) {
			defaultNavItem.setZulNavigation(navItemValue.getZulNavigation());

			if (!StringUtils.isEmpty(navItemValue.getIconName())) {
				// defaultNavItem.setIconName("z-icon-angle-double-right");
			}

		}

		setAttributesWithoutAction(navItemValue, defaultNavItem);
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

	private void setAttributes(MenuDomain menu, Nav nav) {
		String label = menu.getLabel();

		if (StringUtils.isEmpty(label)) {
			label = Labels.getLabel(menu.getId());
		} else {
			label = Labels.getLabel(label);
		}

		nav.setIconSclass("z-icon-angle-double-right");
		nav.setLabel(label);
	}

}
