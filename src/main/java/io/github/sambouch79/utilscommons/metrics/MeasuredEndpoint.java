package io.github.sambouch79.utilscommons.metrics;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation CDI qui instrumente automatiquement un endpoint REST avec trois métriques Micrometer :
 *
 * <ul>
 *   <li>{@code api_endpoint_latency} — histogramme de latence avec SLOs prédéfinis
 *   <li>{@code api_endpoint_calls} — compteur d'appels réussis
 *   <li>{@code api_endpoint_errors} — compteur d'appels en erreur
 * </ul>
 *
 * <p>Exemple d'utilisation :
 *
 * <pre>{@code
 * @GET
 * @MeasuredEndpoint(name = "partenaire.findByUuid", endpoint = "findByUuid")
 * public Response findByUuid(@PathParam("uuid") UUID uuid) { ... }
 * }</pre>
 *
 * <p>Les métriques sont taguées avec {@code endpoint} et {@code operation} pour permettre
 * un filtrage précis dans Grafana/Prometheus.
 */
@Inherited
@Documented
@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface MeasuredEndpoint {

  /**
   * Nom logique de l'opération, utilisé comme tag {@code operation} dans Prometheus.
   * Par défaut, le nom de la méthode est utilisé.
   */
  @Nonbinding
  String name() default "";

  /**
   * Nom de l'endpoint, utilisé comme tag {@code endpoint} dans Prometheus.
   * Par défaut, le nom de la méthode est utilisé.
   */
  @Nonbinding
  String endpoint() default "";
}
