/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
//this class handles positions in n-dimentions 
package ndballsim;

import java.util.ArrayList;

public class Pos {

    //this is a vector that stores in dim,length
    public class Vector {

        public int dim;
        public int length;

        public Vector(int dim, int length) {
            this.dim = dim;
            this.length = length;
        }
        
        @Override
        public boolean equals(Object o) { 
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Vector)) { 
            return false; 
        } 
        // typecast o to Vector so that we can compare data members  
        Vector v = (Vector) o; 
        // Compare the data members and return accordingly  
        return v.dim == dim && v.length == length; 
    } 
        //we add this so hash codes dont mess up from the equals over ride
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + this.dim;
            hash = 97 * hash + this.length;
            return hash;
        }
        
        @Override
        public String toString(){
            return dim+","+length;
        }

    }

    //this array list atores a buch of vectors
    private ArrayList<Vector> list;

    //initalization
    public Pos(int... ints) {
        list = new ArrayList<>();
        for (int i = 0; i < ints.length; i++) {
            
            if (ints[i] != 0) {
                list.add(new Vector(i, ints[i]));
            }
        }
    }

    public int getLength(int dim) {
        for (Vector v : list) {
            if (v.dim == dim) {
                return v.length;
            }
        }
        return 0;
    }

    public void setLength(int dim, int length) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).dim == dim) {
                list.get(i).length = length;
                return;
            }
        }
        list.add(new Vector(dim, length));
    }

    //this tells if 2 positions are the same in n-dimentional space
    public boolean equals(Pos checkPos) {
        //if they are not the same length they dont have the same number of defined dimentions
        if (list.size() != checkPos.list.size()) {
            return false;
        } else {
            //for each vector in check pos check if 
            for (Vector v: checkPos.list) {
                if(!list.contains(v)){
                    return false;
                }
            }
        }
        return true;
    }
    
    //this removes exces zeros
    private void trim() {
        list.removeIf(list -> list.length == 0);
    }

    //this moves the position along ammount in dimention dim
    public void shift(int dim, int amount) {
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).dim == dim){
                list.get(i).length += amount;
                trim();
                return;
            }
        }
        list.add(new Vector(dim, amount));
    }

    //this outputs the dimention as (a,b,c...)
    @Override
    public String toString() {
        //add a starting {
        String listString = "{";
        //add the peices
        for (Vector v : list) {
            listString += v+"|";
        } 
        //remove last "|"
        if (listString.length() != 1){
            listString = listString.substring(0, listString.length() - 1);
        }
        //add closeing }
        listString += "}";
        return listString;
    }
}
