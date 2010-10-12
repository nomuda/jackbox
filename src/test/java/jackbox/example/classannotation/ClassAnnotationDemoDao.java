package jackbox.example.classannotation;

import jackbox.annotations.Dependency;
import jackbox.example.Entity;

import java.util.Arrays;
import java.util.List;

@Dependency
public class ClassAnnotationDemoDao {
    
    public List<Entity> findAll() {
        System.out.println("In " + getClass());
        return Arrays.asList(new Entity("A"), new Entity("B"));
    }

}