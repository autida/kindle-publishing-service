package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BookPublishRequestManager {
    private Queue<BookPublishRequest> bookPublishRequestQueue = new ConcurrentLinkedQueue<>();

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        bookPublishRequestQueue.add(bookPublishRequest);
    }
    public BookPublishRequest getBookPublishRequestToProcess() {
            return bookPublishRequestQueue.poll();
    }
}
