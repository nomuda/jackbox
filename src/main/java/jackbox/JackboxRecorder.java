package jackbox;

import java.util.LinkedList;
import java.util.List;

public class JackboxRecorder {

    private static List<MethodRecording> methodRecordings = new LinkedList<MethodRecording>();
    private static MethodRecording lastCompletedRecording;

    public static MethodRecording getLastCompletedRecording() {
        return lastCompletedRecording;
    }

    public static void addRecording(MethodRecording methodRecording) {
        methodRecordings.add(methodRecording);
        lastCompletedRecording = methodRecording;
    }

    public static MethodRecording[] getAllRecordings() {
        return methodRecordings.toArray(new MethodRecording[]{});
    }


    public static void startRecording() {
        clearRecordings();
    }

    private static void clearRecordings() {
        methodRecordings.clear();
        lastCompletedRecording = null;
    }

}
