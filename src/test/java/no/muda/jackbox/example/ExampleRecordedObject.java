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

    public void exampleMethodThatDelegatesToDependency(String argument) {
        exampleDependency.invokedMethodOnDependency(argument);
    }

}
