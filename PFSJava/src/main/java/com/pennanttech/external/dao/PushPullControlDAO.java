package com.pennanttech.external.dao;

import com.pennanttech.backend.model.external.control.PushPullControl;

public interface PushPullControlDAO {
	
	void save(PushPullControl pushPullControl);

	void update(PushPullControl pushPullControl);

	PushPullControl getValueByName(String name,String type);

}
