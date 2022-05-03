package com.pennanttech.extension.web.menu;

import java.util.HashSet;
import java.util.Set;

public class MenuExtension implements IMenuExtension {

	/**
	 * Add all the not required menu items into Set.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getExclude() {
		return new HashSet<>();
	}

	/**
	 * Add all the required menu items into Set.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getInclude() {
		return new HashSet<>();
	}

}
