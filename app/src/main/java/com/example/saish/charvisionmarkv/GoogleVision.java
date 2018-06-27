package com.example.saish.charvisionmarkv;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.Log;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Block;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Page;
import com.google.api.services.vision.v1.model.Paragraph;
import com.google.api.services.vision.v1.model.Symbol;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.api.services.vision.v1.model.Word;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class GoogleVision {


    static String data = "", textword = "";
    static StringBuilder dataBuild  = new StringBuilder();



    static int nametrap = 0; //used to append few words after "name" to String "namev"
    static int totaltrap = 0; //used to append few words after "total" to String "totalv"
    static int istrapedname = 0; //Ensure "name" is trapped only once
    static int istrapedtotal = 0; //Ensure "total" is trapped only once
    static int result = 0; // used in condition check function


    static String totalv = "";
    static String namev = "";
    static String numberv = "";


    static String[] namearr = new String[]{"name"};
    static String[] totalarr = new String[]{"total"};

    static int isTrapNumber=0;
    static int TrapNumber =0;


    public static String Run_OCR(Context context, Bitmap picture) {



        data ="";
        //Declarations end.....................................................................................

        //creating vision object
        Vision.Builder visionbuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null
        );
        String key = context.getString(R.string.mykey);
        visionbuilder.setVisionRequestInitializer(new VisionRequestInitializer(key));
        final Vision vision = visionbuilder.build();

        //Encoding the Image.
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        //initially picture is to be compressed
        picture.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        // String base64Data = Base64.encodeToString(byteStream.toByteArray(), Base64.URL_SAFE);


        Image inputImage = new Image();
        inputImage.encodeContent(byteStream.toByteArray());

        Feature desiredFeatures = new Feature();
        desiredFeatures.setType("DOCUMENT_TEXT_DETECTION");


        AnnotateImageRequest request = new AnnotateImageRequest();
        request.setImage(inputImage);
        request.setFeatures(Arrays.asList(desiredFeatures));

        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
        batchRequest.setRequests(Arrays.asList(request));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

            try {
                BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();

                //using Textannotation
                final TextAnnotation text = batchResponse.getResponses().get(0).getFullTextAnnotation();
                for (Page page : text.getPages()) {
                    for (Block block : page.getBlocks()) {
                        data = data + "\n new_block_start";

                        for (Paragraph para : block.getParagraphs()) {
                            data = data + "\n new_para_start";
                            for (Word word : para.getWords()) {

                                for (Symbol symbol : word.getSymbols()) {
                                    textword = textword + symbol.getText();
                                }


                                textword = textword + " ";
                                dataBuild.append(textword);

                               // data = data + "\n\n";
                               // data = data + textword ;
                                //data = data + "\n";
                                textword = "";
                            }
                        }
                    }
                }

               // data = data + "\n NAME:" + namev;
               // data = data + "\n TOTAL" + totalv;
               // data = data  + "\n NUMBER " + numberv;
               // data = (data + "\n NAME TRAP : " + istrapedname);
               // data = (data + "\n TOTAL TRAP : " + istrapedtotal);

                data = dataBuild.toString();
                data = data + "\n\n NUMBER::" +Refiner.getField("CUSTOMER NO :",data,3);
                data = data + "\n TOTAL::" + Refiner.getField("TOTAL",data,4);
                return data;

            } catch (IOException e) {
                e.printStackTrace();

                return "SOME ERROR OCCURED";
            }
        }


        return "ERROR SDK < 8";
    }




    //FUNCTION TO TRAP NAME uses name_condition
    public static void name_trap(String textword) {
        if (istrapedname == 0) {
            if (name_condition(textword)) {
                nametrap = 3;
                data = data + "\ntrapped name here \n";
                istrapedname = 1;
            }
        }
    }

    //FUNCTION TO TRAP TOTAL uses total_condition
    public static void total_trap(String textword) {

        if (istrapedtotal == 0) {
            if (total_condition(textword)) {
                totaltrap = 2;
                data = data + "\ntrapped total here \n";
                istrapedtotal = 1;
            }
        }
    }


    //FUNCTION FOR NAME CONDITION to Iterate through name_arr
    public static boolean name_condition(String textword) {
        for (int h = 0; h < namearr.length; h++) {
            if (textword.trim().toLowerCase().equals(namearr[h])) {
                result = 1;
            }
        }
        if (result == 1) {
            result = 0;
            return true;
        } else {
            result = 0;
            return false;
        }
    }

    //FUNCTION FOR TOTAL CONDITION to Iterate through total_arr
    public static boolean total_condition(String textword) {
        for (int h = 0; h < totalarr.length; h++) {
            if (textword.trim().toLowerCase().equals(totalarr[h])) {
                result = 1;

            }
        }
        if (result == 1) {
            result = 0;
            return true;
        } else {
            result = 0;
            return false;
        }
    }

    //GENRAL FUNCTION TO TRAP WORDS BETWEEN TWO KEYWORDS
    //This functions works with trapper block.
    public static void trap_between(String word, String first, String Second)
    {
        if(word.equals(first))
        {
            isTrapNumber =1; //SET the Trap
        }
        //if(Trap == 1 && )


    }

    public static Boolean cmp (String word, String equate)
    {
        if(word.trim().toLowerCase().equals(equate))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}






































































 /*
                                //trap for name:--saving names till customer----------------------------------------------------------------------------------
                                if(textword.trim().toLowerCase().equals("customer"))
                                {
                                    nametrap = 0;
                                }
                                if (nametrap >= 1) {
                                    namev = namev + " "  + textword;
                                }

                                //sets the trap for name:
                                name_trap(textword);
                                // trap for name ends:-------------------------------------------------------------------------------------------------------------

                                // trap for total:------------------------------------------------------------------------------------------------------------
                                if(textword.trim().toLowerCase().equals("shop") || textword.trim().toLowerCase().equals("/") )
                                {
                                    totaltrap = 0;
                                }
                                if (totaltrap >= 1) {
                                    totalv = totalv + " " + textword;
                                }
                                //sets trap for total
                                total_trap(textword);
                                //Trap for total ends here-------------------------------------------------------------------------------------------------


                                //Trapper block starts here--------------------------------------------------------------------------------------------------------------
                                if(cmp(textword,"customer") || isTrapNumber==1)
                                {
                                    isTrapNumber = 1;
                                    if(cmp(textword,"no"))
                                    {
                                        TrapNumber = 4;
                                    }
                                }

                                if(TrapNumber >= 1)
                                {
                                    numberv = numberv + textword;
                                    TrapNumber = TrapNumber -1;
                                }
                                //------------------------------------------------------------------------------------------------------------------------------------

                                */


