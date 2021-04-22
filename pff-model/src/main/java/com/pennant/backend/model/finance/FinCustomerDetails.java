package com.pennant.backend.model.finance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.WSReturnStatus;

@XmlType(propOrder = { "finReference", "cif", "returnStatus" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinCustomerDetails implements java.io.Serializable {
	private static final long serialVersionUID = -8297008832257931628L;
	@XmlElement
	private String finReference;
	@XmlElement
	private List<Category> cif = new ArrayList<FinCustomerDetails.Category>();

	@XmlElement
	private WSReturnStatus returnStatus;

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public class Category {
		private String cif;
		private String category;
		private String name;

		public String getCif() {
			return cif;
		}

		public void setCif(String cif) {
			this.cif = cif;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public List<Category> getCif() {
		return cif;
	}

	public void setCif(List<Category> cif) {
		this.cif = cif;
	}
}
