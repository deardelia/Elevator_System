# INPUT

## Introduction



```java
class PersonRequest {
    /**
    * 省略部分方法，只显示对外接口
    **/
    
    /**
     * 获取出发楼层
     *
     * @return 出发楼层
     */
    public int getFromFloor() {
        return fromFloor;
    }

    /**
     * 获取目标楼层
     *
     * @return 目标楼层
     */
    public int getToFloor() {
        return toFloor;
    }

    /**
     * 获取人员id
     *
     * @return 人员id
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * 转为字符串形式
     *
     * @return 字符串形式
     */
    @Override
    public String toString() {
        return String.format("%d-FROM-%d-TO-%d", personId, fromFloor, toFloor);
    }

    /**
     * 获取哈希值
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{
                this.personId, this.fromFloor, this.toFloor});
    }

    /**
     * 判断对象是否相等
     *
     * @param obj 对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PersonRequest) {
            return (((PersonRequest) obj).fromFloor == this.fromFloor)
                    && (((PersonRequest) obj).toFloor == this.toFloor)
                    && (((PersonRequest) obj).personId == this.personId);
        } else {
            return false;
        }
    }
}
```

## Demo

Exmaple

```java
package com.oocourse.elevator3;

class TestMain {
    public static void main(String[] args) throws Exception {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {  
                break;
            } else {  
                // a new valid request 
                System.out.println(request);
            }
        }
        elevatorInput.close();
    }
}
```

输入：
```shell
3-FROM-2-TO-4
5-FROM-11-TO-14
X-FROM-Y-TO-Z
10-FROM-2-TO-7
11-FROM-3-TO-3
10-FROM-3-TO-1
12-FROM-+5-TO--1
13-FROM--2-TO-10
14-FROM-0-TO--3
15-FROM-+10-TO-0
```

输出结果：
* stdout
```shell
3-FROM-2-TO-4
5-FROM-11-TO-14
10-FROM-2-TO-7
12-FROM-5-TO--1
13-FROM--2-TO-10
```

* stderr
```
com.oocourse.elevator3.InvalidPatternException: Person request parse failed! - "X-FROM-Y-TO-Z"
	at com.oocourse.elevator3.PersonRequest.parse(PersonRequest.java:186)
	at com.oocourse.elevator3.ElevatorInput.nextPersonRequest(ElevatorInput.java:57)
	at com.oocourse.elevator3.TestMain.main(TestMain.java:7)
com.oocourse.elevator3.DuplicatedFromToFloorException: Person request parse failed! - "11-FROM-3-TO-3"
	at com.oocourse.elevator3.PersonRequest.parse(PersonRequest.java:181)
	at com.oocourse.elevator3.ElevatorInput.nextPersonRequest(ElevatorInput.java:57)
	at com.oocourse.elevator3.TestMain.main(TestMain.java:7)
com.oocourse.elevator3.DuplicatedPersonIdException: Person request parse failed! - "10-FROM-3-TO-1"
	at com.oocourse.elevator3.ElevatorInput.nextPersonRequest(ElevatorInput.java:59)
	at com.oocourse.elevator3.TestMain.main(TestMain.java:7)
com.oocourse.elevator3.InvalidFromFloorException: Person request parse failed! - "14-FROM-0-TO--3"
	at com.oocourse.elevator3.PersonRequest.parse(PersonRequest.java:171)
	at com.oocourse.elevator3.ElevatorInput.nextPersonRequest(ElevatorInput.java:57)
	at com.oocourse.elevator3.TestMain.main(TestMain.java:7)
com.oocourse.elevator3.InvalidToFloorException: Person request parse failed! - "15-FROM-+10-TO-0"
	at com.oocourse.elevator3.PersonRequest.parse(PersonRequest.java:177)
	at com.oocourse.elevator3.ElevatorInput.nextPersonRequest(ElevatorInput.java:57)
	at com.oocourse.elevator3.TestMain.main(TestMain.java:7)
```

