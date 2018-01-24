package com.gameon.backend.controller;

import com.gameon.shared.datatypes.Client;
import com.gameon.shared.datatypes.DifficultyType;
import com.gameon.shared.datatypes.GameType;
import com.gameon.shared.datatypes.Session;
import com.gameon.shared.messaging.MessageRequestAvailableClients;
import com.gameon.shared.messaging.MessageRequestJoin;
import com.gameon.shared.messaging.MessageRequestNewGame;
import com.gameon.shared.messaging.MessageRequestSetMove;
import com.gameon.shared.messaging.MessageRequestSms;
import com.gameon.shared.messaging.MessageResponseSession;
import com.gameon.shared.sudoku.datatypes.SudokuMove;
import com.gameon.shared.sudoku.datatypes.SudokuResultType;
import com.gameon.shared.sudoku.SudokuGameData;
import com.gameon.shared.messaging.MessageCompression;
import com.gameon.shared.messaging.MessageResponseSms;
import com.gameon.shared.messaging.MessageResponseClientList;
import com.gameon.shared.datatypes.Packet;

import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Alex on 4/28/2015.
 *
 * Managing all the incoming sudoku messages, and returns messages/interacts with DB.
 */
public class SudokuServerGameManager {
    private final static Logger LOGGER = Logger.getLogger("SudokuServerGameManager");
    private static SudokuServerGameManager _ins = null;

    private SudokuServerGameManager(){

    }

    public static SudokuServerGameManager getInstance(){
        if(_ins == null)
            _ins = new SudokuServerGameManager();
        return _ins;
    }

    public void askJoinGame(MessageRequestJoin msg){
        if(msg.client == null || msg.id == null || msg.sessionIdToJoin == null)
            return;
        Client requestingClient = msg.client;
        UUID requestingId = msg.id;
        UUID sessionIdToJoin = msg.sessionIdToJoin;

        Session session = TemporaryDB.getInstance().findSession(sessionIdToJoin);
        if(session == null)
            return;

        MessageResponseSession returnMsg = new MessageResponseSession();
        returnMsg.responseClient = requestingClient;
        returnMsg.responseId = requestingId;
        returnMsg.activeSession = session;

        Packet p = new Packet();
        p.date = System.currentTimeMillis();
        p.payload = Base64.getEncoder().encodeToString(MessageCompression.getInstance().compress(returnMsg));

        MessageQueues.getInstance().addPacket(requestingClient, p);
    }

    public void getUsers(MessageRequestAvailableClients msg){
        if(msg.client == null || msg.id == null)
            return;
        Client requestingClient = msg.client;
        UUID requestingId = msg.id;

        Client[] clients = TemporaryDB.getInstance().getPlayingClients();
        MessageResponseClientList returnMsg = new MessageResponseClientList();
        returnMsg.clients = clients;
        returnMsg.responseId = requestingId;
        returnMsg.responseClient = requestingClient;

        Packet p = new Packet();
        p.date = System.currentTimeMillis();
        p.payload = Base64.getEncoder().encodeToString(MessageCompression.getInstance().compress(returnMsg));

        MessageQueues.getInstance().addPacket(requestingClient, p);
    }

    public void sendSetMoveToGame(MessageRequestSetMove msg) {
        if(msg.client == null || msg.client.getCurrSessionId() == null || msg.move == null)
            return;
        SudokuMove move = msg.move;
        UUID gameId = msg.client.getCurrSessionId();

        SudokuResultType result = TemporaryDB.getInstance().findSession(gameId)
                .getGameData().setMove(move);
        if(result != SudokuResultType.SUCCESS) {
            //TODO: what to do ?
        }
        else {
            LOGGER.info("submitted successful move");
        }
    }

    public void sendTextMessage(MessageRequestSms msg) {
        if(msg.client == null || msg.id == null || msg.text == null
                || msg.client.getCurrSessionId() == null || msg.client.getName() == null)
            return;
        String name = msg.client.getName();
        String text = msg.text;
        UUID gameId = msg.client.getCurrSessionId();
        Client clientExcluded = msg.client;
        UUID responseId = msg.id;

        Session session = null;
        session = TemporaryDB.getInstance().findSession(gameId);
        if(session == null)
            return;
        LOGGER.info("about to post text message to session " + session.getSessionId());
        for(Client client: session.getClientList()) {
            if(client.equals(clientExcluded))
                continue;

            LOGGER.info("about to post text message to client " + client.getId());

            MessageResponseSms returnMsg = new MessageResponseSms();
            returnMsg.responseClient = client;
            returnMsg.responseId = responseId;
            returnMsg.name = name;
            returnMsg.text = text;

            Packet p = new Packet();
            p.date = System.currentTimeMillis();
            p.payload = Base64.getEncoder().encodeToString(MessageCompression.getInstance().compress(returnMsg));

            MessageQueues.getInstance().addPacket(client, p);
        }
    }

    public void startNewGame(MessageRequestNewGame msg) {
        if(msg.client == null || msg.id == null || msg.gameType == null || msg.difficultyType == null)
            return;
        Client requestingClient = msg.client;
        UUID requestingId = msg.id;
        GameType gameType = msg.gameType;
        DifficultyType difficultyType = msg.difficultyType;

        SudokuGameData gameData = new SudokuGameData(difficultyType);
        Session session = new Session(GameType.SUDOKU, gameData);
        TemporaryDB.getInstance().addAndReplaceSession(session);

        MessageResponseSession sessionMsg = new MessageResponseSession();
        sessionMsg.responseClient = requestingClient;
        sessionMsg.responseId = requestingId;
        sessionMsg.activeSession = session;

        Packet p = new Packet();
        p.date = System.currentTimeMillis();
        p.payload = Base64.getEncoder().encodeToString(MessageCompression.getInstance().compress(sessionMsg));

        MessageQueues.getInstance().addPacket(requestingClient, p);
    }
}
