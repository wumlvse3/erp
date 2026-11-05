package com.jcloud.easypoi.handler.inter;

public interface ICommentHandler {

    default String getAuthor() {
        return null;
    }

    default String getComment(String name) {
        return null;
    }

    default String getComment(String name, Object val) {
        return null;
    }

}
