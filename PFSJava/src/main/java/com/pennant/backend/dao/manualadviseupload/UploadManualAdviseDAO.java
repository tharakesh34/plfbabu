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
 * * FileName : RefundUploadDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-10-2018 * * Modified Date
 * : 08-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-10-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.manualadviseupload;

import java.util.List;

import com.pennant.backend.model.finance.UploadManualAdvise;

/**
 * DAO methods declaration for the <b>ManualAdviseUpload model</b> class.<br>
 * 
 */
public interface UploadManualAdviseDAO {

	List<UploadManualAdvise> getAdviseUploadsByUploadId(long uploadId, String type);

	void update(UploadManualAdvise adviseupload, String type);

	void save(UploadManualAdvise adviseupload, String type);

	void deleteByUploadId(long uploadId, String type);

	boolean getAdviseUploadsByFinReference(long finID, long uploadId, String type);
	
	List<UploadManualAdvise> getManualAdviseListByUploadId(long uploadId, String type);

}