/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algoritmos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;

/**
 *
 * @author Pc
 */
public class SerializarModeloSimilitud implements Serializable{
    HashMap<Long, TreeSet<ItemSim>> modeloSimilitud;

    public SerializarModeloSimilitud(HashMap<Long, TreeSet<ItemSim>> modeloSimilitud) {
        this.modeloSimilitud = modeloSimilitud;
    }

    public SerializarModeloSimilitud() {
    }
    

    public void serializar (String archivo) throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo));
        salida.writeObject(this);
    }
    
    public SerializarModeloSimilitud deserializar (String archivo) throws IOException, ClassNotFoundException {        
        ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo));
        return (SerializarModeloSimilitud) entrada.readObject();
    }
    
    public HashMap<Long, TreeSet<ItemSim>> getModeloSimilitud() {
        return modeloSimilitud;
    }

    public void setModeloSimilitud(HashMap<Long, TreeSet<ItemSim>> modeloSimilitud) {
        this.modeloSimilitud = modeloSimilitud;
    }
    
    
    
}