package net.socialhub.service.twitter;

import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static net.socialhub.define.ActionEnum.BlockUser;
import static net.socialhub.define.ActionEnum.FollowUser;
import static net.socialhub.define.ActionEnum.GetUser;
import static net.socialhub.define.ActionEnum.GetUserMe;
import static net.socialhub.define.ActionEnum.HomeTimeLine;
import static net.socialhub.define.ActionEnum.LikeComment;
import static net.socialhub.define.ActionEnum.MuteUser;
import static net.socialhub.define.ActionEnum.UnblockUser;
import static net.socialhub.define.ActionEnum.UnfollowUser;
import static net.socialhub.define.ActionEnum.UnlikeComment;
import static net.socialhub.define.ActionEnum.UnmuteUser;

/**
 * Twitter Actions
 * (All Actions)
 */
public class TwitterAction extends AccountActionImpl {

    private ServiceAuth<Twitter> auth;

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

            return TwitterMapper.user(user, service);
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

    // ============================================================== //
    // Comment
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
    public void like(Identify id) {
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
    public void unlike(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().destroyFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(UnlikeComment, status);
        });
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, TwitterException> runner) {
        try {
            return runner.proceed();
        } catch (TwitterException e) {
            handleTwitterException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<TwitterException> runner) {
        try {
            runner.proceed();
        } catch (TwitterException e) {
            handleTwitterException(e);
        }
    }

    private static void handleTwitterException(TwitterException e) {
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
