package no.muda.jackbox;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.muda.jackbox.aspects.RecordingAspect;
import no.muda.jackbox.example.ExampleDependency;


@SuppressWarnings("unchecked")
public class MethodRecording {

    private List arguments;
    private Object returnValue;
    private Map<Class<?>, DependencyRecording> dependencyRecordings
           = new HashMap<Class<?>, DependencyRecording>();
    private final Class<?> klass;
    private final Method method;

    public MethodRecording(Class<?> klass, Method method, List arguments) {
        this.klass = klass;
        this.method = method;
        this.arguments = arguments;
    }

    public Method getMethod() {
        return method;
    }

    public Object getRecordedResult() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public List getArguments() {
        return arguments;
    }

    public DependencyRecording getDependencyRecording(Class<?> dependencyClass) {
        return dependencyRecordings.get(dependencyClass);
    }

    private void addDependencyRecording(DependencyRecording dependencyRecording) {
        this.dependencyRecordings.put(
                dependencyRecording.getDependencyClass(),
                dependencyRecording);
    }

    public void replay() throws Exception {
        Object replayInstance = klass.newInstance();

        RecordingAspect.setReplayingRecording(this);
        Object replayedResult = getMethod().invoke(replayInstance, arguments.toArray());
        RecordingAspect.clearReplayingRecording();

        if (!nullSafeEquals(replayedResult, getRecordedResult())) {
            throw new AssertionError("When replaying " + getMethod()
                    + " expected <" + getRecordedResult() + "> got <" +
                    replayedResult + ">");
        }
    }

    private boolean nullSafeEquals(Object replayedResult, Object recordedResult) {
        return replayedResult != null ? replayedResult.equals(recordedResult) : recordedResult == null;
    }

    public void addDependencyMethodCall(MethodRecording dependencyMethodRecording) {
        DependencyRecording dependencyRecording = new DependencyRecording(ExampleDependency.class);
        dependencyRecording.addMethodRecording(dependencyMethodRecording);
        addDependencyRecording(dependencyRecording);
    }

    public MethodRecording getDependencyMethodRecording(Method dependentMethod) {
        DependencyRecording dependencyRecording = dependencyRecordings.get(dependentMethod.getDeclaringClass());
        return dependencyRecording.getMethodRecording(dependentMethod.getName());
    }

}
