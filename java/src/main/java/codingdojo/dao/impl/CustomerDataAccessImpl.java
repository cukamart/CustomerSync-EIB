package codingdojo.dao.impl;

import codingdojo.dao.api.CustomerDataAccess;
import codingdojo.dao.api.CustomerDataLayer;
import codingdojo.entity.Customer;
import codingdojo.model.CustomerMatches;

import static codingdojo.model.MatchTerm.COMPANY_NUMBER;
import static codingdojo.model.MatchTerm.EXTERNAL_ID;

public class CustomerDataAccessImpl implements CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    public CustomerDataAccessImpl(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    @Override
    public CustomerMatches loadCompanyCustomer(String externalId, String companyNumber) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);

        if (matchByExternalId != null) {
            matches.setCustomer(matchByExternalId);
            matches.setMatchTerm(EXTERNAL_ID);
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matches.addDuplicate(matchByMasterId);
            }
        } else {
            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                matches.setMatchTerm(COMPANY_NUMBER);
            }
        }

        return matches;
    }

    @Override
    public CustomerMatches loadPersonCustomer(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber != null) {
            matches.setMatchTerm(EXTERNAL_ID);
        }
        return matches;
    }

    @Override
    public void save(Customer customer) {
        if (customer.getInternalId() == null) {
            customerDataLayer.createCustomerRecord(customer);
        } else {
            customerDataLayer.updateCustomerRecord(customer);
        }
    }
}
