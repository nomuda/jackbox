package no.muda.jackbox.persistence.json;

import java.lang.reflect.Method;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class MethodTypeAdapterTest {
    @Test
    public void testSerializeWithoutAdapterImpossible() {
        Method testMethod = getTestMethod();

        Gson gson = new Gson();
        try {
            gson.toJson(testMethod, Method.class);
            fail("Serializing without adapter should give exception (else adaptor no longer needeed");
        }
        catch (Exception expected) {
        }
    }

    private Method getTestMethod() {
        Method testMethod = getClass().getMethods()[0];
        return testMethod;
    }

    @Test
    public void testSerialize() {
        Method testMethod = getTestMethod();
        Gson gson = getGsonWithAdapter();

        String result = gson.toJson(testMethod);
        assertThat(result).contains(testMethod.getName());
    }

    private Gson getGsonWithAdapter() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Method.class, new MethodTypeAdapter()).create();
        return gson;
    }

    @Test
    public void testSerializeAndDeserialize() {
        Method testMethod = getTestMethod();
        Gson gson = getGsonWithAdapter();

        String result = gson.toJson(testMethod);
        Method parsedMethod = gson.fromJson(result, Method.class);
        assertThat(parsedMethod).isEqualTo(testMethod);
    }
}
