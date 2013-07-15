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
 * FileName    		:  DiaryNotesService.java                                                   * 	  
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

package com.pennant.backend.service.diarynotes;

import java.util.List;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.diarynotes.DiaryNotes;

public interface DiaryNotesService {
	
	DiaryNotes getDiaryNotes();
	DiaryNotes getNewDiaryNotes();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	DiaryNotes getDiaryNotesById(long id);
	DiaryNotes getApprovedDiaryNotesById(long id);
	DiaryNotes refresh(DiaryNotes diaryNotes);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	@SuppressWarnings("rawtypes")
	List getDiaryNoteRecord();
	void updateForScheduled(DiaryNotes diaryNotes); 
	ErrorDetails getErrorDetails(String errorCode,String language,String[] parm);
}