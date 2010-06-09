package no.muda.jackbox;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;

import no.muda.jackbox.example.demoapp.DemoApp;
import no.muda.jackbox.persistance.json.JSONPersister;

import org.junit.Before;
import org.junit.Test;

public class FullExampleTest {

    private ByteArrayOutputStream sysout;

    @Test
    public void shouldCallDaoDuringRecording() throws Exception {
        DemoApp.main(new String[0]);
        assertThat(getSysout()).contains("DemoDao");
    }

    @Test
    public void shouldNotCallDaoDuringPlayback() throws Exception {
        DemoApp.main(new String[0]);
        MethodRecording recording = JackboxRecorder.getLastCompletedRecording();

        JSONPersister persister = new JSONPersister();
        StringWriter serializedRecording = new StringWriter();
        persister.persistToWriter(recording, serializedRecording);

        captureSysout();
        recording = persister.readFromReader(new StringReader(serializedRecording.toString()));

        recording.replay();
        assertThat(getSysout())
            .contains("DemoService")
            .excludes("DemoDao");
    }

    private String getSysout() {
        return new String(sysout.toByteArray());
    }

    @Before
    public void captureSysout() {
        sysout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysout));
    }

}
