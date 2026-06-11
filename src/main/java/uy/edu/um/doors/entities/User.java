package uy.edu.um.doors.entities;

import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.doors.ProcessManagerImpl;
import uy.edu.um.tad.list.MyList;

public class User {

    private int uid;
    private String alias;
    private String type;

    public User() {
    }

    public User(int uid, String alias, String type) {
        this.uid = uid;
        this.alias = alias;
        this.type = type;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    }



