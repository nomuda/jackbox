package no.muda.jackbox;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;

import no.muda.jackbox.example.demoapp.DemoApp;
import no.muda.jackbox.persistance.json.JSONPersister;

import org.junit.Test;

public class FullExampleTest {

    @Test
    public void shouldCallDaoDuringRecording() throws Exception {
        ByteArrayOutputStream sysout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysout));

        DemoApp.main(new String[0]);
        assertThat(new String(sysout.toByteArray())).contains("DemoDao");
    }

    @Test
    public void shouldNotCallDaoDuringPlayback() throws Exception {
        DemoApp.main(new String[0]);
        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        JSONPersister persister = new JSONPersister();
        StringWriter serializedRecording = new StringWriter();
        persister.persistToWriter(recording, serializedRecording);

        System.out.println(serializedRecording);

        recording = persister.readFromReader(new StringReader(serializedRecording.toString()));

        ByteArrayOutputStream sysout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysout));
        recording.replay();
        assertThat(new String(sysout.toByteArray()))
            .contains("DemoService")
            .excludes("DemoDao");
    }

}
