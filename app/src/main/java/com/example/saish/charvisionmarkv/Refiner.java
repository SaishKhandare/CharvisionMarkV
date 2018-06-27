package com.example.saish.charvisionmarkv;

import android.util.Log;

public class Refiner {

    static int valid=0;
    static int h;
    static int k;

    public static String getField(String field,String data,int draw)
    {

        String[] dataArray = data.split(" ");
        String[] fieldArray = field.split(" ");
        for(h = 0; h<dataArray.length;h++)
        {
            Log.e("OUTPUT", "getField: " + dataArray[h] );

            for(k =0; k<fieldArray.length;k++)
            {
                if(dataArray[h+k].equals(fieldArray[k]))
                {
                    valid = 1;
                }
                else
                {
                    valid = 0;
                    break;
                }
            }
            if(valid == 1)
            {
                //if Draw is specified call the draw method if not specified will return NULL
                if(draw  != -1)
                {
                   return Draw(h+k,draw,dataArray);
                }
            }
        }
        return "NULL";
    }


    //FUNCTION TO DRAW OUT "draw" number of Strings after index "start"
    public static String Draw(int start,int draw,String[]dataArray)
    {
        StringBuilder data = new StringBuilder();
        for(int h=0;h<draw;h++)
        {
            data.append(dataArray[start+h] + " ");
        }
        if(draw == 3)
        {
            //further Refinement NUMBER
            return number(data.toString());
        }
        if(draw == 4)
        {
            //further Refinement TOTAL
            return total(data.toString());
        }
        return data.toString();
    }

    //FUNCTION TO REFINE NUMBER
    public static String number(String num)
    {
        StringBuilder refnum = new StringBuilder();
        int size=0;
        for(int h = 0;h<num.length();h++)
        {
            //check for isdigit
            if(isDigit(num.charAt(h)))
            {
                if(size<12)
                {
                    refnum.append(num.charAt(h));
                    size=size+1;
                }
            }
        }
        if(((Character) refnum.charAt(0)).equals('9')&&((Character) refnum.charAt(0)).equals('1'))
        {
            refnum.delete(0,1);
        }
        else
        {
            refnum.delete(10,11);
        }

        return refnum.toString();
    }

    //FUNCTION TO REFINE TOTAL
    public static String  total(String total)
    {
        StringBuilder refTotal = new StringBuilder();
        for(int h=0;h<total.length();h++)
        {
            if(isDigit(total.charAt(h)))
            {
                refTotal.append(total.charAt(h));
            }
            else if(((Character) total.charAt(h)).equals(' '))
            {
                continue;
            }
            else
            {
                break;
            }
        }
        return refTotal.toString();
    }



    //FUNCTION TO FIND CHARACTER IS DIGIT
    public static Boolean isDigit(Character c)
    {
        if(c.equals('0')||c.equals('0')||c.equals('1')||c.equals('2')||c.equals('3')||c.equals('4')||c.equals('5')||c.equals('6')||c.equals('7')||c.equals('8')||c.equals('9'))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
