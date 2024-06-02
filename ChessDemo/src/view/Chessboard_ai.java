package view;

import model.ChessColor;

import javax.swing.*;
import java.util.Random;

public class Chessboard_ai extends Chessboard {
    public Chessboard_ai(int width, int height, JLabel statusLabel, JLabel roundLabel, JDialog singularEvent, String ip, JLabel myIDLabel) {
        super(width, height, statusLabel, roundLabel, singularEvent, ip, myIDLabel);
        myColor = ChessColor.WHITE;
    }

    @Override
    public void ai_L() {

        //TODO 加一个计时器

        Random random = new Random();
        while(currentColor == ChessColor.BLACK) {
            int x = random.nextInt(8);
            int y = random.nextInt(8);
            ChessColor color = chessComponents[x][y].getChessColor();
            int size = chessComponents[x][y].getAllCanMoveTo().size();
            if (size != 0 && color != ChessColor.NONE && color == currentColor) {
                int index = random.nextInt(size);
                swapChessComponents(chessComponents[x][y], chessComponents[x][y].getAllCanMoveTo().get(index));
                break;
            }
        }
    }


}






