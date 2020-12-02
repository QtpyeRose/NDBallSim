# NDBallSim
Program in NDBall (https://esolangs.org/wiki/NDBall)

this is a java program so it run it install java 13+ and run this in command line

java -jar "path/to/NDBallSim.jar" <args> "path/to/file.nds"

the standard file format for saving NDBall files is .nds (N-Dimensinal Space), there structure is identical to just a .txt file, so oyu can use .txt files for storing code if you wish

the args are a bunch of tags as listed below

-h -help : This shows help

-l -log  : This will log extra things in the terminal, such as the ball's position at each step, when memory cells are written to etc

-d -docs : This shows some basic documentation about how to program in NDBall

-s -step : goes through the sim one step at a time, automaticly enables log

-m -max (num) : only runs a max number of steps for the ball (default 10k) use a negative number for unlimited steps

-i -info : spits out info about the program after it completes

the -d tag show some basic docs as follows:


the ball starts at 0,0,0...

dimensions are 5 spaces long (from 0 to 4)

all instructions take up a cell in the n-dim space

cells are defined with the following syntax

POS INSTR    EX: (0,1,...) >1

position are assumed to in the 0th position for all undefined positions
EX: (1,1,0) = (1,1)

position can also be defined as a list of dim|value as this
EX: {0,1|5,1} = (1,0,0,0,0,1)
this is useful for defining things instead of using a lot of zeros

the ball moves along dimensions according to movement instructions

 >n :ball moves forward on dim n
 <n :ball moves backward on dim n
 | :a mirror, direction of ball is reversed, dimention remains unchanged
 Kmov :a one way mirror, direction of ball is reversed unless the balls movment matches mov

if the ball hits a wall the program ends

the ball holds a value, an 8 bit unsigned integer (0-255)

VALUE CHANGE:
 + :increase the ball's value
 - :decrease the ball's value
 R :sets the ball's value to a random number from 0 to 255 (inclusive)
 the balls value wraps

INPUT/OUTPUT INSTR:
p :print out the ASCII char associated with the value of the ball
P :print out the value of the cell
$ :ask for a char and set balls value to its ASCII value
% :ask for an int input (0-255) and set the value of the ball to it

LOGIC INSTR:
Y[X,movA,movB] :if the ball`s value is below a, then move according to movA else move according to movB

MEMORY CELL:

#mov  :a memory cell that holds a value (0-255), mov is a movement statement (ex. >3), if the ball's movement is the same as the cell direction then the memory cell is written to, becoming the balls value. if not the cell is read and the ball becomes the value of the memory cell. they start with a value of 0

APIOFORMS:
these instructions allows you to keep a single value apart from the balls value and change it
a :an apioform joins the hive, increasing the have value by 1
f :an apioform leaves the hive to polinate flowers, decreasing the value of the hive by 1
q :the queen leaves the hive, taking all apioforms with her, hive value is now 0
n :nector attract apioforms to or away from the hive untill its value matches the ball
H :the hive itself, when run into the ball`s value becomes the hive value
SPECIAL INSTR:
E end program

Check out the wiki for more info https://esolangs.org/wiki/NDBall
