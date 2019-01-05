package com.jesta.utils.chat;

import com.jesta.data.Mission;
import com.jesta.data.User;

public class ChatRoom {
    User asker;
    User doer;
    Mission jesta;

    public ChatRoom(User asker, User doer, Mission jesta) {
        if (asker == null || doer == null || jesta == null) {
            throw new NullPointerException("none of the argument can be null!");
        }
        this.asker = asker;
        this.doer = doer;
        this.jesta = jesta;
    }

    public String getId() {
        String sortedIds;
        sortedIds = doer.getId().compareTo(asker.getId()) < 0 ? doer.getId() + "_" + asker.getId() : asker.getId() + "_" + doer.getId();
        return sortedIds + "_" + jesta.getId();
    }


}
