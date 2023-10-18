package qiminfo;



import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import qiminfo.Entry.Service;

@ApplicationScoped
public class ScheduledEntry {

  @Inject
  Service service;

  Logger logger = Logger.getLogger(ScheduledEntry.class);

  @Transactional
  @Scheduled(every = "PT15M")
  void retrieveAndSendToApi() {
    service.getEntriesAndSend();
  }

}
