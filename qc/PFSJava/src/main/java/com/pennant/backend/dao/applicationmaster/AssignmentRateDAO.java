package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.AssignmentRate;

public interface AssignmentRateDAO {

	AssignmentRate getAssignmentRateById(long id, String type);

	void update(AssignmentRate assignmentRate, String type);

	void delete(AssignmentRate assignmentRate, String type);

	long save(AssignmentRate assignmentRate, String type);

	List<AssignmentRate> getAssignmentRatesByAssignmentId(long id, String type);

	void deleteByAssignmentId(long assignmentId, String type);

}
