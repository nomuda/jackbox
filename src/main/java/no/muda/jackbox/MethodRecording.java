package no.muda.jackbox;

import no.muda.jackbox.aspects.JackboxAspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;

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

        JackboxAspect.setReplayingRecording(this);
        Object replayedResult = getMethod().invoke(replayInstance, arguments);
        JackboxAspect.clearReplayingRecording();

        if (!nullSafeEquals(replayedResult, getRecordedResult())) {
            throw new AssertionError("When replaying " + getMethod()
                    + " expected <" + getRecordedResult() + "> got <" +
                    replayedResult + ">");
        }
    }

    public void addDependencyMethodCall(MethodRecording dependencyMethodRecording) {
        Class<?> dependencyClass = dependencyMethodRecording.getMethod().getDeclaringClass();
        DependencyRecording dependencyRecording = getDependencyRecording(dependencyClass);
        if (dependencyRecording == null) dependencyRecording = new DependencyRecording(dependencyClass);
        dependencyRecording.addMethodRecording(dependencyMethodRecording);
        addDependencyRecording(dependencyRecording);
    }

    public MethodRecording[] getDependencyMethodRecordings(Method dependentMethod) {
        DependencyRecording dependencyRecording = dependencyRecordings.get(dependentMethod.getDeclaringClass());
        return dependencyRecording.getMethodRecordings(dependentMethod.getName());
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodRecording)) return false;

        MethodRecording other = (MethodRecording) obj;
        return ArrayUtils.isEquals(arguments, other.arguments) &&
            nullSafeEquals(targetClass, other.targetClass) &&
            nullSafeEquals(method, other.method) &&
            ArrayUtils.isEquals(returnValue, other.returnValue);
    }

    private<T> boolean nullSafeEquals(T a, T b) {
        return ObjectUtils.equals(a, b);
    }

    private<T> boolean nullSafeEquals(T[] a, T[] b) {
        return Arrays.equals(a, b);
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
            for (Queue<MethodRecording> methodRecordingQueue : dependencyRecording.getMethodRecordingQueues().values()) {
                result.addAll(methodRecordingQueue);
            }
        }
        return result;
    }

}
