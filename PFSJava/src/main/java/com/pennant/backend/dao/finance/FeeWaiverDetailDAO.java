package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FeeWaiverDetail;

public interface FeeWaiverDetailDAO extends BasicCrudDao<FeeWaiverDetail> {

	List<FeeWaiverDetail> getFeeWaiverByWaiverId(long waiverId, String type);
}
