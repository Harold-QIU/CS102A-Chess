package model;

import controller.ClickController;
import controller.MouseEnter;
import view.Chessboard;
import view.ChessboardPoint;
import view.Menu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class KingChessComponent extends ChessComponent {
    private static Image KING_WHITE;
    private static Image KING_BLACK;
    private Image kingImage;
    private Image kingStore;
    private Image kingRed;
    private static ImageIcon iconW = new ImageIcon("chessStore/king-white.jpg");
    private static ImageIcon iconB = new ImageIcon("chessStore/king-black.jpg");

    private static Image KING_W = iconW.getImage();
    private static Image KING_B = iconB.getImage();



    public void loadResource() throws IOException {
        if (KING_WHITE == null) {
            KING_WHITE = ImageIO.read(new File("./images/king-white.png"));
        }

        if (KING_BLACK == null) {
            KING_BLACK = ImageIO.read(new File("./images/king-black.png"));
        }
    }


    private void initiateKingImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                kingImage = KING_WHITE;
                kingStore = KING_WHITE;
                kingRed = KING_W;
            } else if (color == ChessColor.BLACK) {
                kingImage = KING_BLACK;
                kingStore = KING_BLACK;
                kingRed = KING_B;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void regionImage() {
        kingImage = kingStore;
    }

    @Override
    public void setImage(ChessColor color) {
        kingImage = kingRed;
        this.repaint();
    }

    public KingChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, MouseEnter mouseEnter, int size, Chessboard chessboard) {
        super(chessboardPoint, location, color, listener, mouseEnter, size, chessboard);
        initiateKingImage(color);
    }


    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessComponent.pawnOnTheWay = null;
        ChessboardPoint source = getChessboardPoint();
        //TODO 王车移位判断调用请写在这里
        map = new HashMap<>();
        ChessComponent r1, r2, r3, r4, l1, l2, l3, l4;
        if (source.getY() + 1 <= 7) {
            r1 = chessComponents[source.getX()][source.getY()+1];
        } else {
            r1 = null;
        }

        if (source.getY() + 2 <= 7) {
            r2 = chessComponents[source.getX()][source.getY()+2];
        } else {
            r2 = null;
        }

        if (source.getY() + 3 <= 7) {
            r3 = chessComponents[source.getX()][source.getY()+3];
        } else {
            r3 = null;
        }

        if (source.getY() + 4 <= 7) {
            r4 = chessComponents[source.getX()][source.getY()+4];
        } else {
            r4 = null;
        }


        if (source.getY() - 1 >= 0) {
            l1 = chessComponents[source.getX()][source.getY()-1];
        } else {
            l1 = null;
        }

        if (source.getY() - 2 >= 0) {
            l2 = chessComponents[source.getX()][source.getY()-2];
        } else {
            l2 = null;
        }

        if (source.getY() - 3 >= 0) {
            l3 = chessComponents[source.getX()][source.getY()-3];
        } else {
            l3 = null;
        }

        if (source.getY() - 4 >= 0) {
            l4 = chessComponents[source.getX()][source.getY()-4];
        } else {
            l4 = null;
        }

        if (r1 instanceof EmptySlotComponent && r2 instanceof EmptySlotComponent) {
            if (r3 instanceof RookChessComponent && r3.getChessColor() == this.getChessColor()) {
                map.put(r2, r3);
                if (destination == r2.getChessboardPoint()) {
                    return true;
                }
            } else if (r3 instanceof EmptySlotComponent && r4 instanceof RookChessComponent && r4.getChessColor() == this.getChessColor()) {
                map.put(r2, r4);
                if (destination == r2.getChessboardPoint()) {
                    return true;
                }
            }
        }

        if (l1 instanceof EmptySlotComponent && l2 instanceof EmptySlotComponent) {
            if (l3 instanceof RookChessComponent && l3. getChessColor() == this.getChessColor()) {
                map.put(l2, l3);
                if (destination == l2.getChessboardPoint()) {
                    return true;
                }
            } else if (l3 instanceof EmptySlotComponent && l4 instanceof RookChessComponent && l4.getChessColor() == this.getChessColor()) {
                map.put(l2, l4);
                if (destination == l2.getChessboardPoint()) {
                    return true;
                }
            }
        }






        if ((source.getX() == destination.getX() && source.getY() == destination.getY()) || this.getChessColor() == chessComponents[destination.getX()][destination.getY()].getChessColor()) {
            return false;
        }

        if (Math.abs(source.getX() - destination.getX()) <= 1 && Math.abs(source.getY() - destination.getY()) <= 1) {
            return true;
        }

        return false;//以false为前提写的
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage(kingImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(kingImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) {
            // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
        if (isCanBeMoved()){
            g.setColor(Color.green);
            g.drawOval(0, 0, getWidth(), getHeight());
        }

    }
}
