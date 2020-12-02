# NDBallSim
Program in NDBall (https://esolangs.org/wiki/NDBall)

this is a java program so it run it install java 13+ and run this in command line

`java -jar "path/to/NDBallSim.jar" <args> "path/to/file.nds"`

the standard file format for saving NDBall files is .nds (N-Dimensinal Space), there structure is identical to just a .txt file, so oyu can use .txt files for storing code if you wish

the args are a bunch of tags as listed below

-h -help : This shows help

-l -log  : This will log extra things in the terminal, such as the ball's position at each step, when memory cells are written to etc

-d -docs : This shows some basic documentation about how to program in NDBall

-s -step : goes through the sim one step at a time, automaticly enables log

-m -max (num) : only runs a max number of steps for the ball (default 10k) use a negative number for unlimited steps

-i -info : spits out info about the program after it completes

