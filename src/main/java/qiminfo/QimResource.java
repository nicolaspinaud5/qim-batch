package qiminfo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class QimResource {

  @Inject
  Entry.Service service;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    service.getEntriesAndSend().subscribe().with(response -> {});
    return "Hello from RESTEasy Reactive";
  }
}
