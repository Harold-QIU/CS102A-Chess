package model;

import controller.MouseEnter;
import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;
import view.Menu;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * 这个类表示棋盘上的空位置
 */
public class EmptySlotComponent extends ChessComponent {

    private static ImageIcon icon = new ImageIcon("chessStore/empty.jpg");
    private Image empty = null;
    private Image emptyStore = null;
    private Image emptyRed = icon.getImage();

    public EmptySlotComponent(ChessboardPoint chessboardPoint, Point location, ClickController listener, MouseEnter mouseEnter, int size, Chessboard chessboard) {
        super(chessboardPoint, location, ChessColor.NONE, listener, mouseEnter, size, chessboard);
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination) {
        return false;
    }

    @Override
    public void loadResource() throws IOException {
        //No resource!
    }

    @Override
    public void regionImage() {
        empty = emptyStore;
    }

    @Override
    public void setImage(ChessColor color) {
        empty = emptyRed;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(empty, 0,0,getWidth(), getHeight(), this);
        if (isCanBeMoved()){
            g.setColor(Color.yellow);
            g.drawOval(0, 0, getWidth(), getHeight());
        }

    }
}
