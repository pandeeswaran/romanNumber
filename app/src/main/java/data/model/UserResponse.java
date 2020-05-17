package data.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class UserResponse{

	@SerializedName("response")
	private Response response;

	@SerializedName("status")
	private boolean status;

	@SerializedName("responseCode")
	private String responseCode;

	public void setResponse(Response response){
		this.response = response;
	}

	public Response getResponse(){
		return response;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	public void setResponseCode(String responseCode){
		this.responseCode = responseCode;
	}

	public String getResponseCode(){
		return responseCode;
	}

	@Override
 	public String toString(){
		return 
			"UserResponse{" + 
			"response = '" + response + '\'' + 
			",status = '" + status + '\'' + 
			",responseCode = '" + responseCode + '\'' + 
			"}";
		}
}