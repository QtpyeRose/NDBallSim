/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
//this class simulates the ball in n-dim space and run ther program based on a list if instructions
package ndballsim;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;

public class Simulator {

    private static boolean log = false;
    private static long startTime;
    private static long parseTime;
    private static long timeToRemove;
    // ... the code being measured ...

    public static void run(String file, int max, boolean doLog, boolean step, boolean infoTag, boolean unlimit) {
        //timeing stuff and 
        log("Attempting parsing");
        long parseStartTime = System.nanoTime();//start measuring parcer time
        Instr[] instrs = Parser.parse(file, unlimit);//this is the sorted list of instructions
        parseTime = System.nanoTime() - parseStartTime;//stop measuring parcer time
        log("Parsing completed");
        //begine messuring Sim time
        startTime = System.nanoTime();

        //this hash map maps the highest dimention to the position of the first ocourance of that dmention in the instr list
        HashMap<Integer, Integer> startPos = new HashMap<>();
        //assemble the hash map
        for (int i = 0; i < instrs.length; i++) {
            if (!startPos.containsKey(instrs[i].pos.getHighestDim())) {
                startPos.put(instrs[i].pos.getHighestDim(), i);
            }
        }

        //varaibles and stats
        log = doLog;
        String input; // this will be used to hold the input
        int stepsDone = 0; //how many teps we have done

        //object used in simulation
        Random ran = new Random();
        Scanner in = new Scanner(System.in); //the scanner used for input from console

        //log the max amount of steps
        log("MAX: " + max);

        //simulation values
        Pos ball = new Pos(0); //the ball itself
        int ballVal = 0; //the value of the ball
        int[] movement = new int[2]; //this represent the balls movement, its [dimention_number, ammount] so if it moving forwards in dim 4 then its [4,1] and backwards is [4,-1]
        int hiveVal = 0;//this is the value of the hive cell
        int newVal; //this is use to store a vlue for later use in the program

        
        log("Starting Simulation");
        while (true) {
            //if we are steping through once at a time
            if (step) {
                //ask for input (pauses program)
                System.out.println("advance? enter|ctrl+c");
                in.nextLine();

            }
            //throw an error if there is no hashmap key
            if (startPos.get(ball.getHighestDim()) == null) {
                error("Traveled into a new highest dimension (" + movement[0] + ") that does not have any instructions, you would just hit a wall");
            }

            //check if there is instruction at balls position
            for (int i = startPos.get(ball.getHighestDim()); i < instrs.length; i++) {
                if (ball.getHighestDim() != instrs[i].pos.getHighestDim()) {
                    break;
                }
                //we found a matching instruction
                if (ball.equals(instrs[i].pos)) {
                    //what instruction is it?
                    switch (instrs[i].name) {
                        //change the balls movment to forward in the dimention in info 0
                        case ">":
                            movement[0] = (Integer) instrs[i].info[0];
                            movement[1] = 1;
                            log("Movement changed to [" + movement[0] + "," + movement[1] + "]");
                            break;
                        //change the balls movment to backward in the dimention in info 0
                        case "<":
                            movement[0] = (Integer) instrs[i].info[0];
                            movement[1] = -1;
                            log("Movement changed to [" + movement[0] + "," + movement[1] + "]");
                            break;
                        //print out the balls value
                        case "P":
                            System.out.print(ballVal);
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
                            ballVal = newVal % 256;
                            log("Input number \"" + input + "\" read in as number: " + newVal);
                            break;
                        //Y logic case
                        case "Y":
                            //check if Y logiv operator is checking for impossible number
                            //if ballVal is less then info 0 we want to send the ball along dimention info 2
                            if (ballVal < (int) instrs[i].info[0]) {
                                switch ((String) instrs[i].info[1]) {
                                    //foward movement
                                    case ">":
                                        movement[0] = (Integer) instrs[i].info[2];
                                        movement[1] = 1;
                                        break;
                                    //backward moevment
                                    case "<":
                                        movement[0] = (Integer) instrs[i].info[2];
                                        movement[1] = -1;
                                        break;
                                }
                                //otherwise we want to send the ball along dimention info 4
                            } else {
                                switch ((String) instrs[i].info[3]) {
                                    //forward mevemnt
                                    case ">":
                                        movement[0] = (Integer) instrs[i].info[4];
                                        movement[1] = 1;
                                        break;
                                    //backwards movement
                                    case "<":
                                        movement[0] = (Integer) instrs[i].info[4];
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
                            if (matchMove(movement, (char) instrs[i].info[1], (int) instrs[i].info[2])) {
                                //write to the cell
                                instrs[i].info[0] = ballVal;
                                log("MEM CELL WRITTEN Val:" + ballVal + " Pos:" + instrs[i].pos);
                            } else {
                                //read from the cell
                                ballVal = (int) instrs[i].info[0];
                                log("MEM CELL READ Val:" + ballVal + " Pos:" + instrs[i].pos);
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
                            ballVal = newVal % 256;
                            log("Input number \"" + input + "\" read in as number: " + newVal);
                            break;
                        //output the value of the ball as a char
                        case "p":
                            System.out.print((char) ballVal);
                            break;
                        //mirror instruction, reverses direction
                        case "|":
                            //set the direction of movement to its oppisite
                            movement[1] = -movement[1];
                            break;
                        //one way mirror 
                        case "K":
                            //if the movement of ball matches defined movemnt of mirror
                            if (matchMove(movement, (char) instrs[i].info[0], (int) instrs[i].info[1])) {
                                //let the ball travel though
                                break;
                            } else {
                                //reverse movement direction
                                movement[1] = -movement[1];
                            }
                            break;
                        //random instruction sets the balls value to (0-255)
                        case "R":
                            log("ball set to random value");
                            ballVal = ran.nextInt(256);//set ball to random int
                            break;
                        //apioform instruction add 1 to hive
                        case "a":
                            hiveVal = (hiveVal + 1) % 256;
                            break;
                        //flower instruction remove 1 from hive
                        case "f":
                            hiveVal--;
                            if (hiveVal == -1) {
                                hiveVal = 255;
                            }
                            break;
                        //queen instruction, set hive value to 0
                        case "q":
                            hiveVal = 0;
                            break;
                        //hive cell
                        case "H":
                            ballVal = hiveVal;
                            break;
                        case "n":
                            hiveVal = ballVal;
                            break;
                        case "L":
                            //check if the list of chars is empty
                            if (((ArrayList<Character>) instrs[1].info[0]).isEmpty()) {
                                //ask for a string
                                System.out.print("\nPlease input a string:");
                                input = getInput(in);
                                //add each char to the array
                                for (char c : input.toCharArray()) {
                                    //check if the charactor is askki
                                    if (c < 0 || c > 255) {
                                        warn("Char \"" + c + "\" not recognised. assumed to be 1");
                                        ((ArrayList<Character>) instrs[1].info[0]).add((char) 1);
                                        //ship the rest of the code and start from next one
                                        continue;
                                    }
                                    ((ArrayList<Character>) instrs[1].info[0]).add(c);
                                }
                                //add an extra 0 to the end to show that the it has run out of chars
                                ((ArrayList<Character>) instrs[1].info[0]).add((char) 0);
                            }
                            //set the balls value to the first char
                            ballVal = (int) ((ArrayList<Character>) instrs[1].info[0]).remove(0);
                            break;
                        //a swap cell, swap the value of ball and this cell;
                        case "s":
                            newVal = (int) instrs[i].info[0];
                            instrs[i].info[0] = ballVal;
                            ballVal = newVal;
                            break;
                        //the parser spit out an unknown instruction
                        default:
                            error("Unknown Internal Instruction.\n"
                                    + "This means the parser spit out an instruction with an unknown name\n"
                                    + "this should have been caught earlier by the parser\n"
                                    + "the error message means that \"I\" messed up in some way\n"
                                    + "if you see this please open an issue on the Github page\n"
                                    + "include a copy of your code, what version of the NDBallSim this error occured on\n"
                                    + "and the name of the instruction given on the next line\n"
                                    + "Instruction name: \"" + instrs[i].name + "\"");

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
            //this will only throw an error if the current dimention were moving through is erased aka (0), in which case the check wont detect anything anyway
            //error if the ball hits the wall and the unlimit tag is not set
            if (ball.getLength(movement[0]) > 4 && !unlimit) {
                error("The ball hit the wall at " + ball + " and shatterd into a thousand peices");
                System.exit(1);
            }
            if (ball.getLength(movement[0]) == -1 && !unlimit) {
                error("The ball hit the wall at " + ball + " and shatterd into a thousand peices");
                System.exit(1);
            }

            //incroment steps counter
            stepsDone++;
            //log incorment in steps counter
            log("Step " + stepsDone + " done");
            
            //end program cause max steps reached
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

    //this exits the program doing nececary exit stuff, souch as printing info under the info tag
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

    //this allows you to get input and pause program execution length timer while waiting for input
    private static String getInput(Scanner scan) {
        long start = System.nanoTime();
        String input = scan.nextLine();
        timeToRemove += (System.nanoTime() - start);
        return input;
    }

    //this complex function takes in the movemnt of the ball as mov, a direction char ( > or < ), and the dimention. and cheks if they match 
    //aka if balls movement is the same af a specific direction
    private static boolean matchMove(int[] mov, char dir, int dim) {
        return (dim == mov[0] && ((dir == '>' && mov[1] == 1) || (dir == '<' && mov[1] == -1)));
    }
}
