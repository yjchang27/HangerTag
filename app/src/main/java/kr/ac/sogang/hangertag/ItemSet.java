package kr.ac.sogang.hangertag;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by OWNER on 2015-04-10.
 */
public class ItemSet implements Serializable{
    int id;
    String name;
    int price;
    String type;
    char size;
    String description;
    ArrayList<Integer> imageList;

    ItemSet(){
        imageList = new ArrayList<>();
    }
}
