package ru.jgalactics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataProvider {
    private Logger LOG = LoggerFactory.getLogger(DataProvider.class);
    private String dirPath;
    public DataProvider(String dirPath){
        this.dirPath = dirPath;
    }

    public void dump(double[][] data, String filename){
        String filePath = dirPath + filename;
        LOG.info("Writing array to {}" , filePath);
        try(RandomAccessFile rafile = new RandomAccessFile(filePath, "rw")){
            //write number of columns to first bytes
            rafile.writeInt(data.length);
            //write number of stars to first bytes
            rafile.writeInt(data[0].length);

            //write data column by column. like "xxxyyyzzzVxVxVxVyVyVyVzVzVz"
            for(int i = 0; i < data.length; i++){
                for(int k = 0; k < data[0].length; k++){
                    rafile.writeDouble(data[i][k]);
                }
            }

        } catch (FileNotFoundException ex) {
            LOG.error("File cant be opened", ex);
        } catch (IOException ex){
            LOG.error("Smth wrong", ex);
        }
        LOG.info("Array dumped to {}" , filePath);
    }
}
