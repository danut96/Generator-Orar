package sample;

import java.util.Random;

public class GeneticAlgorithm {
    public static int populationSize = 100; // trebuie sa fie par
    public static double mutationRateK = 0.01;
    public static double mutationRateD = 0.01;
    public static double mutationRate1 = 0.3;
    public static double crossOverRate = 0.8;
    public static int tournamentSize = 15;
    public static int neighbourhood = 5;
    public static int weightInfes = 200;
    public static int weightManag = 200;
    public static int weightOrg = 10;
    public static int weightProf = 1;
    public static int elitism = 3;
    public static int maxInfes = 5;
    private boolean localSearch = true;
    public void GA(){
        Population population = new Population(populationSize);
        population.evaluatePopulation();
        int generations = 1;

        //begin genetic algorithm
        while(!population.isDone(population) && generations < 1000){
            System.out.println("G"+ generations+ " best fitness (lowest cost) "+ population.getFittest().cost+ " nr. infes " + population.getFittest().nrInfes);
            population = population.reproduction();
            //population.evaluatePopulation();
            //population = population.unifCrossOver();
            population = population.crossOver();
            //population.evaluatePopulation();
            //System.out.println("After cross-over: " + population.getFittest().cost+ " nr. infes: " + population.getFittest().nrInfes);
            for(Chromosome c : population.population){
                //System.out.println(c.fitness + " " + c.cost);
//                System.out.println(c.nrInfes);
                if(mutationRateK > Math.random()){
                    //System.out.println("AICI-K");
                    //if(c == population.getFittest()) System.out.println("DA");
                    int k = new Random().nextInt(Input.nrHours * 5 / 3) + 2;
                    c = population.mutateOfOrderK(c, k);
                }
                if(mutationRate1 > Math.random()){
                    //System.out.println("AICI-1");
                    //if(c == population.getFittest()) System.out.println("DA");
                    c = population.mutateOfOrderOne(c);
                }
//                if(mutationRateD > Math.random()) {
//                    c = population.dayMutation(c);
//                }
//                c.totalFitness();
//                System.out.println(c.fitness);
                    // filter : nefezabil => fezabil (fara conflicte de timp)
//                c.totalFitness();
//                System.out.println(c.nrInfes);
               // if(localSearch) c = population.localSearch(c);
                    c.totalFitness();
                if(c.nrInfes > 3) c = population.filter(c);
//                c.totalFitness();
//                System.out.println(c.nrInfes);
//                System.exit(0);
            }
            //System.out.println();
            population.evaluatePopulation();
            //System.out.println("After mutation: " + population.getFittest().cost+ " nr. infes: " + population.getFittest().nrInfes);
            //for(Chromosome c : population.population){
                //System.out.println(c.fitness + " " + c.cost);
                //c.totalFitness();
                //population.filter(c);
            //}
            //if(generations == 3)System.exit(0);
            //population.evaluatePopulation();
            //System.out.println("After filter: " + population.getFittest().cost + " nr. infes: " + population.getFittest().nrInfes);
            generations++;
        }
        System.out.print("GATA");
        Controller.timetable = population.getFittest().timetable;
        new Write().writeTimetable(population.getFittest().timetable);
    }
}
