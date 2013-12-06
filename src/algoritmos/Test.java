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
    
    /**
     * Ruta donde se guardarán los resultados
     */
    String urlFichResultados;
    
    /**
     * Constructor de la clase Test
     */
    public Test(){
        urlFichResultados = "resultados.txt";
    }

    /**
     * Función que ejecuta los ciclos de 5fcv para realizar los entrenamientos
     * @param k Número de vecinos cercanos, 10, 20, 30
     * @param algSim Tipo de algoritmo de similitud a utilizar, 0 Coseno, 1 Pearson
     * @param usuarios Lista de usuarios para realizar los entrenamientos
     * @param peliculas Lista de películas para realizar los entrenamientos
     * @throws ErrorDatoInvalido
     * @throws IOException
     */
    public void training(int k, int algSim, ArrayList<Usuario>usuarios, ArrayList<Pelicula>peliculas) throws IOException, ErrorDatoInvalido {
        for (int i = 0; i < 5; ++i){
            ejecucionTraining(k,algSim,i, usuarios, peliculas);
        }
    }
    
    
    /**
     * Método de lanzamiento de una ejecución para el estudio de casos de uso, con el coseno como medida de similitud e Item Average + Adjustment como algoritmo de predicción. 
     * @param k Número de vecinos mas cercanos. Valores: 10, 20 o 30.
     * @param pAlgSim Tipo de algoritmo de similitud a utilizar, 0 Coseno 1 Pearson
     * @param ciclo_5fcv Etapa del 5-fold cross validation. Valores: 0, 1, 2, 3 o 4.
     * @param usuarios Lista de usuarios para realizar el entrenamiento
     * @param peliculas Lista de películas para realizar el entrenamiento
     * @throws java.io.IOException
     * @throws ErrorDatoInvalido Esta excepción se lanza si alguno de los parametros es incorrecto.
    */
    public void ejecucionTraining(int k, int pAlgSim, int ciclo_5fcv, ArrayList<Usuario> usuarios, ArrayList<Pelicula> peliculas) throws IOException, ErrorDatoInvalido{
        Algoritmos_similitud as = new Algoritmos_similitud();
        if (k!=10 && k!=20 && k!=30){
            throw new ErrorDatoInvalido(" Método ejecucion - Primer parámetro: El número de vecinos más cercanos (k) debe ser 10, 20 o 30.");
        }
        if (ciclo_5fcv < 0 || ciclo_5fcv > 4){
            throw new ErrorDatoInvalido(" Método ejecucion - Segundo parámetro: El indicador de la etapa del 5-fold cross validation que se está ejecutando debe ser un entero entre 0 y 4.");
        }
        
        // Preguntar a la BD el numero de usuarios y guardarlos en esta variable.
        long numUsuarios = usuarios.size();
        // Preguntar a la BD el número de películas y guardarlas en esta variable.
            //int numPeliculas = getNumPeliculasBD();
        //long numPeliculas = this.getNumPeliculasBD(instancia);
        // Tamaño de la partición para hacer el test (5-fold cross validation). 
        long tamTest = numUsuarios / 5;
        // Posición (usuario) donde comienza la partición del test
        long posIniTest = ciclo_5fcv * tamTest;
        // Posición (usuario) donde comienza la partición del test
        long posFinTest = posIniTest + tamTest - 1;
        // Estructura que representa el modelo de similitud (clave: id de pelicula; valor: lista de idPelicula-Similitud).
        HashMap<Long, TreeSet<ItemSim>> modeloSimilitud;
        // Estructura que almacena los usuarios a testear.
        List<Usuario> test;
        test = usuarios.subList((int)posIniTest,(int)posFinTest);
        //System.out.println(" tamtest = "+test.size()+" de "+numUsuarios+". Bloque "+tamTest);
        //List<Usuario> test =  usuarios.subList((int)posIniTest, (int)posFinTest);
        ArrayList<Long> testSoloIdU = new ArrayList<>(getIdUsuarios(test));
        
        // Variables auxiliares:
        long tiempoTraining;        
        
        // PASO 1: Calcular la similitud de cada item con el resto, saltándonos las valoraciones de los usuarios que forman parte del test.
        tiempoTraining = System.currentTimeMillis();
            if(pAlgSim == 0){
                modeloSimilitud = as.getModeloSimilitud_byCoseno(k, testSoloIdU, peliculas);
            }else{
                modeloSimilitud = as.getModeloSimilitud_byPearson(k, testSoloIdU, peliculas);
            }
        tiempoTraining = System.currentTimeMillis() - tiempoTraining;
        
        // SERIALIZAR DESERIALIZAR UN MODELO SIMILIUTD
        String url = k+"-"+pAlgSim+"-"+ciclo_5fcv;
        SerializarModeloSimilitud selializar = new SerializarModeloSimilitud(modeloSimilitud);
        selializar.serializar("modelosSimilitud/"+url);
    
        String url2 = "tt"+url;
        guardarTiempoTrainingFichero((long) tiempoTraining/1000, "modelosSimilitud/"+url2+".txt");
        
        
    }
    
    /**
     * Función que ejecuta los test de prueba
     * @param k Número de vecinos cercanos que queremos evaluar, 10, 20, 30
     * @param pAlgSim Tipo de algoritmo de similitud, 0 Coseno, 1 Pearson
     * @param pAlgPred Tipo de algoritmo de predicción a utilizar, 0 I+A, 1 WA
     * @param n 
     * @param usuarios Conjunto de usuarios a evaluar
     * @param peliculas Conjunto de películas a evaluar
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
            HashMap<Long, TreeSet<ItemSim>> modeloS = deserializar.deserializar("modelosSimilitud/"+url).getModeloSimilitud();
            
            

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
            long tiempoTraining = leerTiempoTrainingFichero( "modelosSimilitud/"+url2+".txt");
            
            MAE += param.getMAE();
            cobertura += param.getCobertura();
            //System.out.println(param.getCobertura());
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
    
    /**
     * Función que guarda los resultados en un archivo de texto
     * @param algoritmos Combinación de algoritmos utilizados
     * @param k Número de vecinos cercanos utilizados
     * @param n
     * @param MAE Parámetro MAE obtenido
     * @param cobertura Cobertura obtenida
     * @param tiempoTraining Tiempo consumido en la etapa de training en segundos
     * @param tiempoTest Tiempo consumido en la etapa de test en segundos
     * @throws IOException 
     */
    private void guardarResultados(String algoritmos, int k, int n, double MAE, double cobertura, long tiempoTraining, long tiempoTest) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(urlFichResultados,true))) {
                bw.write(algoritmos+","+n+","+k+","+MAE+","+cobertura*100+","+tiempoTraining+","+tiempoTest+"\r\n");
        }catch(Exception e){
             System.out.println("Error al guardar los resultados. "+e.getMessage());
        }
    }
    
    private void guardarTiempoTrainingFichero(long l, String url) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(url))) {
            bw.write( Long.toString(l));
        }catch(Exception e){
            System.out.println("Error al guardar el tiempo en el fichero (training). "+e.getMessage());
        }
    }
    
    private long leerTiempoTrainingFichero(String url) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(url));
        long toRet = Long.parseLong(br.readLine());
        return toRet;
        
    }

    private ArrayList<Long> getIdUsuarios(List<Usuario> test){
        ArrayList<Long> ids = new ArrayList<>();
        for(int i = 0;i < test.size();i++){
            ids.add(test.get(i).getIdUsuario());
        }
        return ids;
    }
}
