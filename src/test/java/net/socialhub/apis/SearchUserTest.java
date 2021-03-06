package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import org.junit.Test;

public class SearchUserTest extends AbstractTimelineTest {

    @Test
    public void testSearchUser_Misskey() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMisskeyAccount();
        Pageable<User> users = account.action().searchUsers("a", paging);
        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }

        paging = users.pastPage();
        Pageable<User> nexts = account.action().searchUsers("a", paging);
        for (User next : nexts.getEntities()) {
            System.out.println(next.getName());
        }
    }
}
