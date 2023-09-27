package database;

import database.models.Boards;
import database.models.Game;
import database.models.Moves;
import database.models.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Класс для обращения к базе данных и записи/чтению в/из нее.
 */
public class Database {
    public final SessionFactory sessionFactory;

    public Database() {
        Configuration configuration = new Configuration().addAnnotatedClass(Player.class)
                .addAnnotatedClass(Moves.class)
                .addAnnotatedClass(Game.class)
                .addAnnotatedClass(Boards.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * Добавляет игрока в базу данных.
     *
     * @param name - никнейм игрока.
     */
    public boolean addPlayerOnDatabase(final String name) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final List<Player> registeredPlayers = session.createQuery("FROM Player").getResultList();

        for (Player p : registeredPlayers) {
            if (p.nickname.equals(name)) {
                session.getTransaction().commit();
                return false;
            }
        }
        final Player player = new Player(name);
        session.save(player);
        session.getTransaction().commit();
        return true;
    }

    /**
     * Проверяет, зарегестрирван ли пользователь с таким именем.
     *
     * @param name - никнейм игрока.
     */
    public boolean checkRegistration(final String name) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final List<Player> registeredPlayers = session.createQuery("FROM Player").getResultList();
        for (Player p : registeredPlayers) {
            if (p.nickname.equals(name)) {
                session.getTransaction().commit();
                return true;
            }
        }
        session.getTransaction().commit();
        return false;
    }

    /**
     * Добавляет игру в базу данных.
     *
     * @param game - созданная игра.
     */
    public void addGame(final Game game) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(game);
        session.getTransaction().commit();
    }

    /**
     * Добавляет доску в базу данных.
     *
     * @param boards - созданная доска.
     */
    public void addBoards(final Boards boards) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(boards);
        session.getTransaction().commit();
    }

    /**
     * Добавляет ход в базу данных.
     *
     * @param moves - созданный ход.
     */
    public void addMoves(final Moves moves) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(moves);
        session.getTransaction().commit();
    }
    /**
     * Возвращает id игрока по никнейму.
     *
     * @param name - никнейм игрока.
     */
    public int getPlayerId(final String name) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final Query query = session.createQuery("FROM Player where nickname = :name").setParameter("name", name);
        final Player player = (Player) query.uniqueResult();
        session.getTransaction().commit();
        return player.id;
    }
    /**
     * Возвращает name игрока по id.
     *
     * @param PlayerId - Id игрока.
     */
    public String getPlayerName(final int PlayerId) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final Player player = session.get(Player.class, PlayerId);
        session.getTransaction().commit();
        return player.nickname;
    }
    /**
     * Задает поля игры в таблице.
     *
     * @param blackId - Id игрока, который играет черным.
     * @param whiteId - Id игрока,  который играет белым.
     * @param gameId - Id игры.
     * @param winnerColor - цвет игрока, который побеил.
     */
    public void setWinnerInGameWithId(final int blackId, final int whiteId, final int gameId, final String winnerColor) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final char winner = (winnerColor.equals("Black")) ? 'B' : 'W';
        final Query query = session.createQuery("FROM Game where gameId = :gameId").setParameter("gameId", gameId);
        final Game game = (Game) query.uniqueResult();
        game.winner = winner;
        game.blackId = blackId;
        game.whiteId = whiteId;
        session.save(game);
        session.getTransaction().commit();
    }
    /**
     * Возвращает все ходы сделанные в игре.
     *
     * @param gameId - Id игры.
     */
    public List<Moves> getGameMoves(final int gameId) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final Query query = session.createQuery("FROM Moves where gameId = :gameId order by numberOfMove").setParameter("gameId", gameId);
        final List<Moves> moves = query.getResultList();
        session.getTransaction().commit();
        return moves;
    }
    /**
     * Возвращает доски после каждого хода, сделанного в игре.
     *
     * @param gameId - Id игры.
     */
    public List<Boards> getGameBoards(final int gameId) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final Query query = session.createQuery("FROM Boards where gameId = :gameId order by numberOfMove").setParameter("gameId", gameId);
        final List<Boards> boards = query.getResultList();
        session.getTransaction().commit();
        return boards;
    }
    /**
     * Возвращает id игрока, из игры по указанному цвету.
     *
     * @param gameId - Id игры.
     * @param color - цвет игрока.
     */
    public int getPlayerIdFromGame(final String gameId, final char color) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final Query query = session.createQuery("FROM Game where gameId = :gameId").setParameter("gameId", Integer.parseInt(gameId));
        final Game game = (Game) query.uniqueResult();
        session.getTransaction().commit();
        if (color == 'b') {
            return game.blackId;
        }
        return game.whiteId;
    }
}
