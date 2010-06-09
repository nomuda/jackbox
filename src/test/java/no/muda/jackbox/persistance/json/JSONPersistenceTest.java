package no.muda.jackbox.persistance.json;

import static org.fest.assertions.Assertions.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;

import no.muda.jackbox.MethodRecording;
import no.muda.jackbox.example.ExampleDependency;
import no.muda.jackbox.example.ExampleEntity;
import no.muda.jackbox.example.ExampleRecordedObject;

import org.junit.Test;

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
    public void shouldPersistUserProvidedObjects() throws Exception {
        MethodRecording recording = new MethodRecording(
                ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodWithEntity", ExampleEntity.class),
                new Object[] { new ExampleEntity("foo") });
        recording.setReturnValue(new ExampleEntity("bar"));

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
        assertThat(readRecording.getDependencyMethodRecording(invokedMethodOnDependency))
            .isEqualTo(recording.getDependencyMethodRecording(invokedMethodOnDependency));
    }

    private MethodRecording persistAndRestore(MethodRecording recording) {
        Persister persister = new JSONPersister();
        StringWriter output = new StringWriter();
        persister.persistToWriter(recording, output);
        MethodRecording readRecording = persister.readFromReader(new StringReader(output.toString()));
        return readRecording;
    }

}
