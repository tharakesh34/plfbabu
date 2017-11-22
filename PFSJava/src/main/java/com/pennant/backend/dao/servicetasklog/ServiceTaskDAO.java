package com.pennant.backend.dao.servicetasklog;

import com.pennant.backend.model.servicetask.ServiceTaskDetail;

public interface ServiceTaskDAO {

	public void save(ServiceTaskDetail serviceTaskDetail, String type);
}
