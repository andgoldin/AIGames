Andrew Goldin (adg2160)
COMS W4701 - Artificial Intelligence
Project 3: Gomoku Player

Written and tested in Java 1.7 using Eclipse Juno

Included files/folders:
- player_adg2160.java -- contains all of the code for the assignment: tracks game states, player actions, contains evaluation function and maintains gameplay
- adg2160_README.txt -- this file


================= DESCRIPTION =================

For this project, I implemented the game Gomoku with 4 modes of play.
 - Mode 0: Human vs Human. Play with a friend!
 - Mode 1: Human vs Computer. Pits human against AI Player who uses time-based minimax with alpha-beta pruning to decide its moves.
 - Mode 2: Random vs Computer. AI Player plays against player whose moves are exclusively random.
 - Mode 3: Computer vs Computer. Two instances of the AI Player compete against each other.
 
For the minimax algorithm with alpha-beta pruning to work, I implemented a relatively simple evaluation function for a computer player to determine
the utility of a a board. The player will first make some initial observations about the game state:
 - If the player is making the first move, it will move randomly.
 - If the player sees that it can win on the next turn, it will automatically go for it.
 - If the player cannot win on the next turn but detects that the opponent can, it will automatically try to block the opponent.
 
NOTE: The search space for the player (i.e. the number of possible moves to evaluate each turn) is all board spaces adjacent to
(including diagonally) tiles that have already been placed in the board.

After the initial observations, the player will proceed to heuristically evaluate successive game states generated during the
alpha-beta algorithm's recursion. Game states (or "nodes" in the search) are assigned scores as follows:
 - A win state or a state in which the player has an unbounded chain of length (winLength - 1) receives INT_MAX score.
 - A state identical to that above but for the opponent player results in INT_MIN score.
 - If none of the above conditions are met, points are added and subtracted as such:
   - Add 2 to score for each unbounded chain of length >= 2 found on the board.
   - Add 1 to score for each 1-bounded chain of lencth >= 2 found on the board.
   - Subtract 2 from score for each unbounded chain of length >= 2 from opponent's player.
   - Subtract 1 from score for each 1-bounded chain of length >= 2 from opponent's player.

The goal of this evaluation function is to give more weight to board states where the player has more opportunities to win over the opponent, and 
a higher chance of winning over that set of opportunities. More points are awarded for the existence of unbounded chains (i.e. chains that are not
blocked on either side) since they have a greater chance to result in victory for the player who owns the chain than would 1-bounded chains (chains
that are only blocked on one side). The idea is that the more freedom a player has to make moves safely, the better their perceived circumstance.
Immediately returning when detecting a win or block state, and automatically assigning INT_MAX and INT_MIN scores for extreme states cuts down on
computation time, since the player spends less time generating and searching through a tree when it can determine an outcome with near certainty.

The search limits time as follows:
 - If at any point during the search (each time the alphabeta function is called recursively) the elapsed time has surpassed the max alloted time for
   searching, the function call will stop and return the heuristic value of whatever node it is currently examining. This effect will bubble up and
   effectively halt the searching process. A downside to this approach is that the player is not guaranteed to evenly examine the search space, and
   may miss out on making better decisions. This could have been avoided by computing a max depth as a function of the time limit and the branching
   factor of a given state, but this proved to be difficult and problematic and was not implemented.

The search limits depth as follows:
 - If, for a given board, there are at most six possible moves, the search depth will be set to 6.
 - If there are at most 10 possible moves, the search depth will be set to 4.
 - If there are more than 10 possible moves, the search depth will be set to 2.
The more moves that need to be evaluated, the (many) more nodes the algorithm has to search. Through some empirical testing I found these
depth values to be satisfactory for boards of a reasonable dimension.

After playing in Mode 1 against my own player five times:
- mode boardsize winlength timelimit compfirst: OUTCOME
- 1 3 3 2 false: DRAW
- 1 5 3 2 true: COMPUTER WIN
- 1 5 4 3 true: COMPUTER WIN
- 1 10 5 5 false: HUMAN WIN
- 1 19 5 10 false: HUMAN WIN
I was able to beat my player about half the time. Some observations I made were that my player performs well on smaller boards where initial
move space is naturally more limited. When board space opened up (especially with a relatively small winlength), it became easier to outsmart
the AI. Unfortunately, the AI does not always successfully detect opponent chains of (winlength - 1) that are only missing one tile.

