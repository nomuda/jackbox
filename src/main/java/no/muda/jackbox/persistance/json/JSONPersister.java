package no.muda.jackbox.persistance.json;

import no.muda.jackbox.DependencyRecording;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONPersister implements Persister {
    public String persistToString(DependencyRecording recording) {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(recording);
    }
}
