package com.example.demo.event;

import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class MessageEventPublisher implements
    Consumer<FluxSink<MessageEvent>> {

    private final BlockingQueue<MessageEvent> queue =
        new LinkedBlockingQueue<>();

    public void onApplicationEvent(MessageEvent event) {
        this.queue.offer(event);
    }

     @Override
    public void accept(FluxSink<MessageEvent> sink) {
         Executors.newSingleThreadExecutor().execute(() -> {
             while (true)
                 try {
                     MessageEvent event = queue.take();
                     sink.next(event);
                 }
                 catch (InterruptedException e) {
                     ReflectionUtils.rethrowRuntimeException(e);
                 }
         });
    }
}
