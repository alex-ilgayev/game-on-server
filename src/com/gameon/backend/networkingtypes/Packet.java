package com.gameon.backend.networkingtypes;

import java.io.Serializable;

/**
 * Created by Alex on 4/11/2015.
 * This class represents a transmitted packet between client and server.
 */
public class Packet implements Serializable{
    public byte[] payload;
    public long date;
}
