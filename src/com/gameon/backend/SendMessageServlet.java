package com.gameon.backend;

import com.gameon.backend.controller.Settings;
import com.gameon.backend.controller.SudokuMessageHandler;
import com.gameon.backend.networkingtypes.Packet;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SendMessageServlet extends HttpServlet {
    private final static Logger LOGGER = Logger.getLogger(SendMessageServlet.class.getName());
    private final static Gson GSON = new Gson();

    /**
     * Main request handler.
     * the request must be as POST, and contain two parameters:
     * Payload - serialized message encoded in base64
     * Date - timestamp (epoch) of the message
     *
     * from this both parameters, creates Packet object, and send it the handler function.
     * the request returns json list of packets (same parameters)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String packetPayload = request.getParameter(Settings.NO_PACKET_PAYLOAD_INCLUDED_ERROR);
        String packetDate = request.getParameter(Settings.NO_PACKET_DATE_INCLUDED_ERROR);
        if(packetPayload == null || packetPayload.equals("")) {
            LOGGER.warning("Status 404: " + Settings.NO_PACKET_PAYLOAD_INCLUDED_ERROR);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, Settings.NO_PACKET_PAYLOAD_INCLUDED_ERROR);
        }
        if(packetDate == null || packetDate.equals("")) {
            LOGGER.warning("Status 404: " + Settings.SERVLET_PACKET_DATE_PARAMETER_NAME);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, Settings.SERVLET_PACKET_DATE_PARAMETER_NAME);
        }

        long lPacketDate = 0;
        try {
            lPacketDate = Long.parseLong(packetDate);
        } catch(Exception e) {
            LOGGER.warning("Status 404: " + Settings.SERVLET_PACKET_DATE_PARAMETER_NAME);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, Settings.SERVLET_PACKET_DATE_PARAMETER_NAME);
        }

        // FINISHED HANDLING REQUEST.

        Packet[] resPackets = SudokuMessageHandler.getInstance().handleMessage(packetPayload.getBytes(), lPacketDate);

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        out.print(GSON.toJson(resPackets));
        out.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        Packet p1 = new Packet(), p2 = new Packet();
        p1.date = 111;
        p1.payload = "aaa".getBytes();
        p2.date = 222;
        p2.payload = "bbb".getBytes();

//        List<Packet> packets = new LinkedList<>();
//        packets.add(p1);
//        packets.add(p2);
        Packet[] packets = new Packet[]{p1, p2};



    }
}
