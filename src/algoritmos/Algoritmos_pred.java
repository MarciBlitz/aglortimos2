/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author Pc
 */
public class Algoritmos_pred {

    public Parametros testIAmasA(int n, HashMap<Long, TreeSet<ItemSim>> modeloSimilitud, List<Usuario> test, ArrayList<Pelicula> peliculas) {
        // Variables auxiliares:
        Iterator<Usuario> it1 = test.iterator();
        Usuario u;
        double mediaP;
        long idP;
        double valoracionEstimada;
        int valoracionReal;
        double MAE = 0;
        int numEstimacionesRealizadas = 0;
        double dif;
        TreeSet<ItemSim> vecinos;
        int numEstimacionesImposibles = 0;
        int numEstimacionesPosibles = 0;

        
        int cont = 0;
        // 1. Recorremos cada usuario de la partición test.
        while (it1.hasNext()){
            u = it1.next();
            //System.out.println(" Usuario "+cont+" de "+test.size());
            ++cont;
            
            // 2. Recorremos cada valoración del usuario en cuestión.
            for (Map.Entry<Long,Valoracion> e : u.getValoraciones().entrySet()) {


                idP = e.getValue().idpeli;

                // 3. Calculamos la valoracion real y la estimada.
                valoracionReal = e.getValue().getValor();
                mediaP = peliculas.get((int)idP).getMedia();
                vecinos = modeloSimilitud.get(idP);
                valoracionEstimada = calcularPrediccionIAmasA(n, u, mediaP,vecinos);

                // 4. Comprobamos si hemos podido hacer la predicción
                if (valoracionEstimada != -1){
                   // 5. Acumulamos el MAE
                   dif = valoracionEstimada - valoracionReal*1.0;
                   if (dif > 0){
                      MAE = MAE + dif;
                   }else{
                      MAE = MAE + dif*(-1);
                   }
                }else{
                    ++numEstimacionesImposibles;
                }
            }
            
            numEstimacionesPosibles += u.getValoraciones().size();
        }
        
        numEstimacionesRealizadas = numEstimacionesPosibles - numEstimacionesImposibles;
        if (numEstimacionesRealizadas != 0){
            MAE = MAE/(numEstimacionesRealizadas*1.0);
        }else{
            MAE = 0;
        }
        
        return new Parametros(MAE,(numEstimacionesRealizadas*1.0)/numEstimacionesPosibles);
        
    }
    
    
    
