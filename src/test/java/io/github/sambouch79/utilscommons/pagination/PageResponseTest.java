package io.github.sambouch79.utilscommons.pagination;

import io.github.sambouch79.utilscommons.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {

  @Test
  void firstPage_shouldHaveCorrectFlags() {
    var page = new PageResponse<>(List.of("a", "b"), 10L, 0, 2);
    assertTrue(page.isFirst());
    assertFalse(page.isLast());
    assertEquals(5, page.getTotalPages());
    assertFalse(page.isEmpty());
  }

  @Test
  void lastPage_shouldHaveCorrectFlags() {
    var page = new PageResponse<>(List.of("a"), 5L, 4, 1);
    assertFalse(page.isFirst());
    assertTrue(page.isLast());
  }

  @Test
  void emptyContent_shouldSetEmptyFlag() {
    var page = new PageResponse<>(List.of(), 0L, 0, 10);
    assertTrue(page.isEmpty());
    assertEquals(1, page.getTotalPages()); // toujours au moins 1 page
  }

  @Test
  void emptyFactory_shouldReturnEmptyPage() {
    var page = PageResponse.empty(0, 20);
    assertTrue(page.isEmpty());
    assertEquals(0L, page.getTotalElements());
    assertEquals(20, page.getPageSize());
  }

  @Test
  void negativePage_shouldBeNormalizedToZero() {
    var page = new PageResponse<>(List.of(), 0L, -1, 10);
    assertEquals(0, page.getCurrentPage());
  }

  @Test
  void zeroPageSize_shouldBeNormalizedToOne() {
    var page = new PageResponse<>(List.of(), 0L, 0, 0);
    assertEquals(1, page.getPageSize());
  }
}
