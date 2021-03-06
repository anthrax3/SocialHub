package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Notification;
import net.socialhub.model.service.Service;

public class MisskeyNotification extends Notification {

    private String reaction;

    private String iconUrl;

    public MisskeyNotification(Service service) {
        super(service);
    }

    // region
    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    // endregion
}
