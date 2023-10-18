package qiminfo;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.micrometer.core.annotation.Timed;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RegisterForReflection
public class Entry {
  @JsonProperty("API")
  public String api;
  @JsonProperty("Description")
  public String description;
  @JsonProperty("Auth")
  public String auth;
  @JsonProperty("HTTPS")
  public boolean https;
  @JsonProperty("Cors")
  public String cors;
  @JsonProperty("Link")
  public String link;
  @JsonProperty("Category")
  public String category;

  @Override
  public String toString() {
    return "Entry [api=" + api + ", description=" + description + ", auth=" + auth + ", https=" + https + ", cors="
        + cors + ", link=" + link + ", category=" + category + "]";
  }

  @RegisterForReflection
  public static class Model {
    public Integer count;
    public List<Entry> entries;
  }

  @RegisterRestClient(configKey = "entry-remote-api")
  public static interface DistantClient {
    @Path("/entries")
    @GET
    @Timed(value = "entry-remote-api", description = "A measure of how long it takes to get entries from remote api.")
    public Uni<Entry.Model> getEntries();
  }

  @RegisterRestClient(configKey = "entry-api")
  public static interface Client {
    @POST
    public Uni<Response> postEntry(Entry entry);

    @PUT
    public Uni<Response> putEntry(Entry entry);

    @POST
    @Path("/bulk")
    @Timed(value = "entry-api", description = "A measure of how long it takes to send entries to entry-api.")
    public Uni<Response> postBulkEntries(List<Entry> entries);

  }

  @ApplicationScoped
  public static class Service {
    @RestClient
    @Inject
    private Client client;

    @RestClient
    @Inject
    private DistantClient distantClient;

    public Uni<List<Entry>> getEntriesAndSend() {
      return distantClient.getEntries()
          .map(entryModel -> entryModel.entries)
          .onItem()
          .call(entries -> client.postBulkEntries(entries));
    }
  }
}
