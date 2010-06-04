package no.muda.jackbox;

public class JackboxRecorder {

    private static MethodRecording lastCompletedRecording;

    public static MethodRecording getLastCompletedRecording() {
        return lastCompletedRecording;
    }

    public static void addRecording(MethodRecording methodRecording) {
        lastCompletedRecording = methodRecording;
    }

}
