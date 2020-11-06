/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
//this class simulates the ball in n-dim space and run ther program based on a list if instructions
package ndballsim;

import java.util.Arrays;
import java.util.Scanner;

public class Simulator {

    private static boolean log = false;
    private static long startTime;
    private static long parseTime;
    private static long timeToRemove;
    // ... the code being measured ...

    public static void run(String file, int max, boolean doLog, boolean step, boolean infoTag) {
        long parseStartTime = System.nanoTime();//start measuring parcer time
        Instr[] instrs = Parser.parse(file);//this is the list of instructions
        parseTime = System.nanoTime() - parseStartTime;//stop measuring parcer time
        //begine messuring Sim time
        startTime = System.nanoTime();
        log = doLog;
        Scanner in = new Scanner(System.in); //the scanner used for input from console
        String input; // this will be used to hold the input
        int stepsDone = 0; //how many teps we have done

        log("MAX: " + max);
        
        Pos ball = new Pos(0); //the ball itself
        int ballVal = 0; //the value of the ball
        int[] movement = new int[2]; //this represent the balls movement, its [dimention_number, ammount] so if it moving forwards in dim 4 then its [4,1] and backwards is [4,-1]

        int newVal;
        log("Attempting parsing");
        
        log("Parsing completed");
        log("Starting Simulation");
        while (true) {
            //if we are steping through once at a time
            if (step) {
                //ask for input (pauses program)
                System.out.println("advance? enter|ctrl+c");
                in.nextLine();

            }

            //check if there is instruction at balls position
            for (Instr instr : instrs) {
                //we found a matching instruction
                if (ball.equals(instr.pos)) {
                    //what instruction is it?
                    switch (instr.name) {
                        //change the balls movment to forward in the dimention in info 0
                        case ">":
                            movement[0] = (Integer) instr.info[0];
                            movement[1] = 1;
                            log("Movement changed to [" + movement[0] + "," + movement[1] + "]");
                            break;
                        //change the balls movment to backward in the dimention in info 0
                        case "<":
                            movement[0] = (Integer) instr.info[0];
                            movement[1] = -1;
                            log("Movement changed to [" + movement[0] + "," + movement[1] + "]");
                            break;
                        //print out the balls value
                        case "P":
                            System.out.println(ballVal);
                            break;
                        //end the program
                        case "E":
                            log("Program ended");
                            exit(infoTag, stepsDone, instrs.length);
                            break;
                        //this get a input number from the console and set the balls value to it
                        case "%":
                            newVal = 0;
                            System.out.print("\nPlease input a number:");
                            input = getInput(in);
                            try {
                                //parse in a new vaule from command line
                                newVal = Integer.parseInt(input);
                            } catch (NumberFormatException e) {
                                warn("Input \"" + input + "\" did not match a number, assumed to be zero");
                            }
                            //bound it in (0-255)
                            while (newVal < 0) {
                                newVal += 256;
                            }
                            ballVal = newVal % 255;
                            log("Input number \"" + input + "\" read in as number: " + newVal);
                            break;
                        //Y logic case
                        case "Y":
                            //if ballVal is less then info 0 we want to send the ball along dimention info 2
                            if (ballVal < (int) instr.info[0]) {
                                switch ((String) instr.info[1]) {
                                    //foward movement
                                    case ">":
                                        movement[0] = (Integer) instr.info[2];
                                        movement[1] = 1;
                                        break;
                                    //backward moevment
                                    case "<":
                                        movement[0] = (Integer) instr.info[2];
                                        movement[1] = -1;
                                        break;
                                }
                                //otherwise we want to send the ball along dimention info 4
                            } else {
                                switch ((String) instr.info[3]) {
                                    //forward mevemnt
                                    case ">":
                                        movement[0] = (Integer) instr.info[4];
                                        movement[1] = 1;
                                        break;
                                    //backwards movement
                                    case "<":
                                        movement[0] = (Integer) instr.info[4];
                                        movement[1] = -1;
                                        break;
                                }
                            }
                            //log the movement change
                            log("Movement changed to [" + movement[0] + "," + movement[1] + "]");
                            break;
                        //add one to the balls value while keeping bounded in (0-255)
                        case "+":
                            if (ballVal == 255) {
                                ballVal = 0;
                                break;
                            }
                            ballVal = (ballVal + 1);
                            break;
                        //add one to the balls value while keeping bounded in (0-255)
                        case "-":
                            if (ballVal == 0) {
                                ballVal = 255;
                                break;
                            }
                            ballVal = (ballVal - 1);
                            break;
                        //data cell 
                        case "#":
                            //check if the balls movement matches the memeory cells writing direction
                            if ((int) instr.info[2] == movement[0] && (((String) instr.info[1]).equals(">") && movement[1] == 1 || (((String) instr.info[1]).equals("<") && movement[1] == -1))) {
                                //write to the cell
                                instr.info[0] = ballVal;
                                log("MEM CELL WRITTEN Val:" + ballVal + " Pos:" + instr.pos);
                            } else {
                                //read from the cell
                                ballVal = (int) instr.info[0];
                                log("MEM CELL READ Val:" + ballVal + " Pos:" + instr.pos);
                            }
                            break;
                        //input a char
                        case "$":
                            newVal = 0;
                            System.out.print("\nPlease input a char:");
                            input = getInput(in);
                            try {
                                //parse in a new vaule from command line
                                newVal = (int) input.charAt(0);
                            } catch (StringIndexOutOfBoundsException e) {
                                warn("Input \"" + input + "\" was empty, assumed to be 0");
                            }
                            //bound it in (0-255)
                            while (newVal < 0) {
                                newVal += 256;
                            }
                            ballVal = newVal % 255;
                            log("Input number \"" + input + "\" read in as number: " + newVal);
                            break;
                        //output the value of the ball as a char
                        case "p":
                            System.out.print((char) ballVal);
                            break;
                        //the parcer spit out an unknown instruction
                        default:
                            error("Unkown Internal Instruction.\n"
                                    + "This means the parcer spit out an instruction with an unknown name\n"
                                    + "this should have been caught earlier by the parcer"
                                    + "the error message means that \"I\" messed up in some way"
                                    + "if you see this please open an issue on the Github page\n"
                                    + "include a copy of your code, what version of the NDBallSim this error occured on"
                                    + "and the name of the instruction given on the next line "
                                    + "Instruction name:\"" + instr.name + "\"");

                    }
                }
            }
            //log the position of the ball
            log("Pos:" + ball + " Val:" + ballVal);
            //this means the ball did not start moving
            if (movement[1] == 0) {
                error("Ball failed to start moving, did you put a movement instruction in (0)?");
            }
            //actaly move the ball based on the movement
            ball.shift(movement[0], movement[1]);
            //this will only throw an error if the current dimention were movinth through is erased aka (0), in which case the check wont detect anything anyway
                //error if the ball hits the wall
                if (ball.getLength(movement[0]) > 4) {
                    error("The ball hit the wall at " + ball + " and shatterd into a thousand peices");
                    System.exit(1);
                }
                if (ball.getLength(movement[0]) == -1) {
                    error("The ball hit the wall at " + ball + " and shatterd into a thousand peices");
                    System.exit(1);
                }

            stepsDone++;
            log("Step " + stepsDone + " done");
            if (max >= 0 && stepsDone >= max) {
                warn("Program terminated: reached max steps (" + max + "), to disable this use -m -1");
                exit(infoTag, stepsDone, instrs.length);
            }
        }

    }

