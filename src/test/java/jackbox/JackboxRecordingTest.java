package jackbox;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import jackbox.example.ExampleDependency;
import jackbox.example.ExampleRecordedObject;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JackboxRecordingTest {

    private static ClassLoader originalClassLoader;

    private ExampleRecordedObject recordedObject = new ExampleRecordedObject();

    @BeforeClass
    public static void setupClassloader() {
        originalClassLoader = Thread.currentThread().getContextClassLoader();
        // TODO: Runtime weaving!!!
        //Thread.currentThread().setContextClassLoader(new RecordingClassLoader());
    }

    @AfterClass
    public static void resetClassloader() {
        Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    @Before
    public void startRecording() {
        JackboxRecorder.startRecording();
    }

    @Test
    public void shouldRecordMethodCall() throws Exception {
        int actualReturnedValue = recordedObject.exampleMethod(2, 3);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        assertExampleMethodCall(2, 3, actualReturnedValue, recording);
    }

    @Test
    public void shouldRecordMethodCallThatCanThrowException() {
        recordedObject.methodThatThrowsException(false);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        assertExceptionNotThrownRegisteredCorrect(recording);
    }

    @Test
    public void shouldRecordMethodCallThatThrowsException() {
        try {
            recordedObject.methodThatThrowsException(true);
            fail("Exception should have been thrown here.");
        }
        catch (IllegalArgumentException expected) {}

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();
        assertExceptionThrowingRegistered(recording, "methodThatThrowsException");
    }

    @Test
    public void shouldRecordDependencyCallThatCanThrowException() throws SecurityException, NoSuchMethodException {
        ExampleDependency exampleDependency = new ExampleDependency();
        recordedObject.setDependency(exampleDependency);

        recordedObject.exampleMethodThatCallsExceptionThrowingMethodInDependency(false, false);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();
        Method invokedMethodOnDependency = ExampleDependency.class.getMethod("methodThatThrowsException", boolean.class);
        MethodRecording dependentRecording =
            recording.getDependencyMethodRecordings(invokedMethodOnDependency)[0];
        assertExceptionNotThrownRegisteredCorrect(dependentRecording);
    }

    @Test
    public void shouldRecordDependencyCallThatThrowsException() throws SecurityException, NoSuchMethodException {
        ExampleDependency exampleDependency = new ExampleDependency();
        recordedObject.setDependency(exampleDependency);
        recordedObject.exampleMethodThatCallsExceptionThrowingMethodInDependency(true, true);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();
        Method invokedMethodOnDependency = ExampleDependency.class.getMethod("methodThatThrowsException", boolean.class);
        MethodRecording dependentRecording =
            recording.getDependencyMethodRecordings(invokedMethodOnDependency)[0];
        assertExceptionThrowingRegistered(dependentRecording, "methodThatThrowsException");
    }

    @Test
    public void shouldRecordDependencyCallThatThrowsExceptionThrough() throws SecurityException, NoSuchMethodException {
        ExampleDependency exampleDependency = new ExampleDependency();
        recordedObject.setDependency(exampleDependency);

        try {
            recordedObject.exampleMethodThatCallsExceptionThrowingMethodInDependency(true, false);
            fail("Exception should have been thrown here.");
        }
        catch (IllegalArgumentException expected) {}

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();
        Method invokedMethodOnDependency = ExampleDependency.class.getMethod("methodThatThrowsException", boolean.class);
        MethodRecording dependentRecording =
            recording.getDependencyMethodRecordings(invokedMethodOnDependency)[0];
        assertExceptionThrowingRegistered(dependentRecording, "methodThatThrowsException");

        assertExceptionThrowingRegistered(recording, "exampleMethodThatCallsExceptionThrowingMethodInDependency");
    }

    private void assertExceptionThrowingRegistered(MethodRecording recording, String methodname) {
        assertThat(recording.getMethod().getName()).isEqualTo(methodname);
        assertThat(recording.getArguments()).contains(true);
        assertThat(recording.getRecordedResult())
            .describedAs("Should get null as return value when exception thrown").isNull();
        assertThat(recording.getExceptionThrown()).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertExceptionNotThrownRegisteredCorrect(
            MethodRecording recording) {
        assertThat(recording.getMethod().getName()).isEqualTo("methodThatThrowsException");
        assertThat(recording.getArguments()).containsOnly(false);
        assertThat(recording.getExceptionThrown()).isNull();
    }

    @Test
    public void shouldRecordDependency() throws Exception {
        ExampleDependency exampleDependency = new ExampleDependency();
        recordedObject.setDependency(exampleDependency);

        String delegatedArgument = "abcd";
        recordedObject.exampleMethodThatDelegatesToDependency(delegatedArgument);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        Method invokedMethodOnDependency = ExampleDependency.class.getMethod("invokedMethodOnDependency", String.class);
        MethodRecording dependentRecording =
            recording.getDependencyMethodRecordings(invokedMethodOnDependency)[0];

        assertThat(dependentRecording.getArguments()).containsOnly(delegatedArgument);
        assertThat(dependentRecording.getRecordedResult()).isEqualTo("ABCD");
    }

    @Test
    public void shouldRecordMultipleMethodCalls() throws Exception {
        int actualReturnedValue = recordedObject.exampleMethod(2, 3);
        int actualReturnedValue2 = recordedObject.exampleMethod(4, 5);
        MethodRecording[] recordings = JackboxRecorder.getAllRecordings();

        assertExampleMethodCall(2, 3, actualReturnedValue, recordings[0]);
        assertExampleMethodCall(4, 5, actualReturnedValue2, recordings[1]);
    }

    private void assertExampleMethodCall(int param1, int param2, int actualReturnedValue,
            MethodRecording recording) {
        assertThat(recording.getMethod().getName()).isEqualTo("exampleMethod");
        assertThat(recording.getArguments()).containsOnly(param1, param2);
        assertThat(recording.getRecordedResult()).isEqualTo(actualReturnedValue);
    }
}
