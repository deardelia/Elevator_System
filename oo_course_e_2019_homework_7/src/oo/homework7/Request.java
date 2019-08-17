package oo.homework7;

import com.oocourse.elevator3.PersonRequest;

public class Request {
    private PersonRequest req;
    private int curFloor = 0;
    private int status = 0;
    private int changeElv = 0;
    private int midFloor = 0;
    private int areadyPrint = 0;

    Request(PersonRequest r) {
        req = r;
    }

    public int getReqDirect() {
        if (changeElv == 0) {
            if (req.getFromFloor() < req.getToFloor()) {
                return 1;
            } else {
                return -1;
            }
        } else if (changeElv == 1) {
            if (req.getFromFloor() <= midFloor) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (midFloor < req.getToFloor()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public int getAreadyPrint() {
        return areadyPrint;
    }

    public void setAreadyPrint(int areadyPrint) {
        this.areadyPrint = areadyPrint;
    }

    public int getMidFloor() {
        return midFloor;
    }

    public void setMidFloor(int midFloor) {
        this.midFloor = midFloor;
    }

    public int getChangeElv() {
        return changeElv;
    }

    public void setChangeElv(int changeElv) {
        this.changeElv = changeElv;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCurFloor() {
        return curFloor;
    }

    public PersonRequest getReq() {
        return req;
    }

    public void setCurFloor(int curFloor) {
        this.curFloor = curFloor;
    }
}
