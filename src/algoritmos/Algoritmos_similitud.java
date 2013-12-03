/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;


import Excepciones.ErrorDatoInvalido;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
public class Algoritmos_similitud 
{

    public void mostrarMediasPeliculas(ArrayList<Pelicula> peliculas){
        System.out.println("Nota media de peliculas");
        Iterator<Pelicula> it = peliculas.iterator();
        Pelicula u;
        while(it.hasNext()){
            u = it.next();
            System.out.println("Id:"+u.getIdPelicula()+" Media:"+u.getMedia());
        }
    }
    
        public void mostrarMediasUsuarios(ArrayList<Usuario> usuarios){
        System.out.println(" Nota media del usuario");
        Iterator<Usuario> it = usuarios.iterator();
        Usuario u;
        while(it.hasNext()){
            u = it.next();
            System.out.println("Id:"+u.getIdUsuario()+" Media:"+u.getMedia());
        }
    }
        
    public void mostrarUsuario(Usuario u){
        System.out.println("Usuario: "+u.getIdUsuario());  
    }
    public void mostrarPelicula(Pelicula p){
        System.out.println("Pelicula: "+p.getIdPelicula());
        System.out.println("Titulo: "+p.getTitulo());
    }
    
    public Usuario buscarUsuario(long idUsuario, ArrayList<Usuario> lista) {
        Iterator<Usuario> it1 = lista.iterator();
        Usuario aux;
        
        while(it1.hasNext()){
            aux = it1.next();
            if(aux.getIdUsuario() == idUsuario){
                return aux;
            }
        }
        return null;
    }
    
        public Pelicula buscarPelicula(long idPelicula, ArrayList<Pelicula> lista) {
        Iterator<Pelicula> it1 = lista.iterator();
        Pelicula aux;
        
        while(it1.hasNext()){
            aux = it1.next();
            if(aux.getIdPelicula() == idPelicula){
                return aux;
            }
        }
        return null;
    }
        
    public void calcularMedias(ArrayList<Usuario> usuarios, ArrayList<Pelicula> peliculas) {
        // 1. Calculo de la media de valoraciones para cada usuario
        double numerador;
        Iterator<Usuario> it1 = usuarios.iterator();
        Usuario u;
        int i;
        while(it1.hasNext()){
            numerador = 0;
            u = it1.next();
            for(i=0; i<u.getValoraciones().size();i++){
                numerador += u.getValoraciones().get(i).estrellas;
            }
            u.setMedia((float) (numerador/u.getValoraciones().size()));
        }
        
        // 2. Calculo de la media de valoraciones para cada pelicula
        Iterator<Pelicula> it2 = peliculas.iterator();
        Pelicula p;
        while(it2.hasNext()){
            numerador = 0;
            i = 0;
            p = it2.next();
            for(i=0;i<p.getValoraciones().size();i++){
                numerador += p.getValoraciones().get(i).estrellas;
            }
             p.setMedia((float) (numerador/p.getValoraciones().size()));
        }
        
    }
    
    public double similitudCoseno(Pelicula i1, Pelicula i2){
        // Variables auxiliares:
        double norma1 = 0;
        double norma2 = 0;
        int val1;
        int val2;
        Long key;
        double numerador = 0;
        int comun = 0;
        // Constante de la MEJORA del Factor de relevancia
        int N = 20;
        
        // 1. Nos quedamos con la películas que tenga menos valoraciones.
        if (i1.getValoraciones().size() < i2.getValoraciones().size()){
            for (Map.Entry<Long,Valoracion> e : i1.getValoraciones().entrySet()) {
                key = e.getKey();
                
                // 3. Comprobamos que la otra película haya sido valorada por el mismo usuario.
                if (i2.getValoraciones().containsKey(key)){
                    // 4. Realizamos los cálculos de similitud.
                    val1 = e.getValue().getValor();
                    val2 = i2.getValoraciones().get(key).getValor();

                    norma1 = norma1 + val1 * val1;
                    norma2 = norma2 + val2 * val2;

                    numerador = numerador + val1 * val2;
                    ++comun;
                }
                
            }
        }else{
            for (Map.Entry<Long,Valoracion> e : i2.getValoraciones().entrySet()) {
                key = e.getKey();
                if (i1.getValoraciones().containsKey(key)){
                    val2 = e.getValue().getValor();
                    val1 = i1.getValoraciones().get(key).getValor();

                    norma1 = norma1 + val1 * val1;
                    norma2 = norma2 + val2 * val2;

                    numerador = numerador + val1 * val2;
                    ++comun;
                }
            }
        }
        
        if (norma1 != 0 && norma2 !=0){
            double sim = numerador / (Math.sqrt(norma1) * Math.sqrt(norma2));
            // Aplicamos la MEJORA del Factor de relevancia.
            if (comun < N){
                sim = sim * ((comun*1.0)/N);
            }
            if (sim > 1){
                return 1;
            }
            return sim;
        }else{
            return 0;
        }
    }
    
