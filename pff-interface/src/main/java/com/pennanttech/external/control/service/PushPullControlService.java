package com.pennanttech.external.control.service;

import com.pennanttech.backend.model.external.control.PushPullControl;

public interface PushPullControlService {

	long save(PushPullControl pushPullControl);

	void update(PushPullControl pushPullControl);

	PushPullControl getValueByName(String name, String type);

}
