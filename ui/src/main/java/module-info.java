module Reversi.othello.ui.main {
    requires Reversi.othello.gamelogic.main;
    requires java.desktop;
    requires org.apache.pdfbox;
    exports gui;
    exports ui;
    exports Replayer;
    requires java.naming;
    requires Reversi.othello.database.main;
    requires com.google.gson;
    requires Reversi.othello.client.main;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
}