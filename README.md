# quarkus-utils-commons

Utilities for Quarkus REST APIs.

## Features

| Module | What it does |
|--------|-------------|
| `@MeasuredEndpoint` | CDI interceptor that auto-instruments REST endpoints with Micrometer: latency histogram, success counter, error counter with exception tag |
| `PageRequest` / `PageResponse` | Generic JAX-RS pagination with safe defaults |

## Installation

```xml
<dependency>
  <groupId>io.github.sambouch79</groupId>
  <artifactId>quarkus-utils-commons</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

### @MeasuredEndpoint

```java
@GET
@Path("/users/{id}")
@MeasuredEndpoint(name = "user.findById", endpoint = "findById")
public Response findById(@PathParam("id") ID id) { ... }
```

Produces three Prometheus metrics:
- `api_endpoint_latency{endpoint="findById", operation="user.findById"}` — histogram
- `api_endpoint_calls{...}` — success counter
- `api_endpoint_errors{..., exception="ResourceNotFoundException"}` — error counter with exception class


### Pagination

```java
public class UserPageRequest extends PageRequest {

    @QueryParam("sortBy")
    @DefaultValue("id")
    private String sortBy;

    @Override public String getSortBy() { return sortBy; }
    @Override public void setSortBy(String s) { this.sortBy = s; }
}

// In resource:
public Response listAll(@BeanParam @Valid UserPageRequest req) {
    List<UserDTO> content = service.findPage(
        req.safePage(), req.safeSize(), req.safeSortField(), req.isAscending()
    );
    long total = service.countAll();
    return Response.ok(new PageResponse<>(content, total, req.safePage(), req.safeSize())).build();
}
```

## Requirements

- Java 17+
- Quarkus 3.x
- `quarkus-micrometer-registry-prometheus` (for `@MeasuredEndpoint`)
- `quarkus-hibernate-validator` (for Bean Validation annotations)

## License

Apache 2.0
