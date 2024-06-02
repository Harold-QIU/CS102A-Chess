package controller;


import model.ChessColor;
import model.ChessComponent;
import model.EmptySlotComponent;
import model.KingChessComponent;
import view.Chessboard;
import view.Chessboard_ai;
import view.Chessboard_web;

import javax.print.DocFlavor;
import javax.swing.*;
import java.util.List;
import java.util.Timer;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.setAllCanMoveTo(true);
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        chessboard.getChessComponents()[i][j].repaint();
                    }
                }
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        chessboard.getChessComponents()[i][j].setCanBeMoved(false);
                        chessboard.getChessComponents()[i][j].repaint();
                    }
                }
                first = null;
            } else if (handleSecond(chessComponent)) {

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        chessboard.getChessComponents()[i][j].setCanBeMoved(false);
                        chessboard.getChessComponents()[i][j].repaint();
                    }
                }

                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();

                first.setSelected(false);
                first = null;

                //TODO 5/12在这里加入判断胜负的方法，注意color已经swap了
                addJudgePane();

                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        chessboard.getChessComponents()[i][j].repaint();
                    }
                }
                if (chessboard instanceof Chessboard_ai) {
                    try {
                        this.chessboard.ai_L();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.chessboard.swapColor();
                    addJudgePane();
                }

            }
        }
    }

    public void addJudgePane() {
//        if (this.JudgeTheWinner() == ChessColor.BLACK) {
//            System.out.println("Black WIN!");
//            this.chessboard.singularEvent("Black Checkmate!");
//        } else if (this.JudgeTheWinner() == ChessColor.WHITE) {
//            System.out.println("White WIN!");
//            this.chessboard.singularEvent("White Checkmate!");
//        } else {
//            System.out.println("No one WIN");
//        }
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessComponents[i][j].getChessColor() == ChessColor.BLACK) {
                    for (ChessComponent checkChess: chessComponents[i][j].getAllCanMoveTo()) {
                        if (checkChess instanceof KingChessComponent) {
                            this.chessboard.singularEvent("BLACK CHECK");
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessComponents[i][j].getChessColor() == ChessColor.WHITE) {
                    for (ChessComponent checkChess: chessComponents[i][j].getAllCanMoveTo()) {
                        if (checkChess instanceof KingChessComponent) {
                            this.chessboard.singularEvent("WHITE CHECK");
                        }
                    }
                }
            }
        }

        boolean whiteKing = false;
        boolean blackKing = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessComponents[i][j] instanceof KingChessComponent && chessComponents[i][j].getChessColor() == ChessColor.WHITE) {
                    whiteKing = true;
                } else if (chessComponents[i][j] instanceof KingChessComponent && chessComponents[i][j].getChessColor() == ChessColor.BLACK) {
                    blackKing = true;
                }
            }
        }

        if (!whiteKing) {
            int i = JOptionPane.showConfirmDialog(null,"BLACK WON, WANT TO RESET?","GAME OVER",JOptionPane.YES_NO_CANCEL_OPTION);
            if (i == 0) {
                List<String> chessData = List.of(Chessboard.defaultChessboard.split("\n"));
                chessboard.loadGame(chessData);
                if (chessboard instanceof Chessboard_web) {
                    chessboard.proxy.send(chessboard.opponentID +"#"+ Chessboard.RESET +chessboard.myID);
                }
                if (chessboard instanceof Chessboard_ai) {
                    chessboard.swapColor();
                }
            }
            return;
        } else if (!blackKing) {
            int i = JOptionPane.showConfirmDialog(null,"WHITE WON, WANT TO RESET?","GAME OVER",JOptionPane.YES_NO_CANCEL_OPTION);
            if (i == 0) {
                List<String> chessData = List.of(Chessboard.defaultChessboard.split("\n"));
                chessboard.loadGame(chessData);
                if (chessboard instanceof Chessboard_web) {
                    chessboard.proxy.send(chessboard.opponentID +"#"+ Chessboard.RESET +chessboard.myID);
                }
                if (chessboard instanceof Chessboard_ai) {
                    chessboard.swapColor();
                }
            }
            return;
        }

        ChessColor checkmateColor = JudgeTheWinner();
        if (checkmateColor == ChessColor.BLACK) {
            JOptionPane.showMessageDialog(null, "Black Checkmate");
        } else if (checkmateColor == ChessColor.WHITE) {
            JOptionPane.showMessageDialog(null, "White Checkmate");
        }
    }

    //下面的方法用于判断胜负,返回胜利的颜色
    public ChessColor JudgeTheWinner() {
        //判断白棋会不会输
        ChessComponent[][] chessComponents = chessboard.getChessComponents();
        //true代表能逃走，false代表不能逃走

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessComponents[i][j].getChessColor() == ChessColor.BLACK) {
                    for (ChessComponent checkChess: chessComponents[i][j].getAllCanMoveTo()) {
                        if (checkChess instanceof KingChessComponent) {
                            if (chessboard.getCurrentColor() == ChessColor.BLACK) {
                                return ChessColor.BLACK;
                            }
                            if (!DoubleCheckWhite()) {
                                return ChessColor.BLACK;
                            } else {
                                return ChessColor.NONE;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessComponents[i][j].getChessColor() == ChessColor.WHITE) {
                    for (ChessComponent checkChess: chessComponents[i][j].getAllCanMoveTo()) {
                        if (checkChess instanceof KingChessComponent) {
                            if (chessboard.getCurrentColor() == ChessColor.WHITE) {
                                return ChessColor.WHITE;
                            }
                            if (!DoubleCheckBlack()) {
                                return ChessColor.WHITE;
                            } else {
                                return ChessColor.NONE;
                            }
                        }
                    }
                }
            }
        }

        return ChessColor.NONE;
    }


    //true: king cannot escape; false: king can escape

    public boolean DoubleCheckWhite() {//true有不死的机会，false则无不死的机会
        String chessboardOrigin = this.chessboard.writeOneChessboard();
        boolean outcome = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessComponent checkChess = chessboard.getChessComponents()[i][j];
                if (checkChess.getChessColor() == ChessColor.WHITE) {
                    for (ChessComponent targetPoint: checkChess.getAllCanMoveTo() ) {
                        boolean innerOutcome = true;
                        chessboard.swapChessComponentsWithoutMusicAndStore(checkChess, targetPoint);
                        chessboard.swapColor();
                        for (int k = 0; k < 8; k++) {//遍历走后的所有棋子能不能走到王
                            for (int l = 0; l < 8; l++) {
                                if (chessboard.getChessComponents()[k][l].getChessColor() == ChessColor.BLACK) {
                                    for (ChessComponent checkChess2: chessboard.getChessComponents()[k][l].getAllCanMoveTo()) {
                                        if (checkChess2 instanceof KingChessComponent) {
                                            innerOutcome = false;
                                        }
                                    }
                                }
                            }
                        }//遍历完所有棋子后，看看在这种情况下能不能逃掉
                        if (innerOutcome == true) {
                            outcome = true;
                        }
                        List<String> chessData = List.of(chessboardOrigin.split("\n"));
                        chessboard.loadOneGame(chessData);
                    }
                }
                }

            }
            return outcome;
        }

    public boolean DoubleCheckBlack() {//true有不死的机会，false则无不死的机会
        String chessboardOrigin = this.chessboard.writeOneChessboard();
        boolean outcome = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessComponent checkChess = chessboard.getChessComponents()[i][j];
                if (checkChess.getChessColor() == ChessColor.BLACK) {
                    for (ChessComponent targetPoint: checkChess.getAllCanMoveTo() ) {
                        boolean innerOutcome = true;
                        chessboard.swapChessComponentsWithoutMusicAndStore(checkChess, targetPoint);
                        chessboard.swapColor();
                        for (int k = 0; k < 8; k++) {//遍历走后的所有棋子能不能走到王
                            for (int l = 0; l < 8; l++) {
                                if (chessboard.getChessComponents()[k][l].getChessColor() == ChessColor.WHITE) {
                                    for (ChessComponent checkChess2: chessboard.getChessComponents()[k][l].getAllCanMoveTo()) {
                                        if (checkChess2 instanceof KingChessComponent) {
                                            innerOutcome = false;
                                        }
                                    }
                                }
                            }
                        }//遍历完所有棋子后，看看在这种情况下能不能逃掉
                        if (innerOutcome == true) {
                            outcome = true;
                        }
                        List<String> chessData = List.of(chessboardOrigin.split("\n"));
                        chessboard.loadOneGame(chessData);

                    }
                }
            }

        }
        return outcome;
    }

