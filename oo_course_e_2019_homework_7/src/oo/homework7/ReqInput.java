package oo.homework7;

import com.oocourse.TimableOutput;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;

class ReqInput {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        Sheduler sch = new Sheduler();
        sch.startElev();
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                break;
            } else {
                Request newReq = new Request(request);
                sch.putRequest(newReq);
            }
        }
        sch.stopElev();
        elevatorInput.close();
    }

}
