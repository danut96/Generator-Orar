package sample;

public class Group {
    int id;
    String name;
    public Group(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Group){
            Group g = (Group) o;
            return this.name.equals(g.name);
        }
        return false;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
