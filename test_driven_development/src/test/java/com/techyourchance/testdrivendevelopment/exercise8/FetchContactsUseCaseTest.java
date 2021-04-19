package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    private static final String FILTER_VALUE = "filter";
    private static final String ID = "id";
    private static final String FULL_NAME = "fullName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String IMAGE_URL = "image_url";
    private static final double AGE = 12.0;


    private FetchContactsUseCase sut;
    @Mock GetContactsHttpEndpoint getContactsHttpEndpointMock;
    @Mock FetchContactsUseCase.Listener listener1Mock;
    @Mock FetchContactsUseCase.Listener listener2Mock;
    @Captor ArgumentCaptor<List<Contact>> listArgumentCaptor;

    @Before
    public void setUp() {
        sut = new FetchContactsUseCase(getContactsHttpEndpointMock);
        success();
    }

    @Test
    public void getContactsAndNotify_correctParameterPassedToEndpoint() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        sut.getContactsAndNotify(FILTER_VALUE);
        Mockito.verify(getContactsHttpEndpointMock).getContacts(argumentCaptor.capture(), Mockito.any(GetContactsHttpEndpoint.Callback.class));
        assertThat(argumentCaptor.getValue(), is(FILTER_VALUE));
    }

    @Test
    public void getContactsAndNotify_success_observersNotifiedWithCorrectData() {
        sut.registerListener(listener1Mock);
        sut.registerListener(listener2Mock);
        sut.getContactsAndNotify(FILTER_VALUE);
        Mockito.verify(listener1Mock).onContactsFetched(listArgumentCaptor.capture());
        Mockito.verify(listener2Mock).onContactsFetched(listArgumentCaptor.capture());
        List<List<Contact>> captorList = listArgumentCaptor.getAllValues();
        List<Contact> contacts1 = captorList.get(0);
        List<Contact> contacts2 = captorList.get(1);
        assertThat(contacts1, is(getContacts()));
        assertThat(contacts2, is(getContacts()));
    }

    @Test
    public void getContactsAndNotify_success_unSubscribedObserversNotNotified() {
        sut.registerListener(listener1Mock);
        sut.registerListener(listener2Mock);
        sut.unregisterListener(listener1Mock);
        sut.getContactsAndNotify(FILTER_VALUE);
        Mockito.verify(listener2Mock).onContactsFetched(Mockito.<Contact>anyList());
        Mockito.verifyNoMoreInteractions(listener1Mock);
    }

    @Test
    public void getContactsAndNotify_generalError_observersNotifiedOfFailure() {
        generalError();
        sut.registerListener(listener1Mock);
        sut.registerListener(listener2Mock);
        sut.getContactsAndNotify(FILTER_VALUE);
        Mockito.verify(listener1Mock).onContactsFailed();
        Mockito.verify(listener2Mock).onContactsFailed();
    }

    @Test
    public void getContactsAndNotify_networkError_observersNotifiedOfFailure() {
        networkError();
        sut.registerListener(listener1Mock);
        sut.registerListener(listener2Mock);
        sut.getContactsAndNotify(FILTER_VALUE);
        Mockito.verify(listener1Mock).onContactsFailed();
        Mockito.verify(listener2Mock).onContactsFailed();
    }

    //======================== HELPER METHODS ==================================//
    private void success() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsSucceeded(getContactSchemas());
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(Mockito.anyString(), Mockito.any(GetContactsHttpEndpoint.Callback.class));
    }

    private void generalError() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(Mockito.anyString(), Mockito.any(GetContactsHttpEndpoint.Callback.class));
    }

    private void networkError() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(Mockito.anyString(), Mockito.any(GetContactsHttpEndpoint.Callback.class));
    }

    private List<ContactSchema> getContactSchemas() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, FULL_NAME, PHONE_NUMBER, IMAGE_URL, AGE));
        return contactSchemas;
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }
}