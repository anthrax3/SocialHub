package net.socialhub.service.slack;

import net.socialhub.define.service.slack.SlackFormKey;
import net.socialhub.model.Account;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Identify;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;

public class SlackRequest extends RequestActionImpl {

    public SlackRequest(Account account) {
        super(account);
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        SlackAction action = (SlackAction) account.action();
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getHomeTimeLine();

        request.setCommentFormSupplier(() -> {
            CommentForm form = new CommentForm();
            form.param(SlackFormKey.CHANNEL_KEY, action.getGeneralChannel());
            return form;
        });
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getChannelTimeLine(Identify id) {
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getChannelTimeLine(id);

        request.setCommentFormSupplier(() -> {
            CommentForm form = new CommentForm();
            form.param(SlackFormKey.CHANNEL_KEY, id.getId());
            return form;
        });
        return request;
    }
}
