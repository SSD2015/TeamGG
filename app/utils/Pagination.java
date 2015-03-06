package utils;

public class Pagination {
    private int itemPerPage;
    private int currentPage = 0;
    private int itemCount = -1;

    public Pagination(int itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public Pagination(int itemPerPage, int currentPage) {
        this.itemPerPage = itemPerPage;
        this.currentPage = currentPage;
    }

    public int getItemPerPage() {
        return itemPerPage;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean hasPreviousPage(){
        return currentPage > 0;
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
        return (currentPage+1) * itemPerPage < itemCount;
    }

    public int getPageCount(){
        return (int) Math.ceil((double) itemCount/(double) itemPerPage);
    }
}
