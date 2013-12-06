/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;

/**
 *
 * @author Pc
 */
public class Valoracion {
    int estrellas;
    long iduser;
    long idpeli;
    
    Valoracion(int est, int usr, int peli){estrellas=est;iduser=usr;idpeli=peli;}
    Valoracion(){
        estrellas=0;
        iduser = 0;
        idpeli = 0;
    }
    int getValor(){return estrellas;}
    long getIdPelicula(){return idpeli;}
}
