package net.socialhub.service.action;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;

public interface RequestAction {

    /**
     * Get Comments
     * コメントを取得
     */
    default Pageable<Comment> getComments(Paging paging) {
        throw new NotImplimentedException();
    }

    static RequestAction of(AccountAction action, ActionType type, Object... args) {
        return new RequestActionImpl(action, type, args);
    }
}
