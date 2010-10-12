package jackbox;

import jackbox.example.ExampleDependency;
import jackbox.example.ExampleRecordedObject;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class JackboxReplayTest {

    private MethodRecording recordedExampleMethodWith2And2;

    @Before
    public void setupRecording() throws Exception {
        recordedExampleMethodWith2And2 =
            new MethodRecording(ExampleRecordedObject.class,
                    ExampleRecordedObject.class.getMethod("exampleMethod", Integer.TYPE, Integer.TYPE),
                    new Object[] { 2, 2 });
    }

    @Test
    public void shouldNotThrowExceptionWhenInvocationIsUnchanged() throws Exception {
        recordedExampleMethodWith2And2.setReturnValue(4);
        recordedExampleMethodWith2And2.replay();
    }

    @Test
    public void shouldThrowExceptionIfReturnValueChanges() throws Exception {
        recordedExampleMethodWith2And2.setReturnValue(5);

        boolean threwException = false;
        try {
            recordedExampleMethodWith2And2.replay();
        } catch (AssertionError e) {
            assertThat(e.getMessage())
                .contains("expected <5>")
                .contains("got <4>")
                .contains("exampleMethod");
            threwException = true;
        }

        assertThat(threwException).describedAs("Should throw when return changes").isTrue();
    }

    @Test
    public void shouldThrowExceptionIfRecordedExceptionIsNotThrown() throws Exception {
        MethodRecording methodRecording = new MethodRecording(ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodThatThrowsException", boolean.class),
                new Object[] {false});
        methodRecording.setExceptionThrown(new IllegalArgumentException());

        boolean threwException = false;
        try {
            methodRecording.replay();
        } catch (AssertionError e) {
            assertThat(e.getMessage())
                .contains("expected throwing of <java.lang.IllegalArgumentException>")
                .contains("no exception")
                .contains("methodThatThrowsException");
            // @TODO: include parameters in Assertion too
            threwException = true;
        }

        assertThat(threwException).describedAs("Should throw when thrown exception changes").isTrue();
    }

    @Test
    public void shouldThrowExceptionIfThrowingAnExceptionWhenNotRecorded() throws Exception {
        MethodRecording methodRecording = new MethodRecording(ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodThatThrowsException", boolean.class),
                new Object[] {true});

        boolean threwException = false;
        try {
            methodRecording.replay();
        } catch (AssertionError e) {
            assertThat(e.getMessage())
                .contains("expected throwing of <no exception>")
                .contains("got <java.lang.IllegalArgumentException>")
                .contains("methodThatThrowsException");
            // @TODO: include parameters in Assertion too
            threwException = true;
        }

        assertThat(threwException).describedAs("Should throw when thrown exception changes").isTrue();
    }

    @Test
    public void shouldThrowExceptionIfThrowingAnotherExceptionThanRecorded() throws Exception {
        MethodRecording methodRecording = new MethodRecording(ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("methodThatThrowsException", boolean.class),
                new Object[] {true});
        methodRecording.setExceptionThrown(new NullPointerException());

        boolean threwException = false;
        try {
            methodRecording.replay();
        } catch (AssertionError e) {
            assertThat(e.getMessage())
                .contains("expected throwing of <java.lang.NullPointerException>")
                .contains("got <java.lang.IllegalArgumentException>")
                .contains("methodThatThrowsException");
            // @TODO: include parameters in Assertion too
            threwException = true;
        }

        assertThat(threwException).describedAs("Should throw when thrown exception changes").isTrue();
    }

    @Test
    public void shouldReplayDelegatedObject() throws Exception {
        String recordedReturnValueFromDependencyMethod = "foo bar baz";

        MethodRecording methodRecording = new MethodRecording(ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("exampleMethodThatDelegatesToDependency", String.class),
                new Object[] { "abcd" });
        methodRecording.setReturnValue(recordedReturnValueFromDependencyMethod);

        MethodRecording dependencyMethodRecording = new MethodRecording(ExampleDependency.class,
                ExampleDependency.class.getMethod("invokedMethodOnDependency", String.class),
                new Object[] { "abcd" });
        dependencyMethodRecording.setReturnValue(recordedReturnValueFromDependencyMethod);
        methodRecording.addDependencyMethodCall(dependencyMethodRecording);

        methodRecording.replay();
    }

    @Test
    public void shouldReplayDelegatedObjectThrowingExceptionThrough() throws Exception {
        MethodRecording methodRecording = new MethodRecording(ExampleRecordedObject.class,
                ExampleRecordedObject.class.getMethod("exampleMethodThatCallsExceptionThrowingMethodInDependency", boolean.class, boolean.class),
                new Object[] {true, false});
        methodRecording.setExceptionThrown(new IllegalArgumentException());

        MethodRecording dependencyMethodRecording = new MethodRecording(ExampleDependency.class,
                ExampleDependency.class.getMethod("methodThatThrowsException", boolean.class),
                new Object[] {true});
        dependencyMethodRecording.setExceptionThrown(new IllegalArgumentException());
        methodRecording.addDependencyMethodCall(dependencyMethodRecording);

        methodRecording.replay();
    }
}
