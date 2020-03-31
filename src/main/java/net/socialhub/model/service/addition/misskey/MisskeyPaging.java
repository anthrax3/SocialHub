package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Paging;

import java.util.List;

/**
 * Misskey Paging
 * Misskey の特殊ページングに対応
 */
public class MisskeyPaging extends Paging {

    private String untilId;
    private String sinceId;

    /**
     * From Paging instance
     */
    public static MisskeyPaging fromPaging(Paging paging) {
        if (paging instanceof MisskeyPaging) {
            return ((MisskeyPaging) paging).copy();
        }

        // Count の取得
        MisskeyPaging pg = new MisskeyPaging();
        if ((paging != null) && (paging.getCount() != null)) {
            pg.setCount(paging.getCount());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging newPage(List<T> entities) {
        MisskeyPaging pg = copy();

        if (entities.size() > 0) {
            T first = entities.get(0);

            pg.setUntilId(null);
            pg.setSinceId((String) first.getId());
        }
        return pg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Identify> Paging pastPage(List<T> entities) {
        MisskeyPaging pg = copy();

        if (entities.size() > 0) {
            T last = entities.get(entities.size() - 1);

            pg.setUntilId((String) last.getId());
            pg.setSinceId(null);
        }
        return pg;
    }

    /**
     * オブジェクトコピー
     */
    public MisskeyPaging copy() {
        MisskeyPaging pg = new MisskeyPaging();
        pg.setSinceId(getSinceId());
        pg.setUntilId(getUntilId());
        copyTo(pg);
        return pg;
    }

    // region
    public String getUntilId() {
        return untilId;
    }

    public void setUntilId(String untilId) {
        this.untilId = untilId;
    }

    public String getSinceId() {
        return sinceId;
    }

    public void setSinceId(String sinceId) {
        this.sinceId = sinceId;
    }
    // endregion
}
