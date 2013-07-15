package com.pennant.webui.diarynotes.diarynotes;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.pennant.backend.dao.diarynotes.DiaryNotesDAO;

public class DiaryNotesSuspendSchedule implements Job {
	private static final long serialVersionUID = 3560027943201460852L;
	private static DiaryNotesDAO diaryNotesDAO;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
			getDiaryNotesDAO().updateForSuspend();
	}

	public static DiaryNotesDAO getDiaryNotesDAO() {
		return diaryNotesDAO;
	}

	public void setDiaryNotesDAO(DiaryNotesDAO diaryNotesDAO) {
		DiaryNotesSuspendSchedule.diaryNotesDAO = diaryNotesDAO;
	}

}
	
