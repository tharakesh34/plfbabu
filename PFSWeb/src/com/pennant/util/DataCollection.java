package com.pennant.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanUtils;

import com.aspose.words.IMailMergeDataSource;

public class DataCollection implements IMailMergeDataSource {
	public DataCollection(Collection<?> collection, String tableName) {
		this.collection = collection;
		this.tableName = tableName;

		iterator = this.collection.iterator();
	}

	@Override
	public IMailMergeDataSource getChildDataSource(String filedName) throws Exception {
		Method[] methods = bean.getClass().getDeclaredMethods();
		for (Method property : methods) {
			if (property.getName().startsWith("get")) {
				String field = property.getName().substring(3);
				Object value = null;
				try {
					if (filedName.equalsIgnoreCase(field)) {
						value = property.invoke(bean);
					} else {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
				if (value != null) {
					if (value instanceof Collection<?>) {
						return new DataCollection((Collection<?>) value, field);
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getTableName() throws Exception {
		return tableName;
	}

	@Override
	public boolean getValue(String arg0, Object[] arg1) throws Exception {
		Object value = BeanUtils.getProperty(bean, arg0);

		if (value != null) {
			arg1[0] = value;
			return true;
		}

		return false;
	}

	@Override
	public boolean moveNext() throws Exception {
		if (iterator.hasNext()) {
			bean = iterator.next();
			return true;
		}

		return false;
	}

	protected Collection<?> collection;
	protected String tableName;
	protected Iterator<?> iterator;
	protected Object bean;
}
