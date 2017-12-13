package com.pennant.backend.model.extendedfield;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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

	@XmlElementWrapper(name="extendedFields")
	@XmlElement(name = "extendedField")
	private List<ExtendedFieldData> extendedFieldDataList;

	public List<ExtendedFieldData> getExtendedFieldDataList() {
		return extendedFieldDataList;
	}

	public void setExtendedFieldDataList(List<ExtendedFieldData> extendedFieldDataList) {
		this.extendedFieldDataList = extendedFieldDataList;
	}
}
