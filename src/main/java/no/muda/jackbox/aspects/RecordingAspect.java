package no.muda.jackbox.aspects;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RecordingAspect {

    @Before("   call(void java.io.PrintStream.println(String)) " +
            "&& !within(no.muda.jackbox.aspects.*)")
    public void beforePrintlnCall() {
        System.out.println("About to make call to print Hello World");
    }

    @After("    call(void java.io.PrintStream.println(String)) " +
            "&&  !within(no.muda.jackbox.aspects.*)")
    public void afterPrintlnCall() {
        System.out.println("Just made call to print Hello World");
    }


//    @Before("  call(@no.muda.jackbox.annotations.Recording)")
//    public void beforeMethod() {
//        System.out.println("Yay: Before");
//    }
//
//    @After("  call(@no.muda.jackbox.annotations.Recording) *")
//    public void afterPrintlnCall() {
//        System.out.println("Done: After");
//    }
}
