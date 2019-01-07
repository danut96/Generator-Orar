package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chromosome {
    public byte[][] timetable; // [prof][# hours * # days]
    public int cost;
    public double fitness = 0.0;
    public double[] rowFitness;
    public int nrInfes;

    // generate a random timetable
    public Chromosome(){
        timetable = new byte[Input.nrProf][5 * Input.nrHours];
        for(int i = 0; i < Input.nrProf; i++){
            Collections.shuffle(Input.profSchedual[i]);
            for(int j = 0; j < 5 * Input.nrHours; j++){
                timetable[i][j] = Input.profSchedual[i].get(j);
            }
        }
    }


    // fitness of every row
    public void localFitness(){
        double[] fitness = new double[Input.nrProf];
        for(byte i = 0; i < Input.nrProf; i++){
            fitness[i] = fitnessRow(i, timetable[i]);
        }
        this.rowFitness = fitness;
    }

    // calculate fitness for a single row
    public double fitnessRow(byte prof, byte[] profRow){
        byte prob = 0;

        // cat mai putine ferestre la profesori
        for(byte hour = 0; hour < Input.nrHours * 5; hour++){
            if(profRow[hour] != -1) {
                int sum = 0;
                for (byte hour2 = (byte)(hour + 1); hour2 < Input.nrHours * 5; hour2++) {
                    if(hour2 / Input.nrHours == hour / Input.nrHours && profRow[hour2] == -1){
                        sum++;
                    }else{
                        break;
                    }
                }
                if(sum > 0) prob++;
            }
        }

        // mate si romana cate 1- 2 / zi (maxim)
        if(Input.profs.get(prof).d.name.equals("MAT") || Input.profs.get(prof).d.name.equals("ROM")) {
            for(byte cls = 0; cls < Input.nrGroups; cls++) {
                for(byte day = 0; day < 5; day++) {
                    int sum = 0;
                    for (byte hour = 0; hour < Input.nrHours * 5; hour++) {
                        if (hour / Input.nrHours == day && profRow[hour] == cls) {
                            sum++;
                        }
                    }
                    if(sum > 2 && Input.classes[prof][cls] < 6) prob++;
                }
            }
        }

        // mate si romana mai devreme
        if(Input.profs.get(prof).d.name.equals("MAT") || Input.profs.get(prof).d.name.equals("ROM")) {
            for(byte hour = 0; hour < Input.nrHours * 5; hour++){
                if(hour % Input.nrHours > Input.nrHours / 2 && profRow[hour] != -1) prob++;
            }
        }

        // ore mai imprastiate
        if(!(Input.profs.get(prof).d.name.equals("MAT") || Input.profs.get(prof).d.name.equals("ROM"))) {
                for(byte hour = 0; hour < Input.nrHours * 5; hour++) {
                    for(byte hour2 = 0; hour2 < hour; hour2++) {
                        if (hour / Input.nrHours == hour2 / Input.nrHours && profRow[hour] != -1 && profRow[hour] == profRow[hour2])
                            prob++;
                    }
            }
        }
        //System.out.println(prob);
        return 1.0 / (prob + 1);
    }

    // # of infeasibilities
    private int nrInfes(){
        int prob = 0;
        // two or more teachers during same hour same class
        for(byte hour = 0; hour < Input.nrHours * 5; hour++){
            for(byte prof1 = 0; prof1 < Input.nrProf; prof1++){
                for(byte prof2 = 0; prof2 < prof1; prof2++){
                    if(timetable[prof1][hour] != -1 && timetable[prof1][hour] == timetable[prof2][hour]) {
                        prob ++;
                    }
                }
            }
        }
        // same prof during same time in two diff groups if the class is scheduled once per two or more weeks
        for(byte prof = 0; prof < Input.nrProf; prof++){
            if(Input.profs.get(prof).name.contains("/")){
                String prof1 = Input.profs.get(prof).name.split("/")[0];
                String prof2 = Input.profs.get(prof).name.split("/")[1];
                for(byte prof3 = 0; prof3 < Input.nrProf; prof3++){
                    if(Input.profs.get(prof3).name.equals(prof1) || Input.profs.get(prof3).name.equals(prof2)){
                        for(byte hour = 0; hour < Input.nrHours * 5; hour++){
                            if(timetable[prof][hour] != -1 && timetable[prof3][hour] != -1){
                                prob++;
                            }
                        }
                    }
                }
            }
        }
        this.nrInfes = prob;
        return prob;
    }

    // # of didactic dissatisfaction
    public int didacticDissatisfaction(){
        int prob = 0;
        boolean sem1;
        boolean sem2;
        boolean sem3;
        // fara ferestre
        for(byte cls = 0; cls < Input.nrGroups; cls++){
            for(byte hour = 0; hour < Input.nrHours * 5 - 2; hour++){
                sem1 = false;
                sem2 = false;
                sem3 = false;
                for(byte prof = 0; prof < Input.nrProf; prof++) {
                    if (this.timetable[prof][hour] == cls) {
                        sem1 = true;
                        break;
                    }
                }
                for(byte prof = 0; prof < Input.nrProf; prof++) {
                    if (this.timetable[prof][hour + 1] == cls && ((hour + 1) / Input.nrHours) == (hour / Input.nrHours)) {
                        sem2 = true;
                        break;
                    }
                }
                for(byte ah = 2; ah < Input.nrHours - hour % Input.nrHours ; ah++) {
                    for (byte prof = 0; prof < Input.nrProf; prof++) {
                        if (this.timetable[prof][hour + ah] == cls && ((hour + ah) / Input.nrHours) == (hour / Input.nrHours)) {
                            sem3 = true;
                            break;
                        }
                    }
                }
                if(sem1 == true && sem2 == false && sem3 == true) {
                    prob++;
                }
            }
        }

        // ore incepute nu mai tarziu de 9
        for(byte cls = 0; cls < Input.nrGroups; cls++){
            boolean sem;
            boolean semm = false;
            for(byte day = 0; day < 5; day++) {
                sem = true;
                for (byte prof = 0; prof < Input.nrProf; prof++) {
                    if (timetable[prof][day * Input.nrHours + 1] == cls) sem = false;
                }
                if(sem) semm = true;
            }
            if(semm){
                prob++;
            }
        }

        // cel putin 3 ore pe zi pentru fiecare clasa
        for(byte cls = 0; cls < Input.nrGroups; cls++){
            for(byte day = 0; day < 5; day++){
                byte sum = 0;
                for(byte prof = 0; prof < Input.nrProf; prof++){
                    for(byte hour = 0; hour < Input.nrHours * 5; hour++){
                        if(hour / Input.nrHours == day && timetable[prof][hour] == cls) sum++;
                    }
                }
                if(sum < 3) {
                    prob++;
                }
            }
        }

        // orele cat mai imprastiate (fara doua ore in aceeasi zi)
        for(byte prof = 0; prof < Input.nrProf; prof++){
            if(!(Input.profs.get(prof).d.name.equals("MAT") || Input.profs.get(prof).d.name.equals("ROM"))) {
                for(byte hour = 0; hour < Input.nrHours * 5; hour++) {
                    for(byte hour2 = 0; hour2 < hour; hour2++){
                        if (hour / Input.nrHours == hour2 / Input.nrHours && timetable[prof][hour2] != -1 && timetable[prof][hour] != -1 && timetable[prof][hour] == timetable[prof][hour2]){
                            prob++;
                            break;
                        }
                    }
                }
            }
        }
        return prob;
    }


    //organizational cost
    public int organizDiss(){
        int prob = 0;

        // matematica si romana mai devreme
        for(byte prof = 0; prof < Input.nrProf; prof++){
            if(Input.profs.get(prof).d.name.equals("MAT") || Input.profs.get(prof).d.name.equals("ROM")) {
                for(byte hour = 0; hour < Input.nrHours * 5; hour++){
                    if(hour % Input.nrHours > Input.nrHours / 2 && timetable[prof][hour] != -1) prob++;
                }
            }
        }

        // mate si romana cate 1 - 2 / zi (maxim)
        for(byte cls = 0; cls < Input.nrGroups; cls++){
            for(byte day = 0; day < 5; day++){
                int sum = 0;
                for(byte hour = 0; hour < Input.nrHours * 5; hour++){
                    for(byte prof = 0; prof < Input.nrProf; prof++){
                        if((Input.profs.get(prof).d.name.equals("MAT") || Input.profs.get(prof).d.name.equals("ROM")) && timetable[prof][hour] == cls){
                            if(hour / Input.nrHours == day){
                                sum++;
                            }
                        }
                    }
                }
                if(sum > 2) {
                    prob++;
                }
            }
        }

        // cat mai putine ferestre la profesori
        for(byte prof = 0; prof < Input.nrProf; prof++){
            for(byte hour = 0; hour < Input.nrHours * 5 - 2; hour++){
                //if(timetable[prof][hour] != -1 && timetable[prof][hour + 1] == -1 && timetable[prof][hour + 2] != -1) prob++;
            }
        }

        return prob;
    }

    // teacher cost
    private int teacherDissat(){
        int prob = 0;
        // prof X nu poate lucra in ziua Y
        // TODO
        return prob;
    }

    // calculate total fitness of timetable
    public int totalFitness(){
        this.cost =
                GeneticAlgorithm.weightInfes * nrInfes() +
                GeneticAlgorithm.weightManag * didacticDissatisfaction() +
                GeneticAlgorithm.weightOrg * organizDiss() +
                GeneticAlgorithm.weightProf * teacherDissat();
        return this.cost;
    }

    // print timetable in chromosome format
    public void printTimetable(){
        for(int i = 0; i < Input.nrProf; i++){
            for(int j = 0; j < Input.nrHours * 5; j++){
                if(this.timetable[i][j] < 0)System.out.print(this.timetable[i][j] + " ");
                else{
                    System.out.print(" " + this.timetable[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public void copyTimetable(byte[][] timetable){
        for(byte i = 0; i < Input.nrProf; i++){
            for(byte j = 0; j < Input.nrHours * 5; j++){
                this.timetable[i][j] = timetable[i][j];
            }
        }
    }
}
