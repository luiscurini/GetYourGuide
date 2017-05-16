# GetYourGuide

Coding challenge for GetYouGuide, this app will retrieve the reviews of an specific tour and shows it in a RecyclerView. It's an endless recyclerView, it loads 20 reviews at a time and when the user gets to the last item, 20 more reviews will be loaded. 
One can add a Review locally, the API call was mocked so it won't connect to the GYG Api. 

It's possible to filter the review list by Date (newest - oldest) or by rating (highest-lowest).

The app stores the review list in sharedPreference for later use. (e.g if the user is offline).

# Libraries

* Gson
* Dagger2
* ButterKnife
* Retrofit2
* RxJava

# How to run the project

Clone or Download the repository, unzip if needed. Import the project on Android Studio, wait for it to build and run it on your preffered device or emulator.

# What's missing
UnitTest
Refactor of some code into Util classes. 


