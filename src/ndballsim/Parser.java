/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
package ndballsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aspwi
 */
public class Parser {

    public static Instr[] parse(String file) {
        return parse(file, false);
    }

    public static Instr[] parse(String file, boolean unlimit) {
        //this is used later for regex matching
        Matcher match;
        //the current number of the file we are parseing, used when spitting out errors
        int lineNum = 0;
        //this array list will be filled with instructions and returned at the end of the funtion
        ArrayList<Instr> list = new ArrayList<>();
        //this scanner is used for reading in the file, we initialize it here
        Scanner scanner = null;
        //we attempt to open the file to begin scanning, and error if it does not exist
        try {
            scanner = new Scanner(new File(file));
        } catch (FileNotFoundException ex) {
            error("No file found at location \"" + file + "\"");
        }
        //the main while loop, we go through each line and extract the data from it
        while (scanner.hasNextLine()) {
            //increase the line num
            lineNum++;
            //initialize the position we will use for the instruction object later
            Pos pos = null;
            //remove whitespaces *(but not newlines), we dont care about them
            String line = scanner.nextLine().replaceAll(" ", "").replaceAll("\t", "");

            try {
                //this allows us to skip a line if there is nothing in a line
                line.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                //skip the parcing of this line
                continue;
            }
            //parse the position identifier
            switch (line.charAt(0)) {
                //if first char is a ( then we use the format (dim_0_length,dim_1_length...)
                case '(':
                    //use regex to get the text that is "(anything)"
                    match = Pattern.compile("\\(.*?\\)").matcher(line);
                    //if we find a match parse it, else we were missing a closing ), so we output an error
                    if (match.find()) {
                        //this takes the string (a,b,c...) and turns it into an array of strings ["a","b","c"...]
                        //first it gets the text that matched the pattern, then it cuts off the first and last parentheses
                        //then it splits the string based on the delimiter ","
                        String[] inputs = match.group(0).substring(1, match.group(0).length() - 1).split(",");
                        //new int array with the length of int input array
                        int[] ints = new int[inputs.length];
                        //go through each string and try to parse it into a number, then assign it into the same position in the int array
                        for (int i = 0; i < ints.length; i++) {
                            try {
                                ints[i] = Integer.parseInt(inputs[i]);
                            } catch (NumberFormatException e) {
                                error(lineNum, "\"" + inputs[i] + "\" could not be converted into a number");
                            }
                            //check if the provided number is from 0-4
                            if (!unlimit && (ints[i] > 4 || ints[i] < 0)) {
                                error(lineNum, "Invalid dimensional length, must be from 0 to 4");
                            }
                        }
                        //set the pos for the instruction acording to the parced values
                        pos = new Pos(ints);
                        //remove the position string from the current line so we can parse the instruction next
                        line = line.replace(match.group(0), "");

                    } else {
                        // error cause there is no closeing )
                        error(lineNum, "Invalid position syntax, missing closeing )");
                    }
                    break;
                //this means we are useing the format {dim_num,length|dim_num,length...}
                case '{':
                    //get text that matches "{anything}"
                    match = Pattern.compile("\\{.*?\\}").matcher(line);
                    //if we found a match parse it, if not error we are missing a closeing }
                    if (match.find()) {
                        //this takes the string {a,b|c,d...} and turns it into an array of strings ["a","b","c","d"...]
                        //first it gets the text that matched the pattern, then it cuts off the first and last parentheses
                        //then it replaces all | with commas
                        //then it splits the string based on the delimiter ","
                        String[] inputs = match.group(0).substring(1, match.group(0).length() - 1).replaceAll("\\|", ",").split(",");
                        //new array of ints
                        int[] ints = new int[inputs.length];
                        //attempt to turn the array of strings into an array of ints, if we cant then output error
                        for (int i = 0; i < ints.length; i++) {
                            try {
                                ints[i] = Integer.parseInt(inputs[i]);
                            } catch (NumberFormatException e) {
                                error(lineNum, "\"" + inputs[i] + "\" could not be converted into a number");
                            }
                        }
                        // check if number of ints is odd (an un closed pair) or if its 0 (no ints) and error if either
                        if (ints.length % 2 == 1 || ints.length == 0) {
                            error(lineNum, "Invalid position syntax, please format it like this {dim,length|dim,length|...}");
                        }
                        //here we will build the position based off the data in the ints array
                        pos = new Pos(0);
                        //go through the ints array in pars of 2, each pair is dim_number,ammount
                        for (int i = 0; i < ints.length; i += 2) {
                            //skip the shifting if the shift amount is 0
                            //this prevents from someone useing just {0,0} then it being shifted along 0
                            //resulting in it being cleaned by the removal of dimentions with 0 length in the shift function.
                            //causeing {0,0} to generate a instruction with no positions.
                            if (ints[i + 1] == 0) {
                                continue;
                            }
                            //shift the current position object along dim_number by ammount
                            pos.shift(ints[i], ints[i + 1]);
                            //check if we shifted the instruction out of the 5 cell area in each dimention
                            if (!unlimit && (ints[i + 1] > 4 || ints[i + 1] < 0)) {
                                error(lineNum, "Invalid dimensional length, must be from 0 to 4");
                            }
                        }
                        //remove the position string from the current line so we can parse the instruction next
                        line = line.replace(match.group(0), "");
                    } else {
                        error(lineNum, "Invalid position statement, missing closeing }");
                    }
                    break;
                //this line is a comment
                case '/':
                    //skip this line and start with the next one
                    continue;
                default:
                    //if we dont find a ( or { as the first char then the position decoration is malformed
                    error(lineNum, "Invalid start of new line, must be a position decloration (a,b,c...) or {a,b|c,d...}, or a comment /");
                    break;
            }   
            //parse the instruction
            try {
                //this allows us to tell an error if there is no instruction for a position
                line.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                error(lineNum, "No instruction provided");
            }
            // this int is not the dimention, but is used to stor the dimension for things that require movement (like > and <)
            int dim = 0;
            switch (line.charAt(0)) {
                //forward movement instruction
                case '>':
                    //try to read in a number ignoring the > at the start
                    try {
                    dim = Integer.parseInt(line.substring(1, line.length()));
                } catch (NumberFormatException e) {
                    //was not a number
                    error(lineNum, "\"" + line.substring(1, line.length()) + "\" could not be converted into a number");
                }
                //add the instruction to the list of instructions to be outputed
                list.add(new Instr(pos, ">", dim));
                break;
                //backward movement instruction
                case '<':
                    //try to read in a number ignoring the < at the start
                    try {
                    dim = Integer.parseInt(line.substring(1, line.length()));
                } catch (NumberFormatException e) {
                    //was not a number
                    error(lineNum, "\"" + line.substring(1, line.length()) + "\" could not be converted into a number");
                }
                //add the instruction to the list of instructions to be outputed
                list.add(new Instr(pos, "<", dim));
                break;
                //its a P instruction, print
                case 'P':
                    //check if its just an P instruction
                    if (line.length() == 1) {
                        list.add(new Instr(pos, "P"));
                        break;
                    }
                    //check if instruction is large enough to be PSt
                    if (line.length() > 5) {
                        //check if it is PSt[?]
                        if (line.charAt(1) == 'S' && line.charAt(2) == 't' && line.charAt(3) == '[' && line.charAt(line.length() - 1) == ']') {
                            try {
                                list.add(new Instr(pos, "PSt", Integer.parseInt(line.substring(4, line.length()-1) )));
                            } catch (NumberFormatException e) {
                                //was not a number
                                error(lineNum, "\"" + line.substring(1, line.length()-1) + "\" could not be converted into a number");
                            }
                            break;
                        }
                    }
                    //uh oh, the instruction was neither
                    error(lineNum, "Unknown Instruction \"" + line + "\"");
                //Its an E instruction, end program or end string
                case 'E':
                    //check if its just an E instruction
                    if (line.length() == 1) {
                        list.add(new Instr(pos, "E"));
                        break;
                    }
                    //check if instruction is large enough to be ESt
                    if (line.length() > 2) {
                        //check if it is ESt
                        if (line.charAt(1) == 'S' && line.charAt(2) == 't') {
                            list.add(new Instr(pos, "ESt"));
                            break;
                        }
                    }
                    //uh oh, the instruction was neither
                    error(lineNum, "Unknown Instruction \"" + line + "\"");
                // its a % instruction, read in number
                case '%':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "%"));
                    break;
                //its a Y logic instruciotn, this is complicated and formated like this Y[a,>b,>c]
                case 'Y':
                    //check if the correct length
                    if (line.length() < 10) {
                        error(lineNum, "Y logic Instruction malformed got: \"" + line + "\" you need this format Y[10, >2, <7]");
                    }
                    //check if it has the [
                    if (line.charAt(1) != '[') {
                        error(lineNum, "Y logic instruction requires a starting \"[\"");
                    }
                    //check if it has the ]
                    if (line.charAt(line.length() - 1) != ']') {
                        error(lineNum, "Y logic instruction requires a ending \"]\"");
                    }
                    //get a list of strings , this takes Y[a,>b,>c] and turns it into ["a",">b",">c"]
                    //it chops off the Y[ and ]
                    //then it splits based on delimiter ","
                    String[] inputs = line.substring(2, line.length() - 1).split(",");
                    //this is the vaule that determines which way the ball goes
                    int value = 0;
                    //this is the dimention of the first mevement
                    int dim1 = 0;
                    //this is the dimention of the second movement
                    int dim2 = 0;
                    //try to parse first string to value
                    try {
                        value = Integer.parseInt(inputs[0]);
                    } catch (NumberFormatException e) {
                        error(lineNum, "\"" + inputs[0] + "\" could not be converted into a number");
                    }
                    //error if the provided value is out of (0-255)
                    if (value < 0 || value > 255) {
                        error(lineNum, "Y logic operation only excepts values from 0 to 255");
                    }
                    //check if the first movement instruction includes a movement
                    if (inputs[1].charAt(0) == '>' || inputs[1].charAt(0) == '<') {
                        //parse in first dimention
                        try {
                            dim1 = Integer.parseInt(inputs[1].substring(1, inputs[1].length()));
                        } catch (NumberFormatException e) {
                            error(lineNum, "\"" + inputs[1].substring(1, inputs[1].length()) + "\" could not be converted into a number");
                        }
                    } else {
                        error(lineNum, "Y Logic instruction requires a directinal input EX: Y[10, >2, <7]");
                    }
                    //check if the second movement instruction includes a movement
                    if (inputs[2].charAt(0) == '>' || inputs[2].charAt(0) == '<') {
                        //parse in second dimention
                        try {
                            dim2 = Integer.parseInt(inputs[2].substring(1, inputs[2].length()));
                        } catch (NumberFormatException e) {
                            error(lineNum, "\"" + inputs[2].substring(1, inputs[2].length()) + "\" could not be converted into a number");
                        }
                    } else {
                        error(lineNum, "Y Logic instruction requires a directinal input EX: Y[10, >2, <7]");
                    }
                    //add a new Ylogic instruction with movment string and dimnetion passed on
                    list.add(new Instr(pos, "Y", value, "" + inputs[1].charAt(0), dim1, "" + inputs[2].charAt(0), dim2));
                    break;
                case '+':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    //add a new + instruction
                    list.add(new Instr(pos, "+"));
                    break;
                case '-':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    //add a new - instruction
                    list.add(new Instr(pos, "-"));
                    break;
                //its a memeory cell
                case '#':
                    //check for a momenment indicator
                    if (line.charAt(1) == '>' || line.charAt(1) == '<') {
                        //parse in the dimention
                        try {
                            dim = Integer.parseInt(line.substring(2, line.length()));
                        } catch (NumberFormatException e) {
                            error(lineNum, "\"" + line.substring(2, line.length()) + "\" could not be converted into a number");
                        }
                    } else {
                        error(lineNum, "Memory cell requires a direction ex: #>12");
                    }
                    //add the memory cell to the instruction list
                    list.add(new Instr(pos, "#", 0, line.charAt(1), dim));
                    break;
                //if we cant find an instruction we error
                //input a char
                case '$':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "$"));
                    break;
                //print out a char
                case 'p':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "p"));
                    break;
                //mirror instruction, reverses direction
                case '|':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "|"));
                    break;
                //random instruction sets the balls value to (0-255)
                case 'R':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "R"));
                    break;
                //apioform instruction add 1 to hive
                case 'a':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "a"));
                    break;
                //flower instruction remove 1 from hive
                case 'f':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "f"));
                    break;
                //queen instruction, set hive value to 0
                case 'q':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "q"));
                    break;
                //hive cell
                case 'H':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "H"));
                    break;
                //one way mirror instruction
                case 'K':
                    try {
                    //try to add a mirror instructions, and read the direction and dimention all at once
                    list.add(new Instr(pos, "K", line.charAt(1), Integer.parseInt(line.substring(2, line.length()))));
                } catch (NumberFormatException e) {
                    //error the number is malformed
                    error(lineNum, "\"" + line.substring(2, line.length()) + "\" could not be converted into a number");
                }
                break;
                //nector instruction
                case 'n':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "n"));
                    break;
                //line of text read in instruction
                case 'L':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    //the line instruction reads in a whole line of text. it is stored into the arrylist defined under the instr and filled at runtime.
                    list.add(new Instr(pos, "L", new ArrayList<Character>()));
                    break;
                //swap cell instrution
                case 's':
                    //check if the correct length
                    checkLength(line, 1, lineNum);
                    list.add(new Instr(pos, "s", 0));
                    break;
                //sync cell
                case 'S':
                    //check which S instruction it is
                    switch (line.charAt(1)) {
                        case '[':
                            //check if it has the ]
                            if (line.charAt(line.length() - 1) != ']') {
                                error(lineNum, "Sync instruction requires a ending \"]\"  ex. S[num]");
                            }
                            try {
                                list.add(new Instr(pos, "S", Long.parseLong(line.substring(2, line.length() - 1)) * 1000000, 0l));
                            } catch (NumberFormatException e) {
                                //was not a number
                                error(lineNum, "\"" + line.substring(2, line.length() - 1) + "\" could not be converted into a number");
                            }
                            break;
                        case 't':
                            if (line.charAt(2) != '[') {
                                error(lineNum, "String instruction requires a starting \"[]\"  ex. St[num]");
                            }
                            if (line.charAt(line.length() - 1) != ']') {
                                error(lineNum, "String instruction requires a ending \"]\"  ex. St[num]");
                            }
                            try {
                                list.add(new Instr(pos, "St", Integer.parseInt(line.substring(3, line.length() - 1))));
                            } catch (NumberFormatException e) {
                                //was not a number
                                error(lineNum, "\"" + line.substring(3, line.length() - 1) + "\" could not be converted into a number");
                            }

                            break;
                        default:
                            error(lineNum, "Instruction S missing next char. it either need to a \"[\" for a sync ex. S[num] or a \"t\" for string ex. St[num]");
                    }

                    break;
                default:
                    error(lineNum, "Unknown Instruction \"" + line.charAt(0) + "\"");
                    break;

            }
        }
        //sort the array list based on the highest dimention
        Collections.sort(list);
        
        //check for dulicates
        //crawl though the sorted array
        for (int i = 1; i < list.size(); i++) {
            //check if the pos is equal to the pevious
            if (list.get(i).pos.equals(list.get(i-1).pos)){
                error("uh oh, there is more then one instruction in pos: "+ list.get(i).pos);
            }
        }
        
        //return a list of all the instruction we parsed
        return list.toArray(new Instr[list.size()]);
    }

    //this prints out an error to user and stops the program
    private static void error(int line, String desc) {
        System.out.println("NDBall Parse ERROR @ LINE " + line + ": " + desc);
        System.exit(1);
    }

    private static void error(String desc) {
        System.out.println("NDBall Parse ERROR: " + desc);
        System.exit(1);
    }

    //this fucntion errors the program if the provided string is the the given length, used when cheking to make sure that the 1 instruction chars are actaly one char
    private static void checkLength(String toCheck, int length, int lineNum) {
        if (toCheck.length() != length) {
            error(lineNum, "Unknown or malformed Instruction \"" + toCheck + "\"");
        }
    }

}
