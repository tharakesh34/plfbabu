package com.pennanttech.ws.model.remark;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WSReturnStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class RemarksResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Notes> notesList = null;
	private WSReturnStatus returnStatus;

	public RemarksResponse() {
		super();
	}

	public List<Notes> getNotesList() {
		return notesList;
	}

	public void setNotesList(List<Notes> notesList) {
		this.notesList = notesList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

}
