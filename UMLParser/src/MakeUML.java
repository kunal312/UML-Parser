
import java.io.*;
import java.net.*;

public class MakeUML {
	
	
	public Boolean makeUML(String parseString, String dst_Path){
		
		String base_yUML_API_Link="https://yuml.me/diagram/plain/class/";
		String complete_yUML_Link= base_yUML_API_Link+ parseString;
		System.out.println("Complete Link: "+complete_yUML_Link);
		HttpURLConnection connection = null;
		//Creating HTTP Connection
		try{
			System.out.println("Generating Class diargamfrom the given Grammar. Making request to yUML API");
			URL url = new URL(complete_yUML_Link);
		//HTTP Request
			 connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			//connection.setUseCaches(false);
			//connection.setDoOutput(true);
			if(connection.getResponseCode()!=200){
				throw new RuntimeException("Cannot Generate Diagram. Request Failed.Error Code:" + connection.getResponseCode());
			}
			
		//Reading and Saving jpg file to destination path
		OutputStream op = new FileOutputStream(new File(dst_Path));
		byte[] bytes = new byte[1024];
		int read=0;
		while((read=connection.getInputStream().read(bytes))!=-1){
			op.write(bytes, 0, read);
		}
		op.close();
		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			
			if(connection!=null){
				connection.disconnect();
			}
		}
		
	return null;

	}
	
}
