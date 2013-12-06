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
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pc
 */
public class Cargadatos{
    /*ArrayList<Pelicula> pelis;
    ArrayList<Usuario> usuarios;
    HashMap mapausuarios;
    String archivopelis;
    String archivovaloraciones;
    Test test;
    */
    /**
     * 
     */
    public Cargadatos(){
       
    }
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
                System.out.println("cargando valoraciones...");
		while ((line2 = br2.readLine()) != null) {
                        String[] cadenavaloracion = line2.split(",");
                        int indice = Integer.parseInt(cadenavaloracion[1]);
                        int indiceusu = Integer.parseInt(cadenavaloracion[0]);
                        tempv = new Valoracion(Integer.parseInt(cadenavaloracion[2]),indiceusu,indice);
                        pelis.get(indice-1).addvaloracion(tempv);
                        
                        if(mapausu.containsKey(indiceusu)){
                            usuarios.get((int) mapausu.get(indiceusu)).getValoraciones().put(tempv.getIdPelicula(), tempv);
                        }else{
                            mapausu.put(indiceusu, j);
                            usuarios.add(new Usuario(indiceusu));
                            usuarios.get((int) mapausu.get(indiceusu)).getValoraciones().put(tempv.getIdPelicula(), tempv);
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
    public static void calcularMedias(ArrayList<Usuario> usuarios, ArrayList<Pelicula> peliculas) {
        // 1. Calculo de la media de valoraciones para cada usuario
        double numerador;
        Iterator<Usuario> it1 = usuarios.iterator();
        
        Usuario u;
        long i;
        while(it1.hasNext()){
            numerador = 0;
            u = it1.next();
            for(Map.Entry<Long,Valoracion> v : u.getValoraciones().entrySet()){
                numerador += v.getValue().getValor();
            }
            u.setMedia(numerador/u.getValoraciones().size());
            
        }
        
        
        // 2. Calculo de la media de valoraciones para cada pelicula
        Iterator<Pelicula> it2 = peliculas.iterator();
        Pelicula p;
        while(it2.hasNext()){
            numerador = 0;
            i = 0;
            p = it2.next();
                for(Map.Entry<Long,Valoracion> v : p.getValoraciones().entrySet()){
                    numerador += v.getValue().getValor();
                    //System.out.println(v.getValue().getValor());
                }
                if(p.getValoraciones().size() == 0){
                    p.setMedia(0);
                }else{
                    p.setMedia(numerador/p.getValoraciones().size());      
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
        final ArrayList<Pelicula> pelis=new ArrayList<>(); //lista peliculas
        final ArrayList<Usuario> usuarios=new ArrayList<>(); //lista usuarios
        HashMap mapausuarios = new HashMap(); //para buscar la posicion de un usuario en el vector a partir de su id
        String archivopelis = "src/algoritmos/peliculas2.csv";
	String archivovaloraciones = "src/algoritmos/ratings7.csv";
        final Test test = new Test();
	
        cargarpeliculas(archivopelis, pelis);
        cargarvaloraciones(archivovaloraciones, pelis, usuarios, mapausuarios);
        calcularMedias(usuarios,pelis);
       
        ///*******************TRAININGS
        /*
        ExecutorService exec = Executors.newFixedThreadPool(5);
        ExecutorService exec2 = Executors.newFixedThreadPool(5);
        for (int k = 10; k <= 30; k+=10) {
            //...execute the task to run concurrently as a runnable:
            final int i = k;
            
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Running in: " + Thread.currentThread());
                    try {
                        // do the work to be done in its own thread
                        test.training(i, 0, usuarios, pelis);
                    } catch (            IOException | ErrorDatoInvalido ex) {
                        Logger.getLogger(Cargadatos.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }
            });
            exec2.execute(new Runnable(){
                @Override
                public void run(){
                    try {
                        test.training(i, 1, usuarios, pelis);
                    } catch (            IOException | ErrorDatoInvalido ex) {
                        Logger.getLogger(Cargadatos.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            
        }
        //Tell the executor that after these 100 steps above, we will be done: 
        exec.shutdown();
        exec2.shutdown();
        try {
            // The tasks are now running concurrently. We wait until all work is done, 
            // with a timeout of 50 seconds:
            boolean b = exec.awaitTermination(60, TimeUnit.MINUTES);
            boolean C = exec2.awaitTermination(60, TimeUnit.MINUTES);
            // If the execution timed out, false is returned:
            System.out.println("All done: " + b);
        } catch (InterruptedException e) {}
        */
        ///**********************TESTS
        
        for(int k=10;k<=30;k+=10){
            System.out.println("K = "+k);
            //Coseno + I+A con n = 0
            test.ejecucionTest(k, 0, 0, 0, usuarios, pelis);
            //Coseno + I+A con n = 2
            
            test.ejecucionTest(k, 0, 0, 2, usuarios, pelis);
            //Coseno + I+A con n = 4
            test.ejecucionTest(k, 0, 0, 4, usuarios, pelis);
            //Coseno + I+A con n = 8
            test.ejecucionTest(k, 0, 0, 8, usuarios, pelis);
            //Coseno + WA
            test.ejecucionTest(k, 0, 1, 0, usuarios, pelis);
            //Pearson + I+A con n = 0
            test.ejecucionTest(k, 1, 0, 0, usuarios, pelis);
            //Pearson + I+A con n = 2
            test.ejecucionTest(k, 1, 0, 2, usuarios, pelis);
            //Pearson + I+A con n = 4
            test.ejecucionTest(k, 1, 0, 4, usuarios, pelis);
            //Pearson + I+A con n = 8
            test.ejecucionTest(k, 1, 0, 8, usuarios, pelis);
            //Coseno + WA
            test.ejecucionTest(k, 1, 1, 0, usuarios, pelis);
            
        }
       
        
	System.out.println("Done");
        //______________________

    }
    
}
