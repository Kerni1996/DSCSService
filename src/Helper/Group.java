package Helper;

public class Group {
    private int ID;
    private int participants;

    public Group(int ID){
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return this.ID+"";
    }
}
