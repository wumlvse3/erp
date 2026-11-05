package com.jcloud.easypoi.handler.inter;

public interface IReadHandler<T> {

    public void handler(T t);

    public void doAfterAll();

}
