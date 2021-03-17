package Helper;

public class Group {
    private int ID;
    private int participants;

    public Group(int ID){
        this.ID = ID;
    }

    @Override
    public String toString() {
        return this.ID+"";
    }
}
