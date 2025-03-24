package stud.ntnu.no.idatt2105.Findigo.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect-oriented logging for service methods in the application.
 *
 * <p>This aspect logs method execution details for all service-layer methods
 * in the {@code stud.ntnu.no.idatt2105.Findigo.service} package.</p>
 */
@Component
@Aspect
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  /**
   * Logs method execution before the method is invoked.
   *
   * <p>Logs the method signature at the debug level.</p>
   *
   * @param joinPoint provides details about the method being executed.
   */
  @Before("execution(* stud.ntnu.no.idatt2105.Findigo.service.*.*(..))")
  public void logBefore(JoinPoint joinPoint) {
    logger.debug("Executing: {}", joinPoint.getSignature().toShortString());
  }

  /**
   * Logs method return values after successful execution.
   *
   * <p>Logs the method signature and returned value at the info level.</p>
   *
   * @param joinPoint provides details about the method being executed.
   * @param result the return value of the method.
   */
  @AfterReturning(value = "execution(* stud.ntnu.no.idatt2105.Findigo.service.*.*(..))", returning = "result")
  public void logAfter(JoinPoint joinPoint, Object result) {
    logger.info("Method {} returned: {}", joinPoint.getSignature().toShortString(), result);
  }

  /**
   * Logs exceptions thrown by service methods.
   *
   * <p>Logs the method signature and exception message at the error level.</p>
   *
   * @param joinPoint provides details about the method where the exception occurred.
   * @param ex the exception that was thrown.
   */
  @AfterThrowing(value = "execution(* stud.ntnu.no.idatt2105.Findigo.service.*.*(..))", throwing = "ex")
  public void logError(JoinPoint joinPoint, Exception ex) {
    logger.error("Exception in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
  }
}
