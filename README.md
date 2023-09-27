# reversi-othello

Для запуска приложения выполните следующие действия:
1. В файле file.properties в модулe server укажите необходимый вам порт( по умолчанию 6070).
2. В файле file.properties в модуле client и UI укажите необходимые вам:
   ip адрес в поле host ( по умолчанию localhost)
   serverPort( по умолчанию 6070)
   player( значение bot, если за вас будет играть bot; human, если будете играть самостоятельно)
4. В файле file.properties в модуле botfarm укажите необходимый вам port( по умолчанию 6071).
5. Запустите Server.java в модуле server.

чтобы запустить бота, проверьте, что в пункте 2 в поле player вы указали значение bot и выполните следующие действия:

1. В классе GetResponsesMethodsBot в методе commandWhereICanGoGameBot замените название бота на необходимого( как черный, так и белый цвета).
2. Запустите класс client, введите имя игрока бота( не обязательно такое же, как и название бота. имя может быть любым из латинских символов).
3. Бот автоматически подключится к серверу и создаст комнату.
4. Если вы хотите подлкючиться в другую комнату, введите команду CONNECCTTOROOM *roomId*, где roomId - комната, к которой вы хотите подключиться(с вами, или с другим ботом).
5. Как только в комнате окажутся двое игроков - игра начнется автоматически.

Чтобы играть самостоятельно( консольный режим), проверьте, что в пункте 2 в поле player вы указали значение human и выполните следующие действия:
1. Запустите client.
2. Введите команду REGISTRATION *nickname*.
3. Введите команду AUTHORIZATION *nickname*.
   4.1. Если вы хотите подлкючиться в другую комнату, введите команду CONNECCTTOROOM *roomId*, где roomId - комната, к которой вы хотите подключиться( с вами, или с другим ботом).
   4.2. Если вы хотите создать комнату, то введите команду CREATEROOM.
5. Как только в комнате окажутся двое игроков - игра начнется автоматически.

Чтобы играть самостоятельно( графический режим), проверьте, что в пункте 2 в поле player вы указали значение human и выполните следующие действия:
1. Запустите clientGui в модуле UI ( пакет guiClient)
2. Следуйте интуитивно-понятному и пользователь-дружелюбному графическому интерфейсу, чтобы начать играть



