package br.com.kotar.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectUtil {

	public static Object[] getPropertyName(String propertyName, Object object) throws InstantiationException, IllegalAccessException {

		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(JsonProperty.class)) {

				JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
				if (jsonProperty.value().equalsIgnoreCase(propertyName)) {
					propertyName = field.getName();

					field.setAccessible(true);

					Object nO = field.getType().newInstance();
					return new Object[] { propertyName, nO };
				}
			}
		}

		Method[] methods = object.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(JsonProperty.class)) {
				JsonProperty jsonProperty = method.getAnnotation(JsonProperty.class);
				if (jsonProperty.value().equalsIgnoreCase(propertyName)) {
					String methodName = method.getName();
					methodName = methodName.substring(3);
					methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

					propertyName = methodName;
					return new Object[] { propertyName, null };

				}
			}
		}
		
		for (Field field : fields) {
			
			if (field.getName().equalsIgnoreCase(propertyName)) {
				propertyName = field.getName();

				field.setAccessible(true);

				Object nO = field.getType().newInstance();
				return new Object[] { propertyName, nO };
			}
			
		}

		return null;
	}
	
}
