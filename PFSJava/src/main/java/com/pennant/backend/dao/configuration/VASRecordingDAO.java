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
 * * FileName : VASRecordingDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016 * * Modified Date
 * : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.configuration;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;

public interface VASRecordingDAO {
	VASRecording getVASRecording();

	VASRecording getNewVASRecording();

	VASRecording getVASRecordingByReference(String vasRefrence, String type);

	void update(VASRecording vASRecording, String type);

	void delete(VASRecording vASRecording, String type);

	String save(VASRecording vASRecording, String type);

	VasCustomer getVasCustomerCif(String primaryLinkRef, String postingAgainst);

	void deleteByPrimaryLinkRef(String primaryLinkRef, String type);

	List<VASRecording> getVASRecordingsByLinkRef(String finReferences, String type);

	List<VASRecording> getVASRecordingsByLinkRef(List<String> finReferences, String type);

	void updateVasStatus(String status, String vasReference);

	VASRecording getVASRecording(String vasReference, String vasStatus, String type);

	void updateVasStatus(String reference, long paymentInsId);

	List<VASRecording> getVASRecordingsStatusByReference(String finReference, String string);

	Long getCustomerId(String finReference);

	void updatePaidAmt(String vasReference, String primaryLinkRef, BigDecimal paidAmt, String type);

	List<VASRecording> getLoanReportVasRecordingByRef(String finReference);

	Long getPaymentInsId(String vasReference, String type);

	String getVasInsStatus(long paymentInsId);

	void updateVasInsStatus(long id);

	String getProductCodeByReference(String primaryLinkRef, String vasReference);

}