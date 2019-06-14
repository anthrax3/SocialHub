package net.socialhub.model.service.addition.tumblr;

import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

public class TumblrUser extends User {

    public TumblrUser(Service service) {
        super(service);
    }

    /** Count of followers */
    private Integer followersCount;

    /** Count of posts */
    private Integer postsCount;

    /** Count of likes */
    private Integer likesCount;

    /** blogUrl */
    private String blogUrl;

    //region // Getter&Setter
    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }
    //endregion
}
