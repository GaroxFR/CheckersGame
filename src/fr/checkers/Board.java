package fr.checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

    private final Piece[][] pieces = new Piece[10][10];
    private List<Move> possibleMoves = new ArrayList<>();
    private Team toPlay = Team.WHITE;
    private Position selectedPiece;
    private Team winner;

    public Board() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < this.pieces.length; j++) {
                if ((i + j) % 2 == 0) {
                    this.pieces[i][j] = new Piece(Team.BLACK);
                } else {
                    this.pieces[9-i][j] = new Piece(Team.WHITE);
                }
            }
        }
        this.computePossibleMoves();
    }

    private void computePossibleMoves() {
        boolean capturePossible = false;
        this.possibleMoves.clear();
        int dir = this.toPlay.getForwardDirection();
        for (int i = 0; i < this.pieces.length ; i++) {
            for (int j = 0; j < this.pieces[i].length; j++) {
                if (this.pieces[i][j] != null && this.pieces[i][j].getTeam() == this.toPlay) {
                    Position from = new Position(j, i);
                    if (this.getPiece(from).isQueen()) {
                        this.addQueenPossibleMove(from);
                    } else {
                        if(this.addPiecePossibleMoves(from, dir)) {
                            capturePossible = true;
                        }
                    }

                }
            }
        }
        if (capturePossible) {
            this.possibleMoves = this.possibleMoves.stream().filter(Move::isCapture).collect(Collectors.toList());
        }
    }

    private boolean addPiecePossibleMoves(Position from, int dir) {
        boolean capturePossible = false;
        for (int i = -1; i <= 1; i += 2) {
            Position to = new Position(from.getX() + i, from.getY() + dir);
            if (this.isOutOfBound(to)) {
                continue;
            }

            if (this.getPiece(to) == null) {
                this.possibleMoves.add(new Move(from, to));
            } else if (this.getPiece(to).getTeam() != this.toPlay) {
                if (this.addCapturePossibleMove(from, to)) {
                    capturePossible = true;
                }
            }

            to = new Position(from.getX() + i, from.getY() - dir);
            if (this.isOutOfBound(to)) {
                continue;
            }
            if (this.getPiece(to) != null && this.getPiece(to).getTeam() != this.toPlay){
                if(this.addCapturePossibleMove(from, to)){
                    capturePossible = true;
                }
            }
        }
        return capturePossible;
    }

    private void addQueenPossibleMove(Position from) {
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                int dist = 1;
                while(true) {
                    Position to = new Position(from.getX() + i * dist, from.getY() + j * dist);

                    if (this.isOutOfBound(to)) {
                        break;
                    }

                    if (this.getPiece(to) == null) {
                        this.possibleMoves.add(new Move(from, to));
                    } else if (this.getPiece(to).getTeam() != this.toPlay) {
                        this.addCapturePossibleMove(from, to);
                        break;
                    } else {
                        break;
                    }
                    dist++;
                }
            }
        }
    }

    private boolean addCapturePossibleMove(Position from, Position capture) {
        int dirX = (capture.getX() - from.getX()) / Math.abs(capture.getX() - from.getX());
        int dirY = (capture.getY() - from.getY()) / Math.abs(capture.getY() - from.getY());
        Position to = new Position(capture.getX() + dirX, capture.getY() + dirY);
        if (this.isOutOfBound(to) || this.getPiece(to) != null) {
            return false;
        }
        this.possibleMoves.add(new Move(from, to, capture));
        return true;
    }

    private Piece getPiece(Position position) {
        return this.pieces[position.getY()][position.getX()];
    }

    private void setPiece(Position position, Piece piece) {
        this.pieces[position.getY()][position.getX()] = piece;
    }

    private boolean isOutOfBound(Position position) {
        return position.getX() < 0 || position.getX() > 9 || position.getY() < 0 || position.getY() > 9;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < this.pieces.length; i++) {
            for (int j = 0; j < this.pieces[i].length; j++) {
                if (this.pieces[j][i] != null) {
                    this.pieces[j][i].draw(g, i * 72, j * 72);
                }
            }
        }

        if (this.selectedPiece == null) {
            return;
        }

        for (Move possibleMove : this.possibleMoves) {
            if (possibleMove.getFrom().equals(this.selectedPiece)) {
                int x = possibleMove.getTo().getX();
                int y = possibleMove.getTo().getY();

                g.setColor(new Color(38, 177, 38));
                g.fillOval(x * 72 + 16, y * 72 + 16, 40, 40);
            }
        }
    }

    public void onClick(int x, int y) {
        if (winner != null){
            return;
        }
        Piece clicked = this.pieces[y][x];
        if (clicked != null && clicked.getTeam() == this.toPlay) {
            this.selectedPiece = new Position(x, y);
            return;
        }

        if (clicked == null) {
            for (Move possibleMove : this.possibleMoves) {
                if (possibleMove.getFrom().equals(this.selectedPiece) && possibleMove.getTo().equals(new Position(x, y))) {
                    this.playMove(possibleMove);
                    return;
                }
            }
            this.selectedPiece = null;
        }
    }

    private void playMove(Move move) {
        Piece piece = this.getPiece(move.getFrom());
        this.setPiece(move.getFrom(), null );
        this.setPiece(move.getTo(), piece);

        int backRow = this.toPlay.getPromotionRow();
        if (move.getTo().getY() == backRow) {
            piece.setQueen(true);
        }

        if (move.isCapture()) {
            this.setPiece(move.getCapturePosition(), null);
            boolean win = true;
            for (int i = 0 ; i<this.pieces.length ; i++){
                for(int j = 0 ; j<this.pieces[i].length ; j++){
                    if(pieces[i][j].getTeam() != toPlay){
                        win = false;
                        break;
                    }
                }
            }
            if(win){
                this.winner = toPlay;
                return;
            }
            if (this.checkChainCapture(move.getTo())) {
                this.selectedPiece = move.getTo();
                return;
            }
        }

        this.toPlay = this.toPlay == Team.WHITE ? Team.BLACK : Team.WHITE;
        this.computePossibleMoves();
        this.selectedPiece = null;
    }

    private boolean checkChainCapture(Position from) {
        this.possibleMoves.clear();
        if (this.addPiecePossibleMoves(from, 1)) {
            this.possibleMoves = this.possibleMoves.stream().filter(Move::isCapture).collect(Collectors.toList());
            return true;
        }

        return false;
    }
}
