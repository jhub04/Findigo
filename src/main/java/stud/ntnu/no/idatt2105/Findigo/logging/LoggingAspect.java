package stud.ntnu.no.idatt2105.Findigo.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Before("execution(* stud.ntnu.no.idatt2105.Findigo.service.*.*(..))")
  public void logBefore(JoinPoint joinPoint) {
    logger.debug("Executing: {}", joinPoint.getSignature().toShortString());
  }

  @AfterReturning(value = "execution(* stud.ntnu.no.idatt2105.Findigo.service.*.*(..))", returning = "result")
  public void logAfter(JoinPoint joinPoint, Object result) {
    logger.info("Method {} returned: {}", joinPoint.getSignature().toShortString(), result);
  }

  @AfterThrowing(value = "execution(* stud.ntnu.no.idatt2105.Findigo.service.*.*(..))", throwing = "ex")
  public void logError(JoinPoint joinPoint, Exception ex) {
    logger.error("Exception in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
  }
}
