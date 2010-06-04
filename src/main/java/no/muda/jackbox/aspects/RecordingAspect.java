package no.muda.jackbox.aspects;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class RecordingAspect {

    @Before("    within(@no.muda.jackbox.annotations.Recording *)")
    public void beforeClassRecording() {
        System.out.println("Before class");
    }

    @Before("    call(@no.muda.jackbox.annotations.Recording * *(..))")
    public void beforeMethodRecording() {
        System.out.println("Before method");
    }


    @After("    call(@no.muda.jackbox.annotations.Recording * *(..))")
    public void afterMethodRecording() {
        System.out.println("After method");
    }

}
