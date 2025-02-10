package it.bancomatpay.sdk.manager.network.api;

import it.bancomatpay.sdk.manager.network.dto.DtoSvc;
import it.bancomatpay.sdk.manager.network.dto.response.DtoServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface BancomatServer {

    @POST("services/sdk/rest/handleRequest")
    Call<DtoServerResponse> handleRequest(@Body DtoSvc dtoSvc, @Header("Authorization") String authHeader);

}
