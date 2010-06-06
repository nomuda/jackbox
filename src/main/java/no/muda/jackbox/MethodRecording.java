package no.muda.jackbox;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.muda.jackbox.aspects.RecordingAspect;

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
        result = prime * result + ((klass == null) ? 0 : klass.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result
                + ((returnValue == null) ? 0 : returnValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodRecording other = (MethodRecording) obj;
        if (arguments == null) {
            if (other.arguments != null)
                return false;
        } else if (!arguments.equals(other.arguments))
            return false;
        if (dependencyRecordings == null) {
            if (other.dependencyRecordings != null)
                return false;
        } else if (!dependencyRecordings.equals(other.dependencyRecordings))
            return false;
        if (klass == null) {
            if (other.klass != null)
                return false;
        } else if (!klass.equals(other.klass))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        if (returnValue == null) {
            if (other.returnValue != null)
                return false;
        } else if (!returnValue.equals(other.returnValue))
            return false;
        return true;
    }

}
