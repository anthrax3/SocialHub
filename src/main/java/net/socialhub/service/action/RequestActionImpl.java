package net.socialhub.service.action;

import com.google.gson.Gson;
import net.socialhub.define.action.ActionType;
import net.socialhub.define.action.TimeLineActionType;
import net.socialhub.define.action.UsersActionType;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;
import net.socialhub.service.action.request.UsersRequest;
import net.socialhub.service.action.request.UsersRequestImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.socialhub.define.action.TimeLineActionType.ChannelTimeLine;
import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MentionTimeLine;
import static net.socialhub.define.action.TimeLineActionType.SearchTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserCommentTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserLikeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserMediaTimeLine;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;
import static net.socialhub.define.action.UsersActionType.SearchUsers;

public class RequestActionImpl implements RequestAction {

    protected Account account;

    public RequestActionImpl(Account account) {
        this.account = account;
    }

    // ============================================================== //
    // User API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest getFollowingUsers(Identify id) {
        return getUsersRequest(GetFollowingUsers,
                (paging) -> account.action().getFollowingUsers(id, paging),
                () -> new SerializeBuilder(GetFollowingUsers).toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest getFollowerUsers(Identify id) {
        return getUsersRequest(GetFollowerUsers,
                (paging) -> account.action().getFollowerUsers(id, paging),
                () -> new SerializeBuilder(GetFollowerUsers)
                        .add("id", id.toString())
                        .toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsersRequest searchUsers(String query) {
        return getUsersRequest(SearchUsers,
                (paging) -> account.action().searchUsers(query, paging),
                () -> new SerializeBuilder(SearchUsers)
                        .add("query", query)
                        .toJson());
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        return getCommentsRequest(HomeTimeLine,
                (paging) -> account.action().getHomeTimeLine(paging),
                () -> new SerializeBuilder(HomeTimeLine).toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getMentionTimeLine() {
        return getCommentsRequest(MentionTimeLine,
                (paging) -> account.action().getMentionTimeLine(paging),
                () -> new SerializeBuilder(MentionTimeLine).toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserCommentTimeLine(Identify id) {
        return getCommentsRequest(UserCommentTimeLine,
                (paging) -> account.action().getUserCommentTimeLine(id, paging),
                () -> new SerializeBuilder(UserCommentTimeLine)
                        .add("id", id.toString())
                        .toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserLikeTimeLine(Identify id) {
        return getCommentsRequest(UserLikeTimeLine,
                (paging) -> account.action().getUserLikeTimeLine(id, paging),
                () -> new SerializeBuilder(UserLikeTimeLine)
                        .add("id", id.toString())
                        .toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserMediaTimeLine(Identify id) {
        return getCommentsRequest(UserMediaTimeLine,
                (paging) -> account.action().getUserMediaTimeLine(id, paging),
                () -> new SerializeBuilder(UserMediaTimeLine)
                        .add("id", id.toString())
                        .toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getSearchTimeLine(String query) {
        return getCommentsRequest(SearchTimeLine, (paging) ->
                account.action().getSearchTimeLine(query, paging),
                () -> new SerializeBuilder(SearchTimeLine)
                        .add("query", query)
                        .toJson());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getChannelTimeLine(Identify id) {
        return getCommentsRequest(ChannelTimeLine,
                (paging) -> account.action().getChannelTimeLine(id, paging),
                () -> new SerializeBuilder(ChannelTimeLine)
                        .add("id", id.toString())
                        .toJson());
    }

    // ============================================================== //
    // Inner Class
    // ============================================================== //

    public static class SerializeBuilder {
        private Map<String, String> params = new HashMap<>();

        private SerializeBuilder(TimeLineActionType action) {
            add("action", action.name());
        }

        private SerializeBuilder(UsersActionType action) {
            add("action", action.name());
        }

        public SerializeBuilder add(String key, String value) {
            params.put(key, value);
            return this;
        }

        public String toJson() {
            return new Gson().toJson(params);
        }
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    // User
    protected UsersRequestImpl getUsersRequest(
            ActionType type,
            Function<Paging, Pageable<User>> usersFunction,
            Supplier<String> serializeSupplier) {

        UsersRequestImpl request = new UsersRequestImpl();
        request.setSerializeSupplier(serializeSupplier);
        request.setUsersFunction(usersFunction);
        request.setActionType(type);
        request.setAccount(account);
        return request;
    }

    // Comments
    protected CommentsRequestImpl getCommentsRequest(
            ActionType type,
            Function<Paging, Pageable<Comment>> commentsFunction,
            Supplier<String> serializeSupplier) {

        CommentsRequestImpl request = new CommentsRequestImpl();
        request.setSerializeSupplier(serializeSupplier);
        request.setCommentsFunction(commentsFunction);
        request.setActionType(type);
        request.setAccount(account);
        return request;
    }

    //region // Getter&Setter
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    //endregion
}
