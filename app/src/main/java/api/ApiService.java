package api;

import com.example.biocapture.FetchStudentData;
import com.example.biocapture.RegisterStudentRequest;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("Registration") // actual endpoint on your server registration
    Call<Void> registerStudent(@Body RegisterStudentRequest request);

    @GET("Registration/{studentId}") //actual endpoint on your server on get student data
    Call<FetchStudentData> FetchStudentData(@Path("studentId") String studentId);

}