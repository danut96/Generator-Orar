package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Input {
    public static int nrProf;    // # of profesors
    public static int nrGroups; // # of groups
    public static int nrHours; // maximum # of hours/day
    public static Map<Byte, Professor> profs;
    public static Map<Byte, Group> groups;
    public static byte[][] classes; //[prof][group] = #classes at group "group" for professor "prof"
    public static List<Byte>[] profSchedual;

    public Input(int nrProf, int nrGroups, int nrHours){
        this.nrProf = nrProf;
        this.nrGroups = nrGroups;
        this.nrHours = nrHours;
        classes = new byte[nrProf][nrGroups];
        profSchedual = new List[nrProf];
    }

    public void proffs(){

        for(byte i = 0; i < nrProf; i++){// prof
            profSchedual[i] = new ArrayList<>();
            for(byte j = 0; j < nrGroups; j++) { //group
                for (byte k = 0; k < classes[i][j]; k++) {// # class at that group
                    profSchedual[i].add(j);
                }
            }
            for(int j = profSchedual[i].size(); j < nrHours * 5; j++){
                profSchedual[i].add((byte)-1); // add free hours
            }
        }
    }

}
