package net.socialhub.service.twitter;

import net.socialhub.define.MediaType;
import net.socialhub.define.service.twitter.TwitterReactionType;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.request.CommentRequest;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.twitter.TwitterComment;
import net.socialhub.model.service.event.DeleteCommentEvent;
import net.socialhub.model.service.event.UpdateCommentEvent;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.IndexPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.action.callback.DeleteCommentCallback;
import net.socialhub.service.action.callback.EventCallback;
import net.socialhub.service.action.callback.UpdateCommentCallback;
import net.socialhub.utils.HandlingUtil;
import net.socialhub.utils.MapperUtil;
import net.socialhub.utils.SnowflakeUtil;
import twitter4j.FilterQuery;
import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static net.socialhub.define.action.OtherActionType.BlockUser;
import static net.socialhub.define.action.OtherActionType.DeleteComment;
import static net.socialhub.define.action.OtherActionType.FollowUser;
import static net.socialhub.define.action.OtherActionType.GetComment;
import static net.socialhub.define.action.OtherActionType.GetRelationship;
import static net.socialhub.define.action.OtherActionType.GetUser;
import static net.socialhub.define.action.OtherActionType.GetUserMe;
import static net.socialhub.define.action.OtherActionType.LikeComment;
import static net.socialhub.define.action.OtherActionType.MuteUser;
import static net.socialhub.define.action.OtherActionType.ShareComment;
import static net.socialhub.define.action.OtherActionType.UnblockUser;
import static net.socialhub.define.action.OtherActionType.UnfollowUser;
import static net.socialhub.define.action.OtherActionType.UnlikeComment;
import static net.socialhub.define.action.OtherActionType.UnmuteUser;
import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MentionTimeLine;
import static net.socialhub.define.action.TimeLineActionType.SearchTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserCommentTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserLikeTimeLine;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;
import static net.socialhub.define.action.UsersActionType.SearchUsers;

/**
 * Twitter Actions
 * (All Actions)
 */
public class TwitterAction extends AccountActionImpl {

    private ServiceAuth<Twitter> auth;

    /** My Account */
    private User me;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User user = auth.getAccessor().verifyCredentials();
            service.getRateLimit().addInfo(GetUserMe, user);

            me = TwitterMapper.user(user, service);
            return me;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();

            // ID
            if (id.getId(Long.class).isPresent()) {
                twitter4j.User user = auth.getAccessor().showUser((Long) id.getId());
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
            }

