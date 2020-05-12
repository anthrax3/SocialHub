package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.service.Stream;

import java.util.function.Consumer;

public class TwitterStream implements Stream {

    private boolean connecting = false;
    private twitter4j.TwitterStream stream;
    private Consumer<twitter4j.TwitterStream> open;

    public TwitterStream(
            twitter4j.TwitterStream stream,
            Consumer<twitter4j.TwitterStream> open) {
        this.stream = stream;
        this.open = open;
    }

    @Override
    public void open() {
        open.accept(stream);
        connecting = true;
    }

    @Override
    public void close() {
        stream.cleanUp();
        connecting = false;
    }

    @Override
    public boolean isOpened() {
        return connecting;
    }
}
