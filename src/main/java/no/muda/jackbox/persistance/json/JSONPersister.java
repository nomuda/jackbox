package no.muda.jackbox.persistance.json;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import no.muda.jackbox.DependencyRecording;
import no.muda.jackbox.MethodRecording;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class JSONPersister implements Persister {
    public String persistToString(DependencyRecording recording) {
		Gson gson = new GsonBuilder().registerTypeAdapter(DependencyRecording.class, new DependencyRecordingTypeAdaptor())
			.registerTypeAdapter(MethodRecording.class, new MethodRecordingTypeAdaptor())
			.registerTypeAdapter(Method.class, new MethodTypeAdapter())
			.create();
        return gson.toJson(recording);
    }
}

class MethodRecordingTypeAdaptor implements JsonSerializer<MethodRecording>, JsonDeserializer<MethodRecording> {

    public JsonElement serialize(MethodRecording src, Type typeOfSrc, JsonSerializationContext context) {
		  JsonObject obj = new JsonObject();
		  obj.addProperty("classname", src.getClass().getCanonicalName());
		  obj.add("method", context.serialize(src.getMethod()));
		  List returnValues = Arrays.asList(src.getRecordedResult());
		  obj.add("arguments", context.serialize(src.getArguments()) );
		  obj.add("returnvalues", context.serialize(returnValues));

		  return obj;
	  }

	  public MethodRecording deserialize(JsonElement element, Type typeOfSrc,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = element.getAsJsonObject();
		String className = obj.getAsJsonPrimitive("classname").getAsString();

		List returnValues = context.deserialize(obj.get("returnvalues"), List.class);
		List arguments = context.deserialize(obj.get("arguments"), List.class);

		Method method = context.deserialize(obj.get("method"), Method.class);

		try {
			MethodRecording metRecording = new MethodRecording(Class.forName(className), method, arguments);
			if (returnValues.size() > 0) metRecording.setReturnValue(returnValues.get(0));
			return metRecording;
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
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
