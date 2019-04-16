package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.AccountGroup;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.service.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * グループアクション
 */
public class AccountGroupActionImpl implements AccountGroupAction {

    private AccountGroup accountGroup;

    public AccountGroupActionImpl(AccountGroup accountGroup) {
        this.accountGroup = accountGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Account, User> getUserMe() {
        return getAccountGroup().getAccounts().parallelStream() //
                .collect(Collectors.toMap(Function.identity(), //
                        (acc) -> acc.action().getUserMe()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getHomeTimeLine() {
        CommentGroupImpl model = new CommentGroupImpl();
        List<Account> accounts = getAccountGroup().getAccounts();

        model.setEntities(accounts.parallelStream() //
                .collect(Collectors.toMap(Function.identity(), //
                        (acc) -> acc.action().getHomeTimeLine(null))));

        model.setSinceDateFromEntities();
        model.setMaxDate(new Date());
        return model;
    }

    //region // Getter&Setter
    public AccountGroup getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(AccountGroup accountGroup) {
        this.accountGroup = accountGroup;
    }
    //endregion
}
