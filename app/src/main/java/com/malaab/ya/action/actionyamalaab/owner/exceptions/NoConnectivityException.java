package com.malaab.ya.action.actionyamalaab.owner.exceptions;

import java.io.IOException;


public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No Connectivity exception";
    }
}