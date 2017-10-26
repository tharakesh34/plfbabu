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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

import com.pennant.UserWorkspace;
import com.pennant.common.menu.domain.IMenuDomain;
import com.pennant.common.menu.domain.MenuDomain;
import com.pennant.common.menu.domain.MenuItemDomain;
import com.pennant.common.menu.domain.MetaMenuFactory;
import com.pennanttech.pff.core.App.AuthenticationType;

abstract public class MenuFactory implements Serializable {
	private static final long serialVersionUID = 142621423557135573L;

	final private LinkedList<Component> stack;
	private UserWorkspace workspace;
	private String authType = null;

	protected MenuFactory(Component component) {
		super();
		getUserWorkspace();

		assert component != null : "Parent component is null!";
		assert this.workspace != null : "No UserWorkspace exists!";

		this.stack = new LinkedList<Component>();
		push(component);

		List<IMenuDomain> mainMenu = getAuthorizedMenu(MetaMenuFactory.getRootMenuDomain().getItems());

		createMenu(mainMenu, 1);
	}

	private List<IMenuDomain> getAuthorizedMenu(List<IMenuDomain> mainMenu) {
		List<IMenuDomain> result = new ArrayList<>();

		for (IMenuDomain menu : mainMenu) {
			if (menu instanceof MenuDomain) {
				MenuDomain menuGroup = (MenuDomain) menu;

				List<IMenuDomain> itemDomain = getAuthorizedMenu(menuGroup.getItems());

				if (!itemDomain.isEmpty()) {
					result.add(menuGroup.getCopy(itemDomain));
				}
			} else {
				MenuItemDomain menuItem = (MenuItemDomain) menu;

				if (isAllowed(menuItem)) {
					result.add(menuItem);
				}
			}
		}

		return result;
	}

	private void createMenu(List<IMenuDomain> items, int level) {
		if (items.isEmpty()) {
			return;
		}
		for (IMenuDomain menuDomain : items) {
			if (menuDomain instanceof MenuDomain) {
				MenuDomain menu = (MenuDomain) menuDomain;
				addSubMenuImpl(menu, level);
				createMenu(menu.getItems(), level + 1);
				ebeneHoch();
			} else {
				addItemImpl(menuDomain);
				this.workspace.setHasMenuRights(menuDomain.getId(), StringUtils.trimToEmpty(menuDomain.getRightName()));
			}
		}
	}
     
	private void addItemImpl(IMenuDomain itemDomain) {
		if (isAllowed(itemDomain)) {
			setAttributes(itemDomain, createItemComponent(getCurrentComponent()));
			
			if("menu_Item_Home".equals(itemDomain.getId())){
				getCurrentComponent().getChildren().get(0).setVisible(false);
			}	
			if("menu_Item_ChgPwd".equals(itemDomain.getId())){
				getCurrentComponent().getChildren().get(1).setVisible(false);
			}
		}
	}

	abstract protected ILabelElement createItemComponent(Component parent);

	private void addSubMenuImpl(MenuDomain menu, int level) {
		MenuFactoryDto dto = createMenuComponent(getCurrentComponent(), level);

		setAttributes(menu, dto.getNode());

		push(dto.getParent());
	}

	abstract protected MenuFactoryDto createMenuComponent(Component parent, int level);

	private boolean isAllowed(IMenuDomain treecellValue) {
		
		if (StringUtils.isNotEmpty(authType)) {
			if (!StringUtils.equals(AuthenticationType.DAO.name(), authType)) {
				if ("menu_Item_ChgPwd".equals(treecellValue.getId()) || "menu_Item_PasswordResetUser".equals(treecellValue.getId())) {
					return false;
				}
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
	
	private UserWorkspace getUserWorkspace() {
		if (workspace == null) {
			workspace = (UserWorkspace) SpringUtil.getBean("userWorkspace");
		}
		authType = StringUtils.trimToEmpty(workspace.getLoggedInUser().getAuthType());

		return workspace;
	}
}
