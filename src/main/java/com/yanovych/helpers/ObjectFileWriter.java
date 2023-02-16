package com.yanovych.helpers;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ObjectFileWriter<T> {
    public void writeListOfObjects(String fileName, List<T> obj, boolean append) {
        String objJsonFormat = new Gson().toJson(obj);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append));
            writer.write(objJsonFormat);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
