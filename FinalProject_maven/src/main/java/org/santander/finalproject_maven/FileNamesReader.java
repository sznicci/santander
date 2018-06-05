/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Research
 */
public class FileNamesReader {
    
    public static void main(String args[]) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataCSVFiles\\2017\\jan-mar\\filenames.txt"));
            String line = null;
            
            while ((line = br.readLine()) != null) {
                System.out.println("jan-mar\\\\" + line);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileNamesReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
