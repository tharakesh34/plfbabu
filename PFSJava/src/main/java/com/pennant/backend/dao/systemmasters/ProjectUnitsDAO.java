package com.pennant.backend.dao.systemmasters;

import java.util.List;

import com.pennant.backend.model.systemmasters.ProjectUnits;

public interface ProjectUnitsDAO {

	ProjectUnits getProjectUnitsByID(long id, String type);

	long save(ProjectUnits projectUnit, String tableType);

	void update(ProjectUnits projectUnit, String tableType);

	void delete(ProjectUnits projectUnit, String tableType);

	List<ProjectUnits> getProjectUnitsByProjectID(long id, String type);
}
