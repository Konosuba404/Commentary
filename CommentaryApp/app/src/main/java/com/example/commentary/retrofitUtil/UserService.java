package com.example.commentary.retrofitUtil;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("network/user")
    Call<ResponseBody> getPostDataBody(@Body User users);
}
