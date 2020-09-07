package com.pennanttech.pennapps.core.script;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class ScriptEngine implements AutoCloseable {
	private Context context = null;
	private Value bindings = null;

	public ScriptEngine() {
		context = Context.create("js");
		bindings = context.getBindings("js");
	}
	
	/**
	 * Returns {@link java.lang.String} for the specified {@link String}.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map}  which needs to pass the Bindings for the script. 
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
	 *            The Map {@link java.util.Map}  which needs to pass the Bindings for the script. 
	 * @return A {java.math.BigDecimal} or BigDecimal.ZERO.
	 */
	public BigDecimal getResultAsBigDecimal(String rule, Map<String, Object> dataMap) {
		String result = getResultAsString(rule, dataMap);

		if (result.equals("")) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(result.toString());
		}
	}

	/**
	 * Returns int.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map}  which needs to pass the Bindings for the script. 
	 * @return A int or <code>0</code>.
	 */
	public int getResultAsInt(String rule, Map<String, Object> dataMap) {
		String result = getResultAsString(rule, dataMap);

		if (result.equals("")) {
			return 0;
		} else {
			return Integer.parseInt(result.toString());
		}
	}
	
	/**
	 * Returns boolean.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map}  which needs to pass the Bindings for the script. 
	 * @return A boolean of <code>flase</code> or <code>true</code>. 
	 */
	public boolean getResultAsBoolean(String rule, Map<String, Object> dataMap) {
		String result = getResultAsString(rule, dataMap);

		if (result.equals("")) {
			return false;
		} else if(result.equals("1") || result.equals(1)){
			return true;
		}else {
			return false;
		}
	}
	
	public Object getResultAsObject(String rule, Map<String, Object> dataMap) {
		Value result=eval(rule, dataMap);
		return result == null ? new Object() : (Object)result;
	}
	
	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @param rule
	 *            The String rule which needs to executed as JavaScript .
	 * @param dataMap
	 *            The Map {@link java.util.Map}  which needs to pass the Bindings for the script.   
	 * @return A {@link org.graalvm.polyglot.Value} or <code>null</code>.        
	 */
	private Value eval(String rule, Map<String, Object> dataMap) {
		String scriptRule = getRule(rule);

		try {

			putMembers(dataMap);

			context.eval("js", scriptRule);
			Value result = bindings.getMember("Result");
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	private String getRule(String rule) {
		return "function Pennant(){" + rule + "}Pennant();";
	}

	private void putMembers(Map<String, Object> dataMap) {
		if(dataMap.isEmpty()) {
			return;
		}
		for (Entry<String, Object> entry : dataMap.entrySet()) {
			if (entry.getValue() instanceof BigDecimal) {
				bindings.putMember(entry.getKey(), getValue(entry.getValue()));
			} else {
				bindings.putMember(entry.getKey(), entry.getValue());
			}
		}
	}

	private static double getValue(Object object) {
		return object == null ? 0 : ((BigDecimal) object).doubleValue();
	}

	@Override
	public void close() throws Exception {
		context.close();

	}
}
