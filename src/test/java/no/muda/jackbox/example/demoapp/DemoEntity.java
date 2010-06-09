package no.muda.jackbox.example.demoapp;


public class DemoEntity {

    private String name;

    DemoEntity() {
    }

    public DemoEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DemoEntity<" + name + ">";
    }
}
