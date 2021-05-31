package fr.checkers;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;

public class Display extends JPanel {

    private static final int BOARD_SIZE = 10;
    private static final int FRAME_SIZE = 720;
    private static final int CELL_SIZE = Display.FRAME_SIZE / Display.BOARD_SIZE;

    private final Board board;

    public Display(Board board) {
        this.board = board;
        JFrame frame = new JFrame("Jeu de dame");
        frame.setSize(Display.FRAME_SIZE + 14, Display.FRAME_SIZE + 34);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        this.addMouseListener(new MouseListener(board));
        frame.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int i = 0; i < Display.BOARD_SIZE; i++) {
            for (int j = 0; j < Display.BOARD_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(new Color(23, 96, 153));
                } else {
                    g.setColor(new Color(144, 213, 189));
                }

                g.fillRect(i * Display.CELL_SIZE, j * Display.CELL_SIZE, Display.CELL_SIZE, Display.CELL_SIZE);
            }
        }

        this.board.draw(g);
    }
}
