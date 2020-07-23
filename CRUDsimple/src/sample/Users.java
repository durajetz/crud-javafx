package sample;

import java.sql.Date;

public class Users {
    public int Id;
    public String Emri;
    public int Mosha;
    public Date Datakrijuar;

    public Users(int id, String emri, int mosha, Date datakrijuar) {
        this.Id = id;
        this.Emri = emri;
        this.Mosha = mosha;
        this.Datakrijuar = datakrijuar;
    }


    public int getId() {
        return Id;
    }

    public String getEmri() {
        return Emri;
    }

    public int getMosha() {
        return Mosha;
    }

    public Date getDatakrijuar() {
        return Datakrijuar;
    }
}
