package net.socialhub.model;

import net.socialhub.model.service.Service;
import net.socialhub.service.action.AccountAction;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.RequestActionImpl;

import java.io.Serializable;

/**
 * Account Model
 * (Not SNS User model)
 * アカウント情報を扱うモデル
 * (各サービス毎のユーザーではない点に注意)
 */
public class Account implements Serializable {

    /** Identify (not used by SocialHub) */
    private String tag;

    private Service service;

    private AccountAction action;

    public AccountAction action() {
        return action;
    }

    public RequestAction request() {
        return action.request();
    }

    //region // Getter&Setter
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setAction(AccountAction action) {
        this.action = action;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    //endregion
}

