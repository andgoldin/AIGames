Andrew Goldin (adg2160)
COMS W4701 - Artificial Intelligence
Project 2: Sokoban Search Algorithm

Written and tested in Java 1.7 using Eclipse Juno

Included files/folders:
- PuzzleState.java -- contains the code that tracks puzzle states, including board configuration and the sequence of moves
- Solver.java -- contains functions which implement the required search algorithms for the project
- Sokoban.java -- contains the main method, based on command line arguments will run an interactive game or run the solver with specified parameters
- puzzles/ -- folder containg the text files for the three puzzles whose results are shown below
    - p1.txt
	- p2.txt
	- p3.txt
- adg2160_README.txt -- this file


== DESCRIPTION ==
For this project, I implemented five search algorithms for solving Sokoban Puzzles.
 - Breadth-first search: implements breadth first search on a puzzle using a FIFO queue.
 - Depth-first search: implements depth first search on a puzzle using a LIFO queue (stack), imposing a depth limit
 - Uniform-cost search: similar to breadth-first search, but uses a priority queue to prioritize explored nodes based on cost (for this, normal moves have cost 1, pushes have cost 2)
 - Greedy best-first search: similar to uniform-cost search, but prioritizes nodes based on a heuristic that estimates cost to goal
 - A* search: similar to greedy best-first search, but prioritizes nodes based on a function that combines the estimation heuristic with the current cost from the start node
 
For this project, I created two heuristic functions:
 - Function 1 estimates the cost to goal based on the total manhattan distance among three disjoint box/goal pairs
 - Function 2 estimates the cost to goal based on the total straight line distance among three disjoint box/goal pairs
 
The program also computes the following statistics for each search run:
 - Number of nodes generated during the search
 - Number of nodes containing states that were generated previously
    - any node that generates at least one already-generated state is added to this total
 - Number of nodes on the fringe when termination occurs
 - Number of nodes on the explored list when termination occurs
 - The actual runtime of the algorithm, in seconds



Running the program on CLIC:
- cd into the directory containing the listed files/folders
- Compile:
	javac *.java

The program runs on command line arguments, like so:

	java Sokoban puzzles/p2.txt gbfs 2 s
	
This will run the solver on the puzzle specified in puzzles/p2.txt, using a greedy best first search with heuristic 2, and will display statistics.

In general:

	java Sokoban <filename> <searchtype> <depthlimit (only if searchtype is dfs)> <heuristic (only if searchtype is gbfs or astar)> <stats>
	
Where:
 - <filename> is path to a valid puzzle text file
 - <searchtype> determines the search algorithm.
    - valid inputs:
	   - bfs (breadth first search)
	   - dfs (depth first search)
	   - ucs (uniform cost search)
	   - gbfs (greedy best first search)
	   - astar (A* search)
 - <depthlimit> is a number, the depth limit for depth first search
 - <heuristic> represents the type of heuristic function
	- valid inputs:
	   - 1 (first heuristic: total manhattan distance)
	   - 2 (second heuristic: total straight line distance)
 - <stats> tells the program whether to print stats
    - input s to include stats, input nothing to omit stats
	
		
If you only include one argument (the file name), the program creates an interactive game!
You can specify moves by typing u, d, l, and r.

Sokoban Interactive!

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Choose move (u, d, l, r): d

Board:
  ######
  # .. #
  # $$@#
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Choose move (u, d, l, r):





	
	
Example output of search algorithms:

=== BFS ===

java Sokoban puzzles/p1.txt bfs s

Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: breadth-first search
Sequence: d, l, l, u, R, d, r, U, U, U, d, d, r, r, u, u, u, l, L

Statistics:
Nodes generated: 800
Nodes containing previous states: 303
Nodes on the fringe: 22
Nodes on explored list: 345
Total runtime: 0.023 seconds

java Sokoban puzzles/p2.txt bfs s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: breadth-first search
Sequence: d, l, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, r, d, d, r, r, u, L, d, l, U, U

Statistics:
Nodes generated: 1449
Nodes containing previous states: 517
Nodes on the fringe: 17
Nodes on explored list: 589
Total runtime: 0.047 seconds

java Sokoban puzzles/p3.txt bfs s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: breadth-first search
Sequence: l, l, D, D, D, D, D, D, l, d, d, d, r, r, u, u, L, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, l, l, l, d, d, r, r, U, d, l, l, u, u, r, R, d, d, d, r, r, u, u, L, U, U, U, U, U, U, l, u, R, d, d, d, d, d, d, d, r, d, d, l, l, u, U, d, l, l, u, u, r, R, d, r, U, U, U, U, U, U

Statistics:
Nodes generated: 9475
Nodes containing previous states: 3845
Nodes on the fringe: 35
Nodes on explored list: 4173
Total runtime: 1.531 seconds


=== DFS ===

