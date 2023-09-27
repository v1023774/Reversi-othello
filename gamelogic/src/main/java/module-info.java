module Reversi.othello.gamelogic.main {
    requires org.apache.logging.log4j;
    exports logic;
    exports gamelogging;
    exports parsing;
    requires java.naming;
    requires Reversi.othello.database.main;
    requires org.jetbrains.annotations;
}