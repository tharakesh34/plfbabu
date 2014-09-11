package com.pennant.fusioncharts;

import com.pennant.backend.model.Entity;

public class ChartData implements java.io.Serializable,Entity {

	private String label;
	private String Value;
	
	
	
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2054133362924673882L;

	@Override
	public boolean isNew() {
		
		return false;
	}

	@Override
	public long getId() {

		return 0;
	}

	@Override
	public void setId(long id) {

		
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getValue() {
		return Value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Value == null) ? 0 : Value.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChartData other = (ChartData) obj;
		if (Value == null) {
			if (other.Value != null) {
				return false;
			}
		} else if (!Value.equals(other.Value)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}
}
