package no.muda.jackbox.example.demoapp;

import java.util.Arrays;
import java.util.List;

import no.muda.jackbox.annotations.Dependency;

public class DemoDao {

    @Dependency
    public List<DemoEntity> findAll() {
        System.out.println("In " + getClass());
        return Arrays.asList(new DemoEntity("A"), new DemoEntity("B"));
    }

}
