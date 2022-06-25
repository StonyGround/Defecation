package android_serialport_api;

public class StringToAscii {

	public static String HexSum(String hex){
		
	    
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
         
        
        
        int count=0;
 
        for (int i = 0;i < hex.length(); i=i+2) {
            String swap =  hex.substring(i,i+2);
            int aa=Integer.parseInt(swap, 16);
            count+=aa;
        }
        
        //return b;
    return integerToHexString(count & 0xFF);
	
	
}
	 /**
     * byte数组转成字符串
     */
    public String btye2Str(byte[] data) {
        String str = new String(data);
        return str;
    }

    /**
     * 将byte数组化为十六进制串
     */

    public static final StringBuilder byte2hex(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar).trim());
        }
        return stringBuilder;
    }

    /**
     * 将byte数组转化成浮点数（4个字节带小数的浮点数）
     */
    public static float byte2int_Float(byte b[]) {
        int bits = b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;

        int sign = ((bits & 0x80000000) == 0) ? 1 : -1;
        int exponent = ((bits & 0x7f800000) >> 23);
        int mantissa = (bits & 0x007fffff);

        mantissa |= 0x00800000;
        // Calculate the result:
        float f = (float) (sign * mantissa * Math.pow(2, exponent - 150));

        return f;
    }

    /**
     * 将十六进制串转化为byte数组
     */
    public static final byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int aa=Integer.parseInt(swap, 16);
            int byteint = aa & 0xFF;
             byte s=Integer.valueOf(byteint).byteValue();
            b[j] = s;
        }
        return b;
    }

    /**
     * 将十六进制串转换为二进制  
     * */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    /**

     * 将字符串转成ASCII值
     */
    public static String strToASCII(String data) {
        String requestStr = "";
        for (int i = 0; i < data.length(); i++) {
            char a = data.charAt(i);
            int aInt = (int) a;
            requestStr = requestStr + integerToHexString(aInt);
        }
        return requestStr;
    }

    /**
     * 将十进制整数转为十六进制数，并补位
     * */
    public static String integerToHexString(int s) {

        String ss = Integer.toHexString(s);
        if (ss.length() % 2 != 0) {
            ss = "0" + ss;//0F格式
        }
        return ss.toUpperCase();
    }

    /**
     * 将二进制转换成十六进制串  
     * */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }



	
	
	
	
	
	
	
    private static String toHexUtil(int n){
        String rt="";
        switch(n){
        case 10:rt+="A";break;
        case 11:rt+="B";break;
        case 12:rt+="C";break;
        case 13:rt+="D";break;
        case 14:rt+="E";break;
        case 15:rt+="F";break;
        default:
            rt+=n;
        }
        return rt;
    }

    public static String toHex(int n){
        StringBuilder sb=new StringBuilder();
        if(n/16==0){
            return toHexUtil(n);
        }else{
            String t=toHex(n/16);
            int nn=n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseAscii(String str){
        StringBuilder sb=new StringBuilder();
        byte[] bs=str.getBytes();
        for(int i=0;i<bs.length;i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }













    /**
     * 获取异或数据
     */
    public static String getYHResult(String data)
    {
        //fa850401011aa7
        String sum   = "";
        int    value = Integer.valueOf(data.substring(2, 4), 16)^ Integer.valueOf(data.substring(4, 6), 16)^ Integer.valueOf(data.substring(6, 8), 16)^ Integer.valueOf(data.substring(8, 10), 16)^ Integer.valueOf(data.substring(10, 12), 16)^ Integer.valueOf(data.substring(12, 14), 16);
        sum = Integer.toHexString(value);
        if(sum.length()<2){
            String temp = sum;
            for(int i=0;i<2-sum.length();i++){
                temp = "0"+sum;
            }
            sum = temp;
        }
        return sum;
    }

    /**
     * 十六进制字符串转十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

    /**
     * 十六进制字符串按位取反加一
     * @param hex 十六进制字符串
     * @return
     */
    public static int hexString2Binary(String hex){
        String binaryString = Integer.toBinaryString(hexStringToAlgorism(hex));
        if(binaryString.length()<4){
            int zeroLength = 4-binaryString.length();
            for(int i=0;i<zeroLength;i++){
                binaryString = "0"+binaryString;
            }
        }
        System.out.println(binaryString);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<binaryString.length();i++){
            int index = Integer.parseInt(binaryString.charAt(i)+"");
            if(index == 0){
                sb.append(1);
            }else{
                sb.append(0);
            }
        }
        return -(Integer.valueOf(sb.toString(), 2)+1);
    }

    
}