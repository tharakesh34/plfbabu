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
 * FileName    		:  MenuItemDomain.java													*                           
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
package com.pennant.common.menu.domain;

import javax.xml.bind.annotation.XmlAttribute;

public class MenuItemDomain implements IMenuDomain {
	private String id;

	private String zulNavigation;
	private String label;

	private String rightName = null;

	private Boolean withOnClickAction = null;

	private String iconName;

	public MenuItemDomain() {
		super();
	}
	
	@XmlAttribute
	public String getIconName() {
		return this.iconName;
	}

	@XmlAttribute(required = true)
	public String getId() {
		return this.id;
	}

	@XmlAttribute
	public String getLabel() {
		return this.label;
	}

	@XmlAttribute
	public String getRightName() {
		return this.rightName;
	}

	@XmlAttribute
	public String getZulNavigation() {
		return this.zulNavigation;
	}

	@XmlAttribute
	public Boolean isWithOnClickAction() {
		return this.withOnClickAction;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setRightName(String rightName) {
		this.rightName = rightName;
	}

	public void setWithOnClickAction(Boolean withOnClickAction) {
		this.withOnClickAction = withOnClickAction;
	}

	public void setZulNavigation(String zulNavigation) {
		this.zulNavigation = zulNavigation;
	}
}
