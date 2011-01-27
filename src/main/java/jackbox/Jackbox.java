package jackbox;

import jackbox.persistence.json.Persister;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Jackbox {

    public static void startRecording() {
        JackboxRecorder.startRecording();
    }

    public static void saveRecording(Persister persister, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);

        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();
        persister.persistToWriter(recording, fileWriter);
        fileWriter.close();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Put command line interface here?
    }
}
