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
 * * FileName : FeeTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified Date :
 * 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.feetype;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FeeType;
import com.pennanttech.pff.core.TableType;

public interface FeeTypeDAO extends BasicCrudDao<FeeType> {

	boolean isDuplicateKey(long feeTypeID, String feeTypeCode, TableType tableType);

	FeeType getFeeTypeById(long id, String type);

	List<FeeType> getFeeTypeListByIds(List<Long> feeTypeIds);

	List<Long> getFeeTypeIDs(List<String> feeTypeCodes);

	List<FeeType> getManualAdviseFeeType(int adviceType, String type);

	List<FeeType> getAMZReqFeeTypes();

	FeeType getApprovedFeeTypeByFeeCode(String feeTyeCode);

	FeeType getTaxDetailByCode(String feeTypeCode);

	int getAccountingSetIdCount(long accountSetId, String type);

	Long getFinFeeTypeIdByFeeType(String feeTypeCode, String type);

	String getTaxCompByCode(String feeTypeCode);

	Long getFeeTypeId(String feeTypeCode);

	boolean isFeeTypeAmortzReq(String feeTypeCode);

	String getTaxComponent(String feeTypeCode);

	long getManualAdviseFeeTypeById(long id);

	FeeType getFeeTypeByRecvFeeTypeId(long id);

	boolean isValidFee(String feeTypeCode, int adviseType);

	String getFeeTypeCode(String feeTypeCode, String payableLinkTo);

	public String getOtrRecFeeTypeCode(String feeTypeCode, String payableLinkTo, long recvFeeTypeId);

	long getRecvFeeTypeId(String feeTypeCode, String payableLinkTo, long recvFeeTypeId);

	FeeType getPayableFeeType(String feeTypeCode);

	boolean isValidFeeType(String feeTypeCode);

	List<String> getReceivableFeeTypes();

	Long getPayableFeeTypeID(String code);
}