package jackbox.example.methodannotation;

import jackbox.annotations.Dependency;
import jackbox.example.Entity;

import java.util.Arrays;
import java.util.List;

public class MethodAnnotationDemoDao {

    @Dependency
    public List<Entity> findAll() {
        System.out.println("In " + getClass());
        return Arrays.asList(new Entity("A"), new Entity("B"));
    }

}
