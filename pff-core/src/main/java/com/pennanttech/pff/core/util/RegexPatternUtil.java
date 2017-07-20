package com.pennanttech.pff.core.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

/**
 * <p>
 * A suite of utilities surrounding the use of the {@link RegexPatternUtil} that contain information about the regular
 * expressions.
 * </p>
 */
public class RegexPatternUtil {
	private static final Logger			logger			= Logger.getLogger(RegexPatternUtil.class);

	private static Map<String, String>	regExPatterns	= new HashMap<String, String>();

	private RegexPatternUtil() {
		super();
	}

	public static void load(String filePath) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0 || line.startsWith("#")) {
					continue;
				}

				String code = line.replaceFirst("([^=]+)=(.*)", "$1");
				String regex = line.replaceFirst("([^=]+)=(.*)", "$2");

				register(code, regex);

			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

	}

	/**
	 * Registers the specified <code>regex</code> for the specified <code>code</code>. If the regular expression was
	 * already available for the code, the old regular expression is replaced.
	 * 
	 * @param code
	 *            Code of the regular expression for which the <code>regex</code> was provided.
	 * @param <code>regex</code> The specified regular expression.
	 */
	public static void register(String code, String regex) throws PatternSyntaxException {

		try {
			Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			logger.error(Literal.EXCEPTION, e);
			// throw e; FIXME When all existing regular expressions are corrected if not valid. 

			return;
		}

		regExPatterns.put(code, regex);
	}

	/**
	 * Returns the regular expression to which the specified code is registered.
	 * 
	 * @param code
	 *            Code for which the associated name of the regular expression to be returned.
	 * @return The regular expression to which the specified code is registered.
	 * @throws IllegalArgumentException
	 *             - If the specified code of the regular expression is null.
	 * @throws IllegalAccessError
	 *             - If the specified code of the regular expression is not registered.
	 */
	public static String getRegex(String code) {
		if (code == null) {
			throw new IllegalArgumentException();
		}

		String regex = regExPatterns.get(code);

		if (regex == null) {
			throw new IllegalAccessError("Regex registration not available.");
		}

		return regex;
	}
}
