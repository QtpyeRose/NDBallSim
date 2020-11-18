/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
package ndballsim;
//this is the calss is run with the jar file, it will handle the command line input
public class Main {

    public static void main(String[] args) {
        
        boolean step = false;
        boolean log = false;
        boolean info = false;
        int max = 10000;
        String version = "V1.1.0";
        String help = "NDBall Simulator " + version + "\n"
                + "Commands are formated like this:\n"
                + "[flags] (file containing code)\n"
                + "the flags are as follows:\n"
                + "-h -help : This shows help\n"
                + "-l -log  : This will log extra things in the terminal, such as the ball's position at each step,\n"
                + "             when memory cells are written to etc\n"
                + "-d -docs : This shows some basic documentation about how to program in NDBall\n"
                + "-s -step : goes through the sim one step at a time, automaticly enables log\n"
                + "-m -max (num) : only runs a max number of steps for the ball (default 10k) use a negative number for unlimited steps\n"
                + "-i -info : spits out info about the program after it completes";
        //no insput strings given
        if (args.length == 0) {
            System.out.println(help);
        } else {
            for(int i = 0; i < args.length; i++){
                switch(args[i]){
                    case "-h":
                    case "-help":
                        System.out.println(help);
                        break;
                    case "-s":
                    case "-step":
                        step = true;
                        //no break because step also enables log
                    case "-l":
                    case "-log":
                        log = true;
                        break;
                    case "-m":
                    case "-max":
                        try {
                                max = Integer.parseInt(args[i+1]);
                            } catch (NumberFormatException|ArrayIndexOutOfBoundsException e) {
                                error("Max tag (-m -max) requires a valid number");
                            }
                        i++;
                        break;
                    case "-i":
                    case "-info":
                        info = true;
                        break;
                    case "-d":
                    case "-docs":
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
                            + "| :a mirror, direction of ball is reversed, dimention remains unchanged\n"
                            + "Kmov :a one way mirror, direction of ball is reversed unless the balls movment matches mov\n"
                            + "\n"
                            + "if the ball hits a wall the program ends\n"
                            + "\n"
                            + "the ball holds a value, an 8 bit unsigned integer (0-255)\n"
                            + "\n"
                            + "VALUE CHANGE:\n"
                            + "+ :increase the ball's value\n"
                            + "- :decrease the ball's value\n"
                            + "R :sets the ball's value to a random number from 0 to 255 (inclusive)"
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
                            + "APIOFORMS:\n"
                            + "these instructions allows you to keep a single value apart from the balls value and change it\n"
                            + "a :an apioform joins the hive, increasing the have value by 1\n"
                            + "f :an apioform leaves the hive to polinate flowers, decreasing the value of the hive by 1\n"
                            + "q :the queen leaves the hive, taking all apioforms with her, hive value is now 0\n"
                            + "n :nector attract apioforms to or away from the hive untill its value matches the ball"
                            + "H :the hive itself, when run into the ball`s value becomes the hive value"
                            + "SPECIAL INSTR:\n" 
                            + "E end program\n"
                            + "\n"
                            + "Check out the wiki for more info https://esolangs.org/wiki/NDBall"
                            + "");
                            break;
                    default:
                        Simulator.run(args[i], max, log, step, info);
                        System.exit(0);
                        break;
                    
                }
            }
        }
        System.exit(0);
    }
    //tells user error and ends program
    private static void error(String str){
        System.out.println("ERROR: "+str);
        System.exit(1);
    }
}
