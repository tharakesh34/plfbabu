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
 * FileName    		:  MenuFactoryDto.java													*                           
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

import org.zkoss.zk.ui.Component;

public class MenuFactoryDto {

	public MenuFactoryDto(Component parent, ILabelElement node) {
		super();
		this.parent = parent;
		this.node = node;
	}

	public MenuFactoryDto(ILabelElement node) {
		this(node, node);
	}

	public Component getParent() {
		return this.parent;
	}

	public void setParent(Component parent) {
		this.parent = parent;
	}

	public ILabelElement getNode() {
		return this.node;
	}

	public void setNode(ILabelElement node) {
		this.node = node;
	}

	private Component parent;
	private ILabelElement node;

}
