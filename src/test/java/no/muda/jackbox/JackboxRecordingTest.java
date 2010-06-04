package no.muda.jackbox;

import static org.fest.assertions.Assertions.assertThat;
import no.muda.jackbox.example.ExampleDependency;
import no.muda.jackbox.example.ExampleRecordedObject;

import org.junit.Test;

public class JackboxRecordingTest {

    @Test
    public void shouldRecordMethodCall() throws Exception {
        ExampleRecordedObject recordedObject = new ExampleRecordedObject();
        int actualReturnedValue = recordedObject.exampleMethod(2, 3);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        assertThat(recording.getMethodName()).isEqualTo("exampleMethod");
        assertThat(recording.getArguments()).containsExactly(2, 3);
        assertThat(recording.getReturnValue())
            .isEqualTo(actualReturnedValue);
    }

    @Test
    public void shouldRecordDependency() throws Exception {
        ExampleDependency exampleDependency = new ExampleDependency();
        ExampleRecordedObject recordedObject = new ExampleRecordedObject();
        recordedObject.setDependency(exampleDependency);

        String delegatedArgument = "abcd";
        recordedObject.exampleMethodThatDelegatesToDependency(delegatedArgument);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        DependencyRecording dependencyRecording = recording.getDependencyRecording(ExampleDependency.class);
        MethodRecording dependentRecording =
            dependencyRecording.getMethodRecording("invokedMethodOnDependency");

        assertThat(dependentRecording.getArguments()).containsExactly(delegatedArgument);
        assertThat(dependentRecording.getReturnValue())
            .isEqualTo(exampleDependency.invokedMethodOnDependency(delegatedArgument));
    }
}
