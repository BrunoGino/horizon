package br.com.horizon.repository.callback;

public interface LoadedDataCallback<T> {
    void onSuccess(T result);

    void onFail(String error);
}
