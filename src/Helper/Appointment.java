package Helper;

import java.sql.Date;

public class Appointment {
    private Date date;
    private int PersonID;

    public Appointment(Date date, int personID) {
        this.date = date;
        PersonID = personID;
    }

    public Date getDate() {
        return date;
    }

    public int getPersonID() {
        return PersonID;
    }
}
