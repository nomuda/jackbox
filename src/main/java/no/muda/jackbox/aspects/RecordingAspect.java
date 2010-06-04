package no.muda.jackbox.aspects;

import java.util.Arrays;
import java.util.List;

import no.muda.jackbox.DependencyRecording;
import no.muda.jackbox.JackboxRecorder;
import no.muda.jackbox.MethodRecording;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class RecordingAspect {

    private ThreadLocal<MethodRecording> ongoingRecording = new ThreadLocal<MethodRecording>();

    // TODO: Match with annotation on class or method, not just method
    @Around("    call(@no.muda.jackbox.annotations.Recording * *(..))")
    public Object captureRecordedClass(ProceedingJoinPoint thisPointCut) throws Throwable {
        MethodRecording recording = createMethodRecording(thisPointCut);

        if (ongoingRecording.get() != null) {
            throw new IllegalStateException("Don't want to override ongoingRecording");
        }
        ongoingRecording.set(recording);
        Object result = thisPointCut.proceed();
        ongoingRecording.set(null);

        recording.setReturnValue(result);
        JackboxRecorder.addRecording(recording);

        return result;
    }

    // TODO: Match with annotation on class or method, not just method
    @Around("call(@no.muda.jackbox.annotations.Dependency * *(..))")
    public Object captureDependencies(ProceedingJoinPoint thisPointCut) throws Throwable {
        DependencyRecording dependencyRecording = new DependencyRecording(thisPointCut.getSignature().getDeclaringType());
        ongoingRecording.get().addDependencyRecording(dependencyRecording);

        MethodRecording methodRecording = createMethodRecording(thisPointCut);
        dependencyRecording.addMethodRecording(methodRecording);

        Object result = thisPointCut.proceed();
        methodRecording.setReturnValue(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private MethodRecording createMethodRecording(ProceedingJoinPoint thisPointCut) {
        Class targetClass = thisPointCut.getSignature().getDeclaringType();
        String methodName = thisPointCut.getSignature().getName();
        List arguments = Arrays.asList(thisPointCut.getArgs());
        return new MethodRecording(targetClass, methodName, arguments);
    }
}
