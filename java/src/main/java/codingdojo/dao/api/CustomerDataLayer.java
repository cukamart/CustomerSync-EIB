package codingdojo.dao.api;

import codingdojo.entity.Customer;
import codingdojo.entity.ShoppingList;

public interface CustomerDataLayer {

    void updateCustomerRecord(Customer customer);

    void createCustomerRecord(Customer customer);

    Customer findByExternalId(String externalId);

    Customer findByMasterExternalId(String externalId);

    Customer findByCompanyNumber(String companyNumber);
}
