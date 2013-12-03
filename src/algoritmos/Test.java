/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;

import Excepciones.ErrorDatoInvalido;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author Pc
 */
public class Test {
        private final String urlFichResultados;
        
    public Test(String urlFichResults){
        urlFichResultados=urlFichResults;
    }

    /**
     *
     * @param k
     * @param pAlgSim
     * @param pAlgPred
     * @param n
     * @param usuarios
     * @param peliculas
     * @throws ErrorDatoInvalido
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void ejecucionTest(int k, int pAlgSim, int pAlgPred, int n, ArrayList<Usuario> usuarios, ArrayList<Pelicula> peliculas) throws ErrorDatoInvalido, IOException, ClassNotFoundException{
        // Variables auxiliares:
        double MAE = 0;
        double cobertura = 0;
        long tTraining = 0;
        long tTest = 0;
        Algoritmos_pred ap = new Algoritmos_pred();
        
        for (int ciclo_5fcv = 0; ciclo_5fcv < 5; ++ciclo_5fcv){
            
            // Preguntar a la BD el numero de usuarios y guardarlos en esta variable.
            long numUsuarios = usuarios.size();
            // Tamaño de la partición para hacer el test (5-fold cross validation). 
            long tamTest = numUsuarios / 5;
            // Posición (usuario) donde comienza la partición del test
            long posIniTest = ciclo_5fcv * tamTest;
            // Posición (usuario) donde comienza la partición del test
            long posFinTest = posIniTest + tamTest - 1;
            // Estructura que almacena los usuarios a testear.
            List<Usuario> test = usuarios.subList((int)posIniTest,(int)posFinTest);
            // Variables auxiliares:
            long tiempoTest = 0;
            Parametros param = new Parametros();

            // DESERIALIZAR UN MODELO SIMILIUTD
            String url = k+"-"+pAlgSim+"-"+ciclo_5fcv;
            SerializarModeloSimilitud deserializar = new SerializarModeloSimilitud();
            HashMap<Long, TreeSet<ItemSim>> modeloS = deserializar.deserializar("/"+url+".bin").getModeloSimilitud();
            
            

            // PASO 2: Predicción de la partición test
            //si pAlgPred es 0 realiza el test de ImasA
            //si no realiza los testWA
            tiempoTest =  System.currentTimeMillis();
                if(pAlgPred == 0){
                    param = ap.testIAmasA(n, modeloS, test, peliculas);
                }else{
                    param = ap.testWA(modeloS, test, peliculas);
                }
            tiempoTest =  System.currentTimeMillis() - tiempoTest;
            
            String url2 = "tt"+url;
            long tiempoTraining = leerTiempoTrainingFichero( "/"+url2+".txt");
            
            MAE += param.getMAE();
            cobertura += param.getCobertura();
            System.out.println(param.getCobertura());
            tTraining += tiempoTraining;
            tTest += tiempoTest;
                    
        }
        
        String cad1, cad2;
        if (pAlgSim == 0){
           cad1 = "Coseno";
        }else{
           cad1 = "Pearson";
        }
        if (pAlgPred == 0){
           cad2 = "IA+A";
        }else{
           cad2 = "WA";
        }
        
        guardarResultados(cad1+"-"+cad2, k, n, MAE/5, cobertura/5, tTraining/5, tTest/5 );
    }
       
    private void guardarResultados(String algoritmos, int k, int n, double MAE, double cobertura, long tiempoTraining, long tiempoTest) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(urlFichResultados,true));
        
        bw.write( algoritmos+"         "+k+"   "+n+"     "+MAE+"       "+cobertura*100+"%         "+tiempoTraining+"          "+tiempoTest+" \r\n");
        
        bw.close();
    }
    
    private long leerTiempoTrainingFichero(String url) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(url));
        long toRet = Long.parseLong(br.readLine());
        return toRet;
        
    }
    
}
