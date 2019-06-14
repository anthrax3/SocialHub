package net.socialhub.model.service.paging;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Paging with page number
 * ベージ番号付きページング
 */
public class IndexPaging extends Paging {

    private Long page;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {

        if (page > 1) {
            IndexPaging newPage = new IndexPaging();
            newPage.setCount(getCount());
            newPage.setPage(page - 1);
            return newPage;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {

        Long number = (((page == null) ? 0L : page) + 1L);
        IndexPaging pastPage = new IndexPaging();
        pastPage.setCount(getCount());
        pastPage.setPage(number);
        return pastPage;
    }


    /**
     * オブジェクトコピー
     */
    public IndexPaging copy() {
        IndexPaging pg = new IndexPaging();
        pg.setCount(getCount());
        pg.setPage(getPage());
        pg.setHasMore(getHasMore());
        return pg;
    }

    //region // Getter&Setter
    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }
    //endregion
}
