package no.muda.jackbox;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class DependencyRecording {

    private final Class<?> dependencyClass;

    // TODO: Create test that forces Map<String, Queue<MethodRecording>>
    private Map<Method, MethodRecording> methodRecordings = new HashMap<Method, MethodRecording>();

    public DependencyRecording(Class<?> dependencyClass) {
        this.dependencyClass = dependencyClass;
    }

    public MethodRecording getMethodRecording(String methodName) {
        for (Entry<Method, MethodRecording> entry : methodRecordings.entrySet()) {
            if (entry.getKey().getName().equals(methodName)) return entry.getValue();
        }
        return null;
    }

    public Map<Method, MethodRecording> getMethodRecordings() {
        return methodRecordings;
    }

    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    public void addMethodRecording(MethodRecording methodRecording) {
        methodRecordings.put(methodRecording.getMethod(), methodRecording);
    }

    public void setMethodRecordings(Map<Method, MethodRecording> recordings) {
        methodRecordings = recordings;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((dependencyClass == null) ? 0 : dependencyClass.hashCode());
        result = prime
                * result
                + ((methodRecordings == null) ? 0 : methodRecordings.hashCode());
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
        DependencyRecording other = (DependencyRecording) obj;
        if (dependencyClass == null) {
            if (other.dependencyClass != null)
                return false;
        } else if (!dependencyClass.equals(other.dependencyClass))
            return false;
        if (methodRecordings == null) {
            if (other.methodRecordings != null)
                return false;
        } else if (!methodRecordings.equals(other.methodRecordings))
            return false;
        return true;
    }
}
