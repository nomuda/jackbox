package jackbox.example;

import jackbox.Jackbox;
import jackbox.example.methodannotation.MethodAnnotationDemoService;
import jackbox.persistence.json.JSONPersister;

import java.io.File;
import java.io.IOException;

public class IntegrationExec {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Jackbox.startRecording();

        MethodAnnotationDemoService service = new MethodAnnotationDemoService();
        service.doSomething();

        Jackbox.saveRecording( new JSONPersister(), new File("testrecording.json") );
    }
}
