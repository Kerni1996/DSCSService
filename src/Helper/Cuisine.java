package Helper;

public class Cuisine {
    private String name;
    private int ID;

    public Cuisine(int id, String name){
        this.ID = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return name;
    }
}
