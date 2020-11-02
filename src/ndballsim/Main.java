/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
package ndballsim;

//this is the calss is run with the jar file, it will handle the command line input
public class Main {

    public static void main(String[] args) {
        String version = "V1.0.1";
        String help = "NDBall Simulator " + version + "\n"
                + "Commands are formated like this:\n"
                + "[flags] (file containing code)\n"
                + "the flags are as follows:\n"
                + "-h : This shows help\n"
                + "-l : This will log extra things in the terminal, such as the balls position at each step,\n"
                + "     when memory cells are written to etc\n"
                + "-d : This shows some basic documentation about how to program in NDBall";
        //no insput strings given
        if (args.length == 0) {
            System.out.println(help);
        } else {
            switch (args[0]) {
                case "-h":
                    System.out.println(help);
                    break;
                case "-d":
                    System.out.println("the ball starts at 0,0,0...\n"
                            + "\n"
                            + "dimensions are 5 spaces long (from 0 to 4)\n"
                            + "\n"
                            + "all instructions take up a cell in the n-dim space\n"
                            + "\n"
                            + "cells are defined with the following syntax\n"
                            + "\n"
                            + "POS INSTR    EX: (0,1,...) >1\n"
                            + "\n"
                            + "position are assumed to in the 0th position for all undefined positions\n"
                            + "EX: (1,1,0) = (1,1)\n"
                            + "\n"
                            + "position can also be defined as a list of dim|value as this\n"
                            + "EX: {0,1|5,1} = (1,0,0,0,0,1)\n"
                            + "this is useful for defining things instead of using a lot of zeros \n"
                            + "\n"
                            + "the ball moves along dimensions according to movement instructions\n"
                            + "\n"
                            + ">n :ball moves forward on dim n\n"
                            + "<n :ball moves backward on dim n\n"
                            + "\n"
                            + "if the ball hits a wall the program ends\n"
                            + "\n"
                            + "the ball holds a value, an 8 bit unsigned integer (0-255)\n"
                            + "\n"
                            + "VALUE CHANGE:\n"
                            + "+ :increase the ball's value\n"
                            + "- :decrease the ball's value\n"
                            + "\n"
                            + "the balls value wraps\n"
                            + "\n"
                            + "INPUT/OUTPUT INSTR:\n"
                            + "p :print out the ASCII char associated with the value of the ball\n"
                            + "P :print out the value of the cell\n"
                            + "$ :ask for a char and set balls value to its ASCII value\n"
                            + "% :ask for an int input (0-255) and set the value of the ball to it\n"
                            + "\n"
                            + "LOGIC INSTR:\n"
                            + "Y[X,movA,movB] :if the ball`s value is below a, then move according to movA else move according to movB\n"
                            + "\n"
                            + "MEMORY CELL:\n"
                            + "\n"
                            + "#mov  :a memory cell that holds a value (0-255), mov is a movement statement (ex. >3), if the ball's movement is the same as the cell direction then the memory cell is written to, becoming the balls value. if not the cell is read and the ball becomes the value of the memory cell. they start with a value of 0\n"
                            + "\n"
                            + "SPECIAL INSTR:\n"
                            + "E end program\n"
                            + "\n"
                            + "Check out the wiki for more info https://esolangs.org/wiki/NDBall"
                            + "");
                    break;
                case "-log":
                    Simulator.run(args[1], true);
                    break;
                default:
                    Simulator.run(args[0], false);
                    break;
            }
        }
        System.exit(0);
    }
}
