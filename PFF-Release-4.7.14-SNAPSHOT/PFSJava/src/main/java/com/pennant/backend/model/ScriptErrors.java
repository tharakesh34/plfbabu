package com.pennant.backend.model;

import java.util.ArrayList;
import java.util.List;

public class ScriptErrors {
	private List<ScriptError> errors = new ArrayList<>();

	public void add(String property,String value) {
		errors.add(new ScriptError(property, value));
	}

	public void add(String code, String message, String property) {
		errors.add(new ScriptError(code, message, property));
	}

	public ScriptError remove(int index) {
		return errors.remove(index);
	}

	public ScriptError get(int index) {
		return errors.get(index);
	}
	
	public List<ScriptError> getAll() {
		return errors;
	}

	public int size() {
		return errors.size();
	}

	public boolean isEmpty() {
		return errors.isEmpty();
	}

	public boolean contains(ScriptError error) {
		return errors.contains(error);
	}
}
