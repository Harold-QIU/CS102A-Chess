package view;

import web.WebListener;

import javax.swing.*;
import java.awt.*;

public class Chessboard_witness extends Chessboard_web {


    public Chessboard_witness(int width, int height, JLabel statusLabel, JLabel roundLabel, JDialog singularEvent, String ip,JLabel myIDLabel, TextArea chatDisplay) {
        super(width, height, statusLabel, roundLabel, singularEvent, ip, myIDLabel, chatDisplay);
    }


}
