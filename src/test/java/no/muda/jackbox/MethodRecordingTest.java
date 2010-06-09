package no.muda.jackbox;

import no.muda.jackbox.example.ExampleRecordedObject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.fest.assertions.Assertions.assertThat;

public class MethodRecordingTest {
    private Method method;
    private MethodRecording recording;

    @Before
    public void methodSetup() throws NoSuchMethodException {
        method = ExampleRecordedObject.class.getMethod("exampleMethod", Integer.TYPE, Integer.TYPE);
        recording = new MethodRecording(ExampleRecordedObject.class, method, new Object[] { 5, 6 });
        recording.setReturnValue(11);
    }

    @Test
    public void shouldUseHashCodeOfRecordedMethod() throws NoSuchMethodException {
        assertThat(recording.hashCode()).isEqualTo(method.hashCode());
    }

    @Test
    public void shouldHaveTheMostImportantValuesInItsToString() {
        assertThat(recording.toString())
                .contains("exampleMethod(int,int)")
                .contains("arguments=[5, 6]")
                .contains("returnValue=11");
    }
}
