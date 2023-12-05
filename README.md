# DEPRECATED: orders-aws

A microservices-demo service that provides ordering capabilities.

The code is in Scala and uses Spring, Jersey and the Java DynamoDB client.

Compared to the original `orders` service, this service cuts down heavily on dependency injection 'magic'. It does still use spring autowiring, but no autoconfiguration, no integration with other frameworks.
It also pushes the use of reflection out of the core of the application. Reflection is still used in the presentation layer, but poses no maintenance risk on the rest of the application any more, because it is used in isolation.

Simplifications:

 - all error handling is performed using runtime exceptions.
 - all sequences are `List`s

# Build

    GROUP=weaveworksdemos COMMIT=latest ./scripts/build.sh

# Test

    ./test/test.sh unit.py
    ./test/test.sh component.py
    GROUP=weaveworksdemos COMMIT=latest ./test/test.sh container.py --tag latest

# Manual test in `microservices-demo`

    GROUP=weaveworksdemos COMMIT=latest ./scripts/build.sh && (cd ../microservices-demo/deploy/docker-compose && docker-compose up -d && docker-compose logs -f)
