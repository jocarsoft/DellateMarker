package com.iquitosplay.dellatemarker;

import org.json.*;
import java.util.*;


public class Puntos{

    private String descripcionLugar;
    private String id;
    private String imagenLugar;
    private String latitud;
    private String longitud;
    private String nombreLugar;

    public Puntos() {

    }

    public void setDescripcionLugar(String descripcionLugar){
        this.descripcionLugar = descripcionLugar;
    }
    public String getDescripcionLugar(){
        return this.descripcionLugar;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setImagenLugar(String imagenLugar){
        this.imagenLugar = imagenLugar;
    }
    public String getImagenLugar(){
        return this.imagenLugar;
    }
    public void setLatitud(String latitud){
        this.latitud = latitud;
    }
    public String getLatitud(){
        return this.latitud;
    }
    public void setLongitud(String longitud){
        this.longitud = longitud;
    }
    public String getLongitud(){
        return this.longitud;
    }
    public void setNombreLugar(String nombreLugar){
        this.nombreLugar = nombreLugar;
    }
    public String getNombreLugar(){
        return this.nombreLugar;
    }


    /**
     * Instantiate the instance using the passed jsonObject to set the properties values
     */
    public Puntos(JSONObject jsonObject){
        if(jsonObject == null){
            return;
        }
        descripcionLugar = jsonObject.optString("descripcion_lugar");
        id = jsonObject.optString("id");
        imagenLugar = jsonObject.optString("imagen_lugar");
        latitud = jsonObject.optString("latitud");
        longitud = jsonObject.optString("longitud");
        nombreLugar = jsonObject.optString("nombre_lugar");
    }

    /**
     * Returns all the available property values in the form of JSONObject instance where the key is the approperiate json key and the value is the value of the corresponding field
     */
    public JSONObject toJsonObject()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("descripcion_lugar", descripcionLugar);
            jsonObject.put("id", id);
            jsonObject.put("imagen_lugar", imagenLugar);
            jsonObject.put("latitud", latitud);
            jsonObject.put("longitud", longitud);
            jsonObject.put("nombre_lugar", nombreLugar);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

}