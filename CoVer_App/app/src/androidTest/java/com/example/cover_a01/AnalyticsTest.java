package com.example.cover_a01;

import com.example.cover_a01.data.model.Contact;
import com.example.cover_a01.data.model.Exposee;
import com.example.cover_a01.data.model.InfectionStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import static com.example.cover_a01.data.model.InfectionStatus.lowRiskOfInfection;
import static com.example.cover_a01.data.model.InfectionStatus.notInfected;

public class AnalyticsTest {

    private Analytics analytics;

    @Before
    public void setUp() throws Exception {
        //Analytics is private and needs to be set public for this test (Analytics class line 63)
        this.analytics = new Analytics(null,null); //Set this public to test
    }

    @Test
    public void testCheckIfExposedCertainMinutesPositiv() {
        List<Contact> contacts = List.of(
                new Contact("b4515f9e-41bd-493c-ae84-7a14e524f335", 1606743600000l),
                new Contact("abaab5c7-e45d-496c-b173-0f737a78b384", 1606743660000l),
                new Contact("efb80472-8f16-4128-a344-133e6343e072", 1606743710000l),
                new Contact("bd66b97b-0e44-40a3-8d05-52cc538a509b", 1606743720000l),
                new Contact("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef", 1606743780000l)
        );

        List<Exposee> exposees = List.of(
                new Exposee("b4515f9e-41bd-493c-ae84-7a14e524f335"),
                new Exposee("abaab5c7-e45d-496c-b173-0f737a78b384"),
                new Exposee("bd66b97b-0e44-40a3-8d05-52cc538a509b"),
                new Exposee("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef")
        );

        analytics.markExposeeContacts(contacts, exposees);
        Assert.assertEquals((int)1,(int)analytics.checkIfExposed(contacts).second);
        Assert.assertEquals((InfectionStatus) lowRiskOfInfection,analytics.checkIfExposed(contacts).first);
    }

    @Test
    public void testCheckIfExposedCertainMinutesNegative() {
        List<Contact> contacts = List.of(
                new Contact("b4515f9e-41bd-493c-ae84-7a14e524f335", 1606743600000l),
                new Contact("abaab5c7-e45d-496c-b173-0f737a78b384", 1606743660000l),
                new Contact("efb80472-8f16-4128-a344-133e6343e072", 1606743710000l),
                new Contact("bd66b97b-0e44-40a3-8d05-52cc538a509b", 1606743720000l),
                new Contact("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef", 1606743790000l),
                new Contact("e46f08c7-b1a0-4c5c-bdff-32d556a01831", 1606743794000l)
        );

        List<Exposee> exposees = List.of(
                new Exposee("b4515f9e-41bd-493c-ae84-7a14e524f335"),
                new Exposee("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef")
        );

        analytics.markExposeeContacts(contacts, exposees);
        Assert.assertEquals((InfectionStatus) notInfected,analytics.checkIfExposed(contacts).first);

    }

    @Test
    public void testAllContactsExposses() {
        List<Contact> contacts = List.of(
                new Contact("b4515f9e-41bd-493c-ae84-7a14e524f335", 1606743600000l),
                new Contact("abaab5c7-e45d-496c-b173-0f737a78b384", 1606743660000l),
                new Contact("bd66b97b-0e44-40a3-8d05-52cc538a509b", 1606743720000l),
                new Contact("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef", 1606743780000l)
        );

        List<Exposee> exposees = List.of(
                new Exposee("b4515f9e-41bd-493c-ae84-7a14e524f335"),
                new Exposee("abaab5c7-e45d-496c-b173-0f737a78b384"),
                new Exposee("bd66b97b-0e44-40a3-8d05-52cc538a509b"),
                new Exposee("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef")
        );

        analytics.markExposeeContacts(contacts, exposees);
        contacts.forEach(c -> Assert.assertTrue((c.isKnownExposee())));
    }

    @Test
    public void testNoContactsExposses() {
        List<Contact> contacts = List.of(
                new Contact("b4515f9e-41bd-493c-ae84-7a14e524f335", 1606743600000l),
                new Contact("abaab5c7-e45d-496c-b173-0f737a78b384", 1606743660000l),
                new Contact("bd66b97b-0e44-40a3-8d05-52cc538a509b", 1606743720000l),
                new Contact("6dd1fe46-bf78-48fe-ae30-07bb61cff5ef", 1606743780000l)
        );

        List<Exposee> exposees = List.of(
                new Exposee("6d42e3f8-1146-4558-9c72-73365f9f4c4b"),
                new Exposee("efb80472-8f16-4128-a344-133e6343e072"),
                new Exposee("c87702ae-4af4-4e7c-b0a4-5986100bdd40"),
                new Exposee("76abd326-a56d-4974-a3be-cd3d6a7816a3")
        );

        analytics.markExposeeContacts(contacts, exposees);
        contacts.forEach(c -> Assert.assertFalse((c.isKnownExposee())));
    }
}