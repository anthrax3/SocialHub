package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Thread;
import org.junit.Test;

public class ThreadTimelineTest extends AbstractTimelineTest {

    @Test
    public void testThreadTimeline_Twitter() {
        Paging paging = new Paging();
        paging.setCount(50L);

        Account account = SocialAuthUtil.getTwitterAccount();
        Pageable<Thread> threads = account.action().getMessageThread(paging);

        if (threads.getEntities().size() > 0) {
            Pageable<Comment> messages = account.action().getMessageTimeLine(
                    threads.getEntities().get(0), paging);
            printTimeline("Messages", messages);
        }
    }

    @Test
    public void testThreadTimeline_Slack_DirectMessage() {
        Paging paging = new Paging();
        paging.setCount(50L);

        Account account = SocialAuthUtil.getSlackAccount();
        Pageable<Thread> threads = account.action().getMessageThread(paging);

        if (threads.getEntities().size() > 0) {
            for (Thread th : threads.getEntities()) {
                if (((String) th.getId()).startsWith("D")) {
                    Pageable<Comment> messages = account.action().getMessageTimeLine(th, paging);
                    printTimeline("Messages", messages);
                    return;
                }
            }
        }
    }

    @Test
    public void testThreadTimeline_Slack_GroupDirectMessage() {
        Paging paging = new Paging();
        paging.setCount(50L);

        Account account = SocialAuthUtil.getSlackAccount();
        Pageable<Thread> threads = account.action().getMessageThread(paging);

        if (threads.getEntities().size() > 0) {
            for (Thread th : threads.getEntities()) {
                if (((String) th.getId()).startsWith("G")) {
                    Pageable<Comment> messages = account.action().getMessageTimeLine(th, paging);
                    printTimeline("Messages", messages);
                    return;
                }
            }
        }
    }
}
