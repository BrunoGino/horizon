package br.com.horizon.repository;

public interface RepositoryCallback<T> {
    void onFail(String error);
    void onSuccess(T t);
}
