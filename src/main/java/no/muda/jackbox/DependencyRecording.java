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

    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    public void addMethodRecording(MethodRecording methodRecording) {
        methodRecordings.put(methodRecording.getMethod(), methodRecording);
    }

}
