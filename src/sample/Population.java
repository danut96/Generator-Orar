package sample;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Population {

    List<Byte> indexParent1;
    List<Byte> firstK1;
    Chromosome[] population;
    double totalFitness;
    int minOF;
    int maxOF;

    // create population with # of chromosomes = dim
    public Population(int dim){
        population = new Chromosome[dim];
        for(int i = 0; i < dim; i++){
            Chromosome c = new Chromosome();
            population[i] = c;
        }
    }

    // get the fittest chromosome
    public Chromosome getFittest(){
        Arrays.sort(this.population, (i1,i2) -> {
            if(i1.fitness - i2.fitness > 0) return -1;
            if(i1.fitness - i2.fitness < 0) return  1;
            return 0;
        });
        return this.population[0];
    }

    // select chromosomes based on tournament selection
    public Chromosome selection(Population p){
        // Create tournament
        Population tournament = new Population(GeneticAlgorithm.tournamentSize);

        List<Chromosome> shufPop = new ArrayList();
        shufPop = Arrays.asList(p.population);
        // Add random individuals to the tournament
        Collections.shuffle(shufPop);
        for (int i = 0; i < GeneticAlgorithm.tournamentSize; i++) {
            Chromosome tournamentIndividual = shufPop.get(i);
            tournament.population[i] = tournamentIndividual;
        }

        // Return the best
        return tournament.getFittest();
    }

    // select chromosomes based on roulette wheel selection
    private Chromosome select(){
        int i = 0;
        double r = Math.random();
        while(r > 0){
            r -= this.population[i].fitness;
            i++;
        }
        i--;
        return this.population[i];
    }

    // reproduce population
    public Population reproduction(){
        Population p = new Population(GeneticAlgorithm.populationSize);
        for(int i = 0; i < GeneticAlgorithm.populationSize; i++){
            Chromosome c = new Chromosome();
            c.copyTimetable(selection(this).timetable);
            c.copyTimetable(select().timetable);
            c.totalFitness();
            p.population[i] = c;
            //p.population[i] = select();
        }
        return p;
    }

    // uniform cross-over
    public Population unifCrossOver(){
        Population p = new Population(GeneticAlgorithm.populationSize);
        for(int i = 0; i < GeneticAlgorithm.populationSize; i++){
//            Chromosome parent1 = selection(this);
//            Chromosome parent2 = selection(this);
            Chromosome parent1 = select();
            Chromosome parent2 = select();
            Chromosome child = new Chromosome();
            if(GeneticAlgorithm.crossOverRate > Math.random()){
                for(int prof = 0; prof < Input.nrProf; prof++){
                        if(0.5 > Math.random()){
                            child.timetable[prof] = parent1.timetable[prof];
                        }else{
                            child.timetable[prof] = parent2.timetable[prof];
                        }
                }
            }else{
                child = parent1;
            }
            p.population[i] = child;
        }
        return p;
    }

    // cross-over
    public Population crossOver(){
        List<Chromosome> shufPop = new ArrayList();
        shufPop = Arrays.asList(this.population);
        Collections.shuffle(shufPop);
        Population p = new Population(GeneticAlgorithm.populationSize);
        for(int ind = 0; ind < GeneticAlgorithm.populationSize; ind += 2) {
            int p1 = new Random().nextInt(GeneticAlgorithm.populationSize);
            int p2 = new Random().nextInt(GeneticAlgorithm.populationSize);
            //Chromosome parent1 = this.population[p1];// parent 1
            //Chromosome parent2 = this.population[p2];// parent 2
            Chromosome parent1 = shufPop.get(ind);// parent 1
            Chromosome parent2 = shufPop.get(ind + 1);// parent 2
            parent1.totalFitness();
            parent2.totalFitness();
            double minCost = parent1.cost  < parent2.cost  ? parent1.cost  : parent2.cost ;
 //           System.out.println(parent1.cost - parent1.nrInfes * GeneticAlgorithm.weightInfes + " " + (parent2.cost - parent2.nrInfes * GeneticAlgorithm.weightInfes));
            if(GeneticAlgorithm.crossOverRate > Math.random()) {
                indexParent1 = new ArrayList<>();
                firstK1 = new ArrayList<>();
                for (byte i = 0; i < Input.nrProf; i++) {
                    indexParent1.add(i);
                }
                parent1.localFitness();
                parent2.localFitness();

                indexParent1.sort((i1, i2) -> {
                    if (parent1.rowFitness[i1] > parent1.rowFitness[i2]) return -1;
                    if (parent1.rowFitness[i1] < parent1.rowFitness[i2]) return 1;
                    return 0;
                });
                //begin cross-over
                Chromosome c1 = parent1;
                Chromosome c2 = parent2;
                c1.totalFitness();
                c2.totalFitness();
                for(int k1 = 1; k1 < Input.nrProf; k1++){
                    firstK1 = indexParent1.stream().limit(k1).collect(Collectors.toList());
                    Chromosome offspring1 = new Chromosome();
                    Chromosome offspring2 = new Chromosome();
                    for (int i = 0; i < Input.nrProf; i++) {
                        if (i <= k1) {
                            offspring1.timetable[indexParent1.get(i)] = parent1.timetable[indexParent1.get(i)];
                        } else {
                            offspring2.timetable[indexParent1.get(i)] = parent1.timetable[indexParent1.get(i)];
                        }
                        for(byte u = 0; u < Input.nrProf; u++){
                            if(!firstK1.contains(u)){
                                offspring1.timetable[u] = parent2.timetable[u];
                            }
                            else{
                                offspring2.timetable[u] = parent2.timetable[u];
                            }
                        }
                    }
                    offspring1.totalFitness();
                    offspring2.totalFitness();
 //                   System.out.println("InterChild" + (offspring1.cost - offspring1.nrInfes * GeneticAlgorithm.weightInfes) + " " + (offspring2.cost - offspring2.nrInfes * GeneticAlgorithm.weightInfes));
                    if(offspring1.cost  < minCost || offspring2.cost  < minCost) {
                        c1 = offspring1;
                        c2 = offspring2;
                        if(offspring1.cost  < offspring2.cost ){
                            minCost = offspring1.cost ;
                        }
                        else {
                            minCost = offspring2.cost ;
                        }
                    }
//                    c1 = offspring1;
//                    c2 = offspring2;
                }
                p.population[ind] = c1;
                p.population[ind + 1] = c2;
//                c1.totalFitness();
//                c2.totalFitness();
//                System.out.println(c1.cost - c1.nrInfes * GeneticAlgorithm.weightInfes + " " + (c2.cost - c2.nrInfes * GeneticAlgorithm.weightInfes));
//                System.out.println();
            }
            else{
                p.population[ind] = parent1;
                p.population[ind + 1] = parent2;
//                System.out.println("ELSE");
            }
        }
//        p.evaluatePopulation();
//        System.out.println(p.getFittest().totalFitness());
        return p;
    }

    //mutate k contiguous genes
    public Chromosome mutateOfOrderK(Chromosome c, int k){
            byte aux;
            for(byte row = 0; row < Input.nrProf; row++) {
                if(GeneticAlgorithm.mutationRateK > Math.random()) {
                    int r;
                    int r2;
                    while(true){
                        r = new Random().nextInt(Input.nrHours * 5);
                        r2 = new Random().nextInt(Input.nrHours * 5);
                        if((r2 - r > k && r2 <= Input.nrHours * 5 - k ) || (r - r2 > k) && r <= Input.nrHours * 5 - k){
                            break;
                        }
                    }
                    // swap k contigous genes from the same row
                    for (byte i = 0; i < k; i++) {
                        aux = c.timetable[row][r + i];
                        c.timetable[row][r + i] = c.timetable[row][r2 + i];
                        c.timetable[row][r2 + i] = aux;
                    }
                }
            }
        return c;
    }

    // mutate one gene
    public Chromosome mutateOfOrderOne(Chromosome c){
        byte aux;
        for(byte row = 0; row < Input.nrProf; row++) {
            if(GeneticAlgorithm.mutationRate1 > Math.random()){
                int r1 = new Random().nextInt(Input.nrHours * 5);
                int r2 = new Random().nextInt(Input.nrHours * 5);
                aux = c.timetable[row][r1];
                c.timetable[row][r1] = c.timetable[row][r2];
                c.timetable[row][r2] = aux;
            }
        }
        return c;
    }


    // mutate nrHours contigous genes (which represents a day)
    public Chromosome dayMutation(Chromosome c){
            // add probability of mutation pmd
            byte aux;
            for(int row = 0; row < Input.nrProf; row++) {
                if(GeneticAlgorithm.mutationRateD > Math.random()) {
                    int day1 = new Random().nextInt(5);
                    int day2 = new Random().nextInt(5);
                    if (day1 != day2) {
                        for (byte i = 0; i < Input.nrHours; i++) {
                            aux = c.timetable[row][day1 * Input.nrHours + i];
                            c.timetable[row][day1 * Input.nrHours + i] = c.timetable[row][day2 * Input.nrHours + i];
                            c.timetable[row][day2 * Input.nrHours + i] = aux;
                        }
                    }
                }
            }
        return c;
    }

    // apply filter to chromosome in order to reduce # of infeasabilities
    public Chromosome filter(Chromosome c){
        // Step 0 : see which classes have superimpositions or are missing
        List<Integer> indxO;
        List<Integer> indxM;
//        List<Byte> overH = new ArrayList<>();
//        List<Byte> missH = new ArrayList<>();
//        for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
//            for(byte cl = 0; cl < Input.nrGroups; cl++){
//                int sum = 0;
//                for(byte prof = 0; prof < Input.nrProf; prof++){
//                    if(c.timetable[prof][hour] == cl) sum++;
//                }
//                if(sum == 0 && hour % Input.nrHours > 1 && hour % Input.nrHours < 4) {
//                    missH.add(cl);
//                    missH.add(hour);
//                }
//                if(sum > 1) {
//                    overH.add(cl);
//                    overH.add(hour);
//                }
//            }
//        }
        // Step 1 : swap classes that are part of step1
        boolean sem = true;
        int overHind1 = 0;
        int overHind2 = 0;
        int overHind3 = 0;
        int missHind1 = 0;
        int missHind2 = 0;
        int missHind3 = 0;
        while(sem){
            List<Byte> overH = new ArrayList<>();
            List<Byte> missH = new ArrayList<>();
            for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
                for(byte cl = 0; cl < Input.nrGroups; cl++){
                    int sum = 0;
                    for(byte prof = 0; prof < Input.nrProf; prof++){
                        if(c.timetable[prof][hour] == cl) sum++;
                    }
                    if(sum == 0 ) {
                        missH.add(cl);
                        missH.add(hour);
                    }
                    if(sum > 1) {
                        overH.add(cl);
                        overH.add(hour);
                    }
                }
            }
//            System.out.println(overH);
//            System.out.println(overH.size());
//            System.out.println(missH);
//            System.out.println(missH.size());
            sem = false;
            for(byte row = 0; row < Input.nrProf; row++) {
                for (byte col = 0; col < Input.nrHours * 5; col++) {
                    for(byte col2 = 0; col2 < Input.nrHours * 5; col2++){
                        boolean sem1 = false;
                        boolean sem2 = false;
                        boolean sem3 = false;
                        boolean sem4 = false;
                        if(c.timetable[row][col] != -1 && c.timetable[row][col2] != -1) {
                            if (c.timetable[row][col] != c.timetable[row][col2]) {
                                for (int i = 0; i < overH.size(); i += 2) {
                                    if (overH.get(i) == c.timetable[row][col] && overH.get(i + 1) == col) {
                                        sem1 = true;
                                        overHind1 = i;
                                        break;
                                    }
                                }
                                for (int i = 0; i < overH.size(); i += 2) {
                                    if (overH.get(i) == c.timetable[row][col2] && overH.get(i + 1) == col2) {
                                        sem2 = true;
                                        overHind2 = i;
                                        break;
                                    }
                                }
                                for (int i = 0; i < missH.size(); i += 2) {
                                    if (missH.get(i) == c.timetable[row][col2] && missH.get(i + 1) == col) {
                                        sem3 = true;
                                        missHind1 = i;
                                        break;
                                    }
                                }
                                for (int i = 0; i < missH.size(); i += 2) {
                                    if (missH.get(i) == c.timetable[row][col] && missH.get(i + 1) == col2) {
                                        sem4 = true;
                                        missHind2 = i;
                                        break;
                                    }
                                }
                                if (sem1 && sem2 && sem3 && sem4) {
                                    sem = true;
                                    // swap classes
                                    byte aux = c.timetable[row][col];
                                    c.timetable[row][col] = c.timetable[row][col2];
                                    c.timetable[row][col2] = aux;
                                    // remove cls from overH and missH
                                    if (overHind1 < overHind2) {
                                        overH.remove(overHind1);
                                        overH.remove(overHind1);
                                        overH.remove(overHind2 - 2);
                                        overH.remove(overHind2 - 2);
                                    } else {
                                        overH.remove(overHind2);
                                        overH.remove(overHind2);
                                        overH.remove(overHind1 - 2);
                                        overH.remove(overHind1 - 2);
                                    }
                                    if (missHind1 < missHind2) {
                                        missH.remove(missHind1);
                                        missH.remove(missHind1);
                                        missH.remove(missHind2 - 2);
                                        missH.remove(missHind2 - 2);
                                    } else {
                                        missH.remove(missHind2);
                                        missH.remove(missHind2);
                                        missH.remove(missHind1 - 2);
                                        missH.remove(missHind1 - 2);
                                    }

                                }
                            }
                        }
                    }
                }
            }
//            System.out.println(overH);
//            System.out.println(overH.size());
//            System.out.println(missH);
//            System.out.println(missH.size());
        }
//        overH = new ArrayList<>();
//        missH = new ArrayList<>();
//        for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
//            for(byte cl = 0; cl < Input.nrGroups; cl++){
//                int sum = 0;
//                for(byte prof = 0; prof < Input.nrProf; prof++){
//                    if(c.timetable[prof][hour] == cl) sum++;
//                }
//                if(sum == 0 && hour % Input.nrHours > 1 && hour % Input.nrHours < 4) {
//                    missH.add(cl);
//                    missH.add(hour);
//                }
//                if(sum > 1) {
//                    overH.add(cl);
//                    overH.add(hour);
//                }
//            }
//        }
        // Step 2 : swap classes from step 1 with free hours
        sem = true;
        while(sem){
            List<Byte> overH = new ArrayList<>();
            List<Byte> missH = new ArrayList<>();
            for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
                for(byte cl = 0; cl < Input.nrGroups; cl++){
                    int sum = 0;
                    for(byte prof = 0; prof < Input.nrProf; prof++){
                        if(c.timetable[prof][hour] == cl) sum++;
                    }
                    if(sum == 0  ) {
                        missH.add(cl);
                        missH.add(hour);
                    }
                    if(sum > 1) {
                        overH.add(cl);
                        overH.add(hour);
                    }
                }
            }
            sem = false;
            for(byte row = 0; row < Input.nrProf; row++) {
                for (byte col = 0; col < Input.nrHours * 5; col++) {
                    for (byte col2 = 0; col2 < Input.nrHours * 5; col2++) {
                        boolean sem1 = false;
                        boolean sem2 = false;
                        if(c.timetable[row][col] == -1 && c.timetable[row][col2] != -1){
                            for(int i = 0 ; i < overH.size(); i +=2) {
                                if (overH.get(i) == c.timetable[row][col2] && overH.get(i + 1) == col2) {
                                    sem1 = true;
                                    overHind1 = i;
                                    break;
                                }
                            }
                            for(int i = 0; i < missH.size(); i +=2){
                                if (missH.get(i) == c.timetable[row][col2] && missH.get(i + 1) == col) {
                                    sem2 = true;
                                    missHind1 = i;
                                    break;
                                }
                            }
                            if(sem1 && sem2){
                                sem = true;
                                // swap classes
                                byte aux = c.timetable[row][col];
                                c.timetable[row][col] = c.timetable[row][col2];
                                c.timetable[row][col2] = aux;
                                overH.remove(overHind1);
                                overH.remove(overHind1);
                                missH.remove(missHind1);
                                missH.remove(missHind1);
                            }
                        }
                    }
                }
            }
        }
