package jackbox.example;

import jackbox.annotations.Dependency;

public class ExampleDependency {

    @Dependency
    public String invokedMethodOnDependency(String argument) {
        return argument.toUpperCase();
    }

    @Dependency
    public void anotherTestMethodWithoutArgumentsOrReturnValue() {
    }

    @Dependency
    public void methodThatThrowsException(boolean throwException) throws IllegalArgumentException {
        if (throwException) throw new IllegalArgumentException("Was told to throw exception");
    }
}
