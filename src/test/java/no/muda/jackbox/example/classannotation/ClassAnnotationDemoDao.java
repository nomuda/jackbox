package no.muda.jackbox.example.classannotation;

import no.muda.jackbox.annotations.Dependency;
import no.muda.jackbox.example.Entity;

import java.util.Arrays;
import java.util.List;

@Dependency
public class ClassAnnotationDemoDao {
    
    public List<Entity> findAll() {
        System.out.println("In " + getClass());
        return Arrays.asList(new Entity("A"), new Entity("B"));
    }

}