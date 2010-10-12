package jackbox.example;

public class Entity {

    private String name;

    Entity() {
    }

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entity)) return false;
        return ((Entity)obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Entity<" + name + ">";
    }
}

