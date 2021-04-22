package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.service.NotesService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class RemarksController extends ExtendedTestClass {

	private final Logger logger = LogManager.getLogger(getClass());

	private NotesService notesService;

	public RemarksController() {
		super();
	}

	public WSReturnStatus doAddRemarks(List<Notes> remarks) {

		logger.debug(Literal.ENTERING);

		for (Notes notes : remarks) {
			if (notes.getUsrLogin().trim().isEmpty()) {
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				notes.setInputBy(userDetails.getUserId());
			}

			if (notes.getInDate() != null) {
				notes.setInputDate(new Timestamp(notes.getInDate().getTime()));
			} else {
				notes.setInputDate(new Timestamp(DateUtility.getAppDate().getTime()));
			}

			if (notes.getAlignType().isEmpty()) {
				notes.setAlignType("R");
			}

			if (notes.getRemarkType().isEmpty()) {
				notes.setRemarkType("N");
			}

			if (notes.getModuleName().trim().isEmpty()) {
				notes.setModuleName("FinanceMain");
			}

			notesService.saveOrUpdate(notes);
		}

		logger.debug(Literal.LEAVING);

		return APIErrorHandlerService.getSuccessStatus();
	}

	@Autowired
	public void setNotesService(NotesService notesService) {
		this.notesService = notesService;
	}

}
