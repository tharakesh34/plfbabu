package com.pennant.backend.dao.servicetasklog;

import java.util.List;

import com.pennant.backend.model.servicetask.ServiceTaskDetail;

public interface ServiceTaskDAO {

	public void save(ServiceTaskDetail serviceTaskDetail, String type);

	public List<ServiceTaskDetail> getServiceTaskDetails(String module, String reference, String serviceTaskName);
}
