package database.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Game")
public class Game {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column(name = "winner")
    public char winner;

    @Column(name = "blackId")
    public int blackId;

    @Column(name = "whiteId")
    public int whiteId;

    @Column(name = "gameId")
    public int gameId;

    public Game() {
    }

    public Game(final int blackId, final int whiteId, final char winner, final int gameId) {
        this.winner = winner;
        this.blackId = blackId;
        this.whiteId = whiteId;
        this.gameId = gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return winner == game.winner && blackId == game.blackId && whiteId == game.whiteId && gameId == game.gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner, blackId, whiteId, gameId);
    }

    public Game(final int gameId) {
        this.gameId = gameId;
    }
}