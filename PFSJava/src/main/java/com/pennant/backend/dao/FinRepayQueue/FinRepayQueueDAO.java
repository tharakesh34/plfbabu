package com.pennant.backend.dao.FinRepayQueue;

import java.util.List;

import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;

public interface FinRepayQueueDAO {

	void setFinRepayQueueRecords(List<FinRepayQueue> finRepayQueueList);

	void update(FinRepayQueue repayQueue, String type);

}
