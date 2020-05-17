package data.remote;


import data.model.CourtResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by anupamchugh on 09/01/17.
 */

public interface APIInterface {


    @GET("common/api/getCourtIdAndLoginTypeGivenByAddressAndType/{device_id}/TvApp")
    Call<CourtResponse> courtResponse(@Path("device_id") String device_id);
}
