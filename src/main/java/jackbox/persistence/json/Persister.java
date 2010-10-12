package jackbox.persistence.json;

import java.io.Reader;

import jackbox.MethodRecording;


public interface Persister {
    void persistToWriter(MethodRecording recording, Appendable output);

    MethodRecording readFromReader(Reader input);
}
