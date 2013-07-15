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
 * FileName    		:  DiaryNotesSchedule.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
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
package com.pennant.app.util;

import java.io.Serializable;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.app.model.FrequencyDetails;
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.service.diarynotes.DiaryNotesService;

public class DiaryNotesSchedule implements Job,Serializable {

    private static final long serialVersionUID = -2261249418897078589L;
    
	private List<DiaryNotes> dnList = null;
	private transient  		 DiaryNotesService diaryNotesService;
	private DiaryNotes 		 diaryNotes = null;	
	
	@SuppressWarnings("unused")
	private FrequencyUtil 	 frequencyUtil = new FrequencyUtil();
	private FrequencyDetails frequencyDetails = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {	
		System.out.println("TESTING");
		//TODO  Define the Activtiy to be Executed Next Processing Date Calculation
			dnList = getDiaryNotesService().getDiaryNoteRecord();
			if(dnList != null){
				for(int i=0;i<dnList.size();i++){
					diaryNotes 		= new DiaryNotes();
					diaryNotes 		= dnList.get(i);					
					diaryNotes.setLastActionDate(diaryNotes.getNextActionDate());
					System.out.println("LAST ACTION DATE ::::::::::::::::"+diaryNotes.getLastActionDate());
					frequencyDetails = FrequencyUtil.getNextDate(diaryNotes.getFrqCode(), 0,diaryNotes.getNextActionDate()!=null?diaryNotes.getNextActionDate()
																:diaryNotes.getFirstActionDate(), "",true);					
					System.out.println("NEXT ACTION DATE ::::::::::"+frequencyDetails.getNextFrequencyDate());
					diaryNotes.setNextActionDate(frequencyDetails.getNextFrequencyDate());
					getDiaryNotesService().updateForScheduled(diaryNotes);
				}
			}
			System.out.println("TESTING1");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setDiaryNotesService(DiaryNotesService diaryNotesService) {
		this.diaryNotesService = diaryNotesService;
	}
	public DiaryNotesService getDiaryNotesService() {
		return this.diaryNotesService;
	}

}
