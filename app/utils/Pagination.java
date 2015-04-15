package utils;

public class Pagination {
    private int itemPerPage;
    private int currentPage = 0;
    private int itemCount = -1;

    public Pagination(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public Pagination(int itemPerPage, int itemCount, int currentPage) {
        this.itemPerPage = itemPerPage;
        this.itemCount = itemCount;
        this.currentPage = Math.max(1, Math.min(currentPage, getLastPage()));
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public int getItemCount() {
        return itemCount;
    }

    public boolean hasPreviousPage(){
        return currentPage > 1;
    }

    public int getPreviousPage(){
        if(!hasPreviousPage()){
            return currentPage;
        }
        return currentPage - 1;
    }
    public int getNextPage(){
        if(!hasNextPage()){
            return currentPage;
        }
        return currentPage + 1;
    }

    public boolean hasNextPage(){
        return currentPage * itemPerPage < itemCount;
    }

    public int getLastPage(){
        return Math.max(1, getPageCount());
    }

    public int getPageCount(){
        return (int) Math.ceil((double) itemCount/(double) itemPerPage);
    }

    public int getStartRow() {
        return (currentPage-1) * itemPerPage;
    }
}
