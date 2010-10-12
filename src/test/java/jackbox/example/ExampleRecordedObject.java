package jackbox.example;

import java.util.Arrays;
import java.util.List;

import jackbox.annotations.Recording;

public class ExampleRecordedObject {

    private ExampleDependency exampleDependency;

    @Recording
    public int exampleMethod(int parameter, int parameter2) {
        return parameter + parameter2;
    }

    public void setDependency(ExampleDependency exampleDependency) {
        this.exampleDependency = exampleDependency;
    }

    /**
     * 
     * @param throwException
     * @param catchException
     * @return true if exception was catched
     */
    @Recording
    public boolean exampleMethodThatCallsExceptionThrowingMethodInDependency(boolean throwException, boolean catchException) {
        try {
            exampleDependency.methodThatThrowsException(throwException);
            return false;
        }
        catch (IllegalArgumentException e) {
            if (catchException) return true;
            else {
                throw e;
            }
        }
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

    public void methodWithArrayOfPrimitivesArgument(int arg[]) {}
    public void methodWithArrayArgument(Integer arg[]) {}
    public short[] methodWithArrayReturnValue() {
        return new short[]{1, 2, 3};
    }

    @Recording
    public void methodThatThrowsException(boolean throwException) throws IllegalArgumentException {
        if (throwException) throw new IllegalArgumentException("Was told to throw exception");
    }
}
