package kr.ac.sogang.hangertag;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by OWNER on 2015-04-10.
 */
public class ItemSet implements Serializable{
    String description;
    ArrayList<Integer> imageList;

    ItemSet(){
        imageList = new ArrayList<>();
    }
}
