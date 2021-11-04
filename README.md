# Stock Info App
Example application in Kotlin for Android using Clean Architecture


## Features
- Search for stocks by symbol
- Select a stock from the results to view additional information
- Add stocks to a watchlist to show the last 24 hours of price information

## Libraries
This applications demonstrates the use of the following libraries
- Retrofit for web service calls
- Koin for dependency injection
- Room for database access


## Clean Architecture

The application is structured to follow the clean architecture design pattern. Inner layers such as the domain and data/use cases are located in the core module.
The outer framework and presentation layers reside in the app module utlizing the MVVM pattern with live data.

Instrumented unit tests can also be found under the "androidTest" directory