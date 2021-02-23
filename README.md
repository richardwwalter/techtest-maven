## Solution for tech test
### Richard Walter

### Some Notes:

### Approach to unreliable hadoop service
I have wrapped this unreliable service in retries,and a circuit breaker.
If the hadoop service repeatedly fails then we don't update hadoop.
We allow our db transaction to commit and confirm back to the client.
This unreliable service should really be handled by using a message queue to communicate with it.
This will ensure no loss of data to hadoop as messages will be stored until hadoop can process them successfully

As the hadoop service is potentially so slow we don't want to keep a transaction open for the whole time we are interacting with hadoop.
So hadoop publishing is done as part of post commit processing. i.e. after the database transaction has committed.

This policy may not be what is required and you may simply want to fail the whole transaction and report failure to the caller.
This is really just throwing the problem upstream through.

If failing the whole transaction if hadoop update fails is what is required then I am happy to update the solution to reflect this requirement.

Alternatively my current solution could be made more reliable by adding some persistent state for failed hadoop messages.
And having a recovery mechanism or end of day batch to replay any failed messages depending on requirements.

Better to use a message queue to decouple the hadoop service and take advantage of its durability to handle any messages that cannot be processed by hadoop.

### Hibernate 
It's a while since I last used Hibernate so there may be some more optimal hibernate approach

### Lombok
I haven't used lombok in the past so again there may be some optimisations that coudl be made

### Other dependencies
Introducing some other dependencies would improve and simplify the code 