package jackbox.persistence.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class MethodTypeAdapterTest {
    private Method testMethod;
    private Gson gsonWithAdapter;

    @Test
    public void testSerialize() {
        assertThat(gsonWithAdapter.toJson(testMethod)).contains(testMethod.getName());
    }

    @Test
    public void testSerializeAndDeserialize() {
        Method parsedMethod = gsonWithAdapter.fromJson(gsonWithAdapter.toJson(testMethod), Method.class);
        assertThat(parsedMethod).isEqualTo(testMethod);
    }

    @Test
    public void testDeserializeWithoutAdapterImpossible() {
        Gson gson = new Gson();
        String serialized = gson.toJson(testMethod);
        try {
            gson.fromJson(serialized, Method.class);
            fail("Deserializing without adapter should give exception (else adaptor no longer needeed\"");
        } catch (Exception e) {
        }
    }

    @Before
    public void initTestMethod() {
        testMethod = getClass().getMethods()[0];
        gsonWithAdapter = new GsonBuilder().registerTypeAdapter(Method.class, new MethodTypeAdapter()).create();
    }

}
