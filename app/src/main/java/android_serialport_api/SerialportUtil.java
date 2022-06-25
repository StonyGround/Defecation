package android_serialport_api;

import android.util.Log;


import com.kjy.care.activity.BaseApp;

import com.kjy.care.service.MessageEvent;



import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SerialportUtil {

    private static SerialPortManager machineControl;
    private static  String path = "/dev/ttyMT0";
    private static  int baudrate = 115200;
    public static void init(){

        try {

            if (machineControl == null) {
                machineControl = new SerialPortManager(path, baudrate);
                boolean openCOM = machineControl.openCOM();
                if (openCOM) {
                    Log.e("Rair", "打开串口成功");
                    machineControl.setOnDataReceiverListener(new OnDataReceiverListener() {
                        @Override
                        public void onDataReceiver(byte[] buffer, int size) {
                            Log.e("Rair", ByteUtil.hexBytesToString(buffer));
                            recviceData(buffer, size);
                        }
                    });
                    //  machineControl.sendCMD(new byte[0x00]);
                    EventBus.getDefault().post(MessageEvent.Com_open_ok);
                    // SerialportUtil.write(StringToAscii.hex2byte("00"));

                } else {
                    EventBus.getDefault().post(MessageEvent.Com_open_error);
                    Log.e("Rair", "打开串口失败");
                    Toasty.error(BaseApp.getAppContext(),"串口打开失败").show();
                }

            }
        }catch (Exception e){
            Log.e("串口", "打开串口失败");
            EventBus.getDefault().post(MessageEvent.Com_open_error);
            Toasty.error(BaseApp.getAppContext(),"串口打开失败").show();
        }



    }


     private static void recviceData(byte[] buffer, int size){
         if (size > 0) {
             byte[] tempBuffer = new byte[size];
             System.arraycopy(buffer,0,tempBuffer,0,size);
             String msg = StringToAscii.parseByte2HexStr(tempBuffer);
             //   Log.e("收到数据","长度===>"+size+"===>"+ msg);
             receiveSerialMsg(tempBuffer);
                        /*MessageEvent event =MessageEvent.Com_data;
                        event.setData(msg);
                        EventBus.getDefault().post(event);*/
             //  AlertUtil.show("数据:"+msg);
         }
     }

    private static int cmdLength = 5 ;//命令+效验 的长度

    /**
     * 串口数据
     */
    private static ByteBuffer byteBuffer = ByteBuffer.allocateDirect(32) ;

    private static void receiveSerialMsg(byte[] receiveBytes) {
        try {

           Log.e("receiveSerialMsg->>",StringToAscii.parseByte2HexStr(receiveBytes).toUpperCase()+"=="+receiveBytes.length);

            //byte[] receiveBytes = hexStr2bytes(msg);
            // 将串口收到的数据追加到ByteBuffer
            try {
                byteBuffer.put(receiveBytes);
            }catch (BufferOverflowException ex){
                byteBuffer.clear();
                byteBuffer.put(receiveBytes);
            }
            //    Log.e("byteBuffer->>","===byteBuffer=="+byteBuffer.position());
            if(byteBuffer.position() >= 5) {
                byte[] byteLength = new byte[1]; //数据位长度 的位置
                byteLength[0] = byteBuffer.get(3);
               // byteLength[1] = byteBuffer.get(2);
                // 计算一条完整指令的长度
                // 6表示STX(1 byte) + ETX(1 byte) + LEN(2 bytes) + CRC(2 bytes)
                //  int realLen = (byteLength[0] << 8) + byteLength[1] + 6;
                int realLen = (byteLength[0]) + cmdLength; //完整命令的整体长度

                 Log.e("realLen->>","===realLen=="+realLen+"===="+byteBuffer.position());

                //byteBuffer实际长度与LEN字段长度比较，若相等则表示已经接收到一条完整的指令
                if(realLen == byteBuffer.position()) {

                    byte[] effectiveBytes = new byte[realLen];
                    byteBuffer.flip();
                    byteBuffer.get(effectiveBytes, 0, realLen);
                    byteBuffer.clear();

                    dealEffectiveBytes(effectiveBytes);

                } else if(realLen < byteBuffer.position()) {   //粘包，截取有效数据，当一次收到多条完整指令时，根据业务场景决定多条指令的解析
                    int remainLen = byteBuffer.position() - realLen;
                    byte[] effectiveBytes = new byte[realLen];
                    byte[] remainBytes = new byte[remainLen];

                    byteBuffer.flip();
                    byteBuffer.get(effectiveBytes, 0, realLen);
                    byteBuffer.get(remainBytes, 0, remainLen);  //剩余字节
                    byteBuffer.clear();
                    byteBuffer.put(remainBytes);  //回填剩余字节，下一次收到数据继续追加接

                    // 根据业务场景，一次性收到多条完整指令时，只处理第一条指令，其余的舍弃
                    dealEffectiveBytes(effectiveBytes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 解析一条完整的指令
    private static void dealEffectiveBytes(byte[] effectiveBytes) {
        Map<String, String> outputMap = new HashMap<>();
        //将指令解析结果存到Map中
        int result = dataParser(effectiveBytes, outputMap);
    }

    //指令中业务参数解析
    private static int dataParser(byte[] data, Map<String, String> outputMap) {
        String str = StringToAscii.parseByte2HexStr(data).toUpperCase();
       // Log.e("收到数据","str==>"+str);

      // 校验开始符号
        if (data[0] != (byte) 0xFF) {
            return -1;
        }

        // 校验結束符
        /*if (data[data.length - 1] != (byte) 0xFB || data[data.length - 2] != (byte) 0x0E) {
            return -1;
        }*/

       // Log.e("收到数据","str=1=>"+str);

        int index, curLen = 0;
        int dataLen = (data[3]) + cmdLength;
        // 校验长度
        if ((dataLen) != data.length) {
            return -2;
        }

        String sum = StringToAscii.HexSum(str.substring(2,str.length()-2)).toUpperCase();
      //  Log.e("收到数据","str=2=>"+str+"===>sum:"+sum);
        // 校验CRC
        if (!sum.equals(str.substring(str.length()-2))) { return -3; }

          Log.e("收到数据","str==>"+str);


      //  byte[] temp = new byte[1];
        //解析CMD
      //  temp[0] = data[0];
       // String strCmd = bytesToHex(temp);



        //  outputMap.put("SERIAL_PORT_CMD", strCmd.toUpperCase());


        //   Log.e("str->>","===str=="+str);


       // String startFlag = strCmd;
        //  if ("5aea".equals(startFlag)) {
        //    EventBus.getDefault().post(str, EventBusTags.ON_CALL_DATA);
        // } else
        //   if ("fe".equals(startFlag) || "ff".equals(startFlag) || "ff".equals(startFlag)) {
        if(str.toUpperCase().startsWith("FF")){
            MessageEvent event =MessageEvent.Com_data;
            event.setData(str);
            EventBus.getDefault().post(event);
        }




        return 0;
    }



    private static  String bytesToHex(byte[] bytes) {
        String hex = new BigInteger(1, bytes).toString(16);
        return hex;
    }

    private static String byte2HexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }








    public static void close() {

        if (machineControl != null) {
            machineControl.closeCOM();
            machineControl = null;
        }



    }

    public static void write(byte[] mBuffer){
       // mBuffer = new byte[1024];
        //Arrays.fill(mBuffer, (byte) 0x55);
        MessageEvent.Send_data.setData(StringToAscii.byte2hex(mBuffer));
        EventBus.getDefault().post(MessageEvent.Send_data);
        if (machineControl != null) {
            SendingThread   mSendingThread = new SendingThread(mBuffer);
            mSendingThread.start();
        }
    }



    public static class SendingThread extends Thread {
        byte[] mBuffer;
        public SendingThread(byte[] mBuffer){
            this.mBuffer= mBuffer;
        }
        @Override
        public void run() {
            if (!isInterrupted()) {
                try {
                    if (machineControl != null) {
                        machineControl.sendCMD(mBuffer);

                        Log.e("数据","写入===>"+ StringToAscii.parseByte2HexStr(mBuffer));
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }



}