    public HashMap<Long, TreeSet<ItemSim>> getModeloSimilitud_byCoseno(int k, ArrayList<Pelicula> peliculas) {
        // Estructura que representa el modelo de similitud (clave: id de pelicula; valor: lista de idPelicula-Similitud).
        HashMap<Long, TreeSet<ItemSim>> modelo_similitud = new HashMap();
        // Variables auxiliares:
        TreeSet<ItemSim> fila1;
        TreeSet<ItemSim> fila2;
        long id1;
        long id2;
        double similitud;
        long numPeliculas = peliculas.size();
        
        
        for (long i=0; i<numPeliculas; ++i){
            //System.out.println(" pelicula "+i+" de "+numPeliculas);
            //###// 1.1: Sacar la película numero i. Nota: estudiar si se pueden sacar todas de golpe.
            //Pelicula it1 = getPeliculaBD_byPos(instancia, i);
            Pelicula it1 = peliculas.get((int)i);
            id1 = it1.getIdPelicula();
            
            for (long j=i+1; j<numPeliculas; ++j){
                //###// 1.2: Sacar la película numero j.
                //Pelicula it2 = getPeliculaBD_byPos(instancia, j);
                Pelicula it2 = peliculas.get((int)j);
                id2 = it2.getIdPelicula();
                
                // 1.2: Calculo de la similitud entre it1 e it2.
                similitud = similitudCoseno(it1, it2);
                
                // 1.3: Guardar la similitud en una estructura.
                    //### 1.3: En el modelo definitivo, la similitud se guardará en la base de datos.
                    //###//Similitud s1 = new Similitud(it1.id,it2.id,similitud);
                //     NOTA: Hay que guardar, a la vez, tanto la similitud sim(id1,id2) como sim (id2,id1)
                if (modelo_similitud.containsKey(id1)){
                    fila1 =  modelo_similitud.get(id1);
                    fila1.add(new ItemSim(id2,similitud));
                    if (fila1.size() > k){
                        fila1.remove(fila1.last());
                    }
                    
                    if (modelo_similitud.containsKey(id2)){
                        fila2 =  modelo_similitud.get(id2);
                        fila2.add(new ItemSim(id1,similitud));
                        if (fila2.size() > k){
                            fila2.remove(fila2.last());
                        }
                    }else{
                        modelo_similitud.put(id2, new TreeSet<ItemSim>());
                        modelo_similitud.get(id2).add(new ItemSim(id1,similitud));
                    }
                }else{
                    modelo_similitud.put(id1, new TreeSet<ItemSim>());
                    modelo_similitud.get(id1).add(new ItemSim(id2,similitud));
                    
                    if (modelo_similitud.containsKey(id2)){
                        fila2 =  modelo_similitud.get(id2);
                        fila2.add(new ItemSim(id1,similitud));
                        if (fila2.size() > k){
                            fila2.remove(fila2.last());
                        }
                    }else{
                        modelo_similitud.put(id2, new TreeSet<ItemSim>());
                        modelo_similitud.get(id2).add(new ItemSim(id1,similitud));
                    }
                }
            }
        }
        
        return modelo_similitud;
    }


    public void ejecucionTrainingCoseno(int k, ArrayList<Pelicula> peliculas) throws ErrorDatoInvalido, IOException {
        // Estructura que representa el modelo de similitud (clave: id de pelicula; valor: lista de idPelicula-Similitud).
        HashMap<Long, TreeSet<ItemSim>> modeloSimilitud;
        // Variables auxiliares:
        long tiempoTraining;
        Parametros param = new Parametros();

        // PASO 1: Calcular la similitud de cada item con el resto, saltándonos las valoraciones de los usuarios que forman parte del test.
        tiempoTraining = System.currentTimeMillis();
            modeloSimilitud = getModeloSimilitud_byCoseno(k, peliculas);
        tiempoTraining = System.currentTimeMillis() - tiempoTraining;

        // SERIALIZAR DESERIALIZAR UN MODELO SIMILIUTD
        String url = k+"-Coseno";
        SerializarModeloSimilitud selializar = new SerializarModeloSimilitud(modeloSimilitud);
        selializar.serializar(url+".bin");
        
        String url2 = "tt"+url;
        guardarTiempoTrainingFichero((long) tiempoTraining/1000, url2+".txt");
    }
    
