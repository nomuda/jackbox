package jackbox;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;


public class DependencyRecording {

    private final Class<?> dependencyClass;

    private Map<Method, Queue<MethodRecording>> methodRecordings = new HashMap<Method, Queue<MethodRecording>>();

    public DependencyRecording(Class<?> dependencyClass) {
        this.dependencyClass = dependencyClass;
    }

    public MethodRecording[] getMethodRecordings(Method method) {
        Queue<MethodRecording> recordings = methodRecordings.get(method);
        return recordings == null ? null : recordings.toArray(new MethodRecording[]{});
    }

    public MethodRecording[] getMethodRecordings(String methodName) {
        for (Entry<Method, Queue<MethodRecording>> entry : methodRecordings.entrySet()) {
            if (entry.getKey().getName().equals(methodName)) return entry.getValue().toArray(new MethodRecording[]{});
        }
        return null;
    }

    public Map<Method, Queue<MethodRecording>> getMethodRecordingQueues() {
        return methodRecordings;
    }

    public Class<?> getDependencyClass() {
        return dependencyClass;
    }

    public void addMethodRecording(MethodRecording methodRecording) {
        Method method = methodRecording.getMethod();

        Queue<MethodRecording> recordingsQueue = methodRecordings.get(method);
        if (getMethodRecordings(method) == null) {
             recordingsQueue = new LinkedList<MethodRecording>();
            methodRecordings.put(method, recordingsQueue);
        }
        recordingsQueue.add(methodRecording);
    }

    @Override
    public String toString() {
        return "DependencyRecording [dependencyClass=" + dependencyClass
                + ", methodRecordings=" + methodRecordings + "]";
    }
}
