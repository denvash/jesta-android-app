package com.jesta.data;

import androidx.annotation.NonNull;

public class Topic
{

    private final TopicDescriptor topicDescriptor;
    private final User sender;
    private final Mission jesta;
    private String name;

    public Topic(TopicDescriptor topicDescriptor, User sender, Mission jesta) {
        this.topicDescriptor = topicDescriptor;
        this.sender = sender;
        this.jesta = jesta;

        String delimiter = "_";
        String name = sender.getId() + delimiter + topicDescriptor.toString();
        this.name = jesta != null ? (name + delimiter + jesta.getId()) : name;

    }

    @NonNull
    @Override
    public String toString() {
        String delimiter = "_";
        String str = sender.getId() + delimiter + topicDescriptor.toString();
        return jesta != null ? (str + delimiter + jesta.getId()) : str;
    }

    public String topicName() {
        return name;
    }
}