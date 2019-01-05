package com.jesta.utils.chat;

import com.jesta.data.Mission;
import com.jesta.data.User;

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
        String sortedIds;
        sortedIds = poster.getId().compareTo(asker.getId()) < 0 ? poster.getId() + "_" + asker.getId() : asker.getId() + "_" + poster.getId();
        return sortedIds + "_" + jesta.getId();
    }


}
