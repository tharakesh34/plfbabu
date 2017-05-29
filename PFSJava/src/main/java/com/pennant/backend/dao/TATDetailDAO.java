package com.pennant.backend.dao;

import java.util.List;

import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.TATNotificationCode;
import com.pennant.backend.model.finance.TATNotificationLog;

public interface TATDetailDAO {
	List<TATDetail> getAllTATDetail();

	TATDetail getTATDetail(String reference, String rolecode);

	void save(TATDetail tatDetail);

	void update(TATDetail tatDetail);

	TATNotificationLog getLogDetails(TATDetail tatDetail);

	TATNotificationCode getNotificationdetail(String code);

	void saveLogDetail(TATNotificationLog notificationLog);

	void updateLogDetail(TATNotificationLog notificationLog);
}