java Sokoban puzzles/p1.txt dfs 300 s

Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: depth-first search (depth limit: 300)
Sequence: u, u, u, l, l, d, d, d, R, d, r, U, U, U, d, d, d, l, l, u, u, u, u, r, R, d, d, d, d, r, r, u, u, u, u, L, L

Statistics:
Nodes generated: 113
Nodes containing previous states: 38
Nodes on the fringe: 20
Nodes on explored list: 66
Total runtime: 0.007 seconds

java Sokoban puzzles/p2.txt dfs 300 s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: depth-first search (depth limit: 300)
Sequence: u, l, D, r, d, d, l, U, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, D, r, d, d, l, U, r, r, r, u, L, d, l, U, U

Statistics:
Nodes generated: 1335
Nodes containing previous states: 480
Nodes on the fringe: 21
Nodes on explored list: 546
Total runtime: 0.038 seconds

java Sokoban puzzles/p3.txt dfs 300 s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: depth-first search (depth limit: 300)
Sequence: l, l, D, D, D, D, D, D, l, d, d, d, r, r, u, u, L, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, l, l, l, d, d, r, r, U, d, d, r, r, u, u, L, u, L, D, r, r, d, d, l, l, U, l, l, u, u, R, R, D, r, U, U, U, U, U, U, d, d, d, d, d, d, r, d, d, l, l, U, U, r, u, u, u, u, u, u, l, u, R, d, d, d, d, d, d, d, l, d, l, l, u, u, r, R, d, r, U, U, U, U, U, U

Statistics:
Nodes generated: 4329
Nodes containing previous states: 1758
Nodes on the fringe: 48
Nodes on explored list: 1932
Total runtime: 0.322 seconds


=== UCS ===

java Sokoban puzzles/p1.txt ucs s

Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: uniform cost search
Sequence: d, l, l, u, R, d, r, U, U, U, d, d, r, r, u, u, u, l, L

Statistics:
Nodes generated: 851
Nodes containing previous states: 326
Nodes on the fringe: 18
Nodes on explored list: 363
Total runtime: 0.031 seconds

java Sokoban puzzles/p2.txt ucs s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: uniform cost search
Sequence: d, l, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, r, d, d, r, r, u, L, d, l, U, U

Statistics:
Nodes generated: 1451
Nodes containing previous states: 524
Nodes on the fringe: 14
Nodes on explored list: 587
Total runtime: 0.051 seconds

java Sokoban puzzles/p3.txt ucs s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: uniform cost search
Sequence: l, l, D, D, D, D, D, D, l, d, d, d, r, r, u, u, L, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, l, l, l, d, d, r, r, U, d, l, l, u, u, r, R, d, d, d, r, r, u, u, L, U, U, U, U, U, U, l, u, R, d, d, d, d, d, d, d, r, d, d, l, l, u, U, d, l, l, u, u, r, R, d, r, U, U, U, U, U, U

Statistics:
Nodes generated: 9654
Nodes containing previous states: 3927
Nodes on the fringe: 26
Nodes on explored list: 4244
Total runtime: 1.601 seconds



=== Greedy best-first, first heuristic ===

java Sokoban puzzles/p1.txt gbfs 1 s

Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: greedy best first search (Cost heuristic: total Manhattan distance)
Sequence: d, l, l, u, R, d, r, U, U, U, d, d, r, r, u, u, u, l, L

Statistics:
Nodes generated: 275
Nodes containing previous states: 99
Nodes on the fringe: 15
Nodes on explored list: 125
Total runtime: 0.015 seconds

java Sokoban puzzles/p2.txt gbfs 1 s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: greedy best first search (Cost heuristic: total Manhattan distance)
Sequence: u, l, D, r, d, d, l, U, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, D, r, d, r, r, u, L, d, l, U, d, d, l, U, r, u, U

Statistics:
Nodes generated: 1532
Nodes containing previous states: 558
Nodes on the fringe: 9
Nodes on explored list: 616
Total runtime: 0.072 seconds

java Sokoban puzzles/p3.txt gbfs 1 s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: greedy best first search (Cost heuristic: total Manhattan distance)
Sequence: l, l, D, D, D, D, D, D, l, d, d, d, r, r, u, u, L, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, l, l, l, d, d, r, r, U, d, l, l, u, u, r, R, d, d, d, r, r, u, u, L, U, U, U, U, U, U, l, u, R, d, d, d, d, d, d, l, l, l, d, d, r, r, U, r, u, L, r, d, r, d, d, l, l, u, l, l, u, u, R, R, l, l, d, d, r, r, d, r, r, u, u, l, U, U, U, U, U, U

Statistics:
Nodes generated: 9777
Nodes containing previous states: 4010
Nodes on the fringe: 18
Nodes on explored list: 4294
Total runtime: 1.699 seconds


=== Greedy best-first, second heuristic ===

java Sokoban puzzles/p1.txt gbfs 2 s

Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: greedy best first search (Cost heuristic: total straight line distance)
Sequence: u, u, u, l, l, d, d, d, R, d, r, U, U, U, d, d, r, r, u, u, u, l, L

