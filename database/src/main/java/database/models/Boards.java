package database.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Класс созданный для хранения доски и сохранения объектов в базу данных
 */
@Entity
@Table(name = "Boards")
public class Boards {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "board")
    public String board;

    @Column(name = "numberOfMove")
    public int numberOfMove;

    @Column(name = "gameId")
    public int gameId;

    public Boards() {
    }

    public Boards(final String board, final int numberOfMove, final int gameId) {
        this.board = board;
        this.numberOfMove = numberOfMove;
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boards boards = (Boards) o;
        return numberOfMove == boards.numberOfMove && gameId == boards.gameId && Objects.equals(board, boards.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, numberOfMove, gameId);
    }
}
