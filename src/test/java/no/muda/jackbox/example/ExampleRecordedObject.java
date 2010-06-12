package no.muda.jackbox.example;

import java.util.Arrays;
import java.util.List;

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

    public Entity methodWithEntity(Entity entity) {
        return entity;
    }

    public List<Integer> methodWithPatameterizedReturnValue() {
        return Arrays.asList(1, 2);
    }

    public void methodWithParameterizedArgument(List<Integer> argument) {
    }
    

    public void methodWithoutReturnValue() {
    }

    public void methodWithArrayOfPrimitivesArgument(int arg[]) {
    }
}
