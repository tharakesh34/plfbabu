package com.pennant.backend.model.extendedfield;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class ExtendedFieldExtension extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 7769021778760984097L;

	private long id;
	private String extenrnalRef;
	private String purpose;
	private String modeStatus;
	private long instructionUID = Long.MIN_VALUE;
	private int sequence;
	private String event;
	private boolean newRecord;
	private ExtendedFieldExtension befImage;

	public ExtendedFieldExtension() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExtenrnalRef() {
		return extenrnalRef;
	}

	public void setExtenrnalRef(String receiptId) {
		this.extenrnalRef = receiptId;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getModeStatus() {
		return modeStatus;
	}

	public void setModeStatus(String modeStatus) {
		this.modeStatus = modeStatus;
	}

	public long getInstructionUID() {
		return instructionUID;
	}

	public void setInstructionUID(long instructionUID) {
		this.instructionUID = instructionUID;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public ExtendedFieldExtension getBefImage() {
		return befImage;
	}

	public void setBefImage(ExtendedFieldExtension befImage) {
		this.befImage = befImage;
	}

}
