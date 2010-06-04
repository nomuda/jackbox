package no.muda.jackbox.example;

import no.muda.jackbox.annotations.Dependency;

public class ExampleDependency {

    @Dependency
    public String invokedMethodOnDependency(String argument) {
        return argument.toUpperCase();
    }
}
