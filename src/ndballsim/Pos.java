/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
//this class handles positions in n-dimentions 
package ndballsim;

import java.util.ArrayList;
import java.util.Objects;

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
        public String toString() {
            return dim + "," + length;
        }

    }

    //this array list atores a buch of vectors
    private ArrayList<Vector> list = new ArrayList<>();
    private int highestDim = 0;

    //initalization
    public Pos(int... ints) {
        //for each number in the privided array of ints
        for (int i = 0; i < ints.length; i++) {
            //if the value at dim is not 0
            if (ints[i] != 0) {
                //add a vector for that dim and its length
                list.add(new Vector(i, ints[i]));
                //set the highest dimention to that dimentions
                //this works since dimentions are converted from highest to lowest
                highestDim = i;
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
    
    public int getHighestDim(){
        return highestDim;
    }

    //this tells if the position are the same as each other
    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true   
        if (o == this) {
            return true;
        }
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Pos)) {
            return false;
        }
        // typecast o to Vector so that we can compare data members  
        Pos p = (Pos) o;
        // Compare the data members and return accordingly 
        //if they are not the same length they dont have the same number of defined dimentions
        //they dont share highest dim, must not be be the same
        if(p.highestDim != highestDim){
            return false;
        }
        if (list.size() != p.list.size()) {
            return false;
        } else {
            //for each vector in p check if the matching vector is contained in this pos
            for (Vector v : p.list) {
                if (!list.contains(v)) {
                    return false;
                }
            }
        }
        return true;
    }
    //added to keep hash codes in order whn overrideing equals method
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.list);
        return hash;
    }

    private void updateHighestDim() {
        //set highest dim to 0
        highestDim = 0;
        //go throught each vector in list
        for (Vector v : list) {
            //if the dim of the vector is larger the highest dim
            if (v.dim > highestDim) {
                //set highest dim to the dim of the vector
                highestDim = v.dim;
            }
        }
    }

    //this moves the position along ammount in dimention dim
    public void shift(int dim, int amount) {
        //for each vector in list check if there is a vector in the same dimention
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).dim == dim) {
                //ajust the length of said vector
                list.get(i).length += amount;
                //check if length is zero and this is the highest dimention
                if (list.get(i).length == 0 && highestDim == list.get(i).dim) {
                    //remove that dimention
                    list.remove(i);
                    //refind the highest dimention
                    updateHighestDim();
                } else if (list.get(i).length == 0) {
                    //remove the vector but sincence it is not the highest dimention, no need to refind it
                    list.remove(i);
                }
                return;
            }
        }
        //if no current vector is found to be up dated add it
        list.add(new Vector(dim, amount));
        //update highest dimention
        if (dim > highestDim) {
            highestDim = dim;
        }

    }

    //this outputs the dimention as (a,b,c...)
    @Override
    public String toString() {
        //add a starting {
        String listString = "{";
        //add the peices
        for (Vector v : list) {
            listString += v + "|";
        }
        //remove last "|"
        if (listString.length() != 1) {
            listString = listString.substring(0, listString.length() - 1);
        }
        //add closeing }
        listString += "}";
        return listString;
    }
}
