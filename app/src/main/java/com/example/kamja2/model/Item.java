package com.example.kamja2.model;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class Item {
    String name;
    Integer price;
    String nutritions;
    String kcal;
    String gram;
    String item;
    String store;
    List<Double> intNutritions;

    public Item(){

    }

    public Item(String name, Integer price, String item){
        this.name = name;
        this.price = price;
        this.item = item;
    }

    public Item(String name, Integer price, String item, String nutritions, String kcal, String gram, String store ){
        this.name = name;
        this.price = price;
        this.intNutritions = setIntNutritions(nutritions);
        this.kcal = kcal;
        this.gram = gram;
        this.item = item;
        this.store = store;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Integer getPrice(){
        return this.price;
    }

    public void setPrice(Integer price){
        this.price = price;
    }

    public List<Double> getIntNutritions(){
        return this.intNutritions;
    }

    public List<Double> setIntNutritions(String nutritions){
        String[] nutritionList = nutritions.split(",");
        intNutritions = new ArrayList<>();
        for(int i=0; i<nutritionList.length; i++){
            intNutritions.add(Double.parseDouble(nutritionList[i]));
        }
        this.intNutritions = intNutritions;
        return intNutritions;
    }

    public String getKcal(){
        return this.kcal;
    }

    public void setKcal(String kcal){
        this.kcal = kcal;
    }

    public String getGram(){
        return this.gram;
    }

    public void setGram(String gram){
        this.gram = gram;
    }

    public String getItem(){
        return this.item;
    }

    public void setItem(String item){
        this.item = item;
    }

    public String getCategory(){
        return this.store;
    }

    public void setCategory(String store){
        this.store = store;
    }
}
