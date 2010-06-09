package no.muda.jackbox.example.demoapp;

import java.util.List;

import no.muda.jackbox.annotations.Recording;

public class DemoService {

    private DemoDao dao = new DemoDao();

    @Recording
    public int doSomething() {
        System.out.println("In " + getClass());
        List<DemoEntity> entities = dao.findAll();
        for (DemoEntity demoEntity : entities) {
            System.out.println(demoEntity);
        }
        return entities.size();
    }

}
