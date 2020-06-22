package br.com.horizon.repository.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackWithResponse<T> implements Callback<T> {

    private final ResponseCallback<T> responseCallback;

    public CallbackWithResponse(ResponseCallback<T> responseCallback) {
        this.responseCallback = responseCallback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T result = response.body();
            if (result != null) {
                responseCallback.onSuccess(result);
            }
        } else {
            responseCallback.onFail(MessagesCallback.RESPONSE_NOT_SUCCESSFUL.getMessage());
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        responseCallback.onFail(MessagesCallback.RESPONSE_NOT_SUCCESSFUL.getMessage());
    }
}

