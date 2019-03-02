package net.socialhub.model.service.addition;

import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Slack における User 要素
 * Slack specified user's attributes
 */
public class SlackUser extends User {

    public SlackUser(Service service) {
        super(service);
    }

    /** email */
    private AttributedString email;

    /** phone */
    private AttributedString phone;

    /** what user's do */
    private String title;

    /** display name */
    private String displayName;

    @Override
    public List<AttributedFiled> getAdditionalFields() {
        List<AttributedFiled> fields = new ArrayList<>();
        fields.add(new AttributedFiled("Email", getEmail()));
        fields.add(new AttributedFiled("Phone", getPhone()));
        return fields;
    }

    //region // Getter&Setter
    public AttributedString getEmail() {
        return email;
    }

    public void setEmail(AttributedString email) {
        this.email = email;
    }

    public AttributedString getPhone() {
        return phone;
    }

    public void setPhone(AttributedString phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    //endregion
}
