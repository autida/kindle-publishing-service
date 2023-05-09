package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import javax.inject.Inject;

public class BookPublishTask implements Runnable{
    private PublishingStatusDao publishingStatusDao;
    private CatalogDao catalogDao;
    private BookPublishRequestManager bookPublishRequestManager;
    public BookPublishTask() {
    }
    @Inject
    public BookPublishTask(PublishingStatusDao publishingStatusDao, CatalogDao catalogDao, BookPublishRequestManager bookPublishRequestManager) {
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
        this.bookPublishRequestManager = bookPublishRequestManager;
    }

    @Override
    public void run() {
       BookPublishRequest bookPublishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();
       if(bookPublishRequest == null) {
           return;
       }
           //adds entry to the Publishing status table in In_progress state
           publishingStatusDao.setPublishingStatus(
                   bookPublishRequest.getPublishingRecordId(),
                   PublishingRecordStatus.IN_PROGRESS,
                   bookPublishRequest.getBookId()
           );
           //Performs formatting and conversion of the book
           KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(bookPublishRequest);
            try{
                catalogDao.createOrUpdateBook(kindleFormattedBook);
            } catch (Exception e) {
                publishingStatusDao.setPublishingStatus(
                        bookPublishRequest.getPublishingRecordId(),
                        PublishingRecordStatus.FAILED,
                        kindleFormattedBook.getBookId(), "message");
                throw new BookNotFoundException("Book not found!");
            }
           publishingStatusDao.setPublishingStatus(
                   bookPublishRequest.getPublishingRecordId(),
                   PublishingRecordStatus.SUCCESSFUL,
                   kindleFormattedBook.getBookId(), "message");

    }
}
