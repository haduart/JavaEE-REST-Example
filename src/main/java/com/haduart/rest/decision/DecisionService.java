package com.haduart.rest.decision;

import javax.ejb.Stateless;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Path("decision")
@Consumes({"application/json"})
@Produces({"application/json"})
@Stateless
public class DecisionService {
    @PersistenceContext
    EntityManager em;

    @Path("/")
    @GET
    public Response listPurchases(@Context final UriInfo ui,
                                  @DefaultValue("") @QueryParam("email") final String email) {
        return Response.ok().
                entity(queryUser(email))
                .build();
    }

    @Path("/")
    @POST
    public Response purchase(@Context final UriInfo uriInfo, final Purchase purchase) {
        if (purchase.getAmount() > 1000)
            return generateResponse(false, "amount");

        User user = em.find(User.class, purchase.getEmail());
        if (user == null) {
            if (!isEmailValid(purchase.getEmail()))
                return Response.status(400).entity("Malformed email").build();

            em.persist(new User(purchase.getEmail(), purchase.getAmount()));
            return generateResponse(true, "ok");
        }

        if (user.getAmount() + purchase.getAmount() > 1000)
            return generateResponse(false, "debt");

        user.increaseAmount(purchase.getAmount());

        return generateResponse(true, "ok");
    }

    private boolean isEmailValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private Response generateResponse(final boolean accepted, final String reason) {
        return Response.ok().entity(new PurchaseResponse(accepted, reason)).build();
    }

    private List<User> queryUser(String email) {
        List<User> users;
        if (email.isEmpty())
            users = em.createQuery("SELECT m FROM User m").getResultList();
        else {
            Query query = em.createQuery("SELECT m FROM User m WHERE m.email=:email");
            query.setParameter("email", email);
            users = query.getResultList();
        }
        return users;
    }
}
