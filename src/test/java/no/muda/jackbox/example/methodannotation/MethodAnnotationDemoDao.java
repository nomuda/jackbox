package no.muda.jackbox.example.methodannotation;

import no.muda.jackbox.annotations.Dependency;
import no.muda.jackbox.example.Entity;

import java.util.Arrays;
import java.util.List;

public class MethodAnnotationDemoDao {

    @Dependency
    public List<Entity> findAll() {
        System.out.println("In " + getClass());
        return Arrays.asList(new Entity("A"), new Entity("B"));
    }

}
