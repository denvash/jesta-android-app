package com.jesta;
import com.jesta.data.chat.Author;
import com.jesta.data.chat.Message;
import org.junit.Test;
import org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Date;

public class ChatMessageTest {
    @Test
    public void gettersSetters() throws Exception {
        String msgContent = "Message content";

        Author author = new Author("132-fgc-231", "Kimchi", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Circle-icons-profile.svg/512px-Circle-icons-profile.svg.png");
        Message message = new Message("123-xty-256", author, new Date(), msgContent);

        assertNotEquals(author.getId(), null);
        assertNotEquals(author.getAvatar(), null);
        assertNotEquals(author.getImageUrl(), null);
        assertNotEquals(author.getId(), null);

        assertEquals(message.getAuthor().getId(), author.getId());
        assertEquals(message.getText(), msgContent);
    }
}
