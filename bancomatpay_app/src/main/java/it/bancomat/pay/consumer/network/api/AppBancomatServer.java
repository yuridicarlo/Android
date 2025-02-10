package it.bancomat.pay.consumer.network.api;

import it.bancomatpay.sdk.manager.network.dto.DtoSvc;
import it.bancomatpay.sdk.manager.network.dto.response.DtoServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AppBancomatServer {

    @POST("services/app/rest/handleRequest")
    Call<DtoServerResponse> handleRequest(@Body DtoSvc dtoSvc, @Header("Authorization") String authHeader);

}
