package data.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Response{

	@SerializedName("message")
	private String message;

	@SerializedName("loginType")
	private String loginType;

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getCourtId() {
		return courtId;
	}

	public void setCourtId(String courtId) {
		this.courtId = courtId;
	}

	@SerializedName("courtId")
	private String courtId;


	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@SerializedName("data")
	private Data data;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
					"message = '" + message + '\'' +
					"courtId = '" + courtId + '\'' +
					"loginType = '" + loginType + '\'' +
					"data = '" + data + '\'' +
					"}";
		}
}