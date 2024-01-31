package api;

import com.example.biocapture.AuditLogs;
import com.example.biocapture.FetchStudentData;
import com.example.biocapture.FingerprintTemplate;
import com.example.biocapture.RegisterStudentRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers({
            "Content-Type: application/json"
    })
    @POST("Registration") // actual endpoint on server registration
    Call<Void> registerStudent(@Body RegisterStudentRequest request);

    @GET("Registration/{studentId}") //actual endpoint on server on get student data
    Call<FetchStudentData> FetchStudentData(@Path("studentId") String studentId);

    @DELETE("Registration/{studentId}")

    @Headers({
            "Content-Type: application/json"
    })
    @POST("Verification")
    Call<ResponseBody> postAuditLog(@Body AuditLogs log);
    @GET("Verification/GetAllFingerprintTemplates")
    Call<List<FingerprintTemplate>> getAllTemplatesFromDatabase();

}