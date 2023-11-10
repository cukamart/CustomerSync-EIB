package codingdojo.service.api;

import codingdojo.external.ExternalCustomer;

public interface CustomerSync {

    boolean syncWithDataLayer(ExternalCustomer externalCustomer);
}
