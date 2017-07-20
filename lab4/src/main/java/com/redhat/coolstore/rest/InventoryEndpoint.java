package com.redhat.coolstore.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.coolstore.model.Inventory;
import com.redhat.coolstore.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import java.util.Optional;

@Path("/inventory")
@Api(
        value = "The store inventory service",
        description = "This API will tell you how many of a given item are left in inventory and where they are located",
        produces = MediaType.APPLICATION_JSON,
        basePath = "/api"
)
public class InventoryEndpoint {

    @Inject
    private InventoryService inventoryService;

    @Inject
    @ConfigurationValue("stores.closed")
    private Optional<String> storesClosed;

    @ApiOperation(
            value = "Retrieve availability of a given product based on Item ID.",
            notes = "If a store is closed, then quantity will always be 0."
    )
    @GET
    @Path("/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Inventory getAvailability(
            @ApiParam(value = "Unique Item ID of the product", required = true, example = "329299")
            @PathParam("itemId")
                    String itemId) {
        Inventory i = inventoryService.getInventory(itemId);
        for (String store : storesClosed.orElse("").split(",")) {
            if (store.equalsIgnoreCase(i.getLocation())) {
                i.setQuantity(0);
            }
        }
        return i;
    }
}