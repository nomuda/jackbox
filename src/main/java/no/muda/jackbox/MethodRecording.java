package no.muda.jackbox;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.muda.jackbox.aspects.RecordingAspect;

public class MethodRecording {

    private Object[] arguments;
    private Object returnValue;
    private Map<Class<?>, DependencyRecording> dependencyRecordings
           = new HashMap<Class<?>, DependencyRecording>();
    private final Class<?> targetClass;
    private final Method method;

    public MethodRecording(Class<?> klass, Method method, Object[] arguments) {
        this.targetClass = klass;
        this.method = method;
        this.arguments = arguments;
    }

    public Class<?> getTargetClass() {
        return targetClass;
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

    public Object[] getArguments() {
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
        Object replayInstance = targetClass.newInstance();

        RecordingAspect.setReplayingRecording(this);
        Object replayedResult = getMethod().invoke(replayInstance, arguments);
        RecordingAspect.clearReplayingRecording();

        if (!nullSafeEquals(replayedResult, getRecordedResult())) {
            throw new AssertionError("When replaying " + getMethod()
                    + " expected <" + getRecordedResult() + "> got <" +
                    replayedResult + ">");
        }
    }

    public void addDependencyMethodCall(MethodRecording dependencyMethodRecording) {
        Class<?> dependencyClass = dependencyMethodRecording.getMethod().getDeclaringClass();
        DependencyRecording dependencyRecording = new DependencyRecording(dependencyClass);
        dependencyRecording.addMethodRecording(dependencyMethodRecording);
        addDependencyRecording(dependencyRecording);
    }

    public MethodRecording getDependencyMethodRecording(Method dependentMethod) {
        DependencyRecording dependencyRecording = dependencyRecordings.get(dependentMethod.getDeclaringClass());
        return dependencyRecording.getMethodRecording(dependentMethod.getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((arguments == null) ? 0 : arguments.hashCode());
        result = prime
                * result
                + ((dependencyRecordings == null) ? 0 : dependencyRecordings
                        .hashCode());
        result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result
                + ((returnValue == null) ? 0 : returnValue.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodRecording)) return false;

        MethodRecording other = (MethodRecording) obj;
        return nullSafeEquals(arguments, other.arguments) &&
            nullSafeEquals(dependencyRecordings, other.dependencyRecordings) &&
            nullSafeEquals(targetClass, other.targetClass) &&
            nullSafeEquals(method, other.method) &&
            nullSafeEquals(returnValue, other.returnValue);
    }

    private<T> boolean nullSafeEquals(T a, T b) {
        return a != null ? a.equals(b) : b == null;
    }

    private<T> boolean nullSafeEquals(T[] a, T[] b) {
        return a != null ? Arrays.asList(a).equals(Arrays.asList(b)) : b == null;
    }

    @Override
    public String toString() {
        return "MethodRecording [arguments=" + Arrays.asList(arguments)
                + ", dependencyRecordings=" + dependencyRecordings + ", klass="
                + targetClass + ", method=" + method + ", returnValue=" + returnValue
                + "]";
    }

    public List<MethodRecording> getDependencyMethodRecordings() {
        List<MethodRecording> result = new ArrayList<MethodRecording>();
        for (DependencyRecording dependencyRecording : dependencyRecordings.values()) {
            result.addAll(dependencyRecording.getMethodRecordings().values());
        }
        return result;
    }

}
