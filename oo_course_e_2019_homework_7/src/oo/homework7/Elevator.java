package oo.homework7;

import com.oocourse.TimableOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Elevator extends Thread {
    private int operateTime;
    private int maxLoad;
    private int currentLoad;
    private Request fakeReq;
    private ArrayList<Request> reqQue;
    private ArrayList<Request> upQue;
    private ArrayList<Request> downQue;
    private ArrayList<Request> returnQue;
    private ArrayList<Request> recordDelet;
    private final Sheduler sch;
    private int direction;
    private int elevFloor;
    private int[] stopFloor;
    private String threadName;
    private boolean stop;
    private int sleepTime;

    public Elevator(String x, int y, int z, Request fake, Sheduler s,
                    int[] list) {
        threadName = x;
        operateTime = y;
        maxLoad = z;
        fakeReq = fake;
        reqQue = new ArrayList<>();
        upQue = new ArrayList<>();
        downQue = new ArrayList<>();
        returnQue = new ArrayList<>();
        recordDelet = new ArrayList<>();
        stopFloor = list;
        sch = s;
        direction = 1;
        elevFloor = 1;
        currentLoad = 0;
        stop = false;
        sleepTime = 1000;
    }

    private static Comparator<Request> comdown = new Comparator<Request>() {
        @Override
        public int compare(Request o1, Request o2) {
            if (o2.getCurFloor() - o1.getCurFloor() > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    private static Comparator<Request> comup = new Comparator<Request>() {
        @Override
        public int compare(Request o1, Request o2) {
            if (o1.getCurFloor() - o2.getCurFloor() > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    public void run() {
        while (true) {
            if (reqQue.isEmpty()) {
                Request tmpReq = sch.takeRequest();
                if (!tmpReq.equals(fakeReq)) {
                    reqQue.add(tmpReq);
                    currentLoad = currentLoad + 1;
                    operate();
                } else if (stop) {
                    break;
                } else {
                    ThreadSleep(sleepTime);
                }
            }
        }
    }

    public void stopThread() {
        stop = true;
    }

    private void operate() {
        while (!reqQue.isEmpty()) {
            if (getSamedirectin()) {
                while (!(upQue.isEmpty() && downQue.isEmpty())) {
                    getSamedirectoff();
                    updateQue();
                    if (move()) {
                        updateDirect();
                    }
                }
            }
        }
        if (!returnQue.isEmpty()) {
            sch.addreSchedule(returnQue);
            returnQue.clear();
        }
    }

    private void updateDirect() {
        if (direction == 1) {
            direction = -1;
        } else if (direction == -1) {
            direction = 1;
        }
    }

    private boolean move() {
        ArrayList<Request> curQue;
        int length;
        if (direction == 1) {
            curQue = upQue;
        } else {
            curQue = downQue;
        }
        length = curQue.size();
        for (int i = 0; i < length; i++) {
            Request request = curQue.get(i);
            if (request.getAreadyPrint() == 1) {
                continue;
            }
            if (request.getStatus() == 1) {
                if (!arrivePrint(request)) {
                    return false;
                } else {
                    i = i + printCurrentFloor(curQue, i);
                }
            } else {
                if (!arrivePrint(request)) {
                    return false;
                } else {
                    i = i + printCurrentFloor(curQue, i);
                }
            }
        }
        updateUpDown();
        return true;
    }

    private int printCurrentFloor(ArrayList<Request> curQue, int idex) {
        int length = curQue.size();
        int i;
        int count = 0;
        FloorOpen(elevFloor);
        for (i = idex; i < length; i++) {
            Request tmpReq = curQue.get(i);
            if (tmpReq.getCurFloor() == elevFloor
                    && tmpReq.getAreadyPrint() == 0) {
                if (tmpReq.getStatus() == 1) {
                    PeopelInput(tmpReq.getReq().getPersonId(), elevFloor);
                    tmpReq.setAreadyPrint(1);
                } else {
                    PeopelOutput(tmpReq.getReq().getPersonId(), elevFloor);
                    tmpReq.setAreadyPrint(1);
                    currentLoad = currentLoad - 1;
                    recordDelet.add(tmpReq);
                }
                count = count + 1;
            } else {
                continue;
            }
        }
        ThreadSleep(400);
        FloorClose(elevFloor);
        return count - 1;
    }

    private void updateUpDown() {
        ArrayList<Request> tmpListup = new ArrayList<>();
        ArrayList<Request> tmpListdown = new ArrayList<>();
        for (int i = 0; i < recordDelet.size(); i++) {
            for (int j = 0; j < upQue.size(); j++) {
                Request tmpReq = upQue.get(j);
                if (tmpReq.getReq().equals(recordDelet.get(i).getReq())) {
                    tmpListup.add(tmpReq);
                }
            }
            for (int j = 0; j < downQue.size(); j++) {
                Request tmpReq = downQue.get(j);
                if (tmpReq.getReq().equals(recordDelet.get(i).getReq())) {
                    tmpListdown.add(tmpReq);
                }
            }
        }
        upQue.removeAll(tmpListup);
        downQue.removeAll(tmpListdown);
        recordDelet.clear();
    }

    private boolean arrivePrint(Request request) {
        int targetFloor = request.getCurFloor();
        if (direction == 1) {
            while (elevFloor <= targetFloor) {
                if (loadNewpeople()) {
                    return false;
                }
                if (elevFloor == targetFloor) {
                    return true;
                } else {
                    if (elevFloor == -1) {
                        elevFloor = elevFloor + 1;
                    }
                    elevFloor = elevFloor + 1;
                    ThreadSleep(operateTime);
                    TimableOutput.println(String.format("ARRIVE-%d-%s",
                            elevFloor, this.getName()));
                }
            }
        } else {
            while (elevFloor >= targetFloor) {
                if (loadNewpeople()) {
                    return false;
                }
                if (elevFloor == targetFloor) {
                    return true;
                } else {
                    if (elevFloor == 1) {
                        elevFloor = elevFloor - 1;
                    }
                    elevFloor = elevFloor - 1;
                    ThreadSleep(operateTime);
                    TimableOutput.println(String.format("ARRIVE-%d-%s",
                            elevFloor, this.getName()));
                }
            }
        }
        return true;
    }

    private boolean loadNewpeople() {
        if (judgeStop() && currentLoad < maxLoad) {
            Request additionReq = sch.takeRequest();
            if (!additionReq.equals(fakeReq)) {
                currentLoad = currentLoad + 1;
                reqQue.add(additionReq);
                boolean flag = getSamedirectin();
                getSamedirectoff();
                updateQue();
                return true;
            }
        }
        return false;
    }

    private boolean judgeStop() {
        for (int i : stopFloor) {
            if (i == elevFloor) {
                return true;
            }
        }
        return false;
    }

    private void PeopelInput(int id, int floor) {
        TimableOutput.println(String.format("IN-%d-%d-%s", id, floor,
                threadName));
    }

    private void PeopelOutput(int id, int floor) {
        TimableOutput.println(String.format("OUT-%d-%d-%s", id, floor,
                threadName));
    }

    private void FloorOpen(int floor) {
        TimableOutput.println(String.format("OPEN-%d-%s", floor,
                threadName));
    }

    private void FloorClose(int floor) {
        TimableOutput.println(String.format("CLOSE-%d-%s", floor,
                threadName));
    }

    private void ThreadSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateQue() {
        if (direction == 1) {
            upQue.sort(comup);
        } else {
            downQue.sort(comdown);
        }
    }

    private void getFirstpeople(Request firstReq) {
        int fromFloor;
        firstReq.setStatus(1);
        firstReq.setStatus(1);
        if (firstReq.getChangeElv() == 2) {
            firstReq.setCurFloor(firstReq.getMidFloor());
            fromFloor = firstReq.getMidFloor();
        } else {
            firstReq.setCurFloor(firstReq.getReq().getFromFloor());
            fromFloor = firstReq.getReq().getFromFloor();
        }
        if (fromFloor > elevFloor) {
            upQue.add(firstReq);
            direction = 1;
        } else if (fromFloor < elevFloor) {
            downQue.add(firstReq);
            direction = -1;
        } else {
            if (fromFloor < firstReq.getReq().getToFloor()) {
                direction = 1;
                upQue.add(firstReq);
            } else {
                direction = -1;
                downQue.add(firstReq);
            }
        }
    }

    private void getSamedirectoff() {
        ArrayList<Request> tmpOff = new ArrayList<>();
        for (int i = 0; i < upQue.size(); i++) {
            Request tmpReq = upQue.get(i);
            if (judgeWhetheroff(tmpReq)) {
                Request outReq = getOffreq(tmpReq);
                tmpOff.add(outReq);
            }
        }
        for (int i = 0; i < downQue.size(); i++) {
            Request tmpReq = downQue.get(i);
            if (judgeWhetheroff(tmpReq)) {
                Request outReq = getOffreq(tmpReq);
                tmpOff.add(outReq);
            }
        }
        if (direction == 1) {
            upQue.addAll(tmpOff);
        } else {
            downQue.addAll(tmpOff);
        }
        tmpOff.clear();
    }

    private boolean getSamedirectin() {
        Iterator<Request> it = reqQue.iterator();
        ArrayList<Request> tmpIn = new ArrayList<>();
        if (upQue.isEmpty() && downQue.isEmpty()) {
            Request firstReq = it.next();
            getFirstpeople(firstReq);
            it.remove();
        }
        while (it.hasNext()) {
            Request tmpreq = it.next();
            if (judgeWhetherIn(tmpreq)) {
                tmpreq.setStatus(1);
                if (tmpreq.getChangeElv() == 2) {
                    tmpreq.setCurFloor(tmpreq.getMidFloor());
                } else {
                    tmpreq.setCurFloor(tmpreq.getReq().getFromFloor());
                }
                tmpIn.add(tmpreq);
                it.remove();
            }
        }

        if (direction == 1) {
            upQue.addAll(tmpIn);
        } else {
            downQue.addAll(tmpIn);
        }
        tmpIn.clear();
        return true;
    }

    private Request getOffreq(Request request) {
        Request tmpReq = new Request(request.getReq());
        tmpReq.setStatus(2);
        if (request.getChangeElv() == 0) {
            tmpReq.setCurFloor(request.getReq().getToFloor());
        } else if (request.getChangeElv() == 1) {
            tmpReq.setCurFloor(request.getMidFloor());
            returnQue.add(request);
        } else {
            tmpReq.setCurFloor(request.getReq().getToFloor());
        }
        return tmpReq;
    }

    private boolean noaddOffque(Request request) {
        for (int i = 0; i < upQue.size(); i++) {
            Request tmpReq = upQue.get(i);
            if (request.getReq().equals(tmpReq.getReq())
                && tmpReq.getStatus() == 2) {
                return false;
            }
        }
        for (int i = 0; i < downQue.size(); i++) {
            Request tmpReq = downQue.get(i);
            if (request.getReq().equals(tmpReq.getReq())
                    && tmpReq.getStatus() == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean judgeWhetheroff(Request request) {
        int toFloor = request.getReq().getToFloor();
        int fromFloor = request.getReq().getFromFloor();
        if (request.getChangeElv() == 1) {
            toFloor = request.getMidFloor();
        }
        if (request.getChangeElv() == 2) {
            fromFloor = request.getMidFloor();
        }

        if (noaddOffque(request) && request.getStatus() == 1
        && ((direction == 1 && fromFloor < toFloor)
        || (direction == -1 && fromFloor > toFloor))) {
            return true;
        }
        return false;
    }

    private boolean judgeWhetherIn(Request request) {
        int fromFloor = request.getReq().getFromFloor();
        if (request.getChangeElv() == 2) {
            fromFloor = request.getMidFloor();
        }
        if ((direction == 1 && fromFloor >= elevFloor)
                || (direction == -1 && fromFloor <= elevFloor)) {
            return true;
        }
        return false;
    }
}
