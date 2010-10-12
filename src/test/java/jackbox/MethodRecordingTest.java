package jackbox;

import jackbox.example.ExampleDependency;
import jackbox.example.ExampleRecordedObject;
import jackbox.example.classannotation.ClassAnnotationDemoService;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.fest.assertions.Assertions.assertThat;

public class MethodRecordingTest {
    private Method method;
    private MethodRecording recording;

    private MethodRecording dependencyMethodCall;
    private MethodRecording dependencyMethodCallOtherParameters;
    private Method dependencyMethod1;
    private MethodRecording dependencyMethodCall2;
    private Method dependencyMethod2;

    private MethodRecording demoMethodCall;
    private Method demoMethod;

    @Before
    public void methodSetup() throws NoSuchMethodException {
        method = ExampleRecordedObject.class.getMethod("exampleMethod", Integer.TYPE, Integer.TYPE);

        recording = new MethodRecording(ExampleRecordedObject.class, method, new Object[] { 5, 6 });
        recording.setReturnValue(11);

        dependencyMethod1 = ExampleDependency.class.getMethod("invokedMethodOnDependency", String.class);
        dependencyMethodCall = new MethodRecording(ExampleDependency.class, dependencyMethod1, new Object[] {"test"});
        dependencyMethodCall.setReturnValue("testreturn");
        dependencyMethodCallOtherParameters = new MethodRecording(ExampleDependency.class, dependencyMethod1, new Object[] {"test2"});
        dependencyMethodCallOtherParameters.setReturnValue("testreturn2");

        dependencyMethod2 = ExampleDependency.class.getMethod("anotherTestMethodWithoutArgumentsOrReturnValue");
        dependencyMethodCall2 = new MethodRecording(ExampleDependency.class, dependencyMethod2, new Object[] {});

        demoMethod = ClassAnnotationDemoService.class.getMethod("doSomething");
        demoMethodCall = new MethodRecording(ClassAnnotationDemoService.class, demoMethod, new Object[]{});
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

    @Test
    public void recordOneDependency() {
        recording.addDependencyMethodCall(dependencyMethodCall);

        DependencyRecording recorded = recording.getDependencyRecording(dependencyMethodCall.getTargetClass());
        assertThat(recorded.getMethodRecordings(dependencyMethod1)[0]).isEqualTo(dependencyMethodCall);
    }

    @Test
    public void recordTwoDependencyRecordingsOnDifferentClasses() {
        recording.addDependencyMethodCall(dependencyMethodCall);
        recording.addDependencyMethodCall(demoMethodCall);

        DependencyRecording recorded = recording.getDependencyRecording(dependencyMethodCall.getTargetClass());
        assertThat(recorded.getMethodRecordings(dependencyMethod1)[0]).isEqualTo(dependencyMethodCall);

        recorded = recording.getDependencyRecording(demoMethodCall.getTargetClass());
        assertThat(recorded.getMethodRecordings(demoMethod)[0]).isEqualTo(demoMethodCall);
    }

    @Test
    public void recordTwoDependencyRecordingsOnSameClassDifferentMethods() {
        recording.addDependencyMethodCall(dependencyMethodCall);
        recording.addDependencyMethodCall(dependencyMethodCall2);

        DependencyRecording recorded = recording.getDependencyRecording(dependencyMethodCall.getTargetClass());
        assertThat(recorded.getMethodRecordings(dependencyMethod1)[0]).isEqualTo(dependencyMethodCall);

        recorded = recording.getDependencyRecording(dependencyMethodCall2.getTargetClass());
        assertThat(recorded.getMethodRecordings(dependencyMethod2)[0]).isEqualTo(dependencyMethodCall2);
    }

    @Test
    public void recordTwoDependencyRecordingsOnSameMethod() {
        recording.addDependencyMethodCall(dependencyMethodCall);
        recording.addDependencyMethodCall(dependencyMethodCallOtherParameters);

        DependencyRecording recorded = recording.getDependencyRecording(dependencyMethodCall.getTargetClass());
        assertThat(recorded.getMethodRecordings(dependencyMethod1)).isEqualTo(new MethodRecording[]{dependencyMethodCall,
            dependencyMethodCallOtherParameters});
    }
}
