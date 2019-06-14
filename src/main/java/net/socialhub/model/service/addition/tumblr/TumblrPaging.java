package net.socialhub.model.service.addition.tumblr;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Tumblr Paging
 * Tumblr の特殊ページング対応
 */
public class TumblrPaging extends Paging {

    // Since ID
    private Long sinceId;

    // Page Number (1 Origin)
    private Long page;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        TumblrPaging pg = copy();

        if (entities.size() > 0) {
            T first = entities.get(0);
            pg.setSinceId((Long) first.getId());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        TumblrPaging pg = copy();

        if (pg.getPage() == null) {
            pg.setPage(2L);
        } else {
            pg.setPage(pg.getPage() + 1);
        }
        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public TumblrPaging copy() {
        TumblrPaging pg = new TumblrPaging();
        pg.setPage(getPage());
        pg.setCount(getCount());
        pg.setSinceId(getSinceId());
        pg.setHasMore(getHasMore());
        return pg;
    }

    //region // Getter&Setter
    public Long getSinceId() {
        return sinceId;
    }

    public void setSinceId(Long sinceId) {
        this.sinceId = sinceId;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }
    //endregion
}
