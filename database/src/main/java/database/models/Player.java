package database.models;

import javax.persistence.*;

@Entity
@Table(name = "Player")
public class Player {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "nickname")
    public String nickname;

    public Player() {
    }

    public Player(final String nickname) {
        this.nickname = nickname;
    }
}
