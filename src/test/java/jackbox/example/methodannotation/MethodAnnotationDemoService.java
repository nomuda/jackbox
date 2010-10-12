package jackbox.example.methodannotation;

import jackbox.annotations.Recording;
import jackbox.example.Entity;

import java.util.List;

public class MethodAnnotationDemoService {

    private MethodAnnotationDemoDao dao = new MethodAnnotationDemoDao();

    @Recording
    public int doSomething() {
        System.out.println("In " + getClass());
        List<Entity> entities = dao.findAll();
        for (Entity entity : entities) {
            System.out.println(entity);
        }
        return entities.size();
    }

}
