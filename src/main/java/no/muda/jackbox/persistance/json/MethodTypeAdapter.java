package no.muda.jackbox.persistance.json;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MethodTypeAdapter implements JsonSerializer<Method>, JsonDeserializer<Method> {
    public JsonElement serialize(Method src, Type typeOfSrc, JsonSerializationContext context) {
	    String declaringClassName = src.getDeclaringClass().getCanonicalName();
	    Class<?>[] parameters = src.getParameterTypes();

	    JsonObject obj = new JsonObject();
	    obj.addProperty("classname", declaringClassName);
	    obj.addProperty("name", src.getName());
	    obj.add("parameters", getJsonArrayForParameters(parameters));

	    return obj;
	}

	private JsonArray getJsonArrayForParameters(Class<?>[] parameters) {
		JsonArray parametersArray = new JsonArray();
		for (Class<?> p : parameters) {
			parametersArray.add(new JsonPrimitive(p.getCanonicalName()));
		}
		return parametersArray;
	}

	private Class<?>[] getParametersFromJsonArray(JsonArray array) throws ClassNotFoundException {
		List<Class<?>> parameters = new LinkedList<Class<?>>();

		for (int i = 0; i < array.size(); i++) {
			parameters.add( Class.forName(array.get(i).getAsString()) );
		}
		return (Class<?>[]) parameters.toArray();
	}

	public Method deserialize(JsonElement element, Type typeOfSrc,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = element.getAsJsonObject();
			String className = obj.getAsJsonPrimitive("classname").getAsString();
			Class<?>[] parameters;
			try {
				parameters = getParametersFromJsonArray(obj.get("parameters").getAsJsonArray());
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}

			String methodName = obj.getAsJsonPrimitive("name").getAsString();

			Method method;
			try {
				method = Class.forName(className).getMethod(methodName, parameters);
			} catch (SecurityException e) {
				throw new JsonParseException(e);
			} catch (NoSuchMethodException e) {
				throw new JsonParseException(e);
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
			return method;
		}
}
