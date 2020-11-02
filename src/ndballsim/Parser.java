/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
package ndballsim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aspwi
 */
public class Parser {

    public static Instr[] parse(String file) {
        //this is used later for regex matching
        Matcher match;
        //the current number of the file we are parseing, used when spitting out errors
        int lineNum = 0;
        //this array list will be filled with instructions and returned at the end of the funtion
        ArrayList<Instr> list = new ArrayList<>();
        //this scanner is used for reading in the file, we initalise it here
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
            //intinalise the position we will use for the instruction object later
            Pos pos = null;
            //remove whitespaces, we dont care about them
            String line = scanner.nextLine().replaceAll(" ", "").replaceAll("\t", "");

            //parse the position identifier
            switch (line.charAt(0)) {
                //if first char is a ( then we pase with (dim_0_length,dim_1_length...)
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
                        }
                        //set the pos for the instructs acording to the parced values
                        pos = new Pos(ints);
                        //remove the position string from the current line so we can parse the instruction next
                        line = line.replace(match.group(0), "");

                    } else {
                        // error cause there is no closeing )
                        error(lineNum, "Invalid position, missing closeing )");
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
                            error(lineNum, "Invalid Position syntax, please format it like this {dim,length|dim,length|...}");
                        }
                        //here we will build the position based off the data in the ints array
                        pos = new Pos(0);
                        //go through the ints array in pars of 2, each pair is dim_number,ammount
                        for (int i = 0; i < ints.length; i += 2) {
                            //shift the current position object along dim_number by ammount
                            pos.shift(ints[i], ints[i + 1]);
                            //check if we shifted the instruction out of the 5 cell area in each dimention
                            if (ints[i + 1] > 4 || ints[i + 1] < 0) {
                                error(lineNum, "Invalid dimentinal length, must be from 0 to 4");
                            }
                        }
                        //remove the position string from the current line so we can parse the instruction next
                        line = line.replace(match.group(0), "");
                    } else {
                        error(lineNum, "Invalid position statement, missing closeing }");
                    }
                    break;
                default:
                    //if we dont find a ( or { as the first char then the position decoration is malformed
                    error(lineNum, "Invalid start of new line, must be a position decloration (a,b,c...) or {a,b|c,d...}");
                    break;
            }
            //parse the instruction
            try {
                //this allows us to tell an error if there is no instruction for a position
                line.charAt(0);
            } catch (StringIndexOutOfBoundsException e) {
                error(lineNum, "No instruction provided");
            }
            // this int is not the dimention, but is used to stor tyhe dimention for things that require movement (like > and <)
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
                    list.add(new Instr(pos, "P"));
                    break;
                //Its an E instruction, end program
                case 'E':
                    list.add(new Instr(pos, "E"));
                    break;
                // its a % instruction, read in number
                case '%':
                    list.add(new Instr(pos, "%"));
                    break;
                //its a Y logic instruciotn, this is complicated and formated like this Y[a,>b,>c]
                case 'Y':
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
                    //add a new + instruction
                    list.add(new Instr(pos, "+"));
                    break;
                case '-':
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
                    list.add(new Instr(pos, "#", 0, "" + line.charAt(1), dim));
                    break;
                    //if we cant find an instruction we error
                //input a char
                case '$':
                    list.add(new Instr(pos, "$"));
                    break;
                //print out a char
                case 'p':
                    list.add(new Instr(pos, "p"));
                    break;
                default:
                    error(lineNum, "Unknown Instruction \"" + line.charAt(0) + "\"");
                    break;
                

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
}
