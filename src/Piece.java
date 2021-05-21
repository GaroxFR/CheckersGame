import java.awt.Color;
import java.awt.Graphics;

public class Piece {

    private static final int SIZE = 60;

    private Team team;
    private boolean queen;

    public Piece(Team team) {
        this.team = team;
        this.queen = false;
    }

    public Team getTeam() {
        return this.team;
    }

    public boolean isQueen() {
        return this.queen;
    }

    public void setQueen(boolean queen) {
        this.queen = queen;
    }

    public void draw(Graphics g, int x, int y) {
        if (this.team == Team.WHITE) {
            g.setColor(new Color(200, 200, 200));
        } else {
            g.setColor(new Color(55, 55, 55));
        }

        g.fillOval(x + 6, y + 6, Piece.SIZE, Piece.SIZE);
    }
}