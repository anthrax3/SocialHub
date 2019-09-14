package net.socialhub.utils;

import net.socialhub.SocialHub;
import net.socialhub.define.ServiceType;
import org.junit.Test;

public class ServiceUtilTest {

    @Test
    public void testCommentLengthLevel() {

        String text = "Get Comment Length Level for Post Comment.";
        float level = SocialHub.getUtilServices().getCommentLengthLevel(text, ServiceType.Mastodon);

        System.out.println("Text  : " + text);
        System.out.println("Level : " + level);
    }
}
