package com.malaab.ya.action.actionyamalaab.owner.events;

import java.io.File;


public class OnEventImageCompressionCompleted {

    private File image;

    public OnEventImageCompressionCompleted(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }
}
