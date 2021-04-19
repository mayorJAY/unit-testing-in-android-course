package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    public interface Listener {
        void onContactsFetched(List<Contact> contacts);
        void onContactsFailed();
    }

    private GetContactsHttpEndpoint getContactsHttpEndpoint;
    private List<Listener> listeners = new ArrayList<>();

    public FetchContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void getContactsAndNotify(String filter) {
        getContactsHttpEndpoint.getContacts(filter, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> cartItems) {
                notifySuccess(cartItems);
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR:
                    case NETWORK_ERROR:
                        notifyFailure();
                        break;
                }
            }
        });
    }

    private void notifySuccess(List<ContactSchema> cartItems) {
        for (Listener listener: listeners) {
            listener.onContactsFetched(getContactFromSchema(cartItems));
        }
    }

    private void notifyFailure() {
        for (Listener listener: listeners) {
            listener.onContactsFailed();
        }
    }

    private List<Contact> getContactFromSchema(List<ContactSchema> cartItems) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema contactSchema: cartItems) {
            Contact contact = new Contact(contactSchema.getId(), contactSchema.getFullName(), contactSchema.getImageUrl());
            contacts.add(contact);
        }
        return contacts;
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }
}
