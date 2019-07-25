//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.serialport;

import android.os.SystemClock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DeviceControl {
    public static final String POWER_MAIN = "/sys/class/misc/mtgpio/pin";
    public static final String POWER_EXTERNAL = "/sys/class/misc/aw9523/gpio";
    public static final String POWER_EXTERNAL2 = "/sys/class/misc/aw9524/gpio";
    public static final String POWER_NEWMAIN = "/sys/bus/platform/drivers/mediatek-pinctrl/10005000.pinctrl/mt_gpio";
    private BufferedWriter CtrlFile;
    private String poweron = "";
    private String poweroff = "";
    private String currentPath = "";
    int[] gpios;
    private DeviceControl.PowerType power_type;

    public DeviceControl(String path) throws IOException {
        File DeviceName = new File(path);
        this.CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
        this.currentPath = path;
    }

    public void setGpio(int gpio) {
        if (this.currentPath.equals("/sys/class/misc/aw9523/gpio")) {
            this.poweron = gpio + "on";
            this.poweroff = gpio + "off";
        } else {
            this.poweron = "-wmode " + gpio + " 0";
            this.poweron = "-wdir " + gpio + " 1";
            this.poweron = "-wdout " + gpio + " 1";
            this.poweroff = "-wdout " + gpio + " 0";
        }

    }

    public DeviceControl(DeviceControl.PowerType power_type, int... gpios) throws IOException {
        this.gpios = gpios;
        this.power_type = power_type;
    }

    public DeviceControl(String power_type, int... gpios) throws IOException {
        this.gpios = gpios;
        byte var4 = -1;
        switch(power_type.hashCode()) {
            case -1731040455:
                if (power_type.equals("NEWMAIN")) {
                    var4 = 3;
                }
                break;
            case -591494952:
                if (power_type.equals("EXPAND2")) {
                    var4 = 4;
                }
                break;
            case -467271576:
                if (power_type.equals("MAIN_AND_EXPAND")) {
                    var4 = 2;
                }
                break;
            case 2358713:
                if (power_type.equals("MAIN")) {
                    var4 = 0;
                }
                break;
            case 2059129498:
                if (power_type.equals("EXPAND")) {
                    var4 = 1;
                }
        }

        switch(var4) {
            case 0:
                this.power_type = DeviceControl.PowerType.MAIN;
                break;
            case 1:
                this.power_type = DeviceControl.PowerType.EXPAND;
                break;
            case 2:
                this.power_type = DeviceControl.PowerType.MAIN_AND_EXPAND;
                break;
            case 3:
                this.power_type = DeviceControl.PowerType.NEWMAIN;
                break;
            case 4:
                this.power_type = DeviceControl.PowerType.EXPAND2;
        }

    }

    public void MainPowerOn(int gpio) throws IOException {
        DeviceControl deviceControl = new DeviceControl("/sys/class/misc/mtgpio/pin");
        deviceControl.setGpio(gpio);
        deviceControl.writeON();
        deviceControl.DeviceClose();
    }

    public void MainPowerOff(int gpio) throws IOException {
        DeviceControl deviceControl = new DeviceControl("/sys/class/misc/mtgpio/pin");
        deviceControl.setGpio(gpio);
        deviceControl.WriteOff();
        deviceControl.DeviceClose();
    }

    public void ExpandPowerOn(int gpio) throws IOException {
        DeviceControl deviceControl = new DeviceControl("/sys/class/misc/aw9523/gpio");
        deviceControl.setGpio(gpio);
        deviceControl.writeON();
        deviceControl.DeviceClose();
    }

    public void ExpandPowerOff(int gpio) throws IOException {
        DeviceControl deviceControl = new DeviceControl("/sys/class/misc/aw9523/gpio");
        deviceControl.setGpio(gpio);
        deviceControl.WriteOff();
        deviceControl.DeviceClose();
    }

    private void writeON() throws IOException {
        this.CtrlFile.write(this.poweron);
        this.CtrlFile.flush();
    }

    private void WriteOff() throws IOException {
        this.CtrlFile.write(this.poweroff);
        this.CtrlFile.flush();
    }

    public void PowerOnDevice() throws IOException {
        switch(this.power_type) {
            case MAIN:
                this.MainPowerOn(this.gpios[0]);
                SystemClock.sleep(200L);
                break;
            case EXPAND:
                this.ExpandPowerOn(this.gpios[0]);
                SystemClock.sleep(200L);
                break;
            case MAIN_AND_EXPAND:
                this.MainPowerOn(this.gpios[0]);
//                SystemClock.sleep(200L);

                for(int i = 1; i < this.gpios.length; ++i) {
                    this.ExpandPowerOn(this.gpios[i]);
//                    SystemClock.sleep(200L);
                }
        }

    }

    public void PowerOffDevice() throws IOException {
        switch(this.power_type) {
            case MAIN:
                this.MainPowerOff(this.gpios[0]);
                break;
            case EXPAND:
                this.ExpandPowerOff(this.gpios[0]);
                break;
            case MAIN_AND_EXPAND:
                this.MainPowerOff(this.gpios[0]);

                for(int i = 1; i < this.gpios.length; ++i) {
                    this.ExpandPowerOff(this.gpios[i]);
                }
        }

    }

    public void DeviceClose() throws IOException {
        this.CtrlFile.close();
    }

    public void setMode(int num, int mode) throws IOException {
        this.CtrlFile.write("-wmode" + num + " " + mode);
        this.CtrlFile.flush();
    }

    public void setDir(int num, int mode, String path) throws IOException {
        File DeviceName = new File(path);
        this.CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
        this.CtrlFile.write("-wdir" + num + " " + mode);
        this.CtrlFile.flush();
    }

    public void setPull(int num, int mode, String path) throws IOException {
        File DeviceName = new File(path);
        this.CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
        this.CtrlFile.write("-wpsel" + num + " " + mode);
        this.CtrlFile.flush();
        this.CtrlFile.write("-wpen" + num + " " + mode);
        this.CtrlFile.flush();
    }

    public void newSetGpioOn(int gpio) throws IOException {
        this.CtrlFile.write("out " + gpio + " 1");
        this.CtrlFile.flush();
        this.DeviceClose();
    }

    public void newSetGpioOff(int gpio) throws IOException {
        this.CtrlFile.write("out " + gpio + " 1");
        this.CtrlFile.flush();
        this.DeviceClose();
    }

    public void newSetMode(int gpio) throws IOException {
        this.CtrlFile.write("mode " + gpio + " 0");
        this.CtrlFile.flush();
        this.DeviceClose();
    }

    public void newSetDir(int gpio, int dir) throws IOException {
        this.CtrlFile.write("dir " + gpio + " " + dir);
        this.CtrlFile.flush();
        this.DeviceClose();
    }

    public void PowerOnDevice(String gpio) // poweron barcode device
    {
        try {
            CtrlFile.write("-wmode" + gpio + " 0");   //将GPIO99设置为GPIO模式
            CtrlFile.flush();
            CtrlFile.write("-wdir" + gpio + " 1");        //将GPIO99设置为输出模式
            CtrlFile.flush();
            CtrlFile.write("-wdout" + gpio + " 1");
            CtrlFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PowerOffDevice(String gpio) // poweroff barcode device
    {
        try {
            CtrlFile.write("-wdout" + gpio + " 0");
            CtrlFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static enum PowerType {
        MAIN,
        EXPAND,
        MAIN_AND_EXPAND,
        NEWMAIN,
        EXPAND2;

        private PowerType() {
        }
    }
}
