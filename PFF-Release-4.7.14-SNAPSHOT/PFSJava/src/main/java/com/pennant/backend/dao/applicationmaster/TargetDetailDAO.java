package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.model.applicationmaster.TargetDetail;

public interface TargetDetailDAO {
	TargetDetail getTargetDetailById(String id, String type);
	void update(TargetDetail targetDetail, String type);
	void delete(TargetDetail targetDetail, String type);
	String save(TargetDetail targetDetail, String type);

}
