package com.pennanttech.activity.log;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.model.Notes;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class ActivityLogServiceImpl implements ActivityLogService {
	private static Logger logger = Logger.getLogger(ActivityLogServiceImpl.class);

	private ActivityLogDAO activityLogDAO;
	protected NotesDAO notesDAO;

	public ActivityLogServiceImpl() {
		super();
	}

	@Override
	public List<Activity> getActivities(String moduleCode, Object keyValue) {
		logger.debug(Literal.ENTERING);

		return activityLogDAO.getActivities(ModuleUtil.getTableName(moduleCode), ModuleUtil.getLovFields(moduleCode)[0],
				keyValue);
	}

	@Override
	public List<Activity> getActivities(String moduleCode, Object keyValue, long fromAuditId, long toAuditId) {
		logger.debug(Literal.ENTERING);

		return activityLogDAO.getActivities(ModuleUtil.getTableName(moduleCode), ModuleUtil.getLovFields(moduleCode)[0],
				keyValue, fromAuditId, toAuditId);
	}

	@Override
	public List<Notes> getNotesList(Object reference, List<String> moduleNames) {
		return notesDAO.getNotesListAsc(String.valueOf(reference), moduleNames);
	}

	public void setActivityLogDAO(ActivityLogDAO activityLogDAO) {
		this.activityLogDAO = activityLogDAO;
	}

	@Autowired
	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

}
