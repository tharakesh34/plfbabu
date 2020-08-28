package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FeeWaiverDetail;

public interface FeeWaiverDetailDAO extends BasicCrudDao<FeeWaiverDetail> {

	List<FeeWaiverDetail> getFeeWaiverByWaiverId(long waiverId, String type);

	List<FeeWaiverDetail> getFeeWaiverEnqDetailList(String finReference);

	BigDecimal getFeeWaiverDetailList(String finReference, long adviseId);
}