Statistics:
Nodes generated: 173
Nodes containing previous states: 62
Nodes on the fringe: 17
Nodes on explored list: 86
Total runtime: 0.013 seconds

java Sokoban puzzles/p2.txt gbfs 2 s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: greedy best first search (Cost heuristic: total straight line distance)
Sequence: d, l, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, D, r, d, r, r, u, L, d, l, U, U, d, d, d, l, U

Statistics:
Nodes generated: 1136
Nodes containing previous states: 411
Nodes on the fringe: 22
Nodes on explored list: 472
Total runtime: 0.04 seconds

java Sokoban puzzles/p3.txt gbfs 2 s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: greedy best first search (Cost heuristic: total straight line distance)
Sequence: l, l, D, D, D, D, D, D, l, l, l, d, d, r, r, d, r, r, u, u, L, u, l, l, l, d, d, r, r, U, r, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, L, d, d, l, l, u, u, R, R, d, d, d, r, r, u, u, L, U, U, U, U, U, U, l, u, R, d, d, d, d, d, d, d, r, d, d, l, l, u, U, d, l, l, u, u, r, R, d, r, U, U, U, U, U, U

Statistics:
Nodes generated: 5228
Nodes containing previous states: 2086
Nodes on the fringe: 45
Nodes on explored list: 2298
Total runtime: 0.539 seconds


=== A*, first heuristic ===

java Sokoban puzzles/p1.txt astar 1 s

Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: A* search (Cost heuristic: total Manhattan distance)
Sequence: d, l, l, u, R, d, r, U, U, U, d, d, r, r, u, u, u, l, L

Statistics:
Nodes generated: 606
Nodes containing previous states: 229
Nodes on the fringe: 18
Nodes on explored list: 263
Total runtime: 0.03 seconds

java Sokoban puzzles/p2.txt astar 1 s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: A* search (Cost heuristic: total Manhattan distance)
Sequence: d, l, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, r, d, d, r, r, u, L, d, l, U, U

Statistics:
Nodes generated: 1023
Nodes containing previous states: 366
Nodes on the fringe: 21
Nodes on explored list: 425
Total runtime: 0.039 seconds

java Sokoban puzzles/p3.txt astar 1 s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: A* search (Cost heuristic: total Manhattan distance)
Sequence: l, l, D, D, D, D, D, D, l, d, d, d, r, r, u, u, L, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, l, l, l, d, d, r, r, U, d, l, l, u, u, r, R, d, d, d, r, r, u, u, L, U, U, U, U, U, U, l, u, R, d, d, d, d, d, d, d, r, d, d, l, l, u, U, d, l, l, u, u, r, R, d, r, U, U, U, U, U, U

Statistics:
Nodes generated: 6364
Nodes containing previous states: 2559
Nodes on the fringe: 54
Nodes on explored list: 2803
Total runtime: 0.798 seconds


=== A*, second heuristic ===

java 
Board:
#######
# .   #
# # # #
# # # #
# $@  #
#     #
#######

Search type: A* search (Cost heuristic: total straight line distance)
Sequence: d, l, l, u, R, d, r, U, U, U, d, d, r, r, u, u, u, l, L

Statistics:
Nodes generated: 675
Nodes containing previous states: 255
Nodes on the fringe: 18
Nodes on explored list: 290
Total runtime: 0.026 seconds

java Sokoban puzzles/p2.txt astar 2 s

Board:
####
# .#
#  ###
#*@  #
#  $ #
#  ###
####

Search type: A* search (Cost heuristic: total straight line distance)
Sequence: d, l, U, r, r, r, d, L, u, l, l, d, d, r, U, l, u, R, u, u, l, D, r, d, d, r, r, u, L, d, l, U, U

Statistics:
Nodes generated: 1433
Nodes containing previous states: 513
Nodes on the fringe: 17
Nodes on explored list: 583
Total runtime: 0.057 seconds

java Sokoban puzzles/p3.txt astar 2 s

Board:
  ######
  # ..@#
  # $$ #
  ## ###
   # #
   # #
#### #
#    ##
# #   #
#   # #
###   #
  #####

Search type: A* search (Cost heuristic: total straight line distance)
Sequence: l, l, D, D, D, D, D, D, l, d, d, d, r, r, u, u, L, u, u, u, u, u, u, u, r, r, d, L, u, l, D, D, D, D, D, D, l, l, l, d, d, r, r, U, d, l, l, u, u, r, R, d, d, d, r, r, u, u, L, U, U, U, U, U, U, l, u, R, d, d, d, d, d, d, l, l, l, d, d, r, r, U, d, l, l, u, u, r, R, d, r, U, U, U, U, U, U

Statistics:
Nodes generated: 9204
Nodes containing previous states: 3722
Nodes on the fringe: 54
Nodes on explored list: 4069
Total runtime: 1.535 seconds