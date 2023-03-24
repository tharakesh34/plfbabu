package com.pennanttech.extension.api;

import java.util.HashSet;
import java.util.Set;

public class ExtensionServices implements IExtensionServices {

	/**
	 * Add all the client specific API services into Set.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getExtended() {
		Set<String> services = new HashSet<>();

		return services;
	}

	/**
	 * Add not required API services to particular client.
	 * 
	 * Added names will removed with the help of returned object.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getFilter() {
		return new HashSet<>();
	}

	/**
	 * Add whatever the API services required for particular client.
	 * 
	 * This method will override the existing Services.
	 * 
	 * @return Set of names will be return, When list is empty, new object will return
	 */
	@Override
	public Set<String> getOverride() {
		return new HashSet<>();
	}

}
