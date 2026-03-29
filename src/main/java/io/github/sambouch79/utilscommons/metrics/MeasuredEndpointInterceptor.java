package io.github.sambouch79.utilscommons.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepteur CDI qui implémente la logique de {@link MeasuredEndpoint}.
 *
 * <p>Pour chaque méthode annotée, il enregistre automatiquement :
 * <ul>
 *   <li>Un {@link Timer} avec histogramme et SLOs (1ms → 5s)</li>
 *   <li>Un {@link Counter} pour les appels réussis</li>
 *   <li>Un {@link Counter} pour les appels en erreur, taguée avec la classe d'exception</li>
 * </ul>
 *
 * <p>Les métriques sont mises en cache dans des {@link ConcurrentHashMap} pour éviter
 * la surcharge de lookup au MeterRegistry à chaque appel.
 */
@MeasuredEndpoint
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class MeasuredEndpointInterceptor {

  static final String METRIC_LATENCY  = "api_endpoint_latency";
  static final String METRIC_CALLS    = "api_endpoint_calls";
  static final String METRIC_ERRORS   = "api_endpoint_errors";

  @Inject
  MeterRegistry registry;

  private final Map<String, Timer>   timers        = new ConcurrentHashMap<>();
  private final Map<String, Counter> successCounters = new ConcurrentHashMap<>();
  private final Map<String, Counter> errorCounters   = new ConcurrentHashMap<>();

  @AroundInvoke
  public Object measure(InvocationContext ctx) throws Exception {

    Method method = ctx.getMethod();
    MeasuredEndpoint ann = resolveAnnotation(method, ctx.getTarget());

    if (ann == null) {
      return ctx.proceed();
    }

    String name     = ann.name().isBlank()     ? method.getName() : ann.name();
    String endpoint = ann.endpoint().isBlank() ? method.getName() : ann.endpoint();
    String cacheKey = endpoint + ":" + name;

    Timer   timer          = timers.computeIfAbsent(cacheKey,        k -> buildTimer(endpoint, name));
    Counter successCounter = successCounters.computeIfAbsent(cacheKey, k -> buildSuccessCounter(endpoint, name));

    Timer.Sample sample = Timer.start(registry);
    try {
      Object result = ctx.proceed();
      successCounter.increment();
      return result;

    } catch (Exception ex) {
      errorCounters
          .computeIfAbsent(cacheKey + ":" + ex.getClass().getSimpleName(),
              k -> buildErrorCounter(endpoint, name, ex.getClass().getSimpleName()))
          .increment();
      throw ex;

    } finally {
      sample.stop(timer);
    }
  }

  // ===== builders =====

  private Timer buildTimer(String endpoint, String name) {
    return Timer.builder(METRIC_LATENCY)
        .description("Latence de " + endpoint)
        .tag("endpoint", endpoint)
        .tag("operation", name)
        .publishPercentileHistogram()
        .serviceLevelObjectives(
            Duration.ofMillis(1),
            Duration.ofMillis(5),
            Duration.ofMillis(10),
            Duration.ofMillis(50),
            Duration.ofMillis(100),
            Duration.ofMillis(500),
            Duration.ofSeconds(1),
            Duration.ofSeconds(2),
            Duration.ofSeconds(5))
        .register(registry);
  }

  private Counter buildSuccessCounter(String endpoint, String name) {
    return Counter.builder(METRIC_CALLS)
        .description("Appels réussis — " + endpoint)
        .tag("endpoint", endpoint)
        .tag("operation", name)
        .register(registry);
  }

  private Counter buildErrorCounter(String endpoint, String name, String exceptionClass) {
    return Counter.builder(METRIC_ERRORS)
        .description("Appels en erreur — " + endpoint)
        .tag("endpoint", endpoint)
        .tag("operation", name)
        .tag("exception", exceptionClass)
        .register(registry);
  }

  // ===== résolution de l'annotation (méthode > classe) =====

  private MeasuredEndpoint resolveAnnotation(Method method, Object target) {
    MeasuredEndpoint ann = method.getAnnotation(MeasuredEndpoint.class);
    if (ann == null && target != null) {
      ann = target.getClass().getAnnotation(MeasuredEndpoint.class);
    }
    return ann;
  }
}
