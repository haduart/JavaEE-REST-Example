package com.haduart.rest.decision;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DecisionServiceTest {
    public static final int DEFAULT_AMOUNT = 5;
    public static final String DEFAULT_EMAIL = "haduart@gmail.com";
    public static final String MALFORMET_EMAIL = "xtooomail.com";

    private Purchase purchase;
    private URI baseURI;
    private UriInfo uriInfo;
    private DecisionService decisionService;
    private EntityManager em;

    @Before
    public void setUp() throws Exception {
        decisionService = new DecisionService();
        em = decisionService.em = mock(EntityManager.class);

        baseURI = URI.create("http://restexample.haduart.com/rest/");

        uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(baseURI);

        purchase = new Purchase(DEFAULT_EMAIL, "Eduard", "Cespedes", DEFAULT_AMOUNT);
    }

    @Test
    public void ListPurchasesWithoutEmail_ShouldReturnPurchases() {
        List<User> users = mockQueryResults();

        Response response = decisionService.listPurchases(uriInfo, "");

        verify(em).createQuery("SELECT m FROM User m");
        assertEquals(users, response.getEntity());
        assertEquals(200, response.getStatus());
    }

    @Test
    public void ListPurchasesWithEmail_ShouldReturnPurchasesForEmail() {
        mockQueryResults();

        decisionService.listPurchases(uriInfo, DEFAULT_EMAIL);

        verify(em).createQuery("SELECT m FROM User m WHERE m.email=:email");
    }

    /*
    If the amount is less than 10, it should always be accepted
    {“accepted”: true, “reason”: “ok”}
     */
    @Test
    public void PurchaseLessThan10_AlwaysAccepted() {
        User user = new User(DEFAULT_EMAIL, 20);
        when(em.find(eq(User.class), eq(DEFAULT_EMAIL))).thenReturn(user);

        Response response = decisionService.purchase(uriInfo, purchase);

        assertResponse(response, "ok", true);
    }

    @Test
    public void PurchaseLessThan10_ShouldIncreaseAmount() {
        User user = mock(User.class);
        when(em.find(eq(User.class), eq(DEFAULT_EMAIL))).thenReturn(user);

        decisionService.purchase(uriInfo, purchase);

        verify(user).increaseAmount(DEFAULT_AMOUNT);
    }

    /*
    If the amount is higher than 1000, it should always be rejected with the reason “amount”
     */
    @Test
    public void PurchaseMoreThan1000_AlwaysRejected() {
        purchase.setAmount(2000);

        Response response = decisionService.purchase(uriInfo, purchase);

        assertResponse(response, "amount", false);
    }

    /*
    If the sum of purchases from a particular email is larger than 1000 (including current
purchase), it should be rejected with reason “debt”
     */
    @Test
    public void PurchaseSumMoreThan1000_Rejected() {
        purchase.setAmount(400);
        User user = mock(User.class);
        when(em.find(eq(User.class), eq(DEFAULT_EMAIL))).thenReturn(user);
        when(user.getAmount()).thenReturn(800);

        Response response = decisionService.purchase(uriInfo, purchase);

        verify(user, never()).increaseAmount(800);
        assertResponse(response, "debt", false);
    }

    @Test
    public void PurchaseSumLessThan1000_Accepted() {
        purchase.setAmount(400);
        User user = mock(User.class);
        when(em.find(eq(User.class), eq(DEFAULT_EMAIL))).thenReturn(user);
        when(user.getAmount()).thenReturn(200);

        Response response = decisionService.purchase(uriInfo, purchase);

        verify(user).increaseAmount(400);
        assertResponse(response, "ok", true);
    }

    @Test
    public void PurchaseNewEmail_Accepted() {
        when(em.find(eq(User.class), eq(DEFAULT_EMAIL))).thenReturn(null);

        Response response = decisionService.purchase(uriInfo, purchase);

        User user = new User(DEFAULT_EMAIL, DEFAULT_AMOUNT);
        verify(em).persist(user);
        assertResponse(response, "ok", true);
    }

    @Test
    public void PurchaseInvalidEmail_NotAccepted(){
        when(em.find(eq(User.class), eq(MALFORMET_EMAIL))).thenReturn(null);
        purchase.setEmail(MALFORMET_EMAIL);

        Response response = decisionService.purchase(uriInfo, purchase);

        verify(em, never()).persist(any());
        assertEquals(400, response.getStatus());
    }

    private void assertResponse(Response response, String reason, boolean isAccepted) {
        assertEquals(200, response.getStatus());
        PurchaseResponse purchaseResponse = (PurchaseResponse) response.getEntity();
        assertEquals(isAccepted, purchaseResponse.isAccepted());
        assertEquals(reason, purchaseResponse.getReason());
    }

    private List<User> mockQueryResults() {
        List<User> users = new ArrayList<>();
        users.add(new User(DEFAULT_EMAIL, 100));
        Query query = mock(Query.class);
        when(query.getResultList()).thenReturn(users);
        when(em.createQuery(Matchers.anyString())).thenReturn(query);
        return users;
    }
}
