package sample;

import java.io.File;
import java.io.PrintStream;

public class Write implements AutoCloseable {

    public void writeTimetable(byte[][] timetable){
        try(PrintStream fout = new PrintStream(new File("Orar.txt"))){
            for(byte cls = 0; cls < Input.nrGroups; cls++){
                for(byte prof = 0; prof < Input.nrProf; prof++){
                    for(byte hour = 0; hour < Input.nrHours * 5; hour++){
                        if(timetable[prof][hour] == cls) {
                            fout.println(Input.groups.get(cls)+ " ZIUA: "+ hour / Input.nrHours+ " ORA: "+ hour % Input.nrHours + " PROFESOR: " +Input.profs.get(prof));
                        }
                    }
                }
                fout.println();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void close(){

    }
}
