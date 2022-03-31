package com.pennanttech.extension;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.pennanttech.extension.web.menu.IMenuExtension;
import com.pennanttech.pennapps.core.FactoryException;

@Component
public class Services {
	private static Set<String> services = new HashSet<>();

	@PostConstruct
	public void initialize() {
		services.addAll(getExtension().getExclude());
	}

	public static boolean isExclude(String serviceName) {
		return services.contains(serviceName);
	}

	private static IMenuExtension getExtension() {
		IMenuExtension menuExtension;
		String exception = "The IExtensionServices implimentation should be available in the client exetension layer to override the API services list.";
		try {
			Object object = Class.forName("com.pennanttech.extension.web.menu.MenuExtension").getDeclaredConstructor()
					.newInstance();
			if (object != null) {
				menuExtension = (IMenuExtension) object;
				return menuExtension;
			} else {
				throw new FactoryException(exception);
			}
		} catch (Exception e) {
			throw new FactoryException(exception);

		}
	}

}
