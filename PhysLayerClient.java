import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TreeMap;

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
    		byte[] decodedArray = new byte[32];
    		String[] signalArray = new String[64];
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
    		System.out.printf("Baseline from preamble: %.2f\n", baseline);
    		
    		
    		
    		//Reads in 32 bytes randomly generated and decodes data using 4B/5B
    		n = 0;
    		TreeMap<String, String> fBfB = new TreeMap<>();
    		lookUpTable(fBfB);
			boolean lastSignal = false;
    		for(int i = 0; i < 64; i++)
    		{
    			String bVal = "";
    			for(int j = 0; j < 5; j++)
    			{
    				boolean thisSignal = is.read() > baseline;
    				bVal += (lastSignal == thisSignal)? "0":"1";
					lastSignal = thisSignal;
    			}
        		signalArray[i] = fBfB.get(bVal); 
    		}
    		System.out.print("Received 32 bytes: ");
    
    		
    		
    		//Reconstructs into bytes.
    		for(int i = 0; i < 32; i++)
    		{
    			String upperNibble = signalArray[2*i];
				String lowerNibble = signalArray[2*i+1];
				System.out.printf("%X", Integer.parseInt(upperNibble, 2));
				System.out.printf("%X", Integer.parseInt(lowerNibble, 2));
				String wholeByte = upperNibble + lowerNibble;
				decodedArray[i] = (byte)Integer.parseInt(wholeByte, 2);
    		}
    		
    		
    		 
    		//Send the 32Bytes back (no encoding)
    		dos.write(decodedArray);
    		
    		
    		
    		
    		
    		//Receive signal either 0 or 1 for error checking.
    		reply = dis.readByte() ;  
    		
    		if(reply == 1)
    		{
    			System.out.println("\nDecode correctly.");
    		}
    		else
    		{
    			System.out.println("\nDid not decode correctly.");
    		}
        }
	}
	
	
	
	
	
	
	
	
	//Creates a look up table for converting 5Bits to 4Bits.
	public static void lookUpTable(TreeMap<String,String> table)
	{
		table.put("11110","0000");
		table.put("01001","0001");
		table.put("10100","0010");
		table.put("10101","0011");
		table.put("01010","0100");
		table.put("01011","0101");
		table.put("01110","0110");
		table.put("01111","0111");
		table.put("10010","1000");
		table.put("10011","1001");
		table.put("10110","1010");
		table.put("10111","1011");
		table.put("11010","1100");
		table.put("11011","1101");
		table.put("11100","1110");
		table.put("11101","1111");
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
