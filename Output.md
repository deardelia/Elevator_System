# OUTPUT

Demo

```java
import com.oocourse.TimableOutput;

class SelfTestMainClassForTimableOutput {
    public static void main(String[] args) throws Exception {
        // please MUST initialize start timestamp at the beginning
        TimableOutput.initStartTimestamp();

        // print something
        TimableOutput.println(1.0 / 7);

        // sleep for a while, then print something again
        Thread.sleep(1000);
        TimableOutput.println(
                String.format("result of 2 / 7 is %.10f", 2.0 / 7));

        // sleep for a while, then print something again
        Thread.sleep(3000);
        TimableOutput.println(
                String.format("result of 3 / 7 is %.10f", 3.0 / 7));
    }
}

```

输出结果（参考，具体时间戳不一定严格一致）：

```
[   0.0010]0.14285714285714285
[   1.0240]result of 2 / 7 is 0.2857142857
[   4.0250]result of 3 / 7 is 0.4285714286
```