            // Screen Name
            if (id.getId(String.class).isPresent()) {
                twitter4j.User user = auth.getAccessor().showUser((String) id.getId());
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
            }

            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createFriendship((Long) id.getId());
            service.getRateLimit().addInfo(FollowUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyFriendship((Long) id.getId());
            service.getRateLimit().addInfo(UnfollowUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createMute((Long) id.getId());
            service.getRateLimit().addInfo(MuteUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyMute((Long) id.getId());
            service.getRateLimit().addInfo(UnmuteUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createBlock((Long) id.getId());
            service.getRateLimit().addInfo(BlockUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyBlock((Long) id.getId());
            service.getRateLimit().addInfo(UnblockUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();

            User me = getUserMeWithCache();
            twitter4j.Relationship relationship = auth.getAccessor() //
                    .showFriendship((Long) me.getId(), (Long) id.getId());

            service.getRateLimit().addInfo(GetRelationship, relationship);
            return TwitterMapper.relationship(relationship);
        });
    }

    // ============================================================== //
    // Users
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();

            long cursor = -1;
            int count = 20;
            if (paging != null) {
                if (paging.getCount() != null) {
                    count = paging.getCount().intValue();
                }
                if (paging instanceof CursorPaging) {
                    CursorPaging cpg = (CursorPaging) paging;
                    if (cpg.getCurrentCursor() instanceof Long) {
                        cursor = (long) cpg.getCurrentCursor();
                    }
                }
            }

            PagableResponseList<twitter4j.User> users = auth.getAccessor() //
                    .getFriendsList((Long) id.getId(), cursor, count);

            service.getRateLimit().addInfo(GetFollowingUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();

            long cursor = -1;
            int count = 20;
            if (paging != null) {
                if (paging.getCount() != null) {
                    count = paging.getCount().intValue();
                }
                if (paging instanceof CursorPaging) {
                    CursorPaging cpg = (CursorPaging) paging;
                    if (cpg.getCurrentCursor() instanceof Long) {
                        cursor = (long) cpg.getCurrentCursor();
                    }
                }
            }

            PagableResponseList<twitter4j.User> users = auth.getAccessor() //
                    .getFollowersList((Long) id.getId(), cursor, count);

            service.getRateLimit().addInfo(GetFollowerUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> searchUsers(String query, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();

            int page = 1;
            if (paging != null) {
                paging.setCount(20L);

                if (paging instanceof IndexPaging) {
                    IndexPaging ind = (IndexPaging) paging;
                    if (ind.getPage() != null) {
                        page = ind.getPage().intValue();
                    }
                }
            }

            ResponseList<twitter4j.User> users = auth.getAccessor() //
                    .searchUsers(query, page);

            service.getRateLimit().addInfo(SearchUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    // ============================================================== //
    // TimeLine
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getHomeTimeline() //
                    : twitter.getHomeTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(HomeTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getMentionsTimeline() //
                    : twitter.getMentionsTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(MentionTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<Status> statues = null;
            twitter4j.Paging page = (paging == null) ? //
                    null : TwitterMapper.fromPaging(paging);

            // ID
            if (id.getId(Long.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getUserTimeline((Long) id.getId()) //
                        : twitter.getUserTimeline((Long) id.getId(), page);
            }
            // Screen Name
            if (id.getId(String.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getUserTimeline((String) id.getId()) //
                        : twitter.getUserTimeline((String) id.getId(), page);
            }

            if (statues != null) {
                service.getRateLimit().addInfo(UserCommentTimeLine, statues);
                return TwitterMapper.timeLine(statues, service, paging);
            }
            throw new IllegalStateException();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<Status> statues = null;
            twitter4j.Paging page = (paging == null) ? //
                    null : TwitterMapper.fromPaging(paging);

            // ID
            if (id.getId(Long.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getFavorites((Long) id.getId()) //
                        : twitter.getFavorites((Long) id.getId(), page);
            }
            // Screen Name
            if (id.getId(String.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getFavorites((String) id.getId()) //
                        : twitter.getFavorites((String) id.getId(), page);
            }

            if (statues != null) {
                service.getRateLimit().addInfo(UserLikeTimeLine, statues);
                return TwitterMapper.timeLine(statues, service, paging);
            }
            throw new IllegalStateException();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return proceed(() -> {

            int requestCount = 0;
            int maxMediaCount = 50;
            if (paging != null && paging.getCount() != null) {
                maxMediaCount = paging.getCount().intValue();
            }

            // リクエスト時の PagingCount は 200 に固定
            // (メディアが存在しない場合もあるので多めに取得)
            Paging mediaPaging = (paging != null) ? paging : new Paging();
            mediaPaging.setCount(200L);

            Paging storedCursor = paging;
            Paging currentCursor = paging;
            List<Comment> comments = new ArrayList<>();

            // 順にリクエストする必要があるのでループを実行
            while ((requestCount < 10) && (comments.size() <= maxMediaCount)) {

                storedCursor = currentCursor;
                Pageable<Comment> results = getUserCommentTimeLine(id, currentCursor);
                currentCursor = results.pastPage();
                requestCount++;

                for (Comment comment : results.getEntities()) {
                    if (comment.getMedias().size() > 0) {
                        if (comment.getMedias().stream().anyMatch((e) -> //
                                (e.getType() == MediaType.Image) || //
                                        (e.getType() == MediaType.Movie))) {

                            // メディアコメントの場合
                            comments.add(comment);
                        }
                    }
                }
            }

            Pageable<Comment> pageable = new Pageable<>();
            pageable.setPaging(storedCursor);
            pageable.setEntities(comments);
            return pageable;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            Query q = TwitterMapper.queryFromPaging(paging).query(query);
            QueryResult result = twitter.search(q);

            service.getRateLimit().addInfo(SearchTimeLine, result);
            return TwitterMapper.timeLine(result, service, paging);
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public void postComment(CommentRequest req) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            ExecutorService pool = Executors.newCachedThreadPool();
            StatusUpdate update = new StatusUpdate(req.getMessage());

            // 返信の処理
            if (req.getReplyId() != null) {
                update.setInReplyToStatusId((Long) req.getReplyId());
            }

            // 画像の処理
            if (req.getImages() != null && !req.getImages().isEmpty()) {

                // 画像を並列でアップロードする
                List<Future<Long>> medias = req.getImages() //
                        .stream().map(image -> pool.submit(() -> {
                            InputStream input = new ByteArrayInputStream(image.getData());
                            return twitter.uploadMedia(image.getName(), input).getMediaId();
                        })).collect(Collectors.toList());

                update.setMediaIds(medias.stream().mapToLong( //
                        (e) -> HandlingUtil.runtime(e::get)).toArray());
            }

            // センシティブな内容
            if (req.getSensitive() != null && req.getSensitive()) {
                update.setPossiblySensitive(true);
            }

            Status status = twitter.updateStatus(update);
            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment getComment(Identify id) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.showStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetComment, status);

            return TwitterMapper.comment(status, service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().createFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(LikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlikeComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().destroyFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(UnlikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shareComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().retweetStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(ShareComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshareComment(Identify id) {
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (TwitterReactionType.Favorite.getCode().contains(type)) {
                likeComment(id);
                return;
            }
            if (TwitterReactionType.Retweet.getCode().contains(type)) {
                shareComment(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (TwitterReactionType.Favorite.getCode().contains(type)) {
                unlikeComment(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().destroyStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(DeleteComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return TwitterMapper.reactionCandidates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext(Identify id) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ExecutorService pool = Executors.newCachedThreadPool();

            TwitterComment originComment = toTwitterComment(id);
            TwitterComment comment = toTwitterComment(originComment.getDisplayComment());

            Future<List<Comment>> ancestors = null;
            Future<List<Status>> afterRecent;
            Future<List<Status>> afterWhole;
            Future<List<Status>> afterQuote;

            // ------------------------------------------------ //
            // 前の会話内容を取得
            // ------------------------------------------------ //

            if (comment.getReplyTo() != null) {
                ancestors = pool.submit(() -> {
                    return proceed(() -> {
                        List<Comment> results = new ArrayList<>();
                        Long replyId = (Long) comment.getReplyTo().getId();

                        for (int i = 0; i < 10; i++) {
                            Status status = twitter.showStatus(replyId);
                            Comment c = TwitterMapper.comment(status, service);
                            results.add(0, c);

                            if (status.getInReplyToStatusId() > 0) {
                                replyId = status.getInReplyToStatusId();
                                continue;
                            }
                            break;
                        }
                        return results;
                    });
                });
            }

            // ------------------------------------------------ //
            // 後の会話情報を取得
            // ------------------------------------------------ //
            List<Comment> descendants;

            {
                // クエリを組み上げる処理
                User user = comment.getUser();
                String mention = user.getScreenName();
                Long sinceId = (Long) comment.getId();
                Long maxId = SnowflakeUtil.ofTwitter().addHoursToID(sinceId, 2L);

                // ツイート後の二時間を対象に取得
                afterRecent = pool.submit(() -> {
                    return proceed(() -> {
                        Query query = new Query();
                        query.setSinceId(sinceId);
                        query.setMaxId(maxId);
                        query.setQuery(mention + " -RT");
                        query.setCount(200);

                        return twitter.search(query).getTweets();
                    });
                });

                // 検索可能な全期間を検索
                afterWhole = pool.submit(() -> {
                    return proceed(() -> {
                        Query query = new Query();
                        query.setSinceId(sinceId);
                        query.setQuery(mention + " -RT");
                        query.setCount(200);

                        return twitter.search(query).getTweets();
                    });
                });

                // 引用 RT のアカウントを取得
                afterQuote = pool.submit(() -> {
                    return proceed(() -> {
                        Query query = new Query();
                        query.setQuery(comment.getUrl() + " -RT");
                        query.setCount(200);

                        return twitter.search(query).getTweets();
                    });
                });

                // 結果を統合
                List<Status> statuses = new ArrayList<>();
                statuses.addAll(afterRecent.get());
                statuses.addAll(afterWhole.get());
                statuses = statuses.stream().distinct().collect(Collectors.toList());

                // 結果として扱うステータス一覧
                List<Status> results = new ArrayList<>(afterQuote.get());

                // 返信リストを取得
                List<Long> idList = new ArrayList<>();
                idList.add(sinceId);

                while (true) {
                    List<Status> inserts = new ArrayList<>();

                    for (Status status : statuses) {
                        if (idList.contains(status.getInReplyToStatusId())) {
                            if (!results.contains(status)) {
                                inserts.add(status);
                            }
                        }
                    }

                    // 既に全て加えてあれば終了
                    if (inserts.isEmpty()) {
                        break;
                    }

                    // 返信関連の ID を一覧に加える
                    idList.addAll(inserts.stream().map(Status::getId).collect(Collectors.toList()));
                    idList = idList.stream().distinct().collect(Collectors.toList());

                    statuses.removeAll(inserts);
                    results.addAll(inserts);
                    inserts.clear();
                }

                descendants = results.stream()
                        .map((c) -> TwitterMapper.comment(c, service))
                        .collect(Collectors.toList());
            }

            Context context = new Context();
            context.setDescendants(descendants);
            context.setAncestors((ancestors != null) ? ancestors.get() : new ArrayList<>());
            MapperUtil.sortContext(context);
            return context;
        });
    }

    // ============================================================== //
    // Stream
    // ============================================================== //

    /**
     * Set Home Timeline Stream
     * ホームタイムラインのイベントを取得
     * (5000 人までフォローしているユーザー専用
     * filter ストリームで誤魔化して使用)
     */
    public net.socialhub.model.service.Stream
    setHomeTimeLineStream(EventCallback callback) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            long id = (Long) getUserMeWithCache().getId();
            IDs ids = twitter.getFriendsIDs(id, -1L, 5000);

            TwitterStream stream = ((TwitterAuth) auth).getStreamAccessor();
            stream.addListener(new TwitterCommentsListener(callback, service));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(ids.getIDs());
                stream.filter(q);
            });
        });
    }

    /**
     * Set Search Timeline Stream
     * 検索タイムラインのイベントを取得
     */
    public net.socialhub.model.service.Stream
    setSearchTimeLineStream(EventCallback callback, String query) {
        return proceed(() -> {
            Service service = getAccount().getService();
            TwitterStream stream = ((TwitterAuth) auth).getStreamAccessor();
            stream.addListener(new TwitterCommentsListener(callback, service));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(query);
                stream.filter(q);
            });
        });
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    /**
     * ID を MiniBlogComment に変換
     */
    private TwitterComment toTwitterComment(Identify id) {

        // コメント情報を取得
        if (id instanceof TwitterComment) {
            return (TwitterComment) id;
        } else {
            Comment c = getComment(id);
            if (c instanceof TwitterComment) {
                return (TwitterComment) c;
            }
        }

        throw new IllegalStateException();
    }

    // ============================================================== //
    // Cache
    // ============================================================== //

    /**
     * キャッシュ付きで自分のユーザーを取得
     */
    private User getUserMeWithCache() {
        return (me != null) ? me : getUserMe();
    }

    // ============================================================== //
    // Classes
    // ============================================================== //

    static class TwitterCommentsListener extends StatusAdapter {

        private EventCallback listener;
        private Service service;

        TwitterCommentsListener(
                EventCallback listener,
                Service service) {
            this.listener = listener;
            this.service = service;
        }

        @Override
        public void onStatus(Status status) {
            if (listener instanceof UpdateCommentCallback) {
                Comment comment = TwitterMapper.comment(status, service);
                UpdateCommentEvent event = new UpdateCommentEvent(comment);
                ((UpdateCommentCallback) listener).onUpdate(event);
            }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice delete) {
            if (listener instanceof DeleteCommentCallback) {
                if (delete.getStatusId() > 0L) {
                    DeleteCommentEvent event = new DeleteCommentEvent(delete.getStatusId());
                    ((DeleteCommentCallback) listener).onDelete(event);
                }
            }
        }
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    // FIXME: TwitterException
    private <T> T proceed(ActionCaller<T, Exception> runner) {
        try {
            return runner.proceed();
        } catch (Exception e) {
            handleTwitterException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<Exception> runner) {
        try {
            runner.proceed();
        } catch (Exception e) {
            handleTwitterException(e);
        }
    }

    private static void handleTwitterException(Exception e) {
        System.out.println(e.getMessage());
    }

    //region // Getter&Setter
    TwitterAction(Account account, ServiceAuth<Twitter> auth) {
        this.account(account);
        this.auth(auth);
    }

    TwitterAction auth(ServiceAuth<Twitter> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
