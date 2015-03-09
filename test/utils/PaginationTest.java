package utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class PaginationTest {

    @Test
    public void testGetItemPerPage() throws Exception {
        Pagination pager = new Pagination(120);
        assertEquals(120, pager.getItemPerPage());
    }

    @Test
    public void testGetItemCount() throws Exception {
        Pagination pager = new Pagination(30, 100, 0);
        assertEquals(100, pager.getItemCount());
    }

    @Test
    public void testGetCurrentPage() throws Exception {
        Pagination pager = new Pagination(30, 100, 0);
        assertEquals("first page is zero", 0, pager.getCurrentPage());

        // test set by constructor
        pager = new Pagination(30, 100, 100);
        assertEquals("last page is capped when using constructor", 3, pager.getCurrentPage());
    }

    @Test
    public void testHasPreviousPage() throws Exception {
        Pagination pager = new Pagination(30, 100, 0);
        assertFalse("at first page", pager.hasPreviousPage());

        pager = new Pagination(30, 100, 1);
        assertTrue("at second page", pager.hasPreviousPage());

        pager = new Pagination(30, 100, pager.getLastPage());
        assertTrue("at last page", pager.hasPreviousPage());

        pager = new Pagination(30, 0, 0);
        assertFalse("empty pager", pager.hasPreviousPage());
    }

    @Test
    public void testGetPreviousPage() throws Exception {
        Pagination pager = new Pagination(30, 100, 0);
        assertEquals("at first page", 0, pager.getPreviousPage());

        pager = new Pagination(30, 100, 1);
        assertEquals("at second page", 0, pager.getPreviousPage());

        pager = new Pagination(30, 100, pager.getLastPage());
        assertEquals("at last page", 2, pager.getPreviousPage());

        pager = new Pagination(30, 0, 0);
        assertEquals("empty pager", 0, pager.getPreviousPage());
    }

    @Test
    public void testGetNextPage() throws Exception {
        Pagination pager = new Pagination(30, 100, 0);
        assertEquals("at first page", 1, pager.getNextPage());

        pager = new Pagination(30, 100, 1);
        assertEquals("at second page", 2, pager.getNextPage());

        pager = new Pagination(30, 100, pager.getLastPage());
        assertEquals("at last page", 3, pager.getNextPage());

        pager = new Pagination(30, 0, 0);
        assertEquals("empty pager", 0, pager.getNextPage());

        pager = new Pagination(30, 10, 0);
        assertEquals("half full pager", 0, pager.getNextPage());
    }

    @Test
    public void testHasNextPage() throws Exception {
        Pagination pager = new Pagination(30, 100, 0);
        assertTrue("at first page", pager.hasNextPage());

        pager = new Pagination(30, 100, 1);
        assertTrue("at second page", pager.hasNextPage());

        pager = new Pagination(30, 100, pager.getLastPage());
        assertFalse("at last page", pager.hasNextPage());

        pager = new Pagination(30, 0, 0);
        assertFalse("empty pager", pager.hasNextPage());

        pager = new Pagination(30, 10, 0);
        assertFalse("half full pager", pager.hasNextPage());
    }

    @Test
    public void testGetPageCount() throws Exception {
        Pagination pager = new Pagination(30);
        assertEquals("no items must has a page", 0, pager.getPageCount());
    }
}