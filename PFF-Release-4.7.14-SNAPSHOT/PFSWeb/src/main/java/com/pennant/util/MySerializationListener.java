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
 * FileName    		:  MySerializationListener.java											*                           
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.ComponentSerializationListener;
import org.zkoss.zk.ui.util.EventInterceptor;

public class MySerializationListener implements EventListener<Event>, EventInterceptor, java.io.Serializable,
		ComponentSerializationListener {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MySerializationListener.class);

	public MySerializationListener() {
	}

	@Override
	public void onEvent(Event event) throws Exception {
		logger.info("onEvent 1. --> : " + event.getTarget().getId());
		logger.info("onEvent 2. --> : " + event.toString());

	}

	@Override
	public void didDeserialize(Component comp) {
		logger.info("didSerialize --> : " + comp.getId());
		logger.info("didSerialize --> : " + comp.toString());
	}

	// ComponentSerializationListener//
	@Override
	public void willSerialize(Component comp) {
		logger.info("willSerialize --> : " + comp.getId());
		logger.info("willSerialize --> : " + comp.toString());

	}

	@Override
	public void afterProcessEvent(Event event) {
		logger.info("onEvent --> : " + event.getTarget().getId());
		logger.info("onEvent --> : " + event.toString());

	}

	@Override
	public Event beforePostEvent(Event event) {
		logger.info("beforePostEvent 1. --> : " + event.getTarget().getId());
		logger.info("beforePostEvent 2. --> : " + event.toString());
		logger.info("beforePostEvent 3. --> : " + event.getTarget().getDesktop().getSession().toString());

		final Map<String, ?> map = event.getTarget().getDesktop().getSession().getAttributes();

		int i = 1;
		for (final String str : map.keySet()) {
			logger.info("Object Nr.: " + i++ + " / " + str);
		}
		return null;
	}

	@Override
	public Event beforeProcessEvent(Event event) {
		logger.info("onEvent --> : " + event.getTarget().getId());
		logger.info("onEvent --> : " + event.toString());
		return null;
	}

	@Override
	public Event beforeSendEvent(Event event) {
		logger.info("onEvent --> : " + event.getTarget().getId());
		logger.info("onEvent --> : " + event.toString());
		return null;
	}

}
