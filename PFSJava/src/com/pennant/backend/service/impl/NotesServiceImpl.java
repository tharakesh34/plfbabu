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
 * FileName    		:  NotesServiceImpl.java												*                           
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

package com.pennant.backend.service.impl;

import java.util.List;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.service.NotesService;

/**
 * Service implementation for methods that depends on <b>Customers</b>.<br>
 */
public class NotesServiceImpl implements NotesService {

	private NotesDAO notesDAO;

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	@Override
	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	@Override
	public Notes getNewNotes() {
		return getNotesDAO().getNewNotes();
	}

	@Override
	public void saveOrUpdate(Notes notes) {
		if (notes.isNew()) {
			getNotesDAO().save(notes);
		} else {
			getNotesDAO().update(notes);
		}
	}

	@Override
	public void delete(Notes notes) {
		getNotesDAO().delete(notes);
	}

	@Override
	public List<Notes> getNotesById(Notes notes) {
		return getNotesDAO().getNotesByID(notes);
	}
	
	@Override
	public List<Notes> getNotesList(Notes notes) {
		return getNotesDAO().getNotesList(notes);
	}	

	@Override
	public Notes refresh(Notes notes) {
		getNotesDAO().refresh(notes);
		getNotesDAO().initialize(notes);
		return notes;
	}
	
	@Override
	public void deleteAllNotes(Notes notes) {
		getNotesDAO().deleteAllNotes(notes);
	}
	
}