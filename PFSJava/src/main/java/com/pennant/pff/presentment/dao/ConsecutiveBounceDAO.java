package com.pennant.pff.presentment.dao;

import java.util.Date;

import com.pennanttech.pff.presentment.model.ConsecutiveBounce;

public interface ConsecutiveBounceDAO {
	ConsecutiveBounce getBounces(long mandateId);

	void create(long mandateId, long bounceId, Date schdDate);

	void update(long mandateId, Date schdDate, int bounceCount);

	void resetConter(long mandateId, long bounceId, Date schdDate);

	int delete(long mandateId);

}
