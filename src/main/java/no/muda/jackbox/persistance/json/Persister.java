package no.muda.jackbox.persistance.json;

import java.io.Reader;

import no.muda.jackbox.MethodRecording;


public interface Persister {
    void persistToWriter(MethodRecording recording, Appendable output);

    MethodRecording readFromReader(Reader input);
}
