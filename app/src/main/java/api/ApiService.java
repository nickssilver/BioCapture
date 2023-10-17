package api;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface ApiService {
    @FormUrlEncoded
    @POST("register") // Replace "register" with the actual endpoint on your server
    Call<Void> registerStudent(
            @Field("studentNumber") String studentNumber,
            @Field("studentName") String studentName,
            @Field("course") String course,
            @Field("department") String department,
            @Field("status") String status,
            @Field("classValue") String classValue
    );
}
