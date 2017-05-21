# TicTacToe!
Simple version of TicTacToe. Human player is player 1, AI is player 2.

# Build
Clone the repo:
```git clone https://github.com/ilganeli/TicTacToe.git```

Compile with :
```mvn clean package```

# Run
Run with:
```java -cp target/tictactoe-1.0-SNAPSHOT.jar com.homework.Main```

You can configure the AI with an optional parameter on the command line. Options are available, "GREEDY", "EVIL", or "RANDOM". Random selects moves randomly. GREEDY and EVIL gets a little more clever in how it fights!

```java -cp target/tictactoe-1.0-SNAPSHOT.jar com.homework.Main GREEDY```

```java -cp target/tictactoe-1.0-SNAPSHOT.jar com.homework.Main RANDOM```

```java -cp target/tictactoe-1.0-SNAPSHOT.jar com.homework.Main EVIL```
