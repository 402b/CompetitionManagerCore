package com.github.b402.cmc.core.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModuleDescription {

    private String name;
    private List<String> denepdns;
    private String mainClass;

    public ModuleDescription(InputStream inputStream) {
        try (InputStreamReader br = new InputStreamReader(inputStream)) {
            JsonParser jp = new JsonParser();
            JsonElement e = jp.parse(br);
            if(e.isJsonObject()){
                JsonObject json = e.getAsJsonObject();
                name = json.get("name").getAsString();
                denepdns = new ArrayList<>();
                if(json.has("depends")){
                    JsonArray arr = json.get("depends").getAsJsonArray();
                    for (JsonElement d : arr) {
                        denepdns.add(d.getAsString());
                    }
                }
                mainClass = json.get("main").getAsString();
            }else{
                throw new IllegalArgumentException("传入的模块说明文件不是json文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getDenepdns() {
        return denepdns;
    }

    public String getMainClass() {
        return mainClass;
    }
}
