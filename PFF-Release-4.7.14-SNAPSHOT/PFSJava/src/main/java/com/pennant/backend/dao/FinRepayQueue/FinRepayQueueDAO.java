package com.pennant.backend.dao.FinRepayQueue;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;

public interface FinRepayQueueDAO {

	void setFinRepayQueueRecords(List<FinRepayQueue> finRepayQueueList);

	void deleteRepayQueue();

	FinRepayQueue getFinRePayDetails(String finReference, Date repayDate);

	void update(FinRepayQueue repayQueue, String type);

	void deleteByCustID(long customerID);
}
