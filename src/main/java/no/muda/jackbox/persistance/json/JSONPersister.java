package no.muda.jackbox.persistance.json;

import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import no.muda.jackbox.DependencyRecording;
import no.muda.jackbox.MethodRecording;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JSONPersister implements Persister {
    public String persistToString(Object recording) {
		Gson gson = new GsonBuilder()
		    .registerTypeAdapter(DependencyRecording.class, new DependencyRecordingTypeAdaptor())
			.registerTypeAdapter(MethodRecording.class, new MethodRecordingTypeAdaptor())
			.registerTypeAdapter(Method.class, new MethodTypeAdapter())
			.create();
        return gson.toJson(recording);
    }

    public void persistToWriter(MethodRecording recording, Appendable output) {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(DependencyRecording.class, new DependencyRecordingTypeAdaptor())
            .registerTypeAdapter(MethodRecording.class, new MethodRecordingTypeAdaptor())
            .registerTypeAdapter(Method.class, new MethodTypeAdapter())
            .create();
        gson.toJson(recording, output);
    }

    public MethodRecording readFromReader(Reader input) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(DependencyRecording.class, new DependencyRecordingTypeAdaptor())
            .registerTypeAdapter(MethodRecording.class, new MethodRecordingTypeAdaptor())
            .registerTypeAdapter(Method.class, new MethodTypeAdapter())
            .create();

        return gson.fromJson(input, MethodRecording.class);
    }
}

class MethodRecordingTypeAdaptor implements JsonSerializer<MethodRecording>, JsonDeserializer<MethodRecording> {

    public JsonElement serialize(MethodRecording src, Type typeOfSrc, JsonSerializationContext context) {
		  JsonObject obj = new JsonObject();
		  obj.addProperty("classname", src.getTargetClass().getCanonicalName());
		  obj.add("method", context.serialize(src.getMethod()));
		  obj.add("arguments",
		          context.serialize(Arrays.asList(src.getArguments())));
		  obj.add("returnvalue", context.serialize(src.getRecordedResult(),
		          src.getMethod().getGenericReturnType()));
		  obj.add("dependencies", context.serialize(src.getDependencyMethodRecordings()));

		  return obj;
	  }

	  public MethodRecording deserialize(JsonElement element, Type typeOfSrc,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = element.getAsJsonObject();
		Class<?> clazz = getTargetClass(obj);

		Method method = context.deserialize(obj.get("method"), Method.class);
		Object returnValue = context.deserialize(obj.get("returnvalue"),
		        method.getGenericReturnType());
		JsonArray jsonArguments = (JsonArray) obj.get("arguments");
		Object[] arguments = new Object[method.getParameterTypes().length];
		for (int i = 0; i < jsonArguments.size(); i++) {
		    arguments[i] = context.deserialize(jsonArguments.get(i), method.getParameterTypes()[i]);
        }

        MethodRecording methodRecording = new MethodRecording(clazz, method, arguments);
		methodRecording.setReturnValue(returnValue);

		JsonArray jsonArray = (JsonArray) obj.get("dependencies");
        for (JsonElement dependencyMethodRecording : jsonArray) {
            methodRecording.addDependencyMethodCall(
                    (MethodRecording) context.deserialize(dependencyMethodRecording, MethodRecording.class));
        }

		return methodRecording;
	}

    private Class<?> getTargetClass(JsonObject obj) {
        try {
            return Class.forName(obj.getAsJsonPrimitive("classname").getAsString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

class DependencyRecordingTypeAdaptor implements JsonSerializer<DependencyRecording>, JsonDeserializer<DependencyRecording> {
    static final Type methodRecordingsType = new TypeToken<Map<Method, MethodRecording>>() {}.getType();

    public JsonElement serialize(DependencyRecording src, Type typeOfSrc, JsonSerializationContext context) {
		  JsonObject obj = new JsonObject();
		  obj.addProperty("classname", src.getClass().getCanonicalName());

		  obj.add( "methodrecordings", context.serialize(src.getMethodRecordings(), methodRecordingsType ) );
		  return obj;
	  }

	  public DependencyRecording deserialize(JsonElement element, Type typeOfSrc,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = element.getAsJsonObject();
		String className = obj.getAsJsonPrimitive("classname").getAsString();


		JsonObject methodRecordingsJson = obj.getAsJsonObject("methodrecordings");

		Map<Method, MethodRecording> methodrecordings =
			context.deserialize(methodRecordingsJson, methodRecordingsType);

		try {
			DependencyRecording depRecording = new DependencyRecording(Class.forName(className));
			depRecording.setMethodRecordings(methodrecordings);
			return depRecording;
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}
}
