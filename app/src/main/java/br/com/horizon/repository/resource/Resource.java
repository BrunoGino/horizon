package br.com.horizon.repository.resource;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Resource<T> {
    @Nullable
    private T data;
    @Nullable
    private String error;

    public static Resource<Resource> createOnFailResource(Resource currentResource, String error){
        return currentResource != null ? new Resource<Resource>(currentResource,error) : new Resource<Resource>(null,error);
    }

}
