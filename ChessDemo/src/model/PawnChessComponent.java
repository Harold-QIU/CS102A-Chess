package model;

import controller.ClickController;
import controller.MouseEnter;
import view.Chessboard;
import view.ChessboardPoint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PawnChessComponent extends ChessComponent{
    private static Image PAWN_WHITE;
    private static Image PAWN_BLACK;
    private Image pawnImage;

    private Image pawnStore;
    private Image pawnRed;
    private static ImageIcon iconW = new ImageIcon("chessStore/pawn-white.jpg");
    private static ImageIcon iconB = new ImageIcon("chessStore/pawn-black.jpg");

    private static Image PAWN_W = iconW.getImage();
    private static Image PAWN_B = iconB.getImage();

    public static int CHESS_SIZE;



    public void loadResource() throws IOException {
        if (PAWN_WHITE == null) {
            PAWN_WHITE = ImageIO.read(new File("./images/pawn-white.png"));
        }

        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("./images/pawn-black.png"));
        }

    }


    private void initiatePawnImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                pawnImage = PAWN_WHITE;
                pawnStore = PAWN_WHITE;
                pawnRed = PAWN_W;
            } else if (color == ChessColor.BLACK) {
                pawnImage = PAWN_BLACK;
                pawnStore = PAWN_BLACK;
                pawnRed = PAWN_B;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void regionImage() {
        pawnImage = pawnStore;
    }

    @Override
    public void setImage(ChessColor color) {
        pawnImage = pawnRed;
        this.repaint();
    }

    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, MouseEnter mouseEnter, int size, Chessboard chessboard) {
        super(chessboardPoint, location, color, listener, mouseEnter, size, chessboard);
        initiatePawnImage(color);
    }


    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessComponent.pawnOnTheWay = null;
        ChessboardPoint source = getChessboardPoint();
        if ((source.getX() == destination.getX() && source.getY() == destination.getY()) || this.getChessColor() == chessComponents[destination.getX()][destination.getY()].getChessColor()) {
            return false;
        }

        List<String> lastChessboard = new ArrayList<>();
        if (chessboard.roundTimeMul2 != 2) {
            lastChessboard = List.of(chessboard.lastStep.split("\n"));
        }

        //黑棋
        if (this.chessColor == ChessColor.BLACK) {
            if (source.getY() == destination.getY()) {//直线前进
                if (source.getX() - destination.getX() == -1 && chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent) {
                    return true;//前进一
                } else if (source.getX() - destination.getX() == -2 && source.getX() == 1 && chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent
                        && chessComponents[destination.getX()-1][destination.getY()] instanceof EmptySlotComponent) {
                    return true;//前进二
                }

            } else if ((Math.abs(source.getY() - destination.getY()) == 1) && (source.getX() - destination.getX() == -1)) {
                if (!(chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent)) {
                    return true;//斜进吃子
                }
                else if ((chessComponents[destination.getX()-1][destination.getY()] instanceof PawnChessComponent) && destination.getX() - 1 == 4 &&  chessboard.roundTimeMul2 != 2 && lastChessboard.get(6).charAt(destination.getY()) == 'p') {
                    pawnOnTheWay = new ChessboardPoint(4, destination.getY());
                    return true;//return true后会斜着前进
                }
            }
        }

        //白棋
        if (this.chessColor == ChessColor.WHITE) {
            if (source.getY() == destination.getY()) {
                if (source.getX() - destination.getX() == 1 && chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent) {
                    return true;
                } else if (source.getX() - destination.getX() == 2 && source.getX() == 6 && chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent
                        && chessComponents[destination.getX() + 1][destination.getY()] instanceof EmptySlotComponent) {
                    return true;
                }

            } else if ((Math.abs(source.getY() - destination.getY()) == 1) && (source.getX() - destination.getX() == 1)) {
                if (!(chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent)) {
                    return true;
                }
                else if ((chessComponents[destination.getX() + 1][destination.getY()] instanceof PawnChessComponent) && destination.getX() + 1 == 3 && chessboard.roundTimeMul2 != 2 && lastChessboard.get(1).charAt(destination.getY()) == 'P') {
                    pawnOnTheWay = new ChessboardPoint(3, destination.getY());
                    return true;//return true后会斜着前进
                }

            }
        }

        return false;//以false为默认写
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(pawnImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(pawnImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) {
            // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
       //遍历棋盘来判断是否能够移动，从而画出能走的位置，但是由于chess是JComponent，所以得在棋盘上判断并画
        if (isCanBeMoved()){
            g.setColor(Color.green);
            g.drawOval(0, 0, getWidth(), getHeight());
        }

    }



}
