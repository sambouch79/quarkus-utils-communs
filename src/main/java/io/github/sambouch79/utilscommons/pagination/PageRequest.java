package io.github.sambouch79.utilscommons.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

/**
 * Paramètres de pagination et de tri réutilisables pour les endpoints JAX-RS.
 *
 * <p>Utilisation : étendre cette classe abstraite et implémenter {@link #getSortBy()}
 * avec le champ de tri par défaut et la contrainte {@code @ValidSortField} de ton domaine.
 *
 * <pre>{@code
 * public class PartenairePageRequest extends PageRequest {
 *
 *   @QueryParam("sortBy")
 *   @DefaultValue("id")
 *   @ValidSortField({"id", "raisonSociale", "siret"})
 *   private String sortBy;
 *
 *   @Override public String getSortBy() { return sortBy; }
 *   @Override public void setSortBy(String s) { this.sortBy = s; }
 * }
 * }</pre>
 */
public abstract class PageRequest {

  @QueryParam("page")
  @DefaultValue("0")
  @Min(value = 0, message = "Le numéro de page doit être >= 0")
  private Integer page;

  @QueryParam("size")
  @Min(value = 1,   message = "La taille de page doit être >= 1")
  @Max(value = 100, message = "La taille de page ne peut pas dépasser 100")
  private Integer size;

  @QueryParam("direction")
  @DefaultValue("ASC")
  private SortDirection sortDirection = SortDirection.ASC;

  public enum SortDirection { ASC, DESC }

  // ===== abstract =====

  /** Champ de tri courant — à implémenter par la sous-classe. */
  public abstract String getSortBy();

  public abstract void setSortBy(String sortBy);

  // ===== accesseurs =====

  public Integer getPage()                  { return page; }
  public void    setPage(Integer page)      { this.page = page; }
  public Integer getSize()                  { return size; }
  public void    setSize(Integer size)      { this.size = size; }
  public SortDirection getSortDirection()   { return sortDirection; }
  public void setSortDirection(SortDirection d) { this.sortDirection = d; }

  // ===== helpers =====

  /** {@code true} si un {@code size} a été fourni dans la requête. */
  public boolean isPaginationEnabled() { return size != null; }

  /** Numéro de page sécurisé (jamais négatif). */
  public int safePage() { return page != null && page >= 0 ? page : 0; }

  /** Taille sécurisée, bornée entre 1 et 100. Défaut : 100. */
  public int safeSize() { return size != null ? Math.min(Math.max(size, 1), 100) : 100; }

  /** Champ de tri résolu, avec fallback sur {@code "id"}. */
  public String safeSortField() {
    String field = getSortBy();
    return (field == null || field.isBlank()) ? "id" : field;
  }

  /** {@code true} si le tri est ascendant. */
  public boolean isAscending() { return sortDirection != SortDirection.DESC; }
}
