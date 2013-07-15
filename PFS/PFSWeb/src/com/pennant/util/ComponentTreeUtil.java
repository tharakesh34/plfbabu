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
 * FileName    		:  ComponentTreeUtil.java												*                           
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
package com.pennant.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;

/**
 * Helper class for showing the zkoss component tree in the console for a root
 * component.
 * 
 * <pre>
 * Call it with:
 * System.out.println(ComponentTreeUtil.getZulTree(aComponent));
 * </pre>
 * 
 */
public class ComponentTreeUtil {

	final private static class FieldListener implements IAddListener {
		private final Field field;

		FieldListener(Field field) {
			super();
			this.field = field;
		}

		@Override
		public void addListener(Component component, StringBuilder result, int depth) {
			try {
				Map<?, ?> m = (Map<?, ?>) field.get(component);
				if (m != null && !m.isEmpty()) {
					for (Map.Entry<?, ?> entry : m.entrySet()) {
						result.append(StringUtils.leftPad("", depth << 2) + "  " + entry.getKey() + " -> " + entry.getValue() + "\n");
					}
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
		}
	}

	private interface IAddListener {
		void addListener(Component component, StringBuilder result, int depth);
	}

	final private static IAddListener ADD_LISTENER;

	static {
		IAddListener tmp = null;
		try {
			final Field field;
			field = AbstractComponent.class.getDeclaredField("_listeners");
			field.setAccessible(true);
			tmp = new FieldListener(field);
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}

		if (tmp == null) {
			tmp = new IAddListener() {
				@Override
				public void addListener(Component component, StringBuilder result, int depth) {
				}
			};
		}

		ADD_LISTENER = tmp;
	}

	static public CharSequence getZulTree(Component component) {
		return new ComponentTreeUtil().getZulTreeImpl(component);
	}

	private ComponentTreeUtil() {
		super();
	}

 	private CharSequence createCompName(Component component) {
		StringBuilder sb = new StringBuilder();
		sb.append(component.getClass().getSimpleName());

		String id = component.getId();
		//Upgraded to ZK-6.5.1.1 changed from isAutoId to isImplicit
		if (!Components.isImplicit(id)) {
			sb.append(" id=\"" + id + "\"");
		}
		return sb;
	}

	private CharSequence getZulTreeImpl(Component component) {
		if (component == null) {
			return "Component is null!";
		}

		StringBuilder result = new StringBuilder(6000);
		return getZulTreeImpl(component, result, -1);
	}

	private StringBuilder getZulTreeImpl(Component component, StringBuilder result, int depth) {
		++depth;
		CharSequence id = createCompName(component);
		if (CollectionUtils.isEmpty(component.getChildren())) {
			result.append(StringUtils.leftPad("", depth << 2) + "<" + id + " />\n");
			return result;
		}

		result.append(StringUtils.leftPad("", depth << 2) + "<" + id + ">\n");

		ADD_LISTENER.addListener(component, result, depth);

		for (@SuppressWarnings("rawtypes")
		Iterator iterator = component.getChildren().iterator(); iterator.hasNext();) {
			getZulTreeImpl((Component) iterator.next(), result, depth);
		}

		result.append(StringUtils.leftPad("", depth << 2) + "<" + component.getClass().getSimpleName() + " />\n");
		return result;
	}

}
