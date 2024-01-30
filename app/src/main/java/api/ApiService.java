package api;

import com.example.biocapture.FetchStudentData;
import com.example.biocapture.FingerprintTemplate;
import com.example.biocapture.RegisterStudentRequest;
import com.example.biocapture.StudentDetails;

import java.util.List;

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
    @POST("Registration") // actual endpoint on server registration
    Call<Void> registerStudent(@Body RegisterStudentRequest request);

    @GET("Registration/{studentId}") //actual endpoint on server on get student data
    Call<FetchStudentData> FetchStudentData(@Path("studentId") String studentId);
    @Headers({
            "Content-Type: application/json"
    })
    @POST("Verification")
    Call<StudentDetails> VerifyFingerprint(@Body String fingerprint);
    @GET("Verification/GetAllFingerprintTemplates")
    Call<List<FingerprintTemplate>> getAllTemplatesFromDatabase();

}