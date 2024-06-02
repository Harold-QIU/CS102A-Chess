import view.Menu;

public class Main {
    public static void main(String[] args) {

        Menu menu = new Menu();
        menu.startLocalGame.repaint();
        menu.gameOver.repaint();
        menu.startWebGame.repaint();
        menu.startAIBattle.repaint();
        menu.startWitness.repaint();

//        SwingUtilities.invokeLater(() -> {
//            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760);
//            mainFrame.setVisible(true);
//        });
    }
}