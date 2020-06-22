package br.com.horizon.repository.callback;

public interface ResponseCallback<T> {
    void onFail(String error);
    void onSuccess(T t);
}
