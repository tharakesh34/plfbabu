package com.pennanttech.pennapps.pff.test;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.httpclient.NameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;

@Test
public class ExtendedMapFromPropertiesTest {

	@Test(enabled = true)
	public void getExtendedMapObjectFromJson() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = getJSONResponse();
			assertNotNull(jsonString);

			Map<String, Object> responseMapvalues = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
			assertNotNull(responseMapvalues, "responseMap should not be null");

			Map<String, Object> extendedMap = new HashMap<>(1);
			List<NameValuePair> keyValues = new ArrayList<>(1);			

			Properties properties = new Properties();
			InputStream inputStream = this.getClass().getResourceAsStream("/experianDedup.properties");
			properties.load(inputStream);

			for (final Entry<Object, Object> entry : properties.entrySet()) {
/*				NameValuePair valuePair = new NameValuePair();
				valuePair.setName(entry.getKey());
				valuePair.setValue(entry.getValue());*/
				
				keyValues.add(new NameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
			}

			for (int i = 0; i < keyValues.size(); i++) {
				NameValuePair pair = keyValues.get(i);
				if(responseMapvalues.get(pair.getValue()) instanceof Map) {
					if(responseMapvalues.get(pair.getValue()) != null) {
						@SuppressWarnings("unchecked")
						Map<String, Object> resultMap = (Map<String, Object>) responseMapvalues.get(pair.getValue());
						if(responseMapvalues.containsKey(pair.getValue())) {
							extendedMap.put(pair.getName(), resultMap.get(pair.getValue()));
							keyValues.remove(i);
							i--;
						}
						doSetExtendedMapObject(extendedMap, resultMap, keyValues);
					}
				} else if(responseMapvalues.get(pair.getValue()) instanceof List) {
					if(responseMapvalues.containsKey(pair.getValue())) {
						extendedMap.put(pair.getName(), responseMapvalues.get(pair.getValue()));
						keyValues.remove(i);
						i--;
					}
				} else {
					if(responseMapvalues.containsKey(pair.getValue())) {
						extendedMap.put(pair.getName(), responseMapvalues.get(pair.getValue()));
						keyValues.remove(i);
						i--;
					}
				}
			}
			for(String key:extendedMap.keySet()) {
				System.out.println("Key Value:"+key +" And Value is:"+extendedMap.get(key));
			}
			responseMapvalues = null;
			extendedMap = null;
			keyValues = null;
			inputStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getExtendedMapObjectFromReflection() throws IOException {/*
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonString = getJSONResponse();
			assertNotNull(jsonString);
			
			Map<String, Object> responseMapvalues = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
			assertNotNull(responseMapvalues, "responseMap should not be null");
			
			Map<String, Object> extendedMap = new HashMap<>(1);
			Map<String, Object> keyValues = new HashMap<>(1);
			
			List<NameValuePair> keyValues1 = new ArrayList<>(1);			
			
			Properties properties = new Properties();
			InputStream inputStream = this.getClass().getResourceAsStream("/experianDedup.properties");
			properties.load(inputStream);
			
			for (final Entry<Object, Object> entry : properties.entrySet()) {
				NameValuePair valuePair = new NameValuePair();
				keyValues.put(String.valueOf(entry.getKey()), entry.getValue());
			}
			
			keyValues1.stream().map(p -> {
				keyValues1.remove(p);
                return p;
            });
			
			
			for (int i = 0; i < keyValues.size(); i++) {
				NameValuePair pair = keyValues.get(i);
				if(responseMapvalues.get(pair.getValue()) instanceof Map) {
					if(responseMapvalues.get(pair.getValue()) != null) {
						@SuppressWarnings("unchecked")
						Map<String, Object> resultMap = (Map<String, Object>) responseMapvalues.get(pair.getValue());
						if(responseMapvalues.containsKey(pair.getValue())) {
							extendedMap.put(pair.getName(), resultMap.get(pair.getValue()));
							keyValues.remove(i);
							i--;
						}
						doSetExtendedMapObject(extendedMap, resultMap, keyValues);
					}
				} else if(responseMapvalues.get(pair.getValue()) instanceof List) {
					if(responseMapvalues.containsKey(pair.getValue())) {
						extendedMap.put(pair.getName(), responseMapvalues.get(pair.getValue()));
						keyValues.remove(i);
						i--;
					}
				} else {
					if(responseMapvalues.containsKey(pair.getValue())) {
						extendedMap.put(pair.getName(), responseMapvalues.get(pair.getValue()));
						keyValues.remove(i);
						i--;
					}
				}
			}
			for(String key:extendedMap.keySet()) {
				System.out.println("Key Value:"+key +" And Value is:"+extendedMap.get(key));
			}
			responseMapvalues = null;
			extendedMap = null;
			keyValues = null;
			inputStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	*/}

	@Test(enabled = true)
	public void getFieldsMap() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		InputStream input = null;
		try {
			String inputJson = getJSONResponse();
			Properties properties = new Properties();
			InputStream inputStream = this.getClass().getResourceAsStream("/hunter.properties");
			properties.load(inputStream);
			Enumeration<?> e = properties.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				Object value = JsonPath.read(inputJson, properties.getProperty(key));
				if (value instanceof String) {
					resultMap.put(key, (String) JsonPath.read(inputJson, properties.getProperty(key)));
				} else if (value instanceof Integer) {
					resultMap.put(key, (Integer) JsonPath.read(inputJson, properties.getProperty(key)));
				} else if (value instanceof List) {
					resultMap.put(key, (List) JsonPath.read(inputJson, properties.getProperty(key)));
				} else if (value instanceof Boolean) {
					resultMap.put(key, (Boolean) JsonPath.read(inputJson, properties.getProperty(key)));
				} else if (value instanceof BigDecimal) {
					resultMap.put(key, BigDecimal.valueOf(JsonPath.read(inputJson, properties.getProperty(key))));
				} else {
					resultMap.put(key, JsonPath.read(inputJson, properties.getProperty(key)));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println(resultMap);
	}
	
	
	
	
	
	private void doSetExtendedMapObject(Map<String, Object> extendedMap, Map<String, Object> resultMap,	List<NameValuePair> keyValues) {
			for (int i = 0; i < keyValues.size(); i++) {
				if(resultMap.containsKey(keyValues.get(i).getValue())) {
					extendedMap.put(keyValues.get(i).getName(), resultMap.get(keyValues.get(i).getValue()));
					keyValues.remove(i);
					i--;
				}
			}
	}

	private String getJSONResponse() {
		//return  "{\"statusCode\": 200,\"message\": \"Internal de-dupe processing completed\"}";
		//return  "{\"cif\": \"PC6960\",\"emails\": [{\"custEMailTypeCode\": \"GUARDIAN\",\"custEMail\": \"lakshmi.v@pennanttech.com\",\"custEMailPriority\": 2},{\"custEMailTypeCode\": \"OFFICE\", \"custEMail\": \"lakshmi.v@pennanttech.com\",\"custEMailPriority\": 3}],\"returnStatus\": {\"returnCode\": \"0000\",\"returnText\": \"Success\"}}";
		//return  "{\"cif\": \"PC6960\",\"amount\": 10000000.00,\"match\": true,\"returnStatus\": {\"returnCode\": \"0000\",\"returnText\": \"Success\",\"returnInt\":123658}}";
		return  "{\"statusCode\": 200,\"message\": \"Hunter online matching completed\",\"data\": {\"MatchSummary\": 1,\"TotalMatchScore\": 210,\"Rules\": [ {\"RuleID\": \"NIYO_VEL_ADD\",\"ruleCount\": 4, \"Score\": 30 }, {\"RuleID\": \"NH_NC_PAN\",\"ruleCount\": 1,\"Score\": 90 }],\"MatchSchemes\": [ {\"SchemeID\": 15,\"Score\": 120 }]} } ";
	}
	
	@Test
	public void testCollectionRemove(){
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
			list.remove(i);
			i--;
		}
	}
}
