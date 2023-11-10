package codingdojo.dao.api;

import codingdojo.entity.Customer;
import codingdojo.model.CustomerMatches;

public interface CustomerDataAccess {

    void save(Customer customer);

    CustomerMatches loadCompanyCustomer(String externalId, String companyNumber);

    CustomerMatches loadPersonCustomer(String externalId);
}
