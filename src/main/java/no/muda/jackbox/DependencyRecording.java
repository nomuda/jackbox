package no.muda.jackbox;

import java.util.HashMap;
import java.util.Map;


public class DependencyRecording {

    private final Class<?> dependencyClass;

    // TODO: Create test that forces Map<String, List<MethodRecording>>
    private Map<String, MethodRecording> methodRecordings = new HashMap<String, MethodRecording>();

    public DependencyRecording(Class<?> dependencyClass) {
        this.dependencyClass = dependencyClass;
    }

    public MethodRecording getMethodRecording(String methodName) {
        return methodRecordings.get(methodName);
    }

    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    public void addMethodRecording(MethodRecording methodRecording) {
        methodRecordings.put(methodRecording.getMethodName(), methodRecording);
    }

}
