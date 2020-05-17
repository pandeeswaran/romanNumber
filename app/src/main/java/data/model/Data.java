package data.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Data{

	@SerializedName("realUserId")
	private String realUserId;

	@SerializedName("role")
	private String role;

	@SerializedName("mobileNumber")
	private String mobileNumber;

	@SerializedName("geoCode")
	private String geoCode;

	@SerializedName("actingAs")
	private String actingAs;

	@SerializedName("userId")
	private String userId;

	@SerializedName("token")
	private String token;

	@SerializedName("status")
	private String status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@SerializedName("message")
	private String message;

	public void setRealUserId(String realUserId){
		this.realUserId = realUserId;
	}

	public String getRealUserId(){
		return realUserId;
	}

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public void setGeoCode(String geoCode){
		this.geoCode = geoCode;
	}

	public String getGeoCode(){
		return geoCode;
	}

	public void setActingAs(String actingAs){
		this.actingAs = actingAs;
	}

	public String getActingAs(){
		return actingAs;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"realUserId = '" + realUserId + '\'' + 
			",role = '" + role + '\'' + 
			",mobileNumber = '" + mobileNumber + '\'' + 
			",geoCode = '" + geoCode + '\'' + 
			",actingAs = '" + actingAs + '\'' + 
			",userId = '" + userId + '\'' + 
			",token = '" + token + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}