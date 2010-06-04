package no.muda.jackbox.example;

import no.muda.jackbox.annotations.Recording;

public class ExampleRecordedObject {

    private ExampleDependency exampleDependency;

    @Recording
    public int exampleMethod(Object parameter, Object parameter2) {
        return 0;
    }

    public void setDependency(ExampleDependency exampleDependency) {
        this.exampleDependency = exampleDependency;
    }

    public void exampleMethodThatDelegatesToDependency(String argument) {
    }

}
