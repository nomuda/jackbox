package no.muda.jackbox.persistance.json;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;

import no.muda.jackbox.DependencyRecording;
import no.muda.jackbox.MethodRecording;
import no.muda.jackbox.example.ExampleDependency;
import no.muda.jackbox.example.ExampleRecordedObject;

import org.junit.Test;

public class JSONPersistanceTest {
    final static String testArgumentValue = "abcd";
    final static String testArgumentReturnValue = "efgh";

    @Test
    public void testPersistToString() throws Exception {
        DependencyRecording recording = getTestRecording();

        Persister persister = new JSONPersister();
        String result = persister.persistToString(recording);

        assertThat(result).contains("invokedMethodOnDependency")
                .contains(testArgumentValue)
                .contains(testArgumentReturnValue);
    }

    private DependencyRecording getTestRecording() throws NoSuchMethodException {
        DependencyRecording recording = new DependencyRecording(ExampleRecordedObject.class);
        Method testMethod = ExampleDependency.class.getMethod("invokedMethodOnDependency", String.class);
        MethodRecording testMethodrecording = new MethodRecording(ExampleDependency.class, testMethod, Arrays.asList(testArgumentValue));
        testMethodrecording.setReturnValue(testArgumentReturnValue);
        recording.addMethodRecording(testMethodrecording);

        return recording;
    }
}
