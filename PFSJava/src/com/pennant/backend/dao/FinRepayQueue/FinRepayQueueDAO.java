package com.pennant.backend.dao.FinRepayQueue;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;

public interface FinRepayQueueDAO {

	FinRepayQueue getFinRepayQueueById(FinRepayQueue id,String type);
	void update(FinRepayQueue finRepayQueue,String type);
	String save(FinRepayQueue finRepayQueue,String type);
	void initialize(FinRepayQueue finRepayQueue);
	List<FinRepayQueue> getFinRepayQueues(String finType, Date postDate, String type);
	void setFinRepayQueueRecords(List<FinRepayQueue> finRepayQueueList);
	void deleteRepayQueue();
}
