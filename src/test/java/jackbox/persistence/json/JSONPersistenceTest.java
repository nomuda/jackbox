package jackbox.persistence.json;

import jackbox.MethodRecording;
import jackbox.example.Entity;
import jackbox.example.ExampleDependency;
import jackbox.example.ExampleRecordedObject;
import org.junit.Test;

import com.google.gson.JsonParseException;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class JSONPersistenceTest {

    final static String testArgumentValue = "abcd";
    final static String testArgumentReturnValue = "efgh";

    @Test
    public void shouldContainRecordedMethod() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("exampleMethod", Integer.TYPE, Integer.TYPE),
                new Object[] { 5, 6 });
        recording.setReturnValue(11);

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording.getTargetClass()).isEqualTo(recording.getTargetClass());
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistMethodWithoutReturnValue() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodWithoutReturnValue"),
                new Object[]{});
        recording.setReturnValue(null);

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistUserProvidedObjects() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodWithEntity", Entity.class),
                new Object[] { new Entity("foo") });
        recording.setReturnValue(new Entity("bar"));

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistParameterizedReturnValue() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodWithPatameterizedReturnValue"),
                new Object[]{});
        recording.setReturnValue(Arrays.asList(1, 2));

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistParameterizedArguments() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodWithParameterizedArgument", List.class),
                new Object[] { Arrays.asList(1, 2) });
        recording.setReturnValue(null);

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistArrayOfPrimitivesArgument() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodWithArrayOfPrimitivesArgument", int[].class),
                new Object[] { new int[]{1, 2, 3} });
        recording.setReturnValue(null);

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistArrayArgument() throws Exception {
        MethodRecording recording = new MethodRecording(
        ExampleRecordedObject.class,
        ExampleRecordedObject.class.getMethod("methodWithArrayArgument", Integer[].class),
          new Object[] { new Integer[]{1, 2, 3} });
        recording.setReturnValue(null);

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistArrayReturnValue() throws Exception {
        MethodRecording recording = new MethodRecording(
        ExampleRecordedObject.class,
        ExampleRecordedObject.class.getMethod("methodWithArrayReturnValue", new Class[]{}),
          new Object[] {});
        recording.setReturnValue(new short[]{2, 3, 4});

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldPersistExceptionThrown() throws Exception {
        MethodRecording recording = new MethodRecording(
        ExampleRecordedObject.class,
        ExampleRecordedObject.class.getMethod("methodThatThrowsException", new Class[]{boolean.class}),
          new Object[] {true});
        recording.setExceptionThrown(new IllegalArgumentException());

        MethodRecording readRecording = persistAndRestore(recording);
        assertThat(readRecording).isEqualTo(recording);
    }

    @Test
    public void shouldSerializeDependencies() throws Exception {
        String recordedReturnValueFromDependencyMethod = "ABCD";

        MethodRecording recording = new MethodRecording(ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("exampleMethodThatDelegatesToDependency", String.class),
                new Object[] { "abcd" });
        recording.setReturnValue(recordedReturnValueFromDependencyMethod);

        Method invokedMethodOnDependency = ExampleDependency.class.getMethod("invokedMethodOnDependency", String.class);
        MethodRecording dependencyMethodRecording = new MethodRecording(ExampleDependency.class,
                invokedMethodOnDependency,
                new Object[] { "abcd" });

        dependencyMethodRecording.setReturnValue(recordedReturnValueFromDependencyMethod);
        recording.addDependencyMethodCall(dependencyMethodRecording);

        MethodRecording readRecording = persistAndRestore(recording);

        assertThat(readRecording).isEqualTo(recording);
        assertThat(readRecording.getDependencyMethodRecordings(invokedMethodOnDependency))
            .isEqualTo(recording.getDependencyMethodRecordings(invokedMethodOnDependency));
    }

    private MethodRecording persistAndRestore(MethodRecording recording) {
        return persistAndRestore(recording, false);
    }

    private MethodRecording persistAndRestore(MethodRecording recording, boolean debug) {
        Persister persister = new JSONPersister();
        StringWriter output = new StringWriter();
        persister.persistToWriter(recording, output);
        if (debug) System.out.println("Generated: " + output);
        try {
            MethodRecording readRecording = persister.readFromReader(new StringReader(output.toString()));
            return readRecording;
        }
        catch (JsonParseException e) {
            throw new IllegalStateException("Generated JSON failed to parse: " + output, e);
        }
    }
}
