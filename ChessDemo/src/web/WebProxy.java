package web;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WebProxy {
    WebListener listener;
    Socket socket;
    private Receiver receiver;
    private Sender sender;

    public WebProxy(WebListener listener, String ip, short port){
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            listener.showError();
        }
        this.listener = listener;
        try {
            this.receiver = new Receiver(socket, listener);
            this.sender = new Sender(socket, listener);
        }catch (IOException e){
            listener.showError();
        }
    }

    public WebProxy(WebListener listener, Socket socket){
        this.socket = socket;
        this.listener = listener;
        try {
            this.receiver = new Receiver(socket, listener);
            this.sender = new Sender(socket, listener);
        }catch (IOException e){
            listener.showError();
        }
    }

    public void send(String message){
        sender.send(message);
    }
}

class Receiver implements Runnable{
    private DataInputStream inputStream;
    private Socket socket;
    private boolean flag;

    private WebListener listener;
    private String message = "";
    public Receiver(Socket socket, WebListener listener) throws IOException {
        this.listener = listener;
        flag = true;
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        new Thread(this).start();
    }

    private String receive() throws IOException {
        String s = inputStream.readUTF();
        return s;
    }


    @Override
    public void run() {
        while (flag){
            try {
                String s = this.receive();
                listener.gettingAction(s);
            } catch (IOException e) {
                System.out.println("网络异常");
                listener.showError();
                CloseUtil.close(inputStream, socket);
                flag = false;
            }
        }
    }
}

class Sender implements Runnable, Closeable{
    private DataOutputStream outputStream;
    private Socket socket;
    private String message="";
    private WebListener listener;

    public Sender(Socket socket, WebListener listener) throws IOException {
        this.listener = listener;
        this.socket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String s){
        this.message = s;
        System.out.println("message = "+ s );
        new Thread(this).start();
    }


    @Override
    public void run() {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            this.listener.showError();
            CloseUtil.close(outputStream, socket);
        }
    }

    @Override
    public void close() throws IOException {

    }
}
