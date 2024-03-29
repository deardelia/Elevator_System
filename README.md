# Elevator System

## Introduction

This project aims to achive a multi-elevator scheduling system.

#


### Important Concept

-   电梯系统时间：程序开始运行的时间
-   电梯初始位置：1层（三部电梯均是。电梯系统运行之中停止等待的楼层可自定义）
-   电梯数量：3部，分别编号为`A`，`B`，`C`
-   电梯可运行楼层：-3-1，1-20
-   电梯可停靠楼层：
    -   A:  -3, -2, -1, 1, 15-20
    -   B:  -2, -1, 1, 2, 4-15
    -   C:  1, 3, 5, 7, 9, 11, 13, 15
-   电梯上升或下降一层的时间：
    -   A: 0.4s
    -   B: 0.5s
    -   C: 0.6s
-   电梯开关门的时间：开门0.2s，关门0.2s，共计0.4s，到达楼层后方可立刻开门，关门完毕后方可立刻出发。三个电梯均为此时间。
-   电梯最大载客量（轿厢容量）
    -   A：6名乘客
    -   B：8名乘客
    -   C：7名乘客
-   电梯内部初始乘客数目：0

### 电梯请求说明

本次作业的电梯，为一种比较特殊的电梯，学名叫做**目的选层电梯**（可以百度一下，确实是存在的，且已经投入了主流市场）。

大概意思是，**在电梯的每层入口，都有一个输入装置，让每个乘客输入自己的目的楼层**。电梯基于这样的一个目的地选择系统进行调度，将乘客运送到指定的目标楼层。

所以，一个电梯请求包含这个人的**出发楼层和目的楼层**，以及这个人的id（保证人员id唯一），请求内容将作为一个整体送入电梯系统，在整个运行过程中请求内容不会发生改变。

#
### Exmaple

|  #   | 输入                                       | 输出                                                         | 说明                                                         |
| :--: | ------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
|  1   | [0.0]1-FROM-1-TO-2                         | [    0.0100]OPEN-1-B<br />[    0.0120]IN-1-1-B<br />[    0.4110]CLOSE-1-B<br />[    0.9120]ARRIVE-2-B<br />[    0.9130]OPEN-2-B<br />[    0.9130]OUT-1-2-B<br />[    1.3140]CLOSE-2-B | A电梯不能在2层停靠，因此选择B电梯执行该请求                  |
|  2   | [0.0]1-FROM-1-TO-2<br />[0.0]2-FROM-1-TO-2 | [    0.0120]OPEN-1-B<br />[    0.0160]IN-1-1-B<br />[    0.0170]IN-2-1-B<br />[    0.4120]CLOSE-1-B<br />[    0.9120]ARRIVE-2-B<br />[    0.9120]OPEN-2-B<br />[    0.9130]OUT-1-2-B<br />[    0.9130]OUT-2-2-B<br />[    1.3120]CLOSE-2-B | 本次作业中可以任选调度策略，此处使用了ALS捎带算法            |
|  3   | [1.5]1-FROM-1-TO-2                         | [1.3870]OPEN-1-B<br />[1.3910]IN-1-1-B<br />[1.7930]CLOSE-1-B<br />[2.2940]ARRIVE-2-B<br />[2.2940]OPEN-2-B<br />[2.2940]OUT-1-2-B<br />[2.6940]CLOSE-2-B | 输入的起始时间可以不从0.0开始<br/>**（此处存在时间计测同步性问题，在样例说明部分会有详细的说明）** |
|  4   | [2.2]1-FROM-1-TO-3<br />[2.3]2-FROM-2-TO-4 | [    2.2060]OPEN-1-C<br />[    2.2070]IN-1-1-C<br />[    2.6070]CLOSE-1-C<br />[    2.8020]ARRIVE-2-B<br />[    2.8030]OPEN-2-B<br />[    2.8030]IN-2-2-B<br />[    3.2070]ARRIVE-2-C<br />[    3.2070]CLOSE-2-B<br />[    3.7070]ARRIVE-3-B<br />[    3.8070]ARRIVE-3-C<br />[    3.8080]OPEN-3-C<br />[    3.8080]OUT-1-3-C<br />[    4.2080]ARRIVE-4-B<br />[    4.2090]OPEN-4-B<br />[    4.2090]OUT-2-4-B<br />[    4.2090]CLOSE-3-C<br />[    4.6090]CLOSE-4-B | 多部电梯合作，可以同时执行多条请求<br />                     |
|  5   | [0.0]1-FROM--3-TO-2                        | [    0.4060]ARRIVE--1-A<br />[    0.8070]ARRIVE--2-A<br />[    1.2070]ARRIVE--3-A<br />[    1.2080]OPEN--3-A<br />[    1.2080]IN-1--3-A<br />[    1.6080]CLOSE--3-A<br />[    2.0090]ARRIVE--2-A<br />[    2.4100]ARRIVE--1-A<br />[    2.8100]ARRIVE-1-A<br />[    2.8100]OPEN-1-A<br />[    2.8100]OPEN-1-B<br />[    2.8110]OUT-1-1-A<br />[    2.8110]IN-1-1-B<br />[    3.2120]CLOSE-1-B<br />[    3.2120]CLOSE-1-A<br />[    3.7120]ARRIVE-2-B<br />[    3.7120]OPEN-2-B<br />[    3.7130]OUT-1-2-B<br />[    4.1130]CLOSE-2-B | 如果出发楼层和目标楼层仅靠一部电梯无法到达，则需要中途为乘客更换电梯 |

