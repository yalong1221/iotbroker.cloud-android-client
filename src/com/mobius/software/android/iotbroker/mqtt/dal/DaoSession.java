package com.mobius.software.android.iotbroker.mqtt.dal;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.mobius.software.android.iotbroker.mqtt.dal.Messages;
import com.mobius.software.android.iotbroker.mqtt.dal.Topics;
import com.mobius.software.android.iotbroker.mqtt.dal.Accounts;

import com.mobius.software.android.iotbroker.mqtt.dal.MessagesDao;
import com.mobius.software.android.iotbroker.mqtt.dal.TopicsDao;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountsDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig messagesDaoConfig;
    private final DaoConfig topicsDaoConfig;
    private final DaoConfig accountsDaoConfig;

    private final MessagesDao messagesDao;
    private final TopicsDao topicsDao;
    private final AccountsDao accountsDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        messagesDaoConfig = daoConfigMap.get(MessagesDao.class).clone();
        messagesDaoConfig.initIdentityScope(type);

        topicsDaoConfig = daoConfigMap.get(TopicsDao.class).clone();
        topicsDaoConfig.initIdentityScope(type);

        accountsDaoConfig = daoConfigMap.get(AccountsDao.class).clone();
        accountsDaoConfig.initIdentityScope(type);

        messagesDao = new MessagesDao(messagesDaoConfig, this);
        topicsDao = new TopicsDao(topicsDaoConfig, this);
        accountsDao = new AccountsDao(accountsDaoConfig, this);

        registerDao(Messages.class, messagesDao);
        registerDao(Topics.class, topicsDao);
        registerDao(Accounts.class, accountsDao);
    }
    
    public void clear() {
        messagesDaoConfig.getIdentityScope().clear();
        topicsDaoConfig.getIdentityScope().clear();
        accountsDaoConfig.getIdentityScope().clear();
    }

    public MessagesDao getMessagesDao() {
        return messagesDao;
    }

    public TopicsDao getTopicsDao() {
        return topicsDao;
    }

    public AccountsDao getAccountsDao() {
        return accountsDao;
    }

}