package com.gameon.gameon.messaging;

import com.gameon.gameon.datatypes.Client;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Alex on 4/28/2015.
 */
public class MessageRequestJoin implements IMessage, Serializable{
    private static final long serialVersionUID = 1L;

    public Client client;
    public UUID id;

    public UUID sessionIdToJoin;

    @Override
    public MessageType getMessageType() {
        return MessageType.REQUEST_JOIN;
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public UUID getId(){
        return id;
    }
}
