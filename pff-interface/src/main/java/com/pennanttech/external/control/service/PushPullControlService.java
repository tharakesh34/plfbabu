package com.pennanttech.external.control.service;

import com.pennanttech.backend.model.external.control.PushPullControl;

public interface PushPullControlService {
	
	void save(PushPullControl pushPullControl);

	void update(PushPullControl pushPullControl);

	PushPullControl getValueByName(String name,String type);

}
