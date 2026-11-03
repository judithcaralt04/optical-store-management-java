# Optical Store Management (Java)

A console based Java application for managing an optical store, built with a layered architecture. Developed for the Object Oriented Design and Programming course.

This repository is private. It is shared for portfolio purposes only, and access is limited to people who have been explicitly invited.

## What it does

- Registration and login for two kinds of clients: online clients and company clients
- Browsing providers and the products they sell, including contact lenses, consumables and services, each with its own pricing rules
- A shopping cart with checkout, producing one or more sales
- VAT and discount rules specific to each product type, for example a reduced VAT rate for contact lenses and a discount on consumables nearing their expiry date
- Shipping cost calculation for online clients, based on the distance between their address and the campus, using a geolocation library provided by the course
- Restrictions on which items can be sold to which client type, for example services can only be sold to in person clients
- Persistence that can switch between local JSON and CSV files or a remote API, decided automatically depending on what is available

## Tech stack

- Java
- A console interface built with `Scanner`
- Persistence through local JSON and CSV files, or through a REST API, behind a common set of DAO interfaces

## Architecture

The project follows a layered architecture with three packages:

- `presentation`: `MainUI`, which handles console input and output, and `Controller`, which receives user actions and delegates them to the relevant manager
- `business`: the domain entities (`Client` and its subclasses `OnlineClient` and `CompanyClient`, `Item` and its subclasses `Product`, `ContactLens`, `Consumable` and `Service`, plus `Provider`, `ShoppingCart`, `CartItem` and `Sale`) and the managers that coordinate each use case
- `persistence`: DAO interfaces for products, clients, providers and sales, each with a local file implementation and an API implementation, selected at runtime by `PersistenceManager`

Inheritance and polymorphism are used so that every sellable item is handled uniformly as an `Item` from the business logic's point of view, while each subtype resolves its own pricing and sale restrictions internally, without conditional logic scattered around the codebase.

## My contribution

This was a two person team project. I was responsible for the entire implementation, all three layers, business logic, persistence and the console interface. My teammate, Aras Mora, was responsible for the UML design.

## Running it locally

1. Requirements: Java JDK 11 or higher
2. Make sure `lib/ApiHelper.jar` and `lib/CoordinatesAPI.jar` are on the classpath, they are small helper libraries provided by the course for the API calls and the geolocation calculations
3. Run `src/Main.java` from your IDE
4. The application will try to use the API first, and fall back to the local `clients.json`, `products.json`, `providers.json` and `sales.csv` files if it is not available

## Documentation

- `class_diagram.pdf` contains the final UML class diagram
- `design_report.pdf` is the accompanying design report, explaining the architecture, the use of inheritance and polymorphism, and the error handling strategy
