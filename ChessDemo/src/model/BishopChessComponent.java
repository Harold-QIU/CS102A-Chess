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

public class BishopChessComponent extends ChessComponent {
    private static Image BISHOP_WHITE;
    private static Image BISHOP_BLACK;
    private Image bishopImage;

    private Image bishopStore;
    private Image bishopRed;
    private static ImageIcon iconW = new ImageIcon("chessStore/bishop-white.jpg");
    private static ImageIcon iconB = new ImageIcon("chessStore/bishop-black.jpg");

    private static Image BISHOP_W = iconW.getImage();
    private static Image BISHOP_B = iconB.getImage();

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */


    public void loadResource() throws IOException {
        if (BISHOP_WHITE == null) {
            BISHOP_WHITE = ImageIO.read(new File("./images/bishop-white.png"));
        }

        if (BISHOP_BLACK == null) {
            BISHOP_BLACK = ImageIO.read(new File("./images/bishop-black.png"));
        }
    }


    private void initiateBishopImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                bishopImage = BISHOP_WHITE;
                bishopStore = BISHOP_WHITE;
                bishopRed = BISHOP_W;
            } else if (color == ChessColor.BLACK) {
                bishopImage = BISHOP_BLACK;
                bishopStore = BISHOP_BLACK;
                bishopRed = BISHOP_B;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public BishopChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, MouseEnter mouseEnter, int size, Chessboard chessboard) {
        super(chessboardPoint, location, color, listener, mouseEnter, size, chessboard);
        initiateBishopImage(color);
    }

    @Override
    public void regionImage() {
        bishopImage = bishopStore;
    }

    @Override
    public void setImage(ChessColor color) {
        bishopImage = bishopRed;
        this.repaint();
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessComponent.pawnOnTheWay = null;
        ChessboardPoint source = getChessboardPoint();
        if ((source.getX() == destination.getX() && source.getY() == destination.getY()) || this.getChessColor() == chessComponents[destination.getX()][destination.getY()].getChessColor()) {
            return false;
        }

        if (Math.abs(source.getX() - destination.getX()) == Math.abs(source.getY() - destination.getY())) {
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
        } else {//Not on the cross line
            return false;
        }

        return true;

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(bishopImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(bishopImage, 0, 0, getWidth() , getHeight(), this);
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



