/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;

import Excepciones.ErrorDatoInvalido;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pc
 */
public class Cargadatos{

    /**
     * @param args the command line arguments
     */
    public static void cargarpeliculas(String archivo, ArrayList<Pelicula> pelis)
    {
        BufferedReader br = null;
        String line = "";
        int i = 0;
        Pelicula temp=null;
        try {
		br = new BufferedReader(new FileReader(archivo));
                line = br.readLine();
		while ((line = br.readLine()) != null) {
			String[] peli = line.split("\t");
                            temp = new Pelicula(Integer.parseInt(peli[0]),peli[2],0);
                            //System.out.println("id |" + Integer.parseInt(peli[0]) + "| Nombre: |" + peli[2] + "| anno: |0|");
                            pelis.add(temp);
                            peli[0]="0";peli[1]="0";peli[2]="";
		}
                System.out.println("peliculas cargadas");
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
        }catch(NumberFormatException e) {
            System.out.println("la cagaste");
            e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    }
    
    public static void cargarvaloraciones(String archivo, ArrayList<Pelicula> pelis, ArrayList<Usuario> usuarios, HashMap mapausu)
    {
        BufferedReader br2 = null;
        String line2 = "";
        int i = 0;
        int j=0;
        
	try {                
                br2 = new BufferedReader(new FileReader(archivo));
                line2 = br2.readLine(); //para quitar la linea de cabecera
                Valoracion tempv;
                System.out.println("cargando valoracione...");
		while ((line2 = br2.readLine()) != null) {
                        String[] cadenavaloracion = line2.split(",");
                        int indice = Integer.parseInt(cadenavaloracion[1]);
                        int indiceusu = Integer.parseInt(cadenavaloracion[0]);
                        tempv = new Valoracion(Integer.parseInt(cadenavaloracion[2]),indiceusu,indice);
                        pelis.get(indice-1).addvaloracion(tempv);
                        
                        if(mapausu.containsKey(indiceusu)){
                            usuarios.get((int) mapausu.get(indiceusu)).valoraciones.put(tempv.idpeli, tempv);
                        }else{
                            mapausu.put(indiceusu, j);
                            usuarios.add(new Usuario(indiceusu));
                            j++;
                        }
                        //System.out.println("usuario" + tempv.iduser+"|"+usuarios.get(indiceusu-1).id);
                        i++;
                        //System.out.println(((i/3085))+ "% completo");
                }
                System.out.println(j+ " usuarios cargados");
                System.out.println("valoraciones cargadas");
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
        }catch(NumberFormatException e) {
            System.out.println("la cagaste");
            e.printStackTrace();
	} finally {
		if (br2 != null) {
			try {br2.close();} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    }
    
    public static HashMap<Long, Double> cargarPelisHashMap(ArrayList<Pelicula> pelis){
        HashMap<Long,Double>pelishm = new HashMap();
        for(int i=0;i<pelis.size();i++){
            pelishm.put(pelis.get(i).getIdPelicula(), (double) pelis.get(i).getMedia());            
        }
        return pelishm;
    }
    
    public static void main(String[] args) throws ErrorDatoInvalido, IOException, ClassNotFoundException {
        // TODO code application logic here
        ArrayList<Pelicula> pelis=new ArrayList<>(); //lista peliculas
        ArrayList<Usuario> usuarios=new ArrayList<>(); //lista usuarios
        HashMap mapausuarios = new HashMap(); //para buscar la posicion de un usuario en el vector a partir de su id
        String archivopelis = "/home/jose/NetBeansProjects/aglortimos2/algoritmos2/src/algoritmos/peliculas2.csv";
	String archivovaloraciones = "/home/jose/NetBeansProjects/aglortimos2/algoritmos2/src/algoritmos/ratings7.csv";
	
        cargarpeliculas(archivopelis, pelis);
        cargarvaloraciones(archivovaloraciones, pelis, usuarios, mapausuarios);
        
       Algoritmos_similitud as = new Algoritmos_similitud();
        
       as.ejecucionTrainingCoseno(3, pelis);
        /*for (int i = 0; i < 5; ++i){
            try {
                //ejecucionTraining(k,algSim,i);
                System.out.println("ESTADO:  k = 3, ciclo = "+i);
                as.ejecucionTrainingCoseno(3, pelis);
            } catch (    ErrorDatoInvalido | IOException ex) {
                
            }
        }*/
        
        
        
        //Algoritmos_pred ap = new Algoritmos_pred();
   
        //System.out.println(pelis.get(1000).valoraciones.get(0).estrellas);
 
        //Test t = new Test("/results.txt");
       
        //int n=10;
        //t.ejecucionTest(3, 0, 1, n, usuarios, pelis);
        
	System.out.println("Done");
        //______________________
        
        
        
        
        
        
        
        
        
        
        
    }
    
}
