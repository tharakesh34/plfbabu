package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.eod.model.EodDetail;

public interface EodDetailDAO {

	void save(EodDetail eodDetail);

	void update(EodDetail eodDetail);

	EodDetail getEodDetailById(Date date);

	void updateStatus(EodDetail eodDetail);

}
