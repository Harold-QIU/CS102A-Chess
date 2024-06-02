package view;


import controller.MouseEnter;
import model.*;
import controller.ClickController;
import web.WebProxy;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类表示面板上的棋盘组件对象
 */
public class Chessboard extends JComponent  {
    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    protected static final int CHESSBOARD_SIZE = 8;
    protected final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    protected ChessColor currentColor = ChessColor.WHITE;
    //all chessComponents in this chessboard are shared only one model controller
    protected final ClickController clickController = new ClickController(this);

    protected final MouseEnter mouseEnter = new MouseEnter(this);
    protected final int CHESS_SIZE;

    public Integer roundTimeMul2 = 2;//回合数
    protected Integer kingRookTime_BLACK = 0;//王车移位的次数
    protected Integer kingRookTime_WHITE = 0;//王车移位的次数

    //下面都是一些方法里面用到的参数
    protected String status = "White";

    public JLabel statusLabel;

    public JLabel roundLabel;

    public JLabel myIDLabel;

    public JDialog singularEvent;

    public String lastStep;
    public List<String> stepStorage = new ArrayList<>();

    public WebProxy proxy;

    public String opponentID, myID;
    public static List<String> witnessID = new ArrayList<>();

    public static final char INVITE_CONNECT='a', ACCEPT_CONNECT='b', MOVE='c', REJECT_CONNECT='d', GET_ACCOUNT='e',
            DISCONNECT = 'f', RESET = 'g', SEND_BOARD = 'h', REQUEST_WITNESS = 'i', ACCEPT_WITNESS = 'j', ADD_WITNESS = 'k', SEND_CHAT = 'l';

    public ChessColor myColor = ChessColor.NONE;

    public static String defaultChessboard = "RNBQKBNR\nPPPPPPPP\n________\n________\n________\n________\npppppppp\nrnbqkbnr\n2\n0\n0";
    private JLabel backgroundLabel;
    public String ip;

    public List<String> chatRecord = new ArrayList<>();





    public Chessboard(int width, int height, JLabel statusLabel, JLabel roundLabel, JDialog singularEvent, String ip, JLabel myIDLabel) {
        //TODO 改这个构造器时，另外一个构造器也要跟着改
        setChessboardBackground("images/chessboard3.jpg");
        this.statusLabel = statusLabel;//
        statusLabel.setText("Round for " + status);
        this.roundLabel = roundLabel;//
        this.singularEvent = singularEvent;//
        this.ip = ip;
        this.myIDLabel = myIDLabel;
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

        initiateEmptyChessboard();

        // FIXME: Initialize chessboard for testing only.
        initRookOnBoard(0, 0, ChessColor.BLACK);
        initRookOnBoard(0, CHESSBOARD_SIZE - 1, ChessColor.BLACK);
        initRookOnBoard(CHESSBOARD_SIZE - 1, 0, ChessColor.WHITE);
        initRookOnBoard(CHESSBOARD_SIZE - 1, CHESSBOARD_SIZE - 1, ChessColor.WHITE);
        initBishopOnBoard(0, 2, ChessColor.BLACK);
        initBishopOnBoard(0, CHESSBOARD_SIZE - 3, ChessColor.BLACK);
        initBishopOnBoard(CHESSBOARD_SIZE - 1, 2, ChessColor.WHITE);
        initBishopOnBoard(CHESSBOARD_SIZE - 1, CHESSBOARD_SIZE - 3, ChessColor.WHITE);
        initQueenOnBoard(0,3,ChessColor.BLACK);
        initQueenOnBoard(CHESSBOARD_SIZE - 1,3,ChessColor.WHITE);
        initKingOnBoard(0, 4, ChessColor.BLACK);
        initKingOnBoard(CHESSBOARD_SIZE - 1, 4, ChessColor.WHITE);
        initKnightOnBoard(0, 1, ChessColor.BLACK);
        initKnightOnBoard(0, CHESSBOARD_SIZE - 2, ChessColor.BLACK);
        initKnightOnBoard(CHESSBOARD_SIZE - 1, 1, ChessColor.WHITE);
        initKnightOnBoard(CHESSBOARD_SIZE - 1, CHESSBOARD_SIZE - 2, ChessColor.WHITE);
        initPawnOnBoard(1, 0, ChessColor.BLACK);
        initPawnOnBoard(1, 1, ChessColor.BLACK);
        initPawnOnBoard(1, 2, ChessColor.BLACK);
        initPawnOnBoard(1, 3, ChessColor.BLACK);
        initPawnOnBoard(1, 4, ChessColor.BLACK);
        initPawnOnBoard(1, 5, ChessColor.BLACK);
        initPawnOnBoard(1, 6, ChessColor.BLACK);
        initPawnOnBoard(1, 7, ChessColor.BLACK);
        initPawnOnBoard(6, 0, ChessColor.WHITE);
        initPawnOnBoard(6, 1, ChessColor.WHITE);
        initPawnOnBoard(6, 2, ChessColor.WHITE);
        initPawnOnBoard(6, 3, ChessColor.WHITE);
        initPawnOnBoard(6, 4, ChessColor.WHITE);
        initPawnOnBoard(6, 5, ChessColor.WHITE);
        initPawnOnBoard(6, 6, ChessColor.WHITE);
        initPawnOnBoard(6, 7, ChessColor.WHITE);

        roundLabel.setText(String.format("Round: %d", roundTimeMul2/2));

    }





