package com.pennant.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.polyglot.HostAccess;

public class ScriptErrors {
	private List<ScriptError> errors = new ArrayList<>();

	@HostAccess.Export
	public void add(String property, String value) {
		errors.add(new ScriptError(property, value));
	}

	@HostAccess.Export
	public void add(String code, String message, String property) {
		errors.add(new ScriptError(code, message, property));
	}

	@HostAccess.Export
	public ScriptError remove(int index) {
		return errors.remove(index);
	}

	public ScriptError get(int index) {
		return errors.get(index);
	}

	@HostAccess.Export
	public List<ScriptError> getAll() {
		return errors;
	}

	public int size() {
		return errors.size();
	}

	public boolean isEmpty() {
		return errors.isEmpty();
	}

	@HostAccess.Export
	public boolean contains(ScriptError error) {
		return errors.contains(error);
	}
}
