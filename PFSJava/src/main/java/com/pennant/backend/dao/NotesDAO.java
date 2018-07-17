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
 * FileName    		:  NotesDAO.java												*                           
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
package com.pennant.backend.dao;

import java.util.List;

import com.pennant.backend.model.Notes;

public interface NotesDAO {
	List<Notes> getNotesList(Notes notes, boolean isNotes);
	
	List<Notes> getNotesForAgreements(Notes notes);

	/**
	 * Get the list of notes for the specified record in ascending order.
	 * 
	 * @param notes
	 *            The notes object with required parameters.
	 * @return The list of notes for the specified record in ascending order.
	 */
	List<Notes> getNotesListAsc(Notes notes);

	List<Notes> getNotesListByRole(Notes notes, boolean isNotes, String[] roleCodes);

	void save(Notes notes);

	void delete(Notes notes);

	void deleteAllNotes(Notes notes);

	List<Notes> getNotesListAsc(String reference, List<String> moduleNames);
}