    private void guardarTiempoTrainingFichero(long l, String url) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(url))) {
            bw.write( Long.toString(l));
        }
    }
    
     ///////////////////////////////////////////// SIMILITUD PEARSON //////////////////////////////////////////

    
    public double similitudPearson(Pelicula i1, Pelicula i2, ArrayList<Long> test){
        // Variables auxiliares:
        double norma1 = 0;
        double norma2 = 0;
        int val1;
        int val2;
        Long key;
        double numerador = 0;
        double media1 = i1.getMedia();
        double media2 = i2.getMedia();
        int comun = 0;
        // Constante de la MEJORA del Factor de relevancia
        int N = 200;
        
        // 1. Nos quedamos con la películas que tenga menos valoraciones.
        if (i1.getValoraciones().size() < i2.getValoraciones().size()){
            for (Map.Entry<Long,Valoracion> e : i1.getValoraciones().entrySet()) {
                key = e.getKey();
                // 2. Descartamos los usuarios de la partición test.
                if (!test.contains(key)){
                    // 3. Comprobamos que la otra película haya sido valorada por el mismo usuario.
                    if (i2.getValoraciones().containsKey(key)){
                        // 4. Realizamos los cálculos de similitud.
                        val1 = e.getValue().getValor();
                        val2 = i2.getValoraciones().get(key).getValor();

                        norma1 = norma1 + (val1 - media1)*(val1 - media1);
                        norma2 = norma2 + (val2 - media2)*(val2 - media2);

                        numerador = numerador + (val1 - media1)*(val2 - media2);
                        ++comun;
                    }
                }
            }
        }else{
            for (Map.Entry<Long,Valoracion> e : i2.getValoraciones().entrySet()) {
                key = e.getKey();
                if (!test.contains(key)){
                    if (i1.getValoraciones().containsKey(key)){
                        val2 = e.getValue().getValor();
                        val1 = i1.getValoraciones().get(key).getValor();

                        norma1 = norma1 + (val1 - media1)*(val1 - media1);
                        norma2 = norma2 + (val2 - media2)*(val2 - media2);

                        numerador = numerador + (val1 - media1)*(val2 - media2);
                        ++comun;
                    }
                }
            }
        }
        
        if (norma1 != 0 && norma2 !=0){
            double sim = numerador / (Math.sqrt(norma1*norma2)) ;
            sim = (sim + 1)/2;
            // Aplicamos la MEJORA del Factor de relevancia.
            if (comun < N){
                sim = sim * ((comun*1.0)/N);
            }
            if (sim > 1){
                return 1;
            }
            return sim;
        }else{
            return 0;
        }
        
    }
    
    
    
    
   
    
    
    public HashMap<Long, TreeSet<ItemSim>> getModeloSimilitud_byPearson(int k, ArrayList<Long> test, ArrayList<Pelicula> peliculas) {
        // Estructura que representa el modelo de similitud (clave: id de pelicula; valor: lista de idpelicula-Similitud).
        HashMap<Long, TreeSet<ItemSim>> modelo_similitud = new HashMap();
        // Variables auxiliares:
        TreeSet<ItemSim> fila1;
        TreeSet<ItemSim> fila2;
        long id1;
        long id2;
        double similitud;
        long numpeliculas = peliculas.size();
        
        for (long i=0; i<numpeliculas; ++i){
            //System.out.println(" pelicula "+i+" de "+numpeliculas);
            //###// 1.1: Sacar la película numero i. Nota: estudiar si se pueden sacar todas de golpe.
            //pelicula it1 = getpeliculaBD_byPos(instancia, i);
            Pelicula it1 = peliculas.get((int)i);
            id1 = it1.getIdPelicula();
         
            for (long j=i+1; j<numpeliculas; ++j){
                //###// 1.2: Sacar la película numero j.
                //pelicula it2 = getpeliculaBD_byPos(instancia, j);
                Pelicula it2 = peliculas.get((int)j);
                id2 = it2.getIdPelicula();
                
                // 1.2: Calculo de la similitud entre it1 e it2.
                similitud = similitudPearson(it1, it2, test);
                
                // 1.3: Guardar la similitud en una estructura.
                    //### 1.3: En el modelo definitivo, la similitud se guardará en la base de datos.
                    //###//Similitud s1 = new Similitud(it1.id,it2.id,similitud);
                //     NOTA: Hay que guardar, a la vez, tanto la similitud sim(id1,id2) como sim (id2,id1)
                if (modelo_similitud.containsKey(id1)){
                    fila1 =  modelo_similitud.get(id1);
                    fila1.add(new ItemSim(id2,similitud));
                    if (fila1.size() > k){
                        fila1.remove(fila1.last());
                    }
                    
                    if (modelo_similitud.containsKey(id2)){
                        fila2 =  modelo_similitud.get(id2);
                        fila2.add(new ItemSim(id1,similitud));
                        if (fila2.size() > k){
                            fila2.remove(fila2.last());
                        }
                    }else{
                        modelo_similitud.put(id2, new TreeSet<ItemSim>());
                        modelo_similitud.get(id2).add(new ItemSim(id1,similitud));
                    }
                }else{
                    modelo_similitud.put(id1, new TreeSet<ItemSim>());
                    modelo_similitud.get(id1).add(new ItemSim(id2,similitud));
                    
                    if (modelo_similitud.containsKey(id2)){
                        fila2 =  modelo_similitud.get(id2);
                        fila2.add(new ItemSim(id1,similitud));
                        if (fila2.size() > k){
                            fila2.remove(fila2.last());
                        }
                    }else{
                        modelo_similitud.put(id2, new TreeSet<ItemSim>());
                        modelo_similitud.get(id2).add(new ItemSim(id1,similitud));
                    }
                }
            }
        }
        
        return modelo_similitud;
    }
    
}
