package api;

import com.example.biocapture.Administer.AdminLoginRequest;
import com.example.biocapture.Administer.Biousers;
import com.example.biocapture.Administer.PermissionsResponse;
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
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
//Registration Endpoints
    @Headers({
            "Content-Type: application/json"
    })
    @POST("Registration") // actual endpoint on server registration
    Call<Void> registerStudent(@Body RegisterStudentRequest request);

    @GET("Registration/{studentId}") //actual endpoint on server on get student data
    Call<FetchStudentData> FetchStudentData(@Path("studentId") String studentId);

    @DELETE("Registration/{studentId}")
    Call<ResponseBody> deleteStudent(@Path("studentId") String studentId);

//Verification Endpoints
    @Headers({
            "Content-Type: application/json"
    })
    @POST("Verification")
    Call<ResponseBody> postAuditLog(@Body AuditLogs log);
    @GET("Verification/GetAllFingerprintTemplates")
    Call<List<FingerprintTemplate>> getAllTemplatesFromDatabase();

//Administration Endpoints
    @Headers({
            "Content-Type: application/json"
    })
    @POST("AdminAuth/login")
    Call<Void> adminLogin(@Body AdminLoginRequest adminLoginRequest);
    @POST("Users/authenticate")
    Call<PermissionsResponse> authenticateUser(@Query("pin") String pin);
    @GET("Users")
    Call<List<Biousers>> getAllUsersFromDatabase();

    @POST("Users/register")
    Call<Biousers> registerUser(@Body Biousers user);

    @DELETE("Users/{id}")
    Call<Void> deleteUser(@Path("id") String userId);

    @PUT("Users/{id}")
    Call<Biousers> editUser(@Path("id") String id, @Body Biousers user);

}