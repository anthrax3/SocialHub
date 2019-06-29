package net.socialhub.service.action.group;

import net.socialhub.model.group.CommentGroup;

public interface CommentGroupAction {

    /**
     * Get Newer Comments
     * 最新コメントを取得
     */
    CommentGroup getNewComments();

    /**
     * Get Older Comments
     * 遡ってコメントを取得
     */
    CommentGroup getPastComments();
}