    //report error and abort program
    private static void error(String desc) {
        System.out.println("NDBall ERROR: " + desc);
        System.exit(1);
    }

    //warn user but dont end program, error recovery will have to be done
    private static void warn(String desc) {
        System.out.println("NDBall WARNING: " + desc);
    }

    //this prints only if log is enabled
    private static void log(String str) {
        if (log) {
            System.out.println("LOG: " + str);
        }
    }

    //this exits the program doing nececary exit stuff
    private static void exit(boolean outputInfo, int steps, int instNum) {
        if (outputInfo) {
            System.out.println("\n\n***** Sim Info *****\n"
                    + "Parse Time: "
                    + ((double) parseTime / 1_000_000.0)
                    + "ms\n"
                    + "Sim Time: "
                    + ((double) (System.nanoTime() - startTime - timeToRemove) / 1_000_000.0)
                    + "ms\n"
                    + "Steps: "
                    + steps
                    + "\n"
                    + "Num of Instr: "
                    + instNum
                    + "\n"
                    + memoryStats()
            );
        }
        System.exit(0);
    }

    //memory useage statisitics
    private static String memoryStats() {
        System.gc();
        int kb = 1024;
        // get Runtime instance
        Runtime instance = Runtime.getRuntime();
        // used memory
        return "Active Mem: ~" + (instance.totalMemory() - instance.freeMemory()) / kb + "KB\n";
        
    }
    //this allows up to get input and pause timer for time waiting for input
    private static String getInput(Scanner scan){
        long start = System.nanoTime();
        String input = scan.nextLine();
        timeToRemove+= (System.nanoTime() - start);
        return input;
    }

}
