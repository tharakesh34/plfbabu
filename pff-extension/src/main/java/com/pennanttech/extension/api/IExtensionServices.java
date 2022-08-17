package com.pennanttech.extension.api;

import java.util.Set;

public interface IExtensionServices {
	public Set<String> getExtended();

	public Set<String> getFilter();

	public Set<String> getOverride();
}
