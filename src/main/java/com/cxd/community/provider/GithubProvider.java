package com.cxd.community.provider;

import com.alibaba.fastjson.JSON;
import com.cxd.community.dto.AccessTokenDTO;
import com.cxd.community.dto.GithubUser;
import com.cxd.community.utils.SslUtil;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class GithubProvider {

    @Autowired
    private SslUtil sslUtil;

    public  String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
            Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();

            try (Response response = sslUtil.getUnsafeOkHttpClient().newCall(request).execute();) {
                String string= response.body().string();
                String token = string.split("&")[0].split("=")[1];
                return  token;
            } catch (IOException e) {

            }
        return null;
    }
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user").header("Authorization","token "+accessToken)
                .build();
        try {
            Response response = sslUtil.getUnsafeOkHttpClient().newCall(request).execute();
            String string= response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
