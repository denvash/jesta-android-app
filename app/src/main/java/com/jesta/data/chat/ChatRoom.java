package com.jesta.data.chat;

import com.jesta.data.Mission;
import com.jesta.data.Relation;
import com.jesta.data.User;
import com.jesta.utils.db.SysManager;

public class ChatRoom {
    User asker;
    User poster;
    Mission jesta;

    public ChatRoom(User asker, User poster, Mission jesta) {
        if (asker == null || poster == null || jesta == null) {
            throw new NullPointerException("none of the argument can be null!");
        }
        this.asker = asker;
        this.poster = poster;
        this.jesta = jesta;
    }

    public String getId() {
        SysManager sysManager = new SysManager();
        Relation relation = sysManager.getRelation(asker, poster, jesta);
        return relation.getId();
    }


}
