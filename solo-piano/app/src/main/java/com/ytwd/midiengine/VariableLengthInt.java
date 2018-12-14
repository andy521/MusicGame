package com.ytwd.midiengine;

import java.io.InputStream;

/**
 * Created by yanwei on 4/15/16.
 */
public class VariableLengthInt {
    private int mIntValue;

    private byte[] bytes = new byte[4];
    private int bytesNumber;

    public VariableLengthInt(InputStream inputStream)
    {
        readIntFromStream(inputStream);
    }

    public int getIntegerValue()
    {
        return mIntValue;
    }

    private void readIntFromStream(InputStream inputStream)
    {
        bytesNumber = 0;
        mIntValue = 0;

        for(;;)
        {
            try {
                int value = inputStream.read();
                bytes[bytesNumber++] = (byte)value;
                if((value & 0x80) <= 0) // value == -1 or value & 0x80 == 0
                    break;
            }catch (Exception e){e.printStackTrace();}
        }


        for(int i = 0; i < bytesNumber; i++)
        {
            mIntValue += (bytes[i] - 0x80) * Math.pow(128, bytesNumber - 1 - i);
        }
    }
}
