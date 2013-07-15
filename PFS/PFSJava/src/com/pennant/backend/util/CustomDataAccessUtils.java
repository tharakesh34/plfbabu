/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  CustomDataAccessUtils.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

public final class CustomDataAccessUtils {
	private final static class Array2ListTransformer<T> {
		private final BeanSetterClass[] beanSetter;
		private final ObjectFactory<T> factory;

		Array2ListTransformer(Class<T> beanClass, String... args) {
			factory = new ObjectFactory<T>(beanClass);
			beanSetter = new BeanSetterClass[args.length];
			for (int i = 0; i < args.length; i++) {
				String beanName = convertToSetterName(args[i]);
				beanSetter[i] = new BeanSetterClass(beanClass, beanName);
			}
		}

		Array2ListTransformer(Class<T> beanClass, String[] args, Class<?>[] argsClass) {
			if (!ArrayUtils.isSameLength(args, argsClass)) {
				throw new RuntimeException("?");
			}
			factory = new ObjectFactory<T>(beanClass);
			beanSetter = new BeanSetterClass[args.length];
			for (int i = 0; i < args.length; i++) {
				String beanName = convertToSetterName(args[i]);
				beanSetter[i] = new BeanSetterClass(beanClass, beanName, argsClass[i]);
			}
		}

		List<T> transfer2Bean(Collection<?> col) {
			List<T> result = new ArrayList<T>(col.size());
			for (Object row1 : col) {
				T bean = factory.create();
				Object[] row = (Object[]) row1;
				for (int j = 0; j < row.length; j++) {
					beanSetter[j].invocSetter(bean, row[j]);
				}
				result.add(bean);
			}
			return result;
		}

		List<T> transfer2Bean(Object[] array) {
			return transfer2Bean(Arrays.asList(array));
		}
	}

	private final static class BeanSetterClass {
		final private Method method;

		BeanSetterClass(Class<?> beanClass, String beanName) {
			Method[] methods = beanClass.getMethods();
			Method methodTmp = null;
			for (Method method1 : methods) {
				if (method1.getName().equals(beanName)) {
					methodTmp = method1;
					break;
				}
			}
			if (methodTmp == null) {
				throw new RuntimeException("Methode " + beanName + " nicht gefunden!");
			}
			method = methodTmp;
		}

		BeanSetterClass(Class<?> beanClass, String beanName, Class<?> clazz) {
			try {
				method = beanClass.getMethod(beanName, clazz);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		void invocSetter(Object obj, Object value) {
			try {
				method.invoke(obj, value);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private final static class ObjectFactory<T> {
		private final Class<T> clazz;

		ObjectFactory(Class<T> clazz) {
			super();
			this.clazz = clazz;
		}

		T create() {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

	}

	static String convertToSetterName(String propertyName) {
		return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}

	public static <T> List<T> transfer2Bean(Collection<?> col, Class<T> beanClass, String... args) {
		if (CollectionUtils.isEmpty(col)) {
			return Collections.emptyList();
		}

		return new Array2ListTransformer<T>(beanClass, args).transfer2Bean(col);
	}

	public static <T> List<T> transfer2Bean(Collection<?> col, Class<T> beanClass, String[] args, Class<?>[] argsClass) {
		if (CollectionUtils.isEmpty(col)) {
			return Collections.emptyList();
		}

		return new Array2ListTransformer<T>(beanClass, args, argsClass).transfer2Bean(col);
	}

	public static <T> List<T> transfer2Bean(Object[] array, Class<T> beanClass, String... args) {
		if (array == null || array.length == 0) {
			return Collections.emptyList();
		}

		return new Array2ListTransformer<T>(beanClass, args).transfer2Bean(array);
	}

	public static <T> List<T> transfer2Bean(Object[] array, Class<T> beanClass, String[] args, Class<?>[] argsClass) {
		if (array == null || array.length == 0) {
			return Collections.emptyList();
		}

		return new Array2ListTransformer<T>(beanClass, args).transfer2Bean(array);
	}
}
