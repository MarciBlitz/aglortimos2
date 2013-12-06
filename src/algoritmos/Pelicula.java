/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pc
 */
public class Pelicula {
    long id;
    String nombre;
    int anno;
    //ArrayList<Valoracion> valoraciones;
    HashMap<Long,Valoracion> valoraciones;
    double notamedia;
    
    Pelicula(){
        valoraciones=new HashMap<>();
    };
    Pelicula(int aid, String anombre, int aanno){
        valoraciones=new HashMap<>();
        id=aid;
        nombre=anombre;
        anno=aanno;
        notamedia = 0;
    };
    void addvaloracion(Valoracion e){valoraciones.put(e.iduser,e);};
    Map<Long,Valoracion> getValoraciones(){return valoraciones;}
    long getIdPelicula(){return id;}
    double getMedia(){return notamedia;}
    String getTitulo(){return nombre;}
    void setMedia(double media){notamedia=media;}
}
