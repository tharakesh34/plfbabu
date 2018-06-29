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
 * FileName    		:  LegalNoteDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-06-2018    														*
 *                                                                  						*
 * Modified Date    :  19-06-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-06-2018       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.dao.legal;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.legal.LegalNote;

public interface LegalNoteDAO extends BasicCrudDao<LegalNote> {
	
	/**
	 * Fetch the Record LegalNote by key field
	 * 
	 * @param legalNoteId
	 *            legalNoteId of the LegalNote.
	 * @param tableType
	 *            The type of the table.
	 * @return LegalNote
	 */
	LegalNote getLegalNote(long legalNoteId,String type);

	void deleteList(LegalNote legalNote, String tableType);

	List<LegalNote> getLegalNoteList(long legalId, String type);
	
}