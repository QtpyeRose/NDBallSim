/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */

//this class defines the Instruction object, it has no real maningful data, its just to group together data together
package ndballsim;

import java.util.Arrays;

public class Instr {
    //pos the position of the instruction in n-dim space
    public Pos pos;
    //the name of the instruction, this will be used to indentify what the instruction does during simlation
    public String name;
    //this array of objects will be used for extra info needed to be used when running specific instructions, such as memory cells or Y logic
    public Object[] info;
    
    //the constructer
    public Instr(Pos pos,String name, Object... inputs){
        this.pos = pos;
        this.name = name;
        this.info = inputs; 
    }
    
    @Override
    public String toString(){
        return "Instr: "+name+", "+pos+", "+Arrays.toString(info);
    }
    
}