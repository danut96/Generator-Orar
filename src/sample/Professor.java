package sample;

public class Professor {
    int id;
    String name;
    Disciplina d;
    byte[] freeDays;

    public Professor(int id, String name, Disciplina d){
        this.name = name;
        this.id = id;
        this.d = d;
    }

    // if proffessor specifies some free days
    public Professor(int id, String name, Disciplina d, byte... days){
        this.name = name;
        this.id = id;
        this.d = d;
        this.freeDays = days;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Professor) {
            Professor p = (Professor) o;
            return this.id == p.id;
        }
        return false;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
