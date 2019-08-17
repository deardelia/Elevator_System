package oo.homework7;

import java.util.ArrayList;

public class Sheduler {
    private final ArrayList<Request> requestQueue;
    private final ArrayList<Request> returnQue;
    private final Elevator[] threadPool;
    private final Request fakeReq;
    private final int elevNum = 3;
    private int[] stopFloor1;
    private int[] stopFloor2;
    private int[] stopFloor3;
    private boolean stop;
    private String name1 = "A";
    private String name2 = "B";
    private String name3 = "C";
    private int returnCount = 0;

    public Sheduler() {
        requestQueue = new ArrayList<>();
        returnQue = new ArrayList<>();
        threadPool = new Elevator[elevNum];
        fakeReq = new Request(null);
        stopFloor1 = new int[] { -3, -2, -1, 1, 15, 16, 17, 18, 19, 20 };
        stopFloor2 = new int[] { -2, -1, 1, 2, 4, 5, 6, 7, 8, 9,
                10, 11, 12, 13, 14, 15 };
        stopFloor3 = new int[] { 1, 3, 5, 7, 9, 11, 13, 15 };
        threadPool[0] = new Elevator(name1, 400, 6, fakeReq,
                this, stopFloor1);
        threadPool[0].setName(name1);
        threadPool[1] = new Elevator(name2, 500, 8, fakeReq,
                this, stopFloor2);
        threadPool[1].setName(name2);
        threadPool[2] = new Elevator(name3, 600, 7, fakeReq,
                this, stopFloor3);
        threadPool[2].setName(name3);
        stop = false;
    }

    public void startElev() {
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i].start();
        }
    }

    public void stopElev() {
        stop = true;
    }

    public synchronized void putRequest(Request request) {
        requestQueue.add(request);
        notifyAll();
    }

    public synchronized Request takeRequest() {
        if (!(requestQueue.isEmpty() && returnQue.isEmpty())) {
            if (!requestQueue.isEmpty()) {
                Request request = requestQueue.get(0);
                if (judgeGive(Thread.currentThread().getName(), request)) {
                    if (judgeChangeElv(Thread.currentThread().getName(),
                            request)) {
                        returnCount = returnCount + 1;
                        request.setChangeElv(1);
                        setMidfloor(Thread.currentThread().getName(), request);
                    }
                    requestQueue.remove(request);
                    notifyAll();
                    return request;
                }
            } else {
                Request request = returnQue.get(0);
                if (judgeGive(Thread.currentThread().getName(), request)) {
                    request.setChangeElv(2);
                    returnQue.remove(request);
                    returnCount = returnCount - 1;
                    notifyAll();
                    return request;
                }
            }
        } else if (stop) {
            if (returnCount == 0) {
                for (int i = 0; i < threadPool.length; i++) {
                    threadPool[i].stopThread();
                }
            }
        }
        notifyAll();
        return fakeReq;
    }

    public synchronized void addreSchedule(ArrayList<Request> tmpReq) {
        for (int i = 0; i < tmpReq.size(); i++) {
            Request returnReq = new Request(tmpReq.get(i).getReq());
            returnReq.setMidFloor(tmpReq.get(i).getMidFloor());
            returnReq.setChangeElv(1);
            returnQue.add(returnReq);
        }
        notifyAll();
    }

    private boolean judgeGive(String id, Request request) {
        int[] tmpList;
        int fromFloor = request.getReq().getFromFloor();

        if (request.getChangeElv() == 1) {
            fromFloor = request.getMidFloor();
        }
        if (id.equals(name1)) {
            if (!noElvChange(id, request) && (noElvChange("B", request)
                    || noElvChange("C", request))) {
                return false;
            }
            tmpList = stopFloor1;
        } else if (id.equals(name2)) {
            if (!noElvChange(id, request) && (noElvChange("A", request)
                    || noElvChange("C", request))) {
                return false;
            }
            tmpList = stopFloor2;
        } else {
            if (!noElvChange(id, request) && (noElvChange("A", request)
                    || noElvChange("B", request))) {
                return false;
            }
            tmpList = stopFloor3;
        }

        for (int i : tmpList) {
            if (i == fromFloor) {
                return true;
            }
        }
        return false;
    }

    private boolean noElvChange(String id, Request request) {
        int[] tmpList;
        boolean from = false;
        boolean to = false;
        int fromFloor = request.getReq().getFromFloor();
        int targetFloor = request.getReq().getToFloor();
        if (id.equals(name1)) {
            tmpList = stopFloor1;
        } else if (id.equals(name2)) {
            tmpList = stopFloor2;
        } else {
            tmpList = stopFloor3;
        }

        if (request.getChangeElv() == 1) {
            fromFloor = request.getMidFloor();
        }

        for (int i : tmpList) {
            if (i == fromFloor) {
                from = true;
            } else if (i == targetFloor) {
                to = true;
            }
        }
        if (from && to) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<Integer> findCommonList(String id, Request request) {
        int[] tmp1;
        int[] tmp2;
        int[] firstList;
        int[] secondList;
        ArrayList<Integer> commonList = new ArrayList<>();
        int stopFloor = request.getReq().getToFloor();
        boolean flag1 = false;
        boolean flag2 = false;
        if (id.equals(name1)) {
            firstList = stopFloor1;
            tmp1 = stopFloor2;
            tmp2 = stopFloor3;
        } else if (id.equals(name2)) {
            firstList = stopFloor2;
            tmp1 = stopFloor1;
            tmp2 = stopFloor3;
        } else {
            firstList = stopFloor3;
            tmp1 = stopFloor1;
            tmp2 = stopFloor2;
        }
        for (int i = 0; i < tmp1.length; i++) {
            if (tmp1[i] == stopFloor) {
                flag1 = true;
            }
        }
        for (int i = 0; i < tmp2.length; i++) {
            if (tmp2[i] == stopFloor) {
                flag2 = true;
            }
        }
        if (flag1 && flag2) {
            if (returnCount % 2 == 0) {
                secondList = tmp1;
            } else {
                secondList = tmp2;
            }
        } else if (flag1) {
            secondList = tmp1;
        } else {
            secondList = tmp2;
        }
        for (int i = 0; i < firstList.length; i++) {
            for (int j = 0; j < secondList.length; j++) {
                if (firstList[i] == secondList[j]) {
                    commonList.add(firstList[i]);
                }
            }
        }
        return commonList;
    }

    private void setMidfloor(String id, Request request) {
        ArrayList<Integer> commonList = findCommonList(id, request);
        int range = 25;
        int mid = 1;

        for (Integer i : commonList) {
            if (Math.abs(i - request.getReq().getToFloor()) < range) {
                range = Math.abs(i - request.getReq().getToFloor());
                mid = i;
            }
        }
        request.setMidFloor(mid);
    }

    private boolean judgeChangeElv(String id, Request request) {
        int[] tmpList;
        if (id.equals(name1)) {
            tmpList = stopFloor1;
        } else if (id.equals(name2)) {
            tmpList = stopFloor2;
        } else {
            tmpList = stopFloor3;
        }

        for (int i : tmpList) {
            if (i == request.getReq().getToFloor()) {
                return false;
            }
        }
        return true;
    }
}
