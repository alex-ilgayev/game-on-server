package com.gameon.gameon.messaging;

/**
 * Created by Alex on 4/10/2015.
 */
public enum MessageType {
    REQUEST_JOIN,
    REQUEST_NEW_GAME,
    REQUEST_AVAILABLE_CLIENTS,
    REQUEST_SET_MOVE,
    REQUEST_POLL_MESSAGE_QUEUE,
    REQUEST_SMS,
    INNER_CONNECTION_STATUS,
    RESPONSE_CLIENT_LIST,
    RESPONSE_ERROR,
    RESPONSE_SESSION,
    RESPONSE_SMS,
}