package com.penanttech.pff.model.external.bre;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private String item;

	public Item() {
	}

	public Item(String stringJSON) {
		System.out.println("hi");
		this.item = stringJSON;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

}