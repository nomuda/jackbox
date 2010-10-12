package jackbox.persistence.json;

import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import jackbox.MethodRecording;

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

public class JSONPersister implements Persister {
    public void persistToWriter(MethodRecording recording, Appendable output) {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(
                MethodRecording.class, new MethodRecordingTypeAdaptor())
                .registerTypeAdapter(Method.class, new MethodTypeAdapter())
                .create();
        gson.toJson(recording, output);
    }

    public MethodRecording readFromReader(Reader input) {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                MethodRecording.class, new MethodRecordingTypeAdaptor())
                .registerTypeAdapter(Method.class, new MethodTypeAdapter())
                .create();

        return gson.fromJson(input, MethodRecording.class);
    }
}

class MethodRecordingTypeAdaptor implements JsonSerializer<MethodRecording>,
        JsonDeserializer<MethodRecording> {

    public JsonElement serialize(MethodRecording src, Type typeOfSrc,
            JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("classname", src.getTargetClass().getCanonicalName());
        obj.add("method", context.serialize(src.getMethod()));
        obj.add("arguments", context.serialize(Arrays
                .asList(src.getArguments())));

        if (src.getExceptionThrown() != null) {
            Class<? extends Throwable> exceptionClass = IllegalArgumentException.class; // src.getExceptionThrown().getClass()
            obj.addProperty("thrownexceptionclass", exceptionClass.getCanonicalName());
//            obj.add("thrownexception", context.serialize(src.getExceptionThrown(), exceptionClass));
        }
        else if (src.getMethod().getReturnType() != Void.TYPE) {
            obj.add("returnvalue", context.serialize(src.getRecordedResult(),
                    src.getMethod().getGenericReturnType()));
        }

        obj.add("dependencies", context.serialize(src
                .getDependencyMethodRecordings()));

        return obj;
    }

    public MethodRecording deserialize(JsonElement element, Type typeOfSrc,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = element.getAsJsonObject();
        Class<?> clazz = getTargetClass(obj);

        Method method = context.deserialize(obj.get("method"), Method.class);

        Object returnValue;
        Throwable exceptionThrown;

        JsonElement exceptionClassElement = obj.get("thrownexceptionclass");
        if (exceptionClassElement != null) {
            Class<? extends Throwable> exceptionClass;
            try {
                exceptionClass = (Class<? extends Throwable>) Class.forName(exceptionClassElement.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
  //          exceptionThrown = context.deserialize(obj.get("thrownexception"), exceptionClass);
            try {
                exceptionThrown = exceptionClass.newInstance();
            } catch (InstantiationException e) {
                throw new JsonParseException(e);
            } catch (IllegalAccessException e) {
                throw new JsonParseException(e);
            }
            returnValue = null;
        }
        else {
            exceptionThrown = null;
            if (method.getReturnType() != Void.TYPE) {
                returnValue = context.deserialize(obj.get("returnvalue"), method
                        .getGenericReturnType());
            } else
            returnValue = null;
        }

        JsonArray jsonArguments = (JsonArray) obj.get("arguments");
        Object[] arguments = new Object[method.getParameterTypes().length];
        for (int i = 0; i < jsonArguments.size(); i++) {
            arguments[i] = context.deserialize(jsonArguments.get(i), method
                    .getGenericParameterTypes()[i]);
        }

        MethodRecording methodRecording = new MethodRecording(clazz, method,
                arguments);
        methodRecording.setReturnValue(returnValue);
        methodRecording.setExceptionThrown(exceptionThrown);

        JsonArray jsonArray = (JsonArray) obj.get("dependencies");
        for (JsonElement dependencyMethodRecording : jsonArray) {
            methodRecording.addDependencyMethodCall((MethodRecording) context
                    .deserialize(dependencyMethodRecording,
                            MethodRecording.class));
        }

        return methodRecording;
    }

    private Class<?> getTargetClass(JsonObject obj) {
        try {
            return Class.forName(obj.getAsJsonPrimitive("classname")
                    .getAsString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
