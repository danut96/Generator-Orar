package sample;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class Read implements AutoCloseable{

    Input input;
    // read input from file
    public Read(){
        try(FileReader fin = new FileReader("Input.txt")){
            Scanner scan = new Scanner(fin);
            String[] line;
            line = scan.nextLine().split(" ");
            input = new Input(Integer.parseInt(line[0].trim()),Integer.parseInt(line[1].trim()),Integer.parseInt(line[2].trim()));
            line = scan.nextLine().split(" ");
            Input.groups = new HashMap<>();
            for(byte i = 0; i < Input.nrGroups; i++){
                Group g = new Group(i, line[i]);
                // add new group
                Input.groups.put(i, g);
            }
            byte i = 0;
            Input.profs = new HashMap<>();
            while(scan.hasNext()){
                line = scan.nextLine().split(",");
                Professor p = new Professor(i, line[0].trim(), new Disciplina(line[1].trim()));
                // add new proffessor
                Input.profs.put(i, p);
                String groups[] = line[2].trim().split(" ");
                for(byte j = 0; j < groups.length - 1; j+=2){
                    Group g = new Group(1, groups[j].trim());
                    for(byte k = 0; k < Input.nrGroups; k++){
                        if(Input.groups.get(k).equals(g)) {
                            // define proffessor - group matrix
                            Input.classes[i][k] = Byte.parseByte(groups[j + 1].trim());
                            break;
                        }
                    }
                }
                i++;
            }
            input.proffs();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void close(){
        System.out.println("File closed!");
    }
}
