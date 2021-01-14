package com.example.cover_a01;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cover_a01.data.localdatabase.CoVerDatabase;
import com.example.cover_a01.data.model.Contact;
import com.example.cover_a01.data.model.Exposee;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CoVerDatabaseTest {
    private CoVerDatabase coVerDatabase;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CoVerDatabase.setupAppDatabase(appContext);
        this.coVerDatabase = CoVerDatabase.getAppDatabase();
    }

    @After
    public void tearDown() throws Exception {
        coVerDatabase.contactDao().nukeTable();
        coVerDatabase.exposeesDao().nukeTable();
    }
    @Test
    public void TestInsertContact() {
        Contact sampleContact = new Contact("TestTestTest", 1606743600000l);
        coVerDatabase.contactDao().insert(sampleContact);
        Assert.assertTrue(coVerDatabase.contactDao().getAll().stream().anyMatch(contact -> contact.getKey().equals(sampleContact.getKey())));
    }

/*    @Test
    public void TestDeleteOldContact() {
        Contact sampleContactOld = new Contact("VeryOldTest :D", 1606740000000l);
        Contact sampleContactYoung = new Contact("YoungTest", 1606743600000l);
        coVerDatabase.contactDao().insert(sampleContactOld);
        coVerDatabase.contactDao().insert(sampleContactYoung);

        //coVerDatabase.contactDao().deleteOlderThan(1606742600000l);

        Assert.assertFalse(coVerDatabase.contactDao().getAll().stream().anyMatch(contact -> contact.getKey().equals(sampleContactOld.getKey())));
        Assert.assertTrue(coVerDatabase.contactDao().getAll().stream().anyMatch(contact -> contact.getKey().equals(sampleContactYoung.getKey())));
    }*/

    @Test
    public void TestExposee() {
        Exposee sampleExposee = new Exposee("TestTestTest");
        coVerDatabase.exposeesDao().insert(sampleExposee);
        Assert.assertTrue(coVerDatabase.exposeesDao().getAll().stream().anyMatch(exposee -> exposee.getKey().equals(sampleExposee.getKey())));
    }
}