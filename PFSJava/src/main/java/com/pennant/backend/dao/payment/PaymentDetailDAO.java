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
 * * FileName : PaymentDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.payment;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pff.core.TableType;

public interface PaymentDetailDAO extends BasicCrudDao<PaymentDetail> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param paymentDetailId paymentDetailId of the PaymentDetail.
	 * @param amountType      amountType of the PaymentDetail.
	 * @param tableType       The type of the table.
	 * @return PaymentDetail
	 */
	PaymentDetail getPaymentDetail(long paymentDetailId, String type);

	boolean isDuplicateKey(long paymentDetailId, TableType tableType);

	List<PaymentDetail> getPaymentDetailList(long paymentId, String type);

	void deleteList(PaymentDetail paymentDetail, TableType tableType);

	boolean getPaymentId(long excessID);
}