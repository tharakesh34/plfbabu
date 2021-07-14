package com.pennant.backend.model.extendedfield;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 * This class is designed and used to fulfill API specification requirement
 * 
 * @author durgaprasad.d
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ExtendedField implements Serializable {

	private static final long serialVersionUID = -5159335556711548402L;

	public ExtendedField() {

	}

	@XmlElementWrapper(name = "extendedFields")
	@JsonProperty("extendedField")
	private List<ExtendedFieldData> extendedFieldDataList;

	public ExtendedField copyEntity() {
		ExtendedField entity = new ExtendedField();
		if (extendedFieldDataList != null) {
			entity.setExtendedFieldDataList(new ArrayList<ExtendedFieldData>());
			this.extendedFieldDataList.stream()
					.forEach(e -> entity.getExtendedFieldDataList().add(e == null ? null : e.copyEntity()));
		}
		return entity;
	}

	public List<ExtendedFieldData> getExtendedFieldDataList() {
		return extendedFieldDataList;
	}

	public void setExtendedFieldDataList(List<ExtendedFieldData> extendedFieldDataList) {
		this.extendedFieldDataList = extendedFieldDataList;
	}
}
