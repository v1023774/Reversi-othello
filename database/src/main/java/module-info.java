module Reversi.othello.database.main {
    requires org.hibernate.orm.core;
    exports database;
    exports database.models;
    requires java.naming;
    requires java.persistence;
    requires org.jetbrains.annotations;
}