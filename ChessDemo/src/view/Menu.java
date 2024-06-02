package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame implements ActionListener{
    public JButton startLocalGame;

    public JButton gameOver;

//    public JButton ranking;

    public JButton startWebGame;

    public JButton startWitness;

    public JButton startAIBattle;


    public Menu(){
        //播放背景音乐
        Music music = new Music("images/Summer.mp3");
        music.start();

        Font font = new Font("Times New Roman", Font.ITALIC, 18);
        this.startLocalGame = new JButton(new ImageIcon("button/local_game.png"));
        this.gameOver = new JButton(new ImageIcon("button/exit.jpg"));
        this.startWebGame = new JButton(new ImageIcon("button/web_game.jpg"));
        this.startWitness = new JButton(new ImageIcon("button/witness.jpg"));
        this.startAIBattle = new JButton(new ImageIcon("button/AI_game.jpg"));

        startLocalGame.setFont(font);
        gameOver.setFont(font);
        startWebGame.setFont(font);
        startWitness.setFont(font);
        startAIBattle.setFont(font);


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(300, 200);
        setVisible(true);
        ImageIcon background = new ImageIcon("images/图3.jpeg");
        JLabel background0 = new JLabel(background);
        background0.setSize(background.getIconWidth(), background.getIconHeight());
        this.setSize(background.getIconWidth(), background.getIconHeight());
        this.getLayeredPane().add(background0, 0);

        startLocalGame.setVisible(true);
        startLocalGame.setSize(175, 45);
        this.getLayeredPane().add(startLocalGame, 1);

        gameOver.setSize(175, 45);
        gameOver.setVisible(true);
        this.getLayeredPane().add(gameOver, 1);


        startWebGame.setVisible(true);
        startWebGame.setSize(175, 45);
        this.getLayeredPane().add(startWebGame, 1);

        startWitness.setVisible(true);
        startWitness.setSize(175, 45);
        this.getLayeredPane().add(startWitness, 1);

        startAIBattle.setVisible(true);
        startAIBattle.setSize(175, 45);
        this.getLayeredPane().add(startAIBattle, 1);

        startLocalGame.addActionListener(this);
        startWebGame.addActionListener(this);
        startAIBattle.addActionListener(this);
        gameOver.addActionListener(this);
        startWitness.addActionListener(this);


        repaint();

    }

    //开始与结束游戏的监听器
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(startLocalGame)){
            System.out.println("1");
            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760, GameModel.LocalModel, "");
            mainFrame.setVisible(true);
        }else if (e.getSource().equals(gameOver)){
            this.dispose();
            System.exit(0);
        }else if (e.getSource().equals(startWebGame)){
            System.out.println("2");
            String s = JOptionPane.showInputDialog(null, "input ip: ", null);
            if (s != null){
                ChessGameFrame mainFrame = new ChessGameFrame(1000, 760, GameModel.WebModel, s);
                mainFrame.setVisible(true);
            } else {
                JOptionPane .showMessageDialog(null, "ip errors");
            }
        }else if (e.getSource().equals(startAIBattle)){
            System.out.println("3");
            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760, GameModel.AiModel, "");
            mainFrame.setVisible(true);
        }else if (e.getSource().equals(startWitness)){
            System.out.println("4");
            String s = JOptionPane.showInputDialog(null, "input ip: ", null);
            if (s != null){
                ChessGameFrame mainFrame = new ChessGameFrame(1000, 760, GameModel.WitnessModel, s);
                mainFrame.setVisible(true);
            } else {
                JOptionPane .showMessageDialog(null, "ip errors");
            }
        }
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        //todo
//        ranking.setLocation(150, 200);
        startLocalGame.setLocation(75, 50);
        startAIBattle.setLocation(75, 50 + 75);
        startWebGame.setLocation(75, 50 + 75 * 2);
        startWitness.setLocation(75, 50 + 75 * 3);
        gameOver.setLocation(75, 50 + 75 * 4);


        this.getLayeredPane().add(startLocalGame, 1);
//        this.getLayeredPane().add(ranking, 1);
        this.getLayeredPane().add(gameOver, 1);
        this.getLayeredPane().add(startAIBattle, 1);
        this.getLayeredPane().add(startWebGame, 1);
        this.getLayeredPane().add(startWitness, 1);

    }


}