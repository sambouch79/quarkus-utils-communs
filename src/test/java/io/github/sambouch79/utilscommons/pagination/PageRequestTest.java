package io.github.sambouch79.utilscommons.pagination;

import io.github.sambouch79.utilscommons.pagination.PageRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestTest {

  // Implémentation concrète minimale pour les tests
  private static class TestPageRequest extends PageRequest {
    private String sortBy = "id";
    @Override public String getSortBy()          { return sortBy; }
    @Override public void   setSortBy(String s)  { this.sortBy = s; }
  }

  @Test
  void safePage_null_shouldReturnZero() {
    var req = new TestPageRequest();
    req.setPage(null);
    assertEquals(0, req.safePage());
  }

  @Test
  void safePage_negative_shouldReturnZero() {
    var req = new TestPageRequest();
    req.setPage(-3);
    assertEquals(0, req.safePage());
  }

  @Test
  void safePage_valid_shouldReturnValue() {
    var req = new TestPageRequest();
    req.setPage(5);
    assertEquals(5, req.safePage());
  }

  @Test
  void safeSize_null_shouldReturn100() {
    var req = new TestPageRequest();
    req.setSize(null);
    assertEquals(100, req.safeSize());
  }

  @Test
  void safeSize_tooLarge_shouldBeCappedAt100() {
    var req = new TestPageRequest();
    req.setSize(500);
    assertEquals(100, req.safeSize());
  }

  @Test
  void safeSize_tooSmall_shouldBe1() {
    var req = new TestPageRequest();
    req.setSize(0);
    assertEquals(1, req.safeSize());
  }

  @Test
  void safeSortField_blank_shouldReturnId() {
    var req = new TestPageRequest();
    req.setSortBy("  ");
    assertEquals("id", req.safeSortField());
  }

  @Test
  void safeSortField_null_shouldReturnId() {
    var req = new TestPageRequest();
    req.setSortBy(null);
    assertEquals("id", req.safeSortField());
  }

  @Test
  void safeSortField_valid_shouldReturnValue() {
    var req = new TestPageRequest();
    req.setSortBy("raisonSociale");
    assertEquals("raisonSociale", req.safeSortField());
  }

  @Test
  void isAscending_defaultASC_shouldBeTrue() {
    var req = new TestPageRequest();
    assertTrue(req.isAscending());
  }

  @Test
  void isAscending_DESC_shouldBeFalse() {
    var req = new TestPageRequest();
    req.setSortDirection(PageRequest.SortDirection.DESC);
    assertFalse(req.isAscending());
  }

  @Test
  void isPaginationEnabled_withSize_shouldBeTrue() {
    var req = new TestPageRequest();
    req.setSize(20);
    assertTrue(req.isPaginationEnabled());
  }

  @Test
  void isPaginationEnabled_noSize_shouldBeFalse() {
    var req = new TestPageRequest();
    req.setSize(null);
    assertFalse(req.isPaginationEnabled());
  }
}
