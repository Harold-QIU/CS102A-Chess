package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame implements ActionListener, KeyListener{
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;
    private Chessboard chessboard;
    private JLabel statusLabel;

    private JLabel roundLabel;

    private JLabel statusFrame;

    private JDialog singularEvent;

    public JButton witnessButton;

    public JLabel myIDLabel;

    private JButton webButton;

    private String ip = "";

    private GameModel gameModel;

    private JLabel chessboardBackground;

    private JButton changeTheme;

    private String backgroundPath;

    private JLabel background;

    private String chessboardPath;

    private TextArea chatDisplay;

    private TextField chatField;

    private JButton send;

    private String chat = "";

//    JButton musicStart;

//    JButton musicStop;

//    Music music;

    public ChessGameFrame(int width, int height, GameModel gameModel, String ip) {
        if (gameModel == GameModel.WebModel || gameModel == GameModel.WitnessModel) {
            this.ip = ip;
        }
        this.gameModel  = gameModel;
        setTitle("Western Chess"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;

        this.backgroundPath = "images/background.jpg";
        this.chessboardPath = "images/chessboard4.jpg";

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);
        this.background = addBackground();

        this.chessboardBackground = addChessboardBackground();
        this.statusFrame = addStatusFrame();
        this.statusLabel = addStatusLabel();
        this.roundLabel = addRoundLabel();
        this.singularEvent = addSingularEvent();

        if (gameModel == GameModel.WebModel) {
            this.chatDisplay = addChatArea();
            this.chatField = addChat();
            this.send = addSend();
        }

        addChessboard();
        addResetButton();
        if (!(this.chessboard instanceof Chessboard_ai) && !(this.chessboard instanceof Chessboard_web)) {
            addLoadButton();
            addStoreButton();
        }
        addWithdrawButton();
        this.changeTheme = addChange();
        if (gameModel == GameModel.WebModel) {
            StopThread stopper = new StopThread(100);
            stopper.run();
            addWebButton();
            this.myIDLabel = addMyID();
            myIDLabel.repaint();
            webButton.repaint();
        }
        if (gameModel == GameModel.WitnessModel) {
            StopThread stopper = new StopThread(100);
            stopper.run();
            addWitnessButton();
            this.myIDLabel = addMyID();
            myIDLabel.repaint();
            witnessButton.repaint();
        }


        //播放背景音乐
//        music = new Music("images/22.mp3");

//        this.musicStart = addMusicStart();
//        this.musicStop = addMusicStop();
//        MyListener myListener = new MyListener();
//        musicStart.addActionListener(myListener);
//        musicStop.addActionListener(myListener);
        background.setIcon(new ImageIcon("images/图2.jpeg"));
        background.repaint();
        setChessboardPath("images/chessboard3.jpg");
        addChessboardBackground().add(chessboard);
        chessboardBackground.repaint();
        chessboard.repaint();
        statusLabel.setForeground(Color.YELLOW);
        roundLabel.setForeground(Color.YELLOW);
    }


    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        if (gameModel == GameModel.WebModel) {
            chessboard = new Chessboard_web(CHESSBOARD_SIZE, CHESSBOARD_SIZE, statusLabel, roundLabel, singularEvent, ip, myIDLabel, chatDisplay);
        } else if (gameModel == GameModel.LocalModel) {
            chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE, statusLabel, roundLabel, singularEvent, ip, myIDLabel);
        } else if (gameModel == GameModel.WitnessModel) {
            chessboard = new Chessboard_witness(CHESSBOARD_SIZE, CHESSBOARD_SIZE, statusLabel, roundLabel, singularEvent, ip, myIDLabel, chatDisplay);
        } else {
            chessboard = new Chessboard_ai(CHESSBOARD_SIZE, CHESSBOARD_SIZE, statusLabel, roundLabel, singularEvent, ip, myIDLabel);
        }
        gameController = new GameController(chessboard);
        chessboard.setLocation(0, 0);
        chessboardBackground.add(chessboard);
    }


    /**
     * 在游戏面板中添加标签
     */
    private JLabel addStatusLabel() {
        JLabel statusLabel = new JLabel("Round for White");
//        statusLabel.setLocation(WIDTH * 760 / 1000, HEIGHT / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Times New Roman", Font.BOLD + Font.ITALIC, 18));
        statusLabel.setForeground(Color.CYAN);
        statusFrame.add(statusLabel);
        statusLabel.setLocation(20, 0);
        return statusLabel;
    }



    private JLabel addRoundLabel() {
        JLabel roundLabel = new JLabel("Round: 1");
//        roundLabel.setLocation(WIDTH/ 4, HEIGHT/ 15);
        roundLabel.setSize(120, 30);
        roundLabel.setFont(new Font("Times New Roman", Font.BOLD + Font.ITALIC, 18));
        roundLabel.setForeground(Color.CYAN);
        statusFrame.add(roundLabel);
        roundLabel.setLocation(20, 60);
        return roundLabel;
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addResetButton() {
        JButton button = new JButton(new ImageIcon("button/reset.png"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断是否有棋子被选中
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        System.out.println(chessboard.chessComponents[i][j].isSelected());
                        if (chessboard.chessComponents[i][j].isSelected()) {
                            JOptionPane.showMessageDialog(null, "Can't reset with chess selected");
                            return;
                        }
                    }
                }

                List<String> chessData = List.of(Chessboard.defaultChessboard.split("\n"));
                chessboard.loadGame(chessData);
                if (chessboard instanceof Chessboard_web) {
                    chessboard.proxy.send(chessboard.opponentID +"#"+ Chessboard.RESET +chessboard.myID);
                }
            }
        });
        button.setLocation(HEIGHT + 10, HEIGHT / 10 + 160);
        button.setSize(150, 45);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {//load有个大bug，不能选中一个棋子后再load
        JButton button = new JButton(new ImageIcon("button/load.png"));
        button.setLocation(HEIGHT + 10, HEIGHT / 10 + 300);
        button.setSize(150, 45);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            //判断是否有棋子被选中
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    System.out.println(chessboard.chessComponents[i][j].isSelected());
                    if (chessboard.chessComponents[i][j].isSelected()) {
                        JOptionPane.showMessageDialog(null, "Can't load with chess selected");
                        return;
                    }
                }
            }

            System.out.println("Click load");
            JFileChooser chooser = new JFileChooser("D:\\ChessboardStorage");
            int openDialog = chooser.showOpenDialog(chessboard);
            if (chooser.getSelectedFile() != null) {
                String path = chooser.getSelectedFile().toString();
                File file = new File(path);
                if (getFileExtension(file).equals("txt")) {
                    gameController.loadGameFromFile(path);
                } else {
                    System.out.println();
                    JOptionPane.showMessageDialog(chessboard, "104：支持的存储文件是txt，导入的是"+getFileExtension(file));
                }
            }
            chessboard.repaint();
            statusLabel.repaint();
            roundLabel.repaint();
        });
    }


    private void addStoreButton() {
        JButton button = new JButton(new ImageIcon("button/store.png"));
        button.setLocation(HEIGHT + 10, HEIGHT / 10 + 370);
        button.setSize(150, 45);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            //判断是否有棋子被选中
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    System.out.println(chessboard.chessComponents[i][j].isSelected());
                    if (chessboard.chessComponents[i][j].isSelected()) {
                        JOptionPane.showMessageDialog(null, "Can't store with chess selected");
                        return;
                    }
                }
            }

            System.out.println("Click store");
            JFileChooser chooser = new JFileChooser("D:\\ChessboardStorage");
            chooser.showSaveDialog(chessboard);
            if (chooser.getSelectedFile() != null) {
                String path = chooser.getSelectedFile().toString();
                this.chessboard.writeChessboard(path);
            }
            roundLabel.repaint();
        });
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private void addWithdrawButton() {
        JButton button = new JButton(new ImageIcon("button/undo.png"));
        button.setLocation(HEIGHT + 10, HEIGHT / 10 + 230);
        button.setSize(150, 45);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            //判断是否有棋子被选中
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    System.out.println(chessboard.chessComponents[i][j].isSelected());
                    if (chessboard.chessComponents[i][j].isSelected()) {
                        JOptionPane.showMessageDialog(null, "Can't undo with chess selected");
                        return;
                    }
                }
            }

            System.out.println("Click withdraw");
            if (this.chessboard.roundTimeMul2 != 2) {
                if (chessboard instanceof Chessboard_ai) {
                    List<String> chessData = List.of(chessboard.lastStep.split("\n"));
                    chessboard.loadOneGame(chessData);
                    chessboard.stepStorage.remove(chessboard.stepStorage.size() - 1);
                    if (this.chessboard.roundTimeMul2 != 2) {
                        chessboard.lastStep = chessboard.stepStorage.get(chessboard.stepStorage.size() - 1);
                    }
                }

                List<String> chessData = List.of(chessboard.lastStep.split("\n"));
                chessboard.loadOneGame(chessData);
                chessboard.stepStorage.remove(chessboard.stepStorage.size() - 1);
                if (this.chessboard.roundTimeMul2 != 2) {
                    chessboard.lastStep = chessboard.stepStorage.get(chessboard.stepStorage.size() - 1);
                }

                chessboard.repaint();
                statusLabel.repaint();
                roundLabel.repaint();
            } else {
                singularEvent("Can't withdraw since it has been the initial stage");
            }
            this.chessboard.proxy.send(chessboard.opponentID + "#" + Chessboard.MOVE + this.chessboard.writeChessboard());
            if (Chessboard.witnessID.size() != 0) {
                for (String s : Chessboard.witnessID) {
                    StopThread myThread = new StopThread(20);
                    myThread.run();
                    this.chessboard.proxy.send(s + "#" + Chessboard.MOVE + this.chessboard.writeChessboard());
                    System.out.println(this.chessboard.writeChessboard());
                }
            }
        });
    }

    public void addWitnessButton() {
        JButton button = new JButton(new ImageIcon("button/connect.png"));
        this.witnessButton = button;
        button.setLocation(HEIGHT + 10, HEIGHT / 10 + 440);
        button.setSize(150, 45);
        button.setVisible(true);
        add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断是否有棋子被选中
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        System.out.println(chessboard.chessComponents[i][j].isSelected());
                        if (chessboard.chessComponents[i][j].isSelected()) {
                            JOptionPane.showMessageDialog(null, "Can't do with chess selected");
                            return;
                        }
                    }
                }

                String s = (String)JOptionPane.showInputDialog(null, "input", null);
                if(s==null)return;
                chessboard.proxy.send(s+"#"+Chessboard.REQUEST_WITNESS+chessboard.myID);
            }
        });
    }

    private JButton addChange(){
        JButton change = new JButton(new ImageIcon("button/change.png"));
        change.setVisible(true);
        change.setSize(150, 45);
        change.setLocation(HEIGHT + 10, HEIGHT / 10 + 510);
        change.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断是否有棋子被选中
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        System.out.println(chessboard.chessComponents[i][j].isSelected());
                        if (chessboard.chessComponents[i][j].isSelected()) {
                            JOptionPane.showMessageDialog(null, "Can't do with chess selected");
                            return;
                        }
                    }
                }

                String[] a = {"Theme1", "Theme 2", "Theme 3", "Theme 4"};
                String theme = (String) JOptionPane.showInputDialog(null, "change theme", "change theme", JOptionPane.QUESTION_MESSAGE , null, a, a[0]);
                if (theme != null) {
                    if (theme.equals(a[0])) {
                        System.out.println("Theme 1");
                        background.setIcon(new ImageIcon("images/图1.jpg"));
                        background.repaint();
                        setChessboardPath("images/chessboard2.jpg");
                        addChessboardBackground().add(chessboard);
                        chessboardBackground.repaint();
                        chessboard.repaint();
                        statusLabel.setForeground(Color.WHITE);
                        roundLabel.setForeground(Color.WHITE);
                        repaint();
                    } else if (theme.equals(a[1])) {
                        System.out.println("Theme 2");
                        background.setIcon(new ImageIcon("images/图2.jpeg"));
                        background.repaint();
                        setChessboardPath("images/chessboard3.jpg");
                        addChessboardBackground().add(chessboard);
                        chessboardBackground.repaint();
                        chessboard.repaint();
                        statusLabel.setForeground(Color.YELLOW);
                        roundLabel.setForeground(Color.YELLOW);
                        repaint();
                    } else if (theme.equals(a[2])) {
                        System.out.println("Theme 3");
                        background.setIcon(new ImageIcon("images/background.jpg"));
                        background.repaint();
                        setChessboardPath("images/chessboard4.jpg");
                        addChessboardBackground().add(chessboard);
                        chessboardBackground.repaint();
                        chessboard.repaint();
                        statusLabel.setForeground(Color.RED);
                        roundLabel.setForeground(Color.RED);
                        repaint();
                    }else if (theme.equals(a[3])){
                        System.out.println("Theme 4");
                        background.setIcon(new ImageIcon("images/图4.jpg"));
                        background.repaint();
                        setChessboardPath("images/chessboard4.jpg");
                        addChessboardBackground().add(chessboard);
                        chessboardBackground.repaint();
                        chessboard.repaint();
                        statusLabel.setForeground(Color.YELLOW);
                        roundLabel.setForeground(Color.YELLOW);
                        repaint();
                    }
                }
            }
        });
        add(change);
        return change;
    }


    //异常报出窗口
    private JDialog addSingularEvent(){
        JDialog singularEvent = new JDialog();
        singularEvent.setTitle("Out of expectation");
        singularEvent.setLocation(300, 300);
        singularEvent.setVisible(false);
        singularEvent.setSize(220, 60);
        return singularEvent;

    }

    public void singularEvent(String event){
        JLabel label = new JLabel(event);
        label.setVisible(true);
        label.setSize(120, 50);
        singularEvent.add(label);
        singularEvent.setVisible(true);
    }

    private JLabel addBackground(){//
        ImageIcon background = new ImageIcon(backgroundPath);
        JLabel label = new JLabel(background);
        label.setBounds(0, 0, this.getWidth(), this.getHeight());
        JPanel imagePanel = (JPanel) this.getContentPane();
        imagePanel.setOpaque(false);
        this.getLayeredPane().add(label, Integer.valueOf(Integer.MIN_VALUE));
        return label;
    }

    private JLabel addChessboardBackground(){
        ImageIcon background1 = new ImageIcon(chessboardPath);
        JLabel label1 = new JLabel(background1);
        label1.setBounds(HEIGHT / 10, HEIGHT / 10, CHESSBOARD_SIZE, CHESSBOARD_SIZE);
        this.getLayeredPane().add(label1, 0);
        return label1;
    }

    private JLabel addStatusFrame() {
        ImageIcon statusFrame = new ImageIcon("images/StatusFrame.png");
        JLabel label2 = new JLabel(statusFrame);
        label2.setBounds(WIDTH * 760 / 1000 - 15, HEIGHT / 10 - 20, 200, 150);
        this.getLayeredPane().add(label2, 0);
        return label2;
    }

    public void setChessboardPath(String chessboardPath) {
        this.chessboardPath = chessboardPath;
    }

    //网络连接按钮
    private void addWebButton(){
        JButton button = new JButton(new ImageIcon("button/web.png"));
        webButton = button;
        button.setLocation(HEIGHT + 10, HEIGHT / 10 + 440);
        button.setSize(150, 45);
        button.setVisible(true);
        add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断是否有棋子被选中
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        System.out.println(chessboard.chessComponents[i][j].isSelected());
                        if (chessboard.chessComponents[i][j].isSelected()) {
                            JOptionPane.showMessageDialog(null, "Can't do with chess selected");
                            return;
                        }
                    }
                }

                String s = (String)JOptionPane.showInputDialog(null, "input", null);
                if(s==null)return;
                chessboard.proxy.send(s+"#"+Chessboard.INVITE_CONNECT+chessboard.myID);
            }
        });
    }

    private JLabel addMyID(){
        JLabel label = new JLabel("My WebID: " + chessboard.myID);
        label.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        statusFrame.add(label);
        label.setVisible(true);
        label.setSize(200, 50);
        label.setLocation(20, 90);
        return label;
    }

    private TextArea addChatArea(){
        TextArea label = new TextArea();
        label.setVisible(true);
        label.setSize(220, 120);
        label.setFont(new Font("Times New Roman", Font.BOLD, 14));
        label.setLocation(WIDTH * 760 / 1000 - 25, HEIGHT / 10 + 277);
        label.setEditable(false);
        add(label);
        return label;
    }

    private TextField addChat(){
        TextField text = new TextField();
        text.setSize(170, 40);
        text.setLocation(WIDTH * 760 / 1000 - 25, HEIGHT / 10 + 397);
        text.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        text.setVisible(true);
        text.setBackground(new Color(220,150,87));
        text.addKeyListener(this);
        add(text);
        return text;
    }

    private JButton addSend(){
        JButton button = new JButton(new ImageIcon("button/SEND.png"));
        button.setLocation(WIDTH * 760 / 1000 + 145, HEIGHT / 10 + 397);
        button.setSize(50, 40);
        add(button);

        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {//TODO
        chat = this.chatField.getText();
        if (chat.equals("")) {
            return;
        }
        List<String> s = chessboard.chatRecord;
        s.add(chessboard.myID + ": " + chat);
        String display = new String();
        for (int i = 0; i < s.size(); i++) {
            display = display.concat(s.get(i));
            display = display.concat("\n");
        }

        chatField.setText("");
        this.chatDisplay.setText(display);
        chatDisplay.setCaretPosition(chatDisplay.getText().length());
        if (chessboard.opponentID != null && chessboard.opponentID != "") {
            this.chessboard.proxy.send(chessboard.opponentID + "#" + Chessboard.SEND_CHAT + display);
        }
        chatDisplay.repaint();
        chatField.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            System.out.println(2);
            chat = this.chatField.getText();
            if (chat.equals("")) {
                return;
            }
            List<String> s = chessboard.chatRecord;
            s.add(chessboard.myID + ": " + chat);
            String display = new String();
            for (int i = 0; i < s.size(); i++) {
                display = display.concat(s.get(i));
                display = display.concat("\n");
            }

            chatField.setText("");
            this.chatDisplay.setText(display);
            chatDisplay.setCaretPosition(chatDisplay.getText().length());
            if (chessboard.opponentID != null && chessboard.opponentID != "") {
                this.chessboard.proxy.send(chessboard.opponentID + "#" + Chessboard.SEND_CHAT + display);
            }
            chatDisplay.repaint();
            chatField.repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


//    private JButton addMusicStart(){
//        JButton musicStart = new JButton();
//        musicStart.setSize(50, 30);
//        musicStart.setVisible(true);
//        musicStart.setLocation(700, 650);
//        add(musicStart);
//        return musicStart;
//    }
//
//    private JButton addMusicStop(){
//        JButton musicStop = new JButton();
//        musicStop.setSize(50, 30);
//        musicStop.setVisible(true);
//        musicStop.setLocation(750, 650);
//        add(musicStop);
//        return musicStop;
//    }
//
//    class MyListener implements ActionListener {
//        //TODO to finish music to start and stop;
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if (e.getSource().equals(musicStart) ){
//                music.start();
//            }else if (e.getSource().equals(musicStop) ){
//                music.stop();
//
//            }
//        }
//    }


}




enum GameModel {
    LocalModel, WebModel, AiModel, WitnessModel
}