    public void writeChessboard(String Path) {
        String outcome = this.writeOneChessboard();
        for (int i = this.stepStorage.size() - 1 ; i >= 0 ; i--) {
            outcome = outcome.concat(stepStorage.get(i));
        }

        try {
            String fileName = Path +".txt";
            File file = new File(fileName);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(outcome);// 往文件里写入字符串
            //ps.append("https://www.jb51.net");//在已有的基础上添加字符串

        } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();//异常能否通过JDialog抛出
        }

    }

    public String writeOneChessboard() {
        StringBuilder[] chessboardParts = new StringBuilder[8];
        String outcome = "";
        for (int i = 0; i < 8; i++) {
            chessboardParts[i] = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                if (chessComponents[i][j].getChessColor() == ChessColor.BLACK) {
                    if (chessComponents[i][j] instanceof RookChessComponent) {
                        chessboardParts[i].append("R");
                    } else if (chessComponents[i][j] instanceof KnightChessComponent) {
                        chessboardParts[i].append("N");
                    } else if (chessComponents[i][j] instanceof BishopChessComponent) {
                        chessboardParts[i].append("B");
                    } else if (chessComponents[i][j] instanceof KingChessComponent) {
                        chessboardParts[i].append("K");
                    } else if (chessComponents[i][j] instanceof QueenChessComponent) {
                        chessboardParts[i].append("Q");
                    } else if (chessComponents[i][j] instanceof PawnChessComponent) {
                        chessboardParts[i].append("P");
                    }
                } else if (chessComponents[i][j].getChessColor() == ChessColor.WHITE){
                    if (chessComponents[i][j] instanceof RookChessComponent) {
                        chessboardParts[i].append("r");
                    } else if (chessComponents[i][j] instanceof KnightChessComponent) {
                        chessboardParts[i].append("n");
                    } else if (chessComponents[i][j] instanceof BishopChessComponent) {
                        chessboardParts[i].append("b");
                    } else if (chessComponents[i][j] instanceof KingChessComponent) {
                        chessboardParts[i].append("k");
                    } else if (chessComponents[i][j] instanceof QueenChessComponent) {
                        chessboardParts[i].append("q");
                    } else if (chessComponents[i][j] instanceof PawnChessComponent) {
                        chessboardParts[i].append("p");
                    }
                } else {
                    chessboardParts[i].append("_");
                }
            }

            outcome = outcome.concat(String.valueOf(chessboardParts[i]) + '\n');

        }
        outcome = outcome.concat(String.valueOf(roundTimeMul2 +"\n"));
        outcome = outcome.concat(String.valueOf(kingRookTime_BLACK+"\n"));
        outcome = outcome.concat(String.valueOf(kingRookTime_WHITE+"\n"));
        return outcome;
    }

    public String writeChessboard() {
        String outcome = this.writeOneChessboard();
        for (int i = this.stepStorage.size() - 1 ; i >= 0 ; i--) {
            outcome = outcome.concat(stepStorage.get(i));
        }
        return outcome;
    }



    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        //TODO 储存变化前的三大参数(swapLocation改了两个),之后写悔棋时一定要注意写把storage给还原的操作
        lastStep = this.writeOneChessboard();
        stepStorage.add(lastStep);

        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, mouseEnter,  CHESS_SIZE, this));
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

    }

    public void swapChessComponentsWithoutMusicAndStore(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        //TODO 储存变化前的三大参数(swapLocation改了两个),之后写悔棋时一定要注意写把storage给还原的操作

        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, mouseEnter,  CHESS_SIZE, this));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    chessComponents[i][j].getMouseEnter().setEnter(null);
                }
            }
        }
        //play chess music;
