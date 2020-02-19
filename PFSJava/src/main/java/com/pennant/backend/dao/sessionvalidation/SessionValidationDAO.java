package com.pennant.backend.dao.sessionvalidation;

import java.util.List;

import com.pennant.backend.model.sessionvalidation.SessionValidation;

public interface SessionValidationDAO {

	SessionValidation getSessionById(long agentId);

	void save(SessionValidation sessionValidation);

	void update(SessionValidation sessionValidation);

	List<SessionValidation> getActiveSessions();
}
