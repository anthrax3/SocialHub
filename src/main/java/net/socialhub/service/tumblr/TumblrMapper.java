package net.socialhub.service.tumblr;

import com.tumblr.jumblr.types.*;
import net.socialhub.define.EmojiCategoryType;
import net.socialhub.define.MediaType;
import net.socialhub.define.service.tumblr.TumblrReactionType;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.User;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.tumblr.TumblrComment;
import net.socialhub.model.service.addition.tumblr.TumblrUser;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.utils.MapperUtil;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class TumblrMapper {

    /** J2ObjC はダイナミックロードできない為に使用を明示するために使用 */
    private final static List<Class<?>> ClassLoader = Arrays.asList( //
            com.tumblr.jumblr.types.TextPost.class, //
            com.tumblr.jumblr.types.Video.class, //
            com.tumblr.jumblr.types.Note.class);

    /**
     * ユーザーマッピング
     * (Primary のブログをユーザーと設定)
     * (User is user's primary blog)
     */
    public static User user(
            com.tumblr.jumblr.types.User user, //
            Map<String, String> iconMap, Service service) {

        // プライマリブログについての処理
        for (Blog blog : user.getBlogs()) {
            if (blog.isPrimary()) {
                return user(blog, iconMap, service);
            }
        }

        User model = new User(service);
        model.setName(user.getName());
        return model;
    }

    /**
     * ユーザーマッピング
     * (ブログをユーザーと設定)
     * (User is user's blog)
     */
    public static User user(
            com.tumblr.jumblr.types.Blog blog, //
            Map<String, String> iconMap, Service service) {

        TumblrUser model = new TumblrUser(service);

        model.setName(blog.getName());
        model.setDescription(new AttributedString(blog.getDescription()));

        String host = getBlogIdentify(blog);
        model.setIconImageUrl(iconMap.get(host));
        model.setScreenName(host);
        model.setId(host);

        model.setFollowersCount(blog.getFollowersCount());
        model.setPostsCount(blog.getPostCount());
        model.setLikesCount(blog.getLikeCount());
        model.setBlogUrl(blog.getUrl());

        return model;
    }

    /**
     * ユーザーマッピング
     * (そのブログの投稿に紐づくユーザーと設定)
     */
    public static User user(
            com.tumblr.jumblr.types.Post post,
            Map<String, String> iconMap, Service service) {

        User model = user(post.getBlog(), iconMap, service);
        List<Trail> trails = post.getTrail();
        if ((trails != null) && (trails.size() > 0)) {

            Trail trail = trails.get(0);
            model.setCoverImageUrl(trail.getBlog().getTheme().getHeaderImage());
        }

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment(
            com.tumblr.jumblr.types.Post post, //
            Map<String, String> iconMap, //
            Service service) {

        TumblrComment model = new TumblrComment(service);

        model.setId(post.getId());
        model.setUser(user(post, iconMap, service));

        model.setReblogKey(post.getReblogKey());
        model.setText(new AttributedString(post.getSummary()));
        model.setCreateAt(new Date(post.getTimestamp() * 1000));
        model.setMedias(new ArrayList<>());

        if (post instanceof PhotoPost) {
            PhotoPost photo = (PhotoPost) post;
            model.getMedias().addAll(photos(photo.getPhotos()));
        }

        if (post instanceof VideoPost) {
            model.getMedias().add(video((VideoPost) post));
        }

        return model;
    }

    /**
     * 画像マッピング
     */
    private static List<Media> photos(
            List<Photo> photos) {

        List<Media> results = new ArrayList<>();
        for (Photo photo : photos) {

            Media media = new Media();
            media.setType(MediaType.Image);
            media.setSourceUrl(photo.getOriginalSize().getUrl());
            media.setPreviewUrl(photo.getOriginalSize().getUrl());
            results.add(media);
        }

        return results;
    }

    /**
     * 動画マッピング
     */
    private static Media video(
            VideoPost video) {

        Media media = new Media();
        media.setType(MediaType.Movie);
        media.setSourceUrl(video.getPermalinkUrl());
        media.setPreviewUrl(video.getThumbnailUrl());
        return media;
    }

    /**
     * リアクション候補マッピング
     */
    public static List<ReactionCandidate> reactionCandidates() {
        List<ReactionCandidate> candidates = new ArrayList<>();

        ReactionCandidate like = new ReactionCandidate();
        like.setCategory(EmojiCategoryType.Activities.getCode());
        like.setName(TumblrReactionType.Like.getCode().get(0));
        like.addAlias(TumblrReactionType.Like.getCode());
        candidates.add(like);

        ReactionCandidate share = new ReactionCandidate();
        share.setCategory(EmojiCategoryType.Activities.getCode());
        share.setName(TumblrReactionType.Reblog.getCode().get(0));
        share.addAlias(TumblrReactionType.Reblog.getCode());
        candidates.add(share);

        return candidates;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine(
            List<com.tumblr.jumblr.types.Post> posts, //
            Map<String, String> iconMap, //
            Service service, //
            Paging paging) {


        Pageable<Comment> model = new Pageable<>();
        model.setEntities(posts.stream() //
                .map(e -> comment(e, iconMap, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingTumblrPaging(paging));
        return model;
    }

    /**
     * ユーザーマッピング
     */
    public static Pageable<User> users(
            List<com.tumblr.jumblr.types.User> users, //
            Map<String, String> iconMap, //
            Service service, //
            Paging paging) {


        Pageable<User> model = new Pageable<>();
        model.setEntities(users.stream() //
                .map(e -> user(e, iconMap, service)) //
                .collect(Collectors.toList()));

        model.setPaging(MapperUtil.mappingTumblrPaging(paging));
        return model;
    }

    /**
     * ホスト名を取得 (プライマリを取得)
     * Get primary blog host from url
     */
    public static String getUserIdentify(List<Blog> blogs) {
        for (Blog blog : blogs) {
            if (blog.isPrimary()) {
                return getBlogIdentify(blog);
            }
        }
        return null;
    }

    /**
     * ホスト名を取得
     * Get blog host from url
     */
    public static String getBlogIdentify(Blog blog) {
        try {
            URL url = new URL(blog.getUrl());
            return url.getHost();

        } catch (Exception ignore) {
            return null;
        }
    }

}
