/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : AssignmentUploadDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-11-2018 * * Modified
 * Date : 19-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-11-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.assignmentupload;

import java.util.List;

import com.pennant.backend.model.assignmentupload.AssignmentUpload;

/**
 * DAO methods declaration for the <b>AssignmentUpload model</b> class.<br>
 * 
 */
public interface AssignmentUploadDAO {

	List<AssignmentUpload> getAssignmentUploadsByUploadId(long uploadId, String type);

	void update(AssignmentUpload refundupload, String type);

	String save(AssignmentUpload refundupload, String type);

	void deleteByUploadId(long uploadId, String type);

	boolean getAssignmentUploadsByFinReference(String finReference, long uploadId, String type);

}