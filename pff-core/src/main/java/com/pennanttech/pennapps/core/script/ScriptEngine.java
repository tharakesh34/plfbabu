package com.pennanttech.pennapps.core.script;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.pennanttech.pennapps.core.resource.Literal;

public class ScriptEngine implements AutoCloseable {
	private Logger logger = LogManager.getLogger(ScriptEngine.class);

	private Context context = null;
	private boolean eod;

	public ScriptEngine() {
		context = Context.create("js");
	}

	public ScriptEngine(boolean eod) {
		this.eod = eod;
		context = Context.create("js");
	}

	/**
	 * Returns {@link java.lang.String} for the specified {@link String}.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map} which needs to pass the Bindings for the script.
	 * @return A {@link java.lang.String} or <code>""</code>.
	 */
	public String getResultAsString(String rule, Map<String, Object> dataMap) {
		Value result = eval(rule, dataMap);

		return result == null ? "" : result.toString();
	}

	/**
	 * Returns {@link java.math.BigDecimal} for the specified {@link BigDecimal}.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map} which needs to pass the Bindings for the script.
	 * @return A {java.math.BigDecimal} or BigDecimal.ZERO.
	 */
	public BigDecimal getResultAsBigDecimal(String rule, Map<String, Object> dataMap) {
		String result = getResultAsString(rule, dataMap);

		if (result == null || result.equals("")) {
			return BigDecimal.ZERO;
		} else {
			return converAsBigDecimal(result);
		}
	}

	/**
	 * Returns int.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map} which needs to pass the Bindings for the script.
	 * @return A int or <code>0</code>.
	 */
	public int getResultAsInt(String rule, Map<String, Object> dataMap) {
		String result = getResultAsString(rule, dataMap);

		if (result == null || result.equals("")) {
			return 0;
		} else {
			return converAsInteger(rule);
		}
	}

	/**
	 * Returns boolean.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map} which needs to pass the Bindings for the script.
	 * @return A boolean of <code>flase</code> or <code>true</code>.
	 */
	public boolean getResultAsBoolean(String rule, Map<String, Object> dataMap) {
		String result = getResultAsString(rule, dataMap);

		if (result.equals("")) {
			return false;
		} else if (result.equals(1) || result.equals("1") || result.equalsIgnoreCase("Y")
				|| result.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	public Object getResultAsObject(String rule, Map<String, Object> dataMap) {
		Value result = eval(rule, dataMap);
		return result == null ? new Object() : (Object) result;
	}
	
	public Object getResultAsObject(String rule, Map<String, Object> dataMap, String reference) {
		Value result = eval(rule, dataMap, reference);
		return result == null ? new Object() : (Object) result;
	}
	
	
	private Value eval(String rule, Map<String, Object> dataMap, String reference) {
		String scriptRule = getRule(rule);

		Value bindings = null;
		try {
			bindings = context.getBindings("js");

			if ("defaults".equals(reference)) {
				bindings.putMember("defaults", dataMap.get("defaults"));
			} else if ("errors".equals(reference)) {
				bindings.putMember("errors", dataMap.get("errors"));
			}

			context.eval("js", scriptRule);
			Value result = bindings.getMember("Result");
			return result;
		} catch (Exception e) {
			logger.warn("{} Rule is not configured properly", rule);
			return null;
		} finally {
			bindings = null;
		}
	}

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map} which needs to pass the Bindings for the script.
	 * @return A {@link org.graalvm.polyglot.Value} or <code>null</code>.
	 */
	private Value eval(String rule, Map<String, Object> dataMap) {
		String scriptRule = getRule(rule);

		Value bindings = null;
		try {
			bindings = getBindings(rule, dataMap);
			
			context.eval("js", scriptRule);
			
			Value result = bindings.getMember("Result");
			return result;
		} catch (Exception e) {
			logger.warn("{} Rule is not configured properly", rule);
			return null;
		} finally {
			bindings = null;
		}
	}

	private String getRule(String rule) {
		return "function Pennant(){" + rule + "}Pennant();";
	}

	private Value getBindings(String rule, Map<String, Object> dataMap) {
		Value bindings = context.getBindings("js");

		if (dataMap.isEmpty()) {
			return bindings;
		}

		for (Entry<String, Object> entry : dataMap.entrySet()) {
			String key = entry.getKey();

			if (rule.indexOf(key) > 0) {
				Object value = entry.getValue();
				if (value instanceof BigDecimal) {
					bindings.putMember(key, getValue(value));
				} else {
					bindings.putMember(key, value);
				}
			}
		}
		
		return bindings;
	}

	private static double getValue(Object object) {
		return object == null ? 0 : ((BigDecimal) object).doubleValue();
	}

	@Override
	public void close() throws Exception {
		if (!eod) {
			context.close();
		}
	}

	public void setEod(boolean eod) {
		this.eod = eod;
	}

	private BigDecimal converAsBigDecimal(String result) {
		try {
			return new BigDecimal(result);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logger.error("Result {}", result);
		}
		return BigDecimal.ZERO;

	}

	private int converAsInteger(String result) {
		try {
			return Integer.parseInt(result);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logger.error("Result {}", result);
		}
		return 0;

	}
}
