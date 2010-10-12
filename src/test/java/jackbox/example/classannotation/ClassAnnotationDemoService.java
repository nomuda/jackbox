package jackbox.example.classannotation;

import jackbox.annotations.Recording;
import jackbox.example.Entity;

import java.util.List;

@Recording
public class ClassAnnotationDemoService {

    private ClassAnnotationDemoDao dao = new ClassAnnotationDemoDao();

    public int doSomething() {
        System.out.println("In " + getClass());
        List<Entity> entities = dao.findAll();
        for (Entity entity : entities) {
            System.out.println(entity);
        }
        return entities.size();
    }

}