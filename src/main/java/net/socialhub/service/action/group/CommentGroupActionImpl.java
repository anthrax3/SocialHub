package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;

import java.util.HashMap;
import java.util.Map;

/**
 * コメントグループに対しての操作
 */
public class CommentGroupActionImpl implements CommentGroupAction {

    private CommentGroupImpl commentGroup;

    public CommentGroupActionImpl(CommentGroupImpl commentGroup) {
        this.commentGroup = commentGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getNewComments() {
        CommentGroupImpl model = new CommentGroupImpl();
        Map<Account, Pageable<Comment>> map = new HashMap<>();
        commentGroup.getEntities().forEach((account, pageable) -> {

            Paging paging = pageable.newPage();
            Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
            map.put(account, comments);
        });

        model.setEntities(map);
        model.setMaxDateFromEntities();
        model.margeWhenNewPageRequest(commentGroup);
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getPastComments() {
        CommentGroupImpl model = new CommentGroupImpl();
        Map<Account, Pageable<Comment>> map = new HashMap<>();
        commentGroup.getEntities().forEach((account, pageable) -> {

            Paging paging = pageable.pastPage();
            Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
            map.put(account, comments);
        });

        model.setEntities(map);
        model.setSinceDateFromEntities();
        model.margeWhenPastPageRequest(commentGroup);
        return model;
    }

    //region // Getter&Setter
    public CommentGroupImpl getCommentGroup() {
        return commentGroup;
    }

    public void setCommentGroup(CommentGroupImpl commentGroup) {
        this.commentGroup = commentGroup;
    }
    //endregion
}