package database.models;

import javax.persistence.*;

@Entity
@Table(name = "Moves")
public class Moves {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "row")
    public int row;

    @Column(name = "col")
    public int col;
    @Column(name = "numberOfMove")
    public int numberOfMove;
    @Column(name = "color")
    public char color;
    @Column(name = "gameId")
    public int gameId;

    public Moves(final int row, final int col, final int numberOfMove, final char color, final int gameId) {
        this.row = row;
        this.col = col;
        this.numberOfMove = numberOfMove;
        this.color = color;
        this.gameId = gameId;
    }

    public Moves() {
    }
}
