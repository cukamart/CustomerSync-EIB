package codingdojo.model;

import codingdojo.entity.Customer;

import java.util.ArrayList;
import java.util.Collection;

public class CustomerMatches {
    private final Collection<Customer> duplicates = new ArrayList<>();
    private MatchTerm matchTerm;
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void addDuplicate(Customer duplicate) {
        duplicates.add(duplicate);
    }

    public Collection<Customer> getDuplicates() {
        return duplicates;
    }

    public MatchTerm getMatchTerm() {
        return matchTerm;
    }

    public void setMatchTerm(MatchTerm matchTerm) {
        this.matchTerm = matchTerm;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
