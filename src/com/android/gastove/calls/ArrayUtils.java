package com.android.gastove.calls;

public class ArrayUtils {
    public static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++)
            if (need <= (1 << i) - 12)
                return (1 << i) - 12;

        return need;
    }
    
    public static int idealLongArraySize(int need) {
        return idealByteArraySize(need * 8) / 8;
    }
}
