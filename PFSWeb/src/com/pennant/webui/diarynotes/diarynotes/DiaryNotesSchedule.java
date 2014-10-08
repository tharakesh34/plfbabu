package com.pennant.webui.diarynotes.diarynotes;

import java.io.Serializable;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.utils.Key;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.dao.diarynotes.DiaryNotesDAO;
import com.pennant.backend.model.diarynotes.DiaryNotes;


public class DiaryNotesSchedule  extends QuartzJobBean implements StatefulJob, Serializable {
	private static final long serialVersionUID = 3560027943201460852L;
	
	private List<DiaryNotes> dnList = null;
	private DiaryNotes 		 diaryNotes = null;	
	private FrequencyDetails frequencyDetails = null;
	private static DiaryNotesDAO diaryNotesDAO;
		
	
	@SuppressWarnings("unchecked")
	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {	
		System.out.println("TESTING");
		
		Key jobKey = context.getJobDetail().getKey();
		
		if(jobKey.getName().equalsIgnoreCase("NPD")){
			dnList = getDiaryNotesDAO().getDiaryNoteRecord();
			if(dnList != null){
				for(int i=0;i<dnList.size();i++){
					diaryNotes 		= new DiaryNotes();
					diaryNotes 		= dnList.get(i);					
					diaryNotes.setLastActionDate(diaryNotes.getNextActionDate());
					System.out.println("LAST ACTION DATE ::::::::::::::::"+diaryNotes.getLastActionDate());		
					
					frequencyDetails = FrequencyUtil.getNextDate(diaryNotes.getFrqCode(), 1,
								diaryNotes.getNextActionDate()!=null?diaryNotes.getNextActionDate():diaryNotes.getFirstActionDate(),
											"",false);
					
					System.out.println("NEXT ACTION DATE ::::::::::"+frequencyDetails.getNextFrequencyDate()!=null?
										frequencyDetails.getNextFrequencyDate():"");
					
					diaryNotes.setNextActionDate(frequencyDetails.getNextFrequencyDate());
					getDiaryNotesDAO().updateForScheduled(diaryNotes);
				}
			}
		}else if(jobKey.getName().equalsIgnoreCase("SUSPEND")){
				   getDiaryNotesDAO().updateForSuspend();
		}else if(jobKey.getName().equalsIgnoreCase("DELETE")){
				   getDiaryNotesDAO().updateForDelete();
		}
		
		System.out.println("TESTING1");
	}

	public void setDiaryNotesDAO(DiaryNotesDAO diaryNotesDAO) {
		DiaryNotesSchedule.diaryNotesDAO = diaryNotesDAO;
	}

	public static DiaryNotesDAO getDiaryNotesDAO() {
		return diaryNotesDAO;
	}


}
