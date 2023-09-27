module Reversi.othello.client.main {
    requires com.google.gson;
    requires org.jline;
    requires Reversi.othello.gamelogic.main;
    requires org.apache.logging.log4j;
    requires Reversi.othello.localgame.main;
    requires java.desktop;
    requires org.jetbrains.annotations;

    exports client;
    exports clientrequest;
    exports clientresponse;

    opens clientrequest to com.google.gson;
    opens clientresponse to com.google.gson;
}