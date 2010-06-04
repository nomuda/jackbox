package no.muda.jackbox.persistance.json;

import no.muda.jackbox.DependencyRecording;

public interface Persister {
	String persistToString(DependencyRecording recording);
}
