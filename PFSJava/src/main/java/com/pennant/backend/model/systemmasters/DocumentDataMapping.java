package com.pennant.backend.model.systemmasters;

import java.io.Serializable;

public class DocumentDataMapping implements Serializable {

	private static final long serialVersionUID = -5722811453434523809L;

	private String type;
	private long MappingId;

	public DocumentDataMapping() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getMappingId() {
		return MappingId;
	}

	public void setMappingId(long mappingId) {
		MappingId = mappingId;
	}

	

}
