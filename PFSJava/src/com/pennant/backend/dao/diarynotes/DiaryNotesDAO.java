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
 *																							*
 * FileName    		:  DiaryNotesDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-08-2011    														*
 *                                                                  						*
 * Modified Date    :  20-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.diarynotes;

import java.util.List;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.diarynotes.DiaryNotes;

public interface DiaryNotesDAO {

	DiaryNotes getDiaryNotes();
	DiaryNotes getNewDiaryNotes();
	DiaryNotes getDiaryNotesById(long id,String type);
	void update(DiaryNotes diaryNotes,String type);
	void delete(DiaryNotes diaryNotes,String type);
	long save(DiaryNotes diaryNotes,String type);
	void initialize(DiaryNotes diaryNotes);
	void refresh(DiaryNotes entity);
	@SuppressWarnings("rawtypes")
	List getDiaryNoteRecord();
	void updateForScheduled(DiaryNotes diaryNotes);
	void updateForSuspend();
	void updateForDelete();
	ErrorDetails getErrorDetail (String errorId,String errorLanguage,String[] parameters);
}