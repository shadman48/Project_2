import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;

public class PhysLayerClient {

	public static void main(String[] args) throws Exception 
	{
		// Create socket
    	try (Socket socket = new Socket("18.221.102.182", 38002)) 
        {
    		InputStream is = socket.getInputStream();
    		DataInputStream dis = new DataInputStream(is);
    		OutputStream os = socket.getOutputStream();
    		DataOutputStream dos = new DataOutputStream(os);
    		byte[] preamble = new byte[64];
    		byte[] encodedArray = new byte[32];
    		byte[] signalArray = new byte[32];
    		byte[] decodedArray = new byte[32];
    		byte reply;
    		
    		
    		
    		//Reads in preamble and then the esablishes a base line. 
    		
    		
    		//Reads in 64 unsigned bytes
    		int n = 0;
    		double baseline = 0;
    		for(int i = 0; i <= 63; i++)
    		{
    			n = dis.readUnsignedByte();
        		preamble[i] = (byte)n; 
        		baseline += n;
    		}
    		
    		
    		
    		//Create Baseline
    		baseline = baseline / 64;
    		System.out.println("Baseline from preamble: " + baseline + " \n" + Arrays.toString(preamble));
    		
    		//System.out.println("HEX " + bytesToHex(preamble));
    		
    		//Reads in 32 bytes randomly generated
    		n = 0;
    		for(int i = 0; i <= 31; i++)
    		{
    			n = dis.readUnsignedByte();
        		encodedArray[i] = (byte)n; 
        		
        		//converts signal to 1's & 0's
        		if(n > baseline)
        			signalArray[i] = 1;
        		else
        			signalArray[i] = 0;
    		}
    		
    		System.out.println(Arrays.toString(encodedArray));
    		System.out.println(Arrays.toString(signalArray));
    		System.out.println("HEX " + bytesToHex(encodedArray));
    		
    		
    		//decodes data using 4B/5B with NRZI
//    		commit to git
    		int bVal = lookUpTable(11011);
    		System.out.println(bVal);
    		
    		
    		
    		 
    		//Send the 32Bytes back (no encoding)
    		dos.write(decodedArray);
    		
    		
    		
    		
    		
    		//Receive signal either 0 or 1 for error checking.
    		reply = dis.readByte() ;  
    		
    		if(reply > baseline)
    		{
    			System.out.println("Decode correctly.");
    		}
    		else
    		{
    			System.out.println("Did not decode correctly.");
    		}
        }
	}
	
	
	
	
	
	
	
	
	//Creates a look up table for converting 5Bits to 4Bits.
	public static int lookUpTable(int valIn)
	{
		int[][] table = new int[2][16];
		int valOut = 0;
		
		//4 Bit Data.
		table[0][0] = 0000;
		table[0][1] = 0001;
		table[0][2] = 0010;
		table[0][3] = 0011;
		table[0][4] = 0100;
		table[0][5] = 0101;
		table[0][6] = 0110;
		table[0][7] = 0111;
		table[0][8] = 1000;
		table[0][9] = 1001;
		table[0][10] = 1010;
		table[0][11] = 1011;
		table[0][12] = 1100;
		table[0][13] = 1101;
		table[0][14] = 1110;
		table[0][15] = 1111;
		
		//5 Bit Data.
		table[1][0] = 11110;
		table[1][1] = 01001;
		table[1][2] = 10100;
		table[1][3] = 10101;
		table[1][4] = 01010;
		table[1][5] = 01011;
		table[1][6] = 01110;
		table[1][7] = 01111;
		table[1][8] = 10010;
		table[1][9] = 10011;
		table[1][10] = 10110;
		table[1][11] = 10111;
		table[1][12] = 11010;
		table[1][13] = 11011;
		table[1][14] = 11100;
		table[1][15] = 11101;
		
		int temp;
		for(int i = 0; i <= 15; i++)
		{
			temp = table[1][i]; 
			
			if(temp == valIn)
			{
				valOut = table[0][i];
				break;
			}
		}
		
		return valOut;
	}
	
	
	
	//Method to convert bytes to HEX.
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) 
	{
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	
	//End of class
}
