
import java.io.*;
import java.net.*;

public class MakeUML {
	
	HttpURLConnection connection = null;
	private static final int BUFFER_SIZE = 4096;
	
	public Boolean makeUML(String parseString, String dst_Path){
		
		String base_yUML_API_Link="https://yuml.me/diagram/plain/class/";
//		String base_yUML_API_Link="https://yuml.me/diagram/nofunky/class/";		
//		String base_yUML_API_Link="https://yuml.me/diagram/scruffy/class/";
		String complete_yUML_Link= base_yUML_API_Link+ parseString;
		System.out.println("Complete Link: "+complete_yUML_Link);
		
		
		//Creating HTTP Connection
		try{
			
			System.out.println("Generating Class diargamfrom the given Grammar. Making request to yUML API");
		    if(connectToHTTP(complete_yUML_Link))
		    {
		    	//Reading and Saving jpg file to destination path
				InputStream inputStream = connection.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(new File(dst_Path));
				//OutputStream op = new FileOutputStream(new File(dst_Path));
				int read_bytes=-1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while((read_bytes=inputStream.read(buffer))!=-1){
					outputStream.write(buffer, 0, read_bytes);
				}
				
				outputStream.close();
				inputStream.close();
				System.out.println("Class diargam generated");
		    }		    	
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			 	if(connection!=null){
			 		try{
			 			connection.disconnect();
			 		}
			 		catch(Exception e){
			 			e.printStackTrace();
			 		}
				
			}
		}
	
		return null;

}
	
	//Method for making connection to yUML API. Returns true if response code is 200
	private boolean connectToHTTP(String complete_Url){
		try{
		URL url = new URL(complete_Url);
		
		//HTTP Request
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		
		int responseCode = connection.getResponseCode();
		if(responseCode==200){
			System.out.println("Response code:200");
			return true;
		}else{
			throw new RuntimeException("Cannot Generate Diagram. Request Failed.Error Code:" + responseCode);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	return false;
	}
	
}
		
//URL url = new URL(complete_yUML_Link);
////HTTP Request
//connection = (HttpURLConnection) url.openConnection();
//connection.setRequestMethod("GET");
//connection.setRequestProperty("Accept", "application/json");
//
//int responseCode = connection.getResponseCode();
//
//
//if(responseCode!=200){
//	throw new RuntimeException("Cannot Generate Diagram. Request Failed.Error Code:" + responseCode);
//}else{
////Reading and Saving jpg file to destination path
//InputStream inputStream = connection.getInputStream();
//FileOutputStream outputStream = new FileOutputStream(new File(dst_Path));
////OutputStream op = new FileOutputStream(new File(dst_Path));
//int read_bytes=-1;
//byte[] buffer = new byte[BUFFER_SIZE];
//while((read_bytes=inputStream.read(buffer))!=-1){
//	outputStream.write(buffer, 0, read_bytes);
//}
//
//outputStream.close();
//inputStream.close();
//
//System.out.println("Class diargam generated");
//	
//}
//
//}catch(Exception e){
//e.printStackTrace();
//return null;
//}finally{
//
// 	if(connection!=null){
// 		try{
// 			connection.disconnect();
// 		}
// 		catch(Exception e){
// 			e.printStackTrace();
// 		}
//	
//}
//}
