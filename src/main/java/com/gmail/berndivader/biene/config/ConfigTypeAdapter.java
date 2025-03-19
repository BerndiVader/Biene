package com.gmail.berndivader.biene.config;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ConfigTypeAdapter<T> implements JsonDeserializer<T>,JsonSerializer<T>{
	
	private final Class<T>clazz;
	
	public ConfigTypeAdapter(Class<T>clazz) {
		this.clazz=clazz;
	}

	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		return new Gson().toJsonTree(src);
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return applyDefaultValues(new Gson().fromJson(json,clazz));
	}
	
	private T applyDefaultValues(T object) {
		for(Field field:clazz.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				if(field.get(object)==null&&field.isAnnotationPresent(DefaultValue.class)) {
					DefaultValue defaultValue=field.getAnnotation(DefaultValue.class);
					Object value=parse(field.getType(),defaultValue.value());
					field.set(object,value);
				}
			} catch(Exception e) {
				throw new RuntimeException("Fehler beim setzen der Default-Values in Config",e);
			}
		}
		return object;
	}
	
	private Object parse(Class<?>type,String value) {
		if(type.equals(int.class)) {
			return Integer.parseInt(value);
		} else if(type.equals(boolean.class)) {
			return Boolean.parseBoolean(value);
		} else {
			return value;
		}
	}

}
