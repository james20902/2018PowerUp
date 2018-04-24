package frc.team5115;
import java.io.*;
import java.net.*;

public class UDPClient extends Thread {
    DatagramSocket sock;
    DatagramPacket response;
    InetAddress hostname;
    byte[] buffer = new byte[1024];

    private String lastResponse = "null";

    public UDPClient(String host, int port) {
        try {
            hostname = InetAddress.getByName(host);
            sock = new DatagramSocket(port);
            response = new DatagramPacket(buffer, buffer.length);
            //if something breaks
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAsString(){
        try {
            sock.receive(response);
            String packet = new String(buffer, 0, response.getLength());
            System.out.println(packet);
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
            return "something broke sorry";
        }

    }

    public String getLastResponse() {
        return lastResponse;
    }

    public void run() {
        while (true) {
            lastResponse = getAsString();
        }
    }

}