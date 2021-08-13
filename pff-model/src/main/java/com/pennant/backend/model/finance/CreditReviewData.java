package com.pennant.backend.model.finance;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class CreditReviewData extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6579430447635480792L;

	private String finID;
	private String finReference;
	private String templateData;
	private String templateName;
	private int templateVersion;
	private CreditReviewData befImage;

	public String getFinID() {
		return finID;
	}

	public void setFinID(String finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(int templateVersion) {
		this.templateVersion = templateVersion;
	}

	public String getTemplateData() {
		return templateData;
	}

	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}

	@XmlTransient
	public CreditReviewData getBefImage() {
		return this.befImage;
	}

	public void setBefImage(CreditReviewData beforeImage) {
		this.befImage = beforeImage;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("");
		return excludeFields;
	}
}
