package no.muda.jackbox.example;

public class ExampleEntity {

    private String name;

    ExampleEntity() {
    }

    public ExampleEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExampleEntity)) return false;
        return ((ExampleEntity)obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "ExampleEntity<" + name + ">";
    }
}

