package sample;

public class Disciplina {
    String name;
    public Disciplina(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Disciplina) {
            Disciplina d = (Disciplina) o;
            return this.name.equals(d.name);
        }
        return false;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
