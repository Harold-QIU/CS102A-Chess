package controller;

import model.ChessColor;
import model.ChessComponent;
import view.Chessboard;

import javax.swing.*;
import java.awt.*;

public class MouseEnter {
    private final Chessboard chessboard;
    private ChessComponent enter;
    private Image image;

    public MouseEnter(Chessboard chessboard){
        ImageIcon a = new ImageIcon("imagesessTest.jpg");////
        image = a.getImage();////
        this.chessboard = chessboard;
    }

    public void enter(ChessComponent chessComponent){
        if (enter == null){
            enter = chessComponent;
            if (chessComponent.getChessColor().equals(ChessColor.NONE) ||matchColor(chessComponent)){
                enter.setImage(chessboard.getCurrentColor());
                enter.repaint();
            }else return;
        } else {
            enter.regionImage();
            enter.repaint();
            enter = null;
        }
    }

    public void setEnter(ChessComponent enter){
        this.enter = enter;
    }

    private boolean matchColor(ChessComponent chessComponent){
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }
}
