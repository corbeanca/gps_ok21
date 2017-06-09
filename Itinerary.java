package a_barbu.gps_agenda;

import java.util.List;
import java.util.Vector;

/**
 * Created by Alex on 24-May-17.
 */

public class Itinerary {

    String desc;
    String name;
    String date;
    List<Integer> IDs ;
    int length =0;
    int move;
//nu stiu daca o sa vrea cu IDs
    public Itinerary(){

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //in IDs[][x] primul x e Markerul, al doilea x e legatura catre urm Marker(poate fi 0)
    public Itinerary (String desc, String name, int move){
        this.desc = desc;
        this.name = name;
        this.move = move;
        //format();
    }
//    private void format() {
//        for (int i=0;i<25;i++) {
//            IDs.get(i) = 0;
//           // IDs[i][1] = 0;
//        }
//    }
//
//    public void SetDate(String d){
//        this.date=d;
//    }
//
//    public void addID(int i){
//       int u = length;
//            IDs[u][0]=i;
//        this.length += this.length;
//    }

//    public void addIDsec(int i, int j){
//            IDs[i][1]=j;
//    }

    public boolean HasMk(Itinerary it, MarkerObj mk){
        int lg = it.length;
        for(int p =1;p<=lg;p++)
            if (it.IDs.get(p) == mk.ID)
                return true;
        return false;
    }

//    public void removeID(int i){
//        for(int p=0;p<length;p++)
//            if (IDs[p][0]==i)
//            {
//                for(int q=p;p<length-1;q++){
//                    IDs[q][0]=IDs[q+1][0];
//                    IDs[q][1]=IDs[q+1][1];
//                }
//            }
//        IDs[length][0]=0;
//        IDs[length][1]=0;
//        this.length=this.length-1;
//    }

    @Override
    public String toString() {
        return this.name ;
    }

}
