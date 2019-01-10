package net.socialhub.service.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.request.oauth.OAuthAccessRequest;
import com.github.seratch.jslack.api.methods.response.oauth.OAuthAccessResponse;
import net.socialhub.define.ServiceTypeEnum;
import net.socialhub.define.service.SlackConstant;
import net.socialhub.http.HttpParameter;
import net.socialhub.model.Account;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.slack.SlackAuth.SlackAccessor;

import java.util.ArrayList;
import java.util.List;

public class SlackAuth implements ServiceAuth<SlackAccessor> {

    private String clientId;
    private String clientSecret;
    private SlackAccessor accessor;

    public SlackAuth(String clientId,
                     String clientSecret) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public SlackAccessor getAccessor() {
        return accessor;
    }

    /**
     * Authentication with AccessToken
     * アクセストークンから生成
     */
    public Account getAccountWithToken(String token) {
        this.accessor = new SlackAccessor();
        this.accessor.setSlack(Slack.getInstance());
        this.accessor.setToken(token);

        Account account = new Account();
        ServiceTypeEnum type = ServiceTypeEnum.Slack;
        Service service = new Service(type, account);
        account.setAction(new SlackAction(account, this));
        account.setService(service);
        return account;
    }

    /**
     * Get Authorization URL
     * Slack の認証ページの URL を取得
     */
    public String getAuthorizationURL(String redirectUri,
                                      String scopes) {

        List<HttpParameter> params = new ArrayList<>();
        params.add(new HttpParameter("client_id", this.clientId));
        params.add(new HttpParameter("redirect_uri", redirectUri));
        params.add(new HttpParameter("scope", scopes));

        return SlackConstant.AUTHORIZE_URL + "?" //
                + HttpParameter.encodeParameters(params.toArray(new HttpParameter[0]));
    }

    /**
     * Authentication with Code
     * 認証コードよりアカウントモデルを生成
     */
    public Account getAccountWithCode(String redirectUri,
                                      String code) {
        try {
            OAuthAccessResponse response = Slack.getInstance().methods() //
                    .oauthAccess(OAuthAccessRequest.builder() //
                            .clientId(this.clientId) //
                            .clientSecret(this.clientSecret) //
                            .redirectUri(redirectUri) //
                            .code(code) //
                            .build());

            return getAccountWithToken(response.getAccessToken());

        } catch (Exception e) {
            throw new SocialHubException(e);
        }
    }

    //region // Getter&Setter
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    //endregion

    /**
     * Slack Accessor
     * (Slack Instance And Token)
     */
    public static class SlackAccessor {
        private String token;
        private Slack slack;

        //region // Getter&Setter
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Slack getSlack() {
            return slack;
        }

        public void setSlack(Slack slack) {
            this.slack = slack;
        }
        //endregion
    }
}