//        overH = new ArrayList<>();
//        missH = new ArrayList<>();
//        for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
//            for(byte cl = 0; cl < Input.nrGroups; cl++){
//                int sum = 0;
//                for(byte prof = 0; prof < Input.nrProf; prof++){
//                    if(c.timetable[prof][hour] == cl) sum++;
//                }
//                if(sum == 0 && hour % Input.nrHours > 1 && hour % Input.nrHours < 4 ) {
//                    missH.add(cl);
//                    missH.add(hour);
//                }
//                if(sum > 1) {
//                    overH.add(cl);
//                    overH.add(hour);
//                }
//            }
//        }
        // Step 3 : transitive path
        sem = true;
        while (sem) {
            List<Byte> overH = new ArrayList<>();
            List<Byte> missH = new ArrayList<>();
            for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
                for(byte cl = 0; cl < Input.nrGroups; cl++){
                    int sum = 0;
                    for(byte prof = 0; prof < Input.nrProf; prof++){
                        if(c.timetable[prof][hour] == cl) sum++;
                    }
                    if(sum == 0  ) {
                        missH.add(cl);
                        missH.add(hour);
                    }
                    if(sum > 1) {
                        overH.add(cl);
                        overH.add(hour);
                    }
                }
            }
            sem = false;
            for (byte row = 0; row < Input.nrProf; row++) {
                for (byte row2 = 0; row2 < Input.nrProf; row2++){
                    for (byte col = 0; col < Input.nrHours * 5; col++) {
                        for (byte col2 = 0; col2 < Input.nrHours * 5; col2++) {
                            for (byte col3 = 0; col3 < Input.nrHours * 5; col3++) {
                                boolean sem1 = false;
                                boolean sem2 = false;
                                boolean sem3 = false;
                                boolean sem4 = false;
                                boolean sem5 = false;
                                boolean sem6 = false;

                                if(c.timetable[row][col] != -1 && c.timetable[row][col3] != -1 && c.timetable[row2][col] != -1) {
                                    if (c.timetable[row][col] == c.timetable[row2][col2] && c.timetable[row2][col] != c.timetable[row][col] && c.timetable[row][col] != c.timetable[row][col3] && c.timetable[row2][col] != c.timetable[row][col3]) {
                                        for (int i = 0; i < overH.size(); i += 2) {
                                            if (overH.get(i) == c.timetable[row2][col] && overH.get(i + 1) == col) {
                                                sem1 = true;
                                                overHind1 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < overH.size(); i += 2) {
                                            if (overH.get(i) == c.timetable[row][col] && overH.get(i + 1) == col2) {
                                                sem2 = true;
                                                overHind2 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < overH.size(); i += 2) {
                                            if (overH.get(i) == c.timetable[row][col3] && overH.get(i + 1) == col3) {
                                                sem3 = true;
                                                overHind3 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < missH.size(); i += 2) {
                                            if (missH.get(i) == c.timetable[row][col3] && missH.get(i + 1) == col) {
                                                sem4 = true;
                                                missHind1 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < missH.size(); i += 2) {
                                            if (missH.get(i) == c.timetable[row2][col] && missH.get(i + 1) == col2) {
                                                sem5 = true;
                                                missHind2 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < missH.size(); i += 2) {
                                            if (missH.get(i) == c.timetable[row][col] && missH.get(i + 1) == col3) {
                                                sem6 = true;
                                                missHind3 = i;
                                                break;
                                            }
                                        }
                                        if (sem1 && sem2 && sem3 && sem4 && sem5 && sem6) {
                                            sem = true;
                                            byte aux = c.timetable[row][col];
                                            c.timetable[row][col] = c.timetable[row][col3];
                                            c.timetable[row][col3] = aux;
                                            aux = c.timetable[row2][col];
                                            c.timetable[row2][col] = c.timetable[row2][col2];
                                            c.timetable[row2][col2] = aux;
                                            indxO = new ArrayList<>();
                                            indxM = new ArrayList<>();
                                            indxO.add(overHind1);
                                            indxO.add(overHind2);
                                            indxO.add(overHind3);
                                            indxM.add(missHind1);
                                            indxM.add(missHind2);
                                            indxM.add(missHind3);
                                            Collections.sort(indxM);
                                            Collections.sort(indxO);
                                            overH.remove(indxO.get(0));
                                            overH.remove(indxO.get(0));
                                            overH.remove(indxO.get(1) - 2);
                                            overH.remove(indxO.get(1) - 2);
                                            overH.remove(indxO.get(2) - 4);
                                            overH.remove(indxO.get(2) - 4);
                                            missH.remove(indxM.get(0));
                                            missH.remove(indxM.get(0));
                                            missH.remove(indxM.get(1) - 2);
                                            missH.remove(indxM.get(1) - 2);
                                            missH.remove(indxM.get(2) - 4);
                                            missH.remove(indxM.get(2) - 4);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        overH = new ArrayList<>();
//        missH = new ArrayList<>();
//        for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
//            for(byte cl = 0; cl < Input.nrGroups; cl++){
//                int sum = 0;
//                for(byte prof = 0; prof < Input.nrProf; prof++){
//                    if(c.timetable[prof][hour] == cl) sum++;
//                }
//                if(sum == 0 && hour % Input.nrHours > 1 && hour % Input.nrHours < 4) {
//                    missH.add(cl);
//                    missH.add(hour);
//                }
//                if(sum > 1) {
//                    overH.add(cl);
//                    overH.add(hour);
//                }
//            }
//        }
        // Step 4 : transitive path with a free hour
        sem = true;
        while (sem) {
            List<Byte> overH = new ArrayList<>();
            List<Byte> missH = new ArrayList<>();
            for(byte hour = 0; hour < Input.nrHours * 5; hour ++){
                for(byte cl = 0; cl < Input.nrGroups; cl++){
                    int sum = 0;
                    for(byte prof = 0; prof < Input.nrProf; prof++){
                        if(c.timetable[prof][hour] == cl) sum++;
                    }
                    if(sum == 0 ) {
                        missH.add(cl);
                        missH.add(hour);
                    }
                    if(sum > 1) {
                        overH.add(cl);
                        overH.add(hour);
                    }
                }
            }
            sem = false;
            for (byte row = 0; row < Input.nrProf; row++) {
                for (byte row2 = 0; row2 < Input.nrProf; row2++){
                    for (byte col = 0; col < Input.nrHours * 5; col++) {
                        for (byte col2 = 0; col2 < Input.nrHours * 5; col2++) {
                            for (byte col3 = 0; col3 < Input.nrHours * 5; col3++) {
                                boolean sem1 = false;
                                boolean sem2 = false;
                                boolean sem5 = false;
                                boolean sem6 = false;

                                if(c.timetable[row][col] != -1 && c.timetable[row2][col] != -1) {
                                    if (c.timetable[row][col] == c.timetable[row2][col2] && c.timetable[row2][col] != c.timetable[row][col] && c.timetable[row][col3] == -1) {
                                        for (int i = 0; i < overH.size(); i += 2) {
                                            if (overH.get(i) == c.timetable[row2][col] && overH.get(i + 1) == col) {
                                                sem1 = true;
                                                overHind1 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < overH.size(); i += 2) {
                                            if (overH.get(i) == c.timetable[row][col] && overH.get(i + 1) == col2) {
                                                sem2 = true;
                                                overHind2 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < missH.size(); i += 2) {
                                            if (missH.get(i) == c.timetable[row2][col] && missH.get(i + 1) == col2) {
                                                sem5 = true;
                                                missHind1 = i;
                                                break;
                                            }
                                        }
                                        for (int i = 0; i < missH.size(); i += 2) {
                                            if (missH.get(i) == c.timetable[row][col] && missH.get(i + 1) == col3) {
                                                sem6 = true;
                                                missHind2 = i;
                                                break;
                                            }
                                        }
                                        if (sem1 && sem2 && sem5 && sem6) {
                                            sem = true;
                                            byte aux = c.timetable[row][col];
                                            c.timetable[row][col] = c.timetable[row][col3];
                                            c.timetable[row][col3] = aux;
                                            aux = c.timetable[row2][col];
                                            c.timetable[row2][col] = c.timetable[row2][col2];
                                            c.timetable[row2][col2] = aux;
                                            if (overHind1 < overHind2) {
                                                overH.remove(overHind1);
                                                overH.remove(overHind1);
                                                overH.remove(overHind2 - 2);
                                                overH.remove(overHind2 - 2);
                                            } else {
                                                overH.remove(overHind2);
                                                overH.remove(overHind2);
                                                overH.remove(overHind1 - 2);
                                                overH.remove(overHind1 - 2);
                                            }
                                            if (missHind1 < missHind2) {
                                                missH.remove(missHind1);
                                                missH.remove(missHind1);
                                                missH.remove(missHind2 - 2);
                                                missH.remove(missHind2 - 2);
                                            } else {
                                                missH.remove(missHind2);
                                                missH.remove(missHind2);
                                                missH.remove(missHind1 - 2);
                                                missH.remove(missHind1 - 2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return c;
    }

    // local search
    public Chromosome localSearch(Chromosome c){
        Chromosome d = new Chromosome();
        d.copyTimetable(c.timetable);
        // Stage 1 : remove infeasibilities without worsening 2nd level costs
        for(int i = 0; i < GeneticAlgorithm.neighbourhood; i++){
            d.totalFitness();
            c = this.filter(c);
            c.totalFitness();
            if(c.didacticDissatisfaction() + c.organizDiss() < d.didacticDissatisfaction() + d.organizDiss()){
                d = new Chromosome();
                d.copyTimetable(c.timetable);
            }
        }
        // Stage 2 : search for better solutions in neighbourhood
        for(int i = 0; i < GeneticAlgorithm.neighbourhood; i++){
            //c = this.dayMutation(c);
            c = this.mutateOfOrderOne(c);
            c.totalFitness();
            d.totalFitness();
            if(c.cost < d.cost) {
                d = new Chromosome();
                d.copyTimetable(c.timetable);
            }
        }
        return d;
    }

    // check if current population has a perfect individual
    public boolean isDone(Population population){
        if(population.getFittest().cost == 0) return true;
        return false;
    }

    // evaluate current population
    public void evaluatePopulation(){
        this.totalFitness = 0;
        this.population[0].totalFitness();
        this.population[GeneticAlgorithm.populationSize - 1].totalFitness();
        minOF = this.population[GeneticAlgorithm.populationSize - 1].cost;
        maxOF = this.population[0].cost;
        for(Chromosome c : this.population){
            c.cost = c.totalFitness();
            if(c.cost < minOF) minOF = c.cost;
            if(c.cost > maxOF) maxOF = c.cost;
        }
        for(Chromosome c : this.population){
            c.cost = c.totalFitness();
            c.fitness = 1.0 * c.cost / (minOF - maxOF) - 1.0 * maxOF / (minOF - maxOF);
            this.totalFitness += c.fitness;
        }
        for(Chromosome c : this.population){
            c.cost = c.totalFitness();
            c.fitness = 1.0 * c.cost / (minOF - maxOF) - 1.0 * maxOF / (minOF - maxOF);
            c.fitness = c.fitness / this.totalFitness;
        }
    }

}