//        ChessComponent[][] chessComponents = this.chessboard.getChessComponents();
//        for (ChessComponent checkKing: kingCanMoveTo) {
//            //临时变化
//            chessComponents[checkKing.getChessboardPoint().getX()][checkKing.getChessboardPoint().getY()] = king;
//            chessComponents[king.getChessboardPoint().getX()][king.getChessboardPoint().getY()] = new EmptySlotComponent(king.getChessboardPoint(), king.getLocation(), king.getClickController(),king.getHeight());
//            boolean check = true;
//            for (int i = 0; i < 8; i++) {
//                for (int j = 0; j < 8; j++) {
//                    //因为这里check的时候遍历的棋盘是King走动之前的棋盘，所以这里必须要排除King的影响，所以我们要临时改变棋盘
//                    for (ChessComponent checkChess: chessComponents[i][j].getAllCanMoveTo()) {
//                        if (checkChess instanceof KingChessComponent) {
//                            check = false;
//                            break;
//                        }
//                    }
//                }
//            }
//            chessComponents[checkKing.getChessboardPoint().getX()][checkKing.getChessboardPoint().getY()] = checkKing;
//            chessComponents[king.getChessboardPoint().getX()][king.getChessboardPoint().getY()] = king;
//
//            if (check) {
//                return true;
//            }
//        }
//        return false;



    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(ChessComponent chessComponent) {
        if (chessboard instanceof Chessboard_web) {
            return chessComponent.getChessColor() == chessboard.getCurrentColor() && chessComponent.getChessColor() == chessboard.myColor;
        } else if (chessboard instanceof Chessboard_ai) {
            return chessComponent.getChessColor() == chessboard.getCurrentColor() && chessComponent.getChessColor() == chessboard.myColor;
        } else {
            return chessComponent.getChessColor() == chessboard.getCurrentColor();
        }
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(ChessComponent chessComponent) {
        return chessComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
    }


}
