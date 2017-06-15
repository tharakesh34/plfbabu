package com.pennanttech.ws.model.vas;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.configuration.VASRecording;
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"vasRecordingList","returnStatus" })
public class VASRecordingDetail {
	
	@XmlElementWrapper(name="vasDetails")
	@XmlElement(name="vasDetail")
	private List<VASRecording> vasRecordingList;
	@XmlElement
	private WSReturnStatus returnStatus;

	public List<VASRecording> getVasRecordingList() {
		return vasRecordingList;
	}

	public void setVasRecordingList(List<VASRecording> vasRecordingList) {
		this.vasRecordingList = vasRecordingList;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}
	
	
	
}
