package no.muda.jackbox.example;

import no.muda.jackbox.annotations.Recording;

public class ExampleRecordedObject {

    private ExampleDependency exampleDependency;

    @Recording
    public int exampleMethod(int parameter, int parameter2) {
        return parameter + parameter2;
    }

    public void setDependency(ExampleDependency exampleDependency) {
        this.exampleDependency = exampleDependency;
    }

    @Recording
    public String exampleMethodThatDelegatesToDependency(String argument) {
        return exampleDependency.invokedMethodOnDependency(argument);
    }

    @Recording
    public void callRecordingMethodInSameClass() {
        exampleMethod(1, 2);
    }
}
