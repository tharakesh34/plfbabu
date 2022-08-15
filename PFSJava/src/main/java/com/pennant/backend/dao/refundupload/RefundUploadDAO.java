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

package com.pennant.backend.dao.refundupload;

import java.util.List;

import com.pennant.backend.model.refundupload.RefundUpload;

/**
 * DAO methods declaration for the <b>RefundUpload model</b> class.<br>
 * 
 */
public interface RefundUploadDAO {

	List<RefundUpload> getRefundUploadsByUploadId(long uploadId, String type);

	void update(RefundUpload refundupload, String type);

	String save(RefundUpload refundupload, String type);

	void deleteByUploadId(long uploadId, String type);

	boolean getRefundUploadsByFinReference(String finReference, long uploadId, String type);

}