After playing in Mode 2 with a random player five times:
- mode boardsize winlength timelimit compfirst: OUTCOME
- 2 5 3 3 true: COMPUTER WIN
- 2 5 4 3 false: COMPUTER WIN
- 2 7 4 5 false: COMPUTER WIN
- 2 10 5 5 true: COMPUTER WIN
- 2 19 5 10 true: COMPUTER WIN
The AI player beat the random player in all 5 games. In testing, the AI player was able to beat the random player every single time. The
shortcuts in move evaluation made the time to victory short for the AI, even on the larger boards.

After playing in Mode 3 with two AI players against one another:
- mode boardsize winlength timelimit: OUTCOME
- 3 4 3 5: PLAYER 1 WIN
- 3 6 4 10: PLAYER 2 WIN
- 3 7 6 5: DRAW (ran this config 5 times by itself, was a draw every time!)
- 3 10 5 5: PLAYER 1 WIN
- 3 19 5 10: PLAYER 2 WIN
Both players seem to win roughly the same amount of times. In extra testing, multiple runs of the same board occasionally produced different
outcomes and winners, since the first move the player makes is always random, and thus makes the process nondeterministic.

=================================================================

Running the program on CLIC:

- cd into the directory containing the listed files/folders
- Compile:
	javac *.java

The program runs on command line arguments, like so:

	java player_adg2160 1 5 3 2 false
	
This will run a game of Gomoku in Mode 1, with a board dimension of 5, a winning chain length of 3, a 2-second time limit for AI Player, and AI Player not moving first.

In general:

	java player_adg2160 <mode> <board_size> <win_length> <time_limit (for modes > 0)> <ai_goes_first (for modes 1 and 2)>
	
Where:
 - <mode> is a game mode, from 0 to 3.
 - <board_size> is a number that determines the dimension of the game board (board_size x board_size).
 - <win_length> is the length of an unbroken chain required for a player to win.
 - <time_limit> the maximum number of seconds the AI player has to make its move.
 - <ai_goes_first> for human vs computer and random vs computer, tells the program whether the computer will go first or second.

If mode is set to 0, the last two arguments are not needed, since they do not apply to human players.
If mode is set to 3, the last argument <ai_goes_first> is not needed since both players are the same.
	
		
Here is a simple output sample on a 3x3 board in mode 1, winlength of 3, where AI moves second and has a 2-second time limit (essentially tic-tac-toe):

java player_adg2160 1 3 3 2 false

Gomoku Interactive!

   ___________  
  |           | 
 0|  -  -  -  |
 1|  -  -  -  |
 2|  -  -  -  |
  |___________| 
     0  1  2  

Player X: choose move (row col): 0 0
Player X moves: [0, 0]

   ___________  
  |           | 
 0|  X  -  -  |
 1|  -  -  -  |
 2|  -  -  -  |
  |___________| 
     0  1  2  

Computer is thinking . . .
Player O moves: [1, 1]

   ___________  
  |           | 
 0|  X  -  -  |
 1|  -  O  -  |
 2|  -  -  -  |
  |___________| 
     0  1  2  

Player X: choose move (row col): 2 2
Player X moves: [2, 2]

   ___________  
  |           | 
 0|  X  -  -  |
 1|  -  O  -  |
 2|  -  -  X  |
  |___________| 
     0  1  2  

Computer is thinking . . .
Player O moves: [0, 1]

   ___________  
  |           | 
 0|  X  O  -  |
 1|  -  O  -  |
 2|  -  -  X  |
  |___________| 
     0  1  2  

Player X: choose move (row col): 2 1
Player X moves: [2, 1]

   ___________  
  |           | 
 0|  X  O  -  |
 1|  -  O  -  |
 2|  -  X  X  |
  |___________| 
     0  1  2  

Computer is thinking . . .
Player O moves: [2, 0]

   ___________  
  |           | 
 0|  X  O  -  |
 1|  -  O  -  |
 2|  O  X  X  |
  |___________| 
     0  1  2  

Player X: choose move (row col): 0 2
Player X moves: [0, 2]

   ___________  
  |           | 
 0|  X  O  X  |
 1|  -  O  -  |
 2|  O  X  X  |
  |___________| 
     0  1  2  

Computer is thinking . . .
Player O moves: [1, 2]

   ___________  
  |           | 
 0|  X  O  X  |
 1|  -  O  O  |
 2|  O  X  X  |
  |___________| 
     0  1  2  

Player X: choose move (row col): 1 0
Player X moves: [1, 0]

   ___________  
  |           | 
 0|  X  O  X  |
 1|  X  O  O  |
 2|  O  X  X  |
  |___________| 
     0  1  2  

Game over! DRAW.