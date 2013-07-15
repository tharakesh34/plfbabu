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
 * FileName    		:  WindowBaseCtrl.java													*                           
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

package com.pennant.webui.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Window;

/**
 * Base controller for creating the controllers of the zul files with the
 * <b>use</b> tag.
 * 
 */
public abstract class WindowBaseCtrl extends Window implements AfterCompose, Serializable {

	private static final long serialVersionUID = -2179229704315045689L;
	protected transient AnnotateDataBinder binder;

	protected transient Map<String, Object> args;

	public void doOnCreateCommon(Window w) throws Exception {
		binder = new AnnotateDataBinder(w);
		binder.loadAll();
	}

	@SuppressWarnings("unchecked")
	public void doOnCreateCommon(Window w, Event fe) throws Exception {
		doOnCreateCommon(w);
		CreateEvent ce = (CreateEvent) ((ForwardEvent) fe).getOrigin();
		args = (Map<String, Object>) ce.getArg();//Upgraded to ZK-6.5.1.1 Added casting to Map
	}

	public WindowBaseCtrl() {
		super();
		this.setStyle("body {padding 0 0;}");
	}

	@Override
	public void afterCompose() {
		processRecursive(this, this);

		// Components.wireVariables(this, this); // auto wire variables
		// Components.addForwards(this, this); // auto forward
	}

	/**
	 * Go recursive through all founded components and wires all vars and added
	 * all forwarders for ALL window components. <br>
	 * 
	 * @param main
	 * @param child
	 */
	@SuppressWarnings("unchecked")
	private void processRecursive(Window main, Window child) {
		Components.wireVariables(main, child);
		Components.addForwards(main, this);
		List<Component> winList = child.getChildren();
		for (Component window : winList) {
			if (window instanceof Window) {
				processRecursive(main, (Window) window);
			}
		}
	}

}
