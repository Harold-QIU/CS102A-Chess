package model;

import controller.ClickController;
import controller.MouseEnter;
import view.Chessboard;
import view.ChessboardPoint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class QueenChessComponent extends ChessComponent {
    private static Image QUEEN_WHITE;
    private static Image QUEEN_BLACK;
    private Image queenImage;
    private Image queenStore;
    private Image queenRed;
    private static ImageIcon iconW = new ImageIcon("chessStore/queen-white.jpg");
    private static ImageIcon iconB = new ImageIcon("chessStore/queen-black.jpg");
    private static Image QUEEN_W = iconW.getImage();
    private static Image QUEEN_B = iconB.getImage();



    public void loadResource() throws IOException {
        if (QUEEN_WHITE == null) {
            QUEEN_WHITE = ImageIO.read(new File("./images/queen-white.png"));
        }

        if (QUEEN_BLACK == null) {
            QUEEN_BLACK = ImageIO.read(new File("./images/queen-black.png"));
        }
    }


    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                queenImage = QUEEN_WHITE;
                queenStore = QUEEN_WHITE;
                queenRed = QUEEN_W;
            } else if (color == ChessColor.BLACK) {
                queenImage = QUEEN_BLACK;
                queenStore = QUEEN_BLACK;
                queenRed = QUEEN_B;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void regionImage() {
        queenImage = queenStore;
    }

    @Override
    public void setImage(ChessColor color) {
        queenImage = queenRed;
        this.repaint();
    }

    public QueenChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, MouseEnter mouseEnter, int size, Chessboard chessboard) {
        super(chessboardPoint, location, color, listener, mouseEnter, size, chessboard);
        initiateRookImage(color);
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessComponent.pawnOnTheWay = null;
        ChessboardPoint source = getChessboardPoint();
        if ((source.getX() == destination.getX() && source.getY() == destination.getY()) || this.getChessColor() == chessComponents[destination.getX()][destination.getY()].getChessColor()) {
            return false;
        }

        if (source.getX() == destination.getX()) {
            int row = source.getX();
            for (int col = Math.min(source.getY(), destination.getY()) + 1;
                 col < Math.max(source.getY(), destination.getY()); col++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else if (source.getY() == destination.getY()) {
            int col = source.getY();
            for (int row = Math.min(source.getX(), destination.getX()) + 1;
                 row < Math.max(source.getX(), destination.getX()); row++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else if (Math.abs(source.getX() - destination.getX()) == Math.abs(source.getY() - destination.getY())) {
            boolean isStandardCross = (source.getX() > destination.getX() && source.getY() > destination.getY()) || (source.getX() < destination.getX() && source.getY() < destination.getY());

            if (isStandardCross) {
                for (int row = Math.min(source.getX(), destination.getX()) + 1, col = Math.min(source.getY(), destination.getY()) + 1; row < Math.max(source.getX(), destination.getX()); row++, col++) {
                    if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                        return false;
                    }
                }
            } else {//不是standard cross——从左下角开始遍历
                for (int row = Math.max(source.getX(), destination.getX()) - 1, col = Math.min(source.getY(), destination.getY()) + 1; row > Math.min(source.getX(), destination.getX()); row--, col++) {
                    if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                        return false;
                    }
                }
            }
        } else {//Not on the cross line or in a line
            return false;

        }
        return true;//以true为默认来写

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(queenImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(queenImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
        if (isCanBeMoved()){
            g.setColor(Color.green);
            g.drawOval(0, 0, getWidth(), getHeight());
        }

    }
}
