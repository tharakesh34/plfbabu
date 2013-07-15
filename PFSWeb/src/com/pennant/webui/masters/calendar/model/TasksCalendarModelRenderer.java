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
 *//*

*//**
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  TasksCalendarModelRenderer.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  16-04-2011    
 *                                                                  
 * Modified Date    :  16-04-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-04-2011       Pennant	                 0.1                                         * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*//*

package com.pennant.webui.masters.calendar.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.api.RenderContext;
import org.zkoss.calendar.impl.SimpleCalendarModel;
*//**
 * Item renderer for listitems in the listbox.
 * 
 *//*
public class TasksCalendarModelRenderer extends SimpleCalendarModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TasksCalendarModelRenderer.class);
 
 	    private String filterText = "";
	 
	    public TasksCalendarModelRenderer(List calendarEvents) {
	        super(calendarEvents);
	 
	    }
	 
	    public void setFilterText(String filterText) {
	        this.filterText = filterText;
	    }
	 
	    @Override
	    public List get(Date beginDate, Date endDate, RenderContext rc) {
	        List list = new LinkedList();
	        long begin = beginDate.getTime();
	        long end = endDate.getTime();
	        Iterator i$ = _list.iterator();
	        do {
	            if (!i$.hasNext())
	                break;
	            CalendarEvent ce = (CalendarEvent) i$.next();
	            long b = ce.getBeginDate().getTime();
	            long e = ce.getEndDate().getTime();
	            if (e >= begin && b < end && ce.getContent().toLowerCase().contains(filterText.toLowerCase()))
	                list.add(ce);
	        } while (true);
	        return list;
	    }
	}
	 */