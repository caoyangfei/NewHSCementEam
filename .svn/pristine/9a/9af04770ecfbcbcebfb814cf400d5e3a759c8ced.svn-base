//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.serialport;

import android.util.Log;

import com.speedata.libuhf.utils.DataConversionUtils;
import com.speedata.libuhf.utils.MyLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//import com.supcon.mes.middleware.util.DataConversionUtils;

public class SerialPort {
    public static final String TAG = "SerialPortNative";
    public static final String SERIAL_TTYMT0 = "/dev/ttyMT0";
    public static final String SERIAL_TTYMT1 = "/dev/ttyMT1";
    public static final String SERIAL_TTYMT2 = "/dev/ttyMT2";
    public static final String SERIAL_TTYMT3 = "/dev/ttyMT3";
    public static final String SERIAL_TTYG0 = "/dev/ttyG0";
    public static final String SERIAL_TTYG1 = "/dev/ttyG1";
    public static final String SERIAL_TTYG2 = "/dev/ttyG2";
    public static final String SERIAL_TTYG3 = "/dev/ttyG3";
    private MyLogger logger = MyLogger.jLog();
    private int fdx = -1;
    private int writelen;
    private String str;
    private int timeout = 100;

    public SerialPort() {
    }

    public void OpenSerial(String dev, int brd) throws SecurityException, IOException {
        this.fdx = this.openport(dev, brd, 8, 1, 0);
        if (this.fdx < 0) {
            Log.e("SerialPortNative", "native open returns null");
            throw new IOException();
        }
    }

    public void OpenSerial(String device, int baudrate, int databit, int stopbit, int crc) throws SecurityException, IOException {
        System.out.println("open");
        this.fdx = this.openport(device, baudrate, databit, stopbit, crc);
        if (this.fdx < 0) {
            Log.e("SerialPortNative", "native open returns null");
            throw new IOException();
        }
    }

    public void resetParam(int fd, int baudrate) throws SecurityException, IOException {
        this.fdx = this.setparam(fd, baudrate, 8, 1, 0);
        if (this.fdx < 0) {
            Log.e("SerialPortNative", "native setparam returns null");
            throw new IOException();
        }
    }

    public void resetParam(int fd, int baudrate, int databit, int stopbit, int crc) throws SecurityException, IOException {
        this.fdx = this.setparam(fd, baudrate, databit, stopbit, crc);
        if (this.fdx < 0) {
            Log.e("SerialPortNative", "native setparam returns null");
            throw new IOException();
        }
    }

    public int getFd() {
        return this.fdx;
    }

    public int WriteSerialByte(int fd, byte[] str) {
        this.clearPortBuf(fd);
        this.logger.d("--WriteSerialByte---" + DataConversionUtils.byteArrayToString(str));
        this.writelen = this.writeport(fd, str);
        if (this.writelen >= 0) {
            this.logger.d("write success");
        } else {
            this.logger.e("write failed");
        }

        return this.writelen;
    }

    public byte[] writeThenRead(int fd, byte[] buf, int count, int delay, int brd, int bit, int stop, int crc) throws SecurityException, IOException {
        this.clearPortBuf(fd);
        this.logger.d("--WriteSerialByte---" + DataConversionUtils.byteArrayToString(buf));
        byte[] result = this.write_then_read(fd, buf, count, delay, brd, bit, stop, crc);
        if (result != null) {
            this.logger.d("write success");
        } else {
            this.logger.e("write failed");
        }

        return result;
    }

    public byte[] writeThenRead(int fd, byte[] buf, int count, int delay, int brd) throws SecurityException, IOException {
        this.clearPortBuf(fd);
        this.logger.d("--WriteSerialByte---" + DataConversionUtils.byteArrayToString(buf));
        byte[] result = this.write_then_read(fd, buf, count, delay, brd, 8, 1, 0);
        if (result != null) {
            this.logger.d("write success");
        } else {
            this.logger.e("write failed");
        }

        return result;
    }

    public byte[] ReadSerial(int fd, int len) throws UnsupportedEncodingException {
        byte[] tmp = null;
        tmp = this.readport(fd, len, this.timeout);

        for(int count = 0; tmp == null && count < 10; ++count) {
            tmp = this.readport(fd, len, this.timeout);
        }

        if (tmp != null) {
            this.logger.d("read---" + DataConversionUtils.byteArrayToStringLog(tmp, tmp.length));
        } else {
            this.logger.d("read---null");
        }

        return tmp;
    }

    public byte[] ReadSerial(int fd, int len, int delay) throws UnsupportedEncodingException {
        byte[] tmp = null;
        tmp = this.readport(fd, len, delay);

        for(int count = 0; tmp == null && count < 10; ++count) {
            tmp = this.readport(fd, len, delay);
        }

        if (tmp != null) {
            this.logger.d("read---" + DataConversionUtils.byteArrayToStringLog(tmp, tmp.length));
        } else {
            this.logger.d("read---null");
        }

        return tmp;
    }

    public byte[] ReadSerial(int fd, int len, boolean isClear) throws UnsupportedEncodingException {
        byte[] tmp = null;
        tmp = this.readport(fd, len, this.timeout);

        for(int count = 0; tmp == null && count < 10; ++count) {
            tmp = this.readport(fd, len, this.timeout);
        }

        if (tmp != null) {
            this.logger.d("read---" + DataConversionUtils.byteArrayToStringLog(tmp, tmp.length));
            if (isClear) {
                this.clearPortBuf(fd);
            }
        } else {
            this.logger.d("read---null");
        }

        return tmp;
    }

    public String ReadSerialString(int fd, int len) throws UnsupportedEncodingException {
        byte[] tmp = this.readport(fd, len, 50);
        if (tmp == null) {
            return null;
        } else {
            if (this.isUTF8(tmp)) {
                this.str = new String(tmp, "utf8");
                Log.d("SerialPortNative", "is a utf8 string");
            } else {
                this.str = new String(tmp, "gbk");
                Log.d("SerialPortNative", "is a gbk string");
            }

            return this.str;
        }
    }

    public void CloseSerial(int fd) {
        this.closeport(fd);
    }

    private boolean isUTF8(byte[] sx) {
        Log.d("SerialPortNative", "begian to set codeset");
        int i = 0;

        while(true) {
            while(i < sx.length) {
                if (sx[i] < 0) {
                    if (sx[i] >>> 5 == 134217726) {
                        if (i + 1 >= sx.length || sx[i + 1] >>> 6 != 67108862) {
                            return false;
                        }

                        i += 2;
                    } else {
                        if (sx[i] >>> 4 != 268435454) {
                            return false;
                        }

                        if (i + 2 >= sx.length || sx[i + 1] >>> 6 != 67108862 || sx[i + 2] >>> 6 != 67108862) {
                            return false;
                        }

                        i += 3;
                    }
                } else {
                    ++i;
                }
            }

            return true;
        }
    }

    public void clearPortBuf(int fd) {
        this.logger.d("clearPortBuf---");
        this.clearportbuf(fd);
    }

    private native int setparam(int var1, int var2, int var3, int var4, int var5);

    private native byte[] write_then_read(int var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8);

    private native int openport(String var1, int var2, int var3, int var4, int var5);

    private native void closeport(int var1);

    private native byte[] readport(int var1, int var2, int var3);

    private native int writeport(int var1, byte[] var2);

    public native void clearportbuf(int var1);

    static {
        System.loadLibrary("serial_port");
    }
}
