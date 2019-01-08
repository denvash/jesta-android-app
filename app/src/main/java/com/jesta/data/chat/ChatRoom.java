package com.jesta.data.chat;

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

    public User getAsker() {
        return asker;
    }

    public User getPoster() {
        return poster;
    }

    public Mission getJesta() {
        return jesta;
    }
}
