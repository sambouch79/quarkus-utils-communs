package io.github.sambouch79.utilscommons.pagination;

import java.util.List;

/**
 * Réponse paginée générique.
 *
 * <p>Contient la page courante, les métadonnées de navigation (first/last/empty)
 * et le total d'éléments pour permettre au client de calculer le nombre de pages.
 *
 * @param <T> type des éléments de la page
 */
public class PageResponse<T> {

  private List<T> content;
  private long    totalElements;
  private int     totalPages;
  private int     currentPage;
  private int     pageSize;
  private boolean first;
  private boolean last;
  private boolean empty;

  /** Constructeur vide pour la sérialisation JSON. */
  public PageResponse() {}

  /**
   * Construit une réponse paginée complète.
   *
   * @param content       éléments de la page courante
   * @param totalElements nombre total d'éléments dans la collection
   * @param currentPage   numéro de page courant (base 0)
   * @param pageSize      taille demandée
   */
  public PageResponse(List<T> content, long totalElements, int currentPage, int pageSize) {
    this.content       = content;
    this.totalElements = totalElements;
    this.currentPage   = Math.max(0, currentPage);
    this.pageSize      = Math.max(1, pageSize);
    this.totalPages    = totalElements == 0
        ? 1
        : (int) Math.ceil((double) totalElements / this.pageSize);
    this.first = currentPage == 0;
    this.last  = currentPage >= this.totalPages - 1;
    this.empty = content == null || content.isEmpty();
  }

  /**
   * Factory method pour une page vide (aucun résultat).
   *
   * @param currentPage numéro de page
   * @param pageSize    taille de page
   * @param <T>         type des éléments
   * @return une {@link PageResponse} vide avec {@code totalElements = 0}
   */
  public static <T> PageResponse<T> empty(int currentPage, int pageSize) {
    return new PageResponse<>(List.of(), 0, Math.max(0, currentPage), Math.max(1, pageSize));
  }

  // ===== getters =====

  public List<T> getContent()      { return content; }
  public long    getTotalElements() { return totalElements; }
  public int     getTotalPages()    { return totalPages; }
  public int     getCurrentPage()   { return currentPage; }
  public int     getPageSize()      { return pageSize; }
  public boolean isFirst()          { return first; }
  public boolean isLast()           { return last; }
  public boolean isEmpty()          { return empty; }
}
