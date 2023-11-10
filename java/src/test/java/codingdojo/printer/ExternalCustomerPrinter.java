package codingdojo.printer;

import codingdojo.external.ExternalCustomer;

public class ExternalCustomerPrinter {

    public static String print(ExternalCustomer externalCustomer, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("ExternalCustomer {");
        sb.append("\n" + indent + "    externalId='" + externalCustomer.getExternalId() + '\'');
        sb.append("\n" + indent + "    companyNumber='" + externalCustomer.getCompanyNumber() + '\'' );
        sb.append("\n" + indent + "    bonusPointsBalance='" + externalCustomer.getBonusPointsBalance() + '\'' );
        sb.append("\n" + indent + "    name='" + externalCustomer.getName() + '\'' );
        sb.append("\n" + indent + "    preferredStore='" + externalCustomer.getPreferredStore() + '\'');
        sb.append("\n" + indent + "    address=" + AddressPrinter.printAddress(externalCustomer.getPostalAddress()));
        sb.append("\n" + indent + "    shoppingLists=" + ShoppingListPrinter.printShoppingLists(externalCustomer.getShoppingLists(), indent + "    ") );
        sb.append("\n" + indent + "}");

        return sb.toString();
    }
}
