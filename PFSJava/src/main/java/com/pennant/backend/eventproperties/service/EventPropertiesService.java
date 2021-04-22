package com.pennant.backend.eventproperties.service;

import com.pennant.backend.eventproperties.service.impl.EventPropertiesServiceImpl.EventType;
import com.pennant.backend.model.eventproperties.EventProperties;

public interface EventPropertiesService {
	EventProperties getEventProperties(EventType type);
}