    public Parametros testWA(HashMap<Long, TreeSet<ItemSim>> modeloSimilitud, List<Usuario> test, ArrayList<Pelicula> peliculas) {
        // Variables auxiliares:
        Iterator<Usuario> it1 = test.iterator();
        Usuario u;
        long idP;
        double valoracionEstimada;
        int valoracionReal;
        double MAE = 0;
        int numEstimacionesRealizadas = 0;
        double dif;
        TreeSet<ItemSim> vecinos;
        int numEstimacionesImposibles = 0;
        int numEstimacionesPosibles = 0;
        
        // Nota: cargamos todas las medias de las peliculas a memoria para acelerar la ejecución
        //HashMap<Long,Double> medias = getMediasPeliculasBD_HashMap(instancia);
        
        // 1. Recorremos cada usuario de la partición test.
        int cont = 0;
        while (it1.hasNext()){
            u = it1.next();
            //System.out.println(" Usuario "+cont+" de "+test.size());
            ++cont;
            // 2. Recorremos cada valoración del usuario en cuestión.
             for (Map.Entry<Long,Valoracion> e : u.getValoraciones().entrySet()) {
                 idP = e.getValue().idpeli;
                 
                 // 3. Calculamos la valoracion real y la estimada.
                 valoracionReal = e.getValue().getValor();
                 vecinos = modeloSimilitud.get(idP);
                 valoracionEstimada = calcularPrediccionWA(u,vecinos, peliculas);
                 
                 // 4. Comprobamos si hemos podido hacer la predicción
                 if (valoracionEstimada != -1){
                    // 5. Acumulamos el MAE
                    dif = valoracionEstimada - valoracionReal*1.0;
                    if (dif > 0){
                       MAE = MAE + dif;
                    }else{
                       MAE = MAE + dif*(-1);
                    }
                 }else{
                     ++numEstimacionesImposibles;
                 }
             }
             numEstimacionesPosibles += u.getValoraciones().size();
        }
        
        numEstimacionesRealizadas = numEstimacionesPosibles - numEstimacionesImposibles;
        if (numEstimacionesRealizadas != 0){
            MAE = MAE/(numEstimacionesRealizadas*1.0);
        }else{
            MAE = 0;
        }
        
        return new Parametros(MAE,(numEstimacionesRealizadas*1.0)/numEstimacionesPosibles);
        
    }
    
    
    /**
     * Método para predecir la valoracion de un usuario sobre una película, teniendo en cuenta solo los vecinos más cercanos, utilizando el algoritmo de predicción IA+A.
     * @param u Usuario
     * @param idP identificador de la película a predecir.
     * @param vecinos Conjunto de vecinos más cercanos a la película a precedir.
     * @return Devuelve la valoracion estimada. Devuelve -1 si no se ha podido predecir
     * @throws No se lanzan excepciones.
    */
    protected double calcularPrediccionIAmasA(int n, Usuario u, double mediaP, TreeSet<ItemSim> vecinos) {
        // Estructura con solamente las valoraciones que un usuario ha realizado sobre los k vecinos mas cercanos a idP
        ArrayList<Valoracion> valoracionesCercanas = new ArrayList();
        
        // PASO 1: Quedarnos con las valoraciones a las películas más cercanas.
        // 1.1. Se recorren los vecinos mas cercanos a idP
        //mostrarVecinos(vecinos);
        for(ItemSim i : vecinos){
            // 1.2. Se comprueba si el usuario a valorado a dicho vecino
            if (u.getValoraciones().containsKey(i.getId())){
                // 1.3. Si es así se almacena en la estructura valoracionesCercanas.
                valoracionesCercanas.add(u.getValoraciones().get(i.getId()));
            }
        }
        
        if (!valoracionesCercanas.isEmpty()){
            // PASO 2: Conseguir las medias.
            // 2.1. Media de la pelicula idP
            

            // 2.2. Media del usuario en cuentión.
            double mediaU = u.getMedia();


            // PASO 3: Cálculo de la predicción.
            double numerador = 0;
            double denominador = 0;
            long idPAux;
            ItemSim itemSim;
            Valoracion v;
            
            
            // ENFOQUE DADOS-N. Seleccionamos n valoraciones cercanas.
            if ( n > 0 && n < valoracionesCercanas.size()){
                int rand;
                int cont = 0;
                ArrayList<Valoracion> array = new ArrayList();
                
                while (cont < n){
                    rand = (int) (Math.random() * valoracionesCercanas.size());
                    v = valoracionesCercanas.get(rand);
                    if (!array.contains(v)){
                        array.add(v);
                        ++cont;
                    }
                }
                
                valoracionesCercanas = array;
            }
            // FIN ENFOQUE DADOS-N
            
            
            Iterator<Valoracion> it1 = valoracionesCercanas.iterator();
            
            while(it1.hasNext()){
                v = it1.next();
                idPAux = v.idpeli;

                itemSim = buscarVecino(idPAux, vecinos);

                numerador = numerador + itemSim.getSim()*(v.getValor()-mediaU) ;
                //denominador = denominador + itemSim.getSim();
                denominador = denominador + itemSim.getSim() ;

            }

            if (denominador != 0){
                double ajuste = numerador/denominador;
                
                return mediaP + ajuste;
            }else{
                return 0;
            }
        }else{
            return -1;
        }
        
    }
    
    
    /**
     * Método para buscar la similitud de una película
     * @param idP identificador de la película a predecir.
     * @param vecinos Conjunto de vecinos más cercanos a la película a precedir.
     * @return Devuelve la pareja película-similitud. Si existe, devuelve una pareja con sus campos a cero.
     * @throws No se lanzan excepciones.
    */
    private ItemSim buscarVecino(long idP, TreeSet<ItemSim> vecinos) {
        Iterator<ItemSim> it = vecinos.iterator();
        ItemSim i;
        
        while (it.hasNext()){
            i = it.next();
            
            if (i.getId() == idP){
                return i;
            }
        }
        
        return null;
    }
    
    
    private double calcularPrediccionWA(Usuario u, TreeSet<ItemSim> vecinos, ArrayList<Pelicula> peliculas) {
        // Estructura con solamente las valoraciones que un usuario ha realizado sobre los k vecinos mas cercanos a idP
        ArrayList<Valoracion> valoracionesCercanas = new ArrayList();
        
        // PASO 1: Quedarnos con las valoraciones a las películas más cercanas.
        // 1.1. Se recorren los vecinos mas cercanos a idP
        //mostrarVecinos(vecinos);
        for(ItemSim i : vecinos){
            // 1.2. Se comprueba si el usuario a valorado a dicho vecino
            if (u.getValoraciones().containsKey(i.getId())){
                // 1.3. Si es así se almacena en la estructura valoracionesCercanas.
                valoracionesCercanas.add(u.getValoraciones().get(i.getId()));
            }
        }
        
        if (!valoracionesCercanas.isEmpty()){
            // PASO 2: Conseguir las medias.

            // 2.1. Media del usuario en cuentión.
            double mediaU = u.getMedia();
            // NOTA: Necesitamos la media de cada pelicula vecina. Se irá pidiendo con forme haga falta.

            // PASO 3: Cálculo de la predicción.
            double mediaK;
            double numerador = 0;
            double denominador = 0;
            long idPAux;
            ItemSim itemSim;
            Iterator<Valoracion> it1 = valoracionesCercanas.iterator();
            Valoracion v;

            while(it1.hasNext()){
                v = it1.next();
                idPAux = v.idpeli;
                mediaK = peliculas.get((int)idPAux).getMedia();
                itemSim = buscarVecino(idPAux, vecinos);

                numerador = numerador + itemSim.getSim()*(v.getValor()-mediaK) ;
                denominador = denominador + itemSim.getSim();

            }

            if (denominador != 0){
                return mediaU + numerador/denominador;
            }else{
                return 0;
            }
        }else{
            return -1;
        }
        
    }
    
    public HashMap<Long,Double> getMediaspeliculas_HashMap(ArrayList<Pelicula> peliculas){ 
        HashMap<Long,Double> medias = new HashMap();
        
        //List<Object> lista =  this.getMediaspeliculasBD_List(gestorPersistencia);
        
        for(int i=0;i<peliculas.size();i++){
                medias.put((long)peliculas.get(i).getIdPelicula(), (double)peliculas.get(i).getMedia());
        }
        
        return medias;
    }
}



