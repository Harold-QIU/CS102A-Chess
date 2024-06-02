package web;

import view.Chessboard;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server{
    public static Map<Integer, Channel> channelMap = new HashMap<>();

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(8888);
        int id =  100;
        while(true){
            Socket socket = serverSocket.accept();
            final int temp = id;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new DataOutputStream(socket.getOutputStream()).writeUTF(Chessboard.GET_ACCOUNT + "" + temp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            Channel channel = new Channel(socket, id);
            channelMap.put(id++, channel);
        }
    }



}

class Channel implements WebListener{
    private WebProxy proxy;

    public DataOutputStream outputStream;
    public DataInputStream inputStream;
    private Socket socket;
    int myID, targetID;

    public Channel(Socket socket, int id){
        this.myID = id;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            this.proxy = new WebProxy(this, socket);
            System.out.println("New user with id: " + id + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.socket = socket;
    }


    @Override
    public void gettingAction(String message) {
        String strs[] = message.split("#");
        System.out.printf("MESSAGE:\n "+strs[1]+"\n");
        try {
            targetID = Integer.parseInt(strs[0]);
            Channel targetChannel = Server.channelMap.get(targetID);
            targetChannel.outputStream.writeUTF(strs[1]);
        } catch (IOException e) {
            System.out.println("server_gettingAction");
            CloseUtil.close(outputStream, inputStream, socket);
            Server.channelMap.remove(this.myID);
            e.printStackTrace();
        } catch (NullPointerException e2){
            //todo
            e2.printStackTrace();
        } catch (NumberFormatException e3){
            e3.printStackTrace();
        }
    }


    @Override
    public void showError() {
//        Server.channelMap.get(targetID).proxy.listener.gettingAction(String.valueOf(Chessboard.DISCONNECT));
//        proxy.send(targetID +"#" + Chessboard.DISCONNECT);
//        System.out.println("server_showError");
        try {
            Server.channelMap.get(targetID).outputStream.writeUTF(String.valueOf(Chessboard.DISCONNECT) + myID);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

class StopThread extends Thread {
    private int millis;
    public StopThread(int millis) {
        this.millis = millis;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}