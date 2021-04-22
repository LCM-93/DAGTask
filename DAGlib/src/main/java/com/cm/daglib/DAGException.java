package com.cm.daglib;

/**
 * ****************************************************************
 * Author: LiChenMing.Chaman
 * Date: 2021/4/20 4:10 PM
 * Desc:
 * *****************************************************************
 */
public class DAGException extends RuntimeException{
    public DAGException() {
    }

    public DAGException(String message) {
        super(message);
    }

    public DAGException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAGException(Throwable cause) {
        super(cause);
    }

}
