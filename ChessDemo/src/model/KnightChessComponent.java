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

public class KnightChessComponent extends ChessComponent {
    private static Image KNIGHT_WHITE;
    private static Image KNIGHT_BLACK;
    private Image knightImage;

    private Image knightStore;
    private Image knightRed;
    private static ImageIcon iconW = new ImageIcon("chessStore/knight-white.jpg");
    private static ImageIcon iconB = new ImageIcon("chessStore/knight-black.jpg");

    private static Image KNIGHT_W = iconW.getImage();
    private static Image KNIGHT_B = iconB.getImage();



    public void loadResource() throws IOException {
        if (KNIGHT_WHITE == null) {
            KNIGHT_WHITE = ImageIO.read(new File("./images/knight-white.png"));
        }

        if (KNIGHT_BLACK == null) {
            KNIGHT_BLACK = ImageIO.read(new File("./images/knight-black.png"));
        }
    }


    private void initiateKnightImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                knightImage = KNIGHT_WHITE;
                knightStore = KNIGHT_WHITE;
                knightRed = KNIGHT_W;
            } else if (color == ChessColor.BLACK) {
                knightImage = KNIGHT_BLACK;
                knightStore = KNIGHT_BLACK;
                knightRed = KNIGHT_B;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void regionImage() {
        knightImage = knightStore;
    }

    @Override
    public void setImage(ChessColor color) {
        knightImage = knightRed;
        this.repaint();
    }

    public KnightChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, MouseEnter mouseEnter, int size, Chessboard chessboard) {
        super(chessboardPoint, location, color, listener, mouseEnter, size, chessboard);
        initiateKnightImage(color);
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessComponent.pawnOnTheWay = null;
        ChessboardPoint source = getChessboardPoint();

        if ((source.getX() == destination.getX() && source.getY() == destination.getY()) || this.getChessColor() == chessComponents[destination.getX()][destination.getY()].getChessColor()) {
            return false;
        }

        boolean isValid = ((Math.abs(source.getX() - destination.getX()) == 1) && (Math.abs(source.getY() - destination.getY()) == 2)) || ((Math.abs(source.getX() - destination.getX()) == 2) && (Math.abs(source.getY() - destination.getY()) == 1));
        return isValid;
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(knightImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(knightImage, 0, 0, getWidth() , getHeight(), this);
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
