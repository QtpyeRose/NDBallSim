/*
 * NDBall Simulator by Aspen Wilson is licensed under CC0 1.0. To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0 
 */
//this class handles positions in n-dimentions 
package ndballsim;

import java.util.ArrayList;

public class Pos {

    //this array list stores values like this (length_in_dim_0, length_in_dim_1, length_in_dim_2...)
    //this is specificly private to allow for more efficent Pos classes to be able to be used with replacement of this class, withought having to reprogram everything
    private ArrayList<Integer> list;
    private int sum;

    //initalization
    public Pos(int... ints) {
        //new array list
        ArrayList<Integer> arraylist = new ArrayList<>();
        //add all the ints from the input to the list
        for (int i : ints) {
            arraylist.add(i);
        }
        //set the object to the array list generated
        this.list = arraylist;
        arraylist = null;//forces this to be garbge collected (jsut in case)
        //remove excess zeros from the end of the line
        trim();
    }

    public ArrayList<Integer> getPos() {
        return list;
    }

    public void setPos(ArrayList<Integer> newList) {
        list = newList;
    }

    //this tells if 2 positions are the same in n-dimentional space
    public boolean equals(Pos checkPos) {
        //assumend to be true
        boolean isEqual = true;
        //if there not the same length they dont have the same highest dimention so not equal
        if (list.size() != checkPos.list.size()) {
            isEqual = false;
        } else {
            //check if the value in each dimnetion is the same
            for (int i = 0; i < list.size(); i++) {
                //if not 
                if (list.get(i) != checkPos.list.get(i)) {
                    //not equal
                    isEqual = false;
                    //stop the for loop, we dont need to check anymore
                    break;
                }
            }
        }
        return isEqual;
    }

    //this removes exces zeros
    private void trim() {
        //this deals with if the length is zero in all dimntions
        //add all the ints in the array together
        sum = 0;
        for (int i : list) {
            sum += i;
        }
        //if sum = 0 
        if (sum == 0) {
            //set list to (0)
            list.clear();
            list.add(0);
            //exit the function
            return;
        }
        //if not we will reach here
        //remove extra zeros from the end untill no more are left
        while (list.get(list.size() - 1) == 0) {
            list.remove(list.size() - 1);
        }
    }

    //this moves the position along ammount in dimention dim
    public void shift(int dim, int amount) {
        //if the dimention were trying to write to is not defined, add more 0`s untill we reach it
        if (list.size() - 1 < dim) {
            while (list.size() - 1 != dim) {
                list.add(0);
            }
        }
        //shift the value at dimention dim by ammount
        list.set(dim, list.get(dim) + amount);
        //trim off any zeros, this only matters if we shift so the last value is 0
        trim();
    }

    //this outputs the dimention as (a,b,c...)
    @Override
    public String toString() {
        //start pos
        String listString = "(";
        //add ints
        for (int i : list) {
            listString += i + ", ";
        }
        //remove last ", "
        listString = listString.substring(0, listString.length() - 2);
        //add last )
        listString += ")";
        return listString;
    }
}
