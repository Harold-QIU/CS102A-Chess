package view;

import model.*;
import web.WebListener;
import web.WebProxy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Chessboard_web extends Chessboard implements WebListener {

    private TextArea chatDisplay;

    public Chessboard_web(int width, int height, JLabel statusLabel, JLabel roundLabel, JDialog singularEvent, String ip, JLabel myIDLabel, TextArea chatDisplay) {
        super(width, height, statusLabel, roundLabel, singularEvent, ip, myIDLabel);
        proxy = new WebProxy(this, ip, (short)8888);
        this.chatDisplay = chatDisplay;

    }

    @Override
    public void showError() {
        JOptionPane.showMessageDialog(null, "web error occurs");
    }

    @Override
    public void gettingAction(String message) {
//        loadGame();
        process(message.charAt(0), message.substring(1));
    }

    boolean isConnect;

    public void process(char flag, String message){
        switch (flag) {
            case INVITE_CONNECT -> {
                int a = JOptionPane.showConfirmDialog(null, "someone hopes to connect to you! his ID:" + message, "Connect", JOptionPane.YES_NO_CANCEL_OPTION);
                System.out.println("ck:chessboard_process_invite:" + a);
                if (a == 0) {
                    isConnect = true;
                    this.opponentID = message;
                    proxy.send(opponentID + "#" + ACCEPT_CONNECT + myID);
                    myColor = ChessColor.BLACK;
                } else {
                    proxy.send(opponentID + "#" + REJECT_CONNECT);
                }
            }
            case ACCEPT_CONNECT -> {
                isConnect = true;
                opponentID = message;
                JOptionPane.showMessageDialog(null, "successfully connected to: " + opponentID);
                myColor = ChessColor.WHITE;
            }
            case REJECT_CONNECT -> JOptionPane.showMessageDialog(null, "the person had rejected your invitation.");
            case GET_ACCOUNT -> {
                System.out.println("MyId" + message);
                new Thread(()->{
                    myID = message;
                    repaint();
                }).start();

//                if (myIDLabel != null) {
//                    myIDLabel.repaint();
//                }
            }
            case DISCONNECT -> {
                System.out.println("disconnect");
                if (message.equals(opponentID)) {
                    JOptionPane.showMessageDialog(null, "your opponent had run away");
                    isConnect = false;
                    opponentID = "";
                }
                for (String id: witnessID) {
                    if (message.equals(id)) {
                        JOptionPane.showMessageDialog(null, String.format("The witness with ID %s had run away", id));
                        witnessID.remove(id);
                    }
                }

            }
            case MOVE -> {
                System.out.println("Get chess moved");
                List<String> chessData = List.of(message.split("\n"));
                this.loadGame(chessData);
                this.repaint();
                this.roundLabel.repaint();
                this.statusLabel.repaint();
                this.clickController.addJudgePane();

            }
            case RESET -> {
                System.out.println("Reset the chessboard!");
                List<String> chessData = List.of(Chessboard.defaultChessboard.split("\n"));
                this.loadGame(chessData);
            }
            case SEND_BOARD -> {
                System.out.println("Acquired the battle board!");
                List<String> chessData = List.of(message.split("\n"));
                this.loadGame(chessData);
                this.clickController.addJudgePane();
            }
            case REQUEST_WITNESS -> {
                int a_2 = JOptionPane.showConfirmDialog(null, "someone hopes to witness your battle! his ID:" + message, "Allow", JOptionPane.YES_NO_CANCEL_OPTION);
                System.out.println("ck:chessboard_process_invite:" + a_2);
                if (a_2 == 0) {
                    witnessID.add(message);
                    proxy.send(message + "#" + ACCEPT_WITNESS + this.writeChessboard());
                    if (opponentID != "" && opponentID != null) {
                        StopThread stopper = new StopThread(200);
                        stopper.run();
                        proxy.send(opponentID + "#" + ADD_WITNESS + message);
                    }
                } else {
                    proxy.send(message + "#" + REJECT_CONNECT);
                }
            }
            case ACCEPT_WITNESS -> {
                isConnect = true;
                JOptionPane.showMessageDialog(null, "successfully connected, now the battle board is:\n " + message);
                List<String> chessData = List.of(message.split("\n"));
                this.loadGame(chessData);
                myColor = ChessColor.NONE;
            }
            case ADD_WITNESS -> {
                witnessID.add(message);
            }
            case SEND_CHAT -> {
                this.chatDisplay.setText(message);
                this.chatDisplay.setCaretPosition(chatDisplay.getText().length());
                this.chatRecord = new ArrayList<>(List.of(message.split("\n")));//List.of给我返回了个immutableConnections，我就用一个构造器给它解决
                chatDisplay.repaint();
            }
        }
    }


    @Override
    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        //TODO 储存变化前的三大参数(swapLocation改了两个),之后写悔棋时一定要注意写把storage给还原的操作
        lastStep = this.writeOneChessboard();
        stepStorage.add(lastStep);

        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, mouseEnter, CHESS_SIZE,this));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    chessComponents[i][j].getMouseEnter().setEnter(null);
                }
            }
        }


        //play chess music;
        ChessMusic chessMusic = new ChessMusic("images/chessMusic.mp3");
        chessMusic.start();

        //王车移位: attention: 现在chess2和chess1还没有交换
        if (chess1 instanceof KingChessComponent) {
            ChessComponent chessRook = (ChessComponent) ChessComponent.map.get(chess2);
            if (chessRook instanceof RookChessComponent) {
                if (chessRook.getChessboardPoint().getY() > chess1.getChessboardPoint().getY()) {
                    ChessComponent target = chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()-1];
                    chessRook.swapLocation(target);
                    int row1 = chessRook.getChessboardPoint().getX(), col1 = chessRook.getChessboardPoint().getY();
                    chessComponents[row1][col1] = chessRook;
                    int row2 = target.getChessboardPoint().getX(), col2 = target.getChessboardPoint().getY();
                    chessComponents[row2][col2] = target;
                } else if (chessRook.getChessboardPoint().getY() < chess1.getChessboardPoint().getY()){
                    ChessComponent target = chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()+1];
                    chessRook.swapLocation(target);
                    int row1 = chessRook.getChessboardPoint().getX(), col1 = chessRook.getChessboardPoint().getY();
                    chessComponents[row1][col1] = chessRook;
                    int row2 = target.getChessboardPoint().getX(), col2 = target.getChessboardPoint().getY();
                    chessComponents[row2][col2] = target;
                }
            }
            ChessComponent.map = null;
        }

        //和空棋子交换位置
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;



        //在这里写特殊走法
        //吃过路兵
        if (ChessComponent.pawnOnTheWay != null) {
            ChessComponent toRemoved = chessComponents[ChessComponent.pawnOnTheWay.getX()][ChessComponent.pawnOnTheWay.getY()];
            remove(toRemoved);
            add(toRemoved = new EmptySlotComponent(toRemoved.getChessboardPoint(),toRemoved.getLocation() , clickController, mouseEnter, CHESS_SIZE, this));
            toRemoved.repaint();
            chessComponents[toRemoved.getChessboardPoint().getX()][toRemoved.getChessboardPoint().getY()] = toRemoved;
        }
        //兵升变
        if (chess1 instanceof PawnChessComponent && (chess1.getChessboardPoint().getX() == 0 || chess1.getChessboardPoint().getX()==7)) {
            Object[] options = new Object[]{"Queen", "Rook", "Knight", "Bishop"};
            int optionSelected = JOptionPane.showOptionDialog(this, "Please select a Promotion", "Pawn's promotion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            if (optionSelected == 0) {
                System.out.println("Queen Promotion");
                remove(chess1);
                add(chess1 = new QueenChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
            } else if (optionSelected == 1) {
                System.out.println("Rook Promotion");
                remove(chess1);
                add(chess1 = new RookChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
            } else if (optionSelected == 2) {
                System.out.println("Knight Promotion");
                remove(chess1);
                add(chess1 = new KnightChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
            } else if (optionSelected == 3) {
                System.out.println("Bishop Promotion");
                remove(chess1);
                add(chess1 = new BishopChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
            } else {
                remove(chess1);
                add(chess1 = new QueenChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
            }
            chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()] = chess1;
        }

        roundTimeMul2++;
        this.roundLabel.setText(String.format("Round: %d", roundTimeMul2/2));
        roundLabel.repaint();

        chess1.repaint();
        chess2.repaint();

        //向对手发送移动信息
        this.proxy.send(opponentID + "#" + MOVE + this.writeChessboard());
        if (witnessID.size() != 0) {
            for (String s : witnessID) {
                StopThread myThread = new StopThread(20);
                myThread.run();
                this.proxy.send(s + "#" + MOVE + this.writeChessboard());
                System.out.println(this.writeChessboard());
            }

        }
    }
}