//        ChessMusic chessMusic = new ChessMusic("images/chessMusic.mp3");
//        chessMusic.start();

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
        }
//        //兵升变
//        if (chess1 instanceof PawnChessComponent && (chess1.getChessboardPoint().getX() == 0 || chess1.getChessboardPoint().getX()==7)) {
//            Object[] options = new Object[]{"Queen", "Rook", "Knight", "Bishop"};
//            int optionSelected = JOptionPane.showOptionDialog(this, "Please select a Promotion", "Pawn's promotion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
//            if (optionSelected == 0) {
//                System.out.println("Queen Promotion");
//                remove(chess1);
//                add(chess1 = new QueenChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
//            } else if (optionSelected == 1) {
//                System.out.println("Rook Promotion");
//                remove(chess1);
//                add(chess1 = new RookChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
//            } else if (optionSelected == 2) {
//                System.out.println("Knight Promotion");
//                remove(chess1);
//                add(chess1 = new KnightChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
//            } else if (optionSelected == 3) {
//                System.out.println("Bishop Promotion");
//                remove(chess1);
//                add(chess1 = new BishopChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController,mouseEnter,CHESS_SIZE,this));
//            }
//        }

//        roundTimeMul2++;
//        this.roundLabel.setText(String.format("Round: %d", roundTimeMul2/2));
//        roundLabel.repaint();
//
//
//        chess1.repaint();
//        chess2.repaint();

    }



    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, mouseEnter, CHESS_SIZE, this));
            }
        }

    }

    public void swapColor() {
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
        status = "Round for "+ this.getCurrentColor().getName();
        this.statusLabel.setText(status);//
    }

    protected void initRookOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, mouseEnter, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
        RookChessComponent.chessComponents = this.chessComponents;

    }


    protected void initBishopOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new BishopChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, mouseEnter, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
        BishopChessComponent.chessComponents = this.chessComponents;


    }


    protected void initQueenOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, mouseEnter, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
        QueenChessComponent.chessComponents = this.chessComponents;
    }


    protected void initKnightOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KnightChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, mouseEnter, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
        KnightChessComponent.chessComponents = this.chessComponents;
    }


    protected void initPawnOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, mouseEnter, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
        PawnChessComponent.chessComponents = this.chessComponents;
        PawnChessComponent.CHESS_SIZE = this.CHESS_SIZE;


    }


    protected void initKingOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, mouseEnter, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
        KingChessComponent.chessComponents = this.chessComponents;

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    //set chessboard background
    public void setChessboardBackground(String path){
        ImageIcon background = new ImageIcon(path);
        JLabel label = new JLabel(background);
        label.setBounds(0, 0, CHESS_SIZE * 8, CHESS_SIZE * 8);
        this.backgroundLabel = label;
        label.setOpaque(false);
        label.setVisible(true);
        this.add(label);
    }



    protected Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessData) {
        //play chess music;
        ChessMusic chessMusic = new ChessMusic("images/chessMusic.mp3");
        chessMusic.start();

        //judge 1
        boolean judge = true;
        for (int i = 0; i < 8; i++) {
            if (chessData.get(i).length() != 8) {
                JOptionPane.showMessageDialog(this, "101：棋盘并非8*8");
                judge = false;
            }
        }

        if (!checkChessType(chessData)) {
            JOptionPane.showMessageDialog(this, "102：棋子并非六种之一，棋子并非黑白棋子");
            judge = false;
        }

        if (chessData.get(8).equals("") || chessData.get(8).equals(" ") || Integer.parseInt(chessData.get(8)) < 2) {
            JOptionPane.showMessageDialog(this, "103：导入数据只有棋盘，没有下一步行棋方的提示");
            judge = false;
        }

        if (!judge) {
            return;
        }









        //正常读取
        removeAll();
        initiateEmptyChessboard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (chessData.get(i).charAt(j)) {
                    case 'R':
                        initRookOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'N':
                        initKnightOnBoard(i, j , ChessColor.BLACK);
                        break;
                    case 'B':
                        initBishopOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'Q':
                        initQueenOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'K':
                        initKingOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'P':
                        initPawnOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'r':
                        initRookOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'n':
                        initKnightOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'b':
                        initBishopOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'q':
                        initQueenOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'k':
                        initKingOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'p':
                        initPawnOnBoard(i, j, ChessColor.WHITE);
                        break;
                }
            }
        }
        this.roundTimeMul2 = Integer.parseInt(chessData.get(8));
        roundLabel.setText(String.format("Round: %d", roundTimeMul2/2));
        System.out.println("repainted");
        if (roundTimeMul2 %2 == 0) {
            this.currentColor = ChessColor.WHITE;
        } else {
            this.currentColor = ChessColor.BLACK;
        }
        status = "Round for "+ this.getCurrentColor().getName();
        this.statusLabel.setText(status);//
        this.kingRookTime_BLACK = Integer.parseInt(chessData.get(9));
        this.kingRookTime_WHITE = Integer.parseInt(chessData.get(10));


        int storageSize = chessData.size()/11 - 1;
        this.stepStorage = new ArrayList<>();
        for (int i = 0; i < storageSize; i++) {
            String outcome = "";
            for (int j = 0; j < 11; j++) {
                outcome = outcome.concat(chessData.get(11 * i + j + 11)+"\n");
            }
            stepStorage.add(0, outcome);
            if (i == 0) {
                this.lastStep = outcome;
            }
        }
        this.repaint();
        roundLabel.repaint();
        statusLabel.repaint();


    }

    //这个是为悔棋写的loadOneGame
    public void loadOneGame(List<String> chessData) {
        //judge 1
        boolean judge = true;
        for (int i = 0; i < 8; i++) {
            if (chessData.get(i).length() != 8) {
                JOptionPane.showMessageDialog(this, "101：棋盘并非8*8");
                judge = false;
            }
        }

        if (!checkChessType(chessData)) {
            JOptionPane.showMessageDialog(this, "102：棋子并非六种之一，棋子并非黑白棋子");
            judge = false;
        }

        if (chessData.get(8).equals("") || chessData.get(8).equals(" ") || Integer.parseInt(chessData.get(8)) == 0) {
            JOptionPane.showMessageDialog(this, "103：导入数据只有棋盘，没有下一步行棋方的提示");
            judge = false;
        }

        if (!judge) {
            return;
        }




        //正常读取
        removeAll();
        initiateEmptyChessboard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (chessData.get(i).charAt(j)) {
                    case 'R':
                        initRookOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'N':
                        initKnightOnBoard(i, j , ChessColor.BLACK);
                        break;
                    case 'B':
                        initBishopOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'Q':
                        initQueenOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'K':
                        initKingOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'P':
                        initPawnOnBoard(i, j, ChessColor.BLACK);
                        break;
                    case 'r':
                        initRookOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'n':
                        initKnightOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'b':
                        initBishopOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'q':
                        initQueenOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'k':
                        initKingOnBoard(i, j, ChessColor.WHITE);
                        break;
                    case 'p':
                        initPawnOnBoard(i, j, ChessColor.WHITE);
                        break;
                }
            }
        }
        this.roundTimeMul2 = Integer.parseInt(chessData.get(8));
        roundLabel.setText(String.format("Round: %d", roundTimeMul2/2));
        roundLabel.repaint();
        System.out.println("repainted");
        if (roundTimeMul2 %2 == 0) {
            this.currentColor = ChessColor.WHITE;
        } else {
            this.currentColor = ChessColor.BLACK;
        }
        status = "Round for "+ this.getCurrentColor().getName();
        this.statusLabel.setText(status);//
        this.kingRookTime_BLACK = Integer.parseInt(chessData.get(9));
        this.kingRookTime_WHITE = Integer.parseInt(chessData.get(10));
        statusLabel.repaint();
    }

    //下面写的是一些loadGame的辅助判断方法


    public static boolean checkChessType(List<String> chessData) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (chessData.get(i).charAt(j)) {
                    case 'R':
                    case 'N':
                    case 'B':
                    case 'Q':
                    case 'K':
                    case 'P':
                    case 'r':
                    case 'n':
                    case 'b':
                    case 'q':
                    case 'k':
                    case 'p':
                    case '_':
                        break;
                    default:
                        return false;
                }
            }
        }
        return true;

    }




    //异常事件报错
    public void singularEvent(String event){
        JLabel label = new JLabel(event);
        label.setVisible(true);
        label.setSize(120, 50);
        singularEvent.add(label);
        this.singularEvent.setVisible(true);
    }

    public void ai_L() throws InterruptedException {

    }

}

class StopThread extends Thread{
    int millis;
    public StopThread(int millis){
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
