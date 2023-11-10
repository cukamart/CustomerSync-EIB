### Code changes include following:
- Separate business layer from data layer
- Synchronize an additional field 'bonusPointsBalance'
- Update existing tests to verify bonusPointsBalance is updating correctly
- Introduced interfaces for potential future extensibility
- Structured the application by breaking it into packages
- Removed unreachable code
- Removed unnecessary if statement
- Get rid of duplicate code
- Get rid of hard-coded strings and used enum instead
- Reduce code complexity and improve code readability by introducing several new methods

### Further improvements to consider:
I don't like we are updating duplicates one by one in for each.
If it weren't for a 'fakeDatabase,' I would refactor it to perform the save operation in 'bulk' so that there would be only one SQL statement.
In case we use OracleDB, we could update 1000 customers in one batch rather than hitting database 1000 times.

ExternalCustomer uses the same "Address" and "ShoppingList" as a Customer, which might pose a problem in the future.
In the event that a Customer or ExternalCustomer undergoes changes and no longer aligns with the Address / ShoppingList, resulting in BC-compatibility issues, it's advisable to implement an additional mapping layer between them.
In case we would like to be more flexible I would use ExternalAddress and ExternalShoppingList and map them (e.g. using mapStruct) to enhance flexibility between Customer and ExternalCustomer.
However, for a simple exercise like this, I opted not to make it overly complex or invest too much in future considerations.