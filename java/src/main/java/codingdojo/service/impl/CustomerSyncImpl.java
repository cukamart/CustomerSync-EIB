package codingdojo.service.impl;

import codingdojo.dao.api.CustomerDataAccess;
import codingdojo.dao.api.CustomerDataLayer;
import codingdojo.dao.impl.CustomerDataAccessImpl;
import codingdojo.entity.Customer;
import codingdojo.entity.CustomerType;
import codingdojo.entity.ShoppingList;
import codingdojo.exception.ConflictException;
import codingdojo.external.ExternalCustomer;
import codingdojo.model.CustomerMatches;
import codingdojo.service.api.CustomerSync;

import static codingdojo.model.MatchTerm.COMPANY_NUMBER;
import static codingdojo.model.MatchTerm.EXTERNAL_ID;

public class CustomerSyncImpl implements CustomerSync {

    private final CustomerDataAccess customerDataAccess;

    public CustomerSyncImpl(CustomerDataLayer customerDataLayer) {
        this(new CustomerDataAccessImpl(customerDataLayer));
    }

    public CustomerSyncImpl(CustomerDataAccessImpl db) {
        this.customerDataAccess = db;
    }

    @Override
    public boolean syncWithDataLayer(ExternalCustomer externalCustomer) {
        CustomerMatches customerMatches = loadCustomerMatches(externalCustomer);
        Customer customer = customerMatches.getCustomer();

        if (customer == null) {
            customer = Customer.newCustomerWithExternalId(externalCustomer.getExternalId());
        }

        boolean created = customer.getInternalId() == null;
        syncCustomer(externalCustomer, customer);

        for (Customer duplicate : customerMatches.getDuplicates()) {
            syncDuplicate(externalCustomer, duplicate);
            customerDataAccess.save(duplicate);
        }

        customerDataAccess.save(customer);
        return created;
    }

    private CustomerMatches loadCustomerMatches(ExternalCustomer externalCustomer) {
        String externalId = externalCustomer.getExternalId();

        if (externalCustomer.isCompany()) {
            return loadCompany(externalId, externalCustomer.getCompanyNumber());
        } else {
            return loadPerson(externalId);
        }
    }

    private void syncDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        if (duplicate == null) {
            duplicate = Customer.newCustomerWithExternalId(externalCustomer.getExternalId());
        }

        duplicate.setName(externalCustomer.getName());
    }

    private void syncCustomer(ExternalCustomer externalCustomer, Customer customer) {
        customer.setName(externalCustomer.getName());
        customer.setAddress(externalCustomer.getPostalAddress());
        customer.setPreferredStore(externalCustomer.getPreferredStore());

        for (ShoppingList consumerShoppingList : externalCustomer.getShoppingLists()) {
            customer.addShoppingList(consumerShoppingList);
        }

        if (externalCustomer.isCompany()) {
            customer.setCustomerType(CustomerType.COMPANY);
            customer.setCompanyNumber(externalCustomer.getCompanyNumber());
        } else {
            customer.setCustomerType(CustomerType.PERSON);
            customer.setBonusPointsBalance(externalCustomer.getBonusPointsBalance());
        }
    }

    private CustomerMatches loadPerson(String externalId) {
        CustomerMatches customerMatches = customerDataAccess.loadPersonCustomer(externalId);

        if (isConflictingPerson(customerMatches.getCustomer())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
        }
        return customerMatches;
    }

    private CustomerMatches loadCompany(String externalId, String companyNumber) {
        CustomerMatches customerMatches = customerDataAccess.loadCompanyCustomer(externalId, companyNumber);

        if (isConflictingCompany(customerMatches.getCustomer())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        if (EXTERNAL_ID.equals(customerMatches.getMatchTerm())) {
            handleExternalIdMatch(companyNumber, customerMatches);
        } else if (COMPANY_NUMBER.equals(customerMatches.getMatchTerm())) {
            handleCompanyNumberMatch(externalId, companyNumber, customerMatches);
        }
        return customerMatches;
    }

    private static void handleCompanyNumberMatch(String externalId, String companyNumber, CustomerMatches customerMatches) {
        String customerExternalId = customerMatches.getCustomer().getExternalId();

        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
        }

        Customer customer = customerMatches.getCustomer();
        customer.setExternalId(externalId);
        customer.setMasterExternalId(externalId);
    }

    private static void handleExternalIdMatch(String companyNumber, CustomerMatches customerMatches) {
        String customerCompanyNumber = customerMatches.getCustomer().getCompanyNumber();

        if (!companyNumber.equals(customerCompanyNumber)) {
            customerMatches.getCustomer().setMasterExternalId(null);
            customerMatches.addDuplicate(customerMatches.getCustomer());
            customerMatches.setCustomer(null);
            customerMatches.setMatchTerm(null);
        }
    }

    private boolean isConflictingCompany(Customer customer) {
        return customer != null &&
                !CustomerType.COMPANY.equals(customer.getCustomerType());
    }

    private boolean isConflictingPerson(Customer customer) {
        return customer != null &&
                !CustomerType.PERSON.equals(customer.getCustomerType());
    }
}
