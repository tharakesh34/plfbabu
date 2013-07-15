package com.pennant.backend.dao.FinRepayQueue;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;

public interface FinRepayQueueDAO {

	public FinRepayQueue getFinRepayQueueById(FinRepayQueue id,String type);
	public void update(FinRepayQueue finRepayQueue,String type);
	public String save(FinRepayQueue finRepayQueue,String type);
	public void initialize(FinRepayQueue finRepayQueue);
	public List<FinRepayQueue> getFinRepayQueues(String finType, Date postDate, String type);
	public void setFinRepayQueueRecords(FinRepayQueue finRepayQueue, String type);
}
